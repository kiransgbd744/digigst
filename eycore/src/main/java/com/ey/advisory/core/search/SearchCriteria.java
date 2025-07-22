package com.ey.advisory.core.search;

/**
 * This is the base class for all search criteria. It encapsulates a string 
 * named 'searchType', which is a logical identification of the type of
 * search to be performed. This field can be used to route the search criteria
 * to an appropriate search module. 
 * 
 * @author Sai.Pakanati
 *
 */
public abstract class SearchCriteria {
	
	protected String searchType;

	
	public SearchCriteria(String searchType) {
		this.searchType = searchType;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	
}
