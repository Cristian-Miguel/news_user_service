package com.user.user_service.user.infrastructure.adapter.output.eventpublisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.user_service.shared.domain.event.FormatEventResponse;
import com.user.user_service.shared.infrastructure.constant.EventType;
import com.user.user_service.user.application.port.output.UserEventPublisher;
import com.user.user_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
public class KafkaUserEventPublisherAdapter implements UserEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserPersistenceMapper userPersistenceMapper;
    private final ObjectMapper objectMapper;

    @Override
    public <T> void publishUserUpdatesEvent(T event, EventType userEventType) {
        try {
            FormatEventResponse<T> messageFormat = new FormatEventResponse<>(
                    userEventType,
                    "user-service",
                    event
            );

            String jsonFormat = objectMapper.writeValueAsString(messageFormat);

            kafkaTemplate.send("user-updates", jsonFormat);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting user to JSON: " + e.getMessage());
        }
    }
}
