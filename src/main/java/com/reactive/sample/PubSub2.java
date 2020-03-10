package com.reactive.sample;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
*   pub - [data1] -> mapPub -> [data2] -> logSub
*                  <- subscribe(logSub)
*                  -> onSubscribe(s)
*                  -> onNext
*                  -> onNext
*                  -> onComplete
*   1.map (d1 -> f - > d2)
*
 */
@Slf4j
public class PubSub2 {
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
        Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
        Publisher<Integer> subPub = sumPub(pub);
        Publisher<String> reducePub = reducePub(pub, "", (a,b) -> a + "-" + b);
        subPub.subscribe(logSub());

    }

    private static Publisher<String> reducePub(Publisher<Integer> pub, String i, BiFunction<String, Integer, String> bif) {
        return new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> subscriber) {
                pub.subscribe(new DelegateSub<Integer, String>(subscriber) {
                    String result = i;

                    @Override
                    public void onNext(Integer item) {
                        result = bif.apply(result, item);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(result);
                        subscriber.onComplete();
                    }
                });
            }
        };
    }

    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                pub.subscribe(new DelegateSub<Integer, Integer>(subscriber) {
                    int sum = 0;
                    @Override
                    public void onNext(Integer item) {
                        sum += item;
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(sum);
                        subscriber.onComplete();
                    }
                });
            }
        };
    }

    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> subscriber) {
                pub.subscribe(new DelegateSub<T, R>(subscriber) {
                    @Override
                    public void onNext(T item) {
                        subscriber.onNext(f.apply(item));
                    }
                });
            }
        };
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.debug("onSubscription");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T item) {
                log.debug("onNext:{}", item);
            }

            @Override
            public void onError(Throwable throwable) {
                log.debug("onError:{}", throwable);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(List<Integer> iter) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iter.forEach(s -> subscriber.onNext(s));
                            subscriber.onComplete();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }
}
