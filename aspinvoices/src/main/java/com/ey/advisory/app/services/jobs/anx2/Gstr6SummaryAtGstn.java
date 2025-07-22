/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Gstr6SummaryAtGstn {

	public String getGstr6Summary(Anx2GetInvoicesReqDto dto,
			String groupCode);
	
	public Boolean generateGstr6Summary(Anx2GetInvoicesReqDto dto,
			String groupCode);
	
	public void saveJsonAsRecords(String apiResp, String groupCode,
			Anx2GetInvoicesReqDto dto, GetAnx1BatchEntity batch);
	
}
