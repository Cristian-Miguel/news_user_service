package com.user.user_service.user.infrastructure.adapter.input.rest;

import com.user.user_service.user.application.port.input.BlockUserUseCase;
import com.user.user_service.user.application.port.input.GetUserUseCase;
import com.user.user_service.user.application.port.input.UpdateUserUseCase;
import com.user.user_service.user.domain.model.Block;
import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.BlockUserRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.ChangeRoleRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.UpdateUserRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.UserFilterRequest;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.CreateUserResponse;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.GetUserResponse;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.response.UpdateUserResponse;
import com.user.user_service.role.application.port.input.ChangeRoleUserUseCase;
import com.user.user_service.shared.domain.model.PageFormats;
import com.user.user_service.shared.infrastructure.input.data.response.GenericResponse;
import com.user.user_service.user.infrastructure.adapter.input.rest.mapper.BlockRestMapper;
import com.user.user_service.user.infrastructure.adapter.input.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UpdateUserUseCase updateUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ChangeRoleUserUseCase changeRoleUserUseCase;
    private final BlockUserUseCase blockUserUseCase;

    private final  UserRestMapper userRestMapper;
    private final  BlockRestMapper blockRestMapper;

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
    
    @DeleteMapping("/{uuid}")
    public ResponseEntity<GenericResponse<Void>> deleteUser(
        @RequestHeader("Authorization") String token, @PathVariable String uuid) {

        updateUserUseCase.deleteUserByUuid(uuid, token, false);

        GenericResponse<Void> response = GenericResponse.<Void>builder()
                .success(true)
                .message(HttpStatus.NO_CONTENT.getReasonPhrase())
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PatchMapping("/change-role")
    public ResponseEntity<GenericResponse<CreateUserResponse>> changeUserRole(
        @RequestHeader("Authorization") String token,
        @RequestBody @Valid ChangeRoleRequest request
    ) {
        User mappingUser = userRestMapper.toUser(request);
        User user = changeRoleUserUseCase.changeRole(mappingUser, token, false);

        GenericResponse<CreateUserResponse> response = GenericResponse.<CreateUserResponse>builder()
                .success(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(userRestMapper.toCreateUserResponse(user))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{uuid}/block")
    public ResponseEntity<GenericResponse<String>> blockUser(
        @RequestHeader("Authorization") String token,
        @PathVariable String uuid,
        @RequestBody @Valid BlockUserRequest request
    ) {
        Block block = blockRestMapper.toBlock(request);
        String result = blockUserUseCase.blockUser(block, token, false);

        GenericResponse<String> response = GenericResponse.<String>builder()
                .success(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{uuid}/unblock")
    public ResponseEntity<GenericResponse<String>> unblockUser(
        @RequestHeader("Authorization") String token,
        @PathVariable String uuid
    ) {
        String result = blockUserUseCase.unblockUser(uuid, token, false);

        GenericResponse<String> response = GenericResponse.<String>builder()
                .success(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{uuid}/profile-picture")
    public ResponseEntity<GenericResponse<String>> getProfilePicture(
        @RequestHeader("Authorization") String token,
        @PathVariable String uuid) {
        // Aquí buscas al usuario y devuelves solo la columna de la URL de S3
        String url = getUserUseCase.getProfilePicture(uuid, token); 
        
        return ResponseEntity.ok(new GenericResponse<>(true, "Success", url));
    }

}