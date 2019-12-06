package com.giftedcat.demo.application;

import android.app.Application;

import com.giftedcat.uploadhelp.DBHelper;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initDb();
    }

    /**
     * 初始化数据库
     * */
    private void initDb() {
        DBHelper.getInstance().init(getApplicationContext());
    }

}
