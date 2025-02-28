package com.example.minglethedog.service;

import com.example.minglethedog.entity.Role;
import com.example.minglethedog.entity.User;
import com.example.minglethedog.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class UserServiceTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    UserRepository userRepository;

    @Test
    void createUser() {

        for (int i = 0; i < 10; i++) {
            User user = new User("test"+i, bCryptPasswordEncoder.encode("123"), Role.USER);
            userRepository.save(user);
        }

        assertEquals(10, userRepository.count());


    }

}