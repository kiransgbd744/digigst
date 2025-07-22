/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import com.ey.advisory.app.docs.dto.erp.Anx1ReviewSummaryRequestDto;

/**
 * @author Umesha M
 *
 */
public interface Anx1ReviewSummaryRequestDocs {

	public Anx1ReviewSummaryRequestDto convertDocsAsDtos(String gstin,
			String entityName, String entityPan, String companyCode,String returnPeriod);

	/*public Integer pushToErp(Anx1ReviewSummaryRequestDto dto,
			String destination, AnxErpBatchEntity batch);*/

}
