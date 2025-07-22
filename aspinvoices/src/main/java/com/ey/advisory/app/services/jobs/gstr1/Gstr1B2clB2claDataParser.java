package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1B2clHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2claHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Umesha.M
 *
 */
public interface Gstr1B2clB2claDataParser {

	/*
	 * public List<GetGstr1B2CLInvoicesEntity> parseB2CLData(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1B2clHeaderEntity> parseB2clData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1B2claHeaderEntity> parseB2claData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
