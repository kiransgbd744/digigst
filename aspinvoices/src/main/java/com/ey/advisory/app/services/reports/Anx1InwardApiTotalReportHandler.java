
	package com.ey.advisory.app.services.reports;

	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.beans.factory.annotation.Qualifier;
	import org.springframework.stereotype.Component;

	import com.aspose.cells.Workbook;
	import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.core.search.SearchCriteria;
	
	@Component("Anx1InwardApiTotalReportHandler")
	public class Anx1InwardApiTotalReportHandler {
		
		private static final Logger LOGGER = LoggerFactory
				.getLogger(Anx1InwardApiTotalReportHandler.class);

		@Autowired
		@Qualifier("Anx1InwardApitotalReportsServiceImpl")
		private Anx1InwardApitotalReportsServiceImpl anx1InwardApiTotalReportsService;

		public Workbook downloadInwardApiTotalRecordsReport(
				SearchCriteria criteria) {

			return anx1InwardApiTotalReportsService.findInwardApiTotalRec(criteria,
					null);

		}

	}


