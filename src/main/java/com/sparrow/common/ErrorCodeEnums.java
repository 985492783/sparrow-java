package com.sparrow.common;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 3:03
 */
public enum ErrorCodeEnums {
    SYSTEM_ERROR(500),
    AUTH_FAILED(403),
    ILLEGAL_FAILED(101);
    
    
    private final int code;
    ErrorCodeEnums(int code) {
        
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
