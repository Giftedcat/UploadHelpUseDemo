package com.giftedcat.demo.http;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class HttpManager {

    public static Observable<Object> upload(){
        return Observable.just(new Object()).delay(1, TimeUnit.SECONDS);
    }

}
