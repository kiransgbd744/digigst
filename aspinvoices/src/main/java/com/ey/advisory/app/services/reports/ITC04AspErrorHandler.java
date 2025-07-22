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
	
	@Component("ITC04AspErrorHandler")
	public class ITC04AspErrorHandler {

		@Autowired
		@Qualifier("ITC04AspErrorServiceImpl")
		private ITC04AspErrorServiceImpl iTC04AspErrorService;

		public Workbook downloadAspError(
				Gstr6SummaryRequestDto setDataSecurity) {
			return iTC04AspErrorService.findAspError(setDataSecurity,
					null);

		}

	}

