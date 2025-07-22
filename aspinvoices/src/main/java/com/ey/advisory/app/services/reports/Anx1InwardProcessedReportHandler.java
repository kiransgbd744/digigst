package com.ey.advisory.app.services.reports;

	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.beans.factory.annotation.Qualifier;
	import org.springframework.stereotype.Component;

	import com.aspose.cells.Workbook;
	import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

	@Component("Anx1InwardProcessedReportHandler")
	public class Anx1InwardProcessedReportHandler {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(Anx1InwardProcessedReportHandler.class);

		@Autowired
		@Qualifier("Anx1InwardProcessedReportsServiceImpl")
		private Anx1InwardProcessedReportsService anx1InwardProcessedReportsService;

		public Workbook downloadProcessedReport(Anx1FileStatusReportsReqDto criteria) {

			return anx1InwardProcessedReportsService.findInwardApiProcesseRec(criteria, null);

		}

	}



