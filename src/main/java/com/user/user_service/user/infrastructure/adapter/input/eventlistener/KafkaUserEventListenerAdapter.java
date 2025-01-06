package com.user.user_service.user.infrastructure.adapter.input.eventlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.user_service.shared.domain.event.FormatEventResponse;
import com.user.user_service.shared.infrastructure.constant.EventType;
import com.user.user_service.user.application.port.input.CreateUserUseCase;
import com.user.user_service.user.application.port.input.UserEventListener;
import com.user.user_service.user.application.port.output.UserOutputPort;
import com.user.user_service.user.domain.event.UserCreatedEvent;
import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

@AllArgsConstructor
public class KafkaUserEventListenerAdapter implements UserEventListener {

    private final ObjectMapper objectMapper;
    private final CreateUserUseCase createUserUseCase;

    @Override
    @KafkaListener(topics = "user-updates", groupId = "user-group")
    public void onUserUpdates(String message) {
        try {

            FormatEventResponse eventResponse = objectMapper.readValue(message, FormatEventResponse.class);

            if (eventResponse.getEventType().equals(EventType.USER_CREATED))
                createUserUseCase.createUserByEvent(message);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
