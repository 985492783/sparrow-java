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
public class SwitcherManager {
    
    private static final Map<Class<?>, Map<String, Field>> fieldMap = new ConcurrentHashMap<>();

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
                            switchItem.setValue(JSON.toJSONString(obj));
                        }
                    } catch (IllegalAccessException e) {
                        throw new SparrowException(ErrorCodeEnums.SYSTEM_ERROR.getCode(),
                                "field init failed: " + field.getName());
                    }
                    fieldItemMap.put(field.getName(), switchItem);
                    map.put(field.getName(), field);
                }
            }
            fieldMap.put(clazz, map);
            clazzMap.put(clazz.getCanonicalName(), fieldItemMap);
        }

        SwitcherResponse response = switcherService.registry(namespace, appName, clazzMap);
        System.out.println(JSON.toJSON(response));
        //TODO 注册并将初始化值拉回来
        
        //TODO 将真实值透还给原对象 SwitcherFieldUtils.setField()
    }

    public static void initManager(Properties properties) throws SparrowException {
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
