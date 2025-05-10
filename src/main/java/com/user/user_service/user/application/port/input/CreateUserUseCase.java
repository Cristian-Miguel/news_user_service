package com.user.user_service.user.application.port.input;

import com.user.user_service.user.domain.model.User;

public interface CreateUserUseCase {

    User createUser(User user, boolean isEvent);

    void createUserByEvent(String message);
}
