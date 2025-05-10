package com.user.user_service.user.infrastructure.adapter.output.persistence.repository;

import com.user.user_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUuid(String uuid);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    //eq: equals, ne: not equals, gt: greater than, lt: less than, ge: greater than or equal to, le: less than or equal to
    //filter types: like, eq, ne, gt, lt, ge, le
    //String: like, eq
    //amount: gt, lt, ge, le
    //boolean: eq, ne
    //Date: gt, lt, ge, le, eq
    //filterField: id, email, username, firstName, lastName, createAt, birthdate, updateAt, loggerAt, role
    //filterValue: String, amount, boolean, date
    Page<UserEntity> findAll(
        Specification<UserEntity> filter,
        org.springframework.data.domain.Pageable pageable
    );

    
}
