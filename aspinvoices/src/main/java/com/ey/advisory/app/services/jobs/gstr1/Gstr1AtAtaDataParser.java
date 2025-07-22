package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1AtHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1AtaHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Gstr1AtAtaDataParser {

	/*public List<GetGstr1ATEntity> parseAtAtaData(Gstr1GetInvoicesReqDto dto, String apiResp);*/
	public List<GetGstr1AtHeaderEntity> parseAtData(Gstr1GetInvoicesReqDto dto, String apiResp);
	public List<GetGstr1AtaHeaderEntity> parseAtaData(Gstr1GetInvoicesReqDto dto, String apiResp);
}
