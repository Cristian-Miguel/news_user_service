package com.user.user_service.shared.domain.event;

import com.user.user_service.shared.infrastructure.constant.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormatEventResponse<T> {
    private EventType eventType;

    private String source;

    private T payload;
}
