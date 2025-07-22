package com.ey.advisory.ewb.app.api;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;
@ToString
public class APIResponse {
	
	protected String response;
	protected List<APIError> errors = new ArrayList<APIError>();
	
	public String getResponse() {
		return response;
	}
	
	public void setResponse(String response) {
		this.response = response;
	}
	
	public APIResponse addError(APIError error) {
		errors.add(error);
		return this;
	}
	
	public List<APIError> getErrors() {
		return errors;
	}
	
	
	public boolean isSuccess() {
		return (response != null) && 
				errors.size() == 0;
	}
	
}
