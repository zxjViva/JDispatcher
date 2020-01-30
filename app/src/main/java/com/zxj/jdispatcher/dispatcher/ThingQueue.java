package com.zxj.jdispatcher.dispatcher;

import java.util.LinkedHashMap;

public class ThingQueue {

    private static LinkedHashMap<String, AbsThing> map = new LinkedHashMap<>(16);

    static synchronized AbsThing add(Class<? extends AbsThing> clazz){
        AbsThing absThing = null;
        try {
            absThing = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        if (absThing != null && !map.containsKey(absThing.tag())){
            map.put(absThing.tag(), absThing);
        }
        return absThing;
    }

    static synchronized AbsThing get(Class<? extends AbsThing> clazz){
        try {
            AbsThing absThing = clazz.newInstance();
            return map.get(absThing.tag());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
