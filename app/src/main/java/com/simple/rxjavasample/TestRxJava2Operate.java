package com.simple.rxjavasample;

import android.util.Log;
import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author hych
 * @date 2019/1/28 16:53
 */
public class TestRxJava2Operate {

    private volatile String TAG = getClass().getSimpleName();

    public void testCreate() {
        Observable<Integer> integerObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
            }
        });
        integerObservable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "testCreate first " + integer);
            }
        });
        integerObservable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "testCreate second " + integer);
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
        Observable.empty()
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

    public void testFlatMap() {
        Observable.range(1, 5)
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        return Observable.just(integer + "");
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "testFlatMap accept " + s);
                    }
                });
    }

    public void testFlatMapIterable() {

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

    public void testTimeout() {
        Observable
                .create(new ObservableOnSubscribe<Long>() {
                    @Override
                    public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                        emitter.onNext(1L);
                        emitter.onNext(2L);
                        Thread.sleep(600);
                        emitter.onNext(3L);
                        emitter.onNext(4L);
                    }
                })
                .timeout(500, TimeUnit.MILLISECONDS, Observable.just(999L))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(TAG, "testTimeout: " + aLong);
                    }
                });
    }

    public void testCatch() {

        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onError(new Exception());
                        emitter.onNext(3);
                    }
                })
                .onErrorReturnItem(666)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "testCatch: " + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "testCatch: " + throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "testCatch Action ");
                    }
                });
    }

    public void testRetry() {
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onError(new Throwable("Error1"));
                        //                        emitter.onError(new Throwable("Error2"));
                        emitter.onNext(3);
                    }
                })
                .retry(2, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        return true;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "testRetry onSubscribe " + d.isDisposed());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "testRetry onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "testRetry onError: " + e);

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "testRetry onComplete");
                    }
                });
    }

    public void testAll() {
        Observable.range(5, 5)
                .all(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer > 5;
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.i(TAG, "testAll " + aBoolean);
                    }
                });
    }

    public void testIsEmpty() {
        Observable
                .range(5, 0)
                .isEmpty()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.i(TAG, "testIsEmpty " + aBoolean);
                    }
                });
    }

    public void testSequenceEqual() {
        Observable
                .sequenceEqual(Observable.range(1, 5),
                        Observable.range(1, 5));
    }

    public void testAmb() {
        Observable
                .amb(Arrays.asList(Observable.range(1, 5),
                        Observable.range(6, 5)))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "testAmb " + integer);
                    }
                });
    }

    public void testToList() {
        Observable
                .range(1, 5)
                .toSortedList(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        Log.i(TAG, "testToList  o1: " + o1 + " o2: " + o2);
                        return -o1;
                    }
                })
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.i(TAG, "testToList " + integers);
                    }
                });
    }

    public void testToMap() {
        Observable.range(8, 10)
                .toMap(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return Integer.toHexString(integer);
                    }
                })
                .subscribe(new Consumer<Map<String, Integer>>() {
                    @Override
                    public void accept(Map<String, Integer> stringIntegerMap) throws Exception {
                        Log.i(TAG, "testToMap " + stringIntegerMap);
                    }
                });

        Observable.range(8, 10)
                .toMultimap(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return Integer.toHexString(integer);
                    }
                })
                .subscribe(new Consumer<Map<String, Collection<Integer>>>() {
                    @Override
                    public void accept(Map<String, Collection<Integer>> stringCollectionMap) throws Exception {
                        Log.i(TAG, "testToMultimap " + stringCollectionMap);
                    }
                });
    }

    public void testTo() {
        String to = Observable.range(1, 5)
                .to(new Function<Observable<Integer>, String>() {
                    @Override
                    public String apply(Observable<Integer> integerObservable) throws Exception {
                        return "";
                    }
                });
    }

    public void testElement() {
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
//                        emitter.onError(new Throwable());
                    }
                })
                .delay(1, TimeUnit.SECONDS)
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

    public void testMerge() {
        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("observable11");
                emitter.onError(new Throwable("observable1"));
                emitter.onNext("observable12");

            }
        });
        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("observable21");
                emitter.onNext("observable22");
                emitter.onError(new Throwable("observable2"));

            }
        });
        Observable.mergeDelayError(observable1, observable2)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "testMerge onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "testMerge onNext " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "testMerge onError " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "testMerge onComplete");
                    }
                });
    }

    public void testConcat() {
        Observable<Integer> o1 = Observable.just(1, 2, 3).delay(5, TimeUnit.SECONDS);
        Observable<Integer> o2 = Observable.just(11, 12, 13).delay(3, TimeUnit.SECONDS);
        Observable<Integer> o3 = Observable.just(21, 22, 23);
        Observer subscriber = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "testConcat onSubscribe");
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, "testConcat onNext " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "testConcat onError " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "testConcat onComplete");
            }
        };

        Observable.concatEager(Arrays.asList(o1, o2, o3)).subscribe(subscriber);
    }
}
