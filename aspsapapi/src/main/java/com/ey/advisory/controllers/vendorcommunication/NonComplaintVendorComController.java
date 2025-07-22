package com.ey.advisory.controllers.vendorcommunication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.asprecon.NonCompVendorRequestEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.NonCompVendorRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.NonCompVendorVGstinRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.NonComplaintVendorCommunicationService;
import com.ey.advisory.app.itcmatching.vendorupload.VendorGstinDto;
import com.ey.advisory.app.services.vendorcomm.NonComplaintVendorComService;
import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.app.vendorcomm.dto.NonCompVendorComReportDto;
import com.ey.advisory.app.vendorcomm.dto.NonCompVendorRequestDto;
import com.ey.advisory.app.vendorcomm.dto.VendorEmailCommDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@RestController
@Slf4j
public class NonComplaintVendorComController {

	@Autowired
	@Qualifier("NonComplaintVendorComServiceImpl")
	private NonComplaintVendorComService nonComplaintVendorComService;

	@Autowired
	@Qualifier("NonComplaintVendorCommunicationServiceImpl")
	private NonComplaintVendorCommunicationService nonComplaintVendorCommunicationServiceImpl;

	@Autowired
	private NonCompVendorRequestRepository nonCompVendorRequestRepository;
	
	@Autowired
	private NonCompVendorVGstinRepository nonCompVendorVGstinRepository;

	private static final String FAILED = "Failed";
	private static final String ENTITY_ID = "entityId";
	private static final String REPORT_TYPE = "Non-Complaint";
	private static final String VENDOR_GSTIN_LIST_NOT_FOUND = "Vendor gstin list not found";

	@PostMapping(value = "/ui/getNonCompComVPan", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNonCompComVPan(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			Long entityId = json.get(ENTITY_ID).getAsLong();

			List<GstinDto> listOfVendorPans = nonComplaintVendorComService
					.getListOfVendorPans(entityId);

			if (!CollectionUtils.isEmpty(listOfVendorPans)) {
				String jsonEINV = gson.toJson(listOfVendorPans);
				JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("vendorPans", einvJsonElement);
				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(jsonObject);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Vendor PAN list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getNonCompComVGstin", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNonCompComVGstin(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray vendorPans = new JsonArray();
		Gson googleJson = new Gson();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			Long entityId = requestObject.get("entityId").getAsLong();
			vendorPans = requestObject.getAsJsonArray("vendorPan");
			List<String> listOfVendorPans;
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			listOfVendorPans = googleJson.fromJson(vendorPans, listType);
			List<GstinDto> vendorGstinList = nonComplaintVendorComService
					.getListOfNonComplaintComVendorGstin(listOfVendorPans,
							entityId);
			if (!CollectionUtils.isEmpty(vendorGstinList)) {
				String jsonEINV = gson.toJson(vendorGstinList);
				JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("vendorGstins", einvJsonElement);
				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(jsonObject);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);

			}

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Vendor Gstin list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getNonCompVName", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNonComplaintVendorName(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray vendorGstinArray = new JsonArray();
		Gson googleJson = new Gson();
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			List<String> listOfVendorGstIn;
			Long entityId = requestObject.get("entityId").getAsLong();
			vendorGstinArray = requestObject.getAsJsonArray("vendorGstIn");
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfVendorGstIn = googleJson.fromJson(vendorGstinArray, listType);

			List<VendorGstinDto> vendorGstinDtoList = nonComplaintVendorComService
					.getVendorNameForGstin(listOfVendorGstIn, entityId);

			if (!CollectionUtils.isEmpty(vendorGstinDtoList)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(vendorGstinDtoList);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
			throw new AppException(VENDOR_GSTIN_LIST_NOT_FOUND);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Vendor name";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getNonCompVendorCode", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNonCompVendorCode(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray vendorNameArray = new JsonArray();
		JsonArray vendorGstinArray = new JsonArray();
		Gson googleJson = new Gson();
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			List<String> listOfVendorGstIn;
			List<String> listOfvendorName;
			List<VendorGstinDto> vendorGstinDtoList = new ArrayList<>();
			Long entityId = requestObject.get("entityId").getAsLong();
			vendorNameArray = requestObject.getAsJsonArray("vendorName");
			vendorGstinArray = requestObject.getAsJsonArray("vendorGstIn");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfvendorName = googleJson.fromJson(vendorNameArray, listType);
			listOfVendorGstIn = googleJson.fromJson(vendorGstinArray, listType);

			List<String> vendorCodeList = nonComplaintVendorComService
					.getListOfVendorCode(listOfvendorName, listOfVendorGstIn,
							entityId);

			vendorCodeList.forEach(eachObje -> {
				VendorGstinDto vendorGstinDto = new VendorGstinDto();
				vendorGstinDto.setVendorCode(eachObje);
				vendorGstinDtoList.add(vendorGstinDto);
			});

			if (!CollectionUtils.isEmpty(vendorGstinDtoList)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(vendorGstinDtoList);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
			throw new AppException(VENDOR_GSTIN_LIST_NOT_FOUND);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while vendor code";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/generateNonCompVendorReq", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateNonCompVendorReq(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"generateNonCompVendorReq Request received from UI as {} ",
						jsonString);
			}

			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();

			NonCompVendorComReportDto nonCompVendorComReportDto = gson
					.fromJson(reqJson, NonCompVendorComReportDto.class);
			String finYear = nonCompVendorComReportDto.getFinancialYear();
			String entityId = nonCompVendorComReportDto.getEntityId();
			List<String> vendorGstinsList = new ArrayList<>();
			if (entityId.isEmpty()) {
				throw new AppException("EntityID is empty");
			}
			if (!CollectionUtils
					.isEmpty(nonCompVendorComReportDto.getVendorGstins())) {
				vendorGstinsList = nonCompVendorComReportDto.getVendorGstins()
						.stream().map(GstinDto::getGstin)
						.collect(Collectors.toList());
			}

			Map<String, Set<String>> nonCompVendorGstins = nonComplaintVendorCommunicationServiceImpl
					.getNonComplaintComVendorGstins(finYear,
							vendorGstinsList, Long.valueOf(entityId), REPORT_TYPE);

			Long requestID = nonComplaintVendorComService
					.createEntryNonComplaintVendorComReq(
							Long.valueOf(nonCompVendorGstins.size()),
							finYear,  Long.valueOf(entityId));

			if (nonCompVendorGstins.size() > 0) {
				nonCompVendorGstins
						.forEach((vendorGstin, returnType) -> nonComplaintVendorComService
								.createEntryNonCompVendorReqVGstin(requestID,
										vendorGstin, returnType));
				nonComplaintVendorComService
						.generateNonCompVendorReportUploadAsync(requestID);
			}
			LOGGER.info(
					"Non Complaint Vendor Report Request has been submitted & RequestId is {}",
					requestID);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp",
					gson.toJsonTree(
							"NonComplaint Vendor Report Request has been submitted & RequestId is "
									+ requestID));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Message", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception on NonComplaint Vendor Report Request "
									+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
	@PostMapping(value = "/ui/getNonCompVendorReqDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNonCompVendorReqDetails() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			List<NonCompVendorRequestEntity> NonCompVendorComReqList = nonComplaintVendorComService
					.getNonCompVendorCommDataByUserName(userName);
			List<NonCompVendorRequestDto> nonCompVendorCommDataList = nonComplaintVendorComService
					.getNonCompVendorCommResponse(NonCompVendorComReqList);
			Collections.sort(nonCompVendorCommDataList,
					new Comparator<NonCompVendorRequestDto>() {
						public int compare(NonCompVendorRequestDto o1,
								NonCompVendorRequestDto o2) {
							return (int) (o2.getRequestId()
									- o1.getRequestId());
						}
					});
			String jsonVendorData = gson.toJson(nonCompVendorCommDataList);
			JsonElement vendorJsonElement = new JsonParser()
					.parse(jsonVendorData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("NonCompVendorData", vendorJsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception while Processing the getNonCompVendorReqDetails request "
									+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
	
	@GetMapping(value = "/ui/downloadNonCompVendorReport")
	public void downloadNonCompVendorReportUI(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Long requestId = Long.valueOf(request.getParameter("requestId"));

		downloadVendorReport(response, requestId);

	}
	
	private void downloadVendorReport(HttpServletResponse response,
			Long requestId) throws Exception {
		LOGGER.debug("inside Async Report file Downloads");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileName = nonCompVendorRequestRepository.getFilePath(requestId);

		String fileFolder = "NonCompVendorCommReport";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Downloading Document with fileName : %s and Folder Name: %s",
					fileName, fileFolder);
			LOGGER.debug(msg);
		}
		Document document = DocumentUtility.downloadDocument(fileName,
				fileFolder);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Document to download : %s", document);
			LOGGER.debug(msg);
		}
		if (document == null) {
			return;
		}
		InputStream inputStream = document.getContentStream().getStream();
		int read = 0;
		byte[] bytes = new byte[1024];

		if (document != null) {
			response.setHeader("Content-Disposition",
					String.format("attachment; filename = " + fileName));
			OutputStream outputStream = response.getOutputStream();
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}
	}
	
	@GetMapping(value = "/vendorApi/downloadNonComplaintReport")
	public byte[] downloadVendorReportStream(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Long requestId = Long.valueOf(request.getHeader("requestId"));
		String vendorGstin = request.getHeader("vendorGstin");

		String fileName = nonCompVendorVGstinRepository.getFilePath(requestId,
				vendorGstin);

		String fileFolder = "NonCompVendorCommReport";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Downloading Document with fileName : %s and Folder Name: %s",
					fileName, fileFolder);
			LOGGER.debug(msg);
		}
		Document document = DocumentUtility.downloadDocument(fileName,
				fileFolder);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Document to download : %s", document);
			LOGGER.debug(msg);
		}
		if (document == null) {
			return null;
		}

		InputStream inputStream = document.getContentStream().getStream();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		int read = 0;
		byte[] bytes = new byte[1024];

		if (document != null) {
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}
		return outputStream.toByteArray();
	}

	@PostMapping(value = "/ui/getNonCompVendorEmailCommData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNonCompVendorEmailCommData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"getNonCompVendorEmailCommData Request received from UI as {} ",
						jsonString);
			}

			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = reqObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			Long requestID = reqJson.get("requestID").getAsLong();
			Long entityId = reqJson.get("entityId").getAsLong();

			Pair<List<VendorEmailCommDto>, Integer> vendorEmailCommDtoListPair = nonComplaintVendorComService
					.getNonCompEmailCommunicationDetails(requestID, entityId, pageSize,
							pageNum);
			List<VendorEmailCommDto> vendorEmailCommDtoList = vendorEmailCommDtoListPair
					.getValue0();
			Collections.sort(vendorEmailCommDtoList,
					new Comparator<VendorEmailCommDto>() {
						public int compare(VendorEmailCommDto o1,
								VendorEmailCommDto o2) {
							return (o1.getVendorGstin()
									.compareTo(o2.getVendorGstin()));
						}
					});

			String jsonEINV = gson
					.toJson(vendorEmailCommDtoList);
			JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
					vendorEmailCommDtoListPair.getValue1(), pageNum, pageSize,
					"S",
					"Successfully fetched NonComplaint Vendor email Communication details")));
			resp.add("resp", einvJsonElement);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Message", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(
					"Exception on NonComplaint Vendor email Communication details Request " + ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
	
	@PostMapping(value = "/ui/postNonCompVEmailCommDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> postNonCompVEmailCommDetails(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"postNonCompVEmailCommDetails Request received from UI as {} ",
						jsonString);
			}

			String res = nonComplaintVendorComService
					.createReqPayloadForEmail(jsonString);
			if (res.equals("SUCCESS")) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp",
						gson.toJsonTree("Vendor's Mail has been sent"));
			} else {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(res));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Message", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree("Exception on NonComp Vendor Email Communication "
							+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

}
