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
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@Component("Anx1FileStatusCsvReportHandler")
public class Anx1FileStatusCsvReportHandler {

	@Autowired
	@Qualifier("Anx1FileStatusCsvReportsServiceImpl")
	private Anx1FileStatusCsvReportsService anx1FileStatusCsvReportsService;

	public void generateCsvForFileStatus(SearchCriteria criteria,
			String fullPath) {

		anx1FileStatusCsvReportsService.generateFileStatusCsv(criteria, null,
				fullPath);

	}

}
