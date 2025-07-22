/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1RevInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx1RevDataParser {

	public Set<GetAnx1RevInvoicesHeaderEntity> parseRevData(
			Anx1GetInvoicesReqDto dto, String apiResp);
}
