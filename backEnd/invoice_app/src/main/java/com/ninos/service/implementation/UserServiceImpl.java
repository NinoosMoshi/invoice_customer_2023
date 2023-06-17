package com.ninos.service.implementation;

import com.ninos.dto.UserDTO;
import com.ninos.dtomapper.UserDTOMapper;
import com.ninos.model.Role;
import com.ninos.model.User;
import com.ninos.repository.RoleRepository;
import com.ninos.repository.UserRepository;
import com.ninos.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;


    @Override
    public UserDTO createUser(User user) {
        User user1 = userRepository.create(user);
        return mapToUserDTO(user1);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User userByEmail = userRepository.getUserByEmail(email);
        return mapToUserDTO(userByEmail);
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
       userRepository.sendVerificationCode(user);
    }


    @Override
    public UserDTO verifyCode(String email, String code) {
        User userByVerifyCode = userRepository.verifyCode(email, code);
        return mapToUserDTO(userByVerifyCode);
    }




    private UserDTO mapToUserDTO(User user){
        return UserDTOMapper.fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }


}
