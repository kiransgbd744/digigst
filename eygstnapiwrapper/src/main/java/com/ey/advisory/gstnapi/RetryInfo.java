package com.ey.advisory.gstnapi;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Khalid1.Khan
 *
 */
@RequiredArgsConstructor
@Getter
public final class RetryInfo {
	
	@Expose
	private final int immediateRetryCount;
	
	@Expose
	private final int delayedRetryCount;
	
	@Expose
	private final LocalDateTime initialExecAt;

	@Override
	public String toString() {
		return "RetryInfo [immediateRetryCount=" + immediateRetryCount
				+ ", delayedRetryCount=" + delayedRetryCount
				+ ", initialExecAt=" + initialExecAt + "]";
	}
	
	

}
