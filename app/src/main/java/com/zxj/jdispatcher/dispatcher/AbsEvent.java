package com.zxj.jdispatcher.dispatcher;

import android.content.Context;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbsEvent<T> {
    public ExecutorService executorService = Executors.newCachedThreadPool();
    private ListenableFuture<T> listenableFuture;
    private final Object LOCK = new Object();

    public abstract T start(Context context);

    ListenableFuture<T> startFuture(final Context context) {
        synchronized (LOCK) {
            if (listenableFuture == null) {
                listenableFuture = MoreExecutors
                        .listeningDecorator(executorService)
                        .submit(new Callable<T>() {
                            @Override
                            public T call() throws Exception {
                                return start(context);
                            }
                        });
                Futures.addCallback(listenableFuture, new FutureCallback<T>() {
                    @Override
                    public void onSuccess(@NullableDecl T result) {
                        Message<T> message = new Message<>(result, null);
                        sendMessage(message);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Message<T> message = new Message<>(null, t);
                        sendMessage(message);
                    }
                }, executorService);
            }
            return listenableFuture;
        }
    }

    boolean cancel() {
        synchronized (LOCK){
            if (listenableFuture != null) {
                boolean cancel = listenableFuture.cancel(true);
                listenableFuture = null;
                return cancel;
            }
            return false;
        }
    }

    public abstract boolean paramterCheck();

    public abstract String tag();

    public void sendMessage(Message<T> message) {
        JEventBus.post(message);
    }

    public void updata(ListenableFuture<T> listenableFuture) {
        synchronized (LOCK){
            this.listenableFuture = listenableFuture;
        }
    }
    public void destory(){
        cancel();
    }
}
