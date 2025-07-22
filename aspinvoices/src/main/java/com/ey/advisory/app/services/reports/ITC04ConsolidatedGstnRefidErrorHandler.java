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
@Component("ITC04ConsolidatedGstnRefidErrorHandler")
public class ITC04ConsolidatedGstnRefidErrorHandler {

	@Autowired
	@Qualifier("ITC04ConsolidatedGstnRefidServiceImpl")
	private ITC04ConsolidatedGstnRefidServiceImpl iTC04ConsolidatedGstnRefidService;

	public Workbook downloadRefidgstn(
			Gstr6SummaryRequestDto setDataSecurity) {
		return iTC04ConsolidatedGstnRefidService.findrefid(setDataSecurity,
				null);

	}

}
