package com.zxj.jdispatcher.dispatcher;

import java.util.LinkedHashMap;

public class EventQueue {

    private static LinkedHashMap<String, AbsEvent> map = new LinkedHashMap<>(16);

    static synchronized AbsEvent add(Class<? extends AbsEvent> clazz){
        AbsEvent absEvent = null;
        try {
            absEvent = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        if (absEvent != null && !map.containsKey(absEvent.tag())){
            map.put(absEvent.tag(), absEvent);
        }
        return absEvent;
    }

    static synchronized AbsEvent get(Class<? extends AbsEvent> clazz){
        try {
            AbsEvent absEvent = clazz.newInstance();
            return map.get(absEvent.tag());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
