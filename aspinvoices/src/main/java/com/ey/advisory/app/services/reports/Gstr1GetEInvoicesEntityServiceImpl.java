package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aspose.cells.BorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.Font;
import com.aspose.cells.FontUnderlineType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.views.client.GSTR1GetEInvoicesEntityDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1EInvReportsReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr1GetEInvoicesEntityServiceImpl")
public class Gstr1GetEInvoicesEntityServiceImpl
		implements Gstr1GetEInvoicesProcessedReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GetEInvoicesEntityServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1GetEInvoicesEntityDaoImpl")
	private Gstr1GetEInvoicesEntityDao gstr1GetEInvoicesEntityDao;

	@Transactional(value = "clientTransactionManager")
	@Override
	public Workbook findGstr1GetEInvoicesRecords(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1EInvReportsReqDto request = (Gstr1EInvReportsReqDto) criteria;
		int startRow = 5;
		int startcolumn = 1;
		boolean isHeaderRequired = false;
		List<GSTR1GetEInvoicesEntityDto> responseFromView = new ArrayList<>();
		responseFromView = gstr1GetEInvoicesEntityDao
				.getGstr1EIReports(request);

		List<GSTR1GetEInvoicesEntityDto> finalExcelDataList = Lists
				.newLinkedList();
		List<String> tableTypes = Lists.newArrayList("ALL_SECTION", "B2B",
				"EXPORTS", "CDNR", "CDNUR");
		if (CollectionUtils.isNotEmpty(responseFromView)) {
			Map<String, List<GSTR1GetEInvoicesEntityDto>> gstinMap = responseFromView
					.stream().collect(Collectors
							.groupingBy(GSTR1GetEInvoicesEntityDto::getGstin));
			gstinMap.keySet().forEach(gstin -> {
				List<GSTR1GetEInvoicesEntityDto> dataList = gstinMap.get(gstin);
				tableTypes.forEach(tableType -> {
					Optional<GSTR1GetEInvoicesEntityDto> entity = null;
					if (tableType.equals("ALL_SECTION")) {
						entity = dataList.stream().filter(
								dto -> dto.getTableType().equals(tableType))
								.findFirst();

						Set<String> statusSet = dataList.stream()
								.filter(dto -> !dto.getTableType()
										.equals("ALL_SECTION"))
								.map(GSTR1GetEInvoicesEntityDto::getInvoiceStatus)
								.collect(Collectors.toSet());
						entity.get().setInvoiceStatus(
								filterDataByStatus(statusSet));

					} else {
						entity = dataList.stream().filter(
								dto -> dto.getTableType().equals(tableType))
								.findFirst();
					}

					finalExcelDataList.add(entity.get());
				});
			});
		}

		if (CollectionUtils.isEmpty(finalExcelDataList)) {
			tableTypes.forEach(tableType -> {
				finalExcelDataList.add(new GSTR1GetEInvoicesEntityDto("",
						tableType, "", "", "", BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
			});
		}

		Workbook workbook = new Workbook();
		LOGGER.debug("Gstr1 Get EInvoices data item level response"
				+ finalExcelDataList);

		if (CollectionUtils.isNotEmpty(finalExcelDataList)) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.einvoices.entity.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"EInvoices_Entity_Level_Summary_Report.xlsx");
			Worksheet worksheet = workbook.getWorksheets().get(0);
			Cells cells = worksheet.getCells();
			cells.get(2, 2).setValue(request.getReturnFrom());
			cells.get(2, 5).setValue(request.getReturnTo());
			cells.get(2, 7).setValue(request.getTableType());
			cells.importCustomObjects(finalExcelDataList, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					finalExcelDataList.size(), true, "yyyy-mm-dd", false);
			AtomicInteger row = new AtomicInteger(5);
			AtomicInteger column = new AtomicInteger(1);
			finalExcelDataList.forEach(dto -> {
				for (int i = 1; i < 11; i++) {
					applyStyles(row.get(), column.getAndIncrement(), worksheet,
							1, false, false, FontUnderlineType.NONE, null, null,
							false, 0);
				}
				row.incrementAndGet();
				column.set(1);
			});

		}
		return workbook;
	}

	private String filterDataByStatus(Set<String> statusSet) {
		String status = "";

		if (statusSet.contains("INITIATED")) {
			status = "INITIATED";
		} else if (statusSet.contains("INPROGRESS")) {
			status = "INPROGRESS";
		} else if ((statusSet.contains("SUCCESS")
				&& statusSet.contains("FAILED")
				&& statusSet.contains("SUCCESS_WITH_NO_DATA"))) {
			status = "PARTIALLY_SUCCESS";
		} else if (statusSet.contains("FAILED")
				&& statusSet.contains("SUCCESS_WITH_NO_DATA")) {
			status = "PARTIALLY_SUCCESS";
		} else if (statusSet.contains("FAILED")
				&& statusSet.contains("SUCCESS")) {
			status = "PARTIALLY_SUCCESS";
		} else if ((statusSet.contains("SUCCESS")
				&& statusSet.contains("SUCCESS_WITH_NO_DATA"))) {
			status = "SUCCESS";
		} else if (statusSet.contains("SUCCESS")) {
			status = "SUCCESS";
		} else if (statusSet.contains("SUCCESS_WITH_NO_DATA")) {
			status = "SUCCESS";
		} else if (statusSet.contains("FAILED")) {
			status = "FAILED";

		}
		return status;
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
