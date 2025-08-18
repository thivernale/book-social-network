package org.thivernale.inventory.info;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class InfoService {
    private static final Log log = LogFactory.getLog(InfoService.class);

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
    public void step3() throws InterruptedException {
        log.info("step3 " + Thread.currentThread()
            .getName());
        Thread.sleep(1000L);
        log.info("step3 finished");
    }

    @Async("asyncTaskExecutor")
    public void step4() throws InterruptedException {
        log.info("step4 " + Thread.currentThread()
            .getName());
        Thread.sleep(2000L);
        log.info("step4 finished");
    }
}
