/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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
import com.ey.advisory.app.data.views.client.ITC04EntitylevelDto;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.google.common.collect.Maps;

/**
 * @author Sujith.Nanga
 *
 */
public class Itc04EntityUtil {

	private static Map<String, String> buildTableDesctiptionMap() {
		Map<String, String> tableDescriptionMap = Maps.newLinkedHashMap();
		tableDescriptionMap.put("Goods sent - Manufacturer to Job Worker (4)",
				"Goods sent - Manufacturer to Job Worker (4)");
		tableDescriptionMap.put("Goods received back - Job Worker to Manufacturer (5A)",
				"Goods received back - Job Worker to Manufacturer (5A)");
		tableDescriptionMap.put("Goods received back - Other Job Worker to Manufacturer (5B)",
				"Goods received back - Other Job Worker to Manufacturer (5B)");
		tableDescriptionMap.put("Goods sold from Job Worker Premises (5C)", "Goods sold from Job Worker Premises (5C)");
		return tableDescriptionMap;
	}
	/**
	 * @param workbook
	 * @param responseFromView
	 * @param entityName
	 * @param taxPeriod
	 * @param date
	 * @param time
	 */
	public static void buildAndGenerateItc04Report(Workbook workbook, List<ITC04EntitylevelDto> responseFromView,
			String entityName, String taxPeriod, String date, String time, Gstr6SummaryRequestDto request) {

		try {
			List<ITC04EntitylevelDto> itc04EntitylevelDtos = responseFromView;
			AtomicBoolean isNoData = new AtomicBoolean(false);
			if (CollectionUtils.isEmpty(itc04EntitylevelDtos)) {
				isNoData.set(true);
				List<String> gstins = request.getDataSecAttrs().get("GSTIN");
				gstins.forEach(gstin -> {
					itc04EntitylevelDtos.add(new ITC04EntitylevelDto(gstin, "", "0", "0.00", "0.00", "0.00", "0",
							"0.00", "0", "0.00", "0.00", "0.00"));
				});
			}
			
			if (!CollectionUtils.isEmpty(itc04EntitylevelDtos)) {
				List<String> dbGstins = itc04EntitylevelDtos.stream().map(ITC04EntitylevelDto::getGstin).collect(Collectors.toList());
				List<String> reqGstins = request.getDataSecAttrs().get("GSTIN");
				dbGstins.forEach(gstin -> reqGstins.remove(gstin));
				if(!CollectionUtils.isEmpty(reqGstins)) {
					reqGstins.forEach(gstin -> {
						itc04EntitylevelDtos.add(new ITC04EntitylevelDto(gstin, "", "0", "0.00", "0.00", "0.00", "0",
								"0.00", "0", "0.00", "0.00", "0.00"));
					});
				}
			}
			
			
			Worksheet worksheet = workbook.getWorksheets().get(0);
			worksheet.autoFitColumns();

			AtomicInteger row = new AtomicInteger();
			AtomicInteger column = new AtomicInteger();
			String[][] dataArray = new String[70 + itc04EntitylevelDtos.size()][100 + itc04EntitylevelDtos.size()];
			applyStyles(row.get(), column.get(), worksheet, 0, false, false, FontUnderlineType.NONE, null, false,
					false);

			dataArray[row.incrementAndGet()][column.incrementAndGet()] = "ITC04 Entity Level Summary";
			applyStyles(row.get(), column.get(), worksheet, 0, true, true, FontUnderlineType.NONE, null, false, false);

			dataArray[row.incrementAndGet()][column.get()] = "Entity Name - " + entityName;
			applyStyles(row.get(), column.get(), worksheet, 0, true, true, FontUnderlineType.NONE, null, false, false);

			dataArray[row.get()][column.incrementAndGet()] = "Date - " + date;
			applyStyles(row.get(), column.get(), worksheet, 0, true, true, FontUnderlineType.NONE, null, false, false);

			dataArray[row.get()][column.incrementAndGet()] = "Time - " + time;
			applyStyles(row.get(), column.get(), worksheet, 0, true, true, FontUnderlineType.NONE, null, false, false);

			dataArray[row.get()][column.incrementAndGet()] = "Tax Period - " + taxPeriod;
			applyStyles(row.get(), column.get(), worksheet, 0, true, true, FontUnderlineType.NONE, null, false, false);

			row.set(5);
			column.set(1);

			worksheet.getCells().merge(row.get(), column.get(), 2, 1);
			dataArray[row.get()][column.get()] = "GSTIN";
			applyStyles(row.get(), column.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
					Color.getLightSteelBlue(), false, true);
			applyStyles(6, column.get(), worksheet, 1, false, true, FontUnderlineType.NONE, Color.getLightSteelBlue(),
					false, true);

			worksheet.getCells().merge(row.get(), column.incrementAndGet(), 2, 1);
			dataArray[row.get()][column.get()] = "Table Description";
			applyStyles(row.get(), column.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
					Color.getLightSteelBlue(), false, true);

			AtomicInteger startColumn = new AtomicInteger(1);
			AtomicInteger endColumn = new AtomicInteger(4);

			worksheet.getCells().merge(row.get(), column.incrementAndGet(), startColumn.get(), endColumn.get());
			dataArray[row.get()][column.get()] = "As available at DigiGST";
			applyStyles(row.get(), column.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
					Color.getLightSteelBlue(), false, true);
			worksheet.getCells().get(row.get(), column.get()).getMergedRange().setOutlineBorder(BorderType.TOP_BORDER,
					CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange().setOutlineBorder(BorderType.RIGHT_BORDER,
					CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange().setOutlineBorder(BorderType.LEFT_BORDER,
					CellBorderType.THIN, Color.fromArgb(0));

			column.set(7);
			worksheet.getCells().merge(row.get(), column.get(), 1, 2);
			dataArray[row.get()][column.get()] = "As available at GSTN";
			applyStyles(row.get(), column.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
					Color.getLightSteelBlue(), false, true);
			worksheet.getCells().get(row.get(), column.get()).getMergedRange().setOutlineBorder(BorderType.TOP_BORDER,
					CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange().setOutlineBorder(BorderType.RIGHT_BORDER,
					CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange().setOutlineBorder(BorderType.LEFT_BORDER,
					CellBorderType.THIN, Color.fromArgb(0));

			column.set(9);
			worksheet.getCells().merge(row.get(), column.get(), 1, 2);
			dataArray[row.get()][column.get()] = "Difference";
			applyStyles(row.get(), column.get(), worksheet, 1, false, true, FontUnderlineType.NONE,
					Color.getLightSteelBlue(), false, true);
			worksheet.getCells().get(row.get(), column.get()).getMergedRange().setOutlineBorder(BorderType.TOP_BORDER,
					CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange().setOutlineBorder(BorderType.RIGHT_BORDER,
					CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange()
					.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.fromArgb(0));
			worksheet.getCells().get(row.get(), column.get()).getMergedRange().setOutlineBorder(BorderType.LEFT_BORDER,
					CellBorderType.THIN, Color.fromArgb(0));

			row.set(6);
			column.set(3);
			dataArray[row.get()][column.get()] = "Count";
			applyStyles(row.get(), column.get(), worksheet, 1, false, false, FontUnderlineType.NONE,
					Color.getLightYellow(), false, false);
			dataArray[row.get()][column.incrementAndGet()] = "Quantity";
			applyStyles(row.get(), column.get(), worksheet, 1, false, false, FontUnderlineType.NONE,
					Color.getLightYellow(), false, false);
			dataArray[row.get()][column.incrementAndGet()] = "Losses Quantity";
			applyStyles(row.get(), column.get(), worksheet, 1, false, false, FontUnderlineType.NONE,
					Color.getLightYellow(), false, false);
			dataArray[row.get()][column.incrementAndGet()] = "Taxable Value";
			applyStyles(row.get(), column.get(), worksheet, 1, false, false, FontUnderlineType.NONE,
					Color.getLightYellow(), false, false);

			dataArray[row.get()][column.incrementAndGet()] = "Count";
			applyStyles(row.get(), column.get(), worksheet, 1, false, false, FontUnderlineType.NONE,
					Color.getLightYellow(), false, false);
			dataArray[row.get()][column.incrementAndGet()] = "Taxable Value";
			applyStyles(row.get(), column.get(), worksheet, 1, false, false, FontUnderlineType.NONE,
					Color.getLightYellow(), false, false);

			dataArray[row.get()][column.incrementAndGet()] = "Count";
			applyStyles(row.get(), column.get(), worksheet, 1, false, false, FontUnderlineType.NONE,
					Color.getLightYellow(), false, false);
			dataArray[row.get()][column.incrementAndGet()] = "Taxable Value";
			applyStyles(row.get(), column.get(), worksheet, 1, false, false, FontUnderlineType.NONE,
					Color.getLightYellow(), false, false);

			Map<String, List<ITC04EntitylevelDto>> gstinsMap = itc04EntitylevelDtos.stream()
					.collect(Collectors.groupingBy(ITC04EntitylevelDto::getGstin));
			System.out.println(gstinsMap);

			Map<String, String> descMap = buildTableDesctiptionMap();
			row.set(7);
			column.set(1);
			Map<String, List<ITC04EntitylevelDto>> dataMap = fillTheMissigDataForGstins(gstinsMap);
			dataMap.keySet().forEach(gstin -> {
				List<ITC04EntitylevelDto> itc04EntitylevelDtos1 = dataMap.get(gstin);
				Set<String> docStrings = descMap.keySet();
				docStrings.forEach(docString -> {
					itc04EntitylevelDtos1.stream().filter(dto -> dto.getTableDesc().equals(docString))
							.forEach(itc04EntitylevelDto -> {
								try {
									dataArray[row.get()][column.get()] = itc04EntitylevelDto.getGstin();
									applyStyles(row.get(), column.get(), worksheet, 1, false, false,
											FontUnderlineType.NONE, null, false, false);
									dataArray[row.get()][column.incrementAndGet()] = descMap
											.get(itc04EntitylevelDto.getTableDesc());
									applyStyles(row.get(), column.get(), worksheet, 1, false, false,
											FontUnderlineType.NONE, null, false, false);
									dataArray[row.get()][column.incrementAndGet()] = isNoData.get() ? "0"
											: itc04EntitylevelDto.getAvailabledigigstCount();
									applyStyles(row.get(), column.get(), worksheet, 1, false, false,
											FontUnderlineType.NONE, null, false, true);
									dataArray[row.get()][column.incrementAndGet()] = isNoData.get() ? "0"
											: itc04EntitylevelDto.getAvailabledigigstQuantity();
									applyStyles(row.get(), column.get(), worksheet, 1, false, false,
											FontUnderlineType.NONE, null, false, true);
									dataArray[row.get()][column.incrementAndGet()] = isNoData.get() ? "0"
											: itc04EntitylevelDto.getAvailablelossesQuantity();
									applyStyles(row.get(), column.get(), worksheet, 1, false, false,
											FontUnderlineType.NONE, null, false, true);
									dataArray[row.get()][column.incrementAndGet()] = isNoData.get() ? "0"
											: itc04EntitylevelDto.getAvailableTaxablevalue();
									applyStyles(row.get(), column.get(), worksheet, 1, false, false,
											FontUnderlineType.NONE, null, false, true);
									dataArray[row.get()][column.incrementAndGet()] = isNoData.get() ? "0"
											: itc04EntitylevelDto.getAvailablegstnCount();
									applyStyles(row.get(), column.get(), worksheet, 1, false, false,
											FontUnderlineType.NONE, null, false, true);

									if ((itc04EntitylevelDto.getAvailablegstnTaxablevalue() == null
											|| itc04EntitylevelDto.getAvailablegstnTaxablevalue().equals("0")
											|| itc04EntitylevelDto.getAvailablegstnTaxablevalue().equals("0.00")
											|| itc04EntitylevelDto.getAvailablegstnTaxablevalue().equals("0.000")
											|| itc04EntitylevelDto.getAvailablegstnTaxablevalue().equals("0.0000"))
													&& !itc04EntitylevelDto.getTableDesc()
															.equals("Goods sent - Manufacturer to Job Worker (4)")) {
										dataArray[row.get()][column.incrementAndGet()] = "NA";
										applyStyles(row.get(), column.get(), worksheet, 1, false, false,
												FontUnderlineType.NONE, Color.getLightGray(), false, true);
									} else {
										dataArray[row.get()][column.incrementAndGet()] = isNoData.get() ? "0"
												: itc04EntitylevelDto.getAvailablegstnTaxablevalue();
										applyStyles(row.get(), column.get(), worksheet, 1, false, false,
												FontUnderlineType.NONE, null, false, true);
									}
									dataArray[row.get()][column.incrementAndGet()] = isNoData.get() ? "0"
											: itc04EntitylevelDto.getDiffCount();
									applyStyles(row.get(), column.get(), worksheet, 1, false, false,
											FontUnderlineType.NONE, null, false, true);

									if ((itc04EntitylevelDto.getDiffTaxablevalue() == null
											|| itc04EntitylevelDto.getDiffTaxablevalue().equals("0")
											|| itc04EntitylevelDto.getDiffTaxablevalue().equals("0.00")
											|| itc04EntitylevelDto.getDiffTaxablevalue().equals("0.000")
											|| itc04EntitylevelDto.getDiffTaxablevalue().equals("0.0000"))
													&& !itc04EntitylevelDto.getTableDesc()
															.equals("Goods sent - Manufacturer to Job Worker (4)")) {
										dataArray[row.get()][column.incrementAndGet()] = "NA";
										applyStyles(row.get(), column.get(), worksheet, 1, false, false,
												FontUnderlineType.NONE, Color.getLightGray(), false, true);
									} else {
										dataArray[row.get()][column.incrementAndGet()] = isNoData.get() ? "0"
												: itc04EntitylevelDto.getDiffTaxablevalue();
										applyStyles(row.get(), column.get(), worksheet, 1, false, false,
												FontUnderlineType.NONE, null, false, true);
									}
									row.getAndIncrement();
									column.set(1);
								} catch (Exception e) {
									e.printStackTrace();
								}
							});
				});
				for (int i = 1; i < 11; i++) {
					try {
						applyStyles(row.get(), i, worksheet, 1, false, false, FontUnderlineType.NONE, null, false,
								true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				row.getAndIncrement();
			});

			AtomicInteger columnWidths = new AtomicInteger();
			columnWidths.set(0);
			Cells cells = worksheet.getCells();
			cells.setColumnWidth(columnWidths.get(), 5);
			cells.setColumnWidth(columnWidths.incrementAndGet(), 30);
			cells.setColumnWidth(columnWidths.incrementAndGet(), 52);
			cells.setColumnWidth(columnWidths.incrementAndGet(), 15);
			cells.setColumnWidth(columnWidths.incrementAndGet(), 30);
			for (int i = 0; i < 10; i++) {
				try {
					cells.setColumnWidth(columnWidths.incrementAndGet(), 15);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			cells.importArray(dataArray, 0, 0);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	private static Map<String, List<ITC04EntitylevelDto>> fillTheMissigDataForGstins(
			Map<String, List<ITC04EntitylevelDto>> gstinsMap) {
		Map<String, List<ITC04EntitylevelDto>> dataMap = Maps.newLinkedHashMap();
		gstinsMap.keySet().forEach(gstin -> {
			List<ITC04EntitylevelDto> itc04EntitylevelDtos = gstinsMap.get(gstin);
			List<String> dbStrings = itc04EntitylevelDtos.stream().map(ITC04EntitylevelDto::getTableDesc)
					.collect(Collectors.toList());
			Set<String> descStrings = buildTableDesctiptionMap().keySet();
			descStrings.removeAll(dbStrings);
			if (!CollectionUtils.isEmpty(descStrings)) {
				descStrings.forEach(docString -> {
					itc04EntitylevelDtos.add(new ITC04EntitylevelDto(gstin, docString, "0", "0.00", "0.00", "0.00", "0",
							"0.00", "0", "0.00", "0.00", "0.00"));
				});
			}
			dataMap.put(gstin, itc04EntitylevelDtos);
		});
		return dataMap;
	}

	private static void applyStyles(int row, int column, Worksheet worksheet, int borderStyle, boolean isItalic,
			boolean isBold, int isUderLine, Color color, boolean isFontColor, boolean isAlignment) throws Exception {
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

		Font font = style.getFont();
		font.setItalic(isItalic);
		font.setBold(isBold);
		font.setUnderline(isUderLine);
		// font.setName("EYInterstate Light");
		font.setSize(11);
		if (isFontColor) {
			font.setColor(Color.getRed());
		}
		worksheet.autoFitColumns();
		worksheet.autoFitRows(true);
		worksheet.getCells().get(row, column).setStyle(style);
	}

}
