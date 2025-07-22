package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

/**
 * @author Siva.Nandam
 *
 */
@Component("Anx1InwardErrorReportHandler")
public class Anx1InwardErrorReportHandler {

	@Autowired
	@Qualifier("Anx1InwardErrorReportsServiceImpl")
	private Anx1InwardErrorReportsServiceImpl anx1ErrorReportsService;

	public Workbook downloadErrorReport(Anx1FileStatusReportsReqDto criteria) {

		return anx1ErrorReportsService.findError(criteria, null);

	}

}

