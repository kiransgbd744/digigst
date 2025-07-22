/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.math.BigDecimal;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.entities.simplified.client.Gstr6CrossItcSaveComputeEntity;
import com.ey.advisory.app.data.repositories.client.CrossItcProcessRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6CrossItcSaveComputeRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdItcCrossDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6SaveCrossITCDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6SaveCrossItcReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.BatchHandler;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr6SaveCrossITCDetailsHandler")
public class Gstr6SaveCrossITCDetailsHandler {

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("batchHandler")
	private BatchHandler batchHandler;
	
	@Autowired
	private GstnUserRequestRepository gstnUserRequestRepo;
	
	@Autowired
	private CrossItcProcessRepository crossITCRepo;
	
	@Autowired
	private Gstr6CrossItcSaveComputeRepository crossItcComputeRepo;

	@Transactional(value = "clientTransactionManager")
	public void saveCrossItc(String jsonString, String groupCode,
			String userName) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr6SaveCrossITCDetailsHandler with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			Gstr6SaveCrossItcReqDto dto = gson.fromJson(jsonString,
					Gstr6SaveCrossItcReqDto.class);

			TenantContext.setTenantId(groupCode);

			boolean isCrossItcUserInput = false;
			Optional<GstnUserRequestEntity> optionalEntity = 
					gstnUserRequestRepo.findById(dto.getUserRequestId());
			if (optionalEntity.isPresent()) {
				GstnUserRequestEntity gstnUserRequestEntity = optionalEntity.get();
				if(gstnUserRequestEntity != null) {
				isCrossItcUserInput = gstnUserRequestEntity.isCrossItcUserInput();
				}
			}
			
			
			Object[] crossITCSummaryResultSet = null;
			
			if(isCrossItcUserInput == true) {
				crossITCSummaryResultSet = getUserInputCrossITCSummaryResultSet(
							dto.getGstin(), dto.getRetPeriod());;
			} else {
				 crossITCSummaryResultSet = getDigiGstCrossITCSummaryResultSet(
							dto.getGstin(), dto.getRetPeriod());
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 SaveCrossITCDetails Docs found is {} ",
						crossITCSummaryResultSet != null
								? crossITCSummaryResultSet.length : 0);
			}

			if (crossITCSummaryResultSet == null) {
				LOGGER.error("No Docs found to do Gstr6 SaveCrossITCDetails.");
				return;
			}


			Gstr1SaveBatchEntity saveBatch = batchHandler.saveBatch(dto.getGstin(),
					dto.getRetPeriod(), "", groupCode, APIConstants.GSTR6, 0,
					APIConstants.SAVE_CROSS_ITC, 0l, dto.getUserRequestId());
			
			Long gstnBatchId = saveBatch.getId();
			
			Gstr6SaveCrossITCDto convertAsDto = convertAsDto(
					crossITCSummaryResultSet);
			String json = gson.toJson(convertAsDto, Gstr6SaveCrossITCDto.class);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 SaveCrossITCDetails json schema is {} ",
						json);
			}

			String docKey = crossITCSummaryResultSet[10] != null
					? String.valueOf(crossITCSummaryResultSet[10]) : null;
			
			APIResponse response = hitGstnServer.gstr6SaveCrossItcCall(
					groupCode, json, dto.getGstin(), dto.getRetPeriod(), gstnBatchId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6SaveCrossITCDetailsHandler Sandbox response {} ",
						response);
			}
			
			LocalDateTime now = saveBatch.getCreatedOn();
			Clob responseClob = new javax.sql.rowset.serial.SerialClob(
					json.toCharArray());

			UUID uuid = UUID.randomUUID();
			String refId = uuid.toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 SaveCrossITCDetails ref_id {} ", refId);
			}
			if (response.isSuccess()) {
				/**
				 * Adding the payload data as a record in the table with savedToGstn as true
				 * and the same is used the Cross Itc screen to display the data
				 */
				sofDeleteAndSaveComputeEntity(docKey, convertAsDto, !isCrossItcUserInput, gstnBatchId,
						true, false, userName, now);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr6 SaveCrossITCDetails call got success for the input {} "
									+ "and response {} ",
							jsonString, response);
				}
				String statusCode = response.getResponse();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Response clob %s ",
							responseClob != null ? responseClob.toString()
									: "Null Gsnt SAVE req data");
					LOGGER.debug(msg);
				}
				
				String txnId = response.getTxnId();
				batchRepo.updateBatchRefID(refId, gstnBatchId, txnId, now);
				batchRepo.updateGstr1SaveBatchbyBatchId(gstnBatchId, APIConstants.P, 0, now);
				batchRepo.updateStatusById(gstnBatchId, APIConstants.POLLING_COMPLETED, now);
				if(isCrossItcUserInput == true) {
				crossITCRepo.markDocsAsSavedForBatch(gstnBatchId, now);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr6 SaveCrossITCDetails P - Save Batch entity id {} ", gstnBatchId);
				}

			} else {
				
				/**
				 * Adding the payload data as a record in the table with savedToGstn as false 
				 * and the same is used the Cross Itc screen to display the data
				 */
				sofDeleteAndSaveComputeEntity(docKey, convertAsDto, !isCrossItcUserInput, gstnBatchId,
						false, true, userName, now);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr6 SaveCrossITCDetails call got falied for the input {} "
									+ "and response {} ",
							jsonString, response);
				}
				APIError error = response.getError();
			
				String txnId = response != null ? response.getTxnId() : null;
				batchRepo.updateBatchRefID(refId, gstnBatchId, txnId, now);
				batchRepo.updateGstr1SaveBatchbyBatchId(gstnBatchId, APIConstants.ER, 0, now);
				if (response.getError() != null) {
					String errorCode = error.getErrorCode();
					String errorDesc = error.getErrorDesc();
					batchRepo.updateErrorMesg(gstnBatchId, errorCode, errorDesc, now);
					}
				batchRepo.updateStatusById(gstnBatchId, APIConstants.POLLING_COMPLETED, now);
				if (isCrossItcUserInput == true) {
					crossITCRepo.markDocsAsErrorForBatch(gstnBatchId, now);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr6 SaveCrossITCDetails P - Save Batch entity id {} ", gstnBatchId);
				}
			}

		} catch (Exception ex) {
			String msg = "App Exception";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@SuppressWarnings("unchecked")
	private Object[] getDigiGstCrossITCSummaryResultSet(String gstin,
			String retPeriod) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND ISD_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND TAX_PERIOD = :retPeriod ");
		}
		
		
		String sql = "SELECT ISD_GSTIN,TAX_PERIOD,"
				+ "       SUM((IFNULL(IAMTI_A,0)-IFNULL(IAMTI_M,0))+IFNULL(IAMTI_TOT_M,0)) AS DG_IAMTI,"
				+ "                               SUM(IFNULL(IAMTS_A,0)-IFNULL(IAMTS_M,0)) AS DG_IAMTS,"
				+ "                                SUM(IFNULL(IAMTC_A,0)-IFNULL(IAMTC_M,0)) AS DG_IAMTC,"
				+ "       SUM((IFNULL(SAMTS_A,0)-IFNULL(SAMTS_M,0))+IFNULL(SAMTS_TOT_M,0)) AS DG_SAMTS,"
				+ "                               SUM(IFNULL(SAMTI_A,0)-IFNULL(SAMTI_M,0)) AS DG_SAMTI,"
				+ "        SUM((IFNULL(CAMTC_A,0)-IFNULL(CAMTC_M,0))+IFNULL(CAMTC_TOT_M,0)) AS DG_CAMTC,"
				+ "                               SUM(IFNULL(CAMTI_A,0)-IFNULL(CAMTI_M,0)) AS DG_CAMTI,"
				+ "                                                 SUM(IFNULL(CSAMT_A,0)) AS DG_CSAMT,DOC_KEY  "
				+ "  FROM (SELECT ISD_GSTIN,TAX_PERIOD,"
				+ "       SUM(CASE WHEN DOC_TYPE ='INV' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(IGST_AMT_AS_IGST,0) END) AS IAMTI_A,"
				+ "       SUM(CASE WHEN DOC_TYPE ='CR' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(IGST_AMT_AS_IGST,0) END) AS IAMTI_M,"
				+ "       SUM(CASE WHEN DOC_TYPE ='CR' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN "
				+ "                IFNULL(IGST_AMT_AS_IGST,0) + IFNULL(CGST_AMT_AS_IGST,0) + IFNULL(SGST_AMT_AS_IGST,0) END) AS IAMTI_TOT_M,"
				+ "       SUM(CASE WHEN DOC_TYPE ='INV' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(CGST_AMT_AS_IGST,0) END) AS IAMTC_A,"
				+ "       SUM(CASE WHEN DOC_TYPE ='CR' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(CGST_AMT_AS_IGST,0) END) AS IAMTC_M,"
				+ "       SUM(CASE WHEN DOC_TYPE ='INV' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(SGST_AMT_AS_IGST,0) END) AS IAMTS_A,"
				+ "       SUM(CASE WHEN DOC_TYPE ='CR' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(SGST_AMT_AS_IGST,0) END) AS IAMTS_M,"
				+ "       SUM(CASE WHEN DOC_TYPE ='INV' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(CGST_AMT_AS_CGST,0) END) AS CAMTC_A,"
				+ "       SUM(CASE WHEN DOC_TYPE ='CR' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(CGST_AMT_AS_CGST,0) END) AS CAMTC_M,"
				+ "       SUM(CASE WHEN DOC_TYPE ='CR' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN "
				+ "                IFNULL(IGST_AMT_AS_CGST,0) + IFNULL(CGST_AMT_AS_CGST,0) END) AS CAMTC_TOT_M,"
				+ "       SUM(CASE WHEN DOC_TYPE ='INV' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(IGST_AMT_AS_CGST,0) END) AS CAMTI_A,"
				+ "       SUM(CASE WHEN DOC_TYPE ='CR' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(IGST_AMT_AS_CGST,0) END) AS CAMTI_M,"
				+ "       SUM(CASE WHEN DOC_TYPE ='INV' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(SGST_AMT_AS_SGST,0) END) AS SAMTS_A,"
				+ "       SUM(CASE WHEN DOC_TYPE ='CR' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(SGST_AMT_AS_SGST,0) END) AS SAMTS_M,"
				+ "       SUM(CASE WHEN DOC_TYPE ='CR' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN "
				+ "                IFNULL(IGST_AMT_AS_SGST,0) + IFNULL(SGST_AMT_AS_SGST,0) END) AS SAMTS_TOT_M,"
				+ "       SUM(CASE WHEN DOC_TYPE ='INV' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(IGST_AMT_AS_SGST,0) END) AS SAMTI_A,"
				+ "       SUM(CASE WHEN DOC_TYPE ='CR' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(IGST_AMT_AS_SGST,0) END) AS SAMTI_M,"
				+ "       SUM(CASE WHEN DOC_TYPE ='INV' AND ELIGIBLE_INDICATOR IN ('E','IE') THEN IFNULL(CESS_AMT,0) END) AS CSAMT_A, "
				+ "	ISD_GSTIN||'|'||TAX_PERIOD DOC_KEY FROM GSTR6_ISD_DISTRIBUTION "
				+ " WHERE IS_DELETE = FALSE AND SUPPLY_TYPE IS NULL "
				+ buildQuery + " GROUP BY ISD_GSTIN,TAX_PERIOD) "
				+ " GROUP BY ISD_GSTIN,TAX_PERIOD,DOC_KEY";
		
		/*String sql = "SELECT ISD_GSTIN,TAX_PERIOD,"
				+ "SUM(IFNULL(IAMTI_A,0)-IFNULL(IAMTI_S,0)) IAMTI,"
				+ "SUM(IFNULL(IAMTS_A,0)-IFNULL(IAMTS_S,0)) IAMTS,"
				+ "SUM(IFNULL(IAMTC_A,0)-IFNULL(IAMTC_S,0)) IAMTC,"
				+ "SUM(IFNULL(SAMTS_A,0)-IFNULL(SAMTS_S,0)) SAMTS,"
				+ "SUM(IFNULL(SAMTI_A,0)-IFNULL(SAMTI_S,0)) SAMTI,"
				+ "SUM(IFNULL(CAMTC_A,0)-IFNULL(CAMTC_S,0)) CAMTC,"
				+ "SUM(IFNULL(CAMTI_A,0)-IFNULL(CAMTI_S,0)) CAMTI,"
				+ "SUM(IFNULL(CSAMT_A,0)-IFNULL(CSAMT_S,0)) CSAMT "
				+ "FROM(SELECT ISD_GSTIN,TAX_PERIOD,"
				+ "CASE WHEN DOC_TYPE ='INV' THEN IFNULL(IGST_AMT_AS_IGST,0) END AS IAMTI_A,"
				+ "CASE WHEN DOC_TYPE ='INV' THEN IFNULL(SGST_AMT_AS_IGST,0) END AS IAMTS_A,"
				+ "CASE WHEN DOC_TYPE ='INV' THEN IFNULL(CGST_AMT_AS_IGST,0) END AS IAMTC_A,"
				+ "CASE WHEN DOC_TYPE ='INV' THEN IFNULL(SGST_AMT_AS_SGST,0) END AS SAMTS_A,"
				+ "CASE WHEN DOC_TYPE ='INV' THEN IFNULL(IGST_AMT_AS_SGST,0) END AS SAMTI_A,"
				+ "CASE WHEN DOC_TYPE ='INV' THEN IFNULL(CGST_AMT_AS_CGST,0) END AS CAMTC_A,"
				+ "CASE WHEN DOC_TYPE ='INV' THEN IFNULL(IGST_AMT_AS_CGST,0) END AS CAMTI_A,"
				+ "CASE WHEN DOC_TYPE ='INV' THEN IFNULL(CESS_AMT,0) END AS CSAMT_A,"
				+ "CASE WHEN DOC_TYPE ='CR' THEN IFNULL(IGST_AMT_AS_IGST,0) END AS IAMTI_S,"
				+ "CASE WHEN DOC_TYPE ='CR' THEN IFNULL(SGST_AMT_AS_IGST,0) END AS IAMTS_S,"
				+ "CASE WHEN DOC_TYPE ='CR' THEN IFNULL(CGST_AMT_AS_IGST,0) END AS IAMTC_S,"
				+ "CASE WHEN DOC_TYPE ='CR' THEN IFNULL(SGST_AMT_AS_SGST,0) END AS SAMTS_S,"
				+ "CASE WHEN DOC_TYPE ='CR' THEN IFNULL(IGST_AMT_AS_SGST,0) END AS SAMTI_S,"
				+ "CASE WHEN DOC_TYPE ='CR' THEN IFNULL(CGST_AMT_AS_CGST,0) END AS CAMTC_S,"
				+ "CASE WHEN DOC_TYPE ='CR' THEN IFNULL(IGST_AMT_AS_CGST,0) END AS CAMTI_S,"
				+ "CASE WHEN DOC_TYPE ='CR' THEN IFNULL(CESS_AMT,0) END AS CSAMT_S "
				+ "FROM GSTR6_ISD_DISTRIBUTION "
				+ "WHERE IS_DELETE = FALSE "
				+ "AND SUPPLY_TYPE IS NULL "
				+ buildQuery +") "
				+ "GROUP BY ISD_GSTIN,TAX_PERIOD";*/
		
		//Working older Query
		/*String sql = "SELECT ISD_GSTIN,TAX_PERIOD,"
		+ "IFNULL(SUM(IGST_AMT_AS_IGST),0) AS IAMTI,"
		+ "IFNULL(SUM(SGST_AMT_AS_IGST),0) AS IAMTS,"
		+ "IFNULL(SUM(CGST_AMT_AS_IGST),0) AS IAMTC,"
		+ "IFNULL(SUM(SGST_AMT_AS_SGST),0) AS SAMTS,"
		+ "IFNULL(SUM(IGST_AMT_AS_SGST),0) AS SAMTI,"
		+ "IFNULL(SUM(CGST_AMT_AS_CGST),0) AS CAMTC,"
		+ "IFNULL(SUM(IGST_AMT_AS_CGST),0) AS CAMTI,"
		+ "IFNULL(SUM(CESS_AMT),0) AS CSAMT "
        + "FROM GSTR6_ISD_DISTRIBUTION WHERE IS_DELETE = FALSE "
        + "AND DOC_TYPE ='INV' "
        + "AND SUPPLY_TYPE IS NULL "
        + buildQuery
        + "GROUP BY ISD_GSTIN,TAX_PERIOD";*/

		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {

			Query query = entityManager.createNativeQuery(sql);

			/**
			 * setting the values based on condition the key values in the
			 * setParameter should be same as index names. index names are
			 * dynamically coming from the criteria.
			 */
			if (gstin != null && retPeriod != null) {
				query.setParameter("gstin", gstin);
				query.setParameter("retPeriod", retPeriod);
			}

			result = query.getResultList();
			Object[] arr = result != null ? result.get(0) : null;
			return arr;

		}
		return null;

	}
	
	@SuppressWarnings("unchecked")
	private Object[] getUserInputCrossITCSummaryResultSet(String gstin,
			String retPeriod) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND ISD_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND RETURN_PERIOD = :retPeriod ");
		}
		
		String sql = "SELECT ISD_GSTIN,RETURN_PERIOD,"
		+ "IFNULL(SUM(IGST_USED_AS_IGST),0) AS IAMTI,"
		+ "IFNULL(SUM(SGST_USED_AS_IGST),0) AS IAMTS,"
		+ "IFNULL(SUM(CGST_USED_AS_IGST),0) AS IAMTC,"
		+ "IFNULL(SUM(SGST_USED_AS_SGST),0) AS SAMTS,"
		+ "IFNULL(SUM(IGST_USED_AS_SGST),0) AS SAMTI,"
		+ "IFNULL(SUM(CGST_USED_AS_CGST),0) AS CAMTC,"
		+ "IFNULL(SUM(IGST_USED_AS_CGST),0) AS CAMTI,"
		+ "IFNULL(SUM(CESS_USED_AS_CESS),0) AS CSAMT,DOC_KEY "
        + "FROM CROSS_ITC_PROCESSED WHERE IS_DELETE = FALSE "
       // + "AND DOC_TYPE ='INV' "
       // + "AND SUPPLY_TYPE IS NULL "
        + buildQuery
        + "GROUP BY ISD_GSTIN,RETURN_PERIOD,DOC_KEY";

		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {

			Query query = entityManager.createNativeQuery(sql);

			/**
			 * setting the values based on condition the key values in the
			 * setParameter should be same as index names. index names are
			 * dynamically coming from the criteria.
			 */
			if (gstin != null && retPeriod != null) {
				query.setParameter("gstin", gstin);
				query.setParameter("retPeriod", retPeriod);
			}

			result = query.getResultList();
			Object[] arr = result != null ? result.get(0) : null;
			return arr;

		}
		return null;

	}

	private Gstr6SaveCrossITCDto convertAsDto(Object[] objArray) {

		String gstin = objArray[0] != null ? String.valueOf(objArray[0]) : null;
		String retPeriod = objArray[1] != null ? String.valueOf(objArray[1])
				: null;

		BigDecimal iamti = objArray[2] != null
				? new BigDecimal(String.valueOf(objArray[2])) : BigDecimal.ZERO;
		BigDecimal iamts = objArray[3] != null
				? new BigDecimal(String.valueOf(objArray[3])) : BigDecimal.ZERO;
		BigDecimal iamtc = objArray[4] != null
				? new BigDecimal(String.valueOf(objArray[4])) : BigDecimal.ZERO;

		BigDecimal samts = objArray[5] != null
				? new BigDecimal(String.valueOf(objArray[5])) : BigDecimal.ZERO;
		BigDecimal samti = objArray[6] != null
				? new BigDecimal(String.valueOf(objArray[6])) : BigDecimal.ZERO;

		BigDecimal camtc = objArray[7] != null
				? new BigDecimal(String.valueOf(objArray[7])) : BigDecimal.ZERO;
		BigDecimal camti = objArray[8] != null
				? new BigDecimal(String.valueOf(objArray[8])) : BigDecimal.ZERO;

		BigDecimal cess = objArray[9] != null
				? new BigDecimal(String.valueOf(objArray[9])) : BigDecimal.ZERO;

		Gstr6IsdItcCrossDto childDto = new Gstr6IsdItcCrossDto();

		childDto.setIamti(iamti);
		childDto.setIamts(iamts);
		childDto.setIamtc(iamtc);
		childDto.setSamts(samts);
		childDto.setSamti(samti);
		childDto.setCamtc(camtc);
		childDto.setCamti(camti);
		childDto.setCess(cess);

		Gstr6SaveCrossITCDto parentDto = new Gstr6SaveCrossITCDto();
		parentDto.setGstin(gstin);
		parentDto.setFp(retPeriod);
		parentDto.setIsdItcCross(childDto);

		return parentDto;
	}

	
	public Gstr6CrossItcSaveComputeEntity sofDeleteAndSaveComputeEntity(String docKey,
			Gstr6SaveCrossITCDto parentDto, boolean isDigiGstCompute,
			Long gstnBatchId, boolean isSavedToGstn, boolean isGstnError, String userName,
			LocalDateTime now) {

		if (parentDto.getGstin() != null && parentDto.getFp() != null) {
			crossItcComputeRepo.softlyDelete(isDigiGstCompute, isSavedToGstn,
					isGstnError, parentDto.getGstin(), parentDto.getFp());

			Gstr6CrossItcSaveComputeEntity crossItcCompute = new Gstr6CrossItcSaveComputeEntity();

			crossItcCompute.setDocKey(docKey);
			crossItcCompute.setGstin(parentDto.getGstin());
			crossItcCompute.setTaxPeriod(parentDto.getFp());
			crossItcCompute.setDerTaxPeriod(
					GenUtil.convertTaxPeriodToInt(parentDto.getFp()));
			crossItcCompute.setDigiGstCompute(isDigiGstCompute);
			crossItcCompute.setGstnBatchId(gstnBatchId);
			crossItcCompute.setSavedToGstn(isSavedToGstn);
			crossItcCompute.setGstnError(isGstnError);
			crossItcCompute.setIamti(parentDto.getIsdItcCross().getIamti());
			crossItcCompute.setIamtc(parentDto.getIsdItcCross().getIamtc());
			crossItcCompute.setIamts(parentDto.getIsdItcCross().getIamts());
			crossItcCompute.setSamts(parentDto.getIsdItcCross().getSamts());
			crossItcCompute.setSamti(parentDto.getIsdItcCross().getSamti());
			crossItcCompute.setCamtc(parentDto.getIsdItcCross().getCamtc());
			crossItcCompute.setCamti(parentDto.getIsdItcCross().getCamti());
			crossItcCompute.setCess(parentDto.getIsdItcCross().getCess());
			crossItcCompute.setCreatedBy(userName);
			crossItcCompute.setCreatedOn(now);

			crossItcCompute = crossItcComputeRepo.save(crossItcCompute);
			return crossItcCompute;
		}
		LOGGER.error("NUll Gstin/TaxPeriod is not allowed {}, {}",
				parentDto.getGstin(), parentDto.getFp());
		return null;
	}
}
