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
 * 
 */

@Slf4j
@Component("Gstr1aGetReportHandler")
public class Gstr1aGetReportHandler {

	@Autowired
	@Qualifier("Gstr1aGetReportServiceImpl")
	private Gstr1AGstnErrorReportService gstr1AGstnErrorReportService;
	
	@Autowired
	@Qualifier("Gstr1AaGetReportServiceImpl")
	private Gstr1AAGstnErrorReportService gstr1AAGstnErrorReportService;

	public Workbook findGstnErrorReports(
			List<GstnConsolidatedReqDto> criteria,String returnType) {
		
		if("GSTR1A".equalsIgnoreCase(returnType))
			
		{
			return gstr1AAGstnErrorReportService.findGstnErrorReports(criteria,
					null);
			
		}
		
		else
		{
		return gstr1AGstnErrorReportService.findGstnErrorReports(criteria,
				null);

	}
		
	}

}