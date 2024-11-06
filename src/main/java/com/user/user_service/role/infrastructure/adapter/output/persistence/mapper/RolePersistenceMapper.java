package com.user.user_service.role.infrastructure.adapter.output.persistence.mapper;

import com.user.user_service.role.domain.model.Role;
import com.user.user_service.role.infrastructure.adapter.output.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RolePersistenceMapper {

    RoleEntity toRoleEntity(Role role);

    Role toRole(RoleEntity roleEntity);
}
