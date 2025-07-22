/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1EcomInvoicesEntity;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx1EcomDataParser {

	public Set<GetAnx1EcomInvoicesEntity> parseEcomData(
			Anx1GetInvoicesReqDto dto, String apiResp);
}
