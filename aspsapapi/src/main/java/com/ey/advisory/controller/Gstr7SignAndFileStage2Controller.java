package com.ey.advisory.controller;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.docs.dto.gstr1.FileGstr1;
import com.ey.advisory.app.services.sign.file.SignAndFileDao;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.CryptoUtils;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@RestController
public class Gstr7SignAndFileStage2Controller {

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	private GstinAPIAuthInfoRepository gstinAuthInfo;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	private GSTNDetailRepository gstinInfoRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepo;

	@Autowired
	@Qualifier("SignAndFileDaoImpl")
	private SignAndFileDao signAndFileDao;

	@PostMapping(value = "/signreturn/gstr7ExecSignAndFileStage2", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> execSignAndFileStage2(
			@PathVariable("username") String username,
			@PathVariable("appKey") Long appKey,
			@PathVariable("signedData") String signedData) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstr7ExecSignAndFileStage2 Request received from "
					+ "execSignAndFileStage1 as {} ", username);
		}
		// appKey is mandatory.
		if (appKey != null) {
			return null;
		}
		String groupCode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupCode);
		Optional<SignAndFileEntity> opEntity = signAndFileRepo.findById(appKey);
		SignAndFileEntity signAndFileEntity = null;
		if (opEntity.isPresent()) {
			signAndFileEntity = opEntity.get();
		}
		if (signAndFileEntity == null) {
			return null;
		}
		String gstin = signAndFileEntity.getGstin();
		String taxPeriod = signAndFileEntity.getTaxPeriod();
		Clob payload = signAndFileEntity.getPayload();

		GstnAPIAuthInfo gstinAuth = gstinAuthInfo
				.findByGstinAndProviderName(gstin, "GSTN");

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		// Expire time need to check
		Date gstnTokenExpiryTime = gstinAuth.getGstnTokenExpiryTime();
		LocalDateTime utcgstnTokenExpiryTime = EYDateUtil
				.toUTCDateTimeFromIST(gstnTokenExpiryTime);

		// SessionTime/Auth-token has expired.
		if (now.compareTo(utcgstnTokenExpiryTime) > 0) {
			return null;
		}
		String encryptedSignedData = CryptoUtils.encryptAPIReqData(
				payload.toString(), gstinAuth.getSessionKey());

		GSTNDetailEntity gstinInfo = gstinInfoRepo
				.findByGstinAndIsDeleteFalse(gstin);
		EntityInfoEntity entityInfo = null;
		if (gstinInfo != null && gstinInfo.getEntityId() != null) {
			Optional<EntityInfoEntity> opEntityInfo = entityInfoRepo
					.findById(gstinInfo.getEntityId());
			if (opEntityInfo.isPresent()) {
				entityInfo = opEntityInfo.get();
			}
		}
		if (entityInfo == null) {
			return null;
		}
		String pan = entityInfo.getPan();
		// PAN is mandatory.
		if (pan == null) {
			return null;
		}
		signAndFileEntity
				.setSignedData(GenUtil.convertStringToClob(signedData));
		signAndFileEntity.setBase64Signed(
				GenUtil.convertStringToClob(encryptedSignedData));
		signAndFileEntity.setPan(pan);
		signAndFileRepo.save(signAndFileEntity);

		Gson gson = GsonUtil.newSAPGsonInstance();
		FileGstr1 fileGstr1 = new FileGstr1();
		fileGstr1.setSt("DSC");
		fileGstr1.setAction("RETFILE");
		// PAN of GSTIN
		fileGstr1.setSid(pan);
		// Encrypted Sign data
		fileGstr1.setData(encryptedSignedData);
		// User given Sign data
		fileGstr1.setSign(signedData);

		String data = gson.toJson(fileGstr1, FileGstr1.class);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SignAndFie final json data {} ", data);
		}

		APIResponse gstr7FileApiResp = hitGstnServer.gstr7FileApiCall(groupCode,
				data, gstin, taxPeriod);

		if (gstr7FileApiResp.isSuccess()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("SignAndFie Sandbox api success {} ",
						gstr7FileApiResp.getResponse());
			}
			String response = gstr7FileApiResp.getResponse();

			signAndFileDao.updateGstr1Tables(taxPeriod, gstin, groupCode);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(response);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("SignAndFie Sandbox api Failed {} ",
						gstr7FileApiResp.getResponse());
			}
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson
					.toJsonTree(gstr7FileApiResp.getError().getErrorDesc());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
