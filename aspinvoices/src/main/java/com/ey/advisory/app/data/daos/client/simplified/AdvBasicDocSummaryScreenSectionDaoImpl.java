/**
 * 
 */
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
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author BalaKrishna S
 *
 */
@Service("AdvBasicDocSummaryScreenSectionDaoImpl")
public class AdvBasicDocSummaryScreenSectionDaoImpl
		implements AdvBasicGstr1DocSummaryScreenSectionDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AdvBasicDocSummaryScreenSectionDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Override
	public List<Gstr1SummarySectionDto> loadBasicSummaryATSection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub
		
		if (Strings.isNullOrEmpty(request.getReturnType())) {
			request.setReturnType(APIConstants.GSTR1.toUpperCase());
		}


		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		
		String taxPeriodReq = req.getTaxPeriod();
		String taxPeriodFrom = req.getTaxPeriodFrom();
		String taxPeriodTo = req.getTaxPeriodTo();
		int taxPeriod = 0;
		int taxPeriodFrom1 = 0;
		int taxPeriodTo1 = 0;
		if (null != taxPeriodReq) {
			taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		}

		if (null != taxPeriodFrom && taxPeriodTo != null) {
			taxPeriodFrom1 = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
			taxPeriodTo1 = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
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

		StringBuilder buildAdv = new StringBuilder();
		StringBuilder buildTrans = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildAdv.append(" SUPPLIER_GSTIN IN :gstinList ");
				buildTrans.append(" HDR.SUPPLIER_GSTIN IN :gstinList ");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildAdv.append(" AND PROFIT_CENTRE IN :pcList ");
				buildTrans.append(" AND HDR.PROFIT_CENTRE IN :pcList ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildAdv.append(" AND PLANT_CODE IN :plantList ");
				buildTrans.append(" AND HDR.PLANT_CODE IN :plantList ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildAdv.append(" AND SALES_ORGANIZATION IN :salesList ");
				buildTrans.append(" AND HDR.SALES_ORGANIZATION IN :salesList ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				buildAdv.append(" AND PURCHASE_ORGANIZATION IN :purcList ");
				buildTrans
						.append(" AND HDR.PURCHASE_ORGANIZATION IN :purcList ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildAdv.append(" AND DISTRIBUTION_CHANNEL IN :distList ");
				buildTrans
						.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildAdv.append(" AND DIVISION IN :divisionList ");
				buildTrans.append(" AND HDR.DIVISION IN :divisionList ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildAdv.append(" AND LOCATION IN :locationList ");
				buildTrans.append(" AND HDR.LOCATION IN :locationList ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildAdv.append(" AND USERACCESS1 IN :ud1List ");
				buildTrans.append(" AND HDR.USERACCESS1 IN :ud1List ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildAdv.append(" AND USERACCESS2 IN :ud2List ");
				buildTrans.append(" AND HDR.USERACCESS2 IN :ud2List ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildAdv.append(" AND USERACCESS3 IN :ud3List ");
				buildTrans.append(" AND HDR.USERACCESS3 IN :ud3List ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildAdv.append(" AND USERACCESS4 IN :ud4List ");
				buildTrans.append(" AND HDR.USERACCESS4 IN :ud4List ");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildAdv.append(" AND USERACCESS5 IN :ud5List ");
				buildTrans.append(" AND HDR.USERACCESS5 IN :ud5List ");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildAdv.append(" AND USERACCESS6 IN :ud6List ");
				buildTrans.append(" AND HDR.USERACCESS6 IN :ud6List ");
			}
		}
		if (taxPeriod != 0) {

			buildAdv.append(" AND DERIVED_RET_PERIOD  = :taxPeriod ");
			buildTrans.append(" AND HDR.DERIVED_RET_PERIOD  = :taxPeriod ");
		}

		if (null != taxPeriodFrom && taxPeriodTo != null) {
			buildAdv.append(
					" AND DERIVED_RET_PERIOD  BETWEEN :taxPeriodFrom1 AND :taxPeriodTo1 ");
			buildTrans.append(
					" AND HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom1 AND :taxPeriodTo1 ");

		}

		String buildQuery1 = buildAdv.toString();
		String buildTransQuery = buildTrans.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Prepared where Condition and apply in Outward Query BEGIN");
		}

		String queryStr = null;

		if (!Strings.isNullOrEmpty(request.getReturnType())
				&& APIConstants.GSTR1A
						.equalsIgnoreCase(request.getReturnType())) {
			queryStr = createQueryATStringForGstr1a(buildQuery1,
					buildTransQuery);

		} else {
			queryStr = createQueryATString(buildQuery1, buildTransQuery);

		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("AT Query Executed ----->" + queryStr);
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

			if (taxPeriodFrom1 != 0 && taxPeriodTo1 != 0) {
				q.setParameter("taxPeriodFrom1", taxPeriodFrom1);
				q.setParameter("taxPeriodTo1", taxPeriodTo1);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Gstr1SummarySectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Fetching data From Query ----->" + retList);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummarySectionDto convert(Object[] arr) {
		Gstr1SummarySectionDto obj = new Gstr1SummarySectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTaxDocType((String) arr[0]);
		obj.setRecords((GenUtil.getBigInteger(arr[2])).intValue());
		obj.setInvValue((BigDecimal) arr[3]);
		obj.setTaxableValue((BigDecimal) arr[4]);
		obj.setTaxPayable((BigDecimal) arr[5]);
		obj.setIgst((BigDecimal) arr[6]);
		obj.setCgst((BigDecimal) arr[7]);
		obj.setSgst((BigDecimal) arr[8]);
		obj.setCess((BigDecimal) arr[9]);
		return obj;
	}

	private String createQueryATString(String buildQuery1,
			String buildTransQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Advance Received Query Execution BEGIN ");

		String queryStr = "WITH SUMMARY_VIEW AS ("
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,DIFF_PERCENT,POS,IFNULL(SUM(TOTAL_VALUE),0) AS TOTAL_VALUE,"
				+ "IFNULL(SUM(GROSS_ADVANCE),0) AS GROSS_ADVANCE,IFNULL(SUM(TOTAL_TAX),0) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM (SELECT  'ADV REC' AS TAX_DOC_TYPE,'RV' AS DOC_TYPE,IFNULL(HDR.DIFF_PERCENT,'') AS DIFF_PERCENT ,IFNULL(HDR.POS,9999) AS POS,"
				+ "IFNULL(ITM.ONB_LINE_ITEM_AMT,0) AS TOTAL_VALUE,"
				+ "IFNULL(ITM.TAXABLE_VALUE,0) AS GROSS_ADVANCE,"
				+ "IFNULL(ITM.IGST_AMT ,0)+IFNULL(ITM.CGST_AMT ,0)+IFNULL(ITM.SGST_AMT ,0)+IFNULL(ITM.CESS_AMT_SPECIFIC,0) + "
				+ "IFNULL(ITM.CESS_AMT_ADVALOREM,0) AS TOTAL_TAX,"
				+ "IFNULL(ITM.IGST_AMT ,0) AS IGST_AMT,IFNULL(ITM.CGST_AMT ,0) AS CGST_AMT,IFNULL(ITM.SGST_AMT ,0) AS SGST_AMT,"
				+ "IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0) AS CESS_AMT FROM "
				+ "ANX_OUTWARD_DOC_HEADER HDR INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE "
				+ "ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND IS_DELETE = FALSE AND HDR.RETURN_TYPE='GSTR1' AND HDR.TAX_DOC_TYPE IN ('AT') AND "
				+ buildTransQuery
				+ ") ADV GROUP BY TAX_DOC_TYPE,DOC_TYPE,DIFF_PERCENT,POS "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS,IFNULL(SUM(TOTAL_VALUE),0) AS TOTAL_VALUE,"
				+ "IFNULL(SUM(GROSS_ADVANCE),0) AS GROSS_ADVANCE,IFNULL(SUM(TOTAL_TAX),0) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM (SELECT 'ADV REC' AS TAX_DOC_TYPE,'RV' AS DOC_TYPE,TRAN_TYPE,IFNULL(NEW_POS,9999) AS NEW_POS,"
				+ "IFNULL(NEW_GROSS_ADV_RECEIVED,0)+IFNULL(IGST_AMT,0)+ IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0) AS TOTAL_VALUE,"
				+ "IFNULL(NEW_GROSS_ADV_RECEIVED,0) AS GROSS_ADVANCE,IFNULL(IGST_AMT,0)+ IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT,0) AS TOTAL_TAX,"
				+ "IFNULL(IGST_AMT,0) AS IGST_AMT, IFNULL(CGST_AMT,0) AS CGST_AMT,IFNULL(SGST_AMT,0) AS SGST_AMT, IFNULL(CESS_AMT,0) AS CESS_AMT "
				+ "FROM GSTR1_PROCESSED_ADV_RECEIVED WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE AND "
				+ buildQuery1
				+ ") GROUP BY TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS,SUM(TOTAL_VALUE) TOTAL_VALUE,SUM(GROSS_ADVANCE) GROSS_ADVANCE,SUM(TOTAL_TAX)"
				+ "TOTAL_TAX,SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS FROM ("
				+ "SELECT 'ADV REC-A' AS TAX_DOC_TYPE,'RRV' AS DOC_TYPE,"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END) AS TRAN_TYPE,NEW_POS,"
				+ "SUM(IFNULL(NEW_GROSS_ADV_RECEIVED,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_VALUE,"
				+ "IFNULL(SUM(NEW_GROSS_ADV_RECEIVED),0) AS GROSS_ADVANCE,SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0))"
				+ "AS TOTAL_TAX,IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0)"
				+ "AS CESS FROM GSTR1_PROCESSED_ADV_RECEIVED WHERE IS_DELETE = FALSE  AND IS_AMENDMENT=TRUE AND "
				+ buildQuery1 + " GROUP BY TRAN_TYPE,NEW_POS,"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END),MONTH,NEW_POS) "
				+ "GROUP BY TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS,SUM(TOTAL_VALUE) TOTAL_VALUE,SUM(GROSS_ADVANCE) GROSS_ADVANCE,"
				+ "SUM(TOTAL_TAX) TOTAL_TAX,SUM(IGST) IGST,"
				+ "SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS FROM(SELECT 'ADV ADJ' AS TAX_DOC_TYPE,'AV' AS DOC_TYPE,"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END) AS TRAN_TYPE,NEW_POS,"
				+ "SUM(IFNULL(NEW_GROSS_ADV_ADJUSTED,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0))"
				+ "AS TOTAL_VALUE,IFNULL(SUM(NEW_GROSS_ADV_ADJUSTED),0) AS GROSS_ADVANCE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,IFNULL(SUM(IGST_AMT),0) AS IGST,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM GSTR1_PROCESSED_ADV_ADJUSTMENT WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE AND "
				+ buildQuery1 + " GROUP BY"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END),NEW_POS) "
				+ "GROUP BY TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE, DIFF_PERCENT,POS ,IFNULL(SUM(TOTAL_VALUE),0) AS TOTAL_VALUE,"
				+ "IFNULL(SUM(GROSS_ADVANCE),0) AS GROSS_ADVANCE,IFNULL(SUM(TOTAL_TAX),0) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM (SELECT  'ADV ADJ' AS TAX_DOC_TYPE,'AV' AS DOC_TYPE,HDR.DIFF_PERCENT,IFNULL(HDR.POS,9999) AS POS,"
				+ "IFNULL(ITM.ONB_LINE_ITEM_AMT,0) AS TOTAL_VALUE,IFNULL(ITM.TAXABLE_VALUE,0) AS GROSS_ADVANCE,"
				+ "IFNULL(ITM.IGST_AMT ,0)+IFNULL(ITM.CGST_AMT ,0)+IFNULL(ITM.SGST_AMT ,0)+IFNULL(ITM.CESS_AMT_SPECIFIC,0) + "
				+ "IFNULL(ITM.CESS_AMT_ADVALOREM,0) AS TOTAL_TAX,"
				+ "IFNULL(ITM.IGST_AMT ,0) AS IGST_AMT,IFNULL(ITM.CGST_AMT ,0) AS CGST_AMT,IFNULL(ITM.SGST_AMT ,0) AS SGST_AMT,"
				+ "IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0) AS CESS_AMT FROM "
				+ "ANX_OUTWARD_DOC_HEADER HDR INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE "
				+ "ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND IS_DELETE = FALSE AND HDR.RETURN_TYPE='GSTR1' AND HDR.TAX_DOC_TYPE IN ('TXP') AND "
				+ buildTransQuery
				+ ") GROUP BY TAX_DOC_TYPE,DOC_TYPE, DIFF_PERCENT,POS "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS,SUM(TOTAL_VALUE) TOTAL_VALUE,SUM(GROSS_ADVANCE) GROSS_ADVANCE,"
				+ "SUM(TOTAL_TAX) TOTAL_TAX,SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS FROM ("
				+ "SELECT 'ADV ADJ-A' AS TAX_DOC_TYPE,'RAV' AS DOC_TYPE,"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END) AS TRAN_TYPE,NEW_POS,"
				+ "SUM(IFNULL(NEW_GROSS_ADV_ADJUSTED,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_VALUE,"
				+ "IFNULL(SUM(NEW_GROSS_ADV_ADJUSTED),0) AS GROSS_ADVANCE,SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM GSTR1_PROCESSED_ADV_ADJUSTMENT "
				+ "WHERE IS_DELETE = FALSE  AND IS_AMENDMENT=TRUE AND "
				+ buildQuery1 + " " + "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END),MONTH,NEW_POS) "
				+ "GROUP BY TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS)"
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,COUNT(DISTINCT DIFF_PERCENT,POS) AS RECORD_COUNT,SUM(TOTAL_VALUE) AS TOTAL_VALUE,SUM(GROSS_ADVANCE) AS GROSS_ADVANCE,"
				+ "SUM(TOTAL_TAX) AS TOTAL_TAX,SUM(IGST) AS IGST,SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS "
				+ "FROM SUMMARY_VIEW GROUP BY TAX_DOC_TYPE,DOC_TYPE";

		// String queryStr = "SELECT TAX_DOC_TYPE,DOC_TYPE,"
		// + "SUM(RECORD_COUNT) RECORD_COUNT,SUM(TOTAL_VALUE) TOTAL_VALUE,"
		// + "SUM(GROSS_ADVANCE) GROSS_ADVANCE,SUM(TOTAL_TAX) TOTAL_TAX,"
		// + "SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS "
		// + "FROM ( "
		// + "SELECT TAX_DOC_TYPE,DOC_TYPE,COUNT(*) RECORD_COUNT,"
		// + "SUM(TOTAL_VALUE) TOTAL_VALUE,"
		// + "SUM(GROSS_ADVANCE) GROSS_ADVANCE,SUM(TOTAL_TAX) TOTAL_TAX,"
		// + "SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS "
		// + "FROM ( SELECT 'ADV REC' AS TAX_DOC_TYPE,'RV' AS DOC_TYPE,"
		// + "SUM(IFNULL(NEW_GROSS_ADV_RECEIVED,0)+IFNULL(IGST_AMT,0)+"
		// + "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
		// + "AS TOTAL_VALUE,"
		// + "IFNULL(SUM(NEW_GROSS_ADV_RECEIVED),0) AS GROSS_ADVANCE,"
		// + "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
		// + "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,"
		// + "IFNULL(SUM(IGST_AMT),0) AS IGST,"
		// + "IFNULL(SUM(CGST_AMT),0) AS CGST,"
		// + "IFNULL(SUM(SGST_AMT),0) AS SGST,"
		// + "IFNULL(SUM(CESS_AMT),0) AS CESS "
		// + "FROM GSTR1_PROCESSED_ADV_RECEIVED "
		// + "WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE AND "
		// + buildQuery1 + "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
		// + "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65')
		// THEN 'L65' "
		// + "WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN
		// 'N' END),NEW_POS ) "
		// + "GROUP BY TAX_DOC_TYPE,DOC_TYPE " + "UNION ALL "
		// + "SELECT TAX_DOC_TYPE,DOC_TYPE,COUNT(*) RECORD_COUNT,"
		// + "SUM(TOTAL_VALUE) TOTAL_VALUE,SUM(GROSS_ADVANCE) GROSS_ADVANCE,"
		// + "SUM(TOTAL_TAX) TOTAL_TAX,SUM(IGST) IGST,SUM(CGST) CGST,"
		// + "SUM(SGST) SGST,SUM(CESS) CESS "
		// + "FROM ( SELECT 'ADV REC-A' AS TAX_DOC_TYPE,'RRV' AS DOC_TYPE,"
		// + "SUM(IFNULL(NEW_GROSS_ADV_RECEIVED,0)+IFNULL(IGST_AMT,0)+"
		// + "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
		// + "AS TOTAL_VALUE,"
		// + "IFNULL(SUM(NEW_GROSS_ADV_RECEIVED),0) AS GROSS_ADVANCE,"
		// + "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
		// + "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,"
		// + "IFNULL(SUM(IGST_AMT),0) AS IGST,"
		// + "IFNULL(SUM(CGST_AMT),0) AS CGST,"
		// + "IFNULL(SUM(SGST_AMT),0) AS SGST,"
		// + "IFNULL(SUM(CESS_AMT),0) AS CESS "
		// + "FROM GSTR1_PROCESSED_ADV_RECEIVED WHERE "
		// + "IS_DELETE = FALSE AND IS_AMENDMENT=TRUE AND " + buildQuery1
		// + "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
		// + "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65')
		// THEN 'L65' "
		// + "WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN
		// 'N' END), "
		// + "MONTH,NEW_POS) " + "GROUP BY TAX_DOC_TYPE,DOC_TYPE "
		// + "UNION ALL "
		// + "SELECT TAX_DOC_TYPE,DOC_TYPE,COUNT(*) RECORD_COUNT,"
		// + "SUM(TOTAL_VALUE) TOTAL_VALUE,"
		// + "SUM(GROSS_ADVANCE) GROSS_ADVANCE,SUM(TOTAL_TAX) TOTAL_TAX,"
		// + "SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS "
		// + "FROM ( "
		// + "SELECT 'ADV ADJ' AS TAX_DOC_TYPE,'AV' AS DOC_TYPE,"
		// + "SUM(IFNULL(NEW_GROSS_ADV_ADJUSTED,0)+IFNULL(IGST_AMT,0)+"
		// + "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
		// + "IFNULL(CESS_AMT,0)) AS TOTAL_VALUE,"
		// + "IFNULL(SUM(NEW_GROSS_ADV_ADJUSTED),0) AS GROSS_ADVANCE,"
		// + "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
		// + "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,"
		// + "IFNULL(SUM(IGST_AMT),0) AS IGST,"
		// + "IFNULL(SUM(CGST_AMT),0) AS CGST,"
		// + "IFNULL(SUM(SGST_AMT),0) AS SGST,"
		// + "IFNULL(SUM(CESS_AMT),0) AS CESS "
		// + "FROM GSTR1_PROCESSED_ADV_ADJUSTMENT "
		// + "WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE AND "
		// + buildQuery1 + "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
		// + "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65')
		// THEN 'L65' "
		// + "WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN
		// 'N' END),NEW_POS ) "
		// + "GROUP BY TAX_DOC_TYPE,DOC_TYPE "
		// + "UNION ALL SELECT TAX_DOC_TYPE,DOC_TYPE,COUNT(*) RECORD_COUNT,"
		// + "SUM(TOTAL_VALUE) TOTAL_VALUE,SUM(GROSS_ADVANCE) GROSS_ADVANCE,"
		// + "SUM(TOTAL_TAX) TOTAL_TAX,SUM(IGST) IGST,SUM(CGST) CGST,"
		// + "SUM(SGST) SGST,SUM(CESS) CESS "
		// + "FROM ( SELECT 'ADV ADJ-A' AS TAX_DOC_TYPE,'RAV' AS DOC_TYPE,"
		// + "SUM(IFNULL(NEW_GROSS_ADV_ADJUSTED,0)+IFNULL(IGST_AMT,0)+"
		// + "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) "
		// + "AS TOTAL_VALUE,"
		// + "IFNULL(SUM(NEW_GROSS_ADV_ADJUSTED),0) AS GROSS_ADVANCE,"
		// + "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+"
		// + "IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,"
		// + "IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,"
		// + "IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
		// + "FROM GSTR1_PROCESSED_ADV_ADJUSTMENT "
		// + "WHERE IS_DELETE = FALSE AND IS_AMENDMENT=TRUE AND "
		// + buildQuery1 + "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
		// + "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65')
		// THEN 'L65' "
		// + "WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN
		// 'N' END),MONTH,NEW_POS)"
		// + "GROUP BY TAX_DOC_TYPE,DOC_TYPE ) "
		// + "GROUP BY TAX_DOC_TYPE,DOC_TYPE ";

		LOGGER.debug("AT Query Execution Completed");
		return queryStr;
	}

	private String createQueryATStringForGstr1a(String buildQuery1,
			String buildTransQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Advance Received Query Execution BEGIN ");

		String queryStr = "WITH SUMMARY_VIEW AS ("
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,DIFF_PERCENT,POS,IFNULL(SUM(TOTAL_VALUE),0) AS TOTAL_VALUE,"
				+ "IFNULL(SUM(GROSS_ADVANCE),0) AS GROSS_ADVANCE,IFNULL(SUM(TOTAL_TAX),0) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM (SELECT  'ADV REC' AS TAX_DOC_TYPE,'RV' AS DOC_TYPE,IFNULL(HDR.DIFF_PERCENT,'') AS DIFF_PERCENT ,IFNULL(HDR.POS,9999) AS POS,"
				+ "IFNULL(ITM.ONB_LINE_ITEM_AMT,0) AS TOTAL_VALUE,"
				+ "IFNULL(ITM.TAXABLE_VALUE,0) AS GROSS_ADVANCE,"
				+ "IFNULL(ITM.IGST_AMT ,0)+IFNULL(ITM.CGST_AMT ,0)+IFNULL(ITM.SGST_AMT ,0)+IFNULL(ITM.CESS_AMT_SPECIFIC,0) + "
				+ "IFNULL(ITM.CESS_AMT_ADVALOREM,0) AS TOTAL_TAX,"
				+ "IFNULL(ITM.IGST_AMT ,0) AS IGST_AMT,IFNULL(ITM.CGST_AMT ,0) AS CGST_AMT,IFNULL(ITM.SGST_AMT ,0) AS SGST_AMT,"
				+ "IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0) AS CESS_AMT FROM "
				+ "ANX_OUTWARD_DOC_HEADER_1A HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE "
				+ "ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND IS_DELETE = FALSE AND HDR.RETURN_TYPE='GSTR1A' AND HDR.TAX_DOC_TYPE IN ('AT') AND "
				+ buildTransQuery
				+ ") ADV GROUP BY TAX_DOC_TYPE,DOC_TYPE,DIFF_PERCENT,POS "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS,IFNULL(SUM(TOTAL_VALUE),0) AS TOTAL_VALUE,"
				+ "IFNULL(SUM(GROSS_ADVANCE),0) AS GROSS_ADVANCE,IFNULL(SUM(TOTAL_TAX),0) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM (SELECT 'ADV REC' AS TAX_DOC_TYPE,'RV' AS DOC_TYPE,TRAN_TYPE,IFNULL(NEW_POS,9999) AS NEW_POS,"
				+ "IFNULL(NEW_GROSS_ADV_RECEIVED,0)+IFNULL(IGST_AMT,0)+ IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0) AS TOTAL_VALUE,"
				+ "IFNULL(NEW_GROSS_ADV_RECEIVED,0) AS GROSS_ADVANCE,IFNULL(IGST_AMT,0)+ IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT,0) AS TOTAL_TAX,"
				+ "IFNULL(IGST_AMT,0) AS IGST_AMT, IFNULL(CGST_AMT,0) AS CGST_AMT,IFNULL(SGST_AMT,0) AS SGST_AMT, IFNULL(CESS_AMT,0) AS CESS_AMT "
				+ "FROM GSTR1A_PROCESSED_ADV_RECEIVED WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE AND "
				+ buildQuery1
				+ ") GROUP BY TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS,SUM(TOTAL_VALUE) TOTAL_VALUE,SUM(GROSS_ADVANCE) GROSS_ADVANCE,SUM(TOTAL_TAX)"
				+ "TOTAL_TAX,SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS FROM ("
				+ "SELECT 'ADV REC-A' AS TAX_DOC_TYPE,'RRV' AS DOC_TYPE,"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END) AS TRAN_TYPE,NEW_POS,"
				+ "SUM(IFNULL(NEW_GROSS_ADV_RECEIVED,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_VALUE,"
				+ "IFNULL(SUM(NEW_GROSS_ADV_RECEIVED),0) AS GROSS_ADVANCE,SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0))"
				+ "AS TOTAL_TAX,IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0)"
				+ "AS CESS FROM GSTR1A_PROCESSED_ADV_RECEIVED WHERE IS_DELETE = FALSE  AND IS_AMENDMENT=TRUE AND "
				+ buildQuery1 + " GROUP BY TRAN_TYPE,NEW_POS,"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END),MONTH,NEW_POS) "
				+ "GROUP BY TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS,SUM(TOTAL_VALUE) TOTAL_VALUE,SUM(GROSS_ADVANCE) GROSS_ADVANCE,"
				+ "SUM(TOTAL_TAX) TOTAL_TAX,SUM(IGST) IGST,"
				+ "SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS FROM(SELECT 'ADV ADJ' AS TAX_DOC_TYPE,'AV' AS DOC_TYPE,"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END) AS TRAN_TYPE,NEW_POS,"
				+ "SUM(IFNULL(NEW_GROSS_ADV_ADJUSTED,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0))"
				+ "AS TOTAL_VALUE,IFNULL(SUM(NEW_GROSS_ADV_ADJUSTED),0) AS GROSS_ADVANCE,"
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,IFNULL(SUM(IGST_AMT),0) AS IGST,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM GSTR1A_PROCESSED_ADV_ADJUSTMENT WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE AND "
				+ buildQuery1 + " GROUP BY"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END),NEW_POS) "
				+ "GROUP BY TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE, DIFF_PERCENT,POS ,IFNULL(SUM(TOTAL_VALUE),0) AS TOTAL_VALUE,"
				+ "IFNULL(SUM(GROSS_ADVANCE),0) AS GROSS_ADVANCE,IFNULL(SUM(TOTAL_TAX),0) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM (SELECT  'ADV ADJ' AS TAX_DOC_TYPE,'AV' AS DOC_TYPE,HDR.DIFF_PERCENT,IFNULL(HDR.POS,9999) AS POS,"
				+ "IFNULL(ITM.ONB_LINE_ITEM_AMT,0) AS TOTAL_VALUE,IFNULL(ITM.TAXABLE_VALUE,0) AS GROSS_ADVANCE,"
				+ "IFNULL(ITM.IGST_AMT ,0)+IFNULL(ITM.CGST_AMT ,0)+IFNULL(ITM.SGST_AMT ,0)+IFNULL(ITM.CESS_AMT_SPECIFIC,0) + "
				+ "IFNULL(ITM.CESS_AMT_ADVALOREM,0) AS TOTAL_TAX,"
				+ "IFNULL(ITM.IGST_AMT ,0) AS IGST_AMT,IFNULL(ITM.CGST_AMT ,0) AS CGST_AMT,IFNULL(ITM.SGST_AMT ,0) AS SGST_AMT,"
				+ "IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0) AS CESS_AMT FROM "
				+ "ANX_OUTWARD_DOC_HEADER_1A HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE "
				+ "ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND IS_DELETE = FALSE AND HDR.RETURN_TYPE='GSTR1A' AND HDR.TAX_DOC_TYPE IN ('TXP') AND "
				+ buildTransQuery
				+ ") GROUP BY TAX_DOC_TYPE,DOC_TYPE, DIFF_PERCENT,POS "
				+ "UNION ALL "
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS,SUM(TOTAL_VALUE) TOTAL_VALUE,SUM(GROSS_ADVANCE) GROSS_ADVANCE,"
				+ "SUM(TOTAL_TAX) TOTAL_TAX,SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS FROM ("
				+ "SELECT 'ADV ADJ-A' AS TAX_DOC_TYPE,'RAV' AS DOC_TYPE,"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END) AS TRAN_TYPE,NEW_POS,"
				+ "SUM(IFNULL(NEW_GROSS_ADV_ADJUSTED,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_VALUE,"
				+ "IFNULL(SUM(NEW_GROSS_ADV_ADJUSTED),0) AS GROSS_ADVANCE,SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST,IFNULL(SUM(CGST_AMT),0) AS CGST,IFNULL(SUM(SGST_AMT),0) AS SGST,IFNULL(SUM(CESS_AMT),0) AS CESS "
				+ "FROM GSTR1A_PROCESSED_ADV_ADJUSTMENT "
				+ "WHERE IS_DELETE = FALSE  AND IS_AMENDMENT=TRUE AND "
				+ buildQuery1 + " " + "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
				+ "(CASE WHEN TRAN_TYPE IN ('ZL65','L65','zl65','l65','zL65','Zl65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','','z','n') OR TRAN_TYPE IS NULL) THEN 'N' END),MONTH,NEW_POS) "
				+ "GROUP BY TAX_DOC_TYPE,DOC_TYPE,TRAN_TYPE,NEW_POS)"
				+ "SELECT TAX_DOC_TYPE,DOC_TYPE,COUNT(DISTINCT DIFF_PERCENT,POS) AS RECORD_COUNT,SUM(TOTAL_VALUE) AS TOTAL_VALUE,SUM(GROSS_ADVANCE) AS GROSS_ADVANCE,"
				+ "SUM(TOTAL_TAX) AS TOTAL_TAX,SUM(IGST) AS IGST,SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS "
				+ "FROM SUMMARY_VIEW GROUP BY TAX_DOC_TYPE,DOC_TYPE";

		LOGGER.debug("AT Query Execution Completed");
		return queryStr;
	}

}
