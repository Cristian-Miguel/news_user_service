package com.user.user_service.user.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.user_service.role.application.port.input.ChangeRoleUserUseCase;
import com.user.user_service.role.application.port.output.RoleOutputPort;
import com.user.user_service.role.domain.exception.RoleNotFoundException;
import com.user.user_service.role.domain.model.Role;
import com.user.user_service.role.infrastructure.constant.RoleEnum;
import com.user.user_service.shared.domain.exception.AccessDeniedException;
import com.user.user_service.shared.domain.exception.IllegalFilterException;
import com.user.user_service.shared.domain.model.PageFormats;
import com.user.user_service.shared.infrastructure.constant.ErrorMessage;
import com.user.user_service.shared.infrastructure.constant.EventType;
import com.user.user_service.shared.infrastructure.constant.FilterTypeAllowed;
import com.user.user_service.shared.infrastructure.utils.FilterValueParse;
import com.user.user_service.shared.infrastructure.utils.JwtUtils;
import com.user.user_service.shared.infrastructure.utils.RoleUtil;
import com.user.user_service.user.application.port.input.BlockUserUseCase;
import com.user.user_service.user.application.port.input.CreateUserUseCase;
import com.user.user_service.user.application.port.input.GetUserUseCase;
import com.user.user_service.user.application.port.input.UpdateUserUseCase;
import com.user.user_service.user.application.port.output.UserEventPublisher;
import com.user.user_service.user.application.port.output.UserOutputPort;
import com.user.user_service.user.domain.event.UserBlockEvent;
import com.user.user_service.user.domain.event.UserChangeRoleEvent;
import com.user.user_service.user.domain.event.UserCreatedEvent;
import com.user.user_service.user.domain.event.UserDeleteEvent;
import com.user.user_service.user.domain.event.UserUnblockEvent;
import com.user.user_service.user.domain.event.UserUpdateEvent;
import com.user.user_service.user.domain.exception.UserAlreadyExistsException;
import com.user.user_service.user.domain.exception.UserNotFoundException;
import com.user.user_service.user.domain.model.Block;
import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@AllArgsConstructor
public class UserService implements CreateUserUseCase, UpdateUserUseCase, GetUserUseCase, ChangeRoleUserUseCase, BlockUserUseCase {

    private final UserOutputPort userOutputPort;
    private final RoleOutputPort roleOutputPort;

    private final ErrorMessage errorMessage;

    private final UserEventPublisher userEventPublisher;

    private final ObjectMapper objectMapper;
    private final UserPersistenceMapper userPersistenceMapper;

    private final JwtUtils jwtUtils;
    private final RoleUtil roleUtil;

    @Override
    @Transactional
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

        user.setCreateAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());

        user = userOutputPort.saveUser(user);

        if (!isEvent)
            userEventPublisher.publishUserUpdatesEvent(user, EventType.USER_CREATED);

        return user;
    }

    @Override
    @Transactional
    public void createUserByEvent(String message) {
            UserCreatedEvent eventPayload = (UserCreatedEvent) changeMessageEventToObject(message, UserCreatedEvent.class);

            User user = userPersistenceMapper.toUser(eventPayload);

            createUser(user, true);
    }

    @Override
    @Transactional
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
        user.setUpdateAt(LocalDateTime.now());

        user = userOutputPort.saveUser(user);

        if (!isEvent)
            userEventPublisher.publishUserUpdatesEvent(user, EventType.USER_UPDATE);

        return user;
    }

    @Override
    @Transactional
    public void updateUserByEvent(String message) {
            UserUpdateEvent eventPayload = (UserUpdateEvent) changeMessageEventToObject(message, UserUpdateEvent.class);

            User user = userPersistenceMapper.toUser(eventPayload);

            updateUser(user, true, null);
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

    @Override
    @Transactional
    public boolean deleteUserByUuid(String uuid, String token, boolean isEvent) {
        User userToDelete = userOutputPort.findByUuid(uuid)
            .orElseThrow(
                () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(uuid))
            );

        if(!isEvent){
            RoleEnum roleEnum = roleUtil.getRoleFromToken(token);
            String uuidToken = jwtUtils.getUuidFromToken(token);

            User userAdmin = userOutputPort.findByUuid(uuidToken)
                .orElseThrow(
                    () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(uuidToken))
                );

            boolean isAdmin = roleEnum.equals(RoleEnum.ADMINISTRATOR);
            boolean isNewsEnterprise = roleEnum.equals(RoleEnum.NEWS_ENTERPRICE) && userToDelete.getAdminUser().getId().equals(userAdmin.getId());
            boolean isSameUser = uuidToken.equals(uuid);
            
            if(!isAdmin && !isNewsEnterprise && !isSameUser){
                throw new AccessDeniedException(errorMessage.buildAccessDeniedByRoleError(roleEnum));
            }
        }

        User userDelete = userOutputPort.deleteUserByUuid(uuid);

        if (!isEvent) {
            UserDeleteEvent userDeleteEvent = userPersistenceMapper.toUserDeleteEvent(userDelete);
            userEventPublisher.publishUserUpdatesEvent(userDeleteEvent, EventType.USER_DELETE);
        }

        return userDelete != null;
    }

    @Override
    public void deleteUserByEvent(String message) {
            UserDeleteEvent eventPayload = (UserDeleteEvent) changeMessageEventToObject(message, UserDeleteEvent.class);

            User user = userPersistenceMapper.toUser(eventPayload);

            deleteUserByUuid(user.getUuid(), null, true);
    }

    @Override
    @Transactional
    public User changeRole(User userChangedRole, String token, boolean isEvent) {
        User user = userOutputPort.findByUuid(userChangedRole.getUuid())
            .orElseThrow(
                () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(userChangedRole.getUuid()))
            );
        
        if (!isEvent) {
            roleUtil.checkValidRoleAccessResource(token, new RoleEnum[] {RoleEnum.ADMINISTRATOR, RoleEnum.NEWS_ENTERPRICE});
            String userUuid = jwtUtils.getUuidFromToken(token);

            User userAdmin = userOutputPort.findByUuid(userUuid)
                .orElseThrow(
                    () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(userUuid))
                );

            RoleEnum roleAdmin = userAdmin.getRole().getEnumName();
            RoleEnum roleUser  = user.getRole().getEnumName();
            
            boolean hasAccessAdmin = (roleAdmin.equals(RoleEnum.ADMINISTRATOR));
            boolean hasAccessNewsEnterprise = (roleUser.equals(RoleEnum.NEWS_ENTERPRICE) && user.getAdminUser().getId() == userAdmin.getId());

            if(!hasAccessAdmin && !hasAccessNewsEnterprise){
                throw new AccessDeniedException(errorMessage.buildAccessDeniedByRoleError(roleAdmin));
            }
        }
        
        Role role = roleOutputPort.findByEnumName(userChangedRole.getRole().getEnumName())
            .orElseThrow(
                () -> new RoleNotFoundException(errorMessage.ROLE_NOT_FOUND)
            );

        user.setRole(role);
        user.setUpdateAt(LocalDateTime.now());

        User updatedUser = userOutputPort.saveUser(user);

        if (!isEvent) {
            UserChangeRoleEvent userChangeRoleEvent = userPersistenceMapper.toUserChangeRoleEvent(updatedUser);
            userEventPublisher.publishUserUpdatesEvent(userChangeRoleEvent, EventType.USER_CHANGE_ROLE);
        }

        return updatedUser;
    }

    @Override
    public void changeRoleByEvent(String message) {
            UserChangeRoleEvent eventPayload = (UserChangeRoleEvent) changeMessageEventToObject(message, UserChangeRoleEvent.class);

            User user = userPersistenceMapper.toUser(eventPayload);

            changeRole(user, null, true);
    }

    @Override
    @Transactional
    public String blockUser(Block block, String token, boolean isEvent) {
        User user = userOutputPort.findByUuid(block.getUser().getUuid())
        .orElseThrow(
            () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(block.getUser().getUuid()))
        );

        if (!isEvent) {
            roleUtil.checkValidRoleAccessResource(token, new RoleEnum[] {RoleEnum.ADMINISTRATOR});
            String userUuid = jwtUtils.getUuidFromToken(token);

            User userAdmin = userOutputPort.findByUuid(userUuid)
                .orElseThrow(
                    () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(userUuid))
                );

            if (!userAdmin.getRole().getEnumName().equals(RoleEnum.ADMINISTRATOR)){
                throw new AccessDeniedException("Access denied for role: " + userAdmin.getRole().getEnumName());
            }
        }

        user.setLockTime(block.getLockTime());
        user.setUpdateAt(LocalDateTime.now());

        User updatedUser = userOutputPort.saveUser(user);

        if (!isEvent) {
            UserUpdateEvent userUpdateEvent = userPersistenceMapper.toUserUpdateEvent(updatedUser);
            userEventPublisher.publishUserUpdatesEvent(userUpdateEvent, EventType.USER_BLOCK);
        }

        return "User with UUID " + user.getUuid() + " has been blocked.";
    }

    @Override
    public void blockUserByEvent(String message) {
            UserBlockEvent eventPayload = (UserBlockEvent) changeMessageEventToObject(message, UserBlockEvent.class);

            User user = userPersistenceMapper.toUser(eventPayload);

            changeRole(user, null, true);
    }

    @Override
    @Transactional
    public String unblockUser(String userUuid, String token, boolean isEvent) {
        User user = userOutputPort.findByUuid(userUuid)
            .orElseThrow(
                () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(userUuid))
            );

        if (!isEvent) {
            roleUtil.checkValidRoleAccessResource(token, new RoleEnum[] {RoleEnum.ADMINISTRATOR});
            String userUuidAdmin = jwtUtils.getUuidFromToken(token);

            User userAdmin = userOutputPort.findByUuid(userUuidAdmin)
                .orElseThrow(
                    () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(userUuidAdmin))
                );

            if (!userAdmin.getRole().getEnumName().equals(RoleEnum.ADMINISTRATOR)){
                throw new AccessDeniedException("Access denied for role: " + userAdmin.getRole().getEnumName());
            }
        }

        user.setLockTime(null);
        user.setUpdateAt(LocalDateTime.now());

        User updatedUser = userOutputPort.saveUser(user);

        if (!isEvent) {
            UserUnblockEvent userUnblockEvent = userPersistenceMapper.toUserUnblockEvent(updatedUser);
            userEventPublisher.publishUserUpdatesEvent(userUnblockEvent, EventType.USER_UNBLOCK);
        }

        return "User with UUID " + userUuid + " has been unblocked.";
    }

    @Override
    public void unblockUserByEvent(String message) {
        UserUnblockEvent eventPayload = (UserUnblockEvent) changeMessageEventToObject(message, UserUnblockEvent.class);

        User user = userPersistenceMapper.toUser(eventPayload);

        changeRole(user, null, true);
    }

    @Override
    public String getProfilePicture(String uuid, String token) {
        String tokenUuid = jwtUtils.getUuidFromToken(token.substring(7));
        if (!uuid.equals(tokenUuid)) {
            throw new AccessDeniedException("Access denied: cannot access profile picture of another user");
        }

        User user = userOutputPort.findByUuid(uuid)
            .orElseThrow(
                () -> new UserNotFoundException(errorMessage.buildUuidUserDontExistError(uuid))
            );

        return user.getProfilePictureUrl();
    }

    private Object changeMessageEventToObject(String message, Class<?> clazz) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode payloadNode = rootNode.get("payload");

            if (payloadNode == null) {
                throw new RuntimeException("El mensaje Kafka no contiene 'payload'");
            }

            return objectMapper.treeToValue(payloadNode, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
