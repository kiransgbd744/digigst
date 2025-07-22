/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr1;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Gstr1InvoicesAtGstn {

	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto, String groupCode, String type, Long batchId);

}
