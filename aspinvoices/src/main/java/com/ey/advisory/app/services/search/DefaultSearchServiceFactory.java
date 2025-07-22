package com.ey.advisory.app.services.search;

import org.springframework.stereotype.Service;

import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchService;
import com.ey.advisory.core.search.SearchServiceFactory;

@Service("DefaultSearchServiceFactory")
public class DefaultSearchServiceFactory implements SearchServiceFactory {

	@Override
	public <R> SearchService getSearchService(SearchCriteria criteria,
			Class<? extends R> searchResult) {
		
		return null;
	}
	
}
