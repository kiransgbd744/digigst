package com.ey.advisory.einv.app.api.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NICAPIExecStatisticsLogger {
	
	public static void printExecStatistics(String msg) {
		LOGGER.info(msg);
	}

}
