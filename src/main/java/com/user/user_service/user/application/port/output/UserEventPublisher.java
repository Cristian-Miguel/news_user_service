package com.user.user_service.user.application.port.output;

import com.user.user_service.shared.infrastructure.constant.EventType;
import com.user.user_service.user.domain.event.UserCreatedEvent;
import com.user.user_service.user.domain.model.User;

public interface UserEventPublisher {

    void  publishUserUpdatesEvent(User user, EventType userEventType);

}
