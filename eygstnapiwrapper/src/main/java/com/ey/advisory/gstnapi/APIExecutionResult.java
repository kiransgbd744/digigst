/**
 * 
 */
package com.ey.advisory.gstnapi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Khalid1.Khan
 *
 */
@Getter
@RequiredArgsConstructor
public abstract class APIExecutionResult {
	
	private final Long transactionId;
	
	private final String ctxParams;

}
