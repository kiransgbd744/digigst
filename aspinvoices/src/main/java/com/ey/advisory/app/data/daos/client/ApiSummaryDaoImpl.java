package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.ApiSummaryResDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("ApiSummaryDaoImpl")
public class ApiSummaryDaoImpl implements BasicApiSummaryDao {
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<ApiSummaryResDto> loadApiSummarySection(String criteria,
			List<String> selectedSgtins, List<LocalDate> selectedDates,
			LocalDate docDataFrom, LocalDate docDataTo, LocalDate docRecFrom,
			LocalDate docRecTo, int derRetPeriodFrom, int derRetPeriodTo,
			List<Long> entityId) {
		String queryStr = createQueryString(criteria);
		Query q = entityManager.createNativeQuery(queryStr);

		if (docDataFrom != null) {
			q.setParameter("docDateFrom", docDataFrom);
			q.setParameter("docDateTo", docDataTo);
		} else if (docRecFrom != null) {
			q.setParameter("dataRecvFrom", docRecFrom);
			q.setParameter("dataRecvTo", docRecTo);
		} else if (derRetPeriodFrom != 0) {
			q.setParameter("retPeriodFrom", derRetPeriodFrom);
			q.setParameter("retPeriodTo", derRetPeriodTo);
		}
		if (entityId != null && entityId.size() > 0) {
			q.setParameter("entityId", entityId);
		}
		if (selectedSgtins != null && selectedSgtins.size() > 0) {
			q.setParameter("gstin", selectedSgtins);
		}
		if (selectedDates != null && selectedDates.size() > 0) {
			q.setParameter("dates", selectedDates);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<ApiSummaryResDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private ApiSummaryResDto convert(Object[] arr) {
		ApiSummaryResDto obj = new ApiSummaryResDto();
		java.sql.Date recvDate = (java.sql.Date) arr[0];
		obj.setReceivedDate(recvDate.toLocalDate());
		/*java.sql.Date docDate = (java.sql.Date) arr[1];
		obj.setDocDate(docDate.toLocalDate());
		*/obj.setGstin((String) arr[1]);
		obj.setPeriod((String) arr[2]);
		obj.setSection((String) arr[3]);
		obj.setCount((Integer) arr[4]);
		obj.setTaxableValue((BigDecimal) arr[5]);
		obj.setToatlTaxes((BigDecimal) arr[6]);
		obj.setIgst((BigDecimal) arr[7]);
		obj.setCgst((BigDecimal) arr[8]);
		obj.setSgst((BigDecimal) arr[9]);
		obj.setCess((BigDecimal) arr[10]);
		obj.setAuthToken("");
		obj.setStatus("");
		return obj;
	}

	private String createQueryString(String criteria) {

		String queryStr = "SELECT RECEIVED_DATE, SUPPLIER_GSTIN, "
				+ "RETURN_PERIOD, TABLE_SECTION, COUNT, TAXABLE_VALUE, "
				+ "TAX_PAYABLE, IGST, CGST, SGST, CESS FROM "
				+ "\"GST_VIEW/API_SUMMARY\" WHERE "+ criteria + ", "
				+ "SUPPLIER_GSTIN, RETURN_PERIOD ASC";

		return queryStr;
	}

}
