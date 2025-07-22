package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.search.SearchCriteria;


	@Component("GSTR3BErrorReportHandler")
	public class GSTR3BErrorReportHandler {
		@Autowired
		@Qualifier("Gstr3BErrorReportsServiceImpl")
		private GSTR3BErrorReportService gSTR3BErrorReportService;

		public void generateErrorCsv(SearchCriteria criteria,
				String fullPath) {

			gSTR3BErrorReportService.generateFileStatusCsv(criteria, null,
					fullPath);

		}

	}

