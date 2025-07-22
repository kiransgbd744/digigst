/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusReqDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestDataHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestDataSummaryHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestItemDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusResultDto;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Hemasundar.J
 *
 */
@Service("AnxDataStatusDataFetcherForErpImpl")
public class AnxDataStatusDataFetcherForErpImpl
		implements AnxDataStatusDataFetcherForErp {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamsCheck;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AnxDataStatusDataFetcherForErpImpl.class);

	@Autowired
	private GSTNDetailRepository gstinRepo;

	@Override
	public List<Object[]> findDataStatusApiSummary(AnxDataStatusReqDto req) {

		LocalDate recivedFromDate = req.getDataRecvFrom();
		LocalDate recivedToDate = req.getDataRecvTo();
		String dataType = req.getDataType();
		String gstin = req.getGstin();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside {} query method with search params {}",
					dataType, req);
		}
		if ((recivedFromDate != null && recivedFromDate.lengthOfYear() > 0)
				&& (recivedToDate != null
						&& recivedToDate.lengthOfYear() > 0)) {

			String apiQueryStr = createApiQueryString(dataType);
			Query q = entityManager.createNativeQuery(apiQueryStr);
			// q.setParameter("recivedFromDate", recivedFromDate);
			// q.setParameter("recivedToDate", recivedToDate);
			q.setParameter("gstin", gstin);
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			LOGGER.debug("{} data Query execution ended ", list);
			return list;
		}
		return null;

	}

	String prevGstin = null;
	List<AnxDataStatusResultDto> list = null;

	// GSTNDetailEntity gstinEntity = new GSTNDetailEntity();
	@Override
	public List<AnxDataStatusResultDto> convertGstinWise(String entityPan,
			String entityName, List<Object[]> arrs, String companyCode) {

		// List<List<AnxDataStatusResultDto>> listOfDdtos = new ArrayList<>();
		list = new ArrayList<>();
		if (!arrs.isEmpty() && arrs.size() > 0) {
			Object[] firstObj = arrs.get(0);
			prevGstin = String.valueOf(firstObj[11]);
		}

		// gstinEntity = gstinRepo.findByGstinAndIsDeleteFalse(prevGstin);
		arrs.forEach(arr -> {

			String retPeriod = arr[10] != null ? String.valueOf(arr[10]) : null;
			String gstin = arr[11] != null ? String.valueOf(arr[11]) : null;
			String division = arr[12] != null ? String.valueOf(arr[12]) : null;
			String profitCentre = arr[13] != null ? String.valueOf(arr[13])
					: null;
			String location = arr[14] != null ? String.valueOf(arr[14]) : null;
			String plantcode = arr[15] != null ? String.valueOf(arr[15]) : null;
			String salesOrg = arr[16] != null ? String.valueOf(arr[16]) : null;
			String purchaseOrg = arr[17] != null ? String.valueOf(arr[17])
					: null;
			String distributionChannel = arr[18] != null
					? String.valueOf(arr[18]) : null;
			String userAccess1 = arr[19] != null ? String.valueOf(arr[19])
					: null;
			String userAccess2 = arr[20] != null ? String.valueOf(arr[20])
					: null;
			String userAccess3 = arr[21] != null ? String.valueOf(arr[21])
					: null;
			String userAccess4 = arr[22] != null ? String.valueOf(arr[22])
					: null;
			String userAccess5 = arr[23] != null ? String.valueOf(arr[23])
					: null;
			String userAccess6 = arr[24] != null ? String.valueOf(arr[24])
					: null;

			if (!prevGstin.equals(gstin)) {

				// listOfDdtos.add(list);
				// list = new ArrayList<>();
				prevGstin = gstin;
				// gstinEntity = gstinRepo.findByGstinAndIsDeleteFalse(gstin);
			}
			AnxDataStatusResultDto obj = new AnxDataStatusResultDto();
			list.add(obj);

			// Long entityId = gstinEntity.getEntityId();

			// Entity at ERP is Company code but in ASP it is PAN
			obj.setEntity(entityPan);
			obj.setEntityName(entityName);
			obj.setCompanyCode(companyCode != null ? companyCode : "");
			java.sql.Date sqlRecieveDate = (java.sql.Date) arr[0];

			obj.setReceivedDate(
					arr[0] != null ? sqlRecieveDate.toLocalDate() : null);

			obj.setSapTotal(arr[1] != null
					? Integer.parseInt(String.valueOf(arr[1])) : null);

			obj.setTotalRecords(arr[2] != null
					? Integer.parseInt(String.valueOf(arr[2])) : null);

			obj.setSapTotal(arr[3] != null
					? Integer.parseInt(String.valueOf(arr[3])) : null);

			obj.setProcessedActive(arr[4] != null
					? Integer.parseInt(String.valueOf(arr[4])) : null);

			obj.setProcessedInactive(arr[5] != null
					? Integer.parseInt(String.valueOf(arr[5])) : null);

			obj.setErrorActive(arr[6] != null
					? Integer.parseInt(String.valueOf(arr[6])) : null);

			obj.setErrorInactive(arr[7] != null
					? Integer.parseInt(String.valueOf(arr[7])) : null);

			obj.setInfoActive(arr[8] != null
					? Integer.parseInt(String.valueOf(arr[8])) : null);

			obj.setInfoInactive(arr[9] != null
					? Integer.parseInt(String.valueOf(arr[9])) : null);

			obj.setGstin(gstin);
			obj.setRetPeriod(retPeriod);
			obj.setDivision(division);
			obj.setProfitCentre(profitCentre);
			obj.setLocation(location);
			obj.setPlantcode(plantcode);
			obj.setSalesOrg(salesOrg);
			obj.setPurchaseOrg(purchaseOrg);
			obj.setDistributionChannel(distributionChannel);
			obj.setUserAccess1(userAccess1);
			obj.setUserAccess2(userAccess2);
			obj.setUserAccess3(userAccess3);
			obj.setUserAccess4(userAccess4);
			obj.setUserAccess5(userAccess5);
			obj.setUserAccess6(userAccess6);

		});
		return list;
	}

	private String createApiQueryString(String dataType) {

		String apiQueryStr = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Data Query Execution BEGIN", dataType);
		}

		if (dataType.equalsIgnoreCase(APIConstants.OUTWARD)) {

			apiQueryStr = "SELECT  RECEIVED_DATE AS RECEIVED_DATE,"
					+ "0 as SAPTOTAL,count(1) as TOTALRECORDS,"
					+ "0 as DIFFERENCE_INCOUNT,"
					+ "COUNT(case when IS_PROCESSED = TRUE AND "
					+ "IS_DELETE = FALSE then 1 else NULL END) "
					+ "AS ACT_PROCESSED,COUNT(case when "
					+ "IS_PROCESSED = TRUE AND IS_DELETE = TRUE then "
					+ "1 else NULL END) AS INACT_PROCESSED,"
					+ "COUNT(case when IS_ERROR = TRUE AND "
					+ "IS_DELETE = FALSE then 1 else NULL END) AS ACT_ERRORS,"
					+ "COUNT(case when IS_ERROR = TRUE AND "
					+ "IS_DELETE = TRUE then 1 else NULL END) "
					+ "AS INACT_ERRORS,COUNT(case when "
					+ "IS_INFORMATION = TRUE AND IS_PROCESSED = "
					+ "TRUE AND IS_DELETE = FALSE then 1 else NULL END)AS "
					+ "ACT_INFORMATION,COUNT(case when "
					+ "IS_INFORMATION = TRUE AND IS_PROCESSED = TRUE AND "
					+ "IS_DELETE = TRUE then 1 else NULL END) "
					+ "as INACT_INFORMATION,"

					+ " RETURN_PERIOD," + " SUPPLIER_GSTIN," + " DIVISION,"
					+ " PROFIT_CENTRE," + " LOCATION," + " PLANT_CODE,"
					+ " SALES_ORGANIZATION," + "NULL AS PURCHASE_ORGANIZATION,"
					+ " DISTRIBUTION_CHANNEL," + " USERACCESS1,"
					+ " USERACCESS2," + " USERACCESS3," + " USERACCESS4,"
					+ " USERACCESS5," + " USERACCESS6 "

					+ "FROM ANX_OUTWARD_DOC_HEADER   "
					+ "WHERE  DATAORIGINTYPECODE IN ('A','AI') AND "
					+ "  SUPPLIER_GSTIN = :gstin " + "GROUP BY  RECEIVED_DATE,"

					+ " RETURN_PERIOD," + " SUPPLIER_GSTIN," + " DIVISION,"
					+ " PROFIT_CENTRE," + " LOCATION," + " PLANT_CODE,"
					+ " SALES_ORGANIZATION," + " DISTRIBUTION_CHANNEL,"
					+ " USERACCESS1," + " USERACCESS2," + " USERACCESS3,"
					+ " USERACCESS4," + " USERACCESS5," + " USERACCESS6 "

					+ " ORDER BY  RECEIVED_DATE DESC";

		} else if (dataType.equalsIgnoreCase(APIConstants.INWARD)) {
			apiQueryStr = "SELECT  RECEIVED_DATE AS RECEIVED_DATE,"
					+ "0 as SAPTOTAL,count(1) as TOTALRECORDS,"
					+ "0 as DIFFERENCE_INCOUNT,"
					+ "COUNT(case when IS_PROCESSED = TRUE AND IS_DELETE = "
					+ "FALSE then 1 else NULL END) AS ACT_PROCESSED,"
					+ "COUNT(case when IS_PROCESSED = TRUE AND IS_DELETE = "
					+ "TRUE then 1 else NULL END) AS INACT_PROCESSED,"
					+ "COUNT(case when IS_ERROR = TRUE AND IS_DELETE = "
					+ "FALSE then 1 else NULL END) AS ACT_ERRORS,"
					+ "COUNT(case when IS_ERROR = TRUE AND IS_DELETE = "
					+ "TRUE then 1 else NULL END) AS INACT_ERRORS,"
					+ "COUNT(case when IS_INFORMATION = TRUE AND IS_PROCESSED ="
					+ " TRUE AND IS_DELETE = FALSE then 1 else NULL END) "
					+ "AS ACT_INFORMATION,COUNT(case when IS_INFORMATION = "
					+ "TRUE AND IS_PROCESSED = TRUE AND IS_DELETE = TRUE then "
					+ "1 else NULL END) as INACT_INFORMATION,"

					+ " RETURN_PERIOD," + " CUST_GSTIN," + " DIVISION,"
					+ " PROFIT_CENTRE," + " LOCATION," + " PLANT_CODE,"
					+ "NULL AS SALES_ORGANIZATION," + " PURCHASE_ORGANIZATION,"
					+ "NULL AS DISTRIBUTION_CHANNEL," + " USERACCESS1,"
					+ " USERACCESS2," + " USERACCESS3," + " USERACCESS4,"
					+ " USERACCESS5," + " USERACCESS6 "

					+ "FROM ANX_INWARD_DOC_HEADER   "
					+ "WHERE  DATAORIGINTYPECODE IN ('A','AI') AND "
					+ "  CUST_GSTIN = :gstin " + "GROUP BY  RECEIVED_DATE,"

					+ " RETURN_PERIOD," + " CUST_GSTIN," + " DIVISION,"
					+ " PROFIT_CENTRE," + " LOCATION," + " PLANT_CODE,"
					+ " PURCHASE_ORGANIZATION," + " USERACCESS1,"
					+ " USERACCESS2," + " USERACCESS3," + " USERACCESS4,"
					+ " USERACCESS5," + " USERACCESS6 "

					+ " ORDER BY  RECEIVED_DATE DESC";

		} else if (dataType.equalsIgnoreCase(APIConstants.OUTWARD_SUMMARY)) {

			apiQueryStr = "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,RECEIVED_DATE,"
					+ "RETURN_SECTION,RETURN_TYPE,SUM(CNT) CNT,SUM(TAXABLE"
					+ "_VALUE) TAXABLE_VALUE,SUM(TOTAL_TAX) TOTAL_TAX,"
					+ "SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,"
					+ "SUM(CESS) CESS,DOC_DATE,DOC_TYPE,DIVISION,PROFIT_CENTRE,"
					+ "LOCATION,PLANT_CODE,SALES_ORGANIZATION,"
					+ "DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,"
					+ "USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6"
					+ " FROM(SELECT  SUPPLIER_GSTIN, "
					+ " RETURN_PERIOD, RECEIVED_DATE,  AN_TAX_DOC_TYPE"
					+ " ||' -'||  AN_TABLE_SECTION RETURN_SECTION,"
					+ "AN_RETURN_TYPE AS RETURN_TYPE, COUNT( ID) AS CNT,"
					+ "SUM( TAXABLE_VALUE) AS TAXABLE_VALUE,SUM( IGST_AMT"
					+ "+ CGST_AMT+ SGST_AMT+  CESS_AMT_SPECIFIC + "
					+ " CESS_AMT_ADVALOREM) AS TOTAL_TAX,SUM( IGST_AMT)"
					+ " AS IGST,SUM( CGST_AMT) AS CGST,SUM( SGST_AMT) AS"
					+ " SGST,SUM( CESS_AMT_SPECIFIC +  CESS_AMT_ADVALOREM) "
					+ "AS CESS, DOC_DATE, DOC_TYPE,"

					+ " DIVISION," + " PROFIT_CENTRE," + " LOCATION,"
					+ " PLANT_CODE," + " SALES_ORGANIZATION,"
					+ " DISTRIBUTION_CHANNEL," + " USERACCESS1,"
					+ " USERACCESS2," + " USERACCESS3," + " USERACCESS4,"
					+ " USERACCESS5," + " USERACCESS6 "

					+ "FROM ANX_OUTWARD_DOC_HEADER   "
					+ " WHERE  DATAORIGINTYPECODE IN ('A','AI')"
					+ "AND  IS_PROCESSED=TRUE AND  IS_DELETE=FALSE"

					+ " AND  IS_DELETE=FALSE AND" + "  SUPPLIER_GSTIN = :gstin "

					+ "GROUP BY  SUPPLIER_GSTIN, RETURN_PERIOD,"
					+ "  DOC_DATE, AN_TABLE_SECTION,  AN_TAX_DOC_TYPE,"
					+ " AN_RETURN_TYPE, RECEIVED_DATE,"
					+ " TABLE_SECTION,  TAX_DOC_TYPE, RETURN_TYPE,"
					+ " DOC_TYPE,"

					+ " DIVISION," + " PROFIT_CENTRE," + " LOCATION,"
					+ " PLANT_CODE," + " SALES_ORGANIZATION,"
					+ " DISTRIBUTION_CHANNEL," + " USERACCESS1,"
					+ " USERACCESS2," + " USERACCESS3," + " USERACCESS4,"
					+ " USERACCESS5," + " USERACCESS6 "

					+ " UNION "

					+ "SELECT  SUPPLIER_GSTIN,  RETURN_PERIOD, RECEIVED_DATE, "
					+ " TAX_DOC_TYPE ||' -'||  TABLE_SECTION AS RETURN_SECTION,"
					+ " RETURN_TYPE  AS RETURN_TYPE,COUNT( ID) AS CNT,"
					+ "SUM( TAXABLE_VALUE) AS TAXABLE_VALUE,SUM( IGST_AMT"
					+ "+ CGST_AMT+ SGST_AMT+  CESS_AMT_SPECIFIC +"
					+ "  CESS_AMT_ADVALOREM) AS TOTAL_TAX,SUM( IGST_AMT)"
					+ " AS IGST,SUM( CGST_AMT) AS CGST,SUM( SGST_AMT) AS"
					+ " SGST,SUM( CESS_AMT_SPECIFIC +  CESS_AMT_ADVALOREM) "
					+ "AS CESS, DOC_DATE, DOC_TYPE,"

					+ " DIVISION," + " PROFIT_CENTRE," + " LOCATION,"
					+ " PLANT_CODE," + " SALES_ORGANIZATION,"
					+ " DISTRIBUTION_CHANNEL," + " USERACCESS1,"
					+ " USERACCESS2," + " USERACCESS3," + " USERACCESS4,"
					+ " USERACCESS5," + " USERACCESS6 "

					+ " FROM ANX_OUTWARD_DOC_HEADER  " + "  WHERE"
					+ "  DATAORIGINTYPECODE IN ('A','AI') AND  IS_PROCESSED=TRUE"

					+ " AND  IS_DELETE=FALSE AND "
					+ "  SUPPLIER_GSTIN = :gstin "

					+ "  GROUP BY   SUPPLIER_GSTIN, RETURN_PERIOD,"
					+ "  DOC_DATE, AN_TABLE_SECTION,  AN_TAX_DOC_TYPE,"
					+ " AN_RETURN_TYPE, RECEIVED_DATE, TABLE_SECTION, "
					+ " TAX_DOC_TYPE, RETURN_TYPE, DOC_TYPE,"

					+ " DIVISION," + " PROFIT_CENTRE," + " LOCATION,"
					+ " PLANT_CODE," + " SALES_ORGANIZATION,"
					+ " DISTRIBUTION_CHANNEL," + " USERACCESS1,"
					+ " USERACCESS2," + " USERACCESS3," + " USERACCESS4,"
					+ " USERACCESS5," + " USERACCESS6 "

					+ " ORDER BY " + " SUPPLIER_GSTIN, RETURN_PERIOD)"
					+ "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,RECEIVED_DATE,"
					+ "RETURN_SECTION,RETURN_TYPE,DOC_DATE,DOC_TYPE,"
					+ "DIVISION," + "PROFIT_CENTRE," + "LOCATION,"
					+ "PLANT_CODE," + "SALES_ORGANIZATION,"
					+ "DISTRIBUTION_CHANNEL," + "USERACCESS1," + "USERACCESS2,"
					+ "USERACCESS3," + "USERACCESS4," + "USERACCESS5,"
					+ "USERACCESS6 "
					+ " ORDER BY SUPPLIER_GSTIN,RETURN_PERIOD,RETURN_TYPE";

		} else if (dataType.equalsIgnoreCase(APIConstants.INWARD_SUMMARY)) {
			apiQueryStr = "SELECT  CUST_GSTIN,"
					+ " RETURN_PERIOD, RECEIVED_DATE,"
					+ " AN_TAX_DOC_TYPE ||'-'||  AN_TABLE_SECTION AS"
					+ " RETURN_SECTION,(CASE "
					+ "WHEN  AN_RETURN_TYPE='ANX2' THEN 'ANX2' END)"
					+ " AS RETURN_TYPE,COUNT( ID) AS COUNT,"
					+ "SUM( TAXABLE_VALUE) AS TAXABLE_VALUE,SUM( IGST_AMT+"
					+ " CGST_AMT+ SGST_AMT+ CESS_AMT_SPECIFIC + "
					+ " CESS_AMT_ADVALOREM) AS TOTAL_TAX,"
					+ "SUM( IGST_AMT) AS IGST,SUM( CGST_AMT) AS CGST,"
					+ "SUM( SGST_AMT) AS SGST,SUM( CESS_AMT_SPECIFIC + "
					+ " CESS_AMT_ADVALOREM) AS CESS, DOC_DATE, DOC_TYPE,"

					+ " DIVISION," + " PROFIT_CENTRE," + " LOCATION,"
					+ " PLANT_CODE," + " PURCHASE_ORGANIZATION, USERACCESS1,"
					+ " USERACCESS2," + " USERACCESS3," + " USERACCESS4,"
					+ " USERACCESS5," + " USERACCESS6 "

					+ " FROM ANX_INWARD_DOC_HEADER   "
					+ " WHERE  DATAORIGINTYPECODE IN ('A','AI') AND  IS_PROCESSED=TRUE "
					+ " AND  IS_DELETE=FALSE  "

					+ " AND  IS_DELETE=FALSE AND" + "  SUPPLIER_GSTIN = :gstin "

					+ " GROUP BY  CUST_GSTIN, "
					+ "  RETURN_PERIOD, DOC_DATE, AN_TABLE_SECTION,"
					+ "  AN_TAX_DOC_TYPE, AN_RETURN_TYPE, RECEIVED_DATE,"
					+ "  DOC_TYPE,"

					+ " DIVISION," + " PROFIT_CENTRE," + " LOCATION,"
					+ " PLANT_CODE," + " PURCHASE_ORGANIZATION, USERACCESS1,"
					+ " USERACCESS2," + " USERACCESS3," + " USERACCESS4,"
					+ " USERACCESS5," + " USERACCESS6 ";
		}
		LOGGER.debug("Outward query from database is--> ", apiQueryStr);
		return apiQueryStr;
	}

	@Override
	public AnxDataStatusRequestDataSummaryHeaderDto convertDataToOutwarSummary(
			String entityPan, String entityName, List<Object[]> outwordSummary,
			Long entityId, String companyCode) {

		AnxDataStatusRequestDataSummaryHeaderDto headerDto = new AnxDataStatusRequestDataSummaryHeaderDto();
		List<AnxDataStatusRequestItemDto> subItmList = new ArrayList<>();
		outwordSummary.forEach(arr -> {
			AnxDataStatusRequestItemDto itemDto = new AnxDataStatusRequestItemDto();
			String gstin = String.valueOf(arr[0]);
			String retPeriod = arr[1] != null ? String.valueOf(arr[1]) : null;
			String date = arr[2] != null ? String.valueOf(arr[2]) : null;
			String retSection = arr[3] != null ? String.valueOf(arr[3]) : null;
			String retType = arr[4] != null ? String.valueOf(arr[4]) : null;
			BigInteger tCount = GenUtil.getBigInteger(arr[5]);
			BigDecimal tValue = (BigDecimal) arr[6];
			BigDecimal totalTax = (BigDecimal) arr[7];
			BigDecimal igst = (BigDecimal) arr[8];
			BigDecimal cgst = (BigDecimal) arr[9];
			BigDecimal sgst = (BigDecimal) arr[10];
			BigDecimal cess = (BigDecimal) arr[11];
			String docType = arr[13] != null ? String.valueOf(arr[13]) : null;
			String division = arr[14] != null ? String.valueOf(arr[14]) : null;
			String profitCentre = arr[15] != null ? String.valueOf(arr[15])
					: null;
			String location = arr[16] != null ? String.valueOf(arr[16]) : null;
			String plantcode = arr[17] != null ? String.valueOf(arr[17]) : null;
			String salesOrg = arr[18] != null ? String.valueOf(arr[18]) : null;
			String distributionChannel = arr[19] != null
					? String.valueOf(arr[19]) : null;
			String userAccess1 = arr[20] != null ? String.valueOf(arr[20])
					: null;
			String userAccess2 = arr[21] != null ? String.valueOf(arr[21])
					: null;
			String userAccess3 = arr[22] != null ? String.valueOf(arr[22])
					: null;
			String userAccess4 = arr[23] != null ? String.valueOf(arr[23])
					: null;
			String userAccess5 = arr[24] != null ? String.valueOf(arr[24])
					: null;
			String userAccess6 = arr[25] != null ? String.valueOf(arr[25])
					: null;

			itemDto.setEntity(entityPan);
			itemDto.setCompanyCode(companyCode != null ? companyCode : "");
			itemDto.setEntityName(entityName);
			itemDto.setDatatype("outward");
			itemDto.setDocType(docType);
			itemDto.setGstin(gstin);
			itemDto.setRetPeriod(retPeriod);
			itemDto.setDate(date);
			itemDto.setRetType(retType);
			itemDto.setRetSection(retSection);
			itemDto.setTCount(tCount.intValue());
			itemDto.setTaxableValue(tValue);
			itemDto.setTotalTax(totalTax);
			itemDto.setIgst(igst);
			itemDto.setCgst(cgst);
			itemDto.setSgst(sgst);
			itemDto.setCess(cess);
			itemDto.setDivision(division);
			itemDto.setProfitCentre(profitCentre);
			itemDto.setLocation(location);
			itemDto.setPlantcode(plantcode);
			itemDto.setSalesOrg(salesOrg);
			itemDto.setDistributionChannel(distributionChannel);
			itemDto.setUserAccess1(userAccess1);
			itemDto.setUserAccess2(userAccess2);
			itemDto.setUserAccess3(userAccess3);
			itemDto.setUserAccess4(userAccess4);
			itemDto.setUserAccess5(userAccess5);
			itemDto.setUserAccess6(userAccess6);

			subItmList.add(itemDto);

		});
		headerDto.setImItem(subItmList);
		return headerDto;
	}

	@Override
	public AnxDataStatusRequestDataSummaryHeaderDto calculateDataByDocTypeAndReturnPeiod(
			AnxDataStatusRequestDataSummaryHeaderDto itemDto, Long entityId) {
		List<AnxDataStatusRequestItemDto> subItmList = itemDto.getImItem();
		if (!subItmList.isEmpty() && subItmList.size() > 0) {
			List<AnxDataStatusRequestItemDto> finalDataResps = new ArrayList<AnxDataStatusRequestItemDto>();
			Map<String, List<AnxDataStatusRequestItemDto>> returnSectionMap = createMapByReturnSection(
					subItmList);
			calculateDataByPeriodAndDocType(returnSectionMap, finalDataResps,
					entityId);

			itemDto = new AnxDataStatusRequestDataSummaryHeaderDto();
			itemDto.setImItem(segregateTheDataByEntityIdWithTaxPeriod(
					finalDataResps, entityId));
		}

		return itemDto;
	}

	/**
	 * @param finalDataResps
	 * @param gstin
	 * @return
	 */
	private List<AnxDataStatusRequestItemDto> segregateTheDataByEntityIdWithTaxPeriod(
			List<AnxDataStatusRequestItemDto> finalDataResps, Long entityId) {

		List<AnxDataStatusRequestItemDto> segTaxPeriod = new ArrayList<AnxDataStatusRequestItemDto>();
		if (!finalDataResps.isEmpty() && finalDataResps.size() > 0
				&& entityId != 0) {
			Arrays.asList(entityId).forEach(entId -> {
				Map<String, String> entityAndReturnPeriodMap = onboardingConfigParamsCheck
						.getQuestionAndAnswerMap(entId);
				for (AnxDataStatusRequestItemDto dto : finalDataResps) {
					String entityTaxPeriod = entityAndReturnPeriodMap.get("G9");
					String mapTaxPeriod = "01" + entityTaxPeriod;
					DateTimeFormatter formatter = DateTimeFormatter
							.ofPattern("ddMMyyyy");
					LocalDate maptaxdate = LocalDate.parse(mapTaxPeriod,
							formatter);
					String returnType = dto.getRetType();
					String returnPeriod = dto.getRetPeriod();
					String returnMapPeriod = "01" + returnPeriod;
					LocalDate taxdate = LocalDate.parse(returnMapPeriod,
							formatter);
					if (taxdate.compareTo(maptaxdate) <= 0
							&& returnType.equalsIgnoreCase("GSTR1")) {
						segTaxPeriod.add(dto);
					}
					if (taxdate.compareTo(maptaxdate) > 0
							&& returnType.equalsIgnoreCase("ANX1")) {
						segTaxPeriod.add(dto);
					}
					if (taxdate.compareTo(maptaxdate) > 0
							&& returnType.equalsIgnoreCase("RET1")) {
						segTaxPeriod.add(dto);
					}
				}
			});

		}

		return segTaxPeriod;
	}

	/**
	 * @param returnSectionMap
	 * @param finalDataResps
	 */
	private void calculateDataByPeriodAndDocType(
			Map<String, List<AnxDataStatusRequestItemDto>> returnSectionMap,
			List<AnxDataStatusRequestItemDto> finalDataResps, Long entityId) {
		returnSectionMap.keySet().forEach(key -> {
			List<AnxDataStatusRequestItemDto> dataList = returnSectionMap
					.get(key);
			if (!dataList.isEmpty() && dataList.size() > 0) {
				String entity = "";
				String companyCode = "";
				String entityName = "";
				String dataType = "";
				String date = "";
				String gstins = "";
				String retPeriod = "";
				String retType = "";
				String docType = "";
				String returnSection = "";
				String division = "";
				String profitCentre = "";
				String location = "";
				String plantcode = "";
				String salesOrg = "";
				String purchaseOrg = "";
				String distributionChannel = "";
				String userAccess1 = "";
				String userAccess2 = "";
				String userAccess3 = "";
				String userAccess4 = "";
				String userAccess5 = "";
				String userAccess6 = "";
				int count = 0;
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				BigDecimal totalTaxes = BigDecimal.ZERO;
				BigDecimal taxableValue = BigDecimal.ZERO;

				AnxDataStatusRequestItemDto respDto = new AnxDataStatusRequestItemDto();
				for (AnxDataStatusRequestItemDto dto : dataList) {
					entityName = dto.getEntityName();
					dataType = dto.getDatatype();
					companyCode = dto.getCompanyCode();
					gstins = dto.getGstin();
					docType = dto.getDocType();
					date = dto.getDate();
					returnSection = dto.getRetSection();
					retPeriod = dto.getRetPeriod();
					retType = dto.getRetType();
					count = count + dto.getTCount();
					entity = dto.getEntity();
					division = dto.getDivision();
					profitCentre = dto.getProfitCentre();
					location = dto.getLocation();
					plantcode = dto.getPlantcode();
					salesOrg = dto.getSalesOrg();
					purchaseOrg = dto.getPurchaseOrg();
					distributionChannel = dto.getDistributionChannel();
					userAccess1 = dto.getUserAccess1();
					userAccess2 = dto.getUserAccess2();
					userAccess3 = dto.getUserAccess3();
					userAccess4 = dto.getUserAccess4();
					userAccess5 = dto.getUserAccess5();
					userAccess6 = dto.getUserAccess6();
					if (docType != null && (docType.equals("RCR")
							|| docType.equals("CR") || docType.equals("RFV")
							|| docType.equals("RRFV"))) {
						totalTaxes = totalTaxes
								.subtract((dto.getTotalTax() != null
										&& dto.getTotalTax().intValue() > 0)
												? dto.getTotalTax()
												: BigDecimal.ZERO);
						taxableValue = taxableValue
								.subtract((dto.getTaxableValue() != null
										&& dto.getTaxableValue().intValue() > 0)
												? dto.getTaxableValue()
												: BigDecimal.ZERO);
						cess = cess.subtract((dto.getCess() != null
								&& dto.getCess().intValue() > 0) ? dto.getCess()
										: BigDecimal.ZERO);
						igst = igst.subtract((dto.getIgst() != null
								&& dto.getIgst().intValue() > 0) ? dto.getIgst()
										: BigDecimal.ZERO);
						cgst = cgst.subtract((dto.getCgst() != null
								&& dto.getCgst().intValue() > 0) ? dto.getCgst()
										: BigDecimal.ZERO);
						sgst = sgst.subtract((dto.getSgst() != null
								&& dto.getSgst().intValue() > 0) ? dto.getSgst()
										: BigDecimal.ZERO);
					} else {
						totalTaxes = totalTaxes.add((dto.getTotalTax() != null
								&& dto.getTotalTax().intValue() > 0)
										? dto.getTotalTax() : BigDecimal.ZERO);
						taxableValue = taxableValue
								.add((dto.getTaxableValue() != null
										&& dto.getTaxableValue().intValue() > 0)
												? dto.getTaxableValue()
												: BigDecimal.ZERO);
						cess = cess.add((dto.getCess() != null
								&& dto.getCess().intValue() > 0) ? dto.getCess()
										: BigDecimal.ZERO);
						igst = igst.add((dto.getIgst() != null
								&& dto.getIgst().intValue() > 0) ? dto.getIgst()
										: BigDecimal.ZERO);
						cgst = cgst.add((dto.getCgst() != null
								&& dto.getCgst().intValue() > 0) ? dto.getCgst()
										: BigDecimal.ZERO);
						sgst = sgst.add((dto.getSgst() != null
								&& dto.getSgst().intValue() > 0) ? dto.getSgst()
										: BigDecimal.ZERO);
					}
				}
				respDto.setEntity(entity);
				respDto.setEntityName(entityName);
				respDto.setCompanyCode(companyCode != null ? companyCode : "");
				respDto.setDocType(docType);
				respDto.setDatatype(dataType);
				respDto.setDate(date);
				respDto.setGstin(gstins);
				respDto.setRetPeriod(retPeriod);
				respDto.setRetSection(returnSection);
				respDto.setRetType(retType);
				respDto.setTCount(count);
				respDto.setTaxableValue(taxableValue);
				respDto.setTotalTax(totalTaxes);
				respDto.setIgst(igst);
				respDto.setCgst(cgst);
				respDto.setSgst(sgst);
				respDto.setCess(cess);
				respDto.setDivision(division);
				respDto.setProfitCentre(profitCentre);
				respDto.setLocation(location);
				respDto.setPlantcode(plantcode);
				respDto.setSalesOrg(salesOrg);
				respDto.setPurchaseOrg(purchaseOrg);
				respDto.setDistributionChannel(distributionChannel);
				respDto.setUserAccess1(userAccess1);
				respDto.setUserAccess2(userAccess2);
				respDto.setUserAccess3(userAccess3);
				respDto.setUserAccess4(userAccess4);
				respDto.setUserAccess5(userAccess5);
				respDto.setUserAccess6(userAccess6);

				finalDataResps.add(respDto);

			}

		});
	}

	/**
	 * @param subItmList
	 * @return
	 */
	private Map<String, List<AnxDataStatusRequestItemDto>> createMapByReturnSection(
			List<AnxDataStatusRequestItemDto> subItmList) {
		Map<String, List<AnxDataStatusRequestItemDto>> returnSectionMap = new HashMap<String, List<AnxDataStatusRequestItemDto>>();

		subItmList.forEach(dto -> {
			StringBuffer key = new StringBuffer();
			key.append(dto.getDate()).append("_").append(dto.getGstin())
					.append("_").append(dto.getRetPeriod()).append("_")
					.append(dto.getRetSection());

			String dataKey = key.toString();
			if (returnSectionMap.containsKey(dataKey)) {
				List<AnxDataStatusRequestItemDto> dtos = returnSectionMap
						.get(dataKey);
				dtos.add(dto);
				returnSectionMap.put(dataKey, dtos);
			} else {
				List<AnxDataStatusRequestItemDto> dtos = new ArrayList<>();
				dtos.add(dto);
				returnSectionMap.put(dataKey, dtos);
			}
		});
		return returnSectionMap;
	}

	@Override
	public List<Object[]> find(AnxDataStatusReqDto req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnxDataStatusRequestDataHeaderDto convertDataToInward(
			List<AnxDataStatusResultDto> results, String dataType) {

		List<AnxDataStatusRequestItemDto> itemList = new ArrayList<>();

		results.forEach(result -> {

			AnxDataStatusRequestItemDto item = new AnxDataStatusRequestItemDto();

			LocalDate recievedDate = result.getReceivedDate();
			Integer aspTotal = result.getTotalRecords();
			Integer activeProcess = result.getProcessedActive();
			Integer inactiveProcess = result.getProcessedInactive();
			Integer activeError = result.getErrorActive();
			Integer inactiveError = result.getErrorInactive();
			Integer activeInfo = result.getInfoActive();
			Integer inactiveInfo = result.getInfoInactive();

			String gstin = result.getGstin();
			String companyCode = result.getCompanyCode();
			String retPeriod = result.getRetPeriod();
			String division = result.getDivision();
			String profitCentre = result.getProfitCentre();
			String location = result.getLocation();
			String plantcode = result.getPlantcode();
			String salesOrg = result.getSalesOrg();
			String purchaseOrg = result.getPurchaseOrg();
			String distributionChannel = result.getDistributionChannel();
			String userAccess1 = result.getUserAccess1();
			String userAccess2 = result.getUserAccess2();
			String userAccess3 = result.getUserAccess3();
			String userAccess4 = result.getUserAccess4();
			String userAccess5 = result.getUserAccess5();
			String userAccess6 = result.getUserAccess6();

			String entity = result.getEntity();
			String entityName = result.getEntityName();
			item.setEntity(entity != null ? String.valueOf(entity) : null);
			item.setRetPeriod(
					retPeriod != null ? String.valueOf(retPeriod) : null);
			item.setGstin(gstin != null ? String.valueOf(gstin) : null);
			item.setDatatype(
					dataType != null ? String.valueOf(dataType) : null);
			item.setEntityName(
					entityName != null ? String.valueOf(entityName) : null);
			item.setDivision(
					division != null ? String.valueOf(division) : null);
			item.setProfitCentre(
					profitCentre != null ? String.valueOf(profitCentre) : null);
			item.setLocation(
					location != null ? String.valueOf(location) : null);
			item.setPlantcode(
					plantcode != null ? String.valueOf(plantcode) : null);
			item.setSalesOrg(
					salesOrg != null ? String.valueOf(salesOrg) : null);
			item.setPurchaseOrg(
					purchaseOrg != null ? String.valueOf(purchaseOrg) : null);
			item.setDistributionChannel(distributionChannel != null
					? String.valueOf(distributionChannel) : null);
			item.setUserAccess1(
					userAccess1 != null ? String.valueOf(userAccess1) : null);
			item.setUserAccess2(
					userAccess2 != null ? String.valueOf(userAccess2) : null);
			item.setUserAccess3(
					userAccess3 != null ? String.valueOf(userAccess3) : null);
			item.setUserAccess4(
					userAccess4 != null ? String.valueOf(userAccess4) : null);
			item.setUserAccess5(
					userAccess5 != null ? String.valueOf(userAccess5) : null);
			item.setUserAccess6(
					userAccess6 != null ? String.valueOf(userAccess6) : null);
			item.setDate(
					recievedDate != null ? String.valueOf(recievedDate) : null);
			item.setAspTotal(aspTotal);
			item.setCompanyCode(companyCode != null ? companyCode : "");
			item.setActiveProcess(activeProcess);
			item.setInactiveProcess(inactiveProcess);
			item.setActiveError(activeError);
			item.setInactiveError(inactiveError);
			item.setActiveInfo(activeInfo);
			item.setInactiveInfo(inactiveInfo);
			itemList.add(item);
		});

		AnxDataStatusRequestDataHeaderDto dto = new AnxDataStatusRequestDataHeaderDto();

		dto.setImItem(itemList);

		return dto;

	}

	@Override
	public AnxDataStatusRequestDataSummaryHeaderDto convertDataToInwardDataSummary(
			String entityPan, String entityName, List<Object[]> arrs,
			Long entityId, String companyCode) {

		AnxDataStatusRequestDataSummaryHeaderDto headerDto = new AnxDataStatusRequestDataSummaryHeaderDto();
		List<AnxDataStatusRequestItemDto> subItmList = new ArrayList<>();
		arrs.forEach(arr -> {
			AnxDataStatusRequestItemDto itemDto = new AnxDataStatusRequestItemDto();
			String gstin = String.valueOf(arr[0]);
			String retPeriod = arr[1] != null ? String.valueOf(arr[1]) : null;
			String date = arr[2] != null ? String.valueOf(arr[2]) : null;
			String retSection = arr[3] != null ? String.valueOf(arr[3]) : null;
			String retType = arr[4] != null ? String.valueOf(arr[4]) : null;
			Integer tCount = (Integer) arr[5];
			BigDecimal tValue = (BigDecimal) arr[6];
			BigDecimal totalTax = (BigDecimal) arr[7];
			BigDecimal igst = (BigDecimal) arr[8];
			BigDecimal cgst = (BigDecimal) arr[9];
			BigDecimal sgst = (BigDecimal) arr[10];
			BigDecimal cess = (BigDecimal) arr[11];
			String docType = arr[13] != null ? String.valueOf(arr[13]) : null;
			String division = arr[14] != null ? String.valueOf(arr[14]) : null;
			String profitCentre = arr[15] != null ? String.valueOf(arr[15])
					: null;
			String location = arr[16] != null ? String.valueOf(arr[16]) : null;
			String plantcode = arr[17] != null ? String.valueOf(arr[17]) : null;
			String purchaseOrg = arr[18] != null ? String.valueOf(arr[18])
					: null;
			String userAccess1 = arr[19] != null ? String.valueOf(arr[19])
					: null;
			String userAccess2 = arr[20] != null ? String.valueOf(arr[20])
					: null;
			String userAccess3 = arr[21] != null ? String.valueOf(arr[21])
					: null;
			String userAccess4 = arr[22] != null ? String.valueOf(arr[22])
					: null;
			String userAccess5 = arr[23] != null ? String.valueOf(arr[23])
					: null;
			String userAccess6 = arr[24] != null ? String.valueOf(arr[24])
					: null;
			itemDto.setEntityName(entityName);
			itemDto.setEntity(entityPan);
			itemDto.setCompanyCode(companyCode != null ? companyCode : "");
			itemDto.setDatatype("inward");
			itemDto.setDocType(docType);
			itemDto.setGstin(gstin);
			itemDto.setRetPeriod(retPeriod);
			itemDto.setDate(date);
			itemDto.setRetType(retType);
			itemDto.setRetSection(retSection);
			itemDto.setTCount(tCount.intValue());
			itemDto.setTaxableValue(tValue);
			itemDto.setTotalTax(totalTax);
			itemDto.setIgst(igst);
			itemDto.setCgst(cgst);
			itemDto.setSgst(sgst);
			itemDto.setCess(cess);
			itemDto.setDivision(division);
			itemDto.setProfitCentre(profitCentre);
			itemDto.setLocation(location);
			itemDto.setPlantcode(plantcode);
			itemDto.setPurchaseOrg(purchaseOrg);
			itemDto.setUserAccess1(userAccess1);
			itemDto.setUserAccess2(userAccess2);
			itemDto.setUserAccess3(userAccess3);
			itemDto.setUserAccess4(userAccess4);
			itemDto.setUserAccess5(userAccess5);
			itemDto.setUserAccess6(userAccess6);

			subItmList.add(itemDto);

		});
		headerDto.setImItem(subItmList);
		return headerDto;
	}

}
