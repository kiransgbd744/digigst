package com.ey.advisory.app.service.upload.way3recon;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetEwbCntrlRepo;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.config.ConfigConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("GetEwbStatusServiceImpl")
public class GetEwbStatusServiceImpl {
	private static long count = 1;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetEwbStatusServiceImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	private GetEwbCntrlRepo getEwbCntrlRepo;

	@Autowired
	CommonUtility commonUtility;

	public Workbook generateReport(EwbStatusInputDto request)
			throws IOException {
		Workbook workbook = null;
		List<String> gstinList = request.getGstins();
		LocalDate fromDate = LocalDate.parse(request.getFromdate());
		LocalDate toDate = LocalDate.parse(request.getToDate());
		DateTimeFormatter f = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		DateTimeFormatter dateFormatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy");
		List<GetEwbCntrlEntity> entityList = getEwbCntrlRepo.getEwableData(
				gstinList, fromDate, toDate);

		List<EwbStatusDownloadDto> ewbStatusDownloadDtoList = new ArrayList<>();
		for (GetEwbCntrlEntity entity : entityList) {
			EwbStatusDownloadDto dto = new EwbStatusDownloadDto();
			dto.setSrNumber(count);
			dto.setGsin(entity.getGstin() != null ? entity.getGstin().toString()
					: null);
			dto.setGetEwbStatus(entity.getGetStatus() != null
					? entity.getGetStatus().toString() : null);
			dto.setEwbDate(entity.getGetCallDate() != null
					? dateFormatter.format(entity.getGetCallDate()).toString()
					: null);
			dto.setGetCallInitiatedOn(entity.getUpdatedOn() != null ? f
					.format(EYDateUtil
							.toISTDateTimeFromUTC(entity.getUpdatedOn()))
					.toString() : null);
			dto.setErrorMessage(entity.getErrMsg() != null
					? entity.getErrMsg().toString() : null);
			count++;
			ewbStatusDownloadDtoList.add(dto);
		}

		ewbStatusDownloadDtoList.stream()
				.sorted(Comparator.comparing(EwbStatusDownloadDto::getSrNumber))
				.collect(Collectors.toCollection(ArrayList::new));
		String requestId = "";
		workbook = writeToExcel(ewbStatusDownloadDtoList, request.getEntityId(),
				request.getFromdate(), request.getToDate(), requestId);
		count = 1;
		return workbook;
	}

	private Workbook writeToExcel(List<EwbStatusDownloadDto> response,
			Long entityId, String fromDate, String toDate, String requestId) {

		Workbook workbook = null;
		int startRow = 6;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (response != null && !response.isEmpty()) {

			String[] invoiceHeaders = null;
			invoiceHeaders = commonUtility
					.getProp("get.ewable.status.download.header").split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "Get_EWB_Status_Report.xlsx");

			if (LOGGER.isDebugEnabled()) {
				String msg = "GetEwableServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}
			Long entityIds = Long.valueOf(entityId);

			Optional<EntityInfoEntity> optional = entityInfoRepo
					.findById(entityIds);
			EntityInfoEntity entity = optional.get();
			String entityName = entity.getEntityName();
			String pan = entity.getPan();
			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			Cell cellB1 = reportCells.get("B1");
			cellB1.setValue(entityName);
			Cell cellB2 = reportCells.get("B2");
			cellB2.setValue(pan);
			Cell cellB4 = reportCells.get("B3");
			cellB4.setValue(fmtDate(fromDate));
			Cell cellD4 = reportCells.get("D3");
			cellD4.setValue(fmtDate(toDate));

			String createdBy = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			Cell cellB5 = reportCells.get("B4");
			cellB5.setValue(createdBy);

			reportCells.importCustomObjects(response, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn, response.size(),
					true, "yyyy-mm-dd", false);

			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "GetEwableServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.EWB_FOR_EWB_STATUS,
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "saving excel sheet into folder, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}

		} else {
			throw new AppException("No records found, cannot generate report");
		}
		return workbook;
	}

	public static String fmtDate(String date) {
		if (date != null) {
			if (date instanceof String) {
				String[] s = ((String) date).split("-|/");
				if (s.length == 3) {
					if (s[0].length() == 4)
						return s[2] + "-" + s[1] + "-" + s[0];
					else if (s[2].length() == 4)
						return s[0] + "-" + s[1] + "-" + s[2];
				} else {
					return null;
				}
			}
		}
		return null;
	}
}
