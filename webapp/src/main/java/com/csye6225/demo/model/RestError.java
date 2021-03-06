package com.csye6225.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"error_type", "code", "error_message"})
public class RestError {

	@JsonProperty("code")
    int code;

    @JsonProperty("error_type")
    String type;

    @JsonProperty("error_message")
    String message;

    public RestError() {
        super();
    }

    public RestError(String type, int code, String message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
