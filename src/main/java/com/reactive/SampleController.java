package com.reactive;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    @RequestMapping("/hello")
    public Publisher<String> hello(String name) {
        return s -> s.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                s.onNext("hello " + name);
                s.onComplete();
            }

            @Override
            public void cancel() {

            }
        });
    }
}
