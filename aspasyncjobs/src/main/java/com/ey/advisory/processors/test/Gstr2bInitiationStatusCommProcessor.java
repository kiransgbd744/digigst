package com.ey.advisory.processors.test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javatuples.Quintet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.common.GstinWiseEmailDto;
import com.ey.advisory.app.common.GstrConsolidatedEmailService;
import com.ey.advisory.app.common.GstrEmailDetailsDto;
import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;
import com.ey.advisory.app.data.repositories.client.RecipientMasterUploadRepository;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("Gstr2bInitiationStatusCommProcessor")
public class Gstr2bInitiationStatusCommProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("RecipientMasterUploadRepository")
	private RecipientMasterUploadRepository masterUploadRepo;

	@Autowired
	@Qualifier("GstrConsolidatedEmailServiceImpl")
	private GstrConsolidatedEmailService consolidatedEmailService;

	@Override
	public void execute(Message message, AppExecContext context) {

		Gson gson = new Gson();
		String jsonString = message.getParamsJson();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String iGstinString = requestObject.get("iGstins").getAsString();
			String aGstinString = requestObject.get("aGstins").getAsString();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> inActiveGstins = gson.fromJson(iGstinString, listType);
			List<String> activeGstins = gson.fromJson(aGstinString, listType);

			List<String> allGstins = new ArrayList<>();
			allGstins.addAll(activeGstins);
			allGstins.addAll(inActiveGstins);

			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			String returnType = "GSTR2B";
			String fy = GenUtil.getFinancialYearByTaxperiod(taxPeriod);

			List<RecipientMasterUploadEntity> uploadEntites = masterUploadRepo
					.findByRecipientGstinInAndIsDeleteFalseAndIsGetGstr2BEmailTrue(
							allGstins);

			if (uploadEntites.isEmpty()) {
				LOGGER.error(
						"No Gstins are opted for Gstr2b Email Communication");
				return;
			}

			Map<String, List<RecipientMasterUploadEntity>> map = consolidatedEmailService
					.groupByPrimayEmail(uploadEntites);

			List<GstrEmailDetailsDto> reqDtos = new ArrayList<>();

			for (Entry<String, List<RecipientMasterUploadEntity>> pair : map
					.entrySet()) {
				GstrEmailDetailsDto dto = new GstrEmailDetailsDto();
				dto.setReturnType(returnType);
				dto.setFromTaxPeriod(taxPeriod);
				dto.setToTaxPeriod(taxPeriod);
				dto.setFy(fy);
				// dto.setEntityName(entityName);
				dto.setPrimaryEmail(pair.getKey());

				Quintet<List<String>, List<GstinWiseEmailDto>, String, Boolean, String> gstinsData = consolidatedEmailService
						.getGstinsData(pair.getValue(), activeGstins);

				dto.setSecondaryEmail(gstinsData.getValue0());
				dto.setGstins(gstinsData.getValue1());
				dto.setNotfnCode(gstinsData.getValue2());
				dto.setSecondEmailEligible(
						gstinsData.getValue3().booleanValue());
				dto.setActiveGstins(gstinsData.getValue4());
				reqDtos.add(dto);
			}

			consolidatedEmailService.persistAndSendEmail(reqDtos, true);
		} catch (Exception ee) {
			LOGGER.error(
					"Exception occured while sending email for Initiated/Not Initiated");
		}
	}

}
