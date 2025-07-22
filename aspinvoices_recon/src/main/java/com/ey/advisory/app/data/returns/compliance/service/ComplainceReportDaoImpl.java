/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.StateCodeInfoEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.google.common.collect.Lists;

import com.google.common.base.Strings;

/**
 * @author Sujith.Nanga
 *
 */

@Component("ComplainceReportDaoImpl")
public class ComplainceReportDaoImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ComplainceReportDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	public List<Object> getComplaince(Gstr2ProcessedRecordsReqDto criteria) {
		String returnType = criteria.getReturnType();
		String fy = criteria.getFinancialYear();
		String startMonth = "04";
		String endMonth = "03";
		String appendMonthYear = null;
		String appendMonthYear1 = null;
		if (fy != null && !fy.isEmpty()) {
			String[] arrOfStr = fy.split("-", 2);
			appendMonthYear = arrOfStr[0] + startMonth;
			appendMonthYear1 = "20" + arrOfStr[1] + endMonth;
		}
		int derivedStartPeriod = Integer.parseInt(appendMonthYear);

		int derivedEndPeriod = Integer.parseInt(appendMonthYear1);

		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();

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
				buildQuery.append(" WHERE GSTIN IN :gstinList");

			}
		}

		if (returnType != null && !returnType.isEmpty()) {
			buildQuery.append(" AND RETURN_TYPE = :returnType ");
		}

		buildQuery.append(
				" AND TAXPERIOD BETWEEN :derivedStartPeriod AND :derivedEndPeriod");

		String queryStr = creategstnTransQueryString(buildQuery.toString());

		Query outquery = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				outquery.setParameter("gstinList", gstinList);
			}
		}

		if (returnType != null && !returnType.isEmpty()) {

			outquery.setParameter("returnType", returnType);
		}

		outquery.setParameter("derivedStartPeriod", derivedStartPeriod);
		outquery.setParameter("derivedEndPeriod", derivedEndPeriod);

		List<Object[]> list = outquery.getResultList();
		List<Object> verticalHsnList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			List<StateCodeInfoEntity> MasterEntities = statecodeRepository
					.findAll();
			Map<String, String> hsnMap = new HashMap<String, String>();
			MasterEntities.forEach(entity -> {
				hsnMap.put(entity.getStateCode(), entity.getStateName());
			});

			for (Object arr[] : list) {
				verticalHsnList.add(convertcomplaince(arr, hsnMap));
			}
		}
		return verticalHsnList;
	}

	private ReturnComplianceDto convertcomplaince(Object[] arr,
			Map<String, String> hsnMap) {
		ReturnComplianceDto obj = new ReturnComplianceDto();

		obj.setgStin(arr[0] != null ? arr[0].toString() : null);

		List<String> regName = gSTNDetailRepository
				.findRegTypeByGstin(obj.getgStin());
		if (regName != null && regName.size() > 0) {
			String regTypeName = regName.get(0);

			obj.setRegistrationType(regTypeName.toUpperCase());
		} else {
			obj.setRegistrationType("");
		}

		// obj.setRegistrationType(arr[1] != null ? arr[1].toString() : null);
		String stateName = null;
		String stateCode = (arr[2] != null ? arr[2].toString() : null);
		obj.setStateCode(stateCode);

		if (!Strings.isNullOrEmpty(stateCode)) {
			stateName = stateCode + "-" + hsnMap.get(stateCode);
		}
		obj.setStateName(stateName);

		obj.setTaxPeriod(arr[4] != null ? arr[4].toString() : null);
		obj.setReturnType(arr[5] != null ? arr[5].toString() : null);

		if (arr[6] != null) {

			LOGGER.debug("FillingSubDate :" + arr[6].toString());

			String timestamp = arr[6].toString();
			String[] dateTime = timestamp.split(" ");

			String date = dateTime[0];
			String time = dateTime[1];
			obj.setFillingSubDate(date);
			obj.setFillingSubTime(time);

		}
		obj.setArnNo(arr[7] != null ? arr[7].toString() : null);
		obj.setStatus(arr[8] != null ? arr[8].toString() : null);

		return obj;

	}

	private String creategstnTransQueryString(String buildQuery) {

		return "SELECT GSTIN,'' REGIST_TYPE,SUBSTR(GSTIN,1,2) AS STATE_CODE, "
				+ "'' STATE_NAME,TAXPERIOD,RETURN_TYPE,FILING_DATE,ARN_NO,"
				+ " STATUS FROM GSTR_RETURN_STATUS  " + buildQuery;

	}

}
