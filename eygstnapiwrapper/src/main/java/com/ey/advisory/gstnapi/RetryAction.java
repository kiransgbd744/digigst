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
@RequiredArgsConstructor
@Getter
public final class RetryAction {
	
	private final RetryActionType retryType;
	
	private final RetryInfo nextRetryInfo;
	
	private final long delay;
	
	
	public boolean isImmediateRetry() {
		return retryType == RetryActionType.RETRY_IMMEDIATELY;
	}
	
	public boolean isDelayedRetry() {
		return retryType == RetryActionType.RETRY_LATER;
	}
	
	public boolean isFailure() {
		return retryType == RetryActionType.MARK_AS_FAILED;
	}

	@Override
	public String toString() {
		return "RetryAction [retryType=" + retryType + ", nextRetryInfo="
				+ nextRetryInfo + ", delay=" + delay + "]";
	}
	
	

}
