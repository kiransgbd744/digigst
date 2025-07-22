package com.ey.advisory.app.services.gstr1afileupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.entities.master.NatureOfDocEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.NatureDocTypeRepo;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredInvEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AInvoiceFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDocSeriesRespDto;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.services.strcutvalidation.InvoiceFile.InvoiceSeriesStructValidationChain;
import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;
import com.ey.advisory.app.services.validation.gstr1a.InvoiceFile.Gstr1AInvoiceFileValidationChain;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Sakshi.jain
 *
 */

@Service("Gstr1ADocSeriesUpdateService")
public class Gstr1ADocSeriesUpdateService {

	private final static String WEB_UPLOAD_KEY = "|";
	private static final String DOC_KEY_JOINER = "|";
	@Autowired
	@Qualifier("InvoiceSeriesStructValidationChain")
	private InvoiceSeriesStructValidationChain invoiceSeriesStructValidationChain;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("Gstr1AInvoiceFileValidationChain")
	private Gstr1AInvoiceFileValidationChain invoiceFileValidationChain;

	@Autowired
	@Qualifier("Gstr1AInvoiceRepository")
	private Gstr1AInvoiceRepository gstr1AInvoiceRepository;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	@Qualifier("NatureDocTypeRepo")
	private NatureDocTypeRepo natureDocTypeRepo;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Transactional(value = "clientTransactionManager")
	public JsonObject updateVerticalData(
			List<Gstr1VerticalDocSeriesRespDto> list) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonElement respBody = null;
		JsonObject resp = new JsonObject();
		List<Gstr1VerticalDocSeriesRespDto> respList = new ArrayList<>();
		Map<String, List<Gstr1VerticalDocSeriesRespDto>> totalResponse = new HashMap<>();
		List<String> listKeys = new ArrayList<>();

		for (Gstr1VerticalDocSeriesRespDto dtos : list) {
			Gstr1AAsEnteredInvEntity strucProcessed = convertGstr1VerticalAtRespDto(
					dtos);
			String invAtKey = strucProcessed.getInvoiceKey();
			listKeys.add(invAtKey);
			totalResponse.computeIfAbsent(invAtKey, k -> new ArrayList<>())
					.add(dtos);
		}
		List<Object[]> listObAt = convertsJavaObjects(list);
		Map<String, List<ProcessingResult>> processingResults = invoiceSeriesStructValidationChain
				.validationApi(listObAt, list);

		Map<String, Gstr1VerticalDocSeriesRespDto> dtoResponse = new HashMap<>();

		List<String> strErrorKeys = new ArrayList<>();

		for (String keys : processingResults.keySet()) {
			String errkey = keys.substring(0, keys.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		List<String> processedKeys = new ArrayList<>();
		List<Gstr1VerticalDocSeriesRespDto> listStrProcess = new ArrayList<>();
		List<Gstr1VerticalDocSeriesRespDto> listStrErrors = new ArrayList<>();

		for (Gstr1VerticalDocSeriesRespDto strDto : list) {
			Gstr1AAsEnteredInvEntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			String invAtKey = strucProcessed.getInvoiceKey();
			if (!strErrorKeys.contains(invAtKey)) {
				processedKeys.add(invAtKey);
				listStrProcess.add(strDto);
			} else {
				listStrErrors.add(strDto);
			}
		}
		if (strErrorKeys.size() > 0 && !strErrorKeys.isEmpty()) {
			for (Gstr1VerticalDocSeriesRespDto strDto : listStrErrors) {
				Gstr1AAsEnteredInvEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getInvoiceKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalDocSeriesRespDto dtoRes = convertReponseDto(strDto,
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
		List<Gstr1VerticalDocSeriesRespDto> listInfoErrors = new ArrayList<>();

		if (listStrProcess.size() > 0 && !listStrProcess.isEmpty()) {
			ProcessingContext context = new ProcessingContext();
			settingFiledGstins(context);
			gstinInfocache(context);
			Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
					.getEntityAndConfParamMap();
			List<Long> entityIds = clientGroupService
					.findEntityDetailsForGroupCode();
			Map<String, Long> gstinAndEntityMap = clientGroupService
					.getGstinAndEntityMapForGroupCode(entityIds);
			for (Gstr1VerticalDocSeriesRespDto strDto : listStrProcess) {
				Gstr1AAsEnteredInvEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				strucProcessed.setEntityId(
						gstinAndEntityMap.get(strucProcessed.getSgstin()));
				strucProcessed.setEntityConfigParamMap(map);

				String invAtKey = strucProcessed.getInvoiceKey();
				String keys = invAtKey.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				List<ProcessingResult> results = invoiceFileValidationChain
						.validate(strucProcessed, context);
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
								listInfoErrors.add(strDto);
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
								listInfoErrors.add(strDto);
							}
						}
					}
				}
			}
		}
		List<String> businessKeys = new ArrayList<String>();
		List<Gstr1VerticalDocSeriesRespDto> listBusinessResponse = new ArrayList<>();

		if (businessValErrors.size() > 0 && !businessValErrors.isEmpty()) {
			for (String businessRules : businessValErrors.keySet()) {
				String errkey = businessRules.substring(0,
						businessRules.lastIndexOf('-'));
				businessKeys.add(errkey);
			}
		}
		List<Gstr1VerticalDocSeriesRespDto> listBusiProcess = new ArrayList<>();
		List<Gstr1VerticalDocSeriesRespDto> listBusiErrors = new ArrayList<>();

		for (Gstr1VerticalDocSeriesRespDto strDto : listStrProcess) {
			Gstr1AAsEnteredInvEntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			String invAtKey = strucProcessed.getInvoiceKey();
			if (!businessKeys.contains(invAtKey)) {
				processedKeys.add(invAtKey);
				listBusiProcess.add(strDto);
			} else {
				listBusiErrors.add(strDto);
			}
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			for (Gstr1VerticalDocSeriesRespDto strDto : listInfoErrors) {
				Gstr1AAsEnteredInvEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getInvoiceKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalDocSeriesRespDto dtoRes = convertReponseDto(strDto,
						infoWithProcessed.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		if (businessKeys.size() > 0 && !businessKeys.isEmpty()) {
			for (Gstr1VerticalDocSeriesRespDto strDto : listBusiErrors) {
				Gstr1AAsEnteredInvEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);
				String errorKey = strucProcessed.getInvoiceKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalDocSeriesRespDto dtoRes = convertReponseDto(strDto,
						businessValErrors.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		List<Gstr1AAsEnteredInvEntity> listBusinessProcess = new ArrayList<>();
		for (Gstr1VerticalDocSeriesRespDto strDto : listStrProcess) {
			Gstr1AAsEnteredInvEntity strucProcessed = convertGstr1VerticalAtRespDto(
					strDto);
			listBusinessResponse.add(strDto);
			String invAtKey = strucProcessed.getInvoiceKey();
			if (!businessKeys.contains(invAtKey)) {
				listBusinessProcess.add(strucProcessed);
			}
		}
		List<Gstr1AInvoiceFileUploadEntity> finalList = new ArrayList<>();
		List<String> existProcessData = new ArrayList<>();
		if (listBusinessResponse != null && !listBusinessResponse.isEmpty()) {
			for (Gstr1VerticalDocSeriesRespDto strDto : listBusinessResponse) {
				Gstr1AAsEnteredInvEntity strucProcessed = convertGstr1VerticalAtRespDto(
						strDto);

				String errorKey = strucProcessed.getInvoiceKey()
						.concat(GSTConstants.SLASH)
						.concat(strDto.getSNo().toString());
				Gstr1VerticalDocSeriesRespDto dtoRes = convertReponseDto(strDto,
						processingResults.get(errorKey));
				dtoResponse.put(errorKey, dtoRes);
			}
		}
		List<Gstr1VerticalDocSeriesRespDto> listOfFinal = new ArrayList<>();
		if (listBusinessProcess != null && !listBusinessProcess.isEmpty()) {
			List<Gstr1AInvoiceFileUploadEntity> atDoc1 = convertSRFileToOutwardTransDoc(
					listBusinessProcess);
			Gstr1VerticalDocSeriesRespDto dtoConvert = new Gstr1VerticalDocSeriesRespDto();

			for (Gstr1AInvoiceFileUploadEntity atProcessed : atDoc1) {
				String atInvKey = atProcessed.getInvoiceKey();
				finalList.add(atProcessed);
				existProcessData.add(atInvKey);
			}
			for (Gstr1AInvoiceFileUploadEntity atProcessed : atDoc1) {
				if (atProcessed.getId() != null) {
					Optional<Gstr1AInvoiceFileUploadEntity> findById = gstr1AInvoiceRepository
							.findById(atProcessed.getId());
					if (findById.isPresent()) {
						atProcessed.setAsEnterId(findById.get().getAsEnterId());
						atProcessed.setFileId(findById.get().getFileId());
						gstr1AInvoiceRepository.save(atProcessed);
					}
				} else {
					Gstr1AInvoiceFileUploadEntity save = gstr1AInvoiceRepository
							.save(atProcessed);

					dtoConvert = saveDtoConvert(save);
					listOfFinal.add(dtoConvert);
				}
			}
			if (listOfFinal != null && !listOfFinal.isEmpty()) {
				for (Gstr1VerticalDocSeriesRespDto strDto : listOfFinal) {
					Gstr1AAsEnteredInvEntity strucProcessed = convertGstr1VerticalAtRespDto(
							strDto);

					String processKey = strucProcessed.getInvoiceKey()
							.concat(GSTConstants.SLASH)
							.concat(strDto.getSNo().toString());
					Gstr1VerticalDocSeriesRespDto dtoRes = convertReponseDto(
							strDto, infoWithProcessed.get(processKey));
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

	private Gstr1VerticalDocSeriesRespDto saveDtoConvert(
			Gstr1AInvoiceFileUploadEntity save) {
		Gstr1VerticalDocSeriesRespDto dto = new Gstr1VerticalDocSeriesRespDto();
		dto.setId(save.getId());
		dto.setSNo(save.getSNo());
		dto.setSgstin(save.getSgstin());
		dto.setRetPeriod(save.getReturnPeriod());
		dto.setSeriesTo(save.getTo());
		dto.setSeriesFrom(save.getFrom());
		dto.setDocNature(save.getNatureOfDocument());
		dto.setDocNatureId(Long.valueOf(save.getSerialNo()));
		dto.setTotal(save.getTotalNumber().toString());
		dto.setCancelled(save.getCancelled().toString());
		dto.setNetIssued(save.getNetNumber().toString());
		return dto;
	}

	private List<Gstr1AInvoiceFileUploadEntity> convertSRFileToOutwardTransDoc(
			List<Gstr1AAsEnteredInvEntity> busiProcess) {
		List<Gstr1AInvoiceFileUploadEntity> listOfAt = new ArrayList<>();

		Gstr1AInvoiceFileUploadEntity at = null;
		for (Gstr1AAsEnteredInvEntity obj : busiProcess) {
			at = new Gstr1AInvoiceFileUploadEntity();
			Integer deriRetPeriod = 0;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}
			if (obj.getId() != null)
				at.setId(obj.getId());
			at.setSgstin(obj.getSgstin());
			at.setReturnPeriod(obj.getReturnPeriod());
			at.setDerivedRetPeriod(deriRetPeriod);
			at.setTo(obj.getTo());
			at.setSNo(obj.getSNo());
			at.setFrom(obj.getFrom());
			at.setNatureOfDocument(obj.getNatureOfDocument());
			at.setSerialNo(Integer.parseInt(obj.getSerialNo()));
			at.setTotalNumber(Integer.parseInt(obj.getTotalNumber()));
			at.setCancelled(Integer.parseInt(obj.getCancelled()));
			at.setNetNumber(Integer.parseInt(obj.getNetNumber()));
			at.setInvoiceKey(obj.getInvoiceKey());
			at.setCreatedBy(obj.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			at.setCreatedOn(convertNow);
			listOfAt.add(at);
		}
		return listOfAt;
	}

	private Gstr1VerticalDocSeriesRespDto convertReponseDto(
			Gstr1VerticalDocSeriesRespDto dto, List<ProcessingResult> results) {
		dto.setId(dto.getId() != null && !dto.getId().toString().isEmpty()
				? dto.getId() : null);
		dto.setSNo(dto.getSNo());
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

	private Gstr1AAsEnteredInvEntity convertGstr1VerticalAtRespDto(
			Gstr1VerticalDocSeriesRespDto dto) {

		Gstr1AAsEnteredInvEntity entity = new Gstr1AAsEnteredInvEntity();
		entity.setSgstin(dto.getSgstin());
		entity.setSNo(dto.getSNo());
		entity.setReturnPeriod(dto.getRetPeriod());
		entity.setId(dto.getId());
		String serialNum = String.valueOf(dto.getDocNatureId());
		entity.setSerialNo(serialNum);
		String natureOfDoc = dto.getDocNature();
		if (natureOfDoc == null || natureOfDoc.isEmpty()) {
			natureDocTypeRepo = StaticContextHolder.getBean("NatureDocTypeRepo",
					NatureDocTypeRepo.class);
			if (serialNum.matches("[0-9]+")) {
				Long intSerial = Long.parseLong(serialNum);
				NatureOfDocEntity doc = natureDocTypeRepo
						.findNatureDocType(intSerial);
				if (doc != null) {
					entity.setNatureOfDocument(doc.getNatureDocType());
				}
			}
		} else {
			entity.setNatureOfDocument(dto.getDocNature());
		}
		entity.setTo(dto.getSeriesTo());
		entity.setFrom(dto.getSeriesFrom());
		entity.setTotalNumber(dto.getTotal());
		entity.setNetNumber(dto.getNetIssued());
		entity.setCancelled(dto.getCancelled());
		String atInvKey = createInvKeyForInv(entity);
		entity.setInvoiceKey(atInvKey);
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		entity.setCreatedOn(convertNow);
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		entity.setCreatedBy(userName);
		Integer derivedRetPeriod = GenUtil
				.convertTaxPeriodToInt(dto.getRetPeriod());
		if (derivedRetPeriod != null) {
			String derPeriod = String.valueOf(derivedRetPeriod);
			entity.setDerivedRetPeriod(derPeriod);
		} else {
			String derPeriod = String.valueOf(999999);
			entity.setDerivedRetPeriod(derPeriod);
		}
		return entity;

	}

	private List<Object[]> convertsJavaObjects(
			List<Gstr1VerticalDocSeriesRespDto> lists) {
		List<Object[]> docObjList = new ArrayList<>();
		if (lists != null && !lists.isEmpty()) {
			lists.forEach(list -> {
				Object[] objArr = new Object[9];
				objArr[0] = list.getSgstin();
				objArr[1] = list.getRetPeriod();
				objArr[2] = list.getDocNatureId();
				objArr[3] = list.getDocNature();
				objArr[4] = list.getSeriesFrom();
				objArr[5] = list.getSeriesTo();
				objArr[6] = list.getTotal();
				objArr[7] = list.getCancelled();
				objArr[8] = list.getNetIssued();
				docObjList.add(objArr);
			});
		}
		return docObjList;
	}

	public String createInvKeyForInv(Gstr1AAsEnteredInvEntity obj) {
		String sgstin = (obj.getSgstin() != null && !obj.getSgstin().isEmpty())
				? String.valueOf(obj.getSgstin()).trim() : "";
		String retPeriod = (obj.getReturnPeriod() != null
				&& !obj.getReturnPeriod().isEmpty())
						? String.valueOf(obj.getReturnPeriod()).trim() : "";
		String serialNum = (obj.getSerialNo() != null
				&& !obj.getSerialNo().isEmpty())
						? String.valueOf(obj.getSerialNo()).trim() : "";
		String from = (obj.getFrom() != null && !obj.getFrom().isEmpty())
				? String.valueOf(obj.getFrom()).trim() : "";
		String to = (obj.getTo() != null && !obj.getTo().isEmpty())
				? String.valueOf(obj.getTo()).trim() : "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(retPeriod)
				.add(serialNum).add(from).add(to).toString();
	}

	private void settingFiledGstins(ProcessingContext context) {
		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR1A", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {

			filedSet.add(
					entity.getGstin() + DOC_KEY_JOINER + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}

	private void gstinInfocache(ProcessingContext context) {
		List<GSTNDetailEntity> findDetails = gstinInfoRepository.findDetails();

		Map<String, String> gstinInfoMap = findDetails.stream()
				.collect(Collectors.toMap(GSTNDetailEntity::getGstin,
						GSTNDetailEntity::getRegistrationType));
		context.seAttribute("gstinInfoMap", gstinInfoMap);
	}

}
