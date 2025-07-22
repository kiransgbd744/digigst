package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1SupEcomAmdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1SupEcomHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ASupEcomAmdHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ASupEcomHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr1ASupEcomSupEcomAmdDataParser {

	public List<GetGstr1ASupEcomHeaderEntity> parseSupEcomData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1ASupEcomAmdHeaderEntity> parseSupEcomAmdData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
