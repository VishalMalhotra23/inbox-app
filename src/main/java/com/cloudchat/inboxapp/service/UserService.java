package com.cloudchat.inboxapp.service;

import com.cloudchat.inboxapp.models.User;
import com.cloudchat.inboxapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void register(User user) {
        user.setUserId(user.getUserId().split("\\|")[1]);
        userRepository.save(user);
    }
}
