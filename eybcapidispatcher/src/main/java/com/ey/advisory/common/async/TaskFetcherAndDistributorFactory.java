package com.ey.advisory.common.async;

/**
 * The implementation of this interface is responsible for looking-up
 * and returning the appropriate instance of the TaskFetcherAndDistributor
 * instance.
 * 
 * @author Sai.Pakanati
 *
 */
public interface TaskFetcherAndDistributorFactory {
	
	public TaskFetcherAndDistributor getTaskFetcherAndDistributor();
}
