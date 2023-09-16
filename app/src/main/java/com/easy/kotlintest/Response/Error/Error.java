package com.easy.kotlintest.Response.Error;

public class Error {
    String id,message;

    public Error(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
