package com.user.user_service.user.application.service;

import com.user.user_service.role.application.port.output.RoleOutputPort;
import com.user.user_service.role.domain.exception.RoleNotFoundException;
import com.user.user_service.role.domain.model.Role;
import com.user.user_service.role.infrastructure.constant.RoleEnum;
import com.user.user_service.shared.infrastructure.constant.ErrorMessage;
import com.user.user_service.user.application.port.input.CreateUserUseCase;
import com.user.user_service.user.application.port.output.UserOutputPort;
import com.user.user_service.user.domain.exception.UserAlreadyExistsException;
import com.user.user_service.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@AllArgsConstructor
public class UserService implements CreateUserUseCase {

    private final UserOutputPort userOutputPort;
    private final RoleOutputPort roleOutputPort;

    private final ErrorMessage errorMessage;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {

        if(userOutputPort.existByUsername(user.getUsername()))
            throw new UserAlreadyExistsException(errorMessage.buildUsernameTakenError(user.getUsername()));

        if (userOutputPort.existByEmail(user.getEmail()))
            throw new UserAlreadyExistsException(errorMessage.buildUsernameTakenError(user.getEmail()));

        Role role = roleOutputPort.findByEnumName(RoleEnum.valueOf(user.getRole().getName().toUpperCase()))
                .orElseThrow(
                        () -> new RoleNotFoundException(errorMessage.ROLE_NOT_FOUND)
                );

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());
        user.setLoggerAt(LocalDateTime.now());

        user = userOutputPort.saveUser(user);

        return user;
    }

}
