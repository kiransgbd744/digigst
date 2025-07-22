package com.ey.advisory.app.data.daos.client.anx2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.VendorSummaryRespDto;
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
@Component("VendorPRSummaryDaoImpl")
public class VendorPRSummaryDaoImpl implements VendorPRSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorPRSummaryDaoImpl.class);

	@Override
	public List<VendorSummaryRespDto> loadVendorPRSummary(
			VendorSummaryReqDto criteria) {

		String taxPeriod = criteria.getTaxPeriod();
		List<String> vendorGstin = criteria.getVendorGstin();
		List<String> vendorPan = criteria.getVendorPan();
		List<String> data = criteria.getData();
		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();
		int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);

		String ProfitCenter = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String purchase = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String gstin = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purcList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;
		List<String> gstinList = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.SO)
									.size() > 0) {
						salesList = dataSecAttrs.get(OnboardingConstant.SO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purcList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					if (dataSecAttrs.get(OnboardingConstant.DC) != null
							&& dataSecAttrs.get(OnboardingConstant.DC)
									.size() > 0) {
						distList = dataSecAttrs.get(OnboardingConstant.DC);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& dataSecAttrs.get(OnboardingConstant.UD1)
									.size() > 0) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& dataSecAttrs.get(OnboardingConstant.UD2)
									.size() > 0) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& dataSecAttrs.get(OnboardingConstant.UD3)
									.size() > 0) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& dataSecAttrs.get(OnboardingConstant.UD4)
									.size() > 0) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& dataSecAttrs.get(OnboardingConstant.UD5)
									.size() > 0) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& dataSecAttrs.get(OnboardingConstant.UD6)
									.size() > 0) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
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
				build.append(" AND CUST_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORGANIZATION IN :salesList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND PURCHASE_ORGANIZATION IN :purcList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USERACCESS6 IN :ud6List");
			}
		}
		/*
		 * if (taxPeriod != null) {
		 * 
		 * build.append(" AND DERIVED_RET_PERIOD = :derivedRetPeriod"); }
		 */
		if (vendorPan != null && vendorPan.size() > 0) {
			build.append(" AND DERIVED_CGSTIN_PAN IN :vendorPan");
		}
		if (vendorGstin != null && vendorGstin.size() > 0) {
			build.append(" AND SUPPLIER_GSTIN IN :vendorGstin");
		}

		String buildQuery = build.toString();
		String queryStr = createQueryString(buildQuery);
		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
					q.setParameter("pcList", pcList);
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && !plantList.isEmpty()
						&& plantList.size() > 0) {
					q.setParameter("plantList", plantList);
				}
			}
			if (sales != null && !sales.isEmpty()) {
				if (salesList != null && !salesList.isEmpty()
						&& salesList.size() > 0) {
					q.setParameter("salesList", salesList);
				}
			}
			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && !divisionList.isEmpty()
						&& divisionList.size() > 0) {
					q.setParameter("divisionList", divisionList);
				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && !locationList.isEmpty()
						&& locationList.size() > 0) {
					q.setParameter("locationList", locationList);
				}
			}
			if (purchase != null && !purchase.isEmpty()) {
				if (purcList != null && !purcList.isEmpty()
						&& purcList.size() > 0) {
					q.setParameter("purcList", purcList);
				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && !distList.isEmpty()
						&& distList.size() > 0) {
					q.setParameter("distList", distList);
				}
			}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && !ud1List.isEmpty()
						&& ud1List.size() > 0) {
					q.setParameter("ud1List", ud1List);
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && !ud2List.isEmpty()
						&& ud2List.size() > 0) {
					q.setParameter("ud2List", ud2List);
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && !ud3List.isEmpty()
						&& ud3List.size() > 0) {
					q.setParameter("ud3List", ud3List);
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && !ud4List.isEmpty()
						&& ud4List.size() > 0) {
					q.setParameter("ud4List", ud4List);
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && !ud5List.isEmpty()
						&& ud5List.size() > 0) {
					q.setParameter("ud5List", ud5List);
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && !ud6List.isEmpty()
						&& ud6List.size() > 0) {
					q.setParameter("ud6List", ud6List);
				}
			}
			if (derivedRetPeriod != 0) {
				q.setParameter("derivedRetPeriod", derivedRetPeriod);
			}

			LOGGER.debug("Executing Query And Storing to ResultSet ");
			List<Object[]> list = q.getResultList();
			
			LOGGER.debug("Converting Query And converting to List BEGIN");
			List<VendorSummaryRespDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Converting Query And converting to List END");
			return retList;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Converting ResultSet to List in Vendor "
					+ "PR Summary Query Execution ");
			throw new AppException("Unexpected error in query execution.");
		}
	}

	private String createQueryString(String buildQuery) {
		LOGGER.debug("Executing Query For Vendor PR Summary BEGIN");
		String query = "SELECT " + "CASE WHEN SUPPLIER_GSTIN IS NOT NULL THEN "
				+ "SUBSTRING(SUPPLIER_GSTIN, 3, 10) ELSE '-' END "
				+ "AS DERIVED_SGSTIN_PAN, "
				+ "IFNULL(SUPPLIER_GSTIN, '-') AS SUPPLIER_GSTIN, "
				+ "IFNULL(AN_TAX_DOC_TYPE, '-') AS TABLE_TYPE, "
				+ "IFNULL(DOC_TYPE, '-') AS DOC_TYPE, COUNT(ID) AS COUNT, "
				+ "SUM(IFNULL(DOC_AMT, 0)) AS INVOICE, "
				+ "SUM(IFNULL(TAXABLE_VALUE, 0)) AS TAXABLE_VALUE, "
				+ "SUM(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT, 0) + "
				+ "IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT, 0)) AS TOTAL_TAX, "
				+ "SUM(IFNULL(IGST_AMT, 0)) AS TP_IGST, "
				+ "SUM(IFNULL(CGST_AMT, 0)) AS TP_CGST, "
				+ "SUM(IFNULL(SGST_AMT, 0)) AS TP_SGST, "
				+ "SUM(IFNULL(CESS_AMT, 0)) AS TP_CESS, "
				+ "SUM(IFNULL(AVAILABLE_IGST, 0) + IFNULL(AVAILABLE_CGST, 0) + "
				+ "IFNULL(AVAILABLE_SGST, 0) + IFNULL(AVAILABLE_CESS, 0)) "
				+ "AS  TOTAL_CREDIT_ELIGIBLE, "
				+ "SUM(IFNULL(AVAILABLE_IGST, 0)) AS CE_IGST, "
				+ "SUM(IFNULL(AVAILABLE_CGST, 0)) AS CE_CGST, "
				+ "SUM(IFNULL(AVAILABLE_SGST,0)) AS CE_SGST, "
				+ "SUM(IFNULL(AVAILABLE_CESS, 0)) AS CE_CESS "
				+ "FROM ANX_INWARD_DOC_HEADER "
				+ "WHERE IS_PROCESSED = TRUE AND (AN_TAX_DOC_TYPE "
				+ "IN ('B2B','DXP','SEZWP','SEZWOP'))  " + buildQuery
				+ " GROUP BY " + "CASE WHEN SUPPLIER_GSTIN IS NOT NULL THEN "
				+ "SUBSTRING(SUPPLIER_GSTIN, 3, 10) ELSE '-' END, "
				+ "IFNULL(SUPPLIER_GSTIN, '-'), "
				+ "IFNULL(AN_TAX_DOC_TYPE, '-'), " + "IFNULL(DOC_TYPE, '-')";
		
		LOGGER.debug("Executing Query For Vendor PR Summary END");
		return query;
	}

	private VendorSummaryRespDto convert(Object[] arr) {

		VendorSummaryRespDto obj = new VendorSummaryRespDto();

		obj.setVendorPAN((String) arr[0]);
		obj.setGstin((String) arr[1]);
		obj.setTableType((String) arr[2]);
		obj.setDocType((String) arr[3]);
		obj.setCount((GenUtil.getBigInteger(arr[4])).intValue());
		obj.setInvValue((BigDecimal) arr[5]);
		obj.setTaxableValue((BigDecimal) arr[6]);
		obj.setTotalTaxPayable((BigDecimal) arr[7]);
		obj.setTpIGST((BigDecimal) arr[8]);
		obj.setTpCGST((BigDecimal) arr[9]);
		obj.setTpSGST((BigDecimal) arr[10]);
		obj.setTpCess((BigDecimal) arr[11]);
		obj.setTotalCreditEligible((BigDecimal) arr[12]);
		obj.setCeIGST((BigDecimal) arr[13]);
		obj.setCeCGST((BigDecimal) arr[14]);
		obj.setCeSGST((BigDecimal) arr[15]);
		obj.setCeCess((BigDecimal) arr[16]);
		obj.setLevel("L4");
		return obj;
	}

}
