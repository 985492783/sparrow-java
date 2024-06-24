package com.sparrow.common;

import lombok.Data;

import java.util.Map;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 5:21
 */
@Data
public abstract class Request implements Payload {

    private String clientId;

    private String namespace;

    public abstract Map<String, String> getHeaders();

}
