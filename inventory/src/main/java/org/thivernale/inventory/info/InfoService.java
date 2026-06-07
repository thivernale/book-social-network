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
        Thread.sleep(100L);
        return CompletableFuture.supplyAsync(() -> {
            double nextDouble = random.nextDouble() * 1000;
            long randomSleep = (long) nextDouble;
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

    @Async("asyncTaskExecutor")
    public CompletableFuture<Long> step5() throws InterruptedException {
        log.info("step5 " + Thread.currentThread()
            .getName());
        Thread.sleep(100L);
        log.info("step5 finished");
        return CompletableFuture.supplyAsync(System::currentTimeMillis);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<Double> step6() throws InterruptedException {
        log.info("step6 " + Thread.currentThread()
            .getName());
        Thread.sleep(1000L);
        log.info("step6 finished");
        return CompletableFuture.supplyAsync(random::nextGaussian);
    }
}
