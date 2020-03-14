package com.reactive.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@EnableAsync
@Component
public class AsyncExample implements ApplicationRunner {

    @Component
    public static class MyService {
        @Async
        public ListenableFuture<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(1000);
            return new AsyncResult<>("hello");
        }
    }

    @Bean
    ThreadPoolTaskExecutor tp() {
        ThreadPoolTaskExecutor tp = new ThreadPoolTaskExecutor();
        tp.setCorePoolSize(10);
        tp.setMaxPoolSize(100);
        tp.setQueueCapacity(200);
        tp.setThreadNamePrefix("myThread");
        tp.initialize();
        //core pool 생성 -> queue 생성 -> max pool 생성

        return tp;
    }

    @Autowired
    private MyService myService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("run()");
        ListenableFuture<String> f = myService.hello();
        f.addCallback(s -> System.out.println(s), e -> System.out.println(e.getMessage()));
        log.info("exit");
    }
}
