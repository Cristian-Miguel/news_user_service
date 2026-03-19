package com.user.user_service.shared.infrastructure.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    @Value("${crypto.secret-key}")
    private String SECRET_KEY;

    @Value("${crypto.salt}")
    private String SALT;

    public String encryptRefreshToken(String refreshToken){
        TextEncryptor encryptor = Encryptors.text(SECRET_KEY, SALT);
        return encryptor.encrypt(refreshToken);
    }

    public String decryptRefreshToken(String encryptedRefreshToken){
        TextEncryptor decryptor = Encryptors.text(SECRET_KEY, SALT);
        return decryptor.decrypt(encryptedRefreshToken);
    }

}
