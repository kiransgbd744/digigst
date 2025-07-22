package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.ReplaceOptions;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.docs.dto.PermissibleITC10PerctDwnldDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh NK
 *
 */
@Service("PermissibleITC10PerctDwnldServiceImpl")
@Slf4j
public class PermissibleITC10PerctDwnldServiceImpl
		implements PermissibleITC10PerctDwnldService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	private EntityConfigPrmtRepository qstnRepo;

	@SuppressWarnings("unchecked")
	@Override
	public Workbook getPermissibleReport(List<String> gstnsList,
			String toTaxPeriod, String fromTaxPeriod, List<String> docType,
			String reconType, Long entityId) {

		Workbook workbook = null;
		boolean is2APR = false;
		String itcPerc = null;
		BigDecimal totalItc = BigDecimal.ZERO;
		BigDecimal totalIgst = BigDecimal.ZERO;
		BigDecimal totalCgst = BigDecimal.ZERO;
		BigDecimal totalSgst = BigDecimal.ZERO;
		BigDecimal totalCess = BigDecimal.ZERO;
		BigDecimal totalAvlItc = BigDecimal.ZERO;
		BigDecimal totalAvailableIgst = BigDecimal.ZERO;
		BigDecimal totalAvailableCgst = BigDecimal.ZERO;
		BigDecimal totalAvailableSgst = BigDecimal.ZERO;
		BigDecimal totalAvailableCess = BigDecimal.ZERO;
		BigDecimal totalPermItc = BigDecimal.ZERO;
		BigDecimal totalPermIgst = BigDecimal.ZERO;
		BigDecimal totalPermCgst = BigDecimal.ZERO;
		BigDecimal totalPermSgst = BigDecimal.ZERO;
		BigDecimal totalPermCess = BigDecimal.ZERO;
		BigDecimal totalDeferredItc = BigDecimal.ZERO;
		BigDecimal totalDeferredIgst = BigDecimal.ZERO;
		BigDecimal totalDeferredCgst = BigDecimal.ZERO;
		BigDecimal totalDeferredSgst = BigDecimal.ZERO;
		BigDecimal totalDeferredCess = BigDecimal.ZERO;
		BigDecimal totalAcceptedPRItc = BigDecimal.ZERO;
		BigDecimal totalAcceptedPRIgst = BigDecimal.ZERO;
		BigDecimal totalAcceptedPRCgst = BigDecimal.ZERO;
		BigDecimal totalAcceptedPRSgst = BigDecimal.ZERO;
		BigDecimal totalAcceptedPRCess = BigDecimal.ZERO;
		BigDecimal totalAccepted2AItc = BigDecimal.ZERO;
		BigDecimal totalAccepted2AIgst = BigDecimal.ZERO;
		BigDecimal totalAccepted2ACgst = BigDecimal.ZERO;
		BigDecimal totalAccepted2ASgst = BigDecimal.ZERO;
		BigDecimal totalAccepted2ACess = BigDecimal.ZERO;

		List<Object[]> list = null;

		if (docType == null || docType.isEmpty()) {
			docType.add("R");
			docType.add("C");
			docType.add("D");
		}

		String docTypes = String.join(",", docType);

		Integer fromTaxPer = Integer.valueOf(fromTaxPeriod.substring(2)
				.concat(fromTaxPeriod.substring(0, 2)));

		Integer toTaxPer = Integer.valueOf(
				toTaxPeriod.substring(2).concat(toTaxPeriod.substring(0, 2)));

		String gstins = String.join(",", gstnsList);

		LOGGER.debug(
				"Calling stored proc BEGIN for Permissible ITC10p {} report download ",
				reconType);

		StoredProcedureQuery storedProc = null;

		if ("2A_PR".equalsIgnoreCase(reconType)) {
			is2APR = true;
			storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_ITC_PERM_RPT");
		} else if ("2B_PR".equalsIgnoreCase(reconType)) {
			storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_2BPR_ITC_PERM_RPT");
		}

		storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
				String.class, ParameterMode.IN);

		storedProc.setParameter("P_GSTIN_LIST", gstins);

		storedProc.registerStoredProcedureParameter("P_FROM_TXPRD",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_FROM_TXPRD", fromTaxPer);

		storedProc.registerStoredProcedureParameter("P_TO_TXPRD", Integer.class,
				ParameterMode.IN);

		storedProc.setParameter("P_TO_TXPRD", toTaxPer);

		storedProc.registerStoredProcedureParameter("P_DOCTYPE", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_DOCTYPE", docTypes);

		list = storedProc.getResultList();

		LOGGER.debug(
				"Calling stored proc ENDED for Permissible ITC10p report download");

		long dbLoadStTime = System.currentTimeMillis();

		long dbLoadEndTime = System.currentTimeMillis();
		long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Total Time taken to load the Data"
							+ " from DB is '%d' millisecs Data count: '%s'",
					dbLoadTimeDiff, list.size());
			LOGGER.debug(msg);
		}

		LOGGER.debug("Converting Query And converting to List BEGIN");
		List<PermissibleITC10PerctDwnldDto> permItc10Perc = list.stream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		LOGGER.debug("Converting Query And converting to List END");

		for (int i = 0; i < permItc10Perc.size(); i++) {

			totalItc = totalItc.add(permItc10Perc.get(i).getTotalItc());
			totalIgst = totalIgst.add(permItc10Perc.get(i).getTotalIgst());
			totalCgst = totalCgst.add(permItc10Perc.get(i).getTotalCgst());
			totalSgst = totalSgst.add(permItc10Perc.get(i).getTotalSgst());
			totalCess = totalCess.add(permItc10Perc.get(i).getTotalCess());
			totalAvlItc = totalAvlItc
					.add(permItc10Perc.get(i).getTotalAvlItc());
			totalAvailableIgst = totalAvailableIgst
					.add(permItc10Perc.get(i).getTotalAvailableIgst());
			totalAvailableCgst = totalAvailableCgst
					.add(permItc10Perc.get(i).getTotalAvailableCgst());
			totalAvailableSgst = totalAvailableSgst
					.add(permItc10Perc.get(i).getTotalAvailableSgst());
			totalAvailableCess = totalAvailableCess
					.add(permItc10Perc.get(i).getTotalAvailableCess());
			totalPermItc = totalPermItc
					.add(permItc10Perc.get(i).getTotalPermItc());
			totalPermIgst = totalPermIgst
					.add(permItc10Perc.get(i).getTotalPermIgst());
			totalPermCgst = totalPermCgst
					.add(permItc10Perc.get(i).getTotalPermCgst());
			totalPermSgst = totalPermSgst
					.add(permItc10Perc.get(i).getTotalPermSgst());
			totalPermCess = totalPermCess
					.add(permItc10Perc.get(i).getTotalPermCess());
			totalDeferredItc = totalDeferredItc
					.add(permItc10Perc.get(i).getTotalDeferredItc());
			totalDeferredIgst = totalDeferredIgst
					.add(permItc10Perc.get(i).getTotalDeferredIgst());
			totalDeferredCgst = totalDeferredCgst
					.add(permItc10Perc.get(i).getTotalDeferredCgst());
			totalDeferredSgst = totalDeferredSgst
					.add(permItc10Perc.get(i).getTotalDeferredSgst());
			totalDeferredCess = totalDeferredCess
					.add(permItc10Perc.get(i).getTotalDeferredCess());
			totalAcceptedPRItc = totalAcceptedPRItc
					.add(permItc10Perc.get(i).getTotalAcceptedPRItc());
			totalAcceptedPRIgst = totalAcceptedPRIgst
					.add(permItc10Perc.get(i).getTotalAcceptedPRIgst());
			totalAcceptedPRCgst = totalAcceptedPRCgst
					.add(permItc10Perc.get(i).getTotalAcceptedPRCgst());
			totalAcceptedPRSgst = totalAcceptedPRSgst
					.add(permItc10Perc.get(i).getTotalAcceptedPRSgst());
			totalAcceptedPRCess = totalAcceptedPRCess
					.add(permItc10Perc.get(i).getTotalAcceptedPRCess());
			totalAccepted2AItc = totalAccepted2AItc
					.add(permItc10Perc.get(i).getTotalAccepted2AItc());
			totalAccepted2AIgst = totalAccepted2AIgst
					.add(permItc10Perc.get(i).getTotalAccepted2AIgst());
			totalAccepted2ACgst = totalAccepted2ACgst
					.add(permItc10Perc.get(i).getTotalAccepted2ACgst());
			totalAccepted2ASgst = totalAccepted2ASgst
					.add(permItc10Perc.get(i).getTotalAccepted2ASgst());
			totalAccepted2ACess = totalAccepted2ACess
					.add(permItc10Perc.get(i).getTotalAccepted2ACess());

		}
		LOGGER.debug(
				"Calculating total value for PermissibleITC10Perct completed");

		PermissibleITC10PerctDwnldDto perItc = new PermissibleITC10PerctDwnldDto();
		perItc.setTaxPeriod("Total");
		perItc.setTotalItc(totalItc);
		perItc.setTotalIgst(totalIgst);
		perItc.setTotalCgst(totalCgst);
		perItc.setTotalSgst(totalSgst);
		perItc.setTotalCess(totalCess);
		perItc.setTotalAvlItc(totalAvlItc);
		perItc.setTotalAvailableIgst(totalAvailableIgst);
		perItc.setTotalAvailableCgst(totalAvailableCgst);
		perItc.setTotalAvailableSgst(totalAvailableSgst);
		perItc.setTotalAvailableCess(totalAvailableCess);
		perItc.setTotalPermItc(totalPermItc);
		perItc.setTotalPermIgst(totalPermIgst);
		perItc.setTotalPermCgst(totalPermCgst);
		perItc.setTotalPermSgst(totalPermSgst);
		perItc.setTotalPermCess(totalPermCess);
		perItc.setTotalDeferredItc(totalDeferredItc);
		perItc.setTotalDeferredIgst(totalDeferredIgst);
		perItc.setTotalDeferredCgst(totalDeferredCgst);
		perItc.setTotalDeferredSgst(totalDeferredSgst);
		perItc.setTotalDeferredCess(totalDeferredCess);
		perItc.setTotalAcceptedPRItc(totalAcceptedPRItc);
		perItc.setTotalAcceptedPRIgst(totalAcceptedPRIgst);
		perItc.setTotalAcceptedPRCgst(totalAcceptedPRCgst);
		perItc.setTotalAcceptedPRSgst(totalAcceptedPRSgst);
		perItc.setTotalAcceptedPRCess(totalAcceptedPRCess);
		perItc.setTotalAccepted2AItc(totalAccepted2AItc);
		perItc.setTotalAccepted2AIgst(totalAccepted2AIgst);
		perItc.setTotalAccepted2ACgst(totalAccepted2ACgst);
		perItc.setTotalAccepted2ASgst(totalAccepted2ASgst);
		perItc.setTotalAccepted2ACess(totalAccepted2ACess);

		List<PermissibleITC10PerctDwnldDto> total = new ArrayList<>();
		total.add(perItc);

		EntityConfigPrmtEntity e = qstnRepo
				.findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
						TenantContext.getTenantId(), entityId, "I23");
		if (e == null) {
			itcPerc = "5";
		} else {
			itcPerc = getValidPercentage(e.getParamValue());
		}

		workbook = writeToExcel(permItc10Perc, total, is2APR, itcPerc);

		return workbook;
	}

	@SuppressWarnings("deprecation")
	private Workbook writeToExcel(
			List<PermissibleITC10PerctDwnldDto> permItc10Perc,
			List<PermissibleITC10PerctDwnldDto> total, boolean is2APR,
			String itcPerc) {

		Workbook workbook = null;
		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (permItc10Perc != null && !permItc10Perc.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("itc.permissible.summary.report.header")
					.split(",");
			if (is2APR) {
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates",
						"Permisible_ITC_as_per_Rule_36(4).xlsx");
			} else {
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates",
						"Permisible_ITC_as_per_Rule_36(4)_2BPR.xlsx");

			}
			if (LOGGER.isDebugEnabled()) {
				String msg = "PermissibleITC10PerctDwnldServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			ReplaceOptions replace = new ReplaceOptions();
			replace.setCaseSensitive(false);
			replace.setMatchEntireCellContents(false);

			workbook.replace("<<perc>>", itcPerc, replace);
			reportCells.importCustomObjects(permItc10Perc, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					permItc10Perc.size(), true, "yyyy-mm-dd", false);

			startRow = startRow + permItc10Perc.size();
			startcolumn = 0;

			reportCells.importCustomObjects(total, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn, total.size(), true,
					"yyyy-mm-dd", false);
			Style style = workbook.createStyle();
			Font font = style.getFont();
			font.setBold(true);
			// For each cell in the row
			for (int i = 0; i < reportCells.getMaxDataColumn() - 1; i++) {
				reportCells.getRow(startRow).getCellByIndex(i).setStyle(style);

			}

			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "PermissibleITC10PerctDwnldServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.PERMISSIBLE_ITC_10_PERC,
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

	private PermissibleITC10PerctDwnldDto convert(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to "
					+ " PermissibleITC10PerctDwnldDto object";
			LOGGER.debug(str);
		}

		PermissibleITC10PerctDwnldDto obj = new PermissibleITC10PerctDwnldDto();

		obj.setRgstin(arr[0] != null ? arr[0].toString() : null);
		obj.setVgstin(arr[1] != null ? arr[1].toString() : null);
		obj.setTaxPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setTotalItc(arr[3] != null ? (BigDecimal) arr[3] : BigDecimal.ZERO);
		obj.setTotalIgst(
				arr[4] != null ? (BigDecimal) arr[4] : BigDecimal.ZERO);
		obj.setTotalCgst(
				arr[5] != null ? (BigDecimal) arr[5] : BigDecimal.ZERO);
		obj.setTotalSgst(
				arr[6] != null ? (BigDecimal) arr[6] : BigDecimal.ZERO);
		obj.setTotalCess(
				arr[7] != null ? (BigDecimal) arr[7] : BigDecimal.ZERO);
		obj.setTotalAvlItc(
				arr[8] != null ? (BigDecimal) arr[8] : BigDecimal.ZERO);
		obj.setTotalAvailableIgst(
				arr[9] != null ? (BigDecimal) arr[9] : BigDecimal.ZERO);
		obj.setTotalAvailableCgst(
				arr[10] != null ? (BigDecimal) arr[10] : BigDecimal.ZERO);
		obj.setTotalAvailableSgst(
				arr[11] != null ? (BigDecimal) arr[11] : BigDecimal.ZERO);
		obj.setTotalAvailableCess(
				arr[12] != null ? (BigDecimal) arr[12] : BigDecimal.ZERO);
		obj.setTotalPermItc(
				arr[13] != null ? (BigDecimal) arr[13] : BigDecimal.ZERO);
		obj.setTotalPermIgst(
				arr[14] != null ? (BigDecimal) arr[14] : BigDecimal.ZERO);
		obj.setTotalPermCgst(
				arr[15] != null ? (BigDecimal) arr[15] : BigDecimal.ZERO);
		obj.setTotalPermSgst(
				arr[16] != null ? (BigDecimal) arr[16] : BigDecimal.ZERO);
		obj.setTotalPermCess(
				arr[17] != null ? (BigDecimal) arr[17] : BigDecimal.ZERO);
		obj.setTotalDeferredItc(
				arr[18] != null ? (BigDecimal) arr[18] : BigDecimal.ZERO);
		obj.setTotalDeferredIgst(
				arr[19] != null ? (BigDecimal) arr[19] : BigDecimal.ZERO);
		obj.setTotalDeferredCgst(
				arr[20] != null ? (BigDecimal) arr[20] : BigDecimal.ZERO);
		obj.setTotalDeferredSgst(
				arr[21] != null ? (BigDecimal) arr[21] : BigDecimal.ZERO);
		obj.setTotalDeferredCess(
				arr[22] != null ? (BigDecimal) arr[22] : BigDecimal.ZERO);
		obj.setTotalAcceptedPRItc(
				arr[23] != null ? (BigDecimal) arr[23] : BigDecimal.ZERO);
		obj.setTotalAcceptedPRIgst(
				arr[24] != null ? (BigDecimal) arr[24] : BigDecimal.ZERO);
		obj.setTotalAcceptedPRCgst(
				arr[25] != null ? (BigDecimal) arr[25] : BigDecimal.ZERO);
		obj.setTotalAcceptedPRSgst(
				arr[26] != null ? (BigDecimal) arr[26] : BigDecimal.ZERO);
		obj.setTotalAcceptedPRCess(
				arr[27] != null ? (BigDecimal) arr[27] : BigDecimal.ZERO);
		obj.setTotalAccepted2AItc(
				arr[28] != null ? (BigDecimal) arr[28] : BigDecimal.ZERO);
		obj.setTotalAccepted2AIgst(
				arr[29] != null ? (BigDecimal) arr[29] : BigDecimal.ZERO);
		obj.setTotalAccepted2ACgst(
				arr[30] != null ? (BigDecimal) arr[30] : BigDecimal.ZERO);
		obj.setTotalAccepted2ASgst(
				arr[31] != null ? (BigDecimal) arr[31] : BigDecimal.ZERO);
		obj.setTotalAccepted2ACess(
				arr[32] != null ? (BigDecimal) arr[32] : BigDecimal.ZERO);

		return obj;
	}

	private String getValidPercentage(String percentage) {
		try {
			double value = Double.parseDouble(percentage);
			if (value < 0)
				return "5";
			else
				return percentage;
		} catch (NumberFormatException e) {
			return "5";
		}
	}
}
