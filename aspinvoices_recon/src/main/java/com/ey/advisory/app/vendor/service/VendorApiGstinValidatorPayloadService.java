/**
 * 
 */
package com.ey.advisory.app.vendor.service;

/**
 * @author vishal.verma
 *
 */
public interface VendorApiGstinValidatorPayloadService {
	
	//payloadId, companyCode, gstinCount,0, genCheckSum,customerCode,category,APIConstants.DUP
	public void create(String payloadId, String companyCode, String gstinCount, String pushType, String genCheckSum,
			String category, String apiStatus, String checkSum, String jsonString);

	public void update(String payloadId, String status, Integer errorCount,
			Integer totalCount);

	public void updateError(String payloadId, String status, String errorCode,
			String jsonErrorResp);

	public void updateErrorExc(String payloadId, String status,
			String errorCode, String jsonErrorRespy, Integer pushType);

	public void saveCPIError(String payloadId, String companyCode,
			String docCount, String checkSum, String pushType, String errorCode,
			String jsonErrorResp);
}
