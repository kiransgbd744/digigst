package com.ey.advisory.controllers.vendorcommunication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.asprecon.VendorApiUploadStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterErrorReportEntityRepository;
import com.ey.advisory.app.itcmatching.vendorupload.ItcMatchingVendorDataFileUpld;
import com.ey.advisory.app.itcmatching.vendorupload.VendorAPIListDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorAPIPushDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorAPIPushListDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorAPIPushStatusDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorApiUploadStatusEntity;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterErrorReportEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@RestController
@Slf4j
public class VendorAPIUploadController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("VendorApiUploadStatusRepository")
	VendorApiUploadStatusRepository apiStatusRepository;

	@Autowired
	@Qualifier("ItcMatchingVendorDataFileUpld")
	private ItcMatchingVendorDataFileUpld vendorDataFileUpload;

	@Autowired
	@Qualifier("VendorMasterErrorReportEntityRepository")
	private VendorMasterErrorReportEntityRepository vendorMasterErrorReportEntityRepository;

	@PostMapping(value = "/api/saveVendorMaster", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveVendorMaster(
			@RequestBody String jsonString) {
		JsonObject requestObject = null;
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		VendorApiUploadStatusEntity apiStatus = null;

		JsonObject json = null;
		JsonElement respBody = null;
		int errorRecords = 0;
		try {

			try {
				requestObject = (new JsonParser()).parse(jsonString)
						.getAsJsonObject();

				/*
				 * if (!requestObject.has("req")) throw new
				 * AppException("Invalid request payload");
				 */

				json = requestObject.get("req").getAsJsonObject();

			} catch (Exception ex) {
				LOGGER.error("Error while parsing the request-{}", jsonString,
						ex);
				throw new AppException("Invalid request payload");
			}

			if (!json.has("vendorData"))
				throw new AppException("Vendor Data is mandatory");
			VendorAPIListDto reqDto = gson.fromJson(json,
					VendorAPIListDto.class);
			if (reqDto.getVendorData() == null
					|| reqDto.getVendorData().isEmpty()) {
				String msg = "Vendor Data cannot be null";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			apiStatus = logRequestPayload(jsonString, true);

			List<VendorAPIPushListDto> vendorData = reqDto.getVendorData();
			String refId = apiStatus.getRefId();

			List<Object[]> req = new ArrayList<>();

			for (VendorAPIPushListDto dto : vendorData) {

				Object[] obj = new Object[28];
				convertDtoToObj(obj, dto);
				req.add(obj);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("List of objects created -{}", req);
			}

			vendorDataFileUpload.validation(req, null, refId, null);

			Optional<VendorApiUploadStatusEntity> errorCount = apiStatusRepository
					.findByRefId(refId);
			if(errorCount.isPresent()){
			   errorRecords = errorCount.get().getError();
			}
			if (errorRecords > 0) {

				List<VendorMasterErrorReportEntity> errorList = vendorMasterErrorReportEntityRepository
						.findByRefId(apiStatus.getRefId());

				List<VendorMasterErrorReportEntity> errors = errorList.stream()
						.filter(eachObject -> Objects
								.nonNull(eachObject.getError())
								&& !Objects.equals("null",
										eachObject.getError()))
						.collect(Collectors.toList());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("error records are --> {}", errors);
				}

				List<VendorAPIPushDto> errorDataList = errors.stream()
						.map(o -> convert(o))
						.collect(Collectors.toCollection(ArrayList::new));
				respBody = gson.toJsonTree(errorDataList);

			} else {
				// List<JsonObject> responseList = new ArrayList<>();
				JsonObject obj = new JsonObject();
				obj.addProperty("refId", apiStatus.getRefId());
				obj.addProperty("msg",
						"Vendor Data has been processed successfully");

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("records has been processed successfully");
				}
				// responseList.add(obj);
				respBody = gson.toJsonTree(Arrays.asList(obj));
			}

			JsonObject resps = new JsonObject();

			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			if (respBody != null)
				resps.add("resp", respBody);
			else
				resps.add("resp", resp);
			
			if(errorCount.isPresent()){
			errorCount.get().setRespPayload(
					GenUtil.convertStringToClob(resps.toString()));
			apiStatusRepository.save(errorCount.get());
			}
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Fetching vendor api Status ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("errMsg", gson.toJsonTree(ex.getMessage()));

			apiStatus = logRequestPayload(jsonString, false);
			/*
			 * UUID uuid = UUID.randomUUID();
			 * apiStatus.setRefId(uuid.toString());
			 * apiStatus.setCreatedBy("API");
			 * apiStatus.setCreatedOn(LocalDateTime.now());
			 * apiStatus.setReqPayload(
			 * GenUtil.convertStringToClob(jsonString.toString()));
			 */

			apiStatus.setError(0);
			apiStatus.setInformation(0);
			apiStatus.setProcessed(0);
			apiStatus.setTotal(0);
			apiStatus.setRespPayload(
					GenUtil.convertStringToClob(resp.toString()));
			apiStatusRepository.save(apiStatus);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/vendorApiStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> vendorApiStatus(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE Vendor API Upload Controller");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			int totalCount = 0;
			Pageable pageReq = PageRequest.of(pageNum, pageSize, Direction.DESC,
					"createdOn");
			List<String> createdBy = new ArrayList<>();
			createdBy.add("API");
			createdBy.add("ERP");
			List<VendorApiUploadStatusEntity> vendorApiUploadEntities = apiStatusRepository
					.findByCreatedByIn(createdBy, pageReq);
			;
			totalCount = apiStatusRepository.getCount();

			Pair<List<VendorApiUploadStatusEntity>, Integer> respList = new Pair<>(
					vendorApiUploadEntities, totalCount);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"vendor list fetched - {} and total count found - {}",
						respList, totalCount);
			}
			List<VendorAPIPushStatusDto> dataList = respList.getValue0()
					.stream().map(o -> convertEntityToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			String responseData = gson.toJson(dataList);

			JsonElement jsonElement = new JsonParser().parse(responseData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("VendorApiPushStatus", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(
							respList.getValue1(), pageNum, pageSize, "S",
							"Successfully fetched Vendor Api Status records")));
			resp.add("resp", jsonElement);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error fetching API Status records";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	private static void convertDtoToObj(Object[] obj,
			VendorAPIPushListDto reqDto) {
		obj[0] = reqDto.getRecipientPAN();
		obj[1] = reqDto.getVendorPAN();
		obj[2] = reqDto.getVendorGstin();

		obj[3] = reqDto.getSupplierCode();
		obj[4] = reqDto.getVendorName();
		obj[5] = reqDto.getVendPrimEmailId();
		obj[6] = reqDto.getVendorContactNumber();
		obj[7] = reqDto.getVendorEmailId1();
		obj[8] = reqDto.getVendorEmailId2();
		obj[9] = reqDto.getVendorEmailId3();
		obj[10] = reqDto.getVendorEmailId4();
		obj[11] = reqDto.getRecipientEmailId1();
		obj[12] = reqDto.getRecipientEmailId2();
		obj[13] = reqDto.getRecipientEmailId3();
		obj[14] = reqDto.getRecipientEmailId4();
		obj[15] = reqDto.getRecipientEmailId5();
		obj[16] = reqDto.getVendorType();
		obj[17] = reqDto.getHsn();
		obj[18] = reqDto.getVendorRiskCategory();
		obj[19] = reqDto.getVendorPaymentTerms();
		obj[20] = reqDto.getVendorRemarks();
		obj[21] = reqDto.getApprovalStatus();
		obj[22] = reqDto.getExcludeVendorRemarks();
		obj[23] = reqDto.getIsVendorCom();
		obj[24] = reqDto.getIsExcludeVendor();
		obj[25] = reqDto.getIsNonComplaintCom();
		obj[26] = reqDto.getIsCreditEligibility();
		obj[27] = reqDto.getIsDelete();
	}

	private VendorAPIPushDto convert(VendorMasterErrorReportEntity entity) {

		VendorAPIPushDto obj = new VendorAPIPushDto();

		obj.setError(entity.getError());
		obj.setRefId(entity.getRefId());
		obj.setRecipientPAN(entity.getRecipientPAN());
		obj.setVendorGstin(entity.getVendorGstin());
		obj.setVendorPAN(entity.getVendorPAN());
		obj.setSupplierCode(entity.getVendorCode());
		obj.setVendorName(entity.getVendorName());
		obj.setVendPrimEmailId(entity.getVendPrimEmailId());
		obj.setVendorContactNumber(entity.getVendorContactNumber());
		obj.setVendorEmailId1(entity.getVendorEmailId1());
		obj.setVendorEmailId2(entity.getVendorEmailId2());
		obj.setVendorEmailId3(entity.getVendorEmailId3());
		obj.setVendorEmailId4(entity.getVendorEmailId4());
		obj.setRecipientEmailId1(entity.getRecipientEmailId1());
		obj.setRecipientEmailId2(entity.getRecipientEmailId2());
		obj.setRecipientEmailId3(entity.getRecipientEmailId3());
		obj.setRecipientEmailId4(entity.getRecipientEmailId4());
		obj.setRecipientEmailId5(entity.getRecipientEmailId5());
		obj.setVendorType(entity.getVendorType());
		obj.setHsn(entity.getHsn());
		obj.setVendorRiskCategory(entity.getVendorRiskCategory());
		obj.setVendorPaymentTerms(entity.getVendorPaymentTerms());
		obj.setVendorRemarks(entity.getVendorRemarks());
		obj.setApprovalStatus(entity.getApprovalStatus());
		obj.setExcludeVendorRemarks(entity.getExcludeVendorRemarks());
		obj.setIsVendorCom(entity.getIsVendorCom());
		obj.setIsExcludeVendor(entity.getIsExcludeVendor());
		obj.setIsNonComplaintCom(entity.getIsNonComplaintCom());
		obj.setIsCreditEligibility(entity.getIsCreditEligibility());
		obj.setIsDelete(entity.getIsDelete());
		return obj;

	}

	private VendorAPIPushStatusDto convertEntityToDto(
			VendorApiUploadStatusEntity entity) {

		VendorAPIPushStatusDto obj = new VendorAPIPushStatusDto();

		obj.setApiPushStatus(entity.getApiStatus());
		if (entity.getCreatedOn() != null)

		{
			obj.setDateOfPush(dateChange(entity.getCreatedOn()).toString());
		} else
			obj.setDateOfPush(entity.getCreatedOn().toString());

		obj.setErrorRecords(entity.getError());
		obj.setInfoRecords(entity.getInformation());
		obj.setProcessedRecords(entity.getProcessed());
		obj.setPushedBy(entity.getCreatedBy());
		obj.setTotalRecords(entity.getTotal());
		obj.setRefId(entity.getRefId());

		return obj;
	}

	public String dateChange(LocalDateTime oldDate) {
		DateTimeFormatter formatter = null;
		String dateTime = oldDate.toString();
		char ch = 'T';
		dateTime = dateTime.substring(0, 10) + ch + dateTime.substring(10 + 1);
		String s1 = dateTime.substring(0, 19);
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

		LocalDateTime dateTimes = LocalDateTime.parse(s1, formatter);

		LocalDateTime dateTimeFormatter = EYDateUtil
				.toISTDateTimeFromUTC(dateTimes);
		DateTimeFormatter FOMATTER = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");
		String newdate = FOMATTER.format(dateTimeFormatter);
		return newdate;

	}

	private VendorApiUploadStatusEntity logRequestPayload(String jsonString,
			boolean value) {
		VendorApiUploadStatusEntity apiStatus = new VendorApiUploadStatusEntity();

		UUID uuid = UUID.randomUUID();
		apiStatus.setRefId(uuid.toString());
		apiStatus.setCreatedBy("API");
		apiStatus.setCreatedOn(LocalDateTime.now());
		apiStatus.setReqPayload(
				GenUtil.convertStringToClob(jsonString.toString()));
		if (value) {
			apiStatus.setApiStatus(JobStatusConstants.UPLOADED);
		} else {
			apiStatus.setApiStatus("FAILED");
		}
		return apiStatusRepository.save(apiStatus);

	}

}
