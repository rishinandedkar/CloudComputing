package com.csye6225.demo.auth;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;



@Configuration
@EnableWebSecurity
public class SecConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  @Qualifier("dataSource")
  private DataSource dataSource;
  
  @Autowired
  private BasicAuthEntryPoint basicAuthEntryPoint;


  @Value("${spring.queries.users-query}")
  private String usersQuery;



  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
            .csrf()
            .disable()
            .authorizeRequests()
            .antMatchers("/user/register").permitAll()
            .antMatchers("/resetPassword").permitAll()
            .antMatchers("/time").permitAll()
            .antMatchers("/test").permitAll()
            .antMatchers("/").permitAll()
            .antMatchers("/user/transaction").permitAll()
            .antMatchers("/user/transactions").permitAll()
            .antMatchers("/user/transaction/{id}").permitAll()
            .antMatchers("/user/transaction/{id}/attachments").permitAll()
            .antMatchers("/user/transaction/{id}/attachmentss").permitAll()
            .antMatchers("/user/transaction/{id}/attachments/{idAttachments}").permitAll()
            .anyRequest().authenticated();

  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
          throws Exception {
    auth.
            jdbcAuthentication()
            .usersByUsernameQuery(usersQuery)
            .dataSource(dataSource)
            .passwordEncoder(bCryptPasswordEncoder);
  }

  @Bean
  public HttpSessionStrategy httpSessionStrategy() {
    return new HeaderHttpSessionStrategy();
  }

}