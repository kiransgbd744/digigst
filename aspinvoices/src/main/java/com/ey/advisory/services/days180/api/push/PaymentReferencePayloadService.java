/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

/**
 * @author vishal.verma
 *
 */
public interface PaymentReferencePayloadService {

	public void create(String payloadId, String companyCode, String docCount,
			String checkSum, String pushType, String genCheckSum,
			String sourceId, String apiStatus,String jsonString);

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
