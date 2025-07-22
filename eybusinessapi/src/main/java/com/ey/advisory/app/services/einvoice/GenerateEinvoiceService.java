package com.ey.advisory.app.services.einvoice;

import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;

public interface GenerateEinvoiceService {
	
	public GenerateIrnResponseDto generateEinvRequest(OutwardTransDocument hdr,boolean isVersionEnabled);

}
