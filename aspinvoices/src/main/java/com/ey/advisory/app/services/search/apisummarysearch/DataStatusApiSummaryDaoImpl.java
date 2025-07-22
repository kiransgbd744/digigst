package com.ey.advisory.app.services.search.apisummarysearch;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.DataStatusApiSummaryResDto;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.DataStatusApiSummaryReqDto;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.base.Strings;

@Component("DataStatusApiSummaryDaoImpl")
public class DataStatusApiSummaryDaoImpl implements DataStatusApiSummaryDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataStatusApiSummaryDaoImpl.class);
	@Autowired
	@Qualifier("GstinAPIAuthInfoRepository")
	private GstinAPIAuthInfoRepository gstinAPIAuthInfoRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamsCheck;

	public static final String DATATYPE = "dataType";
	public static final String OUTWARD = "outward";
	public static final String INWARD = "inward";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<DataStatusApiSummaryResDto> findDataStatusApiSummary(
			DataStatusApiSummaryReqDto dataStatusApiSummaryReqDto) {

		String dataType = dataStatusApiSummaryReqDto.getDataType();
		List<Long> entityId = dataStatusApiSummaryReqDto.getEntityId();
		List<String> gstin = dataStatusApiSummaryReqDto.getGstin();
		String dataRecvFrom = dataStatusApiSummaryReqDto.getDataRecvFrom();
		String dataRecvTo = dataStatusApiSummaryReqDto.getDataRecvTo();
		String docFromDate = dataStatusApiSummaryReqDto.getDocFromDate();
		String docFromTo = dataStatusApiSummaryReqDto.getDocToDate();
		String taxPeriodFrom = dataStatusApiSummaryReqDto.getTaxPeriodFrom();
		String taxPeriodTo = dataStatusApiSummaryReqDto.getTaxPeriodTo();
		String documentFromDate = dataStatusApiSummaryReqDto.getDocumentDateFrom();
		String documentToDate = dataStatusApiSummaryReqDto.getDocumentDateTo();
		String accVoucherFromDate = dataStatusApiSummaryReqDto.getAccVoucherDateFrom();
		String accVoucherToDate = dataStatusApiSummaryReqDto.getAccVoucherDateTo();
		Map<String, List<String>> dataSecAttrs = dataStatusApiSummaryReqDto.getDataSecAttrs();
		List<String> uiGstinList = new ArrayList<>();
		String uiGstin = null;

		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String purchase = null, distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null;

		List<String> pcList = null, plantList = null;
		List<String> salesList = null, divisionList = null;
		List<String> locationList = null, purcList = null;
		List<String> distList = null;
		List<String> ud1List = null, ud2List = null, ud3List = null;
		List<String> ud4List = null, ud5List = null, ud6List = null;

		List<DataStatusApiSummaryResDto> apiSummaryResDtos = new ArrayList<DataStatusApiSummaryResDto>();

		try {

			/*
			 * String finalDateString = null;
			 */ List<String> selectedDates = dataStatusApiSummaryReqDto.getDates();
			if (!selectedDates.isEmpty() && selectedDates.size() > 0) {
				selectedDates.sort(Comparator.reverseOrder());
				/*
				 * StringBuilder buf = new StringBuilder(); for (String s :
				 * selectedDates) { buf.append("'"); buf.append(s);
				 * buf.append("'"); buf.append(","); }
				 * 
				 * finalDateString = buf.toString().substring(0,
				 * buf.toString().length() - 1);
				 */
			}

			if (dataSecAttrs != null && !dataSecAttrs.isEmpty() && dataSecAttrs.size() > 0) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase("PC")) {
						profitCenter = key;
						pcList = dataSecAttrs.get("PC");
					}
					if (key.equalsIgnoreCase("Plant")) {
						plant = key;
						plantList = dataSecAttrs.get("Plant");
					}
					if (key.equalsIgnoreCase("SO")) {
						sales = key;
						salesList = dataSecAttrs.get("SO");
					}
					if (key.equalsIgnoreCase("D")) {
						division = key;
						divisionList = dataSecAttrs.get("D");
					}
					if (key.equalsIgnoreCase("L")) {
						location = key;
						locationList = dataSecAttrs.get("L");
					}

					if (key.equalsIgnoreCase("PO")) {
						purchase = key;
						purcList = dataSecAttrs.get("PO");
					}
					if (key.equalsIgnoreCase("DC")) {
						distChannel = key;
						distList = dataSecAttrs.get("DC");
					}
					if (key.equalsIgnoreCase("UD1")) {
						ud1 = key;
						ud1List = dataSecAttrs.get("UD1");
					}
					if (key.equalsIgnoreCase("UD2")) {
						ud2 = key;
						ud2List = dataSecAttrs.get("UD2");
					}
					if (key.equalsIgnoreCase("UD3")) {
						ud3 = key;
						ud3List = dataSecAttrs.get("UD3");
					}
					if (key.equalsIgnoreCase("UD4")) {
						ud4 = key;
						ud4List = dataSecAttrs.get("UD4");
					}
					if (key.equalsIgnoreCase("UD5")) {
						ud5 = key;
						ud5List = dataSecAttrs.get("UD5");
					}
					if (key.equalsIgnoreCase("UD6")) {
						ud6 = key;
						ud6List = dataSecAttrs.get("UD6");
					}
					if (key.equalsIgnoreCase("GSTIN")) {
						uiGstin = key;
						uiGstinList = dataSecAttrs.get("GSTIN");
					}
				}
			}

			String queryStr = createQueryString(dataType, entityId, gstin, dataRecvFrom, dataRecvTo, docFromDate,
					docFromTo, taxPeriodFrom, taxPeriodTo, selectedDates, dataSecAttrs, profitCenter, plant, division,
					location, sales, purchase, distChannel, ud1, ud2, ud3, ud4, ud5, ud6, pcList, plantList, salesList,
					divisionList, locationList, purcList, distList, ud1List, ud2List, ud3List, ud4List, ud5List,
					ud6List, documentFromDate, documentToDate, accVoucherFromDate, accVoucherToDate);

			Query outquery = entityManager.createNativeQuery(queryStr);
			Query inquery = entityManager.createNativeQuery(queryStr);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("outquery -->", outquery);
				LOGGER.debug("inquery -->", inquery);
			}
			if (gstin != null && !gstin.isEmpty()) {
				outquery.setParameter("GSTIN", gstin);
				inquery.setParameter("GSTIN", gstin);
			}
			if (entityId != null && !entityId.isEmpty()) {
				outquery.setParameter("entityId", entityId);
				inquery.setParameter("entityId", entityId);
			}
			if (profitCenter != null && !profitCenter.isEmpty() && pcList != null && !pcList.isEmpty()) {
				outquery.setParameter("pcList", pcList);
				inquery.setParameter("pcList", pcList);
			}
			if (plant != null && !plant.isEmpty() && plantList != null && !plantList.isEmpty()) {
				outquery.setParameter("plantList", plantList);
				inquery.setParameter("plantList", plantList);
			}
			if (sales != null && !sales.isEmpty() && salesList != null && !salesList.isEmpty()) {
				outquery.setParameter("salesList", salesList);
				inquery.setParameter("salesList", salesList);
			}
			if (division != null && !division.isEmpty() && divisionList != null && !divisionList.isEmpty()) {
				outquery.setParameter("divisionList", divisionList);
				inquery.setParameter("divisionList", divisionList);
			}
			if (location != null && !location.isEmpty() && locationList != null && !locationList.isEmpty()) {
				outquery.setParameter("locationList", locationList);
				inquery.setParameter("locationList", locationList);
			}
			if (purchase != null && !purchase.isEmpty() && purcList != null && !purcList.isEmpty()) {
				outquery.setParameter("purcList", purcList);
				inquery.setParameter("purcList", purcList);
			}
			if (distChannel != null && !distChannel.isEmpty() && distList != null && !distList.isEmpty()) {
				outquery.setParameter("distList", distList);
				inquery.setParameter("distList", distList);
			}
			if (ud1 != null && !ud1.isEmpty() && ud1List != null && !ud1List.isEmpty()) {
				outquery.setParameter("ud1List", ud1List);
				inquery.setParameter("ud1List", ud1List);
			}
			if (ud2 != null && !ud2.isEmpty() && ud2List != null && !ud2List.isEmpty()) {
				outquery.setParameter("ud2List", ud2List);
				inquery.setParameter("ud2List", ud2List);
			}
			if (ud3 != null && !ud3.isEmpty() && ud3List != null && !ud3List.isEmpty()) {
				outquery.setParameter("ud3List", ud3List);
				inquery.setParameter("ud3List", ud3List);
			}
			if (ud4 != null && !ud4.isEmpty() && ud4List != null && !ud4List.isEmpty()) {
				outquery.setParameter("ud4List", ud4List);
				inquery.setParameter("ud4List", ud4List);
			}
			if (ud5 != null && !ud5.isEmpty() && ud5List != null && !ud5List.isEmpty()) {
				outquery.setParameter("ud5List", ud5List);
				inquery.setParameter("ud5List", ud5List);
			}
			if (ud6 != null && !ud6.isEmpty() && ud6List != null && !ud6List.isEmpty()) {
				outquery.setParameter("ud6List", ud6List);
				inquery.setParameter("ud6List", ud6List);
			}
			if (selectedDates != null && !selectedDates.isEmpty()) {
				outquery.setParameter("selectedDates", selectedDates);
				inquery.setParameter("selectedDates", selectedDates);

			}
			if (!Strings.isNullOrEmpty(docFromDate) && !Strings.isNullOrEmpty(docFromTo)) {
				outquery.setParameter("docDateFrom", docFromDate);
				outquery.setParameter("docDateTo", docFromTo);
				inquery.setParameter("docDateFrom", docFromDate);
				inquery.setParameter("docDateTo", docFromTo);
			}
			if (!Strings.isNullOrEmpty(dataRecvFrom) && !Strings.isNullOrEmpty(dataRecvTo)) {
				outquery.setParameter("dataRecvFrom", dataRecvFrom);
				outquery.setParameter("dataRecvTo", dataRecvTo);
				inquery.setParameter("dataRecvFrom", dataRecvFrom);
				inquery.setParameter("dataRecvTo", dataRecvTo);
			}
			if (!Strings.isNullOrEmpty(documentFromDate) && !Strings.isNullOrEmpty(documentToDate)
					&& ("outward".equalsIgnoreCase(dataType))) {
				outquery.setParameter("documentFromDate", documentFromDate);
				outquery.setParameter("documentToDate", documentToDate);
			}
			if (!Strings.isNullOrEmpty(documentFromDate) && !Strings.isNullOrEmpty(documentToDate)
					&& ("inward".equalsIgnoreCase(dataType))) {
				inquery.setParameter("documentFromDate", documentFromDate);
				inquery.setParameter("documentToDate", documentToDate);
			}
			if (!Strings.isNullOrEmpty(accVoucherFromDate) && !Strings.isNullOrEmpty(accVoucherToDate)
					&& ("outward".equalsIgnoreCase(dataType))) {
				outquery.setParameter("accVoucherFromDate", accVoucherFromDate);
				outquery.setParameter("accVoucherToDate", accVoucherToDate);
			}
			if (!Strings.isNullOrEmpty(accVoucherFromDate) && !Strings.isNullOrEmpty(accVoucherToDate)
					&& ("inward".equalsIgnoreCase(dataType))) {
				inquery.setParameter("accVoucherFromDate", accVoucherFromDate);
				inquery.setParameter("accVoucherToDate", accVoucherToDate);
			}
			if (!Strings.isNullOrEmpty(taxPeriodFrom) && !Strings.isNullOrEmpty(taxPeriodTo)) {
				outquery.setParameter("taxPeriodFrom", GenUtil.convertTaxPeriodToInt(taxPeriodFrom));
				outquery.setParameter("taxPeriodTo", GenUtil.convertTaxPeriodToInt(taxPeriodTo));
				inquery.setParameter("taxPeriodFrom", GenUtil.convertTaxPeriodToInt(taxPeriodFrom));
				inquery.setParameter("taxPeriodTo", GenUtil.convertTaxPeriodToInt(taxPeriodTo));
			}

			List<Object[]> list = new ArrayList<>();
			if (dataType.equalsIgnoreCase(OUTWARD)) {
				list = outquery.getResultList();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Outward data list from database is:{}", list);
				}
			} else if (dataType.equalsIgnoreCase(INWARD)) {
				list = inquery.getResultList();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Inward data list from database is:{}", list);
				}
			}

			for (Object status[] : list) {
				DataStatusApiSummaryResDto apiSummaryResDto = new DataStatusApiSummaryResDto();
				String GSTIN = String.valueOf(status[0]);
				apiSummaryResDto.setGstin(GSTIN);
				apiSummaryResDto.setReturnPeriod(String.valueOf(status[1]));
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				if (docFromDate != null && !docFromDate.equals("") && docFromTo != null && !docFromTo.equals("")) {
					apiSummaryResDto.setDate(dateFormat.format(status[2]));
				} else if (dataRecvFrom != null && !dataRecvFrom.equals("") && dataRecvTo != null
						&& !dataRecvTo.equals("")) {
					apiSummaryResDto.setDate(dateFormat.format(status[2]));
				} else {
					apiSummaryResDto.setDate(dateFormat.format(status[2]));
				}
				if (status[3] == null || status[3] == "null") {
					apiSummaryResDto.setReturnSection("");
				} else {
					apiSummaryResDto.setReturnSection(String.valueOf(status[3]));
				}
				if (status[4] == null || status[4] == "null") {
					apiSummaryResDto.setReturnType("");
				} else {
					apiSummaryResDto.setReturnType(String.valueOf(status[4]));
				}
				apiSummaryResDto.setCount(GenUtil.getBigInteger(status[5]));
				apiSummaryResDto.setTaxableValue((BigDecimal) status[6]);
				apiSummaryResDto.setTotalTaxes((BigDecimal) status[7]);
				apiSummaryResDto.setIgst((BigDecimal) status[8]);
				apiSummaryResDto.setCgst((BigDecimal) status[9]);
				apiSummaryResDto.setSgst((BigDecimal) status[10]);
				apiSummaryResDto.setCess((BigDecimal) status[11]);
				apiSummaryResDto.setDocType(String.valueOf(status[13]));
				apiSummaryResDto.setReviewStatus("");
				apiSummaryResDto.setSaveStatus("");

				String gstintoken = defaultGSTNAuthTokenService.getAuthTokenStatusForGstin(GSTIN);
				if ("A".equalsIgnoreCase(gstintoken)) {
					apiSummaryResDto.setAuthToken("Active");
				} else {
					apiSummaryResDto.setAuthToken("Inactive");
				}
				apiSummaryResDto.setItems(new ArrayList<>());

				apiSummaryResDtos.add(apiSummaryResDto);
			}
			apiSummaryResDtos.sort(Comparator.comparing(DataStatusApiSummaryResDto::getDate).reversed());
		} catch (Exception ex) {
			throw new AppException("Error in fetching the Data Status Api summary Data", ex);

		}

		List<DataStatusApiSummaryResDto> finalDataResps = new ArrayList<>();
		List<DataStatusApiSummaryResDto> gstinDtoList = new LinkedList<>();
		if (dataType.equalsIgnoreCase(OUTWARD)) {
			if (uiGstin != null && !uiGstin.isEmpty() && uiGstinList != null) {
				for (DataStatusApiSummaryResDto processedDto : apiSummaryResDtos) {
					if (uiGstinList.contains(processedDto.getGstin())) {
						gstinDtoList.add(processedDto);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("After converting data to Object and the result is:{}", gstinDtoList);
						}
					}
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstinDtoList:{}", gstinDtoList);
				}
				Map<String, List<DataStatusApiSummaryResDto>> returnSectionMap = createMapByReturnSection(gstinDtoList);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("returnSectionMap:{}", returnSectionMap);
				}
				calculateDataByPeriodAndDocType(returnSectionMap, finalDataResps);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("After calculateDataByPeriodAndDocType returnSectionMap:{}", returnSectionMap);
					LOGGER.debug("After calculateDataByPeriodAndDocType finalDataResps:{}", finalDataResps);
				}
				/*
				 * List<DataStatusApiSummaryResDto> returnList =
				 * segregateTheDataByEntityIdWithTaxPeriod(finalDataResps,
				 * entityId); if (LOGGER.isDebugEnabled()) { LOGGER.debug(
				 * "returnList:{}", returnList); }
				 */
				return finalDataResps;
			}
		} else if (dataType.equalsIgnoreCase(INWARD)) {
			Map<String, List<DataStatusApiSummaryResDto>> returnSectionMap = createMapByReturnSection(
					apiSummaryResDtos);
			calculateDataByPeriodAndDocType(returnSectionMap, finalDataResps);

			return segregateTheDataByEntityIdWithTaxPeriodinward(finalDataResps, entityId);
		}
		/*
		 * return segregateTheDataByEntityIdWithTaxPeriod(finalDataResps,
		 * entityId);
		 */
		return finalDataResps;

	}

	/**
	 * @param finalDataResps
	 * @param entityId
	 * @return
	 */
	private List<DataStatusApiSummaryResDto> segregateTheDataByEntityIdWithTaxPeriodinward(
			List<DataStatusApiSummaryResDto> finalDataResps, List<Long> entityIds) {

		List<DataStatusApiSummaryResDto> segTaxPeriod = new ArrayList<>();
		if (!finalDataResps.isEmpty() && !entityIds.isEmpty()) {
			for (DataStatusApiSummaryResDto dto : finalDataResps) {
				segTaxPeriod.add(dto);
			}
		}
		return segTaxPeriod;
	}

	private List<DataStatusApiSummaryResDto> segregateTheDataByEntityIdWithTaxPeriod(
			List<DataStatusApiSummaryResDto> finalDataResps, List<Long> entityIds) {

		List<DataStatusApiSummaryResDto> segTaxPeriod = new ArrayList<DataStatusApiSummaryResDto>();
		if (!finalDataResps.isEmpty() && !entityIds.isEmpty()) {
			entityIds.forEach(entityId -> {
				Map<String, String> entityAndReturnPeriodMap = onboardingConfigParamsCheck
						.getQuestionAndAnswerMap(entityId);
				for (DataStatusApiSummaryResDto dto : finalDataResps) {
					try {
						String entityTaxPeriod = entityAndReturnPeriodMap.get("G9");
						String mapTaxPeriod = "01" + entityTaxPeriod;
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
						LocalDate maptaxdate = LocalDate.parse(mapTaxPeriod, formatter);
						String returnType = dto.getReturnType();
						String returnPeriod = dto.getReturnPeriod();
						String returnMapPeriod = "01" + returnPeriod;
						LocalDate taxdate = LocalDate.parse(returnMapPeriod, formatter);
						// if (taxdate.compareTo(maptaxdate) <= 0
						// && returnType.equalsIgnoreCase("GSTR1")) {
						segTaxPeriod.add(dto);
						// }
						// if (taxdate.compareTo(maptaxdate) > 0
						// && returnType.equalsIgnoreCase("ANX1")) {
						// segTaxPeriod.add(dto);
						// }
						// if (taxdate.compareTo(maptaxdate) > 0
						// && returnType.equalsIgnoreCase("RET1")) {
						// segTaxPeriod.add(dto);
						// }

					} catch (NullPointerException e) {
						throw new NullPointerException("Please provide cutoff date for selected entity.");
					}
				}
			});
		}

		/*
		 * Comparator<DataStatusApiSummaryResDto> compareByReturnPeriod = (
		 * DataStatusApiSummaryResDto o1, DataStatusApiSummaryResDto o2) ->
		 * o1.getReturnPeriod() .compareTo(o2.getReturnPeriod());
		 * Collections.sort(segTaxPeriod, compareByReturnPeriod);
		 */

		return segTaxPeriod;
	}

	private void calculateDataByPeriodAndDocType(Map<String, List<DataStatusApiSummaryResDto>> returnSectionMap,
			List<DataStatusApiSummaryResDto> finalDataResps) {
		returnSectionMap.keySet().forEach(key -> {
			List<DataStatusApiSummaryResDto> dataList = returnSectionMap.get(key);
			if (!dataList.isEmpty() && dataList.size() > 0) {
				String date = "";
				String gstins = "";
				String retperiod = "";
				String retType = "";
				String docType = "";
				String authToken = "";
				String returnSection = "";
				BigInteger count = BigInteger.ZERO;
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				BigDecimal totalTaxes = BigDecimal.ZERO;
				BigDecimal taxableValue = BigDecimal.ZERO;

				DataStatusApiSummaryResDto respDto = new DataStatusApiSummaryResDto();
				for (DataStatusApiSummaryResDto dto : dataList) {
					gstins = dto.getGstin();
					authToken = dto.getAuthToken();
					docType = dto.getDocType();
					date = dto.getDate();
					returnSection = dto.getReturnSection();
					retperiod = dto.getReturnPeriod();
					retType = dto.getReturnType();

					count = count.add((dto.getCount() != null && dto.getCount().intValue() > 0) ? dto.getCount()
							: BigInteger.ZERO);
					if (docType != null && (docType.equals("RCR") || docType.equals("CR") || docType.equals("RFV")
							|| docType.equals("RRFV"))) {
						totalTaxes = totalTaxes
								.subtract((dto.getTotalTaxes() != null && dto.getTotalTaxes().intValue() > 0)
										? dto.getTotalTaxes() : BigDecimal.ZERO);
						taxableValue = taxableValue
								.subtract((dto.getTaxableValue() != null && dto.getTaxableValue().intValue() > 0)
										? dto.getTaxableValue() : BigDecimal.ZERO);
						cess = cess.subtract((dto.getCess() != null && dto.getCess().intValue() > 0) ? dto.getCess()
								: BigDecimal.ZERO);
						igst = igst.subtract((dto.getIgst() != null && dto.getIgst().intValue() > 0) ? dto.getIgst()
								: BigDecimal.ZERO);
						cgst = cgst.subtract((dto.getCgst() != null && dto.getCgst().intValue() > 0) ? dto.getCgst()
								: BigDecimal.ZERO);
						sgst = sgst.subtract((dto.getSgst() != null && dto.getSgst().intValue() > 0) ? dto.getSgst()
								: BigDecimal.ZERO);
					} else {
						totalTaxes = totalTaxes.add((dto.getTotalTaxes() != null && dto.getTotalTaxes().intValue() > 0)
								? dto.getTotalTaxes() : BigDecimal.ZERO);
						taxableValue = taxableValue
								.add((dto.getTaxableValue() != null && dto.getTaxableValue().intValue() > 0)
										? dto.getTaxableValue() : BigDecimal.ZERO);
						cess = cess.add((dto.getCess() != null && dto.getCess().intValue() > 0) ? dto.getCess()
								: BigDecimal.ZERO);
						igst = igst.add((dto.getIgst() != null && dto.getIgst().intValue() > 0) ? dto.getIgst()
								: BigDecimal.ZERO);
						cgst = cgst.add((dto.getCgst() != null && dto.getCgst().intValue() > 0) ? dto.getCgst()
								: BigDecimal.ZERO);
						sgst = sgst.add((dto.getSgst() != null && dto.getSgst().intValue() > 0) ? dto.getSgst()
								: BigDecimal.ZERO);
					}
				}
				respDto.setDate(date);
				respDto.setGstin(gstins);
				respDto.setReturnPeriod(retperiod);
				respDto.setReturnType(retType);
				respDto.setReturnSection(returnSection);
				respDto.setAuthToken(authToken);
				respDto.setCount(count);
				respDto.setTaxableValue(taxableValue);
				respDto.setTotalTaxes(totalTaxes);
				respDto.setIgst(igst);
				respDto.setCgst(cgst);
				respDto.setSgst(sgst);
				respDto.setCess(cess);
				finalDataResps.add(respDto);

			}

		});
	}

	private Map<String, List<DataStatusApiSummaryResDto>> createMapByReturnSection(
			List<DataStatusApiSummaryResDto> apiSummaryResDtos) {
		Map<String, List<DataStatusApiSummaryResDto>> returnSectionMap = new LinkedHashMap<String, List<DataStatusApiSummaryResDto>>();

		apiSummaryResDtos.forEach(dto -> {
			StringBuffer key = new StringBuffer();
			key.append(dto.getDate()).append("_").append(dto.getGstin()).append("_").append(dto.getReturnPeriod())
					.append("_").append(dto.getReturnSection());

			String dataKey = key.toString();
			if (returnSectionMap.containsKey(dataKey)) {
				List<DataStatusApiSummaryResDto> dtos = returnSectionMap.get(dataKey);
				dtos.add(dto);
				returnSectionMap.put(dataKey, dtos);
			} else {
				List<DataStatusApiSummaryResDto> dtos = new LinkedList<>();
				dtos.add(dto);
				returnSectionMap.put(dataKey, dtos);
			}
		});
		return returnSectionMap;
	}

	/**
	 * This function creates the sql to be used in the IN clause with date
	 * fields.
	 * 
	 * @return
	 */
	private String createDatesSql(List<String> dates) {
		FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd");
		List<String> dateStrList = dates.stream().map(d -> "TO_DATE('" + format.format(d) + "', 'YYYY-MM-DD')")
				.collect(Collectors.toList());
		return StringUtils.join(dateStrList, ", ");
	}

	private String createQueryString(String dataType, List<Long> entityId, List<String> gstins, String dataRecvFrom,
			String dataRecvTo, String docFromDate, String docFromTo, String taxPeriodFrom, String taxPeriodTo,
			List<String> selectedDates, Map<String, List<String>> dataSecAttrs, String profitCenter, String plant,
			String division, String location, String sales, String purchase, String distChannel, String ud1, String ud2,
			String ud3, String ud4, String ud5, String ud6, List<String> pcList, List<String> plantList,
			List<String> salesList, List<String> divisionList, List<String> locationList, List<String> purcList,
			List<String> distList, List<String> ud1List, List<String> ud2List, List<String> ud3List,
			List<String> ud4List, List<String> ud5List, List<String> ud6List, String documentFromDate,
			String documentToDate, String accVoucherFromDate, String accVoucherToDate) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty() && dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
				}
				if (key.equalsIgnoreCase("SO")) {
					sales = key;
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
			}
		}

		StringBuffer outwardBuilder = new StringBuffer();
		StringBuffer inwardBuilder = new StringBuffer();
		if (entityId != null && !entityId.isEmpty()) {
			outwardBuilder
					.append(" AND  SUPPLIER_GSTIN IN (SELECT GSTIN FROM " + "GSTIN_INFO WHERE ENTITY_ID IN :entityId)");
			inwardBuilder
					.append(" AND  CUST_GSTIN IN (SELECT GSTIN FROM " + "GSTIN_INFO WHERE ENTITY_ID IN :entityId)");

		}
		if (gstins != null && !gstins.isEmpty()) {
			outwardBuilder.append(" AND  SUPPLIER_GSTIN IN (:GSTIN)");
			inwardBuilder.append(" AND CUST_GSTIN IN (:GSTIN)");
		}

		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null && !pcList.isEmpty()) {
			outwardBuilder.append(" AND  PROFIT_CENTRE IN :pcList");
			inwardBuilder.append(" AND  PROFIT_CENTRE IN :pcList");
		}
		if (plant != null && !plant.isEmpty() && plant != null && !plantList.isEmpty()) {
			outwardBuilder.append(" AND  PLANT_CODE IN :plantList");
			inwardBuilder.append(" AND  PLANT_CODE IN :plantList");
		}
		if (sales != null && !sales.isEmpty() && !salesList.isEmpty()) {
			outwardBuilder.append(" AND  SALES_ORGANIZATION IN :salesList");
			inwardBuilder.append(" AND  SALES_ORGANIZATION IN :salesList");
		}
		if (division != null && !division.isEmpty() && divisionList != null && !divisionList.isEmpty()) {
			outwardBuilder.append(" AND  DIVISION IN :divisionList");
			inwardBuilder.append(" AND  DIVISION IN :divisionList");
		}
		if (location != null && !location.isEmpty() && locationList != null && !locationList.isEmpty()) {
			outwardBuilder.append(" AND  LOCATION IN :locationList");
			inwardBuilder.append(" AND  LOCATION IN :locationList");
		}
		if (purchase != null && !purchase.isEmpty() && purcList != null && !purcList.isEmpty()) {
			outwardBuilder.append(" AND  DISTRIBUTION_CHANNEL IN :purcList");
			inwardBuilder.append(" AND  DISTRIBUTION_CHANNEL IN :purcList");
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null && !distList.isEmpty()) {
			outwardBuilder.append(" AND  DISTRIBUTION_CHANNEL IN :distList");
			inwardBuilder.append(" AND  DISTRIBUTION_CHANNEL IN :distList");
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null && !ud1List.isEmpty()) {
			outwardBuilder.append(" AND  USERDEFINED_FIELD1 IN :ud1List");
			inwardBuilder.append(" AND  USERDEFINED_FIELD1 IN :ud1List");
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null && !ud2List.isEmpty()) {
			outwardBuilder.append(" AND  USERDEFINED_FIELD2 IN :ud2List");
			inwardBuilder.append(" AND  USERDEFINED_FIELD2 IN :ud2List");
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null && !ud3List.isEmpty()) {
			outwardBuilder.append(" AND  USERDEFINED_FIELD3 IN :ud3List");
			inwardBuilder.append(" AND  USERDEFINED_FIELD3 IN :ud3List");
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null && !ud4List.isEmpty()) {
			outwardBuilder.append(" AND  USERDEFINED_FIELD4 IN :ud4List");
			inwardBuilder.append(" AND  USERDEFINED_FIELD4 IN :ud4List");
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null && !ud5List.isEmpty()) {
			outwardBuilder.append(" AND  USERDEFINED_FIELD5 IN :ud5List");
			inwardBuilder.append(" AND  USERDEFINED_FIELD5 IN :ud5List");
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null && !ud6List.isEmpty()) {
			outwardBuilder.append(" AND  USERDEFINED_FIELD6 IN :ud6List");
			inwardBuilder.append(" AND  USERDEFINED_FIELD6 IN :ud6List");
		}

		if (selectedDates != null && !selectedDates.isEmpty()) {
			outwardBuilder.append(outwardBuilder.toString().isEmpty() ? " " : " AND ");
			outwardBuilder.append(" RECEIVED_DATE IN (:selectedDates) ");
			inwardBuilder.append(inwardBuilder.toString().isEmpty() ? " " : " AND ");
			inwardBuilder.append(" RECEIVED_DATE IN (:selectedDates) ");
		}

		/**
		 * @Required field
		 */
		if (!Strings.isNullOrEmpty(dataRecvFrom) && !Strings.isNullOrEmpty(dataRecvFrom)) {
			outwardBuilder.append(outwardBuilder.toString().isEmpty() ? " " : " AND ");
			outwardBuilder.append("  RECEIVED_DATE BETWEEN :dataRecvFrom AND :dataRecvTo ");
			inwardBuilder.append(inwardBuilder.toString().isEmpty() ? " " : " AND ");
			inwardBuilder.append("  RECEIVED_DATE BETWEEN :dataRecvFrom AND :dataRecvTo ");
		}
		if (!Strings.isNullOrEmpty(docFromDate) && !Strings.isNullOrEmpty(docFromTo)) {
			outwardBuilder.append(outwardBuilder.toString().isEmpty() ? " " : " AND ");
			outwardBuilder.append("  DOC_DATE BETWEEN :docDateFrom " + "AND :docDateTo ");
			inwardBuilder.append(inwardBuilder.toString().isEmpty() ? " " : " AND ");
			inwardBuilder.append("  DOC_DATE BETWEEN :docDateFrom " + "AND :docDateTo ");
		}

		if (!Strings.isNullOrEmpty(documentFromDate) && !Strings.isNullOrEmpty(documentToDate)) {

			outwardBuilder.append(outwardBuilder.toString().isEmpty() ? " " : " AND ");
			outwardBuilder.append("  DOC_DATE BETWEEN :documentFromDate " + "AND :documentToDate ");
			inwardBuilder.append(inwardBuilder.toString().isEmpty() ? " " : " AND ");
			inwardBuilder.append("  DOC_DATE BETWEEN :documentFromDate " + "AND :documentToDate ");
		}
		if (!Strings.isNullOrEmpty(accVoucherFromDate) && !Strings.isNullOrEmpty(accVoucherToDate)) {
			outwardBuilder.append(outwardBuilder.toString().isEmpty() ? " " : " AND ");
			outwardBuilder.append("  ACCOUNTING_VOUCHER_DATE BETWEEN :accVoucherFromDate " + "AND :accVoucherToDate ");
			inwardBuilder.append(inwardBuilder.toString().isEmpty() ? " " : " AND ");
			inwardBuilder.append("  PURCHASE_VOUCHER_DATE BETWEEN :accVoucherFromDate " + "AND :accVoucherToDate ");
		}
		if (!Strings.isNullOrEmpty(taxPeriodFrom) && !Strings.isNullOrEmpty(taxPeriodTo)) {
			outwardBuilder.append(outwardBuilder.toString().isEmpty() ? " " : " AND ");
			outwardBuilder.append(" DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom " + "AND :taxPeriodTo ");
			inwardBuilder.append(inwardBuilder.toString().isEmpty() ? " " : " AND ");
			inwardBuilder.append("  DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom " + "AND :taxPeriodTo ");
		}

		String outbuildQuery = outwardBuilder.toString();
		String inBuildQuery = inwardBuilder.toString();

		String queryStr = null;

		if (dataType.equalsIgnoreCase(OUTWARD)) {

			queryStr = "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,RECEIVED_DATE,"
					+ " RETURN_SECTION,RETURN_TYPE,SUM(CNT) CNT,SUM(TAXABLE_VALUE)"
					+ " TAXABLE_VALUE,SUM(TOTAL_TAX) TOTAL_TAX," + "SUM(IGST) IGST,SUM(CGST) CGST,SUM(SGST) SGST,"
					+ "SUM(CESS) CESS,DOC_DATE,DOC_TYPE FROM("
					+ " SELECT  SUPPLIER_GSTIN,  RETURN_PERIOD, RECEIVED_DATE,"
					+ " TAX_DOC_TYPE ||' -'||  TABLE_SECTION AS RETURN_SECTION,"
					+ " RETURN_TYPE  AS RETURN_TYPE,COUNT(  DOC_KEY) AS CNT,"
					+ "SUM( TAXABLE_VALUE) AS TAXABLE_VALUE,SUM( IFNULL(IGST_AMT,0)"
					+ " + IFNULL(CGST_AMT,0)+ IFNULL(SGST_AMT,0)+  IFNULL(CESS_AMT_SPECIFIC,0) +"
					+ " IFNULL(CESS_AMT_ADVALOREM,0)) AS TOTAL_TAX,SUM( IFNULL(IGST_AMT,0))"
					+ " AS IGST,SUM( IFNULL(CGST_AMT,0)) AS CGST,SUM( IFNULL(SGST_AMT,0)) AS"
					+ " SGST,SUM( IFNULL(CESS_AMT_SPECIFIC,0) +  IFNULL(CESS_AMT_ADVALOREM,0))"
					+ " AS CESS, DOC_DATE, DOC_TYPE FROM ANX_OUTWARD_DOC_HEADER "
					+ " WHERE  DATAORIGINTYPECODE IN ('A','AI','B','BI') AND "
					+ " ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE" + " AND IS_DELETE=FALSE " + outbuildQuery
					+ " GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD," + "  DOC_DATE, AN_TABLE_SECTION,  AN_TAX_DOC_TYPE,"
					+ " AN_RETURN_TYPE, RECEIVED_DATE, TABLE_SECTION," + " TAX_DOC_TYPE, RETURN_TYPE, DOC_TYPE ORDER BY"
					+ " SUPPLIER_GSTIN, RETURN_PERIOD)" + " GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,RECEIVED_DATE,"
					+ " RETURN_SECTION,RETURN_TYPE,DOC_DATE,DOC_TYPE" + " ORDER BY 5,3,1,2,4";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Outward query from database is:{}", outbuildQuery);
			}
		} else if (dataType.equalsIgnoreCase(INWARD)) {
			queryStr = "SELECT  CUST_GSTIN," + " RETURN_PERIOD, RECEIVED_DATE,"
					+ " AN_TAX_DOC_TYPE ||'-'||  AN_TABLE_SECTION AS" + " RETURN_SECTION,(CASE "
					+ "WHEN  AN_RETURN_TYPE='ANX2' THEN 'ANX2' END)" + " AS RETURN_TYPE,COUNT(  DOC_KEY) AS COUNT,"
					+ "SUM( TAXABLE_VALUE) AS TAXABLE_VALUE,SUM( IFNULL(IGST_AMT,0)+"
					+ " IFNULL(CGST_AMT,0)+ IFNULL(SGST_AMT,0)+ IFNULL(CESS_AMT_SPECIFIC,0) + "
					+ " IFNULL(CESS_AMT_ADVALOREM,0)) AS TOTAL_TAX,"
					+ "SUM( IFNULL(IGST_AMT,0)) AS IGST,SUM( IFNULL(CGST_AMT,0)) AS CGST,"
					+ "SUM( IFNULL(SGST_AMT,0)) AS SGST,SUM( IFNULL(CESS_AMT_SPECIFIC,0) + "
					+ " IFNULL(CESS_AMT_ADVALOREM,0)) AS CESS, DOC_DATE, DOC_TYPE" + " FROM ANX_INWARD_DOC_HEADER   "
					+ " WHERE  DATAORIGINTYPECODE IN ('A','AI') AND  IS_PROCESSED=TRUE " + " AND  IS_DELETE=FALSE  "
					+ inBuildQuery + " GROUP BY  CUST_GSTIN, " + "  RETURN_PERIOD, DOC_DATE, AN_TABLE_SECTION,"
					+ "  AN_TAX_DOC_TYPE, AN_RETURN_TYPE, RECEIVED_DATE," + "  DOC_TYPE ORDER BY 5,3,1,2,4";
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inward query from database is:{}", inBuildQuery);
		}
		return queryStr;
	}

}
