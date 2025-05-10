package com.user.user_service.user.infrastructure.adapter.input.rest;

import com.user.user_service.user.application.port.input.CreateUserUseCase;
import com.user.user_service.user.application.port.input.GetUserUseCase;
import com.user.user_service.user.application.port.input.UpdateUserUseCase;
import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.CreateUserRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.UpdateUserRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.UserFilterRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.CreateUserResponse;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.GetUserResponse;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.UpdateUserResponse;
import com.user.user_service.shared.domain.model.PageFormats;
import com.user.user_service.shared.infrastructure.input.data.response.GenericResponse;
import com.user.user_service.user.infrastructure.adapter.input.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final GetUserUseCase getUserUseCase;

    private final  UserRestMapper userRestMapper;

    @PostMapping
    public ResponseEntity<GenericResponse<CreateUserResponse>> createUser(@RequestBody @Valid CreateUserRequest request){
        User user = userRestMapper.toUser(request);
        user.getRole().setEnumName(request.getRole());
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

    @PutMapping
    public ResponseEntity<GenericResponse<UpdateUserResponse>> updateUser(@RequestHeader("Authorization") String token, @RequestBody @Valid UpdateUserRequest request){
        User user = userRestMapper.toUser(request);
        user.getRole().setEnumName(request.getRole());
        user = updateUserUseCase.updateUser(user, false, token);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        GenericResponse.<UpdateUserResponse>builder()
                                .success(true)
                                .message(HttpStatus.OK.getReasonPhrase())
                                .data(
                                        userRestMapper.toUpdateUserResponse(user)
                                )
                                .build()
                );
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<GenericResponse<GetUserResponse>> getUserByUuid(
        @RequestHeader("Authorization") String token, @PathVariable String uuid) {

        User user = getUserUseCase.getUserById(uuid, token);
        GetUserResponse getUser = userRestMapper.toGetUserResponse(user);

        GenericResponse<GetUserResponse> response = GenericResponse.<GetUserResponse>builder()
                .success(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(getUser)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<GenericResponse<Page<CreateUserResponse>>> getUserByPaginated(
        @RequestHeader("Authorization") String token,
        @Valid UserFilterRequest params) {
        PageFormats pageFormats = userRestMapper.toPageFormats(params);

        Page<CreateUserResponse> users =  getUserUseCase.getUsers(pageFormats, token).map(userRestMapper::toCreateUserResponse);

        GenericResponse<Page<CreateUserResponse>> response = GenericResponse.<Page<CreateUserResponse>>builder()
                .success(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(users)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    

}