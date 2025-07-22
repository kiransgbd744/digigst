package com.ey.advisory.app.services.jobs.erp;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.erp.Gstr6ApprovalRequestDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6ApprovalRequestHeaderDto;

public interface Gstr6ApprovalReqRevIntService {

	public Integer pushToERP(final Gstr6ApprovalRequestHeaderDto headerDto,
			Gstr6ApprovalRequestDto reqDto, AnxErpBatchEntity batch,
			String destinationName);
}
