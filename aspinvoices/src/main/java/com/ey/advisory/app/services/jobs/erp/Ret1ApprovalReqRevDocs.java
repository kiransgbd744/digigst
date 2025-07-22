package com.ey.advisory.app.services.jobs.erp;

import java.util.List;
import java.util.Map;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.Ret1SummarySectionDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ApprovalRequestDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ApprovalRequestHeaderDto;

public interface Ret1ApprovalReqRevDocs {

	public Ret1ApprovalRequestHeaderDto convertDocsAsHeaderDtos(String retPer,
			Map<Long, String> gstinNums, String entityName, String companyCode);

	public Ret1ApprovalRequestDto convertObjToDtos(String entityName,
			String entityPan, String companyCode,
			List<Ret1SummarySectionDto> ret1BasicDoc);

	public Integer pushToErp(Ret1ApprovalRequestHeaderDto headerDto,
			Ret1ApprovalRequestDto itemDto, String destName, AnxErpBatchEntity batch);

}
