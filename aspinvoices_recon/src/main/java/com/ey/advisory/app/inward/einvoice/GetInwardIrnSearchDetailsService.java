package com.ey.advisory.app.inward.einvoice;

public interface GetInwardIrnSearchDetailsService {

	public IrnSearchDetailsDto getIrnDtls(String irn, String groupCode, String activeGstins);
	
	
//Gstr1GetInvoicesReqDto dto
}
