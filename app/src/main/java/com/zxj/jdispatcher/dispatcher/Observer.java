package com.zxj.jdispatcher.dispatcher;

public interface Observer<T> {
    void onResult(T t, Throwable error);
}
