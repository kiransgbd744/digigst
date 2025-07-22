package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.VendorGstinFilingTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterApiEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.VendorGstinFilingTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterApiEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.docs.dto.erp.GetFilingFrequencyDto;
import com.ey.advisory.app.docs.dto.erp.GetPreferenceDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
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

@Service("GetFilingFrequencyVendorCommunicationServiceImpl")
@Slf4j
public class GetFilingFrequencyVendorCommunicationServiceImpl
		implements GetFilingFrequencyVendorCommunicationService {

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	@Qualifier("VendorMasterApiEntityRepository")
	private VendorMasterApiEntityRepository vendorMasterApiRepo;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	private DocRepository docRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	@Qualifier("VendorGstinFilingTypeRepository")
	private VendorGstinFilingTypeRepository vendorGstinFilingTypeRepo;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private PublicApiEndPointResolver endPointResolver;

	private static final Map<String, MonthDay> dueDates = new HashMap<>();

	static {

		// GSTR1 Monthly due dates
		dueDates.put("Q1", MonthDay.of(4, 30));
		dueDates.put("Q2", MonthDay.of(7, 31));
		dueDates.put("Q3", MonthDay.of(10, 31));
		dueDates.put("Q4", MonthDay.of(1, 31));
	}

	@Override
	public void getFilingFrequency(String entityId, String financialYear,
			String complianceType, String noOfDays) {
		try {
			List<String> recipientPanList = entityInfoRepository
					.findPanByEntityId(Long.valueOf(entityId));
			List<String> gstinList = new ArrayList<>();
			if (complianceType.equalsIgnoreCase("VendorCompliance")) {
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.PREFRENCE_VENDOR_COMP);
				// Entities from Vendor Master
				List<VendorMasterUploadEntity> masterEntities = vendorMasterUploadEntityRepository
						.findByIsDeleteFalseAndRecipientPANIn(recipientPanList);
				gstinList.addAll(masterEntities.stream()
						.map(VendorMasterUploadEntity::getVendorGstin)
						.distinct().collect(Collectors.toList()));

				// Entities from Vendor Async Api Push
				List<VendorMasterApiEntity> apiEntities = vendorMasterApiRepo
						.findByIsDeleteFalseAndRecipientPANIn(recipientPanList);
				gstinList.addAll(apiEntities.stream()
						.map(VendorMasterApiEntity::getVendorGstin).distinct()
						.collect(Collectors.toList()));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get Preference api gstin for vendor compliance size {}",
							gstinList.size());
				}

			} else if (complianceType.equalsIgnoreCase("CustomerCompliance")) {
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.PREFRENCE_CUST_COMP);
				gstinList = getListOfCustomerGstin();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get Preference api gstin for customer compliance size {}",
							gstinList.size());
				}
			} else if (complianceType.equalsIgnoreCase("MyCompliance")) {
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.PREFRENCE_MY_COMP);
				gstinList = gstNDetailRepository
						.findgstinByEntityIdWithRegTypeForGstr1(
								Long.valueOf(entityId));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get Preference api gstin for my compliance size {}",
							gstinList.size());
				}
			} else {
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.PREFRENCE_NON_COMP);
				// Entities from Vendor Master for Non Compliance Vendor
				
				gstinList.addAll(vendorMasterUploadEntityRepository
						.findVendorGstin());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get Preference api gstin for non compliance size {}",
							gstinList.size());
				}
			}
			Set<String> q1FilingGstinList = new HashSet<>();
			Set<String> q2FilingGstinList = new HashSet<>();
			Set<String> q3FilingGstinList = new HashSet<>();
			Set<String> q4FilingGstinList = new HashSet<>();
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
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Get Preference api q1FilingGstinList size {} q2FilingGstinList size {} "
								+ "q3FilingGstinList size {}  q4FilingGstinList {} ",
						q1FilingGstinList.size(), q2FilingGstinList.size(),
						q3FilingGstinList.size(), q4FilingGstinList.size());
			}

			Set<String> requiredGetGstinList = getRequiredGstinList(gstinList,
					financialYear, q1FilingGstinList, q2FilingGstinList,
					q3FilingGstinList, q4FilingGstinList);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get Preference api requiredGetGstinList size {} ",
						requiredGetGstinList.size());
			}
			Pair<List<String>, List<GetFilingFrequencyDto>> pair = getFilingResponse(
					requiredGetGstinList, financialYear);
			List<String> requiredGstinList = pair.getValue0();
			List<GetFilingFrequencyDto> getFilingFreqDtoList = pair.getValue1();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get Preference api requiredGstinList size {} ",
						requiredGstinList.size());
			}

			List<List<String>> gstinchunks = Lists.partition(requiredGstinList,
					2000);
			Set<String> gstinListQ1 = new HashSet<>();
			Set<String> gstinListQ2 = new HashSet<>();
			Set<String> gstinListQ3 = new HashSet<>();
			Set<String> gstinListQ4 = new HashSet<>();
			for (List<String> chunk : gstinchunks) {
				gstinListQ1.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q1"));
				gstinListQ2.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q2"));
				gstinListQ3.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q3"));
				gstinListQ4.addAll(
						vendorGstinFilingTypeRepo.findByGstinInAndFyAndQuarter(
								chunk, financialYear, "Q4"));
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Get Preference api gstinListQ1 size {} gstinListQ2 size {} "
								+ "gstinListQ3 size {}  gstinListQ4 {} ",
						gstinListQ1.size(), gstinListQ2.size(),
						gstinListQ3.size(), gstinListQ4.size());
			}
			Integer year = Integer.parseInt(financialYear.substring(0, 4));
			LocalDate dueDateQ1 = dueDates.get("Q1").atYear(year);
			LocalDate dueDateQ2 = dueDates.get("Q2").atYear(year);
			LocalDate dueDateQ3 = dueDates.get("Q3").atYear(year);
			LocalDate dueDateQ4 = dueDates.get("Q4").atYear(year + 1);
			LocalDate currentDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			List<VendorGstinFilingTypeEntity> entities = new ArrayList<>();
			for (GetFilingFrequencyDto dto : getFilingFreqDtoList) {
				String gstin = dto.getGstin();
				if (((dto.getQuarter().equalsIgnoreCase("Q1"))
						&& (!gstinListQ1.contains(gstin))
						&& (currentDate.isAfter(dueDateQ1)))
						|| ((dto.getQuarter().equalsIgnoreCase("Q2"))
								&& (!gstinListQ2.contains(gstin))
								&& (currentDate.isAfter(dueDateQ2)))
						|| ((dto.getQuarter().equalsIgnoreCase("Q3"))
								&& (!gstinListQ3.contains(gstin))
								&& (currentDate.isAfter(dueDateQ3)))
						|| ((dto.getQuarter().equalsIgnoreCase("Q4"))
								&& (!gstinListQ4.contains(gstin))
								&& (currentDate.isAfter(dueDateQ4)))) {

					VendorGstinFilingTypeEntity entity = new VendorGstinFilingTypeEntity();
					entity.setGstin(gstin);
					entity.setReturnType("GSTR1");
					entity.setCreatedOn(LocalDateTime.now());
					entity.setQuarter(dto.getQuarter());
					entity.setFilingType(dto.getFilingType());
					entity.setFy(dto.getFinancialYear());
					entities.add(entity);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get Preference api entities size {} ",
						entities.size());
			}
			vendorGstinFilingTypeRepo.saveAll(entities);
		} catch (Exception e) {
			LOGGER.error(
					"Exception while processing the vendor filing type request:",
					e);
			throw new AppException(e);
		}
	}

	public List<String> getListOfCustomerGstin() {
		try {
			List<String> gstins = new ArrayList<>();
			List<String> custPans = docRepo.getDistinctCustomerPans();
			List<List<String>> panChunks = Lists.partition(custPans, 2000);
			for (List<String> chunk : panChunks) {
				gstins.addAll(docRepo.getDistinctCustomerGstin(chunk));
			}
			return gstins;
		} catch (Exception ee) {
			String msg = "Exception occered while getting cutomer Gstin";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
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

	public Set<String> getRequiredGstinList(List<String> gstinList,
			String financialYear, Set<String> q1FilingGstinList,
			Set<String> q2FilingGstinList, Set<String> q3FilingGstinList,
			Set<String> q4FilingGstinList) {
		Set<String> requiredGetGstinList = new HashSet<>();

		Integer year = Integer.parseInt(financialYear.substring(0, 4));
		LocalDate dueDateQ1 = dueDates.get("Q1").atYear(year);
		LocalDate dueDateQ2 = dueDates.get("Q2").atYear(year);
		LocalDate dueDateQ3 = dueDates.get("Q3").atYear(year);
		LocalDate dueDateQ4 = dueDates.get("Q4").atYear(year + 1);
		LocalDate currentDate = EYDateUtil
				.toISTDateTimeFromUTC(LocalDate.now());

		String currentFy = GenUtil.getCurrentFinancialYear();
		List<String> quarters = getListOfQuarters();
		if (currentFy.equalsIgnoreCase(financialYear)) {
			if (quarters.size() == 4) {
				for (String gstin : gstinList) {
					if (!(q1FilingGstinList.contains(gstin)
							&& q2FilingGstinList.contains(gstin)
							&& q3FilingGstinList.contains(gstin))
							|| ((!q4FilingGstinList.contains(gstin))
									&& (currentDate.isAfter(dueDateQ4)))) {
						requiredGetGstinList.add(gstin);
					}
				}
			} else if (quarters.size() == 3) {
				for (String gstin : gstinList) {
					if (!(q1FilingGstinList.contains(gstin)
							&& q2FilingGstinList.contains(gstin))
							|| (!q3FilingGstinList.contains(gstin)
									&& (currentDate.isAfter(dueDateQ3)))) {
						requiredGetGstinList.add(gstin);
					}
				}
			} else if (quarters.size() == 2) {
				for (String gstin : gstinList) {
					if (!(q1FilingGstinList.contains(gstin))
							|| (!q2FilingGstinList.contains(gstin)
									&& (currentDate.isAfter(dueDateQ2)))) {
						requiredGetGstinList.add(gstin);
					}
				}
			} else {
				for (String gstin : gstinList) {
					if (!q1FilingGstinList.contains(gstin)
							&& (currentDate.isAfter(dueDateQ1))) {
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
		endPointResolver.resolveEndPoint(PublicApiConstants.RETURNS);

		PublicApiContext.setContextMap(PublicApiConstants.GSTIN, gstin);
		PublicApiContext.setContextMap(PublicApiConstants.FY, fy);

		APIResponse resp = new APIResponse();
		APIParam param1 = new APIParam("gstin", gstin);
		APIParam param2 = new APIParam("fy", fy);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.VENDOR_GET_PREFERENCE, param1, param2);
		resp = apiExecutor.execute(params, null);
		return resp;
	}

	public Pair<List<String>, List<GetFilingFrequencyDto>> getFilingResponse(
			Set<String> requiredGetGstinList, String financialYear) {
		List<GetFilingFrequencyDto> getFilingFreqDtoList = new ArrayList<>();
		Set<String> gstinList = new HashSet<>();
		String groupCode = TenantContext.getTenantId();
		for (String gstin : requiredGetGstinList) {
			try {
				Gson gson = new Gson();
				JsonArray jsonArray = new JsonArray();
				APIResponse apiResponse = getFilingFrequencyResp(groupCode,
						gstin, financialYear);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get Preference apiResponse {} "
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
							if (!dto.getPreference().equalsIgnoreCase("N")) {
								GetFilingFrequencyDto getFilingfrequencyDto = new GetFilingFrequencyDto();
								getFilingfrequencyDto.setGstin(gstin);
								getFilingfrequencyDto
										.setFinancialYear(financialYear);
								getFilingfrequencyDto
										.setQuarter(dto.getQuarter());
								if (dto.getPreference().equalsIgnoreCase("M")) {
									getFilingfrequencyDto
											.setFilingType("Monthly");
								} else if (dto.getPreference()
										.equalsIgnoreCase("Q")) {
									getFilingfrequencyDto
											.setFilingType("Quarterly");
								}
								getFilingFreqDtoList.add(getFilingfrequencyDto);
								gstinList.add(gstin);
							}
						}
					} else {

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Get Prefence Detail failure  block for gstin {} and error is {} ",
									gstin,
									apiResponse.getError().getErrorDesc());
						}
					}
				}
			} catch (Exception ex) {
				LOGGER.error(
						"Exception while processing the vendor filing type request {} "
								+ "groupcode {} for gstin {} and financialYear{}",
						ex, groupCode, gstin, financialYear);

			}
		}
		return new Pair<List<String>, List<GetFilingFrequencyDto>>(
				new ArrayList<>(gstinList), getFilingFreqDtoList);
	}
}
