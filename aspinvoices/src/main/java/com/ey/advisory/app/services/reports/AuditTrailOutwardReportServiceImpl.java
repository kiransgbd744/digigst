package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.AuditTrailReportsReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
/**
 * @author Sujith.Nanga
 *
 */
@Service("AuditTrailOutwardReportServiceImpl")
public class AuditTrailOutwardReportServiceImpl
		implements AuditTrailOutwardReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuditTrailOutwardReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("AuditOutwardReportsDaoImpl")
	private AuditOutwardReportsDao auditOutwardReportsDao;

	@Transactional(value = "clientTransactionManager")
	@Override
	public Workbook findAuditOutwardRecords(SearchCriteria criteria,
			PageRequest pageReq) {
		AuditTrailReportsReqDto request = (AuditTrailReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromout = new ArrayList<>();
		responseFromout = auditOutwardReportsDao.getAudOutwardReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"outwardaudit.xlsx");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Audit trail Outward response" + responseFromout);
		}

		if (responseFromout != null && responseFromout.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("audit.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromout, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromout.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;
	}

	private Workbook createWorkbookWithExcelTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}
}
