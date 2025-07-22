/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Anx1SummaryAtGstn {

	public String getAnx1Summary(Anx1GetInvoicesReqDto dto,
			String groupCode);
	
	public Boolean generateAnx1Summary(Anx1GetInvoicesReqDto dto,
			String groupCode);
	
	public void saveJsonAsRecords(String apiResp, String groupCode,
			Anx1GetInvoicesReqDto dto, GetAnx1BatchEntity batch);

}
