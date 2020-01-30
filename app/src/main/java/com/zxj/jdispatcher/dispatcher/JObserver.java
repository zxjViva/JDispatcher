package com.zxj.jdispatcher.dispatcher;

import android.os.Handler;
import android.os.Looper;

import com.google.common.eventbus.Subscribe;

public class JObserver<T> {
    Observer<T> observer;
    static Handler handler = new Handler(Looper.getMainLooper());
    public JObserver(Observer<T> observer) {
        this.observer = observer;
    }

    public void register(){
        JEventBus.register(this);
    }

    public void unRegister(){
        JEventBus.unregister(this);
    }

    @Subscribe
    void onMessageEvent(final Message<T> event){
        if (observer != null && event != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    observer.onResult(event.result,event.error);
                }
            });
        }
    }

}
