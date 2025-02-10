package com.user.user_service.role.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.user.user_service.role.domain.model.Role;
import com.user.user_service.role.infrastructure.adapter.output.persistence.RolePersistenceAdapter;
import com.user.user_service.role.infrastructure.constant.RoleEnum;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RoleInitializerConfig implements CommandLineRunner {

    private final RolePersistenceAdapter rolePersistenceAdapter;

    @Override
    public void run(String... args) {
        
        for (RoleEnum roleEnum : RoleEnum.values()) {
            Role role = rolePersistenceAdapter.findByEnumName(roleEnum)
            .orElse(null);

            if (role == null) {
                saveRole(roleEnum);
            }
        }
        
        System.out.println("✅ Roles initialized successfully.");
    }

    private Role saveRole(RoleEnum role){
        return rolePersistenceAdapter.saveUser(Role.builder()
            .enumName(role)
            .description(role.getDescription())
            .name(role.getName())
            .build()
        );
    }
    
}
