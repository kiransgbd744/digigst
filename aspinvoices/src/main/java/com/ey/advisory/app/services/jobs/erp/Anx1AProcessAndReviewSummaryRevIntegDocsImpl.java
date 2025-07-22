package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Anx1AHeaderItemProcessReviewSummaryReqDto;
import com.ey.advisory.app.docs.dto.erp.Anx1AHeaderItemProcessSummaryReqDto;
import com.ey.advisory.app.docs.dto.erp.Anx1AHeaderProcessSummaryReqDto;
import com.ey.advisory.app.docs.dto.erp.Anx1AHeaderReviewSummaryReqDto;
import com.ey.advisory.app.docs.dto.erp.Anx1AItemReviewSummaryReqDto;
import com.ey.advisory.app.docs.dto.erp.Anx1AProcessReviewSummaryReqHeaderItemDto;
import com.ey.advisory.core.api.APIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service("Anx1AProcessAndReviewSummaryRevIntegDocsImpl")
public class Anx1AProcessAndReviewSummaryRevIntegDocsImpl
		implements Anx1AProcessAndReviewSummaryRevIntegDocs {

	public static final String Empty = "";
	private Logger LOGGER = LoggerFactory
			.getLogger(Anx1AProcessAndReviewSummaryRevIntegDocsImpl.class);
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public static final String OUTWARD = "OUTWARD";
	public static final String INWARD = "INWARD";

	private static final String DATE_FORMATE = "yyyy-MM-dd";

	List<String> returnPeriods = new ArrayList<>();

	private static final int MAX = 20;

	public Anx1AProcessReviewSummaryReqHeaderItemDto convertProcReviewSum(
			String gstin, String entityPan, String stateName, String entityName,
			String companyCode) {
		Anx1AProcessReviewSummaryReqHeaderItemDto finalHeaderItemDto = new Anx1AProcessReviewSummaryReqHeaderItemDto();
		List<Anx1AHeaderItemProcessReviewSummaryReqDto> dtos = new ArrayList<>();

		List<Object[]> outwardProcessSummary = getProcessSummary(
				APIConstants.OUTWARD, gstin);
		List<Object[]> inwardProcessSummary = getProcessSummary(
				APIConstants.INWARD, gstin);

		List<Anx1AHeaderItemProcessSummaryReqDto> itemProcess = itemProcessSummary(
				stateName, outwardProcessSummary, inwardProcessSummary);

		Set<String> returnPeriods = new HashSet<>();
		Map<String, List<Anx1AHeaderItemProcessSummaryReqDto>> mapProcess = mapProcessSummary(
				itemProcess);

		List<String> returnPeriodList = new ArrayList<>();

		itemProcess.forEach(finalDto -> {
			returnPeriods.add(finalDto.getRetPer());
		});

		returnPeriods.forEach(returnPeriod -> {
			returnPeriodList.add(returnPeriod);
		});
		List<Object[]> outwardReviewSummary = new ArrayList<>();
		List<Object[]> inwardReviewSummary = new ArrayList<>();
		List<Object[]> table4ReviewSummary = new ArrayList<>();
		if (!returnPeriodList.isEmpty()) {
			outwardReviewSummary = getReviewSummary(APIConstants.OUTWARD, gstin,
					returnPeriodList);
			inwardReviewSummary = getReviewSummary(APIConstants.INWARD, gstin,
					returnPeriodList);
			table4ReviewSummary = getReviewSummary(APIConstants.TABLE4, gstin,
					returnPeriodList);
		}
		List<Anx1AItemReviewSummaryReqDto> itemReviewSumDtos = convertObjToReviewSummary(
				outwardReviewSummary, inwardReviewSummary, table4ReviewSummary);

		Map<String, List<Anx1AItemReviewSummaryReqDto>> mapItemReviewSumDtos = mapReviewSummary(
				itemReviewSumDtos);

		finalSummary(mapProcess, mapItemReviewSumDtos, dtos, entityName,
				entityPan, companyCode);
		finalHeaderItemDto.setDtos(dtos);
		return finalHeaderItemDto;
	}

	private List<Anx1AHeaderItemProcessSummaryReqDto> itemProcessSummary(
			String stateName, List<Object[]> outwardObjs,
			List<Object[]> inwardObjs) {
		List<Anx1AHeaderItemProcessSummaryReqDto> itemProcessSummary = new ArrayList<>();

		if (outwardObjs != null) {
			outwardObjs.forEach(obj -> {
				Anx1AHeaderItemProcessSummaryReqDto reqDto = new Anx1AHeaderItemProcessSummaryReqDto();

				reqDto.setState(stateName);
				convertObjToProcess(obj, reqDto);
				itemProcessSummary.add(reqDto);
			});
		}
		if (inwardObjs != null) {
			inwardObjs.forEach(obj -> {
				Anx1AHeaderItemProcessSummaryReqDto reqDto = new Anx1AHeaderItemProcessSummaryReqDto();
				reqDto.setState(stateName);
				convertObjToProcess(obj, reqDto);
				itemProcessSummary.add(reqDto);
			});
		}
		return itemProcessSummary;
	}

	// Map the Dockey based on Return field and Gstin
	private Map<String, List<Anx1AHeaderItemProcessSummaryReqDto>> mapProcessSummary(
			List<Anx1AHeaderItemProcessSummaryReqDto> procSummReqDto) {
		Map<String, List<Anx1AHeaderItemProcessSummaryReqDto>> itemProcessSummMap = new LinkedHashMap<>();
		procSummReqDto.forEach(dto -> {
			StringBuilder docs = new StringBuilder();
			docs.append(dto.getGstinNum());
			docs.append("-");
			docs.append(dto.getRetPer());
			String docKey = docs.toString();
			if (itemProcessSummMap.containsKey(docKey)) {
				List<Anx1AHeaderItemProcessSummaryReqDto> dtos = itemProcessSummMap
						.get(docKey);
				dtos.add(dto);
				itemProcessSummMap.put(docKey, dtos);
			} else {
				List<Anx1AHeaderItemProcessSummaryReqDto> dtos = new ArrayList<>();
				dtos.add(dto);
				itemProcessSummMap.put(docKey, dtos);
			}
		});
		return itemProcessSummMap;
	}

	private Map<String, List<Anx1AHeaderItemProcessSummaryReqDto>> mapProcessSummaryForType(
			List<Anx1AHeaderItemProcessSummaryReqDto> procSummReqDto) {
		Map<String, List<Anx1AHeaderItemProcessSummaryReqDto>> itemProcessSummMap = new LinkedHashMap<>();
		procSummReqDto.forEach(dto -> {
			StringBuilder key = new StringBuilder();
			key.append(dto.getDataType());
			key.append("_");
			key.append(dto.getProfitCenter());
			key.append("_");
			key.append(dto.getPlantCode());
			key.append("_");
			key.append(dto.getLocation());
			key.append("_");
			key.append(dto.getDistChannel());
			key.append("_");
			key.append(dto.getDivision());
			key.append("_");
			key.append(dto.getSalesOrganization());
			key.append("_");
			key.append(dto.getUseraccess1());
			key.append("_");
			key.append(dto.getUseraccess2());
			key.append("_");
			key.append(dto.getUseraccess3());
			key.append("_");
			key.append(dto.getUseraccess4());
			key.append("_");
			key.append(dto.getUseraccess5());
			key.append("_");
			key.append(dto.getUseraccess6());
			String docKey = key.toString();
			if (itemProcessSummMap.containsKey(docKey)) {
				List<Anx1AHeaderItemProcessSummaryReqDto> dtos = itemProcessSummMap
						.get(docKey);
				dtos.add(dto);
				itemProcessSummMap.put(docKey, dtos);
			} else {
				List<Anx1AHeaderItemProcessSummaryReqDto> dtos = new ArrayList<>();
				dtos.add(dto);
				itemProcessSummMap.put(docKey, dtos);
			}
		});
		return itemProcessSummMap;
	}

	private void finalSummary(
			Map<String, List<Anx1AHeaderItemProcessSummaryReqDto>> procSumDtos,
			Map<String, List<Anx1AItemReviewSummaryReqDto>> mapItemReviewSumDtos,
			List<Anx1AHeaderItemProcessReviewSummaryReqDto> headerItemDtos,
			String entityName, String entityPan, String companyCode) {

		procSumDtos.entrySet().forEach(procSumDto -> {
			Anx1AHeaderItemProcessReviewSummaryReqDto finalHeaderItemDto = new Anx1AHeaderItemProcessReviewSummaryReqDto();
			List<Anx1AHeaderItemProcessSummaryReqDto> dtos = procSumDto
					.getValue();

			Map<String, List<Anx1AHeaderItemProcessSummaryReqDto>> processSummaryForTypeMap = mapProcessSummaryForType(
					dtos);
			Anx1AHeaderProcessSummaryReqDto headerProcSumDto = new Anx1AHeaderProcessSummaryReqDto();
			List<Anx1AHeaderItemProcessSummaryReqDto> itemProcessSummary = new ArrayList<>();
			processSummaryForTypeMap.entrySet()
					.forEach(processSummaryForType -> {
						BigInteger count = BigInteger.ZERO;
						BigDecimal eyOutsupp = BigDecimal.ZERO;
						BigDecimal igst = BigDecimal.ZERO;
						BigDecimal cgst = BigDecimal.ZERO;
						BigDecimal sgst = BigDecimal.ZERO;
						BigDecimal cess = BigDecimal.ZERO;

						List<Anx1AHeaderItemProcessSummaryReqDto> itemProcessSummaryReqDto = processSummaryForType
								.getValue();
						if (itemProcessSummaryReqDto != null
								&& !itemProcessSummaryReqDto.isEmpty()) {
							Anx1AHeaderItemProcessSummaryReqDto child = new Anx1AHeaderItemProcessSummaryReqDto();
							for (Anx1AHeaderItemProcessSummaryReqDto dto : itemProcessSummaryReqDto) {
								finalHeaderItemDto.setRetPer(dto.getRetPer());
								finalHeaderItemDto
										.setGstinNum(dto.getGstinNum());
								finalHeaderItemDto.setEntityName(entityName);
								finalHeaderItemDto.setEntityPan(entityPan);
								finalHeaderItemDto.setCompanyCode(companyCode);
								child.setDataType(dto.getDataType());
								child.setState(dto.getState());
								count = count.add(dto.getEyTotDoc());
								String docType = dto.getDocType();
								if (docType != null && (docType.equals("RCR")
										|| docType.equals("CR")
										|| docType.equals("RFV")
										|| docType.equals("RRFV"))) {
									eyOutsupp = eyOutsupp
											.add(dto.getEyOutsupp());
									igst = igst.add(dto.getEyIgstval());
									cgst = cgst.add(dto.getEyCgstval());
									sgst = sgst.add(dto.getEySgstval());
									cess = cess.add(dto.getEyCessval());
								} else {
									eyOutsupp = eyOutsupp
											.subtract(dto.getEyOutsupp());
									igst = igst.subtract(dto.getEyIgstval());
									cgst = cgst.subtract(dto.getEyCgstval());
									sgst = sgst.subtract(dto.getEySgstval());
									cess = cess.subtract(dto.getEyCessval());
								}
								child.setEyTotDoc(count);
								child.setEyOutsupp(eyOutsupp);
								child.setEyIgstval(igst);
								child.setEyCgstval(cgst);
								child.setEySgstval(sgst);
								child.setEyCessval(cess);
								child.setEyStatus(dto.getEyStatus());
								child.setEyDate(dto.getEyDate());
								child.setEyTime(dto.getEyTime());
								child.setProfitCenter(dto.getProfitCenter());
								child.setPlantCode(dto.getPlantCode());
								child.setLocation(dto.getLocation());
								child.setSalesOrganization(
										dto.getSalesOrganization());
								child.setDistChannel(dto.getDistChannel());
								child.setDivision(dto.getDivision());
								child.setUseraccess1(dto.getUseraccess1());
								child.setUseraccess2(dto.getUseraccess2());
								child.setUseraccess3(dto.getUseraccess3());
								child.setUseraccess4(dto.getUseraccess4());
								child.setUseraccess5(dto.getUseraccess5());
								child.setUseraccess6(dto.getUseraccess6());
							}
							itemProcessSummary.add(child);
							headerProcSumDto
									.setItemProcessSummary(itemProcessSummary);
						}
					});
			finalHeaderItemDto.setHeaderProcessSummDto(headerProcSumDto);

			/* Review Summary Screen */
			Anx1AHeaderReviewSummaryReqDto headerReviewSummDto = new Anx1AHeaderReviewSummaryReqDto();
			reviewSummary(mapItemReviewSumDtos, finalHeaderItemDto,
					headerReviewSummDto);
			finalHeaderItemDto.setHeaderReviewSummDto(headerReviewSummDto);
			headerItemDtos.add(finalHeaderItemDto);

		});
	}

	private void reviewSummary(
			Map<String, List<Anx1AItemReviewSummaryReqDto>> mapItemReviewSumDtos,
			Anx1AHeaderItemProcessReviewSummaryReqDto child,
			Anx1AHeaderReviewSummaryReqDto headerReviewSummDto) {
		StringBuilder sb = new StringBuilder();
		sb.append(child.getGstinNum());
		sb.append("-");
		sb.append(child.getRetPer());
		String docKey = sb.toString();
		List<Anx1AItemReviewSummaryReqDto> itemReviewSummaryReqDtos = mapItemReviewSumDtos
				.get(docKey);
		headerReviewSummDto.setItemReview(itemReviewSummaryReqDtos);
	}

	// List out Process Summary data
	public List<Object[]> getProcessSummary(String type, String gstin) {

		List<Object[]> objs = new ArrayList<>();
		String sql = null;
		try {
			if (APIConstants.OUTWARD.equalsIgnoreCase(type)) {
				sql = getQueryOutwardProcessSummary();
			} else if (APIConstants.INWARD.equalsIgnoreCase(type)) {
				sql = getQueryInwardProcessSummary();
			}
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter("gstin", gstin);
			objs = q.getResultList();
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return objs;
	}

	private void convertObjToProcess(Object[] obj,
			Anx1AHeaderItemProcessSummaryReqDto reqDto) {

		reqDto.setGstinNum(obj[0] != null ? String.valueOf(obj[0]) : null);
		reqDto.setRetPer(obj[1] != null ? String.valueOf(obj[1]) : null);
		reqDto.setDocType(obj[3] != null ? String.valueOf(obj[3]) : null);
		reqDto.setDataType(obj[4] != null ? String.valueOf(obj[4]) : null);
		reqDto.setEyTotDoc(obj[5] != null
				? new BigInteger(String.valueOf(obj[5])) : BigInteger.ZERO);
		reqDto.setEyOutsupp(obj[6] != null
				? new BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);
		reqDto.setEyIgstval(obj[7] != null
				? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
		reqDto.setEyCgstval(obj[8] != null
				? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);
		reqDto.setEySgstval(obj[9] != null
				? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
		reqDto.setEyCessval(obj[10] != null
				? new BigDecimal(String.valueOf(obj[10])) : BigDecimal.ZERO);
		reqDto.setEyStatus(obj[11] != null ? String.valueOf(obj[11]) : null);

		String currentDateTime = null;
		String profitCenter = obj[12] != null ? String.valueOf(obj[12]) : null;
		if (profitCenter != null) {
			if (profitCenter.length() > MAX) {
				reqDto.setProfitCenter(profitCenter.substring(0, MAX));
			} else {
				reqDto.setProfitCenter(profitCenter);
			}
		}
		String plantCode = obj[13] != null ? String.valueOf(obj[13]) : null;
		if (plantCode != null) {
			if (plantCode.length() > MAX) {
				reqDto.setPlantCode(plantCode.substring(MAX));
			} else {
				reqDto.setPlantCode(plantCode);
			}
		}
		String location = obj[14] != null ? String.valueOf(obj[14]) : null;
		if (location != null) {
			if (location.length() > MAX) {
				reqDto.setLocation(location.substring(0, MAX));
			} else {
				reqDto.setLocation(location);
			}
		}
		if (APIConstants.OUTWARD.equalsIgnoreCase(reqDto.getDataType())) {
			currentDateTime = outwardProcessSummaryConversion(obj, reqDto);
		} else if (APIConstants.INWARD.equalsIgnoreCase(reqDto.getDataType())) {

			currentDateTime = inwardProcessSummaryConversion(obj, reqDto);
		}

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
		reqDto.setEyDate(currentDate);
		reqDto.setEyTime(currentTime);
	}

	private String inwardProcessSummaryConversion(Object[] obj,
			Anx1AHeaderItemProcessSummaryReqDto reqDto) {
		String currentDateTime;
		String parchOrg = obj[15] != null ? String.valueOf(obj[15]) : null;
		if (parchOrg != null) {
			if (parchOrg.length() > MAX) {
				reqDto.setParchOrganization(parchOrg.substring(0, MAX));
			} else {
				reqDto.setParchOrganization(parchOrg);
			}
		}

		String division = obj[16] != null ? String.valueOf(obj[16]) : null;
		if (division != null) {
			if (division.length() > MAX) {
				reqDto.setDivision(division.substring(0, MAX));
			} else {
				reqDto.setDivision(division);
			}
		}
		String userAccess1 = obj[17] != null ? String.valueOf(obj[17]) : null;
		if (userAccess1 != null) {
			if (userAccess1.length() > MAX) {
				reqDto.setUseraccess1(userAccess1.substring(0, MAX));
			} else {
				reqDto.setUseraccess1(userAccess1);
			}
		}
		String userAccess2 = obj[18] != null ? String.valueOf(obj[18]) : null;
		if (userAccess2 != null) {
			if (userAccess2.length() > MAX) {
				reqDto.setUseraccess2(userAccess2.substring(0, MAX));
			} else {
				reqDto.setUseraccess2(userAccess2);
			}
		}
		String userAccess3 = obj[19] != null ? String.valueOf(obj[19]) : null;
		if (userAccess3 != null) {
			if (userAccess3.length() > MAX) {
				reqDto.setUseraccess3(userAccess3.substring(0, MAX));
			} else {
				reqDto.setUseraccess3(userAccess3);
			}
		}
		String userAccess4 = obj[20] != null ? String.valueOf(obj[20]) : null;
		if (userAccess4 != null) {
			if (userAccess4.length() > MAX) {
				reqDto.setUseraccess4(userAccess4.substring(0, MAX));
			} else {
				reqDto.setUseraccess4(userAccess4);
			}
		}
		String userAccess5 = obj[21] != null ? String.valueOf(obj[21]) : null;
		if (userAccess5 != null) {
			if (userAccess5.length() > MAX) {
				reqDto.setUseraccess5(userAccess5.substring(0, MAX));
			} else {
				reqDto.setUseraccess5(userAccess5);
			}
		}
		String userAccess6 = obj[22] != null ? String.valueOf(obj[22]) : null;
		if (userAccess6 != null) {
			if (userAccess6.length() > MAX) {
				reqDto.setUseraccess6(userAccess6.substring(0, MAX));
			} else {
				reqDto.setUseraccess6(userAccess6);
			}
		}
		currentDateTime = obj[23] != null ? String.valueOf(obj[23]) : null;
		return currentDateTime;
	}

	private String outwardProcessSummaryConversion(Object[] obj,
			Anx1AHeaderItemProcessSummaryReqDto reqDto) {
		String currentDateTime;
		String salesOrg = obj[15] != null ? String.valueOf(obj[15]) : null;
		if (salesOrg != null) {
			if (salesOrg.length() > MAX) {
				reqDto.setSalesOrganization(salesOrg.substring(0, MAX));
			} else {
				reqDto.setSalesOrganization(salesOrg);
			}
		}
		String distChannel = obj[16] != null ? String.valueOf(obj[16]) : null;
		if (distChannel != null) {
			if (distChannel.length() > MAX) {
				reqDto.setDistChannel(distChannel.substring(0, MAX));
			} else {
				reqDto.setDistChannel(distChannel);
			}
		}
		String division = obj[17] != null ? String.valueOf(obj[17]) : null;
		if (division != null) {
			if (division.length() > MAX) {
				reqDto.setDivision(division.substring(0, MAX));
			} else {
				reqDto.setDivision(division);
			}
		}
		String userAccess1 = obj[18] != null ? String.valueOf(obj[18]) : null;
		if (userAccess1 != null) {
			if (userAccess1.length() > MAX) {
				reqDto.setUseraccess1(userAccess1.substring(0, MAX));
			} else {
				reqDto.setUseraccess1(userAccess1);
			}
		}
		String userAccess2 = obj[19] != null ? String.valueOf(obj[19]) : null;
		if (userAccess2 != null) {
			if (userAccess2.length() > MAX) {
				reqDto.setUseraccess2(userAccess2.substring(0, MAX));
			} else {
				reqDto.setUseraccess2(userAccess2);
			}
		}
		String userAccess3 = obj[20] != null ? String.valueOf(obj[20]) : null;
		if (userAccess3 != null) {
			if (userAccess3.length() > MAX) {
				reqDto.setUseraccess3(userAccess3.substring(0, MAX));
			} else {
				reqDto.setUseraccess3(userAccess3);
			}
		}
		String userAccess4 = obj[21] != null ? String.valueOf(obj[21]) : null;
		if (userAccess4 != null) {
			if (userAccess4.length() > MAX) {
				reqDto.setUseraccess4(userAccess4.substring(0, MAX));
			} else {
				reqDto.setUseraccess4(userAccess4);
			}
		}
		String userAccess5 = obj[22] != null ? String.valueOf(obj[22]) : null;
		if (userAccess5 != null) {
			if (userAccess5.length() > MAX) {
				reqDto.setUseraccess5(userAccess5.substring(0, MAX));
			} else {
				reqDto.setUseraccess5(userAccess5);
			}
		}
		String userAccess6 = obj[23] != null ? String.valueOf(obj[23]) : null;
		if (userAccess6 != null) {
			if (userAccess6.length() > MAX) {
				reqDto.setUseraccess6(userAccess6.substring(0, MAX));
			} else {
				reqDto.setUseraccess6(userAccess6);
			}
		}
		currentDateTime = obj[24] != null ? String.valueOf(obj[24]) : null;
		return currentDateTime;
	}

	private List<Object[]> getReviewSummary(String type, String gstin,
			List<String> returnPeriod) {

		List<Object[]> objs = new ArrayList<>();
		String sql = null;
		try {
			if (APIConstants.OUTWARD.equalsIgnoreCase(type)) {
				sql = getOutwardReviewSummaryQuery();
			} else if (APIConstants.INWARD.equalsIgnoreCase(type)) {
				sql = getInwardReviewSummaryQuery();
			} else if (APIConstants.TABLE4.equalsIgnoreCase(type)) {
				sql = table4ReviewSummary();
			}
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter("gstin", gstin);
			q.setParameter("returnPeriod", returnPeriod);
			objs = q.getResultList();
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return objs;
	}

	private Map<String, List<Anx1AItemReviewSummaryReqDto>> mapReviewSummary(
			List<Anx1AItemReviewSummaryReqDto> reviewSumReqDtos) {

		Map<String, List<Anx1AItemReviewSummaryReqDto>> mapReviewSummaryReqDto = new HashMap<>();
		reviewSumReqDtos.forEach(reviewSumReqDto -> {
			StringBuilder sb = new StringBuilder();
			sb.append(reviewSumReqDto.getGstinNum());
			sb.append("-");
			sb.append(reviewSumReqDto.getRetPer());
			String docKey = sb.toString();
			if (mapReviewSummaryReqDto.containsKey(docKey)) {
				List<Anx1AItemReviewSummaryReqDto> itemReviewSum = mapReviewSummaryReqDto
						.get(docKey);
				mapReviewSummaryReqDto.put(docKey, itemReviewSum);
			} else {
				List<Anx1AItemReviewSummaryReqDto> itemReviewSumReqDtos = new ArrayList<>();
				itemReviewSumReqDtos.add(reviewSumReqDto);
				mapReviewSummaryReqDto.put(docKey, itemReviewSumReqDtos);
			}
		});
		return mapReviewSummaryReqDto;
	}

	private List<Anx1AItemReviewSummaryReqDto> convertObjToReviewSummary(
			List<Object[]> outwardReviewSummary,
			List<Object[]> inwardReviewSummary,
			List<Object[]> table4ReviewSummary) {
		List<Anx1AItemReviewSummaryReqDto> reviewSummaryDetails = new ArrayList<>();
		if (outwardReviewSummary != null) {
			for (Object[] obj : outwardReviewSummary) {
				Anx1AItemReviewSummaryReqDto dto = new Anx1AItemReviewSummaryReqDto();
				outwardConvertion(obj, dto);
				reviewSummaryDetails.add(dto);
			}
			if (inwardReviewSummary != null) {
				inwardReviewSummary.forEach(obj -> {
					Anx1AItemReviewSummaryReqDto dto = new Anx1AItemReviewSummaryReqDto();
					inwardReviewSummaryConversion(obj, dto);
					reviewSummaryDetails.add(dto);
				});
			}
			if (table4ReviewSummary != null) {
				table4ReviewSummary.forEach(obj -> {
					Anx1AItemReviewSummaryReqDto dto = new Anx1AItemReviewSummaryReqDto();
					table4ReviewConversion(obj, dto);
					reviewSummaryDetails.add(dto);
				});
			}
		}
		return reviewSummaryDetails;
	}

	private void inwardReviewSummaryConversion(Object[] obj,
			Anx1AItemReviewSummaryReqDto dto) {
		dto.setGstinNum(obj[0] != null ? String.valueOf(obj[0]) : null);
		dto.setRetPer(obj[1] != null ? String.valueOf(obj[1]) : null);
		dto.setDataType(INWARD);
		dto.setDoctype(obj[2] != null ? String.valueOf(obj[2]) : null);

		dto.setTaxTable(obj[3] != null ? String.valueOf(obj[3]) : null);
		dto.setAspTotDoc(obj[4] != null ? new BigInteger(String.valueOf(obj[4]))
				: BigInteger.ZERO);
		dto.setAspInval(obj[5] != null ? new BigDecimal(String.valueOf(obj[5]))
				: BigDecimal.ZERO);
		dto.setAspTbval(obj[6] != null ? new BigDecimal(String.valueOf(obj[6]))
				: BigDecimal.ZERO);
		dto.setAspTxval(obj[7] != null ? new BigDecimal(String.valueOf(obj[7]))
				: BigDecimal.ZERO);
		dto.setAspIgstval(obj[8] != null
				? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);
		dto.setAspCgstval(obj[9] != null
				? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
		dto.setAspSgstval(obj[10] != null
				? new BigDecimal(String.valueOf(obj[10])) : BigDecimal.ZERO);
		dto.setAspCessval(obj[11] != null
				? new BigDecimal(String.valueOf(obj[11])) : BigDecimal.ZERO);

		dto.setMemoTotDoc(obj[4] != null
				? new BigInteger(String.valueOf(obj[4])) : BigInteger.ZERO);
		dto.setMemoInval(obj[5] != null ? new BigDecimal(String.valueOf(obj[5]))
				: BigDecimal.ZERO);
		dto.setMemoTbval(obj[6] != null ? new BigDecimal(String.valueOf(obj[6]))
				: BigDecimal.ZERO);
		dto.setMemoTxval(obj[12] != null
				? new BigDecimal(String.valueOf(obj[12])) : BigDecimal.ZERO);
		dto.setMemoIgstval(obj[13] != null
				? new BigDecimal(String.valueOf(obj[13])) : BigDecimal.ZERO);
		dto.setMemoCgstval(obj[14] != null
				? new BigDecimal(String.valueOf(obj[14])) : BigDecimal.ZERO);
		dto.setMemoSgstval(obj[15] != null
				? new BigDecimal(String.valueOf(obj[15])) : BigDecimal.ZERO);
		dto.setMemoCessval(obj[16] != null
				? new BigDecimal(String.valueOf(obj[16])) : BigDecimal.ZERO);

		String profitCenter = obj[17] != null ? String.valueOf(obj[17]) : null;
		if (profitCenter != null) {
			if (profitCenter.length() > MAX) {
				dto.setProfitCenter(profitCenter.substring(0, MAX));
			} else {
				dto.setProfitCenter(profitCenter);
			}
		}
		String plantCode = obj[18] != null ? String.valueOf(obj[18]) : null;
		if (plantCode != null) {
			if (plantCode.length() > MAX) {
				dto.setPlantCode(plantCode.substring(0, MAX));
			} else {
				dto.setPlantCode(plantCode);
			}
		}
		String location = obj[19] != null ? String.valueOf(obj[19]) : null;
		if (location != null) {
			if (location.length() > MAX) {
				dto.setLocation(location.substring(0, MAX));
			} else {
				dto.setLocation(location);
			}
		}
		String parchOrg = obj[20] != null ? String.valueOf(obj[20]) : null;
		if (parchOrg != null) {
			if (parchOrg.length() > MAX) {
				dto.setParchOrganization(parchOrg.substring(0, MAX));
			} else {
				dto.setParchOrganization(parchOrg);
			}
		}
		String division = obj[21] != null ? String.valueOf(obj[21]) : null;
		if (division != null) {
			if (division.length() > MAX) {
				dto.setDivision(division.substring(0, MAX));
			} else {
				dto.setDivision(division);
			}
		}
		String userAccess1 = obj[22] != null ? String.valueOf(obj[22]) : null;
		if (userAccess1 != null) {
			if (userAccess1.length() > MAX) {
				dto.setUseraccess1(userAccess1.substring(0, MAX));
			} else {
				dto.setUseraccess1(userAccess1);
			}
		}
		String userAccess2 = obj[23] != null ? String.valueOf(obj[23]) : null;
		if (userAccess2 != null) {
			if (userAccess2.length() > MAX) {
				dto.setUseraccess2(userAccess2.substring(0, MAX));
			} else {
				dto.setUseraccess2(userAccess2);
			}
		}
		String userAccess3 = obj[24] != null ? String.valueOf(obj[24]) : null;
		if (userAccess3 != null) {
			if (userAccess3.length() > MAX) {
				dto.setUseraccess3(userAccess3.substring(0, MAX));
			} else {
				dto.setUseraccess3(userAccess3);
			}
		}
		String userAccess4 = obj[25] != null ? String.valueOf(obj[25]) : null;
		if (userAccess4 != null) {
			if (userAccess4.length() > MAX) {
				dto.setUseraccess4(userAccess4.substring(0, MAX));
			} else {
				dto.setUseraccess4(userAccess4);
			}
		}
		String userAccess5 = obj[26] != null ? String.valueOf(obj[26]) : null;
		if (userAccess5 != null) {
			if (userAccess5.length() > MAX) {
				dto.setUseraccess5(userAccess5.substring(0, MAX));
			} else {
				dto.setUseraccess5(userAccess5);
			}
		}
		String userAccess6 = obj[27] != null ? String.valueOf(obj[27]) : null;
		if (userAccess6 != null) {
			if (userAccess6.length() > MAX) {
				dto.setUseraccess6(userAccess6.substring(0, MAX));
			} else {
				dto.setUseraccess6(userAccess6);
			}
		}
		dto.setGstnTotDoc(BigInteger.ZERO);
		dto.setGstnTbval(BigDecimal.ZERO);
		dto.setGstnTxval(BigDecimal.ZERO);
		dto.setGstnInval(BigDecimal.ZERO);
		dto.setGstnIgstval(BigDecimal.ZERO);
		dto.setGstnCgstval(BigDecimal.ZERO);
		dto.setGstnSgstval(BigDecimal.ZERO);
		dto.setGstnCessval(BigDecimal.ZERO);

		dto.setDiffInval(dto.getMemoInval() != null
				? dto.getMemoInval().subtract(dto.getGstnInval()) : null);

		dto.setDiffInval(dto.getMemoInval() != null
				? dto.getMemoInval().subtract(dto.getGstnInval()) : null);
		dto.setDiffTbval(dto.getMemoTbval() != null
				? dto.getMemoTbval().subtract(dto.getGstnTbval()) : null);
		dto.setDiffTxval(dto.getMemoTxval() != null
				? dto.getMemoTxval().subtract(dto.getGstnTxval()) : null);

		dto.setDiffIgstval(dto.getMemoIgstval() != null
				? dto.getMemoIgstval().subtract(dto.getGstnIgstval()) : null);
		dto.setDiffCgstval(dto.getMemoCgstval() != null
				? dto.getMemoCgstval().subtract(dto.getGstnCgstval()) : null);
		dto.setDiffSgstval(dto.getMemoSgstval() != null
				? dto.getMemoSgstval().subtract(dto.getGstnSgstval()) : null);
		dto.setDiffCessval(dto.getMemoCessval() != null
				? dto.getMemoCessval().subtract(dto.getGstnCessval()) : null);
		dto.setDiffTotDoc(dto.getMemoTotDoc() != null
				? dto.getMemoTotDoc().subtract(dto.getGstnTotDoc()) : null);
	}

	private void table4ReviewConversion(Object[] obj,
			Anx1AItemReviewSummaryReqDto dto) {
		dto.setGstinNum(obj[0] != null ? String.valueOf(obj[0]) : null);
		dto.setRetPer(obj[1] != null ? String.valueOf(obj[1]) : null);
		dto.setDataType(APIConstants.TABLE4);
		dto.setTaxTable(obj[3] != null ? String.valueOf(obj[3]) : null);
		dto.setAspSuppmode(obj[4] != null
				? new BigDecimal(String.valueOf(obj[4])) : BigDecimal.ZERO);
		dto.setAspSuppreturn(obj[5] != null
				? new BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);
		dto.setAspSuppnet(obj[6] != null
				? new BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);

		dto.setAspIgstval(obj[7] != null
				? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
		dto.setAspCgstval(obj[8] != null
				? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);
		dto.setAspSgstval(obj[9] != null
				? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
		dto.setAspCessval(obj[10] != null
				? new BigDecimal(String.valueOf(obj[10])) : BigDecimal.ZERO);
		dto.setAspTxval(obj[11] != null
				? new BigDecimal(String.valueOf(obj[11])) : BigDecimal.ZERO);

		dto.setMemoSuppmode(obj[4] != null
				? new BigDecimal(String.valueOf(obj[4])) : BigDecimal.ZERO);
		dto.setMemoSuppreturn(obj[5] != null
				? new BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);
		dto.setMemoSuppnet(obj[6] != null
				? new BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);

		dto.setMemoIgstval(obj[7] != null
				? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);

		dto.setMemoCgstval(obj[8] != null
				? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);

		dto.setMemoSgstval(obj[9] != null
				? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
		dto.setMemoCessval(obj[10] != null
				? new BigDecimal(String.valueOf(obj[10])) : BigDecimal.ZERO);
		dto.setMemoTxval(obj[11] != null
				? new BigDecimal(String.valueOf(obj[11])) : BigDecimal.ZERO);

		String profitCenter = obj[12] != null ? String.valueOf(obj[12]) : null;
		if (profitCenter != null) {
			if (profitCenter.length() > MAX) {
				dto.setProfitCenter(profitCenter.substring(0, MAX));
			} else {
				dto.setProfitCenter(profitCenter);
			}
		}
		String plantCode = obj[13] != null ? String.valueOf(obj[13]) : null;
		if (plantCode != null) {
			if (plantCode.length() > MAX) {
				dto.setPlantCode(plantCode.substring(MAX));
			} else {
				dto.setPlantCode(plantCode);
			}
		}
		String location = obj[14] != null ? String.valueOf(obj[14]) : null;
		if (location != null) {
			if (location.length() > MAX) {
				dto.setLocation(location.substring(0, MAX));
			} else {
				dto.setLocation(location);
			}
		}
		String salesOrg = obj[15] != null ? String.valueOf(obj[15]) : null;
		if (salesOrg != null) {
			if (salesOrg.length() > MAX) {
				dto.setSalesOrganization(salesOrg.substring(0, MAX));
			} else {
				dto.setSalesOrganization(salesOrg);
			}
		}
		String distChannel = obj[16] != null ? String.valueOf(obj[16]) : null;
		if (distChannel != null) {
			if (distChannel.length() > MAX) {
				dto.setDistChannel(distChannel.substring(0, MAX));
			} else {
				dto.setDistChannel(distChannel);
			}
		}
		String division = obj[17] != null ? String.valueOf(obj[17]) : null;
		if (division != null) {
			if (division.length() > MAX) {
				dto.setDivision(division.substring(0, MAX));
			} else {
				dto.setDivision(division);
			}
		}
		String userAccess1 = obj[18] != null ? String.valueOf(obj[18]) : null;
		if (userAccess1 != null) {
			if (userAccess1.length() > MAX) {
				dto.setUseraccess1(userAccess1.substring(0, MAX));
			} else {
				dto.setUseraccess1(userAccess1);
			}
		}
		String userAccess2 = obj[19] != null ? String.valueOf(obj[19]) : null;
		if (userAccess2 != null) {
			if (userAccess2.length() > MAX) {
				dto.setUseraccess2(userAccess2.substring(0, MAX));
			} else {
				dto.setUseraccess2(userAccess2);
			}
		}
		String userAccess3 = obj[20] != null ? String.valueOf(obj[20]) : null;
		if (userAccess3 != null) {
			if (userAccess3.length() > MAX) {
				dto.setUseraccess3(userAccess3.substring(0, MAX));
			} else {
				dto.setUseraccess3(userAccess3);
			}
		}
		String userAccess4 = obj[21] != null ? String.valueOf(obj[21]) : null;
		if (userAccess4 != null) {
			if (userAccess4.length() > MAX) {
				dto.setUseraccess4(userAccess4.substring(0, MAX));
			} else {
				dto.setUseraccess4(userAccess4);
			}
		}
		String userAccess5 = obj[22] != null ? String.valueOf(obj[22]) : null;
		if (userAccess5 != null) {
			if (userAccess5.length() > MAX) {
				dto.setUseraccess5(userAccess5.substring(0, MAX));
			} else {
				dto.setUseraccess5(userAccess5);
			}
		}
		String userAccess6 = obj[23] != null ? String.valueOf(obj[23]) : null;
		if (userAccess6 != null) {
			if (userAccess6.length() > MAX) {
				dto.setUseraccess6(userAccess6.substring(0, MAX));
			} else {
				dto.setUseraccess6(userAccess6);
			}
		}
		dto.setGstSuppmode(BigDecimal.ZERO);
		dto.setGstSuppreturn(BigDecimal.ZERO);
		dto.setGstSuppnet(BigDecimal.ZERO);

		dto.setGstnIgstval(BigDecimal.ZERO);
		dto.setGstnCgstval(BigDecimal.ZERO);
		dto.setGstnSgstval(BigDecimal.ZERO);
		dto.setGstnCessval(BigDecimal.ZERO);
		dto.setGstnTbval(BigDecimal.ZERO);

		dto.setGstnTotDoc(BigInteger.ZERO);

		dto.setDiffTotDoc(dto.getMemoTotDoc() != null
				? dto.getMemoTotDoc().subtract(dto.getGstnTotDoc()) : null);

		dto.setDiffTbval(dto.getMemoTbval() != null
				? dto.getMemoTbval().subtract(dto.getGstnTbval()) : null);
		dto.setDiffIgstval(dto.getMemoIgstval() != null
				? dto.getMemoIgstval().subtract(dto.getGstnIgstval()) : null);
		dto.setDiffCgstval(dto.getMemoCgstval() != null
				? dto.getMemoCgstval().subtract(dto.getGstnCgstval()) : null);
		dto.setDiffSgstval(dto.getMemoSgstval() != null
				? dto.getMemoSgstval().subtract(dto.getGstnSgstval()) : null);
		dto.setDiffCessval(dto.getMemoCessval() != null
				? dto.getMemoCessval().subtract(dto.getGstnCessval()) : null);
	}

	private void outwardConvertion(Object[] obj,
			Anx1AItemReviewSummaryReqDto dto) {
		dto.setGstinNum(obj[0] != null ? String.valueOf(obj[0]) : null);
		dto.setRetPer(obj[1] != null ? String.valueOf(obj[1]) : null);
		dto.setDataType(OUTWARD);
		dto.setDoctype(obj[2] != null ? String.valueOf(obj[2]) : null);

		dto.setTaxTable(obj[3] != null ? String.valueOf(obj[3]) : null);
		dto.setAspTotDoc(obj[4] != null ? new BigInteger(String.valueOf(obj[4]))
				: BigInteger.ZERO);
		dto.setAspInval(obj[5] != null ? new BigDecimal(String.valueOf(obj[5]))
				: BigDecimal.ZERO);
		dto.setAspTbval(obj[6] != null ? new BigDecimal(String.valueOf(obj[6]))
				: BigDecimal.ZERO);
		dto.setAspTxval(obj[7] != null ? new BigDecimal(String.valueOf(obj[7]))
				: BigDecimal.ZERO);
		dto.setAspIgstval(obj[8] != null
				? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);
		dto.setAspCgstval(obj[9] != null
				? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
		dto.setAspSgstval(obj[10] != null
				? new BigDecimal(String.valueOf(obj[10])) : BigDecimal.ZERO);
		dto.setAspCessval(obj[11] != null
				? new BigDecimal(String.valueOf(obj[11])) : BigDecimal.ZERO);

		dto.setMemoTotDoc(obj[4] != null
				? new BigInteger(String.valueOf(obj[4])) : BigInteger.ZERO);
		dto.setMemoInval(obj[5] != null ? new BigDecimal(String.valueOf(obj[5]))
				: BigDecimal.ZERO);
		dto.setMemoTbval(obj[6] != null ? new BigDecimal(String.valueOf(obj[6]))
				: BigDecimal.ZERO);
		dto.setMemoTxval(obj[7] != null ? new BigDecimal(String.valueOf(obj[7]))
				: BigDecimal.ZERO);
		dto.setMemoIgstval(obj[8] != null
				? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);
		dto.setMemoCgstval(obj[9] != null
				? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
		dto.setMemoSgstval(obj[10] != null
				? new BigDecimal(String.valueOf(obj[10])) : BigDecimal.ZERO);
		dto.setMemoCessval(obj[11] != null
				? new BigDecimal(String.valueOf(obj[11])) : BigDecimal.ZERO);

		String profitCenter = obj[12] != null ? String.valueOf(obj[12]) : null;
		if (profitCenter != null) {
			if (profitCenter.length() > MAX) {
				dto.setProfitCenter(profitCenter.substring(0, MAX));
			} else {
				dto.setProfitCenter(profitCenter);
			}
		}
		String plantCode = obj[13] != null ? String.valueOf(obj[13]) : null;
		if (plantCode != null) {
			if (plantCode.length() > MAX) {
				dto.setPlantCode(plantCode.substring(MAX));
			} else {
				dto.setPlantCode(plantCode);
			}
		}
		String location = obj[14] != null ? String.valueOf(obj[14]) : null;
		if (location != null) {
			if (location.length() > MAX) {
				dto.setLocation(location.substring(0, MAX));
			} else {
				dto.setLocation(location);
			}
		}
		String salesOrg = obj[15] != null ? String.valueOf(obj[15]) : null;
		if (salesOrg != null) {
			if (salesOrg.length() > MAX) {
				dto.setSalesOrganization(salesOrg.substring(0, MAX));
			} else {
				dto.setSalesOrganization(salesOrg);
			}
		}
		String distChannel = obj[16] != null ? String.valueOf(obj[16]) : null;
		if (distChannel != null) {
			if (distChannel.length() > MAX) {
				dto.setDistChannel(distChannel.substring(0, MAX));
			} else {
				dto.setDistChannel(distChannel);
			}
		}
		String division = obj[17] != null ? String.valueOf(obj[17]) : null;
		if (division != null) {
			if (division.length() > MAX) {
				dto.setDivision(division.substring(0, MAX));
			} else {
				dto.setDivision(division);
			}
		}
		String userAccess1 = obj[18] != null ? String.valueOf(obj[18]) : null;
		if (userAccess1 != null) {
			if (userAccess1.length() > MAX) {
				dto.setUseraccess1(userAccess1.substring(0, MAX));
			} else {
				dto.setUseraccess1(userAccess1);
			}
		}
		String userAccess2 = obj[19] != null ? String.valueOf(obj[19]) : null;
		if (userAccess2 != null) {
			if (userAccess2.length() > MAX) {
				dto.setUseraccess2(userAccess2.substring(0, MAX));
			} else {
				dto.setUseraccess2(userAccess2);
			}
		}
		String userAccess3 = obj[20] != null ? String.valueOf(obj[20]) : null;
		if (userAccess3 != null) {
			if (userAccess3.length() > MAX) {
				dto.setUseraccess3(userAccess3.substring(0, MAX));
			} else {
				dto.setUseraccess3(userAccess3);
			}
		}
		String userAccess4 = obj[21] != null ? String.valueOf(obj[21]) : null;
		if (userAccess4 != null) {
			if (userAccess4.length() > MAX) {
				dto.setUseraccess4(userAccess4.substring(0, MAX));
			} else {
				dto.setUseraccess4(userAccess4);
			}
		}
		String userAccess5 = obj[22] != null ? String.valueOf(obj[22]) : null;
		if (userAccess5 != null) {
			if (userAccess5.length() > MAX) {
				dto.setUseraccess5(userAccess5.substring(0, MAX));
			} else {
				dto.setUseraccess5(userAccess5);
			}
		}
		String userAccess6 = obj[23] != null ? String.valueOf(obj[23]) : null;
		if (userAccess6 != null) {
			if (userAccess6.length() > MAX) {
				dto.setUseraccess6(userAccess6.substring(0, MAX));
			} else {
				dto.setUseraccess6(userAccess6);
			}
		}

		dto.setGstnTotDoc(BigInteger.ZERO);
		dto.setGstnTbval(BigDecimal.ZERO);
		dto.setGstnTxval(BigDecimal.ZERO);
		dto.setGstnInval(BigDecimal.ZERO);
		dto.setGstnIgstval(BigDecimal.ZERO);
		dto.setGstnCgstval(BigDecimal.ZERO);
		dto.setGstnSgstval(BigDecimal.ZERO);
		dto.setGstnCessval(BigDecimal.ZERO);

		dto.setDiffInval(dto.getMemoInval() != null
				? dto.getMemoInval().subtract(dto.getGstnInval()) : null);

		dto.setDiffInval(dto.getMemoInval() != null
				? dto.getMemoInval().subtract(dto.getGstnInval()) : null);
		dto.setDiffTbval(dto.getMemoTbval() != null
				? dto.getMemoTbval().subtract(dto.getGstnTbval()) : null);
		dto.setDiffTxval(dto.getMemoTxval() != null
				? dto.getMemoTxval().subtract(dto.getGstnTxval()) : null);

		dto.setDiffIgstval(dto.getMemoIgstval() != null
				? dto.getMemoIgstval().subtract(dto.getGstnIgstval()) : null);
		dto.setDiffCgstval(dto.getMemoCgstval() != null
				? dto.getMemoCgstval().subtract(dto.getGstnCgstval()) : null);
		dto.setDiffSgstval(dto.getMemoSgstval() != null
				? dto.getMemoSgstval().subtract(dto.getGstnSgstval()) : null);
		dto.setDiffCessval(dto.getMemoCessval() != null
				? dto.getMemoCessval().subtract(dto.getGstnCessval()) : null);
		dto.setDiffTotDoc(dto.getMemoTotDoc() != null
				? dto.getMemoTotDoc().subtract(dto.getGstnTotDoc()) : null);
	}

	private String getQueryOutwardProcessSummary() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TAX_DOC_TYPE,");
		sql.append(
				"DOC_TYPE,TYPE,SUM(TOT_COUNT) TOT_COUNT,SUPPLIES,SUM(IGST) IGST,");
		sql.append(
				"SUM(CGST) AS CGST,SUM(SGST) AS SGST,SUM(CESS) AS CESS,STATUS,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,SALES_ORGANIZATION,");
		sql.append("DISTRIBUTION_CHANNEL,DIVISION,USERACCESS1,USERACCESS2,");
		sql.append("USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6, ");
		sql.append("MAX(MODIFIED_ON) AS STATUS_TIME ");
		sql.append("FROM (( SELECT ");
		sql.append("HDR.SUPPLIER_GSTIN AS SUPPLIER_GSTIN,");
		sql.append("HDR.RETURN_PERIOD AS RETURN_PERIOD,");
		sql.append("HDR.TAX_DOC_TYPE AS TAX_DOC_TYPE,");
		sql.append("HDR.DOC_TYPE AS DOC_TYPE,");
		sql.append("'OUTWARD' AS TYPE,");
		sql.append("COUNT(DISTINCT HDR.DOC_KEY) AS TOT_COUNT,");
		sql.append("SUM(ITM.TAXABLE_VALUE) AS SUPPLIES,");
		sql.append("SUM(ITM.IGST_AMT) AS IGST,");
		sql.append("SUM(ITM.CGST_AMT) AS CGST,");
		sql.append("SUM(ITM.SGST_AMT) AS SGST,");
		sql.append(
				"SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0)) AS CESS,");
		sql.append(
				"CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE  ");
		sql.append(
				"THEN 1 ELSE NULL END) = 0 and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND ");
		sql.append(
				"IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN 'NOT INTTIATED' ");
		sql.append(
				"WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = ");
		sql.append("COUNT(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE ");
		sql.append("THEN 1 ELSE NULL END)) THEN 'FAILED' ");
		sql.append(
				"WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = ");
		sql.append("COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = ");
		sql.append("FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' ");
		sql.append(
				"WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = ");
		sql.append(
				"FALSE then 1 ELSE NULL END) < COUNT(CASE WHEN IS_DELETE = FALSE ");
		sql.append(
				"THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS,");
		sql.append(
				"HDR.PROFIT_CENTRE AS PROFIT_CENTRE,HDR.PLANT_CODE AS PLANT_CODE,");
		sql.append(
				"HDR.LOCATION AS LOCATION,HDR.SALES_ORGANIZATION AS SALES_ORGANIZATION,");
		sql.append(
				"HDR.DISTRIBUTION_CHANNEL AS DISTRIBUTION_CHANNEL,HDR.DIVISION AS DIVISION,");
		sql.append(
				"HDR.USERACCESS1 AS USERACCESS1,HDR.USERACCESS2 AS USERACCESS2,");
		sql.append(
				"HDR.USERACCESS3 AS USERACCESS3,HDR.USERACCESS4 AS USERACCESS4,");
		sql.append(
				"HDR.USERACCESS5 AS USERACCESS5,HDR.USERACCESS6 AS USERACCESS6,");
		sql.append("HDR.MODIFIED_ON ");
		sql.append("FROM ANX_OUTWARD_DOC_HEADER HDR ");
		sql.append("LEFT OUTER JOIN ANX_OUTWARD_DOC_ITEM ITM ");
		sql.append(
				"ON HDR.ID= ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD=ITM.DERIVED_RET_PERIOD  ");
		sql.append("WHERE IS_PROCESSED= TRUE AND IS_DELETE=FALSE AND ");
		sql.append(
				"HDR.AN_RETURN_TYPE='ANX1A' AND AN_TABLE_SECTION IN ('3A','3B','3C') ");
		sql.append("AND SUPPLIER_GSTIN=:gstin ");
		sql.append("GROUP BY  ");
		sql.append("HDR.SUPPLIER_GSTIN,");
		sql.append("HDR.RETURN_PERIOD,");
		sql.append("HDR.TAX_DOC_TYPE,");
		sql.append("HDR.DOC_TYPE,");
		sql.append(
				"HDR.PROFIT_CENTRE,HDR.PLANT_CODE,HDR.LOCATION,HDR.SALES_ORGANIZATION,");
		sql.append(
				"HDR.DISTRIBUTION_CHANNEL,HDR.DIVISION,HDR.USERACCESS1,HDR.USERACCESS2,");
		sql.append(
				"HDR.USERACCESS3,HDR.USERACCESS4,HDR.USERACCESS5,HDR.USERACCESS6,");
		sql.append("HDR.MODIFIED_ON");
		sql.append(" ) ");
		sql.append("UNION ALL ");
		sql.append("( SELECT ");
		sql.append("SUPPLIER_GSTIN,");
		sql.append("RETURN_PERIOD,");
		sql.append("'B2C' AS TAX_DOC_TYPE,");
		sql.append("'RNV' AS DOC_TYPE,");
		sql.append("'OUTWARD' AS TYPE,");
		sql.append("'0' AS TOT_COUNT,");
		sql.append("SUM(TAXABLE_VALUE) AS SUPPLIES,");
		sql.append("SUM(IGST_AMT) AS IGST,");
		sql.append("SUM(CGST_AMT) AS CGST,");
		sql.append("SUM(SGST_AMT) AS SGST,");
		sql.append("SUM(CESS_AMT) AS CESS,");
		sql.append("CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN=FALSE AND ");
		sql.append(
				"IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN 'NOT INTTIATED' ");
		sql.append(
				"WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = ");
		sql.append(
				"COUNT(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ");
		sql.append("ELSE NULL END)) THEN 'FAILED' ");
		sql.append(
				"WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = ");
		sql.append(
				"COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE ");
		sql.append("THEN 1 ELSE NULL END)) THEN 'SAVED' ");
		sql.append(
				"WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = ");
		sql.append("FALSE then 1 ELSE NULL END) < ");
		sql.append("COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) ");
		sql.append("THEN 'PARTIALLY SAVED' END AS STATUS,");
		sql.append("PROFIT_CENTER AS PROFIT_CENTRE,PLANT AS PLANT_CODE,");
		sql.append(
				"LOCATION AS LOCATION,SALES_ORG,DISTRIBUTION_CHANNEL,DIVISION,");
		sql.append("USER_ACCESS1 AS USERACCESS1,USER_ACCESS2 AS USERACCESS2,");
		sql.append("USER_ACCESS3 AS USERACCESS3,USER_ACCESS4 AS USERACCESS4,");
		sql.append("USER_ACCESS5 AS USERACCESS5,USER_ACCESS6 AS USERACCESS6,");
		sql.append("MODIFIED_ON ");
		sql.append("FROM ANX_PROCESSED_B2C WHERE IS_DELETE= FALSE ");
		sql.append("AND IS_AMENDMENT= TRUE AND RETURN_TYPE='ANX1A' ");
		sql.append("AND SUPPLIER_GSTIN=:gstin ");
		sql.append("GROUP BY ");
		sql.append("SUPPLIER_GSTIN,");
		sql.append("RETURN_PERIOD,");
		sql.append("PROFIT_CENTER,PLANT,LOCATION, SALES_ORG,");
		sql.append("DISTRIBUTION_CHANNEL,DIVISION,USER_ACCESS1,USER_ACCESS2,");
		sql.append("USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,USER_ACCESS6,");
		sql.append("MODIFIED_ON ");
		sql.append(")) ");
		sql.append("GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,");
		sql.append("TAX_DOC_TYPE,DOC_TYPE,TYPE,SUPPLIES,STATUS,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,SALES_ORGANIZATION,");
		sql.append("DISTRIBUTION_CHANNEL,DIVISION,USERACCESS1,USERACCESS2,");
		sql.append("USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6, ");
		sql.append("MODIFIED_ON");
		return sql.toString();
	}

	public String getQueryInwardProcessSummary() {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT CUST_GSTIN,RETURN_PERIOD,TAX_DOC_TYPE,DOC_TYPE,TYPE,");
		sql.append("SUM(TOT_COUNT) TOT_COUNT,SUPPLIES,SUM(IGST) IGST,");
		sql.append("SUM(CGST) CGST,SUM(SGST) SGST,SUM(CESS) CESS,STATUS,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,PURCHASE_ORGANIZATION,");
		sql.append("DIVISION,USERACCESS1,USERACCESS2,");
		sql.append("USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,");
		sql.append("MAX(MODIFIED_ON) AS STATUS_TIME ");
		sql.append("FROM ((SELECT ");
		sql.append("HDR.CUST_GSTIN AS CUST_GSTIN, ");
		sql.append("HDR.RETURN_PERIOD AS RETURN_PERIOD,");
		sql.append("HDR.TAX_DOC_TYPE AS TAX_DOC_TYPE,");
		sql.append("HDR.DOC_TYPE AS DOC_TYPE,");
		sql.append("'INWARD' AS TYPE,");
		sql.append("COUNT(DISTINCT HDR.DOC_KEY) AS TOT_COUNT,");
		sql.append("SUM(ITM.TAXABLE_VALUE) AS SUPPLIES,");
		sql.append("SUM(ITM.IGST_AMT) AS IGST,");
		sql.append("SUM(ITM.CGST_AMT) AS CGST,");
		sql.append("SUM(ITM.SGST_AMT) AS SGST,");
		sql.append(
				"SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0)) AS CESS,");
		sql.append(
				"CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 ");
		sql.append(
				"and COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN 'NOT INTTIATED' ");
		sql.append(
				"WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = ");
		sql.append(
				"COUNT(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' ");
		sql.append(
				"WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = ");
		sql.append(
				"COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' ");
		sql.append(
				"WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL END) < ");
		sql.append(
				"COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS,");
		sql.append(
				"HDR.PROFIT_CENTRE AS PROFIT_CENTRE,HDR.PLANT_CODE AS PLANT_CODE,");
		sql.append(
				"HDR.LOCATION AS LOCATION,HDR.PURCHASE_ORGANIZATION AS PURCHASE_ORGANIZATION,");
		sql.append(
				"HDR.DIVISION AS DIVISION,HDR.USERACCESS1 AS USERACCESS1,HDR.USERACCESS2 AS USERACCESS2,");
		sql.append(
				"HDR.USERACCESS3 AS USERACCESS3,HDR.USERACCESS4 AS USERACCESS4,");
		sql.append(
				"HDR.USERACCESS5 AS USERACCESS5,HDR.USERACCESS6 AS USERACCESS6,");
		sql.append("HDR.MODIFIED_ON ");
		sql.append("FROM ANX_INWARD_DOC_HEADER HDR ");
		sql.append("LEFT OUTER JOIN ANX_INWARD_DOC_ITEM ITM ");
		sql.append("ON HDR.ID= ITM.DOC_HEADER_ID ");
		sql.append("WHERE IS_PROCESSED= TRUE AND IS_DELETE=FALSE AND ");
		sql.append(
				"HDR.AN_RETURN_TYPE='ANX1A' AND AN_TABLE_SECTION IN ('3H','3I','3J','3K') ");
		sql.append("AND HDR.CUST_GSTIN=:gstin ");
		sql.append("GROUP BY ");
		sql.append(
				"HDR.CUST_GSTIN,HDR.RETURN_PERIOD,HDR.TAX_DOC_TYPE,HDR.DOC_TYPE,");
		sql.append("HDR.PROFIT_CENTRE,HDR.PLANT_CODE,HDR.LOCATION,");
		sql.append("HDR.PURCHASE_ORGANIZATION,HDR.DIVISION,");
		sql.append("HDR.USERACCESS1,HDR.USERACCESS2,HDR.USERACCESS3,");
		sql.append("HDR.USERACCESS4,HDR.USERACCESS5, HDR.USERACCESS6,");
		sql.append("HDR.MODIFIED_ON ");
		sql.append("UNION ALL ");
		sql.append("SELECT CUST_GSTIN,RETURN_PERIOD,");
		sql.append("(CASE WHEN TRAN_FLAG='RC' THEN 'RCM' ");
		sql.append("WHEN TRAN_FLAG='IMPS' THEN 'IMPS' END) AS TAX_DOC_TYPE,");
		sql.append("(CASE WHEN TRAN_FLAG='RC' THEN 'RSLF'  ");
		sql.append("WHEN TRAN_FLAG='IMPS' THEN 'RSLF' END) AS DOC_TYPE,");
		sql.append("'INWARD' AS TYPE,");
		sql.append("'0' AS TOT_COUNT,");
		sql.append("SUM(TAXABLE_VALUE) AS SUPPLIES,");
		sql.append("SUM(IGST_AMT) AS IGST,");
		sql.append("SUM(CGST_AMT) AS CGST,");
		sql.append("SUM(SGST_AMT) AS SGST,");
		sql.append("SUM(CESS_AMT) AS CESS,");
		sql.append("CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN=FALSE AND  ");
		sql.append(
				"IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) THEN 'NOT INTTIATED' ");
		sql.append(
				"WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = ");
		sql.append(
				"COUNT(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) THEN 'FAILED' ");
		sql.append(
				"WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = ");
		sql.append(
				"COUNT(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'SAVED' ");
		sql.append(
				"WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL END) < ");
		sql.append(
				"COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS,");
		sql.append(
				"PROFIT_CENTER AS PROFIT_CENTRE,PLANT AS PLANT_CODE,LOCATION AS LOCATION,");
		sql.append(
				"PURCHAGE_ORG AS PURCHASE_ORGANIZATION,DIVISION,USER_ACCESS1 AS USERACCESS1,USER_ACCESS2 AS USERACCESS2,");
		sql.append("USER_ACCESS3 AS USERACCESS3,USER_ACCESS4 AS USERACCESS4,");
		sql.append("USER_ACCESS5 AS USERACCESS5,USER_ACCESS6 AS USERACCESS6,");
		sql.append("MODIFIED_ON ");
		sql.append(
				"FROM ANX_PROCESSED_3H_3I WHERE IS_DELETE= FALSE AND IS_AMENDMENT= TRUE AND RETURN_TYPE='ANX1A' ");
		sql.append("AND CUST_GSTIN=:gstin ");
		sql.append("GROUP BY ");
		sql.append("CUST_GSTIN,TRAN_FLAG,RETURN_PERIOD,");
		sql.append("PROFIT_CENTER,PLANT,LOCATION,");
		sql.append("PURCHAGE_ORG,DIVISION,USER_ACCESS1,USER_ACCESS2,");
		sql.append("USER_ACCESS3,USER_ACCESS4,");
		sql.append("USER_ACCESS5,USER_ACCESS6,MODIFIED_ON)) ");
		sql.append(
				"GROUP BY CUST_GSTIN,RETURN_PERIOD,TAX_DOC_TYPE,DOC_TYPE,TYPE,SUPPLIES,STATUS,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,PURCHASE_ORGANIZATION,");
		sql.append("DIVISION,USERACCESS1,USERACCESS2,");
		sql.append(
				"USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,MODIFIED_ON ");
		return sql.toString();
	}

	private String getOutwardReviewSummaryQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DOC_TYPE,");
		sql.append(
				"TABLE_SECTION,SUM(RECORD_COUNT) AS RECORD_COUNT,SUM(INVOICE_VALUE) AS INVOICE_VALUE,");
		sql.append(
				"SUM(TAXABLE_VALUE) AS TAXABLE_VALUE,SUM(TOTAL_TAX) AS TOTAL_TAX,");
		sql.append(
				"SUM(IGST_AMT) AS IGST_AMT,SUM(CGST_AMT) AS CGST_AMT,SUM(SGST_AMT) AS SGST_AMT,SUM(CESS_AMT) AS CESS_AMT,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,SALES_ORGANIZATION,");
		sql.append("DISTRIBUTION_CHANNEL,DIVISION,USERACCESS1,USERACCESS2,");
		sql.append("USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6 ");
		sql.append("FROM ");
		sql.append("( ");
		sql.append(
				"SELECT HDR.SUPPLIER_GSTIN AS SUPPLIER_GSTIN,HDR.RETURN_PERIOD AS RETURN_PERIOD,");
		sql.append(
				"DOC_TYPE,AN_TABLE_SECTION AS TABLE_SECTION,COUNT(DISTINCT DOC_KEY) AS RECORD_COUNT,");
		sql.append("IFNULL(SUM(HDR.DOC_AMT),0) AS INVOICE_VALUE,");
		sql.append("IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,");
		sql.append(
				"(IFNULL(SUM(ITM.IGST_AMT),0)+IFNULL(SUM(ITM.CGST_AMT),0)+IFNULL(SUM(ITM.SGST_AMT),0)+");
		sql.append(
				"IFNULL(SUM(ITM.CESS_AMT_SPECIFIC),0)+IFNULL(SUM(ITM.CESS_AMT_ADVALOREM),0)) AS TOTAL_TAX,");
		sql.append("IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT,");
		sql.append("IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT,");
		sql.append("IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT,");
		sql.append(
				"(IFNULL(SUM(ITM.CESS_AMT_SPECIFIC),0)+IFNULL(SUM(ITM.CESS_AMT_ADVALOREM),0)) AS CESS_AMT,");
		sql.append(
				"HDR.PROFIT_CENTRE AS PROFIT_CENTRE,HDR.PLANT_CODE AS PLANT_CODE,");
		sql.append(
				"HDR.LOCATION AS LOCATION,HDR.SALES_ORGANIZATION AS SALES_ORGANIZATION,");
		sql.append(
				"HDR.DISTRIBUTION_CHANNEL AS DISTRIBUTION_CHANNEL,HDR.DIVISION AS DIVISION,");
		sql.append(
				"HDR.USERACCESS1 AS USERACCESS1,HDR.USERACCESS2 AS USERACCESS2,");
		sql.append(
				"HDR.USERACCESS3 AS USERACCESS3,HDR.USERACCESS4 AS USERACCESS4,");
		sql.append(
				"HDR.USERACCESS5 AS USERACCESS5,HDR.USERACCESS6 AS USERACCESS6 ");
		sql.append("FROM ANX_OUTWARD_DOC_HEADER HDR ");
		sql.append("LEFT OUTER JOIN ANX_OUTWARD_DOC_ITEM ITM ");
		sql.append(
				"ON HDR.ID= ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD=ITM.DERIVED_RET_PERIOD ");
		sql.append("WHERE IS_PROCESSED= TRUE AND IS_DELETE=FALSE AND ");
		sql.append(
				"HDR.AN_RETURN_TYPE='ANX1A' AND AN_TABLE_SECTION IN ('3A','3B','3C') ");
		sql.append(
				"AND HDR.RETURN_PERIOD IN (:returnPeriod)  AND SUPPLIER_GSTIN=:gstin ");
		sql.append(
				"GROUP BY DOC_TYPE,AN_TABLE_SECTION,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,");
		sql.append(
				"HDR.PROFIT_CENTRE,HDR.PLANT_CODE,HDR.LOCATION,HDR.SALES_ORGANIZATION,");
		sql.append(
				"HDR.DISTRIBUTION_CHANNEL,HDR.DIVISION,HDR.USERACCESS1,HDR.USERACCESS2,");
		sql.append(
				"HDR.USERACCESS3,HDR.USERACCESS4,HDR.USERACCESS5,HDR.USERACCESS6 ");
		sql.append("UNION ALL ");
		sql.append("SELECT SUPPLIER_GSTIN,RETURN_PERIOD,'RNV' AS DOC_TYPE,");
		sql.append("'3A' AS TABLE_SECTION,'0' AS RECORD_COUNT,");
		sql.append(
				"(IFNULL(SUM(TAXABLE_VALUE),0)+IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+");
		sql.append(
				"IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) AS INVOICE_VALUE,");
		sql.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE,");
		sql.append("(IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+");
		sql.append(
				"IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) AS TOTAL_TAX,");
		sql.append("IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,");
		sql.append("IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,");
		sql.append("IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,");
		sql.append("(IFNULL(SUM(CESS_AMT),0)) AS CESS_AMT,");
		sql.append(
				"PROFIT_CENTER AS PROFIT_CENTRE,PLANT AS PLANT_CODE,LOCATION AS LOCATION,");
		sql.append("SALES_ORG,DISTRIBUTION_CHANNEL,DIVISION,");
		sql.append("USER_ACCESS1 AS USERACCESS1,USER_ACCESS2 AS USERACCESS2,");
		sql.append("USER_ACCESS3 AS USERACCESS3,USER_ACCESS4 AS USERACCESS4,");
		sql.append("USER_ACCESS5 AS USERACCESS5,USER_ACCESS6 AS USERACCESS6 ");
		sql.append("FROM ANX_PROCESSED_B2C ");
		sql.append(
				"WHERE IS_DELETE=FALSE AND RETURN_TYPE='ANX1A' AND IS_AMENDMENT=TRUE ");
		sql.append(
				"AND RETURN_PERIOD IN (:returnPeriod) AND SUPPLIER_GSTIN=:gstin ");
		sql.append("GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,");
		sql.append("PROFIT_CENTER ,PLANT,LOCATION, SALES_ORG,");
		sql.append("DISTRIBUTION_CHANNEL,DIVISION,USER_ACCESS1,USER_ACCESS2,");
		sql.append("USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,USER_ACCESS6 ");
		sql.append(") ");
		sql.append(
				"GROUP BY DOC_TYPE, TABLE_SECTION,SUPPLIER_GSTIN,RETURN_PERIOD,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,SALES_ORGANIZATION,");
		sql.append("DISTRIBUTION_CHANNEL,DIVISION,USERACCESS1,USERACCESS2,");
		sql.append("USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6");
		return sql.toString();
	}

	private String getInwardReviewSummaryQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT CUST_GSTIN,RETURN_PERIOD,DOC_TYPE,");
		sql.append("TABLE_SECTION,SUM(RECORD_COUNT) AS RECORD_COUNT,");
		sql.append("SUM(INVOICE_VALUE) AS INVOICE_VALUE,");
		sql.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE,");
		sql.append("SUM(TOTAL_TAX) AS TOTAL_TAX,");
		sql.append("SUM(IGST_AMT) AS IGST_AMT,SUM(CGST_AMT) AS CGST_AMT,");
		sql.append("SUM(SGST_AMT) AS SGST_AMT,SUM(CESS_AMT) AS CESS_AMT,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,");
		sql.append("PURCHASE_ORGANIZATION,DIVISION,");
		sql.append("USERACCESS1,USERACCESS2,USERACCESS3,");
		sql.append("USERACCESS4,USERACCESS5,USERACCESS6 ");
		sql.append("FROM( ");
		sql.append(
				"SELECT HDR.CUST_GSTIN AS CUST_GSTIN,HDR.RETURN_PERIOD AS RETURN_PERIOD,");
		sql.append("DOC_TYPE,AN_TABLE_SECTION AS TABLE_SECTION,");
		sql.append("COUNT(DISTINCT DOC_KEY) AS RECORD_COUNT,");
		sql.append("IFNULL(SUM(HDR.DOC_AMT),0) AS INVOICE_VALUE,");
		sql.append("IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,");
		sql.append("(IFNULL(SUM(ITM.IGST_AMT),0)+IFNULL(SUM(ITM.CGST_AMT),0)+");
		sql.append(
				"IFNULL(SUM(ITM.SGST_AMT),0)+IFNULL(SUM(ITM.CESS_AMT_SPECIFIC),0)+");
		sql.append("IFNULL(SUM(ITM.CESS_AMT_ADVALOREM),0)) AS TOTAL_TAX,");
		sql.append("IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT,");
		sql.append("IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT,");
		sql.append("IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT,");
		sql.append(
				"(IFNULL(SUM(ITM.CESS_AMT_SPECIFIC),0)+IFNULL(SUM(ITM.CESS_AMT_ADVALOREM),0)) AS CESS_AMT,");
		sql.append(
				"HDR.PROFIT_CENTRE AS PROFIT_CENTRE,HDR.PLANT_CODE AS PLANT_CODE,");
		sql.append(
				"HDR.LOCATION AS LOCATION,HDR.PURCHASE_ORGANIZATION AS PURCHASE_ORGANIZATION,");
		sql.append(
				"HDR.DIVISION AS DIVISION,HDR.USERACCESS1 AS USERACCESS1,HDR.USERACCESS2 AS USERACCESS2,");
		sql.append(
				"HDR.USERACCESS3 AS USERACCESS3,HDR.USERACCESS4 AS USERACCESS4,");
		sql.append(
				"HDR.USERACCESS5 AS USERACCESS5,HDR.USERACCESS6 AS USERACCESS6 ");
		sql.append("FROM ANX_INWARD_DOC_HEADER HDR ");
		sql.append("LEFT OUTER JOIN ANX_INWARD_DOC_ITEM ITM ");
		sql.append(
				"ON HDR.ID= ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD=ITM.DERIVED_RET_PERIOD ");
		sql.append(
				"WHERE IS_PROCESSED= TRUE AND IS_DELETE=FALSE AND HDR.AN_RETURN_TYPE='ANX1A' AND AN_TABLE_SECTION IN ('3H','3I','3J','3K') ");
		sql.append(
				"AND HDR.CUST_GSTIN=:gstin AND HDR.RETURN_PERIOD IN (:returnPeriod) ");
		sql.append(
				"GROUP BY  HDR.CUST_GSTIN,HDR.RETURN_PERIOD,DOC_TYPE,AN_TABLE_SECTION,");
		sql.append("HDR.PROFIT_CENTRE,HDR.PLANT_CODE,HDR.LOCATION,");
		sql.append("HDR.PURCHASE_ORGANIZATION,HDR.DIVISION,");
		sql.append("HDR.USERACCESS1,HDR.USERACCESS2,HDR.USERACCESS3,");
		sql.append("HDR.USERACCESS4,HDR.USERACCESS5, HDR.USERACCESS6 ");
		sql.append("UNION ALL ");
		sql.append("SELECT CUST_GSTIN,RETURN_PERIOD,");
		sql.append("(CASE WHEN TRAN_FLAG='RC' THEN 'RSLF' ");
		sql.append("WHEN TRAN_FLAG='IMPS' THEN 'RSLF' END) AS DOC_TYPE,");
		sql.append("(CASE WHEN TRAN_FLAG='RC' THEN '3H' ");
		sql.append("WHEN TRAN_FLAG='IMPS' THEN '3I' END) AS TABLE_SECTION,");
		sql.append("0 AS RECORD_COUNT,");
		sql.append(
				"(IFNULL(SUM(TAXABLE_VALUE),0)+IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) AS INVOICE_VALUE,");
		sql.append("IFNULL(SUM(TAXABLE_VALUE),0) AS TAXABLE_VALUE,");
		sql.append(
				"(IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) AS TOTAL_TAX,");
		sql.append("IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,");
		sql.append("IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,");
		sql.append("IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,");
		sql.append("IFNULL(SUM(CESS_AMT),0) AS CESS_AMT,");
		sql.append(
				"PROFIT_CENTER AS PROFIT_CENTRE,PLANT AS PLANT_CODE,LOCATION AS LOCATION,");
		sql.append(
				"PURCHAGE_ORG AS PURCHASE_ORGANIZATION,DIVISION,USER_ACCESS1 AS USERACCESS1,USER_ACCESS2 AS USERACCESS2,");
		sql.append("USER_ACCESS3 AS USERACCESS3,USER_ACCESS4 AS USERACCESS4,");
		sql.append("USER_ACCESS5 AS USERACCESS5,USER_ACCESS6 AS USERACCESS6 ");
		sql.append("FROM ANX_PROCESSED_3H_3I  ");
		sql.append(
				"WHERE IS_DELETE=FALSE AND IS_AMENDMENT= TRUE AND RETURN_TYPE='ANX1A' ");
		sql.append(
				"AND CUST_GSTIN=:gstin AND RETURN_PERIOD IN (:returnPeriod) ");
		sql.append("GROUP BY CUST_GSTIN,RETURN_PERIOD,TRAN_FLAG,");
		sql.append("PROFIT_CENTER,PLANT,LOCATION,PURCHAGE_ORG,DIVISION,");
		sql.append("USER_ACCESS1,USER_ACCESS2,USER_ACCESS3,");
		sql.append("USER_ACCESS4,USER_ACCESS5,USER_ACCESS6 ");
		sql.append(")");
		sql.append(
				"GROUP BY DOC_TYPE, TABLE_SECTION,CUST_GSTIN,RETURN_PERIOD,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,PURCHASE_ORGANIZATION,");
		sql.append("DIVISION,USERACCESS1,USERACCESS2,");
		sql.append("USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6");
		return sql.toString();
	}

	public String table4ReviewSummary() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TABLE_SECTION,");
		sql.append("SUM (SUPPLIES_MADE) AS SUPPLIES_MADE,");
		sql.append("SUM (SUPPLIES_RETURN) AS SUPPLIES_RETURN,");
		sql.append("SUM (NET_SUPPLIES) AS NET_SUPPLIES,");
		sql.append("SUM (IGST_AMT) AS IGST,");
		sql.append("SUM (CGST_AMT) AS CGST,");
		sql.append("SUM (SGST_AMT) AS SGST,");
		sql.append("SUM (CESS_AMT) AS CESS,");
		sql.append("SUM(TAX_PAYABLE) AS TAX_PAYABLE,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,SALES_ORGANIZATION,");
		sql.append("DISTRIBUTION_CHANNEL,DIVISION,USERACCESS1,USERACCESS2,");
		sql.append("USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6 ");
		sql.append("FROM (");
		sql.append(
				"(SELECT HDR.SUPPLIER_GSTIN AS SUPPLIER_GSTIN,HDR.RETURN_PERIOD AS RETURN_PERIOD,'TABLE-4' AS TABLE_SECTION,");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN 0 ELSE ITM.TAXABLE_VALUE END) AS SUPPLIES_MADE,");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN ITM.TAXABLE_VALUE ELSE 0 END) AS SUPPLIES_RETURN,");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.TAXABLE_VALUE),0)*-1 ELSE IFNULL((ITM.TAXABLE_VALUE),0) END) AS NET_SUPPLIES,");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.IGST_AMT),0)*-1 ELSE IFNULL((ITM.IGST_AMT),0) END) AS IGST_AMT,");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.CGST_AMT),0)*-1 ELSE IFNULL((ITM.CGST_AMT),0) END) AS CGST_AMT,");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.SGST_AMT),0)*-1 ELSE IFNULL((ITM.SGST_AMT),0) END) AS SGST_AMT,");
		sql.append("(");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.CESS_AMT_SPECIFIC),0)*-1 ELSE IFNULL((ITM.CESS_AMT_SPECIFIC),0) END)+");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.CESS_AMT_ADVALOREM),0)*-1 ELSE IFNULL((ITM.CESS_AMT_ADVALOREM),0) END)) AS CESS_AMT,");
		sql.append("(");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.IGST_AMT),0)*-1 ELSE IFNULL((ITM.IGST_AMT),0) END)+");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.CGST_AMT),0)*-1 ELSE IFNULL((ITM.CGST_AMT),0) END)+");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.SGST_AMT),0)*-1 ELSE IFNULL((ITM.SGST_AMT),0) END)+");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.CESS_AMT_SPECIFIC),0)*-1 ELSE IFNULL((ITM.CESS_AMT_SPECIFIC),0) END)+");
		sql.append(
				"SUM(CASE WHEN HDR.DOC_TYPE='CR' THEN IFNULL((ITM.CESS_AMT_ADVALOREM),0)*-1 ELSE IFNULL((ITM.CESS_AMT_ADVALOREM),0) END)) AS TAX_PAYABLE,");
		sql.append(
				"HDR.PROFIT_CENTRE AS PROFIT_CENTRE,HDR.PLANT_CODE AS PLANT_CODE,");
		sql.append(
				"HDR.LOCATION AS LOCATION,HDR.SALES_ORGANIZATION AS SALES_ORGANIZATION,");
		sql.append(
				"HDR.DISTRIBUTION_CHANNEL AS DISTRIBUTION_CHANNEL,HDR.DIVISION AS DIVISION,");
		sql.append(
				"HDR.USERACCESS1 AS USERACCESS1,HDR.USERACCESS2 AS USERACCESS2,");
		sql.append(
				"HDR.USERACCESS3 AS USERACCESS3,HDR.USERACCESS4 AS USERACCESS4,");
		sql.append(
				"HDR.USERACCESS5 AS USERACCESS5,HDR.USERACCESS6 AS USERACCESS6 ");
		sql.append("FROM ANX_OUTWARD_DOC_HEADER HDR ");
		sql.append("LEFT OUTER JOIN ANX_OUTWARD_DOC_ITEM ITM ");
		sql.append(
				"ON HDR.ID= ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD=ITM.DERIVED_RET_PERIOD ");
		sql.append(
				"WHERE IS_PROCESSED= TRUE AND IS_DELETE=FALSE AND HDR.AN_RETURN_TYPE='ANX1A' ");
		sql.append(
				"AND AN_TABLE_SECTION IN ('3A','3B','3C','3D','3E','3F','3G') AND HDR.TCS_FLAG = 'Y' ");
		sql.append(
				"AND HDR.SUPPLIER_GSTIN=:gstin AND HDR.RETURN_PERIOD IN (:returnPeriod) ");
		sql.append("GROUP BY HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,");
		sql.append(
				"HDR.PROFIT_CENTRE,HDR.PLANT_CODE,HDR.LOCATION,HDR.SALES_ORGANIZATION,");
		sql.append(
				"HDR.DISTRIBUTION_CHANNEL,HDR.DIVISION,HDR.USERACCESS1,HDR.USERACCESS2,");
		sql.append(
				"HDR.USERACCESS3,HDR.USERACCESS4,HDR.USERACCESS5,HDR.USERACCESS6 ");
		sql.append(") ");
		sql.append("UNION ALL");
		sql.append(
				"(SELECT SUPPLIER_GSTIN,RETURN_PERIOD,'TABLE-4' AS TABLE_SECTION,");
		sql.append("SUM(ECOM_VAL_SUPMADE) AS SUPPLIES_MADE,");
		sql.append("SUM(ECOM_VAL_SUPRET) AS SUPPLIES_RETURN,");
		sql.append("SUM(ECOM_NETVAL_SUP) AS NET_SUPPLIES,");
		sql.append("SUM(IGST_AMT) AS IGST_AMT,");
		sql.append("SUM(CGST_AMT) AS CGST_AMT,");
		sql.append("SUM(SGST_AMT) AS SGST_AMT,");
		sql.append("SUM(CESS_AMT) AS CESS_AMT,");
		sql.append(
				"SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TAX_PAYABLE,");
		sql.append(
				"PROFIT_CENTER AS PROFIT_CENTRE,PLANT AS PLANT_CODE,LOCATION AS LOCATION,");
		sql.append("SALES_ORG,DISTRIBUTION_CHANNEL,DIVISION,");
		sql.append("USER_ACCESS1 AS USERACCESS1,USER_ACCESS2 AS USERACCESS2,");
		sql.append("USER_ACCESS3 AS USERACCESS3,USER_ACCESS4 AS USERACCESS4,");
		sql.append("USER_ACCESS5 AS USERACCESS5,USER_ACCESS6 AS USERACCESS6 ");
		sql.append("FROM ANX_PROCESSED_TABLE4 ");
		sql.append(
				"WHERE IS_DELETE = FALSE AND IS_AMENDMENT=TRUE AND RETURN_TYPE='ANX1A' ");
		sql.append(
				"AND SUPPLIER_GSTIN=:gstin AND RETURN_PERIOD IN (:returnPeriod) ");
		sql.append("GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,");
		sql.append("PROFIT_CENTER ,PLANT,LOCATION, SALES_ORG,");
		sql.append("DISTRIBUTION_CHANNEL,DIVISION,USER_ACCESS1,USER_ACCESS2,");
		sql.append("USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,USER_ACCESS6 ");
		sql.append(")");
		sql.append("UNION ");
		sql.append(
				"(SELECT SUPPLIER_GSTIN,RETURN_PERIOD,'TABLE-4' AS TABLE_SECTION,");
		sql.append("SUM(ECOM_VAL_SUPMADE) AS SUPPLIES_MADE,");
		sql.append("SUM(ECOM_VAL_SUPRET) AS SUPPLIES_RETURN,");
		sql.append("SUM(ECOM_NETVAL_SUP) AS NET_SUPPLIES,");
		sql.append("SUM(IGST_AMT) AS IGST_AMT,");
		sql.append("SUM(CGST_AMT) AS CGST_AMT,");
		sql.append("SUM(SGST_AMT) AS SGST_AMT,");
		sql.append("SUM(CESS_AMT) AS CESS_AMT,");
		sql.append(
				"SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TAX_PAYABLE,");
		sql.append(
				"PROFIT_CENTER AS PROFIT_CENTRE,PLANT AS PLANT_CODE,LOCATION AS LOCATION,");
		sql.append("SALES_ORG,DISTRIBUTION_CHANNEL,DIVISION,");
		sql.append("USER_ACCESS1 AS USERACCESS1,USER_ACCESS2 AS USERACCESS2,");
		sql.append("USER_ACCESS3 AS USERACCESS3,USER_ACCESS4 AS USERACCESS4,");
		sql.append("USER_ACCESS5 AS USERACCESS5,USER_ACCESS6 AS USERACCESS6 ");
		sql.append("FROM ANX_PROCESSED_B2C  ");
		sql.append(
				"WHERE IS_DELETE = FALSE AND IS_AMENDMENT=TRUE AND RETURN_TYPE='ANX1A' ");
		sql.append(
				"AND SUPPLIER_GSTIN=:gstin AND RETURN_PERIOD IN (:returnPeriod) ");
		sql.append("GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,");
		sql.append("PROFIT_CENTER ,PLANT,LOCATION, SALES_ORG,");
		sql.append("DISTRIBUTION_CHANNEL,DIVISION,USER_ACCESS1,USER_ACCESS2,");
		sql.append("USER_ACCESS3,USER_ACCESS4,USER_ACCESS5,USER_ACCESS6 ");
		sql.append(")) ");
		sql.append("GROUP BY TABLE_SECTION,SUPPLIER_GSTIN,RETURN_PERIOD,");
		sql.append("PROFIT_CENTRE,PLANT_CODE,LOCATION,SALES_ORGANIZATION,");
		sql.append("DISTRIBUTION_CHANNEL,DIVISION,USERACCESS1,USERACCESS2,");
		sql.append("USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6");
		return sql.toString();
	}

	/*
	 * @Override public Integer
	 * pushToErp(Anx1AProcessReviewSummaryReqHeaderItemDto dto, String
	 * destinationName, AnxErpBatchEntity batch) { try { ByteArrayOutputStream
	 * outwardStream = new ByteArrayOutputStream(); JAXBContext context =
	 * JAXBContext.newInstance(
	 * Anx1AProcessReviewSummaryReqHeaderItemDto.class); Marshaller hdearMarshr
	 * = context.createMarshaller();
	 * hdearMarshr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	 * hdearMarshr.marshal(dto, outwardStream); String headerXml =
	 * outwardStream.toString(); if (headerXml != null && headerXml.length() >
	 * 0) { headerXml = headerXml.substring(headerXml.indexOf('\n') + 1); }
	 * String header =
	 * "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
	 * + "xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'>" +
	 * "<soapenv:Header/>" + "<soapenv:Body>" + "<urn:ZupdateAnx1aAspdata>";
	 * String footer =
	 * "</urn:ZupdateAnx1aAspdata></soapenv:Body></soapenv:Envelope>"; if
	 * (headerXml != null) { headerXml = header + headerXml + footer; } if
	 * (headerXml != null && destinationName != null) { return
	 * destinationConn.post(destinationName, headerXml, batch); } } catch
	 * (Exception e) { LOGGER.error("Exception Occured:{}", e); } return null; }
	 */
}
