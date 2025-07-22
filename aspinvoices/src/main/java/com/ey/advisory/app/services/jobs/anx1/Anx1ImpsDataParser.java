/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.
                                            GetAnx1ImpsInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Mahesh.Golla
 *
 */
public interface Anx1ImpsDataParser {

	public Set<GetAnx1ImpsInvoicesHeaderEntity> parseImpsData(
			Anx1GetInvoicesReqDto dto, String apiResp);
}
