package org.thivernale.inventory.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(org.thivernale.inventory.logging.Logged)")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("Class {}, Thread {}, Args {}",
            joinPoint.getSignature()
                .getDeclaringType()
                .getSimpleName(),
            Thread.currentThread()
                .getName(), Arrays.stream(joinPoint.getArgs())
                .map(Object::toString)
                .collect(Collectors.joining(", ")));

        return joinPoint.proceed();
    }
}
