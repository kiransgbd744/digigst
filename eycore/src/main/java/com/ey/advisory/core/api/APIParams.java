package com.ey.advisory.core.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.AppException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIParams implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(APIParams.class);
	
	/**
	 * This represents the group for which the API is executed. This might
	 * be required if a separate app (like GSP) is responsible for validating
	 * the API requests using a group sepecific API key and API secret. Can
	 * be null if the API channel doesn't involve another app like GSP.
	 */
	@Expose
	@SerializedName("grp")
	protected String groupCode;
	
	/**
	 * This is a unique code given for each API to be executed. Note that it
	 * might not be the 'ACTION' parameter mentioned in the API specifications.
	 * This is because, the same 'ACTION' parameter can be reused for different
	 * APIs, but this action variable represents a unique string for an API.
	 * A separate constant file will be maintained that contains all possible
	 * actions for the API.
	 */
	@Expose
	@SerializedName("id")
	protected String apiIdentifier;
	
	/**
	 * This variable represents the version of the API to be executed. There 
	 * might be scenarios where more than one version of the same API can 
	 * exist in production (due to various business reasons). One example 
	 * would be to use a higher version of the API for all tax periods 
	 * after a certain month and the previous version of the API for all
	 * prior tax periods. The version of the api is determined by a combination
	 * of configuration values and the input data provided by the caller. 
	 */
	@Expose
	@SerializedName("version")
	protected String apiVersion;
	
	
	/**
	 * Currently, there are 2 supported providers - GSTN and NIC.
	 */
	protected APIProviderEnum apiProvider;
	
	@Expose
	@SerializedName("params")
	protected Map<String, String> apiParamsMap = new HashMap<>();
	
	/**
	 * A copy constructor that copies all values from an input APIParams object,
	 * and overrides only the specified version. This is useful if the client
	 * wants to specify a particular version.
	 * 
	 * @param params the input APIParams object to be copied.
	 * 
	 * @param version the new version to override the earlier one.
	 */
	public APIParams(APIParams params, String version) {
		this.apiIdentifier = params.apiIdentifier;
		this.apiProvider = params.apiProvider;
		this.groupCode = params.groupCode;
		this.apiVersion = version; // The new version passed.
		this.apiParamsMap = params.apiParamsMap;
		
		if (version == null || version.trim().isEmpty()) {
			String msg = "API Version cannot be null/empty. This constructor "
					+ "is used specifically to reset the current "
					+ "API version. Check if API version is configured in "
					+ "the DB";
			LOGGER.error(msg);
			throw new APIException(msg);
		}
	}
	
	@SafeVarargs
	public APIParams(String groupCode, APIProviderEnum apiProvider, 
			String apiIdentifier, APIParam...apiParams) {
		this.apiIdentifier = apiIdentifier;
		this.apiProvider = apiProvider;
		this.groupCode = groupCode;

		// Copy all the input parameters to the param map.
		copyParamsToMap(this.apiParamsMap, apiParams);
	}
	
	@SafeVarargs
	public APIParams(String groupCode, APIProviderEnum apiProvider, 
			String apiIdentifier, String apiVersion, 
			APIParam...apiParams) {
		this.apiIdentifier = apiIdentifier;
		this.apiProvider = apiProvider;
		this.groupCode = groupCode;
		this.apiVersion = (apiVersion != null) ? apiVersion.trim() : null;
		
		copyParamsToMap(this.apiParamsMap, apiParams);
	}
	
	private static void copyParamsToMap(Map<String, 
			String> apiParamsMap,  APIParam[] apiParams) {
		for(APIParam param: apiParams) {
			String paramName = param.getParamName();
			if (param.getParamType() == APIParamType.HEADER) {
				// Add the specified value only for Header
				apiParamsMap.put(paramName + ":H", param.getParamValue());
			} else if (param.getParamType() == APIParamType.URLPARAM) {
				// Add the specified value only for URL param.
				apiParamsMap.put(paramName + ":U", param.getParamValue());
			} else {
				// Add the value for both header and URL param.
				apiParamsMap.put(paramName + ":H", param.getParamValue());
				apiParamsMap.put(paramName + ":U", param.getParamValue());
			}
		}				
	}	
	
	public String getApiIdentifier() {
		return apiIdentifier;
	}
	
	public  boolean isVersionSpecified() {
		return (apiVersion != null) && !apiVersion.isEmpty();
	}
	
	public String getApiVersion() {
		return apiVersion;
	}

	/**
	 * Get the API param value based on whether the param type is header, URL
	 * param or ANY.
	 * 
	 * @param paramName
	 * @param paramType
	 * @return
	 */
	public String getAPIParamValue(String paramName, APIParamType paramType) {
		
		if (paramType == APIParamType.HEADER) {
			return apiParamsMap.get(paramName + ":H");
		}
		
		if (paramType == APIParamType.URLPARAM) {
			return apiParamsMap.get(paramName + ":U");
		}		
		
		// This point in code will be encountered only if the param type is
		// 'ANY'. In that case, first get the Header Value and 
		// then get the URL param value.
		String hdrVal = apiParamsMap.get(paramName + ":H");
		String urlParamVal = apiParamsMap.get(paramName + ":U");
		
		// The following 2 statements will ensure that if one of them is
		// non-null and the other is null, then the non-null value will
		// be returned. Also, if both the values are null, then null 
		// will be returned.
		if (hdrVal == null) return urlParamVal;
		if (urlParamVal == null) return hdrVal;
		
		// If control reaches here, then both are non null. In this case
		// we check if both the values are equal. If so, return the value.
		// Otherwise, throw an exception to ask for the specific 
		// param type.
		
		if (!hdrVal.equals(urlParamVal)) {
			String msg = "There are 2 different values for Header and "
					+ "URL param, for the same paramter name. Hence "
					+ "getApiParamValue should be used with a specific "
					+ "param type";
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		
		// Return the value that is same for header and URL param.
		return hdrVal;		
	}
	
	/**
	 * Overloaded method that delegates the call to getApiParamValue method
	 * with APIParamType.ANY as the paramType parameter.
	 * 
	 * @param paramName
	 * @return
	 */
	public String getAPIParamValue(String paramName) {
		return getAPIParamValue(paramName, APIParamType.ANY);
	}

	public APIProviderEnum getApiProvider() {
		return apiProvider;
	}
	
	public String getGroupCode() {
		return groupCode;
	}

	@Override
	public String toString() {
		return "APIParams [groupCode=" + groupCode + ", apiIdentifier="
				+ apiIdentifier + ", apiVersion=" + apiVersion
				+ ", apiProvider=" + apiProvider + ", apiParamsMap="
				+ apiParamsMap + "]";
	}
	
	public void addApiParam(APIParam apiParam) {
		// Add the value for both header and URL param.
		apiParamsMap.put(apiParam.getKey() + ":H", apiParam.getValue());
		apiParamsMap.put(apiParam.getKey() + ":U", apiParam.getValue());
	}
	
}

