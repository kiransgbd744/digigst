package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
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
import com.ey.advisory.app.data.services.anx1.Gstr2bVsGstr3bPrSummaryFetchService;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bProcessSummaryFinalRespDto;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bProcessSummaryScreenRespDto;
import com.ey.advisory.app.docs.dto.Gstr2bVsGstr3bProcessSummaryReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

/**
 * @author Shashikant.Shukla
 *
 * 
 */

@Service("Gstr2bvs3bProcessedRecordsScreenServiceImpl")
public class Gstr2bvs3bProcessedRecordsScreenServiceImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2bvs3bProcessedRecordsScreenServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	@Autowired
	@Qualifier("Gstr2bVsGstr3bPrSummaryFetchService")
	Gstr2bVsGstr3bPrSummaryFetchService gstr2bVsGstr3bPrSummaryFetchService;

	public Workbook findGstr2bvs3bProcessedScreenDownload(
			Gstr2bVsGstr3bProcessSummaryReqDto criteria) throws Exception {
		Workbook workbook = new Workbook();
		int startRow = 5;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		
		Gstr1VsGstr3bProcessSummaryReqDto reqCriteria = new Gstr1VsGstr3bProcessSummaryReqDto();
		reqCriteria.setConfigId(criteria.getConfigId());
		reqCriteria.setDataSecAttrs(criteria.getDataSecAttrs());
		reqCriteria.setEntityId(criteria.getEntityId());
		reqCriteria.setTaxPeriodFrom(criteria.getTaxPeriodFrom());
		reqCriteria.setTaxPeriodTo(criteria.getTaxPeriodTo());
		reqCriteria.setType(criteria.getType());
		List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> response = gstr2bVsGstr3bPrSummaryFetchService
				.fetchResponse(reqCriteria);
		List<Gstr2aVsGstr3bProcessSummaryScreenRespDto> responseFromViewProcess = convertProcessSummaryRecordsToScreenDtos(
				response);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Gstr2bvs3b_Processed_Records_Screen_Download.xlsx");

		LOGGER.debug("Gstr2bvs3b Processed rec data response"
				+ responseFromViewProcess);

		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2avs3b.processed.screen.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();

			List<Long> entityId = criteria.getEntityId();
			EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
			for (Long entity : entityId) {
				findEntityByEntityId = repo.findEntityByEntityId(entity);
			}
			// errorDumpCells.get("A1").setValue(" GSTR-6 Processed Summary ");
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
			String startTaxPeriod = startDate.getMonth().getDisplayName(
					TextStyle.SHORT, Locale.US) + " " + startDate.getYear();
			String toTaxPeriod = criteria.getTaxPeriodTo();
			LocalDate endDate = LocalDate.of(
					Integer.parseInt(toTaxPeriod.substring(2)),
					Integer.parseInt(toTaxPeriod.substring(0, 2)), 01);
			String endTaxPeriod = endDate.getMonth().getDisplayName(
					TextStyle.SHORT, Locale.US) + " " + endDate.getYear();
			errorDumpCells.get("E2")
					.setValue("Start TaxPeriod-" + startTaxPeriod);
			errorDumpCells.get("F2").setValue("End TaxPeriod-" + endTaxPeriod);
			//String gstins = criteria.getDataSecAttrs().get("GSTIN").get(0);
			// errorDumpCells.get("B2").setValue("GSTIN-" + gstins);
			errorDumpCells.importCustomObjects(responseFromViewProcess,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;

	}

	private List<Gstr2aVsGstr3bProcessSummaryScreenRespDto> convertProcessSummaryRecordsToScreenDtos(
			List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> response) {
		List<Gstr2aVsGstr3bProcessSummaryScreenRespDto> dtos = new ArrayList<Gstr2aVsGstr3bProcessSummaryScreenRespDto>();
		response.stream().forEach(dto -> {
			Gstr2aVsGstr3bProcessSummaryScreenRespDto screenDto = new Gstr2aVsGstr3bProcessSummaryScreenRespDto();
			screenDto.setGstin(dto.getGstin());
			screenDto.setGstr2AIgst(dto.getGstr2AIgst());
			screenDto.setGstr2ACgst(dto.getGstr2ACgst());
			screenDto.setGstr2ASgst(dto.getGstr2ASgst());
			screenDto.setGstr2ACess(dto.getGstr2ACess());
			screenDto.setGstr3BIgst(dto.getGstr3BIgst());
			screenDto.setGstr3BCgst(dto.getGstr3BCgst());
			screenDto.setGstr3BSgst(dto.getGstr3BSgst());
			screenDto.setGstr3BCess(dto.getGstr3BCess());
			screenDto.setDiffIgst(dto.getDiffIgst());
			screenDto.setDiffCgst(dto.getDiffCgst());
			screenDto.setDiffSgst(dto.getDiffSgst());
			screenDto.setDiffCess(dto.getDiffCess());
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
