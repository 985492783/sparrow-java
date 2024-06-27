package com.sparrow.utils;

import com.alibaba.fastjson2.JSON;
import com.sparrow.common.AppSwitcherItem;
import com.sparrow.common.ErrorCodeEnums;
import com.sparrow.exception.SparrowException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 3:16
 */
public class SwitcherFieldUtils {
    public static AppSwitcherItem createSwitchItem(Field field) throws SparrowException {
        AppSwitcherItem switchItem = new AppSwitcherItem();
        switchItem.setFieldName(field.getName());
        if (field.getType() == Integer.class || field.getType() == Long.class) {
            switchItem.setType("int");
        } else if (field.getType() == Double.class || field.getType() == Float.class) {
            switchItem.setType("float");
        } else if (field.getType() == Boolean.class) {
            switchItem.setType("bool");
        } else if (field.getType() == String.class) {
            switchItem.setType("string");
        } else {
            switchItem.setType("json");
        }
        return switchItem;
    }
    
    /**
     * json数值赋给field
     */
    public static void setField(Field field, String json) throws SparrowException {
        try {
            Type genericType = field.getGenericType();
            field.set(null, JSON.parseObject(json, genericType));
        } catch (Exception e) {
            throw new SparrowException(ErrorCodeEnums.SYSTEM_ERROR.getCode(), "parse object failed");
        }
    }
}
