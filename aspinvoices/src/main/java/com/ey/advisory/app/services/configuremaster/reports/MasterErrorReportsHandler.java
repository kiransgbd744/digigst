package com.ey.advisory.app.services.configuremaster.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

@Component("MasterErrorReportsHandler")
public class MasterErrorReportsHandler {

	@Autowired
	@Qualifier("masterErrorReportsService")
	private MasterErrorReportsService masterErrorReportsService;

	public Workbook downloadMasterErrorReports(
	        final Anx1FileStatusReportsReqDto criteria) {
		return masterErrorReportsService.downloadMasterErrorReports(criteria,
		        null);
	}
}
