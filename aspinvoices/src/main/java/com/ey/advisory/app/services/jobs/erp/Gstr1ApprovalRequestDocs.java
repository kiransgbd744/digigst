package com.ey.advisory.app.services.jobs.erp;

import com.ey.advisory.app.docs.dto.erp.Gstr1ApprovalRequestHeaderDto;

public interface Gstr1ApprovalRequestDocs {

	public Gstr1ApprovalRequestHeaderDto convertDocsAsHeaderDtos(String retPer,
			Long gstinId,String gstinName, String entityName, String companyCode);

	
}
