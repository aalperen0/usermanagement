package com.example.my_app.Handler;

public class Validation extends RuntimeException {
    public Validation(String message) {
        super(message);
    }
}
