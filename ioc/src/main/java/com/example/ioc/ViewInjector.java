package com.example.ioc;

import android.app.Activity;

import com.example.ioc.processor.AbstractInjector;
import com.example.ioc.processor.ProxyInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 描    述：
 * 作    者：liyx@13322.com
 * 时    间：2016/8/8
 */
public class ViewInjector {

    private static final String TAG = "TAG";
    static final Map<Class<?> , AbstractInjector<Object>> INJECTORS = new LinkedHashMap<Class<?>, AbstractInjector<Object>>();

    public static void inject(Activity activity){
        inject(activity , activity);
    }

    public static void inject(Object host , Object root){
        AbstractInjector viewInjector = findInjector(host);
        viewInjector.inject(host , root);
    }


    /**
     * 生成 injector ，类似 MainActivity$$Injector
     * @param obj
     * @return
     */
    private static  AbstractInjector<Object> findInjector(Object obj){
        Class<?> clazz = obj.getClass();
        AbstractInjector<Object> injector = INJECTORS.get(clazz);
        if (injector == null){
            try {
                Class injectorClazz = Class.forName(clazz.getName()+"$$"+ ProxyInfo.PROXY);
                injector = (AbstractInjector<Object>) injectorClazz.newInstance();
                INJECTORS.put(clazz , injector);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return injector;
    }
}
