package org.thivernale.inventory.info;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class InfoService {
    private static final Log log = LogFactory.getLog(InfoService.class);
    private final Random random = new Random();

    public void step1() throws InterruptedException {
        log.info("step1 " + Thread.currentThread()
            .getName());
        Thread.sleep(100L);
    }

    public void step2() throws InterruptedException {
        log.info("step2 " + Thread.currentThread()
            .getName());
        Thread.sleep(200L);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<String> step3() throws InterruptedException {
        log.info("step3 " + Thread.currentThread()
            .getName());
        Thread.sleep(1000L);
        return new CompletableFuture<String>().completeAsync(() -> {
            long randomSleep = random.nextLong() * 1000;
            try {
                Thread.sleep(randomSleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("step3 finished");
            return "after %d ms".formatted(randomSleep);
        });
    }

    @Async("asyncTaskExecutor")
    public void step4() throws InterruptedException {
        log.info("step4 " + Thread.currentThread()
            .getName());
        Thread.sleep(2000L);
        log.info("step4 finished");
    }
}
