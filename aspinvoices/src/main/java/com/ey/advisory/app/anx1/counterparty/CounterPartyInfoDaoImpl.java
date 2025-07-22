package com.ey.advisory.app.anx1.counterparty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */

@Slf4j
@Component("CounterPartyInfoDaoImpl")
public class CounterPartyInfoDaoImpl implements CounterPartyInfoDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<CounterPartyInfoDto> getAllCounterPartyInfo(String taxPeriod,
			List<String> sgstinsList, List<String> tableSection,
			List<String> docTypes) {

		if (LOGGER.isDebugEnabled()) {
			String str = sgstinsList.stream().collect(Collectors.joining(", "));
			String msg = String
					.format("Getting the CounterPartyInfo for GSTINs in DAO: [%s], "
							+ "TaxPeriod: %s", str, taxPeriod);
			LOGGER.debug(msg);
		}

		String queryString = createQueryString(sgstinsList, taxPeriod,
				tableSection, docTypes);

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for CounterParty : %s",
					queryString);
			LOGGER.debug(str);
		}

		Query q = entityManager.createNativeQuery(queryString);

		if (taxPeriod != null && !taxPeriod.isEmpty()) {
			q.setParameter("taxPeriod", taxPeriod);
		}

		if (!sgstinsList.isEmpty() && sgstinsList.size() > 0) {
			q.setParameter("sgstin", sgstinsList);
		}

		if (docTypes != null && (!docTypes.isEmpty() && docTypes.size() > 0)) {
			q.setParameter("docTypes", docTypes);
		}
		if (tableSection != null
				&& (!tableSection.isEmpty() && tableSection.size() > 0)) {
			q.setParameter("tableSection", tableSection);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<CounterPartyInfoDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String str = "Before returning the response. Obtained the "
					+ "list of CounterPartyInfoDto objs";
			LOGGER.debug(str);
		}

		return retList;
	}

	private static String createQueryString(List<String> sgstin,
			String taxPeriod, List<String> tableSection,
			List<String> docTypes) {

		StringBuilder condition1 = new StringBuilder();
		StringBuilder condition2 = new StringBuilder();

		queryAppending(sgstin, taxPeriod, tableSection, docTypes, condition1,
				condition2);

		String queryStr = "SELECT    gstin " + "        , TABLE_SECTION "
				+ "        , action " + "        , COUNT(CNT) "
				+ "        , SUM(CASE WHEN DOC_TYPE='CR' THEN -TAXABLE_VALUE "
				+ "                   ELSE TAXABLE_VALUE "
				+ "                   END )AS TAXABLE_VALUE "
				+ "                    "
				+ "         , SUM(CASE WHEN DOC_TYPE='CR' THEN -IGST_AMT "
				+ "                   ELSE IGST_AMT "
				+ "                   END )AS IGST_AMT " + " "
				+ "         , SUM(CASE WHEN DOC_TYPE='CR' THEN -CGST_AMT "
				+ "                   ELSE CGST_AMT "
				+ "                   END )AS CGST_AMT "
				+ "                    "
				+ "         , SUM(CASE WHEN DOC_TYPE='CR' THEN -SGST_AMT "
				+ "                   ELSE SGST_AMT "
				+ "                   END )AS SGST_AMT "
				+ "                    "
				+ "        , SUM(CASE WHEN DOC_TYPE='CR' THEN -CESS_AMT "
				+ "                   ELSE CESS_AMT "
				+ "                   END )AS CESS_AMT "
				+ "                    "
				+ "        , SUM(CASE WHEN DOC_TYPE='CR' THEN -DOC_AMT "
				+ "                   ELSE DOC_AMT "
				+ "                   END )AS DOC_AMT " + "         "
				+ "        , SUM(CASE WHEN DOC_TYPE='CR' THEN -TAX_PAYABLE "
				+ "                   ELSE TAX_PAYABLE "
				+ "                   END )AS TAX_PAYABLE " + "      FROM "
				+ "( " + "SELECT gstin, "
				+ "       '3B'                                             AS TABLE_SECTION, "
				+ "       action, "
				+ "       Count (*)                                        AS CNT, "
				+ "       SUM(Ifnull(taxable_value, 0))                    AS TAXABLE_VALUE, "
				+ "       SUM(Ifnull(igst_amt, 0))                         AS IGST_AMT, "
				+ "       SUM(Ifnull(cgst_amt, 0))                         AS CGST_AMT, "
				+ "       SUM(Ifnull(sgst_amt, 0))                         AS SGST_AMT, "
				+ "       SUM(Ifnull(cess_amt, 0))                         AS CESS_AMT, "
				+ "       SUM(Ifnull(doc_amt, 0))                          AS DOC_AMT, "
				+ " " + "       SUM(Ifnull(igst_amt, 0) + Ifnull(cgst_amt, 0) "
				+ "           + Ifnull(sgst_amt, 0) + Ifnull(cess_amt, 0)) AS TAX_PAYABLE "
				+ "       , DOC_TYPE "
				+ "FROM   getanx1_b2b_header " + "WHERE "
				+ condition1.toString() + "       AND is_delete = FALSE "
				+ "GROUP  BY gstin, " + "          action,DOC_TYPE " + "UNION "
				+ "SELECT gstin, "
				+ "       '3F'                          AS TABLE_SECTION, "
				+ "       action, "
				+ "       Count (*)                     AS CNT, "
				+ "       SUM(Ifnull(taxable_value, 0)) AS TAXABLE_VALUE, "
				+ "       0                             AS IGST_AMT, "
				+ "       0                             AS CGST_AMT, "
				+ "       0                             AS SGST_AMT, "
				+ "       0                             AS CESS_AMT, "
				+ "       SUM(Ifnull(doc_amt, 0))       AS DOC_AMT, "
				+ "       0                             AS TAX_PAYABLE "
				+ "       , DOC_TYPE "
				+ "FROM   getanx1_sezwop_header " + "WHERE "
				+ condition1.toString() + "     AND is_delete = FALSE "
				+ "GROUP  BY gstin, " + "          action, DOC_TYPE " + "UNION "
				+ "SELECT gstin, "
				+ "       '3E'                                           AS TABLE_SECTION, "
				+ "       action, "
				+ "       Count (*)                                      AS CNT, "
				+ "       SUM(Ifnull(taxable_value, 0))                  AS TAXABLE_VALUE, "
				+ "       SUM(Ifnull(igst_amt, 0))                       AS IGST_AMT, "
				+ "       0                                              AS CGST_AMT, "
				+ "       0                                              AS SGST_AMT, "
				+ "       SUM(Ifnull(cess_amt, 0))                       AS CESS_AMT, "
				+ "       SUM(Ifnull(doc_amt, 0))                        AS DOC_AMT, "
				+ "       SUM(Ifnull(igst_amt, 0) + Ifnull(cess_amt, 0)) AS TAX_PAYABLE "
				+ "       , DOC_TYPE "
				+ "FROM   getanx1_sezwp_header " + "WHERE "
				+ condition1.toString() + "   AND is_delete = FALSE "
				+ "GROUP  BY gstin, " + "          action, DOC_TYPE " + "UNION "
				+ "SELECT gstin, "
				+ "       '3G'                                             AS TABLE_SECTION, "
				+ "       action, "
				+ "       Count (*)                                        AS CNT, "
				+ "       SUM(Ifnull(taxable_value, 0))                    AS TAXABLE_VALUE, "
				+ "       SUM(Ifnull(igst_amt, 0))                         AS IGST_AMT, "
				+ "       SUM(Ifnull(cgst_amt, 0))                         AS CGST_AMT, "
				+ "       SUM(Ifnull(sgst_amt, 0))                         AS SGST_AMT, "
				+ "       SUM(Ifnull(cess_amt, 0))                         AS CESS_AMT, "
				+ "       SUM(Ifnull(doc_amt, 0))                          AS DOC_AMT, "
				+ "       SUM(Ifnull(igst_amt, 0) + Ifnull(cgst_amt, 0) "
				+ "           + Ifnull(sgst_amt, 0) + Ifnull(cess_amt, 0)) AS TAX_PAYABLE "
				+ "       , DOC_TYPE " + "FROM   getanx1_de_header "
				+ "WHERE " + condition1.toString()
				+ "      AND is_delete = FALSE " + "GROUP  BY gstin, "
				+ "          action, DOC_TYPE " + " " + "UNION "
				+ "SELECT supplier_gstin                       AS GSTIN, "
				+ "       an_table_section                     AS TABLE_SECTION, "
				+ "       'NS'                                 AS ACTION, "
				+ "       Count (*)                            AS CNT, "
				+ "       SUM(Ifnull(taxable_value, 0))        AS TAXABLE_VALUE, "
				+ "       SUM(Ifnull(igst_amt, 0))             AS IGST_AMT, "
				+ "       SUM(Ifnull(cgst_amt, 0))             AS CGST_AMT, "
				+ "       SUM(Ifnull(sgst_amt, 0))             AS SGST_AMT, "
				+ "       SUM(Ifnull(cess_amt_specific, 0) "
				+ "           + Ifnull(cess_amt_advalorem, 0)) AS CESS_AMT, "
				+ "       SUM(Ifnull(doc_amt, 0))              AS DOC_AMT, "
				+ "       SUM(Ifnull(igst_amt, 0) + Ifnull(cgst_amt, 0) "
				+ "           + Ifnull(sgst_amt, 0) "
				+ "           + Ifnull(cess_amt_specific, 0) "
				+ "           + Ifnull(cess_amt_advalorem, 0)) AS TAX_PAYABLE "
				+ "       , DOC_TYPE "
				+ "FROM   anx_outward_doc_header " + "WHERE  "
				+ "      is_processed = TRUE "
				+ "       AND is_saved_to_gstn = FALSE " + condition2.toString()
				+ "       AND is_delete = FALSE " + "GROUP  BY supplier_gstin, "
				+ "          an_table_section " + "          , DOC_TYPE "
				+ "UNION "
				+ "SELECT supplier_gstin                       AS GSTIN, "
				+ "       an_table_section                     AS TABLE_SECTION, "
				+ "       'S'                                  AS ACTION, "
				+ "       Count (*)                            AS CNT, "
				+ "       SUM(Ifnull(taxable_value, 0))        AS TAXABLE_VALUE, "
				+ "       SUM(Ifnull(igst_amt, 0))             AS IGST_AMT, "
				+ "       SUM(Ifnull(cgst_amt, 0))             AS CGST_AMT, "
				+ "       SUM(Ifnull(sgst_amt, 0))             AS SGST_AMT, "
				+ "       SUM(Ifnull(cess_amt_specific, 0) "
				+ "           + Ifnull(cess_amt_advalorem, 0)) AS CESS_AMT, "
				+ "       SUM(Ifnull(doc_amt, 0))              AS DOC_AMT, "
				+ "       SUM(Ifnull(igst_amt, 0) + Ifnull(cgst_amt, 0) "
				+ "           + Ifnull(sgst_amt, 0) "
				+ "           + Ifnull(cess_amt_specific, 0) "
				+ "           + Ifnull(cess_amt_advalorem, 0)) AS TAX_PAYABLE "
				+ "       , DOC_TYPE "
				+ "FROM   anx_outward_doc_header " + "WHERE   "
				+ "       is_processed = TRUE "
				+ "       AND is_saved_to_gstn = TRUE " + condition2.toString()
				+ "       AND is_delete = FALSE " + "GROUP  BY supplier_gstin, "
				+ "          an_table_section " + "          , DOC_TYPE "
				+ "          )A " + "  GROUP BY  gstin "
				+ "        , TABLE_SECTION " + "        , action";
		return queryStr;
	}

	private static void queryAppending(List<String> sgstin, String taxPeriod,
			List<String> tableSection, List<String> docTypes,
			StringBuilder condition1, StringBuilder condition2) {
		if (sgstin != null) {
			condition1.append(" GSTIN in (:sgstin) AND ");
			condition2.append(" AND SUPPLIER_GSTIN in (:sgstin)");
		}

		if (taxPeriod != null && !taxPeriod.equals("")) {
			condition1.append(" TAX_PERIOD = :taxPeriod ");
			condition2.append(" AND RETURN_PERIOD = :taxPeriod ");
		}

		if (docTypes != null && (!docTypes.isEmpty() && !docTypes.equals(""))) {
			condition1.append(" AND DOC_TYPE in (:docTypes)");
			condition2.append(" AND DOC_TYPE in (:docTypes)");
		}

		if (tableSection != null
				&& (!tableSection.isEmpty() && !tableSection.equals(""))) {
			condition2.append(" AND  AN_TABLE_SECTION in (:tableSection)");
		} else {
			condition2.append(" AND AN_TABLE_SECTION in ('3B','3E','3F','3G')");
		}
	}

	private CounterPartyInfoDto convert(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " CounterPartyInfoDto object";
			LOGGER.debug(str);
		}
		CounterPartyInfoDto obj = new CounterPartyInfoDto();
		obj.setSgstin((String) arr[0]);
		obj.setTableSection((String) arr[1]);
		obj.setAction((String) arr[2]);
		BigInteger bi = GenUtil.getBigInteger(arr[3]);
		Integer count = bi.intValue();
		obj.setCnt(count);
		obj.setTaxableValue((BigDecimal) arr[4]);
		obj.setIgstAmt((BigDecimal) arr[5]);
		obj.setCgstAmt((BigDecimal) arr[6]);
		obj.setSgstAmt((BigDecimal) arr[7]);
		obj.setCessAmt((BigDecimal) arr[8]);
		obj.setDocAmt((BigDecimal) arr[9]);
		obj.setTaxPayable((BigDecimal) arr[10]);

		return obj;
	}

}
