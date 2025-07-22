package com.ey.advisory.controllers.vendorcommunication;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetGstinVendorMasterDetailRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.itcmatching.vendorupload.GetGstinVendorMasterDetailEntity;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran s
 *
 */
@RestController
@Slf4j
public class VendorGstnInitiateGetCallController {

	private static final String FAILED = "Failed";
	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("GetGstinVendorMasterDetailRepository")
	private GetGstinVendorMasterDetailRepository getGstinVendorMasterDetailRepository;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("VendorMasterUploadEntityRepository")
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	
	 /*@Autowired 
	 private InitiateGetVendorGstnDetailsProcessorTestingg  initiateGetVendorGstnDetailsProcessorTesting;*/
	 
	@PostMapping(value = "/ui/initateGetCallForVendorGstnDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorsGstinDetails(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		List<String> vendorGstins = new ArrayList<>();
		// JsonObject resp = new JsonObject();
		try {
			JsonObject request = JsonParser.parseString(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			Long entityId = request.get("entityId").getAsLong();
			List<String> vendorGstinList = new ArrayList<>();
			if (request.has("vendorGstins")) {
				JsonArray vendorGstinArray = request
						.getAsJsonArray("vendorGstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();

				vendorGstinList = gson.fromJson(vendorGstinArray, listType);
			}
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("insidhe the method initateGetVendorGstnDetails "
						+ " of controller VendorMasterController ");
			}
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("entityId",
					entityId);
			String lastGetCallStatus = "In Progress";
			if (!vendorGstinList.isEmpty()) {
				jsonParams.add("vendorGstins",
						gson.toJsonTree(vendorGstinList).getAsJsonArray());
				changeTheStatusOfLastGetCall(vendorGstinList, userName,entityId);
				LocalDateTime thresholdDate = LocalDateTime.now();
				getGstinVendorMasterDetailRepository
				.updatingInitiateGetCallStatus(vendorGstinList,
						lastGetCallStatus,entityId);
			} else {
				List<String> recipientPanList = entityInfoRepository
						.findPanByEntityId(entityId);
				List<VendorMasterUploadEntity> masterEntities = vendorMasterUploadEntityRepository
						.findByIsDeleteFalseAndRecipientPANIn(recipientPanList);
				vendorGstins.addAll(masterEntities.stream()
						.map(VendorMasterUploadEntity::getVendorGstin)
						.distinct()
						.collect(Collectors.toList()));
				if (vendorGstins != null && !vendorGstins.isEmpty()) {
					changeTheStatusOfLastGetCall(vendorGstins, userName,entityId);
					LocalDateTime thresholdDate = LocalDateTime.now();
					getGstinVendorMasterDetailRepository
					.updatingInitiateGetCallStatus(vendorGstins,
							lastGetCallStatus,entityId);

				}
			}
			
			/*  //for testing
			 initiateGetVendorGstnDetailsProcessorTesting.execute(null, null);*/
			 
			if ((vendorGstinList != null && !vendorGstinList.isEmpty()) || (vendorGstins!=null && !vendorGstins.isEmpty()) ) {
				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.INITIATE_GET_VENDOR_GSTN_DETAIL,
						jsonParams.toString(), userName, 5L, 0L, 0L);

				JsonObject hdr = new JsonObject();
				String message = String.format(
						"Get call initiated successfully for vendor GSTINs and "
								+ "status of each vendor GSTIN will keep updating on screen "
								+ "as and when get call is completed for that particular GSTIN. "
								+ "Kindly check after sometime");
				hdr.addProperty("status", "S");
				hdr.addProperty("message", message);

				resp.add("hdr", gson.toJsonTree(hdr));
			}
			else{
				JsonObject hdr = new JsonObject();
				String message = String.format(
						"There is no vendor GSTIN to initiate the Get Call");
				hdr.addProperty("status", "E");
				hdr.addProperty("message", message);

				resp.add("hdr", gson.toJsonTree(hdr));
			}
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());

			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			resp.add("resp", respBody);
			LOGGER.error(
					"exception while intiating vendor initiage get call status: ",
					e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * @param vendorGstinList
	 * @param userName
	 */
	public void changeTheStatusOfLastGetCall(List<String> vendorGstinList,
			String userName,Long entityId) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter1 = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formattedDateTime = currentDateTime
				.format(formatter1);
		LocalDateTime parsedDateTime = LocalDateTime
				.parse(formattedDateTime, formatter1);
		List<Object[]> vendorGstinNameList = vendorMasterUploadEntityRepository
				.getVendorGstinAndVendorName(vendorGstinList);

		Map<String, String> vendorGstinNameMap = new HashMap<>();

		for (Object[] result : vendorGstinNameList) {
			String vendorGstin = (String) result[0];
			String vendorName = (String) result[1];

			vendorGstinNameMap.put(vendorGstin, vendorName);
		}

		final List<String>[] vendorGstinListArray = new List[] {
				vendorGstinList };
		List<GetGstinVendorMasterDetailEntity> findByVendorGstinIn = getGstinVendorMasterDetailRepository
				.findByVendorGstinIn(vendorGstinList,entityId);

		Set<String> findByVendorGstinSet = findByVendorGstinIn.stream()
				.map(GetGstinVendorMasterDetailEntity::getVendorGstin)
				.collect(Collectors.toSet());

		// Find values in vendorGstinListArray that are not in
		// findByVendorGstinSet
		List<String> notInFindByVendorGstin = Arrays
				.stream(vendorGstinListArray)
				.flatMap(Collection::stream)
				.filter(gstin -> !findByVendorGstinSet.contains(gstin))
				.collect(Collectors.toList());

		List<GetGstinVendorMasterDetailEntity> newEntityList = notInFindByVendorGstin
				.stream()
				.map(gstin -> {
					GetGstinVendorMasterDetailEntity newEntity = new GetGstinVendorMasterDetailEntity();
					newEntity.setVendorGstin(gstin);
					// Set other properties as needed
					// newEntity.setVendorNameAsUploaded("some name");
					String vendorName = vendorGstinNameMap.get(gstin);
					newEntity.setVendorNameAsUploaded(vendorName);// Replace
																	// with the
																	// actual
																	// vendor
																	// name
					newEntity.setLastGetCallStatus("In Progress");
					//newEntity.setLastUpdated(parsedDateTime);
					newEntity.setIsDelete(false);
					newEntity.setEntityId(entityId);
					newEntity.setUpdatedBy(userName);
					return newEntity;
				})
				.collect(Collectors.toList());

		getGstinVendorMasterDetailRepository.saveAll(newEntityList);
	}

}
