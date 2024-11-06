package com.user.user_service.user.application.port.output;

import com.user.user_service.user.domain.model.User;

import java.util.Optional;

public interface UserOutputPort {

    User saveUser(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existByUsername(String username);

    boolean existByEmail(String email);

}
