package com.user.user_service.user.application.port.input;

import com.user.user_service.user.domain.event.UserCreatedEvent;

public interface UserEventListener {

    void onUserUpdates(String message);
}
