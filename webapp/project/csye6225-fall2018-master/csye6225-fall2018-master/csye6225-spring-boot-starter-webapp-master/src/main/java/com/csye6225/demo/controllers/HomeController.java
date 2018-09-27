package com.csye6225.demo.controllers;


import com.csye6225.demo.model.User;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.csye6225.demo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;




@Controller
public class HomeController {

  @Autowired
  private UserService userService;


  @RequestMapping(value="/time", method= RequestMethod.GET, produces= "application/json")
  @ResponseBody
  public String welcomeUser(HttpServletRequest request, HttpServletResponse response){

    JsonObject jO = new JsonObject();
    	String header = request.getHeader("Authorization");
    		if (header != null && header.contains("Basic")) {
        String[] credentialValues= decode(header);

        User userExists = userService.findByEmail(credentialValues[0]);
        	if (userExists != null) {
        		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (encoder.matches(credentialValues[1], userExists.getPassword()) || credentialValues[1].equals(userExists.getPassword()))
        	jO.addProperty("message", "You are logged in. Current time is: " + new Date().toString());
        else
        	jO.addProperty("message", "Incorrect credentials");
      } else {
    	  jO.addProperty("message", "Incorrect credentials");
      }
    } else {
    	jO.addProperty("message", "You are not logged in !!");
    }
    return jO.toString();
  }

  


  @RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public String registerUser(@RequestBody User user) {
    JsonObject jo = new JsonObject();
    User userExists = userService.findByEmail(user.getEmail());
    if (userExists != null) {
    	jo.addProperty("message", "Account already exist !");
    } else {
      User createUser = new User();
      createUser.setEmail(user.getEmail());
      createUser.setPassword(user.getPassword());
      userService.saveUser(createUser);
      jo.addProperty("message", "Account created successfully");
      jo.addProperty("email", createUser.getEmail());
      jo.addProperty("password", createUser.getPassword());
    }
    return jo.toString();
  }

  public String[] decode(String header){
    assert header.substring(0, 6).equals("Basic");
    String basicAuthEncoded = header.substring(6);
    String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
    final String[] credentialValues = basicAuthAsString.split(":", 2);
    return  credentialValues;
  }

}