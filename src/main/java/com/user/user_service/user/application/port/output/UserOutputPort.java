package com.user.user_service.user.application.port.output;

import com.user.user_service.role.infrastructure.constant.RoleEnum;
import com.user.user_service.shared.domain.model.PageFormats;
import com.user.user_service.user.domain.model.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserOutputPort {

    User deleteUserByUuid(String uuid);

    User saveUser(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUuid(String uuid);

    boolean existByUsername(String username);

    boolean existByEmail(String email);

    Page<User> findAll(PageFormats pageFormats, RoleEnum roleEnum, Long userId);

}
