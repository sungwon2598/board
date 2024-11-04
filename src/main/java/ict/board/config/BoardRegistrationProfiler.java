package ict.board.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
@Profile("local")
public class BoardRegistrationProfiler {

    private ThreadLocal<StopWatch> totalStopWatch = new ThreadLocal<>();

    @Around("execution(public * ict.board.service.board.BoardService.*(..))")
    public Object profileMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        if (totalStopWatch.get() == null) {
            totalStopWatch.set(new StopWatch());
            totalStopWatch.get().start("Total Execution");
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        stopWatch.stop();
        log.info("{}.{} 실행 시간: {}ms", className, methodName, stopWatch.getTotalTimeMillis());

        if (methodName.equals("saveBoard") || methodName.equals("saveReservationBoard")) {
            totalStopWatch.get().stop();
            log.info("총 실행 시간: {}ms", totalStopWatch.get().getTotalTimeMillis());
            totalStopWatch.remove();
        }

        return result;
    }
}