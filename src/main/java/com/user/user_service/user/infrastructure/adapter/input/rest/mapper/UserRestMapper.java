package com.user.user_service.user.infrastructure.adapter.input.rest.mapper;

import com.user.user_service.shared.domain.model.PageFormats;
import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.ChangeRoleRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.CreateUserRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.UpdateUserRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.UserFilterRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.CreateUserResponse;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.GetUserResponse;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.UpdateUserResponse;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    User toUser(CreateUserRequest createUserRequest);

    User toUser(UpdateUserRequest updateUserRequest);

    User toUser(GetUserResponse getUserResponse);

    User toUser(ChangeRoleRequest changeRoleRequest);

    CreateUserResponse toCreateUserResponse(User user);

    UpdateUserResponse toUpdateUserResponse(User user);

    GetUserResponse toGetUserResponse(User user);

    PageFormats toPageFormats(UserFilterRequest userFilterRequest);

    UserFilterRequest toUserFilterRequest(PageFormats pageFormats);
}
