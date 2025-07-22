/**
 * 
 */
package com.ey.advisory.processors.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.SalesRegisterConfigRepository;
import com.ey.advisory.app.data.repositories.client.SalesRegisterProcedureRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.service.gstr1.sales.register.Gstr1SalesRegisterInitiateReconFetchReportDetails;
import com.ey.advisory.service.gstr1.sales.register.SalesRegisterProcedureEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("SalesRegisterInitiateReconProcessor")
public class SalesRegisterInitiateReconProcessor implements TaskProcessor {

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("SalesRegisterConfigRepository")
	SalesRegisterConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("SalesRegisterProcedureRepository")
	SalesRegisterProcedureRepository procRepo;

	@Autowired
	@Qualifier("Gstr1SalesRegisterInitiateReconFetchReportDetailsImpl")
	Gstr1SalesRegisterInitiateReconFetchReportDetails fetchReportDetails;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin SalesRegisterInitiateReconProcessor :%s",
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

		BigDecimal cess = new BigDecimal(10);
		BigDecimal cgst = new BigDecimal(10);
		BigDecimal igst = new BigDecimal(10);
		BigDecimal sgst = new BigDecimal(10);
		BigDecimal invoiceValue = new BigDecimal(10);
		BigDecimal taxableVal = new BigDecimal(10);

		String procName = null;
		String response = null;
		String gstin = null;
		try {
			List<SalesRegisterProcedureEntity> procList = procRepo.findProcedure();
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
					.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			List<String>  gstins = reconConfigRepo.findByConfigId(configId);
						
			for (String egstin : gstins) {
				gstin = egstin;
				for (Integer k : procMap.keySet())

				{
					procName = procMap.get(k);

					StoredProcedureQuery storedProc = procCall(configId, gstin,
							invoiceValue, taxableVal, cgst, sgst, igst, cess,
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

	private StoredProcedureQuery procCall(Long configId, String gstin,
			BigDecimal invoiceValue, BigDecimal taxableVal, BigDecimal cgst,
			BigDecimal sgst, BigDecimal igst, BigDecimal cess,
			String procName) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute Recon SP with ConfigId :%s",
					configId.toString());
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_RECON_REPORT_CONFIG_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

		storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_GSTIN", gstin);

		storedProc.registerStoredProcedureParameter("P_INVOICE_VALUE",
				BigDecimal.class, ParameterMode.IN);

		storedProc.setParameter("P_INVOICE_VALUE", invoiceValue);
		
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

		return storedProc;
	}

}
