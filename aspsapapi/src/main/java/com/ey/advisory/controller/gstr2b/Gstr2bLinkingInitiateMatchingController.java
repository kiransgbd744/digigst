package com.ey.advisory.controller.gstr2b;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Gstr2BLinkingConfigEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2bLinkingConfigRepository;
import com.ey.advisory.app.gstr2b.Gstr2bGetLinkingDto;
import com.ey.advisory.app.gstr2b.InitiateGstr2bLinkingDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
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
public class Gstr2bLinkingInitiateMatchingController {

	@Autowired
	@Qualifier("Gstr2bLinkingConfigRepository")
	private Gstr2bLinkingConfigRepository linkingRepo;

	@PostMapping(value = "/ui/initiateGstr2bLinking", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> InitiateReconcile(
			@RequestBody String jsonString) {
		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Begin Gstr2InitiateMatchingRecon to Initiate "
								+ "Recon : %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			
			Gstr2bGetLinkingDto dto = gson.fromJson(requestObject,
					Gstr2bGetLinkingDto.class);
			String status = "Amount linking has been initiated successfully";

			List<InitiateGstr2bLinkingDto> gstinTaxPeriodList = dto.getGstinTaxPeriodList();
			
			List<Pair<String, String>> initiatedGstinTaxPeriodKeyList = new ArrayList<>();
			List<String> gstinList = new ArrayList<>();
			List<String> taxPeriodList = new ArrayList<>();
			for (InitiateGstr2bLinkingDto gstinTaxperiodDto : gstinTaxPeriodList) {
				String gstin = gstinTaxperiodDto.getGstin();
				gstinList.add(gstin);
				List<String> taxPeriods = gstinTaxperiodDto
						.getTaxPeriodList();
				for (String taxPeriod : taxPeriods) {
					taxPeriodList.add(taxPeriod);
					initiatedGstinTaxPeriodKeyList
							.add(new Pair<String, String>(gstin, taxPeriod));
				}
			}

			List<Gstr2BLinkingConfigEntity> availableConfigEntities = linkingRepo
					.findByGstinTaxPeriodAndStatus(gstinList, taxPeriodList);

			List<String> availableKeyList = new ArrayList<>();
			for (Gstr2BLinkingConfigEntity linkingEntity : availableConfigEntities) {
				availableKeyList.add(linkingEntity.getGstin() + "|"
						+ linkingEntity.getTaxPeriod());
			}

			List<Gstr2BLinkingConfigEntity> linkingEntities = new ArrayList<>();
			for (Pair<String, String> pair : initiatedGstinTaxPeriodKeyList) {
				String initiatedKey = pair.getValue0() + "|" + pair.getValue1();
				if (!availableKeyList.contains(initiatedKey)) {
					Gstr2BLinkingConfigEntity configEntity = new Gstr2BLinkingConfigEntity();
					configEntity.setGstin(pair.getValue0());
					configEntity.setTaxPeriod(pair.getValue1());
					configEntity.setStatus("Linking In Queue");
					if (userName != null)
						configEntity.setCreatedBy(userName);
					configEntity.setCreatedDate(LocalDateTime.now());
					configEntity.setIsDelete(false);

					linkingRepo.updateInActive(pair.getValue0(), pair.getValue1());
					linkingEntities.add(configEntity);
				}
			}

			linkingRepo.saveAll(linkingEntities);
			JsonObject gstinDetResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinDetResp.add("status", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinDetResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"End Gstr2bLinkingInitiateMatchingController to Initiate 2b Linking"
								+ " before returning response : %s",
						gstinDetResp);
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

}
