package com.zxj.jdispatcher.dispatcher;

import android.os.Handler;
import android.os.Looper;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class JDispatcher<T>{
    static Handler handler = new Handler(Looper.getMainLooper());
    public JCallback<T> start(Class<? extends AbsThing> clazz, Callback<T> callback){
        final JCallback<T> tjCallback = new JCallback<>(callback);
        AbsThing absThing = getIThing(clazz);
        Futures.addCallback(absThing.startFuture(), new FutureCallback<T>() {
            private final Callback<T> realCallback = tjCallback.callback;

            @Override
            public void onSuccess(@NullableDecl final T result) {
                if (!tjCallback.cancel){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            realCallback.onResult(result,null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                if (!tjCallback.cancel){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            realCallback.onResult(null,t);
                        }
                    });
                }
            }
        }, Executors.newCachedThreadPool());
        return tjCallback;
    }

    public JObserver<T> start(Class<? extends AbsThing> clazz, Observer<T> callback){
        final JObserver<T> tjObserver = new JObserver<>(callback);
        tjObserver.register();
        AbsThing absThing = getIThing(clazz);
        absThing.startFuture();
        return tjObserver;
    }

    public JObserver<T> startSticky(Class<? extends AbsThing> clazz, Observer<T> callback){
        final JObserver<T> tjObserver = new JObserver<>(callback);

        AbsThing absThing = getIThing(clazz);
        ListenableFuture<T> listenableFuture = absThing.startFuture();
        if (listenableFuture.isDone()){
            Message<T> message = new Message<>();
            try {
                message.result = listenableFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                message.error = e;
            }
            tjObserver.onMessageEvent(message);
        }else {
            tjObserver.register();
        }

        return tjObserver;
    }

    public T start(Class<? extends AbsThing> clazz){
        AbsThing<T> absThing = getIThing(clazz);
        try {
            return absThing.startFuture().get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    private AbsThing<T> getIThing(Class<? extends AbsThing> clazz) {
        AbsThing<T> absThing = ThingQueue.get(clazz);
        if (absThing == null){
            absThing = ThingQueue.add(clazz);
        }
        return absThing;
    }
}
