package com.swp391.maid4uni.service;

import com.swp391.maid4uni.dto.OrderDto;
import com.swp391.maid4uni.request.OrderRequest;
import com.swp391.maid4uni.request.UpdateOrderRequest;
import com.swp391.maid4uni.response.OrderResponse;
import com.swp391.maid4uni.response.ResponseObject;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getOrderInfoByCustomer(int id);

    OrderResponse createOrder(OrderDto dto);

    ResponseObject updateOrderStatus(UpdateOrderRequest request);
}
