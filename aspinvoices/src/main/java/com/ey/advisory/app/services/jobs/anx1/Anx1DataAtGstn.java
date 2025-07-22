/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx1DataAtGstn {

	public String findAnx1DataAtGstn(Anx1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq);
}
