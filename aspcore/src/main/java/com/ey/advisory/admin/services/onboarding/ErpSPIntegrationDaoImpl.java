/**
 * 
 */
package com.ey.advisory.admin.services.onboarding;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.ErpScenarioItmDetailsDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("ErpSPIntegrationDaoImpl")
public class ErpSPIntegrationDaoImpl implements ErpSPIntegrationDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<ErpScenarioItmDetailsDto> getErpSPItems(Long entityId) {

		StringBuilder buildQuery = new StringBuilder();

		if (entityId != null) {
			buildQuery.append(" AND ENTITY_ID = :entityId");
		}

		String queryStr = createQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (entityId != null) {
			q.setParameter("entityId", entityId);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public List<ErpScenarioItmDetailsDto> getErpEventsSP() {

		String queryStr = createEventQueryString();
		Query q = entityManager.createNativeQuery(queryStr);

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertEvents(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ErpScenarioItmDetailsDto convert(Object[] arr) {
		ErpScenarioItmDetailsDto erpScenario = new ErpScenarioItmDetailsDto();

		erpScenario.setGstinId((String) arr[0]);
		BigInteger scenarioId = GenUtil.getBigInteger(arr[1]);
		erpScenario.setScenarioId(scenarioId.intValue());
		BigInteger erpid = GenUtil.getBigInteger(arr[2]);
		erpScenario.setErpId(erpid.intValue());
		erpScenario.setDestName((String) arr[3]);
		erpScenario.setJobFrequency((String) arr[4]);
		erpScenario.setStartRootTag((String) arr[5]);
		erpScenario.setEndRootTag((String) arr[6]);
		erpScenario.setEndPointURI((String) arr[7]);
		erpScenario.setCompanyCode((String) arr[8]);
		return erpScenario;
	}

	private ErpScenarioItmDetailsDto convertEvents(Object[] arr) {

		ErpScenarioItmDetailsDto erpScenario = new ErpScenarioItmDetailsDto();
		BigInteger scenarioId = GenUtil.getBigInteger(arr[0]);
		erpScenario.setScenarioId(scenarioId.intValue());
		BigInteger erpid = GenUtil.getBigInteger(arr[1]);
		erpScenario.setErpId(erpid.intValue());
		erpScenario.setDestName((String) arr[2]);
		erpScenario.setStartRootTag((String) arr[3]);
		erpScenario.setEndRootTag((String) arr[4]);
		erpScenario.setEndPointURI((String) arr[5]);
		return erpScenario;
	}

	private String createQueryString(String buildQuery) {

		return "SELECT STRING_AGG(GSTIN_ID,',') GSTIN_ID,SCENARIO_ID,ERP_ID,"
				+ "DESTINATION_NAME,JOB_FREQUENCY,START_ROOT_TAG,END_ROOT_TAG,ENDPOINT_URI,COMPANY_CODE "
				+ "FROM SCENARIO_PERMISSION WHERE IS_DELETE = FALSE "
				+ buildQuery
				+ " GROUP BY SCENARIO_ID,ERP_ID,DESTINATION_NAME,JOB_FREQUENCY,START_ROOT_TAG,END_ROOT_TAG,ENDPOINT_URI,COMPANY_CODE ";
	}

	private String createEventQueryString() {

		return "SELECT SCENARIO_ID,ERP_ID,DESTINATION_NAME,START_ROOT_TAG,END_ROOT_TAG,ENDPOINT_URI "
				+ "FROM EVENTS_SCENARIO_PERMISSION  WHERE IS_DELETE = FALSE "
				+ " GROUP BY SCENARIO_ID,ERP_ID,DESTINATION_NAME,START_ROOT_TAG,END_ROOT_TAG,ENDPOINT_URI ";
	}
}
