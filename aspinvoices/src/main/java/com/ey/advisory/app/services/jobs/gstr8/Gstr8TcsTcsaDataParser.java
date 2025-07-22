package com.ey.advisory.app.services.jobs.gstr8;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr8TcsHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr8TcsaHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public interface Gstr8TcsTcsaDataParser {

	public List<GetGstr8TcsHeaderEntity> parseTcsData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr8TcsaHeaderEntity> parseTcsaData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
