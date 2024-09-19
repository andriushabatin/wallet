package com.batin.wallet.annotation;

import com.batin.wallet.thread.RequestQueueProcessor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Aspect
@Component
public class RequestPerSecondLimitingAspect {

    @Autowired
    private RequestQueueProcessor requestQueueProcessor;

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RequestPerSecondLimited rateLimited) throws Throwable {
        CompletableFuture<Object> future = new CompletableFuture<>();
        requestQueueProcessor.addRequest(() -> {
            try {
                Object result = joinPoint.proceed();
                future.complete(result);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        return future.join();
    }
}
