package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2csHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2csaHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Umesha.M
 *
 */
public interface Gstr1AB2csB2csaDataParser {

	/*
	 * public List<GetGstr1B2CSInvoicesEntity> parseB2CSData(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1AB2csHeaderEntity> parseB2csData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1AB2csaHeaderEntity> parseB2csaData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
