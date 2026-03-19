package com.user.user_service.user.infrastructure.adapter.output.persistence;

import com.user.user_service.role.infrastructure.constant.RoleEnum;
import com.user.user_service.shared.domain.model.PageFormats;
import com.user.user_service.user.application.port.output.UserOutputPort;
import com.user.user_service.user.domain.model.User;
import com.user.user_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.user.user_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.user.user_service.user.infrastructure.adapter.output.persistence.mapper.UserSpecificationFilterMapper;
import com.user.user_service.user.infrastructure.adapter.output.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserOutputPort {

    private final UserRepository userRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public User deleteUserByUuid(String uuid) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUuid(uuid);
        if(userEntityOptional.isEmpty()){
            return null;
        }
        UserEntity userEntity = userEntityOptional.get();
        userRepository.delete(userEntity);
        return userPersistenceMapper.toUser(userEntity);
    }

    @Override
    public User saveUser(User user) {
        UserEntity userEntity = userPersistenceMapper.toUserEntity(user);
        userEntity = userRepository.save(userEntity);

        return userPersistenceMapper.toUser(userEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        final Optional<UserEntity> userEntity = userRepository.findByUsername(username);

        if(userEntity.isEmpty())
            return Optional.empty();

        final User user = userPersistenceMapper.toUser(userEntity.get());

        return Optional.of(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        if(userEntity.isEmpty())
            return Optional.empty();

        final User user = userPersistenceMapper.toUser(userEntity.get());

        return Optional.of(user);
    }

    @Override
    public Optional<User> findByUuid(String uuid) {
        final Optional<UserEntity> userEntity = userRepository.findByUuid(uuid);

        if(userEntity.isEmpty())
            return Optional.empty();

        final User user = userPersistenceMapper.toUser(userEntity.get());

        return Optional.of(user);
    }

    @Override
    public boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Page<User> findAll(PageFormats pageFormats, RoleEnum roleEnum, Long userId) {
        PageRequest pageable = PageRequest.of(
            pageFormats.getPage(), 
            pageFormats.getSize(),
            pageFormats.getOrderType().equals("desc") ?
                Sort.by(pageFormats.getOrderField()).descending() :
                Sort.by(pageFormats.getOrderField()).ascending()
        );
        Specification<UserEntity> spec = null;
        if(pageFormats.getFilterField() != null &&
            pageFormats.getFilterType() != null &&
            pageFormats.getFilterValue() != null
        ){
            spec = UserSpecificationFilterMapper
                .buildFilter(
                    pageFormats.getFilterField(),
                    pageFormats.getFilterType(),
                    pageFormats.getFilterValue()
                );
        }

        if(roleEnum.equals(RoleEnum.NEWS_ENTERPRICE)){
            Specification<UserEntity> specByRole = UserSpecificationFilterMapper.getUserByRole(
                roleEnum.name(), 
                userId
            );
            if(spec == null){
                spec = specByRole;
            } else {
                spec = spec.and(specByRole);
            }
        }

        Page<UserEntity> userEntityPage = userRepository.findAll(
            spec,
            pageable
        );
        
        return userEntityPage.map(userPersistenceMapper::toUser);
    }

}
