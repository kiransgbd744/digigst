package com.ey.advisory.controller;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2BManualApiStatusRepo;
import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgsezHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BItcItemRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingB2bHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingB2baHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingCdnrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingCdnraHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingEcomHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingEcomaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BSuppSmryRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bAutoCommRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderB2BRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterConfigEntityRepository;
import com.ey.advisory.app.gstr2b.Gstr2BDupCheckProcCallHandler;
import com.ey.advisory.app.gstr2b.Gstr2BGETDataDto;
import com.ey.advisory.app.gstr2b.Gstr2BGetRevIntgHelper;
import com.ey.advisory.app.gstr2b.Gstr2BManualApiStatus;
import com.ey.advisory.app.gstr2b.Gstr2BbatchUtil;
import com.ey.advisory.app.gstr2b.Gstr2RespParser;
import com.ey.advisory.app.gstr2b.Gstr2bGetInvoiceReqDto;
import com.ey.advisory.app.ims.handlers.ImsCountDataParserImpl;
import com.ey.advisory.app.ims.handlers.ImsInvoicesDataParser;
import com.ey.advisory.app.ims.handlers.ImsInvoicesProcCallService;
import com.ey.advisory.app.inward.einvoice.GetInwardIrnDetailSectionHandler;
import com.ey.advisory.app.inward.einvoice.GetIrnDetailsJsonVsPdfReconServiceImpl;
import com.ey.advisory.app.inward.einvoice.InwardGetIrnDetailsDataParserService;
import com.ey.advisory.app.inward.einvoice.InwardGetIrnListDataParser;
import com.ey.advisory.app.services.docs.ComprehensiveSRFileArrivalHandler;
import com.ey.advisory.app.services.docs.Gstr3bSummaryFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;
import com.ey.advisory.gstnapi.APIInvoker;
import com.ey.advisory.gstnapi.APILogger;
import com.ey.advisory.gstnapi.GstnApiWrapperConstants;
import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TestController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	Gstr2BGetRevIntgHelper revIntgHelper;

	@Autowired
	@Qualifier("Gstr2RespParserImpl")
	private Gstr2RespParser parser;

	@Autowired
	@Qualifier("GstinApiCallRepository")
	private GstinApiCallRepository gstnStatusRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingB2bHeaderRepository")
	private Gstr2GetGstr2BLinkingB2bHeaderRepository b2bHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingB2baHeaderRepository")
	private Gstr2GetGstr2BLinkingB2baHeaderRepository b2bAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingCdnrHeaderRepository")
	private Gstr2GetGstr2BLinkingCdnrHeaderRepository cdnrHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingCdnraHeaderRepository")
	private Gstr2GetGstr2BLinkingCdnraHeaderRepository cdnrAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BIsdHeaderRepository")
	private Gstr2GetGstr2BIsdHeaderRepository isdHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BIsdaHeaderRepository")
	private Gstr2GetGstr2BIsdaHeaderRepository isdAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BImpgHeaderRepository")
	private Gstr2GetGstr2BImpgHeaderRepository impgHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BImpgsezHeaderRepository")
	private Gstr2GetGstr2BImpgsezHeaderRepository impgSezHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BItcItemRepository")
	private Gstr2GetGstr2BItcItemRepository itcSummaryRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BSuppSmryRepository")
	private Gstr2GetGstr2BSuppSmryRepository suppSummaryRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Gstr2bAutoCommRepository")
	private Gstr2bAutoCommRepository gstr2bAutoCommRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingEcomHeaderRepository")
	private Gstr2GetGstr2BLinkingEcomHeaderRepository ecomHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingEcomaHeaderRepository")
	private Gstr2GetGstr2BLinkingEcomaHeaderRepository ecomaHeaderRepo;

	@Autowired
	private Gstr2BDupCheckProcCallHandler procCallHandler;

	@Autowired
	@Qualifier("ComprehensiveSRFileArrivalHandler")
	private ComprehensiveSRFileArrivalHandler gstr1FileUploadUtil;

	@Autowired
	@Qualifier("InwardGetIrnDetailsDataParserServiceImpl")
	private InwardGetIrnDetailsDataParserService convertHdrLineService;

	@Autowired
	@Qualifier("InwardGetIrnListDataParserImpl")
	private InwardGetIrnListDataParser irnListParser;

	@Autowired
	@Qualifier("VendorMasterConfigEntityRepository")
	private VendorMasterConfigEntityRepository vendorConfigGstinDetailsRepository;

	@Autowired
	private GetInwardIrnDetailSectionHandler handler;

	@Autowired
	private ImsCountDataParserImpl impl;

	@Autowired
	@Qualifier("ImsInvoicesDataParserImpl")
	private ImsInvoicesDataParser imsListParser;

	@Autowired
	private GetIrnHeaderB2BRepository headerRepo;

	@Autowired
	@Qualifier("GetIrnListingRepository")
	private com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository GetIrnListingRepository;

	@Autowired
	@Qualifier("Gstr3bSummaryFileArrivalHandler")
	private Gstr3bSummaryFileArrivalHandler gstr3bSummaryFileArrivalHandler;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("ImsInvoicesProcCallServiceImpl")
	private ImsInvoicesProcCallService imsProcCallInvoiceParser;

	@Autowired
	private GetIrnDetailsJsonVsPdfReconServiceImpl seviceImpl;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private Gstr2BbatchUtil gstr2bBatchUtil;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Autowired
	private APIInvocationReqRepository reqRepo;

	@Autowired
	@Qualifier("APIResponseRepository")
	private APIResponseRepository respRepo;
	
	@Autowired
	private GetGstr2BManualApiStatusRepo apiStatusRepo;


	private static final List<String> EXT_LIST = ImmutableList.of("csv", "xlsx",
			"xlsm");

	private static final List<String> GETIMS_GOODS_TYPES = ImmutableList
			.copyOf(Arrays.asList(APIConstants.IMS_COUNT_TYPE_ALL_OTH,
					APIConstants.IMS_COUNT_TYPE_INV_SUPP_ISD,
					APIConstants.IMS_COUNT_TYPE_IMP_GDS));

	// @RequestMapping(value = "/ui/arithmeticCheckTaxAmount", method =
	// RequestMethod.POST)
	//
	// public String RcrAndRdrAmendmentValidator(
	// @RequestBody String RfvValidate2) {
	//
	// Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
	// new JsonDeserializer<LocalDate>() {
	// @Override
	// public LocalDate deserialize(JsonElement json, Type type,
	// JsonDeserializationContext jsonDeserializationContext)
	// throws JsonParseException {
	// return LocalDate
	// .parse(json.getAsJsonPrimitive().getAsString());
	// }
	// }).create();
	//
	// OutwardTransDocument obj = gson.fromJson(RfvValidate2,
	// OutwardTransDocument.class);
	//
	// List<ProcessingResult> processResult1 = arithmeticCheckTaxAmount
	// .validate(obj, null);
	//
	// return gson.toJson(processResult1);
	//
	// }
	//
	// @RequestMapping(value = "/ui/LUSgstinValidator", method =
	// RequestMethod.POST)
	// public String LUSgstinValidator(@RequestBody String RfvValidate2) {
	//
	// Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
	// new JsonDeserializer<LocalDate>() {
	// @Override
	// public LocalDate deserialize(JsonElement json, Type type,
	// JsonDeserializationContext jsonDeserializationContext)
	// throws JsonParseException {
	// return LocalDate
	// .parse(json.getAsJsonPrimitive().getAsString());
	// }
	// }).create();
	//
	// OutwardTransDocument obj = gson.fromJson(RfvValidate2,
	// OutwardTransDocument.class);
	//
	// List<ProcessingResult> processResult1 = LUSgstinValidator.validate(obj,
	// null);
	//
	// return gson.toJson(processResult1);
	//
	// }
	//
	// @RequestMapping(value = "/ui/LUCgstinStatecodeCheckValidator", method =
	// RequestMethod.POST)
	// public String LUCgstinStatecodeCheckValidator(
	// @RequestBody String RfvValidate2) {
	//
	// Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
	// new JsonDeserializer<LocalDate>() {
	// @Override
	// public LocalDate deserialize(JsonElement json, Type type,
	// JsonDeserializationContext jsonDeserializationContext)
	// throws JsonParseException {
	// return LocalDate
	// .parse(json.getAsJsonPrimitive().getAsString());
	// }
	// }).create();
	//
	// OutwardTransDocument obj = gson.fromJson(RfvValidate2,
	// OutwardTransDocument.class);
	//
	// List<ProcessingResult> processResult1 = LUCgstinStatecodeCheckValidator
	// .validate(obj, null);
	//
	// return gson.toJson(processResult1);
	//
	// }
	//
	// @RequestMapping(value = "/ui/CMOriginalCgstinValidator", method =
	// RequestMethod.POST)
	// public String CMOriginalCgstinValidator(@RequestBody String RfvValidate2)
	// {
	//
	// Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
	// new JsonDeserializer<LocalDate>() {
	// @Override
	// public LocalDate deserialize(JsonElement json, Type type,
	// JsonDeserializationContext jsonDeserializationContext)
	// throws JsonParseException {
	// return LocalDate
	// .parse(json.getAsJsonPrimitive().getAsString());
	// }
	// }).create();
	//
	// OutwardTransDocument obj = gson.fromJson(RfvValidate2,
	// OutwardTransDocument.class);
	//
	// List<ProcessingResult> processResult1 = CMOriginalCgstinValidator
	// .validate(obj, null);
	//
	// return gson.toJson(processResult1);
	//
	// }

	@PostMapping(value = "/ui/testRevIntg", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> test(@RequestBody String request) {

		PayloadDocsRevIntegrationReqDto dto = new PayloadDocsRevIntegrationReqDto();
		dto.setGroupcode("y8nvcqp4f9");
		dto.setPayloadId("12345678911");
		dto.setScenarioName("GstinValidatorPayloadMetadataRevIntg");
		dto.setSourceId("PRDCLNT800");

		return null;
	}

	@PostMapping(value = "/ui/createImsCountData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createImsCOuntData(

			@RequestHeader("groupCode") String groupCode,
			@RequestHeader("gstin") String gstin,
			@RequestHeader("type") String type,

			@RequestHeader("batchId") Long batchId,
			@RequestBody String request) {
		Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
		dto.setGstin(gstin);
		dto.setType(type);
		dto.setGroupcode(groupCode);

		impl.parseImsCountData(null, dto, batchId, request);
		return null;
	}

	@PostMapping(value = "/ui/createImsInvoiceData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createImsInvoiceData(
			@RequestHeader("groupCode") String groupCode,
			@RequestHeader("gstin") String gstin,
			@RequestHeader("type") String type,
			@RequestHeader("batchId") Long batchId,
			@RequestBody String request) {
		Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
		dto.setGstin(gstin);
		dto.setType(type);
		dto.setGroupcode(groupCode);

		imsListParser.parseImsInvoicesData(null, dto, batchId, request);
		imsProcCallInvoiceParser.procCall(dto, batchId);
		return null;
	}

	@PostMapping(value = "/ui/GetCallInwardIrnListExample", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr2AGstnGetJob(
			@RequestBody String request) {

		gstr1FileUploadUtil.processEInvoiceSRFile(new Message(),
				new AppExecContext(), new ArrayList<>());

		return null;

	}

	@PostMapping(value = "/ui/getGstr2bGetCallDummy", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public String getGstr2bGetCallDummy(@RequestHeader("gstin") String gstin,
			@RequestHeader("taxPeriod") String taxPeriod,
			@RequestBody String request) {

		try {
			Long invocationId;

			Gson gson = GsonUtil.newSAPGsonInstance();

			String groupCode = TenantContext.getTenantId();

			GetAnx1BatchEntity batch = null;
			Gstr2bGetInvoiceReqDto dto = new Gstr2bGetInvoiceReqDto();
			dto.setGstin(gstin);
			dto.setReturnPeriod(taxPeriod);
			dto.setGroupcode(TenantContext.getTenantId());

			batch = gstr2bBatchUtil.makeBatchGstr2B(dto);

			batchRepo.softlyDelete(APIConstants.GSTR2B_GET_ALL,
					APIConstants.GSTR2B, dto.getGstin(), dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);

			saveOrUpdateGstnGetStatus(gstin, taxPeriod);

			dto.setBatchId(batch.getId());
			String ctxParam = gson.toJson(dto);
			APIParam param1 = new APIParam(APIConstants.GSTR2B_GSTIN, gstin);
			APIParam param2 = new APIParam(APIConstants.GSTR2B_TaxPERIOD,
					taxPeriod);
			APIParam returnPeriodParam = new APIParam("ret_period", taxPeriod);
			APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
					"GSTR2B");

			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.GSTR2B_GET, param1,
					param2, returnPeriodParam, returnTypeParam);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Before invoking apiInvoker.invoke() "
								+ "method {} params {}, ctxParam {}",
						params.toString(), ctxParam);

			APIInvocationReqEntity result = createInvocReq(params, ctxParam,
					"Gstr2BGetSuccessHandler", "GSTR2BFailureHandler");

			//
			//
			// APIInvocationResult result = apiInvoker.invoke(params, null,
			// "Gstr2BGetSuccessHandler", "GSTR2BFailureHandler", ctxParam);

			batch.setStatus(APIConstants.INPROGRESS);
			batch.setRequestId(result.getId());
			batchRepo.save(batch);

			invocationId = result.getId();// updating invocationId

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Gstr2B GET successfully initiated for Gstin {}, "
								+ "TaxPeriod {} : transctionId %s {} ",
						gstin, taxPeriod, result.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Invoking softlyDeleteHeader : Gstr2BGetSuccessHandler");
			}

			String userName = "SYSTEM";
			int cntItc = itcSummaryRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : Itc Summary table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						cntItc, gstin, taxPeriod);
			}
			int suppSmryCnt = suppSummaryRepo.softlyDeleteHeader(gstin,
					taxPeriod, LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : Supplier Summary table Row "
								+ "deleted {} for gstin {}, taxPeriod {}",
						suppSmryCnt, gstin, taxPeriod);
			}
			int b2bCnt = b2bHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : B2B table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						b2bCnt, gstin, taxPeriod);
			}
			int b2bAcnt = b2bAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : B2BA  table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						b2bAcnt, gstin, taxPeriod);
			}
			int cdnrCnt = cdnrHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : CDNR table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						cdnrCnt, gstin, taxPeriod);
			}
			int cdnrACnt = cdnrAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : CDNRA table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						cdnrACnt, gstin, taxPeriod);
			}
			int isdcnt = isdHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : ISD table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						isdcnt, gstin, taxPeriod);
			}
			int isdACnt = isdAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : ISDA table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						isdACnt, gstin, taxPeriod);
			}
			int impgCnt = impgHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : IMPG table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						impgCnt, gstin, taxPeriod);
			}
			int impgSezCnt = impgSezHeaderRepo.softlyDeleteHeader(gstin,
					taxPeriod, LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : IMPG SEZ table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						impgSezCnt, gstin, taxPeriod);
				LOGGER.debug(
						"Invoked All softlyDeleteHeader : Gstr2BGetSuccessHandler");
			}
			int ecomCnt = ecomHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : Ecom table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						ecomCnt, gstin, taxPeriod);
			}
			int ecomAcnt = ecomaHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler :EcomA  table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						ecomAcnt, gstin, taxPeriod);
			}

			String apiResp = request;

			Gstr2BGETDataDto reqDto = gson.fromJson(apiResp,
					Gstr2BGETDataDto.class);

			// parsing and saving json
			parser.pasrseResp(true, reqDto, invocationId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GET2B Success Handler End of for Loop");
			}
			boolean isTokenResp = false;

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("gstin", gstin);
			jsonParams.addProperty("taxPeriod", taxPeriod);
			jsonParams.addProperty("invocationId", invocationId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" Gstr2b dup check proc method getting called for groupCode - {} ",
						groupCode);
			}

			String response = procCallHandler.callDupCheckProcs(gstin,
					taxPeriod, invocationId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" Gstr2b dup check proc method ended for groupCode - {} ",
						groupCode);
			}

			if (response != null && "SUCCESS".equalsIgnoreCase(response)) {
				batchRepo.updateStatus(APIConstants.SUCCESS,
						APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B, gstin,
						taxPeriod, LocalDateTime.now(), isTokenResp, null,
						null);

				if (false) {
					gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.SUCCESS, "");
					gstnStatusRepo.updateStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.SUCCESS, null,
							null, LocalDateTime.now());
				} else {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"GET2B Success Handler, About to post Report Job - {}",
								groupCode);

					gstnStatusRepo.updateStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.SUCCESS, null,
							null, LocalDateTime.now());
					asyncJobsService.createJob(groupCode,
							JobConstants.GSTR2B_GET_CSV_REPORT,
							jsonParams.toString(), userName, 1L, null, null);

					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"GET2B Success Handler, Processing is completed - {}",
								groupCode);
				}
			} else {
				String msg = "Gstr2b proc call return null as the response";

				if (false) {
					gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.FAILED, msg);
					gstnStatusRepo.updateStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
							LocalDateTime.now());
				} else {
					gstnStatusRepo.updateStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
							LocalDateTime.now());
				}
				batchRepo.updateStatus(APIConstants.FAILED,
						APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B, gstin,
						taxPeriod, LocalDateTime.now(), false, null, null);

			}

		} catch (Exception e) {
			LOGGER.error("Exception while handling success "
					+ "GSTR2BSingleResponseProcessor ", e);
			String msg = e.getMessage().length() > 500
					? e.getMessage().substring(0, 498) : e.getMessage();
			if (false) {
				gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.FAILED, msg);
				gstnStatusRepo.updateStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
						LocalDateTime.now());
			} else {
				gstnStatusRepo.updateStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
						LocalDateTime.now());
			}
			batchRepo.updateStatus(APIConstants.FAILED,
					APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B, gstin,
					taxPeriod, LocalDateTime.now(), false, null, null);
			throw new APIException(e.getLocalizedMessage());
		}

		return "SUCCESS";

	}

	@PostMapping(value = "/api/getGstr2bGetCallDummyNew", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public String getGstr2bGetCallDummyNew(@RequestParam("gstin") String gstin,
			@RequestParam("taxPeriod") String taxPeriod,
			@RequestParam("file") MultipartFile inputfile) {

		Long invocationId;
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			File tempDir = Files.createTempDir();
			String tempFileName = tempDir.getAbsolutePath() + File.separator
					+ inputfile.getOriginalFilename();

			File tempFile = new File(tempFileName);
			inputfile.transferTo(tempFile);

			String groupCode = TenantContext.getTenantId();

			GetAnx1BatchEntity batch = null;
			Gstr2bGetInvoiceReqDto dto = new Gstr2bGetInvoiceReqDto();
			dto.setGstin(gstin);
			dto.setReturnPeriod(taxPeriod);
			dto.setGroupcode(TenantContext.getTenantId());

			batch = gstr2bBatchUtil.makeBatchGstr2B(dto);

			batchRepo.softlyDelete(APIConstants.GSTR2B_GET_ALL,
					APIConstants.GSTR2B, dto.getGstin(), dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);

			saveOrUpdateGstnGetStatus(gstin, taxPeriod);

			dto.setBatchId(batch.getId());
			String ctxParam = gson.toJson(dto);
			APIParam param1 = new APIParam(APIConstants.GSTR2B_GSTIN, gstin);
			APIParam param2 = new APIParam(APIConstants.GSTR2B_TaxPERIOD,
					taxPeriod);
			APIParam returnPeriodParam = new APIParam("ret_period", taxPeriod);
			APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
					"GSTR2B");

			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.GSTR2B_GET, param1,
					param2, returnPeriodParam, returnTypeParam);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Before invoking apiInvoker.invoke() "
								+ "method {} params {}, ctxParam {}",
						params.toString(), ctxParam);

			APIInvocationReqEntity result = createInvocReq(params, ctxParam,
					"Gstr2BGetSuccessHandler", "GSTR2BFailureHandler");

			batch.setStatus(APIConstants.INPROGRESS);
			batch.setRequestId(result.getId());
			batchRepo.save(batch);

			invocationId = result.getId();

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Gstr2B GET successfully initiated for Gstin {}, "
								+ "TaxPeriod {} : transctionId %s {} ",
						gstin, taxPeriod, result.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Invoking softlyDeleteHeader : Gstr2BGetSuccessHandler");
			}

			String userName = "SYSTEM";

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GET2B Success Handler End of for Loop");
			}
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("gstin", gstin);
			jsonParams.addProperty("taxPeriod", taxPeriod);
			jsonParams.addProperty("invocationId", invocationId);

			Pair<String, String> uploadedDocDetails = DocumentUtility
					.uploadFile(tempFile, ConfigConstants.GSTR2B_RESPONSEFILE_UPLOADS);
			Gstr2BManualApiStatus apiStatus=  new Gstr2BManualApiStatus();
			apiStatus.setInvocationId(invocationId);
			apiStatus.setUpdatedBy(userName);
			apiStatus.setDateOfUpload(LocalDate.now());
			apiStatus.setStartTime(LocalDateTime.now());
			apiStatus.setFilePath(uploadedDocDetails.getValue0());
			apiStatus.setDocId(uploadedDocDetails.getValue1());
			apiStatus.setFileName(inputfile.getOriginalFilename	());
			apiStatus.setGstin(gstin);
			apiStatus.setTaxPeriod(taxPeriod);
			apiStatusRepo.save(apiStatus);
			asyncJobsService.createJob(groupCode, JobConstants.GSTR2B_API_PUSH,
					jsonParams.toString(), "SYSTEM", 1L, null, null);

		} catch (Exception e) {
			LOGGER.error("Exception while handling success "
					+ "GSTR2BSingleResponseProcessor ", e);
			String msg = e.getMessage().length() > 500
					? e.getMessage().substring(0, 498) : e.getMessage();
			if (false) {
				gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.FAILED, msg);
				gstnStatusRepo.updateStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
						LocalDateTime.now());
			} else {
				gstnStatusRepo.updateStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
						LocalDateTime.now());
			}
			batchRepo.updateStatus(APIConstants.FAILED,
					APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B, gstin,
					taxPeriod, LocalDateTime.now(), false, null, null);
			throw new APIException(e.getLocalizedMessage());
		}

		return "SUCCESS";

	}

	private APIInvocationReqEntity createInvocReq(APIParams apiParams,
			String params, String successHandler, String failureHandler) {
		Gson gson = new Gson();
		String apiParamsHash = Hashing.sha256()
				.hashString(gson.toJson(params), StandardCharsets.UTF_8)
				.toString();
		APIInvocationReqEntity invocationReqEntity = new APIInvocationReqEntity(
				apiParams.getAPIParamValue("gstin"),
				apiParams.getApiIdentifier(), params, apiParamsHash,
				GstnApiWrapperConstants.SUBMITTED, successHandler,
				failureHandler, "SYSTEM", LocalDateTime.now(), "SYSTEM",
				LocalDateTime.now());
		APIInvocationReqEntity reqEntity = reqRepo.save(invocationReqEntity);
		String msg = String.format(
				"Invoc Req created with reqId = %d And Api" + " Id = '%s'",
				invocationReqEntity.getId(),
				invocationReqEntity.getApiIdentifier());
		APILogger.logInfo(invocationReqEntity.getId(), msg);
		return reqEntity;

	}

	private void saveOrUpdateGstnGetStatus(String gstin, String taxPeriod) {

		GstnGetStatusEntity entity = gstnStatusRepo
				.findByGstinAndTaxPeriodAndReturnTypeAndSection(gstin,
						taxPeriod, APIConstants.GSTR2B,
						APIConstants.GSTR2B_GET_ALL);

		if (entity == null) {
			entity = new GstnGetStatusEntity();
			entity.setGstin(gstin);
			entity.setTaxPeriod(taxPeriod);
			entity.setDerivedTaxPeriod(
					GenUtil.convertTaxPeriodToInt(taxPeriod));
			entity.setCreatedOn(LocalDateTime.now());
			entity.setUpdatedOn(LocalDateTime.now());
			entity.setReturnType(APIConstants.GSTR2B);
			entity.setSection(APIConstants.GSTR2B_GET_ALL);
			entity.setUpdatedOn(LocalDateTime.now());
			entity.setStatus(APIConstants.INITIATED);
			gstnStatusRepo.save(entity);
		} else {
			gstnStatusRepo.updateStatus(gstin, taxPeriod, APIConstants.GSTR2B,
					APIConstants.INITIATED, null, null, LocalDateTime.now());

		}

	}

}