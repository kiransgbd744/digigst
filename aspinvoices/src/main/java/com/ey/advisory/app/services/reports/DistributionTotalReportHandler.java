/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("DistributionTotalReportHandler")
public class DistributionTotalReportHandler {
	@Autowired
	@Qualifier("DistributionTotalReportsServiceImpl")
	private DistributionReportsService distributionReportsService;

	public void generateCsvForFileStatus(SearchCriteria criteria,
			String fullPath) {

		distributionReportsService.generateFileStatusCsv(criteria, null,
				fullPath);

	}

}
