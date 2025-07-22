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

@Component("ITC04AspProcessAsUploadedHandler")
public class ITC04AspProcessAsUploadedHandler {

	@Autowired
	@Qualifier("ITC04AspProcessAsUploadedServiceImpl")
	private ITC04AspProcessAsUploadedServiceImpl iTC04AspProcessAsUploadedService;

	public Workbook downloadAspProcessAsUploaded(
			Gstr6SummaryRequestDto setDataSecurity) {
		return iTC04AspProcessAsUploadedService.findAspUploaded(setDataSecurity,
				null);

	}

}
