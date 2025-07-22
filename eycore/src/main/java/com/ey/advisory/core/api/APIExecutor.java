package com.ey.advisory.core.api;

/**
 * This is the core interface for interacting with external REST APIs from our 
 * application. The core method of this interface is the execute method, that
 * takes an APIParams and the request data as as argument and returns an 
 * APIResponse object. The APIPramas object contains the list of headers/
 * url parameters to be passed to execute the API and the reqData contains the
 * JSON payload for the API.
 * 
 * @author Sai.Pakanati
 *
 */
public interface APIExecutor {
	
	/**
	 * This method is responsible for executing the API and returning the 
	 * response by performing the following activities using the input
	 * APIParams and reqData:
	 * 
	 * a.) Load the necessary API configuration required to execute the API.
	 * b.) Optionally, perform necessary validations on the APIParam objects
	 * 	   in the APIParams parameter.
	 * c.) If all the required params are present and valid, then load any 
	 * 	   additional params required for framing the URL params and the HTTP
	 *     headers of the request.
	 * d.) Frame the URL params and the HTTP request headers.
	 * e.) Perform the necessary encryption and encoding on the URL params, 
	 * 	   header params and the Request body JSON (represented by reqData).
	 * f.) Frame the HttpRequest using any of the Http client libraries 
	 * 	   available
	 * g.) Execute the HTTP request and get the HTTP response.
	 * h.) Parse the HTTP response, perform necessary decryption and extract
	 *     the relevant data.
	 * i.) Create the APIResponse instance with either error information or 
	 * 	   the actual Response information and return to the APIResponse to
	 *     the client.
	 * 
	 * @param params the APIParams object that has all information required
	 * 	to load the necessary API Configuration information, load & frame the 
	 * 	necessary HTTP request header values and URL params
	 * 
	 * @param reqData The un-encrypted JSON value to be sent as the HTTP 
	 * 	request body.
	 * 
	 * @return the APIResponse instance, which contains either the error
	 * 	information OR the actual response.
	 * 
	 */
	public APIResponse execute(APIParams params, String reqData);

}
