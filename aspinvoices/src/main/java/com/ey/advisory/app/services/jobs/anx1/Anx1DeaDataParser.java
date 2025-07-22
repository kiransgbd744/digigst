package com.ey.advisory.app.services.jobs.anx1;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1DeaInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */

public interface Anx1DeaDataParser {

	public Set<GetAnx1DeaInvoicesHeaderEntity> parseDeaData(
			Anx1GetInvoicesReqDto dto, String apiResp);
}
