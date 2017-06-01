package com.declaratiiavere.iam.config;

/**
 * @author Viorel Vesa.
 */
public class ErrorWrapper {

    private String message;

    public ErrorWrapper(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
