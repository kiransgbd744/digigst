package com.ey.advisory.common.async;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.StaticContextHolder;

/**
 * Currently, this method hard codes the use of the 
 * DBTaskFetcherAndDistributor. Later on, the Spring Component name can be 
 * read from a DB, depending on the Message Source and Executor Thread Pool 
 * implementation used.
 * 
 * @author Sai.Pakanati
 *
 */

@Component("DefaultTaskFetcherAndDistributorFactory")
public class DefaultTaskFetcherAndDistributorFactory 
			implements TaskFetcherAndDistributorFactory {

	@Override
	public TaskFetcherAndDistributor getTaskFetcherAndDistributor() {
		TaskFetcherAndDistributor fetcherAndDistributor = 
				StaticContextHolder.getBean("DBTaskFetcherAndDistributor", 
						TaskFetcherAndDistributor.class);
		return fetcherAndDistributor;
	}

}
