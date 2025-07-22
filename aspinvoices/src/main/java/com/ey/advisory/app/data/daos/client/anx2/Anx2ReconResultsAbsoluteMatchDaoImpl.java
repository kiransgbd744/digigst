package com.ey.advisory.app.data.daos.client.anx2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.anx2.Anx2ReconResultsAbsoluteMatchResDto;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Anand3.M
 *
 */

@Component("Anx2ReconResultsAbsoluteMatchDao")
public class Anx2ReconResultsAbsoluteMatchDaoImpl
		implements Anx2ReconResultsAbsoluteMatchDao {

	private static final String ABSOLUTE = "Absolute";
	private static final String MISMATCH = "Mismatch";
	private static final String POTENTIAL = "Potential";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx2ReconResultsAbsoluteMatchResDto> fetchAbsoluteDetails(
			List<String> selectedSgtins, String retPeriod) {
		String queryStr = createQueryString(selectedSgtins, retPeriod,
				ABSOLUTE);
		return fetchTheQueryDetails(selectedSgtins, retPeriod, queryStr);
	}

	@Override
	public List<Anx2ReconResultsAbsoluteMatchResDto> fetchMismatchDetails(
			List<String> selectedSgtins, String retPeriod) {
		String queryStr = createQueryString(selectedSgtins, retPeriod,
				MISMATCH);
		return fetchTheQueryDetails(selectedSgtins, retPeriod, queryStr);

	}

	@Override
	public List<Anx2ReconResultsAbsoluteMatchResDto> fetchPotentialMatchDetails(
			List<String> selectedSgtins, String retPeriod) {
		String queryStr = createQueryString(selectedSgtins, retPeriod,
				POTENTIAL);
		return fetchTheQueryDetails(selectedSgtins, retPeriod, queryStr);
	}

	private String createQueryString(List<String> selectedSgtins,
			String retPeriod, String fetchType) {
		StringBuffer condition = new StringBuffer();

		if (fetchType.equals(ABSOLUTE)) {
			if (retPeriod != null) {
				condition
						.append(" AND  \"A2_DERIVED_RET_PERIOD\" = :retPeriod");
			}

			if (selectedSgtins != null && !selectedSgtins.isEmpty()) {
				condition.append(" AND \"A2_CGSTIN\" in :gstin");
			}
		} else if (fetchType.equals(MISMATCH) || fetchType.equals(POTENTIAL)) {
			if (retPeriod != null) {
				condition.append(
						" AND  \"T1\".\"A2_DERIVED_RET_PERIOD\" = :retPeriod");
			}

			if (selectedSgtins != null && !selectedSgtins.isEmpty()) {
				condition.append(" AND \"T1\".\"A2_CGSTIN\" in :gstin");
			}
		}

		String queryStr = null;
		if (fetchType.equals(ABSOLUTE)) {
			queryStr = "SELECT \"ID\",\"A2_TAX_PERIOD\", \"A2_CGSTIN\", \"A2_SGSTIN\", "
					+ "\"A2_TAXABLE_VALUE\", \"A2_IGST_AMT\", \"A2_CGST_AMT\", \"A2_SGST_AMT\", "
					+ "\"A2_CESS_AMT\",\"A2_INVOICE_KEY\", \"PR_TAXABLE_VALUE\", \"PR_IGST_AMT\", \"PR_CGST_AMT\", \"PR_SGST_AMT\", "
					+ "\"PR_CESS_AMT\",\"PR_INVOICE_KEY\" "
					+ " FROM \"A2_PR_BIGTABLE\" "
					+ "WHERE \"PR_EXIST_FLAG\" = TRUE AND \"A2_EXIST_FLAG\" = TRUE "
					+ condition.toString();
		} else if (fetchType.equals(MISMATCH)) {
			queryStr = "SELECT \"T1\".\"ID\",\"T1\".\"A2_TAX_PERIOD\",\"T1\".\"A2_CGSTIN\",\"T1\".\"A2_SGSTIN\",\"T1\".\"A2_TAXABLE_VALUE\","
					+ "\"T1\".\"A2_IGST_AMT\",\"T1\".\"A2_CGST_AMT\", \"T1\".\"A2_SGST_AMT\",\"T1\".\"A2_CESS_AMT\","
					+ "\"T1\".\"A2_INVOICE_KEY\",\"T2\".\"PR_TAXABLE_VALUE\",\"T2\".\"PR_IGST_AMT\",\"T2\".\"PR_CGST_AMT\",\"T2\".\"PR_SGST_AMT\",\"T2\".\"PR_CESS_AMT\",\"T2\".\"PR_INVOICE_KEY\",\"T1\".\"REASON_MISSMATCH\" "
					+ " FROM \"A2_PR_BIGTABLE\" T1 INNER JOIN \"A2_PR_BIGTABLE\" T2 ON \"T1\".\"A2_ID\" = \"T2\".\"A2_LINK_ID\" "
					+ " WHERE  (\"T1\".\"RECON_STATUS\" ='MISMATCH' AND \"T2\".\"RECON_STATUS\" = 'MISMATCH') "
					+ condition.toString();
		} else if (fetchType.equals(POTENTIAL)) {
			queryStr = "SELECT \"T1\".\"ID\",\"T1\".\"A2_TAX_PERIOD\",\"T1\".\"A2_CGSTIN\",\"T1\".\"A2_SGSTIN\",\"T1\".\"A2_TAXABLE_VALUE\","
					+ "\"T1\".\"A2_IGST_AMT\",\"T1\".\"A2_CGST_AMT\",\"T1\".\"A2_SGST_AMT\","
					+ "\"T1\".\"A2_CESS_AMT\",\"T1\".\"A2_INVOICE_KEY\",\"T2\".\"PR_TAXABLE_VALUE\",\"T2\".\"PR_IGST_AMT\",\"T2\".\"PR_CGST_AMT\",\"T2\".\"PR_SGST_AMT\",\"T2\".\"PR_CESS_AMT\",\"T2\".\"PR_INVOICE_KEY\" "
					+ " FROM \"A2_PR_BIGTABLE\" T1 INNER JOIN \"A2_PR_BIGTABLE\" T2 ON \"T1\".\"A2_ID\" = \"T2\".\"A2_LINK_ID\" "
					+ " WHERE (\"T1\".\"RECON_STATUS\" ='POTENTIAL_MATCH' AND \"T2\".\"RECON_STATUS\" = 'POTENTIAL_MATCH')  "
					+ condition.toString();
		}

		return queryStr;
	}

	private List<Anx2ReconResultsAbsoluteMatchResDto> fetchTheQueryDetails(
			List<String> selectedSgtins, String retPeriod, String queryStr) {

		Query q = entityManager.createNativeQuery(queryStr);

		if (retPeriod != null) {
			q.setParameter("retPeriod", Integer.parseInt(retPeriod));
		}

		if (selectedSgtins != null && !selectedSgtins.isEmpty()) {
			q.setParameter("gstin", selectedSgtins);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Anx2ReconResultsAbsoluteMatchResDto> retList = list
				.parallelStream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx2ReconResultsAbsoluteMatchResDto convert(Object[] arr) {
		Anx2ReconResultsAbsoluteMatchResDto obj = new Anx2ReconResultsAbsoluteMatchResDto();

		BigInteger bb1 = GenUtil.getBigInteger(arr[0]);
		obj.setId(bb1.longValue());
		obj.setTaxPeriod((String) arr[1]);
		obj.setMyGSTIN((String) arr[2]);
		obj.setVendorGSTIN((String) arr[3]);
		obj.setAnx2taxableValue((BigDecimal) arr[4]);
		obj.setAnx2igst((BigDecimal) arr[5]);
		obj.setAnx2cgst((BigDecimal) arr[6]);
		obj.setAnx2sgst((BigDecimal) arr[7]);
		obj.setAnx2cess((BigDecimal) arr[8]);
		String invoiceKey = (String) arr[9];
		if (invoiceKey == null || invoiceKey.contains("?")) {
			invoiceKey = "";
		}
		obj.setTwoAInvoiceKey(invoiceKey);
		obj.setPrtaxableValue((BigDecimal) arr[10]);
		obj.setPrigst((BigDecimal) arr[11]);
		obj.setPrcgst((BigDecimal) arr[12]);
		obj.setPrsgst((BigDecimal) arr[13]);
		obj.setPrcess((BigDecimal) arr[14]);
		String prInvoiceKey = (String) arr[15];
		if (prInvoiceKey == null || prInvoiceKey.contains("?")) {
			prInvoiceKey = "";
		}
		obj.setPrInvoiceKey(prInvoiceKey);

		return obj;

	}

}
