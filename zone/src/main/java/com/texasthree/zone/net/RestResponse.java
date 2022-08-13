package com.texasthree.zone.net;

public class RestResponse<T> {

    public static final RestResponse SUCCESS = new RestResponse();

    public static final RestResponse UNKNOWN_ERROR = new RestResponse(1, "unknown error");

    /**
     * 返回码
     */
    private final int code;
    /**
     * 描述
     */
    private final String message;

    private final T data;

    public RestResponse() {
        this(0, "success", null);
    }

    public RestResponse(T data) {
        this(0, "success", data);
    }

    public RestResponse(int retcode, String retdesc) {
        this(retcode, retdesc, null);
    }

    public RestResponse(int retcode, String retdesc, T data) {
        this.code = retcode;
        this.message = retdesc;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return this.code == 0;
    }

    @Override
    public String toString() {
        return "{\"message\":\"" + message + "\",\"code\":" + code + "}";
    }

    public T getData() {
        return data;
    }

    public static RestResponse error(String retdesc) {
        return new RestResponse(1, retdesc);
    }
}
