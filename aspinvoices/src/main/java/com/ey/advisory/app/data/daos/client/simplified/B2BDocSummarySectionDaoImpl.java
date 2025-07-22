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

@Component("B2BDocSummarySectionDaoImpl")
public class B2BDocSummarySectionDaoImpl implements BasicDocSummarySectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(B2BDocSummarySectionDaoImpl.class);

	@Override
	public List<Annexure1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto requestDto) {

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(requestDto);

		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

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
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList");
				buildHdr.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND PROFIT_CENTRE IN :pcList");
				buildHdr.append(" AND HDR.PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT_CODE IN :plantList");
				buildHdr.append(" AND HDR.PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORGANIZATION IN :salesList");
				buildHdr.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND PURCHASE_ORGANIZATION IN :purcList");
				buildHdr.append(" AND HDR.PURCHASE_ORGANIZATION IN :purcList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND DISTRIBUTION_CHANNEL IN :distList");
				buildHdr.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND DIVISION IN :divisionList");
				buildHdr.append(" AND HDR.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND LOCATION IN :locationList");
				buildHdr.append(" AND HDR.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND USERACCESS1 IN :ud1List");
				buildHdr.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USERACCESS2 IN :ud2List");
				buildHdr.append(" AND HDR.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USERACCESS3 IN :ud3List");
				buildHdr.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USERACCESS4 IN :ud4List");
				buildHdr.append(" AND HDR.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USERACCESS5 IN :ud5List");
				buildHdr.append(" AND HDR.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USERACCESS6 IN :ud6List");
				buildHdr.append(" AND HDR.USERACCESS6 IN :ud6List");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			buildHdr.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		String buildQueryHdr = buildHdr.toString();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN "+buildQuery);
		}

		String queryStr = createQueryString(buildQuery,buildQueryHdr);
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Outward Query BEGIN----> 3A To 3G "+ queryStr);
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
		obj.setRecords((GenUtil.getBigInteger(arr[2])).intValue());
		//BigInteger i = GenUtil.getBigInteger(arr[2]);
		//obj.setRecords(i.intValue());
		obj.setInvValue((BigDecimal) arr[3]);
		obj.setIgst((BigDecimal) arr[4]);
		obj.setCgst((BigDecimal) arr[5]);
		obj.setSgst((BigDecimal) arr[6]);
		obj.setTaxableValue((BigDecimal) arr[7]);
		obj.setTaxPayble((BigDecimal) arr[8]);
		obj.setCess((BigDecimal) arr[9]);
		obj.setMemoIgst((BigDecimal) arr[10]);
		obj.setMemoCgst((BigDecimal) arr[11]);
		obj.setMemoSgst((BigDecimal) arr[12]);
		obj.setMemoTaxPayable((BigDecimal) arr[13]);
		obj.setMemoCess((BigDecimal) arr[14]);
		return obj;
	}

	private String createQueryString(String buildQuery,String buildQueryHdr) {
		LOGGER.debug("Outward 3A TO 3G Query Execution BEGIN ");
		
		String queryStr = "SELECT DOC_TYPE,TABLE_SECTION,"
				+ "SUM(RECORD_COUNT) RECORD_COUNT,SUM(DOC_AMT) DOC_AMT,"
				+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
				+ "SUM(SGST_AMT) SGST_AMT,SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(TAX_PAYABLE) TAX_PAYABLE,SUM(CESS) CESS,"
				+ "SUM(MEMOIGST) MEMOIGST,SUM(MEMOCGST) MEMOCGST,"
				+ "SUM(MEMOSGST) MEMOSGST,SUM(MEMOTAX_PAYABLE) MEMOTAX_PAYABLE,"
				+ "SUM(MEMOCESS) MEMOCESS "
				+ "from ( SELECT DOC_TYPE,AN_TABLE_SECTION AS TABLE_SECTION,"
				+ "COUNT(*) RECORD_COUNT,IFNULL(SUM(DOC_AMT),0) AS DOC_AMT,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0)  AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+ IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS TAX_PAYABLE,"
				+ "SUM(IFNULL(CESS_AMT_SPECIFIC,0) + "
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS CESS,"
				+ "IFNULL(SUM(MEMO_VALUE_IGST),0) AS MEMOIGST,"
				+ "IFNULL(SUM(MEMO_VALUE_CGST),0) AS MEMOCGST,"
				+ "IFNULL(SUM(MEMO_VALUE_SGST),0) AS MEMOSGST,"
				+ "SUM(IFNULL(MEMO_VALUE_IGST,0)+"
				+ "IFNULL(MEMO_VALUE_CGST,0)+IFNULL(MEMO_VALUE_SGST,0)+"
				+ "IFNULL(MEMO_VALUE_CESS,0)) AS MEMOTAX_PAYABLE,"
				+ "IFNULL(SUM(MEMO_VALUE_CESS),0) AS MEMOCESS "
				+ "FROM ANX_OUTWARD_DOC_HEADER WHERE "
				+ "IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
				+ "AND AN_TABLE_SECTION IN ('3B','3C','3D','3E','3F','3G') "
				+ "AND AN_RETURN_TYPE='ANX1' "
				+ buildQuery
				+ "GROUP BY DOC_TYPE,AN_TABLE_SECTION ) "
				+ "GROUP BY DOC_TYPE, TABLE_SECTION "
				+ "UNION ALL "
				+ "SELECT DOC_TYPE,TABLE_SECTION, COUNT(*) ASP_RECORD_COUNT,"
				+ "SUM(DOC_AMT) DOC_AMT,SUM(IGST_AMT) IGST_AMT,"
				+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
				+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(TAX_PAYABLE) TAX_PAYABLE,"
				+ "SUM(CESS) CESS,SUM(MEMOIGST) MEMOIGST,SUM(MEMOCGST) MEMOCGST,"
				+ "SUM(MEMOSGST) MEMOSGST,SUM(MEMOTAX_PAYABLE) MEMOTAX_PAYABLE,"
				+ "SUM(MEMOCESS) MEMOCESS from ( SELECT DOC_TYPE,TABLE_SECTION,"
				+ "SUM(DOC_AMT) DOC_AMT,SUM(IGST_AMT) IGST_AMT,"
				+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
				+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(TAX_PAYABLE) TAX_PAYABLE,"
				+ "SUM(CESS) CESS,SUM(MEMOIGST) MEMOIGST,SUM(MEMOCGST) MEMOCGST,"
				+ "SUM(MEMOSGST) MEMOSGST,SUM(MEMOTAX_PAYABLE) MEMOTAX_PAYABLE,"
				+ "SUM(MEMOCESS) MEMOCESS FROM ( "
				+ "SELECT DOC_TYPE,AN_TABLE_SECTION AS TABLE_SECTION,"
                + "IFNULL(SUM(DOC_AMT),0) AS DOC_AMT,"
                + "IFNULL(SUM(HDR.IGST_AMT),0) AS IGST_AMT,"
                + "IFNULL(SUM(HDR.CGST_AMT),0) AS CGST_AMT,"
                + "IFNULL(SUM(HDR.SGST_AMT),0) AS SGST_AMT,"
                + "IFNULL(SUM(HDR.TAXABLE_VALUE),0)  AS TAXABLE_VALUE,"
                + "SUM(IFNULL(HDR.IGST_AMT,0)+ IFNULL(HDR.CGST_AMT,0)+"
                + "IFNULL(HDR.SGST_AMT,0)+IFNULL(HDR.CESS_AMT_SPECIFIC,0)+"
                + "IFNULL(HDR.CESS_AMT_ADVALOREM,0)) AS TAX_PAYABLE,"
                + "SUM(IFNULL(HDR.CESS_AMT_SPECIFIC,0) + "
                + "IFNULL(HDR.CESS_AMT_ADVALOREM,0)) AS CESS,"
                + "IFNULL(SUM(HDR.MEMO_VALUE_IGST),0) AS MEMOIGST,"
                + "IFNULL(SUM(HDR.MEMO_VALUE_CGST),0) AS MEMOCGST,"
                + "IFNULL(SUM(HDR.MEMO_VALUE_SGST),0) AS MEMOSGST,"
                + "SUM(IFNULL(HDR.MEMO_VALUE_IGST,0)+IFNULL(HDR.MEMO_VALUE_CGST,0)+"
                + "IFNULL(HDR.MEMO_VALUE_SGST,0)+"
                + "IFNULL(HDR.MEMO_VALUE_CESS,0)) AS MEMOTAX_PAYABLE,"
                + "IFNULL(SUM(HDR.MEMO_VALUE_CESS),0) AS MEMOCESS "
                + "FROM ANX_OUTWARD_DOC_HEADER HDR "
                + " INNER JOIN ANX_OUTWARD_DOC_ITEM ITM "
                + " ON HDR.ID=ITM.DOC_HEADER_ID WHERE "
                + "IS_PROCESSED = TRUE AND HDR.IS_DELETE = FALSE "
                + "AND AN_TABLE_SECTION IN ('3A') AND AN_RETURN_TYPE='ANX1' "
                + buildQueryHdr
                + "GROUP BY DOC_TYPE,AN_TABLE_SECTION,AN_RETURN_TYPE,"
                + " HDR.POS,TAX_RATE, HDR.DIFF_PERCENT, HDR.SECTION7_OF_IGST_FLAG,"
                + " HDR.AUTOPOPULATE_TO_REFUND ) "
				+ "GROUP BY DOC_TYPE,TABLE_SECTION ) "
				+ "GROUP BY DOC_TYPE,TABLE_SECTION ";

		
		LOGGER.debug("Outward 3A TO 3G Query Execution END ");
		return queryStr;

	}

	@Override
	public List<Annexure1SummarySectionDto> loadBasicb2cSummarySection(
			Annexure1SummaryReqDto requestDto) {

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(requestDto);

		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

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
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND PROFIT_CENTER IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORG IN :salesList");
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
				build.append(" AND USER_ACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USER_ACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USER_ACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USER_ACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USER_ACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USER_ACCESS6 IN :ud6List");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Applying where condition For b2c Vertical Files BEGIN ");
		}
		String queryStr = createQueryStringb2C(buildQuery);
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Outward Vertical Query  "+ queryStr);
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
			LOGGER.debug("Converting b2c Vertical Query to Dto ");
			List<Annexure1SummarySectionDto> retList = list.parallelStream()
					.map(o -> convertb2c(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			throw new AppException("Unexpected error in query execution.",e);
		}
	}

	private Annexure1SummarySectionDto convertb2c(Object[] arr) {
		Annexure1SummarySectionDto obj = new Annexure1SummarySectionDto();
		LOGGER.debug(
				" converting array to Dto b2c Vertical Files Query Execution  BEGIN ");
		obj.setDocType((String) arr[0]);
		obj.setTableSection((String) arr[1]);
		obj.setRecords((GenUtil.getBigInteger(arr[2])).intValue());
		obj.setInvValue((BigDecimal) arr[3]);
		obj.setTaxableValue((BigDecimal) arr[4]);
		obj.setTaxPayble((BigDecimal) arr[5]);

		obj.setIgst((BigDecimal) arr[6]);
		obj.setCgst((BigDecimal) arr[7]);
		obj.setSgst((BigDecimal) arr[8]);
		obj.setCess((BigDecimal) arr[9]);
		
		LOGGER.debug(
				" converting array to Dto b2c Vertical Files Query Execution  BEGIN");
		return obj;
	}

	private String createQueryStringb2C(String buildQuery) {

		LOGGER.debug(" b2c Vertical Files Query Execution  BEGIN ");
/*		String queryStr = "SELECT 'INV' AS DOC_TYPE," + "'3A' AS TABLE_SECTION,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
				+ "AS INVOICE_VALUE,IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TAX_PAYABLE,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST,"
				+ "IFNULL(SUM(CESS_AMT),0) AS CESS,"
				+ "COUNT(B2C_GSTN_INVKEY) AS RECORDS_COUNT "
				+ "FROM ANX_PROCESSED_B2C "
				+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='ANX-1' AND "
				+ buildQuery;
*/
		String queryStr = "SELECT DOC_TYPE,TABLE_SECTION,COUNT(*) RECORD_COUNT,"
				+ "SUM(INVOICE_VALUE) INVOICE_VALUE,"
				+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(TAX_PAYABLE) TAX_PAYABLE,"
				+ "SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,"
				+ "SUM(CESS) CESS FROM ( "
				+ "SELECT 'INV' AS DOC_TYPE,'3A' AS TABLE_SECTION,"
				+ "SUM(IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
				+ "AS INVOICE_VALUE,"
				+ "IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
				+ "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TAX_PAYABLE,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST,"
				+ "IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM ANX_PROCESSED_B2C WHERE IS_DELETE = FALSE "
				+ "AND RETURN_TYPE='ANX-1' "
				+ buildQuery
				+ "GROUP BY DIFF_PERCENT,SEC7_OF_IGST_FLAG ,"
				+ "AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE ) "
				+ "GROUP BY  DOC_TYPE,TABLE_SECTION ";
		LOGGER.debug(" b2c Vertical Files Query Execution  END ");
		return queryStr;

	}

}
