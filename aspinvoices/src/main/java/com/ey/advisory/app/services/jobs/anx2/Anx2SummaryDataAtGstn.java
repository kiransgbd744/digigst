/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;

/**
 * @author Dibyakanta.sahoo
 *
 */
public interface Anx2SummaryDataAtGstn {

	String getAnx2Summary(Anx2GetInvoicesReqDto dto, String groupCode);

	String generateAnx2Summary(Anx2GetInvoicesReqDto dto, String groupCode,
			String data);

}
