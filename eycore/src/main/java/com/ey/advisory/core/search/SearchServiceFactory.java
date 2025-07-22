package com.ey.advisory.core.search;

/**
 * This class is responsible for locating the SearchService based on the 
 * criteria passed and the search result type required by the caller. 
 * 
 * @author Sai.Pakanati
 *
 */
public interface SearchServiceFactory {
	
	public <R> SearchService getSearchService(
				SearchCriteria criteria, Class<? extends R> searchResult);
	
}
