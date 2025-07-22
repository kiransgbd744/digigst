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
import com.ey.advisory.app.docs.dto.Gstr1Vs3bReviewSummaryScreenRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Sasidhar Reddy
 *
 * 
 */

public class Gstr1vs3bReviewSummaryServiceUtil {

    public static void downloadGstrVs3bReport(Workbook workbook,
            List<Gstr1Vs3bReviewSummaryScreenRespDto> dtoList,
            Gstr1VsGstr3bProcessSummaryReqDto request, String entityName)
            throws Exception {
        List<String> formulaList = buildSupplies();

        String fromTaxPeriod = request.getTaxPeriodFrom();
        String toTaxPeriod = request.getTaxPeriodTo();
        List<String> taxPeriodList = Lists.newLinkedList();
        LocalDate startDate = LocalDate.of(
                Integer.parseInt(fromTaxPeriod.substring(2)),
                Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
        LocalDate endDate = LocalDate.of(
                Integer.parseInt(toTaxPeriod.substring(2)),
                Integer.parseInt(toTaxPeriod.substring(0, 2)), 01);
        long numOfMonths = ChronoUnit.MONTHS.between(startDate, endDate) + 1;
        if (numOfMonths > 0) {
            List<LocalDate> listOfDates = Stream
                    .iterate(startDate, date -> date.plusMonths(1))
                    .limit(numOfMonths).collect(Collectors.toList());
            // System.out.println(listOfDates);

            listOfDates.forEach(localDate -> taxPeriodList
                    .add(localDate.getMonthValue() < 10
                            ? "0" + localDate.getMonthValue() + ""
                                    + localDate.getYear()
                            : localDate.getMonthValue() + ""
                                    + localDate.getYear()));
            // System.out.println(taxPeriodList);
        }
		
        Map<String, String> suppliesMap = natureSuppliesMap();
        Worksheet worksheet = workbook.getWorksheets().get(0);
        worksheet.autoFitColumns();
        worksheet.autoFitRows();
        Cells cells = worksheet.getCells();
        worksheet.getCells().setStandardWidth(20.5f);
        String[][] dataArray = new String[70
                + (dtoList.size() * 2)][(taxPeriodList.size() + 1) * 10];
        dataArray[1][2] = "Entity";
        applyStyles(1, 2, worksheet, 1, false, false, FontUnderlineType.NONE,
                null, false, false,false);
        dataArray[1][3] = entityName;
        applyStyles(1, 3, worksheet, 1, false, false, FontUnderlineType.NONE,
                null, false, false,false);
        dataArray[2][2] = "From Tax Period";
        applyStyles(2, 2, worksheet, 1, false, false, FontUnderlineType.NONE,
                null, false, false,false);
        dataArray[2][3] = startDate.getMonth().getDisplayName(TextStyle.SHORT,
                Locale.US) + " - " + startDate.getYear();
        applyStyles(2, 3, worksheet, 1, false, false, FontUnderlineType.NONE,
                null, false, false,false);
        dataArray[2][4] = "To Tax Period";
        applyStyles(2, 4, worksheet, 1, false, false, FontUnderlineType.NONE,
                null, false, false,false);
        dataArray[2][5] = endDate.getMonth().getDisplayName(TextStyle.SHORT,
                Locale.US) + " - " + endDate.getYear();
        applyStyles(2, 5, worksheet, 1, false, false, FontUnderlineType.NONE,
                null, false, false,false);

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
        endColumn.set(5);
        headerRow.set(4);
        headerColumn.set(4);

        if (!CollectionUtils.isEmpty(taxPeriodList)) {
            taxPeriodList.add("Total");
        }

        taxPeriodList.forEach(taxPeriod -> {
            cells.createRange(headerRow.get(), headerColumn.get(),
                    startColumn.get(), endColumn.get()).merge();
            dataArray[headerRow.get()][headerColumn.get()] = taxPeriod;
            try {
				applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
						null, false, true,false);
			
			worksheet.getCells().get(headerRow.get(), headerColumn.get()).getMergedRange()
					.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(headerRow.get(), headerColumn.get()).getMergedRange()
					.setOutlineBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(headerRow.get(), headerColumn.get()).getMergedRange()
					.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(headerRow.get(), headerColumn.get()).getMergedRange()
					.setOutlineBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.fromArgb(0));
            headerColumn.set(headerColumn.incrementAndGet() + 4);
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            });

        headerRow.set(4);
        headerColumn.set(4);
        dataArray[5][1] = "GSTIN";
        applyStyles(5, 1, worksheet, 1, false, true, FontUnderlineType.NONE,
                null, false, false,false);
        dataArray[5][2] = "Nature of Supplies";
        applyStyles(5, 2, worksheet, 1, false, true, FontUnderlineType.NONE,
                null, false, false,false);
        dataArray[5][3] = "Table No";
        applyStyles(5, 3, worksheet, 1, false, true, FontUnderlineType.NONE,
                null, false, false,false);
        worksheet.autoFitColumns();
        worksheet.autoFitRows();
        taxPeriodList.forEach(taxPeriod -> {
            try {
                headerRow.getAndIncrement();
                dataArray[headerRow.get()][headerColumn
                        .get()] = "Taxable Value";
                applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1,
                        false, true, FontUnderlineType.NONE, null, false,
                        false,false);
                dataArray[headerRow.get()][headerColumn
                        .incrementAndGet()] = "IGST Amount";
                applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1,
                        false, true, FontUnderlineType.NONE, null, false,
                        false,false);
                dataArray[headerRow.get()][headerColumn
                        .incrementAndGet()] = "CGST Amount";
                applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1,
                        false, true, FontUnderlineType.NONE, null, false,
                        false,false);
                dataArray[headerRow.get()][headerColumn
                        .incrementAndGet()] = "SGST Amount";
                applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1,
                        false, true, FontUnderlineType.NONE, null, false,
                        false,false);
                dataArray[headerRow.get()][headerColumn
                        .incrementAndGet()] = "Cess Amount";
                applyStyles(headerRow.get(), headerColumn.get(), worksheet, 1,
                        false, true, FontUnderlineType.NONE, null, false,
                        false,false);
                headerColumn.incrementAndGet();
                headerRow.decrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Map<String, List<Gstr1Vs3bReviewSummaryScreenRespDto>> gstnMap = dtoList
                .stream().collect(Collectors.groupingBy(
                        Gstr1Vs3bReviewSummaryScreenRespDto::getGstin));
        // System.out.println(gstnMap);
        fillTheDataWhichIsNotPresent(gstnMap, taxPeriodList, formulaList);
        calculateTheTaxPeriodData(gstnMap, taxPeriodList, formulaList);
        // Adding the rows
        AtomicInteger dataRow = new AtomicInteger();
        dataRow.set(6);
        AtomicInteger dataColumnRow = new AtomicInteger();
        dataColumnRow.set(1);

        gstnMap.keySet().forEach(gstin -> {
            List<Gstr1Vs3bReviewSummaryScreenRespDto> gstrData2 = gstnMap
                    .get(gstin);
            formulaList.forEach(formula -> {
                try {
                    dataArray[dataRow.get()][dataColumnRow.get()] = gstin;
                    applyStyles(dataRow.get(), dataColumnRow.get(), worksheet, 1, false, false, FontUnderlineType.NONE,
							null, false, false, false);
                    dataArray[dataRow.get()][dataColumnRow
                            .incrementAndGet()] = suppliesMap.get(formula);
                    applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false);
					Style style = worksheet.getCells().get(dataRow.get(), dataColumnRow.get()).getStyle();
					style.setTextWrapped(true);
                    dataArray[dataRow.get()][dataColumnRow
                            .incrementAndGet()] = formula;
                    applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, true);
                    List<Gstr1Vs3bReviewSummaryScreenRespDto> rowList = gstrData2
                            .stream()
                            .filter(gstrData1 -> gstrData1.getFormula()
                                    .equals(formula))
                            .collect(Collectors.toList());
                    taxPeriodList.forEach(taxPeriod -> {
                        Optional<Gstr1Vs3bReviewSummaryScreenRespDto> data = rowList
                                .stream().filter(gstrData1 -> gstrData1
                                        .getTaxPeriod().equals(taxPeriod))
                                .findFirst();
                        if (data.isPresent()) {
                            try {
                                Gstr1Vs3bReviewSummaryScreenRespDto data1 = data
                                        .get();
                                dataArray[dataRow.get()][dataColumnRow
                                        .incrementAndGet()] = data1
                                                .getTaxableValue();
                                applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false);
                                dataArray[dataRow.get()][dataColumnRow
                                        .incrementAndGet()] = data1.getIgst();
                                applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false);
                                dataArray[dataRow.get()][dataColumnRow
                                        .incrementAndGet()] = data1.getCgst();
                                applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false);
                                dataArray[dataRow.get()][dataColumnRow
                                        .incrementAndGet()] = data1.getSgst();
                                applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false);
                                dataArray[dataRow.get()][dataColumnRow
                                        .incrementAndGet()] = data1.getCess();
                                applyStylesByBold(dataRow.get(), dataColumnRow.get(), worksheet, formula, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
		cells.setColumnWidth(columnWidths.incrementAndGet(), 20);
		cells.setColumnWidth(columnWidths.incrementAndGet(), 50);
		for (int i = 0; i < (taxPeriodList.size() * 6); i++) {
			try {
				cells.setColumnWidth(columnWidths.incrementAndGet(), 20);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

        cells.importArray(dataArray, 0, 0);
    }
    private static void applyStylesByBold(int i, int j, Worksheet worksheet, String formula, boolean isAlign) {
		try {
			if (Lists.newArrayList("A", "B", "C=A-B","K=G+J").contains(formula)) {
				applyStyles(i, j, worksheet, 1, false, true, FontUnderlineType.NONE, null, false, isAlign, true);
			} else {
				applyStyles(i, j, worksheet, 1, false, false, FontUnderlineType.NONE, null, false, isAlign, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
    private static void applyStyles(int row, int column, Worksheet worksheet,
            int borderStyle, boolean isItalic, boolean isBold, int isUderLine,
            Color color, boolean isFontColor, boolean isAlignment,boolean isWrap)
            throws Exception {
        Style style = worksheet.getCells().get(row, column).getStyle();
        style.setBorder(BorderType.LEFT_BORDER, borderStyle, Color.getBlack());
        style.setBorder(BorderType.RIGHT_BORDER, borderStyle, Color.getBlack());
        style.setBorder(BorderType.TOP_BORDER, borderStyle, Color.getBlack());
        style.setBorder(BorderType.BOTTOM_BORDER, borderStyle,Color.getBlack());
        if (color != null) {
            style.setForegroundColor(color);
            style.setPattern(1);
        }
        if (isAlignment) {
            style.setHorizontalAlignment(TextAlignmentType.CENTER);
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

    private static void calculateTheTaxPeriodData(
            Map<String, List<Gstr1Vs3bReviewSummaryScreenRespDto>> gstnMap,
            List<String> taxPeriodList, List<String> formulaList) {
        gstnMap.keySet().forEach(gstin -> {
            List<Gstr1Vs3bReviewSummaryScreenRespDto> gstrData = gstnMap
                    .get(gstin);
            formulaList.forEach(formula -> {
                List<Gstr1Vs3bReviewSummaryScreenRespDto> rowList = gstrData
                        .stream().filter(gstrData1 -> gstrData1.getFormula()
                                .equals(formula))
                        .collect(Collectors.toList());
                calculateAndUpdateTotalonGstin(rowList);
            });
        });

    }

    private static void calculateAndUpdateTotalonGstin(
            List<Gstr1Vs3bReviewSummaryScreenRespDto> rowList) {
        final BigDecimal[] taxbleValue = { BigDecimal.ZERO };
        final BigDecimal[] igst = { BigDecimal.ZERO };
        final BigDecimal[] cgst = { BigDecimal.ZERO };
        final BigDecimal[] sgst = { BigDecimal.ZERO };
        final BigDecimal[] cess = { BigDecimal.ZERO };
        rowList.forEach(gstrData -> {
            if (!gstrData.getGstin().equalsIgnoreCase("Total")) {
                taxbleValue[0] = gstrData.getTaxableValue() != null
                        ? taxbleValue[0]
                                .add(new BigDecimal(gstrData.getTaxableValue()))
                        : taxbleValue[0];
                igst[0] = gstrData.getIgst() != null
                        ? igst[0].add(new BigDecimal(gstrData.getIgst()))
                        : igst[0];
                cgst[0] = gstrData.getCgst() != null
                        ? cgst[0].add(new BigDecimal(gstrData.getCgst()))
                        : cgst[0];
                sgst[0] = gstrData.getSgst() != null
                        ? sgst[0].add(new BigDecimal(gstrData.getSgst()))
                        : sgst[0];
                cess[0] = gstrData.getCess() != null
                        ? cess[0].add(new BigDecimal(gstrData.getCess()))
                        : cess[0];
            }
        });

        rowList.forEach(gstrData -> {
            if (gstrData.getGstin().equalsIgnoreCase("Total")) {
                gstrData.setTaxableValue(taxbleValue[0].toPlainString());
                gstrData.setIgst(igst[0].toPlainString());
                gstrData.setCgst(cgst[0].toPlainString());
                gstrData.setSgst(sgst[0].toPlainString());
                gstrData.setCess(cess[0].toPlainString());
            }
        });

    }

    private static void fillTheDataWhichIsNotPresent(
            Map<String, List<Gstr1Vs3bReviewSummaryScreenRespDto>> gstnMap,
            List<String> taxPeriodList, List<String> formulaList) {
        gstnMap.keySet().forEach(gstin -> {
            List<Gstr1Vs3bReviewSummaryScreenRespDto> gstrData = gstnMap
                    .get(gstin);
            Map<String, List<Gstr1Vs3bReviewSummaryScreenRespDto>> taxPeriodMap = gstrData
                    .stream().collect(Collectors.groupingBy(
                            Gstr1Vs3bReviewSummaryScreenRespDto::getTaxPeriod));
            taxPeriodList.forEach(taxPeriod -> {
                if (taxPeriod != null && !taxPeriod.equals("Total")) {
                    if (taxPeriodMap.containsKey(taxPeriod)) {
                        List<Gstr1Vs3bReviewSummaryScreenRespDto> gstrDataList = taxPeriodMap
                                .get(taxPeriod);
                        formulaList.forEach(formula -> {
                            Optional<Gstr1Vs3bReviewSummaryScreenRespDto> inFormulaList = gstrDataList
                                    .stream().filter(gstrData1 -> gstrData1
                                            .getFormula().equals(formula))
                                    .findFirst();
                            if (!inFormulaList.isPresent()) {
                                gstrDataList
                                        .add(new Gstr1Vs3bReviewSummaryScreenRespDto(
                                                gstin, null, formula, taxPeriod,
                                                "0", "0", "0", "0",
                                                "0"));
                            }
                        });
                        taxPeriodMap.put(taxPeriod, gstrDataList);
                    } else {
                        List<Gstr1Vs3bReviewSummaryScreenRespDto> gstrDataList = Lists
                                .newArrayList();
                        formulaList.forEach(formula -> {
                            gstrDataList
                                    .add(new Gstr1Vs3bReviewSummaryScreenRespDto(
                                            gstin, null, formula, taxPeriod,
                                            "0", "0", "0", "0",
                                            "0"));
                        });
                        taxPeriodMap.put(taxPeriod, gstrDataList);
                    }
                }
            });
            Collection<List<Gstr1Vs3bReviewSummaryScreenRespDto>> values = taxPeriodMap
                    .values();
            List<Gstr1Vs3bReviewSummaryScreenRespDto> gstrDataList = Lists
                    .newArrayList();
            values.forEach(gstrData1 -> gstrDataList.addAll(gstrData1));
            gstnMap.put(gstin, gstrDataList);
        });
        gstnMap.keySet().forEach(gstin -> {
            List<Gstr1Vs3bReviewSummaryScreenRespDto> gstrData = gstnMap
                    .get(gstin);
            formulaList.forEach(formula -> {
                gstrData.add(new Gstr1Vs3bReviewSummaryScreenRespDto("Total",
                        null, formula, "Total", "0", "0", "0", "0",
                        "0"));
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
        supplies.add("B8-B9");
        supplies.add("B10");
        supplies.add("B11");
        supplies.add("B12");
        supplies.add("B13");
        supplies.add("B14");
        supplies.add("B15");
        supplies.add("B16-B17");
        supplies.add("B18");
		supplies.add("B19");
        supplies.add("C=A-B");
        supplies.add("D1");
        supplies.add("D2");
        supplies.add("E1");
		supplies.add("E2");
		supplies.add("E3");
        supplies.add("F=D1+D2-E");
        supplies.add("G=C+F");
        supplies.add("H");
        supplies.add("I");
        supplies.add("J=H-I");
        supplies.add("K=G+J");

        return supplies;
    }

    private static Map<String, String> natureSuppliesMap() {
        Map<String, String> natureSuppliesMap = Maps.newHashMap();
        natureSuppliesMap.put("A", "Table 3.1 (a), 3.1 (b) & 3.1.1.a as per GSTR - 3B");
        natureSuppliesMap.put("A1",
                "Table 3.1(a) Outward Taxable Supplies (other than zero rated, nil rated and exempted)");
        natureSuppliesMap.put("A2",
                "Table 3.1(b) Outward taxable supplies (zero rated)");
        natureSuppliesMap.put("A3",
				"Table 3.1.1.a Taxable supplies on which electronic commerce operator pays tax u/s 9(5)");
        natureSuppliesMap.put("B", "Details of supplies reported in GSTR - 1");
        natureSuppliesMap.put("B1",
                "Supplies made to registered person on forward charge (B2B - 4A, 6B, 6C)");
        natureSuppliesMap.put("B2",
                "Supplies made to registered person on reverse charge  (B2B - 4B)");
        natureSuppliesMap.put("B3",
                "Supplies made to unregistered person (B2CL - 5)");
        natureSuppliesMap.put("B4",
                "Supplies made to other unregistered person (B2CS - 7)");
        natureSuppliesMap.put("B5", "Exports (6A)");
        natureSuppliesMap.put("B6",
                "CR/DR issued against supplies reported in B1 & B2 (CDNR - 9B)");
        natureSuppliesMap.put("B7",
                "CR/DR issued against supplies reported in B3 & B5 (CDNUR - 9B)");
        natureSuppliesMap.put("B8-B9",
                "Advance Received Less Advance Adjusted (11)");
        natureSuppliesMap.put("B10",
                "Amendment of supplies reported in B1 & B2 of previous tax periods (B2BA - 9A)");
        natureSuppliesMap.put("B11",
                "Amendment of supplies reported in B3 of previous tax periods (B2CLA - 9A)");
        natureSuppliesMap.put("B12",
                "Amendment of supplies reported in B4 of previous tax periods (B2CSA - 10)");
        natureSuppliesMap.put("B13",
                "Amendment of supplies reported in B5 of previous tax periods (EXPA - 9A)");
        natureSuppliesMap.put("B14",
                "Amendment of supplies reported in B6 of previous tax periods (CDNRA - 9C)");
        natureSuppliesMap.put("B15",
                "Amendment of supplies reported in B7 of previous tax periods (CDNURA - 9C)");
        natureSuppliesMap.put("B16-B17",
                "Amendment of advance received reported in B8 of previous "
                        + "tax periods Less Amendment of advance adjusted reported in"
                        + " B9 of previous tax periods (11)");
        natureSuppliesMap.put("B18",
				"Supplies made to other unregistered person (ECOM - 15)");
		natureSuppliesMap.put("B19",
				"Amendment of Supplies reported in B18 of Previous tax periods (ECOMA-15)");
        natureSuppliesMap.put("C=A-B",
                "Difference of Table 3.1 (a),3.1 (b) & 3.1.1.a and GSTR-1");
        natureSuppliesMap.put("D1",
                "Table 3.1 (c) Other outward supplies (Nil rated, exempted) - GSTR - 3B");
        natureSuppliesMap.put("D2",
				"Table 3.1.1.b Taxable supplies made by a registered person through an electronic commerce operator,"
				+ "on which electronic commerce operator is required to pay tax u/s 9(5)");
        natureSuppliesMap.put("E1",
				"Table 8 - Details of NIL Rated & Exempt supplies reported in GSTR - 1 & 1A");
		natureSuppliesMap.put("E2",
				"Taxable supplies on which electronic commerce operator pays tax u/s 9(5) (SUPECOM - 14(ii))");
		natureSuppliesMap.put("E3",
				"Amendment of Supplies reported in E2 of Previous tax periods (SUPECOM-14A(ii))");
        natureSuppliesMap.put("F=D1+D2-E",
                "Difference of Table 3.1 (c) , 3.1.1.b and GSTR-1");
        natureSuppliesMap.put("G=C+F", "Difference excluding NON-GST Supplies");
        natureSuppliesMap.put("H",
                "Table 3.1 (e) Non-GST outward supplies - GSTR - 3B");
        natureSuppliesMap.put("I",
                "Table 8 - Details of NON-GST Supplies reported in GSTR - 1");
        natureSuppliesMap.put("J=H-I",
                "Difference of Table 3.1 (e) and GSTR-1");
        natureSuppliesMap.put("K=G+J",
                "Net Difference of GSTR - 1 and GSTR - 3B");
        return natureSuppliesMap;
    }

}
