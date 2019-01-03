package com.intel.podm.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wanxian.He
 */
public class ThreadUtil {

    private static ThreadLocal<Map> threadLocal = new ThreadLocal<Map>();

    public static void putThreadVariable(String key, Object obj) {
        Map vm = threadLocal.get();
        if (vm == null) {
            vm = new HashMap();
            threadLocal.set(vm);
        }
        vm.put(key, obj);
    }

    public static Object getThreadVariable(String key) {
        Map vm = threadLocal.get();
        if (vm == null) {
            return null;
        } else {
            return vm.get(key);
        }
    }

    public static Object removeThreadVariable(String key) {
        Map vm = threadLocal.get();
        if (vm == null) {
            return null;
        } else {
            return vm.remove(key);
        }
    }

    public static void clearThreadVariable() {
        Map vm = threadLocal.get();
        if (vm != null) {
            vm.clear();
            vm = null;
        }
    }
}
