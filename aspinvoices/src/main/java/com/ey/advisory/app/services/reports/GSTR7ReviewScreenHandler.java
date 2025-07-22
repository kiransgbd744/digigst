/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

/**
 * @author Sujith.Nanga
 *
 */

	@Component("GSTR7ReviewScreenHandler")
	public class GSTR7ReviewScreenHandler {

		@Autowired
		@Qualifier("GSTR7ReviewScreenServiceImpl")
		private GSTR7ReviewScreenService gSTR7ReviewScreenService;

		public Workbook getreviewsumReports(
				Gstr1ReviwSummReportsReqDto criteria) {

			return gSTR7ReviewScreenService
					.findReviewScreenDownload(criteria, null);

		}
	}

