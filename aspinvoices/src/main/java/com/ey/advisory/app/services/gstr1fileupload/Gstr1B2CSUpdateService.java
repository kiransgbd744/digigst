package com.ey.advisory.app.services.gstr1fileupload;

import java.lang.reflect.Type;
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
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.data.entities.client.Gstr1B2csDetailsEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSRepository;
import com.ey.advisory.app.docs.dto.Gstr1VerticalSummaryRespDto;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.services.strcutvalidation.b2cs.B2CSAAspStructValidationChain;
import com.ey.advisory.app.services.strcutvalidation.b2cs.B2csApiStructValidationChain;
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
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Service("Gstr1B2CSUpdateService")
public class Gstr1B2CSUpdateService {

	private final static String WEB_UPLOAD_KEY = "|";

	@Autowired
	@Qualifier("B2csValidationChain")
	private B2csValidationChain b2csValidationChain;

	@Autowired
	@Qualifier("Gstr1B2CSRepository")
	Gstr1B2CSRepository repository;

	@Autowired
	@Qualifier("B2csApiStructValidationChain")
	B2csApiStructValidationChain b2csStructValidationChain;

	@Autowired
	@Qualifier("B2CSAAspStructValidationChain")
	B2CSAAspStructValidationChain b2csaStructValidationChain;

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
	public JsonObject updateVerticalData(
			List<Gstr1VerticalSummaryRespDto> list) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonElement respBody = null;
		JsonObject resp = new JsonObject();
		List<Gstr1VerticalSummaryRespDto> respList = new ArrayList<>();

		Gstr1VerticalSummaryRespDto convertEntityToDto = null;
		deriveEntityId(list);
		for (Gstr1VerticalSummaryRespDto dto : list) {

			List<ErrorDescriptionDto> errorList = new ArrayList<>();
			List<ErrorDescriptionDto> infoList = new ArrayList<>();

			String section = dto.getSection();

			Gstr1AsEnteredB2csEntity entity = new Gstr1AsEnteredB2csEntity();

			entity.setEntityId(dto.getEntityId());
			entity.setEntityConfigParamMap(dto.getEntityConfigParamMap());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("entity onboarding question map{} for entityid{}",
						entity.getEntityConfigParamMap(), entity.getEntityId());
			}
			entity.setSgstin(dto.getSgstn());
			entity.setReturnPeriod(dto.getTaxPeriod());
			Boolean naConsideredAsUqcValueInHsn = gstnApi
					.isNAConsideredAsUqcValueInHsn(dto.getTaxPeriod());

			entity.setTransType(dto.getTransType());

			if ("B2CSA".equalsIgnoreCase(section)) {
				entity.setMonth(dto.getMonth());

				// String hsn = dto.getOrgHsnOrSac();

				String orgQunty = (dto.getOrgQunty() != null
						&& !dto.getOrgQunty().toString().trim().isEmpty())
								? String.valueOf(dto.getOrgQunty()) : null;

				entity.setOrgUom(dto.getOrgUom());
				entity.setOrgQnt(orgQunty);

				entity.setOrgPos(dto.getOrgPos());
				entity.setOrgHsnOrSac(dto.getOrgHsnOrSac());

				String orgRate = (dto.getOrgRate() != null
						&& !dto.getOrgRate().toString().trim().isEmpty())
								? String.valueOf(dto.getOrgRate()) : null;

				entity.setOrgRate(orgRate);

				String orgTaxValue = (dto.getOrgTaxableValue() != null && !dto
						.getOrgTaxableValue().toString().trim().isEmpty())
								? String.valueOf(dto.getOrgTaxableValue())
								: null;

				entity.setOrgTaxVal(orgTaxValue);

				String orgCGstin = (dto.getOrgEcomGstin() != null
						&& !dto.getOrgEcomGstin().toString().trim().isEmpty())
								? String.valueOf(dto.getOrgEcomGstin()) : null;

				entity.setOrgCGstin(orgCGstin);

				String orgSupVal = (dto.getOrgEcomSupplValue() != null && !dto
						.getOrgEcomSupplValue().toString().trim().isEmpty())
								? String.valueOf(dto.getOrgEcomSupplValue())
								: null;

				entity.setOrgSupVal(orgSupVal);

			}

			String newQnt = (dto.getNewQunty() != null
					&& !dto.getNewQunty().toString().trim().isEmpty())
							? String.valueOf(dto.getNewQunty()) : null;

			/*
			 * if (naConsideredAsUqcValueInHsn) { if
			 * ("99".equalsIgnoreCase(dto.getNewHsnOrSac().substring(0, 2)) ||
			 * "OTH".equalsIgnoreCase(dto.getNewUom()) ||
			 * com.google.common.base.Strings .isNullOrEmpty(dto.getNewUom())) {
			 * 
			 * entity.setNewUom("NA"); entity.setNewQnt("0"); } else {
			 * entity.setNewUom(dto.getNewUom()); entity.setNewQnt(newQnt);
			 * 
			 * }
			 * 
			 * }else {
			 */ entity.setNewUom(dto.getNewUom());
			entity.setNewQnt(newQnt);

			// }

			entity.setNewPos(dto.getNewPos());
			entity.setNewHsnOrSac(dto.getNewHsnOrSac());
			// entity.setNewUom(dto.getNewUom());

			String newRate = (dto.getNewRate() != null
					&& !dto.getNewRate().toString().trim().isEmpty())
							? String.valueOf(dto.getNewRate()) : null;

			entity.setNewRate(newRate);

			String newTaxValue = (dto.getNewTaxableValue() != null
					&& !dto.getNewTaxableValue().toString().trim().isEmpty())
							? String.valueOf(dto.getNewTaxableValue()) : null;

			entity.setNewTaxVal(newTaxValue);
			entity.setNewGstin(dto.getNewEcomGstin());

			String newSupValue = (dto.getNewEcomSupplValue() != null
					&& !dto.getNewEcomSupplValue().toString().trim().isEmpty())
							? String.valueOf(dto.getNewEcomSupplValue()) : null;

			entity.setNewSupVal(newSupValue);

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

			String totalValue = (dto.getTotalValue() != null
					&& !dto.getTotalValue().toString().trim().isEmpty())
							? String.valueOf(dto.getTotalValue()) : null;
			entity.setTotalValue(totalValue);
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
			entity.setCreatedOn(LocalDateTime.now());
			String b2cInvKey = getInvKey(entity);
			if (b2cInvKey != null && b2cInvKey.length() > 2200) {
				b2cInvKey = b2cInvKey.substring(0, 2200);
			}
			String b2cGstnKey = getGstnKey(entity);
			if (b2cGstnKey != null && b2cGstnKey.length() > 1000) {
				b2cGstnKey = b2cGstnKey.substring(0, 1000);
			}
			entity.setGstnB2csKey(b2cGstnKey);
			entity.setInvB2csKey(b2cInvKey);

			List<Object[]> docObjList = new ArrayList<>();
			List<ProcessingResult> structuralProcessingResults = new ArrayList<>();
			if ("B2CS".equalsIgnoreCase(dto.getSection())) {
				docObjList = convertB2CSListToArrObj(dto);
				structuralProcessingResults = b2csStructValidationChain
						.validation(docObjList);
			} else {

				docObjList = convertB2CSAListToArrObj(dto);
				structuralProcessingResults = b2csaStructValidationChain
						.validation(docObjList);

			}

			if (!structuralProcessingResults.isEmpty()
					&& structuralProcessingResults.size() > 0) {

				for (ProcessingResult pr : structuralProcessingResults) {

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

			List<ProcessingResult> businessProcessingResults = new ArrayList<>();
			if (structuralProcessingResults.isEmpty()
					&& structuralProcessingResults.size() == 0) {
				businessProcessingResults = b2csValidationChain.validate(entity,
						null);
			}
			if (!businessProcessingResults.isEmpty()
					&& businessProcessingResults.size() > 0) {
				for (ProcessingResult pr : businessProcessingResults) {

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

			if (errorList.size() > 0) {

				dto.setErrorList(errorList);
				respList.add(dto);
				respBody = gson.toJsonTree(respList);
			} else {
				Gstr1B2csDetailsEntity save2 = save(entity, section);
				if ("B2CSA".equalsIgnoreCase(section)) {
					save2.setSectionType(true);
				}
				repository.UpdateId(dto.getId());
				Gstr1B2csDetailsEntity save = repository.save(save2);

				convertEntityToDto = convertEntityToDto(save, section);
				if (infoList != null && infoList.size() > 0) {
					convertEntityToDto.setErrorList(infoList);
				}
				convertEntityToDto.setSNo(dto.getSNo());
				respList.add(convertEntityToDto);

			}

		}
		respBody = gson.toJsonTree(respList);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return resp;
	}

	private void deriveEntityId(List<Gstr1VerticalSummaryRespDto> list) {

		Long entityId = gSTNDetailRepository
				.findEntityIdByGstin(list.get(0).getSgstn());
		Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
				.getEntityAndConfParamMap();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("onboarding question map{} for entityid{}", map,
					entityId);
		}
		list.stream().forEach(data -> {
			data.setEntityId(entityId);
			data.setEntityConfigParamMap(map);
		});

	}

	public String getInvKey(Gstr1AsEnteredB2csEntity obj) {
		String sgstin = (obj.getSgstin() != null)
				? String.valueOf(obj.getSgstin()).trim() : "";
		String retPeriod = (obj.getReturnPeriod() != null)
				? String.valueOf(obj.getReturnPeriod()).trim() : "";
		String transType = (obj.getTransType() != null)
				? String.valueOf(obj.getTransType()).trim() : GSTConstants.N;
		String month = (obj.getMonth() != null)
				? String.valueOf(obj.getMonth()).trim() : GSTConstants.N;
		String orgPos = (obj.getOrgPos() != null)
				? String.valueOf(obj.getOrgPos()).trim() : GSTConstants.N;
		String orgRate = (obj.getOrgRate() != null)
				? String.valueOf(obj.getOrgRate()).trim() : GSTConstants.N;
		String orgEcom = (obj.getOrgCGstin() != null)
				? String.valueOf(obj.getOrgCGstin()).trim() : GSTConstants.N;
		String newPos = (obj.getNewPos() != null)
				? String.valueOf(obj.getNewPos()).trim() : "";
		String newHsn = (obj.getNewHsnOrSac() != null)
				? String.valueOf(obj.getNewHsnOrSac()).trim() : "";
		String newUOM = (obj.getNewUom() != null)
				? String.valueOf(obj.getNewUom()).trim() : GSTConstants.N;
		String newRate = (obj.getNewRate() != null)
				? String.valueOf(obj.getNewRate()).trim() : "";
		String newEcom = (obj.getNewGstin() != null)
				? String.valueOf(obj.getNewGstin()).trim() : GSTConstants.N;
		String profitCenter = (obj.getProfitCentre() != null)
				? String.valueOf(obj.getProfitCentre()).trim()
				: GSTConstants.NA;
		String plant = (obj.getPlant() != null)
				? String.valueOf(obj.getPlant()).trim() : GSTConstants.NA;
		String divison = (obj.getDivision() != null)
				? String.valueOf(obj.getDivision()).trim() : GSTConstants.NA;
		String location = (obj.getLocation() != null)
				? String.valueOf(obj.getLocation()).trim() : GSTConstants.NA;
		String salesOrg = (obj.getSalesOrganisation() != null)
				? String.valueOf(obj.getSalesOrganisation()).trim()
				: GSTConstants.NA;
		String distributedChannel = (obj.getDistributionChannel() != null)
				? String.valueOf(obj.getDistributionChannel()).trim()
				: GSTConstants.NA;
		String user1 = (obj.getUserAccess1() != null)
				? String.valueOf(obj.getUserAccess1()).trim() : GSTConstants.NA;
		String user2 = (obj.getUserAccess2() != null)
				? String.valueOf(obj.getUserAccess2()).trim() : GSTConstants.NA;
		String user3 = (obj.getUserAccess3() != null)
				? String.valueOf(obj.getUserAccess3()).trim() : GSTConstants.NA;
		String user4 = (obj.getUserAccess4() != null)
				? String.valueOf(obj.getUserAccess4()).trim() : GSTConstants.NA;
		String user5 = (obj.getUserAccess5() != null)
				? String.valueOf(obj.getUserAccess5()).trim() : GSTConstants.NA;
		String user6 = (obj.getUserAccess6() != null)
				? String.valueOf(obj.getUserAccess6()).trim() : GSTConstants.NA;
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(retPeriod)
				.add(transType).add(month).add(orgPos).add(orgRate).add(orgEcom)
				.add(newPos).add(newHsn).add(newUOM).add(newRate).add(newEcom)
				.add(profitCenter).add(plant).add(divison).add(location)
				.add(salesOrg).add(distributedChannel).add(user1).add(user2)
				.add(user3).add(user4).add(user5).add(user6).toString();
	}

	private String getGstnKey(Gstr1AsEnteredB2csEntity obj) {
		String sgstin = (obj.getSgstin() != null)
				? String.valueOf(obj.getSgstin()).trim() : "";
		String retPeriod = (obj.getReturnPeriod() != null)
				? String.valueOf(obj.getReturnPeriod()).trim() : "";
		String transType = (obj.getTransType() != null)
				? String.valueOf(obj.getTransType()).trim() : GSTConstants.N;
		String month = (obj.getMonth() != null)
				? String.valueOf(obj.getMonth()).trim() : GSTConstants.N;
		String orgPos = (obj.getOrgPos() != null)
				? String.valueOf(obj.getOrgPos()).trim() : GSTConstants.N;
		String orgRate = (obj.getOrgRate() != null)
				? String.valueOf(obj.getOrgRate()).trim() : GSTConstants.N;
		String orgEcom = (obj.getOrgCGstin() != null)
				? String.valueOf(obj.getOrgCGstin()).trim() : GSTConstants.N;
		String newPos = (obj.getNewPos() != null)
				? String.valueOf(obj.getNewPos()).trim() : GSTConstants.N;
		String newHsn = (obj.getNewHsnOrSac() != null)
				? String.valueOf(obj.getNewHsnOrSac()).trim() : GSTConstants.N;
		String newUOM = (obj.getNewUom() != null)
				? String.valueOf(obj.getNewUom()).trim() : GSTConstants.N;
		String newRate = (obj.getNewRate() != null)
				? String.valueOf(obj.getNewRate()).trim() : GSTConstants.N;
		String newEcom = (obj.getNewGstin() != null)
				? String.valueOf(obj.getNewGstin()).trim() : GSTConstants.N;
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(retPeriod)
				.add(transType).add(month).add(orgPos).add(orgRate).add(orgEcom)
				.add(newPos).add(newHsn).add(newUOM).add(newRate).add(newEcom)
				.toString();

	}

	public Gstr1B2csDetailsEntity save(Gstr1AsEnteredB2csEntity dto,
			String section) {

		Gstr1B2csDetailsEntity entity = new Gstr1B2csDetailsEntity();
		if (dto != null) {

			/*
			 * if (dto.getId() != null) { entity.setId(dto.getId()); }
			 */
			entity.setSgstin(dto.getSgstin());
			entity.setReturnPeriod(dto.getReturnPeriod());

			int derived = GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod());
			entity.setDerivedRetPeriod(derived);
			entity.setCreatedOn(dto.getCreatedOn());
			entity.setTransType(dto.getTransType());
			if ("B2CSA".equalsIgnoreCase(section)) {
				entity.setMonth(dto.getMonth());
				entity.setOrgPos(dto.getOrgPos());
				entity.setOrgHsnOrSac(dto.getOrgHsnOrSac());
				entity.setOrgUom(dto.getOrgUom());

				BigDecimal orgQnt = BigDecimal.ZERO;
				if (dto.getOrgQnt() != null
						&& !dto.getOrgQnt().toString().trim().isEmpty()) {
					String orgQntStr = (String.valueOf(dto.getOrgQnt()));
					orgQnt = new BigDecimal(orgQntStr);
				}

				entity.setOrgQnt(orgQnt);

				BigDecimal orgRate = BigDecimal.ZERO;
				if (dto.getOrgRate() != null
						&& !dto.getOrgRate().toString().trim().isEmpty()) {
					String orgRateStr = (String.valueOf(dto.getOrgRate()));
					orgRate = new BigDecimal(orgRateStr);
				}
				entity.setOrgRate(orgRate);

				BigDecimal orgTaxValue = BigDecimal.ZERO;
				if (dto.getOrgTaxVal() != null
						&& !dto.getOrgTaxVal().toString().trim().isEmpty()) {
					String orgTaxValStr = (String.valueOf(dto.getOrgTaxVal()));
					orgTaxValue = new BigDecimal(orgTaxValStr);
				}

				entity.setOrgTaxVal(orgTaxValue);
				entity.setOrgCGstin(dto.getOrgCGstin());

				BigDecimal orgSupValue = BigDecimal.ZERO;
				if (dto.getOrgSupVal() != null
						&& !dto.getOrgSupVal().toString().trim().isEmpty()) {
					String orgSupValStr = (String.valueOf(dto.getOrgSupVal()));
					orgSupValue = new BigDecimal(orgSupValStr);
				}

				entity.setOrgSupVal(orgSupValue);
			}
			entity.setNewPos(dto.getNewPos());
			entity.setNewHsnOrSac(dto.getNewHsnOrSac());
			entity.setNewUom(dto.getNewUom());

			BigDecimal newQnt = BigDecimal.ZERO;
			if (dto.getNewQnt() != null
					&& !dto.getNewQnt().toString().trim().isEmpty()) {
				String orgAntStr = (String.valueOf(dto.getNewQnt()));
				newQnt = new BigDecimal(orgAntStr);
			}
			entity.setNewQnt(newQnt);

			BigDecimal newRate = BigDecimal.ZERO;
			if (dto.getNewRate() != null
					&& !dto.getNewRate().toString().trim().isEmpty()) {
				String newRateStr = (String.valueOf(dto.getNewRate()));
				newRate = new BigDecimal(newRateStr);
			}
			entity.setNewRate(newRate);

			BigDecimal newTaxValue = BigDecimal.ZERO;
			if (dto.getNewTaxVal() != null
					&& !dto.getNewTaxVal().toString().trim().isEmpty()) {
				String newTaxValStr = (String.valueOf(dto.getNewTaxVal()));
				newTaxValue = new BigDecimal(newTaxValStr);
			}

			entity.setNewTaxVal(newTaxValue);
			entity.setNewGstin(dto.getNewGstin());

			BigDecimal newSupValue = BigDecimal.ZERO;
			if (dto.getNewSupVal() != null
					&& !dto.getNewSupVal().toString().trim().isEmpty()) {
				String newSupValStr = (String.valueOf(dto.getNewSupVal()));
				newSupValue = new BigDecimal(newSupValStr);
			}

			entity.setNewSupVal(newSupValue);

			BigDecimal newIgst = BigDecimal.ZERO;
			if (dto.getIgstAmt() != null
					&& !dto.getIgstAmt().toString().trim().isEmpty()) {
				String newIgstStr = (String.valueOf(dto.getIgstAmt()));
				newIgst = new BigDecimal(newIgstStr);
			}

			entity.setIgstAmt(newIgst);

			BigDecimal cgst = BigDecimal.ZERO;
			if (dto.getCgstAmt() != null
					&& !dto.getCgstAmt().toString().trim().isEmpty()) {
				String newCgstStr = (String.valueOf(dto.getCgstAmt()));
				cgst = new BigDecimal(newCgstStr);
			}
			entity.setCgstAmt(cgst);

			BigDecimal sgst = BigDecimal.ZERO;
			if (dto.getSgstAmt() != null
					&& !dto.getSgstAmt().toString().trim().isEmpty()) {
				String newSgstStr = (String.valueOf(dto.getSgstAmt()));
				sgst = new BigDecimal(newSgstStr);
			}
			entity.setSgstAmt(sgst);

			BigDecimal cess = BigDecimal.ZERO;
			if (dto.getCessAmt() != null
					&& !dto.getCessAmt().toString().trim().isEmpty()) {
				String newCessStr = (String.valueOf(dto.getCessAmt()));
				cess = new BigDecimal(newCessStr);
			}
			entity.setCessAmt(cess);

			BigDecimal totalValue = BigDecimal.ZERO;
			if (dto.getTotalValue() != null
					&& !dto.getTotalValue().toString().trim().isEmpty()) {
				String newTotalValStr = (String.valueOf(dto.getTotalValue()));
				totalValue = new BigDecimal(newTotalValStr);
			}
			entity.setTotalValue(totalValue);
			entity.setProfitCentre(dto.getProfitCentre());
			entity.setPlant(dto.getPlant());
			entity.setDivision(dto.getDivision());
			entity.setLocation(dto.getLocation());
			entity.setSalesOrganisation(dto.getSalesOrganisation());
			entity.setDistributionChannel(dto.getDistributionChannel());
			entity.setUserAccess1(dto.getUserAccess1());
			entity.setUserAccess2(dto.getUserAccess2());
			entity.setUserAccess3(dto.getUserAccess3());
			entity.setUserAccess4(dto.getUserAccess4());
			entity.setUserAccess5(dto.getUserAccess5());
			entity.setUserAccess6(dto.getUserAccess6());
			entity.setUserDef1(dto.getUserDef1());
			entity.setUserDef2(dto.getUserDef2());
			entity.setUserDef3(dto.getUserDef3());

			entity.setGstnB2csKey(dto.getGstnB2csKey());
			entity.setInvB2csKey(dto.getInvB2csKey());

		}
		return entity;
	}

	public Gstr1VerticalSummaryRespDto convertEntityToDto(
			Gstr1B2csDetailsEntity save, String section) {

		Gstr1VerticalSummaryRespDto respEnt = new Gstr1VerticalSummaryRespDto();

		respEnt.setId(save.getId());
		respEnt.setSgstn(save.getSgstin());
		respEnt.setTaxPeriod(save.getReturnPeriod());
		respEnt.setTransType(save.getTransType());
		respEnt.setOrgEcomGstin(save.getOrgCGstin());
		respEnt.setOrgEcomSupplValue(save.getOrgSupVal());
		respEnt.setOrgHsnOrSac(save.getOrgHsnOrSac());
		respEnt.setOrgPos(save.getOrgPos());
		respEnt.setOrgQunty(save.getOrgQnt());
		respEnt.setOrgRate(save.getOrgRate());
		respEnt.setOrgTaxableValue(save.getOrgTaxVal());
		respEnt.setOrgUom(save.getOrgUom());
		respEnt.setNewPos(save.getNewPos());
		respEnt.setNewHsnOrSac(save.getNewHsnOrSac());
		respEnt.setNewUom(save.getNewUom());
		respEnt.setNewQunty(save.getNewQnt());
		respEnt.setNewRate(save.getNewRate());
		respEnt.setNewTaxableValue(save.getNewTaxVal());
		respEnt.setNewEcomGstin(save.getNewGstin());
		respEnt.setNewEcomSupplValue(save.getNewSupVal());
		respEnt.setIgst(save.getIgstAmt());
		respEnt.setCgst(save.getCgstAmt());
		respEnt.setSgst(save.getSgstAmt());
		respEnt.setCess(save.getCessAmt());
		respEnt.setTotalValue(save.getTotalValue());
		respEnt.setProfitCntr(save.getProfitCentre());
		respEnt.setPlant(save.getPlant());
		respEnt.setDivision(save.getDivision());
		respEnt.setLocation(save.getLocation());
		respEnt.setSalesOrg(save.getSalesOrganisation());
		respEnt.setDistrChannel(save.getDistributionChannel());
		respEnt.setUsrAccess1(save.getUserAccess1());
		respEnt.setUsrAccess2(save.getUserAccess2());
		respEnt.setUsrAccess3(save.getUserAccess3());
		respEnt.setUsrAccess4(save.getUserAccess4());
		respEnt.setUsrAccess5(save.getUserAccess5());
		respEnt.setUsrAccess6(save.getUserAccess6());
		respEnt.setUsrDefined1(save.getUserDef1());
		respEnt.setUsrDefined2(save.getUserDef2());
		respEnt.setUsrDefined3(save.getUserDef3());

		return respEnt;

	}

	/**
	 * Converting List To Object Array For Section B2CS
	 * 
	 * @param list
	 * @return
	 */

	private List<Object[]> convertB2CSListToArrObj(
			Gstr1VerticalSummaryRespDto list) {
		// TODO Auto-generated method stub

		List<Object[]> docObjList = new ArrayList<>();
		if (list != null) {

			/* arrayList.forEach(list -> { */
			Object[] objArr = new Object[28];

			objArr[0] = list.getSgstn();
			objArr[1] = list.getTaxPeriod();
			objArr[2] = list.getTransType();
			objArr[3] = list.getNewPos();
			objArr[4] = list.getNewHsnOrSac();
			objArr[5] = list.getNewUom();
			objArr[6] = list.getNewQunty();
			objArr[7] = list.getNewRate();
			objArr[8] = list.getNewTaxableValue();
			objArr[9] = list.getNewEcomGstin();
			objArr[10] = list.getNewEcomSupplValue();
			objArr[11] = list.getIgst();
			objArr[12] = list.getCgst();
			objArr[13] = list.getSgst();
			objArr[14] = list.getCess();
			objArr[15] = list.getTotalValue();
			objArr[16] = list.getProfitCntr();
			objArr[17] = list.getPlant();
			objArr[18] = list.getDivision();
			objArr[19] = list.getLocation();
			objArr[20] = list.getSalesOrg();
			objArr[21] = list.getDistrChannel();
			objArr[22] = list.getUsrAccess1();
			objArr[23] = list.getUsrAccess2();
			objArr[24] = list.getUsrAccess3();
			objArr[25] = list.getUsrAccess4();
			objArr[26] = list.getUsrAccess5();
			objArr[27] = list.getUsrAccess6();
			docObjList.add(objArr);
		}

		return docObjList;
	}

	/**
	 * Converting List To Object Array For Section B2CSA
	 * 
	 * @param list
	 * @return
	 */

	private List<Object[]> convertB2CSAListToArrObj(
			Gstr1VerticalSummaryRespDto list) {
		// TODO Auto-generated method stub

		List<Object[]> docObjList = new ArrayList<>();
		if (list != null) {

			Object[] objArr = new Object[37];

			objArr[0] = list.getSgstn();
			objArr[1] = list.getTaxPeriod();
			objArr[2] = list.getTransType();
			objArr[3] = list.getMonth();
			objArr[4] = list.getOrgPos();
			objArr[5] = list.getOrgHsnOrSac();
			objArr[6] = list.getOrgUom();
			objArr[7] = list.getOrgQunty();
			objArr[8] = list.getOrgRate();
			objArr[9] = list.getOrgTaxableValue();
			objArr[10] = list.getOrgEcomGstin();
			objArr[11] = list.getOrgEcomSupplValue();
			objArr[12] = list.getNewPos();
			objArr[13] = list.getNewHsnOrSac();
			objArr[14] = list.getNewUom();
			objArr[15] = list.getNewQunty();
			objArr[16] = list.getNewRate();
			objArr[17] = list.getNewTaxableValue();
			objArr[18] = list.getNewEcomGstin();
			objArr[19] = list.getNewEcomSupplValue();
			objArr[20] = list.getIgst();
			objArr[21] = list.getCgst();
			objArr[22] = list.getSgst();
			objArr[23] = list.getCess();
			objArr[24] = list.getTotalValue();
			objArr[25] = list.getProfitCntr();
			objArr[26] = list.getPlant();
			objArr[27] = list.getDivision();
			objArr[28] = list.getLocation();
			objArr[29] = list.getSalesOrg();
			objArr[30] = list.getDistrChannel();
			objArr[31] = list.getUsrAccess1();
			objArr[32] = list.getUsrAccess2();
			objArr[33] = list.getUsrAccess3();
			objArr[34] = list.getUsrAccess4();
			objArr[35] = list.getUsrAccess5();
			objArr[36] = list.getUsrAccess6();
			docObjList.add(objArr);

		}
		return docObjList;
	}

}
