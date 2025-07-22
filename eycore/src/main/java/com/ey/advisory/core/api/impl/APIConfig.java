package com.ey.advisory.core.api.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class APIConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;
	protected String apiId;
	protected String curVersion;
	protected String action;
	
	private Map<String, APIVersionConfig> versionConfigMap = 
					new LinkedHashMap<>();
	
	public APIConfig(String apiId, String curVersion, String action) {
		super();
		this.apiId = apiId;
		this.curVersion = curVersion;
		this.action = action;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public String getApiId() {
		return apiId;
	}

	public String getCurVersion() {
		return curVersion;
	}

	public List<String> getActiveVersions() {
		List<String> versions = new ArrayList<>();
		for(Map.Entry<String, APIVersionConfig> entry: 
					versionConfigMap.entrySet()) {
			versions.add(entry.getKey());
		}
		return versions;
	}
	
	public APIConfig addVersionConfig(
			String version, APIVersionConfig config) {
		versionConfigMap.put(version, config);
		return this;
	}
	
	public APIVersionConfig getConfigForVersion(String version) {
		return versionConfigMap.get(version);
	}

	@Override
	public String toString() {
		return "APIConfig [apiId=" + apiId + ", curVersion=" + curVersion 
				+ ", action=" + action + ", versionConfigMap="
				+ versionConfigMap + "]";
	}

	

}
