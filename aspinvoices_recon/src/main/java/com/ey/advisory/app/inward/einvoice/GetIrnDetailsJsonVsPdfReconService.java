
package com.ey.advisory.app.inward.einvoice;

public interface GetIrnDetailsJsonVsPdfReconService {

	public JsonValidatorRespDto getIrnforRecon(String irn, String groupCode, String recipientGstin);

}
