/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.views.client.GSTR1EntityLevelSummaryDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("Gstr1EntityLevelSummaryServiceImpl")
public class Gstr1EntityLevelSummaryServiceImpl
		implements Gstr1EntityLevelSummaryService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1EntityLevelSummaryServiceImpl.class);

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1EntityLevelSummaryDaoImpl")
	private Gstr1EntityLevelSummaryDao gstr1EntityLevelSummaryDao;
	
	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	@Override
	public Workbook findEntityLevelSummary(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 6;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<GSTR1EntityLevelSummaryDto> responseFromView = new ArrayList<>();
		responseFromView = gstr1EntityLevelSummaryDao
				.getEntityLevelSummary(request);
		
		List<GSTR1EntityLevelSummaryDto> finalExcelDataList = Lists
				.newLinkedList();

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.entity.level.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"GSTR-1_ASP_Entity_Level_Summary.xlsx");
			Worksheet worksheet = workbook.getWorksheets().get(0);
			Cells cells = worksheet.getCells();
			List<Long> entityId = request.getEntityId();
			EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
			for (Long entity : entityId) {
				findEntityByEntityId = repo.findEntityByEntityId(entity);
			}
			if (findEntityByEntityId != null) {

				cells.get("B3").setValue(
				 findEntityByEntityId.getEntityName());
			
			}
			
			String gstin = request.getDataSecAttrs().get("GSTIN")
					.stream().findFirst().get();
			cells.get("D3").setValue(gstin);
			
			String fromTaxPeriod = request.getTaxperiod();
			LocalDate startDate = LocalDate.of(
					Integer.parseInt(fromTaxPeriod.substring(2)),
					Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
			String taxPeriod = startDate.getMonth().getDisplayName(
					TextStyle.SHORT, Locale.US) + "-" + startDate.getYear();
			
			cells.get("J3").setValue(taxPeriod);

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy");

			DateTimeFormatter FOMATTER1 = DateTimeFormatter
					.ofPattern("HH:mm:ss");

			String date = FOMATTER.format(istDateTimeFromUTC);
			String time = FOMATTER1.format(istDateTimeFromUTC);

			cells.get("F3").setValue( date);
			cells.get("H3").setValue( time);
			
			cells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
			
			AtomicInteger row = new AtomicInteger(6);
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

	public static String convertTaxPeriodToString(Integer taxPeriod) {
		String dateStr = taxPeriod.toString();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
		YearMonth ym = YearMonth.parse(dateStr, formatter);
		int year = ym.getYear();
		int monthValue = ym.getMonthValue();
		StringBuffer monthYear = new StringBuffer();
		String monthValueOf = String.valueOf(monthValue);
		String yearValueOf = String.valueOf(year);
		if (monthValue < 9) {
			monthYear.append("0");
			monthYear.append(monthValueOf);
			monthYear.append(yearValueOf);
		} else {
			monthYear.append(monthValueOf);
			monthYear.append(yearValueOf);
		}
		return monthYear.toString();
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
