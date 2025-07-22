/**
 * 
 */
package com.ey.advisory.gstr1A.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.ey.advisory.app.data.daos.client.simplified.RequestForApprovalDaoImpl;
import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.services.Gstr1A.Gstr1AUpdatedMofifiedDateFetchDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.GetGstnSubmitStatusDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1FlagRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1ProcessedScreenDiffRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenDocRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenNilRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.GstnSummarySectionService;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenAdvReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenDocReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenEcomSupReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenHSNReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenSezReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleDocGstnSummarySearchService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Sakshi.jain
 * 
 * GSTR1A
 *
 */
@RestController
public class Gstr1ASummaryController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ASummaryController.class);
	public static final String SAVE = "SAVE";
	public static final String GSTR1A = "GSTR1A";

	@Autowired
	@Qualifier("Gstr1SummaryScreenReqRespHandler")
	Gstr1SummaryScreenReqRespHandler gstr1ReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenAdvReqRespHandler")
	Gstr1SummaryScreenAdvReqRespHandler gstr1AdvReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenSezReqRespHandler")
	Gstr1SummaryScreenSezReqRespHandler gstr1SezReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenEcomSupReqRespHandler")
	Gstr1SummaryScreenEcomSupReqRespHandler gstr1ecomSupReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenDocReqRespHandler")
	Gstr1SummaryScreenDocReqRespHandler gstr1DocReqRespHandler;

	@Autowired
	@Qualifier("GstnSummarySectionService")
	GstnSummarySectionService gstnService;

	@Autowired
	@Qualifier("Gstr1SummaryScreenHSNReqRespHandler")
	Gstr1SummaryScreenHSNReqRespHandler gstr1HsnReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SimpleDocGstnSummarySearchService")
	Gstr1SimpleDocGstnSummarySearchService tableSearchService;

	@Autowired
	@Qualifier("Gstr1AUpdatedMofifiedDateFetchDaoImpl")
	Gstr1AUpdatedMofifiedDateFetchDaoImpl dateFetchDao;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GstnApi")
	GstnApi gstnApi;

	@Autowired
	@Qualifier("RequestForApprovalDaoImpl")
	RequestForApprovalDaoImpl requestForApproval;

	@Autowired
	private GstnUserRequestRepository gstnUserRequestRepo;

	@PostMapping(value = "/ui/gstr1ASummaryScreen", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyGstr1Summary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto annexure1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			if ("UPDATEGSTIN"
					.equalsIgnoreCase(annexure1SummaryRequest.getAction())) {

				String gstin = null;
				List<String> gstinList = null;
				Map<String, List<String>> dataSecAttrs = annexure1SummaryRequest
						.getDataSecAttrs();
				if (!dataSecAttrs.isEmpty()) {
					for (String key : dataSecAttrs.keySet()) {
						if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
							gstin = key;
							if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()
									&& dataSecAttrs
											.get(OnboardingConstant.GSTIN)
											.size() > 0) {
								gstinList = dataSecAttrs
										.get(OnboardingConstant.GSTIN);
							}
						}
					}
				}

				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstinList.get(0));

				// String authStatus = "A";

				if (!"A".equalsIgnoreCase(authStatus)) {

					String msg = " Auth Token is InActive, Please Active ";
					// LOGGER.error(msg, ex);

					JsonObject resp = new JsonObject();
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));

					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}
			}
			
			annexure1SummaryRequest.setReturnType(APIConstants.GSTR1A);
			
			LOGGER.debug("GSTN Data Summary Execution BEGIN ");
			SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult = (SearchResult<Gstr1CompleteSummaryDto>) tableSearchService
					.find(annexure1SummaryRequest, null,
							Gstr1CompleteSummaryDto.class);
			LOGGER.debug("OUTWARD GSTN Data Summary Execution END ");

			String lastUpdatedDate = dateFetchDao
					.getLastUpdatedGstr1ASummStatus(annexure1SummaryRequest);

			// String lastUpdatedDate =
			// loadBasicSummarySection.getLastUpdatedDate();

			List<? extends Gstr1CompleteSummaryDto> gstnResult = gstnSumryResult
					.getResult();
//done
			JsonElement outwardSummaryRespBody = gson
					.toJsonTree(gstr1ReqRespHandler.handleGstr1ReqAndResp(
							annexure1SummaryRequest, gstnResult));

			
		//done	
			JsonElement hsnSummaryRespBody = gson
					.toJsonTree(gstr1HsnReqRespHandler.handleGstr1HsnReqAndResp(
							annexure1SummaryRequest, gstnResult));

			//done
			JsonElement sezSummaryRespBody = gson
					.toJsonTree(gstr1SezReqRespHandler
							.handleGstr1SezReqAndResp(annexure1SummaryRequest));
			//done
			JsonElement advSummaryRespBody = gson
					.toJsonTree(gstr1AdvReqRespHandler.handleGstr1AdvReqAndResp(
							annexure1SummaryRequest, gstnResult));
			//done
			JsonElement nilSummaryRespBody = gson
					.toJsonTree(gstr1DocReqRespHandler.handleGstr1NilReqAndResp(
							annexure1SummaryRequest, gstnResult));
			//done
			JsonElement docSummaryRespBody = gson
					.toJsonTree(gstr1DocReqRespHandler.handleGstr1DocReqAndResp(
							annexure1SummaryRequest, gstnResult));

			JsonElement ecomSupRespBody = gson.toJsonTree(
					gstr1ecomSupReqRespHandler.handleGstr1ReqAndResp(
							annexure1SummaryRequest, gstnResult));

			JsonElement updatedDateRespbody = gson.toJsonTree(lastUpdatedDate);

			Gstr1FlagRespDto loadHsnFlagection = loadHsnFlagection(
					annexure1SummaryRequest);

			Boolean rateIncludedInHsn = gstnApi.isRateIncludedInHsn(
					annexure1SummaryRequest.getTaxPeriod());
			Boolean hsnUserInput = loadHsnFlagection.getIsHsnUserInput();
			Boolean nilUserInput = loadHsnFlagection.getIsNilUserInput();

			JsonElement hsn = gson.toJsonTree(hsnUserInput);
			JsonElement nil = gson.toJsonTree(nilUserInput);
			JsonElement hsnRate = gson.toJsonTree(rateIncludedInHsn);

			String updatedTimeStampForHsn = dateFetchDao
					.loadLastUpdatedDateForHsnSection(annexure1SummaryRequest);
			String updatedTimeStampForNil = dateFetchDao
					.loadLastUpdatedDateForNilSection(annexure1SummaryRequest);

			// This method is Used For Giving Flag For Request For Approval
			
			 

			JsonElement hsnTimeStamp = gson.toJsonTree(updatedTimeStampForHsn);
			JsonElement nilTimeStamp = gson.toJsonTree(updatedTimeStampForNil);
			// JsonElement requestForApprovalFlag =
			// gson.toJsonTree(requestForApprovalflag);

			Map<String, JsonElement> combinedMap = new HashMap<>();
			// lastUpdatedDate
			combinedMap.put("hsnUserInput", hsn);
			combinedMap.put("nilUserInput", nil);
			combinedMap.put("rateIncludedInHsn", hsnRate);
			combinedMap.put("lastUpdatedHsnTime", hsnTimeStamp);
			combinedMap.put("lastUpdatedNilTime", nilTimeStamp);
			combinedMap.put("lastUpdatedDate", updatedDateRespbody);
			combinedMap.put("outward", outwardSummaryRespBody);
			combinedMap.put("hsnSummary", hsnSummaryRespBody);
			combinedMap.put("sez", sezSummaryRespBody);
			combinedMap.put("Advances", advSummaryRespBody);
			combinedMap.put("nil", nilSummaryRespBody);
			combinedMap.put("docIssue", docSummaryRespBody);
			combinedMap.put("ecomSup", ecomSupRespBody);
			// combinedMap.put("requestForApprovalFlag",
			// requestForApprovalFlag);

			JsonElement summaryRespbody = gson.toJsonTree(combinedMap);
			JsonElement respBody = gson.toJsonTree(summaryRespbody);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	// This API gives Gstn Submit Status
	@PostMapping(value = "/ui/getGstr1AGstnSubmitStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstnSubmitStatsu(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto annexure1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			GetGstnSubmitStatusDto loadForGetGstnSubmitStatus = dateFetchDao
					.loadSubmitBasicSummarySection(annexure1SummaryRequest);

			JsonElement respBody = gson.toJsonTree(loadForGetGstnSubmitStatus);
			// JsonElement respBody = gson.toJsonTree(summaryRespbody);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//
	
	// Save Gstn Implementation

	// This API gives Gstn Submit Status
	@PostMapping(value = "/ui/getGstr1ASaveGstnStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSaveGstnStatsu(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto annexure1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);
			 

			String lastUpdatedDate = dateFetchDao
					.loadSaveGstnStatus(annexure1SummaryRequest);

						 
			JsonObject response = new JsonObject();
			response.add("updatedDate", gson.toJsonTree(lastUpdatedDate));

			JsonElement respBody = gson.toJsonTree(response);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/gstr1ADifferenceSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1DifferenceSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto annexure1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);
			
			annexure1SummaryRequest.setReturnType("GSTR1A");
			
			String lastUpdatedDate = dateFetchDao
					.gstr1AloadBasicSummarySection(annexure1SummaryRequest);

			LOGGER.debug("GSTN Data Summary Execution BEGIN ");
			SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult = (SearchResult<Gstr1CompleteSummaryDto>) tableSearchService
					.find(annexure1SummaryRequest, null,
							Gstr1CompleteSummaryDto.class);
			// SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult =
			// (SearchResult<Gstr1CompleteSummaryDto>) gstnService
			// .find(annexure1SummaryRequest, null,
			// Gstr1CompleteSummaryDto.class);
			LOGGER.debug("OUTWARD GSTN Data Summary Execution END ");

			List<Gstr1ProcessedScreenDiffRespDto> diffRespDtos = new ArrayList<>();

			List<? extends Gstr1CompleteSummaryDto> gstnResult = gstnSumryResult
					.getResult();
			//done
			List<Gstr1SummaryScreenRespDto> outwardSummaryRespBody = gstr1ReqRespHandler
					.handleGstr1ReqAndResp(annexure1SummaryRequest, gstnResult);
			addSummaryResultsToDto(outwardSummaryRespBody, diffRespDtos);
			
			//done
			List<Gstr1SummaryScreenRespDto> hsnSummaryRespBody = gstr1HsnReqRespHandler
					.handleGstr1HsnReqAndResp(annexure1SummaryRequest,
							gstnResult);
			addSummaryResultsToDto(hsnSummaryRespBody, diffRespDtos);

			// List<Gstr1SummaryScreenRespDto> sezSummaryRespBody =
			// gstr1SezReqRespHandler
			// .handleGstr1SezReqAndResp(annexure1SummaryRequest);
			// addSummaryResultsToDto(sezSummaryRespBody, diffRespDtos);

			//done
			List<Gstr1SummaryScreenRespDto> advSummaryRespBody = gstr1AdvReqRespHandler
					.handleGstr1AdvReqAndResp(annexure1SummaryRequest,
							gstnResult);
			addSummaryResultsToDto(advSummaryRespBody, diffRespDtos);
			//done
			List<Gstr1SummaryScreenNilRespDto> nilSummaryRespBody = gstr1DocReqRespHandler
					.handleGstr1NilReqAndResp(annexure1SummaryRequest,
							gstnResult);
			addNilSummaryResultsToDto(nilSummaryRespBody, diffRespDtos);
			
			//done
			List<Gstr1SummaryScreenDocRespDto> docSummaryRespBody = gstr1DocReqRespHandler
					.handleGstr1DocReqAndResp(annexure1SummaryRequest,
							gstnResult);
			addDocSummaryResultsToDto(docSummaryRespBody, diffRespDtos);
			
			//done
			List<Gstr1SummaryScreenRespDto> ecomSupRespBody = gstr1ecomSupReqRespHandler
					.handleGstr1ReqAndResp(annexure1SummaryRequest, gstnResult);
			addSupEcomTransToDto(ecomSupRespBody, diffRespDtos);
			
			Gstr1FlagRespDto loadHsnFlagection = dateFetchDao
					.loadHsnFlagection(annexure1SummaryRequest);

			boolean hsnUserInput = loadHsnFlagection.getIsHsnUserInput();
			boolean nilUserInput = loadHsnFlagection.getIsNilUserInput();

			JsonElement hsn = gson.toJsonTree(hsnUserInput);
			JsonElement nil = gson.toJsonTree(nilUserInput);

			Boolean rateIncludedInHsn = gstnApi.isRateIncludedInHsn(
					annexure1SummaryRequest.getTaxPeriod());

			JsonElement summaryRespbody = gson.toJsonTree(diffRespDtos);
			JsonElement respBody = gson.toJsonTree(summaryRespbody);
			JsonObject resp = new JsonObject();

			JsonObject dateResp = new JsonObject();

			dateResp.add("lastUpdatedDate",
					new Gson().toJsonTree(lastUpdatedDate));
			dateResp.add("hsnUserInput", hsn);
			dateResp.add("nilUserInput", nil);
			dateResp.add("rateIncludedInHsn",
					new Gson().toJsonTree(rateIncludedInHsn));
			resp.add("hdr", dateResp);

			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void addDocSummaryResultsToDto(
			List<Gstr1SummaryScreenDocRespDto> docSummaryRespBody,
			List<Gstr1ProcessedScreenDiffRespDto> diffRespDtos) {
		if (!docSummaryRespBody.isEmpty() && docSummaryRespBody.size() > 0) {
			docSummaryRespBody.stream().forEach(dto -> {
				Gstr1ProcessedScreenDiffRespDto diffRespDto = new Gstr1ProcessedScreenDiffRespDto();
				diffRespDto.setSection(dto.getTaxDocType());
				diffRespDto.setCount(dto.getDiffTotal());
				diffRespDto.setTaxableValue(BigDecimal.ZERO);
				diffRespDto.setTaxPayble(BigDecimal.ZERO);
				diffRespDto.setInvoiceValue(BigDecimal.ZERO);
				diffRespDto.setIgst(BigDecimal.ZERO);
				diffRespDto.setSgst(BigDecimal.ZERO);
				diffRespDto.setCgst(BigDecimal.ZERO);
				diffRespDto.setCess(BigDecimal.ZERO);
				diffRespDtos.add(diffRespDto);
			});
		}

	}

	private void addNilSummaryResultsToDto(
			List<Gstr1SummaryScreenNilRespDto> nilSummaryRespBody,
			List<Gstr1ProcessedScreenDiffRespDto> diffRespDtos) {
		if (!nilSummaryRespBody.isEmpty() && nilSummaryRespBody.size() > 0) {
			nilSummaryRespBody.stream().forEach(dto -> {
				Gstr1ProcessedScreenDiffRespDto diffRespDto = new Gstr1ProcessedScreenDiffRespDto();

				BigDecimal total = dto.getDiffNitRated()
						.add(dto.getDiffExempted()).add(dto.getDiffNonGst());
				diffRespDto.setSection(dto.getTaxDocType());
				diffRespDto.setCount(0);
				diffRespDto.setTaxableValue(BigDecimal.ZERO);
				diffRespDto.setTaxPayble(BigDecimal.ZERO);
				diffRespDto.setInvoiceValue(total);
				diffRespDto.setIgst(BigDecimal.ZERO);
				diffRespDto.setSgst(BigDecimal.ZERO);
				diffRespDto.setCgst(BigDecimal.ZERO);
				diffRespDto.setCess(BigDecimal.ZERO);
				diffRespDtos.add(diffRespDto);
			});
		}

	}

	private void addSummaryResultsToDto(
			List<Gstr1SummaryScreenRespDto> gstr1SummaryScreenRespDtos,
			List<Gstr1ProcessedScreenDiffRespDto> diffRespDtos) {
		if (!gstr1SummaryScreenRespDtos.isEmpty()
				&& gstr1SummaryScreenRespDtos.size() > 0) {
			gstr1SummaryScreenRespDtos.stream().forEach(dto -> {
				Gstr1ProcessedScreenDiffRespDto diffRespDto = new Gstr1ProcessedScreenDiffRespDto();
				diffRespDto.setSection(dto.getTaxDocType());
				diffRespDto.setCount(dto.getDiffCount());
				diffRespDto.setTaxableValue(dto.getDiffTaxableValue());
				diffRespDto.setTaxPayble(dto.getDiffTaxPayble());
				diffRespDto.setInvoiceValue(dto.getDiffInvoiceValue());
				diffRespDto.setIgst(dto.getDiffIgst());
				diffRespDto.setSgst(dto.getDiffSgst());
				diffRespDto.setCgst(dto.getDiffCgst());
				diffRespDto.setCess(dto.getDiffCess());
				diffRespDtos.add(diffRespDto);
			});
		}
	}

	private void addSupEcomTransToDto(
			List<Gstr1SummaryScreenRespDto> gstr1SummaryScreenRespDtos,
			List<Gstr1ProcessedScreenDiffRespDto> diffRespDtos) {
		if (!gstr1SummaryScreenRespDtos.isEmpty()
				&& gstr1SummaryScreenRespDtos.size() > 0) {
			gstr1SummaryScreenRespDtos.stream().forEach(dto -> {
				Gstr1ProcessedScreenDiffRespDto diffRespDto = new Gstr1ProcessedScreenDiffRespDto();
				diffRespDto.setSection(dto.getTaxDocType());
				diffRespDto.setCount(dto.getDiffCount());
				diffRespDto.setTaxableValue(dto.getDiffTaxableValue());
				diffRespDto.setIgst(dto.getDiffIgst());
				diffRespDto.setSgst(dto.getDiffSgst());
				diffRespDto.setCgst(dto.getDiffCgst());
				diffRespDto.setCess(dto.getDiffCess());
				// since 14 & 15 we will not have Total Taxable
				diffRespDto.setTaxPayble(
					    calculateNonNull(dto.getDiffIgst())
					            .add(calculateNonNull(dto.getDiffCgst()))
					            .add(calculateNonNull(dto.getDiffSgst()))
					            .add(calculateNonNull(dto.getDiffCess()))
					            .setScale(2, BigDecimal.ROUND_HALF_UP)
					);
				diffRespDto.setInvoiceValue(
					    calculateNonNull(diffRespDto.getTaxPayble())
					            .add(calculateNonNull(dto.getDiffTaxableValue()))
					            .setScale(2, BigDecimal.ROUND_HALF_UP)
					);

				diffRespDtos.add(diffRespDto);
			});
		}
	}

	private BigDecimal calculateNonNull(BigDecimal value) {
	    return value != null ? value : BigDecimal.ZERO;
	}


	public Gstr1FlagRespDto loadHsnFlagection(Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		String taxPeriodReq = request.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		Optional<GstnUserRequestEntity> previousEntity = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		if (gstinList != null && !gstinList.isEmpty()) {
		 previousEntity = gstnUserRequestRepo
				.findTop1ByGstinAndTaxPeriodAndRequestTypeAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
						gstinList.get(0), taxPeriodReq, SAVE, "GSTR1A");
		}

		Boolean isNilUserInput = null;
		Boolean isHsnUserInput = null;

		if (previousEntity != null && previousEntity.isPresent()) {
			GstnUserRequestEntity gstnUserRequestEntity = previousEntity.get();
			isNilUserInput = gstnUserRequestEntity.isNilUserInput();
			isHsnUserInput = gstnUserRequestEntity.isHsnUserInput();
		}

		Gstr1FlagRespDto entity = new Gstr1FlagRespDto();

		entity.setIsNilUserInput(
				isNilUserInput != null ? isNilUserInput : false);
		entity.setIsHsnUserInput(
				isHsnUserInput != null ? isHsnUserInput : false);
		return entity;

	}
	
	
	// This API gives Gstn Submit Status
		@PostMapping(value = "/ui/getGstr1AGstnSaveFileStatus", produces = {
				MediaType.APPLICATION_JSON_VALUE })
		public ResponseEntity<String> getGstnSaveStatsu(
				@RequestBody String jsonString) {
			String groupCode = TenantContext.getTenantId();
			LOGGER.debug(String.format("Group Code = '%s'", groupCode));
			try {
				JsonObject obj = new JsonParser().parse(jsonString)
						.getAsJsonObject();
				String reqJson = obj.get("req").getAsJsonObject().toString();
				Gson gson = GsonUtil.newSAPGsonInstance();

				Annexure1SummaryReqDto annexure1SummaryRequest = gson
						.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

				GetGstnSubmitStatusDto loadForGetGstnSubmitStatus = dateFetchDao
						.loadForGstr1AGetGstnSaveStatus(annexure1SummaryRequest);

				JsonElement respBody = gson.toJsonTree(loadForGetGstnSubmitStatus);
				// JsonElement respBody = gson.toJsonTree(summaryRespbody);

				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);

				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} catch (JsonParseException ex) {
				String msg = "Error while parsing the input Json";
				LOGGER.error(msg, ex);

				JsonObject resp = new JsonObject();
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (Exception ex) {
				String msg = "Unexpected error while fecthing Annexure1 "
						+ "Summary Documents " + ex;
				LOGGER.error(msg, ex);

				JsonObject resp = new JsonObject();
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

}
