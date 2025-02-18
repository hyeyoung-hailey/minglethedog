package com.example.minglethedog.service;

import com.example.minglethedog.common.ErrorCode;
import com.example.minglethedog.entity.Role;
import com.example.minglethedog.entity.User;
import com.example.minglethedog.exception.DuplicateUsernameException;
import com.example.minglethedog.exception.UserNotFoundException;
import com.example.minglethedog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 새로운 사용자 생성
     * */
    public User createUser(String username, String password, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUsernameException(ErrorCode.USER_DUPLICATED);
        }

        String bcryptPassword = bCryptPasswordEncoder.encode(password);
        User user = new User(username, bcryptPassword, role);

        return userRepository.save(user);
    }

    /**
     * 비밀번호 변경
     * */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(userId + " not found"));
        user.changePassword(bCryptPasswordEncoder.encode(oldPassword));
    }


}
