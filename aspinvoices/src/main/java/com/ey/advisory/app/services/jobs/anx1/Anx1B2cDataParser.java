/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1B2cInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx1B2cDataParser {

	public Set<GetAnx1B2cInvoicesHeaderEntity> parseB2cData(
			Anx1GetInvoicesReqDto dto, String apiResp);
}
