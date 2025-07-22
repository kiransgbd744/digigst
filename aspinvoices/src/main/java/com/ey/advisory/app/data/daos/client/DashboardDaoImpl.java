package com.ey.advisory.app.data.daos.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.DashboardReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service("dashboardDaoImpl")
public class DashboardDaoImpl implements DashboardDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DashboardDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Object[]> getGstrReturnStatus(List<String> gstins,
			String retPeriod, List<String> returnType, String status) {
		LOGGER.debug(
				"executing the getGstrReturnStatus method in DashboardDaoImpl");

		List<Object[]> result = new ArrayList<>();
		StringBuilder build = new StringBuilder();
		if (gstins != null && retPeriod != null) {
			build.append(" returnStatus.GSTIN in :gstins");
			build.append(" AND returnStatus.TAXPERIOD = :retPeriod");
			build.append(" AND returnStatus.RETURN_TYPE in :returnType");
			build.append(" AND returnStatus.STATUS = :status");
		}

		String sql = "SELECT * FROM GSTR_RETURN_STATUS returnStatus WHERE "
				+ build.toString();
		LOGGER.debug("getGstrReturnStatus query" + sql);
		if (sql != null && sql.trim().length() > 0) {
			try {
				Query query = entityManager.createNativeQuery(sql);
				if (gstins != null && retPeriod != null) {
					query.setParameter("gstins", gstins);
					// .toString().replace("[", "").replace("]", ""));
					query.setParameter("retPeriod", retPeriod);
					query.setParameter("returnType", returnType);
					query.setParameter("status", status);
				}
				result = query.getResultList();
			} catch (Exception e) {
				LOGGER.error(
						"Erorr While Executing the Query for Dashboard Data {}",
						e);
				e.printStackTrace();
			}
		}
		LOGGER.debug(" exit getGstrReturnStatus with " + result.size());
		return result;
	}

	@Override
	public List<Object[]> getSupplyDetails(Long entityId,
			DashboardReqDto reqDto) {
		LOGGER.debug(
				"executing the getSupplyDetails method in DashboardDaoImpl");
		List<Object[]> result = new ArrayList<>();
		StringBuilder build = new StringBuilder();
		StringBuilder query = new StringBuilder();
		String taxPeriod = reqDto.getTaxPeriod();
		/*
		 * List<String> gstins = reqDto.getDataSecAttrs()
		 * .get(OnboardingConstant.GSTIN);
		 */
		List<Long> entityIdList = new ArrayList<>();
		entityIdList.add(reqDto.getEntityId());
		Map<String, List<String>> outDataSecAttrs = reqDto
				.getOutwardDataSecAttrs();
		Map<String, List<String>> inDataSecAttrs = reqDto
				.getInwardDataSecAttrs();
		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String purchase = null, distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null, sgstin = null;
		String cgstin = null;

		List<String> outGstinList = null;
		List<String> outPcList = null;
		List<String> outPlantList = null;
		List<String> outDivisionList = null;
		List<String> outLocationList = null;
		List<String> outSalesList = null;
		List<String> outDistList = null;
		List<String> outUd1List = null;
		List<String> outUd2List = null;
		List<String> outUd3List = null;
		List<String> outUd4List = null;
		List<String> outUd5List = null;
		List<String> outUd6List = null;

		List<String> inGstinList = null;
		List<String> inPcList = null;
		List<String> inPlantList = null;
		List<String> inDivisionList = null;
		List<String> inLocationList = null;
		List<String> inPurcList = null;
		List<String> inUd1List = null;
		List<String> inUd2List = null;
		List<String> inUd3List = null;
		List<String> inUd4List = null;
		List<String> inUd5List = null;
		List<String> inUd6List = null;

		if (outDataSecAttrs != null && !outDataSecAttrs.isEmpty()
				&& outDataSecAttrs.size() > 0) {
			for (String key : outDataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					outPcList = outDataSecAttrs.get(OnboardingConstant.PC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					plant = key;
					outPlantList = outDataSecAttrs
							.get(OnboardingConstant.PLANT);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					outDivisionList = outDataSecAttrs
							.get(OnboardingConstant.DIVISION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					outLocationList = outDataSecAttrs
							.get(OnboardingConstant.LOCATION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					outSalesList = outDataSecAttrs.get(OnboardingConstant.SO);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					outDistList = outDataSecAttrs.get(OnboardingConstant.DC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					outUd1List = outDataSecAttrs.get(OnboardingConstant.UD1);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					outUd2List = outDataSecAttrs.get(OnboardingConstant.UD2);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					outUd3List = outDataSecAttrs.get(OnboardingConstant.UD3);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					outUd4List = outDataSecAttrs.get(OnboardingConstant.UD4);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					outUd5List = outDataSecAttrs.get(OnboardingConstant.UD5);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					outUd6List = outDataSecAttrs.get(OnboardingConstant.UD6);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					outGstinList = outDataSecAttrs
							.get(OnboardingConstant.GSTIN);
				}
			}
		}

		if (inDataSecAttrs != null && !inDataSecAttrs.isEmpty()
				&& inDataSecAttrs.size() > 0) {
			for (String key : inDataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					inPcList = inDataSecAttrs.get(OnboardingConstant.PC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					plant = key;
					inPlantList = inDataSecAttrs.get(OnboardingConstant.PLANT);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					inDivisionList = inDataSecAttrs
							.get(OnboardingConstant.DIVISION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					inLocationList = inDataSecAttrs
							.get(OnboardingConstant.LOCATION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					inPurcList = inDataSecAttrs.get(OnboardingConstant.PO);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					inUd1List = inDataSecAttrs.get(OnboardingConstant.UD1);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					inUd2List = inDataSecAttrs.get(OnboardingConstant.UD2);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					inUd3List = inDataSecAttrs.get(OnboardingConstant.UD3);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					inUd4List = inDataSecAttrs.get(OnboardingConstant.UD4);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					inUd5List = inDataSecAttrs.get(OnboardingConstant.UD5);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					inUd6List = inDataSecAttrs.get(OnboardingConstant.UD6);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					cgstin = key;
					inGstinList = inDataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}

		String qryStr = createQueryString(entityIdList, outGstinList,
				inGstinList, taxPeriod, outDataSecAttrs, inDataSecAttrs,
				profitCenter, sgstin, cgstin, plant, division, location, sales,
				purchase, distChannel, ud1, ud2, ud3, ud4, ud5, ud6, outPcList,
				outPlantList, outSalesList, outDivisionList, outLocationList,
				outDistList, outUd1List, outUd2List, outUd3List, outUd4List,
				outUd5List, outUd6List, inPcList, inPlantList, inDivisionList,
				inLocationList, inPurcList, inUd1List, inUd2List, inUd3List,
				inUd4List, inUd5List, inUd6List);

		String arr[] = qryStr.split("#@");
		String outQueryStr = arr.length > 0 ? arr[0] : "";
		String outQueryStrB2c = arr.length > 0 ? arr[1] : "";
		String inQueryStr = arr.length > 0 ? arr[2] : "";
		String inQuertStr3H = arr.length > 0 ? arr[3] : "";
		
		LOGGER.debug("outQueryStr-->" + outQueryStr);
		LOGGER.debug(" outQueryStrB2c -->"+outQueryStrB2c);
		LOGGER.debug("inQueryStr-->" + inQueryStr);
        LOGGER.debug("inQuertStr3H -->"+ inQuertStr3H);
		
		if (entityId != null) {
			build.append("EI.ID=" + entityId.toString());
			query.append("EI.ID=" + entityId.toString());
		}

		if (taxPeriod != null && !taxPeriod.equals("")) {
			build.append(" AND OUTR.RETURN_PERIOD= '" + taxPeriod + "'");
			query.append(" AND INV.RETURN_PERIOD= '" + taxPeriod + "'");
		}

		String queryStr = getquery(outQueryStr, outQueryStrB2c,  inQueryStr,
				inQuertStr3H, build.toString())
				.toString() + query.toString()
				+ " AND GI.IS_DELETE=FALSE AND EI.IS_DELETE=FALSE"
				+ ")FUT GROUP BY GSTIN,RETURN_PERIOD ORDER BY GSTIN ;";

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created : %s", queryStr);
			LOGGER.debug(str);
		}

		Query q = entityManager.createNativeQuery(queryStr);

		try {

			/*
			 * * if (gstins != null && gstins.size() > 0) {
			 * q.setParameter("gstins", gstins); } if (taxPeriod != null &&
			 * !taxPeriod.isEmpty()) { q.setParameter("taxPeriod", taxPeriod); }
			 */

			if (outGstinList != null && outGstinList.size() > 0
					&& !outGstinList.contains("")) {
				q.setParameter("sgstins", outGstinList);
			}

			if (inGstinList != null && inGstinList.size() > 0
					&& !inGstinList.contains("")) {
				q.setParameter("cgstins", inGstinList);
			}

			if (profitCenter != null && !profitCenter.isEmpty()
					&& !profitCenter.isEmpty() && outPcList != null
					&& outPcList.size() > 0 && inPcList != null
					&& inPcList.size() > 0) {
				q.setParameter("outPcList", outPcList);
				q.setParameter("inPcList", inPcList);
			}
			if (plant != null && !plant.isEmpty() && !plant.isEmpty()
					&& outPlantList != null && outPlantList.size() > 0
					&& inPlantList != null && inPlantList.size() > 0) {
				q.setParameter("outPlantList", outPlantList);
				q.setParameter("inPlantList", inPlantList);
			}
			if (sales != null && !sales.isEmpty() && outSalesList != null
					&& outSalesList.size() > 0) {
				q.setParameter("outSalesList", outSalesList);
			}
			if (division != null && !division.isEmpty()
					&& outDivisionList != null && outDivisionList.size() > 0
					&& inDivisionList != null && inDivisionList.size() > 0) {
				q.setParameter("outDivisionList", outDivisionList);
				q.setParameter("inDivisionList", inDivisionList);
			}
			if (location != null && !location.isEmpty()
					&& outLocationList != null && outLocationList.size() > 0
					&& inLocationList != null && inLocationList.size() > 0) {
				q.setParameter("outLocationList", outLocationList);
				q.setParameter("inLocationList", inLocationList);
			}
			if (purchase != null && !purchase.isEmpty() && inPurcList != null
					&& inPurcList.size() > 0) {
				q.setParameter("inPurcList", inPurcList);
			}
			if (distChannel != null && !distChannel.isEmpty()
					&& outDistList != null && outDistList.size() > 0) {
				q.setParameter("outDistList", outDistList);
			}
			if (ud1 != null && !ud1.isEmpty() && outUd1List != null
					&& outUd1List.size() > 0 && inUd1List != null
					&& inUd1List.size() > 0) {
				q.setParameter("outUd1List", outUd1List);
				q.setParameter("inUd1List", inUd1List);
			}
			if (ud2 != null && !ud2.isEmpty() && outUd2List != null
					&& outUd2List.size() > 0 && inUd2List != null
					&& inUd2List.size() > 0) {
				q.setParameter("outUd2List", outUd2List);
				q.setParameter("inUd2List", inUd2List);
			}
			if (ud3 != null && !ud3.isEmpty() && outUd3List != null
					&& outUd3List.size() > 0 && inUd3List != null
					&& inUd3List.size() > 0) {
				q.setParameter("outUd3List", outUd3List);
				q.setParameter("inUd3List", inUd3List);
			}
			if (ud4 != null && !ud4.isEmpty() && outUd4List != null
					&& outUd4List.size() > 0 && inUd4List != null
					&& inUd4List.size() > 0) {
				q.setParameter("outUd4List", outUd4List);
				q.setParameter("inUd4List", inUd4List);
			}
			if (ud5 != null && !ud5.isEmpty() && outUd5List != null
					&& outUd5List.size() > 0 && inUd5List != null
					&& inUd5List.size() > 0) {
				q.setParameter("outUd5List", outUd5List);
				q.setParameter("inUd5List", inUd5List);
			}
			if (ud6 != null && !ud6.isEmpty() && outUd6List != null
					&& outUd6List.size() > 0 && inUd6List != null
					&& inUd6List.size() > 0) {
				q.setParameter("outUd6List", outUd6List);
				q.setParameter("inUd6List", inUd6List);
			}

			result = q.getResultList();

		} catch (Exception e) {
			LOGGER.error(
					"Erorr While Executing the Query for Dashboard Data {}", e);
			e.printStackTrace();
		}
		LOGGER.debug("exit the getSupplyDetails method in DashboardDaoImpl : "
				+ result.size());
		return result;

	}

	private String getquery(String outQueryStr, String outQueryB2C, 
			String inQueryStr, String inQuery3H3I ,
			String condition) {

		return "SELECT GSTIN , RETURN_PERIOD , SUM(TAXABLE_VALUE) AS TAXABLE_"
				+ "VALUE , SUM(OUTPUT_TAX) AS OUTPUT_TAX , SUM(INWARD_SUPPLY) "
				+ "AS INWARD_SUPPLY , SUM(TAX_AMOUNT) AS TAX_AMOUNT , SUM("
				+ "AVAILABLE_CREDIT) AS AVAILABLE_CREDIT FROM ( SELECT "
				+ "OUTR.GSTIN,OUTR.RETURN_PERIOD,OUTR.TAXABLE_VALUE,OUTPUT_TAX,"
				+ "0 AS INWARD_SUPPLY,0 AS TAX_AMOUNT,0 AS AVAILABLE_CREDIT FROM"
				+ " ( SELECT SUPPLIER_GSTIN AS GSTIN,RETURN_PERIOD ,SUM(TAXABLE"
				+ "_VALUE) AS TAXABLE_VALUE ,SUM(OUTPUT_TAX) AS OUTPUT_TAX FROM"
				+ " ( SELECT SUPPLIER_GSTIN , RETURN_PERIOD , SUM(IFNULL(TAXABLE"
				+ "_VALUE,0)) AS TAXABLE_VALUE , SUM(IFNULL(IGST_AMT,0)+IFNULL"
				+ "(CGST_AMT,0)+IFNULL(SGST_AMT,0) +IFNULL(CESS_AMT_SPECIFIC,0)"
				+ "+IFNULL(CESS_AMT_ADVALOREM,0)) AS OUTPUT_TAX FROM \"CLIENT1_"
				+ "GST\".\"ANX_OUTWARD_DOC_HEADER\" WHERE " + outQueryStr
				+ " AN_TABLE_SECTION IN "
				+ "('3A','3B','3C','3D','3E','3F','3G') AND DOC_TYPE IN ('INV',"
				+ "'DR','RNV','RDR') AND IS_DELETE=FALSE AND AN_RETURN_TYPE="
				+ "'ANX1' AND IS_PROCESSED=TRUE GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD UNION ALL SELECT"
				+ " SUPPLIER_GSTIN , RETURN_PERIOD , -SUM(IFNULL(TAXABLE_VALUE,"
				+ "0)) AS TAXABLE_VALUE , -SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_"
				+ "AMT,0)+IFNULL(SGST_AMT,0) +IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0)) AS OUTPUT_TAX FROM "
				+ "\"CLIENT1_GST\".\"ANX_OUTWARD_DOC_HEADER\" WHERE "
				+ outQueryStr + " AN_TABLE_"
				+ "SECTION IN ('3A','3B','3C','3D','3E','3F','3G') AND DOC_TYPE"
				+ " IN ('CR','RCR') AND IS_DELETE=FALSE AND AN_RETURN_TYPE='ANX1'"
				+ " AND IS_PROCESSED=TRUE GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD UNION ALL SELECT "
				+ "SUPPLIER_GSTIN , RETURN_PERIOD , SUM(IFNULL(TAXABLE_VALUE,0))"
				+ " AS TAXABLE_VALUE , SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)"
				+ "+IFNULL(SGST_AMT,0) +IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_"
				+ "AMT_ADVALOREM,0)) AS OUTPUT_TAX FROM \"CLIENT1_GST\"."
				+ "\"ANX_OUTWARD_DOC_HEADER\" WHERE " + outQueryStr
				+ " AN_TABLE_SECTION IN ('3C3','3D1','3D2','3D3','3D4') AND "
				+ "IS_DELETE=FALSE AND AN_RETURN_TYPE='RET1' AND IS_PROCESSED=TRUE GROUP BY SUPPLIER_"
				+ "GSTIN,RETURN_PERIOD UNION ALL SELECT SUPPLIER_GSTIN , RETURN_"
				+ "PERIOD , -SUM(IFNULL(TAXABLE_VALUE,0)) AS TAXABLE_VALUE , "
				+ "-SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)"
				+ " +IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)) "
				+ "AS OUTPUT_TAX FROM \"CLIENT1_GST\".\"ANX_OUTWARD_DOC_HEADER\" "
				+ "WHERE" + outQueryStr
				+ " AN_TABLE_SECTION='3C4' AND IS_DELETE=FALSE AND AN_"
				+ "RETURN_TYPE='RET1' AND IS_PROCESSED=TRUE GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD "
				+ "UNION ALL SELECT SUPPLIER_GSTIN ,RETURN_PERIOD,SUM(IFNULL(TAXABLE_VALUE,0)) AS  TAXABLE_VALUE ,"
				+ "SUM(IFNULL(IGST_AMT,0))+SUM(IFNULL(CGST_AMT,0))"
				+ "+SUM(IFNULL(SGST_AMT,0))+SUM(IFNULL(CESS_AMT,0))  AS OUTPUT_TAX "
				+ " FROM \"CLIENT1_GST\".\"ANX_PROCESSED_B2C\"  WHERE"
				+ outQueryB2C + " IS_DELETE =FALSE"
				+ " GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD )OT GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD "
				+ ")OUTR INNER JOIN \"CLIENT1_GST\".\"GSTIN_INFO\" GI ON GI.GSTIN=OUTR.GSTIN "
				+ "INNER JOIN \"CLIENT1_GST\".\"ENTITY_INFO\" EI  ON EI.ID=GI.ENTITY_ID "
				+ "WHERE " + condition
				+ " AND GI.IS_DELETE=FALSE AND EI.IS_DELETE=FALSE "
				+ " UNION SELECT INV.GSTIN,INV.RETURN_PERIOD,0,0,INV.INWARD_"
				+ "SUPPLY,INV.TAX_AMOUNT,INV.AVAILABLE_CREDIT FROM ( SELECT "
				+ "CUST_GSTIN AS GSTIN,RETURN_PERIOD,SUM(INWARD_SUPPLY) AS "
				+ "INWARD_SUPPLY ,SUM(TAX_AMOUNT) AS TAX_AMOUNT,SUM(AVAILABLE"
				+ "_CREDIT) AS AVAILABLE_CREDIT FROM ( SELECT CUST_GSTIN , "
				+ "RETURN_PERIOD , SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARD_SUPPLY"
				+ " , SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT"
				+ ",0) +IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)"
				+ ") AS TAX_AMOUNT , SUM(IFNULL(AVAILABLE_IGST,0)+IFNULL("
				+ "AVAILABLE_CGST,0)+IFNULL(AVAILABLE_SGST,0) +IFNULL(AVAILABLE"
				+ "_CESS,0)) AS AVAILABLE_CREDIT FROM \"CLIENT1_GST\"."
				+ "\"ANX_INWARD_DOC_HEADER\" WHERE " + inQueryStr
				+ " AN_TABLE_SECTION IN ('3B',"
				+ "'3E','3F','3G') AND DOC_TYPE IN ('INV','DR','RNV','RDR') AND"
				+ " IS_DELETE=FALSE AND AN_RETURN_TYPE='ANX2' AND IS_PROCESSED=TRUE GROUP BY CUST_"
				+ "GSTIN,RETURN_PERIOD UNION ALL SELECT CUST_GSTIN , RETURN_"
				+ "PERIOD , -SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARD_SUPPLY , "
				+ "-SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)"
				+ " +IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)) "
				+ "AS TAX_AMOUNT , -SUM(IFNULL(AVAILABLE_IGST,0)+IFNULL("
				+ "AVAILABLE_CGST,0)+IFNULL(AVAILABLE_SGST,0) +IFNULL(AVAILABLE"
				+ "_CESS,0)) AS AVAILABLE_CREDIT FROM \"CLIENT1_GST\"."
				+ "\"ANX_INWARD_DOC_HEADER\" WHERE " + inQueryStr
				+ " AN_TABLE_SECTION IN "
				+ "('3B','3E','3F','3G') AND DOC_TYPE IN ('CR','RCR') AND "
				+ "IS_DELETE=FALSE AND AN_RETURN_TYPE='ANX2' AND IS_PROCESSED=TRUE GROUP BY CUST_"
				+ "GSTIN,RETURN_PERIOD UNION ALL SELECT CUST_GSTIN , RETURN_"
				+ "PERIOD , SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARD_SUPPLY , "
				+ "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)"
				+ " +IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0))"
				+ " AS TAX_AMOUNT , SUM(IFNULL(AVAILABLE_IGST,0)+IFNULL"
				+ "(AVAILABLE_CGST,0)+IFNULL(AVAILABLE_SGST,0) +IFNULL("
				+ "AVAILABLE_CESS,0)) AS AVAILABLE_CREDIT FROM \"CLIENT1_GST\""
				+ ".\"ANX_INWARD_DOC_HEADER\" WHERE " + inQueryStr
				+ " AN_TABLE_SECTION IN ('3H',"
				+ "'3I','3J','3K') AND DOC_TYPE IN ('INV','SLF','DR') AND IS_"
				+ "DELETE=FALSE AND AN_RETURN_TYPE='ANX1' AND IS_PROCESSED=TRUE GROUP BY CUST_GSTIN,"
				+ "RETURN_PERIOD UNION ALL SELECT CUST_GSTIN , RETURN_PERIOD ,"
				+ " -SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARD_SUPPLY , -SUM(IFNULL"
				+ "(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0) +IFNULL"
				+ "(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)) AS "
				+ "TAX_AMOUNT , -SUM(IFNULL(AVAILABLE_IGST,0)+IFNULL(AVAILABLE"
				+ "_CGST,0)+IFNULL(AVAILABLE_SGST,0) +IFNULL(AVAILABLE_CESS,0)) "
				+ "AS AVAILABLE_CREDIT FROM \"CLIENT1_GST\".\"ANX_INWARD_"
				+ "DOC_HEADER\" WHERE " + inQueryStr
				+ " AN_TABLE_SECTION IN ('3H','3I','3J','3K') "
				+ "AND DOC_TYPE IN ('CR') AND IS_DELETE=FALSE AND AN_RETURN_"
				+ "TYPE='ANX1' AND IS_PROCESSED=TRUE GROUP BY CUST_GSTIN,RETURN_PERIOD UNION ALL "
				+ "SELECT CUST_GSTIN,RETURN_PERIOD,SUM(IFNULL(TAXABLE_VALUE,0)) "
				+ "AS INWARD_SUPPLY ,NULL AS TAX_AMOUNT ,NULL AS AVAILABLE_"
				+ "CREDIT FROM \"CLIENT1_GST\".\"ANX_INWARD_DOC_HEADER\" WHERE "
				+ inQueryStr
				+ " SUPPLY_TYPE IN ('NON','EXT','NIL') AND DOC_TYPE IN ('INV',"
				+ "'SLF','DR','RNV','RDR') AND IS_DELETE=FALSE  AND IS_PROCESSED=TRUE GROUP BY CUST_"
				+ "GSTIN,RETURN_PERIOD UNION ALL SELECT CUST_GSTIN,RETURN_PERIOD"
				+ ",-SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARD_SUPPLY ,NULL AS "
				+ "TAX_AMOUNT ,NULL AS AVAILABLE_CREDIT FROM \"CLIENT1_GST\"."
				+ "\"ANX_INWARD_DOC_HEADER\" WHERE " + inQueryStr
				+ " SUPPLY_TYPE IN ('NON','EXT'"
				+ ",'NIL') AND DOC_TYPE IN ('CR','RCR') AND IS_DELETE=FALSE AND "
				+ "  IS_PROCESSED=TRUE GROUP BY CUST_GSTIN,RETURN_PERIOD  UNION ALL "
				+ " SELECT  CUST_GSTIN,RETURN_PERIOD,SUM(IFNULL(TAXABLE_VALUE,0)) AS  INWARD_SUPPLY ,"
				+ " SUM(IFNULL(IGST_AMT,0))+SUM(IFNULL(CGST_AMT,0)) +SUM(IFNULL(SGST_AMT,0)) "
				+ " + SUM(IFNULL(CESS_AMT,0)) AS TAX_AMOUNT "
				+ ",SUM(IFNULL(AVAIL_IGST,0)+IFNULL(AVAIL_SGST,0)+IFNULL(AVAIL_CGST,0)"	
                + "+IFNULL(AVAIL_CESS,0)) AS AVAILABLE_CREDIT "
				+ " FROM \"CLIENT1_GST\".\"ANX_PROCESSED_3H_3I\"  WHERE "
				+ inQuery3H3I + " IS_DELETE =FALSE "
				+ " GROUP BY   CUST_GSTIN,RETURN_PERIOD"
				+ " ) INW GROUP BY CUST_GSTIN"
				+ ",RETURN_PERIOD )INV INNER JOIN \"CLIENT1_GST\".\"GSTIN_INFO\""
				+ " GI ON GI.GSTIN=INV.GSTIN INNER JOIN \"CLIENT1_GST\"."
				+ "\"ENTITY_INFO\" EI ON EI.ID=GI.ENTITY_ID WHERE ";

		/*
		 * "SELECT GSTIN , RETURN_PERIOD , SUM(TAXABLE_VALUE) AS TAXABLE_" +
		 * "VALUE , SUM(OUTPUT_TAX) AS OUTPUT_TAX , SUM(INWARD_SUPPLY) " +
		 * "AS INWARD_SUPPLY , SUM(TAX_AMOUNT) AS TAX_AMOUNT , SUM(" +
		 * "AVAILABLE_CREDIT) AS AVAILABLE_CREDIT FROM ( SELECT " +
		 * "OUTR.GSTIN,OUTR.RETURN_PERIOD,OUTR.TAXABLE_VALUE,OUTPUT_TAX," +
		 * "0 AS INWARD_SUPPLY,0 AS TAX_AMOUNT,0 AS AVAILABLE_CREDIT FROM" +
		 * " ( SELECT SUPPLIER_GSTIN AS GSTIN,RETURN_PERIOD ,SUM(TAXABLE" +
		 * "_VALUE) AS TAXABLE_VALUE ,SUM(OUTPUT_TAX) AS OUTPUT_TAX FROM" +
		 * " ( SELECT SUPPLIER_GSTIN , RETURN_PERIOD , SUM(IFNULL(TAXABLE" +
		 * "_VALUE,0)) AS TAXABLE_VALUE , SUM(IFNULL(IGST_AMT,0)+IFNULL" +
		 * "(CGST_AMT,0)+IFNULL(SGST_AMT,0) +IFNULL(CESS_AMT_SPECIFIC,0)" +
		 * "+IFNULL(CESS_AMT_ADVALOREM,0)) AS OUTPUT_TAX FROM \"CLIENT1_" +
		 * "GST\".\"ANX_OUTWARD_DOC_HEADER\" WHERE " + outQueryStr +
		 * " AN_TABLE_SECTION IN " +
		 * "('3A','3B','3C','3D','3E','3F','3G') AND DOC_TYPE IN ('INV'," +
		 * "'DR','RNV','RDR') AND IS_DELETE=FALSE AND AN_RETURN_TYPE=" +
		 * "'ANX1' GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD UNION ALL SELECT" +
		 * " SUPPLIER_GSTIN , RETURN_PERIOD , -SUM(IFNULL(TAXABLE_VALUE," +
		 * "0)) AS TAXABLE_VALUE , -SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_" +
		 * "AMT,0)+IFNULL(SGST_AMT,0) +IFNULL(CESS_AMT_SPECIFIC,0)+" +
		 * "IFNULL(CESS_AMT_ADVALOREM,0)) AS OUTPUT_TAX FROM " +
		 * "\"CLIENT1_GST\".\"ANX_OUTWARD_DOC_HEADER\" WHERE " + outQueryStr +
		 * " AN_TABLE_" +
		 * "SECTION IN ('3A','3B','3C','3D','3E','3F','3G') AND DOC_TYPE" +
		 * " IN ('CR','RCR') AND IS_DELETE=FALSE AND AN_RETURN_TYPE='ANX1'" +
		 * " GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD UNION ALL SELECT " +
		 * "SUPPLIER_GSTIN , RETURN_PERIOD , SUM(IFNULL(TAXABLE_VALUE,0))" +
		 * " AS TAXABLE_VALUE , SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)" +
		 * "+IFNULL(SGST_AMT,0) +IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_" +
		 * "AMT_ADVALOREM,0)) AS OUTPUT_TAX FROM \"CLIENT1_GST\"." +
		 * "\"ANX_OUTWARD_DOC_HEADER\" WHERE " + outQueryStr +
		 * " AN_TABLE_SECTION='3C3' AND " +
		 * "IS_DELETE=FALSE AND AN_RETURN_TYPE='RET1' GROUP BY SUPPLIER_" +
		 * "GSTIN,RETURN_PERIOD UNION ALL SELECT SUPPLIER_GSTIN , RETURN_" +
		 * "PERIOD , -SUM(IFNULL(TAXABLE_VALUE,0)) AS TAXABLE_VALUE , " +
		 * "-SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)" +
		 * " +IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)) " +
		 * "AS OUTPUT_TAX FROM \"CLIENT1_GST\".\"ANX_OUTWARD_DOC_HEADER\" " +
		 * "WHERE" + outQueryStr +
		 * " AN_TABLE_SECTION='3C4' AND IS_DELETE=FALSE AND AN_" +
		 * "RETURN_TYPE='RET1' GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD " +
		 * "UNION ALL SELECT SUPPLIER_GSTIN , RETURN_PERIOD , SUM(IFNULL" +
		 * "(TAXABLE_VALUE,0)) AS TAXABLE_VALUE , SUM(IFNULL(IGST_AMT,0)" +
		 * "+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0) +IFNULL(CESS_AMT_" +
		 * "SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)) AS OUTPUT_TAX FROM" +
		 * " \"CLIENT1_GST\".\"ANX_OUTWARD_DOC_HEADER\" WHERE " + outQueryStr +
		 * " AN_TABLE_" +
		 * "SECTION IN ('3D1','3D2','3D3','3D4') AND IS_DELETE=FALSE AND" +
		 * " AN_RETURN_TYPE='RET1' GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD" +
		 * " )OT GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD )OUTR INNER JOIN " +
		 * "\"CLIENT1_GST\".\"GSTIN_INFO\" GI ON GI.GSTIN=OUTR.GSTIN " +
		 * "INNER JOIN \"CLIENT1_GST\".\"ENTITY_INFO\" EI ON EI.ID=GI." +
		 * "ENTITY_ID WHERE "+condition +
		 * " AND GI.IS_DELETE=FALSE AND EI.IS_DELETE=FALSE" +
		 * " UNION SELECT INV.GSTIN,INV.RETURN_PERIOD,0,0,INV.INWARD_" +
		 * "SUPPLY,INV.TAX_AMOUNT,INV.AVAILABLE_CREDIT FROM ( SELECT " +
		 * "CUST_GSTIN AS GSTIN,RETURN_PERIOD,SUM(INWARD_SUPPLY) AS " +
		 * "INWARD_SUPPLY ,SUM(TAX_AMOUNT) AS TAX_AMOUNT,SUM(AVAILABLE" +
		 * "_CREDIT) AS AVAILABLE_CREDIT FROM ( SELECT CUST_GSTIN , " +
		 * "RETURN_PERIOD , SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARD_SUPPLY" +
		 * " , SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT" +
		 * ",0) +IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)" +
		 * ") AS TAX_AMOUNT , SUM(IFNULL(AVAILABLE_IGST,0)+IFNULL(" +
		 * "AVAILABLE_CGST,0)+IFNULL(AVAILABLE_SGST,0) +IFNULL(AVAILABLE" +
		 * "_CESS,0)) AS AVAILABLE_CREDIT FROM \"CLIENT1_GST\"." +
		 * "\"ANX_INWARD_DOC_HEADER\" WHERE " + inQueryStr +
		 * " AN_TABLE_SECTION IN ('3B'," +
		 * "'3E','3F','3G') AND DOC_TYPE IN ('INV','DR','RNV','RDR') AND" +
		 * " IS_DELETE=FALSE AND AN_RETURN_TYPE='ANX2' GROUP BY CUST_" +
		 * "GSTIN,RETURN_PERIOD UNION ALL SELECT CUST_GSTIN , RETURN_" +
		 * "PERIOD , -SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARD_SUPPLY , " +
		 * "-SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)" +
		 * " +IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)) " +
		 * "AS TAX_AMOUNT , -SUM(IFNULL(AVAILABLE_IGST,0)+IFNULL(" +
		 * "AVAILABLE_CGST,0)+IFNULL(AVAILABLE_SGST,0) +IFNULL(AVAILABLE" +
		 * "_CESS,0)) AS AVAILABLE_CREDIT FROM \"CLIENT1_GST\"." +
		 * "\"ANX_INWARD_DOC_HEADER\" WHERE " + inQueryStr +
		 * " AN_TABLE_SECTION IN " +
		 * "('3B','3E','3F','3G') AND DOC_TYPE IN ('CR','RCR') AND " +
		 * "IS_DELETE=FALSE AND AN_RETURN_TYPE='ANX2' GROUP BY CUST_" +
		 * "GSTIN,RETURN_PERIOD UNION ALL SELECT CUST_GSTIN , RETURN_" +
		 * "PERIOD , SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARD_SUPPLY , " +
		 * "SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)" +
		 * " +IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0))" +
		 * " AS TAX_AMOUNT , SUM(IFNULL(AVAILABLE_IGST,0)+IFNULL" +
		 * "(AVAILABLE_CGST,0)+IFNULL(AVAILABLE_SGST,0) +IFNULL(" +
		 * "AVAILABLE_CESS,0)) AS AVAILABLE_CREDIT FROM \"CLIENT1_GST\"" +
		 * ".\"ANX_INWARD_DOC_HEADER\" WHERE " + inQueryStr +
		 * " AN_TABLE_SECTION IN ('3H'," +
		 * "'3I','3J','3K') AND DOC_TYPE IN ('INV','SLF','DR') AND IS_" +
		 * "DELETE=FALSE AND AN_RETURN_TYPE='ANX1' GROUP BY CUST_GSTIN," +
		 * "RETURN_PERIOD UNION ALL SELECT CUST_GSTIN , RETURN_PERIOD ," +
		 * " -SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARD_SUPPLY , -SUM(IFNULL" +
		 * "(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0) +IFNULL" +
		 * "(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0)) AS " +
		 * "TAX_AMOUNT , -SUM(IFNULL(AVAILABLE_IGST,0)+IFNULL(AVAILABLE" +
		 * "_CGST,0)+IFNULL(AVAILABLE_SGST,0) +IFNULL(AVAILABLE_CESS,0)) " +
		 * "AS AVAILABLE_CREDIT FROM \"CLIENT1_GST\".\"ANX_INWARD_" +
		 * "DOC_HEADER\" WHERE " + inQueryStr +
		 * " AN_TABLE_SECTION IN ('3H','3I','3J','3K') " +
		 * "AND DOC_TYPE IN ('CR') AND IS_DELETE=FALSE AND AN_RETURN_" +
		 * "TYPE='ANX1' GROUP BY CUST_GSTIN,RETURN_PERIOD UNION ALL " +
		 * "SELECT CUST_GSTIN,RETURN_PERIOD,SUM(IFNULL(TAXABLE_VALUE,0)) " +
		 * "AS INWARD_SUPPLY ,NULL AS TAX_AMOUNT ,NULL AS AVAILABLE_" +
		 * "CREDIT FROM \"CLIENT1_GST\".\"ANX_INWARD_DOC_HEADER\" WHERE " +
		 * inQueryStr +
		 * " SUPPLY_TYPE IN ('NON','EXT','NIL') AND DOC_TYPE IN ('INV',"
		 * +"'SLF','DR','RNV','RDR') AND IS_DELETE=FALSE GROUP BY CUST_" +
		 * "GSTIN,RETURN_PERIOD UNION ALL SELECT CUST_GSTIN,RETURN_PERIOD" +
		 * ",-SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARD_SUPPLY ,NULL AS " +
		 * "TAX_AMOUNT ,NULL AS AVAILABLE_CREDIT FROM \"CLIENT1_GST\"." +
		 * "\"ANX_INWARD_DOC_HEADER\" WHERE SUPPLY_TYPE IN ('NON','EXT'" +
		 * ",'NIL') AND DOC_TYPE IN ('CR','RCR') AND IS_DELETE=FALSE " +
		 * "GROUP BY CUST_GSTIN,RETURN_PERIOD ) INW GROUP BY CUST_GSTIN" +
		 * ",RETURN_PERIOD )INV INNER JOIN \"CLIENT1_GST\".\"GSTIN_INFO\"" +
		 * " GI ON GI.GSTIN=INV.GSTIN INNER JOIN \"CLIENT1_GST\"." +
		 * "\"ENTITY_INFO\" EI ON EI.ID=GI.ENTITY_ID WHERE ";
		 */

	}

	private String createQueryString(List<Long> entityId,
			List<String> outGstinList, List<String> inGstinList,
			String initialTaxPeriod, Map<String, List<String>> outDataSecAttrs,
			Map<String, List<String>> inDataSecAttrs, String profitCenter,
			String sgstin, String cgstin, String plant, String division,
			String location, String sales, String purchase, String distChannel,
			String ud1, String ud2, String ud3, String ud4, String ud5,
			String ud6, List<String> outPcList, List<String> outPlantList,
			List<String> outSalesList, List<String> outDivisionList,
			List<String> outLocationList, List<String> outDistList,
			List<String> outUd1List, List<String> outUd2List,
			List<String> outUd3List, List<String> outUd4List,
			List<String> outUd5List, List<String> outUd6List,
			List<String> inPcList, List<String> inPlantList,
			List<String> inDivisionList, List<String> inLocationList,
			List<String> inPurcList, List<String> inUd1List,
			List<String> inUd2List, List<String> inUd3List,
			List<String> inUd4List, List<String> inUd5List,
			List<String> inUd6List) {

		if (outDataSecAttrs != null && !outDataSecAttrs.isEmpty()
				&& outDataSecAttrs.size() > 0) {
			for (String key : outDataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
				}
				if (key.equalsIgnoreCase("D")) {
					division = key;
				}
				if (key.equalsIgnoreCase("L")) {
					location = key;
				}
				if (key.equalsIgnoreCase("SO")) {
					sales = key;
				}
				if (key.equalsIgnoreCase("DC")) {
					distChannel = key;
				}
				if (key.equalsIgnoreCase("UD1")) {
					ud1 = key;
				}
				if (key.equalsIgnoreCase("UD2")) {
					ud2 = key;
				}
				if (key.equalsIgnoreCase("UD3")) {
					ud3 = key;
				}
				if (key.equalsIgnoreCase("UD4")) {
					ud4 = key;
				}
				if (key.equalsIgnoreCase("UD5")) {
					ud5 = key;
				}
				if (key.equalsIgnoreCase("UD6")) {
					ud6 = key;
				}
				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		if (inDataSecAttrs != null && !inDataSecAttrs.isEmpty()
				&& inDataSecAttrs.size() > 0) {
			for (String key : inDataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
				}
				if (key.equalsIgnoreCase("D")) {
					division = key;
				}
				if (key.equalsIgnoreCase("L")) {
					location = key;
				}
				if (key.equalsIgnoreCase("PO")) {
					purchase = key;
				}
				if (key.equalsIgnoreCase("UD1")) {
					ud1 = key;
				}
				if (key.equalsIgnoreCase("UD2")) {
					ud2 = key;
				}
				if (key.equalsIgnoreCase("UD3")) {
					ud3 = key;
				}
				if (key.equalsIgnoreCase("UD4")) {
					ud4 = key;
				}
				if (key.equalsIgnoreCase("UD5")) {
					ud5 = key;
				}
				if (key.equalsIgnoreCase("UD6")) {
					ud6 = key;
				}
				if (key.equalsIgnoreCase("GSTIN")) {
					cgstin = key;
				}
			}
		}

		StringBuilder outwardBuilder = new StringBuilder();
		StringBuilder inwardBuilder = new StringBuilder();
		StringBuilder outwardBuilderB2C = new StringBuilder();
		StringBuilder inwardBuilder3H3I = new StringBuilder();
		if (outGstinList != null && outGstinList.size() > 0) {
			outwardBuilder.append(" SUPPLIER_GSTIN IN :sgstins AND");
			outwardBuilderB2C.append(" SUPPLIER_GSTIN IN :sgstins AND");
		}
		if (inGstinList != null && inGstinList.size() > 0) {
			inwardBuilder.append("  CUST_GSTIN IN :cgstins AND");
			inwardBuilder3H3I.append(" CUST_GSTIN IN :cgstins AND");
		}
		if (profitCenter != null && !profitCenter.isEmpty() && outPcList != null
				&& outPcList.size() > 0 && inPcList != null
				&& inPcList.size() > 0) {
			outwardBuilder.append(" PROFIT_CENTRE IN :outPcList AND");
			outwardBuilderB2C.append(" PROFIT_CENTER IN :outPcList AND");
			inwardBuilder.append("  PROFIT_CENTRE IN :inPcList AND");
			inwardBuilder3H3I.append(" PROFIT_CENTER IN :inPcList AND");
			
			
		}
		if (plant != null && !plant.isEmpty() && outPlantList != null
				&& outPlantList.size() > 0 && inPlantList != null
				&& inPlantList.size() > 0) {
			outwardBuilder.append("  PLANT_CODE IN :outPlantList AND");
			outwardBuilderB2C.append(" PLANT IN :outPlantList AND");
			inwardBuilder.append("  PLANT_CODE IN :inPlantList AND");
			inwardBuilder3H3I.append(" PLANT IN :inPlantList AND");
			
		}
		if (sales != null && !sales.isEmpty() && outSalesList != null
				&& outSalesList.size() > 0) {
			outwardBuilder.append(" SALES_ORGANIZATION IN :outSalesList AND");
			outwardBuilderB2C.append(" SALES_ORG IN :outSalesList AND");
		}
		if (distChannel != null && !distChannel.isEmpty() && outDistList != null
				&& outDistList.size() > 0) {
			outwardBuilder.append(" DISTRIBUTION_CHANNEL IN :outDistList AND");
			outwardBuilderB2C.append(" DISTRIBUTION_CHANNEL IN :outDistList AND");
		}
		if (division != null && !division.isEmpty() && outDivisionList != null
				&& outDivisionList.size() > 0 && inDivisionList != null
				&& inDivisionList.size() > 0) {
			outwardBuilder.append(" DIVISION IN :outDivisionList AND");
			outwardBuilderB2C.append(" DIVISION IN :outDivisionList AND");
			inwardBuilder.append("  DIVISION IN :inDivisionList AND");
			inwardBuilder3H3I.append(" DIVISION IN :inDivisionList AND");
		}
		if (location != null && !location.isEmpty() && outLocationList != null
				&& outLocationList.size() > 0 && inLocationList != null
				&& inLocationList.size() > 0) {
			outwardBuilder.append("  LOCATION IN :outLocationList AND");
			outwardBuilderB2C.append(" LOCATION IN :outLocationList AND");
			inwardBuilder.append(" LOCATION IN :inLocationList AND");
			inwardBuilder3H3I.append(" LOCATION IN :inLocationList AND");
		}
		if (purchase != null && !purchase.isEmpty() && inPurcList != null
				&& inPurcList.size() > 0) {
			inwardBuilder.append(" PURCHASE_ORGANIZATION IN :inPurcList AND");
			inwardBuilder3H3I.append(" PURCHAGE_ORG IN :inPurcList AND");
		}
		if (ud1 != null && !ud1.isEmpty() && outUd1List != null
				&& outUd1List.size() > 0 && inUd1List != null
				&& inUd1List.size() > 0) {
			outwardBuilder.append(" USERACCESS1 IN :outUd1List AND");
			outwardBuilderB2C.append(" USER_ACCESS1 IN :outUd1List AND");
			inwardBuilder.append("  USERACCESS1 IN :inUd1List AND");
			inwardBuilder3H3I.append(" USER_ACCESS1 IN :inUd1List AND");
		}
		if (ud2 != null && !ud2.isEmpty() && outUd2List != null
				&& outUd2List.size() > 0 && inUd2List != null
				&& inUd2List.size() > 0) {
			outwardBuilder.append("  USERACCESS2 IN :outUd2List AND");
			outwardBuilderB2C.append(" USER_ACCESS2 IN :outUd2List AND");
			inwardBuilder.append("  USERACCESS2 IN :inUd2List AND");
			inwardBuilder3H3I.append(" USER_ACCESS2 IN :inUd2List AND");
		}
		if (ud3 != null && !ud3.isEmpty() && outUd3List != null
				&& outUd3List.size() > 0 && inUd3List != null
				&& inUd3List.size() > 0) {
			outwardBuilder.append(" USERACCESS3 IN :outUd3List AND");
			outwardBuilderB2C.append(" USER_ACCESS3 IN :outUd3List AND");
			inwardBuilder.append(" USERACCESS3 IN :inUd3List AND");
			inwardBuilder3H3I.append(" USER_ACCESS3 IN :inUd3List AND");
		}
		if (ud4 != null && !ud4.isEmpty() && outUd4List != null
				&& outUd4List.size() > 0 && inUd4List != null
				&& inUd4List.size() > 0) {
			outwardBuilder.append(" USERACCESS4 IN :outUd4List AND");
			outwardBuilderB2C.append(" USER_ACCESS4 IN :outUd4List AND");
			inwardBuilder.append(" USERACCESS4 IN :inUd4List AND");
			inwardBuilder3H3I.append(" USER_ACCESS4 IN :inUd4List AND");
		}
		if (ud5 != null && !ud5.isEmpty() && outUd5List != null
				&& outUd5List.size() > 0 && inUd5List != null
				&& inUd5List.size() > 0) {
			outwardBuilder.append("  USERACCESS5 IN :outUd5List AND");
			outwardBuilderB2C.append(" USER_ACCESS5 IN :outUd5List AND");
			inwardBuilder.append("  USERACCESS5 IN :inUd5List AND");
			inwardBuilder3H3I.append(" USER_ACCESS5 IN :inUd5List AND");
		}
		if (ud6 != null && !ud6.isEmpty() && outUd6List != null
				&& outUd6List.size() > 0 && inUd6List != null
				&& inUd6List.size() > 0) {
			outwardBuilder.append(" USERACCESS6 IN :outUd6List AND");
			outwardBuilderB2C.append(" USER_ACCESS6 IN :outUd6List AND");
			inwardBuilder.append("  USERACCESS6 IN :inUd6List AND");
			inwardBuilder3H3I.append(" USER_ACCESS6 IN :inUd6List AND");
		}

		String outCondition = outwardBuilder.toString();
		String outConditionB2c = outwardBuilderB2C.toString();
		String inCondition = inwardBuilder.toString();
		String inCondition3h3i = inwardBuilder3H3I.toString();

		return outCondition + "#@" + outConditionB2c + "#@" +inCondition + "#@" 
				+ inCondition3h3i;

	}

}
