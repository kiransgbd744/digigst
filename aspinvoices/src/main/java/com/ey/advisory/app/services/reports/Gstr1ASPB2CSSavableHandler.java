
	
	package com.ey.advisory.app.services.reports;

	import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
	
	/**
	 * @author Sujith.Nanga
	 *
	 * 
	 */

	@Component("Gstr1ASPB2CSSavableHandler")
	public class Gstr1ASPB2CSSavableHandler {
		
		@Autowired
		@Qualifier("Gstr1ASPB2CSSavableServiceImpl")
		private Gstr1ASPB2CSSavableService gstr1ASPB2CSSavableService;

		public Workbook getGstr1B2CSSavableReports(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gstr1ASPB2CSSavableService
					.findGstr1B2CSSavableReports(criteria, null);

		}

	}


