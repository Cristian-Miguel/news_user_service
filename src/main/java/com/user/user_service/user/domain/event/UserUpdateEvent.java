package com.user.user_service.user.domain.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.user.user_service.role.domain.model.Role;
import com.user.user_service.user.domain.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateEvent {

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
