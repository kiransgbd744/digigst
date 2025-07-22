package com.ey.advisory.einv.app.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.core.api.APIException;

public class APIResponse {
	
	protected String response;
	protected List<APIError> errors = new ArrayList<>();
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(APIResponse.class);
	
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
	
	public APIError getError() {
		if (isSuccess()) {		
			String msg = "Attempt to call getErrors method "
					+ "when GSTN API returned success!!";
			LOGGER.error(msg);
			throw new APIException(msg);			
		}		
		return errors.get(0); 
	}
	
	
	public boolean isSuccess() {
		return (response != null) && 
				errors.size() == 0;
	}
	
}
