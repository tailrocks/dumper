package com.zhokhov.dumper.api.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.inject.Singleton;

@Singleton
public class PasswordEncoder {

    public boolean matches(String rawPassword, String encodedPassword) {
        BCrypt.Verifyer verifyer = BCrypt.verifyer();
        BCrypt.Result result = verifyer.verify(rawPassword.toCharArray(), encodedPassword);

        return result.verified;
    }

    public String encode(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

}
