package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.beust.jcommander.internal.Lists;
import com.ey.advisory.app.docs.dto.anx1a.GetGstr2aReviewSummaryFinalRespDto;
import com.ey.advisory.app.docs.dto.anx1a.GetGstr2aReviewSummaryRespDto;
import com.ey.advisory.app.services.search.anx2.GetGstr2aReviewSummaryService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;

@Component("Gstr2aReviewSummaryDownloadServiceImpl")
public class Gstr2aReviewSummaryDownloadServiceImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ProcessedRecordsScreenServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("GetGstr2aReviewSummaryService")
	private GetGstr2aReviewSummaryService getGstr2aReviewSummaryService;

	public Workbook findGstr2aRevSummTablesReports(
			Gstr2AProcessedRecordsReqDto criteria) {
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		@SuppressWarnings("unused")
		GetGstr2aReviewSummaryFinalRespDto resp = getGstr2aReviewSummaryService
				.<GetGstr2aReviewSummaryFinalRespDto>loadSummaryDetails(
						criteria);

		List<Gstr2aReviewSummScreenRespDto> responseFromView = convertProcessSummaryRecordsToScreenDtos(
				resp);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Gstr2a_Table_Wise_Summary.xlsx");

		LOGGER.debug(
				"Gstr2a_Table_Wise_Summary data response" + responseFromView);

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.review.screen.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;

	}

	private List<Gstr2aReviewSummScreenRespDto> convertProcessSummaryRecordsToScreenDtos(
			GetGstr2aReviewSummaryFinalRespDto resp) {
		LinkedList<GetGstr2aReviewSummaryRespDto> finalRespDtos = Lists
				.newLinkedList();
		finalRespDtos.addAll(resp.getB2b());
		finalRespDtos.addAll(resp.getB2ba());
		finalRespDtos.addAll(resp.getCdn());
		finalRespDtos.addAll(resp.getCdna());
		finalRespDtos.addAll(resp.getIsd());
		finalRespDtos.addAll(resp.getIsda());

		List<Gstr2aReviewSummScreenRespDto> dtos = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(finalRespDtos)) {
			finalRespDtos.stream().forEach(dto -> {
				Gstr2aReviewSummScreenRespDto screenDto = new Gstr2aReviewSummScreenRespDto();
				screenDto.setTable(dto.getTable().toUpperCase());
				screenDto.setTaxDocType(dto.getTaxDocType().toUpperCase());
				screenDto.setCount(dto.getCount());
				screenDto.setInvoiceValue(dto.getInvoiceValue());
				screenDto.setTaxPayble(dto.getTaxPayble());
				screenDto.setTaxableValue(dto.getTaxableValue());
				screenDto.setIgst(dto.getIgst());
				screenDto.setCgst(dto.getCgst());
				screenDto.setSgst(dto.getSgst());
				screenDto.setCess(dto.getCess());
				dtos.add(screenDto);
			});
		}
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
