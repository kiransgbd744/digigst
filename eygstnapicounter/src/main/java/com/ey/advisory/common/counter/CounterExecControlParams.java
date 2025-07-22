package com.ey.advisory.common.counter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * 
 * @author Jithendra.B
 *
 */
@Component
public final class CounterExecControlParams {

	private Map<String, Map<Integer, Integer>> limitMap = new ConcurrentHashMap<>();

	private Map<String, Map<Integer, Integer>> usageMap = new ConcurrentHashMap<>();

	public Map<String, Map<Integer, Integer>> getLimitMap() {
		return limitMap;
	}

	public Map<String, Map<Integer, Integer>> getUsageMap() {
		return usageMap;
	}

	public void clearLimitMap(String groupCode) {

		if (limitMap.containsKey(groupCode)) {
			limitMap.get(groupCode).clear();
		}

	}

	public void resetUsageMap() {
		usageMap.clear();
	}

}
