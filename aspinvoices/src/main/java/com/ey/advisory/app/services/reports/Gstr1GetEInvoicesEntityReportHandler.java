package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1EInvReportsReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr1GetEInvoicesEntityReportHandler")
public class Gstr1GetEInvoicesEntityReportHandler {

	@Autowired
	@Qualifier("Gstr1GetEInvoicesEntityServiceImpl")
	private Gstr1GetEInvoicesProcessedReportsService gstr1GetEInvoicesProcessedReportsService;

	public Workbook downloadEntityReport(Gstr1EInvReportsReqDto criteria) {

		return gstr1GetEInvoicesProcessedReportsService
				.findGstr1GetEInvoicesRecords(criteria, null);

	}

}
