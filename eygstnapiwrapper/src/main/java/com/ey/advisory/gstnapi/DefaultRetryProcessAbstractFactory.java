package com.ey.advisory.gstnapi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Khalid1.Khan
 *
 */
@Getter
@RequiredArgsConstructor
public class DefaultRetryProcessAbstractFactory implements 
			RetryProcessAbstractFactory {
	
	private final SuccessBlock successBlock;
	
	private final FailureBlock failureBlock;
	
	private final PolicyManager policyManager;
	
	private final RetryBlock retryBlock;
	

}
