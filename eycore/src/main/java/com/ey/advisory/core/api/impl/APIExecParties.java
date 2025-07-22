package com.ey.advisory.core.api.impl;

public class APIExecParties {
	
	protected APIProvider apiProvider;
	
	protected APIExecChannel apiExecChannel;
	
	protected APIEndUser apiEndUser;
	
	public APIExecParties() {}

	public APIExecParties(APIProvider apiProvider,
			APIExecChannel apiExecChannel, APIEndUser apiEndUser) {
		super();
		this.apiProvider = apiProvider;
		this.apiExecChannel = apiExecChannel;
		this.apiEndUser = apiEndUser;
	}

	public APIProvider getApiProvider() {
		return apiProvider;
	}

	public APIExecChannel getApiExecChannel() {
		return apiExecChannel;
	}

	public APIEndUser getApiEndUser() {
		return apiEndUser;
	}

	@Override
	public String toString() {
		return "APIExecParties [apiProvider=" + apiProvider 
				+ ", apiExecChannel=" + apiExecChannel + ", apiEndUser="
				+ apiEndUser + "]";
	}

}
