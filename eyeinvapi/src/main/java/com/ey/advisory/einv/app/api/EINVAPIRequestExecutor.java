package com.ey.advisory.einv.app.api;

import org.apache.http.client.methods.HttpUriRequest;
import org.javatuples.Pair;

public interface EINVAPIRequestExecutor {
	
	public Pair<String, Integer> execute(HttpUriRequest req) throws Exception; 

}
