/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.ey.advisory.app.data.views.client.ITC04SavedSubmittedReportsDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Service("ITC04SavedSubmittedServiceImpl")
public class ITC04SavedSubmittedServiceImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ITC04SavedSubmittedServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ITC04SavedSubmittedReportDaoImpl")
	private ITC04SavedSubmittedReportDaoImpl iTC04SavedSubmittedReportDaoImpl;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	public Workbook findsavedsubmitted(Gstr6SummaryRequestDto setDataSecurity,
			PageRequest pageReq) {
		Gstr6SummaryRequestDto request = (Gstr6SummaryRequestDto) setDataSecurity;
		Workbook workbook = new Workbook();

		int startRow = 3;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<ITC04SavedSubmittedReportsDto> responseFromView = new ArrayList<>();
		responseFromView = iTC04SavedSubmittedReportDaoImpl
				.getSavedSubmittedReports(request);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"ITC04_Saved Submitted records.xlsx");

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("itc.saved.submit.report.columns").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();

			List<Long> entityId = setDataSecurity.getEntityId();
			EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
			for (Long entity : entityId) {
				findEntityByEntityId = repo.findEntityByEntityId(entity);
			}
			errorDumpCells.get("A1").setValue("ITC04 Saved / Submitted Records ");
			if (findEntityByEntityId != null) {
				errorDumpCells.get("A2").setValue(
						"ENTITY NAME- " + findEntityByEntityId.getEntityName());
			}

			// LocalDateTime day = LocalDateTime.now();

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy");

			DateTimeFormatter FOMATTER1 = DateTimeFormatter
					.ofPattern("HH:mm:ss");

			String date = FOMATTER.format(istDateTimeFromUTC);
			String time = FOMATTER1.format(istDateTimeFromUTC);

			errorDumpCells.get("B2").setValue("Date-" + date);
			errorDumpCells.get("C2").setValue("Time-" + time);

			Optional<ITC04SavedSubmittedReportsDto> fromTaxPeriod = responseFromView
					.stream().findFirst();
			if (fromTaxPeriod.isPresent()) {
				errorDumpCells.get("D2").setValue(
						"TaxPeriod-" + fromTaxPeriod.get().getReturnPeriod());
			}
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
