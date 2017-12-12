package com.example.noushad.shopaholic.event;

/**
 * Created by noushad on 11/29/17.
 */

public class RegisterEvent {

    private String message;
    public RegisterEvent(String successfull) {
        message = successfull;
    }

    public String getMessage() {
        return message;
    }
}
