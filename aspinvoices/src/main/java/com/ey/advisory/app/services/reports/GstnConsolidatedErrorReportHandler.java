package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.GstnConsolidatedErrorReqDto;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.core.search.SearchCriteria;

import lombok.extern.slf4j.Slf4j;


	@Slf4j
	@Component("GstnConsolidatedErrorReportHandler")
	public class GstnConsolidatedErrorReportHandler {

		@Autowired
		@Qualifier("GstnConsolidatedErrorReportsServiceImpl")
		private GstnConsolidatedReportsService gstnConsolidatedReportsService;

		public Workbook getConsolidatedReports(
				GstnConsolidatedErrorReqDto criteria) {

			
			return gstnConsolidatedReportsService
					.generateGstnReports(criteria, null);

		}

	}
