/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.util.Set;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1DeInvoicesHeaderEntity;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx1DeDataParser {

	public Set<GetAnx1DeInvoicesHeaderEntity> parseDeData(
			Anx1GetInvoicesReqDto dto, String apiResp);
}