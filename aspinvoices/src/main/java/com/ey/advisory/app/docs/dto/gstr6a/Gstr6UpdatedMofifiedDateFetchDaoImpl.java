package com.ey.advisory.app.docs.dto.gstr6a;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

@Component("Gstr6UpdatedMofifiedDateFetchDaoImpl")
public class Gstr6UpdatedMofifiedDateFetchDaoImpl {

	// LastUpdatedDateDto

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6UpdatedMofifiedDateFetchDaoImpl.class);

	public String loadBasicSummarySection(Annexure1SummaryReqDto request) {
		String lastUpdatedDate = "";
		String taxPeriodReq = request.getTaxPeriod();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}
		StringBuilder build = new StringBuilder();
		if (gstin != null && !gstin.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			build.append(" GSTIN IN :gstinList");
		}
		if (taxPeriodReq != null) {
			build.append(" AND TAX_PERIOD = :taxPeriodReq  ");
		}
		String buildQuery = build.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Prepared where Condition and apply in Outward Query BEGIN");
		}
		String queryStr = createQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is:{}", queryStr);
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
			List<Object> list = q.getResultList();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ResultList data Converting to Dto");
			}

			LocalDateTime localdateTime = null;

			if (list.get(0) != null) {
				localdateTime = ((Timestamp) list.get(0)).toLocalDateTime();
			}
			if (localdateTime != null) {
				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(localdateTime);
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("yyyy-MM-dd HH:mm:ss");
				lastUpdatedDate = formatter.format(istDateTimeFromUTC);
			}

		} catch (Exception e) {
			LOGGER.error("Exception", e);
		}
		return lastUpdatedDate;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Outward Query Execution BEGIN ");
		}
		String queryStr = "SELECT MAX(CREATED_ON) LAST_UPDATED_DATE "
				+ "FROM GETGSTR6_ISD_SUMMARY WHERE " + "IS_DELETE = FALSE AND "
				+ buildQuery;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Last Updated Date  Query Execution END ");
		}
		return queryStr;
	}

	
	
}
