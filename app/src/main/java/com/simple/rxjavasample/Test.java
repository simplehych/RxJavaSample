package com.simple.rxjavasample;

import android.util.Log;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author hych
 * @date 2019/1/17 09:08
 */
public class Test {

    private String TAG = "Test";

    public void testCreate() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "ObservableOnSubscribe emit 1");
                emitter.onNext(1);
                Log.i(TAG, "ObservableOnSubscribe emit 2");
                emitter.onNext(2);
                Log.i(TAG, "ObservableOnSubscribe emit 3");
                emitter.onNext(3);
                Log.i(TAG, "ObservableOnSubscribe emit 4");
                emitter.onNext(4);
                Log.i(TAG, "ObservableOnSubscribe onComplete");
                emitter.onComplete();
                Log.i(TAG, "ObservableOnSubscribe emit 5");
                emitter.onNext(5);
                Log.i(TAG, "ObservableOnSubscribe emit -");
                Log.i(TAG, "ObservableOnSubscribe emit --");

            }
        }).subscribe(new Observer<Integer>() {

            private Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "Observer onSubscribe " + d.isDisposed());
                mDisposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, "Observer onNext " + integer);
                if (integer == 2) {
                    mDisposable.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "Observer onError " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "Observer onComplete ");
            }
        });
    }

    public void testMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "apply " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String str) throws Exception {
                Log.i(TAG, "testMap accept: " + str);
            }
        });
    }

    public void testZip() {
        ObservableOnSubscribe stringObservableOnSubscribe = new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("A");
                emitter.onNext("B");
                emitter.onNext("C");
            }
        };


        ObservableOnSubscribe integerObservaleOnSubscribe = new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onNext(5);
            }
        };

        ObservableSource<String> stringObservableSource = Observable.create(stringObservableOnSubscribe);
        ObservableSource<Integer> integerObservableSource = Observable.create(integerObservaleOnSubscribe);

        Observable.zip(stringObservableSource, integerObservableSource, new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "testZip: " + s);
            }
        });

    }

}
