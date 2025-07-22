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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.aspose.cells.BorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.FontUnderlineType;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.views.client.ReversalComputeDto;
import com.ey.advisory.common.AppException;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Slf4j
public class ReversalComputeItcUtil {

	/**
	 * @param workbook
	 * @param computeDtoList
	 */
	private static final List<String> underLinesList = Lists.newArrayList(
			"Exempt Supplies",
			"Memo Information (Outward Reverse Charge Supplies)",
			"Other Memo Information (may impact ITC)", "ITC Reversal Ratio");

	private static final List<String> redTextList = Lists.newArrayList(
			"DTA ( SEZ to DTA )", "Less :  (Explanation to Rule 42)  Note 6",
			"DTA ( SEZ to DTA ) : Reverse Charge", "DXP : Reverse Charge",
			"SEZ (DTA to SEZ) : Reverse Charge",
			"- Explanation to Rule 42    (10)");

	private static final List<String> suppliesList = Lists.newArrayList(
			"Taxable (Normal)", "Taxable (Normal) B2CS Vertical Upload",
			"SEZ ( DTA to SEZ )", "DXP", "EXPT", "EXPWT", "DTA ( SEZ to DTA )",
			"Exempt Supplies", "Exempt", "Nil Rated",
			"Nil Rated - B2CS vertical Upload", "Non GST /SCH3",
			"Less :  (Explanation to Rule 42)  Note 6", "Total GSTIN Supplies",
			"Memo Information (Outward Reverse Charge Supplies)", "",
			"Taxable (Normal)- Reverse Charge",
			"DTA ( SEZ to DTA ) : Reverse Charge", "DXP : Reverse Charge",
			"SEZ (DTA to SEZ) : Reverse Charge", "",
			"Total Reverse Charge Supplies", "",
			"Other Memo Information (may impact ITC)", "", "NSY", "",
			"Grand Total for Controls ", "", "ITC Reversal Ratio", "",
			"Ratio 1", "- Exempt  (7)", "- Nil Rated  (8)",
			"- Outward Reverse Charge Supplies  (16)",
			"- Explanation to Rule 42    (10)", "",
			"- Total GSTIN Supplies  (11-9)", "Ratio 1 (%)", "", "", "Ratio 2",
			"- Exempt  (7)", "- Nil Rated  (8)", "- NON GST   (9)",
			"-NSY- Sec 17(5)    (17)",
			"- Outward Reverse Charge Supplies  (16)",
			"- Explanation to Rule 42    (10)", "",
			"- Total GSTIN Supplies   (11)", "Ratio 2 (%)", "", "", "Ratio 3",
			"- Exempt  (7)", "- Nil Rated  (8)", "- NON GST   (9)",
			"- Outward Reverse Charge Supplies  (16)",
			"- Explanation to Rule 42    (10)", "",
			"- Total GSTIN Supplies   (11)", "Ratio 3 (%)");

	public static void prepareDataForComputeItcReport(Workbook workbook,
			List<ReversalComputeDto> computeDtoList, String entityName,
			String taxPeriod) {
		List<ReversalComputeDto> gstinProdDtos = computeDtoList;

		try {
			Collections.sort(gstinProdDtos,
					Comparator.comparing(ReversalComputeDto::getGstin));
			Map<String, List<ReversalComputeDto>> datListMap = gstinProdDtos
					.stream()
					.collect(Collectors.groupingBy(ReversalComputeDto::getGstin,
							LinkedHashMap::new, Collectors.toList()));
			Worksheet worksheet = workbook.getWorksheets().get(1);
			worksheet.autoFitColumns();

			String[][] dataArray = new String[70][datListMap.keySet().size()
					+ 5];
			applyStyles(0, 0, worksheet, 0, false, false,
					FontUnderlineType.NONE, false, false, false);
			applyStyles(1, 1, worksheet, 0, false, false,
					FontUnderlineType.NONE, false, false, false);
			dataArray[1][2] = "Legal Entity:";
			applyStyles(1, 2, worksheet, 1, true, true, FontUnderlineType.NONE,
					false, false, false);
			dataArray[2][2] = "Tax Period:";
			applyStyles(2, 2, worksheet, 1, true, true, FontUnderlineType.NONE,
					false, false, false);
			dataArray[3][2] = "Turnover Details";
			applyStyles(3, 2, worksheet, 1, true, true, FontUnderlineType.NONE,
					false, false, false);

			dataArray[1][3] = entityName;
			applyStyles(1, 3, worksheet, 1, false, false,
					FontUnderlineType.NONE, false, false, false);
			dataArray[2][3] = taxPeriod;
			applyStyles(2, 3, worksheet, 1, false, false,
					FontUnderlineType.NONE, false, false, false);
			dataArray[3][3] = null;
			applyStyles(3, 3, worksheet, 1, false, false,
					FontUnderlineType.NONE, false, false, false);

			dataArray[5][2] = "Taxable Supplies";
			applyStyles(5, 2, worksheet, 1, true, true,
					FontUnderlineType.SINGLE, true, false, false);

			// Adding headers
			AtomicInteger row = new AtomicInteger();
			AtomicInteger column = new AtomicInteger();
			row.set(5);
			column.set(3);
			datListMap.put("Total", Lists.newArrayList());
			datListMap.keySet().forEach(gstin -> {
				try {
					dataArray[row.get()][column.get()] = "Amount - " + gstin;
					applyStyles(row.get(), column.get(), worksheet, 1, false,
							true, FontUnderlineType.NONE, true, false, true);
					column.incrementAndGet();
				} catch (Exception e) {
					e.printStackTrace();
				}

			});

			// Adding the supplies rows
			AtomicInteger supplRow = new AtomicInteger();
			supplRow.set(6);
			AtomicInteger supplColumnRow = new AtomicInteger();
			supplColumnRow.set(1);

			Map<String, String> map = buildSuppliersHashMap();
			map.keySet().forEach(key -> {
				try {
					dataArray[supplRow.get()][supplColumnRow.get()] = key
							.startsWith("empty")
									? null
									: Lists.newArrayList("1A", "8A")
											.contains(key) ? key
													: Integer.parseInt(key) > 18
															? null : key;
					applyStyles(supplRow.get(), supplColumnRow.get(), worksheet,
							1, true, false, FontUnderlineType.NONE, false,
							false, true);
					supplRow.incrementAndGet();
				} catch (Exception e) {
					String errMsg = String.format(
							"Exception occurred while creating the Reversal Compute Excel for Entity Name %s and TaxPeriod %s",
							entityName, taxPeriod);
					LOGGER.error(errMsg, e);
					throw new AppException(errMsg);
				}
			});
			fillTheRemainingData(suppliesList, supplRow, supplColumnRow, 6, 1,
					worksheet);

			supplRow.set(6);
			supplColumnRow.set(2);
			suppliesList.forEach(suppl -> {
				try {
					dataArray[supplRow.get()][supplColumnRow.get()] = suppl;
					if (isPresentInList(suppl, underLinesList)) {
						applyStyles(supplRow.get(), supplColumnRow.get(),
								worksheet, 1, true, true,
								FontUnderlineType.SINGLE, false, false, false);
					} else if (isPresentInList(suppl, redTextList)) {
						applyStyles(supplRow.get(), supplColumnRow.get(),
								worksheet, 1, true, false,
								FontUnderlineType.NONE, false, true, false);
					} else if (suppl.startsWith("Ratio")) {
						applyStyles(supplRow.get(), supplColumnRow.get(),
								worksheet, 1, true, true,
								FontUnderlineType.NONE, false, false, false);
					} else {
						applyStyles(supplRow.get(), supplColumnRow.get(),
								worksheet, 1, true, false,
								FontUnderlineType.NONE, false, false, false);
					}
					supplRow.incrementAndGet();
				} catch (Exception e) {
					String errMsg = String.format(
							"Exception occurred while creating the Reversal Compute Excel for Entity Name %s and TaxPeriod %s",
							entityName, taxPeriod);
					LOGGER.error(errMsg, e);
					throw new AppException(errMsg);
				}
			});

			// Adding the data rows
			AtomicInteger dataRow = new AtomicInteger();
			dataRow.set(6);
			AtomicInteger dataColumnRow = new AtomicInteger();
			dataColumnRow.set(3);
			// Have to fill the data for total in section name based
			// Total = section name = ABC + DEF + CEF --> TAX--group by section
			// name
			// and sum the values
			List<ReversalComputeDto> updateTotalDtoList = Lists.newArrayList();
			datListMap.keySet().forEach(gstin -> {
				if (!gstin.equals("Total")) {
					List<ReversalComputeDto> productDtoList = datListMap
							.get(gstin);
					map.values().forEach(sectionName -> {
						try {
							Optional<ReversalComputeDto> productDtoStream = productDtoList
									.stream()
									.filter(dto -> dto.getSubSectionName()
											.equals(sectionName))
									.findFirst();
							if (productDtoStream.isPresent()) {
								final BigDecimal[] taxValue = {
										new BigDecimal(productDtoStream.get()
												.getTaxbleValue()) };
								List<ReversalComputeDto> updateDtoStream = updateTotalDtoList
										.stream()
										.filter(dto -> dto.getSubSectionName()
												.equals(sectionName))
										.collect(Collectors.toList());
								if (!CollectionUtils.isEmpty(updateDtoStream)) {
									updateDtoStream.stream().forEach(dto -> {
										taxValue[0] = new BigDecimal(
												dto.getTaxbleValue())
														.add(taxValue[0]);
									});
								}
								updateExistingRecordIfFound(updateTotalDtoList,
										"Total", sectionName,
										String.valueOf(taxValue[0]));
								datListMap.put("Total", updateTotalDtoList);
							}
						} catch (Exception e) {
							String errMsg = String.format(
									"Exception occurred while creating the Reversal Compute Excel for Entity Name %s and TaxPeriod %s",
									entityName, taxPeriod);
							LOGGER.error(errMsg, e);
							throw new AppException(errMsg);
						}
					});
				}
			});

			datListMap.keySet().forEach(gstinKey -> {
				List<ReversalComputeDto> productDtoList = datListMap
						.get(gstinKey);
				map.values().forEach(sectionName -> {
					try {
						Optional<ReversalComputeDto> productDtoStream = productDtoList
								.stream().filter(dto -> dto.getSubSectionName()
										.equals(sectionName))
								.findFirst();
						if (productDtoStream.isPresent()) {
							final BigDecimal[] taxValue = { new BigDecimal(
									productDtoStream.get().getTaxbleValue()) };
							// if (gstinKey.equals("Total") &&
							// sectionName.equals("SEZ")) {
							// taxValue[0] =
							// taxValue[0].divide(BigDecimal.valueOf(2));
							// }
							dataArray[dataRow.get()][dataColumnRow
									.get()] = String.valueOf(taxValue[0]);
						}
						applyStyles(dataRow.get(), dataColumnRow.get(),
								worksheet, 1, false, false,
								FontUnderlineType.NONE, false, false, true);
						dataRow.incrementAndGet();
					} catch (Exception e) {
						String errMsg = String.format(
								"Exception occurred while creating the Reversal Compute Excel for Entity Name %s and TaxPeriod %s",
								entityName, taxPeriod);
						LOGGER.error(errMsg, e);
						throw new AppException(errMsg);
					}
				});
				dataColumnRow.getAndIncrement();
				dataRow.set(6);
			});

			AtomicInteger atomicInteger = new AtomicInteger();
			atomicInteger.set(3);
			datListMap.keySet().forEach(gstinKey -> {
				fillTheRemainingData(suppliesList, dataRow, dataColumnRow, 6,
						atomicInteger.get(), worksheet);
				atomicInteger.getAndIncrement();
			});

			AtomicInteger columnWidths = new AtomicInteger();
			columnWidths.set(0);
			Cells cells = worksheet.getCells();
			cells.setColumnWidth(columnWidths.get(), 3);
			cells.setColumnWidth(columnWidths.incrementAndGet(), 5);
			cells.setColumnWidth(columnWidths.incrementAndGet(), 60);
			datListMap.keySet().forEach(key -> {
				cells.setColumnWidth(columnWidths.incrementAndGet(), 30);
			});

			cells.importArray(dataArray, 0, 0);
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception occurred while creating the Reversal Compute Excel for Entity Name %s and TaxPeriod %s",
					entityName, taxPeriod);
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	private static void updateExistingRecordIfFound(
			List<ReversalComputeDto> updateTotalDtoList, String total,
			String sectionName, String taxableValue) {
		ReversalComputeDto gstinProdDto;
		Optional<ReversalComputeDto> first = updateTotalDtoList.stream()
				.filter(dto -> dto.getSubSectionName().equals(sectionName)
						&& dto.getGstin().equals(total))
				.findFirst();
		if (first.isPresent()) {
			gstinProdDto = first.get();
			updateTotalDtoList.remove(gstinProdDto);
		}
		gstinProdDto = new ReversalComputeDto(total, sectionName, taxableValue);
		updateTotalDtoList.add(gstinProdDto);
	}

	private static void fillTheRemainingData(List<String> suppliesList,
			AtomicInteger supplRow, AtomicInteger supplColumnRow, int row,
			int column, Worksheet worksheet) {
		supplRow.set(row);
		supplColumnRow.set(column);
		suppliesList.forEach(suppl -> {
			Object value = worksheet.getCells()
					.get(supplRow.get(), supplColumnRow.get()).getValue();
			if (value == null) {
				try {
					applyStyles(supplRow.get(), supplColumnRow.get(), worksheet,
							1, true, false, 0, false, false, true);
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

	private static void applyStyles(int row, int column, Worksheet worksheet,
			int borderStyle, boolean isItalic, boolean isBold, int isUderLine,
			boolean isBackground, boolean isFontColor, boolean isAlignment)
			throws Exception {
		Style style = worksheet.getCells().get(row, column).getStyle();
		style.setBorder(BorderType.LEFT_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.RIGHT_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.TOP_BORDER, borderStyle, Color.getBlack());
		style.setBorder(BorderType.BOTTOM_BORDER, borderStyle,
				Color.getBlack());
		if (isBackground) {
			style.setForegroundColor(Color.getLightSteelBlue());
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

	private static final Map<String, String> buildSuppliersHashMap() {
		Map<String, String> suppliesMap = newLinkedHashMap();
		suppliesMap.put("1", "TAX");
		suppliesMap.put("1A", "B2CS <>0");
		suppliesMap.put("2", "SEZ");
		suppliesMap.put("3", "DXP");
		suppliesMap.put("4", "EXPT");
		suppliesMap.put("5", "EXPWT");
		suppliesMap.put("6", "DTA");
		suppliesMap.put("empty", "");
		suppliesMap.put("7", "EXT");
		suppliesMap.put("8", "NIL");
		suppliesMap.put("8A", "B2CS = 0");
		suppliesMap.put("9", "Non GST /SCH3");
		suppliesMap.put("10", "Explanation to Rule 42");
		suppliesMap.put("11", "TOTAL_GSTIN_SUPPLIES");
		suppliesMap.put("empty1", "");
		suppliesMap.put("empty2", "");
		suppliesMap.put("12", "TAX_REVERSE_CHARGE");
		suppliesMap.put("13", "DTA_REVERSE_CHARGE");
		suppliesMap.put("14", "DXP_REVERSE_CHARGE");
		suppliesMap.put("15", "SEZ_REVERSE_CHARGE");
		suppliesMap.put("empty3", "");
		suppliesMap.put("16", "TTL_RCS");
		suppliesMap.put("empty4", "");
		suppliesMap.put("empty5", "");
		suppliesMap.put("empty6", "");
		suppliesMap.put("17", "NSY");
		suppliesMap.put("empty7", "");
		suppliesMap.put("18", "TOTAL_GSTIN_SUPPLIES_18");
		suppliesMap.put("empty8", "");
		suppliesMap.put("empty9", "");
		suppliesMap.put("empty10", "");
		suppliesMap.put("empty11", "");
		suppliesMap.put("19", "EXT_19");
		suppliesMap.put("20", "NIL_20");
		suppliesMap.put("21", "TTL_RCS_21");
		suppliesMap.put("22", "Explanation to Rule 42");
		suppliesMap.put("empty12", "");
		suppliesMap.put("23", "T_GSTIN_SUPPLIES");
		suppliesMap.put("24", "ITC Reversal Ratio1");
		suppliesMap.put("empty13", "");
		suppliesMap.put("empty14", "");
		suppliesMap.put("empty15", "");
		suppliesMap.put("25", "EXT_25");
		suppliesMap.put("26", "NIL_26");
		suppliesMap.put("27", "Non GST /SCH3_27");
		suppliesMap.put("28", "NSY");
		suppliesMap.put("29", "TTL_RCS_29");
		suppliesMap.put("30", "Explanation to Rule 42");
		suppliesMap.put("empty16", "");
		suppliesMap.put("31", "TOTAL_GSTIN_SUPPLIES__31");
		suppliesMap.put("32", "ITC Reversal Ratio2");
		suppliesMap.put("empty17", "");
		suppliesMap.put("empty18", "");
		suppliesMap.put("empty19", "");
		suppliesMap.put("33", "EXT_33");
		suppliesMap.put("34", "NIL_34");
		suppliesMap.put("35", "Non GST /SCH3_35");
		suppliesMap.put("36", "TTL_RCS_36");
		suppliesMap.put("37", "Explanation to Rule 42");
		suppliesMap.put("empty20", "");
		suppliesMap.put("38", "TOTAL_GSTIN_SUPPLIES__38");
		suppliesMap.put("39", "ITC Reversal Ratio3");

		return suppliesMap;
	}
}