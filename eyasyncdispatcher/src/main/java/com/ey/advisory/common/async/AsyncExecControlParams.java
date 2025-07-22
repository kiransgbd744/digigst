package com.ey.advisory.common.async;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

/**
 * This class represents all the control parameters that are available to the
 * AsyncExec application. Note that this class is a singleton class shared by
 * all components within the AsyncExec application. Extreme care has to be
 * exercised to ensure that every thread safety measure is included in the code
 * to set/accces the state in this class.
 * 
 * @author Sai.Pakanati
 *
 */
@Component
public final class AsyncExecControlParams {
	/**
	 * The flag that is set when the suspend web service call is executed. The
	 * fetcher and distributor loop will honor this flag and stop fetching and
	 * distributing. Instead the thread will sleep and periodically check if the
	 * suspend flag is revoked. If so, the thread will resume its normal
	 * activities.
	 */
	private AtomicBoolean suspended = new AtomicBoolean(false);

	public AtomicBoolean getSuspendFlag() {
		return this.suspended;
	}

	public boolean isSuspended() {
		return suspended.get();
	}

	public boolean setSuspended(boolean suspended) {

		return this.suspended.compareAndSet(!suspended, suspended);
	}

	public String getStatus() {
		return this.suspended.get() ? "SUSPENDED" : "RUNNING";
	}
}
