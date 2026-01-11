package com.nicolas.pulse.infrastructure.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
@Slf4j
public class ControllerLogAspect {
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logAroundController(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = method.getName();

        long start = System.currentTimeMillis();
        log.info("[IN ] {}#{}", className, methodName);

        Object result = pjp.proceed(); // 呼叫原方法

        // 判斷返回值是否是 Mono 或 Flux
        if (result instanceof Mono<?> mono) {
            return mono
                    .doOnSubscribe(s -> log.info("[SUBSCRIBE] {}#{}", className, methodName))
                    .doFinally(signal -> log.info("[OUT] {}#{} | duration={}ms | signal={}",
                            className, methodName, System.currentTimeMillis() - start, signal));
        } else if (result instanceof Flux<?> flux) {
            return flux
                    .doOnSubscribe(s -> log.info("[SUBSCRIBE] {}#{}", className, methodName))
                    .doFinally(signal -> log.info("[OUT] {}#{} | duration={}ms | signal={}",
                            className, methodName, System.currentTimeMillis() - start, signal));
        } else {
            // 非 reactive 方法也打 log
            log.info("[OUT] {}#{} | duration={}ms", className, methodName, System.currentTimeMillis() - start);
            return result;
        }
    }

    @Around("@annotation(org.springframework.messaging.handler.annotation.MessageMapping)")
    public Object logRSocketLifecycle(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = method.getName();

        long start = System.currentTimeMillis();
        log.info("[IN ] {}#{}", className, methodName);

        Object result = joinPoint.proceed();
        if (result instanceof Mono<?> mono) {
            return mono
                    .doOnSubscribe(s -> log.info("[SUBSCRIBE] {}#{}", className, methodName))
                    .doFinally(signal -> log.info("[OUT] {}#{} | duration={}ms | signal={}", className, methodName, System.currentTimeMillis() - start, signal));
        } else if (result instanceof Flux<?> flux) {
            AtomicLong count = new AtomicLong(0);
            return flux
                    .doOnSubscribe(s -> log.info("[SUBSCRIBE] {}#{}", className, methodName))
                    .doOnNext(data -> count.incrementAndGet()) // 靜悄悄地計數
                    .doFinally(signal -> log.info("[OUT] {}#{} | items={} | duration={}ms | signal={}", className, methodName, count.get(), System.currentTimeMillis() - start, signal));
        } else {
            log.info("[OUT] {}#{} | duration={}ms", className, methodName, System.currentTimeMillis() - start);
            return result;
        }
    }
}
