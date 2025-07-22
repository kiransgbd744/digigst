package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1CdnrHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1CdnraHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnrHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnraHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr1ACdnrCdnraDataParser {

	/*
	 * public List<GetGstr1CDNRInvoicesEntity> parseCdrnCdrnaData(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1ACdnrHeaderEntity> parseCdnrData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1ACdnraHeaderEntity> parseCdnraData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
