package com.ey.advisory.controllers.gstr6a;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.gstr6.Gstr6ComputeDigiConfigStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6ComputeDigiConfigStatusRepository;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6DistChannelRevSumRespDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryStringResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6UpdatedMofifiedDateFetchDaoImpl;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6ReviewSummaryServiceImpl;
import com.ey.advisory.app.services.jobs.anx2.Gstr6SummaryAtGstnImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Gstr6ReviewSummaryController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ReviewSummaryController.class);
	@Autowired
	@Qualifier("Gstr6ReviewSummaryServiceImpl")
	private Gstr6ReviewSummaryServiceImpl gstr6RevSumSerImpl;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr6UpdatedMofifiedDateFetchDaoImpl")
	private Gstr6UpdatedMofifiedDateFetchDaoImpl gstr6UpdatedMofifiedDateFetchDao;

	@Autowired
	@Qualifier("Gstr6SummaryAtGstnImpl")
	private Gstr6SummaryAtGstnImpl gstr6SummaryAtGstn;

	@Autowired
	@Qualifier("Gstr6SummaryAtGstnImpl")
	private Gstr6SummaryAtGstnImpl gstnImpl;

	@Autowired
	@Qualifier("Gstr6ComputeDigiConfigStatusRepository")
	Gstr6ComputeDigiConfigStatusRepository digiConfigStatusRepo;
	
	@PostMapping(value = "/ui/getGstr6ReviewSummary", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getGstr6ReviewSummary(
			@RequestBody String jsonReq) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);

			String lastUpdate = null;
			String digiStatus = null;
			String digiUpdatedOn = null;

			if ("UPDATEGSTIN".equalsIgnoreCase(reqDto.getAction())) {
				List<String> gstinList = null;
				Map<String, List<String>> dataSecAttrs = reqDto
						.getDataSecAttrs();
				if (!dataSecAttrs.isEmpty()) {
					for (String key : dataSecAttrs.keySet()) {
						if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
								&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
										.isEmpty()) {
							gstinList = dataSecAttrs
									.get(OnboardingConstant.GSTIN);
						}
					}
				}

				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstinList.get(0));
				if (!"A".equalsIgnoreCase(authStatus)) {
					String msg = " Auth Token is InActive, Please Active ";
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}
				if (gstinList != null && !gstinList.isEmpty()) {
					String groupCode = TenantContext.getTenantId();
					gstinList.forEach(gstin -> {
						Gstr6GetInvoicesReqDto reqDto1 = new Gstr6GetInvoicesReqDto();
						reqDto1.setGstin(gstin);
						reqDto1.setReturnPeriod(reqDto.getTaxPeriod());

						gstr6SummaryAtGstn.getGstr6Summary(reqDto1, groupCode);

						// ITC Section
						gstnImpl.getGstr6ITCDetailsSummary(reqDto1, groupCode);
					});
				}
			}
			lastUpdate = gstr6UpdatedMofifiedDateFetchDao
					.loadBasicSummarySection(reqDto);
			List<Gstr6ReviewSummaryResponseDto> respDtos = gstr6RevSumSerImpl
					.getGstr6RevSummary(reqDto);

			List<Gstr6ReviewSummaryStringResponseDto> finalRespDtos = gstr6RevSumSerImpl
					.getGstr6SectionsSummary(respDtos);
			
			String taxPeriod = GenUtil.getDerivedTaxPeriod(reqDto.getTaxPeriod()).toString();
			Gstr6ComputeDigiConfigStatusEntity configEntity = digiConfigStatusRepo
					.findByGstinAndTaxPeriodAndIsActiveTrue(reqDto.getDataSecAttrs().get("GSTIN").get(0),
							taxPeriod);
			if(configEntity != null){
				digiStatus = configEntity.getStatus();
				if (configEntity.getUpdatedOn() != null) {
					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("dd-MM-yyyy : HH:mm:ss");
					String newdate = FOMATTER.format(EYDateUtil
							.toISTDateTimeFromUTC(configEntity.getUpdatedOn()));
					digiUpdatedOn = newdate.toString();
				}
			}
			JsonElement jsonBody = gson.toJsonTree(finalRespDtos);
			JsonElement lastUpdateJson = gson.toJsonTree(lastUpdate);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonBody);
			resp.add("lastUpdate", lastUpdateJson);
			resp.add("digiStatus", gson.toJsonTree(digiStatus));
			resp.add("digiUpdatedOn", gson.toJsonTree(digiUpdatedOn));

		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
		}

		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/getDistChannelGstr6RevSum", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getGstr6ForDistributionalChannel(
			@RequestBody String jsonReq) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);

			List<Gstr6DistChannelRevSumRespDto> distChanRevSumRespDtos = gstr6RevSumSerImpl
					.getGstr6DistChannelRevSum(reqDto);
			if (distChanRevSumRespDtos != null
					&& !distChanRevSumRespDtos.isEmpty()) {
				JsonElement jsonBody = gson.toJsonTree(distChanRevSumRespDtos);
				resp.add("resp", jsonBody);
			} else {
				resp.add("resp", gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), "No record found")));
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
