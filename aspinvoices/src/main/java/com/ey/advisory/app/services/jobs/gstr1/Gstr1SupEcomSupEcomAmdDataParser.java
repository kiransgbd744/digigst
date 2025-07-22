package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1SupEcomAmdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1SupEcomHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr1SupEcomSupEcomAmdDataParser {

	public List<GetGstr1SupEcomHeaderEntity> parseSupEcomData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1SupEcomAmdHeaderEntity> parseSupEcomAmdData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
