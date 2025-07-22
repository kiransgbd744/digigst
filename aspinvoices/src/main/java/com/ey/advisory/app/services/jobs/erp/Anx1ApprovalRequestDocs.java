/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import com.ey.advisory.app.docs.dto.erp.Anx1ApprovalRequestHeaderDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx1ApprovalRequestDocs {

	public Anx1ApprovalRequestHeaderDto convertDocsAsHeaderDtos(String retPer,
			String gstinNum, String entityName, String pan, String companyCode);

	/*public Integer pushToErp(Anx1ApprovalRequestHeaderDto headerDto,
			Anx1ReviewSummaryRequestDto reqDto, String destination,
			AnxErpBatchEntity batch);*/

	/*
	 * public List<List<Object[]>> splitDataIntoGstinAndRetPeriodWise(
	 * List<Object[]> objs);
	 */

}
