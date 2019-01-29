package com.simple.rxjavasample;

import android.util.Log;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author hych
 * @date 2019/1/28 16:53
 */
public class TestRxJava2Operate {

    private volatile String TAG = getClass().getSimpleName();

    public void testCreate() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

            }
        });
    }

    public void testJust() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    public void testFrom() {
//        Observable.fromArray();
//        Observable.fromIterable();
//        Observable.fromCallable();
//        Observable.fromPublisher();
//        Observable.fromFuture();
    }

    public void testDefer() {
        Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return Observable.just(1);
            }
        });
    }

    public void testTimer() {
        Observable.timer(2, TimeUnit.SECONDS);
    }

    public void testIntervalRange() {
        Observable.interval(1, TimeUnit.SECONDS);
    }

    public void testRange() {
        Observable.range(1, 5);
    }

    public void testEmptyNeverError() {
        Observable.never()
                .subscribe(new Observer<Object>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "==================onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.i(TAG, "==================onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "==================onError " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "==================onComplete");
                    }
                });
    }
}
