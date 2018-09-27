package com.csye6225.demo.service;

import com.csye6225.demo.model.User;

public interface UserService {

    public User findByEmail(String email);
    public void saveUser(User user);

}