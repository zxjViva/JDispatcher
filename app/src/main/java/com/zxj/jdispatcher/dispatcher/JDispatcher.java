package com.zxj.jdispatcher.dispatcher;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class JDispatcher<T> {
    static Handler handler = new Handler(Looper.getMainLooper());

    public JCallback<T> start(Class<? extends AbsEvent> clazz, Context context, Callback<T> callback) {
        final JCallback<T> tjCallback = new JCallback<>(callback);
        AbsEvent absEvent = getEvent(clazz);
        Futures.addCallback(absEvent.startFuture(context), new FutureCallback<T>() {
            private final Callback<T> realCallback = tjCallback.callback;

            @Override
            public void onSuccess(@NullableDecl final T result) {
                if (!tjCallback.cancel) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            realCallback.onResult(result, null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                if (!tjCallback.cancel) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            realCallback.onResult(null, t);
                        }
                    });
                }
            }
        }, Executors.newCachedThreadPool());
        return tjCallback;
    }

    //事件级的取消，不是针对回调级别的，
    public void cancelEvent(Class<? extends AbsEvent> clazz) {
        AbsEvent absEvent = getEvent(clazz);
        absEvent.cancel();
    }

    public JObserver<T> start(Class<? extends AbsEvent> clazz, Context context, Observer<T> callback) {
        final JObserver<T> tjObserver = new JObserver<>(callback);
        tjObserver.register();
        AbsEvent absEvent = getEvent(clazz);
        absEvent.startFuture(context);
        return tjObserver;
    }

    public JObserver<T> startSticky(Class<? extends AbsEvent> clazz, Context context, Observer<T> callback) {
        final JObserver<T> tjObserver = new JObserver<>(callback);

        AbsEvent absEvent = getEvent(clazz);
        ListenableFuture<T> listenableFuture = absEvent.startFuture(context);
        if (listenableFuture.isDone()) {
            Message<T> message = new Message<>();
            try {
                message.result = listenableFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                message.error = e;
            }
            tjObserver.onMessageEvent(message);
        } else {
            tjObserver.register();
        }

        return tjObserver;
    }

    public T start(Class<? extends AbsEvent> clazz, Context context) {
        AbsEvent<T> absEvent = getEvent(clazz);
        try {
            return absEvent.startFuture(context).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    private AbsEvent<T> getEvent(Class<? extends AbsEvent> clazz) {
        AbsEvent<T> absEvent = EventQueue.get(clazz);
        if (absEvent == null) {
            absEvent = EventQueue.add(clazz);
        }
        return absEvent;
    }
}
