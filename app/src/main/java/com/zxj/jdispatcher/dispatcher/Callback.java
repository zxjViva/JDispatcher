package com.zxj.jdispatcher.dispatcher;

public interface Callback<T> {
    void onResult(T t, Throwable error);
}
