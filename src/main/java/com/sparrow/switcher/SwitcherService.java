package com.sparrow.switcher;

import com.sparrow.common.AppSwitcherItem;
import com.sparrow.exception.SparrowException;
import com.sparrow.switcher.payload.SwitcherResponse;

import java.util.Map;

/**
 * @author 985492783@qq.com
 * @date 2024/6/15 2:48
 */
public interface SwitcherService {


    SwitcherResponse registry(String namespace, String appName, Map<String, Map<String, AppSwitcherItem>> classMap) throws SparrowException;
}
