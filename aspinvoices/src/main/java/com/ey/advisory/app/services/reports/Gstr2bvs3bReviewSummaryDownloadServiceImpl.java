package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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
import com.ey.advisory.app.data.services.anx1.Gstr2bVsGstr3bReviewSummaryFetchService;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bReviewSummaryRespDto;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bReviewSummaryScreenRespDto;
import com.ey.advisory.app.docs.dto.Gstr2bVsGstr3bProcessSummaryReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.collect.Lists;

/**
 * @author Shashikant.Shukla
 *
 * 
 */

@Service("Gstr2bvs3bReviewSummaryDownloadServiceImpl")
public class Gstr2bvs3bReviewSummaryDownloadServiceImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2bvs3bReviewSummaryDownloadServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr2bVsGstr3bReviewSummaryFetchService")
	Gstr2bVsGstr3bReviewSummaryFetchService gstr2bVsGstr3bReviewSummaryFetchService;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	public Workbook findGstr2avs3bRevSummTablesReports(
			Gstr2bVsGstr3bProcessSummaryReqDto criteria) throws Exception {
		Workbook workbook = new Workbook();
		int startRow = 4;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Gstr2aVsGstr3bReviewSummaryRespDto> response = gstr2bVsGstr3bReviewSummaryFetchService
				.gstr2bVsGstr3bReviewSummaryRecords(criteria);
		Gstr2aVsGstr3bReviewSummaryRespDto responseA = new Gstr2aVsGstr3bReviewSummaryRespDto();
		Gstr2aVsGstr3bReviewSummaryRespDto responseB = new Gstr2aVsGstr3bReviewSummaryRespDto();
		for(Gstr2aVsGstr3bReviewSummaryRespDto dto : response){
			if(dto.getCalFeild().equalsIgnoreCase("A"))
				responseA = dto;
			if(dto.getCalFeild().equalsIgnoreCase("B"))
				responseB = dto;
		}
		Gstr2aVsGstr3bReviewSummaryRespDto differenceDto = new Gstr2aVsGstr3bReviewSummaryRespDto();
		differenceDto.setCalFeild("C=A-B");
		differenceDto.setDescription("Difference");
		differenceDto.setIgst(responseA.getIgst().subtract(responseB.getIgst()));
		differenceDto.setCgst(responseA.getCgst().subtract(responseB.getCgst()));
		differenceDto.setSgst(responseA.getSgst().subtract(responseB.getSgst()));
		differenceDto.setCess(responseA.getCess().subtract(responseB.getCess()));
		response.add(differenceDto);
		List<Gstr2aVsGstr3bReviewSummaryScreenRespDto> responseFromViewProcess = convertProcessSummaryRecordsToScreenDtos(
				response);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Gstr2bvs3b_ReviewSummary_Screen_Download.xlsx");

		LOGGER.debug("Gstr2bvs3b_ReviewSummary_Screen_Download "
				+ responseFromViewProcess);

		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2avs3b.review.screen.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();

			List<Long> entityId = criteria.getEntityId();
			EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
			for (Long entity : entityId) {
				findEntityByEntityId = repo.findEntityByEntityId(entity);
			}
			if (findEntityByEntityId != null) {
				errorDumpCells.get("A2").setValue(
						"Entity Name- " + findEntityByEntityId.getEntityName());
			}

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

			errorDumpCells.get("C2").setValue("Date-" + date);
			errorDumpCells.get("D2").setValue("Time-" + time);

			String fromTaxPeriod = criteria.getTaxPeriodFrom();
			LocalDate startDate = LocalDate.of(
					Integer.parseInt(fromTaxPeriod.substring(2)),
					Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
			String taxPeriod = startDate.getMonth().getDisplayName(
					TextStyle.SHORT, Locale.US) + " " + startDate.getYear();

			errorDumpCells.get("B3").setValue("From TaxPeriod-" + taxPeriod);

			String toTaxPeriod = criteria.getTaxPeriodTo();
			LocalDate endDate = LocalDate.of(
					Integer.parseInt(toTaxPeriod.substring(2)),
					Integer.parseInt(toTaxPeriod.substring(0, 2)), 01);
			String endTaxPeriod = endDate.getMonth().getDisplayName(
					TextStyle.SHORT, Locale.US) + " " + endDate.getYear();
			errorDumpCells.get("C3").setValue("To TaxPeriod-" + endTaxPeriod);

			String gstins = criteria.getGstin();
//			String gstins = criteria.getDataSecAttrs().get("GSTIN").get(0);
			errorDumpCells.get("B2").setValue("GSTIN-" + gstins);
			errorDumpCells.importCustomObjects(responseFromViewProcess,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;

	}

	private List<Gstr2aVsGstr3bReviewSummaryScreenRespDto> convertProcessSummaryRecordsToScreenDtos(
			List<Gstr2aVsGstr3bReviewSummaryRespDto> response) {
		List<Gstr2aVsGstr3bReviewSummaryScreenRespDto> dtos = Lists
				.newLinkedList();
		response.stream().forEach(dto -> {
			Gstr2aVsGstr3bReviewSummaryScreenRespDto screenDto = new Gstr2aVsGstr3bReviewSummaryScreenRespDto();
			screenDto.setDescription(dto.getDescription());
			screenDto.setCalFeild(dto.getCalFeild());
			screenDto.setIgst(dto.getIgst());
			screenDto.setSgst(dto.getSgst());
			screenDto.setCgst(dto.getCgst());
			screenDto.setCess(dto.getCess());
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
