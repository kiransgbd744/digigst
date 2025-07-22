
package com.ey.advisory.app.inward.einvoice;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface InwardGetIrnDetailsDataParser {

	public String parseHeaderAndLineIrnDtls(String signedPayload,
			Gstr1GetInvoicesReqDto dto, GetIrnDtlsRespDto respDto,
			String groupCode, String signedQrCode);

}

