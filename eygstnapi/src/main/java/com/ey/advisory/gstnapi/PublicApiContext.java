package com.ey.advisory.gstnapi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * This Class is responsible to hold the Public API call details through out the
 * Thread cycle
 * 
 * @author Jithendra.B
 *
 */
@Component
public class PublicApiContext {

	private PublicApiContext() {
	}

	private static final ThreadLocal<Map<String, Object>> contextMap = ThreadLocal
			.withInitial(HashMap::new);

	public static void setContextMap(String key, Object value) {
		contextMap.get().put(key, value);
	}

	public static Object getContextMap(String key) {

		return contextMap.get().get(key);
	}

	public static void clearPublicApiContext() {
		contextMap.remove();
	}

}
