/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1B2bInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx1B2bDataParser {

	public Set<GetAnx1B2bInvoicesHeaderEntity> parseB2bData(
			Anx1GetInvoicesReqDto dto, String apiResp);
}
