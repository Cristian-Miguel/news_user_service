package com.user.user_service.shared.infrastructure.constant;

import org.springframework.context.annotation.Configuration;

import com.user.user_service.role.infrastructure.constant.RoleEnum;

@Configuration
public class ErrorMessage {

    public final String ROLE_NOT_FOUND = "The role is not in the system.";
    public final String BAD_CREDENTIALS = "Invalid username or password.";
    public final String LOCKED_ACCOUNT = "Your account is locked due to too many failed login attempts. Please try again later.";
    public final String TOKEN_EXPIRED = "Token has expired.";
    public final String TOKEN_MALFORMAT = "Token doesn't have the correct format.";
    public final String INVALID_UPDATE_SUBUSER = "Don't have permission to update this user.";
    public final String INVALID_GET_SUBUSER = "Don't have permission to get this user.";

    public String buildEmailTakenError(String email){
        return "The email " +
                "'" +
                email +
                "'" +
                " is already taken in the system.";
    }

    public String buildUsernameTakenError(String username){
        return "The username " +
                "'" +
                username +
                "'" +
                " is already taken in the system.";
    }

    public String buildEmailAndUsernameTakenError(String email, String username){
        return "The username " +
                "'" +
                username +
                "'" +
                " and the email "+
                "'"+
                email+
                "'"+
                " is already taken in the system.";
    }

    public String buildUsernameDontExistError(String username){
        return "The username " +
                "'" +
                username +
                "'" +
                " isn't exist in the system.";
    }

    public String buildUuidUserDontExistError(String uuid){
        return "The user uuid" +
                "'" +
                uuid +
                "'" +
                " isn't exist in the system.";
    }

    public String buildAccessDeniedByRoleError(RoleEnum role){
        return "The role " +
                "'" +
                role.name() +
                "'" +
                " doesn't have access to this resource.";
    }
}
