package com.declaratiiavere.restclient;

/**
 * Encapsulates the details of a REST error response.
 *
 * @author Razvan Dani
 */
public class RestException extends Exception {
    private Integer statusCode;
    private Object responseObject;

    public RestException(Integer statusCode, Object responseObject) {
        this.statusCode = statusCode;
        this.responseObject = responseObject;
    }

    @Override
    public String getMessage() {
        return "Status code " + statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public Object getResponseObject() {
        return responseObject;
    }
}
