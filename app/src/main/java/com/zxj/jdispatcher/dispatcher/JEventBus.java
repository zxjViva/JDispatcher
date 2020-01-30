package com.zxj.jdispatcher.dispatcher;

import com.google.common.eventbus.EventBus;

public class JEventBus {
    private static EventBus eventBus = new EventBus();

    private JEventBus() {

    }

    public static void register(Object obj) {
        eventBus.register(obj);
    }

    public static void unregister(Object obj) {
        eventBus.unregister(obj);
    }

    public static void post(Object obj) {
        eventBus.post(obj);
    }

}
