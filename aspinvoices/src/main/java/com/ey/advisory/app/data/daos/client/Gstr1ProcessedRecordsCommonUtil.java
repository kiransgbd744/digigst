package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr1EinvSeriesCompEntity;
import com.ey.advisory.app.data.entities.client.Gstr1InvoiceFileUploadEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ARRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ATARepository;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EinvSeriesCompRepo;
import com.ey.advisory.app.data.repositories.client.Gstr1InvoiceRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1Vs3bStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1vs3BComputeRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bProcessSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.ITC04ProcessSummaryRespDto;
import com.ey.advisory.app.processors.handler.GstnReturnStatusUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr1ProcessedRecordsCommonUtil")
public class Gstr1ProcessedRecordsCommonUtil {

	private static final String NOT_INITIATED = "NOT_INITIATED";

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GstnSubmitRepository")
	private GstnSubmitRepository gstnSubmitRepository;

	@Autowired
	@Qualifier("Gstr1Vs3bStatusRepository")
	private Gstr1Vs3bStatusRepository gstr1Vs3bStatusRepository;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository getAnx1BatchRepository;

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
	@Qualifier("Gstr1B2CSRepository")
	private Gstr1B2CSRepository gstr1B2CSRepository;

	@Autowired
	@Qualifier("Gstr1ARRepository")
	private Gstr1ARRepository gstr1ARRepository;

	@Autowired
	@Qualifier("Gstr1ATARepository")
	private Gstr1ATARepository gstr1ATARepository;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	@Qualifier("Gstr1vs3BComputeRepository")
	Gstr1vs3BComputeRepository gstr1vs3bRepo;

	@Autowired
	@Qualifier("Gstr1InvoiceRepository")
	private Gstr1InvoiceRepository gstr1InvoiceRepository;

	@Autowired
	private Gstr1EinvSeriesCompRepo gstr1EinvSeriesCompRepo;

	@Autowired
	DefaultStateCache defaultStateCache;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;

	@Autowired
	private GstnReturnStatusUtil returnStatusUtil;

	public List<Gstr1ProcessedRecordsRespDto> convertGstr1RecordsIntoObject(
			List<Object[]> outDataArray, Map<String, String> gstinAuthMap,
			Map<String, String> regTypeMap) throws Exception {
		List<Gstr1ProcessedRecordsRespDto> outList = new ArrayList<Gstr1ProcessedRecordsRespDto>();
		if (!outDataArray.isEmpty()) {
			for (Object obj[] : outDataArray) {
				Gstr1ProcessedRecordsRespDto dto = new Gstr1ProcessedRecordsRespDto();
				String gstin = String.valueOf(obj[0]);
				String returnPeriod = String.valueOf(obj[1]);
				dto.setGstin(gstin);
				dto.setReturnPeriod(returnPeriod);
				dto.setTaxDocType(String.valueOf(obj[2]));
				String stateCode = gstin.substring(0, 2);
				String stateName = defaultStateCache.getStateName(stateCode);
				dto.setState(stateName);
				if (!regTypeMap.isEmpty()) {
				String regTypeName = regTypeMap.get(gstin);
				if (!Strings.isNullOrEmpty(regTypeName)) {
					dto.setRegType(regTypeName.toUpperCase());
				} else {
					dto.setRegType("");
				}
				}
				// Checking GSTIN Active Status.
				if (!gstinAuthMap.isEmpty()) {
					String gstnAct = gstinAuthMap.get(gstin);
					if (gstnAct.equalsIgnoreCase("A")) {
						dto.setAuthToken("Active");
					} else {
						dto.setAuthToken("Inactive");
					}
				} else {
					dto.setAuthToken("Inactive");
				}

				BigInteger integer = GenUtil.getBigInteger(obj[13]);
				dto.setCount(integer);
				dto.setSupplies((BigDecimal) obj[3]);
				dto.setIgst((BigDecimal) obj[4]);
				dto.setCgst((BigDecimal) obj[5]);
				dto.setSgst((BigDecimal) obj[6]);
				dto.setCess((BigDecimal) obj[7]);
				outList.add(dto);
			}
		}
		return outList;

	}

	public List<Gstr1ProcessedRecordsRespDto> convertCalcuDataToResp(
			List<Gstr1ProcessedRecordsRespDto> sortedGstinDtoList) {
		List<Gstr1ProcessedRecordsRespDto> finalRespDtos = new ArrayList<>();

		sortedGstinDtoList.stream().forEach(dto -> {
			Gstr1ProcessedRecordsRespDto respDto = new Gstr1ProcessedRecordsRespDto();
			respDto.setState(dto.getState());
			respDto.setGstin(dto.getGstin());
			respDto.setReturnPeriod(dto.getReturnPeriod());
			respDto.setRegType(dto.getRegType());
			respDto.setAuthToken(dto.getAuthToken());
			respDto.setCount(dto.getCount());
			respDto.setSupplies(dto.getSupplies());
			respDto.setIgst(dto.getIgst());
			respDto.setCgst(dto.getCgst());
			respDto.setSgst(dto.getSgst());
			respDto.setCess(dto.getCess());
			respDto.setStatus(dto.getStatus());

			Optional<Gstr1InvoiceFileUploadEntity> isAvailable = gstr1InvoiceRepository
					.findTop1BySgstinAndReturnPeriodAndIsDeleteFalseOrderByCreatedOnDesc(
							dto.getGstin(), dto.getReturnPeriod());

			if (isAvailable.isPresent()) {
				if ("C".equalsIgnoreCase(
						isAvailable.get().getDataOriginType())) {
					Optional<Gstr1EinvSeriesCompEntity> isCompDtlsAva = gstr1EinvSeriesCompRepo
							.findByGstinAndReturnPeriodAndIsActiveTrue(
									dto.getGstin(), dto.getReturnPeriod());
					if (isCompDtlsAva.isPresent()) {
						dto.setInvTimeStamp(
								EYDateUtil.fmt(EYDateUtil.toISTDateTimeFromUTC(
										isAvailable.get().getCreatedOn())));
						dto.setInvSerStatus("DigiGST compute -"
								+ isCompDtlsAva.get().getRequestStatus());
					} else {
						dto.setInvSerStatus(APIConstants.NOT_INITIATED);
					}
				} else {
					dto.setInvSerStatus("File Upload");
				}
			} else {
				dto.setInvSerStatus(APIConstants.NOT_INITIATED);
			}

			Pair<String, String> lastTransactionReturnStatus = returnStatusUtil
					.getLastTransactionReturnStatus(dto.getGstin(),
							dto.getReturnPeriod(),
							APIConstants.GSTR1.toUpperCase());
			if (lastTransactionReturnStatus != null) {
				respDto.setStatus(lastTransactionReturnStatus.getValue0());
				respDto.setTimeStamp(lastTransactionReturnStatus.getValue1());
			}

			LOGGER.debug("RespDto {} ", respDto);
			finalRespDtos.add(respDto);
		});
		return finalRespDtos;
	}

	public void createMapByGstinBasedOnType(
			Map<String, List<Gstr1ProcessedRecordsRespDto>> outMap,
			List<Gstr1ProcessedRecordsRespDto> outRespDtoList) {
		outRespDtoList.stream().forEach(dto -> {
			outMap.put(dto.getGstin(),
					outRespDtoList.stream().filter(
							resp -> resp.getGstin().equals(dto.getGstin()))
							.collect(Collectors.toList()));
		});

	}

	public void calculateDataByDocType(
			Map<String, List<Gstr1ProcessedRecordsRespDto>> outMap,
			List<Gstr1ProcessedRecordsRespDto> outDistList) {
		outMap.keySet().forEach(gstinKey -> {
			List<Gstr1ProcessedRecordsRespDto> dtoList = outMap.get(gstinKey);
			Gstr1ProcessedRecordsRespDto respDto = new Gstr1ProcessedRecordsRespDto();
			String state = "";
			String gstins = "";
			String retPeriod = "";
			String regType = "";
			String taxDocType = "";
			String authToken = "";
			BigInteger count = BigInteger.ZERO;
			BigDecimal outSupplies = BigDecimal.ZERO;
			BigDecimal igst = BigDecimal.ZERO;
			BigDecimal cgst = BigDecimal.ZERO;
			BigDecimal sgst = BigDecimal.ZERO;
			BigDecimal cess = BigDecimal.ZERO;
			String timeStamp = "";
			String status = "";
			for (Gstr1ProcessedRecordsRespDto dto : dtoList) {
				state = dto.getState();
				gstins = dto.getGstin();
				retPeriod = dto.getReturnPeriod();
				regType = dto.getRegType();
				authToken = dto.getAuthToken();
				timeStamp = dto.getTimeStamp();
				status = dto.getStatus();
				taxDocType = dto.getTaxDocType();
				count = count.add((dto.getCount() != null
						&& dto.getCount().intValue() > 0) ? dto.getCount()
								: BigInteger.ZERO);
				if (taxDocType != null && taxDocType.equals("ADV ADJ")) {
					outSupplies = outSupplies
							.subtract((dto.getSupplies() != null
									&& dto.getSupplies().intValue() > 0)
											? dto.getSupplies()
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
					outSupplies = outSupplies.add((dto.getSupplies() != null)
							? dto.getSupplies() : BigDecimal.ZERO);
					cess = cess.add((dto.getCess() != null) ? dto.getCess()
							: BigDecimal.ZERO);
					igst = igst.add((dto.getIgst() != null) ? dto.getIgst()
							: BigDecimal.ZERO);
					cgst = cgst.add((dto.getCgst() != null) ? dto.getCgst()
							: BigDecimal.ZERO);
					sgst = sgst.add((dto.getSgst() != null) ? dto.getSgst()
							: BigDecimal.ZERO);
				}
			}
			respDto.setState(state);
			respDto.setGstin(gstins);
			respDto.setReturnPeriod(retPeriod);
			respDto.setRegType(regType);
			respDto.setAuthToken(authToken);
			respDto.setCount(count);
			respDto.setSupplies(outSupplies);
			respDto.setIgst(igst);
			respDto.setCgst(cgst);
			respDto.setSgst(sgst);
			respDto.setTimeStamp(timeStamp);
			respDto.setCess(cess);
			respDto.setStatus(status);
			/*
			 * respDto.setTotalCount(totalCount);
			 * respDto.setNotSavedCount(notSavedCount);
			 * respDto.setNotSentCount(notSentCount);
			 * respDto.setSavedCount(savedCount);
			 * respDto.setErrorCount(errorCount);
			 */

			outDistList.add(respDto);
		});
	}

	@SuppressWarnings("unused")
	public void fillTheDataFromDataSecAttr(
			List<Gstr1ProcessedRecordsRespDto> dataDtoList,
			List<String> gstinList, String taxPeriod,
			Map<String, String> gstinAuthMap, Map<String, String> regTypeMap) {

		List<String> dataGstinList = new ArrayList<>();
		dataDtoList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Starting Line 493 dataDtoList is ->{},dataGstinList is -{}"
							+ dataDtoList.size(),
					dataGstinList.size());
		}

		for (String gstin : gstinList) {
			int count = checkTheCountByGstinTaxperiod(gstin, taxPeriod);
			if (count == 0 || !dataGstinList.contains(gstin)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							" Line 501 dataDtoList is ->{},dataGstinList is -{}, count ->{}"
									+ dataDtoList.size(),
							dataGstinList.size(), count);
				}
				Gstr1ProcessedRecordsRespDto dummy = new Gstr1ProcessedRecordsRespDto();
				dummy.setGstin(gstin);
				dummy.setReturnPeriod(taxPeriod);
				String status = "NOT INITIATED";
				dummy.setStatus(status);
				dummy.setCount(new BigInteger("0"));
				dummy.setSupplies(new BigDecimal("0.0"));
				dummy.setIgst(new BigDecimal("0.0"));
				dummy.setCgst(new BigDecimal("0.0"));
				dummy.setSgst(new BigDecimal("0.0"));
				dummy.setCess(new BigDecimal("0.0"));
				String stateCode = gstin.substring(0, 2);
				String stateName = defaultStateCache.getStateName(stateCode);
				dummy.setState(stateName);
				if (!regTypeMap.isEmpty()) {
					String regTypeName = regTypeMap.get(gstin);
					if (!Strings.isNullOrEmpty(regTypeName)) {
						dummy.setRegType(regTypeName.toUpperCase());
					} else {
						dummy.setRegType("");
					}
				}
				// Checking GSTIN Active Status.
				if (!gstinAuthMap.isEmpty()) {
					String gstnAct = gstinAuthMap.get(gstin);
					if (gstnAct.equalsIgnoreCase("A")) {
						dummy.setAuthToken("Active");
					} else {
						dummy.setAuthToken("Inactive");
					}
				} else {
					dummy.setAuthToken("Inactive");
				}

				dataGstinList.add(gstin);
				dataDtoList.add(dummy);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Line 548 dataDtoList is ->{},dataGstinList is -{}"
						+ dataDtoList.size(), dataGstinList.size());
			}
		}

	}

	private int checkTheCountByGstinTaxperiod(String gstin, String taxPeriod) {
		int count = 0;
		int docCount = docRepository.gstinCount(gstin, taxPeriod);
		int b2csCount = gstr1B2CSRepository.gstinCount(gstin, taxPeriod);
		int arCount = gstr1ARRepository.gstinCount(gstin, taxPeriod);
		int ataCount = gstr1ATARepository.gstinCount(gstin, taxPeriod);
		count = count + docCount + b2csCount + arCount + ataCount;
		return count;
	}

	/*
	 * private int checkTheCountForGstr1vs3bByGstinTaxperiod(String gstin,
	 * String taxPeriodFrom, String taxPeriodTo) { int count = 0; int
	 * derivedRetPerFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom); int
	 * derivedRetPerTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
	 * 
	 * int docCount = gstr1vs3bRepo.gstinCount(gstin, derivedRetPerFrom,
	 * derivedRetPerTo); int statusCount =
	 * gstr1Vs3bStatusRepository.gstinCount(gstin, derivedRetPerFrom,
	 * derivedRetPerTo);
	 * 
	 * count = count + docCount + statusCount; return count; }
	 */

	public void fillGstr1vs3BTheDataFromDataSecAttr(
			List<Gstr1VsGstr3bProcessSummaryRespDto> dataDtoList,
			List<String> gstinList, String taxPeriodFrom, String taxPeriodTo,
			Gstr1VsGstr3bProcessSummaryReqDto reqDto,
			Map<String, String> statusMap,
			Map<String, String> gstr3bStatusMap,Map<String,String> gstr1aStatusMap) {

		List<String> dataGstinList = new ArrayList<>();
		dataDtoList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		for (String gstin : gstinList) {
			// int count = checkTheCountForGstr1vs3bByGstinTaxperiod(gstin,
			// taxPeriodFrom, taxPeriodTo);
			if (dataDtoList.size() == 0 || !dataGstinList.contains(gstin)) {
				Gstr1VsGstr3bProcessSummaryRespDto dummy = new Gstr1VsGstr3bProcessSummaryRespDto();
				dummy.setGstin(gstin);
				String status = "NOT_INITIATED";

				if (statusMap.containsKey(gstin)) {
					String value[] = statusMap.get(gstin).split("__");
					if (value[0] != null && !value[0].contains("null")) {
						dummy.setGstr1Status(value[0]);
					}
					String s = value[1];
					if (value[1] != null && !s.contains("null")) {
						dummy.setGstr1Time(value[1]);
					}
				} else {
					dummy.setGstr1Status(NOT_INITIATED);
				}
				//Gstr1a Status
			/*	if (gstr1aStatusMap.containsKey(gstin)) {
					String value[] = statusMap.get(gstin).split("__");
					if (value[0] != null && !value[0].contains("null")) {
						dummy.setGstr1aStatus(value[0]);
					}
					String s = value[1];
					if (value[1] != null && !s.contains("null")) {
						dummy.setGstr1aTime(value[1]);
					}
				} else {
					dummy.setGstr1aStatus(NOT_INITIATED);
				}*/
				
				if (gstr1aStatusMap.containsKey(gstin)) {
				    String status1a = gstr1aStatusMap.get(gstin);
				    
				    if (status1a != null) {
				        String value[] = status1a.split("__");

				        if (value.length > 0 && value[0] != null && !value[0].contains("null")) {
				            dummy.setGstr1aStatus(value[0]);
				        }

				        if (value.length > 1 && value[1] != null && !value[1].contains("null")) {
				            dummy.setGstr1aTime(value[1]);
				        }
				    } else {
				        // Handle the case where status is null
				        dummy.setGstr1aStatus(NOT_INITIATED);
				    }
				} else {
				    dummy.setGstr1aStatus(NOT_INITIATED);
				}
				if (gstr3bStatusMap.containsKey(gstin)) {
					String value[] = gstr3bStatusMap.get(gstin).split("__");
					if (value[0] != null && !value[0].contains("null")) {
						dummy.setGstr3bStatus(value[0]);
					}
					String s = value[1];
					if (value[1] != null && !s.contains("null")) {
						dummy.setGstr3bTime(value[1]);
					}
				} else {
					dummy.setGstr3bStatus(NOT_INITIATED);
				}
				dummy.setGstr1TaxableValue(new BigDecimal("0.0"));
				dummy.setGstr1TotalTax(new BigDecimal("0.0"));
				dummy.setGstr3bTaxableValue(new BigDecimal("0.0"));
				dummy.setGstr3bTotalTax(new BigDecimal("0.0"));
				dummy.setReconStatus(status);
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

				dataGstinList.add(gstin);
				dataDtoList.add(dummy);
			}
		}

	}

	public void fillITC04TheDataFromDataSecAttr(
			List<ITC04ProcessSummaryRespDto> dataDtoList,
			List<String> gstinList, String taxPeriod) {

		List<String> dataGstinList = new ArrayList<>();
		dataDtoList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		for (String gstin : gstinList) {
			// int count =0;
			// int count =
			// checkTheCountForGstr1vs3bByGstinTaxperiod(gstin,taxPeriodFrom,taxPeriodTo);
			if (dataDtoList.size() == 0 || !dataGstinList.contains(gstin)) {
				ITC04ProcessSummaryRespDto dummy = new ITC04ProcessSummaryRespDto();
				dummy.setGstin(gstin);
				String status = "NOT INITIATED";
				dummy.setGrCount(0);
				dummy.setGrQuantityLoss(new BigInteger("0"));
				dummy.setGrQuantityRece(new BigInteger("0"));
				dummy.setGsCount(0);
				dummy.setGsQuantity(new BigDecimal("0.0"));
				dummy.setGsTaxableValue(new BigDecimal("0.0"));
				dummy.setSaveStatus(status);
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

				/*
				 * LOGGER.
				 * error("Group Code before calling RegType Method -------->{}",
				 * TenantContext.getTenantId()); String groupCode =
				 * TenantContext.getTenantId(); GSTNDetailEntity gstinlist =
				 * ehcachegstin.getGstinInfo(groupCode, gstin); String
				 * regTypeName = null; if(gstinlist.getRegistrationType() !=
				 * null){ regTypeName =
				 * gstinlist.getRegistrationType().toUpperCase(); } if
				 * (regTypeName == null ||
				 * regTypeName.equalsIgnoreCase("normal") ||
				 * regTypeName.equalsIgnoreCase("regular")) {
				 * dummy.setRegType(""); } else {
				 * dummy.setRegType(regTypeName.toUpperCase()); }
				 */

				dataGstinList.add(gstin);
				dataDtoList.add(dummy);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Printing Dummy Data For Gstn's ---> {}",
							dataDtoList);
					LOGGER.debug("GSTIN LIST pertivular Entity is ---> {}",
							dataGstinList);
				}
			}
		}

	}

	public List<ITC04ProcessSummaryRespDto> convertItc04CalcuDataToResp(
			List<ITC04ProcessSummaryRespDto> sortedGstinDtoList) {
		List<ITC04ProcessSummaryRespDto> finalRespDtos = new ArrayList<>();
		sortedGstinDtoList.stream().forEach(dto -> {
			ITC04ProcessSummaryRespDto respDto = new ITC04ProcessSummaryRespDto();
			respDto.setState(dto.getState());
			respDto.setGstin(dto.getGstin());
			respDto.setAuthToken(dto.getAuthToken());
			// respDto.setTotalCount(dto.getTotalCount());
			respDto.setGrCount(dto.getGrCount());
			respDto.setGrQuantityLoss(dto.getGrQuantityLoss());
			respDto.setGrQuantityRece(dto.getGrQuantityRece());
			respDto.setGsCount(dto.getGsCount());
			respDto.setGsQuantity(dto.getGsQuantity());
			respDto.setGsTaxableValue(dto.getGsTaxableValue());
			respDto.setRegType(dto.getRegType());
			// respDto.setTimeStamp(dto.getTimeStamp());
			// setting null value here to execute the if block and obtain actual
			// status
			respDto.setSaveStatus(null);
			/*
			 * if (respDto.getSaveStatus() == null ||
			 * respDto.getSaveStatus().isEmpty()) {
			 * respDto.setSaveStatus(deriveStatusByTotSavedErrorCount(
			 * dto.getTotalCount(), dto.getSavedCount(), dto.getErrorCount(),
			 * dto.getNotSentCount(), dto.getNotSavedCount(), dto.getGstin(),
			 * dto.getTaxPeriod(), APIConstants.ITC04.toUpperCase(),
			 * TenantContext.getTenantId()));
			 * 
			 * } if ("FILED".equalsIgnoreCase(respDto.getSaveStatus()) ||
			 * "SUBMITTED".equalsIgnoreCase(respDto.getSaveStatus()) ||
			 * "IN PROGRESS" .equalsIgnoreCase(respDto.getSaveStatus())) {
			 * LocalDateTime saveInitiatedTime = getCallInitiatedTime(
			 * dto.getGstin(), dto.getTaxPeriod(),
			 * APIConstants.ITC04.toUpperCase(), respDto.getSaveStatus());
			 * String lastUpdatedDate = ""; if (saveInitiatedTime != null) {
			 * LocalDateTime istDateTimeFromUTC = EYDateUtil
			 * .toISTDateTimeFromUTC(saveInitiatedTime); DateTimeFormatter
			 * FOMATTER = DateTimeFormatter .ofPattern("dd-MM-yyyy HH:mm:ss");
			 * lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);
			 * 
			 * } respDto.setTimeStamp(lastUpdatedDate); } else
			 */

			Pair<String, String> lastTransactionReturnStatus = returnStatusUtil
					.getLastTransactionReturnStatus(dto.getGstin(),
							dto.getTaxPeriod(),
							APIConstants.ITC04.toUpperCase());

			if (lastTransactionReturnStatus != null) {
				respDto.setSaveStatus(lastTransactionReturnStatus.getValue0());
				respDto.setTimeStamp(lastTransactionReturnStatus.getValue1());
			}

			/*
			 * if ("NOT INITIATED".equalsIgnoreCase(respDto.getSaveStatus())) {
			 * respDto.setTimeStamp(""); } else {
			 * respDto.setTimeStamp(dto.getTimeStamp()); }
			 */
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Group Code before calling RegType Method -------->{}",
						TenantContext.getTenantId());
			}

			List<String> regName = gSTNDetailRepository
					.findRegTypeByGstin(dto.getGstin());
			if (regName != null && regName.size() > 0) {
				String regTypeName = regName.get(0);
				if (regTypeName == null
						|| regTypeName.equalsIgnoreCase("normal")
						|| regTypeName.equalsIgnoreCase("regular")) {
					respDto.setRegType("");
				} else {
					respDto.setRegType(regTypeName.toUpperCase());
				}
			} else {
				respDto.setRegType("");
			}

			/*
			 * String groupCode = TenantContext.getTenantId(); GSTNDetailEntity
			 * gstinlist = ehcachegstin.getGstinInfo(groupCode, dto.getGstin());
			 * String regTypeName = null; if(gstinlist.getRegistrationType() !=
			 * null){ regTypeName =
			 * gstinlist.getRegistrationType().toUpperCase(); } if (regTypeName
			 * == null || regTypeName.equalsIgnoreCase("normal") ||
			 * regTypeName.equalsIgnoreCase("regular")) {
			 * respDto.setRegType(""); } else {
			 * respDto.setRegType(regTypeName.toUpperCase()); }
			 */

			finalRespDtos.add(respDto);
		});
		return finalRespDtos;
	}

}