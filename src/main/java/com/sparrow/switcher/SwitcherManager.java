package com.sparrow.switcher;

import com.alibaba.fastjson2.JSON;
import com.sparrow.annotation.AppSwitch;
import com.sparrow.common.AppSwitcherItem;
import com.sparrow.common.ErrorCodeEnums;
import com.sparrow.config.Constants;
import com.sparrow.config.SparrowProperties;
import com.sparrow.exception.SparrowException;
import com.sparrow.remote.client.GrpcClientFactory;
import com.sparrow.switcher.payload.SwitcherResponse;
import com.sparrow.utils.SwitcherFieldUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 2:52
 */
@Slf4j
public class SwitcherManager {
    
    private static final Map<String, Map<String, Field>> fieldMap = new ConcurrentHashMap<>();

    private static volatile SwitcherService switcherService;
    private static volatile boolean isStart = false;

    private static String appName = null;
    private static String namespace = null;
    /**
     * client register method.
     *
     * @param classes class
     */
    public static void init(Class<?>... classes) throws SparrowException {
        if (classes.length == 0) {
            throw new SparrowException(ErrorCodeEnums.ILLEGAL_FAILED.getCode(), "clazz is empty");
        }
        Map<String, Map<String, AppSwitcherItem>> clazzMap = new HashMap<>();
        for (Class<?> clazz : classes) {
            if (fieldMap.containsKey(clazz)) {
                continue;
            }
            Map<String, Field> map = new HashMap<>();
            Field[] fields = clazz.getDeclaredFields();
            Map<String, AppSwitcherItem> fieldItemMap = new HashMap<>();
            for (Field field : fields) {
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                boolean isFinal = Modifier.isFinal(field.getModifiers());
                AppSwitch appSwitch;
                if (isStatic && !isFinal && (appSwitch = field.getAnnotation(AppSwitch.class)) != null) {
                    AppSwitcherItem switchItem = SwitcherFieldUtils.createSwitchItem(field);
                    switchItem.setDesc(appSwitch.desc());
                    field.setAccessible(true);
                    try {
                        Object obj = field.get(null);
                        if (obj != null) {
                            if (switchItem.isJson()) {
                                switchItem.setValue(JSON.toJSONString(obj));
                            } else {
                                switchItem.setValue(obj);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        throw new SparrowException(ErrorCodeEnums.SYSTEM_ERROR.getCode(),
                                "field init failed: " + field.getName());
                    }
                    fieldItemMap.put(field.getName(), switchItem);
                    map.put(field.getName(), field);
                }
            }
            fieldMap.put(clazz.getCanonicalName(), map);
            clazzMap.put(clazz.getCanonicalName(), fieldItemMap);
        }

        SwitcherResponse response = switcherService.registry(namespace, appName, clazzMap);

        if (response.getClassMap() != null) {
            response.getClassMap().forEach((k, v)-> {
                v.forEach((field, item)-> {
                    try {
                        SwitcherFieldUtils.setField(fieldMap.get(k).get(field), item.getValue(), item.getType());

                    } catch (SparrowException e) {
                        log.error("register upload failed");
                    }
                });
            });
        }
    }

    public static void initManager(Properties properties) {
        if (isStart) {
            return;
        }
        synchronized (SwitcherManager.class) {
            if (isStart) {
                return;
            }
            switcherService = new SparrowSwitcherService(new SparrowProperties(properties));
            appName = properties.getProperty(Constants.SPARROW_APP_NAME, Constants.DEFAULT_SPARROW_APP_NAME);
            namespace = properties.getProperty(Constants.SPARROW_NAMESPACE, Constants.DEFAULT_SPARROW_NAMESPACE);
            isStart = true;
        }
    }
}
