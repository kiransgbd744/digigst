package com.ey.advisory.app.data.services.einvoice;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;

public interface GenerateEinvoiceService {
	
	public GenerateIrnResponseDto generateEinvRequest(OutwardTransDocument hdr);

}
