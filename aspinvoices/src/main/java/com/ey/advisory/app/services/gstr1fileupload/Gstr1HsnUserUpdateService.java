package com.ey.advisory.app.services.gstr1fileupload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredHsnEntity;
import com.ey.advisory.app.data.entities.client.Gstr1UserInputHsnSacEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputHsnSacRepository;
import com.ey.advisory.app.docs.dto.Gstr1HsnSummarySectionDto;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.services.strcutvalidation.HsnSacSummery.HsnSacAspStructValidationChain;
import com.ey.advisory.app.services.strcutvalidation.HsnSacSummery.HsnSacSummeryStructValidationChain;
import com.ey.advisory.app.services.validation.HsnSacSummery.HsnValidationChain;
import com.ey.advisory.app.services.validation.b2cs.B2csValidationChain;
import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("Gstr1HsnUserUpdateService")
public class Gstr1HsnUserUpdateService {

	@Autowired
	@Qualifier("Gstr1UserInputHsnSacRepository")
	Gstr1UserInputHsnSacRepository repo;

	@Autowired
	@Qualifier("HsnSacAspStructValidationChain")
	HsnSacAspStructValidationChain hsnStructural;

	@Autowired
	@Qualifier("HsnSacSummeryStructValidationChain")
	HsnSacSummeryStructValidationChain hsnSacStructural;

	@Autowired
	@Qualifier("HsnValidationChain")
	HsnValidationChain hsnBusinessChain;

	
	@Autowired
    @Qualifier("GstnApi")
    GstnApi gstnApi;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;
	
	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	@Transactional(value = "clientTransactionManager")
	public JsonObject updateVerticalData(List<Gstr1HsnSummarySectionDto> list) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonElement respBody = null;
		JsonObject resp = new JsonObject();
		// List<Gstr1UserInputHsnSacEntity> entityList = new ArrayList<>();

		List<Gstr1HsnSummarySectionDto> dtoResp = new ArrayList<>();
		Boolean rateIncludedInHsn;
		 deriveEntityId(list);
		
		for (Gstr1HsnSummarySectionDto dto : list) {

			 rateIncludedInHsn = gstnApi.isRateIncludedInHsn(dto.getTaxPeriod());
			List<ErrorDescriptionDto> errorList = new ArrayList<>();
			List<ErrorDescriptionDto> infoList = new ArrayList<>();
			Gstr1UserInputHsnSacEntity entity = new Gstr1UserInputHsnSacEntity();
			// entity.setId(dto.getId());
			
			Boolean naConsideredAsUqcValueInHsn = gstnApi.isNAConsideredAsUqcValueInHsn(dto.getTaxPeriod());
			/*if(naConsideredAsUqcValueInHsn){
				
				String hsn = dto.getHsn();
				//hsn.substring(0, 1);
				if ("99".equalsIgnoreCase(hsn.substring(0, 2))
						|| "OTH".equalsIgnoreCase(entity.getUqc())
						|| com.google.common.base.Strings.isNullOrEmpty(entity.getUqc())){
		
					entity.setUqc("NA");
					entity.setUsrQunty(BigDecimal.ZERO);
					
				}				
			}else{
				entity.setUqc(dto.getUqc());
				entity.setUsrQunty(dto.getUsrQunty());
			}
*/
		//	entity.setUqc(dto.getUqc());
		//	entity.setUsrQunty(dto.getUsrQunty());
			entity.setHsn(dto.getHsn());
			entity.setDesc(dto.getUiDesc());
		
			entity.setUsrRate(dto.getTaxRate());
			
			entity.setUsrCess(dto.getUsrCess());
			entity.setUsrTaxableValue(dto.getUsrTaxableValue());
			entity.setUsrTotalValue(dto.getUsrTotalValue());
			entity.setUsrTotalValue(dto.getUsrTotalValue());
			entity.setUsrIgst(dto.getUsrIgst());
			entity.setUsrCgst(dto.getUsrCgst());
			entity.setUsrSgst(dto.getUsrSgst());
			entity.setSgstin(dto.getSgstn());
			entity.setReturnPeriod(dto.getTaxPeriod());
			entity.setCreatedOn(LocalDateTime.now());
			entity.setRecordType(dto.getRecordType());

		/*	if (dto.getDocKey() != null) {
				// hsnData = repo.getHsnData(dto.getDocKey());
				// entity.setDesc(dto.getUiDesc());
				repo.UpdateId(dto.getDocKey());
				entity.setDocKey(dto.getDocKey());
			} else {
				String docKey ;
				if(rateIncludedInHsn){
				 docKey = getProcessedKey(dto);
				}else{
					 docKey = getProcessedKeywR(dto);
				}
				// entity.setDesc(dto.getUiDesc());
				// hsnData = repo.getHsnData(docKey);
				entity.setDocKey(docKey);
			}*/

			entity.setDerivedRetPeriod(GenUtil
					.convertTaxPeriodToInt(dto.getTaxPeriod()).toString());

			// Converting list to Array for Executing Structural Validations

			List<Object[]> convertHsnSAcListToArrObj = convertHsnSAcListToArrObj(
					dto);

			List<ProcessingResult> structureValidation = hsnStructural
					.validation(convertHsnSAcListToArrObj);

			if (!structureValidation.isEmpty()) {

				for (ProcessingResult pr : structureValidation) {

					ErrorDescriptionDto errorDto = new ErrorDescriptionDto();
					if ("ERROR".equalsIgnoreCase((pr.getType().toString()))) {
						errorDto.setErrorCode(pr.getCode());
						errorDto.setErrorDesc(pr.getDescription());
						// errorDto.setErrorField(errorField);
						errorDto.setErrorType(pr.getType().toString());
						TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
								.getLocation();
						if (null != loc) { // In case of bifurcation failure,
											// loc is
							// null
							Object[] arr = loc.getFieldIdentifiers();
							String[] fields = Arrays.copyOf(arr, arr.length,
									String[].class);
							String errField = StringUtils.join(fields, ',');
							errorDto.setErrorField(errField);

						}
						errorList.add(errorDto);

					}

					if ("INFO".equalsIgnoreCase((pr.getType().toString()))) {
						errorDto.setErrorCode(pr.getCode());
						errorDto.setErrorDesc(pr.getDescription());
						// errorDto.setErrorField(errorField);
						errorDto.setErrorType(pr.getType().toString());
						TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
								.getLocation();
						if (null != loc) { // In case of bifurcation failure,
											// loc is
							// null
							Object[] arr = loc.getFieldIdentifiers();
							String[] fields = Arrays.copyOf(arr, arr.length,
									String[].class);
							String errField = StringUtils.join(fields, ',');
							errorDto.setErrorField(errField);

						}
						infoList.add(errorDto);
					}
				}

			}
			List<ProcessingResult> businessValidate = new ArrayList<>();
			if (structureValidation.isEmpty()) {

				Gstr1AsEnteredHsnEntity convertBusinessEntityToChain = convertBusinessEntityToChain(
						dto);

				businessValidate = hsnBusinessChain
						.validate(convertBusinessEntityToChain, null);

				if (!businessValidate.isEmpty()) {

					for (ProcessingResult pr : businessValidate) {

						ErrorDescriptionDto errorDto = new ErrorDescriptionDto();
						if ("ERROR"
								.equalsIgnoreCase((pr.getType().toString()))) {
							errorDto.setErrorCode(pr.getCode());
							errorDto.setErrorDesc(pr.getDescription());
							// errorDto.setErrorField(errorField);
							errorDto.setErrorType(pr.getType().toString());
							TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
									.getLocation();
							if (null != loc) { // In case of bifurcation
												// failure,
												// loc is
								// null
								Object[] arr = loc.getFieldIdentifiers();
								String[] fields = Arrays.copyOf(arr, arr.length,
										String[].class);
								String errField = StringUtils.join(fields, ',');
								errorDto.setErrorField(errField);

							}
							errorList.add(errorDto);
						}
						if ("INFO"
								.equalsIgnoreCase((pr.getType().toString()))) {
							errorDto.setErrorCode(pr.getCode());
							errorDto.setErrorDesc(pr.getDescription());
							// errorDto.setErrorField(errorField);
							errorDto.setErrorType(pr.getType().toString());
							TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
									.getLocation();
							if (null != loc) { // In case of bifurcation
												// failure,
												// loc is
								// null
								Object[] arr = loc.getFieldIdentifiers();
								String[] fields = Arrays.copyOf(arr, arr.length,
										String[].class);
								String errField = StringUtils.join(fields, ',');
								errorDto.setErrorField(errField);

							}
							infoList.add(errorDto);
						}
					}
				}
			}
			
			
			if(naConsideredAsUqcValueInHsn){
				
				String hsn = dto.getHsn();
				//hsn.substring(0, 1);
				if ("99".equalsIgnoreCase(hsn.substring(0, 2))
					/*	|| "OTH".equalsIgnoreCase(dto.getUqc())
						|| com.google.common.base.Strings.isNullOrEmpty(dto.getUqc()) */
						 ){
		
					entity.setUqc("NA");
					entity.setUsrQunty(BigDecimal.ZERO);
					
				}else{
					entity.setUqc(dto.getUqc());
					entity.setUsrQunty(dto.getUsrQunty());
				}				
			}else{
				entity.setUqc(dto.getUqc());
				entity.setUsrQunty(dto.getUsrQunty());
			}
			if (dto.getDocKey() != null) {
				// hsnData = repo.getHsnData(dto.getDocKey());
				// entity.setDesc(dto.getUiDesc());
				repo.UpdateId(dto.getDocKey());
				entity.setDocKey(dto.getDocKey());
			} else {
				String docKey ;
				if(rateIncludedInHsn){
				 docKey = getProcessedKey(entity);
				}else{
					 docKey = getProcessedKeywR(entity);
				}
				// entity.setDesc(dto.getUiDesc());
				// hsnData = repo.getHsnData(docKey);
				entity.setDocKey(docKey);
			}
			
			if (!errorList.isEmpty()) {
				dto.setErrorList(errorList);
				dtoResp.add(dto);
			} else {
				Gstr1UserInputHsnSacEntity hsnData = repo
						.getHsnData(entity.getDocKey());

				if (hsnData != null) {
					repo.UpdateId(entity.getDocKey());
					aggregateHsn(hsnData, entity);
				}

			

				
				Gstr1UserInputHsnSacEntity save = repo.save(entity);
				Gstr1HsnSummarySectionDto convertEntityToDto = convertEntityToDto(
						save);
				convertEntityToDto.setSNo(dto.getSNo());
				convertEntityToDto.setErrorList(infoList);
				dtoResp.add(convertEntityToDto);
			}

		}
		respBody = gson.toJsonTree(dtoResp);

		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);

		return resp;

	}

	private void deriveEntityId(List<Gstr1HsnSummarySectionDto> list) {

		Long entityId = gSTNDetailRepository
				.findEntityIdByGstin(list.get(0).getSgstn());
		Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
				.getEntityAndConfParamMap();
		list.stream().forEach(data->{
			data.setEntityId(entityId);
			data.setEntityConfigParamMap(map);
		});
	
		
	
		
	}

	private void aggregateHsn(Gstr1UserInputHsnSacEntity hsnData,
			Gstr1UserInputHsnSacEntity entity) {
		entity.setDesc(entity.getDesc());
		entity.setUsrQunty(hsnData.getUsrQunty().add(entity.getUsrQunty()));
		entity.setUsrCess(hsnData.getUsrCess().add(entity.getUsrCess()));
		entity.setUsrCgst(hsnData.getUsrCgst().add(entity.getUsrCgst()));
		entity.setUsrIgst(hsnData.getUsrIgst().add(entity.getUsrIgst()));
		entity.setUsrSgst(hsnData.getUsrSgst().add(entity.getUsrSgst()));
		entity.setUsrTaxableValue(
				hsnData.getUsrTaxableValue().add(entity.getUsrTaxableValue()));
		entity.setUsrTotalValue(
				hsnData.getUsrTotalValue().add(entity.getUsrTotalValue()));

	}

	@SuppressWarnings("unused")
	private List<Object[]> convertHsnSAcListToArrObj(
			Gstr1HsnSummarySectionDto list) {
		// TODO Auto-generated method stub

		List<Object[]> docObjList = new ArrayList<>();
		if (list != null) {

			Object[] objArr = new Object[28];

			objArr[0] = list.getSgstn();
			objArr[1] = list.getTaxPeriod();
			objArr[2] = list.getHsn();
			objArr[3] = list.getUiDesc();
			objArr[4] = list.getUqc();
			objArr[5] = list.getUsrQunty();
			objArr[6] = list.getUsrTaxableValue();
			objArr[7] = list.getUsrIgst();
			objArr[8] = list.getUsrCgst();
			objArr[9] = list.getUsrSgst();
			objArr[10] = list.getUsrCess();

			objArr[11] = list.getUsrTotalValue();
			docObjList.add(objArr);
		}
		return docObjList;
	}

	public Gstr1AsEnteredHsnEntity convertBusinessEntityToChain(
			Gstr1HsnSummarySectionDto list) {
		Gstr1AsEnteredHsnEntity entity = new Gstr1AsEnteredHsnEntity();
		if (list != null) {
			entity.setSgstin(list.getSgstn());
			entity.setReturnPeriod(list.getTaxPeriod());
			entity.setHsn((list.getHsn()));
			entity.setUqc(list.getUqc());
			entity.setQuentity(list.getUsrQunty().toString());
			entity.setTaxableValue(list.getUsrTaxableValue().toString());
			entity.setTotalValue(list.getUsrTotalValue().toString());
			entity.setIgst(list.getUsrIgst().toString());
			entity.setCgst(list.getUsrCgst().toString());
			entity.setSgst(list.getUsrSgst().toString());
			entity.setCess(list.getUsrCess().toString());
			entity.setEntityConfigParamMap(list.getEntityConfigParamMap());
			entity.setEntityId(list.getEntityId());

		}
		return entity;
	}

	public Gstr1HsnSummarySectionDto convertEntityToDto(
			Gstr1UserInputHsnSacEntity save) {

		Gstr1HsnSummarySectionDto respdto = new Gstr1HsnSummarySectionDto();
		if (save != null) {
			// respdto.setId(save.getId());
			respdto.setDocKey(save.getDocKey());
			respdto.setSgstn(save.getSgstin());
			respdto.setTaxPeriod(save.getReturnPeriod());
			respdto.setHsn(save.getHsn());
			respdto.setUiDesc(save.getDesc());
			respdto.setUqc(save.getUqc());
			respdto.setTaxRate(save.getUsrRate());
			respdto.setUsrQunty(save.getUsrQunty());
			respdto.setUsrIgst(save.getUsrIgst());
			respdto.setUsrSgst(save.getUsrSgst());
			respdto.setUsrCgst(save.getUsrCgst());
			respdto.setUsrCess(save.getUsrCess());
			respdto.setUsrTaxableValue(save.getUsrTaxableValue());
			respdto.setUsrTotalValue(save.getUsrTotalValue());
		}
		return respdto;
	}

	public String getProcessedKey(Gstr1UserInputHsnSacEntity obj) {

		// String sgstn = obj.getSgstn();

		String sgstn = (obj.getSgstin() != null
				&& !obj.getSgstin().toString().trim().isEmpty())
						? String.valueOf(obj.getSgstin()) : null;
		// String taxPeriodString = obj.getTaxPeriod();
		String taxPeriod = GenUtil.convertTaxPeriodToInt(obj.getReturnPeriod())
				.toString();

		String hsn = null;
		if (obj.getHsn() != null) {
			hsn = obj.getHsn().toString();
		}

		/*
		 * String hsn = (obj.getHsn() != null &&
		 * !obj.getHsn().toString().trim().isEmpty()) ?
		 * String.valueOf(obj.getHsn()) : null;
		 */

		// String uqc = obj.getUqc();

		
		
		String uqc = (obj.getUqc() != null
				&& !obj.getUqc().toString().trim().isEmpty())
						? String.valueOf(obj.getUqc()) : null;

		BigDecimal taxRate = obj.getUsrRate();
		String rate = (taxRate != null && !taxRate.toString().trim().isEmpty())
				? String.valueOf(taxRate) : "0.00";
				if(hsn != null){
				 if("99".equalsIgnoreCase(hsn.substring(0, 2))){
				 uqc ="NA";
				}}
		if (rate.contains(".")) {
			return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(taxPeriod)
					.add(sgstn).add(uqc).add(rate).add(hsn).toString();
		} else {
			return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(taxPeriod)
					.add(sgstn).add(uqc).add(rate.concat(".00")).add(hsn)
					.toString();
		}

	}
	public String getProcessedKeywR(Gstr1UserInputHsnSacEntity obj) {

		// String sgstn = obj.getSgstn();

		String sgstn = (obj.getSgstin() != null
				&& !obj.getSgstin().toString().trim().isEmpty())
						? String.valueOf(obj.getSgstin()) : null;
		// String taxPeriodString = obj.getTaxPeriod();
		String taxPeriod = GenUtil.convertTaxPeriodToInt(obj.getReturnPeriod())
				.toString();

		String hsn = null;
		if (obj.getHsn() != null) {
			hsn = obj.getHsn().toString();
		}

		String uqc = (obj.getUqc() != null
				&& !obj.getUqc().toString().trim().isEmpty())
						? String.valueOf(obj.getUqc()) : null;

		
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(taxPeriod)
					.add(sgstn).add(uqc).add(hsn)
					.toString();
		}

	
}
