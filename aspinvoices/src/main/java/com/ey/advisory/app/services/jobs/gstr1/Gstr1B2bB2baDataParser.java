package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1B2bHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2baHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Gstr1B2bB2baDataParser {

	/*
	 * public List<GetGstr1B2bInvoicesEntity> parseB2bB2baData(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1B2bHeaderEntity> parseB2bData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1B2baHeaderEntity> parseB2baData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
