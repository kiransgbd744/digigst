package com.ey.advisory.app.services.search.filestatussearch;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.javatuples.Pair;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
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
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.gstr3b.Gstr3bEntityLevelReportDto;
import com.ey.advisory.app.gstr3b.Gstr3bEntityLevelReportFinalDto;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.CompressAndZipXlsxFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

@Component("AsyncGstr3bEntityReportDownloadServiceImpl")
@Slf4j
public class AsyncGstr3bEntityReportDownloadServiceImpl
		implements AsyncReportDownloadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	CompressAndZipXlsxFiles compressAndZipXlsxFiles;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@Override
	public void generateReports(Long id) {

		List<Object[]> list = null;
		Workbook workbook = null;
		File tempDir = null;
		try {
			tempDir = createTempDir(id);

			Integer chunk = 500;
			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);

			FileStatusDownloadReportEntity entity = optEntity.get();

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_GSTR_3B_SUMMARY_REPORT");

			storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
					Long.class, ParameterMode.IN);
			storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunk);

			list = storedProc.getResultList();

			Map<String, List<Object[]>> map = new HashMap<>();
			for (Object[] obj : list) {
				map.computeIfAbsent(obj[22].toString(), k -> new ArrayList<>())
						.add(obj);
			}
			List<Gstr3bEntityLevelReportDto> gstr3bList = new ArrayList<>();
			map.entrySet().forEach(entry -> {
				List<Object[]> taxPeriodList = entry.getValue();
				gstr3bList.addAll(taxPeriodList.stream()
						.filter(o -> CommonUtility.Gstr3bTableHeadingAndDescription
								.containsKey(o[1].toString()))
						.map(o -> converttoDto(o))
						.collect(Collectors.toCollection(ArrayList::new)));

			});

			Long entityId = entity.getEntityId();
			Optional<EntityInfoEntity> optional = entityInfoRepo
					.findById(entityId);

			EntityInfoEntity entityInfo = optional.get();
			String entityName = entityInfo.getEntityName();
			String taxPeriod = entity.getTaxPeriodFrom() + "-"
					+ entity.getTaxPeriodTo();

			workbook = writeToExcel(gstr3bList, entityName, taxPeriod, tempDir,
					id);

			if (workbook != null) {

				String zipFileName = CommonUtility.getReportZipFiles(tempDir,
						entityName, "GSTR3B_Entity Level Summary_" + id);

				File zipFile = new File(tempDir, zipFileName);

				Pair<String, String> uploadedZipName = DocumentUtility
						.uploadFile(zipFile,
								ConfigConstants.GSTR3B_SUMMARY_DOWNLOAD_REPORT);
				String uploadedDocName = uploadedZipName.getValue0();
				String docId = uploadedZipName.getValue1();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}

				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						docId);
			} else {
				LOGGER.error("No Data found for report id : {} ", id);
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}

		} catch (Exception ex) {
			String errMsg = String
					.format("Error while creating Error report {} ", id);

			LOGGER.error(errMsg, ex);
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occued while while generating csv file";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);

		} finally {
			GenUtil.deleteTempDir(tempDir);
		}

	}

	private static File createTempDir(Long batchId) throws IOException {

		return Files.createTempDirectory("DownloadReports").toFile();
	}

	public Gstr3bEntityLevelReportDto converttoDto(Object[] arr) {

		Gstr3bEntityLevelReportDto obj = new Gstr3bEntityLevelReportDto();
		obj.setGstin(arr[0] != null ? arr[0].toString() : "");
		String tableSec = arr[1] != null ? arr[1].toString() : "";
		obj.setTableSection(tableSec);
		Pair<String, String> table = CommonUtility
				.getGstr3bHeadingandDesc(tableSec);
		obj.setTableHeading(table.getValue0());
		obj.setTableDesc(table.getValue1());
		obj.setComputeTaxableVal(
				arr[2] != null ? (BigDecimal) arr[2] : BigDecimal.ZERO);
		obj.setComputeIgst(
				arr[3] != null ? (BigDecimal) arr[3] : BigDecimal.ZERO);
		obj.setComputeCgst(
				arr[4] != null ? (BigDecimal) arr[4] : BigDecimal.ZERO);
		obj.setComputeSgst(
				arr[5] != null ? (BigDecimal) arr[5] : BigDecimal.ZERO);
		obj.setComputeCess(
				arr[6] != null ? (BigDecimal) arr[6] : BigDecimal.ZERO);
		obj.setUserTaxableVal(
				arr[7] != null ? (BigDecimal) arr[7] : BigDecimal.ZERO);
		obj.setUserIgst(arr[8] != null ? (BigDecimal) arr[8] : BigDecimal.ZERO);
		obj.setUserCgst(arr[9] != null ? (BigDecimal) arr[9] : BigDecimal.ZERO);
		obj.setUserSgst(
				arr[10] != null ? (BigDecimal) arr[10] : BigDecimal.ZERO);
		obj.setUserCess(
				arr[11] != null ? (BigDecimal) arr[11] : BigDecimal.ZERO);
		obj.setGstnTaxableVal(
				arr[12] != null ? (BigDecimal) arr[12] : BigDecimal.ZERO);
		obj.setGstnIgst(
				arr[13] != null ? (BigDecimal) arr[13] : BigDecimal.ZERO);
		obj.setGstnCgst(
				arr[14] != null ? (BigDecimal) arr[14] : BigDecimal.ZERO);
		obj.setGstnSgst(
				arr[15] != null ? (BigDecimal) arr[15] : BigDecimal.ZERO);
		obj.setGstnCess(
				arr[16] != null ? (BigDecimal) arr[16] : BigDecimal.ZERO);
		obj.setDiffTaxableVal(
				arr[17] != null ? (BigDecimal) arr[17] : BigDecimal.ZERO);
		obj.setDiffIgst(
				arr[18] != null ? (BigDecimal) arr[18] : BigDecimal.ZERO);
		obj.setDiffCgst(
				arr[19] != null ? (BigDecimal) arr[19] : BigDecimal.ZERO);
		obj.setDiffSgst(
				arr[20] != null ? (BigDecimal) arr[20] : BigDecimal.ZERO);
		obj.setDiffCess(
				arr[21] != null ? (BigDecimal) arr[21] : BigDecimal.ZERO);
		obj.setTaxPeriod(arr[22] != null ? arr[22].toString() : null);
		obj.setDerivedTaxPeriod(arr[22] != null
				? GenUtil.getDerivedTaxPeriod(arr[22].toString()).toString()
				: null);

		return obj;

	}

	private Workbook writeToExcel(
			List<Gstr3bEntityLevelReportDto> gstr3bRespList, String entityName,
			String taxPeriod, File tempDir, Long id) {
		Workbook workbook = null;
		int startRow = 6;
		int startcolumn = 1;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin GenerateErrorReportImpl.writeToExcel "
					+ "Gstr3b Response Size = " + gstr3bRespList.size();
			LOGGER.debug(msg);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Workbook has been generated successfully with the"
							+ " gstr3b response list in the directory : {}",
					gstr3bRespList);
		}

		if (gstr3bRespList != null && !gstr3bRespList.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("gstr3b.summary.report.header").split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "GSTR3BSummaryReport.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "GenerateErrorReportImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			Worksheet sheet = workbook.getWorksheets().get(0);

			Cell cell = sheet.getCells().get("A2");

			cell.setValue("ENTITY NAME- " + entityName + "| Date - "
					+ LocalDate.now(DateTimeZone.forID("Asia/Kolkata"))
							.toString("dd-MM-yyyy")
					+ " | Time - "
					+ LocalTime.now(DateTimeZone.forID("Asia/Kolkata"))
							.toString("HH:mm:ss")
					+ "| Tax Period - " + taxPeriod);

			Style style = workbook.createStyle();
			Font font = style.getFont();
			font.setBold(true);
			font.setSize(12);

			Map<String, List<Gstr3bEntityLevelReportDto>> groupByMap = gstr3bRespList
					.stream().collect(Collectors.groupingBy(o -> o.getGstin()
							+ o.getTableSection() + o.getTaxPeriod()));

			Map<String, List<Gstr3bEntityLevelReportDto>> respMap = new HashMap<>();
			List<Gstr3bEntityLevelReportDto> list = new ArrayList<>();
			groupByMap.forEach((K, V) -> {
				Gstr3bEntityLevelReportDto dto = V.stream().reduce(
						new Gstr3bEntityLevelReportDto(),
						(o1, o2) -> sumOf(o1, o2));
				list.add(dto);
			});
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin GenerateErrorReportImpl.writeToExcel "
						+ "GroupMap Size = " + groupByMap.size();
				LOGGER.debug(msg);
			}
			respMap = list.stream().collect(Collectors
					.groupingBy(o -> o.getGstin() + o.getDerivedTaxPeriod()));
			respMap.forEach((k, v) -> addMissingDto(v));
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin GenerateErrorReportImpl.writeToExcel "
						+ "respMap Size = " + respMap.size();
				LOGGER.debug(msg);
			}
			Map<String, List<Gstr3bEntityLevelReportDto>> sortedMap = sortValues(
					respMap);

			for (Map.Entry map : sortedMap.entrySet()) {

				List<Gstr3bEntityLevelReportDto> sortList = respMap
						.get(map.getKey()).stream()
						.sorted((o1, o2) -> o1.getTableSection()
								.compareTo(o2.getTableSection()))
						.collect(Collectors.toList());

				if (LOGGER.isDebugEnabled()) {
					String msg = "Begin GenerateErrorReportImpl.writeToExcel "
							+ "sortList Size = " + sortList.size();
					LOGGER.debug(msg);
				}
				List<Gstr3bEntityLevelReportFinalDto> finalList = sortList
						.stream().map(o -> convertToDecimalDto(o))
						.collect(Collectors.toList());

				if (LOGGER.isDebugEnabled()) {
					String msg = "Begin GenerateErrorReportImpl.writeToExcel "
							+ "finalList Size = " + finalList.size();
					LOGGER.debug(msg);
				}

				reportCells.importCustomObjects(finalList, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						sortList.size(), true, "yyyy-mm-dd", false);

				startRow = startRow + sortList.size() + 1;

			}
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "GenerateErrorReportImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}

				LocalDateTime timeOfGeneration = LocalDateTime.now();
				LocalDateTime convertISDDate = EYDateUtil
						.toISTDateTimeFromUTC(timeOfGeneration);
				DateTimeFormatter format = DateTimeFormatter
						.ofPattern("yyyyMMddHHmmss");
				String fileName = tempDir.getAbsolutePath() + File.separator
						+ "GSTR3B_Entity Level Summary_" + id + "_"
						+ format.format(convertISDDate) + ".xlsx";

				workbook.save(fileName, SaveFormat.XLSX);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " gstr3b response list in the directory : {}",
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

	private void addMissingDto(List<Gstr3bEntityLevelReportDto> v) {
		String gstin = v.get(0).getGstin();
		String taxPeriod = v.get(0).getTaxPeriod();
		Set<String> tableSectionSet = CommonUtility.Gstr3bTableHeadingAndDescription
				.keySet();
		List<String> respTableSecList = v.stream().map(o -> o.getTableSection())
				.collect(Collectors.toList());
		tableSectionSet.forEach(o -> {
			if (!respTableSecList.contains(o)) {
				v.add(getTable3MandatoryData(o, gstin, taxPeriod));
			}
		});
	}

	private Gstr3bEntityLevelReportFinalDto convertToDecimalDto(
			Gstr3bEntityLevelReportDto dto) {

		Gstr3bEntityLevelReportFinalDto obj = new Gstr3bEntityLevelReportFinalDto();

		obj.setGstin(dto.getGstin());
		obj.setTableSection(dto.getTableSection());
		obj.setTableHeading(dto.getTableHeading());
		obj.setTableDesc(dto.getTableDesc());
		obj.setComputeTaxableVal(
				appendDecimalDigit(dto.getComputeTaxableVal()));
		obj.setComputeIgst(appendDecimalDigit(dto.getComputeIgst()));
		obj.setComputeCgst(appendDecimalDigit(dto.getComputeCgst()));
		obj.setComputeSgst(appendDecimalDigit(dto.getComputeSgst()));
		obj.setComputeCess(appendDecimalDigit(dto.getComputeCess()));
		obj.setUserTaxableVal(appendDecimalDigit(dto.getUserTaxableVal()));
		obj.setUserIgst(appendDecimalDigit(dto.getUserIgst()));
		obj.setUserCgst(appendDecimalDigit(dto.getUserCgst()));
		obj.setUserSgst(appendDecimalDigit(dto.getUserSgst()));
		obj.setUserCess(appendDecimalDigit(dto.getUserCess()));
		obj.setGstnTaxableVal(appendDecimalDigit(dto.getGstnTaxableVal()));
		obj.setGstnIgst(appendDecimalDigit(dto.getGstnIgst()));
		obj.setGstnCgst(appendDecimalDigit(dto.getGstnCgst()));
		obj.setGstnSgst(appendDecimalDigit(dto.getGstnSgst()));
		obj.setGstnCess(appendDecimalDigit(dto.getGstnCess()));
		obj.setDiffTaxableVal(appendDecimalDigit(dto.getDiffTaxableVal()));
		obj.setDiffIgst(appendDecimalDigit(dto.getDiffIgst()));
		obj.setDiffCgst(appendDecimalDigit(dto.getDiffCgst()));
		obj.setDiffSgst(appendDecimalDigit(dto.getDiffSgst()));
		obj.setDiffCess(appendDecimalDigit(dto.getDiffCess()));
		obj.setTaxPeriod(DownloadReportsConstant.CSVCHARACTER
				.concat(dto.getTaxPeriod()));

		return obj;
	}

	private String appendDecimalDigit(BigDecimal b) {

		String val = b.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();

		String[] s = val.split("\\.");
		if (s.length == 2) {
			if (s[1].length() == 1)
				return "'" + (s[0] + "." + s[1] + "0");
			else {
				return "'" + val;
			}
		} else
			return "'" + (val + ".00");

	}

	private Gstr3bEntityLevelReportDto getTable3MandatoryData(String tableSec,
			String gstin, String taxPeriod) {

		Gstr3bEntityLevelReportDto obj = new Gstr3bEntityLevelReportDto();
		obj.setGstin(gstin);
		obj.setTableSection(tableSec);
		Pair<String, String> table = CommonUtility
				.getGstr3bHeadingandDesc(tableSec);
		obj.setTableHeading(table.getValue0());
		obj.setTableDesc(table.getValue1());
		obj.setComputeTaxableVal(BigDecimal.ZERO);
		obj.setComputeIgst(BigDecimal.ZERO);
		obj.setComputeCgst(BigDecimal.ZERO);
		obj.setComputeSgst(BigDecimal.ZERO);
		obj.setComputeCess(BigDecimal.ZERO);
		obj.setUserTaxableVal(BigDecimal.ZERO);
		obj.setUserIgst(BigDecimal.ZERO);
		obj.setUserCgst(BigDecimal.ZERO);
		obj.setUserSgst(BigDecimal.ZERO);
		obj.setUserCess(BigDecimal.ZERO);
		obj.setGstnTaxableVal(BigDecimal.ZERO);
		obj.setGstnIgst(BigDecimal.ZERO);
		obj.setGstnCgst(BigDecimal.ZERO);
		obj.setGstnSgst(BigDecimal.ZERO);
		obj.setGstnCess(BigDecimal.ZERO);
		obj.setDiffTaxableVal(BigDecimal.ZERO);
		obj.setDiffIgst(BigDecimal.ZERO);
		obj.setDiffCgst(BigDecimal.ZERO);
		obj.setDiffSgst(BigDecimal.ZERO);
		obj.setDiffCess(BigDecimal.ZERO);
		obj.setTaxPeriod(taxPeriod);

		return obj;

	}

	private Gstr3bEntityLevelReportDto sumOf(Gstr3bEntityLevelReportDto obj1,
			Gstr3bEntityLevelReportDto obj2) {

		Gstr3bEntityLevelReportDto obj = new Gstr3bEntityLevelReportDto();
		obj.setGstin(obj2.getGstin());
		obj.setTableSection(obj2.getTableSection());
		Pair<String, String> table = CommonUtility
				.getGstr3bHeadingandDesc(obj2.getTableSection());
		obj.setTableHeading(table.getValue0());
		obj.setTableDesc(table.getValue1());
		obj.setComputeTaxableVal(
				obj1.getComputeTaxableVal().add(obj2.getComputeTaxableVal()));
		obj.setComputeIgst(obj1.getComputeIgst().add(obj2.getComputeIgst()));
		obj.setComputeCgst(obj1.getComputeCgst().add(obj2.getComputeCgst()));
		obj.setComputeSgst(obj1.getComputeSgst().add(obj2.getComputeSgst()));
		obj.setComputeCess(obj1.getComputeCess().add(obj2.getComputeCess()));
		obj.setUserTaxableVal(
				obj1.getUserTaxableVal().add(obj2.getUserTaxableVal()));
		obj.setUserIgst(obj1.getUserIgst().add(obj2.getUserIgst()));
		obj.setUserCgst(obj1.getUserCgst().add(obj2.getUserCgst()));
		obj.setUserSgst(obj1.getUserSgst().add(obj2.getUserSgst()));
		obj.setUserCess(obj1.getUserCess().add(obj2.getUserCess()));
		obj.setGstnTaxableVal(
				obj1.getGstnTaxableVal().add(obj2.getGstnTaxableVal()));
		obj.setGstnIgst(obj1.getGstnIgst().add(obj2.getGstnIgst()));
		obj.setGstnCgst(obj1.getGstnCgst().add(obj2.getGstnCgst()));
		obj.setGstnSgst(obj1.getGstnSgst().add(obj2.getGstnSgst()));
		obj.setGstnCess(obj1.getGstnCess().add(obj2.getGstnCess()));
		obj.setDiffTaxableVal(
				obj1.getDiffTaxableVal().add(obj2.getDiffTaxableVal()));
		obj.setDiffIgst(obj1.getDiffIgst().add(obj2.getDiffIgst()));
		obj.setDiffCgst(obj1.getDiffCgst().add(obj2.getDiffCgst()));
		obj.setDiffSgst(obj1.getDiffSgst().add(obj2.getDiffSgst()));
		obj.setDiffCess(obj1.getDiffCess().add(obj2.getDiffCess()));
		obj.setTaxPeriod(obj2.getTaxPeriod());
		obj.setDerivedTaxPeriod(obj2.getDerivedTaxPeriod());
		return obj;
	}

	// method to sort values
	private HashMap<String, List<Gstr3bEntityLevelReportDto>> sortValues(
			Map<String, List<Gstr3bEntityLevelReportDto>> respMap) {
		List list = new LinkedList(respMap.entrySet());
		// Custom Comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getKey())
						.compareTo(((Map.Entry) (o2)).getKey());
			}
		});
		// copying the sorted list in HashMap to preserve the iteration order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

}
