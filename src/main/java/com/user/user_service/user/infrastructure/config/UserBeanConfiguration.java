package com.user.user_service.user.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.user_service.role.application.port.output.RoleOutputPort;
import com.user.user_service.shared.infrastructure.constant.ErrorMessage;
import com.user.user_service.user.application.port.input.CreateUserUseCase;
import com.user.user_service.user.application.port.output.UserEventPublisher;
import com.user.user_service.user.application.port.output.UserOutputPort;
import com.user.user_service.user.application.service.UserService;
import com.user.user_service.user.infrastructure.adapter.input.eventlistener.KafkaUserEventListenerAdapter;
import com.user.user_service.user.infrastructure.adapter.output.eventpublisher.KafkaUserEventPublisherAdapter;
import com.user.user_service.user.infrastructure.adapter.output.persistence.UserPersistenceAdapter;
import com.user.user_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.user.user_service.user.infrastructure.adapter.output.persistence.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserBeanConfiguration {

    @Bean
    public UserService userService(final UserOutputPort userOutputPort, final RoleOutputPort roleOutputPort,
                                   final ErrorMessage errorMessage, final PasswordEncoder passwordEncoder,
                                   final UserEventPublisher userEventPublisher,
                                   final ObjectMapper objectMapper, final UserPersistenceMapper userPersistenceMapper) {
        return new UserService(
                userOutputPort, roleOutputPort,
                errorMessage, passwordEncoder,
                userEventPublisher, objectMapper,
                userPersistenceMapper);
    }

    @Bean
    public UserPersistenceAdapter userPersistenceAdapter(final UserRepository userRepository, final UserPersistenceMapper userPersistenceMapper){
        return new UserPersistenceAdapter(userRepository, userPersistenceMapper);
    }

    @Bean
    public KafkaUserEventPublisherAdapter userEventPublisherAdapter(
            final KafkaTemplate<String, String> kafkaTemplate,
            final ObjectMapper objectMapper,
            final UserPersistenceMapper userPersistenceMapper
    ){
        return new KafkaUserEventPublisherAdapter(kafkaTemplate, userPersistenceMapper, objectMapper);
    }

    @Bean
    public KafkaUserEventListenerAdapter userEventListenerAdapter(
            final ObjectMapper objectMapper,
            final CreateUserUseCase createUserUseCase
    ){
        return new KafkaUserEventListenerAdapter(objectMapper, createUserUseCase);
    }
}
