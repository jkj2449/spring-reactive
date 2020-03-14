package com.reactive.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Slf4j
@EnableAsync
@Component
public class AsyncExample implements ApplicationRunner {

    @Component
    public static class MyService {
        @Async
        public Future<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(1000);
            return new AsyncResult<>("hello");
        }
    }

    @Autowired
    private MyService myService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("run()");
        Future<String> f = myService.hello();
        log.info("exit: " + f.isDone());
        log.info("result: " + f.get());
    }
}
