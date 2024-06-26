package com.sparrow.switcher.payload;

import com.sparrow.common.Response;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 5:36
 */
public class SwitcherResponse extends Response {
    private String resp;

    @Override
    public String getType() {
        return "handler.SwitcherResponse";
    }
}
