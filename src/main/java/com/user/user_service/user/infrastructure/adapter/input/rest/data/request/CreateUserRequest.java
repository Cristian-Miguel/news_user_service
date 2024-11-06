package com.user.user_service.user.infrastructure.adapter.input.rest.data.request;

import com.user.user_service.role.infrastructure.constant.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "Email cannot be null.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Password cannot be null.")
    private String password;

    @NotBlank(message = "Username cannot be empty.")
    private String username;

    @NotBlank(message = "First name cannot be null.")
    private String firstName;

    @NotBlank(message = "Last name cannot be null.")
    private String lastName;

    @NotNull(message = "The role cannot be null.")
    private RoleEnum role;

    @NotNull(message = "Birth date cannot be null.")
    @Past(message = "Birth date should be valid")
    private LocalDate birthDate;
}
