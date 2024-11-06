package com.user.user_service.role.application.port.output;

import com.user.user_service.role.domain.model.Role;
import com.user.user_service.role.infrastructure.constant.RoleEnum;

import java.util.Optional;

public interface RoleOutputPort {

    Optional<Role> findByEnumName(RoleEnum enumName);
}
