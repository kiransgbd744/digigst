/**
 * 
 */
package com.ey.advisory.app.anx1.counterparty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;



/**
 * @author Arun KA
 *
 */

@Component("Anx1FetchInfoDaoImpl")
public class Anx1FetchInfoDaoImpl implements Anx1FetchInfoDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx1FetchInfoDto> findAnx1FetchInfoByGstins(List<String> gstins,
			String taxPeriod) {

		StringBuilder condition = new StringBuilder();

		if (gstins != null) {
			condition.append(" \"GSTIN\" in (:sgstinsList) ");
		}

		if (taxPeriod != null && !taxPeriod.equals("")) {
			condition.append(" AND \"RETURN_PERIOD\" = :taxPeriod");
		}

		String queryString = "select \"GSTIN\",\"STATUS\",\"CREATED_ON\" from "
				+ " \"CLIENT1_GST\".\"GETANX1_BATCH_TABLE\" where " + condition;

		Query q = entityManager.createNativeQuery(queryString);

		if (taxPeriod != null && !taxPeriod.isEmpty()) {
			q.setParameter("taxPeriod", taxPeriod);
		}

		if (gstins != null && gstins.size() > 0) {
			q.setParameter("sgstinsList", gstins);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Anx1FetchInfoDto> retList = list.parallelStream()
				.map(o -> convertStatus(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;

	}

	private Anx1FetchInfoDto convertStatus(Object[] arr) {
		Anx1FetchInfoDto obj = new Anx1FetchInfoDto();
		obj.setGstin((String)arr[0]);
		String status = (String) arr[1];
		if (status.equalsIgnoreCase("SUCCESS"))
			status = "S";
		else if (status.equalsIgnoreCase("FAILED"))
			status = "F";
		else
			status = "IP";
		obj.setLastFetchStatus(status);
		obj.setLastFetchDate((Date) arr[2]);

		return obj;
	}

}
