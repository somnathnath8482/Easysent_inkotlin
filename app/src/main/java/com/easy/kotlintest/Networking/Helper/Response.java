package com.easy.kotlintest.Networking.Helper;

public class Response {
    String url;
    int code;
    String response;

    public Response() {
    }

    public Response(String url, int code, String response) {
        this.url = url;
        this.code = code;
        this.response = response;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
