package com.user.user_service.user.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.user_service.role.application.port.output.RoleOutputPort;
import com.user.user_service.role.domain.exception.RoleNotFoundException;
import com.user.user_service.role.domain.model.Role;
import com.user.user_service.role.infrastructure.constant.RoleEnum;
import com.user.user_service.shared.domain.event.FormatEventResponse;
import com.user.user_service.shared.domain.exception.AccessDeniedException;
import com.user.user_service.shared.domain.exception.IllegalFilterException;
import com.user.user_service.shared.domain.model.PageFormats;
import com.user.user_service.shared.infrastructure.constant.ErrorMessage;
import com.user.user_service.shared.infrastructure.constant.EventType;
import com.user.user_service.shared.infrastructure.constant.FilterTypeAllowed;
import com.user.user_service.shared.infrastructure.utils.FilterValueParse;
import com.user.user_service.shared.infrastructure.utils.JwtUtils;
import com.user.user_service.shared.infrastructure.utils.RoleUtil;
import com.user.user_service.user.application.port.input.CreateUserUseCase;
import com.user.user_service.user.application.port.input.GetUserUseCase;
import com.user.user_service.user.application.port.input.UpdateUserUseCase;
import com.user.user_service.user.application.port.output.UserEventPublisher;
import com.user.user_service.user.application.port.output.UserOutputPort;
import com.user.user_service.user.domain.event.UserCreatedEvent;
import com.user.user_service.user.domain.event.UserUpdateEvent;
import com.user.user_service.user.domain.exception.UserAlreadyExistsException;
import com.user.user_service.user.domain.exception.UserNotFoundException;
import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@AllArgsConstructor
public class UserService implements CreateUserUseCase, UpdateUserUseCase, GetUserUseCase {

    private final UserOutputPort userOutputPort;
    private final RoleOutputPort roleOutputPort;

    private final ErrorMessage errorMessage;
    private final PasswordEncoder passwordEncoder;

    private UserEventPublisher userEventPublisher;

    private final ObjectMapper objectMapper;
    private final UserPersistenceMapper userPersistenceMapper;

    private final JwtUtils jwtUtils;
    private final RoleUtil roleUtil;

    @Override
    public User createUser(User user, boolean isEvent) {

        if(userOutputPort.existByUsername(user.getUsername())){
            if (isEvent) {
                System.out.println("User " + user.getUsername() + " already exists. Skipping creation from event.");
                return user; 
            }
            throw new UserAlreadyExistsException(errorMessage.buildUsernameTakenError(user.getUsername()));
        }

        if (userOutputPort.existByEmail(user.getEmail())){
            if (isEvent) {
                System.out.println("Email " + user.getEmail() + " already exists. Skipping creation from event.");
                return user;
            }
            throw new UserAlreadyExistsException(errorMessage.buildUsernameTakenError(user.getEmail()));
        }

        Role role = roleOutputPort.findByEnumName(user.getRole().getEnumName())
                .orElseThrow(
                        () -> new RoleNotFoundException(errorMessage.ROLE_NOT_FOUND)
                );

        user.setRole(role);
        if (!isEvent) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        user.setCreateAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());
        user.setLoggerAt(LocalDateTime.now());

        user = userOutputPort.saveUser(user);

        if (!isEvent)
            userEventPublisher.publishUserUpdatesEvent(user, EventType.USER_CREATED);

        return user;
    }

    @Override
    public void createUserByEvent(String message) {
        try {
            FormatEventResponse<UserCreatedEvent> eventResponse = objectMapper.readValue(
                    message,
                    new TypeReference<FormatEventResponse<UserCreatedEvent>>() {}
            );

            User user = userPersistenceMapper.toUser(eventResponse.getPayload());

            createUser(user, true);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User updateUser(User user, boolean isEvent, String token) {
        if(userOutputPort.existByUsername(user.getUsername()))
            throw new UserAlreadyExistsException(errorMessage.buildUsernameTakenError(user.getUsername()));

        if (userOutputPort.existByEmail(user.getEmail()))
            throw new UserAlreadyExistsException(errorMessage.buildUsernameTakenError(user.getEmail()));

        if(isEvent){
            
            Role role = roleOutputPort.findByEnumName(user.getRole().getEnumName())
                .orElseThrow(
                    () -> new RoleNotFoundException(errorMessage.ROLE_NOT_FOUND)
                );

            user.setRole(role);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUpdateAt(LocalDateTime.now());

            user = userOutputPort.saveUser(user);

            return user;
        }

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            throw new AccessDeniedException("Invalid token");
        }

        Claims claims = jwtUtils.getAllClaims(token);
        String uuidUser = claims.get("uuid", String.class);
        RoleEnum roleEnum = roleUtil.getRoleFromToken(token);
        String username = user.getUsername();
        
        if(!uuidUser.equals(user.getUuid()) &&
            (roleEnum.equals(RoleEnum.READERS) || roleEnum.equals(RoleEnum.PREMIUM) ||
            roleEnum.equals(RoleEnum.JOURNALIST) || roleEnum.equals(RoleEnum.PUBLISHER) ||
            roleEnum.equals(RoleEnum.NEWS_ENTERPRICE))
        ) {
            throw new AccessDeniedException(errorMessage.buildAccessDeniedByRoleError(roleEnum));
        }

        User subUserNewsCompany = null;
        if(roleEnum.equals(RoleEnum.NEWS_ENTERPRICE)){
            subUserNewsCompany = userOutputPort.findByEmail(user.getEmail())
                .orElseThrow(
                    () -> new UserNotFoundException(errorMessage.buildUsernameDontExistError(username))
                );
            if(!user.getUuid().equals(subUserNewsCompany.getAdminUser().getUuid())){
                throw new AccessDeniedException(errorMessage.INVALID_UPDATE_SUBUSER);
            }
        }

        Role role = roleOutputPort.findByEnumName(user.getRole().getEnumName())
                .orElseThrow(
                        () -> new RoleNotFoundException(errorMessage.ROLE_NOT_FOUND)
                );

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUpdateAt(LocalDateTime.now());

        user = userOutputPort.saveUser(user);

        if (!isEvent)
            userEventPublisher.publishUserUpdatesEvent(user, EventType.USER_UPDATE);

        return user;
    }

    @Override
    public void updateUserByEvent(String message) {
        try {
            FormatEventResponse<UserUpdateEvent> eventResponse = objectMapper.readValue(
                message,
                new TypeReference<FormatEventResponse<UserUpdateEvent>>() {}
            );

            User user = userPersistenceMapper.toUser(eventResponse.getPayload());

            updateUser(user, true, null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserById(String uuid, String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            throw new AccessDeniedException("Invalid token");
        }

        Claims claims = jwtUtils.getAllClaims(token);
        String email = claims.get("email", String.class);
        RoleEnum roleEnum = roleUtil.getRoleFromToken(token);
        User userRequest = userOutputPort.findByEmail(email)
            .orElseThrow(
                () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(uuid))
            );
        User userResponse = userOutputPort.findByUuid(uuid)
            .orElseThrow(
                () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(uuid))
            );
        
        String uuidUser = userRequest.getUuid();
        String uuidAdmin = userResponse.getAdminUser() != null ? userResponse.getAdminUser().getUuid() : null;

        if(!uuidUser.equals(uuid) && 
            (!roleEnum.equals(RoleEnum.NEWS_ENTERPRICE) && !roleEnum.equals(RoleEnum.ADMINISTRATOR)) 
        ) {
            throw new AccessDeniedException(errorMessage.buildAccessDeniedByRoleError(roleEnum));
        }

        if(roleEnum.equals(RoleEnum.NEWS_ENTERPRICE) && !uuidUser.equals(uuidAdmin) && !uuidUser.equals(uuid)){
            throw new AccessDeniedException(errorMessage.INVALID_GET_SUBUSER);
        }

        return userResponse;
    }

    @Override
    public Page<User> getUsers(PageFormats pageFormats, String token) {
        
        roleUtil.checkValidRoleAccessResource(token, new RoleEnum[] {RoleEnum.NEWS_ENTERPRICE, RoleEnum.ADMINISTRATOR});

        FilterTypeAllowed[] typeFilterAllow = FilterTypeAllowed.values();

        for (FilterTypeAllowed fieldType : typeFilterAllow) {
            if (fieldType.getShortValue().equals(pageFormats.getFilterType())) {
                for (String filterType : fieldType.getAllowedField()) {
                    String filterField = FilterValueParse.findFieldByClass(
                        User.class,
                        pageFormats.getFilterField()
                    );
                    if (filterType.equalsIgnoreCase(filterField)) {
                        pageFormats.setFilterType(fieldType.getShortValue());
                        RoleEnum roleEnum = roleUtil.getRoleFromToken(token);
                        String username = jwtUtils.getUsernameFromToken(token.substring(7));
                        User user = userOutputPort.findByUsername(username)
                            .orElseThrow(
                                () -> new UserNotFoundException(errorMessage.buildUsernameDontExistError(username))
                            );
                        
                        return userOutputPort.findAll(pageFormats, roleEnum, user.getId());
                    }
                }
            } 
        }

        throw new IllegalFilterException("Invalid filter type: " + pageFormats.getFilterType());
    }

    

}
