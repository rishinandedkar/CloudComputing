package com.csye6225.demo.controllers;

import com.csye6225.demo.model.RestError;
import com.google.gson.JsonObject;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@ControllerAdvice
public class DefaultExceptionHandler {

	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotSupportErrorHandler(HttpServletRequest req, Exception e, HttpServletResponse response) throws Exception {
        RestError error = new RestError("MethodNotSupportedException", 405, "Method not supported");
        return new ResponseEntity<RestError>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

}
