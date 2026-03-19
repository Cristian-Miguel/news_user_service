package com.user.user_service.role.application.port.input;

import com.user.user_service.user.domain.model.User;

public interface ChangeRoleUserUseCase {

    User changeRole(User userChangedRole, String token, boolean isEvent);

    void changeRoleByEvent(String message);

}
