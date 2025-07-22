package com.ey.advisory.app.azure;

public interface FetchAzureAuthTokensService {
	
	void getAuthTokens(String sapGroupCode, String azureGroupCode);
}
