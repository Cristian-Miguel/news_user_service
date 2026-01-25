package com.user.user_service.shared.infrastructure.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.user.user_service.role.infrastructure.constant.RoleEnum;
import com.user.user_service.shared.domain.exception.AccessDeniedException;
import com.user.user_service.shared.infrastructure.constant.ErrorMessage;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RoleUtil {

    private final ErrorMessage errorMessage;
    private final JwtUtils jwtUtils;

    public void checkValidRoleAccessResource(String token, RoleEnum[] rolesWithAccess) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        RoleEnum role = getRoleFromToken(token);

        for (RoleEnum roleEnum : rolesWithAccess) {
            if(role.equals(roleEnum)) {
                return;
            }
        }

        throw new AccessDeniedException(errorMessage.buildAccessDeniedByRoleError(role));
    }

    public RoleEnum getRoleFromToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = jwtUtils.getAllClaims(token);
        String roleCode = claims.get("role", String.class); 
        String enumName = roleCode.replace("ROLE_", "");

        return RoleEnum.valueOf(enumName);
    }

}
