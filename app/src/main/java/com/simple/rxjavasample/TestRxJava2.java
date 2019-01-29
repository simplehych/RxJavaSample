package com.simple.rxjavasample;

import android.util.Log;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.*;
import io.reactivex.subscribers.ResourceSubscriber;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author hych
 * @date 2019/1/25 09:31
 */
public class TestRxJava2 {

    private volatile String TAG = getClass().getSimpleName();

    public void test() {
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        Log.i(TAG, "ObservableEmitter onNext 0");
                        emitter.onNext(0);
                        Log.i(TAG, "ObservableEmitter onComplete");
                        emitter.onComplete();
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "doOnTerminate ");
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "doAfterTerminate ");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "doFinally ");
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "doOnComplete ");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "doOnError ");
                    }
                })
                .doOnEach(new Consumer<Notification<Integer>>() {
                    @Override
                    public void accept(Notification<Integer> integerNotification) throws Exception {
                        Log.i(TAG, "doOnEach ");
                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "doOnNext ");
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.i(TAG, "doOnSubscribe ");
                    }
                })
                .doAfterNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "doAfterNext ");
                    }
                })
                .doOnLifecycle(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.i(TAG, "doOnLifecycle Consumer");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "doOnLifecycle Action");
                    }
                })
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "Observer onSubscribe ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "Observer onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "Observer onError " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "Observer onComplete");
                    }
                });
    }

    public void testFlowable1() {
        Flowable
                .range(0, 100)
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Integer>() {
                    Subscription sub;

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.w(TAG, "onsubscribe start");
                        sub = s;
                        s.request(2);
                        Log.w(TAG, "onsubscribe end");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.w(TAG, "onNext--->" + integer);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sub.request(3);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.w(TAG, "onComplete");
                    }
                });
    }

    public void testFlowable2() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "Consumer accept integer: " + integer);
                    }
                });
    }

    public void testCompositeDisposable() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        ResourceSubscriber<Integer> resourceSubscriber
                = Flowable
                .range(1, 8)
                .subscribeWith(new ResourceSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(resourceSubscriber);

        DisposableObserver<Integer> disposableObserver = Observable.range(1, 8).subscribeWith(new DisposableObserver<Integer>() {
            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        compositeDisposable.add(disposableObserver);
    }

    public void testOnly() {
        /**
         * Thread Thread[Thread-1080,5,main]
         * Thread[RxCachedThreadScheduler-1,5,main]
         * Thread[main,5,main]
         */
        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        Log.i(TAG, "testOnly Observable " + Thread.currentThread());
                        emitter.onNext("1");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();


        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.i(TAG, "testOnly Thread " + Thread.currentThread());
            }
        }.start();
    }

    public void testSingle() {
        Single.create(new SingleOnSubscribe<Integer>() {
            @Override
            public void subscribe(SingleEmitter<Integer> emitter) throws Exception {

            }
        }).subscribe();
    }

    public void testCompletable() {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
            }
        }).subscribe();
    }

    public void testMaybe() {
        Maybe
                .create(new MaybeOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(MaybeEmitter<Integer> emitter) throws Exception {

                    }
                })
                .subscribe(new MaybeObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void testFuture() {
        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Log.d(TAG, "CallableDemo is Running");
                return "返回结果";
            }
        });
    }

    public void testFromCallable() {
        Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return null;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        });
    }

    public void testFromPublisher() {
        Observable.fromPublisher(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                s.onNext(1);
            }
        }).subscribe();
    }

    public void testFrom() {
        final FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 10;
            }
        });
        Observable
                .fromFuture(futureTask)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        futureTask.run();
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });

    }

    /**
     * 非粘性，只有先注册后发送事件才能接收
     * <p>
     * 输出
     * 01-27 18:14:33.600 20612-20612/com.simple.rxjavasample I/TestRxJava2: testPublishSubject first: 3
     * 01-27 18:14:33.600 20612-20612/com.simple.rxjavasample I/TestRxJava2: testPublishSubject first: 4
     * 01-27 18:14:33.600 20612-20612/com.simple.rxjavasample I/TestRxJava2: testPublishSubject first: 5
     * 01-27 18:14:33.600 20612-20612/com.simple.rxjavasample I/TestRxJava2: testPublishSubject second: 5
     * 01-27 18:14:33.600 20612-20612/com.simple.rxjavasample I/TestRxJava2: testPublishSubject first: 6
     * 01-27 18:14:33.600 20612-20612/com.simple.rxjavasample I/TestRxJava2: testPublishSubject second: 6
     */
    public void testPublishSubject() {
        PublishSubject<Integer> subject = PublishSubject.create();
        subject.onNext(1);
        subject.onNext(2);
        subject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "testPublishSubject first: " + integer);
            }
        });
        subject.onNext(3);
        subject.onNext(4);
        subject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "testPublishSubject second: " + integer);
            }
        });
        subject.onNext(5);
        subject.onNext(6);
    }

    /**
     * 非粘性，只有先注册后发送事件才能接收
     * 只有调用 onComplete 才能出发
     * <p>
     * 输出
     * 01-27 18:18:03.190 20612-20612/com.simple.rxjavasample I/TestRxJava2: testAsyncSubject: three
     */
    public void testAsyncSubject() {
        AsyncSubject<String> subject = AsyncSubject.create();
        subject.onNext("one");
        subject.onNext("two");
        subject.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "testAsyncSubject: " + s);
            }
        });
        subject.onNext("three");
        subject.onComplete();
    }

    /**
     * 只能收到订阅之前的最后一个事件 和订阅之后发送的事件
     * <p>
     * 输出
     * 01-27 18:18:27.990 20612-20612/com.simple.rxjavasample I/TestRxJava2: testBehaviorSubject: two
     * 01-27 18:18:27.990 20612-20612/com.simple.rxjavasample I/TestRxJava2: testBehaviorSubject: three
     * 01-27 18:18:27.990 20612-20612/com.simple.rxjavasample I/TestRxJava2: testBehaviorSubject: four
     */
    public void testBehaviorSubject() {
        BehaviorSubject<String> subject = BehaviorSubject.create();
        subject.onNext("zero");
        subject.onNext("one");
        subject.onNext("two");
        subject.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "testBehaviorSubject first: " + s);
            }
        });
//        subject.subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                Log.i(TAG, "testBehaviorSubject second: " + s);
//            }
//        });
        subject.onNext("three");
        subject.onNext("four");
    }

    /**
     * 粘性事件
     * <p>
     * 输出
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject first: zero
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject first: one
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject first: two
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject first: four
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject first: five
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject first: six
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject first: seven
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject second: zero
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject second: one
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject second: two
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject second: four
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject second: five
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject second: six
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject second: seven
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject first: eight
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject second: eight
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject first: nine
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject second: nine
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject first onComplete
     * 01-27 18:21:14.840 I/TestRxJava2: testRelaySubject second onComplete
     */
    public void testRelaySubject() {
        ReplaySubject<String> subject = ReplaySubject.create();
        subject.onNext("zero");
        subject.onNext("one");
        subject.onNext("two");
        subject.onNext("four");
        subject.onNext("five");

        subject.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "testRelaySubject first: " + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.i(TAG, "testRelaySubject first error");

            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.i(TAG, "testRelaySubject first onComplete");
            }
        });
        subject.onNext("six");
        subject.onNext("seven");
        subject.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "testRelaySubject second: " + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.i(TAG, "testRelaySubject second error");
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.i(TAG, "testRelaySubject second onComplete");
            }
        });
        subject.onNext("eight");
        subject.onNext("nine");
        subject.onComplete();
    }

    /**
     * 粘性事件，只能有一个观察者，java.lang.IllegalStateException: Only a single observer allowed.
     * <p>
     * 输出
     * 01-27 18:22:10.440 I/TestRxJava2: testUnicastSubject first: 0
     * 01-27 18:22:10.450 I/TestRxJava2: testUnicastSubject first: 1
     * 01-27 18:22:10.450 I/TestRxJava2: testUnicastSubject first: 2
     * 01-27 18:22:10.450 I/TestRxJava2: testUnicastSubject first: 3
     * 01-27 18:22:10.450 I/TestRxJava2: testUnicastSubject first: 4
     * 01-27 18:22:10.450 I/TestRxJava2: testUnicastSubject first: 5
     * 01-27 18:22:10.450 I/TestRxJava2: testUnicastSubject first: 6
     * 01-27 18:22:10.450 I/TestRxJava2: testUnicastSubject first: 7
     * 01-27 18:22:10.450 I/TestRxJava2: testUnicastSubject first: 8
     * 01-27 18:22:10.450 I/TestRxJava2: testUnicastSubject first: 9
     */
    public void testUnicastSubject() {
        UnicastSubject<Integer> subject = UnicastSubject.create();
        subject.onNext(0);
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);
        subject.onNext(4);
        subject.onNext(5);
        subject.onNext(6);
        subject.onNext(7);
        subject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "testUnicastSubject first: " + integer);
            }
        });
        subject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "testUnicastSubject second: " + integer);
            }
        });
        subject.onNext(8);
        subject.onNext(9);
    }



    public void testPublishProcessor() {
        PublishProcessor<Integer> publishProcessor = PublishProcessor.create();
        publishProcessor
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });
    }

    public void testMissBackpressureException1() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        source
//                .observeOn(Schedulers.computation(), false, 1024 * 1024)
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        compute(integer);
                    }
                });

        for (int i = 1; i < 1_000_000; i++) {
            source.onNext(i);
        }
    }

    public void testMissBackpressureException2() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        source
                .sample(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        compute(integer);
                    }
                });

        for (int i = 1; i < 1_000_000; i++) {
            source.onNext(i);
        }
    }

    public void testMissBackpressureException3() {
        Flowable
                .range(1, Integer.MAX_VALUE)
                .onBackpressureBuffer()
                .observeOn(Schedulers.computation(), false, 8)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        compute(integer);
                    }
                });
    }


    public void testMissBackpressureException4() {
        Flowable
                .range(1, Integer.MAX_VALUE)
                .onBackpressureDrop()
                .observeOn(Schedulers.computation(), false, 1)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        compute(integer);
                    }
                });
    }

    public void compute(int i) throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("computing : " + i + " thread: " + Thread.currentThread());
    }


}
