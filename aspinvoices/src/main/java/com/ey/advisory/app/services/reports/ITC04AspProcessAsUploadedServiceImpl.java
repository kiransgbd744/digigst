/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.views.client.ITC04EntitylevelDto;
import com.ey.advisory.app.data.views.client.ItcTotalRecords;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Sujith.Nanga
 *
 * 
 */	
	@Service("ITC04AspProcessAsUploadedServiceImpl")
	public class ITC04AspProcessAsUploadedServiceImpl {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(ITC04AspProcessAsUploadedServiceImpl.class);

		@Autowired
		CommonUtility commonUtility;

		@Autowired
		@Qualifier("ITC04AspProcessAsUploadedReportDaoImpl")
		private ITC04AspProcessAsUploadedReportDaoImpl iTC04AspProcessAsUploadedReportDaoImpl;


		public Workbook findAspUploaded(Gstr6SummaryRequestDto setDataSecurity,
				PageRequest pageReq) {
			Gstr6SummaryRequestDto request = (Gstr6SummaryRequestDto) setDataSecurity;
			Workbook workbook = new Workbook();

			int startRow = 2;
			int startcolumn = 0;
			boolean isHeaderRequired = false;
			List<ItcTotalRecords> responseFromView = new ArrayList<>();
			responseFromView = iTC04AspProcessAsUploadedReportDaoImpl
					.getAspUploadedReport(request);
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"ITC04AspAsUploadedRecords.xlsx");

			if (responseFromView != null && responseFromView.size() > 0) {
				String[] invoiceHeaders = commonUtility
						.getProp("itc.process.asupload.report.columns").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
				errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						responseFromView.size(), true, "yyyy-mm-dd", false);
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

