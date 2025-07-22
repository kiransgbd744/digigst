/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

/**
 * @author Mahesh.Golla
 *
 */
public interface Itc04PayloadService {

	public void create(String payloadId, String companyCode, String docCount,
			String checkSum, String pushType, String sourceId, String genCheckSum);

	public void update(String payloadId, String status, Integer errorCount,
			String sourceId, Integer totalCount);

	public void updateError(String payloadId, String status, String errorCode,
			String sourceId, String jsonErrorResp);

	public void saveCPIError(String payloadId, String companyCode,
			String docCount, String checkSum, String pushType, String errorCode,
			String sourceId, String jsonErrorResp);
}
