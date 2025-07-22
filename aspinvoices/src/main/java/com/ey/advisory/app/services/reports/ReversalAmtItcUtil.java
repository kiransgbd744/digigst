/**
 * 
 */
package com.ey.advisory.app.services.reports;

import static com.google.common.collect.Maps.newLinkedHashMap;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
import com.ey.advisory.app.data.views.client.ComptReversalAmtDto;
import com.ey.advisory.common.AppException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jcraft.jsch.Logger;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Slf4j
public class ReversalAmtItcUtil {

	private static final List<String> particularsList = Lists.newArrayList(
			"Total Tax Amount (As per PR for Input Services and Input Goods)",
			"empty2", "Common Credit for Input Goods (IG) - A",
			"Common Credit for Input Services (IS) - B",
			"Total Common Credit (C = A+B)", "empty3",
			"Less: Ineligible credit based on ITC Reversal Idnefier - D",
			"T1: Used exclusively for the purposes other than business",
			"T2: Used exclusively for effecting exempt supplies",
			"T3: Credit is not available under sub-section (5) of section 17",
			"T4: Used exclusively for effecting supplies other than exempted but including zero rated supplies",
			"empty4", "Net Eligible Common Credit (E = C-D) denoted as C2",
			"empty5",
			"Ratio 1: Reversal Percentage (Refer Computation of Turnover)",
			"Ratio 2: Reversal Percentage (Refer Computation of Turnover)",
			"Ratio 3: Reversal Percentage (Refer Computation of Turnover)",
			"Ratio 1: Reversal Amount as per Rule 42 =D1 (C2 * Ratio 1)",
			"Ratio 2: Reversal Amount as per Rule 42= D1 (C2 * Ratio 2)",
			"Ratio 3: Reversal Amount as per Rule 42= D1 (C2 * Ratio3)",
			"Credit used partly for business and partly for non-business purposes =D2 (C2*5%)",
			"Total Reversal As Per Ratio 1 (D1 + D2)",
			"Total Reversal As Per Ratio 2 (D1 + D2)",
			"Total Reversal As Per Ratio 3 (D1 + D2)");

	private static final List<String> boldParticularsList = Lists.newArrayList(
			"Total Tax Amount (As per PR for Input Services and Input Goods)",
			"Common Credit for Input Goods (IG) - A",
			"Common Credit for Input Services (IS) - B",
			"Total Common Credit (C = A+B)",
			"Total Reversal As Per Ratio 1 (D1 + D2)",
			"Total Reversal As Per Ratio 2 (D1 + D2)",
			"Total Reversal As Per Ratio 3 (D1 + D2)");

	private static final List<String> backGroundList = Lists.newArrayList(
			"Total Reversal As Per Ratio 1 (D1 + D2)",
			"Total Reversal As Per Ratio 2 (D1 + D2)",
			"Total Reversal As Per Ratio 3 (D1 + D2)");

	/**
	 * @param workbook
	 * @param computeDtoList
	 */
	public static void prepareDataForComputeAmtItcReport(Workbook workbook,
			List<ComptReversalAmtDto> productDtos, String entityName,
			String taxPeriod) {
		try {
			List<ComptReversalAmtDto> gstinProdTaxDtos = productDtos;
			Collections.sort(gstinProdTaxDtos,
					Comparator.comparing(ComptReversalAmtDto::getGstin));
			Map<String, List<ComptReversalAmtDto>> datListMap = gstinProdTaxDtos
					.stream().collect(
							Collectors.groupingBy(ComptReversalAmtDto::getGstin,
									LinkedHashMap::new, Collectors.toList()));
			Worksheet worksheet = workbook.getWorksheets().get(0);
			worksheet.autoFitColumns();
			String[][] dataArray = new String[70 + gstinProdTaxDtos.size()][100
					+ gstinProdTaxDtos.size()];
			applyStyles(0, 0, worksheet, 0, false, false,
					FontUnderlineType.NONE, null, false, false);
			dataArray[1][1] = "Legal Entity:";
			applyStyles(1, 1, worksheet, 1, true, true, FontUnderlineType.NONE,
					null, false, false);
			dataArray[2][1] = "Tax Period:";
			applyStyles(2, 1, worksheet, 1, true, true, FontUnderlineType.NONE,
					null, false, false);

			dataArray[1][2] = entityName;
			applyStyles(1, 2, worksheet, 1, false, false,
					FontUnderlineType.NONE, null, false, false);
			dataArray[2][2] = taxPeriod;
			applyStyles(2, 2, worksheet, 1, false, false,
					FontUnderlineType.NONE, null, false, false);

			dataArray[4][1] = "Particulars";
			applyStyles(4, 1, worksheet, 1, false, true, FontUnderlineType.NONE,
					Color.getLightSteelBlue(), false, false);

			dataArray[5][1] = null;
			applyStyles(5, 1, worksheet, 1, false, true, FontUnderlineType.NONE,
					null, false, false);

			// Adding headers
			AtomicInteger row = new AtomicInteger();
			AtomicInteger column = new AtomicInteger();
			row.set(5);
			column.set(2);

			AtomicInteger headerRow = new AtomicInteger();
			AtomicInteger headerColumn = new AtomicInteger();
			AtomicInteger startColumn = new AtomicInteger();
			AtomicInteger endColumn = new AtomicInteger();
			startColumn.set(1);
			endColumn.set(5);
			headerRow.set(4);
			headerColumn.set(2);

			datListMap.put("Total", Lists.newArrayList());
			datListMap.keySet().forEach(gstin -> {
				try {
					worksheet.getCells().merge(headerRow.get(),
							headerColumn.get(), startColumn.get(),
							endColumn.get());
					dataArray[headerRow.get()][headerColumn.get()] = "Amount - "
							+ gstin;
					applyStyles(headerRow.get(), headerColumn.get(), worksheet,
							1, false, true, FontUnderlineType.NONE,
							Color.getLightSteelBlue(), false, true);
					worksheet.getCells()
							.get(headerRow.get(), headerColumn.get())
							.getMergedRange()
							.setOutlineBorder(BorderType.TOP_BORDER,
									CellBorderType.THIN, Color.fromArgb(0));
					worksheet.getCells()
							.get(headerRow.get(), headerColumn.get())
							.getMergedRange()
							.setOutlineBorder(BorderType.RIGHT_BORDER,
									CellBorderType.THIN, Color.fromArgb(0));
					worksheet.getCells()
							.get(headerRow.get(), headerColumn.get())
							.getMergedRange()
							.setOutlineBorder(BorderType.BOTTOM_BORDER,
									CellBorderType.THIN, Color.fromArgb(0));
					worksheet.getCells()
							.get(headerRow.get(), headerColumn.get())
							.getMergedRange()
							.setOutlineBorder(BorderType.LEFT_BORDER,
									CellBorderType.THIN, Color.fromArgb(0));
					headerColumn.set(headerColumn.incrementAndGet() + 4);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			// Adding the supplies rows
			AtomicInteger supplRow = new AtomicInteger();
			supplRow.set(6);
			AtomicInteger supplColumnRow = new AtomicInteger();
			supplColumnRow.set(1);

			particularsList.forEach(particular -> {
				try {
					dataArray[supplRow.get()][supplColumnRow.get()] = particular
							.startsWith("empty") ? null : particular;
					if (isPresentInList(particular, boldParticularsList)) {
						applyStyles(supplRow.get(), supplColumnRow.get(),
								worksheet, 1, false, true,
								FontUnderlineType.NONE, null, false, false);
					} else {
						applyStyles(supplRow.get(), supplColumnRow.get(),
								worksheet, 1, false, false,
								FontUnderlineType.NONE, null, false, false);
					}
					supplRow.incrementAndGet();
				} catch (Exception e) {
					String errMsg = String.format(
							"Exception occurred while creating the Reversal Amt Excel for Entity Name %s and TaxPeriod %s",
							entityName, taxPeriod);
					LOGGER.error(errMsg, e);
					throw new AppException(errMsg);
				}
			});

			supplRow.set(4);
			supplColumnRow.set(2);

			datListMap.keySet().forEach(gstin -> {
				try {
					supplRow.incrementAndGet();
					dataArray[supplRow.get()][supplColumnRow
							.get()] = "Total Tax";
					applyStyles(supplRow.get(), supplColumnRow.get(), worksheet,
							1, false, true, FontUnderlineType.NONE,
							Color.getLightGray(), false, true);
					dataArray[supplRow.get()][supplColumnRow
							.incrementAndGet()] = "IGST";
					applyStyles(supplRow.get(), supplColumnRow.get(), worksheet,
							1, false, true, FontUnderlineType.NONE,
							Color.getLightGray(), false, true);
					dataArray[supplRow.get()][supplColumnRow
							.incrementAndGet()] = "CGST";
					applyStyles(supplRow.get(), supplColumnRow.get(), worksheet,
							1, false, true, FontUnderlineType.NONE,
							Color.getLightGray(), false, true);
					dataArray[supplRow.get()][supplColumnRow
							.incrementAndGet()] = "SGST";
					applyStyles(supplRow.get(), supplColumnRow.get(), worksheet,
							1, false, true, FontUnderlineType.NONE,
							Color.getLightGray(), false, true);
					dataArray[supplRow.get()][supplColumnRow
							.incrementAndGet()] = "Cess";
					applyStyles(supplRow.get(), supplColumnRow.get(), worksheet,
							1, false, true, FontUnderlineType.NONE,
							Color.getLightGray(), false, true);
					worksheet.getCells().setColumnWidth(supplColumnRow.get(),
							6);
					supplColumnRow.incrementAndGet();
					supplRow.decrementAndGet();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			// Adding the rows
			AtomicInteger dataRow = new AtomicInteger();
			dataRow.set(6);
			AtomicInteger dataColumnRow = new AtomicInteger();
			dataColumnRow.set(2);
			// sectionName
			Map<String, List<ComptReversalAmtDto>> calculatedMap = calculatedTotalGstins(
					datListMap, gstinProdTaxDtos);
			// System.out.println(calculatedMap);
			List<String> suppliesList = Lists
					.newArrayList(buildSuppliersHashMap().values());
			// System.out.println(suppliesList);
			suppliesList.forEach(sectionName -> {
				List<ComptReversalAmtDto> productDtoList = calculatedMap
						.get(sectionName);
				try {
					if (!CollectionUtils.isEmpty(productDtoList)) {
						productDtoList.forEach(productDto -> {
							try {
								dataArray[dataRow.get()][dataColumnRow
										.get()] = productDto.getTotalTax();
								applyStyles(dataRow.get(), dataColumnRow.get(),
										worksheet, 1, false, false,
										FontUnderlineType.NONE, null, false,
										true);
								dataArray[dataRow.get()][dataColumnRow
										.incrementAndGet()] = productDto
												.getIgst();
								applyStyles(dataRow.get(), dataColumnRow.get(),
										worksheet, 1, false, false,
										FontUnderlineType.NONE, null, false,
										true);
								dataArray[dataRow.get()][dataColumnRow
										.incrementAndGet()] = productDto
												.getCgst();
								applyStyles(dataRow.get(), dataColumnRow.get(),
										worksheet, 1, false, false,
										FontUnderlineType.NONE, null, false,
										true);
								dataArray[dataRow.get()][dataColumnRow
										.incrementAndGet()] = productDto
												.getSgst();
								applyStyles(dataRow.get(), dataColumnRow.get(),
										worksheet, 1, false, false,
										FontUnderlineType.NONE, null, false,
										true);
								dataArray[dataRow.get()][dataColumnRow
										.incrementAndGet()] = productDto
												.getCess();
								applyStyles(dataRow.get(), dataColumnRow.get(),
										worksheet, 1, false, false,
										FontUnderlineType.NONE, null, false,
										true);
								dataColumnRow.incrementAndGet();
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				dataColumnRow.set(2);
				dataRow.incrementAndGet();
			});

			dataRow.set(6);
			dataColumnRow.set(2);

			int count = datListMap.keySet().size() * 5;
			for (int i = 0; i < count; i++) {
				try {
					fillTheRemainingData(particularsList, dataRow,
							dataColumnRow, worksheet);
					dataColumnRow.incrementAndGet();
					dataRow.set(6);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			supplRow.set(6);
			supplColumnRow.set(1);
			particularsList.forEach(particlar -> {
				if (isPresentInList(particlar, backGroundList)) {
					try {
						applyStyles(supplRow.get(), supplColumnRow.get(),
								worksheet, 1, false, true, 0,
								Color.getLightYellow(), false, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				supplRow.incrementAndGet();
			});

			AtomicInteger columnWidths = new AtomicInteger();
			columnWidths.set(0);
			Cells cells = worksheet.getCells();
			cells.setColumnWidth(columnWidths.get(), 5);
			cells.setColumnWidth(columnWidths.incrementAndGet(), 90);
			cells.setColumnWidth(columnWidths.incrementAndGet(), 20);
			for (int i = 0; i < count; i++) {
				try {
					cells.setColumnWidth(columnWidths.incrementAndGet(), 10);
				} catch (Exception e) {
					String errMsg = String.format(
							"Exception occurred while creating the Reversal Amt Excel for Entity Name %s and TaxPeriod %s",
							entityName, taxPeriod);
					LOGGER.error(errMsg, e);
					throw new AppException(errMsg);
				}
			}
			cells.importArray(dataArray, 0, 0);
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception occurred while creating the Reversal Amt Excel for Entity Name %s and TaxPeriod %s",
					entityName, taxPeriod);
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	private static void fillTheRemainingData(List<String> particularsList,
			AtomicInteger supplRow, AtomicInteger supplColumnRow,
			Worksheet worksheet) {
		particularsList.forEach(particlar -> {
			if (isPresentInList(particlar, backGroundList)) {
				try {
					applyStyles(supplRow.get(), supplColumnRow.get(), worksheet,
							1, false, false, 0, Color.getLightYellow(), false,
							true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Object value = worksheet.getCells()
					.get(supplRow.get(), supplColumnRow.get()).getValue();
			if (value == null) {
				try {
					applyStyles(supplRow.get(), supplColumnRow.get(), worksheet,
							1, false, false, 0, null, false, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			supplRow.incrementAndGet();
		});
	}

	private static boolean isPresentInList(String suppl,
			List<String> stringList) {
		List<String> strings = stringList.stream()
				.filter(supplStr -> supplStr.equals(suppl))
				.collect(Collectors.toList());
		return strings.size() > 0;
	}

	private static Map<String, List<ComptReversalAmtDto>> calculatedTotalGstins(
			Map<String, List<ComptReversalAmtDto>> datListMap,
			List<ComptReversalAmtDto> gstinProdTaxDtos) {
		// System.out.println(datListMap);
		List<ComptReversalAmtDto> missingUpdatedDtos = Lists.newArrayList();
		Collections.sort(gstinProdTaxDtos,
				Comparator.comparing(ComptReversalAmtDto::getSubsectionName));
		Map<String, List<ComptReversalAmtDto>> dataListMap = gstinProdTaxDtos
				.stream().collect(Collectors
						.groupingBy(ComptReversalAmtDto::getSubsectionName));
		// System.out.println("------------------------");
		dataListMap.keySet().forEach(sectionName -> {
			List<String> gstinKeys = Lists.newArrayList(datListMap.keySet());
			// System.out.println(sectionName);
			List<ComptReversalAmtDto> productDtoList = dataListMap
					.get(sectionName);
			missingUpdatedDtos.addAll(productDtoList);
			List<String> dbGstins = productDtoList.stream()
					.map(ComptReversalAmtDto::getGstin)
					.collect(Collectors.toList());
			gstinKeys.removeAll(dbGstins);
			// System.out.println("-------gstin-----" + gstinKeys);
			updateMissingDtos(gstinKeys, missingUpdatedDtos, sectionName);

		});
		Map<String, List<ComptReversalAmtDto>> calculatedMap = missingUpdatedDtos
				.stream().collect(Collectors
						.groupingBy(ComptReversalAmtDto::getSubsectionName));
		calculatedMap.keySet().forEach(sectionName -> {
			List<ComptReversalAmtDto> productDtoList = calculatedMap
					.get(sectionName);
			calculateAndUpdateTotalonGstin(productDtoList);
		});
		return orderByGstin(calculatedMap, datListMap);
	}

	private static Map<String, List<ComptReversalAmtDto>> orderByGstin(
			Map<String, List<ComptReversalAmtDto>> calculatedMap,
			Map<String, List<ComptReversalAmtDto>> datListMap) {
		Map<String, List<ComptReversalAmtDto>> listMap = Maps
				.newLinkedHashMap();
		calculatedMap.keySet().forEach(sectionName -> {
			List<ComptReversalAmtDto> productDtoList = calculatedMap
					.get(sectionName);
			datListMap.keySet().forEach(gstin -> {
				if (listMap.containsKey(sectionName)) {
					List<ComptReversalAmtDto> dtos = listMap.get(sectionName);
					List<ComptReversalAmtDto> productDtos = productDtoList
							.stream()
							.filter(productDto -> productDto.getGstin()
									.equals(gstin))
							.collect(Collectors.toList());
					dtos.add(productDtos.stream().findFirst().get());
					listMap.put(sectionName, dtos);
				} else {
					List<ComptReversalAmtDto> dtos = Lists.newLinkedList();
					List<ComptReversalAmtDto> productDtos = productDtoList
							.stream()
							.filter(productDto -> productDto.getGstin()
									.equals(gstin))
							.collect(Collectors.toList());
					dtos.add(productDtos.stream().findFirst().get());
					listMap.put(sectionName, dtos);
				}
			});
		});
		// System.out.println(listMap);
		return listMap;
	}

	private static void calculateAndUpdateTotalonGstin(
			List<ComptReversalAmtDto> productDtoList) {
		final BigDecimal[] taxbleValue = { BigDecimal.ZERO };
		final BigDecimal[] igst = { BigDecimal.ZERO };
		final BigDecimal[] cgst = { BigDecimal.ZERO };
		final BigDecimal[] sgst = { BigDecimal.ZERO };
		final BigDecimal[] cess = { BigDecimal.ZERO };
		productDtoList.forEach(productDto -> {
			if (!productDto.getGstin().equalsIgnoreCase("Total")) {
				taxbleValue[0] = productDto.getTotalTax() != null
						? taxbleValue[0]
								.add(new BigDecimal(productDto.getTotalTax()))
						: taxbleValue[0];
				igst[0] = productDto.getIgst() != null
						? igst[0].add(new BigDecimal(productDto.getIgst()))
						: igst[0];
				cgst[0] = productDto.getCgst() != null
						? cgst[0].add(new BigDecimal(productDto.getCgst()))
						: cgst[0];
				sgst[0] = productDto.getSgst() != null
						? sgst[0].add(new BigDecimal(productDto.getSgst()))
						: sgst[0];
				cess[0] = productDto.getCess() != null
						? cess[0].add(new BigDecimal(productDto.getCess()))
						: cess[0];
			}
		});
		productDtoList.forEach(productDto -> {
			if (productDto.getGstin().equalsIgnoreCase("Total")) {
				productDto.setTotalTax(taxbleValue[0].toPlainString());
				productDto.setIgst(igst[0].toPlainString());
				productDto.setCgst(cgst[0].toPlainString());
				productDto.setSgst(sgst[0].toPlainString());
				productDto.setCess(cess[0].toPlainString());
			}
		});
	}

	private static void updateMissingDtos(List<String> gstinKeys,
			List<ComptReversalAmtDto> missingUpdatedDtos, String sectionName) {
		gstinKeys.forEach(gstin -> {
			missingUpdatedDtos.add(new ComptReversalAmtDto(null, sectionName,
					gstin, null, null, null, null, null));
		});
	}

	private static void applyStyles(int row, int column, Worksheet worksheet,
			int borderStyle, boolean isItalic, boolean isBold, int isUderLine,
			Color color, boolean isFontColor, boolean isAlignment)
			throws Exception {
		Style style = worksheet.getCells().get(row, column).getStyle();
		style.setBorder(BorderType.LEFT_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.RIGHT_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.TOP_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.BOTTOM_BORDER, borderStyle,
				Color.getBlack());
		if (color != null) {
			style.setForegroundColor(color);
			style.setPattern(1);
		}
		if (isAlignment) {
			style.setHorizontalAlignment(TextAlignmentType.CENTER);
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

	private static Map<String, String> buildSuppliersHashMap() {
		Map<String, String> integerStringMap = newLinkedHashMap();
		integerStringMap.put("2", "Total Tax Amount");
		integerStringMap.put("3", "empty");
		integerStringMap.put("4", "A");
		integerStringMap.put("5", "B");
		integerStringMap.put("6", "C");
		integerStringMap.put("7", "empty");
		integerStringMap.put("8", "empty");
		integerStringMap.put("9", "D_T1");
		integerStringMap.put("10", "D_T2");
		integerStringMap.put("11", "D_T3");
		integerStringMap.put("12", "D_T4");
		integerStringMap.put("13", "empty");
		integerStringMap.put("14", "E");
		integerStringMap.put("15", "empty");
		integerStringMap.put("16", "ITC Reversal Ratio1");
		integerStringMap.put("17", "ITC Reversal Ratio2");
		integerStringMap.put("18", "ITC Reversal Ratio3");
		integerStringMap.put("19", "Ratio_1_R42");
		integerStringMap.put("20", "Ratio_2_R42");
		integerStringMap.put("21", "Ratio_3_R42");
		integerStringMap.put("22", "D2");
		integerStringMap.put("23", "Total Reversal_RATIO_1");
		integerStringMap.put("24", "Total Reversal_RATIO_2");
		integerStringMap.put("25", "Total Reversal_RATIO_3");

		return integerStringMap;
	}
}
