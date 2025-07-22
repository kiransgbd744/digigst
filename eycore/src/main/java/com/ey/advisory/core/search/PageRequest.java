package com.ey.advisory.core.search;

/**
 * A common search pattern is to request for a page of information from a 
 * search service.  This class encapsulates the details be be provided while
 * invoking the search service for a particular piece of information. This 
 * class can be overridden to provide any additional pagination related 
 * information (if required). 
 * 
 * @author Sai.Pakanati
 *
 */
public class PageRequest {
	
	/**
	 * The number of the page of information required by the caller.
	 */
	protected int pageNo;
	/**
	 * The number of items to be included in a page of information. The search
	 * service will use this to calculate the total number of pages available,
	 * if needed.
	 */
	protected int pageSize;
	
	/**
	 * A common search pattern is to display a page of information to the user
	 * along with a footer information of the current page number and total 
	 * number of pages required. Usually, the user can get the total count
	 * while displaying the first page of the search, and then reuse this total
	 * count for the subsequent pages, instead of getting the total count 
	 * re-evaluated for each page of information. This variable specifies 
	 * whether the search service should evaluate the count and return it
	 * as part of the search result. Setting this to false will reduce the load
	 * on the DB.
	 */
	protected boolean isCountReq = false;

	public PageRequest(int pageNo, int pageSize) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	public PageRequest(int pageNo, int pageSize, boolean isCountReq) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.isCountReq = isCountReq;
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public boolean isCountReq() {
		return isCountReq;
	}

	@Override
	public String toString() {
		return "PageRequest [pageNo=" + pageNo + ", pageSize=" + pageSize
				+ ", isCountReq=" + isCountReq + "]";
	}	

}
