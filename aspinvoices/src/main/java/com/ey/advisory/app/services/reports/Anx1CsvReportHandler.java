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
@Component("Anx1CsvReportHandler")
public class Anx1CsvReportHandler {

	@Autowired
	@Qualifier("Anx1CsvReportsServiceImpl")
	private Anx1CsvReportsService anx1CsvReportsService;

	public void generateCsvForCriteira(SearchCriteria criteria,
			String fullPath) {

		anx1CsvReportsService.generateCsvForCriteira(criteria, null, fullPath);

	}

}
