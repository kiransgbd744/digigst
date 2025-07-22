/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr8;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface Gstr8InvoicesAtGstn {

	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto, String groupCode,
			String type, Long batchId);

}
