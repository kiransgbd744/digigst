package com.ey.advisory.app.data.daos.client;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.anx1.GetAnx2RequestIdWiseReqDto;
import com.ey.advisory.app.docs.dto.anx1.GetAnx2RequestIdWiseRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("GetAnx2RequestIdWiseFetchDaoImpl")
public class GetAnx2RequestIdWiseFetchDaoImpl
		implements GetAnx2RequestIdWiseFetchDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetAnx2RequestIdWiseFetchDaoImpl.class);
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository getAnx1BatchRepository;

	@Override
	public List<GetAnx2RequestIdWiseRespDto> getAnx2DetailsByRequestId(
			GetAnx2RequestIdWiseReqDto idWiseReqDto) throws Exception {

		String requestId = idWiseReqDto.getRequestId();
		String taxPeriod = idWiseReqDto.getTaxPeriod();
		List<String> initiatedBy = idWiseReqDto.getInitiatedBy();
		String initiatedDate = idWiseReqDto.getInitiationDateTime();
		String completionDate = idWiseReqDto.getCompletionDateTime();
		Map<String, List<String>> dataSecAttrs = idWiseReqDto.getDataSecAttrs();
		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null, sgstin = null;

		List<String> gstinList = null;
		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;

		StringBuffer queryStr = createQueryString(idWiseReqDto, initiatedDate,
				completionDate, dataSecAttrs, profitCenter, sgstin, plant,
				division, location, sales, gstinList, distChannel, ud1, ud2,
				ud3, ud4, ud5, ud6, pcList, plantList, salesList, divisionList,
				locationList, distList, ud1List, ud2List, ud3List, ud4List,
				ud5List, ud6List);
		LOGGER.debug("outQueryStr-->" + queryStr);

		Query query = entityManager.createNativeQuery(queryStr.toString());

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					pcList = dataSecAttrs.get(OnboardingConstant.PC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					plant = key;
					plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					divisionList = dataSecAttrs
							.get(OnboardingConstant.DIVISION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					locationList = dataSecAttrs
							.get(OnboardingConstant.LOCATION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					salesList = dataSecAttrs.get(OnboardingConstant.SO);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					distList = dataSecAttrs.get(OnboardingConstant.DC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
			if (gstinList != null && gstinList.size() > 0
					&& !gstinList.contains("")) {
				query.setParameter("sgstin", gstinList);
			}
			if (profitCenter != null && !profitCenter.isEmpty()
					&& !profitCenter.isEmpty() && pcList != null
					&& pcList.size() > 0) {
				query.setParameter("pcList", pcList);
			}
			if (plant != null && !plant.isEmpty() && !plant.isEmpty()
					&& plantList != null && plantList.size() > 0) {
				query.setParameter("plantList", plantList);
			}
			if (sales != null && !sales.isEmpty() && salesList != null
					&& salesList.size() > 0) {
				query.setParameter("salesList", salesList);
			}
			if (division != null && !division.isEmpty() && divisionList != null
					&& divisionList.size() > 0) {
				query.setParameter("divisionList", divisionList);
			}
			if (location != null && !location.isEmpty() && locationList != null
					&& locationList.size() > 0) {
				query.setParameter("locationList", locationList);
			}
			if (distChannel != null && !distChannel.isEmpty()
					&& distList != null && distList.size() > 0) {
				query.setParameter("distList", distList);
			}
			if (ud1 != null && !ud1.isEmpty() && ud1List != null
					&& ud1List.size() > 0) {
				query.setParameter("ud1List", ud1List);
			}
			if (ud2 != null && !ud2.isEmpty() && ud2List != null
					&& ud2List.size() > 0) {
				query.setParameter("ud2List", ud2List);
			}
			if (ud3 != null && !ud3.isEmpty() && ud3List != null
					&& ud3List.size() > 0) {
				query.setParameter("ud3List", ud3List);
			}
			if (ud4 != null && !ud4.isEmpty() && ud4List != null
					&& ud4List.size() > 0) {
				query.setParameter("ud4List", ud4List);
			}
			if (ud5 != null && !ud5.isEmpty() && ud5List != null
					&& ud5List.size() > 0) {
				query.setParameter("ud5List", ud5List);
			}
			if (ud6 != null && !ud6.isEmpty() && ud6List != null
					&& ud6List.size() > 0) {
				query.setParameter("ud6List", ud6List);
			}

			if (requestId != null && !requestId.isEmpty()) {
				query.setParameter("requestId", Integer.parseInt(requestId));
			}
			if (taxPeriod != null && !taxPeriod.isEmpty()) {
				query.setParameter("taxPeriod", taxPeriod);
			}
			if (initiatedBy.size() > 0 && initiatedBy != null) {
				query.setParameter("initiatedBy", initiatedBy);
			}
			if (initiatedDate != null && !initiatedDate.isEmpty()) {
				query.setParameter("initiatedDate", initiatedDate);
			}
			if (completionDate != null && !completionDate.isEmpty()) {
				query.setParameter("completionDate", completionDate);
			}

		}
		@SuppressWarnings("unchecked")
		List<Object[]> Qlist = query.getResultList();
		LOGGER.debug("Outward data list from database is-->" + Qlist);
		return convertObjetListToDataList(Qlist);
	}

	private List<GetAnx2RequestIdWiseRespDto> convertObjetListToDataList(
			List<Object[]> qlist) throws Exception {
		List<GetAnx2RequestIdWiseRespDto> dtos = new ArrayList<>();
		for (Object obj[] : qlist) {
			GetAnx2RequestIdWiseRespDto dto = new GetAnx2RequestIdWiseRespDto();
			BigInteger in = GenUtil.getBigInteger(obj[0]);
			// Long in2 = in.longValue();
			String requestId2 = in.toString();
			// BigInteger integ = new BigInteger(obj[0].toString());
			List<String> requestGstins = getAnx1BatchRepository
					.findGstinrequestId(in.longValue());
			dto.setRequestId(requestId2);
			dto.setGstin(requestGstins);
			dto.setTaxPeriod((String) obj[2]);
			dto.setStatus((String) obj[3]);
			dto.setInitiatedBy((String) obj[4]);
			String intiateTimeStamp = String.valueOf(obj[5]);
			String completeTimeStamp = String.valueOf(obj[6]);
			if (intiateTimeStamp == null || intiateTimeStamp == "null") {
				intiateTimeStamp = "";
				dto.setInitiationDateTime(intiateTimeStamp);
			} else {
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(intiateTimeStamp);
				String newstr = new SimpleDateFormat("yyyy-MM-dd : HH:mm:ss")
						.format(date);
				dto.setInitiationDateTime(newstr);
			}
			if (completeTimeStamp == null || completeTimeStamp == "null") {
				completeTimeStamp = "";
				dto.setInitiationDateTime(completeTimeStamp);
			} else {
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(completeTimeStamp);
				String newstr = new SimpleDateFormat("yyyy-MM-dd : HH:mm:ss")
						.format(date);
				dto.setCompletionDateTime(newstr);
			}

			dtos.add(dto);
		}
		return dtos;
	}

	private StringBuffer createQueryString(
			GetAnx2RequestIdWiseReqDto idWiseReqDto, String initiatedDate,
			String completionDate, Map<String, List<String>> dataSecAttrs,
			String profitCenter, String sgstin, String plant, String division,
			String location, String sales, List<String> gstinList, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			String distChannel, List<String> pcList, List<String> plantList,
			List<String> salesList, List<String> divisionList,
			List<String> locationList, List<String> distList,
			List<String> ud1List, List<String> ud2List, List<String> ud3List,
			List<String> ud4List, List<String> ud5List, List<String> ud6List) {
		StringBuffer conditions = new StringBuffer();
		String requestId = idWiseReqDto.getRequestId();
		String taxPeriod = idWiseReqDto.getTaxPeriod();
		List<String> initiatedBy = idWiseReqDto.getInitiatedBy();

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

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
			if (requestId != null && !requestId.isEmpty()) {
				conditions.append(" AND ID = :requestId");
			}
			if (taxPeriod != null && !taxPeriod.isEmpty()) {
				conditions.append(" AND TAX_PERIOD = :taxPeriod");
			}
			if (initiatedBy.size() > 0 && initiatedBy != null) {
				conditions.append(" AND INITITAED_BY IN :initiatedBy");
			}
			if (initiatedDate != null && !initiatedDate.isEmpty()) {
				conditions.append(
						" AND TO_CHAR(INITIATED_ON,'DD-MM-YYYY') = :initiatedDate");
			}
			if (completionDate != null && !completionDate.isEmpty()) {
				conditions.append(
						" AND TO_CHAR(COMPLETED_ON,'DD-MM-YYYY') = :completionDate");
			}

		}
		if (gstinList != null && gstinList.size() > 0
				&& !gstinList.contains("")) {
			conditions.append(" AND SUPPLIER_GSTIN IN :sgstin");
		}

		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& pcList.size() > 0) {
			conditions.append(" AND  PROFIT_CENTRE IN :pcList");
		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& plantList.size() > 0) {
			conditions.append(" AND PLANT_CODE IN :plantList");
		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& salesList.size() > 0) {
			conditions.append(" AND SALES_ORGANIZATION IN :salesList");
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& distList.size() > 0) {
			conditions.append(" AND  DISTRIBUTION_CHANNEL IN :distList");
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& divisionList.size() > 0) {
			conditions.append(" AND  DIVISION IN :divisionList");
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& locationList.size() > 0) {
			conditions.append(" AND  LOCATION IN :locationList");
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& ud1List.size() > 0) {
			conditions.append(" AND  USERACCESS1 IN :ud1List");
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& ud2List.size() > 0) {
			conditions.append(" AND  USERACCESS2 IN :ud2List");
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& ud3List.size() > 0) {
			conditions.append(" AND  USERACCESS3 IN :ud3List");
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& ud4List.size() > 0) {
			conditions.append(" AND  USERACCESS4 IN :ud4List");
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& ud5List.size() > 0) {
			conditions.append(" AND  USERACCESS5 IN :ud5List");
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& ud6List.size() > 0) {
			conditions.append(" AND  USERACCESS6 IN :ud6List");
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ID,NO_OF_GSTINS,TAX_PERIOD,STATUS,"
				+ " INITITAED_BY,INITIATED_ON,COMPLETED_ON"
				+ " FROM REQUEST_ID_WISE ");
		if (!conditions.toString().equals("")) {
			buffer.append(" WHERE IS_DELETE = FALSE ");
			buffer.append(conditions.toString());
		}
		return buffer;
	}

}
