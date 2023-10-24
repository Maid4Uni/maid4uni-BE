package com.swp391.maid4uni.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

/**
 * The type Account.
 */
@Entity
@Table(name = "ACCOUNT_TBL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    int id;

    @Column(nullable = false, unique = true, name = "username")
    String username;
    @Column(nullable = false)
    String password;
    @Column(unique = true)
    String email;
    @Column(unique = true, name = "phone_number")
    String phoneNumber;
    @Column
    String fullName;
    @Column(name = "dob")
    Date dOB;
    @Column
    String gender;
    @Column
    String address;
    @Column
    String img;
    @Column()
    short logicalDeleteStatus;
    @Enumerated
    Role role;

    @OneToMany(mappedBy = "sender")
    List<Feedback> sentFeedback;

    @OneToMany(mappedBy = "receiver")
    List<Feedback> receivedFeedback;

    @OneToMany(mappedBy = "creator")
    List<Package> createdPackage;

    @OneToMany(mappedBy = "customer")
    List<Order> orderedList;

    @ManyToMany(mappedBy = "staffs", fetch = FetchType.LAZY)
    List<Task> taskList;


}
