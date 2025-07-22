package com.ey.advisory.app.services.daos.listgstinforrecon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.anx2.Anx2ListGSTINForReconReq;
import com.ey.advisory.app.docs.dto.anx2.Anx2ListGSTINForReconResp;
import com.ey.advisory.common.GenUtil;

@Component("Anx2ListGstinsForReconDaoImpl")
public class Anx2ListGstinsForReconDaoImpl
		implements Anx2ListGstinsForReconDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getAnx2ListGstinsForRecondao(
			Anx2ListGSTINForReconReq dto) {
		List<String> cgstins = dto.getCgstins();
		String returnperiod = dto.getReturnPeriod();

		StringBuffer buidQuery = new StringBuffer();

		if (returnperiod != null) {
			buidQuery.append(" AND DERIVED_RET_PERIOD = :returnperiod");
		}

		String queryStr = createQueryString(buidQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (cgstins != null && !cgstins.isEmpty()) {
			q.setParameter("cgstins", cgstins);
		}
		if (returnperiod != null && !returnperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(dto.getReturnPeriod());
			q.setParameter("returnperiod", derivedRetPeriod);
		}

		List<Object[]> list = q.getResultList();
		List<Object> retList = list.parallelStream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx2ListGSTINForReconResp convert(Object[] arr) {
		Anx2ListGSTINForReconResp reconentity = new Anx2ListGSTINForReconResp();

		reconentity.setCgstin((String) arr[0]);
		reconentity.setState((String) arr[1]);
		reconentity.setUpdatedDate(LocalDateTime.now());
		reconentity.setStatus("Success");

		return reconentity;
	}

	private String createQueryString(String buidQuery) {

		String queryStr = "SELECT DISTINCT CUST_GSTIN,fnGetState(CUST_GSTIN) State "
				+ "FROM ANX_INWARD_DOC_HEADER WHERE CUST_GSTIN IN (:cgstins) "
				+ buidQuery;

		return queryStr;
	}

}
