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
@Component("ITC04SavedSubmittedHandler")
public class ITC04SavedSubmittedHandler {

	@Autowired
	@Qualifier("ITC04SavedSubmittedServiceImpl")
	private ITC04SavedSubmittedServiceImpl iTC04SavedSubmittedService;

	public Workbook downloadsavedsubmitted(
			Gstr6SummaryRequestDto setDataSecurity) {
		return iTC04SavedSubmittedService.findsavedsubmitted(setDataSecurity,
				null);

	}

}
