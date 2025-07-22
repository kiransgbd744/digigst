/**
 * 
 */
package com.ey.advisory.gstnapi;

import lombok.Getter;

/**
 * @author Khalid1.Khan
 *
 */
@Getter

public class FailureResult extends APIExecutionResult {

	private final APIInvocationError error;

	public FailureResult(Long transactionId, APIInvocationError error,
			String ctxParams) {
		super(transactionId, ctxParams);
		this.error = error;
	}

}
