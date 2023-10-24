package com.swp391.maid4uni.request;

import com.swp391.maid4uni.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

/**
 * The type Update account request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAccountRequest {
    int id;
    String username;
    String password;
    String email;
    String phoneNumber;
    String fullName;
    Date dOB;
    String gender;
    String address;
    String img;
    Role role;
}
