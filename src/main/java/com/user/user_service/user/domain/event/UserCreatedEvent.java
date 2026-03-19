package com.user.user_service.user.domain.event;

import com.user.user_service.role.domain.model.Role;
import com.user.user_service.user.domain.model.User;

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
public class UserCreatedEvent {

    private Long id;

    private String uuid;

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private LocalDateTime createAt;

    private LocalDateTime loggerAt;

    private LocalDateTime updateAt;

    private Role role;

    private LocalDate birthDate;

    private LocalDateTime lockTime;

    private User adminUser;
}
