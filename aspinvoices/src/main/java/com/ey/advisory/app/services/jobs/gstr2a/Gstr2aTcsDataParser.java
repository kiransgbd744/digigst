/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr2a;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingTcsInvoicesEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Gstr2aTcsDataParser {

	public List<GetGstr2aStagingTcsInvoicesEntity> parseTcsData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId);
}
