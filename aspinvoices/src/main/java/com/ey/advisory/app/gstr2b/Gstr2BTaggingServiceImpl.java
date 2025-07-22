package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr2BMonitorTagging2ARepository;
import com.ey.advisory.app.data.repositories.client.Gstr2BTagging2ARepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.api.APIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Service("Gstr2BTaggingServiceImpl")
public class Gstr2BTaggingServiceImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	Gstr2BTagging2ARepository taggingRepo;

	@Autowired
	private Gstr2BMonitorTagging2ARepository monitorTaggingRepo;

	public void persistAndCallTagProcsForGstr2B(Long id, String gstin, String taxPeriod,
			Long invocationId, String section, String source) {

		try {

			int rowsEffected = taggingRepo.inactiveExistingEntries(gstin,
					taxPeriod, source);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Row Effected are '%s' for GSTIN '%s' and TaxPeriod '%s' and Section '%s' and Source '%s'",
						rowsEffected, gstin, taxPeriod, section, source);
				LOGGER.debug(msg);
			}

			boolean isB2BDataAv = true;
			String b2bresponseMsg = callSectionWiseProc(gstin, taxPeriod,
					invocationId, isB2BDataAv, GSTConstants.B2B, source);

			boolean isB2BADataAv = true;
			String b2baresponseMsg = callSectionWiseProc(gstin, taxPeriod,
					invocationId, isB2BADataAv, GSTConstants.B2BA, source);

			boolean isCDNDataAv = true;
			String cdnresponseMsg = callSectionWiseProc(gstin, taxPeriod,
					invocationId, isCDNDataAv, GSTConstants.CDNR, source);

			boolean isCDNADataAv = true;
			String cdnraesponseMsg = callSectionWiseProc(gstin, taxPeriod,
					invocationId, isCDNADataAv, GSTConstants.CDNRA, source);

			boolean isECOMDataAv = true;
			String ecomresponseMsg = callSectionWiseProc(gstin, taxPeriod,
					invocationId, isECOMDataAv, GSTConstants.ECOM, source);

			boolean isECOMADataAv = true;
			String ecomaresponseMsg = callSectionWiseProc(gstin, taxPeriod,
					invocationId, isECOMADataAv, GSTConstants.ECOMA, source);

			monitorTaggingRepo.updateStatus(APIConstants.COMPLETED,
					LocalDateTime.now(), id);

			if (b2bresponseMsg.equalsIgnoreCase(ReconStatusConstants.FAILED)
					&& b2baresponseMsg
							.equalsIgnoreCase(ReconStatusConstants.FAILED)
					&& cdnresponseMsg
							.equalsIgnoreCase(ReconStatusConstants.FAILED)
					&& cdnraesponseMsg
							.equalsIgnoreCase(ReconStatusConstants.FAILED)
					&& ecomresponseMsg
							.equalsIgnoreCase(ReconStatusConstants.FAILED)
					&& ecomaresponseMsg
							.equalsIgnoreCase(ReconStatusConstants.FAILED)) {

				String errMsg = String.format(
						"All the Procs are Failed for Gstin is '%s' and TaxPeriod is '%s',"
								+ " Hence Marking the job as Failed.",
						gstin, taxPeriod);
				LOGGER.error("Exception while initiating 2B Tagging", errMsg);
				throw new AppException(errMsg);
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while Tagging the 2B for Gstin is '%s' and TaxPeriod is '%s'",
					gstin, taxPeriod);
			monitorTaggingRepo.updateStatus(APIConstants.FAILED,
					LocalDateTime.now(), id);
			LOGGER.error("Exception while initiating 2B Tagging", errMsg);
			throw new AppException(e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}
	
	public void persistAndCallTagProcsForGstr2A(Long id, String gstin,
			String taxPeriod, Long invocationId, String section,
			String source) {

		try {
			int rowsEffected = taggingRepo.inactiveExistingEntries(gstin,
					taxPeriod, section, source);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Row Effected are '%s' for GSTIN '%s' and TaxPeriod '%s' and Section '%s' and Source '%s'",
						rowsEffected, gstin, taxPeriod, section, source);
				LOGGER.debug(msg);
			}

			String sectionResponseMsg = callSectionWiseProc(gstin, taxPeriod,
					invocationId, true, section, source);

			monitorTaggingRepo.updateStatus(APIConstants.COMPLETED,
					LocalDateTime.now(), id);

			if (sectionResponseMsg
					.equalsIgnoreCase(ReconStatusConstants.FAILED)) {

				String errMsg = String.format(
						"All the Procs are Failed for Gstin is '%s' and TaxPeriod is '%s',"
								+ " Hence Marking the job as Failed.",
						gstin, taxPeriod);
				LOGGER.error("Exception while initiating 2B Tagging", errMsg);
				throw new AppException(errMsg);
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while Tagging the 2A for Gstin is '%s' and TaxPeriod is '%s'",
					gstin, taxPeriod);
			monitorTaggingRepo.updateStatus(APIConstants.FAILED,
					LocalDateTime.now(), id);
			LOGGER.error("Exception while initiating 2A Tagging", errMsg);
			throw new AppException(e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private String callSectionWiseProc(String gstin, String taxPeriod,
			Long invocationId, boolean isDataAva, String section,
			String source) {

		Long tagId = persistData(gstin, taxPeriod, section, source);
		Pair<String, String> responseMsg = null;
		if (isDataAva) {
			responseMsg = callChunkProcAndDispProc(gstin, taxPeriod,
					invocationId, section, tagId, source);

			updateStatus(gstin, taxPeriod, section, responseMsg);
		} else {
			responseMsg = new Pair<String, String>(
					ReportStatusConstants.NO_DATA_FOUND,
					ReportStatusConstants.NO_DATA_FOUND);

			updateStatus(gstin, taxPeriod, section, responseMsg);
		}
		return responseMsg.getValue0();// ask siva we are returning only one
										// proc response here
	}
	
	

	private Pair<String, String> callChunkProcAndDispProc(String gstin,
			String taxPeriod, Long invocationId, String section, Long tagId,
			String source) {
		String responseMsg = null;
		String removalResponseMsg = null;

		Integer chunkCount = getChunkCount(gstin, taxPeriod,
				procDetails(section).getValue0(), source);
		if (chunkCount == -1) {
			responseMsg = ReconStatusConstants.FAILED;
		} else {
			responseMsg = callProcandGetData(procDetails(section).getValue1(),
					gstin, taxPeriod, chunkCount, tagId);
		}

		if (APIConstants.GSTR2B.toUpperCase().equalsIgnoreCase(source)) {
			Integer removalChunkCount = getTagRmvlChunkCount(gstin, taxPeriod,
					tagRmvlProcDetails(section).getValue0(), invocationId);
			if (removalChunkCount == -1) {
				removalResponseMsg = ReconStatusConstants.FAILED;
			} else {
				removalResponseMsg = callTagRmvlProcandGetData(
						procDetails(section).getValue1(), gstin, taxPeriod,
						removalChunkCount, tagId, invocationId);
			}
		}
		return new Pair<String, String>(responseMsg, removalResponseMsg);
	}

	private String callProcandGetData(String procName, String gstin,
			String retPeriod, int chunkNo, Long tagId) {
		try {
			if (chunkNo == 0) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Invoking '%s' StoreProc and got response as : '%s'",
							procName, chunkNo);
					LOGGER.debug(msg);
				}
				return ReportStatusConstants.NO_DATA_FOUND;
			} else {
				int finalFailedCount = 0;
				for (int i = 1; i <= chunkNo; i++) {
					StoredProcedureQuery dispProc = entityManager
							.createStoredProcedureQuery(procName);
					if (LOGGER.isDebugEnabled()) {
						String msg = String
								.format("Inside for loop with count: %s", i);
						LOGGER.debug(msg);
					}
					dispProc.registerStoredProcedureParameter("P_TAG_ID",
							Long.class, ParameterMode.IN);
					dispProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
							Integer.class, ParameterMode.IN);
					dispProc.setParameter("P_TAG_ID", tagId);
					dispProc.setParameter("P_CHUNK_VALUE", i);
					String response = (String) dispProc.getSingleResult();
					if (!ReconStatusConstants.SUCCESS
							.equalsIgnoreCase(response)) {
						String msg = String.format(
								"Chunk No Id is '%s', Response "
										+ "from SP %s did not "
										+ "return success,"
										+ " Hence updating to Failed",
								chunkNo, procName);
						LOGGER.error(msg);
						finalFailedCount++;
					}
				}
				if (finalFailedCount == chunkNo) {
					return ReconStatusConstants.FAILED;
				} else if (finalFailedCount == 0) {
					return ReconStatusConstants.SUCCESS;
				} else {
					return ReconStatusConstants.PARTIALLY_SUCCESS;
				}
			}
		} catch (Exception e) {
			String msg = String.format(
					"Gstin is '%s' and TaxPeriod is '%s', Response "
							+ "from RECON_MASTER SP %s did not "
							+ "return success," + " Hence updating to Failed",
					gstin, retPeriod, procName);
			LOGGER.error(msg, e);
			return ReconStatusConstants.FAILED;
		}
	}
	
	private Integer getChunkCount(String gstin, String taxPeriod,
			String procName, String source) {
		try {
			StoredProcedureQuery dispProc = entityManager
					.createStoredProcedureQuery(procName);
			dispProc.registerStoredProcedureParameter("P_GSTIN", String.class,
					ParameterMode.IN);
			dispProc.setParameter("P_GSTIN", gstin);
			dispProc.registerStoredProcedureParameter("P_TAX_PERIOD",
					String.class, ParameterMode.IN);
			dispProc.setParameter("P_TAX_PERIOD", taxPeriod);
			dispProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);
			dispProc.setParameter("P_CHUNK_SPILIT_VAL", 10000);
			dispProc.registerStoredProcedureParameter("P_SOURCE", String.class,
					ParameterMode.IN);
			dispProc.setParameter("P_SOURCE", source);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Data proc Executed" + " for '%s'  and  gstin '%s', "
								+ "taxPeriod '%s' ",
						procName, gstin, taxPeriod);
				LOGGER.debug(msg);
			}
			Integer response = (Integer) dispProc.getSingleResult();
			return response;
		} catch (Exception e) {
			String msg = String.format(
					"Gstin is '%s' and TaxPeriod is '%s', Response "
							+ "from Chunk SP %s did not " + "return success,"
							+ " Hence returning as -1",
					gstin, taxPeriod, procName);
			LOGGER.error(msg, e);
			return -1;
		}
	}

	private Integer getTagRmvlChunkCount(String gstin, String taxPeriod,
			String procName, Long invocationId) {
		try {
			StoredProcedureQuery dispProc = entityManager
					.createStoredProcedureQuery(procName);
			dispProc.registerStoredProcedureParameter("P_GSTIN", String.class,
					ParameterMode.IN);
			dispProc.registerStoredProcedureParameter("P_TAX_PERIOD",
					String.class, ParameterMode.IN);
			dispProc.registerStoredProcedureParameter("P_INVOCATION_ID",
					Long.class, ParameterMode.IN);
			dispProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);
			dispProc.setParameter("P_GSTIN", gstin);
			dispProc.setParameter("P_TAX_PERIOD", taxPeriod);
			dispProc.setParameter("P_INVOCATION_ID", invocationId);
			dispProc.setParameter("P_CHUNK_SPILIT_VAL", 10000);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Data proc Executed" + " for '%s'  and  gstin '%s', "
								+ "taxPeriod '%s' ",
						procName, gstin, taxPeriod);
				LOGGER.debug(msg);
			}
			Integer response = (Integer) dispProc.getSingleResult();
			return response;
		} catch (Exception e) {
			String msg = String.format(
					"Gstin is '%s' and TaxPeriod is '%s', Response "
							+ "from Tagging Removal Chunk SP %s did not "
							+ "return success," + " Hence returning as -1",
					gstin, taxPeriod, procName);
			LOGGER.error(msg, e);
			return -1;
		}
	}

	private String callTagRmvlProcandGetData(String procName, String gstin,
			String retPeriod, int chunkNo, Long tagId, Long invocationId) {
		try {
			if (chunkNo == 0) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Invoking '%s' StoreProc and got response as : '%s'",
							procName, chunkNo);
					LOGGER.debug(msg);
				}
				return ReportStatusConstants.NO_DATA_FOUND;
			} else {
				int finalFailedCount = 0;
				for (int i = 1; i <= chunkNo; i++) {
					StoredProcedureQuery dispProc = entityManager
							.createStoredProcedureQuery(procName);
					if (LOGGER.isDebugEnabled()) {
						String msg = String
								.format("Inside for loop with count: %s", i);
						LOGGER.debug(msg);
					}
					dispProc.registerStoredProcedureParameter("P_INVOCATION_ID",
							Long.class, ParameterMode.IN);
					dispProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
							Integer.class, ParameterMode.IN);
					dispProc.setParameter("P_INVOCATION_ID", invocationId);
					dispProc.setParameter("P_CHUNK_VALUE", i);
					String response = (String) dispProc.getSingleResult();
					if (!ReconStatusConstants.SUCCESS
							.equalsIgnoreCase(response)) {
						String msg = String.format(
								"Chunk No Id is '%s', Response "
										+ "from SP %s did not "
										+ "return success,"
										+ " Hence updating to Failed",
								chunkNo, procName);
						LOGGER.error(msg);
						finalFailedCount++;
					}
				}
				if (finalFailedCount == chunkNo) {
					return ReconStatusConstants.FAILED;
				} else if (finalFailedCount == 0) {
					return ReconStatusConstants.SUCCESS;
				} else {
					return ReconStatusConstants.PARTIALLY_SUCCESS;
				}
			}
		} catch (Exception e) {
			String msg = String.format(
					"Gstin is '%s' and TaxPeriod is '%s', Response "
							+ "from Tagging Removal SP %s did not "
							+ "return success," + " Hence updating to Failed",
					gstin, retPeriod, procName);
			LOGGER.error(msg, e);
			return ReconStatusConstants.FAILED;
		}
	}

	private Long persistData(String gstin, String taxPeriod, String section,
			String source) {
		Gstr2BTagging2AEntity tagEntity = new Gstr2BTagging2AEntity();
		tagEntity.setGstin(gstin);
		tagEntity.setTaxPeriod(taxPeriod);
		tagEntity.setSource(source);
		tagEntity.setCreatedBy("SYSTEM");
		tagEntity.setCreatedOn(LocalDateTime.now());
		tagEntity.setGet2BGenTime(LocalDateTime.now());
		tagEntity.setIsDelete(false);
		tagEntity.setSection(section);
		tagEntity.setStatus(APIConstants.INITIATED);
		taggingRepo.save(tagEntity);
		return tagEntity.getId();
	}

	private void updateStatus(String gstin, String taxPeriod, String section,
			Pair<String, String> msg) {
		taggingRepo.updateStatus(gstin, taxPeriod, section, msg.getValue0(),
				msg.getValue1());
	}

	private Pair<String, String> procDetails(String section) {

		Pair<String, String> procsPair;
		// source gstr2b
		switch (section) {
		case GSTConstants.B2B:
			procsPair = new Pair<String, String>("USP_INS_GET2B_TAGGING_2A_B2B",
					"USP_UPD_GET2B_TAGGING_2A_B2B");
			break;
		case GSTConstants.B2BA:
			procsPair = new Pair<String, String>(
					"USP_INS_GET2B_TAGGING_2A_B2BA",
					"USP_UPD_GET2B_TAGGING_2A_B2BA");
			break;
		case GSTConstants.CDNR:
		case "CDN":	
			procsPair = new Pair<String, String>("USP_INS_GET2B_TAGGING_2A_CDN",
					"USP_UPD_GET2B_TAGGING_2A_CDN");
			break;
		case GSTConstants.CDNRA:
		case "CDNA":	
			procsPair = new Pair<String, String>(
					"USP_INS_GET2B_TAGGING_2A_CDNA",
					"USP_UPD_GET2B_TAGGING_2A_CDNA");
			break;
		case GSTConstants.ECOM:
			procsPair = new Pair<String, String>(
					"USP_INS_GET2B_TAGGING_2A_ECOM",
					"USP_UPD_GET2B_TAGGING_2A_ECOM");
			break;
		case GSTConstants.ECOMA:
			procsPair = new Pair<String, String>(
					"USP_INS_GET2B_TAGGING_2A_ECOMA",
					"USP_UPD_GET2B_TAGGING_2A_ECOMA");
			break;
		default:
			procsPair = new Pair<String, String>(null, null);
			break;
		}
		return procsPair;
	}

	private Pair<String, String> tagRmvlProcDetails(String section) {

		Pair<String, String> procsPair;
		// source gstr2b
		switch (section) {
		case GSTConstants.B2B:
			procsPair = new Pair<String, String>(
					"USP_INS_GET2B_2A_B2B_TAG_RMVL",
					"USP_UPD_GET2B_2A_B2B_TAG_RMVL");
			break;
		case GSTConstants.B2BA:
			procsPair = new Pair<String, String>(
					"USP_INS_GET2B_2A_B2BA_TAG_RMVL",
					"USP_UPD_GET2B_2A_B2BA_TAG_RMVL");
			break;
		case GSTConstants.CDNR:
			procsPair = new Pair<String, String>(
					"USP_INS_GET2B_2A_CDNR_TAG_RMVL",
					"USP_UPD_GET2B_2A_CDNR_TAG_RMVL");
			break;
		case GSTConstants.CDNRA:
			procsPair = new Pair<String, String>(
					"USP_INS_GET2B_2A_CDNRA_TAG_RMVL",
					"USP_UPD_GET2B_2A_CDNRA_TAG_RMVL");
			break;
		case GSTConstants.ECOM:
			procsPair = new Pair<String, String>(
					"USP_INS_GET2B_2A_ECOM_TAG_RMVL",
					"USP_UPD_GET2B_2A_ECOM_TAG_RMVL");
			break;
		case GSTConstants.ECOMA:
			procsPair = new Pair<String, String>(
					"USP_INS_GET2B_2A_ECOMA_TAG_RMVL",
					"USP_UPD_GET2B_2A_ECOMA_TAG_RMVL");
			break;
		default:
			procsPair = new Pair<String, String>(null, null);
			break;
		}
		return procsPair;
	}

	public void callGstr2a2bMonitorProc(String gstin, String retPeriod,
			Long invocationId, String source, String section) {
		String procName = "USP_GET_2A_2B_MONITOR_INS";

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Going to call proc : %s for " + "Gstin '%s', "
								+ "TaxPeriod '%s',  InvocationId '%s',  Source '%s'",
						procName, gstin, retPeriod, invocationId, source);
				LOGGER.debug(msg);
			}

			StoredProcedureQuery dispProc = entityManager
					.createStoredProcedureQuery(procName);

			dispProc.registerStoredProcedureParameter("IP_RECIPIENT_GSTIN",
					String.class, ParameterMode.IN);
			dispProc.registerStoredProcedureParameter("IP_RETURN_PERIOD",
					String.class, ParameterMode.IN);
			dispProc.registerStoredProcedureParameter("IP_INVOCATION_ID",
					Long.class, ParameterMode.IN);
			dispProc.registerStoredProcedureParameter("IP_GET_CALL",
					String.class, ParameterMode.IN);
			dispProc.registerStoredProcedureParameter("IP_SECTION",
					String.class, ParameterMode.IN);

			dispProc.setParameter("IP_RECIPIENT_GSTIN", gstin);
			dispProc.setParameter("IP_RETURN_PERIOD", retPeriod);
			dispProc.setParameter("IP_INVOCATION_ID", invocationId);
			dispProc.setParameter("IP_GET_CALL", source);
			dispProc.setParameter("IP_SECTION", section);

			String response = (String) dispProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"GSTR2A & GSTR2B Monitor proc '%s' executed for "
								+ "Gstin '%s', "
								+ "TaxPeriod '%s',  InvocationId '%s',  Source '%s' and Response is %s",
						procName, gstin, retPeriod, invocationId, source,
						response);
				LOGGER.debug(msg);
			}

			// String msg = String.format(
			// "Response from proc '%s' for Gstin '%s', "
			// + "TaxPeriod '%s', InvocationId '%s', Source '%s' is %s",
			// procName, gstin, retPeriod, invocationId, source);
			//
			// LOGGER.error(msg);

			// return response;

		} catch (Exception e) {
			String msg = String.format(
					"Proc '%s' execution for Gstin '%s', TaxPeriod '%s', InvocationId '%s' "
							+ "and Source '%s' is failed",
					procName, gstin, retPeriod, invocationId, source);
			LOGGER.error(msg, e);
			// return ReconStatusConstants.FAILED;
		}
	}

}
