package com.sparrow.common;

import java.util.Map;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 5:21
 */
public abstract class Request implements Payload {
    
    public abstract Map<String, String> getHeaders();
    
}
