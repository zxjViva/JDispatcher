package com.zxj.jdispatcher.simple;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.zxj.jdispatcher.R;
import com.zxj.jdispatcher.dispatcher.Callback;
import com.zxj.jdispatcher.dispatcher.JCallback;
import com.zxj.jdispatcher.dispatcher.JDispatcher;
import com.zxj.jdispatcher.dispatcher.JObserver;
import com.zxj.jdispatcher.dispatcher.Observer;

public class MainActivity extends AppCompatActivity {

    private JObserver<TakeTimeBean> takeTimeBeanJObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final JDispatcher<TakeTimeBean> takeTimeBeanJDispatcher = new JDispatcher<>();

        takeTimeBeanJDispatcher.start(TakeTimeEvent.class, this,new Callback<TakeTimeBean>() {
            @Override
            public void onResult(TakeTimeBean takeTimeBean, Throwable error) {
                Log.e("zxj", "onResult: "+ takeTimeBean.text);
            }
        });
        JCallback<TakeTimeBean> jCallback = takeTimeBeanJDispatcher.start(TakeTimeEvent.class,this, new Callback<TakeTimeBean>() {
            @Override
            public void onResult(TakeTimeBean takeTimeBean, Throwable error) {
                Log.e("zxj", "onResult1: " + takeTimeBean.text);
            }
        });
        jCallback.cancel();
        findViewById(R.id.tv).postDelayed(new Runnable() {
            @Override
            public void run() {
                takeTimeBeanJDispatcher.start(TakeTimeEvent.class,MainActivity.this, new Callback<TakeTimeBean>() {
                    @Override
                    public void onResult(TakeTimeBean takeTimeBean, Throwable error) {
                        Log.e("zxj", "onResult2: "+ takeTimeBean.text);
                    }
                });
            }
        },6000);
        findViewById(R.id.tv).postDelayed(new Runnable() {
            @Override
            public void run() {
                TakeTimeBean result = takeTimeBeanJDispatcher.start(TakeTimeEvent.class,MainActivity.this);
                Log.e("zxj", "onResult3: "+ result.text);
            }
        },7000);
        takeTimeBeanJObserver = takeTimeBeanJDispatcher.start(TakeTimeEvent.class, this, new Observer<TakeTimeBean>() {
            @Override
            public void onResult(TakeTimeBean takeTimeBean, Throwable error) {
                Log.e("zxj", "onResult4: " + takeTimeBean.text);
            }
        });

        findViewById(R.id.tv).postDelayed(new Runnable() {
            @Override
            public void run() {
                takeTimeBeanJDispatcher.startSticky(TakeTimeEvent.class, MainActivity.this,new Observer<TakeTimeBean>() {
                    @Override
                    public void onResult(TakeTimeBean takeTimeBean, Throwable error) {
                        Log.e("zxj", "onResult5: "+ takeTimeBean.text);
                    }
                });
            }
        },8000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        takeTimeBeanJObserver.unRegister();
    }
}
