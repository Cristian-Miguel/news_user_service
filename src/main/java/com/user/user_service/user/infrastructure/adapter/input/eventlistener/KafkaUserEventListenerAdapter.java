package com.user.user_service.user.infrastructure.adapter.input.eventlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.user_service.role.application.port.input.ChangeRoleUserUseCase;
import com.user.user_service.shared.domain.event.FormatEventResponse;
import com.user.user_service.shared.infrastructure.constant.EventType;
import com.user.user_service.user.application.port.input.BlockUserUseCase;
import com.user.user_service.user.application.port.input.CreateUserUseCase;
import com.user.user_service.user.application.port.input.UpdateUserUseCase;
import com.user.user_service.user.application.port.input.UserEventListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;

@AllArgsConstructor
@Slf4j
public class KafkaUserEventListenerAdapter implements UserEventListener {

    private final ObjectMapper objectMapper;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase userUpdateUseCase;
    private final ChangeRoleUserUseCase changeRoleUserUseCase;
    private final BlockUserUseCase blockUserUseCase;

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
            } else if(eventResponse.getEventType().equals(EventType.USER_DELETE)) {
                userUpdateUseCase.deleteUserByEvent(message);
            }  else if(eventResponse.getEventType().equals(EventType.USER_BLOCK)) {
                userUpdateUseCase.updateUserByEvent(message);
            } else if(eventResponse.getEventType().equals(EventType.USER_UNBLOCK)) {
                blockUserUseCase.blockUserByEvent(message);
            } else if(eventResponse.getEventType().equals(EventType.USER_UNBLOCK)) {
                blockUserUseCase.unblockUserByEvent(message);
            } else if(eventResponse.getEventType().equals(EventType.USER_CHANGE_ROLE)) {
                changeRoleUserUseCase.changeRoleByEvent(message);
            }

        } catch (JsonProcessingException e) {
            log.error("Error deserializando mensaje Kafka: {}", message, e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Error inesperado procesando mensaje: {}", message, e);
            throw new RuntimeException(e);
        }
    }

}
