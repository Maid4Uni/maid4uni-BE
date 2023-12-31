package com.swp391.maid4uni.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

/**
 * The type Service.
 */
@Entity
@Table(name = "SERVICE_TBL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    int id;
    @Column(name = "service_name", nullable = false)
    String name;
    @Column
    String description;
    @Column(nullable = false)
    double price;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    Date updateAt;
    @Column
    short logicalDeleteStatus;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    Account creator;

    @ManyToMany(mappedBy = "serviceList", fetch = FetchType.LAZY)
    List<Package> belongedPackage;

    @OneToMany(mappedBy = "service")
    List<Task> taskList;
}
