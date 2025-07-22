package com.ey.advisory.ewb.api;

import java.time.LocalDate;

import com.ey.advisory.ewb.app.api.APIResponse;

public interface GetOtherPartyEWB {
	
	public APIResponse getCounterPartyEwb(String gstin, LocalDate date);	

}
