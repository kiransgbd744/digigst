package com.ey.advisory.common;

import java.util.HashMap;
import java.util.Map;

public class AppExecContext {
	
	/**
	 * Key/Value pairs can be added to this map during the course of 
	 * execution.
	 * 
	 */
	protected Map<String, Object> values = new HashMap<>();
	
	protected String userName;
	
	public Object getValue(String key) { return values.get(key); }
	
	public AppExecContext setValue(String key, Object value) {
		values.put(key, value);
		return this;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
