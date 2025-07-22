package com.ey.advisory.app.services.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Triplet;

public class PerfStatistics {
	
	private PerfStatistics() {}
	
	private static final ThreadLocal<Map<String, Pair<Long, Long>>> 
			VALIDATOR_MAP = 
			new ThreadLocal<Map<String, Pair<Long, Long>>>() {
		@Override
		protected Map<String, Pair<Long, Long>> initialValue() {
			return new HashMap<>();
		}
	};

	public static Map<String, Pair<Long, Long>> getValidationPerfMap() {
		return VALIDATOR_MAP.get();
	}

	public static void updateValidatorStat(String validatorCls,
			long execTime) {
		Map<String, Pair<Long, Long>> map = VALIDATOR_MAP.get();
		if (map.containsKey(validatorCls)) {
			Pair<Long, Long> pair = map.get(validatorCls);
			map.put(validatorCls, new Pair<Long, Long>(
					pair.getValue0() + execTime, pair.getValue1() + 1));
		} else {
			map.put(validatorCls, new Pair<Long, Long>(execTime, 1L));
		}
	}
	
	private static String formatDuration(long millis) {
		int seconds = (int) (millis / 1000) % 60 ;
		int minutes = (int) ((millis / (1000 * 60)) % 60);
		int hours   = (int) ((millis / (1000 * 60 * 60)) % 24);
		
		return String.format("%02d hrs: %02d min: %02d sec", 
				hours, minutes, seconds);
	}
	
	public static String getValidatorStat() {
		Map<String, Pair<Long, Long>> map = VALIDATOR_MAP.get();
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		builder.append("=============== DOC UPLOAD " + 
				"VALIDATION STATISTICS [START]===================");
		
		map.forEach((validatorCls, stats) -> {
			String outputStr = String.format(
					"[%s] -> Exec Time: %s,  No. Of Calls: %d, "
					+ "Per Call Duration: %s)", 
					validatorCls, 
					formatDuration(stats.getValue0()),
					stats.getValue1(),
					formatDuration(stats.getValue0() / stats.getValue1())
				);
			builder.append(outputStr);
		});
		
		builder.append("===============  DOC UPLOAD " + 
				"VALIDATION STATISTICS [ END]===================");		
		return builder.toString();
	}
	
	public static List<Pair<String, String>> getValidatorStatList() {
		Map<String, Pair<Long, Long>> map = VALIDATOR_MAP.get();
		
		List<Pair<String, String>> list = new ArrayList<>();
		
		map.forEach((validatorCls, stats) -> {
			String outputStr = String.format(
					"[%s] -> Exec Time: %s,  No. Of Calls: %d, "
					+ "Per Call Duration: %s)", 
					validatorCls, 
					formatDuration(stats.getValue0()),
					stats.getValue1(),
					formatDuration(stats.getValue0() / stats.getValue1())
				);
			list.add(new Pair<String, String>(validatorCls, outputStr));
		});
		

		return list;
	}
	
	public static List<Triplet<String, String,String>> getValidatorStats() {
		Map<String, Pair<Long, Long>> map = VALIDATOR_MAP.get();
		
		List<Triplet<String, String,String>> list = new ArrayList<>();
		
		map.forEach((validatorCls, stats) -> {
			String outputStr = String.format(
					"Exec Time: %s|  No. Of Calls: %d| "
					+ "Per Call Duration: %s", 
					formatDuration(stats.getValue0()),
					stats.getValue1(),
					formatDuration(stats.getValue0() / stats.getValue1())
				);
			list.add(new Triplet<String, String,String>(validatorCls,
					"validate",outputStr));
		});
		

		return list;
	}
	
	
	
}
