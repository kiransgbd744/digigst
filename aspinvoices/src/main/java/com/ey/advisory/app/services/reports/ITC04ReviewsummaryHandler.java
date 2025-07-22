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
 */
@Component("ITC04ReviewsummaryHandler")
public class ITC04ReviewsummaryHandler {
	
	@Autowired
	@Qualifier("ITC0404ReviewsummaryImpl")
	private ITC0404ReviewsummaryImpl iTC0404ReviewsummaryImpl;

	public Workbook downloadReviewSummary(
			Gstr6SummaryRequestDto setDataSecurity) {
		return iTC0404ReviewsummaryImpl.findReviewSummary(setDataSecurity,
				null);

	}

}

