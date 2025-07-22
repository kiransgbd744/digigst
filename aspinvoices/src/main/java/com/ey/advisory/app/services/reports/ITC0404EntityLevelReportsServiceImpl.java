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
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("ITC0404EntityLevelReportsServiceImpl")
public class ITC0404EntityLevelReportsServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(ITC0404EntityLevelReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ITC04EntityLevelSummReportsDaoImpl")
	private ITC04EntityLevelSummReportsDaoImpl iTC04EntityLevelSummReportsDaoImpl;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	public Workbook findEntitySummary(Gstr6SummaryRequestDto setDataSecurity, PageRequest pageReq) {
		Gstr6SummaryRequestDto request = (Gstr6SummaryRequestDto) setDataSecurity;
		Workbook workbook = new Workbook();
		List<ITC04EntitylevelDto> responseFromView = new ArrayList<>();
		responseFromView = iTC04EntityLevelSummReportsDaoImpl.getEntityLevelSummReports(request);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates", "ITC04-Entity Level Summary.xlsx");

		//if (responseFromView != null && responseFromView.size() > 0) {

			String entityName = null;
			String taxPeriod = null;
			String date = null;
			String time = null;

			List<Long> entityId = setDataSecurity.getEntityId();
			EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
			for (Long entity : entityId) {
				findEntityByEntityId = repo.findEntityByEntityId(entity);
			}
			if (findEntityByEntityId != null) {
				entityName = findEntityByEntityId.getEntityName();
			}

			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

			LocalDateTime istDateTimeFromUTC = EYDateUtil.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

			DateTimeFormatter FOMATTER1 = DateTimeFormatter.ofPattern("HH:mm:ss");

			date = FOMATTER.format(istDateTimeFromUTC);
			time = FOMATTER1.format(istDateTimeFromUTC);

			Optional<ITC04EntitylevelDto> fromTaxPeriod = responseFromView.stream().findFirst();
			if (fromTaxPeriod.isPresent()) {
				String fromYear = fromTaxPeriod.get().getQreturnPeriod().substring(2);
				String toYear = String.valueOf(Integer.parseInt(fromYear) + 1);
				taxPeriod = fromTaxPeriod.get().getReturnPeriod() + " " + fromYear + "-" + toYear;
			} 

			Itc04EntityUtil.buildAndGenerateItc04Report(workbook, responseFromView, entityName, taxPeriod, date, time, request);
		//}
		return workbook;

	}

	private Workbook createWorkbookWithExcelTemplate(String folderName, String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings().setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}

}
