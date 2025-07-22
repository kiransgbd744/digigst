/**
 * 
 */
package com.ey.advisory.gstnapi;

import com.ey.advisory.core.api.impl.APIError;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Khalid1.Khan
 *
 */
@Getter
@RequiredArgsConstructor
public class APIInvocationError extends APIError {
	
	private final String errorType;
	
	private final Exception ex;
	
	

	public APIInvocationError(String errorType,APIError error ,Exception ex) {
		super();
		this.errorType = errorType;
		this.errorCode =error.getErrorCode();
		this.errorDesc = error.getErrorDesc();
		this.ex = ex;
	}
	

}
