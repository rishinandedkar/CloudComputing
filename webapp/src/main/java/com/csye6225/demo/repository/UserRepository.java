package com.csye6225.demo.repository;

import com.csye6225.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository("userRepository")
public interface UserRepository extends JpaRepository<User,Long>{
    User findByEmail(String email);
}