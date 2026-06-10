package com.example.newbee2.model;

public class Result<T> {
    private int resultCode;
    private String message;
    private T data;

    public int getResultCode() { return resultCode; }
    public void setResultCode(int resultCode) { this.resultCode = resultCode; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public boolean isSuccess() {
        return resultCode == 200;
    }
}
