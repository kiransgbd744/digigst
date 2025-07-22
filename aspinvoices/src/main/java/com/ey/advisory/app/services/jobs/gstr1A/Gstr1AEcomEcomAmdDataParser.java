package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1EcomSupAmdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EcomSupHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AEcomSupAmdHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AEcomSupHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr1AEcomEcomAmdDataParser {

	public List<GetGstr1AEcomSupHeaderEntity> parseEcomData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1AEcomSupAmdHeaderEntity> parseEcomAmdData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
