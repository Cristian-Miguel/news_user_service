package com.user.user_service.user.domain.model;

import com.user.user_service.role.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;

    private String uuid;

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private String profilePictureUrl;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private Role role;

    private LocalDate birthDate;

    private LocalDateTime lockTime;

    private User adminUser;
}
