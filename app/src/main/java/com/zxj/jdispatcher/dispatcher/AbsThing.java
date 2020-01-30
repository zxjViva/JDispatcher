package com.zxj.jdispatcher.dispatcher;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbsThing<T> {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private ListenableFuture<T> listenableFuture;

    public abstract T start();

    ListenableFuture<T> startFuture() {
        if (listenableFuture == null){
            listenableFuture = MoreExecutors
                    .listeningDecorator(executorService)
                    .submit(new Callable<T>() {
                        @Override
                        public T call() throws Exception {
                            return start();
                        }
                    });
            Futures.addCallback(listenableFuture, new FutureCallback<T>() {
                @Override
                public void onSuccess(@NullableDecl T result) {
                    Message<T> message = new Message<>(result,null);
                    sendMessage(message);
                }

                @Override
                public void onFailure(Throwable t) {
                    Message<T> message = new Message<>(null,t);
                    sendMessage(message);
                }
            }, executorService);
        }
        return listenableFuture;

    }

    boolean cancel(){
        if (listenableFuture != null){
            return listenableFuture.cancel(true);
        }
        return false;
    }

    public abstract boolean paramterCheck();

    public abstract String tag();

    private void sendMessage(Message<T> message){
        JEventBus.post(message);
    }
}
