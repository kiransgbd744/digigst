package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1B2clHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2claHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2clHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2claHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Umesha.M
 *
 */
public interface Gstr1AB2clB2claDataParser {

	/*
	 * public List<GetGstr1B2CLInvoicesEntity> parseB2CLData(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1AB2clHeaderEntity> parseB2clData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1AB2claHeaderEntity> parseB2claData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
