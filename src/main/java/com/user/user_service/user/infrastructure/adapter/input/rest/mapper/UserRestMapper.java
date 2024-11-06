package com.user.user_service.user.infrastructure.adapter.input.rest.mapper;

import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.CreateUserRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.CreateUserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    User toUser(CreateUserRequest createUserRequest);

    CreateUserResponse toCreateUserResponse(User user);
}
