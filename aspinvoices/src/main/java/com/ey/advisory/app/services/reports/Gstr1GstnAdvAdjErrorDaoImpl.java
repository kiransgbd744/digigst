/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1GstnADVADJErrorDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Gstr1GstnAdvAdjErrorDaoImpl")
public class Gstr1GstnAdvAdjErrorDaoImpl implements Gstr1GstnErrorDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GstnAdvAdjErrorDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getGstr1GstnErrorReport(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String GSTIN = null;
		List<String> gstinList = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}
		StringBuilder buildQuery = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND HDR.GSTIN IN :gstinList");
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
		}

		String queryStr = creategstnAdvAdjErrorQueryString(
				buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertgstnAdvAdjError(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1GstnADVADJErrorDto convertgstnAdvAdjError(Object[] arr) {
		Gstr1GstnADVADJErrorDto obj = new Gstr1GstnADVADJErrorDto();
		
		obj.setPlaceofSupply(arr[0] != null ? arr[0].toString() : null);
		obj.setSupplyType(arr[1] != null ? arr[1].toString() : null);
		obj.setDifferentialPercentage(
				arr[2] != null ? arr[2].toString() : null);
		obj.setAdvanceAmount(arr[3] != null ? arr[3].toString() : null);
		obj.setRate(arr[4] != null ? arr[4].toString() : null);
		obj.setIntegratedAmount(arr[5] != null ? arr[5].toString() : null);
		obj.setCentralTaxAmount(arr[6] != null ? arr[6].toString() : null);
		obj.setStateTaxAmount(arr[7] != null ? arr[7].toString() : null);
		obj.setCessAmount(arr[8] != null ? arr[8].toString() : null);
		obj.setErrorCode(arr[9] != null ? arr[9].toString() : null);
		obj.setGstnErrorMessage(arr[10] != null ? arr[10].toString() : null);

		return obj;
	}

	private String creategstnAdvAdjErrorQueryString(String buildQuery) {

		return "SELECT HDR.POS PLACE_OF_SUPPLUY,HDR.SUPPLY_TYPE,"
				+ "HDR.DIFF_PERCENT,HDR.ADVADJ_AMT,ITM.TAX_RATE,"
				+ "ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT,"
				+ "TRIM(', ' FROM IFNULL(ERROR_CODE,'')) AS GSTN_ERROR_CODE,"
				+ "TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION,'')) AS "
				+ "GSTN_ERROR_DESCRIPTION FROM GETGSTR1_TXP_HEADER HDR "
				+ "INNER JOIN GETGSTR1_TXP_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT OUTER JOIN ANX_OUTWARD_DOC_ERROR ERR ON HDR.ID = "
				+ "ERR.DOC_HEADER_ID AND ITM.HEADER_ID = ERR.DOC_HEADER_ID "
				+ "WHERE ERROR_SOURCE = 'GSTN'" + buildQuery;
	}
}
