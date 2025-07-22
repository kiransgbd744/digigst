/**
 * 
 */
package com.ey.advisory.app.data.daos.client.anx2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.VendorANX2SummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.VendorSummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author BalaKrishna S
 *
 */
@Component("VendorANX2SummaryDaoImpl")
public class VendorANX2SummaryDaoImpl implements VendorANX2SummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorANX2SummaryDaoImpl.class);

	@Override
	public List<VendorANX2SummaryRespDto> loadVendorANX2Summary(
			VendorSummaryReqDto criteria) {

		String taxPeriod = criteria.getTaxPeriod();
		List<String> vendorGstin = criteria.getVendorGstin();
		List<String> vendorPan = criteria.getVendorPan();
		List<String> data = criteria.getData();
		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();
		int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);

		String gstin = null;
		List<String> gstinList = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}
		StringBuilder build = new StringBuilder();

		if (data != null && data.size() > 0) {
			for (String dataFilter : data) {
				if ("All".equalsIgnoreCase(dataFilter)) {

					build.append(" AND DERIVED_RET_PERIOD = :derivedRetPeriod");

				}
				if ("Current Period".equalsIgnoreCase(dataFilter)) {

					build.append(" AND DERIVED_RET_PERIOD = :derivedRetPeriod");

				}
			}
		}
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND CGSTIN IN :gstinList");
			}
		}
		if (vendorPan != null && vendorPan.size() > 0) {
			build.append(" AND SGSTIN_PAN IN :vendorPan");
		}
		if (vendorGstin != null && vendorGstin.size() > 0) {
			build.append(" AND SGSTIN IN :vendorGstin");
		}

		String buildQuery = build.toString();
		String queryStr = createQueryString(buildQuery);
		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (vendorPan != null && vendorPan.size() > 0) {
				q.setParameter("vendorPan", vendorPan);
			}
			if (vendorGstin != null && vendorGstin.size() > 0) {
				q.setParameter("vendorGstin", vendorGstin);
			}
			if (derivedRetPeriod != 0) {
				q.setParameter("derivedRetPeriod", derivedRetPeriod);
			}
			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}

			LOGGER.debug("Executing Query And Storing to ResultSet ");
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();

			LOGGER.debug("Converting Query And converting to List BEGIN");
			List<VendorANX2SummaryRespDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Converting Query And converting to List END");
			return retList;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Converting ResultSet to List in Vendor "
					+ "anx2 Summary Query Execution ");
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private String createQueryString(String buildQuery) {
		LOGGER.debug("Executing Query For Vendor PR Summary BEGIN");
		String query = "select A2_COUNT,IFNULL(SGSTIN_PAN,'-'),"
				+ "IFNULL(SGSTIN,'-'),IFNULL(A2_DOC_TYPE,'-'),"
				+ "IFNULL(SUPPLIER_NAME,'-'), IFNULL(A2_INVOICE_VALUE,0) "
				+ "AS INVOICE,IFNULL(TABLE_SECTION,'-'),"
				+ "IFNULL(A2_TAXABLE_VALUE,0) AS TAXABLE,IFNULL(A2_IGST_AMT,0) "
				+ "AS IGST, IFNULL(A2_CGST_AMT,0) AS CGST,"
				+ "IFNULL(A2_SGST_AMT,0) AS SGST, IFNULL(A2_CESS_AMT,0) AS CESS"
				+ " FROM ((SELECT COUNT(ID) AS A2_COUNT,"
				+ "SGSTIN_PAN,SGSTIN,'-' AS SUPPLIER_NAME,INV_TYPE AS "
				+ "A2_DOC_TYPE,SUM(IFNULL(SUPPLIER_INV_VAL,0)) "
				+ "AS A2_INVOICE_VALUE,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)) AS A2_TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS A2_TOTAL_TAX,"
				+ "SUM(IFNULL(IGST_AMT,0)) AS A2_IGST_AMT,"
				+ "SUM(IFNULL(CGST_AMT,0)) AS A2_CGST_AMT,"
				+ "SUM(IFNULL(SGST_AMT,0)) AS A2_SGST_AMT,"
				+ "SUM(IFNULL(CESS_AMT,0)) AS A2_CESS_AMT,"
				+ "'B2B' AS TABLE_SECTION FROM GETANX2_B2B_HEADER  "
				+ "WHERE IS_DELETE = FALSE " + buildQuery
				+ " GROUP BY INV_TYPE,SGSTIN_PAN,SGSTIN)"
				+ "UNION ALL(SELECT COUNT(ID) AS A2_COUNT,SGSTIN_PAN,"
				+ "SGSTIN,'-' AS SUPPLIER_NAME,INV_TYPE AS A2_DOC_TYPE,"
				+ "SUM(IFNULL(SUPPLIER_INV_VAL,0)) AS A2_INVOICE_VALUE,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)) AS A2_TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS A2_TOTAL_TAX,"
				+ "SUM(IFNULL(IGST_AMT,0)) AS A2_IGST_AMT,"
				+ "SUM(IFNULL(CGST_AMT,0)) AS A2_CGST_AMT,"
				+ "SUM(IFNULL(SGST_AMT,0)) "
				+ "AS A2_SGST_AMT,SUM(IFNULL(CESS_AMT,0)) AS A2_CESS_AMT,"
				+ "'DE' AS TABLE_SECTION FROM GETANX2_DE_HEADER WHERE "
				+ "IS_DELETE = FALSE " + buildQuery
				+ " GROUP BY INV_TYPE,SGSTIN_PAN,SGSTIN) "
				+ "UNION ALL (SELECT COUNT(ID) AS A2_COUNT,SGSTIN_PAN,"
				+ "SGSTIN,'-' AS SUPPLIER_NAME,INV_TYPE AS A2_DOC_TYPE,"
				+ "SUM(IFNULL(SUPPLIER_INV_VAL,0)) AS A2_INVOICE_VALUE,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)) AS A2_TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS A2_TOTAL_TAX,"
				+ "SUM(IFNULL(IGST_AMT,0)) AS A2_IGST_AMT,"
				+ "SUM(IFNULL(CGST_AMT,0)) AS A2_CGST_AMT,"
				+ "SUM(IFNULL(SGST_AMT,0)) AS A2_SGST_AMT,"
				+ "SUM(IFNULL(CESS_AMT,0)) AS A2_CESS_AMT,"
				+ "'SEZWP' AS TABLE_SECTION FROM GETANX2_SEZWP_HEADER "
				+ "WHERE IS_DELETE = FALSE " + buildQuery
				+ " GROUP BY INV_TYPE,SGSTIN_PAN,SGSTIN)"
				+ "UNION ALL( SELECT COUNT(ID) AS A2_COUNT,SGSTIN_PAN,SGSTIN,"
				+ "'-' AS SUPPLIER_NAME,INV_TYPE AS A2_DOC_TYPE,"
				+ "SUM(IFNULL(SUPPLIER_INV_VAL,0)) AS A2_INVOICE_VALUE,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)) AS A2_TAXABLE_VALUE,"
				+ "'0' AS A2_TOTAL_TAX,"
				+ "'0' AS A2_IGST_AMT,'0' AS A2_CGST_AMT, '0' AS A2_SGST_AMT,"
				+ "'0' AS A2_CESS_AMT,'SEZWOP' AS TABLE_SECTION FROM "
				+ "GETANX2_SEZWOP_HEADER WHERE IS_DELETE = FALSE " + buildQuery
				+ " GROUP BY INV_TYPE,SGSTIN_PAN,SGSTIN))";
		LOGGER.debug("Executing Query For Vendor PR Summary END");
		return query;
	}

	private VendorANX2SummaryRespDto convert(Object[] arr) {

		VendorANX2SummaryRespDto obj = new VendorANX2SummaryRespDto();

		obj.setCount((GenUtil.getBigInteger(arr[0])).intValue());
		obj.setVendorPAN((String) arr[1]);
		obj.setGstin((String) arr[2]);
		obj.setDocType((String) arr[3]);
		obj.setVendorName((String) arr[4]);
		obj.setInvValue((BigDecimal) arr[5]);
		obj.setTableType((String) arr[6]);
		obj.setTaxableValue((BigDecimal) arr[7]);
		obj.setTpIGST((BigDecimal) arr[8]);
		obj.setTpCGST((BigDecimal) arr[9]);
		obj.setTpSGST((BigDecimal) arr[10]);
		obj.setTpCess((BigDecimal) arr[11]);
		obj.setLevel("L4");
		return obj;
	}

}
