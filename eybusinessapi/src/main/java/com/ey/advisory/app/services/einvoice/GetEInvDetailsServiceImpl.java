package com.ey.advisory.app.services.einvoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenerateIrnResponseUtil;
import com.ey.advisory.einv.api.GetEInvDetails;
import com.ey.advisory.einv.api.GetEInvDtlsByDocDtls;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;

/**
 * @author Siva Reddy
 *
 */
@Component("GetEInvDetailsServiceImpl")
public class GetEInvDetailsServiceImpl implements GetEInvDetailsService {

	@Autowired
	@Qualifier("GetEInvDetailsImpl")
	private GetEInvDetails getEInvDetails;

	@Autowired
	@Qualifier("GetEInvDtlsByDocDtlsImpl")
	private GetEInvDtlsByDocDtls getEInvDtlsByDocDtls;

	@Override
	public GenerateIrnResponseDto getEInvDetails(String irnNo, String gstin, String source) {
		APIResponse response = getEInvDetails.getEInvDetails(irnNo, gstin, source);
		return GenerateIrnResponseUtil.createGenerateIrnResponse(response);
	}

	@Override
	public GenerateIrnResponseDto getEInvDtlsByDocDtls(String docNum,
			String docType, String docDate,String gstin) {
		APIResponse response = getEInvDtlsByDocDtls
				.getEInvDtlsByDocDetails(docNum, docType, docDate,gstin);
		return GenerateIrnResponseUtil.createGenerateIrnResponse(response);

	}

}
