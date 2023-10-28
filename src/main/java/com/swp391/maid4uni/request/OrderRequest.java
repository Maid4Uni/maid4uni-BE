package com.swp391.maid4uni.request;

import com.swp391.maid4uni.entity.Account;
import com.swp391.maid4uni.entity.OrderDetail;
import com.swp391.maid4uni.entity.Payment;
import com.swp391.maid4uni.enums.PeriodType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.Date;
import java.util.List;

public class OrderRequest {
    double price;
    String address;
    int duration;
    PeriodType periodType;
    AccountDutyRequest customer;
    Payment paymentMethod;
}