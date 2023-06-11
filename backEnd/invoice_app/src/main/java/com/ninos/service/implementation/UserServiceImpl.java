package com.ninos.service.implementation;

import com.ninos.dto.UserDTO;
import com.ninos.dtomapper.UserDTOMapper;
import com.ninos.model.User;
import com.ninos.repository.UserRepository;
import com.ninos.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;

    @Override
    public UserDTO createUser(User user) {
        User user1 = userRepository.create(user);
        return UserDTOMapper.fromUser(user1);
    }


}
