package com.ey.advisory.app.services.gstr1fileupload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Gstr1AdvanceAdjustmentFileUploadEntity;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAdvanceAdjustmentFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AATARepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ATARepository;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSRepository;
import com.ey.advisory.app.docs.dto.Gstr1VerticalAtRespDto;
import com.ey.advisory.app.services.docs.SRFileToTxpdDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.advanceAdjusted.AAStructValidationChain;
import com.ey.advisory.app.services.strcutvalidation.b2cs.B2csApiStructValidationChain;
import com.ey.advisory.app.services.validation.advanceAdjusted.AdvancedAdjustmentBusinessChain;
import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Service("Gstr1TxpdUpdateService")
public class Gstr1TxpdUpdateService {

	private final static String WEB_UPLOAD_KEY = "|";

	@Autowired
	@Qualifier("AAStructValidationChain")
	private AAStructValidationChain aAStructValidationChain;

	@Autowired
	@Qualifier("AdvancedAdjustmentBusinessChain")
	private AdvancedAdjustmentBusinessChain advancedAdjustmentBusinessChain;

	@Autowired
	@Qualifier("Gstr1B2CSRepository")
	Gstr1B2CSRepository repository;

	@Autowired
	@Qualifier("B2csApiStructValidationChain")
	B2csApiStructValidationChain b2csStructValidationChain;

	@Autowired
	@Qualifier("Gstr1ATARepository")
	private Gstr1ATARepository gstr1ATARepository;
	
	@Autowired
	@Qualifier("Gstr1AATARepository")
	private Gstr1AATARepository gstr1aATARepository;

	@Autowired
	@Qualifier("SRFileToATADetailsConvertion")
	private SRFileToTxpdDetailsConvertion sRFileToATADetailsConvertion;

	@Transactional(value = "clientTransactionManager")
	public JsonObject updateVerticalData(List<Gstr1VerticalAtRespDto> list) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonElement respBody = null;
		JsonObject resp = new JsonObject();
		List<Gstr1VerticalAtRespDto> respList = new ArrayList<>();
		List<Object[]> listObAt = convertsJavaObjects(list);
		Map<String, List<ProcessingResult>> processingResults = aAStructValidationChain
				.validationApi(listObAt, list);

		Map<String, Gstr1VerticalAtRespDto> dtoResponse = new HashMap<>();

		List<String> strErrorKeys = new ArrayList<>();

		for (String keys : processingResults.keySet()) {
			String errkey = keys.substring(0, keys.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		List<String> processedKeys = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listStrProcess = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listStrErrors = new ArrayList<>();

		for (Gstr1VerticalAtRespDto strDto : list) {
			Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			String invAtKey = strucProcessed.getTxpdInvKey();
			if (!strErrorKeys.contains(invAtKey)) {
				processedKeys.add(invAtKey);
				listStrProcess.add(strDto);
			} else {
				listStrErrors.add(strDto);
			}
		}
		if (strErrorKeys.size() > 0 && !strErrorKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listStrErrors) {
				Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getTxpdInvKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						processingResults.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();
		List<ProcessingResult> current = null;
		List<String> errorKeys = new ArrayList<>();
		List<String> errorInfo = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listInfoProcess = new ArrayList<>();

		if (listStrProcess.size() > 0 && !listStrProcess.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listStrProcess) {
				Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String invAtKey = strucProcessed.getTxpdInvKey();
				String keys = invAtKey.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				List<ProcessingResult> results = advancedAdjustmentBusinessChain
						.validate(strucProcessed, null);
				if (results != null && !results.isEmpty()) {
					List<ProcessingResultType> listTypes = new ArrayList<>();
					for (ProcessingResult types : results) {
						ProcessingResultType type = types.getType();
						listTypes.add(type);
					}
					List<String> errorType = listTypes.stream()
							.map(object -> Objects.toString(object, null))
							.collect(Collectors.toList());

					current = businessValErrors.get(keys);
					if (current == null) {
						current = new ArrayList<>();
						if (errorType.size() > 0) {
							if (errorType.contains(GSTConstants.ERROR)
									&& errorType.contains(GSTConstants.INFO)) {
								errorInfo.add(invAtKey);
								errorKeys.add(invAtKey);
								businessValErrors.put(keys, results);
							} else if (errorType.contains(GSTConstants.ERROR)) {
								errorKeys.add(invAtKey);
								businessValErrors.put(keys, results);
							} else {
								infoWithProcessed.put(keys, results);
								infoKeys.add(invAtKey);
								listInfoProcess.add(strDto);
							}
						}
					} else {
						if (errorType.size() > 0) {
							if (errorType.contains(GSTConstants.ERROR)
									&& errorType.contains(GSTConstants.INFO)) {
								errorInfo.add(invAtKey);
								errorKeys.add(invAtKey);
								businessValErrors
										.computeIfAbsent(keys,
												k -> new ArrayList<ProcessingResult>())
										.addAll(results);
							} else if (errorType.contains(GSTConstants.ERROR)) {
								errorKeys.add(invAtKey);
								businessValErrors
										.computeIfAbsent(keys,
												k -> new ArrayList<ProcessingResult>())
										.addAll(results);
							} else {
								infoWithProcessed
										.computeIfAbsent(keys,
												k -> new ArrayList<ProcessingResult>())
										.addAll(results);
								infoKeys.add(invAtKey);
								listInfoProcess.add(strDto);
							}
						}
					}
				}
			}
		}

		List<String> businessKeys = new ArrayList<String>();
		List<Gstr1VerticalAtRespDto> listBusiProcess = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listBusiErrors = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listBusinessResponse = new ArrayList<>();

		if (businessValErrors.size() > 0 && !businessValErrors.isEmpty()) {
			for (String businessRules : businessValErrors.keySet()) {
				String errkey = businessRules.substring(0,
						businessRules.lastIndexOf('-'));
				businessKeys.add(errkey);
			}
		}
		for (Gstr1VerticalAtRespDto strDto : listStrProcess) {
			Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			String invAtKey = strucProcessed.getTxpdInvKey();
			if (!businessKeys.contains(invAtKey)) {
				processedKeys.add(invAtKey);
				listBusiProcess.add(strDto);
			} else {
				listBusiErrors.add(strDto);
			}
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listInfoProcess) {
				Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getTxpdInvKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						infoWithProcessed.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		if (businessKeys.size() > 0 && !businessKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listBusiErrors) {
				Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getTxpdInvKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						businessValErrors.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		List<Gstr1AsEnteredTxpdFileUploadEntity> listBusinessProcess = new ArrayList<>();
		for (Gstr1VerticalAtRespDto strDto : listBusiProcess) {
			Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			listBusinessResponse.add(strDto);
			String invAtKey = strucProcessed.getTxpdInvKey();
			if (!businessKeys.contains(invAtKey)) {
				listBusinessProcess.add(strucProcessed);
			}
		}
		List<Gstr1AdvanceAdjustmentFileUploadEntity> finalList = new ArrayList<>();
		List<String> existProcessData = new ArrayList<>();
		if (listBusinessResponse != null && !listBusinessResponse.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listBusinessResponse) {
				Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);

				String errorKey = strucProcessed.getTxpdInvKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						processingResults.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);

			}
		}
		if (listBusinessProcess != null && !listBusinessProcess.isEmpty()) {
			List<Gstr1AdvanceAdjustmentFileUploadEntity> atDoc1 = convertSRFileToAtaDoc(
					listBusinessProcess);

			for (Gstr1AdvanceAdjustmentFileUploadEntity atProcessed : atDoc1) {
				String atInvKey = atProcessed.getTxpdInvKey();
				finalList.add(atProcessed);
				existProcessData.add(atInvKey);
			}
			List<Gstr1VerticalAtRespDto> listOfFinal = new ArrayList<>();
			for (Gstr1AdvanceAdjustmentFileUploadEntity atProcessed : atDoc1) {
				Gstr1VerticalAtRespDto dtoConvert = new Gstr1VerticalAtRespDto();
				if (atProcessed.getId() != null) {
					Optional<Gstr1AdvanceAdjustmentFileUploadEntity> findById = gstr1ATARepository
							.findById(atProcessed.getId());
					if (findById.isPresent()) {
						atProcessed.setAsEnterId(findById.get().getAsEnterId());
						atProcessed.setFileId(findById.get().getFileId());
						gstr1ATARepository.save(atProcessed);
					}
				} else {
					Gstr1AdvanceAdjustmentFileUploadEntity save = gstr1ATARepository
							.save(atProcessed);
					dtoConvert = saveDtoConvert(save);
					listOfFinal.add(dtoConvert);
				}
			}
			if (listOfFinal != null && !listOfFinal.isEmpty()) {
				for (Gstr1VerticalAtRespDto strDto : listOfFinal) {
					Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
							strDto);

					String processKey = strucProcessed.getTxpdInvKey()
							.concat(GSTConstants.SLASH)
							.concat(strDto.getSNo().toString());
					Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
							infoWithProcessed.get(processKey));
					dtoResponse.put(processKey, dtoRes);
				}

			}
			respList.addAll(dtoResponse.values());
			respBody = gson.toJsonTree(respList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
		} else {
			respList.addAll(dtoResponse.values());
			respBody = gson.toJsonTree(respList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
		}
		return resp;
	}

	private Gstr1VerticalAtRespDto saveDtoConvert(
			Gstr1AdvanceAdjustmentFileUploadEntity save) {
		Gstr1VerticalAtRespDto dto = new Gstr1VerticalAtRespDto();
		dto.setId(save.getId());
		dto.setSection(save.getUiSectionType());
		dto.setSNo(save.getSNo());
		dto.setGstin(save.getSgstin());
		dto.setTaxPeriod(save.getReturnPeriod());
		dto.setTransType(save.getTransactionType());
		if ("TXPDA".equalsIgnoreCase(save.getUiSectionType())) {
			dto.setMonth(save.getMonth());
			dto.setOrgPos(save.getOrgPOS());
			String orgRate = (save.getOrgRate() != null
					&& !save.getOrgRate().toString().trim().isEmpty())
							? String.valueOf(save.getOrgRate()) : null;
			dto.setOrgRate(orgRate);
			dto.setOrgAdvance(save.getOrgGrossAdvanceAdjusted().toString());
		}
		dto.setNewPos(save.getNewPOS());
		String newRate = (save.getNewRate() != null
				&& !save.getNewRate().toString().trim().isEmpty())
						? String.valueOf(save.getNewRate()) : null;
		dto.setNewRate(newRate);
		dto.setNewAdvance(save.getNewGrossAdvanceAdjusted().toString());

		String igstAmt = (save.getIntegratedTaxAmount() != null
				&& !save.getIntegratedTaxAmount().toString().trim().isEmpty())
						? String.valueOf(save.getIntegratedTaxAmount()) : null;

		dto.setIgst(igstAmt);

		String cgstAmt = (save.getCentralTaxAmount() != null
				&& !save.getCentralTaxAmount().toString().trim().isEmpty())
						? String.valueOf(save.getCentralTaxAmount()) : null;

		dto.setCgst(cgstAmt);

		String sgstAmt = (save.getStateUTTaxAmount() != null
				&& !save.getStateUTTaxAmount().toString().trim().isEmpty())
						? String.valueOf(save.getStateUTTaxAmount()) : null;
		dto.setSgst(sgstAmt);

		String cessAmt = (save.getCessAmount() != null
				&& !save.getCessAmount().toString().trim().isEmpty())
						? String.valueOf(save.getCessAmount()) : null;
		dto.setCess(cessAmt);

		dto.setProfitCntr(save.getProfitCentre());
		dto.setPlant(save.getPlant());
		dto.setDivision(save.getDivision());
		dto.setLocation(save.getLocation());
		dto.setSalesOrg(save.getSalesOrganisation());
		dto.setDistrChannel(save.getDistributionChannel());
		dto.setUsrAccess1(save.getUserAccess1());
		dto.setUsrAccess2(save.getUserAccess2());
		dto.setUsrAccess3(save.getUserAccess3());
		dto.setUsrAccess4(save.getUserAccess4());
		dto.setUsrAccess5(save.getUserAccess5());
		dto.setUsrAccess6(save.getUserAccess6());
		dto.setUsrDefined1(save.getUserDef1());
		dto.setUsrDefined2(save.getUserDef2());
		dto.setUsrDefined3(save.getUserDef3());
		return dto;
	}

	private List<Gstr1AdvanceAdjustmentFileUploadEntity> convertSRFileToAtaDoc(
			List<Gstr1AsEnteredTxpdFileUploadEntity> busiProcess) {
		List<Gstr1AdvanceAdjustmentFileUploadEntity> listOfAta = new ArrayList<>();
		Gstr1AdvanceAdjustmentFileUploadEntity ataObj = null;
		for (Gstr1AsEnteredTxpdFileUploadEntity obj : busiProcess) {
			ataObj = new Gstr1AdvanceAdjustmentFileUploadEntity();

			BigDecimal orgRates = BigDecimal.ZERO;
			String orgRate = obj.getOrgRate();
			if (orgRate != null && !orgRate.isEmpty()) {
				orgRates = NumberFomatUtil.getBigDecimal(orgRate);
				ataObj.setOrgRate(orgRates);
			}
			BigDecimal orgGross = BigDecimal.ZERO;
			String orgGrosss = obj.getOrgGrossAdvanceAdjusted();
			if (orgGrosss != null && !orgGrosss.isEmpty()) {
				orgGross = NumberFomatUtil.getBigDecimal(orgGrosss);
				ataObj.setOrgGrossAdvanceAdjusted(orgGross);
			}
			BigDecimal newRate = BigDecimal.ZERO;
			String rate = obj.getNewRate();
			if (rate != null && !rate.isEmpty()) {
				newRate = NumberFomatUtil.getBigDecimal(rate);
				ataObj.setNewRate(newRate);
			}
			BigDecimal newGross = BigDecimal.ZERO;
			String newGros = obj.getNewGrossAdvanceAdjusted();
			if (newGros != null && !newGros.isEmpty()) {
				newGross = NumberFomatUtil.getBigDecimal(newGros);
				ataObj.setNewGrossAdvanceAdjusted(newGross);
			}
			BigDecimal igst = BigDecimal.ZERO;
			String igsts = obj.getIntegratedTaxAmount();
			if (igsts != null && !igsts.isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(igsts);
				ataObj.setIntegratedTaxAmount(igst);
			}
			BigDecimal cgst = BigDecimal.ZERO;
			String cgsts = obj.getCentralTaxAmount();
			if (cgsts != null && !cgsts.isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(cgsts);
				ataObj.setCentralTaxAmount(cgst);
			}
			BigDecimal sgst = BigDecimal.ZERO;
			String sgsts = obj.getStateUTTaxAmount();
			if (sgsts != null && !sgsts.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgsts);
				ataObj.setStateUTTaxAmount(sgst);
			}
			BigDecimal cess = BigDecimal.ZERO;
			String cessAmt = obj.getCessAmount();
			if (cessAmt != null && !cessAmt.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cessAmt);
				ataObj.setCessAmount(cess);
			}

			ataObj.setSgstin(obj.getSgstin());
			Integer deriRetPeriod = 0;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}
			ataObj.setReturnPeriod(obj.getReturnPeriod());
			ataObj.setTransactionType(obj.getTransactionType());
			if (obj.getId() != null)
				ataObj.setId(obj.getId());
			ataObj.setSNo(obj.getSNo());
			ataObj.setMonth(obj.getMonth());
			ataObj.setOrgPOS(obj.getOrgPOS());
			ataObj.setNewPOS(obj.getNewPOS());
			ataObj.setDerivedRetPeriod(deriRetPeriod);
			ataObj.setUiSectionType(obj.getUiSectionType());
			ataObj.setProfitCentre(obj.getProfitCentre());
			ataObj.setPlant(obj.getPlant());
			ataObj.setLocation(obj.getLocation());
			ataObj.setDivision(obj.getDivision());
			ataObj.setSalesOrganisation(obj.getSalesOrganisation());
			ataObj.setDistributionChannel(obj.getDistributionChannel());
			ataObj.setInfo(obj.isInfo());
			ataObj.setUserAccess1(obj.getUserAccess1());
			ataObj.setUserAccess2(obj.getUserAccess2());
			ataObj.setUserAccess3(obj.getUserAccess3());
			ataObj.setUserAccess4(obj.getUserAccess4());
			ataObj.setUserAccess5(obj.getUserAccess5());
			ataObj.setUserAccess6(obj.getUserAccess6());
			ataObj.setUserDef1(obj.getUserDef1());
			ataObj.setUserDef2(obj.getUserDef2());
			ataObj.setUserDef3(obj.getUserDef3());
			ataObj.setCreatedBy(obj.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			ataObj.setCreatedOn(convertNow);
			ataObj.setSectionType(obj.isSectionType());
			ataObj.setGstnTxpdKey(obj.getGstnTxpdKey());
			ataObj.setTxpdInvKey(obj.getTxpdInvKey());
			listOfAta.add(ataObj);
		}

		return listOfAta;
	}

	private Gstr1VerticalAtRespDto convertReponseDto(Gstr1VerticalAtRespDto dto,
			List<ProcessingResult> results) {
		String section = dto.getSection();
		dto.setSection(dto.getSection());
		dto.setSNo(dto.getSNo());
		dto.setId(dto.getId() != null && !dto.getId().toString().isEmpty()
				? dto.getId() : null);
		dto.setGstin(dto.getGstin());
		dto.setTaxPeriod(dto.getTaxPeriod());
		dto.setTransType(dto.getTransType());
		if ("TXPDA".equalsIgnoreCase(section)) {
			dto.setMonth(dto.getMonth());
			dto.setOrgPos(dto.getOrgPos());
			String orgRate = (dto.getOrgRate() != null
					&& !dto.getOrgRate().toString().trim().isEmpty())
							? String.valueOf(dto.getOrgRate()) : null;
			dto.setOrgRate(orgRate);
			dto.setOrgAdvance(dto.getOrgAdvance());
		}
		dto.setNewPos(dto.getNewPos());
		String newRate = (dto.getNewRate() != null
				&& !dto.getNewRate().toString().trim().isEmpty())
						? String.valueOf(dto.getNewRate()) : null;
		dto.setNewRate(newRate);
		dto.setNewAdvance(dto.getNewAdvance());

		String igstAmt = (dto.getIgst() != null
				&& !dto.getIgst().toString().trim().isEmpty())
						? String.valueOf(dto.getIgst()) : null;

		dto.setIgst(igstAmt);

		String cgstAmt = (dto.getCgst() != null
				&& !dto.getCgst().toString().trim().isEmpty())
						? String.valueOf(dto.getCgst()) : null;

		dto.setCgst(cgstAmt);

		String sgstAmt = (dto.getSgst() != null
				&& !dto.getSgst().toString().trim().isEmpty())
						? String.valueOf(dto.getSgst()) : null;
		dto.setSgst(sgstAmt);

		String cessAmt = (dto.getCess() != null
				&& !dto.getCess().toString().trim().isEmpty())
						? String.valueOf(dto.getCess()) : null;
		dto.setCess(cessAmt);

		dto.setProfitCntr(dto.getProfitCntr());
		dto.setPlant(dto.getPlant());
		dto.setDivision(dto.getDivision());
		dto.setLocation(dto.getLocation());
		dto.setSalesOrg(dto.getSalesOrg());
		dto.setDistrChannel(dto.getDistrChannel());
		dto.setUsrAccess1(dto.getUsrAccess1());
		dto.setUsrAccess2(dto.getUsrAccess2());
		dto.setUsrAccess3(dto.getUsrAccess3());
		dto.setUsrAccess4(dto.getUsrAccess4());
		dto.setUsrAccess5(dto.getUsrAccess5());
		dto.setUsrAccess6(dto.getUsrAccess6());
		dto.setUsrDefined1(dto.getUsrDefined1());
		dto.setUsrDefined2(dto.getUsrDefined2());
		dto.setUsrDefined3(dto.getUsrDefined3());

		if (results != null && results.size() > 0) {
			List<ProcessingResultType> listTypes = new ArrayList<>();
			List<ErrorDescriptionDto> errorList = new ArrayList<>();
			for (ProcessingResult types : results) {
				ProcessingResultType type = types.getType();
				ErrorDescriptionDto errorDto = new ErrorDescriptionDto();
				errorDto.setErrorCode(types.getCode());
				errorDto.setErrorDesc(types.getDescription());
				errorDto.setErrorType(types.getType().toString());
				TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) types
						.getLocation();
				if (null != loc) { // In case of bifurcation failure, loc is
					// null
					Object[] arr = loc.getFieldIdentifiers();
					String[] fields = Arrays.copyOf(arr, arr.length,
							String[].class);
					String errField = StringUtils.join(fields, ',');
					errorDto.setErrorField(errField);

				}
				errorList.add(errorDto);

				listTypes.add(type);
			}
			if (errorList.size() > 0) {
				dto.setErrorList(errorList);
			}
		}
		return dto;
	}

	private Gstr1AsEnteredTxpdFileUploadEntity convertGstr1VerticalAtRespDto(
			Gstr1VerticalAtRespDto dto) {

		String section = dto.getSection();
		Gstr1AsEnteredTxpdFileUploadEntity entity = new Gstr1AsEnteredTxpdFileUploadEntity();
		entity.setSgstin(dto.getGstin());
		entity.setUiSectionType(dto.getSection());
		entity.setSNo(dto.getSNo());
		entity.setReturnPeriod(dto.getTaxPeriod());
		if (dto.getTransType() == null || dto.getTransType().isEmpty()) {
			entity.setTransactionType(GSTConstants.N);
		} else {
			entity.setTransactionType(dto.getTransType());
		}
		if ("TXPDA".equalsIgnoreCase(section)) {
			entity.setMonth(dto.getMonth());
			entity.setOrgPOS(dto.getOrgPos());
			String orgRate = (dto.getOrgRate() != null
					&& !dto.getOrgRate().toString().trim().isEmpty())
							? String.valueOf(dto.getOrgRate()) : null;
			entity.setOrgRate(orgRate);
			entity.setOrgGrossAdvanceAdjusted(dto.getOrgAdvance());
			entity.setSectionType(true);
		}
		entity.setNewPOS(dto.getNewPos());
		String newRate = (dto.getNewRate() != null
				&& !dto.getNewRate().toString().trim().isEmpty())
						? String.valueOf(dto.getNewRate()) : null;
		entity.setNewRate(newRate);
		entity.setNewGrossAdvanceAdjusted(dto.getNewAdvance());

		String igstAmt = (dto.getIgst() != null
				&& !dto.getIgst().toString().trim().isEmpty())
						? String.valueOf(dto.getIgst()) : null;

		entity.setIntegratedTaxAmount(igstAmt);

		String cgstAmt = (dto.getCgst() != null
				&& !dto.getCgst().toString().trim().isEmpty())
						? String.valueOf(dto.getCgst()) : null;

		entity.setCentralTaxAmount(cgstAmt);

		String sgstAmt = (dto.getSgst() != null
				&& !dto.getSgst().toString().trim().isEmpty())
						? String.valueOf(dto.getSgst()) : null;
		entity.setStateUTTaxAmount(sgstAmt);

		String cessAmt = (dto.getCess() != null
				&& !dto.getCess().toString().trim().isEmpty())
						? String.valueOf(dto.getCess()) : null;
		entity.setCessAmount(cessAmt);
		entity.setProfitCentre(dto.getProfitCntr());
		entity.setPlant(dto.getPlant());
		entity.setDivision(dto.getDivision());
		entity.setLocation(dto.getLocation());
		entity.setSalesOrganisation(dto.getSalesOrg());
		entity.setDistributionChannel(dto.getDistrChannel());
		entity.setUserAccess1(dto.getUsrAccess1());
		entity.setUserAccess2(dto.getUsrAccess2());
		entity.setUserAccess3(dto.getUsrAccess3());
		entity.setUserAccess4(dto.getUsrAccess4());
		entity.setUserAccess5(dto.getUsrAccess5());
		entity.setUserAccess6(dto.getUsrAccess6());
		entity.setUserDef1(dto.getUsrDefined1());
		entity.setUserDef2(dto.getUsrDefined2());
		entity.setUserDef3(dto.getUsrDefined3());
		entity.setId(dto.getId());
		String atInvKey = createInvKeyForAt(entity);
		String atGstnKey = createGstnKeyForAt(entity);
		entity.setTxpdInvKey(atInvKey);
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		entity.setCreatedOn(convertNow);
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		entity.setCreatedBy(userName);
		entity.setGstnTxpdKey(atGstnKey);
		Integer derivedRetPeriod = GenUtil
				.convertTaxPeriodToInt(dto.getTaxPeriod());
		if (derivedRetPeriod != null) {
			String derPeriod = String.valueOf(derivedRetPeriod);
			entity.setDerivedRetPeriod(derPeriod);
		} else {
			String derPeriod = String.valueOf(999999);
			entity.setDerivedRetPeriod(derPeriod);
		}
		return entity;

	}

	private String createGstnKeyForAt(Gstr1AsEnteredTxpdFileUploadEntity obj) {
		String supplierGSTIN = (obj.getSgstin() != null
				&& !obj.getSgstin().isEmpty())
						? String.valueOf(obj.getSgstin()).trim() : "";
		String returnPeriod = (obj.getReturnPeriod() != null
				&& !obj.getReturnPeriod().isEmpty())
						? String.valueOf(obj.getReturnPeriod()).trim() : "";
		String transactionType = (obj.getTransactionType() != null
				&& !obj.getTransactionType().isEmpty())
						? String.valueOf(obj.getTransactionType()).trim()
						: GSTConstants.N;
		String month = (obj.getMonth() != null && !obj.getMonth().isEmpty())
				? String.valueOf(obj.getMonth()).trim() : "";
		String orgPos = (obj.getOrgPOS() != null && !obj.getOrgPOS().isEmpty())
				? String.valueOf(obj.getOrgPOS()).trim() : "";
		String orgRate = (obj.getOrgRate() != null
				&& !obj.getOrgRate().isEmpty())
						? String.valueOf(obj.getOrgRate()).trim() : "";

		String newPos = (obj.getNewPOS() != null && !obj.getNewPOS().isEmpty())
				? String.valueOf(obj.getNewPOS()).trim() : "";

		String newRate = (obj.getNewRate() != null
				&& !obj.getNewRate().isEmpty())
						? String.valueOf(obj.getNewRate()).trim() : "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(supplierGSTIN)
				.add(returnPeriod).add(transactionType).add(month).add(orgPos)
				.add(orgRate).add(newPos).add(newRate).toString();
	}

	private List<Object[]> convertsJavaObjects(
			List<Gstr1VerticalAtRespDto> lists) {
		List<Object[]> docObjList = new ArrayList<>();
		if (lists != null && !lists.isEmpty()) {
			lists.forEach(list -> {
				Object[] objArr = new Object[26];

				objArr[0] = list.getGstin();
				objArr[1] = list.getTaxPeriod();
				objArr[2] = list.getTransType();
				if (objArr[2] == null || objArr[2].toString().isEmpty()) {
					objArr[2] = GSTConstants.N;
				}
				objArr[3] = list.getMonth();
				objArr[4] = list.getOrgPos();
				objArr[5] = list.getOrgRate();
				objArr[6] = list.getOrgAdvance();
				objArr[7] = list.getNewPos();
				objArr[8] = list.getNewRate();
				objArr[9] = list.getNewAdvance();
				objArr[10] = list.getIgst();
				objArr[11] = list.getCgst();
				objArr[12] = list.getSgst();
				objArr[13] = list.getCess();
				objArr[14] = list.getProfitCntr();
				objArr[15] = list.getPlant();
				objArr[16] = list.getDivision();
				objArr[17] = list.getLocation();
				objArr[18] = list.getSalesOrg();
				objArr[19] = list.getDistrChannel();
				objArr[20] = list.getUsrAccess1();
				objArr[21] = list.getUsrAccess2();
				objArr[22] = list.getUsrAccess3();
				objArr[23] = list.getUsrAccess4();
				objArr[24] = list.getUsrAccess5();
				objArr[25] = list.getUsrAccess6();
				docObjList.add(objArr);
			});

		}
		return docObjList;
	}

	public String createInvKeyForAt(Gstr1AsEnteredTxpdFileUploadEntity obj) {
		String supplierGSTIN = (obj.getSgstin() != null
				&& !obj.getSgstin().isEmpty())
						? String.valueOf(obj.getSgstin()).trim() : "";
		String returnPeriod = (obj.getReturnPeriod() != null
				&& !obj.getReturnPeriod().isEmpty())
						? String.valueOf(obj.getReturnPeriod()).trim() : "";
		String transactionType = (obj.getTransactionType() != null
				&& !obj.getTransactionType().isEmpty())
						? String.valueOf(obj.getTransactionType()).trim()
						: GSTConstants.N;
		String month = (obj.getMonth() != null && !obj.getMonth().isEmpty())
				? String.valueOf(obj.getMonth()).trim() : "";
		String orgPos = (obj.getOrgPOS() != null && !obj.getOrgPOS().isEmpty())
				? String.valueOf(obj.getOrgPOS()).trim() : "";
		String orgRate = (obj.getOrgRate() != null
				&& !obj.getOrgRate().isEmpty())
						? String.valueOf(obj.getOrgRate()).trim() : "";

		String newPos = (obj.getNewPOS() != null && !obj.getNewPOS().isEmpty())
				? String.valueOf(obj.getNewPOS()).trim() : "";

		String newRate = (obj.getNewRate() != null
				&& !obj.getNewRate().isEmpty())
						? String.valueOf(obj.getNewRate()).trim() : "";
		String profitCentre = (obj.getProfitCentre() != null
				&& !obj.getProfitCentre().isEmpty())
						? String.valueOf(obj.getProfitCentre()).trim()
						: GSTConstants.NA;
		String plant = (obj.getPlant() != null && !obj.getPlant().isEmpty())
				? String.valueOf(obj.getPlant()).trim() : GSTConstants.NA;
		String division = (obj.getDivision() != null
				&& !obj.getDivision().isEmpty())
						? String.valueOf(obj.getDivision()).trim()
						: GSTConstants.NA;
		String location = (obj.getLocation() != null
				&& !obj.getLocation().isEmpty())
						? String.valueOf(obj.getLocation()).trim()
						: GSTConstants.NA;
		String salesOrganization = (obj.getSalesOrganisation() != null
				&& !obj.getSalesOrganisation().isEmpty())
						? String.valueOf(obj.getSalesOrganisation()).trim()
						: GSTConstants.NA;
		String disChannel = (obj.getDistributionChannel() != null
				&& !obj.getDistributionChannel().isEmpty())
						? String.valueOf(obj.getDistributionChannel()).trim()
						: GSTConstants.NA;
		String userAccess1 = (obj.getUserAccess1() != null
				&& !obj.getUserAccess1().isEmpty())
						? String.valueOf(obj.getUserAccess1()).trim()
						: GSTConstants.NA;
		String userAccess2 = (obj.getUserAccess2() != null
				&& !obj.getUserAccess2().isEmpty())
						? String.valueOf(obj.getUserAccess2()).trim()
						: GSTConstants.NA;
		String userAccess3 = (obj.getUserAccess3() != null
				&& !obj.getUserAccess3().isEmpty())
						? String.valueOf(obj.getUserAccess3()).trim()
						: GSTConstants.NA;
		String userAccess4 = (obj.getUserAccess4() != null
				&& !obj.getUserAccess4().isEmpty())
						? String.valueOf(obj.getUserAccess4()).trim()
						: GSTConstants.NA;
		String userAccess5 = (obj.getUserAccess5() != null
				&& !obj.getUserAccess5().isEmpty())
						? String.valueOf(obj.getUserAccess5()).trim()
						: GSTConstants.NA;
		String userAccess6 = (obj.getUserAccess6() != null
				&& !obj.getUserAccess6().isEmpty())
						? String.valueOf(obj.getUserAccess6()).trim()
						: GSTConstants.NA;
		return new StringJoiner(WEB_UPLOAD_KEY).add(supplierGSTIN)
				.add(returnPeriod).add(transactionType).add(month).add(orgPos)
				.add(orgRate).add(newPos).add(newRate).add(profitCentre)
				.add(plant).add(division).add(location).add(salesOrganization)
				.add(disChannel).add(userAccess1).add(userAccess2)
				.add(userAccess3).add(userAccess4).add(userAccess5)
				.add(userAccess6).toString();
	}
	
	//gstr1a code
	@Transactional(value = "clientTransactionManager")
	public JsonObject updateGstr1aVerticalData(List<Gstr1VerticalAtRespDto> list) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonElement respBody = null;
		JsonObject resp = new JsonObject();
		List<Gstr1VerticalAtRespDto> respList = new ArrayList<>();
		List<Object[]> listObAt = convertsJavaObjects(list);
		Map<String, List<ProcessingResult>> processingResults = aAStructValidationChain
				.validationApi(listObAt, list);

		Map<String, Gstr1VerticalAtRespDto> dtoResponse = new HashMap<>();

		List<String> strErrorKeys = new ArrayList<>();

		for (String keys : processingResults.keySet()) {
			String errkey = keys.substring(0, keys.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		List<String> processedKeys = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listStrProcess = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listStrErrors = new ArrayList<>();

		for (Gstr1VerticalAtRespDto strDto : list) {
			Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			String invAtKey = strucProcessed.getTxpdInvKey();
			if (!strErrorKeys.contains(invAtKey)) {
				processedKeys.add(invAtKey);
				listStrProcess.add(strDto);
			} else {
				listStrErrors.add(strDto);
			}
		}
		if (strErrorKeys.size() > 0 && !strErrorKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listStrErrors) {
				Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getTxpdInvKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						processingResults.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();
		List<ProcessingResult> current = null;
		List<String> errorKeys = new ArrayList<>();
		List<String> errorInfo = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listInfoProcess = new ArrayList<>();

		if (listStrProcess.size() > 0 && !listStrProcess.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listStrProcess) {
				Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String invAtKey = strucProcessed.getTxpdInvKey();
				String keys = invAtKey.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				List<ProcessingResult> results = advancedAdjustmentBusinessChain
						.validate(strucProcessed, null);
				if (results != null && !results.isEmpty()) {
					List<ProcessingResultType> listTypes = new ArrayList<>();
					for (ProcessingResult types : results) {
						ProcessingResultType type = types.getType();
						listTypes.add(type);
					}
					List<String> errorType = listTypes.stream()
							.map(object -> Objects.toString(object, null))
							.collect(Collectors.toList());

					current = businessValErrors.get(keys);
					if (current == null) {
						current = new ArrayList<>();
						if (errorType.size() > 0) {
							if (errorType.contains(GSTConstants.ERROR)
									&& errorType.contains(GSTConstants.INFO)) {
								errorInfo.add(invAtKey);
								errorKeys.add(invAtKey);
								businessValErrors.put(keys, results);
							} else if (errorType.contains(GSTConstants.ERROR)) {
								errorKeys.add(invAtKey);
								businessValErrors.put(keys, results);
							} else {
								infoWithProcessed.put(keys, results);
								infoKeys.add(invAtKey);
								listInfoProcess.add(strDto);
							}
						}
					} else {
						if (errorType.size() > 0) {
							if (errorType.contains(GSTConstants.ERROR)
									&& errorType.contains(GSTConstants.INFO)) {
								errorInfo.add(invAtKey);
								errorKeys.add(invAtKey);
								businessValErrors
										.computeIfAbsent(keys,
												k -> new ArrayList<ProcessingResult>())
										.addAll(results);
							} else if (errorType.contains(GSTConstants.ERROR)) {
								errorKeys.add(invAtKey);
								businessValErrors
										.computeIfAbsent(keys,
												k -> new ArrayList<ProcessingResult>())
										.addAll(results);
							} else {
								infoWithProcessed
										.computeIfAbsent(keys,
												k -> new ArrayList<ProcessingResult>())
										.addAll(results);
								infoKeys.add(invAtKey);
								listInfoProcess.add(strDto);
							}
						}
					}
				}
			}
		}

		List<String> businessKeys = new ArrayList<String>();
		List<Gstr1VerticalAtRespDto> listBusiProcess = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listBusiErrors = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listBusinessResponse = new ArrayList<>();

		if (businessValErrors.size() > 0 && !businessValErrors.isEmpty()) {
			for (String businessRules : businessValErrors.keySet()) {
				String errkey = businessRules.substring(0,
						businessRules.lastIndexOf('-'));
				businessKeys.add(errkey);
			}
		}
		for (Gstr1VerticalAtRespDto strDto : listStrProcess) {
			Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			String invAtKey = strucProcessed.getTxpdInvKey();
			if (!businessKeys.contains(invAtKey)) {
				processedKeys.add(invAtKey);
				listBusiProcess.add(strDto);
			} else {
				listBusiErrors.add(strDto);
			}
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listInfoProcess) {
				Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getTxpdInvKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						infoWithProcessed.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		if (businessKeys.size() > 0 && !businessKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listBusiErrors) {
				Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getTxpdInvKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						businessValErrors.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		List<Gstr1AsEnteredTxpdFileUploadEntity> listBusinessProcess = new ArrayList<>();
		for (Gstr1VerticalAtRespDto strDto : listBusiProcess) {
			Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			listBusinessResponse.add(strDto);
			String invAtKey = strucProcessed.getTxpdInvKey();
			if (!businessKeys.contains(invAtKey)) {
				listBusinessProcess.add(strucProcessed);
			}
		}
		List<Gstr1AAdvanceAdjustmentFileUploadEntity> finalList = new ArrayList<>();
		List<String> existProcessData = new ArrayList<>();
		if (listBusinessResponse != null && !listBusinessResponse.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listBusinessResponse) {
				Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);

				String errorKey = strucProcessed.getTxpdInvKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						processingResults.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);

			}
		}
		if (listBusinessProcess != null && !listBusinessProcess.isEmpty()) {
			List<Gstr1AAdvanceAdjustmentFileUploadEntity> atDoc1 = convertGstr1aSRFileToAtaDoc(
					listBusinessProcess);

			for (Gstr1AAdvanceAdjustmentFileUploadEntity atProcessed : atDoc1) {
				String atInvKey = atProcessed.getTxpdInvKey();
				finalList.add(atProcessed);
				existProcessData.add(atInvKey);
			}
			List<Gstr1VerticalAtRespDto> listOfFinal = new ArrayList<>();
			for (Gstr1AAdvanceAdjustmentFileUploadEntity atProcessed : atDoc1) {
				Gstr1VerticalAtRespDto dtoConvert = new Gstr1VerticalAtRespDto();
				if (atProcessed.getId() != null) {
					Optional<Gstr1AAdvanceAdjustmentFileUploadEntity> findById = gstr1aATARepository
							.findById(atProcessed.getId());
					if (findById.isPresent()) {
						atProcessed.setAsEnterId(findById.get().getAsEnterId());
						atProcessed.setFileId(findById.get().getFileId());
						gstr1aATARepository.save(atProcessed);
					}
				} else {
					Gstr1AAdvanceAdjustmentFileUploadEntity save = gstr1aATARepository
							.save(atProcessed);
					dtoConvert = saveGstr1aDtoConvert(save);
					listOfFinal.add(dtoConvert);
				}
			}
			if (listOfFinal != null && !listOfFinal.isEmpty()) {
				for (Gstr1VerticalAtRespDto strDto : listOfFinal) {
					Gstr1AsEnteredTxpdFileUploadEntity strucProcessed = convertGstr1VerticalAtRespDto(
							strDto);

					String processKey = strucProcessed.getTxpdInvKey()
							.concat(GSTConstants.SLASH)
							.concat(strDto.getSNo().toString());
					Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
							infoWithProcessed.get(processKey));
					dtoResponse.put(processKey, dtoRes);
				}

			}
			respList.addAll(dtoResponse.values());
			respBody = gson.toJsonTree(respList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
		} else {
			respList.addAll(dtoResponse.values());
			respBody = gson.toJsonTree(respList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
		}
		return resp;
	}
	//gstr1a code
	private Gstr1VerticalAtRespDto saveGstr1aDtoConvert(
			Gstr1AAdvanceAdjustmentFileUploadEntity save) {
		Gstr1VerticalAtRespDto dto = new Gstr1VerticalAtRespDto();
		dto.setId(save.getId());
		dto.setSection(save.getUiSectionType());
		dto.setSNo(save.getSNo());
		dto.setGstin(save.getSgstin());
		dto.setTaxPeriod(save.getReturnPeriod());
		dto.setTransType(save.getTransactionType());
		if ("TXPDA".equalsIgnoreCase(save.getUiSectionType())) {
			dto.setMonth(save.getMonth());
			dto.setOrgPos(save.getOrgPOS());
			String orgRate = (save.getOrgRate() != null
					&& !save.getOrgRate().toString().trim().isEmpty())
							? String.valueOf(save.getOrgRate()) : null;
			dto.setOrgRate(orgRate);
			dto.setOrgAdvance(save.getOrgGrossAdvanceAdjusted().toString());
		}
		dto.setNewPos(save.getNewPOS());
		String newRate = (save.getNewRate() != null
				&& !save.getNewRate().toString().trim().isEmpty())
						? String.valueOf(save.getNewRate()) : null;
		dto.setNewRate(newRate);
		dto.setNewAdvance(save.getNewGrossAdvanceAdjusted().toString());

		String igstAmt = (save.getIntegratedTaxAmount() != null
				&& !save.getIntegratedTaxAmount().toString().trim().isEmpty())
						? String.valueOf(save.getIntegratedTaxAmount()) : null;

		dto.setIgst(igstAmt);

		String cgstAmt = (save.getCentralTaxAmount() != null
				&& !save.getCentralTaxAmount().toString().trim().isEmpty())
						? String.valueOf(save.getCentralTaxAmount()) : null;

		dto.setCgst(cgstAmt);

		String sgstAmt = (save.getStateUTTaxAmount() != null
				&& !save.getStateUTTaxAmount().toString().trim().isEmpty())
						? String.valueOf(save.getStateUTTaxAmount()) : null;
		dto.setSgst(sgstAmt);

		String cessAmt = (save.getCessAmount() != null
				&& !save.getCessAmount().toString().trim().isEmpty())
						? String.valueOf(save.getCessAmount()) : null;
		dto.setCess(cessAmt);

		dto.setProfitCntr(save.getProfitCentre());
		dto.setPlant(save.getPlant());
		dto.setDivision(save.getDivision());
		dto.setLocation(save.getLocation());
		dto.setSalesOrg(save.getSalesOrganisation());
		dto.setDistrChannel(save.getDistributionChannel());
		dto.setUsrAccess1(save.getUserAccess1());
		dto.setUsrAccess2(save.getUserAccess2());
		dto.setUsrAccess3(save.getUserAccess3());
		dto.setUsrAccess4(save.getUserAccess4());
		dto.setUsrAccess5(save.getUserAccess5());
		dto.setUsrAccess6(save.getUserAccess6());
		dto.setUsrDefined1(save.getUserDef1());
		dto.setUsrDefined2(save.getUserDef2());
		dto.setUsrDefined3(save.getUserDef3());
		return dto;
	}
	//gstr1a code
	private List<Gstr1AAdvanceAdjustmentFileUploadEntity> convertGstr1aSRFileToAtaDoc(
			List<Gstr1AsEnteredTxpdFileUploadEntity> busiProcess) {
		List<Gstr1AAdvanceAdjustmentFileUploadEntity> listOfAta = new ArrayList<>();
		Gstr1AAdvanceAdjustmentFileUploadEntity ataObj = null;
		for (Gstr1AsEnteredTxpdFileUploadEntity obj : busiProcess) {
			ataObj = new Gstr1AAdvanceAdjustmentFileUploadEntity();

			BigDecimal orgRates = BigDecimal.ZERO;
			String orgRate = obj.getOrgRate();
			if (orgRate != null && !orgRate.isEmpty()) {
				orgRates = NumberFomatUtil.getBigDecimal(orgRate);
				ataObj.setOrgRate(orgRates);
			}
			BigDecimal orgGross = BigDecimal.ZERO;
			String orgGrosss = obj.getOrgGrossAdvanceAdjusted();
			if (orgGrosss != null && !orgGrosss.isEmpty()) {
				orgGross = NumberFomatUtil.getBigDecimal(orgGrosss);
				ataObj.setOrgGrossAdvanceAdjusted(orgGross);
			}
			BigDecimal newRate = BigDecimal.ZERO;
			String rate = obj.getNewRate();
			if (rate != null && !rate.isEmpty()) {
				newRate = NumberFomatUtil.getBigDecimal(rate);
				ataObj.setNewRate(newRate);
			}
			BigDecimal newGross = BigDecimal.ZERO;
			String newGros = obj.getNewGrossAdvanceAdjusted();
			if (newGros != null && !newGros.isEmpty()) {
				newGross = NumberFomatUtil.getBigDecimal(newGros);
				ataObj.setNewGrossAdvanceAdjusted(newGross);
			}
			BigDecimal igst = BigDecimal.ZERO;
			String igsts = obj.getIntegratedTaxAmount();
			if (igsts != null && !igsts.isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(igsts);
				ataObj.setIntegratedTaxAmount(igst);
			}
			BigDecimal cgst = BigDecimal.ZERO;
			String cgsts = obj.getCentralTaxAmount();
			if (cgsts != null && !cgsts.isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(cgsts);
				ataObj.setCentralTaxAmount(cgst);
			}
			BigDecimal sgst = BigDecimal.ZERO;
			String sgsts = obj.getStateUTTaxAmount();
			if (sgsts != null && !sgsts.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgsts);
				ataObj.setStateUTTaxAmount(sgst);
			}
			BigDecimal cess = BigDecimal.ZERO;
			String cessAmt = obj.getCessAmount();
			if (cessAmt != null && !cessAmt.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cessAmt);
				ataObj.setCessAmount(cess);
			}

			ataObj.setSgstin(obj.getSgstin());
			Integer deriRetPeriod = 0;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}
			ataObj.setReturnPeriod(obj.getReturnPeriod());
			ataObj.setTransactionType(obj.getTransactionType());
			if (obj.getId() != null)
				ataObj.setId(obj.getId());
			ataObj.setSNo(obj.getSNo());
			ataObj.setMonth(obj.getMonth());
			ataObj.setOrgPOS(obj.getOrgPOS());
			ataObj.setNewPOS(obj.getNewPOS());
			ataObj.setDerivedRetPeriod(deriRetPeriod);
			ataObj.setUiSectionType(obj.getUiSectionType());
			ataObj.setProfitCentre(obj.getProfitCentre());
			ataObj.setPlant(obj.getPlant());
			ataObj.setLocation(obj.getLocation());
			ataObj.setDivision(obj.getDivision());
			ataObj.setSalesOrganisation(obj.getSalesOrganisation());
			ataObj.setDistributionChannel(obj.getDistributionChannel());
			ataObj.setInfo(obj.isInfo());
			ataObj.setUserAccess1(obj.getUserAccess1());
			ataObj.setUserAccess2(obj.getUserAccess2());
			ataObj.setUserAccess3(obj.getUserAccess3());
			ataObj.setUserAccess4(obj.getUserAccess4());
			ataObj.setUserAccess5(obj.getUserAccess5());
			ataObj.setUserAccess6(obj.getUserAccess6());
			ataObj.setUserDef1(obj.getUserDef1());
			ataObj.setUserDef2(obj.getUserDef2());
			ataObj.setUserDef3(obj.getUserDef3());
			ataObj.setCreatedBy(obj.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			ataObj.setCreatedOn(convertNow);
			ataObj.setSectionType(obj.isSectionType());
			ataObj.setGstnTxpdKey(obj.getGstnTxpdKey());
			ataObj.setTxpdInvKey(obj.getTxpdInvKey());
			listOfAta.add(ataObj);
		}

		return listOfAta;
	}
}
