package com.ey.advisory.app.services.reports;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bSummaryErrorRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.service.gstr3bSummary.Gstr3bSummaryUploadDto;
import com.ey.advisory.service.gstr3bSummary.Gstr3bSummaryUploadErrorEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("Gstr3bSummaryDownloadService")
public class Gstr3bSummaryDownloadService {

	@Autowired
	@Qualifier("Gstr3bSummaryErrorRepository")
	private Gstr3bSummaryErrorRepository errRepo;

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository psdRepo;

	@Autowired
	CommonUtility commonUtility;

	public Workbook generateErrorReport(Integer fileId, String fileName)
			throws IOException {
		Workbook workbook = null;
		List<Gstr3bSummaryUploadErrorEntity> entityList = errRepo
				.findByFileId(fileId.longValue());

		List<Gstr3bSummaryUploadDto> records = entityList.stream()
				.map(o -> convertToErrorList(o, Long.valueOf(fileId), fileName,
						true))
				.collect(Collectors.toList());
		List<Gstr3bSummaryUploadDto> errorRecords = new ArrayList<>();
		for (Gstr3bSummaryUploadDto dto : records) {
			if (filteredPsdRecords(dto) != null) {
				errorRecords.add(dto);
			}
		}
		String type = "Error";
		workbook = writeToExcel(errorRecords, type);
		return workbook;
	}

	private Gstr3bSummaryUploadDto convertToPsdList(
			Gstr3BGstinAspUserInputEntity entity, Integer fileId,
			String fileName, boolean isProcessed) {

		Gstr3bSummaryUploadDto dto = new Gstr3bSummaryUploadDto();

		dto.setCessAmount(
				entity.getCess() != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getCess().toString()) : null);
		dto.setCgstAmount(
				entity.getCgst() != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getCgst().toString()) : null);
		dto.setSgstAmount(
				entity.getSgst() != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getSgst().toString()) : null);
		dto.setGstin(entity.getGstin() != null ? entity.getGstin().toString()
				: null);
		dto.setIgstAmount(
				entity.getIgst() != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getIgst().toString()) : null);
		dto.setTotalTaxableValue(entity.getTaxableVal() != null
				? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getTaxableVal().toString())
				: null);
		dto.setReturnPeriod(entity.getTaxPeriod() != null
				? ("'" + entity.getTaxPeriod().toString()) : null);
		dto.setPos(
				entity.getPos() != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getPos().toString()) : null);
		dto.setFileId(Long.valueOf(fileId));
		dto.setFileName(fileName);

		if (entity.getSectionName() != null) {
			if (entity.getSectionName().equalsIgnoreCase("3.1(a)")) {
				dto.setTableNumber("3.1.a");
			} else if (entity.getSectionName().equalsIgnoreCase("3.1(b)")) {
				dto.setTableNumber("3.1.b");
			} else if (entity.getSectionName().equalsIgnoreCase("3.1(c)")) {
				dto.setTableNumber("3.1.c");
			} else if (entity.getSectionName().equalsIgnoreCase("3.1(d)")) {
				dto.setTableNumber("3.1.d");
			} else if (entity.getSectionName().equalsIgnoreCase("3.1(e)")) {
				dto.setTableNumber("3.1.e");
			} else if (entity.getSectionName().equalsIgnoreCase("3.1.1(a)")) {
				dto.setTableNumber("3.1.1.a");
			} else if (entity.getSectionName().equalsIgnoreCase("3.1.1(b)")) {
				dto.setTableNumber("3.1.1.b");
			} else if (entity.getSectionName().equalsIgnoreCase("3.2(a)")) {
				dto.setTableNumber("3.2.a");
			} else if (entity.getSectionName().equalsIgnoreCase("3.2(b)")) {
				dto.setTableNumber("3.2.b");
			} else if (entity.getSectionName().equalsIgnoreCase("3.2(c)")) {
				dto.setTableNumber("3.2.c");
			} else if (entity.getSectionName().equalsIgnoreCase("4(a)(1)")) {
				dto.setTableNumber("4.a.1");
			} else if (entity.getSectionName().equalsIgnoreCase("4(a)(2)")) {
				dto.setTableNumber("4.a.2");
			} else if (entity.getSectionName().equalsIgnoreCase("4(a)(3)")) {
				dto.setTableNumber("4.a.3");
			} else if (entity.getSectionName().equalsIgnoreCase("4(a)(4)")) {
				dto.setTableNumber("4.a.4");
			} else if (entity.getSectionName().equalsIgnoreCase("4(a)(5)")) {
				dto.setTableNumber("4.a.5");
			} else if (entity.getSectionName().equalsIgnoreCase("4(b)(1)")) {
				dto.setTableNumber("4.b.1");
			} else if (entity.getSectionName().equalsIgnoreCase("4(b)(2)")) {
				dto.setTableNumber("4.b.2");
			} else if (entity.getSectionName().equalsIgnoreCase("4(d)(1)")) {
				dto.setTableNumber("4.d.1");
			} else if (entity.getSectionName().equalsIgnoreCase("4(d)(2)")) {
				dto.setTableNumber("4.d.2");
			} else if (entity.getSectionName().equalsIgnoreCase("5.1(a)")) {
				dto.setTableNumber("5.1.a");
			} else if (entity.getSectionName().equalsIgnoreCase("5.1(b)")) {
				dto.setTableNumber("5.1.b");
			} else if (entity.getSectionName().equalsIgnoreCase("5(a)")) {
				if (entity.getInterState().compareTo(BigDecimal.ZERO) != 0) {
					dto.setTableNumber("5.a.1");
					if (entity.getInterState() != null) {
						dto.setTotalTaxableValue(
								entity.getInterState().toString());
					}
				} else if (entity.getIntraState()
						.compareTo(BigDecimal.ZERO) != 0) {
					dto.setTableNumber("5.a.2");
					if (entity.getIntraState() != null) {
						dto.setTotalTaxableValue(
								entity.getIntraState().toString());
					}
				}
			} else if (entity.getSectionName().equalsIgnoreCase("5(b)")) {
				if (entity.getInterState().compareTo(BigDecimal.ZERO) != 0) {
					dto.setTableNumber("5.b.1");
					if (entity.getInterState() != null) {
						dto.setTotalTaxableValue(
								entity.getInterState().toString());
					}
				} else if (entity.getIntraState()
						.compareTo(BigDecimal.ZERO) != 0) {
					dto.setTableNumber("5.b.2");
					if (entity.getIntraState() != null) {
						dto.setTotalTaxableValue(
								entity.getIntraState().toString());
					}
				}

			} else {
				dto.setTableNumber(entity.getSectionName() != null
						? entity.getSectionName().toString() : null);
			}
		}
		return dto;
	}

	private Gstr3bSummaryUploadDto convertToErrorList(
			Gstr3bSummaryUploadErrorEntity entity, Long fileId, String fileName,
			boolean isError) {

		Gstr3bSummaryUploadDto dto = new Gstr3bSummaryUploadDto();

		dto.setCessAmount(entity.getCessAmount() != null
				? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getCessAmount().toString())
				: null);
		dto.setCgstAmount(entity.getCgstAmount() != null
				? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getCgstAmount().toString())
				: null);
		dto.setSgstAmount(entity.getSgstAmount() != null
				? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getSgstAmount().toString())
				: null);
		dto.setGstin(entity.getGstin() != null ? entity.getGstin().toString()
				: null);
		dto.setIgstAmount(entity.getIgstAmount() != null
				? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getIgstAmount().toString())
				: null);
		dto.setTotalTaxableValue(
				entity.getTotalTaxablevalue() != null
						? DownloadReportsConstant.CSVCHARACTER.concat(
								entity.getTotalTaxablevalue().toString())
						: null);
		dto.setReturnPeriod(entity.getReturnPeriod() != null
				? ("'" + entity.getReturnPeriod().toString()) : null);
		dto.setPos(
				entity.getPos() != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(entity.getPos().toString()) : null);
		dto.setFileId(fileId);
		dto.setFileName(fileName);

		dto.setTableNumber(entity.getTableNumber() != null
				? entity.getTableNumber().toString() : null);
		dto.setErrorCode(entity.getErrorCode() != null
				? entity.getErrorCode().toString() : null);
		dto.setErrorDesc(entity.getErrorMessage() != null
				? entity.getErrorMessage().toString() : null);
		return dto;
	}

	public Workbook generatePSDReport(Integer fileId, String fileName)
			throws IOException {
		Workbook workbook = null;
		List<Gstr3BGstinAspUserInputEntity> entityList = psdRepo
				.findByFileId(fileId.longValue());

		List<Gstr3bSummaryUploadDto> psdRecords = entityList.stream()
				.map(o -> convertToPsdList(o, fileId, fileName, true))
				.filter(a -> (!a.getTableNumber().equalsIgnoreCase("4(a)(5)(5.1.a)")
						&& !a.getTableNumber().equalsIgnoreCase("4(b)(2)(2.1)")
						&& !a.getTableNumber().equalsIgnoreCase("4(b)(1)(1.1)")))
				.collect(Collectors.toList());
		String type = "Processed";
		workbook = writeToExcel(psdRecords, type);
		return workbook;
	}

	public Workbook generateTotalReport(Integer fileId, String fileName)
			throws IOException {

		Workbook workbook = null;

		List<Gstr3BGstinAspUserInputEntity> psdEntityList = psdRepo
				.findByFileId(fileId.longValue());

		List<Gstr3bSummaryUploadDto> psdRecords = psdEntityList.stream()
				.map(o -> convertToPsdList(o, fileId, fileName, false))
				.filter(a -> (!a.getTableNumber().equalsIgnoreCase("4(a)(5)(5.1.a)")
						&& !a.getTableNumber().equalsIgnoreCase("4(b)(2)(2.1)")
						&& !a.getTableNumber().equalsIgnoreCase("4(b)(1)(1.1)")))
				.collect(Collectors.toList());

		List<Gstr3bSummaryUploadErrorEntity> errorEntityList = errRepo
				.findByFileId(fileId.longValue());

		List<Gstr3bSummaryUploadDto> records = errorEntityList.stream()
				.map(o -> convertToErrorList(o, Long.valueOf(fileId), fileName,
						false))
				.collect(Collectors.toList());
		List<Gstr3bSummaryUploadDto> errorRecords = new ArrayList<>();
		for (Gstr3bSummaryUploadDto dto : records) {
			if (filteredPsdRecords(dto) != null) {
				errorRecords.add(dto);
			}
		}
		List<Gstr3bSummaryUploadDto> totalRecords = new ArrayList<Gstr3bSummaryUploadDto>();
		totalRecords.addAll(psdRecords);
		totalRecords.addAll(errorRecords);

		String type = "Total";
		workbook = writeToExcel(totalRecords, type);
		return workbook;
	}

	private Workbook writeToExcel(List<Gstr3bSummaryUploadDto> response,
			String type) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (response != null && !response.isEmpty()) {

			String[] invoiceHeaders = null;
			if (type.equalsIgnoreCase("error")) {

				invoiceHeaders = commonUtility
						.getProp("gstr3b.summary.error.report.header")
						.split(",");

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "GSTR 3B_Error Report.xlsx");
			} else if (type.equalsIgnoreCase("Processed")) {

				invoiceHeaders = commonUtility
						.getProp("gstr3b.summary.processed.report.header")
						.split(",");

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "GSTR 3B_Processed Report.xlsx");
			} else {

				invoiceHeaders = commonUtility
						.getProp("gstr3b.summary.error.report.header")
						.split(",");

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "GSTR 3B_Error Report.xlsx");
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr3bSummary.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(response, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn, response.size(),
					true, "yyyy-mm-dd", false);

			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "GenerateErrorReportImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.DATA_FOR_GSTR3B_SUMMARY,
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "saving excel sheet into folder, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}

		} else {
			throw new AppException("No records found, cannot generate report");
		}
		return workbook;
	}

	public Gstr3bSummaryUploadDto filteredPsdRecords(
			Gstr3bSummaryUploadDto dto) {
		if (dto.getTableNumber() != null) {
			if (dto.getTableNumber().equalsIgnoreCase("4(a)(5)(5.1.a)")
					|| dto.getTableNumber().equalsIgnoreCase("4(b)(2)(a)"))
				return null;
		}
		return dto;
	}

	// public String getTableNumber(String str){
	//
	// }
}
