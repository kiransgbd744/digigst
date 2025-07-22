/**
 * 
 */
package com.ey.advisory.app.data.daos.client.simplified;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.RequestForApprovalRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Balakrishna.S
 *
 */
@Component("RequestForApprovalDaoImpl")
@Slf4j
public class RequestForApprovalDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<RequestForApprovalRespDto> loadBasicSummarySection(
			Annexure1SummaryReqDto request) {
		String statuscode = "";
		String taxPeriodReq = request.getTaxPeriod();
		// int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		StringBuilder build = new StringBuilder();
		List<RequestForApprovalRespDto> retList = null;
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" A.GSTIN IN :gstinList");
			}
		}
		if (taxPeriodReq != null) {

			build.append(" AND A.TAX_PERIOD = :taxPeriodReq  ");
		}
		String buildQuery = build.toString();
		String queryStr = createQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is {} ", queryStr);
		}
		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriodReq != null) {
				q.setParameter("taxPeriodReq", taxPeriodReq);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");

			retList = list.parallelStream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception e) {
			LOGGER.error("Exception", e);
		}
		return retList;
	}

	private RequestForApprovalRespDto convert(Object[] arr) {
		RequestForApprovalRespDto obj = new RequestForApprovalRespDto();
		String lastUpdatedDate = "";
		obj.setStatus((String) arr[0]);
		LocalDateTime localdateTime = ((Timestamp) arr[1]).toLocalDateTime();
		if (localdateTime != null) {
			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(localdateTime);

			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("yyyy-MM-dd HH:mm:ss");
			lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);
		}
		obj.setTimeStamp(lastUpdatedDate);
		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request For Approval Query Execution Starts");
		}
		
		String queryStr = "SELECT APPROVAL_STATUS,CREATED_ON FROM REQUEST_FOR_APPROVAL WHERE "
				+ buildQuery
				+ "IS_DELETE = FALSE";

	/*	String queryStr = "SELECT B.STATUS,B.CREATED_ON FROM REQUEST_FOR_APPROVAL A "
				+ "INNER JOIN REQUEST_STATUS B ON A.ID = REQUEST_ID " + "WHERE "
				+ buildQuery + "IS_DELETE = FALSE "
				+ "AND A.RETURN_TYPE = 'GSTR1'";*/

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request For Approval Query Execution " + "ends {}",
					queryStr);
		}
		return queryStr;
	}

}
