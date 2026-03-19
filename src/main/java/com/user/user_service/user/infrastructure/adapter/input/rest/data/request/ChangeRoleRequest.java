package com.user.user_service.user.infrastructure.adapter.input.rest.data.request;

import org.hibernate.validator.constraints.UUID;

import com.user.user_service.role.infrastructure.constant.RoleEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequest {

    @NotBlank(message = "UuidUser cannot be null.")
    @UUID(message = "UuidUser should be valid")
    private String uuid;

    @NotNull(message = "The role cannot be null.")
    private RoleEnum role;

}
