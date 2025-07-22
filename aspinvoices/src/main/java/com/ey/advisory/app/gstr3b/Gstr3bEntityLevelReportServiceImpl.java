package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
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
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */
@Slf4j
@Component("Gstr3bEntityLevelReportServiceImpl")
public class Gstr3bEntityLevelReportServiceImpl
		implements Gstr3bEntityLevelReportService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	CommonUtility commonUtility;

	final static List<String> sectionList = ImmutableList.of(
			Gstr3BConstants.Table3_1, Gstr3BConstants.Table3_2,
			Gstr3BConstants.Table4, Gstr3BConstants.Table4A,
			Gstr3BConstants.Table4B, Gstr3BConstants.Table4D,
			Gstr3BConstants.Table5, Gstr3BConstants.Table5_1,
			Gstr3BConstants.Table5A, Gstr3BConstants.Table5B);

	@Override
	public Workbook getGstr3bReportData(String taxPeriod,
			List<String> gstinsList, String entityId) {

		List<Object[]> list = null;
		Workbook workbook = null;

		String gstins = String.join(",", gstinsList);

		StoredProcedureQuery storedProc = entityManager
				.createNamedStoredProcedureQuery("Gstr3bSummaryReport");

		storedProc.setParameter("P_GSTIN", gstins);

		storedProc.setParameter("P_TAX_PERIOD", taxPeriod);

		list = storedProc.getResultList();

		List<Gstr3bEntityLevelReportDto> gstr3bList = list.stream()
				.filter(o -> commonUtility.Gstr3bTableHeadingAndDescription
						.containsKey(o[1].toString()))
				.map(o -> converttoDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		Map<String, List<Gstr3bEntityLevelReportDto>> groupByMap = gstr3bList
				.stream().collect(Collectors.groupingBy(o -> o.getGstin()));

		gstinsList.removeAll(groupByMap.keySet());

		List<Gstr3bEntityLevelReportDto> unSavedGstinList = converttoDefaultDto(
				gstinsList);

		gstr3bList.addAll(unSavedGstinList);

		Long entityIds = Long.valueOf(entityId);

		Optional<EntityInfoEntity> optional = entityInfoRepo
				.findById(entityIds);
		EntityInfoEntity entity = optional.get();
		String entityName = entity.getEntityName();

		workbook = writeToExcel(gstr3bList, entityName, taxPeriod);

		return workbook;

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

		return obj;

	}

	private Workbook writeToExcel(
			List<Gstr3bEntityLevelReportDto> gstr3bRespList, String entityName,
			String taxPeriod) {
		Workbook workbook = null;
		int startRow = 6;
		int startcolumn = 1;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin GenerateErrorReportImpl.writeToExcel "
					+ "errorList Size = " + gstr3bRespList.size();
			LOGGER.debug(msg);
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
					+ LocalDate.now() + " | Time - " + LocalTime.now()
					+ "| Tax Period - " + taxPeriod);
			Style style = workbook.createStyle();
			Font font = style.getFont();
			font.setBold(true);
			font.setSize(12);

			Map<String, List<Gstr3bEntityLevelReportDto>> groupByMap = gstr3bRespList
					.stream().collect(Collectors.groupingBy(
							o -> o.getGstin() + o.getTableSection()));

			Map<String, List<Gstr3bEntityLevelReportDto>> respMap = new HashMap<>();
			List<Gstr3bEntityLevelReportDto> list = new ArrayList<>();
			groupByMap.forEach((K, V) -> {
				Gstr3bEntityLevelReportDto dto = V.stream().reduce(
						new Gstr3bEntityLevelReportDto(),
						(o1, o2) -> sumOf(o1, o2));
				list.add(dto);
			});
			respMap = list.stream()
					.collect(Collectors.groupingBy(o -> o.getGstin()));
			respMap.forEach((k, v) -> addMissingDto(v));

			for (Map.Entry map : respMap.entrySet()) {

				List<Gstr3bEntityLevelReportDto> sortList = respMap
						.get(map.getKey()).stream()
						.sorted((o1, o2) -> o1.getTableSection()
								.compareTo(o2.getTableSection()))
						.collect(Collectors.toList());

				List<Gstr3bEntityLevelReportFinalDto> finalList = sortList
						.stream().map(o -> convertToDecimalDto(o))
						.collect(Collectors.toList());

				reportCells.importCustomObjects(finalList, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						sortList.size(), true, "yyyy-mm-dd", false);

				startRow = startRow + sortList.size() + 2;

			}
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "GenerateErrorReportImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.GSTR3B_SUMMARY_REPORT,
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

	private Gstr3bEntityLevelReportFinalDto convertToDecimalDto(
			Gstr3bEntityLevelReportDto dto) {

		Gstr3bEntityLevelReportFinalDto obj = new Gstr3bEntityLevelReportFinalDto();

		obj.setGstin(dto.getGstin());
		obj.setTableSection(dto.getTableSection());
		obj.setTableHeading(dto.getTableHeading());
		obj.setTableDesc(dto.getTableDesc());
		obj.setComputeTaxableVal(appendDecimalDigit(dto.getComputeTaxableVal()));
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

		return obj;
	}

	private void addMissingDto(List<Gstr3bEntityLevelReportDto> v) {
		String gstin = v.get(0).getGstin();
		Set<String> tableSectionSet = commonUtility.Gstr3bTableHeadingAndDescription
				.keySet();
		List<String> respTableSecList = v.stream().map(o -> o.getTableSection())
				.collect(Collectors.toList());
		tableSectionSet.forEach(o -> {
			if (!respTableSecList.contains(o)) {
				v.add(getTable3MandatoryData(o, gstin));
			}
		});
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

		return obj;
	}

	private Gstr3bEntityLevelReportDto getTable3MandatoryData(String tableSec,
			String gstin) {

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

		return obj;

	}

	private List<Gstr3bEntityLevelReportDto> converttoDefaultDto(
			List<String> gstinList) {

		List<Gstr3bEntityLevelReportDto> defaultGstrbList = new ArrayList<>();
		for (String gstin : gstinList) {

			Set<String> tableSection = commonUtility.Gstr3bTableHeadingAndDescription
					.keySet();
			for (String tableSec : tableSection) {
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
				defaultGstrbList.add(obj);
			}
		}
		return defaultGstrbList;

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

	@Override
	public String getGstr3bOnboardingDataByEntityId(String entityID) {
	    String result = null;
	    try {
	        String queryString = createQuery();

	        Query q = entityManager.createNativeQuery(queryString);
	        q.setParameter("entityID", entityID); // Set the parameter

	        result = (String) q.getSingleResult();

	    } catch (NoResultException nre) {
	        LOGGER.error("No result found for ENTITY_ID: {}", entityID, nre);
	    } catch (Exception ex) {
	        String msg = "Error occurred while executing Gstr3b Onboarding query: " + ex.getMessage();
	        LOGGER.error(msg, ex);
	        throw new AppException(msg, ex);
	    }
	    return result;
	}

	private String createQuery() {
		/*
		 * return " SELECT CASE WHEN ANSWER = 'A' THEN 'Purchase Register' " +
		 * "WHEN ANSWER = 'B' THEN 'GSTR-2B' " +
		 * "WHEN ANSWER = 'C' THEN 'Both (PR & 2B)' " + "END AS OPT " +
		 * "FROM ENTITY_CONFG_PRMTR " +
		 * "WHERE CONFG_QUESTION_ID IN (SELECT ID FROM CONFG_QUESTION WHERE " +
		 * "QUESTION_DESCRIPTION = 'What is the base for computing GSTR-3B values for Table 4- Eligible ITC') "
		 * + "AND IS_DELETE = FALSE " + "AND ENTITY_ID = :entityID"; }
		 */
		return "SELECT CASE "
				+ "        WHEN ANSWER = 'A' THEN 'Purchase Register' "
				+ "        WHEN ANSWER = 'B' THEN 'GSTR-2B' "
				+ "        WHEN ANSWER = 'C' THEN 'Both (PR & 2B)' "
				+ "        ELSE 'Unknown' " + "    END AS OPT "
				+ "        FROM ENTITY_CONFG_PRMTR WHERE CONFG_QUESTION_ID IN ( "
				+ "    SELECT ID  FROM CONFG_QUESTION "
				+ "    WHERE QUESTION_DESCRIPTION = 'What is the base for computing GSTR-3B values for Table 4- Eligible ITC' "
				+ ")   AND IS_DELETE = FALSE AND ENTITY_ID = :entityID "
				+ "ORDER BY CREATED_ON DESC LIMIT 1;";

	}

}
