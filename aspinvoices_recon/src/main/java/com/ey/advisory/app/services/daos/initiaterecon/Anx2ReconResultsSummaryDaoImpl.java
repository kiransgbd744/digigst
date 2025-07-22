package com.ey.advisory.app.services.daos.initiaterecon;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;

@Component("Anx2ReconResultsSummaryDaoImpl")
public class Anx2ReconResultsSummaryDaoImpl
		implements Anx2ReconResultsSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx2ReconResultsSummaryResDto> reconResultsabsoluteMatchSummary(
			List<String> sgstins, String retPeriod) {
		String queryStr = createQueryString(sgstins, retPeriod);
		Query q = entityManager.createNativeQuery(queryStr);

		if (retPeriod != null && !retPeriod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(retPeriod);
			q.setParameter("returnPeriod", derivedRetPeriod);

		}

		if (sgstins != null && !sgstins.isEmpty()) {
			q.setParameter("gstin", sgstins);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Anx2ReconResultsSummaryResDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx2ReconResultsSummaryResDto convert(Object[] arr) {
		Anx2ReconResultsSummaryResDto obj = new Anx2ReconResultsSummaryResDto();

		obj.setAction((String) arr[0]);
		obj.setTaxPeriod((String) arr[1]);
		obj.setMyGSTIN((String) arr[2]);
		obj.setVendorGSTIN((String) arr[14]);
		obj.setVendorTradeNmae(null);
		BigInteger bb = GenUtil.getBigInteger(arr[3]);
		obj.setCount(bb.intValue());
		obj.setElTaxableValue((BigDecimal) arr[9]);
		obj.setElIGST((BigDecimal) arr[10]);
		obj.setElCGST((BigDecimal) arr[11]);
		obj.setElSGST((BigDecimal) arr[12]);
		obj.setElCess((BigDecimal) arr[13]);
		obj.setAvTaxableValue((BigDecimal) arr[4]);
		obj.setAvIGST((BigDecimal) arr[5]);
		obj.setAvCGST((BigDecimal) arr[6]);
		obj.setAvSGST((BigDecimal) arr[7]);
		obj.setAvCess((BigDecimal) arr[8]);
		return obj;

	}

	private String createQueryString(List<String> selectedSgtins,
			String retPeriod) {
		StringBuffer condition = new StringBuffer();

		if (selectedSgtins != null && !selectedSgtins.isEmpty()) {
			condition.append(" \"A2_CGSTIN\" in :gstin");
		}

		if (retPeriod != null) {
			condition.append(" AND \"A2_DERIVED_RET_PERIOD\" = :returnPeriod");
		}

		String queryStr = "SELECT \"A2_ACTION_TAKEN\", \"A2_TAX_PERIOD\","
				+ " \"A2_CGSTIN\", " + "COUNT(\"A2_ID\") AS \"A2_ID\","
				+ "SUM(\"A2_TAXABLE_VALUE\") AS \"A2_TAXABLE_VALUE\","
				+ "SUM(\"A2_IGST_AMT\") AS \"A2_IGST_AMT\","
				+ "SUM(\"A2_CGST_AMT\") AS \"A2_CGST_AMT\","
				+ "SUM(\"A2_SGST_AMT\") AS \"A2_SGST_AMT\","
				+ "SUM(\"A2_CESS_AMT\") AS \"A2_CESS_AMT\","
				+ "SUM(\"E_TAXABLE_VALUE\") AS \"E_TAXABLE_VALUE\","
				+ "SUM(\"E_IGST\") AS \"E_IGST\","
				+ "SUM(\"E_CGST\") AS \"E_CGST\","
				+ "SUM(\"E_SGST\") AS \"E_SGST\","
				+ "SUM(\"E_CESS\") AS \"E_CESS\"," + "\"A2_SGSTIN\" "
				+ " FROM \"A2_PR_BIGTABLE\" WHERE " + condition.toString()
				+ " AND \"A2_EXIST_FLAG\" = TRUE AND \"PR_EXIST_FLAG\" = TRUE "
				+ "GROUP BY (\"A2_SGSTIN\"),(\"A2_CGSTIN\"),"
				+ "(\"A2_ACTION_TAKEN\"),(\"A2_TAX_PERIOD\") ";

		return queryStr;
	}

	@Override
	public List<Anx2ReconResultsMisMatchSummaryResDto> reconResultmisMatchSummary(
			List<String> sgstins, String retPeriod) {
		String queryStr = createmisMatchQueryString(sgstins, retPeriod);
		Query q = entityManager.createNativeQuery(queryStr);

		if (retPeriod != null && !retPeriod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(retPeriod);
			q.setParameter("returnPeriod", derivedRetPeriod);

		}

		if (sgstins != null && !sgstins.isEmpty()) {
			q.setParameter("gstin", sgstins);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Anx2ReconResultsMisMatchSummaryResDto> retList = list
				.parallelStream().map(o -> misMatchconvert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx2ReconResultsMisMatchSummaryResDto misMatchconvert(
			Object[] arr) {
		Anx2ReconResultsMisMatchSummaryResDto obj = new Anx2ReconResultsMisMatchSummaryResDto();

		obj.setAction((String) arr[14]);
		obj.setTaxPeriod((String) arr[0]);
		obj.setMyGSTIN((String) arr[1]);
		obj.setVendorGSTIN((String) arr[13]);
		obj.setVendorTradeNmae(null);
		BigInteger bb = GenUtil.getBigInteger(arr[2]);
		obj.setCount(bb.intValue());
		obj.setElTaxableValue((BigDecimal) arr[8]);
		obj.setElIGST((BigDecimal) arr[9]);
		obj.setElCGST((BigDecimal) arr[10]);
		obj.setElSGST((BigDecimal) arr[11]);
		obj.setElCess((BigDecimal) arr[12]);
		obj.setAvTaxableValue((BigDecimal) arr[3]);
		obj.setAvIGST((BigDecimal) arr[4]);
		obj.setAvCGST((BigDecimal) arr[5]);
		obj.setAvSGST((BigDecimal) arr[6]);
		obj.setAvCess((BigDecimal) arr[7]);
		return obj;

	}

	private String createmisMatchQueryString(List<String> selectedSgtins,
			String retPeriod) {
		StringBuffer condition = new StringBuffer();

		if (selectedSgtins != null && !selectedSgtins.isEmpty()) {
			condition.append(" \"A2_CGSTIN\" in :gstin");
		}

		if (retPeriod != null) {
			condition.append(" AND \"A2_DERIVED_RET_PERIOD\" = :returnPeriod");
		}

		String queryStr = "SELECT \"A2_TAX_PERIOD\"," + " \"A2_CGSTIN\", "
				+ "COUNT(\"A2_ID\") AS \"A2_ID\","
				+ "SUM(\"A2_TAXABLE_VALUE\") AS \"A2_TAXABLE_VALUE\","
				+ "SUM(\"A2_IGST_AMT\") AS \"A2_IGST_AMT\","
				+ "SUM(\"A2_CGST_AMT\") AS \"A2_CGST_AMT\","
				+ "SUM(\"A2_SGST_AMT\") AS \"A2_SGST_AMT\","
				+ "SUM(\"A2_CESS_AMT\") AS \"A2_CESS_AMT\","
				+ "SUM(\"E_TAXABLE_VALUE\") AS \"E_TAXABLE_VALUE\","
				+ "SUM(\"E_IGST\") AS \"E_IGST\","
				+ "SUM(\"E_CGST\") AS \"E_CGST\","
				+ "SUM(\"E_SGST\") AS \"E_SGST\","
				+ "SUM(\"E_CESS\") AS \"E_CESS\","
				+ "\"A2_SGSTIN\" , \"A2_ACTION_TAKEN\" "
				+ " FROM \"A2_PR_BIGTABLE\" WHERE " + condition.toString()
				+ " AND (\"RECON_STATUS\" = \'MISMATCH\' "
				+ "OR \"RECON_STATUS\" = \'MISMATCH\') "
				+ " AND \"A2_EXIST_FLAG\" = TRUE "
				+ "GROUP BY (\"A2_SGSTIN\"),(\"A2_CGSTIN\"),"
				+ "(\"A2_TAX_PERIOD\"), (\"A2_ACTION_TAKEN\")";

		return queryStr;
	}

	@Override
	public List<Anx2ReconResultsPotentialMatchSummaryResDto> reconResultpotentialMatchSummary(
			List<String> sgstins, String retPeriod) {
		String queryStr = createpotentialMatchQueryString(sgstins, retPeriod);
		Query q = entityManager.createNativeQuery(queryStr);

		if (retPeriod != null && !retPeriod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(retPeriod);
			q.setParameter("returnPeriod", derivedRetPeriod);

		}

		if (sgstins != null && !sgstins.isEmpty()) {
			q.setParameter("gstin", sgstins);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Anx2ReconResultsPotentialMatchSummaryResDto> retList = list
				.parallelStream().map(o -> potentialMatchconvert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx2ReconResultsPotentialMatchSummaryResDto potentialMatchconvert(
			Object[] arr) {
		Anx2ReconResultsPotentialMatchSummaryResDto obj = new Anx2ReconResultsPotentialMatchSummaryResDto();

		obj.setTaxPeriod((String) arr[0]);
		obj.setMyGSTIN((String) arr[1]);
		obj.setVendorGSTIN((String) arr[13]);
		obj.setVendorTradeNmae(null);
		BigInteger bb = GenUtil.getBigInteger(arr[2]);
		obj.setCount(bb.intValue());
		obj.setElTaxableValue((BigDecimal) arr[8]);
		obj.setElIGST((BigDecimal) arr[9]);
		obj.setElCGST((BigDecimal) arr[10]);
		obj.setElSGST((BigDecimal) arr[11]);
		obj.setElCess((BigDecimal) arr[12]);
		obj.setAvTaxableValue((BigDecimal) arr[3]);
		obj.setAvIGST((BigDecimal) arr[4]);
		obj.setAvCGST((BigDecimal) arr[5]);
		obj.setAvSGST((BigDecimal) arr[6]);
		obj.setAvCess((BigDecimal) arr[7]);
		return obj;

	}

	private String createpotentialMatchQueryString(List<String> selectedSgtins,
			String retPeriod) {
		StringBuffer condition = new StringBuffer();

		if (selectedSgtins != null && !selectedSgtins.isEmpty()) {
			condition.append(" \"A2_CGSTIN\" in :gstin");
		}

		if (retPeriod != null) {
			condition.append(" AND \"A2_DERIVED_RET_PERIOD\" = :returnPeriod");
		}

		String queryStr = "SELECT \"A2_TAX_PERIOD\"," + " \"A2_CGSTIN\", "
				+ "COUNT(\"A2_ID\") AS \"A2_ID\","
				+ "SUM(\"A2_TAXABLE_VALUE\") AS \"A2_TAXABLE_VALUE\","
				+ "SUM(\"A2_IGST_AMT\") AS \"A2_IGST_AMT\","
				+ "SUM(\"A2_CGST_AMT\") AS \"A2_CGST_AMT\","
				+ "SUM(\"A2_SGST_AMT\") AS \"A2_SGST_AMT\","
				+ "SUM(\"A2_CESS_AMT\") AS \"A2_CESS_AMT\","
				+ "SUM(\"E_TAXABLE_VALUE\") AS \"E_TAXABLE_VALUE\","
				+ "SUM(\"E_IGST\") AS \"E_IGST\","
				+ "SUM(\"E_CGST\") AS \"E_CGST\","
				+ "SUM(\"E_SGST\") AS \"E_SGST\","
				+ "SUM(\"E_CESS\") AS \"E_CESS\"," + "\"A2_SGSTIN\" "
				+ " FROM \"A2_PR_BIGTABLE\" WHERE " + condition.toString()
				+ " AND (\"RECON_STATUS\" = \'POTENTIAL_MATCH\') "
				+ " GROUP BY (\"A2_SGSTIN\"),(\"A2_CGSTIN\"),"
				+ "(\"A2_TAX_PERIOD\") ";

		return queryStr;
	}

}
