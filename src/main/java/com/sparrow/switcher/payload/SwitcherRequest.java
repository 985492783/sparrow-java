package com.sparrow.switcher.payload;

import com.sparrow.common.AppSwitcherItem;
import com.sparrow.common.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 5:35
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwitcherRequest extends Request {

    private String kind;

    private String appName;

    private String ip;

    private Map<String, Map<String, AppSwitcherItem>> classMap;

    @Override
    public String getType() {
        return "handler.SwitcherRequest";
    }
    
    @Override
    public Map<String, String> getHeaders() {
        return new HashMap<>();
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Map<String, Map<String, AppSwitcherItem>> getClassMap() {
        return classMap;
    }

    public void setClassMap(Map<String, Map<String, AppSwitcherItem>> classMap) {
        this.classMap = classMap;
    }
}
