package com.swp391.maid4uni.controller;

import com.swp391.maid4uni.converter.PaymentConverter;
import com.swp391.maid4uni.dto.PaymentDto;
import com.swp391.maid4uni.entity.Payment;
import com.swp391.maid4uni.enums.API_PARAMS;
import com.swp391.maid4uni.repository.OrderRepository;
import com.swp391.maid4uni.repository.PaymentRepository;
import com.swp391.maid4uni.request.VNPayRequest;
import com.swp391.maid4uni.response.PaymentResponse;
import com.swp391.maid4uni.response.ResponseObject;
import com.swp391.maid4uni.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(API_PARAMS.API_VERSION)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentController {
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;


    @PostMapping(API_PARAMS.CREATE_VNPAY_PAYMENT)
    public ResponseEntity<ResponseObject> createVNPayPayment(@RequestBody VNPayRequest request){

        // todo
        //  String vnpayUrl = "redirect:" + vnPayService.createPayment(orderTotal, orderInfo, baseUrl);
        String vnpayUrl = vnPayService.createPayment(request.getOrderTotal(), request.getOrderInfo());
        return ResponseEntity.ok().body(
                new ResponseObject("OK", "CREATE PAYMENT SUCCESSFULLY", vnpayUrl)
        );
    }

    @GetMapping(API_PARAMS.GET_VNPAY_PAYMENT)
    public ResponseEntity<ResponseObject> getVNPayPayment(
            @PathVariable int orderId,
            @RequestParam(value = "vnp_Amount") String price,
            @RequestParam(value = "vnp_OrderInfo") String content,
            @RequestParam(value = "vnp_ResponseCode") String resCd)
    {
        PaymentDto dto = new PaymentDto();
            if (resCd.equals("00")) {
                dto.setPaymentContent(content);
                dto.setPaymentStatus("Success");
                dto.setPrice(Double.parseDouble(price));
                dto.setPaymentTime(LocalDateTime.now());
            } else {
                dto.setPaymentTime(LocalDateTime.now());
                dto.setPaymentContent(content);
                dto.setPrice(Double.parseDouble(price));
                dto.setPaymentStatus("Failed");
            }
        //Payment payment = PaymentConverter.INSTANCE.fromDtoToEntity(dto);
       // paymentRepository.save(payment);
        // PaymentResponse response = PaymentConverter.INSTANCE.fromEntityToResponse(payment);
        return ResponseEntity.ok().body(
                new ResponseObject("OK", "GET VNPAY PAYMENT SUCCESSFULLY", vnPayService.getVNPayPayment(dto, orderId))
        );
    }
}
