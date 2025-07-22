package com.ey.advisory.einv.app.api;

import java.io.Serializable;

public class APIHttpReqParamConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String name;
	protected boolean isMandatory;
	
	public APIHttpReqParamConfig(String name, boolean isMandatory) {
		this.name = name;
		this.isMandatory = isMandatory;
	}

	public String getName() {
		return name;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	@Override
	public String toString() {
		return "APIHttpReqParamConfig [name=" + name + 
				", isMandatory=" + isMandatory + "]";
	}

	
}
