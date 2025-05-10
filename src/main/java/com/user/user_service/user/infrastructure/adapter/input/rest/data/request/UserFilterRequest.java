package com.user.user_service.user.infrastructure.adapter.input.rest.data.request;

import com.user.user_service.shared.infrastructure.config.AllowedFields;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterRequest {

    @Min(value = 0, message = "Page number must be greater than or equal to 0")
    @Builder.Default
    private int page = 0;
    
    @Max(value = 100, message = "Page size must be less than or equal to 100")
    @Min(value = 1, message = "Page size must be greater than or equal to 1")
    @Builder.Default
    private int size = 10;
    
    @Pattern(regexp = "asc|desc", message = "Order type must be either 'asc' or 'desc'")
    @Builder.Default
    private String orderType = "asc";

    @Pattern(regexp = "like|eq|ne|gt|lt|ge|le|def", message = "Filter type must be one of the following: like, eq, ne, gt, lt, ge, le")
    @Builder.Default
    private String filterType = "def";

    @Builder.Default
    private String filterValue = "";

    @AllowedFields(
        value = {"id", "username", "email", "firstName", "lastName", "role", "birthDate", "createdAt", "updatedAt"},
        message = "Order field must be one of: username, email, createdAt"
    )
    @Builder.Default
    private String orderField = "id";

    @AllowedFields(
        value = {"id", "username", "email", "firstName", "lastName", "role", "birthDate", "createdAt", "updatedAt"},
        message = "Filter field must be one of: username, email, createdAt"
    )
    @Builder.Default
    private String filterField="id";

}
