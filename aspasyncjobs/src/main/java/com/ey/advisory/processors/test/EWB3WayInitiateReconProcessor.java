/**
 * 
 */
package com.ey.advisory.processors.test;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2ToleranceValueDto;
import com.ey.advisory.app.asprecon.gstr2.pr2b.reports.EWB3WayInitiateReconFetchReportDetails;
import com.ey.advisory.app.data.entities.client.asprecon.Recon3WayErrorLogEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Recon3WayToleranceLimitEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Recon3WayConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Recon3WayErrorLogsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Recon3WayGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Recon3WayProcedureRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Recon3WayToleranceLimitRepository;
import com.ey.advisory.app.recon3way.EWB3WayProcedureListDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.FormatValidationUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("EWB3WayInitiateReconProcessor")
public class EWB3WayInitiateReconProcessor implements TaskProcessor {

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Recon3WayConfigRepository")
	Recon3WayConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Recon3WayToleranceLimitRepository")
	Recon3WayToleranceLimitRepository reconToleranceRepo;

	@Autowired
	@Qualifier("Recon3WayProcedureRepository")
	Recon3WayProcedureRepository procRepo;

	@Autowired
	@Qualifier("Recon3WayGstinRepository")
	Recon3WayGstinRepository gstinRepo;

	@Autowired
	@Qualifier("Recon3WayErrorLogsRepository")
	Recon3WayErrorLogsRepository errorRepo;

	@Autowired
	@Qualifier("EWB3WayInitiateReconFetchReportDetailsImpl")
	EWB3WayInitiateReconFetchReportDetails fetchReportDetails;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin EWB3WayInitiateReconProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();

		execute3WayRecon(configId);
	}

	private void execute3WayRecon(Long configId) {

		BigDecimal cess = null;
		BigDecimal cgst = null;
		BigDecimal igst = null;
		BigDecimal sgst = null;
		BigDecimal taxableVal = null;
		BigDecimal totalTax = null;
		try {
			List<Gstr2ToleranceValueDto> toleranceValue = getToleranceValue(
					entityManager, configId);

			for (Gstr2ToleranceValueDto toleValue : toleranceValue) {
				cess = toleValue.getCess() != null
						? toleValue.getCess() : BigDecimal.ZERO;
				cgst = toleValue.getCgst() != null
						? toleValue.getCgst() : BigDecimal.ZERO;
				igst = toleValue.getIgst() != null
						? toleValue.getIgst() : BigDecimal.ZERO;
				sgst = toleValue.getSgst() != null
						? toleValue.getSgst() : BigDecimal.ZERO;
				taxableVal = toleValue.getTaxableVal() != null
						? toleValue.getTaxableVal()
						: BigDecimal.ZERO;
				//
				totalTax = toleValue.getTotalTax() != null
						? toleValue.getTotalTax()
						: BigDecimal.ZERO;

			}

			saveToTolerance(cess, sgst, cgst, igst, taxableVal, configId);
		} catch (Exception ex) {
			String msg = String.format("Error in getting Tolerance limit" + ex,
					configId.toString());
			LOGGER.error(msg);
		}
		String procName = null;
		String response = null;
		String gstin = null;
		try {
			List<EWB3WayProcedureListDto> procList = getProcList(configId);
			if (procList == null || procList.isEmpty()) {
				String msg = String.format("No Data Found To Reconciliation %d",
						configId);
				LOGGER.error(msg);
				reconConfigRepo.updateReconConfigStatusAndReportName(
						"No Data Found",
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						configId);
				throw new AppException(msg);
			}

			Map<Integer, String> procMap = new TreeMap<>();

			procMap = procList.stream().collect(Collectors
					.toMap(o -> o.getSequenceId(), o -> o.getProcedureName()));

			List<String> gstins = gstinRepo.findByConfigId(configId);

			for (String egstin : gstins) {
				gstin = egstin;
				for (Integer k : procMap.keySet())

				{
					procName = procMap.get(k);

					StoredProcedureQuery storedProc = procCall(cess, cgst, igst,
							sgst, taxableVal, totalTax, gstin, configId,
							procName);

					response = (String) storedProc.getSingleResult();

					LOGGER.debug(procName + " :: " + response);

					if (!ReconStatusConstants.SUCCESS
							.equalsIgnoreCase(response)) {

						String msg = String.format(
								"Config Id is '%s', Response "
										+ "from RECON_MASTER SP %s did not "
										+ "return success,"
										+ " Hence updating to Failed",
								configId.toString(), procName);
						LOGGER.error(msg);

						/*
						 * Recon3WayErrorLogEntity obj = new
						 * Recon3WayErrorLogEntity();
						 * 
						 * obj.setConfigId(configId); obj.setProcName(procName);
						 * 
						 * // to do clob
						 * obj.setErrMessage(GenUtil.convertStringToClob(
						 * response)); obj.setCreatedDate(LocalDateTime.now());
						 * obj.setGstin(gstin); //obj.setProcId(k);
						 * errorRepo.save(obj);
						 * 
						 */ /*
							 * reconConfigRepo.
							 * updateReconConfigStatusAndReportName(
							 * ReconStatusConstants.RECON_FAILED,
							 * EYDateUtil.toUTCDateTimeFromLocal(
							 * LocalDateTime.now()), configId);
							 */
						throw new AppException(msg);
					}
				}
			}
		} catch (Exception e) {
			String msg = String.format(
					"Config Id is '%s', Response "
							+ "from RECON_MASTER SP %s did not "
							+ "return success," + " Hence updating to Failed",
					configId.toString(), procName);

			Recon3WayErrorLogEntity obj = new Recon3WayErrorLogEntity();

			obj.setConfigId(configId);
			obj.setProcName(procName);
			obj.setErrMessage(GenUtil.convertStringToClob(response));
			obj.setCreatedDate(LocalDateTime.now());
			obj.setGstin(gstin);
			// obj.setProcId(k);
			errorRepo.save(obj);

			LOGGER.error(msg, e);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId);

			throw new AppException(e);
		}

		try {

			fetchReportDetails.getInitiateReconReportData(configId);

		} catch (Exception ex) {
			String msg = String.format("Error in report generation" + ex,
					configId.toString());
			LOGGER.error(msg);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.REPORT_GENERATION_FAILED,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId);
			throw new AppException(msg, ex);
		}
	}

	private List<EWB3WayProcedureListDto> getProcList(Long configId) {
		List<EWB3WayProcedureListDto> finallist = new ArrayList<EWB3WayProcedureListDto>();
		try {

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_3WAY_00_VALIDATE_GSTR1_EINV_EWB");

			storedProc.registerStoredProcedureParameter("P_RECON_CONFIG_ID",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("P_RECON_CONFIG_ID", configId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"call stored proc with "
								+ "params {} Config ID is '%s', ",
						configId.toString());
				LOGGER.debug(msg);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> records = storedProc.getResultList();

			if (records != null && !records.isEmpty()) {
				finallist = records.stream().map(o -> convertProcList(o))
						.collect(Collectors.toCollection(ArrayList::new));
			}

		} catch (Exception ex) {
			String msg = String.format("error in validate proc  %d", configId);
			LOGGER.error(msg, ex);

		}
		return finallist;
	}

	private StoredProcedureQuery procCall(BigDecimal cess, BigDecimal cgst,
			BigDecimal igst, BigDecimal sgst, BigDecimal taxableVal,
			BigDecimal totalTax, String gstin, Long configId, String procName) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute Recon SP with ConfigId :%s",
					configId.toString());
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_RECON_CONFIG_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_RECON_CONFIG_ID", configId);

		storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_GSTIN", gstin);

		storedProc.registerStoredProcedureParameter("P_TAXABLE_VALUE",
				BigDecimal.class, ParameterMode.IN);

		storedProc.setParameter("P_TAXABLE_VALUE", taxableVal);

		storedProc.registerStoredProcedureParameter("P_CGST", BigDecimal.class,
				ParameterMode.IN);

		storedProc.setParameter("P_CGST", cgst);

		storedProc.registerStoredProcedureParameter("P_SGST", BigDecimal.class,
				ParameterMode.IN);

		storedProc.setParameter("P_SGST", sgst);

		storedProc.registerStoredProcedureParameter("P_IGST", BigDecimal.class,
				ParameterMode.IN);

		storedProc.setParameter("P_IGST", igst);

		storedProc.registerStoredProcedureParameter("P_CESS", BigDecimal.class,
				ParameterMode.IN);

		storedProc.setParameter("P_CESS", cess);
		/*
		 * storedProc.registerStoredProcedureParameter("P_TOTAL_TAX",
		 * BigDecimal.class, ParameterMode.IN);
		 * 
		 * storedProc.setParameter("P_TOTAL_TAX", totalTax);
		 */
		return storedProc;
	}

	private static List<Gstr2ToleranceValueDto> getToleranceValue(
			EntityManager entityManager, Long configId) {

		String queryStr = "SELECT SUM(IFNULL(CASE WHEN QUESTION_SUB_CATEGORY='TAXABLE VALUE' THEN CAST(ANSWER AS DECIMAL(15,2)) END, 0.00)) AS TAXABLE_VALUE, " +
			    "SUM(IFNULL(CASE WHEN QUESTION_SUB_CATEGORY='IGST' THEN CAST(ANSWER AS DECIMAL(15,2)) END, 0.00)) AS IGST, " +
			    "SUM(IFNULL(CASE WHEN QUESTION_SUB_CATEGORY='CGST' THEN CAST(ANSWER AS DECIMAL(15,2)) END, 0.00)) AS CGST, " +
			    "SUM(IFNULL(CASE WHEN QUESTION_SUB_CATEGORY='SGST' THEN CAST(ANSWER AS DECIMAL(15,2)) END, 0.00)) AS SGST, " +
			    "SUM(IFNULL(CASE WHEN QUESTION_SUB_CATEGORY='CESS' THEN CAST(ANSWER AS DECIMAL(15,2)) END, 0.00)) AS CESS " +
			"FROM " +
			    "ENTITY_CONFG_PRMTR P " +
			    "INNER JOIN TBL_3WAY_RECON_CONFIG RC ON P.ENTITY_ID=RC.ENTITY_ID AND P.IS_DELETE=FALSE AND P.IS_ACTIVE=TRUE " +
			    "INNER JOIN CONFG_QUESTION Q ON Q.ID=P.CONFG_QUESTION_ID AND Q.QUESTION_CODE='O27' AND Q.IS_ACTIVE=TRUE " +
			"WHERE " +
			    "P.QUESTION_CODE='O27' AND IS_DELETE=FALSE AND P.IS_ACTIVE=TRUE AND RECON_CONFIG_ID=:configId";
		
	/*	String queryStr = "SELECT SUM(IFNULL(CASE  "
				+ "WHEN QUESTION_SUB_CATEGORY='TAXABLE VALUE' "
				+ "THEN CAST(ANSWER AS DECIMAL(15,2)) END, 0.00) )"
				+ " AS TAXABLE_VALUE, "
				+ "SUM(IFNULL(CASE WHEN QUESTION_SUB_CATEGORY='IGST' "
				+ "THEN CAST(ANSWER AS DECIMAL(15,2))  END, 0.00)) AS IGST, "
				+ "SUM(IFNULL(CASE WHEN QUESTION_SUB_CATEGORY='CGST' THEN "
				+ "CAST(ANSWER AS DECIMAL(15,2)) ND, 0.00) ) AS CGST, "
				+ "SUM(IFNULL(CASE WHEN QUESTION_SUB_CATEGORY='SGST' "
				+ "THEN CAST(ANSWER AS DECIMAL(15,2))  END, 0.00) ) AS SGST, "
				+ "SUM(IFNULL(CASE WHEN QUESTION_SUB_CATEGORY='CESS' "
				+ "THEN CAST(ANSWER AS DECIMAL(15,2)) END, 0.00)) AS CESS    "
				+ "FROM ENTITY_CONFG_PRMTR P "
				+ "INNER JOIN TBL_3WAY_RECON_CONFIG RC "
				+ "ON P.ENTITY_ID=RC.ENTITY_ID AND P.IS_DELETE=FALSE "
				+ "AND P.IS_ACTIVE=TRUE INNER JOIN CONFG_QUESTION Q "
				+ "ON Q.ID=P.CONFG_QUESTION_ID AND Q.QUESTION_CODE='O27' "
				+ "AND Q.IS_ACTIVE=TRUE  WHERE P.QUESTION_CODE='O27' "
				+ "AND IS_DELETE=FALSE AND P.IS_ACTIVE=TRUE "
				+ "AND RECON_CONFIG_ID=:configId";*/
		
		
		
		

		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter("configId", configId);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing query to getToleranceValue :: configId "
					+ configId);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Gstr2ToleranceValueDto> retList = list.stream()
				.map(o -> convertUserInput(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;

	}

	private static EWB3WayProcedureListDto convertProcList(Object[] arr) {
		EWB3WayProcedureListDto dto = new EWB3WayProcedureListDto();
		dto.setProcedureName((String) arr[0]);
		dto.setSequenceId((Integer) arr[1]);

		return dto;
	}

	private void saveToTolerance(BigDecimal cess, BigDecimal sgst,
			BigDecimal cgst, BigDecimal igst, BigDecimal taxableVal,
			Long configId) {
		Recon3WayToleranceLimitEntity entity = new Recon3WayToleranceLimitEntity();
		entity.setConfigId(configId);
		entity.setTaxableValue(taxableVal);
		entity.setGCess(cess);
		entity.setCGST(cgst);
		entity.setIGST(igst);
		entity.setSGST(sgst);
		entity.setCreatedDate(LocalDate.now());
		reconToleranceRepo.save(entity);
	}

	private static Gstr2ToleranceValueDto convertUserInput(Object[] o) {

		Gstr2ToleranceValueDto dto = new Gstr2ToleranceValueDto();
		try {
			dto.setTaxableVal((BigDecimal) o[0]);
			dto.setIgst((BigDecimal) getDefaultvalue(o[1]));
			dto.setCgst((BigDecimal) getDefaultvalue(o[2]));
			dto.setSgst((BigDecimal) getDefaultvalue(o[3]));
			dto.setCess((BigDecimal) getDefaultvalue(o[4]));
			//// To-Do
			dto.setTotalTax((BigDecimal.ZERO));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while fetching Tolerance Value {object}:: %s ", o);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return dto;
	}

	private static BigDecimal getDefaultvalue(Object o) {
		if (isPresent(o)) {
			if (!FormatValidationUtil.isDecimal(o)) {
				return BigDecimal.ZERO;
			}
		}

		return ((BigDecimal) o);

	}

}
