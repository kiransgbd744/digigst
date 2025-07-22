package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.data.domain.Pageable;

import com.ey.advisory.app.data.entities.client.asprecon.AutoReconRequestEntity;

public interface AutoReconReportDownloadService {

	 Pair<Long, Integer>  createEntryAutoReconReportRequest(Long noOfRecipientsGstin,
			Long noOfReportTypes, String reportTypes, String fromTaxPeriod,
			String toTaxPeriod, String reconFromDate, String reconToDate,
			Long entityId);

	void createEntryAutoReconReqRecipientGstin(Long requestId,
			String recipientGstin);

	Pair<List<AutoReconRequestEntity>, Integer> getAutoReconReqData(
			Long entityId, Pageable pageReq);

	List<AutoReconReqListingDto> getAutoReconReqResponse(
			List<AutoReconRequestEntity> autoReconReqList);
}
