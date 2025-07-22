package com.ey.advisory.app.services.common;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.common.StaticContextHolder;

public class FileStatusPerfUtil {

	public static void setFileId(Long fileId) {
		FileStatusPerfEventsLogger eventsLogger = 
				(FileStatusPerfEventsLogger) StaticContextHolder.getBean(
						"FileStatusPerfEventsLogger", 
						FileStatusPerfEventsLogger.class);
		eventsLogger.setFileId(fileId);
	}

	public static void logEvent(String event) {
		FileStatusPerfEventsLogger eventsLogger = 
				(FileStatusPerfEventsLogger) StaticContextHolder.getBean(
						"FileStatusPerfEventsLogger", 
						FileStatusPerfEventsLogger.class);
		eventsLogger.logEvent(event);
	}
	
	public static void logEvent(String event,String desc) {
		FileStatusPerfEventsLogger eventsLogger = 
				(FileStatusPerfEventsLogger) StaticContextHolder.getBean(
						"FileStatusPerfEventsLogger", 
						FileStatusPerfEventsLogger.class);
		eventsLogger.logEvent(event);
	}
	
	public static void logValidationPerfStats(List<Pair<String, String>> events){
		FileStatusPerfEventsLogger eventsLogger = 
				(FileStatusPerfEventsLogger) StaticContextHolder.getBean(
						"FileStatusPerfEventsLogger", 
						FileStatusPerfEventsLogger.class);
		eventsLogger.logValidationPerfStats(events);
	}
	
}
