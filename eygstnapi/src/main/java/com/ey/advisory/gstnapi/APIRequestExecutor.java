package com.ey.advisory.gstnapi;

import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.javatuples.Pair;

public interface APIRequestExecutor {

	public Pair<String, Integer> execute(HttpUriRequest req,
			Map<String, Object> context) throws Exception;

}
