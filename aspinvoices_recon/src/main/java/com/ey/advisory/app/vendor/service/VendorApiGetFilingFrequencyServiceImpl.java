package com.ey.advisory.app.vendor.service;

/**
 * @author vishal.verma
 */

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.VendorGstinFilingTypeEntity;
import com.ey.advisory.app.data.repositories.client.VendorGstinFilingTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GstinValidatorApiPushRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorValidatorPayloadRepository;
import com.ey.advisory.app.docs.dto.erp.GetFilingFrequencyDto;
import com.ey.advisory.app.docs.dto.erp.GetPreferenceDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.ApiCallLimitExceededException;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.gstnapi.services.PublicApiEndPointResolver;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Service("VendorApiGetFilingFrequencyServiceImpl")
@Slf4j
public class VendorApiGetFilingFrequencyServiceImpl
		implements VendorApiGetFilingFrequencyService {

	@Autowired
	@Qualifier("VendorGstinFilingTypeRepository")
	private VendorGstinFilingTypeRepository vendorGstinFilingTypeRepo;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("VendorValidatorPayloadRepository")
	private VendorValidatorPayloadRepository payloadRepository;

	@Autowired
	private PublicApiEndPointResolver endPointResolver;

	@Autowired
	@Qualifier("GstinValidatorApiPushRepository")
	private GstinValidatorApiPushRepository gstinValidatorRepo;

	List<String> taxPayerType = Arrays.asList("Regular", "SEZ Developer",
			"SEZ Unit");

	@Override
	public void getFilingFrequency(String financialYear, List<String> gstins,
			String payloadId) {

		List<String> q1FilingGstinList = new ArrayList<>();
		List<String> q2FilingGstinList = new ArrayList<>();
		List<String> q3FilingGstinList = new ArrayList<>();
		List<String> q4FilingGstinList = new ArrayList<>();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Invoking VendorApiGetFilingFrequencyServiceImpl "
							+ "gstins {} and financialYear{}",
					gstins, financialYear);
		}

		List<String> gstinList = gstinValidatorRepo
				.getGstinsByTaxPayerTypeInAndGstinAndPayloadId(taxPayerType,
						gstins, payloadId);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Invoked TaxpayerType filter gstinList : {}",
					gstinList);
		}
		if (gstinList.isEmpty()) {
			LOGGER.debug(
					"GstinList is Empty hense returning from frequency api");
			return;
		}

		try {

			List<List<String>> chunks = Lists.partition(gstinList, 2000);
			for (List<String> chunk : chunks) {
				q1FilingGstinList.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q1"));
				q2FilingGstinList.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q2"));
				q3FilingGstinList.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q3"));
				q4FilingGstinList.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q4"));
			}

			List<String> requiredGetGstinList = getRequiredGstinList(gstinList,
					financialYear, q1FilingGstinList, q2FilingGstinList,
					q3FilingGstinList, q4FilingGstinList);
			List<GetFilingFrequencyDto> getFilingFreqDtoList = getFilingResponse(
					requiredGetGstinList, financialYear, payloadId);
			List<VendorGstinFilingTypeEntity> entities = new ArrayList<>();
			for (GetFilingFrequencyDto dto : getFilingFreqDtoList) {
				String gstin = dto.getGstin();
				if (!(q1FilingGstinList.contains(gstin)
						|| q2FilingGstinList.contains(gstin)
						|| q3FilingGstinList.contains(gstin)
						|| q4FilingGstinList.contains(gstin))) {
					VendorGstinFilingTypeEntity entity = new VendorGstinFilingTypeEntity();
					entity.setGstin(gstin);
					entity.setCreatedOn(LocalDateTime.now());
					entity.setQuarter(dto.getQuarter());
					entity.setFilingType(dto.getFilingType());
					entity.setFy(dto.getFinancialYear());

					entities.add(entity);
				}
			}
			vendorGstinFilingTypeRepo.saveAll(entities);
		} catch (Exception e) {
			LOGGER.error(
					"Exception while processing the vendor filing type request:",
					e);

			String errorCode = (e instanceof AppException)
					? ((AppException) e).getErrCode()
					: "ER8888";
			String errorMsg = "ERP (run time error) -" + e.getMessage();
			errorCode = (errorCode == null) ? "ER8887" : errorCode;

			payloadRepository.updateFillingFrequencyErrorStatus(payloadId,
					LocalDateTime.now(),
					errorCode.length() > 1000 ? errorCode.substring(0, 1000)
							: errorCode,
					errorMsg.length() > 1000 ? errorMsg.substring(0, 1000)
							: errorMsg);
			throw new AppException(e);
		}
	}

	public List<String> getListOfQuarters() {
		int month = LocalDate.now().getMonthValue();
		List<String> quarters = new ArrayList<>();
		if (month > 3) {
			quarters.add("Q1");
			if (month > 6) {
				quarters.add("Q2");
				if (month > 9) {
					quarters.add("Q3");
				}
			}
		} else {
			quarters.add("Q1");
			quarters.add("Q2");
			quarters.add("Q3");
			quarters.add("Q4");
		}
		return quarters;
	}

	public List<String> getRequiredGstinList(List<String> gstinList,
			String financialYear, List<String> q1FilingGstinList,
			List<String> q2FilingGstinList, List<String> q3FilingGstinList,
			List<String> q4FilingGstinList) {
		List<String> requiredGetGstinList = new ArrayList<>();

		String currentFy = GenUtil.getCurrentFinancialYear();
		List<String> quarters = getListOfQuarters();
		if (currentFy == financialYear) {
			if (quarters.size() == 4) {
				for (String gstin : gstinList) {
					if (!(q1FilingGstinList.contains(gstin)
							&& q2FilingGstinList.contains(gstin)
							&& q3FilingGstinList.contains(gstin)
							&& q4FilingGstinList.contains(gstin))) {
						requiredGetGstinList.add(gstin);
					}
				}
			} else if (quarters.size() == 3) {
				for (String gstin : gstinList) {
					if (!(q1FilingGstinList.contains(gstin)
							&& q2FilingGstinList.contains(gstin)
							&& q3FilingGstinList.contains(gstin))) {
						requiredGetGstinList.add(gstin);
					}
				}
			} else if (quarters.size() == 2) {
				for (String gstin : gstinList) {
					if (!(q1FilingGstinList.contains(gstin)
							&& q2FilingGstinList.contains(gstin))) {
						requiredGetGstinList.add(gstin);
					}
				}
			} else {
				for (String gstin : gstinList) {
					if (!(q1FilingGstinList.contains(gstin))) {
						requiredGetGstinList.add(gstin);
					}
				}
			}
		} else {
			for (String gstin : gstinList) {
				if (!(q1FilingGstinList.contains(gstin)
						&& q2FilingGstinList.contains(gstin)
						&& q3FilingGstinList.contains(gstin)
						&& q4FilingGstinList.contains(gstin))) {
					requiredGetGstinList.add(gstin);
				}
			}
		}
		return requiredGetGstinList;
	}

	public APIResponse getFilingFrequencyResp(String groupCode, String gstin,
			String fy) {

		APIParam param1 = new APIParam("gstin", gstin);
		APIParam param2 = new APIParam("fy", fy);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.VENDOR_GET_PREFERENCE, param1, param2);

		try {

			endPointResolver.resolveEndPoint(PublicApiConstants.SEARCH);
			PublicApiContext.setContextMap(PublicApiConstants.GSTIN, gstin);
			return apiExecutor.execute(params, null);
		} catch (ApiCallLimitExceededException e) {
			APIResponse errResp = new APIResponse();
			return errResp.addError(new APIError("ER-1000", e.getMessage()));
		} catch (Exception e) {
			APIResponse errResp = new APIResponse();
			return errResp.addError(new APIError("ER-2000", e.getMessage()));

		}

	}

	public List<GetFilingFrequencyDto> getFilingResponse(
			List<String> requiredGetGstinList, String financialYear,
			String payloadId) {
		List<GetFilingFrequencyDto> getFilingFreqDtoList = new ArrayList<>();
		String groupCode = TenantContext.getTenantId();

		List<String> errorCode = new ArrayList<>();
		List<String> errorMsg = new ArrayList<>();

		for (String gstin : requiredGetGstinList) {
			try {

				Gson gson = new Gson();
				JsonArray jsonArray = new JsonArray();
				APIResponse apiResponse = getFilingFrequencyResp(groupCode,
						gstin, financialYear);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Get Preference apiResponse {} "
							+ "groupcode {} for gstin {} and financialYear{}",
							apiResponse.toString(), groupCode, gstin,
							financialYear);
				}
				if (apiResponse.isSuccess()) {

					JsonObject apiResp = JsonParser
							.parseString(apiResponse.getResponse())
							.getAsJsonObject();
					if (apiResp.has("response")) {
						jsonArray = apiResp.get("response").getAsJsonArray();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("jsonArray {} ", jsonArray);
						}
						Type listType = new TypeToken<ArrayList<GetPreferenceDto>>() {
						}.getType();
						List<GetPreferenceDto> dtoList = gson
								.fromJson(jsonArray, listType);

						for (GetPreferenceDto dto : dtoList) {
							GetFilingFrequencyDto getFilingfrequencyDto = new GetFilingFrequencyDto();
							getFilingfrequencyDto.setGstin(gstin);
							getFilingfrequencyDto
									.setFinancialYear(financialYear);
							getFilingfrequencyDto.setQuarter(dto.getQuarter());
							if (dto.getPreference().contains("M")) {
								getFilingfrequencyDto.setFilingType("Monthly");
							} else if (dto.getPreference().contains("Q")) {
								getFilingfrequencyDto
										.setFilingType("Quarterly");
							} else {
								getFilingfrequencyDto
										.setFilingType(dto.getPreference());
							}
							getFilingFreqDtoList.add(getFilingfrequencyDto);

						}
					} else {

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Get Prefence Detail failure  block for gstin {} ",
									gstin);
						}
					}
				} else {

					if (apiResponse != null) {

						errorCode.add(gstin + "-"
								+ apiResponse.getError().getErrorCode());
						errorMsg.add(gstin + "-"
								+ apiResponse.getError().getErrorDesc());

					}

				}
			} catch (Exception ex) {
				LOGGER.error(
						"Exception while processing the vendor filing type request {} "
								+ "groupcode {} for gstin {} and financialYear{}",
						ex, groupCode, gstin, financialYear);

				throw new AppException(ex);

			}
		}

		if (!errorCode.isEmpty()) {
			String errCode = Strings.join(errorCode, ',');
			String errMsg = Strings.join(errorMsg, ',');
			payloadRepository.updateFillingFrequencyErrorStatus(payloadId,
					LocalDateTime.now(), StringUtils.truncate(errCode, 1000),
					StringUtils.truncate(errMsg, 1000));
		}

		return getFilingFreqDtoList;
	}
}
