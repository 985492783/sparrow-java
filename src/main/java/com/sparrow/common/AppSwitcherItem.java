package com.sparrow.common;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 3:00
 */
public class AppSwitcherItem {
    
    private String fieldName;
    
    private String type;
    
    private String desc;
    
    private Object value;
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isJson() {
        return "json".equals(type);
    }
}
