package com.user.user_service.user.infrastructure.adapter.input.rest.mapper;

import org.mapstruct.Mapper;

import com.user.user_service.user.domain.model.Block;
import com.user.user_service.user.infrastructure.adapter.input.rest.data.request.BlockUserRequest;

@Mapper(componentModel = "spring")
public interface BlockRestMapper {

    Block toBlock(BlockUserRequest blockUserRequest);
}
