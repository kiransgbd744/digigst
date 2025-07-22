package com.ey.advisory.app.services.jobs.gstr1;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
//@FunctionalInterface
public interface Gstr1SummaryAtGstn {

	public String getGstr1Summary(Gstr1GetInvoicesReqDto dto, String groupCode);

	public String getGstr1ASummary(Gstr1GetInvoicesReqDto dto, String groupCode);

	public String saveJsonAsRecords(String apiResp, String groupCode,
			Gstr1GetInvoicesReqDto dto, GetAnx1BatchEntity batch, String returnType);
}