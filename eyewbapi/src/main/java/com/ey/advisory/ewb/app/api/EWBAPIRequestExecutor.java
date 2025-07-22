package com.ey.advisory.ewb.app.api;

import org.apache.http.client.methods.HttpUriRequest;
import org.javatuples.Pair;

public interface EWBAPIRequestExecutor {
	
	public Pair<String, Integer> execute(HttpUriRequest req) throws Exception; 

}
