package com.user.user_service.user.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Block {
    
    private int id;

    private User user;

    private String reason;

    private LocalDateTime lockTime;
    
    private LocalDateTime createAt;
    
    private LocalDateTime updateAt;

}
