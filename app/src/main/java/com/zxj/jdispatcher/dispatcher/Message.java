package com.zxj.jdispatcher.dispatcher;

public class Message<T> {
    T result;
    Throwable error;

    public Message(T result, Throwable error) {
        this.result = result;
        this.error = error;
    }

    public Message() {
    }
}
