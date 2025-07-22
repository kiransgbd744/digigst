package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.common.Gstr1CommonUtility;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

@Service("Gstr1B2CSASectionReportServiceImpl")
public class Gstr1B2CSASectionReportServiceImpl
		implements Gstr1ReviewSummReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1B2CSASectionReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1ASPB2CSASavableTotalSummaryDaoImpl")
	private Gstr1ASPB2CSSavableTotalDao gstr1ASPB2CSSavableTotalDao;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("Gstr1B2CSSectionReportDaoImpl") private
	 * Gstr1ReviewSummaryReportsDao gstr1ReviewSummaryReportsDao;
	 */
	@Autowired
	@Qualifier("Gstr1ASPB2CSASavableSummaryLevelDaoImpl")
	private Gstr1ASPB2CSSavableSummaryLevelDao gstr1ASPB2CSSavableSummaryLevelDao;

	@Autowired
	Gstr1CommonUtility gstr1CommonUtility;

	@Override
	public Workbook findGstr1ReviewSummRecords(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Object> responseFromTot = new ArrayList<>();
		responseFromTot = gstr1ASPB2CSSavableTotalDao
				.getGstr1B2CSSavableReports(request);

		/*
		 * List<Object> responseFromViewTrans = new ArrayList<>();
		 * responseFromViewTrans = gstr1ReviewSummaryReportsDao
		 * .getGstr1RSReports(request);
		 */

		List<Object> responseFromViewSum = new ArrayList<>();
		responseFromViewSum = gstr1ASPB2CSSavableSummaryLevelDao
				.getGstr1B2CSSavableReports(request);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 B2CSA processed data item level response"
					+ responseFromViewSum.size());
		}

		if ((responseFromTot != null && responseFromTot.size() > 0)) {
			workbook = gstr1CommonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "GSTR-1_B2CSA_Savable.xlsx");
		} else if (responseFromViewSum != null
				&& responseFromViewSum.size() > 0) {
			workbook = gstr1CommonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "GSTR-1_B2CSA_Savable.xlsx");
		} 

		

		if (responseFromTot != null && responseFromTot.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Savable.b2cS.totalsummary.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromTot, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromTot.size(), true, "yyyy-mm-dd", false);
		}

		/*
		 * if (responseFromViewTrans != null && responseFromViewTrans.size() >
		 * 0) { String[] invoiceHeaders = commonUtility
		 * .getProp("anx1.api.new.report.headers").split(",");
		 * 
		 * Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
		 * errorDumpCells.importCustomObjects(responseFromViewTrans,
		 * invoiceHeaders, isHeaderRequired, startRow, startcolumn,
		 * responseFromViewTrans.size(), true, "yyyy-mm-dd", false); }
		 */
		if (responseFromViewSum != null && responseFromViewSum.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Savable.b2cS.summarylevel.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
			errorDumpCells.importCustomObjects(responseFromViewSum,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewSum.size(), true, "yyyy-mm-dd", false);
		}
		return workbook;
	}
}
