package com.user.user_service.shared.infrastructure.input.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse<T> {

    private boolean success;
    private String message;
    private T data;
}
