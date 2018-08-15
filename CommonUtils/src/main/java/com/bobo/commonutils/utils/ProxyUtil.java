
package com.bobo.commonutils.utils;

import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyUtil {
    public static Method getMethod(String method, Class aClass, Class... parameterTypes) {
        if (!TextUtils.isEmpty(method) && aClass != null) {
            try {
                return aClass.getMethod(method, parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Object invoke(Method method, Object receiver, Object... args) {
        if (receiver != null && method != null) {
            try {
                return method.invoke(receiver, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Object invokeStatic(Method method, Object... args) {
        if (method != null) {
            try {
                return method.invoke(null, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static boolean isOverrideMethod(String method, Class aClass1, Class aClass2, Class... parameterTypes)
            throws NoSuchMethodException {
        if (!TextUtils.isEmpty(method) && aClass1 != null && aClass2 != null) {
            Method method1 = null;
            Method method2 = null;
            try {
                method1 = aClass1.getMethod(method, parameterTypes);
                method2 = aClass2.getMethod(method, parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                throw e;
            }
            if (method1.getDeclaringClass().equals(method2.getDeclaringClass())){
                return true;
            }
            return false;
        }else {
            throw new IllegalArgumentException("some argument is wrong");
        }
        //return false;
    }
}
