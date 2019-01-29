package com.simple.rxjavasample;

import android.util.Log;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;

import java.util.Collections;
import java.util.List;
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

    public void testMapCast() {
        Observable.range(1, 5)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "" + integer;
                    }
                })
                .cast(Integer.class);
    }

    public void testFlatMapIterable() {
        Observable.range(1, 5)
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        return null;
                    }
                });

        Observable.range(1, 5)
                .flatMapIterable(new Function<Integer, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(Integer integer) throws Exception {
                        return Collections.singleton(String.valueOf(integer));
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });
    }

    public void testBuffer() {
        Observable.range(1, 7)
                .buffer(3)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.i(TAG, integers.toString());
                        Log.i(TAG, "----");
                    }
                });
    }

    public void testGroupBy() {
        Observable<GroupedObservable<Integer, Integer>> groupedObservableObservable
                = Observable.range(1, 6)
                .groupBy(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer;
                    }
                });

        Observable.concat(groupedObservableObservable)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, integer + "");
                        Log.i(TAG, "----");
                    }
                });
    }

    public void testWindow() {
        Observable.range(1, 10)
                .window(3)
                .subscribe(new Consumer<Observable<Integer>>() {
                    @Override
                    public void accept(Observable<Integer> integerObservable) throws Exception {
                        integerObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {

                            }
                        });
                    }
                });
    }

    public void testThrottleWithTimeout() {
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .throttleWithTimeout(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(TAG, "testThrottleWithTimeout " + aLong);
                    }
                });
    }

    public void testStartWith() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

            }
        }).startWith("1");
    }
}
