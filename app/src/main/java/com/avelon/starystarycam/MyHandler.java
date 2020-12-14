package com.avelon.starystarycam;

import android.os.Handler;
import android.os.HandlerThread;

public class MyHandler extends Handler {
    Thread thread;

    public MyHandler(String name) {
        super();

        this.thread = new HandlerThread(name);
        this.thread.start();
    }
}
