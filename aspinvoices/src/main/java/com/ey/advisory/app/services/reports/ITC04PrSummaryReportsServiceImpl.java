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
import com.ey.advisory.app.data.daos.client.ITC04ProcessSummaryFetchDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.Geetgstr2xScreensRespDto;
import com.ey.advisory.app.docs.dto.simplified.ITC04ProcessSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.ITC04ProcessSummaryScreenRespDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.ITC04RequestDto;

@Service("ITC04PrSummaryReportsServiceImpl")
public class ITC04PrSummaryReportsServiceImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ITC04PrSummaryReportsServiceImpl.class);

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
	 @Qualifier("entityInfoDetailsRepository")
	 EntityInfoDetailsRepository repo;

	public Workbook downloadPrsummaryReports(ITC04RequestDto criteria) {
		ITC04RequestDto request = (ITC04RequestDto) criteria;

		Workbook workbook = new Workbook();
		int startRow = 5;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		ITC04RequestDto reqDto = processedRecordsCommonSecParam
				.setItc04DataSecuritySearchParams(request);

		List<ITC04ProcessSummaryRespDto> responseFromView = processSummaryFetchDaoImpl
				.fetchITC04Records(reqDto);
		List<ITC04ProcessSummaryScreenRespDto> responseFromViewProcess = convertProcessRecordsToScreenDtos(
				responseFromView);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"ITC04ProcessSummaryData.xlsx");
		
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("ITCProcess data response" + responseFromView);
		}
		
		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("itc.process.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			List<Long> entityId = criteria.getEntityId();
			EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
			for (Long entity : entityId) {
				findEntityByEntityId = repo.findEntityByEntityId(entity);
			}
			errorDumpCells.get("A1").setValue(" ITC04 Processed Summary ");
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
                    responseFromViewProcess.size(), true, "yyyy-mm-dd",
                    false);
		}

		return workbook;
	}

	private List<ITC04ProcessSummaryScreenRespDto> convertProcessRecordsToScreenDtos(
			List<ITC04ProcessSummaryRespDto> responseFromView) {
		List<ITC04ProcessSummaryScreenRespDto> dtos = new ArrayList<ITC04ProcessSummaryScreenRespDto>();
		responseFromView.stream().forEach(dto -> {
			ITC04ProcessSummaryScreenRespDto screenDto = new ITC04ProcessSummaryScreenRespDto();
			screenDto.setGstin(dto.getGstin());
			screenDto.setSaveStatus(String.valueOf(dto.getSaveStatus()));
			screenDto.setGsCount(String.valueOf(dto.getGsCount()));
			screenDto.setGsQuantity(String.valueOf(dto.getGsQuantity()));
			screenDto
					.setGsTaxableValue(String.valueOf(dto.getGsTaxableValue()));
			screenDto.setGrCount(String.valueOf(dto.getGrCount()));
			screenDto
					.setGrQuantityLoss(String.valueOf(dto.getGrQuantityLoss()));
			screenDto
					.setGrQuantityRece(String.valueOf(dto.getGrQuantityRece()));
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
