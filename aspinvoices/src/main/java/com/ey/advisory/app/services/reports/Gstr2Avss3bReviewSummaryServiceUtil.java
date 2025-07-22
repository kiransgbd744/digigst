package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.CollectionUtils;

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
import com.ey.advisory.app.docs.dto.Gstr2AVss3bReviewSummaryScreenRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Siva
 *
 * 
 */

public class Gstr2Avss3bReviewSummaryServiceUtil {

	public static void downloadGstrVs3bReport(Workbook workbook, List<Gstr2AVss3bReviewSummaryScreenRespDto> dtoList,
			Gstr1VsGstr3bProcessSummaryReqDto request, String entityName) throws Exception {
		List<String> formulaList = buildSupplies();
		String fromTaxPeriod = request.getTaxPeriodFrom();
		String toTaxPeriod = request.getTaxPeriodTo();
		List<String> taxPeriodList = Lists.newLinkedList();
		LocalDate startDate = LocalDate.of(Integer.parseInt(fromTaxPeriod.substring(2)),
				Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
		LocalDate endDate = LocalDate.of(Integer.parseInt(toTaxPeriod.substring(2)),
				Integer.parseInt(toTaxPeriod.substring(0, 2)), 01);
		long numOfMonths = ChronoUnit.MONTHS.between(startDate, endDate) + 1;
		if (numOfMonths > 0) {
			List<LocalDate> listOfDates = Stream.iterate(startDate, date -> date.plusMonths(1)).limit(numOfMonths)
					.collect(Collectors.toList());
			// System.out.println(listOfDates);

			listOfDates.forEach(localDate -> taxPeriodList
					.add(localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() + "" + localDate.getYear()
							: localDate.getMonthValue() + "" + localDate.getYear()));

		}
		Map<String, String> suppliesMap = natureSuppliesMap();
		Worksheet worksheet = workbook.getWorksheets().get(0);
		worksheet.autoFitColumns();
		worksheet.autoFitRows();
		Cells cells = worksheet.getCells();
		worksheet.getCells().setStandardWidth(20.5f);
		String[][] dataArray = new String[70 + (dtoList.size() * 2)][(taxPeriodList.size() + 1) * 10];
		dataArray[1][2] = "Entity";
		applyStyles(1, 2, worksheet, 1, false, false, FontUnderlineType.NONE, null, false, false, false,false);
		dataArray[1][3] = entityName;
		applyStyles(1, 3, worksheet, 1, false, false, FontUnderlineType.NONE, null, false, false, false,false);
		dataArray[2][2] = "From Tax Period";
		applyStyles(2, 2, worksheet, 1, false, false, FontUnderlineType.NONE, null, false, false, false,false);
		dataArray[2][3] = startDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + " - " + startDate.getYear();
		applyStyles(2, 3, worksheet, 1, false, false, FontUnderlineType.NONE, null, false, false, false,false);
		dataArray[2][4] = "To Tax Period";
		applyStyles(2, 4, worksheet, 1, false, false, FontUnderlineType.NONE, null, false, false, false,false);
		dataArray[2][5] = endDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + " - " + endDate.getYear();
		applyStyles(2, 5, worksheet, 1, false, false, FontUnderlineType.NONE, null, false, false, false,false);
		// Adding headers
		AtomicInteger row = new AtomicInteger();
		AtomicInteger column = new AtomicInteger();
		row.set(0);
		column.set(0);

		AtomicInteger headerRow = new AtomicInteger();
		AtomicInteger headerColumn = new AtomicInteger();
		AtomicInteger startColumn = new AtomicInteger();
		AtomicInteger endColumn = new AtomicInteger();
		startColumn.set(1);
		endColumn.set(4);
		headerRow.set(4);
		headerColumn.set(4);

		if (!CollectionUtils.isEmpty(taxPeriodList)) {
			taxPeriodList.add(0, "Consolidated");
		}

		taxPeriodList.forEach(taxPeriod -> {
			cells.createRange(headerRow.get(), headerColumn.get(), startColumn.get(), endColumn.get()).merge();
			dataArray[headerRow.get()][headerColumn.get()] = taxPeriod;
			try {
				applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
						Color.getLightSteelBlue(), false, true, false,false);
				worksheet.getCells().get(headerRow.get(), headerColumn.get()).getMergedRange()
						.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.fromArgb(0));
				worksheet.getCells().get(headerRow.get(), headerColumn.get()).getMergedRange()
						.setOutlineBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.fromArgb(0));
				worksheet.getCells().get(headerRow.get(), headerColumn.get()).getMergedRange()
						.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.fromArgb(0));
				worksheet.getCells().get(headerRow.get(), headerColumn.get()).getMergedRange()
						.setOutlineBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.fromArgb(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			headerColumn.set(headerColumn.incrementAndGet() + 3);
		});

		headerRow.set(4);
		headerColumn.set(4);
		dataArray[5][1] = "GSTIN";
		applyStyles(5, 1, worksheet, 1, false, true, FontUnderlineType.NONE, Color.getLightGray(), false, true, false,false);
		dataArray[5][2] = "Description";
		applyStyles(5, 2, worksheet, 1, false, true, FontUnderlineType.NONE, Color.getLightGray(), false, true, false,false);
		dataArray[5][3] = "Table No";
		applyStyles(5, 3, worksheet, 1, false, true, FontUnderlineType.NONE, Color.getLightGray(), false, true, false,false);
		worksheet.autoFitColumns();
		worksheet.autoFitRows();
		taxPeriodList.forEach(taxPeriod -> {
			try {
				headerRow.getAndIncrement();
				dataArray[headerRow.get()][headerColumn.get()] = "IGST";
				applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
						Color.getLightGray(), false, true, false,false);
				dataArray[headerRow.get()][headerColumn.incrementAndGet()] = "CGST";
				applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
						Color.getLightGray(), false, true, false,false);
				dataArray[headerRow.get()][headerColumn.incrementAndGet()] = "SGST";
				applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
						Color.getLightGray(), false, true, false,false);
				dataArray[headerRow.get()][headerColumn.incrementAndGet()] = "Cess";
				applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
						Color.getLightGray(), false, true, false,false);
				
				headerColumn.incrementAndGet();
				headerRow.decrementAndGet();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Map<String, List<Gstr2AVss3bReviewSummaryScreenRespDto>> gstnMap = dtoList.stream()
				.collect(Collectors.groupingBy(Gstr2AVss3bReviewSummaryScreenRespDto::getGstin));
		fillTheDataWhichIsNotPresent(gstnMap, taxPeriodList, formulaList);
		calculateTheTaxPeriodData(gstnMap, taxPeriodList, formulaList);
		// Adding the rows
		AtomicInteger dataRow = new AtomicInteger();
		dataRow.set(6);
		AtomicInteger dataColumnRow = new AtomicInteger();
		dataColumnRow.set(1);

		gstnMap.keySet().forEach(gstin -> {
			List<Gstr2AVss3bReviewSummaryScreenRespDto> gstrData2 = gstnMap.get(gstin);
			formulaList.forEach(formula -> {
				try {
					dataArray[dataRow.get()][dataColumnRow.get()] = gstin;
					applyStyles(dataRow.get(), dataColumnRow.get(), worksheet, 1, false, false, FontUnderlineType.NONE,
							null, false, false, false,false);
					dataArray[dataRow.get()][dataColumnRow.incrementAndGet()] = suppliesMap.get(formula);
					applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false,false);
					Style style = worksheet.getCells().get(dataRow.get(), dataColumnRow.get()).getStyle();
					style.setTextWrapped(true);
					
					dataArray[dataRow.get()][dataColumnRow.incrementAndGet()] = formula;
					applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, true,false);
					List<Gstr2AVss3bReviewSummaryScreenRespDto> rowList = gstrData2.stream()
							.filter(gstrData1 -> gstrData1.getFormula().equals(formula)).collect(Collectors.toList());
					taxPeriodList.forEach(taxPeriod -> {
						Optional<Gstr2AVss3bReviewSummaryScreenRespDto> data = rowList.stream()
								.filter(gstrData1 -> gstrData1.getTaxPeriod().equals(taxPeriod)).findFirst();
						if (data.isPresent()) {
							Gstr2AVss3bReviewSummaryScreenRespDto data1 = data.get();
							dataArray[dataRow.get()][dataColumnRow.incrementAndGet()] = data1.getIgst();
							applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false,true);
							dataArray[dataRow.get()][dataColumnRow.incrementAndGet()] = data1.getCgst();
							applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false,true);
							dataArray[dataRow.get()][dataColumnRow.incrementAndGet()] = data1.getSgst();
							applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false,true);
							dataArray[dataRow.get()][dataColumnRow.incrementAndGet()] = data1.getCess();
							applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false,true);
							
							
						}
					});
					dataColumnRow.set(1);
					dataRow.incrementAndGet();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			dataColumnRow.set(1);
			dataRow.incrementAndGet();
		});

		AtomicInteger columnWidths = new AtomicInteger();
		columnWidths.set(0);
		cells.setColumnWidth(columnWidths.get(), 5);
		cells.setColumnWidth(columnWidths.incrementAndGet(), 15);
		cells.setColumnWidth(columnWidths.incrementAndGet(), 50);
		for (int i = 0; i < (taxPeriodList.size() * 4); i++) {
			try {
				cells.setColumnWidth(columnWidths.incrementAndGet(), 15);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		cells.importArray(dataArray, 0, 0);
	}

	private static void applyStylesByBold(int i, int j, Worksheet worksheet, String formula, boolean isAlign,boolean alignmentRight) {
		try {
			if (Lists.newArrayList("A", "B", "C=A-B").contains(formula)) {
				applyStyles(i, j, worksheet, 1, false, true, FontUnderlineType.NONE, null, false, isAlign, true,alignmentRight);
			} else {
				applyStyles(i, j, worksheet, 1, false, false, FontUnderlineType.NONE, null, false, isAlign, true,alignmentRight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void calculateTheTaxPeriodData(Map<String, List<Gstr2AVss3bReviewSummaryScreenRespDto>> gstnMap,
			List<String> taxPeriodList, List<String> formulaList) {
		gstnMap.keySet().forEach(gstin -> {
			List<Gstr2AVss3bReviewSummaryScreenRespDto> gstrData = gstnMap.get(gstin);
			formulaList.forEach(formula -> {
				List<Gstr2AVss3bReviewSummaryScreenRespDto> rowList = gstrData.stream()
						.filter(gstrData1 -> gstrData1.getFormula().equals(formula)).collect(Collectors.toList());
				calculateAndUpdateTotalonGstin(rowList);
			});
		});

	}

	private static void applyStyles(int row, int column, Worksheet worksheet, int borderStyle, boolean isItalic,
			boolean isBold, int isUderLine, Color color, boolean isFontColor, boolean isAlignment, boolean isWrap,boolean rightAlignment) throws Exception {
		Style style = worksheet.getCells().get(row, column).getStyle();
		style.setBorder(BorderType.LEFT_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.RIGHT_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.TOP_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.BOTTOM_BORDER, borderStyle, Color.getBlack());
		if (color != null) {
			style.setForegroundColor(color);
			style.setPattern(1);
		}
		if (isAlignment) {
			style.setHorizontalAlignment(TextAlignmentType.CENTER);
		}
		if(rightAlignment){
			style.setHorizontalAlignment(TextAlignmentType.RIGHT);
		}
		if(isWrap) {
			style.setTextWrapped(true);
		}

		Font font = style.getFont();
		font.setItalic(isItalic);
		font.setBold(isBold);
		font.setUnderline(isUderLine);
		font.setName("Calibri");
		font.setSize(10);
		if (isFontColor) {
			font.setColor(Color.getRed());
		}
		worksheet.autoFitColumns();
		worksheet.autoFitRows(true);
		worksheet.getCells().get(row, column).setStyle(style);
	}

	private static void calculateAndUpdateTotalonGstin(List<Gstr2AVss3bReviewSummaryScreenRespDto> rowList) {
		// final BigDecimal[] taxbleValue = { BigDecimal.ZERO };
		final BigDecimal[] igst = { BigDecimal.ZERO };
		final BigDecimal[] cgst = { BigDecimal.ZERO };
		final BigDecimal[] sgst = { BigDecimal.ZERO };
		final BigDecimal[] cess = { BigDecimal.ZERO };
		rowList.forEach(gstrData -> {
			if (!gstrData.getGstin().equalsIgnoreCase("Consolidated")) {
				
				igst[0] = gstrData.getIgst() != null ? igst[0].add(new BigDecimal(gstrData.getIgst())) : igst[0];
				cgst[0] = gstrData.getCgst() != null ? cgst[0].add(new BigDecimal(gstrData.getCgst())) : cgst[0];
				sgst[0] = gstrData.getSgst() != null ? sgst[0].add(new BigDecimal(gstrData.getSgst())) : sgst[0];
				cess[0] = gstrData.getCess() != null ? cess[0].add(new BigDecimal(gstrData.getCess())) : cess[0];
			}
		});

		rowList.forEach(gstrData -> {
			if (gstrData.getGstin().equalsIgnoreCase("Consolidated")) {
				gstrData.setIgst(igst[0].toPlainString());
				gstrData.setCgst(cgst[0].toPlainString());
				gstrData.setSgst(sgst[0].toPlainString());
				gstrData.setCess(cess[0].toPlainString());
			}
		});

	}

	private static void fillTheDataWhichIsNotPresent(Map<String, List<Gstr2AVss3bReviewSummaryScreenRespDto>> gstnMap,
			List<String> taxPeriodList, List<String> formulaList) {
		gstnMap.keySet().forEach(gstin -> {
			List<Gstr2AVss3bReviewSummaryScreenRespDto> gstrData = gstnMap.get(gstin);
			Map<String, List<Gstr2AVss3bReviewSummaryScreenRespDto>> taxPeriodMap = gstrData.stream()
					.collect(Collectors.groupingBy(Gstr2AVss3bReviewSummaryScreenRespDto::getTaxPeriod));
			taxPeriodList.forEach(taxPeriod -> {
				if (taxPeriod != null && !taxPeriod.equals("Consolidated")) {
					if (taxPeriodMap.containsKey(taxPeriod)) {
						List<Gstr2AVss3bReviewSummaryScreenRespDto> gstrDataList = taxPeriodMap.get(taxPeriod);
						formulaList.forEach(formula -> {
							Optional<Gstr2AVss3bReviewSummaryScreenRespDto> inFormulaList = gstrDataList.stream()
									.filter(gstrData1 -> gstrData1.getFormula().equals(formula)).findFirst();
							if (!inFormulaList.isPresent()) {
								gstrDataList.add(new Gstr2AVss3bReviewSummaryScreenRespDto(gstin, null, formula,
										taxPeriod, "0", "0", "0", "0"));
							}
						});
						taxPeriodMap.put(taxPeriod, gstrDataList);
					} else {
						List<Gstr2AVss3bReviewSummaryScreenRespDto> gstrDataList = Lists.newArrayList();
						formulaList.forEach(formula -> {
							gstrDataList.add(new Gstr2AVss3bReviewSummaryScreenRespDto(gstin, null, formula, taxPeriod,
									"0", "0", "0", "0"));
						});
						taxPeriodMap.put(taxPeriod, gstrDataList);
					}
				}
			});
			Collection<List<Gstr2AVss3bReviewSummaryScreenRespDto>> values = taxPeriodMap.values();
			List<Gstr2AVss3bReviewSummaryScreenRespDto> gstrDataList = Lists.newArrayList();
			values.forEach(gstrData1 -> gstrDataList.addAll(gstrData1));
			gstnMap.put(gstin, gstrDataList);
		});
		gstnMap.keySet().forEach(gstin -> {
			List<Gstr2AVss3bReviewSummaryScreenRespDto> gstrData = gstnMap.get(gstin);
			formulaList.forEach(formula -> {
				gstrData.add(new Gstr2AVss3bReviewSummaryScreenRespDto("Consolidated", null, formula, "Consolidated",
						"0", "0", "0", "0"));
			});
			gstnMap.put(gstin, gstrData);
		});

	}

	private static LinkedList<String> buildSupplies() {
		LinkedList<String> supplies = Lists.newLinkedList();
		supplies.add("A");
		supplies.add("A1");
		supplies.add("A2");
		supplies.add("A3");
		supplies.add("B");
		supplies.add("B1");
		supplies.add("B2");
		supplies.add("B3");
		supplies.add("B4");
		supplies.add("B5");
		supplies.add("B6");
		supplies.add("B7");
		supplies.add("B8");
		supplies.add("C=A-B");
		return supplies;
	}

	private static Map<String, String> natureSuppliesMap() {
		Map<String, String> natureSuppliesMap = Maps.newHashMap();
		natureSuppliesMap.put("A", "Taxes as per GSTR-2A ");
		natureSuppliesMap.put("A1",
				"Table 3 and 5* of GSTR-2A (* original invoice of RNV must "
				+ "be present in table 3 of GSTR-2A of previous months. "
				+ "Original document for RCR and RDR must be present in "
				+ "table 5 of GSTR-2A of previous months.)");
		  natureSuppliesMap.put("A2",
	                "Table 6 of GSTR-2A (ISD Credit eligible and in-eligible)");
	        natureSuppliesMap.put("A3",
	                "IMPG and IMPGSEZ of GSTR-2A");
		natureSuppliesMap.put("B", "Taxes as per GSTR-3B    ");
		natureSuppliesMap.put("B1", "Table 4A (1) of GSTR-3B (Import of Goods)  ");
		natureSuppliesMap.put("B2", "Table 4A (2) of GSTR-3B (Import of Services)   ");
		natureSuppliesMap.put("B3", "Table 4A (3) of GSTR-3B (Inward supplies liable to reverse charge) ");
		natureSuppliesMap.put("B4", "Table 4A (4) of GSTR-3B (Inward supplies from ISD) ");
		natureSuppliesMap.put("B5", "Table 4A (5) of GSTR-3B (All other ITC)    ");
		natureSuppliesMap.put("B6", "Table 4D of GSTR-3B (Ineligible ITC)   ");
		natureSuppliesMap.put("B7", "less: Table 3.1(d) of GSTR-3B (Inward supplies liable to reverse charge)   ");
		natureSuppliesMap.put("B8", "less: In-eligible credit on Import of Goods based on PR    ");
		natureSuppliesMap.put("C=A-B", "Difference ");
		return natureSuppliesMap;
	}

}
