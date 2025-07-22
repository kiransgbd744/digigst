package com.ey.advisory.app.services.jobs.gstr8;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr8UrdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr8UrdaHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public interface Gstr8UrdUrdaDataParser {

	public List<GetGstr8UrdHeaderEntity> parseUrdData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr8UrdaHeaderEntity> parseUrdaData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
