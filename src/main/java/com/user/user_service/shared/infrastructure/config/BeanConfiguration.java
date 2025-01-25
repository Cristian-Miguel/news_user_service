package com.user.user_service.shared.infrastructure.config;

import com.user.user_service.role.infrastructure.adapter.output.persistence.RolePersistenceAdapter;
import com.user.user_service.role.infrastructure.adapter.output.persistence.mapper.RolePersistenceMapper;
import com.user.user_service.role.infrastructure.adapter.output.persistence.repository.RoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RolePersistenceAdapter roleOutputPort(final RoleRepository roleRepository, final RolePersistenceMapper rolePersistenceMapper){
        return new RolePersistenceAdapter(roleRepository, rolePersistenceMapper);
    }

}
