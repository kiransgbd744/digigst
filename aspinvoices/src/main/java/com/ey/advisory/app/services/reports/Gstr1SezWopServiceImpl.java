/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
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
import com.ey.advisory.app.data.entities.customisedreport.CustomisedFieldSelEntity;
import com.ey.advisory.app.data.repositories.client.customisedreport.CustomisedFieldSelRepo;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.report.convertor.AspSyncCustomisedReportConvertor;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Service("Gstr1SezWopServiceImpl")
public class Gstr1SezWopServiceImpl implements Gstr1ReviewSummReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SezWopServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1SezWopSectionReportDaoImpl")
	private Gstr1ReviewSummaryCustomizedReportsDao gstr1ReviewSummaryReportsDao;

	@Autowired
	CustomisedFieldSelRepo custFieldSeleRepo;

	@Autowired
	@Qualifier("AspSyncCustomisedReportConvertor")
	AspSyncCustomisedReportConvertor aspUploadReptConverter;

	@Transactional(value = "clientTransactionManager")
	@Override
	public Workbook findGstr1ReviewSummRecords(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		Long entityId = request.getEntityId().get(0);
		Pair<List<Object[]>, List<Object>> gstr1rsReports = gstr1ReviewSummaryReportsDao
				.getGstr1RSReports(request);

		List<Object> responseFromView = gstr1rsReports.getValue1();
		List<DataStatusEinvoiceDto> responseFromViewObj = new ArrayList<>();

		LOGGER.debug("Gstr1 Sez processed data item level response"
				+ responseFromView);

		List<CustomisedFieldSelEntity> isActiveFieldSel = custFieldSeleRepo
				.findByEntityIdAndReportTypeAndIsActiveTrue(entityId,
						APIConstants.GSTR1_CUST_INV_REPORT);

		if (responseFromView != null && responseFromView.size() > 0) {

			if (isActiveFieldSel == null || isActiveFieldSel.isEmpty()) {

				String msg = String
						.format("SEZWOP Gstr1_Asp_Processed_TransactionalReport :"
								+ " No Active column List found for Entity Id %s  "
								+ "and ReportType 'GSTR1_TRANS_LEVEL' Generating "
								+ "Default Report ", entityId);
				LOGGER.debug(msg);
				String[] invoiceHeaders = commonUtility
						.getProp("anx1.api.new.report.headers").split(",");
				
				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"GSTR-1_Section_SEZWOP.xlsx");
				Cells errorDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				errorDumpCells.importCustomObjects(responseFromView,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromView.size(), true, "yyyy-mm-dd", false);
			} else {

				CommonUtility.setAsposeLicense();
				List<String> selectedList  = Arrays
						.asList(isActiveFieldSel.get(0).getHeaderMapping().split(","));

				Map<String, Object> ap = new HashMap<>();
				ap.put("selectedList", selectedList);

				String[] invoiceHeaders = selectedList.toArray(new String[selectedList.size()]);

				responseFromViewObj = responseFromView.stream().map(
						o -> (DataStatusEinvoiceDto) aspUploadReptConverter.convert(ap, (DataStatusEinvoiceDto) o))
						.collect(Collectors.toCollection(ArrayList::new));

				isHeaderRequired = true;
				startRow = 1;

				CommonUtility.setAsposeLicense();
				Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();

			errorDumpCells.importArray(invoiceHeaders, 0, 0, false);
			
			List<String> selectedColumnList  = Arrays
					.asList(isActiveFieldSel.get(0).getJavaMapp().split(","));
			String[] invoiceColumns = selectedColumnList.toArray(new String[selectedList.size()]);
				
			errorDumpCells.importCustomObjects(responseFromViewObj, invoiceColumns, false, startRow,
						startcolumn, responseFromViewObj.size(), true, "yyyy-mm-dd", false);

			}
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
