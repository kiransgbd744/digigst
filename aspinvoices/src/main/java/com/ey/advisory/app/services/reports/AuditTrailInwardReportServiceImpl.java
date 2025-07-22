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
@Service("AuditTrailInwardReportServiceImpl")
public class AuditTrailInwardReportServiceImpl
		implements AuditTrailInwardReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuditTrailInwardReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("AuditInwardReportDaoImpl")
	private AuditOutwardReportsDao auditInwardReportsDao;

	@Transactional(value = "clientTransactionManager")
	@Override
	public Workbook findAuditInwardRecords(SearchCriteria criteria,
			PageRequest pageReq) {
		AuditTrailReportsReqDto request = (AuditTrailReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromin = new ArrayList<>();
		responseFromin = auditInwardReportsDao.getAudOutwardReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Inwardaudit.xlsx");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Audit trail Inward response" + responseFromin);
		}

		if (responseFromin != null && responseFromin.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("audit.in.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromin, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromin.size(), true, "yyyy-mm-dd", false);
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
