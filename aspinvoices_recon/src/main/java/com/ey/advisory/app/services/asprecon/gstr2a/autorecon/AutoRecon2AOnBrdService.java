package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import com.google.gson.JsonObject;

public interface AutoRecon2AOnBrdService {

	String saveOnBoardingDetails(AutoRecon2APROnBoardingReqDto reqDto);

	String saveOnBrdAddParamDetails(AutoRecon2APROnBoardingReqDto reqDto);

	JsonObject getOnBoardingDetails(String entityId);

	JsonObject getOnBoardingAddParamDetails(String entityId);

}
