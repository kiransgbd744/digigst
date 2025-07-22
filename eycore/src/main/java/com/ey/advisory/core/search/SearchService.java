package com.ey.advisory.core.search;

import java.util.stream.Stream;

/**
 * Search is an extremely important piece of functionality in any application.
 * Usually searches are made by the end user through a user interface to display
 * paginated results of information that he can browse through OR 
 * by an application (like a reporting app) to locate a certain set of 
 * information to perform it's activities. The search will return a collection
 * of a specific type of object. So each search will input a SearchCriteria
 * object & an optional page info object and return a list of search result
 * objects. The methods of this interface abstracts this idea.
 * 
 * The implementation for a specific search can be decided based on the 
 * SearchCriteria and the search result class type passed by the caller. The
 * interface is modeled this way so that a search for the same entity can be
 * delegated to different processing units based on the conditions to search
 * for in the underlying storage and the information to return to the caller
 * in the search result. This is required because some of the basic searches
 * can be done using straight forward repository queries, while other searches
 * might require efficient mechanisms supported by the underlying storage or
 * other external caching mechanisms. 
 * 
 * This interface shields the caller from all such complexities involved in 
 * a search. So, the idea is to perfect the search mechanisms for each criteria
 * so that either end users OR other app components OR micro-services can use
 * the same search implementation.
 * 
 * 
 * @author Sai.Pakanti
 *
 */
public interface SearchService {

	
	/**
	 * Find a list of objects based on the search criteria. There will be 
	 * different search criteria for different types of results that 
	 * the user wants. This method accepts a page request and returns a
	 * a search result object containing the result list of elements. If the 
	 * pageReq parameter is null, the method returns the entire result in a 
	 * list, in a single page. 
	 * 
	 * This is suitable for displaying a page of information to an end user in 
	 * the UI; but this is highly unsuitable where the search results is very 
	 * huge and the client requires the entire results. An example would be 
	 * a reporting code that needs  generate a huge excel/pdf report that 
	 * contains more than a million rows. If  this method is used for such an 
	 * approach, the caller will end up in using this method multiple times 
	 * for each subsequent pages till the end of results is reached OR pass
	 * null page request to get the entire search results in a single shot. 
	 * The first scenario puts tremendous load on the storage (as several 
	 * hundreds of queries need to be executed) and the second scenario clogs
	 * the memory in the application server where the reporting code executes
	 * by loading every result into memory. For such scenarios, use the 
	 * overloaded method that returns a Stream instead of a list.
	 * 
	 * 
	 * @param criteria the search criteria.
	 * @param pageReq the page request.
	 * @param retType
	 * @return
	 */
	public <R> SearchResult<R> find(
				SearchCriteria criteria, 
				PageRequest pageReq, 
				Class<? extends R> retType);
	
	/**
	 * Find a list of documents based on the search criteria and return it
  	 * as a stream. This can be used for scenarios like generating huge 
	 * reports. This method returns a Stream of search result objects. The 
	 * underlying implementation should use mechanisms like Scrollable, 
	 * Forward only result sets to establish a single connection with the DB
	 * and stream the entire results to the client through the same connection.
	 * This will help the client to avoid hitting the DB multiple times AND
	 * will eliminate loading the entire results in the app server in a single
	 * shot, thereby making it suitable for use cases like generating huge 
	 * reports.
	 * 
	 * Since the method returns a stream that needs to maintain an underlying
	 * DB connection, we should wrap the caller code in a readonly transaction
	 * using the spring provided @Transaction annotation. 
	 * 
	 * @param criteria
	 * @param retType
	 * @return
	 */
	public <R> Stream<R> find(
			SearchCriteria criteria,  
			Class<? extends R> retType);
	
}
