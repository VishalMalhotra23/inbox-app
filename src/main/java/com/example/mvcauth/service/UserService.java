package com.example.mvcauth.service;

import com.example.mvcauth.model.User;
import com.example.mvcauth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void register(User user) {
        String[] userId = user.getUserId().split("\\|");
        user.setUserId(userId[1]);
        userRepository.save(user);
    }
}
