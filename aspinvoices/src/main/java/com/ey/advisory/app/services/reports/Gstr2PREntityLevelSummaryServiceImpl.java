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
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Gstr2PREntityLevelSummaryServiceImpl")
public class Gstr2PREntityLevelSummaryServiceImpl implements Gstr2PREntityLevelSummaryService{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2PREntityLevelSummaryServiceImpl.class);

	
	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr2PREntityLevelSummaryDaoImpl")
	private Gstr2PREntityLevelSummaryDao gstr2PREntityLevelSummaryDao;
	
	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;
	
	
	@Override
	public Workbook findEntityLevelSummary(SearchCriteria searchParams,
			PageRequest pageReq) {
		Gstr2ProcessedRecordsReqDto request = (Gstr2ProcessedRecordsReqDto) searchParams;
		Workbook workbook = new Workbook();
		int startRow = 3;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromQuery = new ArrayList<>();
		responseFromQuery = gstr2PREntityLevelSummaryDao
				.getEntityLevelSummary(request);

		if (responseFromQuery != null && responseFromQuery.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2.entity.level.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"PR_Summary_Entity_Level_Summary.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			
			
			List<Long> entityId = request.getEntityId();
			EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
			for(Long entity : entityId){
			findEntityByEntityId = repo.findEntityByEntityId(entity);
			}
			errorDumpCells.get("A1").setValue("ENTITY LEVEL PR SUMMARY ");
			if(findEntityByEntityId!= null){
			errorDumpCells.get("A2").setValue("ENTITY NAME- "+findEntityByEntityId.getEntityName());
			}
			
			
			errorDumpCells.importCustomObjects(responseFromQuery, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromQuery.size(), true, "yyyy-mm-dd", false);
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
