package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.anx1.Gstr2ProcessedRecordsFinalRespDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.base.Strings;

@Component
public class Gstr2ProcessedRecordsCommonUtil {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ProcessedRecordsFetchDaoImpl.class);
	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("GstinAPIAuthInfoRepository")
	private GstinAPIAuthInfoRepository gstinAPIAuthInfoRepository;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwarddocRepository;

	@Autowired
	DefaultStateCache defaultStateCache;

	public List<Gstr2ProcessedRecordsFinalRespDto> convertGstr2RecordsIntoObject(
			List<Object[]> outDataArray, Map<String, String> gstinAuthMap,
			Map<String, String> regTypeMap) throws Exception {
		List<Gstr2ProcessedRecordsFinalRespDto> outList = new ArrayList<Gstr2ProcessedRecordsFinalRespDto>();

		if (!outDataArray.isEmpty()) {
			for (Object obj[] : outDataArray) {
				Gstr2ProcessedRecordsFinalRespDto dto = new Gstr2ProcessedRecordsFinalRespDto();

				String GSTIN = String.valueOf(obj[0]);
				dto.setGstin(GSTIN);
				String stateCode = GSTIN.substring(0, 2);
				/*
				 * String stateName = statecodeRepository
				 * .findStateNameByCode(stateCode);
				 */
				String stateName = defaultStateCache.getStateName(stateCode);
				dto.setState(stateName);
				/*
				 * List<String> regName = gSTNDetailRepository
				 * .findRegTypeByGstinForGstr2PR(GSTIN); if (regName != null &&
				 * regName.size() > 0) { String regTypeName = regName.get(0); if
				 * (regTypeName == null ||
				 * regTypeName.equalsIgnoreCase("normal")) { // ||
				 * regTypeName.equalsIgnoreCase("regular") dto.setRegType(""); }
				 * else { dto.setRegType(regTypeName.toUpperCase()); } } else {
				 * dto.setRegType(""); }
				 */
				if (!regTypeMap.isEmpty()) {
					String regTypeName = regTypeMap.get(GSTIN);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal")) {
						dto.setRegType("");
					} else if (!Strings.isNullOrEmpty(regTypeName)) {
						dto.setRegType(regTypeName.toUpperCase());
					} else {
						dto.setRegType("");
					}
				}
				if (obj[1] == null || obj[1] == "null") {
					dto.setLastUpdate("");
				} else {
					Timestamp date = (Timestamp) obj[1];
					LocalDateTime dt = date.toLocalDateTime();
					LocalDateTime dateTimeFormatter = EYDateUtil
							.toISTDateTimeFromUTC(dt);
					SimpleDateFormat formatter1 = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss.SSS");
					Date updatedDate = formatter1.parse(String.valueOf(
							dateTimeFormatter.toString().replace("T", " ")));
					SimpleDateFormat out = new SimpleDateFormat(
							"dd-MM-yyyy hh:mm:ss");
					String newdate = out.format(updatedDate);
					String ldt = dt != null ? newdate : null;

					dto.setLastUpdate(ldt);
				}
				dto.setDocType(String.valueOf(obj[2]));
				/*
				 * String gstintoken = defaultGSTNAuthTokenService
				 * .getAuthTokenStatusForGstin(GSTIN);
				 */

				if (!gstinAuthMap.isEmpty()) {
					String gstnAct = gstinAuthMap.get(GSTIN);
					if ("A".equalsIgnoreCase(gstnAct)) {
						dto.setAuthToken("Active");
					} else {
						dto.setAuthToken("Inactive");
					}
				} else {
					dto.setAuthToken("Inactive");
				}

				dto.setTableType(String.valueOf(obj[3]));

				BigInteger integer = GenUtil.getBigInteger(obj[5]);
				dto.setCount(integer);

				dto.setInvoiceValue((BigDecimal) obj[6]);
				dto.setTaxableValue((BigDecimal) obj[7]);
				dto.setTaxPayable((BigDecimal) obj[8]);
				dto.setTaxPayableIgst((BigDecimal) obj[9]);
				dto.setTaxPayableCgst((BigDecimal) obj[10]);
				dto.setTaxPayableSgst((BigDecimal) obj[11]);
				dto.setTaxPayableCess((BigDecimal) obj[12]);
				dto.setCrEligibleTotal((BigDecimal) obj[13]);
				dto.setCrEligibleIgst((BigDecimal) obj[14]);
				dto.setCrEligibleCgst((BigDecimal) obj[15]);
				dto.setCrEligibleSgst((BigDecimal) obj[16]);
				dto.setCrEligibleCess((BigDecimal) obj[17]);
				outList.add(dto);
			}
		}
		return outList;

	}

	public static List<Gstr2ProcessedRecordsFinalRespDto> convertCalcuDataToResp(
			List<Gstr2ProcessedRecordsFinalRespDto> sortedGstinDtoList) {
		List<Gstr2ProcessedRecordsFinalRespDto> finalRespDtos = new ArrayList<>();
		sortedGstinDtoList.stream().forEach(dto -> {
			Gstr2ProcessedRecordsFinalRespDto respDto = new Gstr2ProcessedRecordsFinalRespDto();
			respDto.setState(dto.getState());
			respDto.setGstin(dto.getGstin());
			respDto.setRegType(dto.getRegType());
			respDto.setAuthToken(dto.getAuthToken());
			respDto.setCount(dto.getCount());
			respDto.setInvoiceValue(dto.getInvoiceValue());
			respDto.setTaxableValue(dto.getTaxableValue());
			respDto.setTaxPayable(dto.getTaxPayable());
			respDto.setTaxPayableIgst(dto.getTaxPayableIgst());
			respDto.setTaxPayableCgst(dto.getTaxPayableCgst());
			respDto.setTaxPayableSgst(dto.getTaxPayableSgst());
			respDto.setTaxPayableCess(dto.getTaxPayableCess());
			respDto.setLastUpdate(dto.getLastUpdate());
			respDto.setCrEligibleTotal(dto.getCrEligibleTotal());
			respDto.setCrEligibleIgst(dto.getCrEligibleIgst());
			respDto.setCrEligibleCgst(dto.getCrEligibleCgst());
			respDto.setCrEligibleSgst(dto.getCrEligibleSgst());
			respDto.setCrEligibleCess(dto.getCrEligibleCess());
			finalRespDtos.add(respDto);
		});
		return finalRespDtos;
	}

	public void createMapByGstinBasedOnType(
			Map<String, List<Gstr2ProcessedRecordsFinalRespDto>> outMap,
			List<Gstr2ProcessedRecordsFinalRespDto> outRespDtoList) {
		outRespDtoList.stream().forEach(dto -> {
			outMap.put(dto.getGstin(),
					outRespDtoList.stream().filter(
							resp -> resp.getGstin().equals(dto.getGstin()))
							.collect(Collectors.toList()));
		});

	}

	public void fillTheDataFromDataSecAttr(
			List<Gstr2ProcessedRecordsFinalRespDto> dataDtoList,
			List<String> gstinList, LocalDate docRecvFrom, LocalDate docRecvTo,
			String taxPeriodFrom, String taxPeriodTo, Map<String, String> gstinAuthMap,
			Map<String, String> regTypeMap) {

		List<String> dataGstinList = new ArrayList<>();
		dataDtoList.forEach(dto -> dataGstinList.add(dto.getGstin()));

		for (String gstin : gstinList) {
			List<Object> count = null;
			if (docRecvFrom != null && docRecvTo != null) {
				count = inwarddocRepository.findByGstinDocRecvFromTo(gstin,
						docRecvFrom, docRecvTo);
			} else if (StringUtils.isNotBlank(taxPeriodFrom)
					&& StringUtils.isNotBlank(taxPeriodTo)) {
				count = inwarddocRepository.findByGstinTaxPeriodFromTo(gstin,
						taxPeriodFrom, taxPeriodTo);
			}

			if (CollectionUtils.isEmpty(count)
					|| !dataGstinList.contains(gstin)) {
				Gstr2ProcessedRecordsFinalRespDto dummy = new Gstr2ProcessedRecordsFinalRespDto();
				dummy.setGstin(gstin);
				dummy.setCount(new BigInteger("0"));
				dummy.setInvoiceValue(new BigDecimal("0.0"));
				dummy.setTaxableValue(new BigDecimal("0.0"));
				dummy.setTaxPayable(new BigDecimal("0.0"));
				dummy.setTaxPayableIgst(new BigDecimal("0.0"));
				dummy.setTaxPayableCgst(new BigDecimal("0.0"));
				dummy.setTaxPayableSgst(new BigDecimal("0.0"));
				dummy.setTaxPayableCess(new BigDecimal("0.0"));
				dummy.setCrEligibleTotal(new BigDecimal("0.0"));
				dummy.setCrEligibleIgst(new BigDecimal("0.0"));
				dummy.setCrEligibleCgst(new BigDecimal("0.0"));
				dummy.setCrEligibleSgst(new BigDecimal("0.0"));
				dummy.setCrEligibleCess(new BigDecimal("0.0"));

				String stateCode = gstin.substring(0, 2);
				/*
				 * String stateName = statecodeRepository
				 * .findStateNameByCode(stateCode);
				 */

				String stateName = defaultStateCache.getStateName(stateCode);
				dummy.setState(stateName);
				/*String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						dummy.setAuthToken("Active");
					} else {
						dummy.setAuthToken("Inactive");
					}
				} else {
					dummy.setAuthToken("Inactive");
				}*/
				if (!gstinAuthMap.isEmpty()) {
					String gstnAct = gstinAuthMap.get(gstin);
					if ("A".equalsIgnoreCase(gstnAct)) {
						dummy.setAuthToken("Active");
					} else {
						dummy.setAuthToken("Inactive");
					}
				} else {
					dummy.setAuthToken("Inactive");
				}

				/*List<String> regName = gSTNDetailRepository
						.findRegTypeByGstinForGstr2PR(gstin);
				if (regName != null && regName.size() > 0) {
					String regTypeName = regName.get(0);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal")) {
						// || regTypeName.equalsIgnoreCase("regular")
						dummy.setRegType("");
					} else {
						dummy.setRegType(regTypeName.toUpperCase());
					}
				} else {
					dummy.setRegType("");
				}*/
				if (!regTypeMap.isEmpty()) {
					String regTypeName = regTypeMap.get(gstin);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal")) {
						dummy.setRegType("");
					} else if (!Strings.isNullOrEmpty(regTypeName)) {
						dummy.setRegType(regTypeName.toUpperCase());
					} else {
						dummy.setRegType("");
					}
				}

				dataGstinList.add(gstin);
				dataDtoList.add(dummy);
			}
		}

	}

	public void calculateDataByDocType(
			Map<String, List<Gstr2ProcessedRecordsFinalRespDto>> outMap,
			List<Gstr2ProcessedRecordsFinalRespDto> outDistList) {
		outMap.keySet().forEach(gstinKey -> {
			List<Gstr2ProcessedRecordsFinalRespDto> dtoList = outMap
					.get(gstinKey);
			Gstr2ProcessedRecordsFinalRespDto respDto = new Gstr2ProcessedRecordsFinalRespDto();
			String state = "";
			String gstins = "";
			String regType = "";
			String lastUpdate = "";
			// String docType = "";
			String authToken = "";
			BigInteger count = BigInteger.ZERO;
			BigDecimal invoiceValue = BigDecimal.ZERO;
			BigDecimal taxableValue = BigDecimal.ZERO;
			BigDecimal taxPayable = BigDecimal.ZERO;
			BigDecimal taxPayableIgst = BigDecimal.ZERO;
			BigDecimal taxPayableCgst = BigDecimal.ZERO;
			BigDecimal taxPayableSgst = BigDecimal.ZERO;
			BigDecimal taxPayableCess = BigDecimal.ZERO;
			BigDecimal crEligibleTotal = BigDecimal.ZERO;
			BigDecimal crEligibleIgst = BigDecimal.ZERO;
			BigDecimal crEligibleCgst = BigDecimal.ZERO;
			BigDecimal crEligibleSgst = BigDecimal.ZERO;
			BigDecimal crEligibleCess = BigDecimal.ZERO;

			for (Gstr2ProcessedRecordsFinalRespDto dto : dtoList) {
				state = dto.getState();
				gstins = dto.getGstin();
				regType = dto.getRegType();
				lastUpdate = dto.getLastUpdate();
				authToken = dto.getAuthToken();
				// docType = dto.getDocType();

				count = count.add((dto.getCount() != null) ? dto.getCount()
						: BigInteger.ZERO);

				invoiceValue = invoiceValue.add((dto.getInvoiceValue() != null)
						? dto.getInvoiceValue() : BigDecimal.ZERO);
				taxableValue = taxableValue.add((dto.getTaxableValue() != null)
						? dto.getTaxableValue() : BigDecimal.ZERO);
				taxPayable = taxPayable.add((dto.getTaxPayable() != null)
						? dto.getTaxPayable() : BigDecimal.ZERO);
				taxPayableIgst = taxPayableIgst
						.add((dto.getTaxPayableIgst() != null)
								? dto.getTaxPayableIgst() : BigDecimal.ZERO);
				taxPayableCgst = taxPayableCgst
						.add((dto.getTaxPayableCgst() != null)
								? dto.getTaxPayableCgst() : BigDecimal.ZERO);
				taxPayableSgst = taxPayableSgst
						.add((dto.getTaxPayableSgst() != null)
								? dto.getTaxPayableSgst() : BigDecimal.ZERO);
				taxPayableCess = taxPayableCess
						.add((dto.getTaxPayableCess() != null)
								? dto.getTaxPayableCess() : BigDecimal.ZERO);
				crEligibleTotal = crEligibleTotal
						.add((dto.getCrEligibleTotal() != null)
								? dto.getCrEligibleTotal() : BigDecimal.ZERO);
				crEligibleIgst = crEligibleIgst
						.add((dto.getCrEligibleIgst() != null)
								? dto.getCrEligibleIgst() : BigDecimal.ZERO);
				crEligibleCgst = crEligibleCgst
						.add((dto.getCrEligibleCgst() != null)
								? dto.getCrEligibleCgst() : BigDecimal.ZERO);
				crEligibleSgst = crEligibleSgst
						.add((dto.getCrEligibleSgst() != null)
								? dto.getCrEligibleSgst() : BigDecimal.ZERO);
				crEligibleCess = crEligibleCess
						.add((dto.getCrEligibleCess() != null)
								? dto.getCrEligibleCess() : BigDecimal.ZERO);

			}

			respDto.setState(state);
			respDto.setGstin(gstins);
			respDto.setRegType(regType);
			respDto.setAuthToken(authToken);
			respDto.setLastUpdate(lastUpdate);
			respDto.setCount(count);
			respDto.setInvoiceValue(invoiceValue);
			respDto.setTaxableValue(taxableValue);
			respDto.setTaxPayable(taxPayable);
			respDto.setTaxPayableIgst(taxPayableIgst);
			respDto.setTaxPayableCgst(taxPayableCgst);
			respDto.setTaxPayableSgst(taxPayableSgst);
			respDto.setTaxPayableCess(taxPayableCess);
			respDto.setCrEligibleTotal(crEligibleTotal);
			respDto.setCrEligibleIgst(crEligibleIgst);
			respDto.setCrEligibleCgst(crEligibleCgst);
			respDto.setCrEligibleSgst(crEligibleSgst);
			respDto.setCrEligibleCess(crEligibleCess);

			outDistList.add(respDto);
		});
	}

}
