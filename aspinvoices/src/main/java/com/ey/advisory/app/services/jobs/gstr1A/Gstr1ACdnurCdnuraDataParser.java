package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnurHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnuraHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr1ACdnurCdnuraDataParser {

	/*
	 * public List<GetGstr1CDNURInvoicesEntity> parseCdnurCdnuraData(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1ACdnurHeaderEntity> parseCdnurData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1ACdnuraHeaderEntity> parseCdnuraData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
