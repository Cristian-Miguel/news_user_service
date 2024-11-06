package com.user.user_service.user.infrastructure.adapter.input.rest.data.response;

import com.user.user_service.role.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserResponse {

    private Long id;

    private String uuid;

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private Role role;

    private LocalDate birthDate;

}
