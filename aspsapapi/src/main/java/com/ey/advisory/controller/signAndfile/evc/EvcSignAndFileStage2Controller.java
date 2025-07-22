/**
 * 
 */
package com.ey.advisory.controller.signAndfile.evc;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.CryptoUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi Jain
 *
 */
@Slf4j
@RestController
public class EvcSignAndFileStage2Controller {

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepo;

	@Autowired
	private AsyncJobsService persistenceMngr;

	@PostMapping(value = "/ui/evcSignAndFileStage2", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> execSignAndFileStage2(
			@RequestBody String jsonString) {

		String groupCode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupCode);
		SignAndFileEntity signAndFileEntity = null;

		JsonObject reqObj = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject reqObject = reqObj.get("req").getAsJsonObject();

		JsonObject resp = null;
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			Long signId = reqObject.get("signId").getAsLong();
			String otp = reqObject.get("otp").getAsString();

			Optional<SignAndFileEntity> opEntity = signAndFileRepo
					.findById(Long.valueOf(signId));
			String returnType = null;

			if (opEntity.isPresent()) {
				signAndFileEntity = opEntity.get();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Received {} Sign&File Request for ID -{}"
									+ " and SignedData is {}, for Group {}",
							signAndFileEntity.getReturnType(), signId,
							signAndFileEntity.getSignedData(), groupCode);
				}

			} else {
				String errMsg = String.format(
						"Sign & File is not initiated for id %s", signId);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			returnType = signAndFileEntity.getReturnType();
			String secret = String.format("%s|%s", signAndFileEntity.getPan(),
					otp);
			String message = GenUtil
					.convertClobtoString(signAndFileEntity.getPayload());

			String hash = CryptoUtils.hmacSHA256ForEvc(
					encodeBase64String(message.getBytes("UTF-8")),
					secret.getBytes("UTF-8"));
			signAndFileEntity.setSignedData(GenUtil.convertStringToClob(hash));
			signAndFileEntity.setModifiedOn(LocalDateTime.now());
			signAndFileEntity.setStatus("In Progress");
			signAndFileRepo.save(signAndFileEntity);
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("signId", Long.valueOf(signId));
			jsonParams.addProperty("otp", otp);

			String processorString = null;
			switch (returnType) {
			case "GSTR1":
				processorString = "GSTR1SignAndFile";
				break;
			case "GSTR1A":
				processorString = "GSTR1ASignAndFile";
				break;
			case "GSTR3B":
				processorString = "GSTR3BSignAndFile";
				break;
			case "GSTR6":
				processorString = "GSTR6SignAndFile";
				break;
			case "ITC04":
				processorString = "ITC04SignAndFile";
				break;
			case "DRC01B":
				processorString = "DRC01BSignAndFile";
				break;
				
			case "DRC01C":
				processorString = "DRC01CSignAndFile";
				break;

			}
			persistenceMngr.createJob(groupCode, processorString,
					jsonParams.toString(), "SYSTEM", 50L, null, 0L);

			resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(
					"Return Filing has been initiated successfully.");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>("true", HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Sign&FIle Stage2 ", ex);
			String errorMsg = "Application Error";
			if (signAndFileEntity != null) {
				signAndFileEntity.setModifiedOn(LocalDateTime.now());
				signAndFileEntity.setErrorMsg(errorMsg);
				signAndFileEntity.setStatus("Failed");
				signAndFileRepo.save(signAndFileEntity);
			}
			resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			TenantContext.clearTenant();
		}
	}

	private static final Base64.Encoder BASE64ENCODER = Base64.getEncoder();

	private static String encodeBase64String(byte[] bytes) {
		return new String(BASE64ENCODER.encode(bytes));
	}

}
