package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseScreenDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseStringItemDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryStringResponseDto;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6ReviewSummaryServiceImpl;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.collect.Lists;

@Service("Gstr6ReviewSummaryDownloadServiceImpl")
public class Gstr6ReviewSummaryDownloadServiceImpl
		implements Gstr6ReviewSummaryDownloadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ProcessedRecordsScreenServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr6ReviewSummaryServiceImpl")
	private Gstr6ReviewSummaryServiceImpl gstr6RevSumSerImpl;

	@Override
	public Workbook findGstr6RevSummTablesReports(
			Annexure1SummaryReqDto criteria) {
		Workbook workbook = new Workbook();
		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Gstr6ReviewSummaryResponseDto> respDtos = gstr6RevSumSerImpl
				.getGstr6RevSummary(criteria);
		
		List<Gstr6ReviewSummaryStringResponseDto> finalRespDtos = gstr6RevSumSerImpl
				.getGstr6SectionsSummary(respDtos);
		
		List<Gstr6ReviewSummaryResponseScreenDto> responseFromViewProcess = convertProcessSummaryRecordsToScreenDtos(
				finalRespDtos);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-6_Review_Summary_Screen Download.xlsx");

		LOGGER.debug(
				"Gstr6 Processed rec data response" + responseFromViewProcess);

		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr6.reviewSummary.rec.screen.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewProcess,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;

	}

	private List<Gstr6ReviewSummaryResponseScreenDto> convertProcessSummaryRecordsToScreenDtos(
			List<Gstr6ReviewSummaryStringResponseDto> result) {
		List<Gstr6ReviewSummaryResponseScreenDto> dtos = Lists.newLinkedList();
		result.stream().forEach(dto -> {
			Gstr6ReviewSummaryResponseScreenDto screenDto = new Gstr6ReviewSummaryResponseScreenDto();
			screenDto.setTableDescription(dto.getDocType());
			screenDto.setAspCount(dto.getAspCount());
			screenDto.setAspInvValue(dto.getAspInvValue());
			screenDto.setAspTaxbValue(dto.getAspTaxbValue());
			screenDto.setAspTotTax(dto.getAspTotTax());
			screenDto.setAspIgst(dto.getAspIgst());
			screenDto.setAspSgst(dto.getAspSgst());
			screenDto.setAspCgst(dto.getAspCgst());
			screenDto.setAspCess(dto.getAspCess());
			screenDto.setGstnCount(dto.getGstnCount());
			screenDto.setGstnInvValue(dto.getGstnInvValue());
			screenDto.setGstnTaxbValue(dto.getGstnTaxbValue());
			screenDto.setGstnTotTax(dto.getGstnTotTax());
			screenDto.setGstnIgst(dto.getGstnIgst());
			screenDto.setGstnSgst(dto.getGstnSgst());
			screenDto.setGstnCgst(dto.getGstnCgst());
			screenDto.setGstnCess(dto.getGstnCess());
			screenDto.setDiffCount(dto.getDiffCount());
			screenDto.setDiffInvValue(dto.getDiffInvValue());
			screenDto.setDiffTaxbValue(dto.getDiffTaxbValue());
			screenDto.setDiffTotTax(dto.getDiffTotTax());
			screenDto.setDiffIgst(dto.getDiffIgst());
			screenDto.setDiffSgst(dto.getDiffSgst());
			screenDto.setDiffCgst(dto.getDiffCgst());
			screenDto.setDiffCess(dto.getDiffCess());
			dtos.add(screenDto);
			buildItemsForSection(dtos, dto);
		});
		return dtos;
	}

	private void buildItemsForSection(
			List<Gstr6ReviewSummaryResponseScreenDto> dtos,
			Gstr6ReviewSummaryStringResponseDto dto) {
		List<Gstr6ReviewSummaryResponseStringItemDto> items = dto.getItems();
		if (CollectionUtils.isNotEmpty(items)) {
			items.forEach(item -> {
				Gstr6ReviewSummaryResponseScreenDto itmDto = new Gstr6ReviewSummaryResponseScreenDto();
				itmDto.setTableDescription(" -" + item.getDocType());
				itmDto.setAspCount(item.getAspCount());
				itmDto.setAspInvValue(item.getAspInvValue());
				itmDto.setAspTaxbValue(item.getAspTaxbValue());
				itmDto.setAspTotTax(item.getAspTotTax());
				itmDto.setAspIgst(item.getAspIgst());
				itmDto.setAspSgst(item.getAspSgst());
				itmDto.setAspCgst(item.getAspCgst());
				itmDto.setAspCess(item.getAspCess());
				itmDto.setGstnCount(item.getGstnCount());
				itmDto.setGstnInvValue(item.getGstnInvValue());
				itmDto.setGstnTaxbValue(item.getGstnTaxbValue());
				itmDto.setGstnTotTax(item.getGstnTotTax());
				itmDto.setGstnIgst(item.getGstnIgst());
				itmDto.setGstnSgst(item.getGstnSgst());
				itmDto.setGstnCgst(item.getGstnCgst());
				itmDto.setGstnCess(item.getGstnCess());
				itmDto.setDiffCount(item.getDiffCount());
				itmDto.setDiffInvValue(item.getDiffInvValue());
				itmDto.setDiffTaxbValue(item.getDiffTaxbValue());
				itmDto.setDiffTotTax(item.getDiffTotTax());
				itmDto.setDiffIgst(item.getDiffIgst());
				itmDto.setDiffSgst(item.getDiffSgst());
				itmDto.setDiffCgst(item.getDiffCgst());
				itmDto.setDiffCess(item.getDiffCess());

				dtos.add(itmDto);
			});
		}

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
