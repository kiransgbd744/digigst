package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1AtHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1AtaHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AAtHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AAtaHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Gstr1AAtAtaDataParser {

	/*public List<GetGstr1ATEntity> parseAtAtaData(Gstr1GetInvoicesReqDto dto, String apiResp);*/
	public List<GetGstr1AAtHeaderEntity> parseAtData(Gstr1GetInvoicesReqDto dto, String apiResp);
	public List<GetGstr1AAtaHeaderEntity> parseAtaData(Gstr1GetInvoicesReqDto dto, String apiResp);
}
