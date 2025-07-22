/**
 * 
 */
package com.ey.advisory.gstnapi;

/**
 * @author Khalid1.Khan
 *
 */
public interface RetryProcessAbstractFactory {
	
	public SuccessBlock getSuccessBlock();
	
	public FailureBlock getFailureBlock();
	
	public PolicyManager getPolicyManager();
	
	public RetryBlock getRetryBlock();
	
	public static RetryProcessAbstractFactory of(String name) {
		return RetryProcessAbstractFactoryLoader.getInstance()
				.load(name);
	}

}
