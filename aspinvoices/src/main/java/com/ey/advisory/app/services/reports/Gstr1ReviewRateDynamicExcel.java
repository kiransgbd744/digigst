/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

/**
 * @author Sujith.Nanga
 *
 */
import com.aspose.cells.BorderType;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.FontUnderlineType;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenDocRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenNilRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.collect.Lists;

public class Gstr1ReviewRateDynamicExcel {

	// Table Headers
	private static final List<String> outwardSupplyTable = Lists
			.newArrayList("", "Total Tax", "IGST", "CGST", "SGST", "Cess");
	private static final List<String> outwardTable = Lists.newArrayList("",
			"Count", "Invoice Value", "Taxable Value", "Total Tax", "IGST",
			"CGST", "SGST", "Cess", "Count", "Invoice Value", "Taxable Value",
			"Total Tax", "IGST", "CGST", "SGST", "Cess", "Count",
			"Invoice Value", "Taxable Value", "Total Tax", "IGST", "CGST",
			"SGST", "Cess");
	private static final List<String> nillExemptNonGstTable = Lists
			.newArrayList("", "NIL Rated Supplies", "Exempt Supplies",
					"NON GST Supplies", "NIL Rated Supplies", "Exempt Supplies",
					"NON GST Supplies", "NIL Rated Supplies", "Exempt Supplies",
					"NON GST Supplies");
	private static final List<String> advancesTable = Lists.newArrayList("",
			"Count", "Total Value", "Gross Advances", "Total Tax", "IGST",
			"CGST", "SGST", "Cess", "Count", "Total Value", "Gross Advances",
			"Total Tax", "IGST", "CGST", "SGST", "Cess", "Count", "Total Value",
			"Gross Advances", "Total Tax", "IGST", "CGST", "SGST", "Cess");
	private static final List<String> hsnTable = Lists.newArrayList("", "Count",
			"Assesable Amount", "Total Tax", "IGST", "CGST", "SGST", "Cess",
			"Count", "Assesable Amount", "Total Tax", "IGST", "CGST", "SGST",
			"Cess", "Count", "Assesable Amount", "Total Tax", "IGST", "CGST",
			"SGST", "Cess");
	private static final List<String> documentIssuedTable = Lists.newArrayList(
			"", "Total Number", "Cancelled", "Net Issued", "Total Number",
			"Cancelled", "Net Issued", "Total Number", "Cancelled",
			"Net Issued");
	private static final List<String> sezTable = Lists.newArrayList("", "Count",
			"Invoice Value", "Taxable Value", "Total Tax", "IGST", "CGST",
			"SGST", "Cess");

	// Table Body
	private static final List<String> outwardSupplyTableBody = Lists
			.newArrayList("DigiGST", "GSTN", "Difference");
	private static final List<String> outwardTableBody = Lists.newArrayList(
			"B2B (4, 6B, 6C)", "B2B (Amendment) (9A)", "B2CL (5)",
			"B2CL (Amendment) (9A)", "Exports (6A)", "Exports (Amendment) (9A)",
			"CDNR (9B)", "CDNR (Amendment) (9C)", "CDNUR (9B)",
			"CDNUR (Amendment) (9C)", "B2CS (7)", "B2CS (Amendment) (10)");
	private static final List<String> nillExemptNonGstTableBody = Lists
			.newArrayList("NIL Exempt Non GST Supplies", "ASP_NILEXTNON",
					"UI_NILEXTNON");
	private static final List<String> advancesTableBody = Lists.newArrayList(
			"Advance Received (11A- Part I)", "Advance Adjusted (11B- Part I)",
			"Advance Received (Amendment) (11A- Part II)",
			"Advance Adjusted (Amendment) (11B- Part II)");
	private static final List<String> hsnTableBody = Lists
			.newArrayList("HSN Summary (12)", "HSN_ASP", "HSN_UI");
	private static final List<String> documentIssuedTableBody = Lists
			.newArrayList("Document Issued (13)");
	private static final List<String> sezTableBody = Lists.newArrayList("Total",
			"SEZ With Tax", "SEZ Without Tax");
	private static final String NIL_EXEMPT_NON_GST_SUPPLIES = nillExemptNonGstTableBody
			.get(0);
	private static final String ZERO = "0";

	/**
	 * @param workbook
	 * @param annexure1SummaryRequest
	 * @param outwardSummaryResponse
	 * @param hsnSummaryResponse
	 * @param sezSummaryResponse
	 * @param advSummaryResponse
	 * @param nilSummaryResponse
	 * @param docSummaryResponse
	 * @param taxPeriod
	 * @param time
	 * @param date
	 * @param entityName
	 * @throws Exception
	 */
	public static void generaterateExcel(Workbook workbook,
			Annexure1SummaryReqDto annexure1SummaryRequest,
			List<Gstr1SummaryScreenRespDto> outwardSummaryResponse,
			List<Gstr1SummaryScreenRespDto> hsnSummaryResponse,
			List<Gstr1SummaryScreenRespDto> sezSummaryResponse,
			List<Gstr1SummaryScreenRespDto> advSummaryResponse,
			List<Gstr1SummaryScreenNilRespDto> nilSummaryResponse,
			List<Gstr1SummaryScreenDocRespDto> docSummaryResponse,
			String entityName, String date, String time, String taxPeriod)
			throws Exception {
		Worksheet worksheet = workbook.getWorksheets().get(0);
		worksheet.autoFitColumns();
		String[][] dataArray = new String[70][50];
		String gstin = annexure1SummaryRequest.getDataSecAttrs().get("GSTIN")
				.stream().findFirst().get();
		dataArray[1][1] = "GSTR-1 SUMMARY | PROCESSED RECORDS";
		applyStyles(1, 1, worksheet, 0, false, true, FontUnderlineType.NONE,
				null, null, false, 14);
		dataArray[2][1] = "ENTITY NAME- " + entityName + ".| GSTIN- " + gstin
				+ "| Date - " + date + " | Time - " + time + " | Tax Period- "
				+ taxPeriod + "";
		applyStyles(2, 1, worksheet, 0, false, true, FontUnderlineType.NONE,
				null, null, false, 0);

		AtomicInteger row = new AtomicInteger();
		AtomicInteger column = new AtomicInteger();
		// Table Headers
		outwardsupplyHeader(worksheet, row, column, dataArray);
		buildHeaderTable(worksheet, dataArray, 14, 1, "Outward", outwardTable,
				16, 1, 8, 10, 18);
		buildHeaderTable(worksheet, dataArray, 31, 1, "NIL, Exempt, Non GST",
				nillExemptNonGstTable, 33, 1, 3, 5, 8);
		buildHeaderTable(worksheet, dataArray, 38, 1, "Advances", advancesTable,
				40, 1, 8, 10, 18);
		buildHeaderTable(worksheet, dataArray, 48, 1, "HSN Summary", hsnTable,
				50, 1, 7, 9, 16);
		buildHeaderTable(worksheet, dataArray, 55, 1, "Document Issued",
				documentIssuedTable, 57, 1, 3, 5, 8);
		buildHeaderTable(worksheet, dataArray, 62, 1, "SEZ", sezTable, 64, 1, 8,
				0, 0);

		// Table Body
		List<Gstr1SummaryScreenTotalRespDto> totalList = buildAndCalculateOutwardData(
				outwardSummaryResponse);
		buildTableBody(worksheet, dataArray, 9, 2, outwardSupplyTableBody,
				outwardSupplyTable.size() - 1, totalList);

		buildTableOutwardBody(worksheet, dataArray, 17, 1, outwardTableBody,
				outwardTable.size() - 1, outwardSummaryResponse);
		buildTableNilBody(worksheet, dataArray, 34, 1,
				nillExemptNonGstTableBody, nillExemptNonGstTable.size() - 1,
				nilSummaryResponse);
		buildTableAdvancesBody(worksheet, dataArray, 41, 1, advancesTableBody,
				advancesTable.size() - 1, advSummaryResponse);
		buildTableHsnBody(worksheet, dataArray, 51, 1, hsnTableBody,
				hsnTable.size() - 1, hsnSummaryResponse);
		buildTableDocumentIssuedBody(worksheet, dataArray, 58, 1,
				documentIssuedTableBody, documentIssuedTable.size() - 1,
				docSummaryResponse);
		buildTableSezBody(worksheet, dataArray, 65, 1, sezTableBody,
				sezTable.size() - 1, sezSummaryResponse);

		AtomicInteger columnWidths = new AtomicInteger();
		columnWidths.set(0);
		Cells cells = worksheet.getCells();
		cells.setColumnWidth(columnWidths.get(), 5);
		cells.setColumnWidth(columnWidths.incrementAndGet(), 40);
		for (int i = 0; i < 30; i++) {
			cells.setColumnWidth(columnWidths.incrementAndGet(), 20);
		}

		cells.importArray(dataArray, 0, 0);
	}
	
	public static void generaterateExcelForGstr1a(Workbook workbook,
			Annexure1SummaryReqDto annexure1SummaryRequest,
			List<Gstr1SummaryScreenRespDto> outwardSummaryResponse,
			List<Gstr1SummaryScreenRespDto> hsnSummaryResponse,
			List<Gstr1SummaryScreenRespDto> sezSummaryResponse,
			List<Gstr1SummaryScreenRespDto> advSummaryResponse,
			List<Gstr1SummaryScreenNilRespDto> nilSummaryResponse,
			List<Gstr1SummaryScreenDocRespDto> docSummaryResponse,
			String entityName, String date, String time, String taxPeriod)
			throws Exception {
		Worksheet worksheet = workbook.getWorksheets().get(0);
		worksheet.autoFitColumns();
		String[][] dataArray = new String[70][50];
		String gstin = annexure1SummaryRequest.getDataSecAttrs().get("GSTIN")
				.stream().findFirst().get();
		dataArray[1][1] = "GSTR-1A SUMMARY | PROCESSED RECORDS";
		applyStyles(1, 1, worksheet, 0, false, true, FontUnderlineType.NONE,
				null, null, false, 14);
		dataArray[2][1] = "ENTITY NAME- " + entityName + ".| GSTIN- " + gstin
				+ "| Date - " + date + " | Time - " + time + " | Tax Period- "
				+ taxPeriod + "";
		applyStyles(2, 1, worksheet, 0, false, true, FontUnderlineType.NONE,
				null, null, false, 0);

		AtomicInteger row = new AtomicInteger();
		AtomicInteger column = new AtomicInteger();
		// Table Headers
		outwardsupplyHeader(worksheet, row, column, dataArray);
		buildHeaderTable(worksheet, dataArray, 14, 1, "Outward", outwardTable,
				16, 1, 8, 10, 18);
		buildHeaderTable(worksheet, dataArray, 31, 1, "NIL, Exempt, Non GST",
				nillExemptNonGstTable, 33, 1, 3, 5, 8);
		buildHeaderTable(worksheet, dataArray, 38, 1, "Advances", advancesTable,
				40, 1, 8, 10, 18);
		buildHeaderTable(worksheet, dataArray, 48, 1, "HSN Summary", hsnTable,
				50, 1, 7, 9, 16);
		buildHeaderTable(worksheet, dataArray, 55, 1, "Document Issued",
				documentIssuedTable, 57, 1, 3, 5, 8);
		buildHeaderTable(worksheet, dataArray, 62, 1, "SEZ", sezTable, 64, 1, 8,
				0, 0);

		// Table Body
		List<Gstr1SummaryScreenTotalRespDto> totalList = buildAndCalculateOutwardData(
				outwardSummaryResponse);
		buildTableBody(worksheet, dataArray, 9, 2, outwardSupplyTableBody,
				outwardSupplyTable.size() - 1, totalList);

		buildTableOutwardBody(worksheet, dataArray, 17, 1, outwardTableBody,
				outwardTable.size() - 1, outwardSummaryResponse);
		buildTableNilBody(worksheet, dataArray, 34, 1,
				nillExemptNonGstTableBody, nillExemptNonGstTable.size() - 1,
				nilSummaryResponse);
		buildTableAdvancesBody(worksheet, dataArray, 41, 1, advancesTableBody,
				advancesTable.size() - 1, advSummaryResponse);
		buildTableHsnBody(worksheet, dataArray, 51, 1, hsnTableBody,
				hsnTable.size() - 1, hsnSummaryResponse);
		buildTableDocumentIssuedBody(worksheet, dataArray, 58, 1,
				documentIssuedTableBody, documentIssuedTable.size() - 1,
				docSummaryResponse);
		buildTableSezBody(worksheet, dataArray, 65, 1, sezTableBody,
				sezTable.size() - 1, sezSummaryResponse);

		AtomicInteger columnWidths = new AtomicInteger();
		columnWidths.set(0);
		Cells cells = worksheet.getCells();
		cells.setColumnWidth(columnWidths.get(), 5);
		cells.setColumnWidth(columnWidths.incrementAndGet(), 40);
		for (int i = 0; i < 30; i++) {
			cells.setColumnWidth(columnWidths.incrementAndGet(), 20);
		}

		cells.importArray(dataArray, 0, 0);
	}


	/**
	 * @param outwardSummaryResponse
	 * @return
	 */
	private static List<Gstr1SummaryScreenTotalRespDto> buildAndCalculateOutwardData(
			List<Gstr1SummaryScreenRespDto> outwardSummaryResponse) {
		List<Gstr1SummaryScreenTotalRespDto> totalDtos = Lists.newLinkedList();
		if (outwardSummaryResponse != null
				&& !CollectionUtils.isEmpty(outwardSummaryResponse)) {
			BigDecimal aspInvoiceValue = BigDecimal.ZERO;
			BigDecimal aspIgst = BigDecimal.ZERO;
			BigDecimal aspCgst = BigDecimal.ZERO;
			BigDecimal aspSgst = BigDecimal.ZERO;
			BigDecimal aspCess = BigDecimal.ZERO;

			BigDecimal gstnInvoiceValue = BigDecimal.ZERO;
			BigDecimal gstnIgst = BigDecimal.ZERO;
			BigDecimal gstnCgst = BigDecimal.ZERO;
			BigDecimal gstnSgst = BigDecimal.ZERO;
			BigDecimal gstnCess = BigDecimal.ZERO;

			BigDecimal diffInvoiceValue = BigDecimal.ZERO;
			BigDecimal diffIgst = BigDecimal.ZERO;
			BigDecimal diffCgst = BigDecimal.ZERO;
			BigDecimal diffSgst = BigDecimal.ZERO;
			BigDecimal diffCess = BigDecimal.ZERO;
			for (Gstr1SummaryScreenRespDto dto : outwardSummaryResponse) {
				aspInvoiceValue = aspInvoiceValue.add(dto.getAspTaxPayble());
				aspIgst = aspIgst.add(dto.getAspIgst());
				aspCgst = aspCgst.add(dto.getAspCgst());
				aspSgst = aspSgst.add(dto.getAspSgst());
				aspCess = aspCess.add(dto.getAspCess());

				gstnInvoiceValue = gstnInvoiceValue.add(dto.getGstnTaxPayble());
				gstnIgst = gstnIgst.add(dto.getGstnIgst());
				gstnCgst = gstnCgst.add(dto.getGstnCgst());
				gstnSgst = gstnSgst.add(dto.getGstnSgst());
				gstnCess = gstnCess.add(dto.getGstnCess());

				diffInvoiceValue = diffInvoiceValue.add(dto.getDiffTaxPayble());
				diffIgst = diffIgst.add(dto.getDiffIgst());
				diffCgst = diffCgst.add(dto.getDiffCgst());
				diffSgst = diffSgst.add(dto.getDiffSgst());
				diffCess = diffCess.add(dto.getDiffCess());

			}

			totalDtos.add(new Gstr1SummaryScreenTotalRespDto("DigiGST",
					aspInvoiceValue, aspIgst, aspCgst, aspSgst, aspCess));
			totalDtos.add(new Gstr1SummaryScreenTotalRespDto("GSTN",
					gstnInvoiceValue, gstnIgst, gstnCgst, gstnSgst, gstnCess));
			totalDtos.add(new Gstr1SummaryScreenTotalRespDto("Difference",
					diffInvoiceValue, diffIgst, diffCgst, diffSgst, diffCess));
		} else {
			totalDtos.add(new Gstr1SummaryScreenTotalRespDto("DigiGST",
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO));
			totalDtos.add(new Gstr1SummaryScreenTotalRespDto("GSTN",
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO));
			totalDtos.add(new Gstr1SummaryScreenTotalRespDto("Difference",
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO));
		}

		return totalDtos;
	}

	private static void buildTableSezBody(Worksheet worksheet,
			String[][] dataArray, int dataRow, int dataColumn,
			List<String> outwardSupplyTableBody, int size,
			List<Gstr1SummaryScreenRespDto> sezSummaryResponse) {
		AtomicInteger row = new AtomicInteger(dataRow);
		AtomicInteger column = new AtomicInteger(dataColumn);
		outwardSupplyTableBody.forEach(dataStr -> {
			dataArray[row.get()][column.get()] = dataStr;
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, false, 0);

			List<Gstr1SummaryScreenRespDto> filterResponse = sezSummaryResponse
					.stream().filter(dto -> dto.getTaxDocType().equals(dataStr))
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(filterResponse)) {
				filterResponse.forEach(dto -> {
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCount());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspInvoiceValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspTaxableValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspTaxPayble());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspIgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspSgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCess());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);

				});
			} else {
				for (int i = 0; i < size; i++) {
					dataArray[row.get()][column.incrementAndGet()] = "0";
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, false,
							0);
				}
			}

			row.incrementAndGet();
			column.set(dataColumn);
		});
	}

	private static void buildTableDocumentIssuedBody(Worksheet worksheet,
			String[][] dataArray, int dataRow, int dataColumn,
			List<String> outwardSupplyTableBody, int size,
			List<Gstr1SummaryScreenDocRespDto> docSummaryResponse) {
		AtomicInteger row = new AtomicInteger(dataRow);
		AtomicInteger column = new AtomicInteger(dataColumn);
		outwardSupplyTableBody.forEach(dataStr -> {
			dataArray[row.get()][column.get()] = dataStr;
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, false, 0);

			List<Gstr1SummaryScreenDocRespDto> filterResponse = docSummaryResponse
					.stream().filter(dto -> dto.getTaxDocType().equals(dataStr))
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(filterResponse)) {
				filterResponse.forEach(dto -> {
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspTotal());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCancelled());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspNetIssued());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnTotal());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnCancelled());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnNetIssued());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffTotal());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffCancelled());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffNetIssued());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
				});
			} else {
				for (int i = 0; i < size; i++) {
					dataArray[row.get()][column.incrementAndGet()] = "0";
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, false,
							0);
				}
			}

			row.incrementAndGet();
			column.set(dataColumn);
		});
	}

	private static void buildTableHsnBody(Worksheet worksheet,
			String[][] dataArray, int dataRow, int dataColumn,
			List<String> outwardSupplyTableBody, int size,
			List<Gstr1SummaryScreenRespDto> hsnSummaryResponse)
			throws CloneNotSupportedException {
		AtomicInteger row = new AtomicInteger(dataRow);
		AtomicInteger column = new AtomicInteger(dataColumn);
		List<Gstr1SummaryScreenRespDto> modifiedResponse = Lists
				.newLinkedList();
		if (hsnSummaryResponse != null && hsnSummaryResponse.size() == 2) {
			Gstr1SummaryScreenRespDto dto1 = hsnSummaryResponse.get(0);
			dto1.setTaxDocType("HSN_ASP");
			Gstr1SummaryScreenRespDto dto2 = hsnSummaryResponse.get(1);
			dto2.setTaxDocType("HSN_UI");
			modifiedResponse.add(dto1);
			modifiedResponse.add(dto2);
		}

		List<Gstr1SummaryScreenRespDto> nilList = Lists.newLinkedList();
		if (modifiedResponse != null
				&& !CollectionUtils.isEmpty(modifiedResponse)) {
			List<Gstr1SummaryScreenRespDto> computed = modifiedResponse.stream()
					.filter(dto -> dto.getTaxDocType().equals("HSN_ASP"))
					.collect(Collectors.toList());
			Gstr1SummaryScreenRespDto nillTableData = (Gstr1SummaryScreenRespDto) computed
					.stream().findFirst().get().clone();
			nillTableData.setTaxDocType("HSN Summary (12)");
			nilList.add(nillTableData);
			modifiedResponse.stream().forEach(dto -> nilList.add(dto));
		}
		outwardSupplyTableBody.forEach(dataStr -> {
			if (dataStr.equalsIgnoreCase("HSN Summary (12)")) {
				dataArray[row.get()][column.get()] = "HSN Summary (12)";
			} else if (dataStr.equals("HSN_ASP")) {
				dataArray[row.get()][column.get()] = "As Per DigiGST Computed";
			} else if (dataStr.equals("HSN_UI")) {
				dataArray[row.get()][column.get()] = "As Per User Edited";
			}
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, false, 0);
			List<Gstr1SummaryScreenRespDto> tableData = nilList.stream()
					.filter(dto -> dto.getTaxDocType().equals(dataStr))
					.collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(tableData)) {
				tableData.forEach(dto -> {
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCount());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					/*dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspInvoiceValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);*/
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspTaxableValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspTaxPayble());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspIgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspSgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCess());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					if (dataStr.equalsIgnoreCase("HSN Summary (12)")) {
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnCount());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						/*dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnInvoiceValue());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);*/
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnTaxableValue());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnTaxPayble());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnIgst());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnCgst());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnSgst());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnCess());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffCount());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						/*dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffInvoiceValue());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);*/
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffTaxableValue());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffTaxPayble());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffIgst());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffCgst());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffSgst());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffCess());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
					} else {
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						/*dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);*/
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						/*dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);*/
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
					}
				});
			} else {
				for (int i = 0; i < size; i++) {
					dataArray[row.get()][column.incrementAndGet()] = "0";
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, false,
							0);
				}
			}

			row.incrementAndGet();
			column.set(dataColumn);
		});
	}

	private static void buildTableAdvancesBody(Worksheet worksheet,
			String[][] dataArray, int dataRow, int dataColumn,
			List<String> outwardSupplyTableBody, int size,
			List<Gstr1SummaryScreenRespDto> advSummaryResponse) {
		AtomicInteger row = new AtomicInteger(dataRow);
		AtomicInteger column = new AtomicInteger(dataColumn);
		outwardSupplyTableBody.forEach(dataStr -> {
			dataArray[row.get()][column.get()] = dataStr;
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, false, 0);

			List<Gstr1SummaryScreenRespDto> filterResponse = advSummaryResponse
					.stream().filter(dto -> dto.getTaxDocType().equals(dataStr))
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(filterResponse)) {
				filterResponse.forEach(dto -> {
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCount());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspInvoiceValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspTaxableValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspTaxPayble());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspIgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspSgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCess());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnCount());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnInvoiceValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnTaxableValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnTaxPayble());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnIgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnCgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnSgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnCess());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffCount());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffInvoiceValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffTaxableValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffTaxPayble());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffIgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffCgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffSgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffCess());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
				});
			} else {
				for (int i = 0; i < size; i++) {
					dataArray[row.get()][column.incrementAndGet()] = "0";
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, false,
							0);
				}
			}

			row.incrementAndGet();
			column.set(dataColumn);
		});
	}

	/**
	 * @param worksheet
	 * @param dataArray
	 * @param i
	 * @param j
	 * @param nillexemptnongsttablebody2
	 * @param k
	 * @param nilSummaryResponse
	 * @throws CloneNotSupportedException
	 */
	private static void buildTableNilBody(Worksheet worksheet,
			String[][] dataArray, int dataRow, int dataColumn,
			List<String> outwardSupplyTableBody, int size,
			List<Gstr1SummaryScreenNilRespDto> nilSummaryResponse)
			throws CloneNotSupportedException {
		List<Gstr1SummaryScreenNilRespDto> modifiedResponse = Lists
				.newLinkedList();
		if (nilSummaryResponse != null && nilSummaryResponse.size() == 2) {
			Gstr1SummaryScreenNilRespDto dto1 = nilSummaryResponse.get(0);
			dto1.setTaxDocType("ASP_NILEXTNON");
			Gstr1SummaryScreenNilRespDto dto2 = nilSummaryResponse.get(1);
			dto2.setTaxDocType("UI_NILEXTNON");
			modifiedResponse.add(dto1);
			modifiedResponse.add(dto2);
		}

		AtomicInteger row = new AtomicInteger(dataRow);
		AtomicInteger column = new AtomicInteger(dataColumn);
		List<Gstr1SummaryScreenNilRespDto> nilList = Lists.newLinkedList();
		if (modifiedResponse != null
				&& !CollectionUtils.isEmpty(modifiedResponse)) {
			List<Gstr1SummaryScreenNilRespDto> computed = modifiedResponse
					.stream()
					.filter(dto -> dto.getTaxDocType().equals("ASP_NILEXTNON"))
					.collect(Collectors.toList());
			Gstr1SummaryScreenNilRespDto nillTableData = (Gstr1SummaryScreenNilRespDto) computed
					.stream().findFirst().get().clone();
			nillTableData.setTaxDocType(NIL_EXEMPT_NON_GST_SUPPLIES);
			nilList.add(nillTableData);
			modifiedResponse.stream().forEach(dto -> nilList.add(dto));
		}

		outwardSupplyTableBody.forEach(dataStr -> {
			if (dataStr.equalsIgnoreCase(NIL_EXEMPT_NON_GST_SUPPLIES)) {
				dataArray[row.get()][column
						.get()] = "NIL, Exempt, Non GST Supplies";
			} else if (dataStr.equals("ASP_NILEXTNON")) {
				dataArray[row.get()][column.get()] = "As Per DigiGST Computed";
			} else if (dataStr.equals("UI_NILEXTNON")) {
				dataArray[row.get()][column.get()] = "As Per User Edited";
			}
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, false, 0);
			List<Gstr1SummaryScreenNilRespDto> tableData = nilList.stream()
					.filter(dto -> dto.getTaxDocType().equals(dataStr))
					.collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(tableData)) {
				tableData.forEach(dto -> {
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspNitRated());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspExempted());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspNonGst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					
					if (dataStr.equalsIgnoreCase(NIL_EXEMPT_NON_GST_SUPPLIES)) {
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnNitRated());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnExempted());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getGstnNonGst());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffNitRated());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffExempted());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = String
								.valueOf(dto.getDiffNonGst());
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
					} else {
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
						dataArray[row.get()][column.incrementAndGet()] = "";
						applyStyles(row.get(), column.get(), worksheet, 1,
								false, false, FontUnderlineType.NONE, null,
								null, true, 0);
					}
				});
			} else {
				for (int i = 0; i < size; i++) {
					dataArray[row.get()][column.incrementAndGet()] = "0";
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, false,
							0);
				}
			}

			row.incrementAndGet();
			column.set(dataColumn);
		});
	}

	private static void buildTableOutwardBody(Worksheet worksheet,
			String[][] dataArray, int dataRow, int dataColumn,
			List<String> outwardSupplyTableBody, int size,
			List<Gstr1SummaryScreenRespDto> outwardSummaryResponse) {
		AtomicInteger row = new AtomicInteger(dataRow);
		AtomicInteger column = new AtomicInteger(dataColumn);
		outwardSupplyTableBody.forEach(dataStr -> {
			dataArray[row.get()][column.get()] = dataStr;
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, false, 0);
			List<Gstr1SummaryScreenRespDto> filterResponse = outwardSummaryResponse
					.stream().filter(dto -> dto.getTaxDocType().equals(dataStr))
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(filterResponse)) {
				filterResponse.forEach(dto -> {
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCount());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspInvoiceValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspTaxableValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspTaxPayble());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspIgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspSgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getAspCess());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnCount());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnInvoiceValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnTaxableValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnTaxPayble());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnIgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnCgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnSgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getGstnCess());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffCount());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffInvoiceValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffTaxableValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffTaxPayble());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffIgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffCgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffSgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getDiffCess());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
				});
			} else {
				for (int i = 0; i < size; i++) {
					dataArray[row.get()][column.incrementAndGet()] = "0";
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, false,
							0);
				}
			}

			row.incrementAndGet();
			column.set(dataColumn);
		});
	}

	private static void buildTableBody(Worksheet worksheet,
			String[][] dataArray, int dataRow, int dataColumn,
			List<String> outwardSupplyTableBody, int size,
			List<Gstr1SummaryScreenTotalRespDto> nilTableDataList) {
		AtomicInteger row = new AtomicInteger(dataRow);
		AtomicInteger column = new AtomicInteger(dataColumn);
		outwardSupplyTableBody.forEach(dataStr -> {
			dataArray[row.get()][column.get()] = dataStr;
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, false, 0);

			List<Gstr1SummaryScreenTotalRespDto> tableData = nilTableDataList
					.stream().filter(dto -> dto.getTaxDocType().equals(dataStr))
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(tableData)) {
				tableData.forEach(dto -> {
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getInvoiceValue());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getIgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getCgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getSgst());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
					dataArray[row.get()][column.incrementAndGet()] = String
							.valueOf(dto.getCess());
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, true, 0);
				});
			} else {
				for (int i = 0; i < size; i++) {
					dataArray[row.get()][column.incrementAndGet()] = "";
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							false, FontUnderlineType.NONE, null, null, false,
							0);
				}
			}

			row.incrementAndGet();
			column.set(dataColumn);
		});
	}

	private static void buildHeaderTable(Worksheet worksheet,
			String[][] dataArray, int headerRow, int headerColumn,
			String headerName, List<String> outwardTable,
			int secondHeaderColumn, int startCol, int endCol, int gstn,
			int diff) {
		AtomicInteger row = new AtomicInteger(headerRow);
		AtomicInteger column = new AtomicInteger(headerColumn);
		dataArray[row.get()][column.get()] = headerName;
		applyStyles(row.get(), column.get(), worksheet, 1, false, true,
				FontUnderlineType.NONE, Color.getDarkBlue(),
				Color.getAntiqueWhite(), true, 0);

		row.incrementAndGet();
		worksheet.getCells().merge(row.get(), column.get(), 2, 1);
		dataArray[row.get()][column.get()] = "Table Type";
		applyStyles(row.get(), column.get(), worksheet, 1, false, true,
				FontUnderlineType.NONE, Color.getLightGray(), null, true, 0);

		AtomicInteger startColumn = new AtomicInteger(startCol);
		AtomicInteger endColumn = new AtomicInteger(endCol);

		worksheet.getCells().merge(row.get(), column.incrementAndGet(),
				startColumn.get(), endColumn.get());
		dataArray[row.get()][column.get()] = "DigiGST";
		applyStyles(row.get(), column.get(), worksheet, 1, false, true,
				FontUnderlineType.NONE, Color.getLightGray(), null, true, 0);
		worksheet.getCells().get(row.get(), column.get()).getMergedRange()
				.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.THIN,
						Color.fromArgb(0));
		worksheet.getCells().get(row.get(), column.get()).getMergedRange()
				.setOutlineBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN,
						Color.fromArgb(0));
		worksheet.getCells().get(row.get(), column.get()).getMergedRange()
				.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN,
						Color.fromArgb(0));
		worksheet.getCells().get(row.get(), column.get()).getMergedRange()
				.setOutlineBorder(BorderType.LEFT_BORDER, CellBorderType.THIN,
						Color.fromArgb(0));

		if (gstn != 0) {
			column.set(gstn);
			worksheet.getCells().merge(row.get(), column.get(),
					startColumn.get(), endColumn.get());
			dataArray[row.get()][column.get()] = "GSTN";
			applyStyles(row.get(), column.get(), worksheet, 1, false, true,
					FontUnderlineType.NONE, Color.getLightGray(), null, true,
					0);
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.TOP_BORDER,
							CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.RIGHT_BORDER,
							CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.BOTTOM_BORDER,
							CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.LEFT_BORDER,
							CellBorderType.THIN, Color.fromArgb(0));
		}

		if (diff != 0) {
			column.set(diff);
			worksheet.getCells().merge(row.get(), column.get(),
					startColumn.get(), endColumn.get());
			dataArray[row.get()][column.get()] = "Difference";
			applyStyles(row.get(), column.get(), worksheet, 1, false, true,
					FontUnderlineType.NONE, Color.getLightGray(), null, true,
					0);
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.TOP_BORDER,
							CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.RIGHT_BORDER,
							CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.BOTTOM_BORDER,
							CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.LEFT_BORDER,
							CellBorderType.THIN, Color.fromArgb(0));
		}

		buildHeader(outwardTable, worksheet, dataArray, secondHeaderColumn, 1,
				Color.getLightGray(), null);
	}

	private static void outwardsupplyHeader(Worksheet worksheet,
			AtomicInteger row, AtomicInteger column, String[][] dataArray) {
		row.set(7);
		column.set(2);

		worksheet.getCells().merge(row.get(), column.get(), 1, 6);
		dataArray[row.get()][column.get()] = "Outward Supply";
		applyStyles(row.get(), column.get(), worksheet, 1, false, true,
				FontUnderlineType.NONE, Color.getLightGray(), null, true, 0);
		worksheet.getCells().get(row.get(), column.get()).getMergedRange()
				.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.THIN,
						Color.fromArgb(0));
		worksheet.getCells().get(row.get(), column.get()).getMergedRange()
				.setOutlineBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN,
						Color.fromArgb(0));
		worksheet.getCells().get(row.get(), column.get()).getMergedRange()
				.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN,
						Color.fromArgb(0));
		worksheet.getCells().get(row.get(), column.get()).getMergedRange()
				.setOutlineBorder(BorderType.LEFT_BORDER, CellBorderType.THIN,
						Color.fromArgb(0));
		buildHeader(outwardSupplyTable, worksheet, dataArray, 8, 2,
				Color.getDarkBlue(), Color.getAntiqueWhite());
	}

	private static void buildHeader(List<String> headerData,
			Worksheet worksheet, String[][] dataArray, int startRow,
			int startColumn, Color backgroundColor, Color fontColor) {
		AtomicInteger row = new AtomicInteger();
		AtomicInteger column = new AtomicInteger();
		row.set(startRow);
		column.set(startColumn);

		headerData.forEach(header -> {
			dataArray[row.get()][column.get()] = header;
			applyStyles(row.get(), column.get(), worksheet, 1, false, true,
					FontUnderlineType.NONE, backgroundColor, fontColor, true,
					0);
			column.incrementAndGet();
		});
	}

	private static void applyStyles(int row, int column, Worksheet worksheet,
			int borderStyle, boolean isItalic, boolean isBold, int isUderLine,
			Color backgroundColor, Color fontColor, boolean isAlignment,
			int fontSize) {
		Style style = worksheet.getCells().get(row, column).getStyle();
		style.setBorder(BorderType.LEFT_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.RIGHT_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.TOP_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.BOTTOM_BORDER, borderStyle,
				Color.getBlack());
		if (backgroundColor != null) {
			style.setForegroundColor(backgroundColor);
			style.setPattern(1);
		}
		if (isAlignment) {
			style.setHorizontalAlignment(TextAlignmentType.CENTER);
		}

		Font font = style.getFont();
		font.setItalic(isItalic);
		font.setBold(isBold);
		font.setUnderline(isUderLine);
		if (fontSize <= 0) {
			font.setSize(10);
		} else {
			font.setSize(fontSize);
		}

		if (fontColor != null) {
			font.setColor(fontColor);
		}
		worksheet.getCells().get(row, column).setStyle(style);
	}

	static class Gstr1SummaryScreenTotalRespDto {
		private String taxDocType;
		private BigDecimal invoiceValue = BigDecimal.ZERO;
		private BigDecimal igst = BigDecimal.ZERO;
		private BigDecimal cgst = BigDecimal.ZERO;
		private BigDecimal sgst = BigDecimal.ZERO;
		private BigDecimal cess = BigDecimal.ZERO;

		/**
		 * @param taxDocType
		 * @param invoiceValue
		 * @param igst
		 * @param cgst
		 * @param sgst
		 * @param cess
		 */
		public Gstr1SummaryScreenTotalRespDto(String taxDocType,
				BigDecimal invoiceValue, BigDecimal igst, BigDecimal cgst,
				BigDecimal sgst, BigDecimal cess) {
			super();
			this.taxDocType = taxDocType;
			this.invoiceValue = invoiceValue;
			this.igst = igst;
			this.cgst = cgst;
			this.sgst = sgst;
			this.cess = cess;
		}

		/**
		 * @return the taxDocType
		 */
		public String getTaxDocType() {
			return taxDocType;
		}

		/**
		 * @param taxDocType
		 *            the taxDocType to set
		 */
		public void setTaxDocType(String taxDocType) {
			this.taxDocType = taxDocType;
		}

		/**
		 * @return the invoiceValue
		 */
		public BigDecimal getInvoiceValue() {
			return invoiceValue;
		}

		/**
		 * @param invoiceValue
		 *            the invoiceValue to set
		 */
		public void setInvoiceValue(BigDecimal invoiceValue) {
			this.invoiceValue = invoiceValue;
		}

		/**
		 * @return the igst
		 */
		public BigDecimal getIgst() {
			return igst;
		}

		/**
		 * @param igst
		 *            the igst to set
		 */
		public void setIgst(BigDecimal igst) {
			this.igst = igst;
		}

		/**
		 * @return the cgst
		 */
		public BigDecimal getCgst() {
			return cgst;
		}

		/**
		 * @param cgst
		 *            the cgst to set
		 */
		public void setCgst(BigDecimal cgst) {
			this.cgst = cgst;
		}

		/**
		 * @return the sgst
		 */
		public BigDecimal getSgst() {
			return sgst;
		}

		/**
		 * @param sgst
		 *            the sgst to set
		 */
		public void setSgst(BigDecimal sgst) {
			this.sgst = sgst;
		}

		/**
		 * @return the cess
		 */
		public BigDecimal getCess() {
			return cess;
		}

		/**
		 * @param cess
		 *            the cess to set
		 */
		public void setCess(BigDecimal cess) {
			this.cess = cess;
		}
	}
}
