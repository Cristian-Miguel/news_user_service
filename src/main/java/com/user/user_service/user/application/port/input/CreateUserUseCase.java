package com.user.user_service.user.application.port.input;

import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.CreateUserRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.CreateUserResponse;
import org.springframework.stereotype.Service;

public interface CreateUserUseCase {

    User createUser(User user, boolean isEvent);

    void createUserByEvent(String message);
}
