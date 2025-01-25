package com.user.user_service.user.infrastructure.adapter.input.rest;

import com.user.user_service.role.infrastructure.constant.RoleEnum;
import com.user.user_service.user.application.port.input.CreateUserUseCase;
import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.CreateUserRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.CreateUserResponse;
import com.user.user_service.shared.infrastructure.input.data.response.GenericResponse;
import com.user.user_service.user.infrastructure.adapter.input.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    private final  UserRestMapper userRestMapper;

    @PostMapping
    public ResponseEntity<GenericResponse<CreateUserResponse>> createUser(@RequestBody @Valid CreateUserRequest request){
        User user = userRestMapper.toUser(request);
        user = createUserUseCase.createUser(user, false);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        GenericResponse.<CreateUserResponse>builder()
                            .success(true)
                            .message(HttpStatus.CREATED.getReasonPhrase())
                            .data(
                                    userRestMapper.toCreateUserResponse(user)
                            )
                            .build()
                );
    }

}
