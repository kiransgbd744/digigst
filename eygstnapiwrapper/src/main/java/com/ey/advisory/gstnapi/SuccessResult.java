/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.List;

import lombok.Getter;

/**
 * @author Khalid1.Khan
 *
 */
@Getter

public class SuccessResult extends APIExecutionResult {

	private final List<Long> successIds;

	public SuccessResult(Long transactionId, List<Long> ids, String ctxParams) {
		super(transactionId, ctxParams);
		this.successIds = ids;
	}

}
