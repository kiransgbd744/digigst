package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.docs.dto.simplified.TDSSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Component("TDSSummaryFetchDaoImpl")
public class TDSSummaryFetchDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;
	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	public List<TDSSummaryRespDto> fetchTdsSummaryRecords(ITC04RequestDto req) {

		List<Long> entityId = req.getEntityId();
		String taxPeriodReq = req.getTaxPeriod();
		String action = req.getAction();
		List<TDSSummaryRespDto> retList = new ArrayList<>();
		Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("TDSSummaryFetchDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {},"
					+ "dataSecAttrs -> {}", req);
		}
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
		// StringBuilder buildTCS = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" GSTIN IN (:gstinList) ");
				// buildTCS.append(" AND TCS.GSTIN IN (:gstinList) ");
			}
		}
		if (taxPeriod != null) {

			build.append(" AND DERIVED_RET_PERIOD  = :taxPeriod ");
			// buildTCS.append(" AND TCS.DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		// String buildQuery = buildTCS.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Preparing Build Query For TDS Review Summary is {}"
					+ buildQuery);
		}

		String queryStr = createQueryString(buildQuery, action);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Executing Query For TDS Review Summary is {}" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriod != null) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ResultList data Converting to Dto --->{}", list);
			}
			retList = list.parallelStream().map(o -> convert(o, action))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Executing TDS Query Issue -----> {}", e);
		}
		return retList;
	}

	@SuppressWarnings("static-access")
	private TDSSummaryRespDto convert(Object[] arr, String action) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Array data Setting to Dto");
		}
		TDSSummaryRespDto dto = new TDSSummaryRespDto();
		String gstin = null;
		if (arr.length > 0) {
			if ("TDS".equalsIgnoreCase(action)) {

				String docKey = String.valueOf(arr[0]);
				dto.setDocKey(docKey);
				gstin = String.valueOf(arr[1]);
				dto.setGstinDeductee(gstin);

				dto.setMofDeductorUpld((String) arr[2]);
				dto.setTotamount((BigDecimal) arr[3]);
				dto.setAmountIgst((BigDecimal) arr[4]);
				dto.setAmountCgst((BigDecimal) arr[5]);
				dto.setAmountSgst((BigDecimal) arr[6]);
				dto.setUserAction((String) arr[7]);
				dto.setSavedAction((String) arr[8]);
			} else if ("TDSA".equalsIgnoreCase(action)) {

				String docKey = String.valueOf(arr[0]);
				dto.setDocKey(docKey);

				gstin = String.valueOf(arr[1]);
				dto.setGstinDeductee(gstin);

				dto.setMofDeductorUpld((String) arr[2]);
				dto.setOrgMonOfDeductorUpld((String) arr[3]);
				dto.setTotamount((BigDecimal) arr[4]);
				dto.setAmountIgst((BigDecimal) arr[5]);
				dto.setAmountCgst((BigDecimal) arr[6]);
				dto.setAmountSgst((BigDecimal) arr[7]);
				dto.setUserAction((String) arr[8]);
				dto.setSavedAction((String) arr[9]);

			} else if ("TCS".equalsIgnoreCase(action)) {

				String docKey = String.valueOf(arr[0]);
				dto.setDocKey(docKey);

				gstin = String.valueOf(arr[1]);
				// dto.setGstinDeductee(gstin);
				dto.setGstinOfColectr(gstin);
				dto.setMonOfcollectorUpld((String) arr[2]);
				dto.setPos((String) arr[3]);

				dto.setTotamount((BigDecimal) arr[4]);
				dto.setSuppliesToRegisteredBuyers((BigDecimal) arr[5]);
				dto.setSuppliesReturnedbyRegisteredBuyers((BigDecimal) arr[6]);
				dto.setSuppliestoURbuyers((BigDecimal) arr[7]);
				dto.setSuppliesReturnedbyURbuyers((BigDecimal) arr[8]);
				dto.setAmountIgst((BigDecimal) arr[9]);
				dto.setAmountCgst((BigDecimal) arr[10]);
				dto.setAmountSgst((BigDecimal) arr[11]);
				dto.setUserAction((String) arr[12]);
				dto.setDigiGstRemarks((String) arr[13]);
				dto.setDigiGstComment((String) arr[14]);
				dto.setSavedAction((String) arr[15]);
				dto.setGstnRemarks((String) arr[16]);
				dto.setGstnComment((String) arr[17]);



			} else if ("TCSA".equalsIgnoreCase(action)) {

				String docKey = String.valueOf(arr[0]);
				dto.setDocKey(docKey);

				gstin = String.valueOf(arr[1]);
				// dto.setGstinDeductee(gstin);
				dto.setGstinOfColectr(gstin);
				dto.setMonOfcollectorUpld((String) arr[2]);
				dto.setPos((String) arr[3]);
				dto.setOrgmonOfcollectorUpld((String) arr[4]);
				dto.setTotamount((BigDecimal) arr[5]);
				dto.setSuppliesToRegisteredBuyers((BigDecimal) arr[6]);
				dto.setSuppliesReturnedbyRegisteredBuyers((BigDecimal) arr[7]);
				dto.setSuppliestoURbuyers((BigDecimal) arr[8]);
				dto.setSuppliesReturnedbyURbuyers((BigDecimal) arr[9]);
				dto.setAmountIgst((BigDecimal) arr[10]);
				dto.setAmountCgst((BigDecimal) arr[11]);
				dto.setAmountSgst((BigDecimal) arr[12]);
				dto.setUserAction((String) arr[13]);
				dto.setDigiGstRemarks((String) arr[14]);
				dto.setDigiGstComment((String) arr[15]);
				dto.setSavedAction((String) arr[16]);
				dto.setGstnRemarks((String) arr[17]);
				dto.setGstnComment((String) arr[18]);

			}

		}
		return dto;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery, String action) {
		// TODO Auto-generated method stub
		String queryStr = null;
		if ("TDS".equalsIgnoreCase(action)) {

			queryStr = "SELECT * FROM GSTR2X_RS_TDS WHERE " + buildQuery;

		} else if ("TDSA".equalsIgnoreCase(action)) {

			queryStr = "SELECT * FROM GSTR2X_RS_TDSA WHERE " + buildQuery;

		} else if ("TCS".equalsIgnoreCase(action)) {

			queryStr = "SELECT * FROM GSTR2X_RS_TCS WHERE " + buildQuery;

		} else if ("TCSA".equalsIgnoreCase(action)) {

			queryStr = "SELECT * FROM GSTR2X_RS_TCSA WHERE " + buildQuery;

		}
		return queryStr;

	}

}
