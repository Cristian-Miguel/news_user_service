package com.user.user_service.user.infrastructure.adapter.input.eventlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.user_service.shared.domain.event.FormatEventResponse;
import com.user.user_service.shared.infrastructure.constant.EventType;
import com.user.user_service.user.application.port.input.CreateUserUseCase;
import com.user.user_service.user.application.port.input.UpdateUserUseCase;
import com.user.user_service.user.application.port.input.UserEventListener;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

@AllArgsConstructor
public class KafkaUserEventListenerAdapter implements UserEventListener {

    private final ObjectMapper objectMapper;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase userUpdateUseCase;

    @Override
    @KafkaListener(topics = "user-updates", groupId = "user-group")
    public void onUserUpdates(String message) {
        try {

            FormatEventResponse eventResponse = objectMapper.readValue(message, FormatEventResponse.class);

            if(eventResponse.getSource().equals("user-service"))
                return;

            if(eventResponse.getEventType().equals(EventType.USER_CREATED)){
                createUserUseCase.createUserByEvent(message);
            } else if(eventResponse.getEventType().equals(EventType.USER_UPDATE)) {
                userUpdateUseCase.updateUserByEvent(message);
            }
            // else if(eventResponse.getEventType().equals(EventType.USER_DELETE)) {
            //     userDeleteUseCase.onDeleteUser(message);
            // }
            // if (eventResponse.getEventType().equals(EventType.USER_CREATED))
            //     createUserUseCase.createUserByEvent(message);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
