package com.sparrow.switcher.payload;

import com.sparrow.common.AppSwitcherItem;
import com.sparrow.common.Response;
import lombok.*;

import java.util.Map;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 5:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitcherResponse extends Response {
    private String resp;

    private Map<String, Map<String, AppSwitcherItem>> classMap;

    @Override
    public String getType() {
        return "handler.SwitcherResponse";
    }

}
