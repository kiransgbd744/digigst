
package com.ey.advisory.app.services.dashboard.fiori;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BMonthlyTrendTaxAmountRepository;
import com.ey.advisory.app.gstr3b.Gstr3bMonthlyTrendTaxAmountsEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("Gstr3BDashboardReportDwnldDaoImpl")
public class Gstr3BDashboardReportDwnldDaoImpl
		implements Gstr3BDashboardReportDwnldDao {

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	private Gstr3BMonthlyTrendTaxAmountRepository gstr3BRepo;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	@Override
	public String getDashbrdData(Long batchId) {
		
		Workbook workbook = null;
		int startRow = 4;
		int startcolumn = 1;
		boolean isHeaderRequired = false;

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get dashboard Report Details with batchId:'%s'", batchId);
			LOGGER.debug(msg);
		}

		try {

			Optional<FileStatusDownloadReportEntity> entity = fileStatusDownloadReportRepo
					.findById(batchId);

			String gstin = GenUtil
					.convertClobtoString(entity.get().getGstins());
			List<String> gstinList = Arrays.asList(gstin.split(","));
			
			String taxPeriod = entity.get().getUsrAcs1();
			List<String> taxPeriodList = Arrays.asList(taxPeriod.split(","));

			DateTimeFormatter format = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			LocalDateTime timeOfGeneration = LocalDateTime.now();
			LocalDateTime convertISDDate = EYDateUtil
					.toISTDateTimeFromUTC(timeOfGeneration);

			String fileName = "LiabilityPaid_DetailsReport_"
					+ format.format(convertISDDate) + ".xlsx";

			List<Gstr3bMonthlyTrendTaxAmountsEntity> gstr3BList = new ArrayList<>();

			List<Gstr3bMonthlyTrendTaxAmountsEntity> gstr3BList1 = gstr3BRepo
					.getGstinAndTaxPeriod(gstinList, taxPeriodList);

			if (gstr3BList1 == null || gstr3BList1.isEmpty()) {
				String msg = String.format("for Error Report no.of "
						+ " zero batchId: '%s'", batchId);
				LOGGER.error(msg);

				fileStatusDownloadReportRepo.updateStatus(batchId,
						"NO_DATA_FOUND", null, LocalDateTime.now());

				return null;
			}
			
			Map<String, List<Gstr3bMonthlyTrendTaxAmountsEntity>> mapGstr3b = gstr3BList1
					.stream()
					.collect(Collectors.groupingBy(e -> e.getSuppGstin()));

			for (Map.Entry<String, List<Gstr3bMonthlyTrendTaxAmountsEntity>> entry : mapGstr3b
					.entrySet()) {
				List<Gstr3bMonthlyTrendTaxAmountsEntity> value = entry
						.getValue();
				Collections.sort(value, Comparator.comparing(
						Gstr3bMonthlyTrendTaxAmountsEntity::getTaxPeriod));
				gstr3BList.addAll(value);

			}

			List<Gstr3bReportGraphDetailsDto> reconDataList = gstr3BList
					.stream().map(o -> convertRowsToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			
			if (LOGGER.isDebugEnabled()) {
				
				LOGGER.debug(" reconDataList List "+reconDataList);
				}
				
			
			String[] invoiceHeaders = commonUtility
					.getProp("gstr3b.dashboard.report.mapping").split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "LiabilityPaid_DetailsReport.xlsx");
			
			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(reconDataList, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					reconDataList.size(), true, "yyyy-mm-dd", false);

			
			String workBookPath = workbook.getFileName();
			String wrkFleName = workBookPath
					.substring(workBookPath.lastIndexOf('/') + 1);
			if (LOGGER.isDebugEnabled()) {
				
			LOGGER.debug("woorkBook fileName "+wrkFleName);
			}
			String uploadFileName = DocumentUtility.uploadDocumentWithFileName(
					workbook, ConfigConstants.GSTR3BLiabilityDashboard,
					fileName);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(UPLOADED_FILENAME_MSG,
						uploadFileName);
				LOGGER.debug(msg);
			}
			
			return uploadFileName;
			
		} catch (Exception ex) {
			String errMsg = String
					.format("Error while creating Error report %s", batchId);
			LOGGER.error(errMsg, ex);
			throw new AppException(ex);
		}
		
	}

	private Gstr3bReportGraphDetailsDto convertRowsToDto(
			Gstr3bMonthlyTrendTaxAmountsEntity entity) {

		try {
			SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMddyy");
			SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM-yy");
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");

			Gstr3bReportGraphDetailsDto dto = new Gstr3bReportGraphDetailsDto();
			dto.setSuppGstin(entity.getSuppGstin());
			dto.setMonth(outputDateFormat
					.format(inputDateFormat.parse(entity.getTaxPeriod())));
			dto.setApiCallStatus(entity.getStatus());
			dto.setDateTime(formatter
					.format(EYDateUtil.toISTDateTimeFromUTC(
							entity.getCreateDate()))
					.toString());
			dto.setCashIgst(entity.getCashIgst().toString());
			dto.setCashSgst(entity.getCashSgst().toString());
			dto.setCashCgst(entity.getCashCgst().toString());
			dto.setCashCess(entity.getCashCess().toString());
			dto.setCashTotal(entity.getCashTotal().toString());
			dto.setItcIgst(entity.getItcIgst().toString());
			dto.setItcCgst(entity.getItcCgst().toString());
			dto.setItcSgst(entity.getItcSgst().toString());
			dto.setItcCess(entity.getItcCess().toString());
			dto.setItcTotal(entity.getItcTotal().toString());
			dto.setTaxIgst(entity.getTaxPayIgst().toString());
			dto.setTaxCgst(entity.getTaxPayCgst().toString());
			dto.setTaxSgst(entity.getTaxPaySgst().toString());
			dto.setTaxCess(entity.getTaxPayCess().toString());
			dto.setTaxTotal(entity.getTaxPayTotal().toString());

			return dto;
		} catch (Exception ex) {
			throw new AppException();
		}
	}

	private String getMonth(String taxPeriod) {
		int month = Integer.parseInt(taxPeriod);
		String monthString;
		switch (month) {
		case 1:
			monthString = "Jan";
			break;
		case 2:
			monthString = "Feb";
			break;
		case 3:
			monthString = "Mar";
			break;
		case 4:
			monthString = "Apr";
			break;
		case 5:
			monthString = "May";
			break;
		case 6:
			monthString = "Jun";
			break;
		case 7:
			monthString = "Jul";
			break;
		case 8:
			monthString = "Aug";
			break;
		case 9:
			monthString = "Sep";
			break;
		case 10:
			monthString = "Oct";
			break;
		case 11:
			monthString = "Nov";
			break;
		case 12:
			monthString = "Dec";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		return monthString;
	}
	
}
