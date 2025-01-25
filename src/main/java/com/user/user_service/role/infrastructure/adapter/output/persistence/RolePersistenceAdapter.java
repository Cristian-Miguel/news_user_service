package com.user.user_service.role.infrastructure.adapter.output.persistence;

import com.user.user_service.role.application.port.output.RoleOutputPort;
import com.user.user_service.role.domain.model.Role;
import com.user.user_service.role.infrastructure.adapter.output.persistence.entity.RoleEntity;
import com.user.user_service.role.infrastructure.adapter.output.persistence.mapper.RolePersistenceMapper;
import com.user.user_service.role.infrastructure.adapter.output.persistence.repository.RoleRepository;
import com.user.user_service.role.infrastructure.constant.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
public class RolePersistenceAdapter implements RoleOutputPort {

    private final RoleRepository roleRepository;
    private final RolePersistenceMapper rolePersistenceMapper;

    @Override
    public Optional<Role> findByEnumName(RoleEnum enumName) {
        final Optional<RoleEntity> roleEntity = roleRepository.findByEnumName(enumName);

        if(roleEntity.isEmpty())
            return Optional.empty();

        final Role role =  rolePersistenceMapper.toRole(roleEntity.get());

        return Optional.of(role);
    }
}
