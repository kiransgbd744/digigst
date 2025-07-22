package com.ey.advisory.core.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.core.api.impl.APIError;

public class APIResponse {
	
	protected String response;
	/*rek is random encrypted key returned by gstn*/
	protected String rek;
	/*sk is the session key(decrypted sek)*/
	protected String sk;
	
	protected String txnId;
	
	protected List<APIError> errors = new ArrayList<>();
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(APIResponse.class);
	
	public String getResponse() {
		return response;
	}
	
	public void setResponse(String response) {
		this.response = response;
	}
	
	public String getRek() {
		return rek;
	}

	public void setRek(String rek) {
		this.rek = rek;
	}
	
	public String getSk() {
		return sk;
	}

	public void setSk(String sk) {
		this.sk = sk;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getTxnId() {
		return txnId;
	}

	public APIResponse addError(APIError error) {
		errors.add(error);
		return this;
	}
	
	public List<APIError> getErrors() {
		if (isSuccess()) {
			String msg = "Attempt to call getErrors method "
					+ "when GSTN API returned success!!";
			LOGGER.error(msg);
			throw new APIException(msg);
		}
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
				errors.isEmpty();
	}

	@Override
	public String toString() {
		return "APIResponse [response=" + response + ", txnId=" + txnId
				+ ", errors=" + errors + "]";
	}
	
}
