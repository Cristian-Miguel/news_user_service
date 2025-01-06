package com.user.user_service.user.infrastructure.adapter.output.persistence.mapper;

import com.user.user_service.user.domain.event.UserCreatedEvent;
import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    UserEntity toUserEntity(User user);

    User toUser(UserEntity userEntity);

    UserCreatedEvent toUserCreatedEvent(User user);

    User toUser(UserCreatedEvent userCreatedEvent);
}
