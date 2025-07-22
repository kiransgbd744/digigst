package com.ey.advisory.gstnapi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DefaultAPIExecEventsLogger")
public class DefaultAPIExecEventsLogger implements APIExecEventsLogger {
	
	@Autowired
	@Qualifier("EyLoggerPool")
	private ThreadPoolTaskExecutor execPool;
	
	private Queue<LogInfo> logQueue = 
			new ConcurrentLinkedQueue<>();
	
	@PostConstruct
	private void init() {
		List<LoggerTask> tasks = IntStream.range(0, 5).boxed()
				.map(e -> new LoggerTask(logQueue))
				.collect(Collectors.toList());
		tasks.forEach(task -> execPool.execute(task));
	}
		
	
	
	@Override
	public void log(Long reqId, String msgType, String msg, 
			String errCode, Throwable th) {
		
		LogInfo info = new LogInfo(reqId, msgType, msg, errCode,
				LocalDateTime.now(), TenantContext.getTenantId());
		// Currently only a logger debug message is introduced.
		if (APIExecEventsLogger.MSGTYPE_INFO.equals(msgType)) {
			if (LOGGER.isDebugEnabled()) LOGGER.debug(info.toString());
		} else {
			if (th != null) LOGGER.error(info.toString(), th);
			else LOGGER.error(info.toString());
		}
		
		// Code to store the error message to persistent storage.
		
		logQueue.offer(info);
		
	}

}
