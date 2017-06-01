package com.declaratiiavere.edge;

/**
 * Error message body.
 *
 * @author Razvan Dani
 */
public class ErrorMessageBody {
    private long timestamp = System.currentTimeMillis();
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorMessageBody(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
