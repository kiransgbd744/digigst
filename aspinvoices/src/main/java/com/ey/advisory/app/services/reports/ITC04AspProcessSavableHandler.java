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

@Component("ITC04AspProcessSavableHandler")
public class ITC04AspProcessSavableHandler {
	
	@Autowired
	@Qualifier("ITC04AspProcessSavableServiceImpl")
	private ITC04AspProcessSavableServiceImpl iTC04AspProcessSavableService;

	public Workbook downloadAspProcessSavable(
			Gstr6SummaryRequestDto setDataSecurity) {
		return iTC04AspProcessSavableService.findAspSavable(setDataSecurity,
				null);

	}

}

