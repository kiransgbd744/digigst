package com.ey.advisory.core.search;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * The search service can pass additional information other than the search
 * result entities, back to the caller. One such common example is the count.
 * This class can be extended to pass additional information back to the 
 * caller.
 * 
 * @author Sai.Pakanati
 *
 * @param <T>
 */
public class SearchResult<T> {

	/**
	 * Store an empty Immutable empty List to be returned in case the search
	 * service creates a search result with a null list or an empty list.
	 */
	private static final List<?> EMPTY_LIST = ImmutableList.of();
	
	protected int totalCount = 0;
	protected List<? extends T> result = new ArrayList<>();
	protected boolean isCountAvailable = false;
	
	/**
	 * This data member will have the original page request submitted to the
	 * search service.
	 */
	protected PageRequest pageReq = null;
	
	public SearchResult(T result) {
		super();
		List<T> list = new ArrayList<>();
		list.add(result);
		this.result = list;
	}
	
	@SuppressWarnings("unchecked")
	public SearchResult(List<T> result) {
		super();
		this.result = (result != null && !result.isEmpty()) ? result : 
				(List<? extends T>) EMPTY_LIST;
	}
	
	@SuppressWarnings("unchecked")
	public SearchResult(List<T> result, PageRequest pageReq) {
		super();
		this.result = (result != null && !result.isEmpty()) ? result : 
				(List<? extends T>) EMPTY_LIST;
		this.pageReq = pageReq;
		this.isCountAvailable = false;
	}

	@SuppressWarnings("unchecked")
	public SearchResult(List<T> result, PageRequest pageReq, int count) {
		this.result = (result != null && !result.isEmpty()) ? result : 
				(List<? extends T>) EMPTY_LIST;
		this.totalCount = count;
		this.pageReq = pageReq;
		this.isCountAvailable = true;
	}

	/**
	 * Get the Total number of search result elements matching the search 
	 * criteria. This value will make sense only if the isCountAvailable() 
	 * method returns true. Otherwise, this will always contain 0 as its value.
	 * 
	 * @return the total number of search result elements matching the 
	 * search criteria.
	 * 
	 */
	public int getTotalCount() {
		return totalCount;
	}

	public PageRequest getPageReq() {
		return this.pageReq;
	}
	
	public List<? extends T> getResult() {
		return result;
	}
	
	/**
	 * If the client is using the search result for a single item, then
	 * this method can be used. The method returns null if no item is set to 
	 * the list. Otherwise it returns the first item.
	 * 
	 * @return
	 */
	public T getSingleResult() {
		return !this.result.isEmpty() ? 
					this.result.get(0) : null;
	}

	public boolean isCountAvailable() {
		return isCountAvailable;
	}	
	
}
