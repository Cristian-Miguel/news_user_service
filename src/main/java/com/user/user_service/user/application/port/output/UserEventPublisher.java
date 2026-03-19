package com.user.user_service.user.application.port.output;

import com.user.user_service.shared.infrastructure.constant.EventType;

public interface UserEventPublisher {

    <T> void publishUserUpdatesEvent(T event, EventType userEventType);

}
