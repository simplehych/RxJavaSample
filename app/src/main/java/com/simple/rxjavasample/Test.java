package com.simple.rxjavasample;

import android.util.Log;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author hych
 * @date 2019/1/17 09:08
 */
public class Test {

    private String TAG = "Test";

    public void testCreate() {
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
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
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {

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

    public void testConcat() {

        Observable.concat(Observable.just(1, "2"), Observable.just(3, "4"))
                .subscribe(new Consumer<Serializable>() {
                    @Override
                    public void accept(Serializable serializable) throws Exception {
                        Log.i(TAG, "testConcat: " + serializable.toString());
                    }
                });
    }

    public void testFlatMap() {
        Observable.just(1, 2, 3)
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        return Observable.just(" Fun" + integer).delay(1, TimeUnit.SECONDS);
                    }
                }, new BiFunction<Integer, String, String>() {
                    @Override
                    public String apply(Integer integer, String s) throws Exception {
                        return integer + s;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String str) throws Exception {
                        Log.i(TAG, "testFlatMap: " + str);
                    }
                });
    }

    public void testConcatMap() {
        Observable.just(1, 2, 3)
                .concatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            list.add(integer + "" + integer);
                        }
                        Log.i(TAG, "testConcatMap concatMap apply: " + list);

                        return Observable.fromIterable(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "testConcatMap accept: " + s);
                    }
                });
    }

    /**
     * 去除，若有参数方法根据方法运算之后的去除
     */
    public void testDistinct() {
        Observable.just(1, 1, 2, 3, 4, 4, 4, 5, 6, 7)
                .distinct(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        if (integer == 2) {
                            return integer * 3;
                        } else if (integer == 3) {
                            return integer * 2;
                        }
                        return integer;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "testDistinct accept: " + integer);
                    }
                });
    }

    /**
     * 过滤，保留满足条件的
     */
    public void testFilter() {
        Observable
                .just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        if (integer > 5) {
                            // true，满足条件，发射事件
                            return true;
                        } else {
                            // false，不满足条件，过滤掉
                            return false;
                        }
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "testFilter accept: " + integer);
                    }
                });
    }

    public void testBuffer() {
        Observable
                .just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                // 输出 [1, 2, 3] \n [5, 6, 7] \n [9, 10]
                .buffer(3, 4)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.i(TAG, "testBuffer accept: " + integers);

                    }
                });
    }

    /**
     * 定时任务
     */
    public void testTimer() {
        Log.i(TAG, "testTimer  currentTime: " + new Date().toLocaleString());
        // 页面销毁 关闭 disposable.dispose() 否则继续执行
        Disposable disposable = Observable.timer(5, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(TAG, "testTimer  currentTime: " + new Date().toLocaleString());
                        Log.i(TAG, "testTimer  accept aLong: " + aLong);
                    }
                });
    }

    /**
     * 间隔任务
     */
    public void testInterval() {
        Log.i(TAG, "testInterval  currentTime: " + new Date().toLocaleString());
        // 页面销毁 关闭 disposable.dispose() 否则继续执行
        Disposable disposable = Observable.interval(3, 2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(TAG, "testInterval  currentTime: " + new Date().toLocaleString());
                        Log.i(TAG, "testInterval  aLong: " + aLong);
                    }
                });
    }

    public void testDoOnNext() {
        Observable.just(1, 2, 3, 4, 5)
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "testDoOnNext doOnNext  accept: " + integer * 2);
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "testDoOnNext subscribe  accept: " + integer);
                    }
                });
    }

    public void testSkip() {
        Observable.just(1, 2, 3, 4, 5)
                .skip(1)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "testSkip subscribe  accept: " + integer);
                    }
                });
    }

    public void testTake() {
        Observable.just(1, 2, 3, 4, 5)
                .take(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "testTake subscribe  accept: " + integer);
                    }
                });
    }

    public void testSingle() {

        Single.just(new Random().nextInt())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "testSingle  onSubscribe: " + d);
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.i(TAG, "testSingle  onSuccess: " + integer);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "testSingle  onError: " + e);
                    }
                });
    }

    /**
     * 两个相邻数据发射的时间间隔决定了前一个数据是否被丢弃
     * 去除发送频道过快的事件
     */
    public void testDebounce() {
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        // skip
                        emitter.onNext(1);
                        Thread.sleep(400);
                        // deliver 400ms 收到2 小于设定500ms，把前一个丢掉，现在只有一个2
                        emitter.onNext(2);
                        Thread.sleep(505);
                        // 过了505ms 收到一个3，符合设定时间，保存，现在是 2，3
                        emitter.onNext(3);
                        Thread.sleep(100);
                        // 过了100ms 收到4，小于设定时间，把前一个3丢弃，现在是2，4
                        emitter.onNext(4);
                        Thread.sleep(605);
                        // 过了605ms 收到5，符合设定时间，保存，现在是2，4，5
                        emitter.onNext(5);
                        emitter.onComplete();
                    }
                })
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, " testDebounce subscribe  integer: " + integer);
                    }
                });
    }

    public void testDefer() {
        Observable<Integer> defer = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return Observable.just(1, 2, 3);
            }
        });
        defer.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, " testDefer  onSubscribe: " + d);
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, " testDefer  onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, " testDefer  onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, " testDefer  onComplete ");
            }
        });
    }

    /**
     * 发射的最后一个事件，如果是空Observable，则返回默认值
     */
    public void testLast() {
        Observable
                .just(1, 2, 3, 4, 5)
                .last(4)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, " testLast  accept: " + integer);
                    }
                });
    }

    public void testMerge() {
        Observable
                .merge(Observable.just(1, 2, 3), Observable.just(4, 5, 6))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, " testMerge  accept: " + integer);
                    }
                });
    }

    public void testReduce() {
        Observable
                .just(1, 2, 3, 4, 5)
                .reduce(2, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, " testReduce  accept: " + integer);
                    }
                });
    }

    public void testScan() {
        Observable
                .just(1, 2, 3, 4, 5)
                .scan(3, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, " testScan  accept: " + integer);
                    }
                });

        Observable
                .just(1, 2, 3, 4, 5)
                .scanWith(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return 3;
                    }
                }, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, " testScanWith  accept: " + integer);
                    }
                });


    }

    public void testWindow() {

        Observable.interval(1, TimeUnit.SECONDS)
                .take(15)
                .window(5)
                .subscribe(new Consumer<Observable<Long>>() {
                    @Override
                    public void accept(Observable<Long> longObservable) throws Exception {
                        Log.i(TAG, " testWindow  longObservable: " + longObservable);
                        longObservable.subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                Log.i(TAG, " testWindow  longObservable: " + aLong);
                            }
                        });
                    }
                });
    }

}
