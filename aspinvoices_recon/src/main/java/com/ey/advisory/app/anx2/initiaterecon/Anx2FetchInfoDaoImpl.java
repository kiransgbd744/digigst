package com.ey.advisory.app.anx2.initiaterecon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Arun.KA
 *
 */

@Component("Anx2FetchInfoDaoImpl")
public class Anx2FetchInfoDaoImpl implements Anx2FetchInfoDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx2FetchInfoDto> findAnx2FetchInfoByGstins(List<String> gstins,
			String taxPeriod) {

		List<Anx2FetchInfoDto> defaultInfo = gstins
				.stream()
				.map(o -> new Anx2FetchInfoDto("NOT_INITIATED", null, o))
				.collect(Collectors.toList());

		StringBuilder condition = new StringBuilder();

		if (gstins != null) {
			condition.append(" \"CGSTIN\" in (:gstins) ");
		}

		if (taxPeriod != null && !taxPeriod.equals("")) {
			condition.append(" AND \"TAX_PERIOD\" = :taxPeriod");
		}

		String queryString = "select \"CGSTIN\",\"STATUS\",\"MODIFIED_ON\" from "
				+ " \"CLIENT1_GST\".\"GSTR2A_GET_BATCH\" where " + condition
				+ "" + " AND IS_DELETE = false";

		Query q = entityManager.createNativeQuery(queryString);

		if (taxPeriod != null && !taxPeriod.isEmpty()) {
			q.setParameter("taxPeriod", taxPeriod);
		}

		if (gstins != null && gstins.size() > 0) {
			q.setParameter("gstins", gstins);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Anx2FetchInfoDto> retList = list.parallelStream()
				.map(o -> convertStatus(o))
				.collect(Collectors.toCollection(ArrayList::new));
		List<String> gstinListFromDB = retList.stream().map(o -> o.getGstin())
				.collect(Collectors.toList());

		defaultInfo = defaultInfo.stream()
				.filter(o -> !gstinListFromDB.contains(o.getGstin()))
				.collect(Collectors.toList());
		retList.addAll(defaultInfo);
		return retList;

	}

	private Anx2FetchInfoDto convertStatus(Object[] arr) {
		Anx2FetchInfoDto obj = new Anx2FetchInfoDto();
		obj.setGstin((String) arr[0]);
		obj.setLastFetchStatus((String) arr[1]);
		obj.setLastFetchDate((Date) arr[2]);
		
		return obj;
	}

}
