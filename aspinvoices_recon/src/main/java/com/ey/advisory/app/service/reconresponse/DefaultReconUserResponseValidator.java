/**
 * 
 */
package com.ey.advisory.app.service.reconresponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.LinkA2PREntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReconUserResponseError;
import com.ey.advisory.app.data.repositories.client.asprecon.LinkA2PRRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReconUserResponseErrorRepository;
import com.google.common.collect.ImmutableMap;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */
@Component("DefaultReconUserResponseValidator")
@Slf4j
@Scope("prototype")
public class DefaultReconUserResponseValidator
		implements ReconUserResponseValidator {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ReconUserResponseErrorRepository")
	ReconUserResponseErrorRepository reconUserResponseErrorRepo;

	@Autowired
	@Qualifier("LinkA2PRRepository")
	LinkA2PRRepository linkA2PRRepository;

	private Long errorCount = 0L;

	private static final Map<String, String> MATCH_MISMATCH_PROBABLE_RESPONSES = new ImmutableMap.Builder<String, String>()
			.put("A1", "A1").put("A2", "A2").put("A3", "A3").put("P1", "P1")
			.put("R1", "R1").put("R1U1", "R1U1").put("R1U2", "R1U2")
			.put("A1U1", "A1U1").put("A1U2", "A1U2").put("A4", "A4")
			.put("C1", "C1").build();

	private static final Map<String, String> ADDITIONAL_ANX2_RESPONSES = new ImmutableMap.Builder<String, String>()
			.put("A1", "A1").put("P1", "P1").put("R1", "R1").put("C1", "C1")
			.build();

	private static final Map<String, String> ADDITIONAL_PR_RESPONSES = new ImmutableMap.Builder<String, String>()
			.put("U1", "U1").put("U2", "U2").put("C1", "C1").build();
	private static final Map<String, String> INVALID_REPORT_TYPE = new ImmutableMap.Builder<String, String>()
			.put("Invalid Report Type", "Invalid Report Type").build();

	public static final Map<String, String> getMapForRptType(String rptType) {
		switch (rptType) {
		case "Exact Match":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "Doc Date & Value Mismatch":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "Document Type Mismatch":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "Probable-1":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "Probable-11":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "Match upto Tolerance":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "Value Mismatch":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "POS Mismatch":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "Document Date Mismatch":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "Multi-Mismatch":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "Fuzzy Match":
			return MATCH_MISMATCH_PROBABLE_RESPONSES;
		case "Addition in ANX-2":
			return ADDITIONAL_ANX2_RESPONSES;
		case "Addition in PR":
			return ADDITIONAL_PR_RESPONSES;
		default:
			return INVALID_REPORT_TYPE;
		}

	}

	@Override
	public void validateAndProcessChunk(List<GetReconResponseValidDto> chunk,
			Long fileId) {

		LOGGER.debug("Validating and processing chunk data");

		List<String> gstins = chunk.stream().map(e -> e.getGstin())
				.collect(Collectors.toList());

		Map<String, Long> gstinTaxPeriodStrs = getLatestReqIdsForGstinTaxPeriods(
				gstins, chunk.get(0).getTaxPeriod());

		List<GetReconResponseValidDto> errorList = new ArrayList<>();
		List<GetReconResponseValidDto> validResponseList = new ArrayList<>();
		for (GetReconResponseValidDto obj : chunk) {

			String reportType = obj.getReportType();

			Map<String, String> map = getMapForRptType(reportType);

			if (gstinTaxPeriodStrs
					.containsKey(obj.getGstin() + "-" + obj.getTaxPeriod())) {

				Long reqId = gstinTaxPeriodStrs
						.get(obj.getGstin() + "-" + obj.getTaxPeriod());

				if (reqId.equals(obj.getRequestId())) {
					if (map.containsKey(obj.getUserResponse()) 
							|| obj.getUserResponse().isEmpty()
							|| "".equals(obj.getUserResponse())
							|| obj.getUserResponse() == null 
							|| obj.getUserResponse().replace(" ", "").equals("")) {
						validResponseList.add(obj);
					} else {
						obj.setErrMSg("Invalid User Response");
						errorList.add(obj);
					}
				} else {
					obj.setErrMSg("Not updated request ID");
					errorList.add(obj);
				}
			}

		}

		insertErrorRecords(errorList, fileId);
		updateUserResponseRecord(validResponseList);

	}

	private void insertErrorRecords(List<GetReconResponseValidDto> errorList,
			Long fileId) {

		LOGGER.debug("Inserting error records");
		List<ReconUserResponseError> errorsToBeSaved = new ArrayList<>();
		for (GetReconResponseValidDto obj : errorList) {
			ReconUserResponseError entity = new ReconUserResponseError();
			entity.setReportId(
					obj.getRequestId() != null ? obj.getRequestId() : null);
			entity.setA2InvoiceKey(obj.getInvoiceKeyA2() != null
					? obj.getInvoiceKeyA2() : null);
			entity.setPrInvoiceKey(obj.getInvoiceKeyPR() != null
					? obj.getInvoiceKeyPR() : null);
			entity.setCreatedBy("SYSTEM");
			entity.setCreatedOn(LocalDateTime.now());
			entity.setUpdatedOn(LocalDateTime.now());
			entity.setErrDesc(obj.getErrMSg());
			entity.setStatus("UPDATED");
			entity.setFileId(fileId);
			entity.setUserResponse(obj.getUserResponse());
			errorsToBeSaved.add(entity);
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Number of error records  : %d",
					errorsToBeSaved.size());
			LOGGER.debug(msg);
		}
		
		if (!errorsToBeSaved.isEmpty()) {
			reconUserResponseErrorRepo.saveAll(errorsToBeSaved);
			errorCount += errorsToBeSaved.size();
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Error Count : %s",
					errorCount.toString());
			LOGGER.debug(msg);
		}

	}

	@Transactional
	private void updateUserResponseRecord(
			List<GetReconResponseValidDto> userResponseList) {

		LOGGER.debug("Updating valid user response in table");

		List<LinkA2PREntity> entitiesToBeSaved = new ArrayList<>();
		for (GetReconResponseValidDto obj : userResponseList) {
			String a2Key = obj.getInvoiceKeyA2();
			String prKey = obj.getInvoiceKeyPR();
			Long reqId = obj.getRequestId();
			String userResponse = obj.getUserResponse();
			
			if(!( userResponse.isEmpty()
			|| "".equals(userResponse)
			|| userResponse == null 
			|| userResponse.replace(" ", "").equals("") )){
			
			if (LOGGER.isDebugEnabled()){
				String msg = String.format("Finding the Record in Link Table with"
						+ " A2 Key '%s', PR Key '%s', reqId '%d'",
				a2Key, prKey, reqId);
				LOGGER.debug(msg);
			}
			
			LinkA2PREntity entity = linkA2PRRepository
					.findByA2InvoiceKeyAndPrInvoiceKeyAndReconReportConfigId(
							a2Key, prKey, reqId);
			if (entity != null) {
				
				
				if(obj.getUserResponse().equalsIgnoreCase("C1"))
					entity.setUserResponse(null);
				else
					entity.setUserResponse(obj.getUserResponse());
						
				entitiesToBeSaved.add(entity);
				
				if (LOGGER.isDebugEnabled()){
					String msg = String.format(
							"Found the Record in Link Table with"
									+ " A2 Key '%s', PR Key '%s', reqId '%d'",
							a2Key, prKey, reqId);
					LOGGER.debug(msg);
				}
				
			}
		}
		}
		if (!entitiesToBeSaved.isEmpty()) {
			linkA2PRRepository.saveAll(entitiesToBeSaved);
			
			if (LOGGER.isDebugEnabled()){
				String msg = String.format(
						"Saved the list of User Responses '%s'",
						entitiesToBeSaved.toString());
				LOGGER.debug(msg);
			}
		}

	}

	/**
	 * 
	 * @param list
	 * @return
	 */

	private Map<String, Long> getLatestReqIdsForGstinTaxPeriods(
			List<String> list, String taxPeriod) {
		// The key of the return map should be GSTIN and tax period separated
		// by a pipe symbol. The value should be the latest recon request id for
		// this GSTIN and tax period combination.

		LOGGER.error(list.toString());
		LOGGER.error(taxPeriod);

		String queryStr = "SELECT  GD.RECON_REPORT_CONFIG_ID,GD.GSTIN,RC.TAX_PERIOD,"
				+ "GD.STATUS FROM  RECON_REPORT_GSTIN_DETAILS GD "
				+ "INNER JOIN RECON_REPORT_CONFIG RC "
				+ "ON RC.RECON_REPORT_CONFIG_ID=GD.RECON_REPORT_CONFIG_ID where "
				+ " GD.GSTIN IN ( :gstins) AND RC.TAX_PERIOD= :taxPeriod AND "
				+ "GD.IS_ACTIVE=TRUE";

		Query q = entityManager.createNativeQuery(queryStr);

		if (taxPeriod != null && !taxPeriod.isEmpty()) {
			q.setParameter("taxPeriod", taxPeriod);
		}

		if (list != null && list.size() > 0) {
			q.setParameter("gstins", list);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> result = q.getResultList();

		List<GetReconResponseLatestIdDto> latestIdList = result.stream()
				.map(o -> convert(o)).collect(Collectors.toList());

		Map<String, Long> mapList = new HashMap<String, Long>();

		latestIdList.forEach(o -> {
			mapList.put(o.getGstin() + "-" + o.getTaxPeriod(),
					o.getRequestId());
		});

		return mapList;
	}

	private GetReconResponseLatestIdDto convert(Object[] arr) {

		GetReconResponseLatestIdDto obj = new GetReconResponseLatestIdDto();

		obj.setRequestId(arr[0] != null ? Long.valueOf(arr[0].toString()) : 0L);
		obj.setGstin(arr[1].toString());
		obj.setTaxPeriod(arr[2].toString());
		return obj;

	}

	@Override
	public Long getErrorCount() {
		return errorCount;
	}

}
