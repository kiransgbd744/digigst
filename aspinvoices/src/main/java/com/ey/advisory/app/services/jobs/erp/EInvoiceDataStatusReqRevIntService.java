package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.erp.EInvoiceDataStatusRequestDataHeaderDto;
import com.ey.advisory.app.docs.dto.erp.EInvoiceDataStatusRequestDataSummaryDto;
import com.ey.advisory.app.docs.dto.erp.EInvoiceDataStatusRequestDataSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.EinvoiceDataStatusRequestItemDto;

public interface EInvoiceDataStatusReqRevIntService {

	public Integer pushToErp(EInvoiceDataStatusRequestDataHeaderDto headerDto,
			EInvoiceDataStatusRequestDataSummaryDto sumDto, String destName,
			AnxErpBatchEntity batch);

	public List<EinvoiceDataStatusRequestItemDto> getDataStatusProcessData(
			String companyCode, String entityName,Long entityId, String panNumber,
			String dataType, String gstin,List<EinvoiceDataStatusRequestItemDto> itemDtos);

	public void eInvoiceDataStatus(
			String companyCode, String entityName, String panNumber,
			String gstin, String dataType,List<EInvoiceDataStatusRequestDataSummaryItemDto> dataSummItemDtos);
}
