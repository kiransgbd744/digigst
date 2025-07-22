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
import com.ey.advisory.app.data.daos.client.ITC04ProcessSummaryFetchDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.ITC04SummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.ITC04SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.ITC04SummaryScreenReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.ITC04SimpleDocSummarySearchService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.core.search.SearchResult;

@Service("ITC04ReviewSummaryReportsServiceImpl")
public class ITC04ReviewSummaryReportsServiceImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ITC04ReviewSummaryReportsServiceImpl.class);

	public static final String m2jwsold = "M2JW (Section 4)";
	public static final String jw2m = "JW2M (Section 5A)";
	public static final String otherjw2m = "OtherJW2M (Section 5B)";
	public static final String m2jwsoldFrom = "M2JWSoldfromJW (Section 5C)";

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ItcProcessedReportDaoImpl")
	private ItcReportDao itcProcessReportDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("ITC04ProcessSummaryFetchDaoImpl")
	private ITC04ProcessSummaryFetchDaoImpl processSummaryFetchDaoImpl;

	@Autowired
	@Qualifier("ITC04SimpleDocSummarySearchService")
	ITC04SimpleDocSummarySearchService searchService;

	@Autowired
	@Qualifier("ITC04SummaryScreenReqRespHandler")
	ITC04SummaryScreenReqRespHandler reqResp;
	
	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	public Workbook downloadReviewsummaryReports(ITC04RequestDto criteria) {
		ITC04RequestDto request = (ITC04RequestDto) criteria;

		Workbook workbook = new Workbook();
		int startRow = 5;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<ITC04SummaryRespDto> handleItc04ReqAndResp = reqResp
				.handleItc04ReqAndResp(request);

		List<ITC04SummaryScreenRespDto> responseFromViewProcess = convertProcessRecordsToScreenDtos(
				handleItc04ReqAndResp);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"ITC04ReviewSummaryData.xlsx");
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("ITCReview data response" + responseFromViewProcess);
		}
		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("itc.review.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			List<Long> entityId = criteria.getEntityId();
			EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
			for (Long entity : entityId) {
				findEntityByEntityId = repo.findEntityByEntityId(entity);
			}
			errorDumpCells.get("A1").setValue(" ITC04 Review Summary ");
			if (findEntityByEntityId != null) {

				errorDumpCells.get("A2").setValue(
				"Entity Name- " + findEntityByEntityId.getEntityName());
			
			}
			
			String fromTaxPeriod = request.getTaxPeriod();
			String quarter = fromTaxPeriod.substring(0, 2);
			
			String fYear = GenUtil.getFinancialYearByTaxperiod(fromTaxPeriod);

			String qr = null;

			if (quarter.equalsIgnoreCase("13")) {
				qr = "Apr-Jun";
			} else if (quarter.equalsIgnoreCase("14")) {
				qr = "Jul-Sep";
			} else if (quarter.equalsIgnoreCase("15")) {
				qr = "Oct-Dec";
			} else if (quarter.equalsIgnoreCase("16")) {
				qr = "Jan-Mar";
			} else if (quarter.equalsIgnoreCase("17")) {
				qr = "Apr-Sep";
			} else if (quarter.equalsIgnoreCase("18")) {
				qr = "Oct-Mar";
			}

			
			errorDumpCells.get("E2").setValue("TaxPeriod-" + fYear +" " + qr);
			
			String gstin = request.getDataSecAttrs().get("GSTIN")
					.stream().findFirst().get();
	            errorDumpCells.get("B2").setValue("GSTIN-" + gstin);

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

			errorDumpCells.get("C2").setValue( "Date-" + date);
			errorDumpCells.get("D2").setValue( "Time-" + time);
			errorDumpCells.importCustomObjects(responseFromViewProcess,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;
	}

	private List<ITC04SummaryScreenRespDto> convertProcessRecordsToScreenDtos(
			List<? extends ITC04SummaryRespDto> handleItc04ReqAndResp) {
		List<ITC04SummaryScreenRespDto> dtos = new ArrayList<ITC04SummaryScreenRespDto>();
		handleItc04ReqAndResp.stream().forEach(dto -> {
			ITC04SummaryScreenRespDto screenDto = new ITC04SummaryScreenRespDto();
			if (m2jwsold.equalsIgnoreCase(dto.getTable())) {
				screenDto.setTable(
						"Goods sent - Manufacturer to Job Worker (4)");
			}
			if (jw2m.equalsIgnoreCase(dto.getTable())) {
				screenDto.setTable(
						"Goods received back - Job Worker to Manufacturer (5A)");
			}
			if (otherjw2m.equalsIgnoreCase(dto.getTable())) {
				screenDto.setTable(
						"Goods received back - Other Job Worker to Manufacturer (5B)");
			}
			if (m2jwsoldFrom.equalsIgnoreCase(dto.getTable())) {
				screenDto.setTable("Goods sold from Job Worker Premises (5C)");
			}

			screenDto.setAspCount(String.valueOf(dto.getAspCount()));
			screenDto.setAspTaxableValue(
					String.valueOf(dto.getAspTaxableValue()));
			screenDto.setGstnCount(String.valueOf(dto.getGstnCount()));

			if (jw2m.equalsIgnoreCase(dto.getTable())
					|| otherjw2m.equalsIgnoreCase(dto.getTable())
					|| m2jwsoldFrom.equalsIgnoreCase(dto.getTable())) {

				screenDto.setGstnTaxableValue("NA");
			} else {

				screenDto.setGstnTaxableValue(
						String.valueOf(dto.getGstnTaxableValue()));
			}

			screenDto.setDiffCount(String.valueOf(dto.getDiffCount()));

			if (jw2m.equalsIgnoreCase(dto.getTable())
					|| otherjw2m.equalsIgnoreCase(dto.getTable())
					|| m2jwsoldFrom.equalsIgnoreCase(dto.getTable())) {

				screenDto.setDiffTaxableValue("NA");
			} else {
				
				screenDto.setDiffTaxableValue(
						String.valueOf(dto.getDiffTaxableValue()));

			}	
			dtos.add(screenDto);
		});
		return dtos;

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
