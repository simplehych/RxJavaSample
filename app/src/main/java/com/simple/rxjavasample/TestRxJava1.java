package com.simple.rxjavasample;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author hych
 * @date 2019/1/22 15:37
 */
public class TestRxJava1 {

    private volatile String TAG = getClass().getSimpleName();

    public void test(final Context context) {
        Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        subscriber.onNext(1);
                        subscriber.onNext(2);
                        subscriber.onNext(3);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(Integer integer) {
                        return Observable.just("flatMap-" + integer);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return "map-" + s;
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(context, "doOnSubscribe call", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .take(2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {

                    @Override
                    public void onStart() {
                        Log.i(TAG, "Subscriber onStart");

                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "Subscriber onNext " + s);
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "Subscriber onStart");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "Subscriber onStart");
                    }
                });
    }

    public void testOperator() {
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1);
                subscriber.onCompleted();
            }
        });

        observable.lift(new Observable.Operator<String, Integer>() {

            @Override
            public Subscriber<? super Integer> call(final Subscriber<? super String> subscriber) {
                return new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();

                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        subscriber.onNext("" + integer);
                    }
                };
            }
        });
    }


    public void testCompose() {
        Observable<Integer> observable1 = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {

            }
        });

        Subscriber<String> subscriber1 = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        };

        observable1.compose(new LiftTransform()).subscribe(subscriber1);
    }

    public void testC() {

    }

    class LiftTransform implements Observable.Transformer<Integer, String> {

        @Override
        public Observable<String> call(Observable<Integer> integerObservable) {
            return integerObservable
                    .map(new Func1<Integer, String>() {
                        @Override
                        public String call(Integer integer) {
                            return "" + integer;
                        }
                    })
                    .map(new Func1<String, String>() {
                        @Override
                        public String call(String s) {
                            return "s" + s;
                        }
                    });
        }
    }

}
