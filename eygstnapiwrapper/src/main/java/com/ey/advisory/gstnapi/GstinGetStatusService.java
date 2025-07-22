package com.ey.advisory.gstnapi;

public interface  GstinGetStatusService {

	public void saveOrUpdateGSTNGetStatus(String jsonString, String status, String errDesc);

	public void uploadJsonFileToRepoForGSTN(SuccessResult result, String apiParams);


}
