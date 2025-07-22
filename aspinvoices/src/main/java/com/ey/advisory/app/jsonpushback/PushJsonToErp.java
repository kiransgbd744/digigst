/**
 * 
 */
package com.ey.advisory.app.jsonpushback;

import com.ey.advisory.app.docs.dto.erp.Anx1ErrorDocsRevIntegrationReqDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface PushJsonToErp {
	public void pushErrorRecordJson(Long batchId, String gstin);

	public Integer erpErrorDocsToErp(Anx1ErrorDocsRevIntegrationReqDto dto);

	public void pushErrorRecordJson(Long minId, Long maxId, String gstin);
}
