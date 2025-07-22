/*package com.ey.advisory.app.services.jobs.erp;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Ret1ReviewHeaderSummaryRequestDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ReviewSummaryRequestItemDto;
import com.ey.advisory.core.api.APIConstants;

*//**
 * 
 * @author Mahesh.Golla
 *
 *//*
@Service("Ret1AReviewSummaryRequestDocsImpl")
public class Ret1AReviewSummaryRequestDocsImpl
		implements Ret1ReviewSummaryRequestDocs {
	private static final String GSTIN = "gstin";

	Logger LOGGER = LoggerFactory
			.getLogger(Ret1AReviewSummaryRequestDocsImpl.class);

	@Autowired
	private DestinationConnectivity destinationConn;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;


	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getProcessSumryData(String gstin, String type) {
		List<Object[]> obj = new ArrayList<>();
		if (APIConstants.PROCESS_SUMMARY.equalsIgnoreCase(type)) {
			String sql = getProcessSummaryQuery();
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter(GSTIN, gstin);
			obj = q.getResultList();
		}
		return obj;
	}

	@Override
	public Ret1ReviewHeaderSummaryRequestDto finalConForProcRevSummary(
			List<Object[]> objs, String entityName, String entityPan,
			String companyCode,String stateName) {
		Ret1ReviewHeaderSummaryRequestDto requestDto = 
				new Ret1ReviewHeaderSummaryRequestDto();
		// Convert All Item Dto
		List<Ret1ReviewSummaryRequestItemDto> itemDto = convertProcessToDocs(
				objs, entityName, entityPan, companyCode, stateName);
		if (itemDto != null && !itemDto.isEmpty()) {
			Map<String, List<Ret1ReviewSummaryRequestItemDto>> 
			returnSectionMap = createMapByReturnSection(itemDto);
			List<Ret1ReviewSummaryRequestItemDto> finalDtos = 
					calculateDataPeriodByDocType(returnSectionMap);

			if (!finalDtos.isEmpty()) {
				requestDto = new Ret1ReviewHeaderSummaryRequestDto();
				requestDto.setItem(finalDtos);
			}
		}
		return requestDto;
		
		List<Ret1ReviewSummaryRequestHederDto> headerDtos = new ArrayList<>();
		Ret1ReviewSummaryRequestHederDto headerDto = 
				new Ret1ReviewSummaryRequestHederDto();
		headerDto.setEntity(entityName);
		headerDto.setEntityPan(entityPan);
		headerDto.setCompanyCode(companyCode);
		headerDto.setRetPer("042019");
		headerDto.setGstinNum("33GSPTN0482G1Z9");
		List<Ret1ReviewSummaryRequestItemDto> itemDtos = new ArrayList<>();
		Ret1ReviewSummaryRequestItemDto itemDto = 
				new Ret1ReviewSummaryRequestItemDto();
		itemDto.setState("Karnataka");
		headerDto.setItemDto(itemDtos);
		headerDtos.add(headerDto);
		requestDto.setItem(headerDtos);
		return requestDto;
	}

	private List<Ret1ReviewSummaryRequestItemDto> calculateDataPeriodByDocType(
			Map<String, List<Ret1ReviewSummaryRequestItemDto>> returnSectionMap) {
		List<Ret1ReviewSummaryRequestItemDto> finalItemsDto = new LinkedList<>();
		if (returnSectionMap != null) {
			returnSectionMap.entrySet().forEach(entries -> {
				BigInteger count = BigInteger.ZERO;
				BigDecimal eyOutsupp = BigDecimal.ZERO;
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				List<Ret1ReviewSummaryRequestItemDto> dtos = entries.getValue();
				if (dtos != null && !dtos.isEmpty()) {
					Ret1ReviewSummaryRequestItemDto itemDto = 
							new Ret1ReviewSummaryRequestItemDto();
					for (Ret1ReviewSummaryRequestItemDto dto : dtos) {

						itemDto.setEntity(dto.getEntity());

						itemDto.setEntityPan(dto.getEntityPan());
						itemDto.setCompanyCode(dto.getCompanyCode());
						itemDto.setState(dto.getState());
						itemDto.setRetPer(dto.getRetPer());
						itemDto.setGstinNum(dto.getGstinNum());
						//count = count.add(dto.getEyTotDoc());
						//String docType = dto.getDocType();
						if (docType != null && (docType.equals("RCR")
								|| docType.equals("CR") || docType.equals("RFV")
								|| docType.equals("RRFV"))) {
							eyOutsupp = eyOutsupp.subtract(dto.getEyOutsupp());
							igst = igst.subtract(dto.getEyIgstval());
							cgst = cgst.subtract(dto.getEyCgstval());
							sgst = sgst.subtract(dto.getEySgstval());
							cess = cess.subtract(dto.getEyCessval());
						} else {
							eyOutsupp = eyOutsupp.add(dto.getEyOutsupp());
							igst = igst.add(dto.getEyIgstval());
							cgst = cgst.add(dto.getEyCgstval());
							sgst = sgst.add(dto.getEySgstval());
							cess = cess.add(dto.getEyCessval());
						}
						itemDto.setEyTotDoc(count);
						itemDto.setEyOutsupp(eyOutsupp);
						itemDto.setEyIgstval(igst);
						itemDto.setEyCgstval(cgst);
						itemDto.setEySgstval(sgst);
						itemDto.setEyCessval(cess);
						itemDto.setRtnPerdStatus(dto.getRtnPerdStatus());
						itemDto.setEyDate(dto.getEyDate());
						itemDto.setEyTime(dto.getEyTime());
						 itemDto.setProfitCenter(dto.getProfitCenter());
						itemDto.setPlantCode(dto.getPlantCode());
						  itemDto.setLocation(dto.getLocation());
						  itemDto.setSalesOrganization(dto.getSalesOrganization());
						  itemDto.setDistChannel(dto.getDistChannel());
						  itemDto.setDivision(dto.getDivision());
						  itemDto.setUseraccess1(dto.getUseraccess1());
						  itemDto.setUseraccess2(dto.getUseraccess2());
						  itemDto.setUseraccess3(dto.getUseraccess3());
						  itemDto.setUseraccess4(dto.getUseraccess4());
						  itemDto.setUseraccess5(dto.getUseraccess5());
						  itemDto.setUseraccess6(dto.getUseraccess6());
					}
					finalItemsDto.add(itemDto);
				}
			});
		}
		return finalItemsDto;
	}

	private Map<String, List<Ret1ReviewSummaryRequestItemDto>> 
	createMapByReturnSection(List<Ret1ReviewSummaryRequestItemDto> itemDto) {
		Map<String, List<Ret1ReviewSummaryRequestItemDto>> returnSectionMap 
		                                           = new LinkedHashMap<>();

		itemDto.forEach(dto -> {
			StringBuilder key = new StringBuilder();
			key.append(dto.getGstinNum());
			key.append("_");
			key.append(dto.getRetPer());
			String docKey = key.toString();
			if (returnSectionMap.containsKey(docKey)) {
				List<Ret1ReviewSummaryRequestItemDto> dtos = returnSectionMap
						                                         .get(docKey);
				dtos.add(dto);
				returnSectionMap.put(docKey, dtos);
			} else {
				List<Ret1ReviewSummaryRequestItemDto> dtos = new ArrayList<>();
				dtos.add(dto);
				returnSectionMap.put(docKey, dtos);
			}
		});
		return returnSectionMap;
	}


	private List<Ret1ReviewSummaryRequestItemDto> convertProcessToDocs(
			List<Object[]> processObjs, String entityName, String entityPan,
			String companyCode, String stateName) {
		List<Ret1ReviewSummaryRequestItemDto> itemDto = new ArrayList<>();
		for (Object[] obj : processObjs) {
			Ret1ReviewSummaryRequestItemDto child = 
					new Ret1ReviewSummaryRequestItemDto();
			if (entityName != null) {
				child.setEntity(StringUtils.upperCase(entityName));
			}
			child.setEntityPan(entityPan);
			child.setCompanyCode(companyCode);
			child.setState(stateName);
			child.setGstinNum(obj[0] != null ? String.valueOf(obj[0]) : null);
			child.setRetPer(obj[1] != null ? String.valueOf(obj[1]) : null);
			child.setEyStatus(obj[2] != null ? String.valueOf(obj[2]) : null);
			child.setTotalTaxLiability(obj[3] != null ? 
					new BigDecimal(String.valueOf(obj[3])) : BigDecimal.ZERO);
			child.setRevCharge(obj[4] != null ? 
					new BigDecimal(String.valueOf(obj[4])) : BigDecimal.ZERO);
			child.setOthRevCharge(obj[5] != null ? 
					new BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);
			child.setNetAvaiItc(obj[6] != null ? 
					new BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);
			child.setTds(obj[7] != null ? 
					new BigDecimal(String.valueOf(obj[7])): BigDecimal.ZERO);
			child.setTcs(obj[8] != null ? 		
					new BigDecimal(String.valueOf(obj[8])): BigDecimal.ZERO);
			

			child.setTaxDoctype(obj[2] != null ? String.valueOf(obj[2]) : null);
			child.setDocType(obj[3] != null ? String.valueOf(obj[3]) : null);
			child.setEyTotDoc(obj[4] != null
					? new BigInteger(String.valueOf(obj[4])) : BigInteger.ZERO);
			child.setEyOutsupp(obj[5] != null
					? new BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);
			child.setEyIgstval(obj[6] != null
					? new BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);
			child.setEyCgstval(obj[7] != null
					? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
			child.setEySgstval(obj[8] != null
					? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);
			child.setEyCessval(obj[9] != null
					? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
			child.setRtnPerdStatus(
					obj[10] != null ? String.valueOf(obj[10]) : null);
			String currentDateTime = obj[11] != null ? String.valueOf(obj[11])
					: null;
			Date localCurrentDate;
			Date localCurrentTime;
			String currentDate = null;
			String currentTime = null;
			if (currentDateTime != null) {
				try {
					localCurrentDate = new SimpleDateFormat(DATE_FORMATE)
							.parse(currentDateTime);

					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATE);
					currentDate = sdf.format(localCurrentDate);
					localCurrentTime = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss.SSS").parse(currentDateTime);
					SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
					currentTime = sdf2.format(localCurrentTime);
				} catch (ParseException e) {
					LOGGER.error("Exception Occured: {}", e);
				}
			}
			child.setEyDate(currentDate);
			child.setEyTime(currentTime);
			child.setProfitCenter(
					obj[9] != null ? String.valueOf(obj[9]) : null);
			child.setPlantCode(
					obj[10] != null ? String.valueOf(obj[10]) : null);

			child.setLocation(obj[11] != null ? String.valueOf(obj[11]) : null);
			child.setSalesOrganization(
					obj[12] != null ? String.valueOf(obj[12]) : null);
			child.setDistChannel(
					obj[13] != null ? String.valueOf(obj[13]) : null);
			child.setDivision(obj[14] != null ? String.valueOf(obj[14]) : null);
			child.setUseraccess1(
					obj[15] != null ? String.valueOf(obj[15]) : null);
			child.setUseraccess2(
					obj[16] != null ? String.valueOf(obj[16]) : null);
			child.setUseraccess3(
					obj[17] != null ? String.valueOf(obj[17]) : null);
			child.setUseraccess4(
					obj[18] != null ? String.valueOf(obj[18]) : null);
			child.setUseraccess5(
					obj[19] != null ? String.valueOf(obj[19]) : null);
			child.setUseraccess6(
					obj[20] != null ? String.valueOf(obj[20]) : null);

			itemDto.add(child);
		}

		return itemDto;
	}


	@Override
	public Integer pushToErp(Ret1ReviewHeaderSummaryRequestDto reqDto,
			String destName) {
		Integer respCode = null;
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			JAXBContext contaxt = JAXBContext
					.newInstance(Ret1ReviewHeaderSummaryRequestDto.class);
			Marshaller marshaller = contaxt.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.marshal(reqDto, stream);
			String headerXml = stream.toString();
			if (headerXml != null && headerXml.length() > 0) {
				headerXml = headerXml.substring(headerXml.indexOf('\n') + 1);
			}
			StringBuilder header = new StringBuilder();
			header.append(
					"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' ");
			header.append(
					"xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'>");
			header.append("<soapenv:Header/>");
			header.append("<soapenv:Body>");
			header.append("<urn:ZupdateRet1Aspdata>");

			StringBuilder footer = new StringBuilder();

			footer.append("</urn:ZupdateRet1Aspdata>");
			footer.append("</soapenv:Body>");
			footer.append("</soapenv:Envelope>");

			String xml = null;
			if (headerXml != null) {
				xml = header + headerXml + footer;
			}
			if (xml != null && destName != null) {
				respCode = destinationConn.post(destName, xml);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{} ", e);
		}
		return respCode;
	}
	
	private String getProcessSummaryQuery() {
		return "SELECT GSTIN,RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,DIVISION, "
				+ "PLANT_CODE,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, "
				+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5, "
				+ "USERACCESS6,PURCHASE_ORGANIZATION,STATUS, "
				+ "SUM(RET1_TOTAL_TAX_LIABILITY) RET1_TOTAL_TAX_LIABILITY, "
				+ " SUM(RET1_REVERSECHARGE) RET1_REVERSECHARGE, "
				+ "SUM(RET1_OTHERTHAN_REVERSECHARGE) AS "
				+ "RET1_OTHERTHAN_REVERSECHARGE, SUM(RET1_NET_ITC_AVAILABLE) "
				+ "AS RET1_NET_ITC_AVAILABLE, SUM(RET1_TDS) AS RET1_TDS,"
				+ "SUM(RET1_TCS) AS RET1_TCS  "
				+ "FROM( SELECT GSTIN,RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,"
				+ "DIVISION,PLANT_CODE,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, "
				+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5, "
				+ "USERACCESS6, PURCHASE_ORGANIZATION,STATUS,"
				+ "RET1_TOTAL_TAX_LIABILITY, 0 AS RET1_REVERSECHARGE, "
				+ "0 AS RET1_OTHERTHAN_REVERSECHARGE, 0 AS "
				+ "RET1_NET_ITC_AVAILABLE,0 AS RET1_TDS, 0 AS RET1_TCS "
				+ "FROM ( SELECT SUPPLIER_GSTIN AS GSTIN, RETURN_PERIOD AS "
				+ "RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
				+ "USERACCESS6,'' AS PURCHASE_ORGANIZATION, "
				+ "CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN=TRUE AND "
				+ "IS_DELETE =FALSE THEN 1 ELSE NULL END) = 0 and COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 "
				+ "ELSE NULL END) = 0) THEN 'NOT INTTIATED' "
				+ "WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END)"
				+ " = COUNT(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN "
				+ "1 ELSE NULL END)) THEN 'FAILED' WHEN (COUNT(CASE "
				+ "when IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 "
				+ "ELSE NULL END)) THEN 'SAVED' WHEN (COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 "
				+ "ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 "
				+ "ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS, (CASE "
				+ "WHEN AN_TABLE_SECTION IN ('3A','3B','3C','3D','3E','3F','3G') "
				+ "AND AN_RETURN_TYPE='ANX1' THEN (IFNULL(SUM( IGST_AMT),0)+"
				+ "IFNULL(SUM( CGST_AMT),0)+IFNULL(SUM( SGST_AMT),0)+ "
				+ "IFNULL(SUM( CESS_AMT_SPECIFIC),0)+"
				+ "IFNULL(SUM( CESS_AMT_ADVALOREM),0)) END ) AS "
				+ "RET1_TOTAL_TAX_LIABILITY FROM ANX_OUTWARD_DOC_HEADER "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
				+ "AN_TABLE_SECTION IN ('3A','3B','3C','3D','3E','3F','3G') "
				+ "AND AN_RETURN_TYPE='ANX1' AND SUPPLIER_GSTIN IN (:gstin)"
				+ " GROUP BY SUPPLIER_GSTIN, "
				+ "RETURN_PERIOD,AN_TABLE_SECTION,AN_RETURN_TYPE,PROFIT_CENTRE,"
				+ "LOCATION ,DIVISION,PLANT_CODE,SALES_ORGANIZATION,"
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6 UNION ALL SELECT GSTIN,"
				+ " RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "'' AS PURCHASE_ORGANIZATION, CASE WHEN(COUNT(CASE "
				+ "WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE "
				+ "NULL END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ "'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN "
				+ "1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED'"
				+ " WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL "
				+ "END) = COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND "
				+ "IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND "
				+ "IS_DELETE = FALSE then 1 ELSE NULL END) < COUNT(CASE WHEN "
				+ "IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN "
				+ "'PARTIALLY SAVED' END AS STATUS, (CASE WHEN RETURN_TYPE IN "
				+ "('3A8') AND RETURN_TYPE='RET-1' THEN (IFNULL(SUM(IGST_AMT),0)"
				+ "+IFNULL(SUM(CGST_AMT),0)+ IFNULL(SUM(SGST_AMT),0)+"
				+ "IFNULL(SUM(CESS_AMT),0)) END) AS RET1_TOTAL_TAX_LIABILITY "
				+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE AND "
				+ "RETURN_TYPE='RET-1' AND RETURN_TABLE IN ('3A8') AND "
				+ "GSTIN IN (:gstin) GROUP BY "
				+ "GSTIN, RETURN_PERIOD,RETURN_TABLE,RETURN_TYPE,PROFIT_CENTRE,"
				+ "LOCATION ,DIVISION,PLANT_CODE,SALES_ORGANIZATION,"
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6 "
				+ "UNION ALL SELECT CUST_GSTIN AS GSTIN,RETURN_PERIOD AS "
				+ "RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,''"
				+ " AS SALES_ORGANIZATION,'' AS DISTRIBUTION_CHANNEL, "
				+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
				+ "USERACCESS6,PURCHASE_ORGANIZATION, CASE WHEN(COUNT(CASE "
				+ "WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE"
				+ " NULL END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ "'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN "
				+ "1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' WHEN "
				+ "(COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = "
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN (COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE then 1 "
				+ "ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 "
				+ "ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "(CASE WHEN AN_TABLE_SECTION IN ('3H','3I') AND "
				+ "AN_RETURN_TYPE='ANX1' THEN (IFNULL(SUM( IGST_AMT),0)+"
				+ "IFNULL(SUM( CGST_AMT),0)+IFNULL(SUM( SGST_AMT),0)+ "
				+ "IFNULL(SUM( CESS_AMT_SPECIFIC),0)+"
				+ "IFNULL(SUM( CESS_AMT_ADVALOREM),0)) END ) AS "
				+ "RET1_TOTAL_TAX_LIABILITY "
				+ "FROM ANX_INWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
				+ "IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3H','3I') "
				+ "AND CUST_GSTIN IN (:gstin) GROUP BY CUST_GSTIN, "
				+ "RETURN_PERIOD,AN_TABLE_SECTION,"
				+ "AN_RETURN_TYPE, PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ " USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
				+ "USERACCESS6,PURCHASE_ORGANIZATION UNION ALL SELECT GSTIN,"
				+ "RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "PURCHASE_ORGANIZATION, STATUS,SUM (RET1_TOTAL_TAX_LIABILITY) "
				+ "AS RET1_TOTAL_TAX_LIABILITY FROM ( SELECT SUPPLIER_GSTIN "
				+ "AS GSTIN, RETURN_PERIOD AS RETURN_PERIOD, PROFIT_CENTRE,"
				+ "LOCATION ,DIVISION,PLANT_CODE,SALES_ORGANIZATION,"
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6,'' "
				+ "AS PURCHASE_ORGANIZATION, CASE WHEN(COUNT(CASE WHEN "
				+ "IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL "
				+ "END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ "'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1"
				+ " ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' WHEN "
				+ "(COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = "
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = "
				+ "FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN (COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1"
				+ " ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 "
				+ "ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN IFNULL( IGST_AMT,0)"
				+ "+IFNULL( CGST_AMT,0)+IFNULL( SGST_AMT,0)+"
				+ "IFNULL( CESS_AMT_SPECIFIC,0)+IFNULL( CESS_AMT_ADVALOREM,0)"
				+ " END),0) - IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN "
				+ "IFNULL( IGST_AMT,0)+IFNULL( CGST_AMT,0)+IFNULL( SGST_AMT,0)+"
				+ "IFNULL( CESS_AMT_SPECIFIC,0)+IFNULL( CESS_AMT_ADVALOREM,0) "
				+ "END),0)) AS RET1_TOTAL_TAX_LIABILITY "
				+ "FROM ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
				+ "IS_DELETE = FALSE AND DOC_TYPE IN ('DR' ,'CR') AND"
				+ " AN_RETURN_TYPE='ANX1' AND SUPPLIER_GSTIN IN (:gstin) "
				+ "GROUP BY SUPPLIER_GSTIN, "
				+ "RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6 "
				+ "UNION ALL SELECT SUPPLIER_GSTIN AS GSTIN, RETURN_PERIOD AS "
				+ "RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "'' AS PURCHASE_ORGANIZATION, CASE WHEN(COUNT(CASE WHEN "
				+ "IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL "
				+ "END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ "'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 "
				+ "ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' WHEN "
				+ "(COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) ="
				+ " COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN (COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE "
				+ "NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE "
				+ "NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "(IFNULL(SUM(CASE WHEN AN_TABLE_SECTION='3C3' THEN "
				+ "IFNULL( IGST_AMT,0)+IFNULL( CGST_AMT,0)+IFNULL( SGST_AMT,0)+"
				+ "IFNULL( CESS_AMT_SPECIFIC,0)+IFNULL( CESS_AMT_ADVALOREM,0) "
				+ "END),0) - IFNULL(SUM(CASE WHEN AN_TABLE_SECTION='3C4' THEN "
				+ "IFNULL( IGST_AMT,0)+IFNULL( CGST_AMT,0)+IFNULL( SGST_AMT,0)+"
				+ "IFNULL( CESS_AMT_SPECIFIC,0)+IFNULL( CESS_AMT_ADVALOREM,0) "
				+ "END),0)) AS RET1_TOTAL_TAX_LIABILITY "
				+ "FROM ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
				+ "IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3C3' ,'3C4') "
				+ "AND SUPPLIER_GSTIN IN (:gstin)"
				+ " GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD,PROFIT_CENTRE,"
				+ "LOCATION ,DIVISION,PLANT_CODE,SALES_ORGANIZATION,"
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6 UNION ALL SELECT GSTIN, "
				+ "RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "'' AS PURCHASE_ORGANIZATION, CASE WHEN(COUNT(CASE WHEN "
				+ "IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL "
				+ "END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ "'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN "
				+ "1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL "
				+ "END) = COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND "
				+ "IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE "
				+ "= FALSE then 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = "
				+ "FALSE THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS "
				+ "STATUS, (IFNULL(SUM(CASE WHEN RETURN_TABLE='3C3' THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT,0) END),0) - IFNULL(SUM(CASE WHEN "
				+ "RETURN_TABLE='3C4' THEN IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0) "
				+ "END),0) - IFNULL(SUM(CASE WHEN RETURN_TABLE='3C5' THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT,0) END),0)) AS RET1_TOTAL_TAX_LIABILITY "
				+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE AND "
				+ "RETURN_TYPE='RET-1' AND RETURN_TABLE IN ('3C3','3C4','3C5') "
				+ "AND GSTIN IN (:gstin) GROUP BY "
				+ "GSTIN, RETURN_PERIOD,RETURN_TABLE,PROFIT_CENTRE,"
				+ "LOCATION ,DIVISION,PLANT_CODE,SALES_ORGANIZATION,"
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6 ) GROUP BY GSTIN,"
				+ "RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "PURCHASE_ORGANIZATION,STATUS) --RET1_TOTAL_TAX_LIABILITY "
				+ "UNION ALL SELECT GSTIN,RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,"
				+ "DIVISION,PLANT_CODE,'' AS SALES_ORGANIZATION,'' "
				+ "AS DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6,PURCHASE_ORGANIZATION,"
				+ "STATUS, 0 AS RET1_TOTAL_TAX_LIABILITY, RET1_REVERSECHARGE, 0 "
				+ "AS RET1_OTHERTHAN_REVERSECHARGE, 0 AS RET1_NET_ITC_AVAILABLE,"
				+ "0 AS RET1_TDS, 0 AS RET1_TCS FROM (SELECT CUST_GSTIN AS GSTIN"
				+ ", RETURN_PERIOD AS RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,"
				+ "DIVISION,PLANT_CODE,'' AS SALES_ORGANIZATION,'' "
				+ "AS DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6,PURCHASE_ORGANIZATION, "
				+ "CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 and COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE "
				+ "NULL END) = 0) THEN 'NOT INTTIATED' WHEN (COUNT(CASE WHEN "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT(CASE WHEN "
				+ "GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 "
				+ "ELSE NULL END) = COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND "
				+ "IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN "
				+ "(COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE "
				+ "then 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS,"
				+ " (CASE WHEN AN_TABLE_SECTION IN ('3H','3I') AND "
				+ "AN_RETURN_TYPE='ANX1' THEN (IFNULL(SUM( IGST_AMT),0)+"
				+ "IFNULL(SUM( CGST_AMT),0)+IFNULL(SUM( SGST_AMT),0)+ "
				+ "IFNULL(SUM( CESS_AMT_SPECIFIC),0)+IFNULL(SUM( "
				+ "CESS_AMT_ADVALOREM),0)) END ) AS RET1_REVERSECHARGE "
				+ "FROM ANX_INWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
				+ "IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3H','3I') AND "
				+ "AN_RETURN_TYPE='ANX1' AND CUST_GSTIN IN (:gstin) "
				+ "GROUP BY CUST_GSTIN, RETURN_PERIOD,"
				+ "AN_TABLE_SECTION,AN_RETURN_TYPE, PROFIT_CENTRE,LOCATION ,"
				+ "DIVISION,PLANT_CODE, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6,PURCHASE_ORGANIZATION) "
				+ " UNION ALL"
				+ " SELECT GSTIN,RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,"
				+ "DIVISION,PLANT_CODE,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, "
				+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
				+ "USERACCESS6,PURCHASE_ORGANIZATION,STATUS, 0 AS "
				+ "RET1_TOTAL_TAX_LIABILITY, 0 AS RET1_REVERSECHARGE, "
				+ "RET1_OTHERTHAN_REVERSECHARGE, 0 AS RET1_NET_ITC_AVAILABLE,0 "
				+ "AS RET1_TDS, 0 AS RET1_TCS FROM (SELECT SUPPLIER_GSTIN AS "
				+ "GSTIN, RETURN_PERIOD AS RETURN_PERIOD, PROFIT_CENTRE,"
				+ "LOCATION ,DIVISION,PLANT_CODE,SALES_ORGANIZATION,"
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6,'' AS "
				+ "PURCHASE_ORGANIZATION, CASE WHEN(COUNT(CASE WHEN "
				+ "IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL "
				+ "END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ "'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 "
				+ "ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' WHEN "
				+ "(COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = "
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = "
				+ "FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN (COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 "
				+ "ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 "
				+ "ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "(CASE WHEN AN_TABLE_SECTION IN "
				+ "('3A','3B','3C','3D','3E','3F','3G') AND "
				+ "AN_RETURN_TYPE='ANX1' THEN (IFNULL(SUM( IGST_AMT),0)+"
				+ "IFNULL(SUM( CGST_AMT),0)+IFNULL(SUM( SGST_AMT),0)+ "
				+ "IFNULL(SUM( CESS_AMT_SPECIFIC),0)+"
				+ "IFNULL(SUM( CESS_AMT_ADVALOREM),0)) END ) AS "
				+ "RET1_OTHERTHAN_REVERSECHARGE FROM ANX_OUTWARD_DOC_HEADER "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
				+ "AN_TABLE_SECTION IN ('3A','3B','3C','3D','3E','3F','3G') AND"
				+ " AN_RETURN_TYPE='ANX1' AND SUPPLIER_GSTIN IN (:gstin)"
				+ " GROUP BY SUPPLIER_GSTIN, "
				+ "RETURN_PERIOD,AN_TABLE_SECTION,AN_RETURN_TYPE,PROFIT_CENTRE,"
				+ "LOCATION ,DIVISION,PLANT_CODE,SALES_ORGANIZATION,"
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6 UNION ALL SELECT GSTIN,"
				+ " RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "'' AS PURCHASE_ORGANIZATION, CASE WHEN(COUNT(CASE WHEN "
				+ "IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL "
				+ "END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ "'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN "
				+ "1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL "
				+ "END) = COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE "
				+ "= FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN (COUNT(CASE"
				+ " WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 "
				+ "ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 "
				+ "ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "(CASE WHEN RETURN_TYPE IN ('3A8') AND RETURN_TYPE='RET-1' "
				+ "THEN (IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+ "
				+ "IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) END) "
				+ "AS RET1_OTHERTHAN_REVERSECHARGE FROM RET_PROCESSED_USERINPUT"
				+ " WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' AND "
				+ "RETURN_TABLE IN ('3A8') AND GSTIN IN (:gstin) GROUP BY "
				+ "GSTIN, RETURN_PERIOD,"
				+ "RETURN_TABLE,RETURN_TYPE,PROFIT_CENTRE,LOCATION ,DIVISION,"
				+ "PLANT_CODE,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, "
				+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
				+ "USERACCESS6 UNION ALL SELECT GSTIN,RETURN_PERIOD,"
				+ "PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "PURCHASE_ORGANIZATION,STATUS,"
				+ "SUM (RET1_OTHERTHAN_REVERSECHARGE) AS "
				+ "RET1_OTHERTHAN_REVERSECHARGE FROM "
				+ "( SELECT SUPPLIER_GSTIN AS GSTIN, RETURN_PERIOD AS "
				+ "RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "'' AS PURCHASE_ORGANIZATION, CASE WHEN(COUNT(CASE WHEN "
				+ "IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE "
				+ "NULL END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true "
				+ "AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ "'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN "
				+ "1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL"
				+ " END) = COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE"
				+ " = FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN "
				+ "(COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE "
				+ "then 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE='DR' THEN IFNULL( IGST_AMT,0)+"
				+ "IFNULL( CGST_AMT,0)+IFNULL( SGST_AMT,0)+IFNULL( "
				+ "CESS_AMT_SPECIFIC,0)+IFNULL( CESS_AMT_ADVALOREM,0) END),0) -"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR' THEN IFNULL( IGST_AMT,0)+"
				+ "IFNULL( CGST_AMT,0)+IFNULL( SGST_AMT,0)+"
				+ "IFNULL( CESS_AMT_SPECIFIC,0)+IFNULL( CESS_AMT_ADVALOREM,0) "
				+ "END),0)) AS RET1_OTHERTHAN_REVERSECHARGE FROM "
				+ "ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
				+ "IS_DELETE = FALSE AND DOC_TYPE IN ('DR' ,'CR') AND "
				+ "AN_RETURN_TYPE='ANX1' AND SUPPLIER_GSTIN IN (:gstin)"
				+ " GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD,"
				+ "PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6 "
				+ "UNION ALL "
				+ "SELECT SUPPLIER_GSTIN AS GSTIN, "
				+ "RETURN_PERIOD AS RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,"
				+ "DIVISION,PLANT_CODE,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
				+ " USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
				+ "USERACCESS6,'' AS PURCHASE_ORGANIZATION, CASE "
				+ "WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE"
				+ " THEN 1 ELSE NULL END) = 0 and COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE NULL "
				+ "END) = 0) THEN 'NOT INTTIATED' WHEN (COUNT(CASE WHEN "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT(CASE WHEN "
				+ "GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' WHEN (COUNT(CASE when IS_DELETE = FALSE THEN "
				+ "1 ELSE NULL END) = COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE "
				+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = "
				+ "FALSE then 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = "
				+ "FALSE THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS "
				+ "STATUS, (IFNULL(SUM(CASE WHEN AN_TABLE_SECTION='3C3' THEN "
				+ "IFNULL( IGST_AMT,0)+IFNULL( CGST_AMT,0)+IFNULL( SGST_AMT,0)+"
				+ "IFNULL( CESS_AMT_SPECIFIC,0)+IFNULL( CESS_AMT_ADVALOREM,0) "
				+ "END),0) - IFNULL(SUM(CASE WHEN AN_TABLE_SECTION='3C4' THEN "
				+ "IFNULL( IGST_AMT,0)+IFNULL( CGST_AMT,0)+IFNULL( SGST_AMT,0)+"
				+ "IFNULL( CESS_AMT_SPECIFIC,0)+IFNULL( CESS_AMT_ADVALOREM,0) "
				+ "END),0)) AS RET1_OTHERTHAN_REVERSECHARGE FROM "
				+ "ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
				+ "IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3C3' ,'3C4') "
				+ "AND SUPPLIER_GSTIN IN (:gstin) "
				+ "GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD,PROFIT_CENTRE,"
				+ "LOCATION ,DIVISION,PLANT_CODE,SALES_ORGANIZATION,"
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6 UNION ALL SELECT GSTIN, "
				+ "RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "'' AS PURCHASE_ORGANIZATION, CASE WHEN(COUNT(CASE WHEN "
				+ "IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL "
				+ "END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ "'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN "
				+ "1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' WHEN "
				+ "(COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) ="
				+ " COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = "
				+ "FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN (COUNT(CASE"
				+ " WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1"
				+ " ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 "
				+ "ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "(IFNULL(SUM(CASE WHEN RETURN_TABLE='3C3' THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT,0) END),0) - IFNULL(SUM(CASE WHEN "
				+ "RETURN_TABLE='3C4' THEN IFNULL(IGST_AMT,0)+"
				+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0) "
				+ "END),0) - IFNULL(SUM(CASE WHEN RETURN_TABLE='3C5' THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+"
				+ "IFNULL(CESS_AMT,0) END),0)) AS RET1_OTHERTHAN_REVERSECHARGE "
				+ "FROM RET_PROCESSED_USERINPUT WHERE IS_DELETE = FALSE AND"
				+ " RETURN_TYPE='RET-1' AND RETURN_TABLE IN ('3C3','3C4','3C5') "
				+ "AND GSTIN IN (:gstin) GROUP BY GSTIN, "
				+ "RETURN_PERIOD,RETURN_TABLE,PROFIT_CENTRE,"
				+ "LOCATION ,DIVISION,PLANT_CODE,SALES_ORGANIZATION,"
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6) GROUP BY GSTIN,"
				+ "RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "PURCHASE_ORGANIZATION,STATUS) --RET1_OTHERTHAN_REVERSECHARGE"
				+ " UNION ALL "
				+ "SELECT GSTIN,RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,DIVISION,"
				+ "PLANT_CODE, SALES_ORGANIZATION, DISTRIBUTION_CHANNEL, "
				+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
				+ "USERACCESS6,PURCHASE_ORGANIZATION,STATUS, 0 AS "
				+ "RET1_TOTAL_TAX_LIABILITY, 0 AS RET1_REVERSECHARGE, 0 AS "
				+ "RET1_OTHERTHAN_REVERSECHARGE, RET1_NET_ITC_AVAILABLE,0 AS "
				+ "RET1_TDS, 0 AS RET1_TCS FROM ( SELECT GSTIN,RETURN_PERIOD, "
				+ "PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "PURCHASE_ORGANIZATION,STATUS,SUM(RET1_NET_ITC_AVAILABLE) "
				+ "RET1_NET_ITC_AVAILABLE FROM (SELECT CUST_GSTIN AS GSTIN, "
				+ "RETURN_PERIOD AS RETURN_PERIOD, PROFIT_CENTRE,LOCATION ,"
				+ "DIVISION,PLANT_CODE,'' AS SALES_ORGANIZATION,'' AS "
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6,PURCHASE_ORGANIZATION, "
				+ "CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 and COUNT(CASE "
				+ "WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE "
				+ "NULL END) = 0) THEN 'NOT INTTIATED' WHEN (COUNT(CASE WHEN "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT(CASE WHEN "
				+ "GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1"
				+ " ELSE NULL END) = COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND"
				+ " IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN "
				+ "(COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE"
				+ " then 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "(IFNULL(SUM(CASE WHEN AN_TABLE_SECTION IN "
				+ "('3H','3I','3J','3K','4A10','4A11') THEN IFNULL( IGST_AMT,0)+"
				+ "IFNULL( CGST_AMT,0)+IFNULL( SGST_AMT,0)+"
				+ "IFNULL( CESS_AMT_SPECIFIC,0)+IFNULL( CESS_AMT_ADVALOREM,0) "
				+ "END),0) - IFNULL(SUM(CASE WHEN AN_TABLE_SECTION IN "
				+ "('4B2','4B3') THEN IFNULL( IGST_AMT,0)+IFNULL( CGST_AMT,0)+"
				+ "IFNULL( SGST_AMT,0)+IFNULL( CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL( CESS_AMT_ADVALOREM,0) END),0)) AS "
				+ "RET1_NET_ITC_AVAILABLE FROM ANX_INWARD_DOC_HEADER WHERE "
				+ "IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
				+ "AN_TABLE_SECTION IN "
				+ "('3H','3I','3J','3K','4A10','4A11','4B2','4B3') AND "
				+ "CUST_GSTIN IN (:gstin) GROUP BY "
				+ "CUST_GSTIN, RETURN_PERIOD,AN_TABLE_SECTION,AN_RETURN_TYPE,"
				+ " PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE, USERACCESS1"
				+ ",USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "PURCHASE_ORGANIZATION UNION ALL SELECT GSTIN, RETURN_PERIOD,"
				+ " PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "'' AS PURCHASE_ORGANIZATION, CASE WHEN(COUNT(CASE WHEN "
				+ "IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL "
				+ "END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN "
				+ "'NOT INTTIATED' WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1"
				+ " ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND "
				+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' WHEN "
				+ "(COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = "
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE"
				+ " THEN 1 ELSE NULL END)) THEN 'SAVED' WHEN (COUNT(CASE WHEN "
				+ "IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL"
				+ " END) < COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL"
				+ " END)) THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "(IFNULL(SUM(CASE WHEN RETURN_TABLE IN ('4A4','4A10','4A11')"
				+ " THEN IFNULL( IGST_AMT,0)+IFNULL( CGST_AMT,0)+"
				+ "IFNULL( SGST_AMT,0)+IFNULL( CESS_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN RETURN_TABLE IN ('4B2','4B3','4B4','4B4')"
				+ " THEN IFNULL( IGST_AMT,0)+IFNULL( CGST_AMT,0)+"
				+ "IFNULL( SGST_AMT,0)+IFNULL( CESS_AMT,0) END),0)) "
				+ "AS RET1_NET_ITC_AVAILABLE FROM .RET_PROCESSED_USERINPUT W"
				+ "HERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1' AND"
				+ " RETURN_TABLE IN ('4A4','4A10','4A11','4B2','4B3','4B4','4B4') "
				+ "GROUP BY GSTIN, RETURN_PERIOD,RETURN_TABLE,PROFIT_CENTRE,"
				+ "LOCATION ,DIVISION,PLANT_CODE,SALES_ORGANIZATION,"
				+ "DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6) GROUP BY GSTIN,"
				+ "RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE, "
				+ "SALES_ORGANIZATION, DISTRIBUTION_CHANNEL, USERACCESS1,"
				+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
				+ "PURCHASE_ORGANIZATION,STATUS) "
				+ " UNION ALL "
				+ "SELECT GSTIN,RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,"
				+ "DIVISION,PLANT_CODE, SALES_ORGANIZATION, DISTRIBUTION_CHANNEL,"
				+ " USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
				+ "USERACCESS6,PURCHASE_ORGANIZATION,STATUS,0 AS "
				+ "RET1_TOTAL_TAX_LIABILITY, 0 AS RET1_REVERSECHARGE, 0 AS "
				+ "RET1_OTHERTHAN_REVERSECHARGE, 0 AS RET1_NET_ITC_AVAILABLE,"
				+ "RET1_TDS, 0 AS RET1_TCS FROM (SELECT GSTIN, RETURN_PERIOD,''"
				+ " PROFIT_CENTRE,'' LOCATION ,'' DIVISION,'' PLANT_CODE, '' "
				+ "SALES_ORGANIZATION, '' DISTRIBUTION_CHANNEL, '' USERACCESS1,"
				+ "'' USERACCESS2,'' USERACCESS3,'' USERACCESS4,'' USERACCESS5,"
				+ "'' USERACCESS6,'' PURCHASE_ORGANIZATION,'' STATUS, (CASE "
				+ "WHEN GET_TABLE_SECTION IN ('TDS' ) THEN "
				+ "(IFNULL(SUM( TDS.IGST_AMT),0)+IFNULL(SUM( TDS.CGST_AMT),0)+"
				+ "IFNULL(SUM( TDS.SGST_AMT),0)) END ) AS RET1_TDS FROM "
				+ "GETRET1_TABLE5 TDS WHERE IS_DELETE = FALSE AND "
				+ "GET_TABLE_SECTION IN ('TDS') AND SUPPLIER_GSTIN IN (:gstin)"
				+ " GROUP BY GSTIN,RETURN_PERIOD,GET_TABLE_SECTION) "
				+ "UNION ALL "
				+ "SELECT GSTIN,RETURN_PERIOD,PROFIT_CENTRE,LOCATION ,DIVISION,"
				+ "PLANT_CODE, SALES_ORGANIZATION, DISTRIBUTION_CHANNEL, "
				+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
				+ "USERACCESS6,PURCHASE_ORGANIZATION,STATUS,0 AS "
				+ "RET1_TOTAL_TAX_LIABILITY, 0 AS RET1_REVERSECHARGE, 0 "
				+ "AS RET1_OTHERTHAN_REVERSECHARGE, 0 AS RET1_NET_ITC_AVAILABLE,"
				+ "0 AS RET1_TDS, RET1_TCS FROM (SELECT GSTIN, RETURN_PERIOD,''"
				+ " PROFIT_CENTRE,'' LOCATION ,'' DIVISION,'' PLANT_CODE, '' "
				+ "SALES_ORGANIZATION, '' DISTRIBUTION_CHANNEL, '' USERACCESS1,"
				+ "'' USERACCESS2,'' USERACCESS3,'' USERACCESS4,'' USERACCESS5,"
				+ "'' USERACCESS6,'' PURCHASE_ORGANIZATION, '' STATUS, ("
				+ "CASE WHEN GET_TABLE_SECTION IN ('TCS' ) THEN (IFNULL(SUM( "
				+ "TCS.IGST_AMT),0)+IFNULL(SUM( TCS.CGST_AMT),0)+IFNULL(SUM( "
				+ "TCS.SGST_AMT),0)) END ) AS RET1_TCS FROM GETRET1_TABLE5 TCS "
				+ "WHERE IS_DELETE = FALSE AND GET_TABLE_SECTION IN ('TCS')"
				+ "AND SUPPLIER_GSTIN IN (:gstin) GROUP BY GSTIN, RETURN_PERIOD,"
				+ "GET_TABLE_SECTION)) GROUP BY GSTIN,RETURN_PERIOD,"
				+ "PROFIT_CENTRE,LOCATION ,DIVISION,PLANT_CODE,"
				+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL, "
				+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
				+ "USERACCESS6,PURCHASE_ORGANIZATION,STATUS";
	}

	@Override
	public List<Object[]> getSumryData(String gstin, String type,
			String returnPeriod) {
		// TODO Auto-generated method stub
		return null;
	}
}
*/