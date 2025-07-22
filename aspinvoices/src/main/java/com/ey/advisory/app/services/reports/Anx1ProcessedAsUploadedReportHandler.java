package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

@Component("Anx1ProcessedAsUploadedReportHandler")
public class Anx1ProcessedAsUploadedReportHandler {
	@Autowired
	@Qualifier("Anx1AspProcessedUploadedServiceImpl")
	private Anx1AspProcessedUploadedService anx1AspProcessedUploadedService;

	public Workbook findProcessedUploaded(
			Gstr1ReviwSummReportsReqDto criteria) {

		return anx1AspProcessedUploadedService.findProcessedUploaded(criteria,
				null);

	}

}
