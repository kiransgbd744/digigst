package com.ey.advisory.gstnapi;

import com.ey.advisory.core.api.APIParams;

/**
 * This is the core interface of the eygstnapiwrapper project. The 
 * implementation of this interface is responsible to asynchronously execute
 * the specified API, store the result of execution of the API, perform any 
 * other intermediate tracking/logging etc and in the end invoke either the
 * success or the failure handler provided by the caller of this interface. 
 * For synchronous execution of an API, use the {@link APIExecutor} interface
 * from the eycore project. Implementations of this interface may use
 * this synchronous execution mechanism internally to actually get the API 
 * executed. Developers who want to execute an API can either use the 
 * {@link APIExecutor} interface from the eycore project to synchronously 
 * execute the API OR use this interface to exeucte the API asynchronously. 
 *
 * One of the key benefits of favoring this interface to the APIExecutor 
 * interface is that retries during any failure is internally handled by the
 * implementation of this API. The caller of this API needs to decide only the
 * actions to take on success and on failure of the API. The retry mechanism can
 * be completely shielded from the caller of this API. The implementation can
 * even maintain a policy based retry mechanism that can be configured on a per
 * API basis, thereby making the retry policies for an API highly flexible
 * based on the actual observations of the API execution in the production 
 * environment. The number of retries that needs to be  performed before the
 * API invocation is considered as a failure and the {@link FailureHandler} is
 * invoked can also be configured as part of the retry policy.
 * 
 * Another benefit of using this API is to execute APIs that return a GSTN token 
 * response. Token responses are inherently asynchronous in nature. Shielding 
 * the caller of the API from the handling of the complexities of GSTN token 
 * based API is one of the core design principles of this API. Basically, 
 * GSTN token based API is an API invocation pattern where the invocation of
 * the API specified by the caller (in the APIParams) does not return the final 
 * JSON; but instead returns an intermediate result that requires further 
 * asynchronous processing  (like invoking one or more separate APIs). 
 * Currently, the token based API invocation is the only such scenario known
 * to us. Any APIs that we encounter in the future that uses a similar 
 * mechanism has to use this framework. The details of GSTN token processing
 * can be found in the {@link TokenResponseProcessor} class.
 * 
 * The third benefit of using this API is that the implementation of this API
 * can store the result of execution of each API, which can be later located 
 * using the transaction id provided in the {@link APIInvoicationResult}. The
 * duration for which the result of execution of an API can be stored can be 
 * decided using configurable policies.
 * 
 * The scenarios where a GSTN API has to be invoked synchronously, in order to
 * populate a UI, cannot be achieved by using this interface. In such scenarios,
 * the developer should rely on the APIExecutor interface of the eycore project.
 * 
 * @author Khalid1.Khan
 *
 */
@FunctionalInterface
public interface APIInvoker {

	/**
	 * Invokes the specified api with the specified parameters (and the request
	 * data if it is a post request). In the end, when the API succeeds or 
	 * fails, it invokes either the success or failure handler.
	 * 
	 * This method merely registers the APIExecution request, generate a unique
	 * id for the request and returns the {@link APIInvocationResult} containing 
	 * the unique Id. This id can ignored by the caller or noted down for 
	 * monitoring of the asynchronous execution of the specified API. 
	 * The implementation of this method can provide detailed 
	 * tracking/monitoring details of each of the steps/retries involved in 
	 * executing the API. 
	 * 
	 * @param params The APIParams object that contains the API identifier and
	 * 		the individual url/header parameters.
	 * @param reqData The post data. (only in case of HTTP Post)
	 * @param successHandler the spring bean name of the {@link SuccessHandler} 
	 * 		bean that implements the SuccessHandler interface.
	 * @param failureHandler the spring bean name of the {@link FailureHandler} 
	 * 		bean that implements the FailureHandler interface.
	 * 
	 * @param ctxParams the json representation of anything that the caller of
	 * 		this framework needs to pass to this invocation. Do not serialize 
	 * 		huge objects as context params. The purpose of the context params
	 * 		is to save some data that we already have in hand to later use 
	 * 		within the success/failure handlers. If a piece of data can be 
	 * 		recreated using some minimal information like an id, then only the
	 * 		id should be serialized as a JSON.
	 *  
	 * @return The {@link APIInvocationResult} object which contains a unique 
	 * 		Id that represents the acceptance of the asynchronous API execution 
	 * 		request.
	 * 
	 */
	public APIInvocationResult invoke(APIParams params, String reqData,
			String successHandler, String failureHandler, String ctxParams);
	
	/**
	 * This default implementation assumes that both the success and failure 
	 * handler interfaces are defined within the same spring bean.
	 * 
	 * @see #invoke(APIParams, String, String, String)
	 *  
	 * @param params The APIParams object that contains the API identifier and
	 * 		the individual url/header parameters.
	 * @param reqData The post data. (only in case of HTTP Post)
	 * @param handler the name of the spring bean that contains both the 
	 * 		handleSuccess and handleFailure implementations from the 
	 * 		SuccessHandler and FailureHandler interfaces.
	 * @param ctxParams the json representation of anything that the caller of
	 * 		this framework needs to pass to this invocation. Do not serialize 
	 * 		huge objects as context params. The purpose of the context params
	 * 		is to save some data that we already have in hand to later use 
	 * 		within the success/failure handlers. If a piece of data can be 
	 * 		recreated using some minimal information like an id, then only the
	 * 		id should be serialized as a JSON.
	 * 
	 * @return The {@link APIInvocationResult} object which contains a unique 
	 * 		Id that represents the acceptance of the asynchronous API execution 
	 * 		request.
	 */
	public default APIInvocationResult execute(APIParams params, String reqData,
			String handler, String ctxParams) {
		return invoke(params, reqData, handler, handler, ctxParams);
	}

	
	/**
	 * This default implementation can be used for HTTP Get Requests, where the
	 * success and failure interfaces are defined within the same bean.
	 * 
	 * @see #invoke(APIParams, String, String, String)
	 * 
	 * @param params The APIParams object that contains the API identifier and
	 * 		the individual url/header parameters.
	 * @param handler the name of the spring bean that contains both the 
	 * 		handleSuccess and handleFailure implementations from the 
	 * 		SuccessHandler and FailureHandler interfaces.
	 * @param ctxParams the json representation of anything that the caller of
	 * 		this framework needs to pass to this invocation. Do not serialize 
	 * 		huge objects as context params. The purpose of the context params
	 * 		is to save some data that we already have in hand to later use 
	 * 		within the success/failure handlers. If a piece of data can be 
	 * 		recreated using some minimal information like an id, then only the
	 * 		id should be serialized as a JSON.

	 * @return The {@link APIInvocationResult} object which contains a unique 
	 * 		Id that represents the acceptance of the asynchronous API execution 
	 * 		request.
	 */
	public default APIInvocationResult execute(APIParams params,
			String handler, String ctxParams) {
		return invoke(params, null, handler, handler, ctxParams);
	}
	
	/**
	 * This default implementation can be used for HTTP Get Requests, where the
	 * success and failure interfaces are defined within the same bean.
	 * 
	 * @see #invoke(APIParams, String, String, String)
	 * 
	 * @param params The APIParams object that contains the API identifier and
	 * 		the individual url/header parameters.
	 * @param handler the name of the spring bean that contains both the 
	 * 		handleSuccess and handleFailure implementations from the 
	 * 		SuccessHandler and FailureHandler interfaces.
	 * 
	 * @return The {@link APIInvocationResult} object which contains a unique 
	 * 		Id that represents the acceptance of the asynchronous API execution 
	 * 		request.
	 */	
	public default APIInvocationResult execute(APIParams params,
			String handler) {
		return invoke(params, null, handler, handler, null);
	}

}
