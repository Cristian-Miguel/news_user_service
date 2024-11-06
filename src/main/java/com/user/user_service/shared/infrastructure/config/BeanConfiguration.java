package com.user.user_service.shared.infrastructure.config;

import com.user.user_service.role.application.port.output.RoleOutputPort;
import com.user.user_service.role.infrastructure.adapter.output.persistence.RolePersistenceAdapter;
import com.user.user_service.role.infrastructure.adapter.output.persistence.mapper.RolePersistenceMapper;
import com.user.user_service.role.infrastructure.adapter.output.persistence.repository.RoleRepository;
import com.user.user_service.shared.infrastructure.constant.ErrorMessage;
import com.user.user_service.user.application.port.output.UserOutputPort;
import com.user.user_service.user.application.service.UserService;
import com.user.user_service.user.infrastructure.adapter.output.persistence.UserPersistenceAdapter;
import com.user.user_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.user.user_service.user.infrastructure.adapter.output.persistence.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfiguration {

    //Classes Out put config
    @Bean
    public UserService userService(final UserOutputPort userOutputPort, final RoleOutputPort roleOutputPort,
                                   final ErrorMessage errorMessage, final PasswordEncoder passwordEncoder) {
        return new UserService(userOutputPort, roleOutputPort, errorMessage, passwordEncoder);
    }

    @Bean
    public UserPersistenceAdapter userPersistenceAdapter(final UserRepository userRepository, final UserPersistenceMapper userPersistenceMapper){
        return new UserPersistenceAdapter(userRepository, userPersistenceMapper);
    }

    @Bean
    public RolePersistenceAdapter roleOutputPort(final RoleRepository roleRepository, final RolePersistenceMapper rolePersistenceMapper){
        return new RolePersistenceAdapter(roleRepository, rolePersistenceMapper);
    }

}
