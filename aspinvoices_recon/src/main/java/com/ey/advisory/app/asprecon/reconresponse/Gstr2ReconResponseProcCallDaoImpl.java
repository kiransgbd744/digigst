package com.ey.advisory.app.asprecon.reconresponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2AReconRespUploadProcEntity;
import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2BReconRespUploadProcEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2AReconRespUploadProcRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2BReconRespUploadProcRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.ReconStatusConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2ReconResponseProcCallDaoImpl")
public class Gstr2ReconResponseProcCallDaoImpl
		implements Gstr2ReconResponseProcCallDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("Gstr2AReconRespUploadProcRepository")
	Gstr2AReconRespUploadProcRepository gstr2aProcRepo;
	
	@Autowired
	@Qualifier("Gstr2BReconRespUploadProcRepository")
	Gstr2BReconRespUploadProcRepository gstr2bProcRepo;
	
	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	@Override
	public List<Gstr2ReconResponseButtonReqDto> validateReconResponse(
			Long batchId, String reconType, Long fileId) {
		List<Gstr2ReconResponseButtonReqDto> resp = new ArrayList<>();
		if (reconType.equalsIgnoreCase("2A_PR")) {
			StoredProcedureQuery storedProc = null;
			String response = null;

			try {
				storedProc = entityManager.createStoredProcedureQuery(
						"USP_AUTO_2APR_RECON_RESP_UI");

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"About to execute Recon SP with ConfigId :%s",
							batchId.toString());
					LOGGER.debug(msg);
				}

				storedProc.registerStoredProcedureParameter("VAR_BATCHID",
						Long.class, ParameterMode.IN);

				storedProc.setParameter("VAR_BATCHID", batchId);
				// storedProc.execute();
				response = (String) storedProc.getSingleResult();
				// FAILED or SUCCESS

				LOGGER.debug("USP_AUTO_2APR_RECON_RESP_UI" + " :: " + response);

				if (ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {

					String failurePocName = "USP_RECON_RESP_PSD_FAILURE";
					try {
						Map<Integer, String> procs = fetchStoredGSTR2aProcedures();
						for (Integer indx : procs.keySet()) {
							String procName = procs.get(indx);
							try {
								String status = executeGstr2Procedure(procName,
										Long.valueOf(batchId));
								if (!(ReconStatusConstants.SUCCESS
										.equalsIgnoreCase(status)
										|| "Success with Errors"
												.equalsIgnoreCase(status))) {
									String msg = String.format(
											"GSTR2-APR Batch Id is '%s', Response "
													+ "from PROC %s did not "
													+ "return success,"
													+ " Hence updating to Failed",
											batchId, procName);
									LOGGER.error(msg);
									throw new AppException(msg);
								}

								if (status.contains("Errors")) {

									String queryString = createQueryString();
									Query q = entityManager
											.createNativeQuery(queryString);
									q.setParameter("batchId",
											String.valueOf(batchId));

									@SuppressWarnings("unchecked")
									List<Object[]> list = q.getResultList();

									resp = list.stream()
											.map(o -> convertError(o))
											.collect(Collectors.toCollection(
													ArrayList::new));

								}

							} catch (Exception ex) {
								String msg = String.format(
										"Calling recon response failure Proc '%s' for Batch Id is '%s'",
										failurePocName, batchId);
								LOGGER.debug(msg);
								executeGstr2Procedure(failurePocName,
										Long.valueOf(batchId));
								throw new AppException(ex);
							}
						}

						LOGGER.debug(
								"recon response is Processed for fileId: {}",
								fileId);

						LOGGER.debug(
								"USP_RECON_RESP_VALIDATE" + " :: " + response);

					} catch (Exception e) {
						String msg = String.format(
								"Exception Occure while fetching Proc List for recon response from TBL :'%s'",
								failurePocName);
						LOGGER.error(msg, e);
						throw new AppException(e);
					}

				} else {
					String msg = String.format("Batch Id is '%s', Response "
							+ "from USP_AUTO_2APR_RECON_RESP_UI did not "
							+ "return success," + " Hence updating to Failed",
							batchId.toString());
					LOGGER.error(msg);
					throw new AppException(msg);
				}

			} catch (Exception ex) {
				String msg = String.format(
						"Batch Id is '%s', result result processing failed",
						batchId.toString());
				LOGGER.error(msg, ex);
				fileStatusRepository.updateFailedStatus(batchId,
						StringUtils.truncate(ex.getLocalizedMessage(), 4000),
						"FAILED");
				throw new AppException(msg, ex);
			}

		} else {
			StoredProcedureQuery storedProc = null;
			String response = null;

			try {
				storedProc = entityManager.createStoredProcedureQuery(
						"USP_RECON_2BPR_RECON_RESP_UI");

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"About to execute Recon SP with ConfigId :%s",
							batchId.toString());
					LOGGER.debug(msg);
				}

				storedProc.registerStoredProcedureParameter("VAR_BATCHID",
						Long.class, ParameterMode.IN);

				storedProc.setParameter("VAR_BATCHID", batchId);
				// storedProc.execute();
				response = (String) storedProc.getSingleResult();
				// FAILED or SUCCESS

				LOGGER.debug(
						"USP_RECON_2BPR_RECON_RESP_UI  " + " :: " + response);

				if (ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {

					String failurePocName = "USP_RECON_2BPR_RESP_PSD_FAILURE";
					try {
						Map<Integer, String> procs = fetchStoredGSTR2bProcedures();
						for (Integer indx : procs.keySet()) {
							String procName = procs.get(indx);
							try {
								String status = executeGstr2Procedure(procName,
										Long.valueOf(batchId));
								if (!(ReconStatusConstants.SUCCESS
										.equalsIgnoreCase(status)
										|| "Success with Errors"
												.equalsIgnoreCase(status))) {
									String msg = String.format(
											"GSTR2-BPR Batch Id is '%s', Response "
													+ "from PROC %s did not "
													+ "return success,"
													+ " Hence updating to Failed",
											batchId, procName);
									LOGGER.error(msg);
									throw new AppException(msg);
								}

								if (status.contains("Errors")) {

									String queryString = createQueryString1();
									Query q = entityManager
											.createNativeQuery(queryString);
									q.setParameter("batchId", String.valueOf(batchId));

									@SuppressWarnings("unchecked")
									List<Object[]> list = q.getResultList();

									resp = list.stream().map(o -> convertError1(o))
											.collect(Collectors
													.toCollection(ArrayList::new));

								}
								

							} catch (Exception ex) {
								String msg = String.format(
										"Calling recon response failure Proc '%s' for Batch Id is '%s'",
										failurePocName, batchId);
								LOGGER.debug(msg);
								executeGstr2Procedure(failurePocName,
										Long.valueOf(batchId));
								throw new AppException(ex);
							}
						}

						LOGGER.debug(
								"recon response is Processed for fileId: {}",
								fileId);

						

					} catch (Exception e) {
						String msg = String.format(
								"Exception Occure while fetching Proc List for recon response from TBL :'%s'",
								failurePocName);
						LOGGER.error(msg, e);
						throw new AppException(e);
					}

					
					
				} else {
					String msg = String.format("Batch Id is '%s', Response "
							+ "from USP_RECON_2BPR_RECON_RESP_UI did not "
							+ "return success," + " Hence updating to Failed",
							batchId.toString());
					LOGGER.error(msg);
					throw new AppException(msg);
				}

			} catch (Exception ex) {
				String msg = String.format(
						"Batch Id is '%s', result result processing failed",
						batchId.toString());
				LOGGER.error(msg, ex);
				fileStatusRepository.updateFailedStatus(batchId,
						StringUtils.truncate(ex.getLocalizedMessage(), 4000),
						"FAILED");
				throw new AppException(msg, ex);
			}

		}

		return resp;
	}

	private String createQueryString() {
		String s1 = "select string_agg(Error_Description,','),doc_num from ( "
				+ " select distinct Error_Description, "
				+ "(CASE WHEN REPORT_CATEGORY='IMPG/SEZG' THEN IFNULL(BILL_OF_ENTRY_PR,BILL_OF_ENTRY_2A) "
				+ " ELSE IFNULL(DocumentNumberPR,DocumentNumber2A)END) AS doc_num "
				+ " from TBL_STG_RECON_RESPONSE " + " WHERE BATCHID =:batchId "
				+ " and Error_Description is not null )group by doc_num ; ";
		return s1;
	}

	private Gstr2ReconResponseButtonReqDto convertError(Object[] arr) {

		Gstr2ReconResponseButtonReqDto dto = new Gstr2ReconResponseButtonReqDto();
		try {
			dto.setErrorDesc(arr[0] != null ? arr[0].toString() : null);

			dto.setDocNumberPR(arr[1] != null ? arr[1].toString() : null);

			dto.setType("2A/6A");

		} catch (Exception ex) {
			String msg = String.format("Error while converting dto 2A %s", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return dto;
	}

	private String createQueryString1() {
		String s1 = "select string_agg(Error_Description,','),doc_num from ( "
				+ " select distinct Error_Description,(CASE WHEN REPORT_CATEGORY IN ('IMPG/SEZ','IMPG/SEZG') THEN IFNULL(BILL_OF_ENTRY_PR,BILL_OF_ENTRY_2B) "
				+ " ELSE IFNULL(DocumentNumberPR,DocumentNumber2B)END) AS doc_num "
				+ " from TBL_2BPR_STG_RECON_RESPONSE "
				+ " WHERE BATCHID =:batchId "
				+ " and Error_Description is not null )group by doc_num ; ";
		return s1;
	}

	private Gstr2ReconResponseButtonReqDto convertError1(Object[] arr) {

		Gstr2ReconResponseButtonReqDto dto = new Gstr2ReconResponseButtonReqDto();
		try {
			dto.setErrorDesc(arr[0] != null ? arr[0].toString() : null);
			dto.setDocNumberPR(arr[1] != null ? arr[1].toString() : null);
			dto.setType("2B");

		} catch (Exception ex) {
			String msg = String.format("Error while converting dto 2B %s", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return dto;
	}

	private Map<Integer, String> fetchStoredGSTR2aProcedures() {
		Map<Integer, String> gstr2aProcMap = null;
		try {

			gstr2aProcMap = new TreeMap<>();
			List<Gstr2AReconRespUploadProcEntity> gstr2aProcList = gstr2aProcRepo
					.findAllActiveProc();

			gstr2aProcMap = gstr2aProcList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Getting alls GSTR 2APR recon response upload proc list '%s'",
						gstr2aProcMap.toString());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			String msg = String
					.format("Exception while fetching GSTR 2APR recon "
							+ "response upload proc list from DB ");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return gstr2aProcMap;
	}
	private String executeGstr2Procedure(String procName, Long batchId)
			throws Exception {
		String status = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invking stor proc :%s , BatchId :%s", procName,
						Long.valueOf(batchId));
				LOGGER.debug(msg);
			}
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(procName);
			storedProc.registerStoredProcedureParameter("VAR_BATCHID",
					Long.class, ParameterMode.IN);
			storedProc.setParameter("VAR_BATCHID", batchId);
			long dbLoadStTime = System.currentTimeMillis();
			status = (String) storedProc.getSingleResult();
			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to excecute the Proc :'%s'"
								+ " with staus :'%s' from DB is '%d' millisecs.",
						procName, status, dbLoadTimeDiff);
				LOGGER.debug(msg);
			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while calling proc :'%s' for Batch Id '%s'",
					procName, batchId);
			LOGGER.debug(msg);
			LOGGER.error(msg,e);
			if("USP_RECON_2BPR_RESP_PSD_FAILURE".equalsIgnoreCase(procName) ||
					"USP_RECON_RESP_PSD_FAILURE".equalsIgnoreCase(procName) )
			{
				failedBatAltUtility.prepareAndTriggerAlert(String.valueOf(batchId),
						"RECON_RESPONSE",String.format(" FAILURE PROCNAME : '%s' ",procName));
		
			}
			throw new AppException(msg);
		}
		return status;
	}
	
	private Map<Integer, String> fetchStoredGSTR2bProcedures() {
		Map<Integer, String> gstr2bProcMap = null;
		try {

			gstr2bProcMap = new TreeMap<>();
			List<Gstr2BReconRespUploadProcEntity> gstr2bProcList = gstr2bProcRepo
					.findAllActiveProc();

			gstr2bProcMap = gstr2bProcList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Getting alls GSTR 2BPR recon response upload proc list '%s'",
						gstr2bProcMap.toString());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			String msg = String
					.format("Exception while fetching GSTR 2BPR recon "
							+ "response upload proc list from DB ");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return gstr2bProcMap;
	}

}
