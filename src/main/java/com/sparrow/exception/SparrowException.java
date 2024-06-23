package com.sparrow.exception;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 3:03
 */
public class SparrowException extends Exception {
    
    private int errCode;
    
    private String errMsg;
    
    public SparrowException(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    
    public int getErrCode() {
        return errCode;
    }
    
    public String getErrMsg() {
        return errMsg;
    }
    
}
