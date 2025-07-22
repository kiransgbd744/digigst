package com.ey.advisory.controllers.gstr3b;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.services.sign.file.SignAndFileDao;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GSTR3BSignAndFileStage2Controller {

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepo;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	@Qualifier("SignAndFileDaoImpl")
	private SignAndFileDao signAndFileDao;

	@Autowired
	private AsyncJobsService persistenceMngr;

	@PostMapping(value = "/signreturn/GSTR3BSignAndFileStage2", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> execSignAndFileStage2(String username,
			String appkey, String signedData) {
		String groupCode = username;
		TenantContext.setTenantId(groupCode);
		SignAndFileEntity signAndFileEntity = null;
		JsonObject resp = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Received GSTR3B Sign&File Request for ID -{}"
							+ " and SignedData is {}, for Group {}",
					appkey, signedData, groupCode);
		}
		try {
			Optional<SignAndFileEntity> opEntity = signAndFileRepo
					.findById(Long.valueOf(appkey));

			if (opEntity.isPresent()) {
				signAndFileEntity = opEntity.get();
			} else {
				String errMsg = String.format(
						"3B Sign & File is not initiated for id %s", appkey);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			signAndFileEntity
					.setSignedData(GenUtil.convertStringToClob(signedData));
			signAndFileEntity.setModifiedOn(LocalDateTime.now());
			signAndFileEntity.setStatus("In Progress");
			signAndFileRepo.save(signAndFileEntity);
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("signId", Long.valueOf(appkey));
			persistenceMngr.createJob(groupCode, "GSTR3BSignAndFile",
					jsonParams.toString(), "SYSTEM", 50L, null, 0L);
			resp = new JsonObject();
			JsonElement respBody = gson
					.toJsonTree("Signatur Has been generated Successfully");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>("true", HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while 3B Sign&FIle Stage2 ", ex);
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
}