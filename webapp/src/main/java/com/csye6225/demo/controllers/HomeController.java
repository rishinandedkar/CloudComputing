package com.csye6225.demo.controllers;


import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.csye6225.demo.model.User;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.csye6225.demo.service.UserService;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;



@Controller
public class HomeController {

  @Autowired
  private UserService userService;

  private final static Logger logger = LoggerFactory.getLogger(HomeController.class);

  @RequestMapping(value="/time", method= RequestMethod.GET, produces= "application/json")
  @ResponseBody
  public String welcomeUser(HttpServletRequest request, HttpServletResponse response){

	  JsonObject jO = new JsonObject();
	  try {
		    	String header = request.getHeader("Authorization");
		    		if (header != null && header.contains("Basic")) {
		        String[] credentialValues= decode(header);
		
		        User userExists = userService.findByEmail(credentialValues[0]);
		        	if (userExists != null) {
		        		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		        if (encoder.matches(credentialValues[1], userExists.getPassword()) || credentialValues[1].equals(userExists.getPassword())) {
		        	jO.addProperty("message", "You are logged in. Current time is: " + new Date().toString());
		        }else {
		        	jO.addProperty("message", "Incorrect credentials");
		        } 
		        }else {
		    	  jO.addProperty("message", "Incorrect credentials");
		      }
		    } else {
		    	jO.addProperty("message", "You are not logged in !!");
		    }
		    
  }
  catch(Exception ex){
	  ex.printStackTrace();
  }
	  return jO.toString();
  }

  @RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public String registerUser(@RequestBody User user) {
    JsonObject jo = new JsonObject();
    try {
	    User userExists = userService.findByEmail(user.getEmail());
	    if (userExists != null) {
	    	jo.addProperty("message", "Account already exist !");
	    } else {
	      User createUser = new User();
	      String regexEmail = "^[a-zA-Z0-9.!#$%&�*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
	      String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,10}$";
	      //The above regex has pattern for 1.two uppercase letters, 2. one special case letter,3.two digits,4. three lowercase letters,5.length 8
	      if(user.getEmail().matches(regexEmail) && user.getPassword().matches(regexPassword)) {
	      createUser.setId(user.getId());
	      createUser.setEmail(user.getEmail());
	      createUser.setPassword(user.getPassword());
	      userService.saveUser(createUser);
	      jo.addProperty("message", "Account created successfully");
	      jo.addProperty("id", createUser.getId());
	      jo.addProperty("email", createUser.getEmail());
	      jo.addProperty("password", createUser.getPassword());
	    }else {
	    	jo.addProperty("message", "Please enter your email id and password correctly");
	    }
	    }
    } catch(Exception ex) {
    	ex.printStackTrace();
    }
    return jo.toString();
  }
  
  @RequestMapping(value = "/resetPassword", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public String forgotPassword(@RequestBody User user) {
    JsonObject jo = new JsonObject();
    jo.addProperty("message","Email sent successfully");
    if(user!=null){

      User userExists = userService.findByEmail(user.getEmail());

      if(userExists == null) {
//        response.setStatus(HttpServletResponse.SC_OK);
      } else {

    	  try {
        AmazonSNSClient awssns = new AmazonSNSClient(new InstanceProfileCredentialsProvider());
        String topicArn = awssns.createTopic("password_reset").getTopicArn();
        PublishRequest request = new PublishRequest(topicArn, user.getEmail());
        PublishResult result = awssns.publish(request);
    	  } catch(Exception e) {
    		  jo.addProperty("error",e.getMessage());
    	  }
      }
    }
    return jo.toString();

  }

  @RequestMapping(value="/test", method= RequestMethod.GET, produces= "application/json")
  @ResponseBody
  public String test(HttpServletRequest request, HttpServletResponse response){
    JsonObject jo = new JsonObject();
    jo.addProperty("message","Email sent successfully");
    response.setStatus(HttpServletResponse.SC_OK);

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