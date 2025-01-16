package com.solvd.api.model;

public class ErrorMessage {
    private String field;
    private String message;


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "field='" + field + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
