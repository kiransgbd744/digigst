package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1CdnrHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1CdnraHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr1CdnrCdnraDataParser {

	/*
	 * public List<GetGstr1CDNRInvoicesEntity> parseCdrnCdrnaData(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1CdnrHeaderEntity> parseCdnrData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1CdnraHeaderEntity> parseCdnraData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
