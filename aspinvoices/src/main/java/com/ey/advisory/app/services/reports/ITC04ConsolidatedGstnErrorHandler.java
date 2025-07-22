/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("ITC04ConsolidatedGstnErrorHandler")
public class ITC04ConsolidatedGstnErrorHandler {

	@Autowired
	@Qualifier("ITC04ConsolidatedGstnErrorServiceImpl")
	private ITC04ConsolidatedGstnErrorServiceImpl iTC04ConsolidatedGstnErrorService;

	public Workbook downloadconsolidatedgstn(
			Gstr6SummaryRequestDto setDataSecurity) {
		return iTC04ConsolidatedGstnErrorService.findgstnerror(setDataSecurity,
				null);

	}

}
