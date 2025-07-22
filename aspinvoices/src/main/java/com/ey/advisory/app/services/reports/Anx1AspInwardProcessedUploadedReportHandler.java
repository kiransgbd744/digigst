package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

@Component("Anx1AspInwardProcessedUploadedReportHandler")
public class Anx1AspInwardProcessedUploadedReportHandler {
	@Autowired
	@Qualifier("Anx1AspInwardProcessedUploadedServiceImpl")
	private Anx1AspInwardProcessedUploadedService aspInwardProcessedUploadedService;

	public Workbook findInwardProcessedUploaded(
			Gstr1ReviwSummReportsReqDto criteria) {

		return aspInwardProcessedUploadedService
				.findInwardProcessedUploaded(criteria, null);
	}
}
