/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.services.Gstr1A.Gstr1AAGstnErrorReportService;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 */

@Slf4j
@Component("Gstr1aGetRateReportHandler")
public class Gstr1aGetRateReportHandler {

	@Autowired
	@Qualifier("Gstr1aRateReportServiceImpl")
	private Gstr1AGstnErrorReportService gstr1AGstnErrorReportService;

	@Autowired
	@Qualifier("Gstr1AaRateReportServiceImpl")
	private Gstr1AAGstnErrorReportService gstr1AAGstnErrorReportService;

	public Workbook findRate(List<GstnConsolidatedReqDto> criteria,
			String returnType) {

		if ("GSTR1A".equalsIgnoreCase(returnType))

		{
			return gstr1AAGstnErrorReportService.findGstnErrorReports(criteria,
					null);

		}

		else {
			return gstr1AGstnErrorReportService.findGstnErrorReports(criteria,
					null);

		}

	}

}
