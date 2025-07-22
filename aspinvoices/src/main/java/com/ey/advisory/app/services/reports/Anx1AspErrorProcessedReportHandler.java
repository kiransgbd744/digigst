package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

@Component("Anx1AspErrorProcessedReportHandler")
public class Anx1AspErrorProcessedReportHandler {
	@Autowired
	@Qualifier("Anx1AspErrorUploadedServiceImpl")
	private Anx1AspErrorUploadedService aspErrorUploadedService;

	public Workbook findErrorUploaded(
			Gstr1ReviwSummReportsReqDto criteria) {

		return aspErrorUploadedService
				.findErrorUploaded(criteria, null);
	}
}
