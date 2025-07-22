package com.ey.advisory.app.services.search.gstr7trans;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.service.report.gstr7.Gstr7EntitySmryDto;
import com.ey.advisory.app.service.report.gstr7.Gstr7EntitySmryStringDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr7TransEntityLevelReportDownloadServiceImpl")
public class Gstr7TransEntityLevelReportDownloadServiceImpl
		implements Gstr7TransReportDownloadService {
	
	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	private static final String DISP_PROC = "USP_GSTR7_ENTITY_LEVEL_TRANSACTIONAL_SMRY";

	@Override
	public void generateReports(Long id, String report) {

		try {

			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);

			FileStatusDownloadReportEntity entity = optEntity.get();

			String gstins = GenUtil.convertClobtoString(entity.getGstins());
			String[] split = gstins.split(",");
			List<String> gstnList = new ArrayList<>();
			for (String gstn : split) {
				String gstn1 = gstn.replace(GSTConstants.SPE_CHAR, "");
				gstnList.add(gstn1);
			}
			String taxPeriod = entity.getTaxPeriod();

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Gstins" + String.join(",", gstins));
				LOGGER.debug("TaxPeriod" + taxPeriod);
			}

			List<Gstr7EntitySmryDto> entityDataList = getReviewSummary(id, gstnList);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(" reconDataList List " + entityDataList);
			}

			Workbook workbook = new Workbook();

			Optional<EntityInfoEntity> optional = entityInfoRepo
					.findById(optEntity.get().getEntityId());
			EntityInfoEntity entityInfo = optional.get();
			String entityName = entityInfo.getEntityName();

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Going to create workbook for entityName "
						+ entityName);
			}

			workbook = writeToExcel(entityDataList, entityName, taxPeriod);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Workbook is created " + entityName);
			}

			Pair<String, String> document = DocumentUtility
					.uploadDocumentAndReturnDocID(workbook,
							"GSTR7TransELReport", "XLSX");

			String docId = document.getValue1();
			String uploadedDocName = document.getValue0();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Sucessfully uploaded zip file and updating the "
						+ "status as 'Report Generated'";
				LOGGER.debug(msg);
			}

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					docId);

		} catch (Exception e) {
			String msg = String.format(
					"Exception in Gstr7TransEntityLevelReportDownloadServiceImpl");
			LOGGER.error(msg, e);

			fileStatusDownloadReportRepo.updateStatus(id,
					"REPORT_GENERATION_FAILED ", null, LocalDateTime.now());
			throw new AppException(e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}

	}

	private List<Gstr7EntitySmryDto> getReviewSummary(Long id, List<String> gstins) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(DISP_PROC);
		
		storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);
		storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);


		@SuppressWarnings("unchecked")
		List<Object[]> resultList = storedProc.getResultList();

		List<Gstr7EntitySmryDto> respList = resultList.stream()
				.map(o -> convertDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("respList " + respList);
		}

		if (respList == null || respList.isEmpty()) {
			for (String gstin : gstins) {
				Gstr7EntitySmryDto table3Dto = new Gstr7EntitySmryDto();
				table3Dto.setSection("Table-3");
				table3Dto.setSectionDesc("Original details");
				table3Dto.setAspTotalAmount(BigDecimal.ZERO);
				table3Dto.setAspCount(0);
				table3Dto.setAspIgst(BigDecimal.ZERO);
				table3Dto.setAspCgst(BigDecimal.ZERO);
				table3Dto.setAspSgst(BigDecimal.ZERO);
				table3Dto.setGstnCount(0);
				table3Dto.setGstnTotalAmount(BigDecimal.ZERO);
				table3Dto.setGstnIgst(BigDecimal.ZERO);
				table3Dto.setGstnSgst(BigDecimal.ZERO);
				table3Dto.setGstnCgst(BigDecimal.ZERO);
				table3Dto.setDiffCount(0);
				table3Dto.setDiffTotalAmount(BigDecimal.ZERO);
				table3Dto.setDiffIgst(BigDecimal.ZERO);
				table3Dto.setDiffSgst(BigDecimal.ZERO);
				table3Dto.setDiffCgst(BigDecimal.ZERO);
				table3Dto.setGstin(removeSepecialChar(gstin));
				respList.add(table3Dto);

				Gstr7EntitySmryDto table4Dto = new Gstr7EntitySmryDto();
				table4Dto.setSection("Table-4");
				table4Dto.setSectionDesc("Amendment details");
				table4Dto.setAspTotalAmount(BigDecimal.ZERO);
				table4Dto.setAspCount(0);
				table4Dto.setAspIgst(BigDecimal.ZERO);
				table4Dto.setAspCgst(BigDecimal.ZERO);
				table4Dto.setAspSgst(BigDecimal.ZERO);
				table4Dto.setGstnCount(0);
				table4Dto.setGstnTotalAmount(BigDecimal.ZERO);
				table4Dto.setGstnIgst(BigDecimal.ZERO);
				table4Dto.setGstnSgst(BigDecimal.ZERO);
				table4Dto.setGstnCgst(BigDecimal.ZERO);
				table4Dto.setDiffCount(0);
				table4Dto.setDiffTotalAmount(BigDecimal.ZERO);
				table4Dto.setDiffIgst(BigDecimal.ZERO);
				table4Dto.setDiffSgst(BigDecimal.ZERO);
				table4Dto.setDiffCgst(BigDecimal.ZERO);
				table4Dto.setGstin(removeSepecialChar(gstin));

				respList.add(table4Dto);
			}
		} else {
			if (org.apache.commons.collections.CollectionUtils
					.isNotEmpty(respList)) {
				respList.forEach(dto -> {
					dto.setDiffTotalAmount(dto.getAspTotalAmount()
							.subtract(dto.getGstnTotalAmount()));
					dto.setDiffIgst(
							dto.getAspIgst().subtract(dto.getGstnIgst()));
					dto.setDiffCgst(
							dto.getAspCgst().subtract(dto.getGstnCgst()));
					dto.setDiffSgst(
							dto.getAspSgst().subtract(dto.getGstnSgst()));
					dto.setDiffCount(dto.getAspCount() - (dto.getGstnCount()));
				});
			}
		}
		return respList;
	}

	private Workbook writeToExcel(List<Gstr7EntitySmryDto> responseList,
			String entityName, String taxPeriod) {
		Workbook workbook = null;
		int startRow = 6;
		int startcolumn = 1;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr7TransEntityLevelReportDownloadServiceImpl.writeToExcel "
					+ "errorList Size = " + responseList.size();
			LOGGER.debug(msg);
		}

		if (responseList != null && !responseList.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("gstr7.entity.level.summary.report.header")
					.split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "GSTR7_Entity_Level_Summary.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr7TransEntityLevelReportDownloadServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			Worksheet sheet = workbook.getWorksheets().get(0);

			Cell cell = sheet.getCells().get("B3");

			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now());

			String[] dateTime = istDateTimeFromUTC.toString().split("T");

			cell.setValue("ENTITY NAME- " + entityName + "| Date - "
					+ dateTime[0] + " | Time - " + dateTime[1].substring(0, 8)
					+ "| Tax Period - " + taxPeriod);
			Style style = workbook.createStyle();
			Font font = style.getFont();
			font.setBold(true);
			font.setSize(12);

			List<Gstr7EntitySmryStringDto> finalRespList = responseList.stream()
					.map(o -> convertToStringDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			Map<String, List<Gstr7EntitySmryStringDto>> respMap = finalRespList
					.stream().collect(Collectors.groupingBy(o -> o.getGstin()));

			for (Map.Entry map : respMap.entrySet()) {

				startRow = 6;
				List<Gstr7EntitySmryStringDto> sortList = respMap
						.get(map.getKey()).stream()
						.sorted((o1, o2) -> o1.getSection()
								.compareTo(o2.getSection()))
						.collect(Collectors.toList());

				reportCells.importCustomObjects(sortList, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						sortList.size(), true, "yyyy-mm-dd", false);

				startRow = startRow + sortList.size() + 2;

			}

			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "Gstr7TransEntityLevelReportDownloadServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.GSTR7_SUMMARY_REPORT,
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

	private Gstr7EntitySmryStringDto convertToStringDto(Gstr7EntitySmryDto o) {
		Gstr7EntitySmryStringDto dto = new Gstr7EntitySmryStringDto();

		dto.setSection(o.getSection());
		dto.setSectionDesc(o.getSectionDesc());
		dto.setGstin(o.getGstin());

		dto.setAspCount(o.getAspCount());
		dto.setAspTotalAmount(appendDecimalDigit(o.getAspTotalAmount()));
		dto.setAspIgst(appendDecimalDigit(o.getAspIgst()));
		dto.setAspCgst(appendDecimalDigit(o.getAspCgst()));
		dto.setAspSgst(appendDecimalDigit(o.getAspSgst()));

		dto.setGstnCount(o.getGstnCount());
		dto.setGstnTotalAmount(appendDecimalDigit(o.getGstnTotalAmount()));
		dto.setGstnIgst(appendDecimalDigit(o.getGstnIgst()));
		dto.setGstnCgst(appendDecimalDigit(o.getGstnCgst()));
		dto.setGstnSgst(appendDecimalDigit(o.getGstnSgst()));

		dto.setDiffCount(o.getDiffCount());
		dto.setDiffTotalAmount(appendDecimalDigit(o.getDiffTotalAmount()));
		dto.setDiffIgst(appendDecimalDigit(o.getDiffIgst()));
		dto.setDiffCgst(appendDecimalDigit(o.getDiffCgst()));
		dto.setDiffSgst(appendDecimalDigit(o.getDiffSgst()));

		return dto;
	}

	private Gstr7EntitySmryDto convertDto(Object[] data) {

		Gstr7EntitySmryDto dto = new Gstr7EntitySmryDto();

		dto.setSection(String.valueOf(data[0]));

		if (dto.getSection().equalsIgnoreCase("Table-3")) {
			dto.setSectionDesc("Original details");
		} else {
			dto.setSectionDesc("Amendment details");
		}

		dto.setGstin(data[1] != null ? data[1].toString() : "");

		Integer count = ((BigInteger) data[2]).intValue();
		dto.setAspCount(count);
		dto.setAspTotalAmount((BigDecimal) data[3]);
		dto.setAspIgst((BigDecimal) data[4]);
		dto.setAspCgst((BigDecimal) data[5]);
		dto.setAspSgst((BigDecimal) data[6]);
		dto.setGstnCount((Integer) data[7]);
		dto.setGstnTotalAmount((BigDecimal) data[8]);
		dto.setGstnIgst((BigDecimal) data[9]);
		dto.setGstnCgst((BigDecimal) data[10]);
		dto.setGstnSgst((BigDecimal) data[11]);

		return dto;

	}

	private String appendDecimalDigit(BigDecimal value) {

		try {
			if (isPresent(value)) {
				String val = value.setScale(2, BigDecimal.ROUND_HALF_UP)
						.toPlainString();

				String[] s = val.split("\\.");
				if (s.length == 2) {
					if (s[1].length() == 1) {
						val = (s[0] + "." + s[1] + "0");

						return val;
					} else {

						return val;
					}
				} else {
					val = (val + ".00");

					return val;
				}

			}
		} catch (Exception e) {
			LOGGER.error("Gstr7TransEntityLevelReportDownloadServiceImpl"
					+ " AppendDecimalDigit method {}", value);
			return value != null ? value.toString() : null;
		}
		LOGGER.error(
				"Gstr7TransEntityLevelReportDownloadServiceImpl "
						+ "AppendDecimalDigit method val : {} before final return ",
				value.toString());
		return value.toString();
	}
	
	public static void main(String[] args) {
		String gstins = "'33GSPTN0481G1ZA', '33GSPTN0481G1Z2'";
		String[] split = gstins.split(",");
		List<String> gstnList = new ArrayList<>();
		for (String gstn : split) {
			String gstn1 = gstn.replace(GSTConstants.SPE_CHAR, "");
			gstnList.add(gstn1);
		}

		System.out.println(gstnList);
		
	//OutPut -> 	33GSPTN0481G1ZA, 33GSPTN0481G1Z2
	}
	
	private String  removeSepecialChar(String gstin) {
		return gstin.replace(GSTConstants.SPE_CHAR, "");
	}

}