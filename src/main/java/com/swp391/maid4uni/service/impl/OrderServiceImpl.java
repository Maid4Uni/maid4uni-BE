package com.swp391.maid4uni.service.impl;

import com.swp391.maid4uni.converter.OrderConverter;
import com.swp391.maid4uni.dto.OrderDto;
import com.swp391.maid4uni.entity.Package;
import com.swp391.maid4uni.entity.*;
import com.swp391.maid4uni.enums.OrderStatus;
import com.swp391.maid4uni.enums.PeriodType;
import com.swp391.maid4uni.enums.Role;
import com.swp391.maid4uni.enums.Status;
import com.swp391.maid4uni.exception.Maid4UniException;
import com.swp391.maid4uni.repository.*;
import com.swp391.maid4uni.request.UpdateOrderRequest;
import com.swp391.maid4uni.response.OrderResponse;
import com.swp391.maid4uni.response.ResponseObject;
import com.swp391.maid4uni.service.OrderService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.util.*;

@Service
@Data
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private AccountRepository accountRepository;
    private PackageRepository packageRepository;
    private TaskRepository taskRepository;
    private OrderDetailRepository orderDetailRepository;
    private OrderConverter converter = OrderConverter.INSTANCE;

    @Override
    public List<OrderResponse> getOrderInfoByCustomer(int id) {
        List<Order> orderList = orderRepository.findAllByCustomerIdAndLogicalDeleteStatus(id, (short) 0);
        List<OrderResponse> orderResponseList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderList)) {
            orderResponseList = orderList.stream()
                    .map(converter::fromOrderToOrderResponse)
                    .toList();
        }
        return orderResponseList;
    }

    @Override
    public OrderResponse createOrder(OrderDto dto) {
        Account getCustomer = accountRepository
                .findAccountByIdAndLogicalDeleteStatus(dto.getCustomer().getId(), 0);
        if (getCustomer == null) {
            throw Maid4UniException.notFound("Not found Customer info.");
        }
        dto.setCreatedAt(LocalDateTime.now()); //có thể không cần thiết vì đã dùng @CreatedTimeStamp
        Package pkg = packageRepository.findByIdAndLogicalDeleteStatus(dto.getPackageDto().getId(), 0);
        dto.setPrice(pkg.getPrice());
        if (dto.getPeriodType().equals(PeriodType.ONE_MONTH)) {
            dto.setEndDay(dto.getStartDay().atStartOfDay().toLocalDate().plus(Period.ofDays(30))); // fix cứng, logic tính sau
        } else {
            dto.setEndDay(dto.getStartDay().atStartOfDay().toLocalDate().plus(Period.ofDays(60)));
        }
        Order order = converter.fromDtoToEntity(dto);

        // convert từ array qua string
        String workDay = dto.getWorkDay().toString();
        workDay = workDay.substring(1, workDay.length() - 1);

        order.setWorkDay(workDay);
        order.setAPackage(pkg);
        order.setCustomer(getCustomer);
        order.setOrderStatus(OrderStatus.WAITING_FOR_APPROVAL);
        orderRepository.save(order);

        OrderResponse response = OrderConverter.INSTANCE.fromOrderToOrderResponse(order);
        // payment set sau khi thanh toán thành công
        return response;
    }

    @Override
    public ResponseObject updateOrderStatus(UpdateOrderRequest request) {
        Order order = orderRepository.findByIdAndLogicalDeleteStatus(request.getId(), 0);
        OrderStatus status = request.getOrderStatus();
        String paymentStatus = order.getPayment().getPaymentStatus();
        // truong hop chua thanh toan && duyet thanh cong
        if (status.equals(OrderStatus.APPROVED) && paymentStatus.equals("Failed")) {
//            order.setOrderStatus(OrderStatus.DECLINED);
//            orderRepository.save(order);
            //không làm gì cả, chỉ báo lỗi vì update sai logic (payment fail mà approve -> chửi)
            return new ResponseObject("FAILED", "PAYMENT STATUS IS `FAILED`", OrderConverter.INSTANCE.fromOrderToOrderResponse(order));
        }
        // truong hop da thanh toan && duyet thanh cong
        if (status.equals(OrderStatus.APPROVED) && paymentStatus.equals("Success")) {
            createOrderDetail(order);
            order.setOrderStatus(status);
            orderRepository.save(order);
            return new ResponseObject("OK", "SUCCESSFULLY APPROVE ORDER", OrderConverter.INSTANCE.fromOrderToOrderResponse(order));
        }
        // truong hop da thanh toan && tu choi order
        if (status.equals(OrderStatus.DECLINED) && paymentStatus.equals("Success")) {
            order.setOrderStatus(OrderStatus.REFUNDED);
            orderRepository.save(order);
        }
        // truong hop con lai
        order.setOrderStatus(status);
        orderRepository.save(order);
        return new ResponseObject("OK", "ORDER IS DECLINED, CONTACT HOTLINE FOR REFUND INFO", OrderConverter.INSTANCE.fromOrderToOrderResponse(order));
    }

    @Override
    public List<OrderResponse> getAllOrder(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Order> orderList = orderRepository.findAllOrderByCreatedAtDescWithOffSetAndLimit(0, pageable);
        List<OrderResponse> orderResponseList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderList)) {
            orderResponseList = orderList.stream()
                    .map(converter::fromOrderToOrderResponse)
                    .toList();
        }
        return orderResponseList;
    }

    @Override
    public Map<String, Double> getTotalPriceByMonth(int month) {
        Map<String, Double> totalPriceByMonth = new HashMap<>();

        List<Order> ordersInMonth = orderRepository.findByLogicalDeleteStatusAndCreatedAtBetween(0,
                LocalDateTime.of(LocalDateTime.now().getYear(), month, 1, 0, 0),
                LocalDateTime.of(LocalDateTime.now().getYear(), month + 1, 1, 0, 0)
        );

        for (Order order : ordersInMonth) {
            String monthYear = String.format("%d-%02d", order.getCreatedAt().getYear(), order.getCreatedAt().getMonthValue());
            if (order.getOrderStatus().equals(OrderStatus.APPROVED))
                totalPriceByMonth.merge(monthYear, order.getPrice(), Double::sum);
        }

        return totalPriceByMonth;
    }

    @Override
    public Map<String, Double> getTotalPriceByMonthOfPackage(int month) {
        Map<String, Double> totalPriceByMonthOfPackage = new HashMap<>();
        List<Order> ordersInMonth = orderRepository.findByLogicalDeleteStatusAndCreatedAtBetween(0,
                LocalDateTime.of(LocalDateTime.now().getYear(), month, 1, 0, 0),
                LocalDateTime.of(LocalDateTime.now().getYear(), month + 1, 1, 0, 0)
        );
        List<Package> packageList = packageRepository.findAllByLogicalDeleteStatus(0);
        for (Package aPackage : packageList) {
            String packageName = aPackage.getName();
            totalPriceByMonthOfPackage.put(packageName, (double) 0);
            for (Order order : ordersInMonth) {
                if (order.getAPackage().equals(aPackage) && order.getOrderStatus().equals(OrderStatus.APPROVED))
                    totalPriceByMonthOfPackage.merge(packageName, order.getPrice(), Double::sum);
            }
        }
        return totalPriceByMonthOfPackage;
    }

    private List<OrderDetail> getWorkDay(Order order, ArrayList<Integer> workDayList, List<OrderDetail> orderDetailList, LocalDate workDay) {
        Duration d = Duration.ofHours(order.getDuration());
        for (int j = 0; j < workDayList.size(); j++) {
            OrderDetail detail = OrderDetail
                    .builder()
                    .order(order)
                    .status(Status.ON_GOING)
                    .startTime(order.getStartTime())
                    .endTime(order.getStartTime().plus(d))
                    .build();
            while (workDay.getDayOfWeek().getValue() != workDayList.get(j)) {
                workDay = workDay.atStartOfDay().toLocalDate().plus(Period.ofDays(1));
            }

            detail.setWorkDay(workDay);
            orderDetailList.add(detail);
        }
        return orderDetailList;
    }

    public void createOrderDetail(Order order) {
        ArrayList<String> workDayArr = new ArrayList<>(Arrays.asList(order.getWorkDay().split(", ")));
        ArrayList<Integer> workDayList = new ArrayList<>();
        for (int i = 0; i < workDayArr.size(); i++) {
            workDayList.add(Integer.parseInt(workDayArr.get(i)));
        }
        List<OrderDetail> orderDetailList = new ArrayList<>();
        Collections.sort(workDayList);
        LocalDate currentDate = order.getStartDay().atStartOfDay().toLocalDate();
        LocalDate actualStartDate = currentDate;
        int firstWorkDay = workDayList.get(0);
        DayOfWeek day = actualStartDate.getDayOfWeek();
        while (day.getValue() + 1 != firstWorkDay) {
            firstWorkDay += 1;
        }
        actualStartDate = actualStartDate.plus(Period.ofDays(firstWorkDay));
        LocalDate workDay = actualStartDate;
        Duration d = Duration.ofHours(order.getDuration());
        if (order.getPeriodType().equals(PeriodType.ONE_MONTH)) {
            for (int i = 0; i < 4; i++) {
                workDay = workDay.atStartOfDay().toLocalDate().plusDays(7 - workDayList.get(0) - 1);
                orderDetailList = getWorkDay(order, workDayList, orderDetailList, workDay);
            }
        } else {
            for (int i = 0; i < 8; i++) {
                workDay = workDay.atStartOfDay().toLocalDate().plusDays(7 - workDayList.get(0) - 1);
                orderDetailList = getWorkDay(order, workDayList, orderDetailList, workDay);

            }
        }
        orderDetailRepository.saveAll(orderDetailList);
        //TODO: DONE ORDER DETAIL

        //TODO: XỬ LÝ STAFF - XẾP LỊCH - DANG XU LY RANDOM
        List<Account> staffList = accountRepository.findByRoleAndLogicalDeleteStatus(Role.STAFF, (short) 0);
        Random rand = new Random();
        for (OrderDetail ord : orderDetailList) {
            for (com.swp391.maid4uni.entity.Service item : order.getAPackage().getServiceList()) {
                Account staffTask = staffList.get(rand.nextInt(staffList.size()));
                Task task = Task.builder()
                        .status(false)
                        .service(item)
                        .staff(staffTask)
                        .orderDetail(ord)
                        .build();
                taskRepository.save(task);
            }
        }
    }

    private ArrayList<Integer> getIntegerArray(ArrayList<String> stringArray) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (String stringValue : stringArray) {
            try {
                //Convert String to Integer, and store it into integer array list.
                result.add(Integer.parseInt(stringValue));
            } catch (NumberFormatException nfe) {
                //System.out.println("Could not parse " + nfe);
                log.info("NumberFormat", "Parsing failed! " + stringValue + " can not be an integer");
            }
        }
        return result;
    }

    private String convertToString(ArrayList<Integer> inputList) {
        StringBuilder sb = new StringBuilder();
        for (Integer element : inputList) {
            sb.append(element).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);  // Xóa dấu ',' cuối cùng
        }
        return sb.toString();
    }


}
