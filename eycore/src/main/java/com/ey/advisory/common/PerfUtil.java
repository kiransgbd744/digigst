package com.ey.advisory.common;

import java.time.LocalDateTime;
import java.util.List;

import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.domain.client.PerformanceStatistics;
import com.ey.advisory.repositories.client.PerformanceStstcsRepository;

public class PerfUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PerfUtil.class);

	private PerfUtil() {
	}

	private static boolean isMonitoringEnabled(String moduleName) {

		PerformanceConfig config = StaticContextHolder
				.getBean("PerformanceConfig", PerformanceConfig.class);
		return config.isMonitoringEnabled(moduleName);
	}

	public static void logEvent(String moduleName, String eventName,
			String className, String methodName, String context) {
		if (isMonitoringEnabled(moduleName)) {
			PerformanceStstcsRepository perfRepo = StaticContextHolder.getBean(
					"PerformanceStstcsRepository",
					PerformanceStstcsRepository.class);
			PerformanceStatistics ststcs = new PerformanceStatistics(
					UUIDContext.getUniqueID(), moduleName, eventName, className,
					methodName, "LOC", context);
			ststcs.setEventDateTime(LocalDateTime.now());
			perfRepo.save(ststcs);
		}
	}

	public static void logEventToFile(String moduleName, String eventName,
			String className, String methodName, String context) {
		if (isMonitoringEnabled(moduleName)) {
			String perfLog = String.format("%s,%s,%s,%s,%s,%s,%s,%s",
					UUIDContext.getUniqueID(), moduleName, eventName, className,
					methodName, "LOC", context, LocalDateTime.now());
			LOGGER.error(perfLog);
		}
	}

	private static void logEventToFile(String moduleName, String eventName,
			String className, String methodName, String context,
			LocalDateTime date) {
		String perfLog = String.format("%s,%s,%s,%s,%s,%s,%s,%s",
				UUIDContext.getUniqueID(), moduleName, eventName, className,
				methodName, "LOC", context, date);
		LOGGER.error(perfLog);
	}

	public static void logEventToFile(String moduleName, String eventName,
			String className, String methodName, String context,
			LocalDateTime date, boolean isLoggingRequired) {
		if (isLoggingRequired) {
			String perfLog = String.format("%s,%s,%s,%s,%s,%s,%s,%s",
					UUIDContext.getUniqueID(), moduleName, eventName, className,
					methodName, "LOC", context, date);
			LOGGER.error(perfLog);
		}
	}

	public static void logEventToFile(
			List<Triplet<String, String, String>> events, String moduleName,
			String eventName) {
		if (isMonitoringEnabled(moduleName)) {
			LocalDateTime date = LocalDateTime.now();
			events.forEach(event -> {
				logEventToFile(moduleName, eventName, event.getValue0(),
						event.getValue1(), event.getValue2(), date);
			});
		}
	}

	public static void logEventToFile(String moduleName, String eventName,
			String className, String methodName, String context, boolean isLoggingRequired) {
		if (isLoggingRequired) {
			String perfLog = String.format("%s,%s,%s,%s,%s,%s,%s,%s",
					UUIDContext.getUniqueID(), moduleName, eventName, className,
					methodName, "LOC", context, LocalDateTime.now());
			LOGGER.error(perfLog);
		}
	}
}
