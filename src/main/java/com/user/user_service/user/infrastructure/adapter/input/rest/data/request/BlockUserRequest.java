package com.user.user_service.user.infrastructure.adapter.input.rest.data.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlockUserRequest {

    @NotBlank(message = "Reason cannot be null.")
    private String reason;

    @NotBlank(message = "LockTime cannot be null.")
    @Past(message = "LockTime should be valid")
    private LocalDateTime lockTime;

}
