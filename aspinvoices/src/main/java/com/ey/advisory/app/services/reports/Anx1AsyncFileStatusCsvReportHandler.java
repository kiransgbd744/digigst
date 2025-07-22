/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.search.SearchCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */
@Slf4j
@Component("Anx1AsyncFileStatusCsvReportHandler")
public class Anx1AsyncFileStatusCsvReportHandler {

	@Autowired
	@Qualifier("Anx1AsyncFileStatusCsvReportsServiceImpl")
	private Anx1AsyncFileStatusCsvReportsService anx1AsyncFileStatusCsvReportsService;

	public void generateCsvForFileStatus(SearchCriteria criteria,
			String fullPath, Long reqId) {

		anx1AsyncFileStatusCsvReportsService.generateFileStatusCsv(criteria,
				reqId, fullPath);

	}

}
