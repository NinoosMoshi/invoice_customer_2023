package com.ninos.service;

import com.ninos.dto.UserDTO;
import com.ninos.model.User;

public interface UserService {

    UserDTO createUser(User user);
    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO user);
    UserDTO verifyCode(String email, String code);
    void resetPassword(String email);
    UserDTO verifyPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);
}

