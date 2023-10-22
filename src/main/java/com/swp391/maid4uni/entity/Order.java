package com.swp391.maid4uni.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * The type Order.
 */
@Entity
@Table(name = "ORDER_TBL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column
    double price;
    @Column
    String address;
    @Column
    Date time;
    @Column
    int duration;
    @Column(name = "period_Type")
    String periodType;
    @Column
    short logicalDeleteStatus;

    @ManyToOne
    @JoinColumn(name = "cutomer_id")
    Account customer;

    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderDetailList;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    Payment paymentMethod;
}
