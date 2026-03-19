package com.user.user_service.user.application.port.input;

import com.user.user_service.shared.domain.model.PageFormats;
import com.user.user_service.user.domain.model.User;
import org.springframework.data.domain.Page;

public interface GetUserUseCase {

    User getUserById(String uuid, String token);

    Page<User> getUsers(PageFormats pageFormats, String token);
    
    String getProfilePicture(String uuid, String token);
    
}
