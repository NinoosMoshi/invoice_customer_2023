package com.ninos.service;

import com.ninos.dto.UserDTO;
import com.ninos.model.User;

public interface UserService {

  UserDTO createUser(User user);
  UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO user);
}
