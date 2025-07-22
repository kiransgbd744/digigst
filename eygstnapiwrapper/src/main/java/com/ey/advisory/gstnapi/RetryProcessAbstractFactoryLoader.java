/**
 * 
 */
package com.ey.advisory.gstnapi;

import com.ey.advisory.common.StaticContextHolder;

/**
 * @author Khalid1.Khan
 *
 */
class RetryProcessAbstractFactoryLoader {

	private static final RetryProcessAbstractFactoryLoader INSTANCE = new RetryProcessAbstractFactoryLoader();

	private RetryProcessAbstractFactoryLoader() {
	}

	public RetryProcessAbstractFactory load(String retryBlockName) {

		if (retryBlockName.equals("TokenResponseRetryBlockImpl"))
			return new DefaultRetryProcessAbstractFactory(
					StaticContextHolder.getBean("SuccessBlockImpl",
							SuccessBlock.class),
					StaticContextHolder.getBean("FailureBlockImpl",
							FailureBlock.class),
					StaticContextHolder.getBean("InvocationProcessorPolicyMgr",
							PolicyManager.class),
					StaticContextHolder.getBean("TokenResponseRetryBlockImpl",
							RetryBlock.class));
		else if (retryBlockName.equals("FileChunkResponseRetryBlockImpl"))
			return new DefaultRetryProcessAbstractFactory(
					StaticContextHolder.getBean("SuccessBlockImpl",
							SuccessBlock.class),
					StaticContextHolder.getBean("FailureBlockImpl",
							FailureBlock.class),
					StaticContextHolder.getBean("InvocationProcessorPolicyMgr",
							PolicyManager.class),
					StaticContextHolder.getBean(
							"FileChunkResponseRetryBlockImpl",
							RetryBlock.class));
		else
			return new DefaultRetryProcessAbstractFactory(
					StaticContextHolder.getBean("SuccessBlockImpl",
							SuccessBlock.class),
					StaticContextHolder.getBean("FailureBlockImpl",
							FailureBlock.class),
					StaticContextHolder.getBean("InvocationProcessorPolicyMgr",
							PolicyManager.class),
					StaticContextHolder.getBean("ApiInvocationRetryBlock",
							RetryBlock.class));

	}

	public static RetryProcessAbstractFactoryLoader getInstance() {
		return INSTANCE;
	}

}
