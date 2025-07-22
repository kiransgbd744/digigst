package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2B2bInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2B2baInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aCdnInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aCdnaInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aImpgRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aImpgSezRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetIsdInvoicesAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetIsdaInvoicesAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;
import com.ey.advisory.app.docs.dto.GetGstr2aDetailStatusRespDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedRecordsRespDto;
import com.ey.advisory.app.services.daos.get2a.GetGstr2aDetailStatusService;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Component
public class Gstr2aProcessedRecordsCommonUtil {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2aProcessedRecordsCommonUtil.class);

	private static final String NOT_INITIATED = "NOT_INITIATED";
	private static final String SUCCESS_WITH_NO_DATA = "SUCCESS_WITH_NO_DATA";
	private static final String INPROGRESS = "INPROGRESS";
	private static final String SUCCESS = "SUCCESS";
	private static final String FAILED = "FAILED";
	private static final String INITIATED = "INITIATED";
	private static final String PARTIALLY_SUCCESS = "PARTIALLY_SUCCESS";

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GetGstr2aImpgRepository")
	private GetGstr2aImpgRepository gstr2aImpgRepository;

	@Autowired
	@Qualifier("GetGstr2aImpgSezRepository")
	private GetGstr2aImpgSezRepository gstr2aImpgSezRepository;

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
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository getAnx1BatchRepository;

	@Autowired
	@Qualifier("GetGstr2B2bInvoicesRepository")
	private GetGstr2B2bInvoicesRepository getGstr2B2bInvoicesRepository;

	@Autowired
	@Qualifier("GetGstr2B2baInvoicesRepository")
	private GetGstr2B2baInvoicesRepository getGstr2B2baInvoicesRepository;

	@Autowired
	@Qualifier("Gstr2aGetIsdInvoicesAtGstnRepository")
	private Gstr2aGetIsdInvoicesAtGstnRepository gstr2aGetIsdInvoicesAtGstnRepository;

	@Autowired
	@Qualifier("Gstr2aGetIsdaInvoicesAtGstnRepository")
	private Gstr2aGetIsdaInvoicesAtGstnRepository gstr2aGetIsdaInvoicesAtGstnRepository;

	@Autowired
	@Qualifier("GetGstr2aCdnInvoicesRepository")
	private GetGstr2aCdnInvoicesRepository getGstr2aCdnInvoicesRepository;

	@Autowired
	@Qualifier("GetGstr2aCdnaInvoicesRepository")
	private GetGstr2aCdnaInvoicesRepository getGstr2aCdnaInvoicesRepository;

	@Autowired
	@Qualifier("GetGstr2aDetailStatusService")
	private GetGstr2aDetailStatusService getGstr2aDetailStatusService;

	public List<Gstr2aProcessedRecordsRespDto> convertGstr2aRecordsIntoObjesct(
			List<Object[]> outDataArray,
			Gstr2AProcessedRecordsReqDto gstr2aPRReqDto, List<String> gstinList)
			throws Exception {

		Map<String, String> gstinsStatusMap = getStatusByCriteria(
				gstr2aPRReqDto, gstinList);
		List<Gstr2aProcessedRecordsRespDto> outList = new ArrayList<Gstr2aProcessedRecordsRespDto>();

		if (!outDataArray.isEmpty()) {
			for (Object obj[] : outDataArray) {
				Gstr2aProcessedRecordsRespDto dto = new Gstr2aProcessedRecordsRespDto();

				String GSTIN = String.valueOf(obj[0]);
				dto.setGstin(GSTIN);
				if (gstinsStatusMap.containsKey(GSTIN)) {
					String value[] = gstinsStatusMap.get(GSTIN).split("__");
					if (value[0] != null && !value[0].contains("null")) {
						dto.setStatus(value[0]);
					}
					String s = value[1];
					if (value[1] != null && !s.contains("null")) {
						dto.setTimeStamp(value[1]);
					}
				} else {
					dto.setStatus(NOT_INITIATED);
				}
				String stateCode = GSTIN.substring(0, 2);
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				dto.setState(stateName);
				List<String> regName = gSTNDetailRepository
						.findgstr2avs3bRegTypeByGstin(GSTIN);
				if (regName != null && regName.size() > 0) {
					String regTypeName = regName.get(0);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal"))
					// || regTypeName.equalsIgnoreCase("regular"))
					{
						dto.setRegType("");
					} else {
						dto.setRegType(regTypeName.toUpperCase());
					}
				} else {
					dto.setRegType("");
				}
				dto.setDocType(String.valueOf(obj[4]));
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(GSTIN);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						dto.setAuthToken("Active");
					} else {
						dto.setAuthToken("Inactive");
					}
				} else {
					dto.setAuthToken("Inactive");
				}

				BigInteger integer = GenUtil.getBigInteger(obj[5]);
				dto.setCount(integer);
				dto.setInvoiceValue((BigDecimal) obj[6]);
				dto.setTaxableValue((BigDecimal) obj[7]);
				dto.setTaxPayable((BigDecimal) obj[8]);
				dto.setIgst((BigDecimal) obj[9]);
				dto.setCgst((BigDecimal) obj[10]);
				dto.setSgst((BigDecimal) obj[11]);
				dto.setCess((BigDecimal) obj[12]);

				outList.add(dto);
			}
		}
		return outList;

	}

	/*
	 * private static boolean isStringOnlyAlphabet(String str) { return ((str !=
	 * null) && (!str.equals("")) && (!str.equals("-")) &&
	 * (str.matches("^[a-zA-Z]*$"))); }
	 */

	private Map<String, String> getStatusByCriteria(
			Gstr2AProcessedRecordsReqDto gstr2aPRReqDto, List<String> gstinList)
			throws Exception {
		Map<String, String> gstinStatusMap = Maps.newHashMap();
		GetAnx2DetailStatusReqDto criteria = new GetAnx2DetailStatusReqDto();
		criteria.setEntityId(String.valueOf(
				gstr2aPRReqDto.getEntityId().stream().findFirst().get()));
		criteria.setGstin(gstinList);
		criteria.setTaxPeriod(gstr2aPRReqDto.getFromPeriod());
		List<GetGstr2aDetailStatusRespDto> dtos = getGstr2aDetailStatusService
				.findByCriteria(criteria);

		Map<String, Set<String>> gstinsMap = Maps.newHashMap();
		Map<String, List<String>> timestampMap = Maps.newHashMap();
		dtos.forEach(dto -> {
			Set<String> statusList = Sets.newHashSet();
			statusList.add(dto.getB2bStatus());
			statusList.add(dto.getB2baStatus());
			statusList.add(dto.getCdnStatus());
			statusList.add(dto.getCdnaStatus());
			statusList.add(dto.getIsdStatus());
			statusList.add(dto.getImpgStatus());
			statusList.add(dto.getImpgSezStatus());

			/*
			 * statusList.add(isStringOnlyAlphabet(dto.getIsdaStatus()) ?
			 * dto.getIsdaStatus() : null);
			 */

			List<String> timestampList = Lists.newArrayList();
			timestampList.add(dto.getB2bTimeStamp() != null
					? dto.getB2bTimeStamp() : null);
			timestampList.add(dto.getB2baTimeStamp() != null
					? dto.getB2baTimeStamp() : null);
			timestampList.add(dto.getCdnTimeStamp() != null
					? dto.getCdnTimeStamp() : null);
			timestampList.add(dto.getCdnaTimeStamp() != null
					? dto.getCdnaTimeStamp() : null);
			timestampList.add(dto.getIsdTimeStamp() != null
					? dto.getIsdTimeStamp() : null);
			timestampList.add(dto.getImpgTimeStamp() != null
					? dto.getImpgTimeStamp() : null);
			timestampList.add(dto.getImpgSezTimeStamp() != null
					? dto.getImpgSezTimeStamp() : null);

			timestampMap.put(dto.getGstin(),
					timestampList.stream().filter(
							str -> (str != null && !str.trim().equals("-")))
							.collect(Collectors.toList()));
			gstinsMap.put(dto.getGstin(), statusList);
		});

		gstinsMap.keySet().forEach(gstin -> {
			String finalStatus = NOT_INITIATED;
			Set<String> statusList = gstinsMap.get(gstin);
			List<String> unqueSatusList = statusList.stream()
					.filter(status -> status != null)
					.collect(Collectors.toList());

			if (CollectionUtils.isNotEmpty(unqueSatusList)) {
				// Old logic (Issue in PROD even though signed off from
				// functional team)
				/*
				 * if (unqueSatusList.size() == 1) { if
				 * (unqueSatusList.contains(INPROGRESS)) { finalStatus =
				 * INPROGRESS; } else if (unqueSatusList.contains(INITIATED)) {
				 * finalStatus = INITIATED; } else if
				 * (unqueSatusList.contains(SUCCESS) ||
				 * unqueSatusList.contains(SUCCESS_WITH_NO_DATA)) { finalStatus
				 * = SUCCESS; } else if (unqueSatusList.contains(FAILED)) {
				 * finalStatus = FAILED; } else if
				 * (!unqueSatusList.contains(FAILED) &&
				 * unqueSatusList.contains(SUCCESS)) { finalStatus =
				 * PARTIALLY_SUCCESS; } } else { finalStatus =
				 * PARTIALLY_SUCCESS; }
				 */

				// Enhanced logic
				if (unqueSatusList.size() > 0) {
					if ((unqueSatusList.contains(INPROGRESS)
							|| unqueSatusList.contains(INITIATED)
							|| unqueSatusList.contains(FAILED))
							&& (unqueSatusList.contains(SUCCESS)
									|| unqueSatusList
											.contains(SUCCESS_WITH_NO_DATA))) {
						finalStatus = PARTIALLY_SUCCESS;
					} else if (unqueSatusList.contains(INPROGRESS)) {
						finalStatus = INPROGRESS;
					} else if (unqueSatusList.contains(INITIATED)) {
						finalStatus = INITIATED;
					} else if (unqueSatusList.contains(SUCCESS)
							|| unqueSatusList.contains(SUCCESS_WITH_NO_DATA)) {
						finalStatus = SUCCESS;
					} else if (unqueSatusList.contains(FAILED)) {
						finalStatus = FAILED;
					}
				}
			}

			gstinStatusMap.put(gstin, finalStatus);
		});

		return updateTimeStampOnExisitngGstin(timestampMap, gstinStatusMap);
	}

	private Map<String, String> updateTimeStampOnExisitngGstin(
			Map<String, List<String>> timestampMap,
			Map<String, String> gstinStatusMap) {
		Map<String, String> finalMap = Maps.newHashMap();
		gstinStatusMap.keySet().forEach(gstin -> {
			List<String> timestampList = timestampMap.get(gstin);
			finalMap.put(gstin, gstinStatusMap.get(gstin) + "__"
					+ getTimestamp(timestampList));
		});

		return finalMap;
	}

	private String getTimestamp(List<String> timestampList) {
		SimpleDateFormat out = new SimpleDateFormat("dd-MM-yyyy : hh:mm:ss");
		String returnStamp = null;
		try {
			if (CollectionUtils.isNotEmpty(timestampList)) {
				Date startValue = out.parse(timestampList.get(0));
				for (int i = 1; i < timestampList.size(); i++) {
					Date nextValue = out.parse(timestampList.get(i));
					if (nextValue.after(startValue)) {
						startValue = nextValue;
					}
				}

				returnStamp = out.format(startValue);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return returnStamp;
	}

	public static List<Gstr2aProcessedRecordsRespDto> convertCalcuDataToResp(
			List<Gstr2aProcessedRecordsRespDto> sortedGstinDtoList) {
		List<Gstr2aProcessedRecordsRespDto> finalRespDtos = new ArrayList<>();
		sortedGstinDtoList.stream().forEach(dto -> {
			Gstr2aProcessedRecordsRespDto respDto = new Gstr2aProcessedRecordsRespDto();
			respDto.setState(dto.getState());
			respDto.setGstin(dto.getGstin());
			respDto.setStatus(dto.getStatus());
			respDto.setRegType(dto.getRegType());
			respDto.setAuthToken(dto.getAuthToken());
			respDto.setCount(dto.getCount());
			respDto.setInvoiceValue(dto.getInvoiceValue());
			respDto.setTaxableValue(dto.getTaxableValue());
			respDto.setTaxPayable(dto.getTaxPayable());
			respDto.setIgst(dto.getIgst());
			respDto.setCgst(dto.getCgst());
			respDto.setSgst(dto.getSgst());
			respDto.setCess(dto.getCess());
			respDto.setTimeStamp(dto.getTimeStamp());
			finalRespDtos.add(respDto);
		});
		return finalRespDtos;
	}

	public void createMapByGstinBasedOnType(
			Map<String, List<Gstr2aProcessedRecordsRespDto>> outMap,
			List<Gstr2aProcessedRecordsRespDto> outRespDtoList) {
		outRespDtoList.stream().forEach(dto -> {
			outMap.put(dto.getGstin(),
					outRespDtoList.stream().filter(
							resp -> resp.getGstin().equals(dto.getGstin()))
							.collect(Collectors.toList()));
		});

	}

	public void fillTheDataFromDataSecAttr(
			Gstr2AProcessedRecordsReqDto gstr2aPRReqDto,
			List<Gstr2aProcessedRecordsRespDto> dataDtoList,
			List<String> gstinList, String taxPeriod) throws Exception {

		List<String> dataGstinList = new ArrayList<>();
		dataDtoList.forEach(dto -> dataGstinList.add(dto.getGstin()));

		Map<String, String> gstinsStatusMap = getStatusByCriteria(
				gstr2aPRReqDto, gstinList);

		for (String gstin : gstinList) {

			int count = checkTheCountByGstinTaxperiod(gstin, taxPeriod);
			if (count == 0 && !dataGstinList.contains(gstin)) {
				Gstr2aProcessedRecordsRespDto dummy = new Gstr2aProcessedRecordsRespDto();
				dummy.setGstin(gstin);

				if (gstinsStatusMap.containsKey(gstin)) {
					String value[] = gstinsStatusMap.get(gstin).split("__");
					if (value[0] != null && !value[0].contains("null")) {
						dummy.setStatus(value[0]);
					}
					String s = value[1];
					if (s != null && !s.contains("null")) {
						dummy.setTimeStamp(value[1]);
					}
				} else {
					dummy.setStatus(NOT_INITIATED);
				}

				dummy.setCount(new BigInteger("0"));
				dummy.setInvoiceValue(new BigDecimal("0.0"));
				dummy.setTaxableValue(new BigDecimal("0.0"));
				dummy.setTaxPayable(new BigDecimal("0.0"));
				dummy.setIgst(new BigDecimal("0.0"));
				dummy.setCgst(new BigDecimal("0.0"));
				dummy.setSgst(new BigDecimal("0.0"));
				dummy.setCess(new BigDecimal("0.0"));
				String stateCode = gstin.substring(0, 2);
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				dummy.setState(stateName);
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						dummy.setAuthToken("Active");
					} else {
						dummy.setAuthToken("Inactive");
					}
				} else {
					dummy.setAuthToken("Inactive");
				}

				List<String> regName = gSTNDetailRepository
						.findgstr2avs3bRegTypeByGstin(gstin);
				if (regName != null && regName.size() > 0) {
					String regTypeName = regName.get(0);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal"))
					// || regTypeName.equalsIgnoreCase("regular"))
					{
						dummy.setRegType("");
					} else {
						dummy.setRegType(regTypeName.toUpperCase());
					}
				} else {
					dummy.setRegType("");
				}

				dataGstinList.add(gstin);
				dataDtoList.add(dummy);
			} else {
				List<Gstr2aProcessedRecordsRespDto> presentList = dataDtoList
						.stream().filter(dto -> dto.getGstin().equals(gstin))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(presentList)) {
					Gstr2aProcessedRecordsRespDto dto = presentList.stream()
							.findFirst().get();
					if (gstinsStatusMap.containsKey(gstin)) {
						String value[] = gstinsStatusMap.get(gstin).split("__");
						if (value[0] != null && !value[0].contains("null")) {
							dto.setStatus(value[0]);
						}
						String s = value[1];
						if (s != null && !s.contains("null")) {
							dto.setTimeStamp(value[1]);
						}
					} else {
						dto.setStatus(NOT_INITIATED);
					}
				}
			}
		}

	}

	private int checkTheCountByGstinTaxperiod(String gstin, String taxPeriod) {
		int count = 0;
		int b2b = getGstr2B2bInvoicesRepository.gstinCount(gstin, taxPeriod);
		int b2ba = getGstr2B2baInvoicesRepository.gstinCount(gstin, taxPeriod);
		int cdn = getGstr2aCdnInvoicesRepository.gstinCount(gstin, taxPeriod);
		int cdna = getGstr2aCdnaInvoicesRepository.gstinCount(gstin, taxPeriod);
		int isd = gstr2aGetIsdInvoicesAtGstnRepository.gstinCount(gstin,
				taxPeriod);
		int isda = gstr2aGetIsdaInvoicesAtGstnRepository.gstinCount(gstin,
				taxPeriod);
		int impg = gstr2aImpgRepository.gstinCount(gstin, taxPeriod);
		int imnpgSez = gstr2aImpgSezRepository.gstinCount(gstin, taxPeriod);

		count = b2b + b2ba + cdn + cdna + isd + isda + impg + imnpgSez;

		return count;
	}

	public void calculateDataByDocType(
			Map<String, List<Gstr2aProcessedRecordsRespDto>> outMap,
			List<Gstr2aProcessedRecordsRespDto> outDistList) {
		outMap.keySet().forEach(gstinKey -> {
			List<Gstr2aProcessedRecordsRespDto> dtoList = outMap.get(gstinKey);
			Gstr2aProcessedRecordsRespDto respDto = new Gstr2aProcessedRecordsRespDto();
			String state = "";
			String gstins = "";
			String status = "";
			String regType = "";
			String timeStamp = "";
			String docType = "";
			String authToken = "";
			BigInteger count = BigInteger.ZERO;
			BigDecimal invoiceValue = BigDecimal.ZERO;
			BigDecimal taxableValue = BigDecimal.ZERO;
			BigDecimal taxPayable = BigDecimal.ZERO;
			BigDecimal igst = BigDecimal.ZERO;
			BigDecimal cgst = BigDecimal.ZERO;
			BigDecimal sgst = BigDecimal.ZERO;
			BigDecimal cess = BigDecimal.ZERO;

			for (Gstr2aProcessedRecordsRespDto dto : dtoList) {
				state = dto.getState();
				gstins = dto.getGstin();
				regType = dto.getRegType();
				timeStamp = dto.getTimeStamp();
				authToken = dto.getAuthToken();
				docType = dto.getDocType();
				status = dto.getStatus();

				count = count.add((dto.getCount() != null) ? dto.getCount()
						: BigInteger.ZERO);
				if (docType != null && (docType.equals("RCR")
						|| docType.equals("CR") || docType.equals("RFV")
						|| docType.equals("RRFV") || docType.equals("RDR"))) {
					invoiceValue = invoiceValue
							.subtract((dto.getInvoiceValue() != null)
									? dto.getInvoiceValue() : BigDecimal.ZERO);
					taxableValue = taxableValue
							.subtract((dto.getTaxableValue() != null)
									? dto.getTaxableValue() : BigDecimal.ZERO);
					taxPayable = taxPayable
							.subtract((dto.getTaxPayable() != null)
									? dto.getTaxPayable() : BigDecimal.ZERO);
					igst = igst.subtract((dto.getIgst() != null) ? dto.getIgst()
							: BigDecimal.ZERO);
					cgst = cgst.subtract((dto.getCgst() != null) ? dto.getCgst()
							: BigDecimal.ZERO);
					sgst = sgst.subtract((dto.getSgst() != null) ? dto.getSgst()
							: BigDecimal.ZERO);
					cess = cess.subtract((dto.getCess() != null) ? dto.getCess()
							: BigDecimal.ZERO);

				} else {

					invoiceValue = invoiceValue
							.add((dto.getInvoiceValue() != null)
									? dto.getInvoiceValue() : BigDecimal.ZERO);
					taxableValue = taxableValue
							.add((dto.getTaxableValue() != null)
									? dto.getTaxableValue() : BigDecimal.ZERO);
					taxPayable = taxPayable.add((dto.getTaxPayable() != null)
							? dto.getTaxPayable() : BigDecimal.ZERO);
					igst = igst.add((dto.getIgst() != null) ? dto.getIgst()
							: BigDecimal.ZERO);
					cgst = cgst.add((dto.getCgst() != null) ? dto.getCgst()
							: BigDecimal.ZERO);
					sgst = sgst.add((dto.getSgst() != null) ? dto.getSgst()
							: BigDecimal.ZERO);
					cess = cess.add((dto.getCess() != null) ? dto.getCess()
							: BigDecimal.ZERO);
				}

			}

			respDto.setState(state);
			respDto.setGstin(gstins);
			respDto.setStatus(status);
			respDto.setRegType(regType);
			respDto.setAuthToken(authToken);
			if (timeStamp != null) {
				respDto.setTimeStamp(timeStamp);
			}

			respDto.setCount(count);
			respDto.setInvoiceValue(invoiceValue);
			respDto.setTaxableValue(taxableValue);
			respDto.setTaxPayable(taxPayable);
			respDto.setIgst(igst);
			respDto.setCgst(cgst);
			respDto.setSgst(sgst);
			respDto.setCess(cess);

			outDistList.add(respDto);
		});
	}

}
