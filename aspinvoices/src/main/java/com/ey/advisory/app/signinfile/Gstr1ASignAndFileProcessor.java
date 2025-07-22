package com.ey.advisory.app.signinfile;

import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.docs.dto.gstr1.FileGstr1;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.CryptoUtils;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Component("Gstr1ASignAndFileProcessor")
public class Gstr1ASignAndFileProcessor implements TaskProcessor {

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	private GstinAPIAuthInfoRepository gstinAuthInfo;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	private Gstr1ASignAndFileService gstr1ASignAndFileService;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnStatusRepo;

	@Override
	public void execute(Message message, AppExecContext context) {
		String jsonString = message.getParamsJson();
		LOGGER.debug(
				"GSTR1A Sign&File request received for groupcode {} and params {}",
				TenantContext.getTenantId(), jsonString);
		String signId = null;
		try {
			JsonObject requestParams = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			signId = requestParams.get("signId").getAsString();
			String otp = requestParams.has("otp")
					? requestParams.get("otp").getAsString() : null;

			Optional<SignAndFileEntity> signAndFileEntity = signAndFileRepo
					.findById(Long.valueOf(signId));
			String gstin = signAndFileEntity.get().getGstin();
			GstnAPIAuthInfo gstinAuth = gstinAuthInfo
					.findByGstinAndProviderName(gstin, "GSTN");
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			Date gstnTokenExpiryTime = gstinAuth.getGstnTokenExpiryTime();
			LocalDateTime utcgstnTokenExpiryTime = EYDateUtil
					.toUTCDateTimeFromIST(gstnTokenExpiryTime);
			if (now.compareTo(utcgstnTokenExpiryTime) > 0) {
				String errMsg = String.format(
						"AuthToken has been expired for Sign&File ID %s for GSTIN %s",
						signId, gstin);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			String taxPeriod = signAndFileEntity.get().getTaxPeriod();
			Clob payload = signAndFileEntity.get().getPayload();
			String payloadString = GenUtil.convertClobtoString(payload);
			String data = null;
			if ("EVC".equalsIgnoreCase(
					signAndFileEntity.get().getFilingMode())) {
				data = prepareSignFilePayloadForEvc(signAndFileEntity.get(),
						payloadString, gstinAuth.getSessionKey(), otp);
			} else {
				data = prepareSignFilePayload(signAndFileEntity.get(),
						payloadString, gstinAuth.getSessionKey());
			}
			signAndFileEntity.get()
					.setBase64Signed(GenUtil.convertStringToClob(data));
			signAndFileEntity.get().setModifiedOn(LocalDateTime.now());
			signAndFileRepo.save(signAndFileEntity.get());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GSTR1A Sign&File GSTN payload about to send is {}",
						data);
			}
			APIResponse gstr1FileApiResp = hitGstnServer.gstr1AFileApiCall(
					TenantContext.getTenantId(), data, gstin, taxPeriod);
			if (gstr1FileApiResp.isSuccess()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("SignAndFlie Sandbox api success {} ",
							gstr1FileApiResp.getResponse());
				}
				JsonObject responseParams = (new JsonParser())
						.parse(gstr1FileApiResp.getResponse())
						.getAsJsonObject();

				String ackNum = responseParams.get("ack_num").getAsString();
				gstr1ASignAndFileService.updateGstr1ATables(taxPeriod, gstin,
						signId, ackNum, now);

			} else {
				LOGGER.error("GSTR1A Sign&File Failed with response {} ",
						gstr1FileApiResp.getError());
				signAndFileRepo.updateStatus(null, "Failed",
						gstr1FileApiResp.getError().getErrorDesc(),
						Long.valueOf(signId));
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while GSTR1A Sign&File, jsonParams are '%s'",
					jsonString);
			String errorMsg = "Application Error";
			LOGGER.error(msg, ex);
			signAndFileRepo.updateStatus(null, "Failed", errorMsg,
					Long.valueOf(signId));
			throw new AppException(msg, ex);
		}
	}

	private String prepareSignFilePayload(SignAndFileEntity signAndFileEntity,
			String payload, String sk) {
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		FileGstr1 fileGstr1 = new FileGstr1();
		fileGstr1.setSt("DSC");
		fileGstr1.setAction("RETFILER1A");
		fileGstr1.setSid(signAndFileEntity.getPan());
		fileGstr1.setSign(
				GenUtil.convertClobtoString(signAndFileEntity.getSignedData()));
		String encryptedData = CryptoUtils.encryptAPIReqData(payload, sk);
		fileGstr1.setData(encryptedData);
		return gson.toJson(fileGstr1, FileGstr1.class);
	}

	private String prepareSignFilePayloadForEvc(
			SignAndFileEntity signAndFileEntity, String payload, String sk,
			String otp) {
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		FileGstr1 fileGstr1 = new FileGstr1();
		fileGstr1.setSt("EVC");
		fileGstr1.setAction("RETFILER1A");
		fileGstr1.setSid(signAndFileEntity.getPan()+"|"+otp);
		fileGstr1.setSign(
				GenUtil.convertClobtoString(signAndFileEntity.getSignedData()));
		String encryptedData = CryptoUtils.encryptAPIReqData(payload, sk);
		fileGstr1.setData(encryptedData);
		return gson.toJson(fileGstr1, FileGstr1.class);
	}

}
