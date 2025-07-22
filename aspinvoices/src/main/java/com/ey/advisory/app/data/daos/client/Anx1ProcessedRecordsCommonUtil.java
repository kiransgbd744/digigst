package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.InwardTable3HRepository;
import com.ey.advisory.app.data.repositories.client.OutwardB2cRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsFinalRespDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsRespDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

@Component
public class Anx1ProcessedRecordsCommonUtil {

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
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("OutwardB2cRepository")
	private OutwardB2cRepository b2cRepository;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwarddocRepository;

	@Autowired
	@Qualifier("InwardTable3HRepository")
	private InwardTable3HRepository table3HRepository;

	public List<Anx1ProcessedRecordsRespDto> convertOutwardDbRecordsIntoObject(
			List<Object[]> outDataArray, String gstinUploadDate,
			List<String> outGstinList, String taxperiod) throws Exception {
		List<Anx1ProcessedRecordsRespDto> outList = new ArrayList<Anx1ProcessedRecordsRespDto>();
		List<String> dataGstinList = new ArrayList<>();
		outDataArray.forEach(dto -> dataGstinList.add(dto[0].toString()));

		for (String gstin : outGstinList) {
			Anx1ProcessedRecordsRespDto dummy = null;
			int outwardCount = checkTheCountByOutwardGstinTaxperiod(gstin,
					taxperiod);
			int inwardCount = checkTheCountByInwardGstinTaxperiod(gstin,
					taxperiod);
			if (outwardCount <= 0 && inwardCount <= 0
					|| !dataGstinList.contains(gstin)) {
				dummy = new Anx1ProcessedRecordsRespDto();
				dummy.setGstin(gstin);
				String status = "NOT INITIATED";
				dummy.setStatus(status);
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
				dummy.setOutCount(new BigInteger("0"));
				dummy.setInCount(new BigInteger("0"));
				dummy.setOutSupplies(new BigDecimal("0.0"));
				dummy.setInSupplies(new BigDecimal("0.0"));
				dummy.setOutIgst(new BigDecimal("0.0"));
				dummy.setInIgst(new BigDecimal("0.0"));
				dummy.setOutCgst(new BigDecimal("0.0"));
				dummy.setInCgst(new BigDecimal("0.0"));
				dummy.setOutSgst(new BigDecimal("0.0"));
				dummy.setInSgst(new BigDecimal("0.0"));
				dummy.setOutCess(new BigDecimal("0.0"));
				dummy.setInCess(new BigDecimal("0.0"));
				dummy.setOutType("Outward");
				dummy.setInType("Inward");
				String stateCode = gstin.substring(0, 2);
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				dummy.setState(stateName);
				List<String> regName = gSTNDetailRepository
						.findRegTypeByGstin(gstin);
				if (regName != null && regName.size() > 0) {
					String regTypeName = regName.get(0);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal")
							|| regTypeName.equalsIgnoreCase("regular")) {
						dummy.setRegType("");
					} else {
						dummy.setRegType(regTypeName.toUpperCase());
					}
				} else {
					dummy.setRegType("");
				}
			}
			if (dummy != null) {
				dataGstinList.add(gstin);
				outList.add(dummy);
			}
		}
		if (!outDataArray.isEmpty()) {
			for (Object obj[] : outDataArray) {
				Anx1ProcessedRecordsRespDto dto = new Anx1ProcessedRecordsRespDto();

				String GSTIN = String.valueOf(obj[0]);
				dto.setGstin(GSTIN);
				dto.setDocType(String.valueOf(obj[2]));
				String stateCode = GSTIN.substring(0, 2);
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				dto.setState(stateName);
				List<String> regName = gSTNDetailRepository
						.findRegTypeByGstin(GSTIN);
				if (regName != null && regName.size() > 0) {
					String regTypeName = regName.get(0);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal")
							|| regTypeName.equalsIgnoreCase("regular")) {
						dto.setRegType("");
					} else {
						dto.setRegType(regTypeName.toUpperCase());
					}
				} else {
					dto.setRegType("");
				}

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

				BigInteger integer = GenUtil.getBigInteger(obj[13]);
				dto.setOutCount(integer);
				dto.setOutSupplies((BigDecimal) obj[3]);
				dto.setOutIgst((BigDecimal) obj[4]);
				dto.setOutCgst((BigDecimal) obj[5]);
				dto.setOutSgst((BigDecimal) obj[6]);
				dto.setOutCess((BigDecimal) obj[7]);
				int totalCount, savedCount, errorCount, notSentCount,
						notSavedCount = 0;
				notSentCount = (GenUtil.getBigInteger(obj[8])).intValue();
				savedCount = (GenUtil.getBigInteger(obj[9])).intValue();
				notSavedCount = (GenUtil.getBigInteger(obj[10])).intValue();
				errorCount = (GenUtil.getBigInteger(obj[11])).intValue();
				totalCount = (GenUtil.getBigInteger(obj[12])).intValue();
				dto.setTotalCount(totalCount);
				dto.setNotSavedCount(notSavedCount);
				dto.setNotSentCount(notSentCount);
				dto.setSavedCount(savedCount);
				dto.setErrorCount(errorCount);
				dto.setStatus("");

				String timeStamp = String.valueOf(obj[14]);
				if (timeStamp == null || timeStamp == "null") {
					timeStamp = "";
					dto.setTimeStamp(timeStamp);
				} else {
					Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(timeStamp);
					String newstr = new SimpleDateFormat(
							"yyyy-MM-dd : HH:mm:ss").format(date);
					dto.setTimeStamp(newstr);
				}
				{
					outList.add(dto);
				}
			}
		}
		return outList;

	}

	private int checkTheCountByInwardGstinTaxperiod(String gstin,
			String taxperiod) {
		int count = 0;
		int inCount = inwarddocRepository.gstinCount(gstin, taxperiod);
		int outCount = table3HRepository.gstinCount(gstin, taxperiod);
		count = count + inCount + outCount;
		return count;
	}

	private int checkTheCountByOutwardGstinTaxperiod(String gstin,
			String taxperiod) {
		int count = 0;
		int gstnCount = docRepository.gstinCount(gstin, taxperiod);
		int b2cCount = b2cRepository.gstinCount(gstin, taxperiod);
		count = count + gstnCount + b2cCount;
		return count;
	}

	private static String deriveStatusByTotSavedErrorCount(int totalCount,
			int savedCount, int errorCount, int notSentCount,
			int notSavedCount) {
		if (totalCount != 0) {
			if (totalCount == notSentCount) {
				return "NOT INITIATED";
			} else if (totalCount == savedCount) {
				return "SAVED";
			} else if (totalCount == errorCount) {
				return "FAILED";
			} else {
				return "PARTIALLY SAVED";
			}
		} else {
			return "NOT INITIATED";
		}
	}

	public void createMapByGstinBasedOnType(
			Map<String, List<Anx1ProcessedRecordsRespDto>> outMap,
			List<Anx1ProcessedRecordsRespDto> outRespDtoList,
			List<Anx1ProcessedRecordsRespDto> inwardFinalList) {
		outRespDtoList.addAll(inwardFinalList);

		outRespDtoList.stream().forEach(dto -> {
			outMap.put(dto.getGstin(),
					outRespDtoList.stream().filter(
							resp -> resp.getGstin().equals(dto.getGstin()))
							.collect(Collectors.toList()));
		});

	}

	public void calculateDataByDocType(
			Map<String, List<Anx1ProcessedRecordsRespDto>> outMap,
			List<Anx1ProcessedRecordsRespDto> outDistList) {
		outMap.keySet().forEach(gstinKey -> {
			List<Anx1ProcessedRecordsRespDto> dtoList = outMap.get(gstinKey);
			Anx1ProcessedRecordsRespDto respDto = new Anx1ProcessedRecordsRespDto();
			String state = "";
			String gstins = "";
			String regType = "";
			String docType = "";
			String authToken = "";
			BigInteger outCount = BigInteger.ZERO;
			BigDecimal outSupplies = BigDecimal.ZERO;
			BigDecimal outIgst = BigDecimal.ZERO;
			BigDecimal outCgst = BigDecimal.ZERO;
			BigDecimal outSgst = BigDecimal.ZERO;
			BigDecimal outCess = BigDecimal.ZERO;
			BigInteger inCount = BigInteger.ZERO;
			BigDecimal inSupplies = BigDecimal.ZERO;
			BigDecimal inIgst = BigDecimal.ZERO;
			BigDecimal inCgst = BigDecimal.ZERO;
			BigDecimal inSgst = BigDecimal.ZERO;
			BigDecimal inCess = BigDecimal.ZERO;
			String status = "";
			String timeStamp = "";
			int totalCount = 0;
			int savedCount = 0;
			int errorCount = 0;
			int notSentCount = 0;
			int notSavedCount = 0;

			for (Anx1ProcessedRecordsRespDto dto : dtoList) {
				state = dto.getState();
				gstins = dto.getGstin();
				regType = dto.getRegType();
				authToken = dto.getAuthToken();
				docType = dto.getDocType();
				status = dto.getStatus();
				timeStamp = dto.getTimeStamp();

				totalCount = totalCount + dto.getTotalCount();
				savedCount = savedCount + dto.getSavedCount();
				errorCount = errorCount + dto.getErrorCount();
				notSentCount = notSentCount + dto.getNotSentCount();
				notSavedCount = notSavedCount + dto.getNotSavedCount();

				outCount = outCount.add((dto.getOutCount() != null
						&& dto.getOutCount().intValue() > 0) ? dto.getOutCount()
								: BigInteger.ZERO);
				inCount = inCount.add((dto.getInCount() != null
						&& dto.getInCount().intValue() > 0) ? dto.getInCount()
								: BigInteger.ZERO);
				if (docType != null && (docType.equals("RCR")
						|| docType.equals("CR") || docType.equals("RRFV")
						|| docType.equals("RFV"))) {
					outSupplies = outSupplies
							.subtract((dto.getOutSupplies() != null
									&& dto.getOutSupplies().intValue() > 0)
											? dto.getOutSupplies()
											: BigDecimal.ZERO);
					outCess = outCess.subtract((dto.getOutCess() != null
							&& dto.getOutCess().intValue() > 0)
									? dto.getOutCess() : BigDecimal.ZERO);
					outIgst = outIgst.subtract((dto.getOutIgst() != null
							&& dto.getOutIgst().intValue() > 0)
									? dto.getOutIgst() : BigDecimal.ZERO);
					outCgst = outCgst.subtract((dto.getOutCgst() != null
							&& dto.getOutCgst().intValue() > 0)
									? dto.getOutCgst() : BigDecimal.ZERO);
					outSgst = outSgst.subtract((dto.getOutSgst() != null
							&& dto.getOutSgst().intValue() > 0)
									? dto.getOutSgst() : BigDecimal.ZERO);
					inSupplies = inSupplies
							.subtract((dto.getInSupplies() != null
									&& dto.getInSupplies().intValue() > 0)
											? dto.getInSupplies()
											: BigDecimal.ZERO);
					inCess = inCess.subtract((dto.getInCess() != null
							&& dto.getInCess().intValue() > 0) ? dto.getInCess()
									: BigDecimal.ZERO);
					inIgst = inIgst.subtract((dto.getInIgst() != null
							&& dto.getInIgst().intValue() > 0) ? dto.getInIgst()
									: BigDecimal.ZERO);
					inCgst = inCgst.subtract((dto.getInCgst() != null
							&& dto.getInCgst().intValue() > 0) ? dto.getInCgst()
									: BigDecimal.ZERO);
					inSgst = inSgst.subtract((dto.getInSgst() != null
							&& dto.getInSgst().intValue() > 0) ? dto.getInSgst()
									: BigDecimal.ZERO);
				} else {
					outSupplies = outSupplies.add((dto.getOutSupplies() != null
							&& dto.getOutSupplies().intValue() > 0)
									? dto.getOutSupplies() : BigDecimal.ZERO);
					outCess = outCess.add((dto.getOutCess() != null
							&& dto.getOutCess().intValue() > 0)
									? dto.getOutCess() : BigDecimal.ZERO);
					outIgst = outIgst.add((dto.getOutIgst() != null
							&& dto.getOutIgst().intValue() > 0)
									? dto.getOutIgst() : BigDecimal.ZERO);
					outCgst = outCgst.add((dto.getOutCgst() != null
							&& dto.getOutCgst().intValue() > 0)
									? dto.getOutCgst() : BigDecimal.ZERO);
					outSgst = outSgst.add((dto.getOutSgst() != null
							&& dto.getOutSgst().intValue() > 0)
									? dto.getOutSgst() : BigDecimal.ZERO);
					inSupplies = inSupplies.add((dto.getInSupplies() != null
							&& dto.getInSupplies().intValue() > 0)
									? dto.getInSupplies() : BigDecimal.ZERO);
					inCess = inCess.add((dto.getInCess() != null
							&& dto.getInCess().intValue() > 0) ? dto.getInCess()
									: BigDecimal.ZERO);
					inIgst = inIgst.add((dto.getInIgst() != null
							&& dto.getInIgst().intValue() > 0) ? dto.getInIgst()
									: BigDecimal.ZERO);
					inCgst = inCgst.add((dto.getInCgst() != null
							&& dto.getInCgst().intValue() > 0) ? dto.getInCgst()
									: BigDecimal.ZERO);
					inSgst = inSgst.add((dto.getInSgst() != null
							&& dto.getInSgst().intValue() > 0) ? dto.getInSgst()
									: BigDecimal.ZERO);
				}
			}

			respDto.setState(state);
			respDto.setGstin(gstins);
			respDto.setRegType(regType);
			respDto.setAuthToken(authToken);
			respDto.setOutCount(outCount);
			respDto.setOutSupplies(outSupplies);
			respDto.setOutIgst(outIgst);
			respDto.setOutCgst(outCgst);
			respDto.setOutSgst(outSgst);
			respDto.setOutCess(outCess);
			respDto.setInCount(inCount);
			respDto.setInSupplies(inSupplies);
			respDto.setInIgst(inIgst);
			respDto.setInCgst(inCgst);
			respDto.setInSgst(inSgst);
			respDto.setInCess(inCess);
			respDto.setStatus(status);
			respDto.setTimeStamp(timeStamp);
			respDto.setOutType("Outward");
			respDto.setInType("Inward");
			respDto.setTotalCount(totalCount);
			respDto.setNotSavedCount(notSavedCount);
			respDto.setNotSentCount(notSentCount);
			respDto.setSavedCount(savedCount);
			respDto.setErrorCount(errorCount);

			outDistList.add(respDto);
		});

	}

	public List<Anx1ProcessedRecordsRespDto> convertInwardDbRecordsIntoObject(
			List<Object[]> inQlist, String gstinUploadDate) throws Exception {
		List<Anx1ProcessedRecordsRespDto> inList = new ArrayList<Anx1ProcessedRecordsRespDto>();

		if (!inQlist.isEmpty()) {
			for (Object inObj[] : inQlist) {
				Anx1ProcessedRecordsRespDto dto = new Anx1ProcessedRecordsRespDto();
				String GSTIN = String.valueOf(inObj[0]);
				dto.setGstin(GSTIN);
				dto.setDocType(String.valueOf(inObj[2]));
				String statecode = GSTIN.substring(0, 2);
				String statename = statecodeRepository
						.findStateNameByCode(statecode);
				dto.setState(statename);
				List<String> regName = gSTNDetailRepository
						.findRegTypeByGstin(GSTIN);
				if (regName != null && regName.size() > 0) {
					String regTypeName = regName.get(0);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal")
							|| regTypeName.equalsIgnoreCase("regular")) {
						dto.setRegType("");
					} else {
						dto.setRegType(regTypeName.toUpperCase());
					}
				} else {
					dto.setRegType("");
				}

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

				BigInteger integer = BigInteger.ZERO;
				if (inObj[13] != null) {
					integer = GenUtil.getBigInteger(inObj[13]);
				}
				dto.setInCount(integer);

				BigDecimal inSupplies = BigDecimal.ZERO;
				if (inObj[3] != null) {
					inSupplies = (BigDecimal) inObj[3];
				}
				dto.setInSupplies(inSupplies);
				BigDecimal inIgst = BigDecimal.ZERO;
				if (inObj[4] != null) {
					inIgst = (BigDecimal) inObj[4];
				}
				dto.setInIgst(inIgst);
				BigDecimal inCgst = BigDecimal.ZERO;
				if (inObj[5] != null) {
					inCgst = (BigDecimal) inObj[5];
				}
				dto.setInCgst(inCgst);
				BigDecimal inSgst = BigDecimal.ZERO;
				if (inObj[6] != null) {
					inSgst = (BigDecimal) inObj[6];
				}
				dto.setInSgst(inSgst);
				BigDecimal inCess = BigDecimal.ZERO;
				if (inObj[7] != null) {
					inCess = (BigDecimal) inObj[7];
				}
				dto.setInCess(inCess);
				int totalCount, savedCount, errorCount, notSentCount,
						notSavedCount = 0;

				notSentCount = (GenUtil.getBigInteger(inObj[8])).intValue();
				savedCount = (GenUtil.getBigInteger(inObj[9])).intValue();
				notSavedCount = (GenUtil.getBigInteger(inObj[10])).intValue();
				errorCount = (GenUtil.getBigInteger(inObj[11])).intValue();
				totalCount = (GenUtil.getBigInteger(inObj[12])).intValue();
				dto.setTotalCount(totalCount);
				dto.setNotSavedCount(notSavedCount);
				dto.setNotSentCount(notSentCount);
				dto.setSavedCount(savedCount);
				dto.setErrorCount(errorCount);
				dto.setStatus("");
				String timeStamp = String.valueOf(inObj[14]);
				if (timeStamp == null || timeStamp == "null") {
					timeStamp = "";
					dto.setTimeStamp(timeStamp);
				} else {
					Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(timeStamp);
					String newstr = new SimpleDateFormat(
							"yyyy-MM-dd : HH:mm:ss").format(date);
					dto.setTimeStamp(newstr);
				}
				{
					inList.add(dto);
				}
			}
		}
		return inList;
	}

	public static List<Anx1ProcessedRecordsFinalRespDto> convertCalcuDataToResp(
			List<Anx1ProcessedRecordsRespDto> sortedGstinDtoList) {
		List<Anx1ProcessedRecordsFinalRespDto> finalRespDtos = new ArrayList<>();
		sortedGstinDtoList.stream().forEach(dto -> {
			Anx1ProcessedRecordsFinalRespDto respDto = new Anx1ProcessedRecordsFinalRespDto();
			respDto.setState(dto.getState());
			respDto.setGstin(dto.getGstin());
			respDto.setRegType(dto.getRegType());
			respDto.setAuthToken(dto.getAuthToken());
			respDto.setOutCount(dto.getOutCount());
			respDto.setOutSupplies(dto.getOutSupplies());
			respDto.setOutIgst(dto.getOutIgst());
			respDto.setOutCgst(dto.getOutCgst());
			respDto.setOutSgst(dto.getOutSgst());
			respDto.setOutCess(dto.getOutCess());
			respDto.setInCount(dto.getInCount());
			respDto.setInSupplies(dto.getInSupplies());
			respDto.setInIgst(dto.getInIgst());
			respDto.setInCgst(dto.getInCgst());
			respDto.setInSgst(dto.getInSgst());
			respDto.setInCess(dto.getInCess());
			respDto.setStatus(dto.getStatus());
			respDto.setTimeStamp(dto.getTimeStamp());
			respDto.setOutType(dto.getOutType());
			respDto.setInType(dto.getInType());
			respDto.setStatus(
					deriveStatusByTotSavedErrorCount(dto.getTotalCount(),
							dto.getSavedCount(), dto.getErrorCount(),
							dto.getNotSentCount(), dto.getNotSavedCount()));

			finalRespDtos.add(respDto);
		});
		return finalRespDtos;
	}

}
