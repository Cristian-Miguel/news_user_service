package com.user.user_service.user.application.port.input;

import com.user.user_service.user.domain.model.Block;

public interface BlockUserUseCase {

    String blockUser(Block block, String token, boolean isByEvent);
    
    String unblockUser(String userUuid, String token, boolean isByEvent);

    void blockUserByEvent(String message);
    
    void unblockUserByEvent(String message);

}
