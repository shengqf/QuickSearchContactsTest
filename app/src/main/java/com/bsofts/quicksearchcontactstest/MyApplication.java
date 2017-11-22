package com.bsofts.quicksearchcontactstest;

import android.app.Application;

/**
 * Created by shengdaren on 2017/11/22.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null){
            instance = this;
        }
    }

    public static MyApplication getInstance(){
        return instance;
    }
}
