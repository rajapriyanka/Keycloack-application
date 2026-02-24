package com.flight.aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

	@Pointcut("execution(* com.flight.service.*.*(..)) || " + "execution(* com.flight.controller.*.*(..))")
	public void serviceLayer() {
	}

	@Around("serviceLayer()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

		Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

		logger.info("Method Started: {}", joinPoint.getSignature().toShortString());

		logger.info("Arguments: {}", Arrays.toString(joinPoint.getArgs()));

		Object result = joinPoint.proceed();

		logger.info("Method Ended: {}", joinPoint.getSignature().toShortString());

		logger.info("Return Value: {}", result);

		return result;
	}
}