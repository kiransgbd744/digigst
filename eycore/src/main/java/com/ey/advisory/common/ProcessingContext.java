package com.ey.advisory.common;

import java.util.HashMap;
import java.util.Map;

/**
 * This class can be used as a mechanism to pass certain values to a chain of
 * processing components. For eaxmple, if a chain of validation processes 
 * are to be executed, we'll attach certain attributes at the start of process
 * using setAttribute and pass this context object to each of the validator
 * processes in the chain. This can also be used to pass certain objects 
 * from one process in the chain to the other.
 * 
 * In short, this is a place where we can accumulate state during execution
 * and propagate it along a chain of executors.
 *  
 * @author Sai.Pakanati
 *
 */
public class ProcessingContext {
	
	protected Map<String, Object> map = new HashMap<>();
	
	public void seAttribute(String key, Object value) {
		map.put(key, value);
	}
	
	public Object getAttribute(String key) {
		return map.get(key);
	}
	
	/**
	 * Return a string representation of the map, for debugging.
	 */
	public String toString() {
		return "[" + GenUtil.mapToString(map) + "]";
	}
}

