package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Anx1OutwardDetailSummarySectionDaoImpl")
public class Anx1OutwardDetailSummarySectionDaoImpl
		implements Anx1OutwardSummarySectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1OutwardDetailSummarySectionDaoImpl.class);

	@Override
	public List<Annexure1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto requestDto) {

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(requestDto);

		String taxPeriodReq = req.getTaxPeriod();
		int taxPeriod = 0;
		if (taxPeriodReq != null) {
			taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		}
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

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
		StringBuilder buildHdr = new StringBuilder();
		StringBuilder build1 = new StringBuilder();
		StringBuilder build2 = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList ");
				buildHdr.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList ");
				build1.append(" AND HDR.GSTIN IN :gstinList ");
				build2.append(" AND SUPPLIER_GSTIN IN :gstinList ");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND PROFIT_CENTRE IN :pcList ");
				buildHdr.append(" AND HDR.PROFIT_CENTRE IN :pcList ");
				build2.append(" AND PROFIT_CENTER IN :pcList ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT_CODE IN :plantList ");
				buildHdr.append(" AND HDR.PLANT_CODE IN :plantList ");
				build2.append(" AND PLANT IN :plantList ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORGANIZATION IN :salesList ");
				buildHdr.append(" AND HDR.SALES_ORGANIZATION IN :salesList ");
				build2.append(" AND SALES_ORG IN :salesList ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND PURCHASE_ORGANIZATION IN :purcList ");
				buildHdr.append(" AND HDR.PURCHASE_ORGANIZATION IN :purcList ");
				// build2.append(" AND PURCHASE_ORGANIZATION IN :purcList ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND DISTRIBUTION_CHANNEL IN :distList ");
				buildHdr.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList ");
				build2.append(" AND DISTRIBUTION_CHANNEL IN :distList ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND DIVISION IN :divisionList ");
				buildHdr.append(" AND HDR.DIVISION IN :divisionList ");
				build2.append(" AND DIVISION IN :divisionList ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND LOCATION IN :locationList ");
				buildHdr.append(" AND HDR.LOCATION IN :locationList ");
				build2.append(" AND LOCATION IN :locationList ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND USERACCESS1 IN :ud1List ");
				buildHdr.append(" AND HDR.USERACCESS1 IN :ud1List ");
				build2.append(" AND USER_ACCESS1 IN :ud2List ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USERACCESS2 IN :ud2List ");
				buildHdr.append(" AND HDR.USERACCESS2 IN :ud2List ");
				build2.append(" AND USER_ACCESS2 IN :ud2List ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USERACCESS3 IN :ud3List ");
				buildHdr.append(" AND HDR.USERACCESS3 IN :ud3List ");
				build2.append(" AND USER_ACCESS3 IN :ud3List ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USERACCESS4 IN :ud4List ");
				buildHdr.append(" AND HDR.USERACCESS4 IN :ud4List ");
				build2.append(" AND USER_ACCESS4 IN :ud4List ");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USERACCESS5 IN :ud5List ");
				buildHdr.append(" AND HDR.USERACCESS5 IN :ud5List ");
				build2.append(" AND USER_ACCESS5 IN :ud5List ");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USERACCESS6 IN :ud6List ");
				buildHdr.append(" AND HDR.USERACCESS6 IN :ud6List ");
				build2.append(" AND USER_ACCESS6 IN :ud6List ");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			buildHdr.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			build1.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			build2.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		String buildQueryHdr = buildHdr.toString();
		String buildQuery1 = build1.toString();
		String buildQuery2 = build2.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryString(buildQuery,buildQueryHdr, buildQuery1,
				buildQuery2);
		
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Outward Query BEGIN----> 3A To 3G " + queryStr);
		}

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
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Annexure1SummarySectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.",e);
		}
	}

	private Annexure1SummarySectionDto convert(Object[] arr) {
		Annexure1SummarySectionDto obj = new Annexure1SummarySectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setDocType((String) arr[0]);
		obj.setTableSection((String) arr[1]);
		obj.setReturnPeriod((String) arr[2]);
		// BigInteger i = GenUtil.getBigInteger(arr[2]);
		obj.setRecords((GenUtil.getBigInteger(arr[3])).intValue());
		obj.setInvValue((BigDecimal) arr[4]);
		obj.setIgst((BigDecimal) arr[5]);
		obj.setCgst((BigDecimal) arr[6]);
		obj.setSgst((BigDecimal) arr[7]);
		obj.setTaxableValue((BigDecimal) arr[8]);
		obj.setTaxPayble((BigDecimal) arr[9]);
		obj.setCess((BigDecimal) arr[10]);
		obj.setMemoIgst((BigDecimal) arr[11]);
		obj.setMemoCgst((BigDecimal) arr[12]);
		obj.setMemoSgst((BigDecimal) arr[13]);
		obj.setMemoTaxPayable((BigDecimal) arr[14]);
		obj.setMemoCess((BigDecimal) arr[15]);
		obj.setGstnCount((GenUtil.getBigInteger(arr[16])).intValue());
		obj.setGstnInvoiceValue((BigDecimal) arr[17]);
		obj.setGstnIgst((BigDecimal) arr[18]);
		obj.setGstnCgst((BigDecimal) arr[19]);
		obj.setGstnSgst((BigDecimal) arr[20]);
		obj.setGstnTaxableValue((BigDecimal) arr[21]);
		obj.setGstnTaxPayble((BigDecimal) arr[22]);
		obj.setGstnCess((BigDecimal) arr[23]);

		return obj;
	}

	private String createQueryString(String buildQuery,String buildQueryHdr, String buildQuery1,
			String buildQuery2) {
		LOGGER.debug("Outward 3A TO 3G Query Execution BEGIN ");

		
		String queryStr = "SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,"
				+ "SUM(ASP_RECORD_COUNT) ASP_RECORD_COUNT,"
				+ "SUM(ASP_INVOICE_VALUE) AS ASP_INVOICE_VALUE,"
				+ "SUM(ASP_IGST_AMT) ASP_IGST_AMT,SUM(ASP_CGST_AMT) ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT) ASP_SGST_AMT,"
				+ "SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE,SUM(ASP_CESS) ASP_CESS,"
				+ "SUM(ASP_MEMOIGST) ASP_MEMOIGST,SUM(ASP_MEMOCGST) ASP_MEMOCGST,"
				+ "SUM(ASP_MEMOSGST) ASP_MEMOSGST,"
				+ "SUM(ASP_MEMOTAX_PAYABLE) ASP_MEMOTAX_PAYABLE,"
				+ "SUM(ASP_MEMOCESS) ASP_MEMOCESS,"
				+ "SUM(GSTN_RECORD_COUNT) GSTN_RECORD_COUNT,"
				+ "SUM(GSTN_INVOICE_VALUE) GSTN_INVOICE_VALUE,"
				+ "SUM(GSTN_IGST_AMT) GSTN_IGST_AMT,"
				+ "SUM(GSTN_CGST_AMT) GSTN_CGST_AMT,"
				+ "SUM(GSTN_SGST_AMT) GSTN_SGST_AMT,"
				+ "SUM(GSTN_TAXABLE_VALUE) GSTN_TAXABLE_VALUE,"
				+ "SUM(GSTN_TAX_PAYABLE) GSTN_TAX_PAYABLE,"
				+ "SUM(GSTN_CESS) GSTN_CESS FROM (( "
				+ "SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,ASP_RECORD_COUNT,"
				+ "ASP_INVOICE_VALUE,ASP_IGST_AMT,ASP_CGST_AMT,ASP_SGST_AMT,"
				+ "ASP_TAXABLE_VALUE, ASP_TAX_PAYABLE,ASP_CESS,"
				+ "ASP_MEMOIGST,ASP_MEMOCGST,ASP_MEMOSGST,ASP_MEMOTAX_PAYABLE,ASP_MEMOCESS,"
				+ "0 GSTN_RECORD_COUNT, 0 GSTN_INVOICE_VALUE,0 GSTN_IGST_AMT,"
				+ "0 GSTN_CGST_AMT,0 GSTN_SGST_AMT,0 GSTN_TAXABLE_VALUE,"
				+ "0 GSTN_TAX_PAYABLE,0 GSTN_CESS "
				+ "FROM ( SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,"
				+ "SUM(ASP_RECORD_COUNT) ASP_RECORD_COUNT,"
				+ "SUM(ASP_INVOICE_VALUE) ASP_INVOICE_VALUE,"
				+ "SUM(ASP_IGST_AMT) ASP_IGST_AMT,SUM(ASP_CGST_AMT) ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT) ASP_SGST_AMT,"
				+ "SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE,"
				+ "SUM(ASP_CESS) ASP_CESS,SUM(ASP_MEMOIGST) ASP_MEMOIGST,"
				+ "SUM(ASP_MEMOCGST) ASP_MEMOCGST,SUM(ASP_MEMOSGST) ASP_MEMOSGST,"
				+ "SUM(ASP_MEMOTAX_PAYABLE) ASP_MEMOTAX_PAYABLE,"
				+ "SUM(ASP_MEMOCESS) ASP_MEMOCESS "
				+ "FROM ( SELECT DOC_TYPE,AN_TABLE_SECTION AS TABLE_SECTION,"
				+ "RETURN_PERIOD,COUNT(*) ASP_RECORD_COUNT,"
				+ "IFNULL(SUM(DOC_AMT),0) AS ASP_INVOICE_VALUE,"
				+ "IFNULL(SUM(IGST_AMT),0) AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS ASP_CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS ASP_SGST_AMT,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0)  AS ASP_TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+ IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS ASP_TAX_PAYABLE,"
				+ "SUM(IFNULL(CESS_AMT_SPECIFIC,0) + "
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS ASP_CESS,"
				+ "IFNULL(SUM(MEMO_VALUE_IGST),0) AS ASP_MEMOIGST,"
				+ "IFNULL(SUM(MEMO_VALUE_CGST),0) AS ASP_MEMOCGST,"
				+ "IFNULL(SUM(MEMO_VALUE_SGST),0) AS ASP_MEMOSGST,"
				+ "SUM(IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+"
				+ "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0)) "
				+ "AS ASP_MEMOTAX_PAYABLE,"
				+ "IFNULL(SUM(MEMO_VALUE_CESS),0) AS ASP_MEMOCESS "
				+ "FROM ANX_OUTWARD_DOC_HEADER WHERE  "
				+ "IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
				+ "AND AN_TABLE_SECTION IN ('3B','3C','3D','3E','3F','3G') "
				+ buildQuery
				+ "GROUP BY DOC_TYPE,AN_TABLE_SECTION,RETURN_PERIOD) "
				+ "GROUP BY DOC_TYPE,TABLE_SECTION,RETURN_PERIOD "
				+ "UNION ALL " + "SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,"
				+ "COUNT(*) ASP_RECORD_COUNT,"
				+ "SUM(ASP_INVOICE_VALUE) ASP_INVOICE_VALUE,"
				+ "SUM(ASP_IGST_AMT) ASP_IGST_AMT,"
				+ "SUM(ASP_CGST_AMT) ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT) ASP_SGST_AMT,"
				+ "SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE,"
				+ "SUM(ASP_CESS) ASP_CESS,SUM(ASP_MEMOIGST) ASP_MEMOIGST,"
				+ "SUM(ASP_MEMOCGST) ASP_MEMOCGST,SUM(ASP_MEMOSGST) ASP_MEMOSGST,"
				+ "SUM(ASP_MEMOTAX_PAYABLE) ASP_MEMOTAX_PAYABLE,"
				+ "SUM(ASP_MEMOCESS) ASP_MEMOCESS "
				+ "FROM ( SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,"
				+ "SUM(ASP_INVOICE_VALUE) ASP_INVOICE_VALUE,"
				+ "SUM(ASP_IGST_AMT) ASP_IGST_AMT,"
				+ "SUM(ASP_CGST_AMT) ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT) ASP_SGST_AMT,"
				+ "SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE,"
				+ "SUM(ASP_CESS) ASP_CESS,SUM(ASP_MEMOIGST) ASP_MEMOIGST,"
				+ "SUM(ASP_MEMOCGST) ASP_MEMOCGST,SUM(ASP_MEMOSGST) ASP_MEMOSGST,"
				+ "SUM(ASP_MEMOTAX_PAYABLE) ASP_MEMOTAX_PAYABLE,"
				+ "SUM(ASP_MEMOCESS) ASP_MEMOCESS "
				+ "FROM ( SELECT DOC_TYPE,AN_TABLE_SECTION AS TABLE_SECTION,"
				+ "HDR.RETURN_PERIOD,IFNULL(SUM(DOC_AMT),0) AS ASP_INVOICE_VALUE,"
				+ "IFNULL(SUM(HDR.IGST_AMT),0) AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(HDR.CGST_AMT),0) AS ASP_CGST_AMT,"
				+ "IFNULL(SUM(HDR.SGST_AMT),0) AS ASP_SGST_AMT,"
				+ "IFNULL(SUM(HDR.TAXABLE_VALUE),0)  AS ASP_TAXABLE_VALUE,"
				+ "SUM(IFNULL(HDR.IGST_AMT,0)+ IFNULL(HDR.CGST_AMT,0)+"
				+ "IFNULL(HDR.SGST_AMT,0)+IFNULL(HDR.CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(HDR.CESS_AMT_ADVALOREM,0)) AS ASP_TAX_PAYABLE,"
				+ "SUM(IFNULL(HDR.CESS_AMT_SPECIFIC,0) + "
				+ "IFNULL(HDR.CESS_AMT_ADVALOREM,0)) AS ASP_CESS,"
				+ "IFNULL(SUM(HDR.MEMO_VALUE_IGST),0) AS ASP_MEMOIGST,"
				+ "IFNULL(SUM(HDR.MEMO_VALUE_CGST),0) AS ASP_MEMOCGST,"
				+ "IFNULL(SUM(HDR.MEMO_VALUE_SGST),0) AS ASP_MEMOSGST,"
				+ "SUM(IFNULL(HDR.MEMO_VALUE_IGST,0)+IFNULL(HDR.MEMO_VALUE_CGST,0)+"
				+ "IFNULL(HDR.MEMO_VALUE_SGST,0)+IFNULL(HDR.MEMO_VALUE_CESS,0)) "
				+ "AS ASP_MEMOTAX_PAYABLE,"
				+ "IFNULL(SUM(HDR.MEMO_VALUE_CESS),0) AS ASP_MEMOCESS "
				+ "FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ "INNER JOIN ANX_OUTWARD_DOC_ITEM ITM "
				+ "ON HDR.ID=ITM.DOC_HEADER_ID  WHERE "
				+ "IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
				+ "AND AN_TABLE_SECTION IN ('3A')  AND AN_RETURN_TYPE='ANX1' "
				+ buildQueryHdr
				+ "GROUP BY DOC_TYPE,AN_TABLE_SECTION,AN_RETURN_TYPE,"
				+ "HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,HDR.POS,TAX_RATE,HDR.DIFF_PERCENT,"
				+ "HDR.SECTION7_OF_IGST_FLAG,HDR.AUTOPOPULATE_TO_REFUND ) "
				+ "GROUP BY DOC_TYPE,TABLE_SECTION,RETURN_PERIOD ) "
				+ "GROUP BY DOC_TYPE,TABLE_SECTION,RETURN_PERIOD "
				+ "UNION ALL " + "SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,"
				+ "COUNT(*) ASP_RECORD_COUNT,"
				+ "SUM(ASP_INVOICE_VALUE) ASP_INVOICE_VALUE,"
				+ "SUM(ASP_IGST_AMT) ASP_IGST_AMT,"
				+ "SUM(ASP_CGST_AMT) ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT) ASP_SGST_AMT,"
				+ "SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE,"
				+ "SUM(ASP_CESS) ASP_CESS,SUM(ASP_MEMOIGST) ASP_MEMOIGST,"
				+ "SUM(ASP_MEMOCGST) ASP_MEMOCGST,"
				+ "SUM(ASP_MEMOSGST) ASP_MEMOSGST,"
				+ "SUM(ASP_MEMOTAX_PAYABLE) ASP_MEMOTAX_PAYABLE,"
				+ "SUM(ASP_MEMOCESS) ASP_MEMOCESS "
				+ "FROM ( SELECT 'INV' AS DOC_TYPE,'3A' AS TABLE_SECTION,"
				+ "SUPPLIER_GSTIN,RETURN_PERIOD,DIFF_PERCENT,SEC7_OF_IGST_FLAG,"
				+ "AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
				+ "AS ASP_INVOICE_VALUE,IFNULL(SUM(IGST_AMT),0) AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS ASP_CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS ASP_SGST_AMT,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0) AS ASP_TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+ "
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS ASP_TAX_PAYABLE,"
				+ "IFNULL(SUM(CESS_AMT),0) AS ASP_CESS,"
				+ "0 AS ASP_MEMOIGST,0 AS ASP_MEMOCGST,0 AS ASP_MEMOSGST,"
				+ "0 AS ASP_MEMOTAX_PAYABLE,0 AS ASP_MEMOCESS "
				+ "FROM ANX_PROCESSED_B2C WHERE IS_DELETE = FALSE "
				+ "AND RETURN_TYPE='ANX-1' " + buildQuery2
				+ "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,DIFF_PERCENT,"
				+ "SEC7_OF_IGST_FLAG ,AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE ) "
				+ "GROUP BY  DOC_TYPE,TABLE_SECTION,RETURN_PERIOD "
				+ "UNION ALL " + "SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,"
				+ "COUNT(*) ASP_RECORD_COUNT,"
				+ "SUM(ASP_INVOICE_VALUE) ASP_INVOICE_VALUE,"
				+ "SUM(ASP_IGST_AMT) ASP_IGST_AMT,SUM(ASP_CGST_AMT) ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT) ASP_SGST_AMT,"
				+ "SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE,SUM(ASP_CESS) ASP_CESS,"
				+ "SUM(ASP_MEMOIGST) ASP_MEMOIGST,SUM(ASP_MEMOCGST) ASP_MEMOCGST,"
				+ "SUM(ASP_MEMOSGST) ASP_MEMOSGST,"
				+ "SUM(ASP_MEMOTAX_PAYABLE) ASP_MEMOTAX_PAYABLE,"
				+ "SUM(ASP_MEMOCESS) ASP_MEMOCESS " + "FROM ( "
				+ "SELECT 'total' AS DOC_TYPE, '3A' AS TABLE_SECTION,"
				+ "RETURN_PERIOD,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
				+ "AS ASP_INVOICE_VALUE,IFNULL(SUM(IGST_AMT),0) AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS ASP_CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS ASP_SGST_AMT,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0) AS ASP_TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+ "
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS ASP_TAX_PAYABLE,"
				+ "IFNULL(SUM(CESS_AMT),0) AS ASP_CESS,"
				+ "0 AS ASP_MEMOIGST,0 AS ASP_MEMOCGST,0 AS ASP_MEMOSGST,"
				+ "0 AS ASP_MEMOTAX_PAYABLE,0 AS ASP_MEMOCESS "
				+ "FROM ANX_PROCESSED_B2C WHERE IS_DELETE = FALSE "
				+ "AND RETURN_TYPE='ANX-1' " + buildQuery2
				+ "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,DIFF_PERCENT,"
				+ "SEC7_OF_IGST_FLAG ,AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE ) "
				+ "GROUP BY  DOC_TYPE,TABLE_SECTION,RETURN_PERIOD "
				+ "UNION ALL " + "SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,"
				+ "SUM(ASP_RECORD_COUNT) ASP_RECORD_COUNT,"
				+ "SUM(ASP_INVOICE_VALUE) ASP_INVOICE_VALUE,"
				+ "SUM(ASP_IGST_AMT) ASP_IGST_AMT,SUM(ASP_CGST_AMT) ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT) ASP_SGST_AMT,"
				+ "SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE,"
				+ "SUM(ASP_CESS) ASP_CESS,SUM(ASP_MEMOIGST) ASP_MEMOIGST,"
				+ "SUM(ASP_MEMOCGST) ASP_MEMOCGST,SUM(ASP_MEMOSGST) ASP_MEMOSGST,"
				+ "SUM(ASP_MEMOTAX_PAYABLE) ASP_MEMOTAX_PAYABLE,"
				+ "SUM(ASP_MEMOCESS) ASP_MEMOCESS "
				+ "FROM ( SELECT 'total' AS DOC_TYPE,"
				+ "AN_TABLE_SECTION AS TABLE_SECTION,RETURN_PERIOD,"
				+ "COUNT(*) ASP_RECORD_COUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN DOC_AMT END),0) +  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN DOC_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN DOC_AMT END),0) "
				+ "AS ASP_INVOICE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN IGST_AMT END),0) +  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN IGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN IGST_AMT END),0) "
				+ "AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN CGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN CGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN CGST_AMT END),0) "
				+ "AS ASP_CGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN SGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN SGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN SGST_AMT END),0) "
				+ "AS ASP_SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN TAXABLE_VALUE END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN TAXABLE_VALUE END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN TAXABLE_VALUE END),0) "
				+ "AS ASP_TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
				+ "AS ASP_TAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN "
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN "
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
				+ "AS ASP_CESS,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN MEMO_VALUE_IGST END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN MEMO_VALUE_IGST END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_IGST END),0) "
				+ "AS ASP_MEMOIGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN MEMO_VALUE_CGST END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN MEMO_VALUE_CGST END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_CGST END),0) "
				+ "AS ASP_MEMOCGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN MEMO_VALUE_SGST END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN MEMO_VALUE_SGST END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_SGST END),0) "
				+ "AS ASP_MEMOSGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN IFNULL(MEMO_VALUE_IGST,0)+"
				+ "IFNULL(MEMO_VALUE_CGST,0)+IFNULL(MEMO_VALUE_SGST,0)+"
				+ "IFNULL(MEMO_VALUE_CESS,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN "
				+ "IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+"
				+ "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0)  END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+"
				+ "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0)  END),0) "
				+ "AS ASP_MEMOTAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN MEMO_VALUE_CESS END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN MEMO_VALUE_CESS END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_CESS END),0) "
				+ "AS ASP_MEMOCESS " + "FROM ANX_OUTWARD_DOC_HEADER WHERE "
				+ "IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
				+ "AND AN_TABLE_SECTION IN ('3C','3D')  AND AN_RETURN_TYPE='ANX1' "
				+ buildQuery + "GROUP BY AN_TABLE_SECTION,RETURN_PERIOD ) "
				+ "group by DOC_TYPE,TABLE_SECTION,RETURN_PERIOD "
				+ "UNION ALL " + "SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,"
				+ "COUNT(*) ASP_RECORD_COUNT,"
				+ "SUM(ASP_INVOICE_VALUE) ASP_INVOICE_VALUE,"
				+ "SUM(ASP_IGST_AMT) ASP_IGST_AMT,SUM(ASP_CGST_AMT) ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT) ASP_SGST_AMT,SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE,"
				+ "SUM(ASP_CESS) ASP_CESS,SUM(ASP_MEMOIGST) ASP_MEMOIGST,"
				+ "SUM(ASP_MEMOCGST) ASP_MEMOCGST,SUM(ASP_MEMOSGST) ASP_MEMOSGST,"
				+ "SUM(ASP_MEMOTAX_PAYABLE) ASP_MEMOTAX_PAYABLE,"
				+ "SUM(ASP_MEMOCESS) ASP_MEMOCESS "
				+ "FROM ( SELECT 'total' AS DOC_TYPE,"
				+ "AN_TABLE_SECTION AS TABLE_SECTION,RETURN_PERIOD,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN DOC_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN DOC_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN DOC_AMT END),0) "
				+ "AS ASP_INVOICE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN IGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN IGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN IGST_AMT END),0) "
				+ "AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN CGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN CGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN CGST_AMT END),0) "
				+ "AS ASP_CGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN SGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN SGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN SGST_AMT END),0) "
				+ "AS ASP_SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN TAXABLE_VALUE END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN TAXABLE_VALUE END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN TAXABLE_VALUE END),0) "
				+ "AS ASP_TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) AS ASP_TAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN "
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) AS ASP_CESS,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN MEMO_VALUE_IGST END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN MEMO_VALUE_IGST END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_IGST END),0) "
				+ "AS ASP_MEMOIGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN MEMO_VALUE_CGST END),0) +  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN MEMO_VALUE_CGST END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_CGST END),0) "
				+ "AS ASP_MEMOCGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN MEMO_VALUE_SGST END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN MEMO_VALUE_SGST END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_SGST END),0) "
				+ "AS ASP_MEMOSGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN "
				+ "IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+"
				+ "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN "
				+ "IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+"
				+ "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0)  END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+"
				+ "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0)  END),0) "
				+ "AS ASP_MEMOTAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='INV' THEN MEMO_VALUE_CESS END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN MEMO_VALUE_CESS END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN MEMO_VALUE_CESS END),0) "
				+ "AS ASP_MEMOCESS " + "FROM ANX_OUTWARD_DOC_HEADER WHERE "
				+ "IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
				+ "AND AN_TABLE_SECTION IN ('3A')  AND AN_RETURN_TYPE='ANX1'"
				+ buildQuery
				+ "GROUP BY DOC_TYPE,AN_TABLE_SECTION,RETURN_PERIOD ) "
				+ "GROUP BY DOC_TYPE,TABLE_SECTION,RETURN_PERIOD "
				+ "UNION ALL " + "SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,"
				+ "SUM(ASP_RECORD_COUNT) ASP_RECORD_COUNT,"
				+ "SUM(ASP_INVOICE_VALUE) ASP_INVOICE_VALUE,"
				+ "SUM(ASP_IGST_AMT) ASP_IGST_AMT,SUM(ASP_CGST_AMT) ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT) ASP_SGST_AMT,"
				+ "SUM(ASP_TAXABLE_VALUE) ASP_TAXABLE_VALUE,"
				+ "SUM(ASP_TAX_PAYABLE) ASP_TAX_PAYABLE,"
				+ "SUM(ASP_CESS) ASP_CESS,SUM(ASP_MEMOIGST) ASP_MEMOIGST,"
				+ "SUM(ASP_MEMOCGST) ASP_MEMOCGST,SUM(ASP_MEMOSGST) ASP_MEMOSGST,"
				+ "SUM(ASP_MEMOTAX_PAYABLE) ASP_MEMOTAX_PAYABLE,"
				+ "SUM(ASP_MEMOCESS) ASP_MEMOCESS "
				+ "FROM ( SELECT 'total' AS DOC_TYPE,"
				+ "AN_TABLE_SECTION AS TABLE_SECTION,RETURN_PERIOD,"
				+ "COUNT(*) ASP_RECORD_COUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN DOC_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN DOC_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN DOC_AMT END),0) "
				+ "AS ASP_INVOICE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN IGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN IGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN IGST_AMT END),0) "
				+ "AS ASP_IGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN CGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN CGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN CGST_AMT END),0) "
				+ "AS ASP_CGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN SGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN SGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN SGST_AMT END),0) "
				+ "AS ASP_SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN TAXABLE_VALUE END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN TAXABLE_VALUE END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN TAXABLE_VALUE END),0) "
				+ "AS ASP_TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
				+ "AS ASP_TAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN "
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) "
				+ "AS ASP_CESS,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN "
				+ "MEMO_VALUE_IGST END),0) +  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
				+ "MEMO_VALUE_IGST END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "MEMO_VALUE_IGST END),0) AS ASP_MEMOIGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN "
				+ "MEMO_VALUE_CGST END),0) +  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
				+ "MEMO_VALUE_CGST END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "MEMO_VALUE_CGST END),0) AS ASP_MEMOCGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN "
				+ "MEMO_VALUE_SGST END),0) +  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
				+ "MEMO_VALUE_SGST END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "MEMO_VALUE_SGST END),0) AS ASP_MEMOSGST,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN "
				+ "IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+"
				+ "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
				+ "IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+"
				+ "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0)  END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "IFNULL(MEMO_VALUE_IGST,0)+IFNULL(MEMO_VALUE_CGST,0)+"
				+ "IFNULL(MEMO_VALUE_SGST,0)+IFNULL(MEMO_VALUE_CESS,0)  END),0) "
				+ "AS ASP_MEMOTAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','RNV') THEN "
				+ "MEMO_VALUE_CESS END),0) +  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR','RDR') THEN "
				+ "MEMO_VALUE_CESS END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR') THEN "
				+ "MEMO_VALUE_CESS END),0) AS ASP_MEMOCESS "
				+ "FROM ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE "
				+ "AND IS_DELETE = FALSE "
				+ "AND AN_TABLE_SECTION IN ('3B','3E','3F','3G') "
				+ "AND AN_RETURN_TYPE='ANX1' " + buildQuery
				+ "GROUP BY AN_TABLE_SECTION,RETURN_PERIOD ) "
				+ "group by DOC_TYPE,TABLE_SECTION,RETURN_PERIOD )) "
				+ "UNION ALL "
				+ "( SELECT DOC_TYPE,TABLE_SECTION,RETURN_PERIOD,"
				+ "0 ASP_RECORD_COUNT,0 ASP_INVOICE_VALUE,0 ASP_IGST_AMT,"
				+ "0 ASP_CGST_AMT,0 ASP_SGST_AMT,0 ASP_TAXABLE_VALUE,"
				+ "0 ASP_TAX_PAYABLE,0 ASP_CESS,"
				+ "0 ASP_MEMOIGST,0 ASP_MEMOCGST,0 ASP_MEMOSGST,"
				+ "0 ASP_MEMOTAX_PAYABLE , 0 ASP_MEMOCESS,"
				+ "GSTN_RECORD_COUNT,GSTN_INVOICE_VALUE,GSTN_IGST_AMT,"
				+ "GSTN_CGST_AMT,GSTN_SGST_AMT,GSTN_TAXABLE_VALUE,"
				+ "GSTN_TAX_PAYABLE,GSTN_CESS "
				+ "FROM (SELECT 'total' AS DOC_TYPE, '3A' AS TABLE_SECTION,"
				+ "TAX_PERIOD AS RETURN_PERIOD,"
				+ "COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "SUM(IFNULL(ITM.TAXABLE_VALUE,0)+IFNULL(ITM.IGST_AMT,0)+"
				+ "IFNULL(ITM.CGST_AMT,0)+IFNULL(ITM.SGST_AMT,0)+"
				+ "IFNULL(ITM.CESS_AMT,0)) " + "AS GSTN_INVOICE_VALUE,"
				+ "IFNULL(SUM(ITM.IGST_AMT),0) AS GSTN_IGST_AMT,"
				+ "IFNULL(SUM(ITM.CGST_AMT),0) AS GSTN_CGST_AMT,"
				+ "IFNULL(SUM(ITM.SGST_AMT),0) AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS GSTN_TAXABLE_VALUE,"
				+ "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+"
				+ "IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)) AS GSTN_TAX_PAYABLE,"
				+ "IFNULL(SUM(ITM.CESS_AMT),0) AS GSTN_CESS "
				+ "FROM GETANX1_B2C_HEADER HDR "
				+ "INNER JOIN GETANX1_B2C_ITEM ITM ON HDR.ID=ITM.HEADER_ID "
				+ "WHERE IS_DELETE = FALSE " + buildQuery1
				+ "GROUP BY TAX_PERIOD " + "UNION ALL " + "SELECT "
				+ "(CASE WHEN DOC_TYPE IN ('I','RI') THEN 'INV' "
				+ "WHEN DOC_TYPE IN ('D','RD') THEN 'DR' "
				+ "WHEN DOC_TYPE IN ('C','RC') THEN 'CR' END) DOC_TYPE,"
				+ "'3B' AS TABLE_SECTION,TAX_PERIOD AS RETURN_PERIOD,"
				+ "COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(HDR.DOC_AMT),0) AS GSTN_INVOICE_VALUE,"
				+ "IFNULL(SUM(ITM.IGST_AMT),0) AS GSTN_IGST_AMT,"
				+ "IFNULL(SUM(ITM.CGST_AMT),0) AS GSTN_CGST_AMT,"
				+ "IFNULL(SUM(ITM.SGST_AMT),0) AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS GSTN_TAXABLE_VALUE,"
				+ "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+"
				+ "IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)) "
				+ "AS GSTN_TAX_PAYABLE,IFNULL(SUM(ITM.CESS_AMT),0) AS GSTN_CESS "
				+ "FROM GETANX1_B2B_HEADER HDR INNER JOIN GETANX1_B2B_ITEM ITM "
				+ "ON HDR.ID=ITM.HEADER_ID WHERE IS_DELETE = FALSE "
				+ buildQuery1 + "GROUP BY DOC_TYPE,TAX_PERIOD " + "UNION ALL "
				+ "SELECT 'total' as DOC_TYPE,'3B' AS TABLE_SECTION,"
				+ "TAX_PERIOD AS RETURN_PERIOD,COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN HDR.DOC_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN HDR.DOC_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN HDR.DOC_AMT END),0) "
				+ "AS GSTN_INVOICE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN ITM.IGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN ITM.IGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN ITM.IGST_AMT END),0) "
				+ "AS GSTN_IGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN ITM.CGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN ITM.CGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN ITM.CGST_AMT END),0) "
				+ "AS GSTN_CGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN ITM.SGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN ITM.SGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN ITM.SGST_AMT END),0) "
				+ "AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN ITM.TAXABLE_VALUE END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN ITM.TAXABLE_VALUE END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN ITM.TAXABLE_VALUE END),0) "
				+ "AS GSTN_TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN IFNULL(ITM.IGST_AMT,0)+"
				+ "IFNULL(ITM.CGST_AMT,0)+IFNULL(ITM.SGST_AMT,0)+"
				+ "IFNULL(ITM.CESS_AMT,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+"
				+ "IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+"
				+ "IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0) "
				+ "AS GSTN_TAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN "
				+ "IFNULL(ITM.CESS_AMT,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN "
				+ "IFNULL(ITM.CESS_AMT,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN "
				+ "IFNULL(ITM.CESS_AMT,0) END),0) AS GSTN_CESS "
				+ "FROM GETANX1_B2B_HEADER HDR "
				+ "INNER JOIN GETANX1_B2B_ITEM ITM ON HDR.ID=ITM.HEADER_ID "
				+ "WHERE IS_DELETE = FALSE " + buildQuery1
				+ "GROUP BY TAX_PERIOD " + "UNION ALL "
				+ "SELECT (CASE WHEN DOC_TYPE='I' THEN 'INV' "
				+ "WHEN DOC_TYPE='D' THEN 'DR' "
				+ "WHEN DOC_TYPE='C' THEN 'CR' END) DOC_TYPE,"
				+ "'3C' AS TABLE_SECTION,TAX_PERIOD AS RETURN_PERIOD,"
				+ "COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(HDR.DOC_AMT),0) AS GSTN_INVOICE_VALUE,"
				+ "IFNULL(SUM(ITM.IGST_AMT),0) AS GSTN_IGST_AMT,"
				+ "0 AS GSTN_CGST_AMT,0 AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS GSTN_TAXABLE_VALUE,"
				+ "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)) "
				+ "AS GSTN_TAX_PAYABLE,IFNULL(SUM(ITM.CESS_AMT),0) AS GSTN_CESS "
				+ "FROM GETANX1_EXPWP_HEADER HDR "
				+ "INNER JOIN  GETANX1_EXPWP_ITEM ITM "
				+ "ON HDR.ID=ITM.HEADER_ID WHERE IS_DELETE = FALSE "
				+ buildQuery1 + "GROUP BY DOC_TYPE,TAX_PERIOD " + "UNION ALL "
				+ "SELECT 'total' as DOC_TYPE,'3C' AS TABLE_SECTION,"
				+ "TAX_PERIOD AS RETURN_PERIOD,"
				+ "COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='I' THEN HDR.DOC_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='D' THEN HDR.DOC_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='C' THEN HDR.DOC_AMT END),0) "
				+ "AS GSTN_INVOICE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='I' THEN ITM.IGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='D' THEN ITM.IGST_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='C' THEN ITM.IGST_AMT END),0) "
				+ "AS GSTN_IGST_AMT,0 AS GSTN_CGST_AMT,0 AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='I' THEN ITM.TAXABLE_VALUE END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='D' THEN ITM.TAXABLE_VALUE END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='C' THEN ITM.TAXABLE_VALUE END),0) "
				+ "AS GSTN_TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='I' THEN IFNULL(ITM.IGST_AMT,0)+"
				+ "IFNULL(ITM.CESS_AMT,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='D' THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='C' THEN IFNULL(ITM.IGST_AMT,0)+"
				+ "IFNULL(ITM.CESS_AMT,0) END),0) AS GSTN_TAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='I' THEN IFNULL(ITM.CESS_AMT,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='D' THEN IFNULL(ITM.CESS_AMT,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='C' THEN IFNULL(ITM.CESS_AMT,0) END),0) "
				+ "AS GSTN_CESS " + "FROM GETANX1_EXPWP_HEADER HDR "
				+ "INNER JOIN GETANX1_EXPWP_ITEM ITM "
				+ "ON HDR.ID=ITM.HEADER_ID WHERE IS_DELETE = FALSE "
				+ buildQuery1 + "GROUP BY TAX_PERIOD " + "UNION ALL "
				+ "SELECT (CASE WHEN DOC_TYPE='I' THEN 'INV' "
				+ "WHEN DOC_TYPE='D' THEN 'DR' "
				+ "WHEN DOC_TYPE='C' THEN 'CR' END) DOC_TYPE,"
				+ "'3D' AS TABLE_SECTION," + "TAX_PERIOD AS RETURN_PERIOD,"
				+ "COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(HDR.DOC_AMT),0) AS GSTN_INVOICE_VALUE,"
				+ "0 AS GSTN_IGST_AMT,0 AS GSTN_CGST_AMT,"
				+ "0 AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS GSTN_TAXABLE_VALUE,"
				+ "0 AS GSTN_TAX_PAYABLE,0 AS GSTN_CESS "
				+ "FROM GETANX1_EXPWOP_HEADER HDR INNER JOIN "
				+ "GETANX1_EXPWOP_ITEM ITM ON HDR.ID=ITM.HEADER_ID "
				+ "WHERE IS_DELETE = FALSE " + buildQuery1
				+ "GROUP BY DOC_TYPE,TAX_PERIOD " + "UNION ALL "
				+ "SELECT 'total' as DOC_TYPE,'3D' AS TABLE_SECTION,"
				+ "TAX_PERIOD AS RETURN_PERIOD,COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='I' THEN HDR.DOC_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='D' THEN HDR.DOC_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='C' THEN HDR.DOC_AMT END),0) "
				+ "AS GSTN_INVOICE_VALUE,0 AS GSTN_IGST_AMT,0 AS GSTN_CGST_AMT,"
				+ "0 AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='I' THEN ITM.TAXABLE_VALUE END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='D' THEN ITM.TAXABLE_VALUE END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='C' THEN ITM.TAXABLE_VALUE END),0) "
				+ "AS GSTN_TAXABLE_VALUE,0 AS GSTN_TAX_PAYABLE,0 AS GSTN_CESS "
				+ "FROM GETANX1_EXPWOP_HEADER HDR "
				+ "INNER JOIN GETANX1_EXPWOP_ITEM ITM ON HDR.ID=ITM.HEADER_ID "
				+ "WHERE IS_DELETE = FALSE " + buildQuery1
				+ "GROUP BY TAX_PERIOD " + "UNION ALL "
				+ "SELECT (CASE WHEN DOC_TYPE IN ('I','RI') THEN 'INV' "
				+ "WHEN DOC_TYPE IN ('D','RD') THEN 'DR' "
				+ "WHEN DOC_TYPE IN ('C','RC') THEN 'CR' END) DOC_TYPE,"
				+ "'3E' AS TABLE_SECTION,TAX_PERIOD AS RETURN_PERIOD,"
				+ "COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(HDR.DOC_AMT),0) AS GSTN_INVOICE_VALUE,"
				+ "IFNULL(SUM(ITM.IGST_AMT),0) AS GSTN_IGST_AMT,"
				+ "0 AS GSTN_CGST_AMT,0 AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS GSTN_TAXABLE_VALUE,"
				+ "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)) AS GSTN_TAX_PAYABLE,"
				+ "IFNULL(SUM(ITM.CESS_AMT),0) AS GSTN_CESS "
				+ "FROM GETANX1_SEZWP_HEADER HDR "
				+ "INNER JOIN GETANX1_SEZWP_ITEM ITM ON HDR.ID=ITM.HEADER_ID "
				+ "WHERE IS_DELETE = FALSE " + buildQuery1
				+ "GROUP BY DOC_TYPE,TAX_PERIOD " + "UNION ALL "
				+ "SELECT 'total' as DOC_TYPE,'3E' AS TABLE_SECTION,"
				+ "TAX_PERIOD AS RETURN_PERIOD,"
				+ "COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN HDR.DOC_AMT END),0) +  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN HDR.DOC_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN HDR.DOC_AMT END),0) "
				+ "AS GSTN_INVOICE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN ITM.IGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN ITM.IGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN ITM.IGST_AMT END),0) "
				+ "AS GSTN_IGST_AMT,0 AS GSTN_CGST_AMT,0 AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN ITM.TAXABLE_VALUE END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN ITM.TAXABLE_VALUE END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN ITM.TAXABLE_VALUE END),0) "
				+ "AS GSTN_TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0) "
				+ "AS GSTN_TAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN "
				+ "IFNULL(ITM.CESS_AMT,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN "
				+ "IFNULL(ITM.CESS_AMT,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN "
				+ "IFNULL(ITM.CESS_AMT,0) END),0) AS GSTN_CESS "
				+ "FROM GETANX1_SEZWP_HEADER HDR "
				+ "INNER JOIN GETANX1_SEZWP_ITEM ITM ON HDR.ID=ITM.HEADER_ID "
				+ "WHERE IS_DELETE = FALSE " + buildQuery1
				+ "GROUP BY TAX_PERIOD " + "UNION ALL " + "SELECT "
				+ "(CASE WHEN DOC_TYPE IN ('I','RI') THEN 'INV' "
				+ "WHEN DOC_TYPE IN ('D','RD') THEN 'DR' "
				+ "WHEN DOC_TYPE IN ('C','RC') THEN 'CR' END) DOC_TYPE,"
				+ "'3F' AS TABLE_SECTION,"
				+ "TAX_PERIOD AS RETURN_PERIOD,COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(HDR.DOC_AMT),0) AS GSTN_INVOICE_VALUE,"
				+ "0 AS GSTN_IGST_AMT,0 AS GSTN_CGST_AMT,0 AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS GSTN_TAXABLE_VALUE,"
				+ "0 AS GSTN_TAX_PAYABLE,0 AS GSTN_CESS "
				+ "FROM GETANX1_SEZWOP_HEADER HDR "
				+ "INNER JOIN GETANX1_SEZWOP_ITEM ITM ON HDR.ID=ITM.HEADER_ID "
				+ "WHERE IS_DELETE = FALSE " + buildQuery1
				+ "GROUP BY DOC_TYPE,TAX_PERIOD " + "UNION ALL "
				+ "SELECT 'total' as DOC_TYPE,'3F' AS TABLE_SECTION,"
				+ "TAX_PERIOD AS RETURN_PERIOD,"
				+ "COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN HDR.DOC_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN HDR.DOC_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN HDR.DOC_AMT END),0) "
				+ "AS GSTN_INVOICE_VALUE,0 AS GSTN_IGST_AMT,0 AS GSTN_CGST_AMT,"
				+ "0 AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN "
				+ "ITM.TAXABLE_VALUE END),0) +  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN "
				+ "ITM.TAXABLE_VALUE END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN "
				+ "ITM.TAXABLE_VALUE END),0) AS GSTN_TAXABLE_VALUE,"
				+ "0 AS GSTN_TAX_PAYABLE,0 AS GSTN_CESS "
				+ "FROM GETANX1_SEZWOP_HEADER HDR "
				+ "INNER JOIN GETANX1_SEZWOP_ITEM ITM ON HDR.ID=ITM.HEADER_ID "
				+ "WHERE IS_DELETE = FALSE " + buildQuery1
				+ "GROUP BY TAX_PERIOD " + "UNION ALL "
				+ "SELECT (CASE WHEN DOC_TYPE IN ('I','RI') THEN 'INV' "
				+ "WHEN DOC_TYPE IN ('D','RD') THEN 'DR' "
				+ "WHEN DOC_TYPE IN ('C','RC') THEN 'CR' END) DOC_TYPE,"
				+ "'3G' AS TABLE_SECTION,TAX_PERIOD AS RETURN_PERIOD,"
				+ "COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(HDR.DOC_AMT),0) AS GSTN_INVOICE_VALUE,"
				+ "IFNULL(SUM(ITM.IGST_AMT),0) AS GSTN_IGST_AMT,"
				+ "IFNULL(SUM(ITM.CGST_AMT),0) AS GSTN_CGST_AMT,"
				+ "IFNULL(SUM(ITM.SGST_AMT),0) AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS GSTN_TAXABLE_VALUE,"
				+ "SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+"
				+ "IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)) "
				+ "AS GSTN_TAX_PAYABLE,"
				+ "IFNULL(SUM(ITM.CESS_AMT),0) AS GSTN_CESS "
				+ "FROM GETANX1_DE_HEADER HDR "
				+ "INNER JOIN GETANX1_DE_ITEM ITM ON HDR.ID=ITM.HEADER_ID "
				+ "WHERE IS_DELETE = FALSE " + buildQuery1
				+ "GROUP BY DOC_TYPE,TAX_PERIOD " + "UNION ALL "
				+ "SELECT 'total' as DOC_TYPE,'3G' AS TABLE_SECTION,"
				+ "TAX_PERIOD AS RETURN_PERIOD,"
				+ "COUNT(HDR.ID) AS GSTN_RECORD_COUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN HDR.DOC_AMT END),0) +  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN HDR.DOC_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN HDR.DOC_AMT END),0) "
				+ "AS GSTN_INVOICE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN ITM.IGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN ITM.IGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN ITM.IGST_AMT END),0) "
				+ "AS GSTN_IGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN ITM.CGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN ITM.CGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN ITM.CGST_AMT END),0) "
				+ "AS GSTN_CGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN ITM.SGST_AMT END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN ITM.SGST_AMT END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN ITM.SGST_AMT END),0) "
				+ "AS GSTN_SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN ITM.TAXABLE_VALUE END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN ITM.TAXABLE_VALUE END),0)- "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN ITM.TAXABLE_VALUE END),0) "
				+ "AS GSTN_TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+"
				+ "IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+"
				+ "IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN "
				+ "IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0)+"
				+ "IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0) END),0) "
				+ "AS GSTN_TAX_PAYABLE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('I','RI') THEN "
				+ "IFNULL(ITM.CESS_AMT,0) END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D','RD') THEN "
				+ "IFNULL(ITM.CESS_AMT,0) END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C','RC') THEN "
				+ "IFNULL(ITM.CESS_AMT,0) END),0) AS GSTN_CESS "
				+ "FROM GETANX1_DE_HEADER HDR "
				+ "INNER JOIN GETANX1_DE_ITEM ITM ON HDR.ID=ITM.HEADER_ID "
				+ "WHERE IS_DELETE = FALSE " + buildQuery1
				+ "GROUP BY TAX_PERIOD))) "
				+ "group by DOC_TYPE,TABLE_SECTION,RETURN_PERIOD ";
		LOGGER.debug("Outward 3A TO 3G Query Execution END ");
		return queryStr;

	}

}
