package com.zhang.handler;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 在当前主线程中起 handler
        new MyHandler(MainActivity.this).sendEmptyMessageDelayed(1,3000);
        // 非自定义线程，自定义 handler
        // 在当子线程中起 handler，注意需要自己加 looper
        new Thread(){
            @Override
            public void run() {
                Log.i(TAG, "run: ========= 1"+Thread.currentThread().getName());
                // 延迟3s执行
                Looper.prepare();
                new MyHandler(MainActivity.this).sendEmptyMessageDelayed(2,3000);
                Looper.loop();
            }
        }.start();

        // 在自定义子线程从开启 handler
        MyThread thread = new MyThread();
        thread.start();
        Handler handler = new Handler(thread.myLooper){
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, "handleMessage: ======== 3 "+" message: "+msg.what +" "+Thread.currentThread().getName());
            }
        };
        handler.sendEmptyMessageDelayed(3,3000);
    }

    private class MyThread extends Thread {
        public Looper myLooper;
        @Override
        public void run() {
            Looper.prepare();
            myLooper = Looper.myLooper();
            Looper.loop();
        }
    }

    public static class MyHandler extends Handler{
        private WeakReference<Activity>reference;
        MyHandler(Activity activity){
            reference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            if (reference.get() != null){
                // 在这里去处理 UI刷新
                Log.i(TAG, "handleMessage: =========  2"+" message:"+msg.what +" "+Thread.currentThread().getName());
            }
        }
    }
}
