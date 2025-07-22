package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1EcomSupAmdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EcomSupHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr1EcomEcomAmdDataParser {

	public List<GetGstr1EcomSupHeaderEntity> parseEcomData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1EcomSupAmdHeaderEntity> parseEcomAmdData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
