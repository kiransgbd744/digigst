package com.ey.advisory.gstnapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.gstnapi.domain.client.APIInvocationLogger;
import com.ey.advisory.gstnapi.repositories.client.GstnWrapperLoggerRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
public class LoggerTask implements Runnable {
	
	
	private GstnWrapperLoggerRepository logRepo;

	private static final int BATCH_SIZE = 25;
	
	private int messageCount;

	private Queue<LogInfo> queue;
	
	private Map<String, List<LogInfo>> logInfoMap = new HashMap<>();
	
	private static final long SLEEP_DURATION = 2000;
	
	private static final long EXCEPTION_SLEEP_DURATION = 5000;

	private static final long MAX_IDLE_TIME = 3000;
	
	public LoggerTask(Queue<LogInfo> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		boolean isQueueEmpty = false;
		
		// Iterate till we obtain the repository.
		while (true) {
			logRepo = loadRepo();
			if (logRepo != null) break;
			try {
				Thread.sleep(EXCEPTION_SLEEP_DURATION);
			} catch(InterruptedException ex) {
				// Do nothing 
				Thread.currentThread().interrupt();
			}
		}
		
		while (true) {
			try {
				if (messageCount == BATCH_SIZE
						|| (isQueueEmpty && messageCount > 0)) {
					saveAll(logInfoMap);
					logInfoMap.clear();
					try {
						Thread.sleep(SLEEP_DURATION);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}
				}
				LogInfo logInfo = queue.poll();
				if (logInfo == null) {
					isQueueEmpty = true;
					try {
						Thread.sleep(MAX_IDLE_TIME);
					} catch(InterruptedException ex) {
						// Do nothing
						Thread.currentThread().interrupt();
					}
				} else {
					logInfoMap.computeIfAbsent(logInfo.getGroupCode(), k -> 
						new ArrayList<>()).add(logInfo);
					messageCount++;
					isQueueEmpty = false;
				}
			} catch (Exception e) {
				LOGGER.error("Exception Occured while logging, "
						+ "Suspending the logger thread for " + 
						EXCEPTION_SLEEP_DURATION + " seconds", e);
				try {
					Thread.sleep(EXCEPTION_SLEEP_DURATION);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	private GstnWrapperLoggerRepository loadRepo() {
		try {
			logRepo = StaticContextHolder.getBean("GstnWrapperLoggerRepository",
					GstnWrapperLoggerRepository.class);
			return logRepo;
		} catch(NullPointerException ex) {
			// The NullPointerException will be encountered only if the
			// Spring CONTEXT is not initialized and hence the 
			// StaticContextHolder is not able to load the bean. In this case
			// return null
			String msg = String.format("Spring context is not yet initialized. "
					+ "Waiting for another %d milli seconds to fetch the "
					+ "GstnWrapperLoggerRepository", EXCEPTION_SLEEP_DURATION);
			LOGGER.info(msg);
			return null;
		}
	}

	private void saveAll(Map<String, List<LogInfo>> logInfoMap) {
		logInfoMap.forEach((k,v) -> {
			TenantContext.setTenantId(k);
			logRepo.saveAll(convertToEntity(v));
		});
		
	}

	private List<APIInvocationLogger> convertToEntity(
			List<LogInfo> logInfoList) {
		return logInfoList.stream()
				.map(o -> new APIInvocationLogger(o.getReqId(),
						o.getMsgType(), o.getMsg(),null,
						GstnApiWrapperConstants.SYSTEM, o.getTime()))
				.collect(Collectors.toList());
	}

}
