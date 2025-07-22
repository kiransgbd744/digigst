package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

/**
 * @author Sasidhar
 *
 * 
 */

@Component("Anx1AspSavableProcessedReportHandler")
public class Anx1AspSavableProcessedReportHandler {

	@Autowired
	@Qualifier("Anx1ASPSavableHsnAndLineServiceImpl")
	private Anx1AspSavableUploadedService aspSavableUploadedService;

	public Workbook findSavableUploaded(Gstr1ReviwSummReportsReqDto criteria) {

		return aspSavableUploadedService.findSavableUploaded(criteria, null);
	}

}
