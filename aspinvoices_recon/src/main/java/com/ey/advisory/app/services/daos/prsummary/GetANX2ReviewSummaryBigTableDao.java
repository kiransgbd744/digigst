package com.ey.advisory.app.services.daos.prsummary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.daos.initiaterecon.Anx2BigTableResponseDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;

/**
 * @author Siva.Nandam
 *
 */
@Component("GetANX2ReviewSummaryBigTableDao")
public class GetANX2ReviewSummaryBigTableDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Anx2BigTableResponseDto> getAnx2PRSProcessedRecs(
			Anx2PRSProcessedRequestDto request) {

		String taxperiod = request.getTaxPeriod();
		List<String> gstin = request.getGstins();

		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstin != null && gstin.size() > 0) {
				buildQuery.append(" AND A2_CGSTIN IN :gstin");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			
			buildQuery.append(" AND A2_DERIVED_RET_PERIOD  = :taxperiod ");
		}

		String queryStr = createProcessedQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (taxperiod != null && !taxperiod.isEmpty()) {
			
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriod());
			q.setParameter("taxperiod", derivedRetPeriodFrom);
			//q.setParameter("taxperiod", taxperiod);
		}

		if (gstin != null && !gstin.isEmpty() && gstin.size() > 0) {

			q.setParameter("gstin", gstin);
		}

		List<Object[]> list = q.getResultList();
		List<Anx2BigTableResponseDto> retList = list.parallelStream()
				.map(o -> convertProcessedInfo(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx2BigTableResponseDto convertProcessedInfo(Object[] arr) {
		Anx2BigTableResponseDto obj = new Anx2BigTableResponseDto();

		if (arr[0] == null || arr[0].toString().isEmpty()) {
			arr[0] = new BigDecimal("0");
		}
		if (arr[3] == null || arr[3].toString().isEmpty()) {
			arr[3] = new BigDecimal("0.0");
		}
		if (arr[4] == null || arr[4].toString().isEmpty()) {
			arr[4] = new BigDecimal("0.0");
		}
		if (arr[5] == null || arr[5].toString().isEmpty()) {
			arr[5] = new BigDecimal("0.0");
		}
		if (arr[6] == null || arr[6].toString().isEmpty()) {
			arr[6] = new BigDecimal("0.0");
		}
		if (arr[7] == null || arr[7].toString().isEmpty()) {
			arr[7] = new BigDecimal("0.0");
		}
		if (arr[8] == null || arr[8].toString().isEmpty()) {
			arr[8] = new BigDecimal("0.0");
		}
		if (arr[9] == null || arr[9].toString().isEmpty()) {
			arr[9] = new BigDecimal("0.0");
		}
		

		BigInteger count = GenUtil.getBigInteger(arr[0]);
		obj.setCount(count.intValue());
		obj.setTable((String) arr[2]);
		obj.setInvValue((BigDecimal) arr[3]);
		obj.setTaxableValue((BigDecimal) arr[4]);
		obj.setTotalTaxPayable((BigDecimal) arr[5]);
		obj.setTpIGST((BigDecimal) arr[6]);
		obj.setTpCGST((BigDecimal) arr[7]);
		obj.setTpSGST((BigDecimal) arr[8]);
		obj.setTpCess((BigDecimal) arr[9]);
		return obj;

	}

	private String createProcessedQueryString(String buildQuery) {

		String queryStr = "SELECT count(ID),A2_TAX_PERIOD, A2_INV_TYPE,"
				+ "SUM(A2_DOC_AMT) AS A2_DOC_AMT,"
				+ "SUM(A2_TAXABLE_VALUE) as A2_TAXABLE_VALUE,"
				+ "SUM(A2_IGST_AMT+A2_CGST_AMT+A2_SGST_AMT+A2_CESS_AMT) AS TOTAL_TAX,"
				+ "SUM(A2_IGST_AMT) as A2_IGST_AMT,SUM(A2_CGST_AMT) as A2_CGST_AMT,"
				+ "SUM(A2_SGST_AMT) AS A2_SGST_AMT,SUM(A2_CESS_AMT) AS A2_CESS_AMT"
				+ " FROM A2_PR_BIGTABLE"
				+ " WHERE(A2_EXIST_FLAG=TRUE AND IS_DELETE_FLAG = FALSE)"
				+ buildQuery + " GROUP BY(A2_TAX_PERIOD),(A2_INV_TYPE)";
		return queryStr;
	}

}
