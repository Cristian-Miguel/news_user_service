package com.user.user_service.role.infrastructure.adapter.output.persistence.repository;

import com.user.user_service.role.infrastructure.adapter.output.persistence.entity.RoleEntity;
import com.user.user_service.role.infrastructure.constant.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByEnumName(RoleEnum enumName);

}
