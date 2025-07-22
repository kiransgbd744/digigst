package com.ey.advisory.app.services.reports;

	import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

	@Component("Anx1InwardProcessedInfoReportHandler")
	public class Anx1InwardProcessedInfoReportHandler {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(Anx1InwardProcessedInfoReportHandler.class);

		@Autowired
		@Qualifier("Anx1InwardProcessedInfoReportsServiceImpl")
		private Anx1InwardProcessedInfoReportsService anx1InwardProcessedInfoReportsService;

		public Workbook downloadProcessedInfoReport(Anx1FileStatusReportsReqDto criteria) {

			return anx1InwardProcessedInfoReportsService.findInwardApiProcesseInfoRec(criteria, null);

		}

	}



