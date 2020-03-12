package com.reactive.sample;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class FluxScEx {
    public static void main(String[] args) {
        Flux.range(1, 10)
                .publishOn(Schedulers.newSingle("PUB"))
                .log()
                .subscribeOn(Schedulers.newSingle("SUB"))
                .subscribe(System.out::println);
    }
}
