package com.ey.advisory.test;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.gstr3b.Gstr3BAutoCalConstants;
import com.ey.advisory.app.gstr3b.Gstr3BGetLiabilityAutoCalcHelper;
import com.ey.advisory.app.gstr3b.Gstr3BGstinsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CopyTest {

	@Autowired
	@Qualifier("GstnUserRequestRepository")
	private GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	Gstr3BGetLiabilityAutoCalcHelper gstr3BAutoCalcHelper;

	@PostMapping(value = "/ui/testCopy", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3bAutoCalcAndSave(@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject resp = new JsonObject();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString).getAsJsonObject();

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			String respString = reqJson.get("respString").getAsString();

			String gstin = reqJson.get("gstin").getAsString();

			String taxPeriod = reqJson.get("taxPeriod").getAsString();

			// calling json save
			saveAutoCalcDetails(gstin, taxPeriod, respString);

			List<Gstr3BGstinsDto> resultList = getGstrList(respString);

			// Saving Response to DB Table.
			gstr3BAutoCalcHelper.saveOrUpdateAutoCalc(gstin, taxPeriod, resultList, "Interest");

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree("succees"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while get3BAutoCalAndSave";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	private void saveAutoCalcDetails(String gstin, String taxPeriod, String gstnResponse) {

		GstnUserRequestEntity gstnUserRequestEntity = gstnUserRequestRepo.findByGstinAndTaxPeriodAndReturnType(gstin,
				taxPeriod, Gstr3BAutoCalConstants.GSTR3B);

		Clob reqClob;
		try {
			reqClob = new javax.sql.rowset.serial.SerialClob(gstnResponse.toCharArray());

			if (gstnUserRequestEntity == null) {

				gstnUserRequestEntity = new GstnUserRequestEntity();

				String userName = SecurityContext.getUser().getUserPrincipalName() == null ? "SYSTEM"
						: SecurityContext.getUser().getUserPrincipalName();

				gstnUserRequestEntity.setGstin(gstin);
				gstnUserRequestEntity.setTaxPeriod(taxPeriod);
				gstnUserRequestEntity.setReturnType(Gstr3BAutoCalConstants.GSTR3B);
				gstnUserRequestEntity.setRequestType("GET");
				gstnUserRequestEntity.setRequestStatus(1);
				gstnUserRequestEntity.setCreatedBy(userName);
				gstnUserRequestEntity.setCreatedOn(LocalDateTime.now());
				gstnUserRequestEntity.setDelete(false);
				gstnUserRequestEntity.setIntrtAutoCalcResponse(reqClob);

				gstnUserRequestRepo.save(gstnUserRequestEntity);

			} else {
				gstnUserRequestRepo.updateGstinInterestAutoCalGstnResponse(reqClob, 1, gstin, taxPeriod,
						Gstr3BAutoCalConstants.GSTR3B, LocalDateTime.now());

			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting GSTR3B Interest " + "auto calc response";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}

	}

	private List<Gstr3BGstinsDto> getGstrList(String gstnResponse) {

		List<Gstr3BGstinsDto> responseList = new ArrayList<>();
		Gstr3BGstinsDto gstr3bgstnDto = new Gstr3BGstinsDto();

		JsonParser jsonParser = new JsonParser();
		JsonObject parseResponse = (JsonObject) jsonParser.parse(gstnResponse).getAsJsonObject();

		JsonObject interest = parseResponse.has("interest") ? parseResponse.getAsJsonObject("interest") : null;
		
		if(interest!=null){

		if (interest.has("csamt"))
			gstr3bgstnDto.setCess(interest.get("csamt").getAsBigDecimal());

		if (interest.has("camt"))
			gstr3bgstnDto.setCgst(interest.get("camt").getAsBigDecimal());

		if (interest.has("samt"))
			gstr3bgstnDto.setSgst(interest.get("samt").getAsBigDecimal());

		if (interest.has("iamt"))
			gstr3bgstnDto.setIgst(interest.get("iamt").getAsBigDecimal());
		
		}
		gstr3bgstnDto.setSectionName("5.1(a)");
		gstr3bgstnDto.setSubSectionName("Intr");

		responseList.add(gstr3bgstnDto);

		return responseList;

	}

}
