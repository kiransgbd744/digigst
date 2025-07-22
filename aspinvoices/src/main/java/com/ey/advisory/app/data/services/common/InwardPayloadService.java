/**
 * 
 */
package com.ey.advisory.app.data.services.common;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface InwardPayloadService {

	public void create(String payloadId, String companyCode, String docCount,
			String checkSum, String pushType, String genChekSum, String sourceId, String status);

	public void update(String payloadId, String status,Integer errorCount, Integer totalCount);

	public void updateError(String payloadId, String status, String errorCode,
			String jsonErrorResp);

	public void saveCPIError(String payloadId, String companyCode,
			String docCount, String checkSum, String pushType, String errorCode,
			String jsonErrorResp);
}
