package com.sparrow.common;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 5:21
 */
public abstract class Response implements Payload {

    private int statusCode;

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public int getStatusCode() {
        return statusCode;
    }
}
