//package com.csye6225.demo.controllers;
//
//import com.csye6225.demo.model.User;
//import com.csye6225.demo.repository.UserRepository;
//import com.csye6225.demo.service.UserService;
//import com.csye6225.demo.service.UserServiceImpl;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.junit.Assert.*;
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@DataJpaTest
//@WebMvcTest(controllers = HomeController.class, secure = false)
//public class HomeControllerTest {
//
//	@Mock
//    UserRepository userRepository;
//
//    @Autowired
//    MockMvc mockMvc;
//
//
//
//    @Before
//    public void setup() {
//
//        MockitoAnnotations.initMocks(this);
//
//        User createUser = new User();
//        createUser.setEmail("mansii.w@gmail.com");
//        createUser.setPassword("Qwerty@1");
//
//
//        when(userRepository.findByEmail("mansii.w@gmail.com")).thenReturn(createUser);
//    }
//
//    @Test
//    public void register() throws Exception {
//
//        User retrievedUser = userRepository.findByEmail("mansii.w@gmail.com");
//        assertEquals(retrievedUser.getEmail(),"mansii.w@gmail.com");
//    }
//}
