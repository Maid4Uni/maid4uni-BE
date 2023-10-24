package com.swp391.maid4uni.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * The type Package.
 */
@Entity
@Table(name = "PACKAGE_TBL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    int id;
    @Column(name = "package_name", nullable = false)
    String name;
    @Column
    String description;
    @Column(nullable = false)
    double price;
    @Column(name = "created_at", nullable = false)
    Date createdAt;
    @Column(name = "updated_at")
    Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    Account creator;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "package_service"
            , joinColumns = @JoinColumn(name = "package_id")
            , inverseJoinColumns = @JoinColumn(name = "service_id"))
    List<Service> serviceList;
}
