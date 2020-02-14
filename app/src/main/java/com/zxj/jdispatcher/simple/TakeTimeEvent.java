package com.zxj.jdispatcher.simple;

import com.zxj.jdispatcher.dispatcher.AbsEvent;

public class TakeTimeEvent extends AbsEvent<TakeTimeBean> {
    @Override
    public TakeTimeBean start() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TakeTimeBean takeTimeBean = new TakeTimeBean("take 5s");
        return takeTimeBean;
    }

    @Override
    public boolean paramterCheck() {
        return false;
    }

    @Override
    public String tag() {
        return "take time";
    }
}
