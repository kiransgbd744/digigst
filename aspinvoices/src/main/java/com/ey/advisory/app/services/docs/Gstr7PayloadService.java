package com.ey.advisory.app.services.docs;

/**
 * @author Mahesh.Golla
 *
 */
public interface Gstr7PayloadService {
	
	public void persistPayload(String cntrlPayloadId, String cntrlChecksum,
			String cntrlCount, String companyCode, String sgstin, String reurnPeriod);	
	
	public void updateError(String cntrlPayloadId, String status, String errorCode,
			String jsonErrorResp);
	
	public void update(String cntrlPayloadId, String status);

}
