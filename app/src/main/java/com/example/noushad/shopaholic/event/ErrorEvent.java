package com.example.noushad.shopaholic.event;

/**
 * Created by noushad on 11/29/17.
 */

public class ErrorEvent {
    private String message;
    public ErrorEvent(String s) {
        this.message = s;
    }

    public String getMessage() {
        return message;
    }
}
