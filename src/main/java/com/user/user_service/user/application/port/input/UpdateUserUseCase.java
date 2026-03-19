package com.user.user_service.user.application.port.input;

import com.user.user_service.user.domain.model.User;

public interface UpdateUserUseCase {

    User updateUser(User user, boolean isEvent, String role);

    void updateUserByEvent(String message);

    boolean deleteUserByUuid(String uuid, String token, boolean isEvent);

    void deleteUserByEvent(String message);
}
