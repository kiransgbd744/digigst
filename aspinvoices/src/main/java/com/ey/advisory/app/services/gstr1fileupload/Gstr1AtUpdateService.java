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
import com.ey.advisory.app.data.entities.client.Gstr1ARDetailsEntity;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredAREntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AARDetailsEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AARRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ARRepository;
import com.ey.advisory.app.docs.dto.Gstr1VerticalAtRespDto;
import com.ey.advisory.app.services.docs.SRFileToATDetailsConvertion;
import com.ey.advisory.app.services.docs.SRFileToATExcelDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.advanceReceived.ARStructValidationChain;
import com.ey.advisory.app.services.validation.advanceReceived.AdvancedReceivedBusinessChain;
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

@Service("Gstr1AtUpdateService")
public class Gstr1AtUpdateService {

	private final static String WEB_UPLOAD_KEY = "|";

	@Autowired
	@Qualifier("ARStructValidationChain")
	private ARStructValidationChain arStructValidationChain;

	@Autowired
	@Qualifier("SRFileToATExcelDetailsConvertion")
	private SRFileToATExcelDetailsConvertion sRFileToATExcelDetailsConvertion;

	@Autowired
	@Qualifier("AdvancedReceivedBusinessChain")
	private AdvancedReceivedBusinessChain advancedReceivedBusinessChain;

	@Autowired
	@Qualifier("Gstr1ARRepository")
	private Gstr1ARRepository gstr1ARRepository;

	@Autowired
	@Qualifier("SRFileToATDetailsConvertion")
	private SRFileToATDetailsConvertion sRFileToATDetailsConvertion;
	
	@Autowired
	@Qualifier("Gstr1AARRepository")
	private Gstr1AARRepository gstr1aARRepository;

	@Transactional(value = "clientTransactionManager")
	public JsonObject updateVerticalData(List<Gstr1VerticalAtRespDto> list) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonElement respBody = null;
		JsonObject resp = new JsonObject();
		List<Gstr1VerticalAtRespDto> respList = new ArrayList<>();

		List<Object[]> listObAt = convertsJavaObjects(list);
		Map<String, List<ProcessingResult>> processingResults = arStructValidationChain
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
			Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			String invAtKey = strucProcessed.getInvAtKey();
			if (!strErrorKeys.contains(invAtKey)) {
				processedKeys.add(invAtKey);
				listStrProcess.add(strDto);
			} else {
				listStrErrors.add(strDto);
			}
		}

		if (strErrorKeys.size() > 0 && !strErrorKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listStrErrors) {
				Gstr1AsEnteredAREntity strucErrors = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucErrors.getInvAtKey()
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

		List<Gstr1VerticalAtRespDto> listInfoRecords = new ArrayList<>();

		if (listStrProcess.size() > 0 && !listStrProcess.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listStrProcess) {
				Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String invAtKey = strucProcessed.getInvAtKey();
				String keys = invAtKey.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				List<ProcessingResult> results = advancedReceivedBusinessChain
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
								listInfoRecords.add(strDto);
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
								listInfoRecords.add(strDto);
							}
						}
					}
				}
			}
		}
		List<String> businessKeys = new ArrayList<String>();

		List<Gstr1AsEnteredAREntity> listBusinessProcess = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listBusinessResponse = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listBusiProcess = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listBusiErrors = new ArrayList<>();

		if (businessValErrors.size() > 0 && !businessValErrors.isEmpty()) {
			for (String businessRules : businessValErrors.keySet()) {
				String errkey = businessRules.substring(0,
						businessRules.lastIndexOf('-'));
				businessKeys.add(errkey);
			}
		}

		for (Gstr1VerticalAtRespDto strDto : listStrProcess) {
			Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			String invAtKey = strucProcessed.getInvAtKey();
			if (!businessKeys.contains(invAtKey)) {
				processedKeys.add(invAtKey);
				listBusiProcess.add(strDto);
			} else {
				listBusiErrors.add(strDto);
			}
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listInfoRecords) {
				Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getInvAtKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						infoWithProcessed.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		if (businessKeys.size() > 0 && !businessKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listBusiErrors) {
				Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getInvAtKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						businessValErrors.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		for (Gstr1VerticalAtRespDto strDto : listBusiProcess) {
			Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			listBusinessResponse.add(strDto);
			String invAtKey = strucProcessed.getInvAtKey();
			if (!businessKeys.contains(invAtKey)) {
				listBusinessProcess.add(strucProcessed);
			}
		}

		List<Gstr1ARDetailsEntity> finalList = new ArrayList<>();
		List<String> existProcessData = new ArrayList<>();
		if (listBusinessResponse != null && !listBusinessResponse.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listBusinessResponse) {
				Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);

				String errorKey = strucProcessed.getInvAtKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						processingResults.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);

			}
		}
		if (listBusinessProcess != null && !listBusinessProcess.isEmpty()) {
			List<Gstr1ARDetailsEntity> atDoc1 = convertSRFileToOutwardTransDoc(
					listBusinessProcess);

			for (Gstr1ARDetailsEntity atProcessed : atDoc1) {
				String atInvKey = atProcessed.getInvAtKey();
				finalList.add(atProcessed);
				existProcessData.add(atInvKey);
			}
			List<Gstr1VerticalAtRespDto> listOfFinal = new ArrayList<>();
			for (Gstr1ARDetailsEntity atProcessed : atDoc1) {
				Gstr1VerticalAtRespDto dtoConvert = null;
				if (atProcessed.getId() != null) {
					Optional<Gstr1ARDetailsEntity> findById = gstr1ARRepository
							.findById(atProcessed.getId());
					if (findById.isPresent()) {
						atProcessed.setAsEnterId(findById.get().getAsEnterId());
						atProcessed.setFileId(findById.get().getFileId());
						gstr1ARRepository.save(atProcessed);
					}
				} else {
					Gstr1ARDetailsEntity save = gstr1ARRepository
							.save(atProcessed);
					dtoConvert = saveDtoConvert(save);
					listOfFinal.add(dtoConvert);
				}
			}
			if (listOfFinal != null && !listOfFinal.isEmpty()) {
				for (Gstr1VerticalAtRespDto strDto : listOfFinal) {
					Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
							strDto);

					String processKey = strucProcessed.getInvAtKey()
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

	private Gstr1VerticalAtRespDto saveDtoConvert(Gstr1ARDetailsEntity save) {
		Gstr1VerticalAtRespDto dto = new Gstr1VerticalAtRespDto();
		dto.setId(save.getId());
		dto.setSection(save.getUiSectionType());
		dto.setSNo(save.getSNo());
		dto.setGstin(save.getSgstin());
		dto.setTaxPeriod(save.getReturnPeriod());
		dto.setTransType(save.getTransType());
		if ("ATA".equalsIgnoreCase(save.getUiSectionType())) {
			dto.setMonth(save.getMonth());
			dto.setOrgPos(save.getOrgPos());
			String orgRate = (save.getOrgRate() != null
					&& !save.getOrgRate().toString().trim().isEmpty())
							? String.valueOf(save.getOrgRate()) : null;
			dto.setOrgRate(orgRate);
			dto.setOrgAdvance(save.getOrgGrossAdvRec().toString());
		}
		dto.setNewPos(save.getNewPos());
		String newRate = (save.getNewRate() != null
				&& !save.getNewRate().toString().trim().isEmpty())
						? String.valueOf(save.getNewRate()) : null;
		dto.setNewRate(newRate);
		dto.setNewAdvance(save.getNewGrossAdvRec().toString());

		String igstAmt = (save.getIgstAmt() != null
				&& !save.getIgstAmt().toString().trim().isEmpty())
						? String.valueOf(save.getIgstAmt()) : null;

		dto.setIgst(igstAmt);

		String cgstAmt = (save.getCgstAmt() != null
				&& !save.getCgstAmt().toString().trim().isEmpty())
						? String.valueOf(save.getCgstAmt()) : null;

		dto.setCgst(cgstAmt);

		String sgstAmt = (save.getSgstAmt() != null
				&& !save.getSgstAmt().toString().trim().isEmpty())
						? String.valueOf(save.getSgstAmt()) : null;
		dto.setSgst(sgstAmt);

		String cessAmt = (save.getCessAmt() != null
				&& !save.getCessAmt().toString().trim().isEmpty())
						? String.valueOf(save.getCessAmt()) : null;
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

	private List<Gstr1ARDetailsEntity> convertSRFileToOutwardTransDoc(
			List<Gstr1AsEnteredAREntity> busiProcess) {
		List<Gstr1ARDetailsEntity> listOfAt = new ArrayList<>();

		Gstr1ARDetailsEntity at = null;
		for (Gstr1AsEnteredAREntity obj : busiProcess) {

			at = new Gstr1ARDetailsEntity();

			BigDecimal rate = BigDecimal.ZERO;
			String orgRate = obj.getOrgRate();
			if (orgRate != null && !orgRate.isEmpty()) {
				rate = NumberFomatUtil.getBigDecimal(orgRate);
				at.setOrgRate(rate);
			}
			BigDecimal orgGross = BigDecimal.ZERO;
			String orgGrossAdv = obj.getOrgGrossAdvRec();
			if (orgGrossAdv != null && !orgGrossAdv.isEmpty()) {
				orgGross = NumberFomatUtil.getBigDecimal(orgGrossAdv);
				at.setOrgGrossAdvRec(orgGross);
			}
			BigDecimal newRate = BigDecimal.ZERO;
			String nRate = obj.getNewRate();
			if (nRate != null && !nRate.isEmpty()) {
				newRate = NumberFomatUtil.getBigDecimal(nRate);
				at.setNewRate(newRate);
			}
			BigDecimal newGross = BigDecimal.ZERO;
			String newGrossAdv = obj.getNewGrossAdvRec();
			if (newGrossAdv != null && !newGrossAdv.isEmpty()) {
				newGross = NumberFomatUtil.getBigDecimal(newGrossAdv);
				at.setNewGrossAdvRec(newGross);
			}
			BigDecimal igst = BigDecimal.ZERO;
			String igstAnt = obj.getIgstAmt();
			if (igstAnt != null && !igstAnt.isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(igstAnt);
				at.setIgstAmt(igst);
			}
			BigDecimal cgst = BigDecimal.ZERO;
			String cgstAmt = obj.getCgstAmt();
			if (cgstAmt != null && !cgstAmt.isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(cgstAmt);
				at.setCgstAmt(cgst);
			}
			BigDecimal sgst = BigDecimal.ZERO;
			String sgstAmt = obj.getSgstAmt();
			if (sgstAmt != null && !sgstAmt.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgstAmt);
				at.setSgstAmt(sgst);
			}
			BigDecimal cess = BigDecimal.ZERO;
			String cessAmt = obj.getCessAmt();
			if (cessAmt != null && !cessAmt.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cessAmt);
				at.setCessAmt(cess);
			}
			Integer deriRetPeriod = 0;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}

			if (obj.getId() != null)
				at.setId(obj.getId());
			at.setSNo(obj.getSNo());
			at.setUiSectionType(obj.getUiSectionType());
			at.setSgstin(obj.getSgstin());
			at.setReturnPeriod(obj.getReturnPeriod());
			at.setTransType(obj.getTransType());
			at.setMonth(obj.getMonth());
			at.setOrgPos(obj.getOrgPos());
			at.setNewPos(obj.getNewPos());
			at.setProfitCentre(obj.getProfitCentre());
			at.setPlant(obj.getPlant());
			at.setDivision(obj.getDivision());
			at.setLocation(obj.getLocation());
			at.setSalesOrganisation(obj.getSalesOrganisation());
			at.setDistributionChannel(obj.getDistributionChannel());
			at.setUserAccess1(obj.getUserAccess1());
			at.setUserAccess2(obj.getUserAccess2());
			at.setUserAccess3(obj.getUserAccess3());
			at.setUserAccess4(obj.getUserAccess4());
			at.setUserAccess5(obj.getUserAccess5());
			at.setUserAccess6(obj.getUserAccess6());
			at.setInfo(obj.isInfo());
			at.setUserDef1(obj.getUserDef1());
			at.setUserDef2(obj.getUserDef2());
			at.setUserDef3(obj.getUserDef3());
			at.setDerivedRetPeriod(deriRetPeriod);
			at.setSectionType(obj.isSectionType());
			at.setCreatedBy(obj.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			at.setCreatedOn(convertNow);
			at.setGstnAtKey(obj.getGstnAtKey());
			at.setInvAtKey(obj.getInvAtKey());
			listOfAt.add(at);

		}

		return listOfAt;
	}

	private Gstr1VerticalAtRespDto convertReponseDto(Gstr1VerticalAtRespDto dto,
			List<ProcessingResult> results) {
		String section = dto.getSection();
		dto.setId(dto.getId() != null && !dto.getId().toString().isEmpty()
				? dto.getId() : null);
		dto.setSection(dto.getSection());
		dto.setSNo(dto.getSNo());
		dto.setGstin(dto.getGstin());
		dto.setTaxPeriod(dto.getTaxPeriod());
		dto.setTransType(dto.getTransType());
		if ("ATA".equalsIgnoreCase(section)) {
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

	private Gstr1AsEnteredAREntity convertGstr1VerticalAtRespDto(
			Gstr1VerticalAtRespDto dto) {

		String section = dto.getSection();
		Gstr1AsEnteredAREntity entity = new Gstr1AsEnteredAREntity();
		entity.setSgstin(dto.getGstin());
		entity.setUiSectionType(dto.getSection());
		entity.setSNo(dto.getSNo());
		entity.setReturnPeriod(dto.getTaxPeriod());
		if (dto.getTransType() == null || dto.getTransType().isEmpty()) {
			entity.setTransType(GSTConstants.N);
		} else {
			entity.setTransType(dto.getTransType());
		}
		entity.setId(dto.getId());

		if ("ATA".equalsIgnoreCase(section)) {
			entity.setMonth(dto.getMonth());
			entity.setOrgPos(dto.getOrgPos());
			String orgRate = (dto.getOrgRate() != null
					&& !dto.getOrgRate().toString().trim().isEmpty())
							? String.valueOf(dto.getOrgRate()) : null;
			entity.setOrgRate(orgRate);
			entity.setOrgGrossAdvRec(dto.getOrgAdvance());
			entity.setSectionType(true);
		}
		entity.setNewPos(dto.getNewPos());
		String newRate = (dto.getNewRate() != null
				&& !dto.getNewRate().toString().trim().isEmpty())
						? String.valueOf(dto.getNewRate()) : null;
		entity.setNewRate(newRate);
		entity.setNewGrossAdvRec(dto.getNewAdvance());

		String igstAmt = (dto.getIgst() != null
				&& !dto.getIgst().toString().trim().isEmpty())
						? String.valueOf(dto.getIgst()) : null;

		entity.setIgstAmt(igstAmt);

		String cgstAmt = (dto.getCgst() != null
				&& !dto.getCgst().toString().trim().isEmpty())
						? String.valueOf(dto.getCgst()) : null;

		entity.setCgstAmt(cgstAmt);

		String sgstAmt = (dto.getSgst() != null
				&& !dto.getSgst().toString().trim().isEmpty())
						? String.valueOf(dto.getSgst()) : null;
		entity.setSgstAmt(sgstAmt);

		String cessAmt = (dto.getCess() != null
				&& !dto.getCess().toString().trim().isEmpty())
						? String.valueOf(dto.getCess()) : null;
		entity.setCessAmt(cessAmt);
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
		String atGstnKey = createGstnKey(entity);
		entity.setInvAtKey(atInvKey);
		entity.setGstnAtKey(atGstnKey);
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		entity.setCreatedOn(convertNow);
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		entity.setCreatedBy(userName);
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

	private String createGstnKey(Gstr1AsEnteredAREntity obj) {
		String supplierGSTIN = (obj.getSgstin() != null
				&& !obj.getSgstin().isEmpty())
						? String.valueOf(obj.getSgstin()).trim() : "";
		String returnPeriod = (obj.getReturnPeriod() != null
				&& !obj.getReturnPeriod().isEmpty())
						? String.valueOf(obj.getReturnPeriod()).trim() : "";
		String transactionType = (obj.getTransType() != null
				&& !obj.getTransType().isEmpty())
						? String.valueOf(obj.getTransType()).trim()
						: GSTConstants.N;
		String month = (obj.getMonth() != null && !obj.getMonth().isEmpty())
				? String.valueOf(obj.getMonth()).trim() : "";
		String orgPos = (obj.getOrgPos() != null && !obj.getOrgPos().isEmpty())
				? String.valueOf(obj.getOrgPos()).trim() : "";
		String orgRate = (obj.getOrgRate() != null
				&& !obj.getOrgRate().isEmpty())
						? String.valueOf(obj.getOrgRate()).trim() : "";

		String newPos = (obj.getNewPos() != null && !obj.getNewPos().isEmpty())
				? String.valueOf(obj.getNewPos()).trim() : "";

		String newRate = (obj.getNewRate() != null
				&& !obj.getNewRate().isEmpty())
						? String.valueOf(obj.getNewRate()).trim() : "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(supplierGSTIN)
				.add(returnPeriod).add(transactionType).add(month).add(orgPos)
				.add(orgRate).add(newPos).add(newRate).toString();
	}

	private String createInvKeyForAt(Gstr1AsEnteredAREntity obj) {
		String supplierGSTIN = (obj.getSgstin() != null
				&& !obj.getSgstin().isEmpty())
						? String.valueOf(obj.getSgstin()).trim() : "";
		String returnPeriod = (obj.getReturnPeriod() != null
				&& !obj.getReturnPeriod().isEmpty())
						? String.valueOf(obj.getReturnPeriod()).trim() : "";
		String transactionType = (obj.getTransType() != null
				&& !obj.getTransType().isEmpty())
						? String.valueOf(obj.getTransType()).trim()
						: GSTConstants.N;
		String month = (obj.getMonth() != null && !obj.getMonth().isEmpty())
				? String.valueOf(obj.getMonth()).trim() : "";
		String orgPos = (obj.getOrgPos() != null && !obj.getOrgPos().isEmpty())
				? String.valueOf(obj.getOrgPos()).trim() : "";
		String orgRate = (obj.getOrgRate() != null
				&& !obj.getOrgRate().isEmpty())
						? String.valueOf(obj.getOrgRate()).trim() : "";

		String newPos = (obj.getNewPos() != null && !obj.getNewPos().isEmpty())
				? String.valueOf(obj.getNewPos()).trim() : "";

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
	
	
	@Transactional(value = "clientTransactionManager")
	public JsonObject updateGstr1aVerticalData(List<Gstr1VerticalAtRespDto> list) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonElement respBody = null;
		JsonObject resp = new JsonObject();
		List<Gstr1VerticalAtRespDto> respList = new ArrayList<>();

		List<Object[]> listObAt = convertsJavaObjects(list);
		Map<String, List<ProcessingResult>> processingResults = arStructValidationChain
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
			Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			String invAtKey = strucProcessed.getInvAtKey();
			if (!strErrorKeys.contains(invAtKey)) {
				processedKeys.add(invAtKey);
				listStrProcess.add(strDto);
			} else {
				listStrErrors.add(strDto);
			}
		}

		if (strErrorKeys.size() > 0 && !strErrorKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listStrErrors) {
				Gstr1AsEnteredAREntity strucErrors = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucErrors.getInvAtKey()
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

		List<Gstr1VerticalAtRespDto> listInfoRecords = new ArrayList<>();

		if (listStrProcess.size() > 0 && !listStrProcess.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listStrProcess) {
				Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String invAtKey = strucProcessed.getInvAtKey();
				String keys = invAtKey.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				List<ProcessingResult> results = advancedReceivedBusinessChain
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
								listInfoRecords.add(strDto);
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
								listInfoRecords.add(strDto);
							}
						}
					}
				}
			}
		}
		List<String> businessKeys = new ArrayList<String>();

		List<Gstr1AsEnteredAREntity> listBusinessProcess = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listBusinessResponse = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listBusiProcess = new ArrayList<>();
		List<Gstr1VerticalAtRespDto> listBusiErrors = new ArrayList<>();

		if (businessValErrors.size() > 0 && !businessValErrors.isEmpty()) {
			for (String businessRules : businessValErrors.keySet()) {
				String errkey = businessRules.substring(0,
						businessRules.lastIndexOf('-'));
				businessKeys.add(errkey);
			}
		}

		for (Gstr1VerticalAtRespDto strDto : listStrProcess) {
			Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			String invAtKey = strucProcessed.getInvAtKey();
			if (!businessKeys.contains(invAtKey)) {
				processedKeys.add(invAtKey);
				listBusiProcess.add(strDto);
			} else {
				listBusiErrors.add(strDto);
			}
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listInfoRecords) {
				Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getInvAtKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						infoWithProcessed.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		if (businessKeys.size() > 0 && !businessKeys.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listBusiErrors) {
				Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getInvAtKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						businessValErrors.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		for (Gstr1VerticalAtRespDto strDto : listBusiProcess) {
			Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			listBusinessResponse.add(strDto);
			String invAtKey = strucProcessed.getInvAtKey();
			if (!businessKeys.contains(invAtKey)) {
				listBusinessProcess.add(strucProcessed);
			}
		}

		List<Gstr1AARDetailsEntity> finalList = new ArrayList<>();
		List<String> existProcessData = new ArrayList<>();
		if (listBusinessResponse != null && !listBusinessResponse.isEmpty()) {
			for (Gstr1VerticalAtRespDto strDto : listBusinessResponse) {
				Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);

				String errorKey = strucProcessed.getInvAtKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalAtRespDto dtoRes = convertReponseDto(strDto,
						processingResults.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);

			}
		}
		if (listBusinessProcess != null && !listBusinessProcess.isEmpty()) {
			List<Gstr1AARDetailsEntity> atDoc1 = convertGstr1aSRFileToOutwardTransDoc(
					listBusinessProcess);

			for (Gstr1AARDetailsEntity atProcessed : atDoc1) {
				String atInvKey = atProcessed.getInvAtKey();
				finalList.add(atProcessed);
				existProcessData.add(atInvKey);
			}
			List<Gstr1VerticalAtRespDto> listOfFinal = new ArrayList<>();
			for (Gstr1AARDetailsEntity atProcessed : atDoc1) {
				Gstr1VerticalAtRespDto dtoConvert = null;
				if (atProcessed.getId() != null) {
					Optional<Gstr1AARDetailsEntity> findById = gstr1aARRepository
							.findById(atProcessed.getId());
					if (findById.isPresent()) {
						atProcessed.setAsEnterId(findById.get().getAsEnterId());
						atProcessed.setFileId(findById.get().getFileId());
						gstr1aARRepository.save(atProcessed);
					}
				} else {
					Gstr1AARDetailsEntity save = gstr1aARRepository
							.save(atProcessed);
					dtoConvert = saveGstr1aDtoConvert(save);
					listOfFinal.add(dtoConvert);
				}
			}
			if (listOfFinal != null && !listOfFinal.isEmpty()) {
				for (Gstr1VerticalAtRespDto strDto : listOfFinal) {
					Gstr1AsEnteredAREntity strucProcessed = convertGstr1VerticalAtRespDto(
							strDto);

					String processKey = strucProcessed.getInvAtKey()
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
	private Gstr1VerticalAtRespDto saveGstr1aDtoConvert(Gstr1AARDetailsEntity save) {
		Gstr1VerticalAtRespDto dto = new Gstr1VerticalAtRespDto();
		dto.setId(save.getId());
		dto.setSection(save.getUiSectionType());
		dto.setSNo(save.getSNo());
		dto.setGstin(save.getSgstin());
		dto.setTaxPeriod(save.getReturnPeriod());
		dto.setTransType(save.getTransType());
		if ("ATA".equalsIgnoreCase(save.getUiSectionType())) {
			dto.setMonth(save.getMonth());
			dto.setOrgPos(save.getOrgPos());
			String orgRate = (save.getOrgRate() != null
					&& !save.getOrgRate().toString().trim().isEmpty())
							? String.valueOf(save.getOrgRate()) : null;
			dto.setOrgRate(orgRate);
			dto.setOrgAdvance(save.getOrgGrossAdvRec().toString());
		}
		dto.setNewPos(save.getNewPos());
		String newRate = (save.getNewRate() != null
				&& !save.getNewRate().toString().trim().isEmpty())
						? String.valueOf(save.getNewRate()) : null;
		dto.setNewRate(newRate);
		dto.setNewAdvance(save.getNewGrossAdvRec().toString());

		String igstAmt = (save.getIgstAmt() != null
				&& !save.getIgstAmt().toString().trim().isEmpty())
						? String.valueOf(save.getIgstAmt()) : null;

		dto.setIgst(igstAmt);

		String cgstAmt = (save.getCgstAmt() != null
				&& !save.getCgstAmt().toString().trim().isEmpty())
						? String.valueOf(save.getCgstAmt()) : null;

		dto.setCgst(cgstAmt);

		String sgstAmt = (save.getSgstAmt() != null
				&& !save.getSgstAmt().toString().trim().isEmpty())
						? String.valueOf(save.getSgstAmt()) : null;
		dto.setSgst(sgstAmt);

		String cessAmt = (save.getCessAmt() != null
				&& !save.getCessAmt().toString().trim().isEmpty())
						? String.valueOf(save.getCessAmt()) : null;
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
	//gstr1a changes
	private List<Gstr1AARDetailsEntity> convertGstr1aSRFileToOutwardTransDoc(
			List<Gstr1AsEnteredAREntity> busiProcess) {
		List<Gstr1AARDetailsEntity> listOfAt = new ArrayList<>();

		Gstr1AARDetailsEntity at = null;
		for (Gstr1AsEnteredAREntity obj : busiProcess) {

			at = new Gstr1AARDetailsEntity();

			BigDecimal rate = BigDecimal.ZERO;
			String orgRate = obj.getOrgRate();
			if (orgRate != null && !orgRate.isEmpty()) {
				rate = NumberFomatUtil.getBigDecimal(orgRate);
				at.setOrgRate(rate);
			}
			BigDecimal orgGross = BigDecimal.ZERO;
			String orgGrossAdv = obj.getOrgGrossAdvRec();
			if (orgGrossAdv != null && !orgGrossAdv.isEmpty()) {
				orgGross = NumberFomatUtil.getBigDecimal(orgGrossAdv);
				at.setOrgGrossAdvRec(orgGross);
			}
			BigDecimal newRate = BigDecimal.ZERO;
			String nRate = obj.getNewRate();
			if (nRate != null && !nRate.isEmpty()) {
				newRate = NumberFomatUtil.getBigDecimal(nRate);
				at.setNewRate(newRate);
			}
			BigDecimal newGross = BigDecimal.ZERO;
			String newGrossAdv = obj.getNewGrossAdvRec();
			if (newGrossAdv != null && !newGrossAdv.isEmpty()) {
				newGross = NumberFomatUtil.getBigDecimal(newGrossAdv);
				at.setNewGrossAdvRec(newGross);
			}
			BigDecimal igst = BigDecimal.ZERO;
			String igstAnt = obj.getIgstAmt();
			if (igstAnt != null && !igstAnt.isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(igstAnt);
				at.setIgstAmt(igst);
			}
			BigDecimal cgst = BigDecimal.ZERO;
			String cgstAmt = obj.getCgstAmt();
			if (cgstAmt != null && !cgstAmt.isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(cgstAmt);
				at.setCgstAmt(cgst);
			}
			BigDecimal sgst = BigDecimal.ZERO;
			String sgstAmt = obj.getSgstAmt();
			if (sgstAmt != null && !sgstAmt.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgstAmt);
				at.setSgstAmt(sgst);
			}
			BigDecimal cess = BigDecimal.ZERO;
			String cessAmt = obj.getCessAmt();
			if (cessAmt != null && !cessAmt.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cessAmt);
				at.setCessAmt(cess);
			}
			Integer deriRetPeriod = 0;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}

			if (obj.getId() != null)
				at.setId(obj.getId());
			at.setSNo(obj.getSNo());
			at.setUiSectionType(obj.getUiSectionType());
			at.setSgstin(obj.getSgstin());
			at.setReturnPeriod(obj.getReturnPeriod());
			at.setTransType(obj.getTransType());
			at.setMonth(obj.getMonth());
			at.setOrgPos(obj.getOrgPos());
			at.setNewPos(obj.getNewPos());
			at.setProfitCentre(obj.getProfitCentre());
			at.setPlant(obj.getPlant());
			at.setDivision(obj.getDivision());
			at.setLocation(obj.getLocation());
			at.setSalesOrganisation(obj.getSalesOrganisation());
			at.setDistributionChannel(obj.getDistributionChannel());
			at.setUserAccess1(obj.getUserAccess1());
			at.setUserAccess2(obj.getUserAccess2());
			at.setUserAccess3(obj.getUserAccess3());
			at.setUserAccess4(obj.getUserAccess4());
			at.setUserAccess5(obj.getUserAccess5());
			at.setUserAccess6(obj.getUserAccess6());
			at.setInfo(obj.isInfo());
			at.setUserDef1(obj.getUserDef1());
			at.setUserDef2(obj.getUserDef2());
			at.setUserDef3(obj.getUserDef3());
			at.setDerivedRetPeriod(deriRetPeriod);
			at.setSectionType(obj.isSectionType());
			at.setCreatedBy(obj.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			at.setCreatedOn(convertNow);
			at.setGstnAtKey(obj.getGstnAtKey());
			at.setInvAtKey(obj.getInvAtKey());
			listOfAt.add(at);

		}

		return listOfAt;
	}
}