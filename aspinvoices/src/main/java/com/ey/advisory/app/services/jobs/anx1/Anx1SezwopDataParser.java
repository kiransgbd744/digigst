/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1SezwopInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Mahesh.Golla
 *
 */
public interface Anx1SezwopDataParser {

	public Set<GetAnx1SezwopInvoicesHeaderEntity> parseSezwopData(
			Anx1GetInvoicesReqDto dto, String apiResp);
}
