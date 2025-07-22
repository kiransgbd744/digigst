/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Sujith.Nanga
 *
 */
public class GSTR7ReviewSumDynamicExcel {

	// Table Header
	private static final List<String> summaryTable = Lists.newArrayList("",
			"Count", "Total Amount", "IGST", "CGST", "SGST");
	private static final List<String> sectionTable = Lists.newArrayList("",
			"Count", "Total Amount", "IGST", "CGST", "SGST", "Count",
			"Total Amount", "IGST", "CGST", "SGST", "Count", "Total Amount",
			"IGST", "CGST", "SGST");
	// Table Body
	private static final List<String> summaryTableBody = Lists
			.newArrayList("DigiGST", "GSTN", "Difference");

	/*
	 * public static void main(String[] args) throws Exception {
	 * List<GSTR7ReviewScreenDto> screenDtos = buildDummyData(); Workbook
	 * workbook = new Workbook("F:/GSTR7.xlsx"); buildGstr7ExcelData(workbook,
	 * screenDtos); }
	 */

	private static List<GSTR7ReviewScreenDto> buildDummyData() {
		List<GSTR7ReviewScreenDto> screenDtos = Lists.newArrayList();
		screenDtos.add(new GSTR7ReviewScreenDto("GSTN1",
				"Table-3 (Original Details)", "4", "1000", "3", "5", "12", "1",
				"1000", "1", "2", "3", "1", "1000", "5", "5", "12"));
		screenDtos.add(new GSTR7ReviewScreenDto("GSTN1",
				"Table-4 (Amendment Details)", "1", "2000", "6", "10", "24",
				"5", "1000", "3", "5", "12", "1", "500", "6", "25", "12"));
		return screenDtos;
	}
	

	public static void buildGstr7ExcelData(Workbook workbook,
			List<GSTR7ReviewScreenDto> dtos, String entityName,
			String taxPeriod, String date, String time,
			Gstr1ReviwSummReportsReqDto request) throws Exception {
		Map<String, GSTR7ReviewScreenDto> dtoMap = calculateSummaryData(dtos);

		Worksheet worksheet = workbook.getWorksheets().get(0);
		worksheet.autoFitColumns();
		String[][] dataArray = new String[70][50];
		dataArray[1][1] = "GSTR-7 SUMMARY";
		applyStyles(1, 1, worksheet, 0, false, true, FontUnderlineType.NONE,
				null, null, false, 14);
		dataArray[2][1] =  "ENTITY NAME- " + entityName
				+ "| Date - " + date + " | Time - " + time + " | Tax Period- "
				+ taxPeriod + "";
		applyStyles(2, 1, worksheet, 0, false, true, FontUnderlineType.NONE,
				null, null, false, 0);

		AtomicInteger row = new AtomicInteger();
		AtomicInteger column = new AtomicInteger();
		// Table Headers
		summaryHeader(worksheet, row, column, dataArray);
		buildTableBody(worksheet, dataArray, 9, 3, summaryTableBody,
				summaryTable.size(), dtoMap);

		buildHeaderTable(worksheet, dataArray, 14, 2, "Table Section",
				sectionTable, 15, 1, 5, 8, 13);

		row.set(16);
		column.set(2);

		dtos.forEach(dto -> {
			dataArray[row.get()][column.get()] = dto.getSection();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, false, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto
					.getDigigstCount();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto
					.getDigigstTotalAmt();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto
					.getDigigstIgst();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto
					.getDigigstCgst();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto
					.getDigigstSgst();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto.getGstnCount();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto
					.getGstntTotalAmt();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto.getGstnIgst();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto.getGstnCgst();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto.getGstnSgst();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto.getDiffCount();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto
					.getDiffTotalAmt();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto.getDiffIgst();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto.getDiffCgst();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			dataArray[row.get()][column.incrementAndGet()] = dto.getDiffSgst();
			applyStyles(row.get(), column.get(), worksheet, 1, false, false,
					FontUnderlineType.NONE, null, null, true, 0);
			row.incrementAndGet();
			column.set(2);
		});

		AtomicInteger columnWidths = new AtomicInteger();
		columnWidths.set(0);
		Cells cells = worksheet.getCells();
		cells.setColumnWidth(columnWidths.get(), 5);
		cells.setColumnWidth(columnWidths.incrementAndGet(), 5);
		cells.setColumnWidth(columnWidths.incrementAndGet(), 25);
		for (int i = 0; i < 30; i++) {
			cells.setColumnWidth(columnWidths.incrementAndGet(), 10);
		}

		cells.importArray(dataArray, 0, 0);
		/* workbook.save("F:/GSTR7.xlsx"); */
		System.out.println("GSTR7 excel report generated");
	}

	private static Map<String, GSTR7ReviewScreenDto> calculateSummaryData(
			List<GSTR7ReviewScreenDto> dtos) {
		Map<String, GSTR7ReviewScreenDto> dtoMap = Maps.newLinkedHashMap();
		String gstin = "";
		int digigstCount = 0;
		BigDecimal digigstTotalAmt = BigDecimal.ZERO;
		BigDecimal digigstIgst = BigDecimal.ZERO;
		BigDecimal digigstCgst = BigDecimal.ZERO;
		BigDecimal digigstSgst = BigDecimal.ZERO;
		int gstnCount = 0;
		BigDecimal gstntTotalAmt = BigDecimal.ZERO;
		BigDecimal gstnIgst = BigDecimal.ZERO;
		BigDecimal gstnCgst = BigDecimal.ZERO;
		BigDecimal gstnSgst = BigDecimal.ZERO;
		int diffCount = 0;
		BigDecimal diffTotalAmt = BigDecimal.ZERO;
		BigDecimal diffIgst = BigDecimal.ZERO;
		BigDecimal diffCgst = BigDecimal.ZERO;
		BigDecimal diffSgst = BigDecimal.ZERO;
		for (GSTR7ReviewScreenDto dto : dtos) {
			gstin = dto.getGSTIN();
			digigstCount = digigstCount
					+ Integer.parseInt(dto.getDigigstCount());
			digigstTotalAmt = digigstTotalAmt
					.add(new BigDecimal(dto.getDigigstTotalAmt()));
			digigstIgst = digigstIgst.add(new BigDecimal(dto.getDigigstIgst()));
			digigstCgst = digigstCgst.add(new BigDecimal(dto.getDigigstCgst()));
			digigstSgst = digigstSgst.add(new BigDecimal(dto.getDigigstSgst()));

			gstnCount = gstnCount + Integer.parseInt(dto.getGstnCount());
			gstntTotalAmt = gstntTotalAmt
					.add(new BigDecimal(dto.getGstntTotalAmt()));
			gstnIgst = gstnIgst.add(new BigDecimal(dto.getGstnIgst()));
			gstnCgst = gstnCgst.add(new BigDecimal(dto.getGstnCgst()));
			gstnSgst = gstnSgst.add(new BigDecimal(dto.getGstnSgst()));

			diffCount = diffCount + Integer.parseInt(dto.getDiffCount());
			diffTotalAmt = diffTotalAmt
					.add(new BigDecimal(dto.getDiffTotalAmt()));
			diffIgst = diffIgst.add(new BigDecimal(dto.getDiffIgst()));
			diffCgst = diffCgst.add(new BigDecimal(dto.getDiffCgst()));
			diffSgst = diffSgst.add(new BigDecimal(dto.getDiffSgst()));
		}
		dtoMap.put("DigiGST", new GSTR7ReviewScreenDto(gstin, null,
				String.valueOf(digigstCount), String.valueOf(digigstTotalAmt),
				String.valueOf(digigstIgst), String.valueOf(digigstCgst),
				String.valueOf(digigstSgst), null, null, null, null, null, null,
				null, null, null, null));
		dtoMap.put("GSTN",
				new GSTR7ReviewScreenDto(gstin, null, null, null, null, null,
						null, String.valueOf(gstnCount),
						String.valueOf(gstntTotalAmt), String.valueOf(gstnIgst),
						String.valueOf(gstnCgst), String.valueOf(gstnSgst),
						null, null, null, null, null));
		dtoMap.put("Difference",
				new GSTR7ReviewScreenDto(gstin, null, null, null, null, null,
						null, null, null, null, null, null,
						String.valueOf(diffCount), String.valueOf(diffTotalAmt),
						String.valueOf(diffIgst), String.valueOf(diffCgst),
						String.valueOf(diffSgst)));

		return dtoMap;
	}

	private static void buildHeaderTable(Worksheet worksheet,
			String[][] dataArray, int headerRow, int headerColumn,
			String headerName, List<String> outwardTable,
			int secondHeaderColumn, int startCol, int endCol, int gstn,
			int diff) {
		AtomicInteger row = new AtomicInteger(headerRow);
		AtomicInteger column = new AtomicInteger(headerColumn);

		worksheet.getCells().merge(row.get(), column.get(), 2, 1);
		dataArray[row.get()][column.get()] = "Table Section";
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

		column.set(gstn);
		worksheet.getCells().merge(row.get(), column.get(), startColumn.get(),
				endColumn.get());
		dataArray[row.get()][column.get()] = "GSTN";
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

		column.set(diff);
		worksheet.getCells().merge(row.get(), column.get(), startColumn.get(),
				endColumn.get());
		dataArray[row.get()][column.get()] = "Difference";
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

		buildHeader(outwardTable, worksheet, dataArray, secondHeaderColumn, 2,
				Color.fromArgb(210, 221, 247), null);
	}

	private static void buildTableBody(Worksheet worksheet,
			String[][] dataArray, int dataRow, int dataColumn,
			List<String> summaryTableBody, int size,
			Map<String, GSTR7ReviewScreenDto> dtoMap) {
		AtomicInteger row = new AtomicInteger(dataRow);
		AtomicInteger column = new AtomicInteger(dataColumn);
		for (int i = 0; i < size; i++) {
			if (i == 3) {
				break;
			}
			String sectionName = summaryTableBody.get(i);
			GSTR7ReviewScreenDto screenDto = dtoMap.get(sectionName);
			if (sectionName.equals("DigiGST")) {
				dataArray[row.get()][column.get()] = sectionName;
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getDigigstCount();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getDigigstTotalAmt();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getDigigstIgst();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getDigigstCgst();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getDigigstSgst();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
			} else if (sectionName.equals("GSTN")) {
				dataArray[row.get()][column.get()] = sectionName;
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getGstnCount();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getGstntTotalAmt();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getGstnIgst();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getGstnCgst();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getGstnSgst();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
			} else if (sectionName.equals("Difference")) {
				dataArray[row.get()][column.get()] = sectionName;
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getDiffCount();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getDiffTotalAmt();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getDiffIgst();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getDiffCgst();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
				dataArray[row.get()][column.incrementAndGet()] = screenDto
						.getDiffSgst();
				applyStyles(row.get(), column.get(), worksheet, 1, false, false,
						FontUnderlineType.NONE, null, null, true, 0);
			}
			row.incrementAndGet();
			column.set(dataColumn);
		}
	}

	private static void summaryHeader(Worksheet worksheet, AtomicInteger row,
			AtomicInteger column, String[][] dataArray) {
		row.set(7);
		column.set(3);

		worksheet.getCells().merge(row.get(), column.get(), 1, 6);
		dataArray[row.get()][column.get()] = "Summary";
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
		buildHeader(summaryTable, worksheet, dataArray, 8, 3,
				Color.fromArgb(210, 221, 247), null);
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

	static class GSTR7ReviewScreenDto {

		private String GSTIN;
		private String Section;
		private String digigstCount;
		private String digigstTotalAmt;
		private String digigstIgst;
		private String digigstCgst;
		private String digigstSgst;
		private String gstnCount;
		private String gstntTotalAmt;
		private String gstnIgst;
		private String gstnCgst;
		private String gstnSgst;
		private String diffCount;
		private String diffTotalAmt;
		private String diffIgst;
		private String diffCgst;
		private String diffSgst;

		/**
		 * @param gSTIN
		 * @param section
		 * @param digigstCount
		 * @param digigstTotalAmt
		 * @param digigstIgst
		 * @param digigstCgst
		 * @param digigstSgst
		 * @param gstnCount
		 * @param gstntTotalAmt
		 * @param gstnIgst
		 * @param gstnCgst
		 * @param gstnSgst
		 * @param diffCount
		 * @param diffTotalAmt
		 * @param diffIgst
		 * @param diffCgst
		 * @param diffSgst
		 */
		public GSTR7ReviewScreenDto(String gSTIN, String section,
				String digigstCount, String digigstTotalAmt, String digigstIgst,
				String digigstCgst, String digigstSgst, String gstnCount,
				String gstntTotalAmt, String gstnIgst, String gstnCgst,
				String gstnSgst, String diffCount, String diffTotalAmt,
				String diffIgst, String diffCgst, String diffSgst) {
			super();
			GSTIN = gSTIN;
			Section = section;
			this.digigstCount = digigstCount;
			this.digigstTotalAmt = digigstTotalAmt;
			this.digigstIgst = digigstIgst;
			this.digigstCgst = digigstCgst;
			this.digigstSgst = digigstSgst;
			this.gstnCount = gstnCount;
			this.gstntTotalAmt = gstntTotalAmt;
			this.gstnIgst = gstnIgst;
			this.gstnCgst = gstnCgst;
			this.gstnSgst = gstnSgst;
			this.diffCount = diffCount;
			this.diffTotalAmt = diffTotalAmt;
			this.diffIgst = diffIgst;
			this.diffCgst = diffCgst;
			this.diffSgst = diffSgst;
		}

		/**
		 * @return the section
		 */
		public String getSection() {
			return Section;
		}

		/**
		 * @param section
		 *            the section to set
		 */
		public void setSection(String section) {
			Section = section;
		}

		/**
		 * @return the gSTIN
		 */
		public String getGSTIN() {
			return GSTIN;
		}

		/**
		 * @param gSTIN
		 *            the gSTIN to set
		 */
		public void setGSTIN(String gSTIN) {
			GSTIN = gSTIN;
		}

		/**
		 * @return the digigstCount
		 */
		public String getDigigstCount() {
			return digigstCount;
		}

		/**
		 * @param digigstCount
		 *            the digigstCount to set
		 */
		public void setDigigstCount(String digigstCount) {
			this.digigstCount = digigstCount;
		}

		/**
		 * @return the digigstTotalAmt
		 */
		public String getDigigstTotalAmt() {
			return digigstTotalAmt;
		}

		/**
		 * @param digigstTotalAmt
		 *            the digigstTotalAmt to set
		 */
		public void setDigigstTotalAmt(String digigstTotalAmt) {
			this.digigstTotalAmt = digigstTotalAmt;
		}

		/**
		 * @return the digigstIgst
		 */
		public String getDigigstIgst() {
			return digigstIgst;
		}

		/**
		 * @param digigstIgst
		 *            the digigstIgst to set
		 */
		public void setDigigstIgst(String digigstIgst) {
			this.digigstIgst = digigstIgst;
		}

		/**
		 * @return the digigstCgst
		 */
		public String getDigigstCgst() {
			return digigstCgst;
		}

		/**
		 * @param digigstCgst
		 *            the digigstCgst to set
		 */
		public void setDigigstCgst(String digigstCgst) {
			this.digigstCgst = digigstCgst;
		}

		/**
		 * @return the digigstSgst
		 */
		public String getDigigstSgst() {
			return digigstSgst;
		}

		/**
		 * @param digigstSgst
		 *            the digigstSgst to set
		 */
		public void setDigigstSgst(String digigstSgst) {
			this.digigstSgst = digigstSgst;
		}

		/**
		 * @return the gstnCount
		 */
		public String getGstnCount() {
			return gstnCount;
		}

		/**
		 * @param gstnCount
		 *            the gstnCount to set
		 */
		public void setGstnCount(String gstnCount) {
			this.gstnCount = gstnCount;
		}

		/**
		 * @return the gstntTotalAmt
		 */
		public String getGstntTotalAmt() {
			return gstntTotalAmt;
		}

		/**
		 * @param gstntTotalAmt
		 *            the gstntTotalAmt to set
		 */
		public void setGstntTotalAmt(String gstntTotalAmt) {
			this.gstntTotalAmt = gstntTotalAmt;
		}

		/**
		 * @return the gstnIgst
		 */
		public String getGstnIgst() {
			return gstnIgst;
		}

		/**
		 * @param gstnIgst
		 *            the gstnIgst to set
		 */
		public void setGstnIgst(String gstnIgst) {
			this.gstnIgst = gstnIgst;
		}

		/**
		 * @return the gstnCgst
		 */
		public String getGstnCgst() {
			return gstnCgst;
		}

		/**
		 * @param gstnCgst
		 *            the gstnCgst to set
		 */
		public void setGstnCgst(String gstnCgst) {
			this.gstnCgst = gstnCgst;
		}

		/**
		 * @return the gstnSgst
		 */
		public String getGstnSgst() {
			return gstnSgst;
		}

		/**
		 * @param gstnSgst
		 *            the gstnSgst to set
		 */
		public void setGstnSgst(String gstnSgst) {
			this.gstnSgst = gstnSgst;
		}

		/**
		 * @return the diffCount
		 */
		public String getDiffCount() {
			return diffCount;
		}

		/**
		 * @param diffCount
		 *            the diffCount to set
		 */
		public void setDiffCount(String diffCount) {
			this.diffCount = diffCount;
		}

		/**
		 * @return the diffTotalAmt
		 */
		public String getDiffTotalAmt() {
			return diffTotalAmt;
		}

		/**
		 * @param diffTotalAmt
		 *            the diffTotalAmt to set
		 */
		public void setDiffTotalAmt(String diffTotalAmt) {
			this.diffTotalAmt = diffTotalAmt;
		}

		/**
		 * @return the diffIgst
		 */
		public String getDiffIgst() {
			return diffIgst;
		}

		/**
		 * @param diffIgst
		 *            the diffIgst to set
		 */
		public void setDiffIgst(String diffIgst) {
			this.diffIgst = diffIgst;
		}

		/**
		 * @return the diffCgst
		 */
		public String getDiffCgst() {
			return diffCgst;
		}

		/**
		 * @param diffCgst
		 *            the diffCgst to set
		 */
		public void setDiffCgst(String diffCgst) {
			this.diffCgst = diffCgst;
		}

		/**
		 * @return the diffSgst
		 */
		public String getDiffSgst() {
			return diffSgst;
		}

		/**
		 * @param diffSgst
		 *            the diffSgst to set
		 */
		public void setDiffSgst(String diffSgst) {
			this.diffSgst = diffSgst;
		}
	}
}
