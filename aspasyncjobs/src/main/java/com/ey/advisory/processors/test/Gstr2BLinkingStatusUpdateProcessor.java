package com.ey.advisory.processors.test;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr2bLinkingConfigRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2BLinkingStatusUpdateProcessor")
public class Gstr2BLinkingStatusUpdateProcessor implements TaskProcessor {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private Gstr2bLinkingConfigRepository gstr2bLinkingConfigRepository;

	private static final String SUCCESS = GSTConstants.GSTR2B_LINKING_SUCCESS;
	private static final String FAILED = GSTConstants.GSTR2B_LINKING_FAILED;
	private static final String INPROGRESS = GSTConstants.GSTR2B_LINKING_INPROGRESS;
	private static final String NO_DATA = "NO DATA TO LINK";
	private static final String PARTIAL_SUCCESS = "PARTIAL_SUCCESS";

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			// Parse input parameters
			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Long id = json.get("batchId").getAsLong();
			String gstin = json.get(APIConstants.GSTIN).getAsString();
			String taxPeriod = json.get(APIConstants.TAXPERIOD).getAsString();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Processing GSTR2B Linking for Batch ID: {}, GSTIN: {}, Tax Period: {}",
						id, gstin, taxPeriod);
			}

			// At first will mark it as inprogress
			gstr2bLinkingConfigRepository.gstr2bLinkingUpdateStatus(id,
					GSTConstants.GSTR2B_LINKING_INPROGRESS);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Updated status for Batch ID: {} to {}", id,
						GSTConstants.GSTR2B_LINKING_INPROGRESS);
			}

			// List of stored procedure names to be called sequentially
			List<String> storedProcedures = Arrays.asList(
					"USP_GETGSTR2B_B2B_2A_IMS_LINKING",
					"USP_GETGSTR2B_B2BA_2A_IMS_LINKING",
					"USP_GETGSTR2B_ECOM_2A_IMS_LINKING",
					"USP_GETGSTR2B_ECOMA_2A_IMS_LINKING",
					"USP_GETGSTR2B_CDNR_2A_IMS_LINKING",
					"USP_GETGSTR2B_CDNRA_2A_IMS_LINKING");

			// Variables to track status
			int successCount = 0;
			int failedCount = 0;
			int noDataCount = 0;

			// Execute each stored procedure and determine status
			for (String procedure : storedProcedures) {
				String procResult = executeStoredProcedure(procedure, gstin,
						taxPeriod);
				switch (procResult) {
				case SUCCESS:
					successCount++;
					break;
				case NO_DATA:
					noDataCount++;
					break;
				case FAILED:
				default:
					failedCount++;
					LOGGER.error("Stored procedure {} failed with result: {}",
							gstin, taxPeriod, procedure, procResult);
					break;
				}
			}

			// Determine final status based on execution results
			String finalStatus = determineFinalStatus(successCount, failedCount,
					noDataCount, storedProcedures.size());

			// Update status in the database
			gstr2bLinkingConfigRepository.gstr2bLinkingUpdateStatus(id,
					finalStatus);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Updated status for Batch ID: {} to {}", id,
						finalStatus);
			}

		} catch (Exception e) {
			String errMsg = "Error occurred in Gstr2BLinkingProcessor";
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	/**
	 * Executes a stored procedure and returns the result as a string.
	 */
	private String executeStoredProcedure(String procedureName, String gstin,
			String taxPeriod) {
		try {
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(procedureName);
			storedProc.registerStoredProcedureParameter("IP_RECIPIENT_GSTIN",
					String.class, ParameterMode.IN);
			storedProc.registerStoredProcedureParameter("IP_RETURN_PERIOD",
					String.class, ParameterMode.IN);

			storedProc.setParameter("IP_RECIPIENT_GSTIN", gstin);
			storedProc.setParameter("IP_RETURN_PERIOD", taxPeriod);

			Object result = storedProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Result from stored procedure {}: {}",
						procedureName, result);
			}
			return (String) result;

		} catch (Exception e) {
			LOGGER.error("Error executing 2BLinking Stored Procedure {}: {}",
					procedureName, e.getMessage(), e);
			return FAILED; // Return 'FAILED' if an exception occurs
		}
	}

	private String determineFinalStatus(int successCount, int failedCount,
			int noDataCount, int totalProcs) {
		if (successCount == totalProcs || noDataCount == totalProcs) {
			return SUCCESS;
		} else if (failedCount == totalProcs) {
			return FAILED;
		} else if (successCount > 0 && failedCount > 0) {
			return PARTIAL_SUCCESS;
		} else if (noDataCount > 0 && failedCount > 0) {
			return PARTIAL_SUCCESS;
		}
		return SUCCESS;
	}
}
