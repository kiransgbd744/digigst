package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1CdnurHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1CdnuraHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr1CdnurCdnuraDataParser {

	/*
	 * public List<GetGstr1CDNURInvoicesEntity> parseCdnurCdnuraData(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1CdnurHeaderEntity> parseCdnurData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1CdnuraHeaderEntity> parseCdnuraData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
