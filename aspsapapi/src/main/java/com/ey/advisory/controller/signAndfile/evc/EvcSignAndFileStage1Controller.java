package com.ey.advisory.controller.signAndfile.evc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.Base64;

import jakarta.servlet.http.HttpServletResponse;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.data.services.drc.DRC01BGetInvoicesReqDto;
import com.ey.advisory.app.data.services.drc.DRC01BSignAndFileService;
import com.ey.advisory.app.data.services.drc01c.DRC01CSignAndFileService;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.docs.dto.gstr1.SignAndFileReqDto;
import com.ey.advisory.app.gstr3b.Gstr3BSignAndFileService;
import com.ey.advisory.app.itc04.Itc04SignAndFileService;
import com.ey.advisory.app.services.jobs.anx2.Gstr7SummaryAtGstnImpl;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryAtGstn;
import com.ey.advisory.app.signinfile.Gstr1ASignAndFileServiceImpl;
import com.ey.advisory.app.signinfile.Gstr1SignAndFileServiceImpl;
import com.ey.advisory.app.signinfile.Gstr6SignAndFileServiceImpl;
import com.ey.advisory.app.signinfile.evc.EVCSignAndFileService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr3bGetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.repositories.client.GstnAPIGstinConfigRepository;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi Jain
 *
 */
@Slf4j
@RestController
public class EvcSignAndFileStage1Controller {

	@Autowired
	@Qualifier("gstr1SummaryAtGstnImpl")
	private Gstr1SummaryAtGstn gstnGetSummary;

	@Autowired
	@Qualifier("EVCSignAndFileServiceImpl")
	private EVCSignAndFileService gstnGetOtp;

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	private GstnAPIGstinConfigRepository gstinConfigRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("DefaultGSTNAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private Gstr3BSignAndFileService gstr3BSignAndFileService;

	@Autowired
	private Gstr1SignAndFileServiceImpl Gstr1SignAndFileService;
	
	@Autowired
	private Gstr1ASignAndFileServiceImpl Gstr1ASignAndFileService;

	@Autowired
	private Gstr6SignAndFileServiceImpl gstr6SignAndFileService;

	@Autowired
	@Qualifier("Itc04SignAndFileServiceImpl")
	private Itc04SignAndFileService itc04SignAndFileService;

	@Autowired
	@Qualifier("Gstr7SummaryAtGstnImpl")
	private Gstr7SummaryAtGstnImpl gstnGetSummaryGSTR7;
	
	@Autowired
	private DRC01BSignAndFileService drc01BSignAndFileService;
	
	@Autowired
	private DRC01CSignAndFileService drc01CSignAndFileService;

	@PostMapping(value = "/ui/evcSignAndFileStage1", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> execSignAndFileStage1(
			@RequestBody String jsonParam, HttpServletResponse response)
			throws IOException {

		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr1GetInvoicesReqDto summaryDto = null;
		SignAndFileReqDto dto = null;
		String returnType = null;
		SignAndFileEntity entity = new SignAndFileEntity();
		try {
			String isNil = "";
			JsonObject requestObject = JsonParser.parseString(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			if (reqObject.has("isNil")) {
			    isNil = reqObject.get("isNil").getAsString();
			    
			}
			dto = gson.fromJson(reqObject, SignAndFileReqDto.class);

			summaryDto = new Gstr1GetInvoicesReqDto();
			summaryDto.setGstin(dto.getGstin());
			summaryDto.setReturnPeriod(dto.getTaxPeriod());
			summaryDto.setRefId(dto.getRefId());

			switch (dto.getReturnType().toLowerCase()) {
			case "gstr1":
				returnType = APIConstants.GSTR1.toUpperCase();
				break;
			case "gstr3b":
				returnType = APIConstants.GSTR3B;
				break;
			case "gstr6":
				returnType = APIConstants.GSTR6.toUpperCase();
				break;
			case "itc04":
				returnType = APIConstants.ITC04.toUpperCase();
				break;
			case "gstr7":
				returnType = APIConstants.GSTR7;
				break;
			
			case "gstr1a":
				returnType = "GSTR1A";
				break;
				
			case "drc01b":
				returnType = APIConstants.DRC01B.toUpperCase();
					if(Strings.isNullOrEmpty(dto.getRefId())) {
					
					JsonObject resp = new JsonObject();
					APIRespDto apiResp = APIRespDto.creatErrorResp();
					resp.add("hdr", gson.toJsonTree(apiResp));
					resp.add("resp", gson.toJsonTree(
							"Error: Sign and File cannot be initiated without Ref ID."));
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}
				break;
				
			case "drc01c":
				returnType = APIConstants.DRC01C.toUpperCase();
					if(Strings.isNullOrEmpty(dto.getRefId())) {
					
					JsonObject resp = new JsonObject();
					APIRespDto apiResp = APIRespDto.creatErrorResp();
					resp.add("hdr", gson.toJsonTree(apiResp));
					resp.add("resp", gson.toJsonTree(
							"Error: Sign and File cannot be initiated without Ref ID."));
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}
				break;
				
			default:
				throw new AppException(String.format(
						"Return Type - %s  is currently not supported",
						dto.getReturnType()));
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"{} execSignAndFileStage1 EVC Request received from UI as {} ",
						returnType, jsonParam);
			}
			APIRespDto apiResp = null;
			Pair<Boolean, String> summaryJsonPair = null;
			if("Y".equalsIgnoreCase(isNil) && dto.getReturnType().equalsIgnoreCase("gstr3b")){
				JsonObject jsonObject = new JsonObject();
				
				jsonObject.addProperty("gstin", dto.getGstin());
				jsonObject.addProperty("ret_period", dto.getTaxPeriod());
				jsonObject.addProperty("isnil", "Y");
				summaryJsonPair = new Pair<Boolean, String>(true, jsonObject.toString());
			} else{
				
				summaryJsonPair = getSummaryForReturnType(
					summaryDto, returnType);
			
			}
			

			boolean isGetGstr1SummSuccess = summaryJsonPair.getValue0();
			if (!isGetGstr1SummSuccess) {

				saveEntryIntoSignTable(dto, null, null, null, returnType,
						summaryJsonPair.getValue1(),"Failed");

				JsonObject resp = new JsonObject();
				apiResp = APIRespDto.creatErrorResp();
				resp.add("hdr", gson.toJsonTree(apiResp));
				resp.add("resp", gson.toJsonTree(summaryJsonPair.getValue1()));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			byte[] inputJsonBytes = null;
			String modifiedSummaryJsonPair ="";
			try {
				if (returnType.equalsIgnoreCase("GSTR6")) {
					modifiedSummaryJsonPair = setOffsetValue(summaryJsonPair.getValue1());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("GSTR6 modified Value is {} ", modifiedSummaryJsonPair);
					}
					inputJsonBytes = Base64.getEncoder().
							encode(modifiedSummaryJsonPair.getBytes("UTF-8"));

				}
				else {
					inputJsonBytes = Base64.getEncoder()
							.encode(summaryJsonPair.getValue1().getBytes("UTF-8"));
				}
			} catch (UnsupportedEncodingException ex) {
				String msg = String.format(
						"Exception while encrypting the GET %s summary data",
						returnType);
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}

			String sha256hex = Hashing.sha256()
					.hashString(
							new String(inputJsonBytes, StandardCharsets.UTF_8),
							StandardCharsets.UTF_8)
					.toString();
			GstnAPIGstinConfig gstinConfig = gstinConfigRepo
					.findByGstin(dto.getGstin());
			String gstinUserName = gstinConfig.getGstinUserName();

			if (returnType.equalsIgnoreCase("GSTR6")) {
				entity = saveEntryIntoSignTable(dto, modifiedSummaryJsonPair, 
						sha256hex, gstinUserName, returnType,
						null, "Initiated");
			} else {
				entity = saveEntryIntoSignTable(dto, summaryJsonPair.getValue1(), 
						sha256hex, gstinUserName, returnType,
						null, "Initiated");
			}

			Pair<Boolean, String> gstnEvcOtpResp = gstnGetOtp
					.getOtpGstn(entity);
			boolean gstnEvcOtpRespVal = gstnEvcOtpResp.getValue0();
			if (!gstnEvcOtpRespVal) {
				LOGGER.debug("otp get call is failed");
				signAndFileRepo.updateStatusEVC("Failed",
						gstnEvcOtpResp.getValue1().toString(), entity.getId(),
						"FAILED");
				JsonObject resp = new JsonObject();
				apiResp = APIRespDto.creatErrorResp();
				resp.add("hdr", gson.toJsonTree(apiResp));
				resp.add("resp", gson.toJsonTree(gstnEvcOtpResp.getValue1()));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			signAndFileRepo.updateStatusEVC("Initiated", null, entity.getId(),
					"SUCCESS");
			JsonObject resp = new JsonObject();
			apiResp = APIRespDto.createSuccessResp();
			JsonObject gstinResp = new JsonObject();
			JsonElement resp1 = gson.toJsonTree(entity.getId().toString());
			gstinResp.add("signId", resp1);
			resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String errorMsg = "Error: Unexpected Runtime Encountered.";
			saveEntryIntoSignTable(dto, null, null, null, returnType, errorMsg,
					"Failed");
			JsonArray respBody = new JsonArray();
			JsonObject json = new JsonObject();
			json.addProperty("msg", e.getMessage());
			respBody.add(json);
			LOGGER.error("Message", e.getMessage());
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(errorMsg));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);
		}
	}

	private String setOffsetValue(String value) {
		JsonObject jsonObject = JsonParser.parseString(value).getAsJsonObject();

		// Skip processing if "offset" exists
		if (jsonObject.has("offset")) {
			return jsonObject.toString();
		}

		if (jsonObject.has("lateFeemain")) {
			JsonObject lateFeemain = jsonObject.getAsJsonObject("lateFeemain");
			if (lateFeemain.has("latefee")) {
				JsonObject lateFee = lateFeemain.getAsJsonObject("latefee");

				JsonElement cLamt = lateFee.has("cLamt") ? lateFee.get("cLamt") : JsonNull.INSTANCE;
				JsonElement sLamt = lateFee.has("sLamt") ? lateFee.get("sLamt") : JsonNull.INSTANCE;

				JsonObject offset = new JsonObject();
				offset.add("cLamt", cLamt);
				offset.add("sLamt", sLamt);
				
				jsonObject.add("offset", offset);
			}
		}

		return jsonObject.toString();
	}

	private SignAndFileEntity saveEntryIntoSignTable(SignAndFileReqDto dto,
			String summaryJson, String sha256hex, String gstnUserName,
			String returnType, String errorMsg, String status) {

		String filingType = "Normal Filing";
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		SignAndFileEntity entity = new SignAndFileEntity();
		entity.setGstin(dto.getGstin());
		entity.setTaxPeriod(dto.getTaxPeriod());
		entity.setPan(dto.getPan());
		entity.setHashPayload(sha256hex);
		entity.setGstinUserName(gstnUserName);
		entity.setReturnType(returnType);
		entity.setProcessingKey(
				dto.getGstin().concat("|").concat(dto.getTaxPeriod()));
		entity.setErrorMsg(errorMsg);
		entity.setStatus(status);
		entity.setCreatedBy(userName);
		entity.setCreatedOn(LocalDateTime.now());
		entity.setModifiedOn(LocalDateTime.now());
		if (!Strings.isNullOrEmpty(summaryJson)) {
			Clob responseClob = GenUtil.convertStringToClob(summaryJson);
			entity.setPayload(responseClob);
			JsonObject requestObject = JsonParser.parseString(summaryJson)
					.getAsJsonObject();
			if ("GSTR1".equalsIgnoreCase(returnType) || "GSTR1A".equalsIgnoreCase(returnType) 
					|| "GSTR3B".equalsIgnoreCase(returnType)) {
				if (requestObject.has(APIIdentifiers.ISNIL)
						&& APIConstants.Y.equalsIgnoreCase(requestObject
								.get(APIIdentifiers.ISNIL).getAsString())) {
					filingType = "Nil Filing";
				}
				entity.setFilingType(filingType);
			}
			
		}
		
		entity.setFilingMode("EVC");
		return signAndFileRepo.save(entity);

	}

	private Pair<Boolean, String> getSummaryForReturnType(
			Gstr1GetInvoicesReqDto summaryDto, String returnType) {
		Pair<Boolean, String> returnSummary = null;

		switch (returnType) {
		case "GSTR1":
			returnSummary = Gstr1SignAndFileService
					.getGstr1GstnSummary(summaryDto);
			break;
		case "GSTR1A":
			returnSummary = Gstr1ASignAndFileService
					.getGstr1AGstnSummary(summaryDto);
			break;
			
		case "GSTR3B":
			Gstr3bGetInvoicesReqDto gstr3BSummaryDto = new Gstr3bGetInvoicesReqDto();
			gstr3BSummaryDto.setGstin(summaryDto.getGstin());
			gstr3BSummaryDto.setTaxPeriod(summaryDto.getReturnPeriod());
			returnSummary = gstr3BSignAndFileService
					.getGstr3BGstnSummary(gstr3BSummaryDto);
			break;
		case "GSTR6":
			Gstr6GetInvoicesReqDto SummaryDto = new Gstr6GetInvoicesReqDto();
			SummaryDto.setGstin(summaryDto.getGstin());
			SummaryDto.setReturnPeriod(summaryDto.getReturnPeriod());
			returnSummary = gstr6SignAndFileService
					.getGstr6GstnSummary(SummaryDto);
			break;
		case "ITC04":
			Itc04GetInvoicesReqDto summaryDtoItc04 = new Itc04GetInvoicesReqDto();
			summaryDtoItc04.setGstin(summaryDto.getGstin());
			summaryDtoItc04.setReturnPeriod(summaryDto.getReturnPeriod());
			returnSummary = itc04SignAndFileService
					.getItc04GstnSummary(summaryDtoItc04);
			

			break;
		case "gstr7":
			Anx2GetInvoicesReqDto summaryDtoGSTR7 = new Anx2GetInvoicesReqDto();
			summaryDtoGSTR7.setGstin(summaryDto.getGstin());
			summaryDtoGSTR7.setReturnPeriod(summaryDto.getReturnPeriod());
			summaryDtoGSTR7.setGroupcode(TenantContext.getTenantId());
			String gstr7SummaryJson = gstnGetSummaryGSTR7.getGstr7Summary(
					summaryDtoGSTR7, summaryDtoGSTR7.getGroupcode()).toString();

			break;
		
			case "DRC01B":
				DRC01BGetInvoicesReqDto drc01BSummaryDto = new DRC01BGetInvoicesReqDto();
				drc01BSummaryDto.setGstin(summaryDto.getGstin());
				drc01BSummaryDto.setTaxPeriod(summaryDto.getReturnPeriod());
				drc01BSummaryDto.setRefId(summaryDto.getRefId());
				returnSummary = drc01BSignAndFileService
						.getDrc01BGstnSummary(drc01BSummaryDto);
				break;
				
			case "DRC01C":
				DRC01BGetInvoicesReqDto drc01CSummaryDto = new DRC01BGetInvoicesReqDto();
				drc01CSummaryDto.setGstin(summaryDto.getGstin());
				drc01CSummaryDto.setTaxPeriod(summaryDto.getReturnPeriod());
				drc01CSummaryDto.setRefId(summaryDto.getRefId());
				returnSummary = drc01CSignAndFileService
						.getDrc01CGstnSummary(drc01CSummaryDto);
				break;
		}	
		return returnSummary;
	}
	
}
