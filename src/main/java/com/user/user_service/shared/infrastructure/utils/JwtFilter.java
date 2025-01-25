package com.user.user_service.shared.infrastructure.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.user_service.role.infrastructure.constant.RoleEnum;
import com.user.user_service.shared.infrastructure.input.data.response.GenericErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String token = getTokenFromRequest(request);
        final String method = request.getMethod();
        final String uri = request.getRequestURI();
        final String username;
        RoleEnum role;

        if(token==null) {
            handleErrorResponse(response, "Token invalid.",
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
            return;
        }

        try {
            username = jwtUtils.getUsernameFromToken(token);

            Map<String, Object> claims = jwtUtils.getAllClaims(token);

            role = RoleEnum.valueOf( (String) claims.get("role") );
            String email = (String) claims.get("email");

        } catch (ExpiredJwtException ex){
            handleErrorResponse(response, "Token expired.",
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
            return;
        } catch (MalformedJwtException ex){
            handleErrorResponse(response, "Token malformed.",
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
            return;
        } catch (Exception ex){
            handleErrorResponse(response, "Token invalid.",
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
            return;
        }

        // Check if user is allowed to access the route with the specific HTTP method
        if (!isRoleAllowedForRoute(role, uri, method)) {
            handleErrorResponse(response, "Role not allowed for this route.",
                    HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), uri);
            return;
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username, null, authorities
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private void handleErrorResponse(
            HttpServletResponse response, String message, int status, String error, String uri
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse =
        objectMapper.writeValueAsString(
                GenericErrorResponse.builder()
                        .timestamp(null)
                        .status(status)
                        .error(error)
                        .message(message)
                        .path(uri)
                        .build()
        );

        response.getWriter().write(jsonResponse);
    }

    private boolean isRoleAllowedForRoute(RoleEnum role, String uri, String method){
        Map<String, Map<String, List<RoleEnum>>> roleAccessUri = new HashMap<>();

        //Uri and its access Roles
        roleAccessUri.put("/api/user", Map.of(
                "GET", List.of(RoleEnum.ADMINISTRATOR), // e.g., view user info
                "POST", List.of(RoleEnum.ADMINISTRATOR) // e.g., create user
        ));

        // Retrieve allowed roles for the given URI and method
        Map<String, List<RoleEnum>> methodRoles = roleAccessUri.get(uri);
        List<RoleEnum> allowedRoles = (methodRoles != null) ? methodRoles.get(method) : null;

        return allowedRoles == null || allowedRoles.contains(role);
    }
}
