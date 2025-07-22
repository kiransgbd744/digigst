/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Anx1SummaryDataAtGstn {

	String getAnx1Summary(Anx1GetInvoicesReqDto dto, String groupCode);

	String generateAnx1Summary(Anx1GetInvoicesReqDto dto, String groupCode, String data);

}
