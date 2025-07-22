package com.ey.advisory.einv.app.api;

public interface APIEINVChannelAndEndUserBuilder {

	public NICAPIEndUser buildEndUser(APIParams params);
	
	public EYNICChannel buildChannel(APIParams params);
}
