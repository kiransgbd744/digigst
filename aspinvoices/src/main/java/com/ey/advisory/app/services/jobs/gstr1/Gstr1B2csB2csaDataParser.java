package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1B2csHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2csaHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Umesha.M
 *
 */
public interface Gstr1B2csB2csaDataParser {

	/*
	 * public List<GetGstr1B2CSInvoicesEntity> parseB2CSData(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1B2csHeaderEntity> parseB2csData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1B2csaHeaderEntity> parseB2csaData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
