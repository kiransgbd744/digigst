package com.ey.advisory.controllers.vendorcommunication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.asprecon.VendorComplianceRatingAsyncApiResponseEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterApiEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendoComplianceAsyncApiRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterApiEntityRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorAsyncApiGstinDto;
import com.ey.advisory.app.data.services.compliancerating.VendorAsyncApiRequestDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@RestController
@Slf4j
public class VendorComplianceRatingAsyncApiController {
	private static final String FAILED = "Failed";

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Autowired
	@Qualifier("VendoComplianceAsyncApiRepository")
	private VendoComplianceAsyncApiRepository repo;

	@Autowired
	@Qualifier("VendorMasterApiEntityRepository")
	private VendorMasterApiEntityRepository vendorMaserApiRepo;

	@Autowired
	private EntityInfoRepository entityInfoRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@PostMapping(value = "/api/initiateGetVendorAsyncApi", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorsReturnFileStatus(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			User user = SecurityContext.getUser();
			String userPrincipalName = user.getUserPrincipalName();
			JsonObject obj = JsonParser.parseString(jsonString).getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();

			VendorAsyncApiRequestDto dto = gson.fromJson(reqJson,
					VendorAsyncApiRequestDto.class);

			String requestPayload = gson.toJson(dto);
			JsonObject resps = new JsonObject();
			if (Strings.isNullOrEmpty(dto.getFy())
					|| Strings.isNullOrEmpty(dto.getPan())) {
				resps.addProperty("errorMessage",
						"Financial Year or Pan is Empty");
			}
			List<VendorAsyncApiGstinDto> vendorDto = dto.getGstins();
			for (VendorAsyncApiGstinDto vendor : vendorDto) {
				if (Strings.isNullOrEmpty(vendor.getVendorGstin())) {
					resps.addProperty("errorMessage",
							"Vendor Gstin is Null or Empty");
				}
				if (Strings.isNullOrEmpty(vendor.getVendorName())) {
					resps.addProperty("errorMessage",
							"Vendor Name is Null or Empty");
				}
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("insithe the method getVendorsReturnFileStatus "
						+ " of controller VendorComplianceRatingAsyncApiController ");

			Long entityId = entityInfoRepo.findActiveIdByPan(dto.getPan());
			if (entityId == null) {
				resps.addProperty("errorMessage", "Vendor Pan is Invalid");
			} else {
				String userName = SecurityContext.getUser()
						.getUserPrincipalName();
				UUID uuid = UUID.randomUUID();
				VendorComplianceRatingAsyncApiResponseEntity entity = new VendorComplianceRatingAsyncApiResponseEntity();
				entity.setReferenceId(uuid.toString());
				entity.setEntityId(entityId);
				entity.setCreatedBy(userPrincipalName);
				entity.setCreatedOn(LocalDateTime.now());
				entity.setRequestPayload(
						GenUtil.convertStringToClob(requestPayload));
				entity.setGstinCount(dto.getGstins().size());
				entity.setReportCategory("GSTR1,GSTR3B");
				entity.setDataType("All");
				entity.setTableType("VENDOR");
				entity.setFy(dto.getFy());
				entity.setStatus("Initiated");
				entity.setUploadMode("API");
				entity = repo.save(entity);
				Long id = entity.getId();
				List<VendorMasterApiEntity> apiEntities = new ArrayList<>();
				List<String> availableGstins = new ArrayList<>();
				for (VendorAsyncApiGstinDto apiDto : vendorDto) {
					if (apiDto.getVendorGstin() != null)
						availableGstins.add(apiDto.getVendorGstin());
				}
				vendorMaserApiRepo.updateisDeleteBeforePersist(availableGstins);
				for (VendorAsyncApiGstinDto apiDto : vendorDto) {
					VendorMasterApiEntity apiEntity = new VendorMasterApiEntity();
					apiEntity.setEntityId(entityId);
					apiEntity.setRecipientPAN(dto.getPan());
					apiEntity.setVendorGstin(apiDto.getVendorGstin());
					apiEntity.setCreatedBy(userPrincipalName);
					apiEntity.setVendorName(apiDto.getVendorName());
					if (apiDto.getVendorGstin().length() > 12) {
						apiEntity.setVendorPAN(
								apiDto.getVendorGstin().substring(2, 12));
					}
					apiEntity.setIsActive(true);
					apiEntity.setIsDelete(false);
					apiEntity.setCreatedOn(LocalDateTime.now());
					apiEntities.add(apiEntity);
				}
				vendorMaserApiRepo.saveAll(apiEntities);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Successfully saved to DB with Report Id : %d", id);
					LOGGER.debug(msg);
				}
				JsonObject jobParams = new JsonObject();
				jobParams.addProperty("id", id);

				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.InitiateGetVendorFilingApi,
						jobParams.toString(), userName, 5L, 0L, 0L);

				resps.addProperty("refId", uuid.toString());
			}
			JsonElement respBody = gson.toJsonTree(resps);
			resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			resp.add("resp", respBody);
			LOGGER.error("exception while intiating vendor api: ", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}
