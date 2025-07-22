/**
 * 
 */
package com.ey.advisory.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
//import com.ey.advisory.app.data.repositories.client.EwbStatusMasterRepository;
import com.ey.advisory.app.data.services.ewb.EwbService;
import com.ey.advisory.app.data.services.ewb.EwbServiceUi;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.einv.dto.EInvEWBUIResponseDto;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsDtoReq;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbRequestListDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleRequestDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleGetFromAndToDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleGetVehicleRespDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleListGroupResp;
import com.ey.advisory.ewb.dto.EwbMultiVehicleListVehicleResp;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.GenerateEwbUIReqDto;
import com.ey.advisory.ewb.dto.GetEwayBillGeneratedByConsignerDto;
import com.ey.advisory.ewb.dto.GetEwbByDateResponseDto;
import com.ey.advisory.ewb.dto.GetEwbResponseDto;
import com.ey.advisory.ewb.dto.GetGSTINResponseDto;
import com.ey.advisory.ewb.dto.GetHsnDetailsResponseDto;
import com.ey.advisory.ewb.dto.GetPartBDetailsDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRequestDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRespDto;
import com.ey.advisory.ewb.dto.MultiVehicleDetailsRequestDto;
import com.ey.advisory.ewb.dto.RejectEWBUIReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@RestController
public class ManageEWBController {

	private static final String FAILED = "Failed";

	public static final Integer SYNC_SIZE = 5;

	private static final String EWB_VEHICLE_NOT_FOUND = "Vehicle number not found";

	@Autowired
	@Qualifier("EwbServiceImpl")
	private EwbService ewbService;

	@Autowired
	@Qualifier("EwbServiceUiImpl")
	private EwbServiceUi ewbServiceUi;

	@Autowired
	private LoggerAdviceHelper loggerAdviceHelper;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@PostMapping(value = "/ui/generateEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateEwayBillUi(
			@RequestBody String jsonString) {
		// PerfUtil.logEventToFile("GENERATE_EWB", "GENERATE_EWB_BEGIN",
		// "ManageEWBController", "generateEwayBill", "");
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson ewbGson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			GenerateEwbUIReqDto req = gson.fromJson(obj.get("req"),
					GenerateEwbUIReqDto.class);

			List<Long> docList = req.getDocIdList();
			List<EInvEWBUIResponseDto> respList = new ArrayList<>();

			if (SYNC_SIZE > docList.size()) {
				docList.forEach(o -> {
					try {
						loggerAdviceHelper.createReqLog(String.valueOf(o),
								APIIdentifiers.GENERATE_EWB);
						String geneEwbResponse = ewbServiceUi
								.generateEwbSync(o);

						Optional<OutwardTransDocument> doc = docrepo
								.findById(o);

						EInvEWBUIResponseDto respDto = new EInvEWBUIResponseDto(
								o, doc.get().getDocNo(), geneEwbResponse);
						respList.add(respDto);
					} catch (Exception e) {
						reqLogHelper.updateResponsePayload(e.getMessage(),
								false);
						LOGGER.error("ERROR IN  Generate EWB  Iteration Id's",
								e);
					} finally {
						reqLogHelper.saveLogEntity();
					}
				});
				resp.add("resp", gson.toJsonTree(respList));
			} else {
				String asynStr = ewbServiceUi.generateEwbAsync(docList);
				resp.add("resp", gson.toJsonTree(asynStr));
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception in generating eway bill with request ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", ewbGson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/cancelEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> cancelEwayBillUi(
			@RequestBody String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject resp = new JsonObject();

			CancelEwbRequestListDto dtoList = gson.fromJson(jsonString,
					CancelEwbRequestListDto.class);
			List<CancelEwbReqDto> reqList = dtoList.getCancelEwbReqDtoList();
			List<EInvEWBUIResponseDto> respList = new ArrayList<>();
			if (SYNC_SIZE > reqList.size()) {
				reqList.forEach(o -> {
					Pair<String, String> responsepair;
					try {
						loggerAdviceHelper.createReqLog(
								String.valueOf(o.getDocHeaderId()),
								APIIdentifiers.CANCEL_EWB);
						responsepair = ewbServiceUi.cancelEwbSync(o);
						String responsedata = responsepair.getValue1();
						EInvEWBUIResponseDto respDto = new EInvEWBUIResponseDto(
								o.getDocHeaderId(), responsedata);
						respList.add(respDto);
					} catch (Exception e) {
						LOGGER.error("ERROR IN  Cancel EWB  Iteration Id's", e);
						reqLogHelper.updateResponsePayload(e.getMessage(),
								false);
					} finally {
						reqLogHelper.saveLogEntity();
					}
				});
				resp.add("resp", gson.toJsonTree(respList));
			} else {

				String asynStr = ewbServiceUi.cancelEwbAsync(dtoList);
				resp.add("resp", gson.toJsonTree(asynStr));
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception in cancelling eway bill with request ", ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.add("resp",
					gson.toJsonTree(
							"Excpeion while Processing the Cancel EWB Req "
									+ ex.getMessage()));

			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} finally {
			TenantContext.clearTenant();
		}
	}

	@PostMapping(value = "/ui/updatePartBEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updatePartBEwayBillUi(
			@RequestBody String jsonString) {
		return updatePartBEwayBill(jsonString);
	}

	private ResponseEntity<String> updatePartBEwayBill(String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			JsonObject resp = new JsonObject();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			Type updatePartBListType = new TypeToken<List<UpdatePartBEwbRequestDto>>() {
			}.getType();
			List<UpdatePartBEwbRequestDto> dtoList = gson
					.fromJson(obj.get("req"), updatePartBListType);

			List<EInvEWBUIResponseDto> respList = new ArrayList<>();
			if (SYNC_SIZE > dtoList.size()) {
				dtoList.forEach(o -> {
					Pair<String, String> responsepair;
					try {
						loggerAdviceHelper.createReqLog(
								String.valueOf(o.getDocHeaderId()),
								APIIdentifiers.UPDATE_PARTB);
						responsepair = ewbServiceUi.updatePartEwbSync(o);
						String responsedata = responsepair.getValue1();
						EInvEWBUIResponseDto respDto = new EInvEWBUIResponseDto(
								o.getDocHeaderId(), responsedata);
						respList.add(respDto);
					} catch (Exception e) {

						LOGGER.error("ERROR IN  Update Part-B for DocId {}",
								o.getDocHeaderId(), e);
						reqLogHelper.updateResponsePayload(e.getMessage(),
								false);
					} finally {
						reqLogHelper.saveLogEntity();
					}
				});
				resp.add("resp", gson.toJsonTree(respList));
			} else {
				String asynStr = ewbServiceUi.updatePartEwbASync(dtoList);
				resp.add("resp", gson.toJsonTree(asynStr));
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error(
					"Exception in updating ewayBill eway bill with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/api/consolidateEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> consolidateEwayBillApi(
			@RequestBody String jsonString) {
		return consolidateEwayBill(jsonString);
	}

	@PostMapping(value = "/ui/consolidateEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> consolidateEwayBillUi(
			@RequestBody String jsonString) {
		return consolidateEwayBill(jsonString);
	}

	private ResponseEntity<String> consolidateEwayBill(String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			Type consolidateListType = new TypeToken<List<ConsolidateEWBReqDto>>() {
			}.getType();
			List<ConsolidateEWBReqDto> dtoList = gson.fromJson(obj.get("req"),
					consolidateListType);
			String response = ewbServiceUi.consolidateEwb(dtoList);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gsonEwb.toJsonTree(response));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (NullPointerException ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gsonEwb.toJsonTree("Exception in parsing the request"));
			LOGGER.error(
					"Exception in consolidate ewayBill eway bill with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error(
					"Exception in consolidate ewayBill eway bill with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/extendEwbValidity", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> extendEwbValidityUi(
			@RequestBody String jsonString) {
		return extendEwbValidity(jsonString);
	}

	private ResponseEntity<String> extendEwbValidity(String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			JsonObject resp = new JsonObject();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			Type extendListType = new TypeToken<List<ExtendEWBReqDto>>() {
			}.getType();
			List<ExtendEWBReqDto> dtoList = gson.fromJson(obj.get("req"),
					extendListType);
			List<EInvEWBUIResponseDto> respList = new ArrayList<>();
			if (SYNC_SIZE > dtoList.size()) {
				dtoList.forEach(o -> {
					Pair<String, String> responsepair;
					try {
						loggerAdviceHelper.createReqLog(
								String.valueOf(o.getDocHeaderId()),
								APIIdentifiers.EXTEND_VEHICLE_DETAILS);
						responsepair = ewbServiceUi.extendEwbSync(o);
						String responsedata = responsepair.getValue1();
						EInvEWBUIResponseDto respDto = new EInvEWBUIResponseDto(
								o.getDocHeaderId(), responsedata);
						respList.add(respDto);
					} catch (Exception e) {
						LOGGER.error(
								"ERROR IN  UpdatePart B EWB  Iteration Id's",
								e);
					} finally {
						reqLogHelper.saveLogEntity();
					}
				});
				resp.add("resp", gson.toJsonTree(respList));
			} else {

				String asynStr = ewbServiceUi.extendEwbAsync(dtoList);
				resp.add("resp", gson.toJsonTree(asynStr));
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (NullPointerException ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gsonEwb.toJsonTree("Exception in parsing the request"));
			LOGGER.error("Exception in extend ewayBill eway bill with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception in extend ewayBill eway bill with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/updateEwbTransporter", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateEwbTransporterUi(
			@RequestBody String jsonString) {
		return updateEwbTransporter(jsonString);
	}

	private ResponseEntity<String> updateEwbTransporter(String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			JsonObject resp = new JsonObject();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			Type updateTransporterListType = new TypeToken<List<UpdateEWBTransporterReqDto>>() {
			}.getType();
			List<UpdateEWBTransporterReqDto> dtoList = gson
					.fromJson(obj.get("req"), updateTransporterListType);
			List<EInvEWBUIResponseDto> respList = new ArrayList<>();
			if (SYNC_SIZE > dtoList.size()) {
				dtoList.forEach(o -> {
					Pair<String, String> responsepair;
					try {
						loggerAdviceHelper.createReqLog(
								String.valueOf(o.getDocHeaderId()),
								APIIdentifiers.UPDATE_TRANSPORTER);
						responsepair = ewbServiceUi.updateTransporterSync(o);
						String responsedata = responsepair.getValue1();
						EInvEWBUIResponseDto respDto = new EInvEWBUIResponseDto(
								o.getDocHeaderId(), responsedata);
						respList.add(respDto);
					} catch (Exception e) {
						LOGGER.error(
								"ERROR IN  UpdatePart Transporter for DocId {}",
								o.getDocHeaderId(), e);
					} finally {
						reqLogHelper.saveLogEntity();
					}
				});
				resp.add("resp", gson.toJsonTree(respList));
			} else {

				String asynStr = ewbServiceUi.updateTransporterASync(dtoList);
				resp.add("resp", gson.toJsonTree(asynStr));
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception updateTransporter eway bill with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/api/getEwb", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEwbApi(@RequestBody String jsonString) {
		return getEwb(jsonString);
	}

	@PostMapping(value = "/ui/getEwb", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEwbUi(@RequestBody String jsonString) {
		return getEwb(jsonString);
	}

	private ResponseEntity<String> getEwb(String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			JsonObject req = obj.get("req").getAsJsonObject();
			String ewbNo = req.get("ewbNo").getAsString();
			String gstin = req.get("gstin").getAsString();
			if (Strings.isNullOrEmpty(ewbNo) || Strings.isNullOrEmpty(gstin))
				throw new AppException("ewbNo and gstin Both are mandatory.");
			gstin = "29ABYPR4788F1ZJ";
			GetEwbResponseDto response = ewbService.getEWB(ewbNo, gstin, true,
					false);
			JsonObject resp = new JsonObject();
			if (response.getItemList() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));

			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));

			}
			resp.add("resp", gsonEwb.toJsonTree(response));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (NullPointerException ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception in get eway bill with request ", ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception get eway bill with request ", ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getPartBDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPartBDetails(
			@RequestBody String jsonString) {
		return getPartBDetail(jsonString);
	}

	private ResponseEntity<String> getPartBDetail(String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			JsonObject req = obj.get("req").getAsJsonObject();
			JsonArray ewbNosArray = req.get("ewbNo").getAsJsonArray();
			Type listType = new TypeToken<List<String>>() {
			}.getType();
			List<String> ewbNos = gson.fromJson(ewbNosArray, listType);
			List<GetPartBDetailsDto> response = ewbServiceUi
					.getPartBDetailsByEwbNo(ewbNos);
			JsonObject resp = new JsonObject();
			if (response != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			}
			resp.add("resp", gsonEwb.toJsonTree(response));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception get updatepartb details with request ", ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/rejectEwayBill", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> rejectEwayBillUi(
			@RequestBody String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		Gson ewbGson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			RejectEWBUIReqDto req = gson.fromJson(obj.get("req"),
					RejectEWBUIReqDto.class);
			List<Long> docIdList = req.getDocIdList();
			List<EInvEWBUIResponseDto> respList = new ArrayList<>();
			if (LOGGER.isDebugEnabled()) {
				String msg = "End of Reject Eway Bill";
				LOGGER.debug(msg);
			}
			docIdList.forEach(o -> {
				EInvEWBUIResponseDto respDto = null;
				try {
					String r̥ejectewbResp = ewbServiceUi.rejectEwbSync(o);
					respDto = new EInvEWBUIResponseDto(o, r̥ejectewbResp);
				} catch (Exception e) {
					LOGGER.error("ERROR IN  Reject EWB Iteration Id's", e);
					respDto = new EInvEWBUIResponseDto(o, e.getMessage());
				}
				respList.add(respDto);
			});
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", ewbGson.toJsonTree(respList));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception in Reject eway bill with request ", ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.add("resp",
					gson.toJsonTree(
							"Excpeion while Processing the Reject EWB Req "
									+ ex.getMessage()));

			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} finally {
			TenantContext.clearTenant();
		}
	}

	@PostMapping(value = "/ui/initiateMultiVehicle", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> initiateMultiVehicleApi(
			@RequestBody String jsonString) {
		return initiateMultiVehicle(jsonString);
	}

	private ResponseEntity<String> initiateMultiVehicle(String jsonString) {
		PerfUtil.logEventToFile("INITIATE_MULTIVEHICLE",
				"INITIATE_MULTIVEHICLE_BEGIN", "ManageEWBController",
				"initiateMultiVehicle", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		InitiateMultiVehicleRespDto response;
		AddMultiVehicleDetailsRespDto resp;
		@SuppressWarnings("unused")
		boolean nicStatus = false;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			InitiateMultiVehicleRequestDto dto = gson.fromJson(obj.get("req"),
					InitiateMultiVehicleRequestDto.class);

			if (dto.getEwbNo() == null || dto.getTotalQuantity() == null
					|| dto.getTransMode() == null || dto.getFromPlace() == null
					|| dto.getToPlace() == null || dto.getFromState() == null
					|| dto.getToState() == null) {

				String msg = "EWB Number, total Quantity, transMode,"
						+ " From Place, To Place, From State, To Sate"
						+ " should be mandatory";
				JsonObject respon = new JsonObject();
				respon.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				respon.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(respon.toString(),
						HttpStatus.OK);
			}

			String gstin = dto.getGstin();

			LOGGER.info("Initiate multi vehicle NIC call started.....");
			response = ewbService.initiateMultiVehicles(dto, gstin);

			JsonObject res = new JsonObject();
			if (response.getGroupNo() == null
					|| response.getGroupNo().isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"There is no response for initiate multi vehcile "
									+ response);
				}
				res.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				res.add("resp", gsonEwb.toJsonTree(response));
				return new ResponseEntity<>(res.toString(), HttpStatus.OK);
			}

			List<AddMultiVehicleDetailsDtoReq> req = dto.getVechicleDetails();

			if (dto.getVechicleDetails() == null
					|| dto.getVechicleDetails().isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Group number is : " + response);
				}
				if (response.getGroupNo() != null) {
					res.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					res.add("resp",
							gsonEwb.toJsonTree("Initiated Successfully"));
					return new ResponseEntity<>(res.toString(), HttpStatus.OK);
				} else if (response.getErrorMessage() != null
						|| !response.getErrorMessage().isEmpty()) {
					res.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
					res.add("resp", gsonEwb.toJsonTree(response));
					return new ResponseEntity<>(res.toString(), HttpStatus.OK);
				}

			}
			JsonArray respBody = new JsonArray();
			for (AddMultiVehicleDetailsDtoReq reqDto : req) {

				if (dto.getTransMode().equalsIgnoreCase("1")) {
					if (reqDto.getVehicleNo() == null
							|| reqDto.getVehicleNo().isEmpty()) {
						String msg = "Please Fill the Required Details";
						JsonObject respon = new JsonObject();
						respon.add("hdr",
								gson.toJsonTree(APIRespDto.creatErrorResp()));
						respon.add("resp", gson.toJsonTree(msg));
						return new ResponseEntity<String>(respon.toString(),
								HttpStatus.OK);
					}
				} else if (dto.getTransMode().equalsIgnoreCase("2")
						|| dto.getTransMode().equalsIgnoreCase("3")
						|| dto.getTransMode().equalsIgnoreCase("4")
						|| dto.getTransMode().equalsIgnoreCase("5")) {

					if (reqDto.getTransDocDate() == null
							|| reqDto.getTransDocDate().isEmpty()) {
						String msg = "Please Fill the Required Details";
						JsonObject respon = new JsonObject();
						respon.add("hdr",
								gson.toJsonTree(APIRespDto.creatErrorResp()));
						respon.add("resp", gson.toJsonTree(msg));
						return new ResponseEntity<String>(respon.toString(),
								HttpStatus.OK);
					}
					if (reqDto.getTransDocNo() == null
							|| reqDto.getTransDocNo().isEmpty()) {
						String msg = "Please Fill the Required Details";
						JsonObject respon = new JsonObject();
						respon.add("hdr",
								gson.toJsonTree(APIRespDto.creatErrorResp()));
						respon.add("resp", gson.toJsonTree(msg));
						return new ResponseEntity<String>(respon.toString(),
								HttpStatus.OK);
					}
				}

				JsonObject json = new JsonObject();
				reqDto.setEwbNo(dto.getEwbNo());
				reqDto.setGroupNo(response.groupNo);
				reqDto.setGstin(dto.getGstin());
				String vehicleNo = reqDto.getVehicleNo();
				String gstinNo = reqDto.getGstin();
				resp = ewbService.addMultiVehicles(reqDto, gstinNo);
				json.addProperty("vehicleNo", vehicleNo.toString());
				if (resp.getErrorMessage() != null
						&& !resp.getErrorMessage().isEmpty()) {
					String errorMesage = resp.getErrorMessage();
					json.addProperty("msg", errorMesage);
				} else if (resp.getMessage() != null
						&& !resp.getMessage().isEmpty()) {
					String message = resp.getMessage();
					json.addProperty("msg", message);
				}
				respBody.add(json);
			}

			res.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			res.add("resp", gsonEwb.toJsonTree(respBody));
			return new ResponseEntity<>(res.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject erroResp = new JsonObject();
			response = new InitiateMultiVehicleRespDto();
			response.setErrorCode("INTERNALEXP");
			response.setErrorMessage(ex.getMessage());
			erroResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			erroResp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in initiate multi vehicle eway bill with request ",
					ex);
			return new ResponseEntity<>(erroResp.toString(), HttpStatus.OK);
		} finally {
			PerfUtil.logEventToFile("INITIATE_MULTIVEHICLE",
					"INITIATE_MULTIVEHICLE_END", "ManageEWBController",
					"initiateMultiVehicle", "");
		}
	}

	@PostMapping(value = "/ui/addMultiVehicle", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> addMultiVehicleApi(
			@RequestBody String jsonString) {
		return addMultiVehicle(jsonString);
	}

	private ResponseEntity<String> addMultiVehicle(String jsonString) {
		PerfUtil.logEventToFile("ADD MULTIVEHICLE", "ADD_MULTIVEHICLE",
				"ManageEWBController", "addMultiVehicle", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		AddMultiVehicleDetailsRespDto response;
		@SuppressWarnings("unused")
		boolean nicStatus = false;
		try {
			JsonArray jsonReqArray = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Requested Json for Add MultiVehicle :" + jsonReqArray);
			}

			Type listType = new TypeToken<List<AddMultiVehicleDetailsDtoReq>>() {
			}.getType();

			List<AddMultiVehicleDetailsDtoReq> dto = gson.fromJson(jsonReqArray,
					listType);

			JsonArray respBody = new JsonArray();
			for (AddMultiVehicleDetailsDtoReq req : dto) {
				JsonObject json = new JsonObject();
				String vehicleNo = req.getVehicleNo();
				String gstin = req.getGstin();
				response = ewbService.addMultiVehicles(req, gstin);
				json.addProperty("vehicleNo", vehicleNo.toString());
				if (response.getErrorMessage() != null
						&& !response.getErrorMessage().isEmpty()) {
					String errorMesage = response.getErrorMessage();
					json.addProperty("msg", errorMesage);
				} else if (response.getMessage() != null
						&& !response.getMessage().isEmpty()) {
					String message = response.getMessage();
					json.addProperty("msg", message);
				}
				respBody.add(json);
			}

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new AddMultiVehicleDetailsRespDto();
			response.setErrorCode("INTERNALEXP");
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in add multi vehicle eway bill with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			PerfUtil.logEventToFile("UPDATE_TRANSPORTER",
					"UPDATE_TRANSPORTER_END", "ManageEWBController",
					"updateEwbTransporter", "");
		}
	}

	@PostMapping(value = "/ui/changeMultiVehicle", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> changeMultiVehicleApi(
			@RequestBody String jsonString) {
		return changeMultiVehicle(jsonString);
	}

	private ResponseEntity<String> changeMultiVehicle(String jsonString) {
		PerfUtil.logEventToFile("CHANGE MULTIVEHICLE", "CHANGE_MULTIVEHICLE",
				"ManageEWBController", "changeMultiVehicle", "");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		ChangeMultiVehicleDetailsRespDto response;
		@SuppressWarnings("unused")
		boolean nicStatus = false;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + obj);
			}
			ChangeMultiVehicleRequestDto dto = gson.fromJson(obj.get("req"),
					ChangeMultiVehicleRequestDto.class);
			String gstin = dto.getGstin();
			response = ewbService.changeMultiVehicles(dto, gstin);
			JsonObject resp = new JsonObject();
			if (response.getMessage() != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gsonEwb.toJsonTree(response));
				nicStatus = true;
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gsonEwb.toJsonTree(response));
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject resp = new JsonObject();
			response = new ChangeMultiVehicleDetailsRespDto();
			response.setErrorCode("INTERNALEXP");
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			LOGGER.error(
					"Exception in change multi vehicle eway bill with request ",
					ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			PerfUtil.logEventToFile("CHANGE MULTIVEHICLE",
					"CHANGE MULTIVEHICLE_END", "ManageEWBController",
					"changeMultiVehicle", "");
		}
	}

	@PostMapping(value = "/ui/getGroupsNoForEwb", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGroupsNoForEwb(
			@RequestBody String jsonString) throws IOException {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			InitiateMultiVehicleRequestDto req = gson.fromJson(
					reqJson.toString(), InitiateMultiVehicleRequestDto.class);

			if (req.getEwbNo() == null) {
				String msg = "EWB Number should be mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			Long eWbno = req.getEwbNo();
			List<EwbMultiVehicleListGroupResp> listOfGroups = ewbService
					.getGroupNumber(eWbno);
			if (!CollectionUtils.isEmpty(listOfGroups)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(listOfGroups);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "Group number not found";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Recipient list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/listEwbFromAndToData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEwbFromAndToData(
			@RequestBody String jsonString) {

		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			InitiateMultiVehicleRequestDto req = gson.fromJson(
					reqJson.toString(), InitiateMultiVehicleRequestDto.class);

			Long eWbno = req.getEwbNo();

			EwbMultiVehicleGetFromAndToDto dtoResponse = ewbService
					.getEwbFromAndToData(eWbno);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(dtoResponse));
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/getVehicleNumber", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVehicleNumber(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			AddMultiVehicleDetailsDtoReq req = gson.fromJson(reqJson.toString(),
					AddMultiVehicleDetailsDtoReq.class);

			if (req.getEwbNo() == null || req.getGroupNo() == null) {
				String msg = "EWB number and Group numbershould be mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			Long eWbno = req.getEwbNo();
			String groupNo = req.getGroupNo();
			List<EwbMultiVehicleListVehicleResp> listOfVehicle = ewbService
					.listVehicleNumber(eWbno, groupNo);

			JsonObject resps = new JsonObject();

			if (listOfVehicle.get(0).vehicleNo == null
					|| listOfVehicle.get(0).vehicleNo.isEmpty()) {
				
				List<EwbMultiVehicleListVehicleResp> listOfVeh = new ArrayList<>();
				
				JsonElement respBody = gson.toJsonTree(listOfVeh);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}

			if (!CollectionUtils.isEmpty(listOfVehicle)) {

				JsonElement respBody = gson.toJsonTree(listOfVehicle);

				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
			throw new AppException(EWB_VEHICLE_NOT_FOUND);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Recipient list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/GetHsnDetailsByHsnCode", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getHsnDetailsByHsnCode(
			@RequestBody String jsonString) {
		boolean nicStatus = false;
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject resp = null;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GetHsnDetailsByHsnCode(..) :: Request Json " + obj);
			}
			JsonObject req = obj.get("req").getAsJsonObject();
			String hsnCode = req.get("hsncode").getAsString();
			String gstin = req.get("gstin").getAsString();
			if (Strings.isNullOrEmpty(hsnCode) || Strings.isNullOrEmpty(gstin))
				throw new AppException("hsncode and gstin Both are mandatory.");

			GetHsnDetailsResponseDto response = ewbService
					.getHsnDetails(hsnCode, gstin, true, false);
			if (LOGGER.isDebugEnabled()) {
				Gson gson2 = new Gson();
				LOGGER.debug("GetHsnDetailsResponseDto  ::"
						+ gson2.toJson(response));
			}
			resp = new JsonObject();
			// adding hdr as S or E
			if (response.getErrorCode() != null) {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				nicStatus = true;
			} else {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			}

			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			// adding res
			resp.add("resp", gsonEwb.toJsonTree(response));

		} catch (Exception ex) {
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception to get HsnDetails with request ", ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/GetEwayBillsByDate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEwayBillsByDate(
			@RequestBody String jsonString) {
		boolean nicStatus = false;
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject resp = null;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getEwayBillsByDate(..) :: Request Json " + obj);
			}
			JsonObject req = obj.get("req").getAsJsonObject();
			String date = req.get("date").getAsString();
			String gstin = req.get("gstin").getAsString();

			if (Strings.isNullOrEmpty(date) || Strings.isNullOrEmpty(gstin))
				throw new AppException("date and gstin Both are mandatory.");

			List<GetEwbByDateResponseDto> getEwbByDateResponseList = ewbService
					.getEWBByDate(date, gstin, true, false);

			if (LOGGER.isDebugEnabled()) {
				Gson gson2 = new Gson();
				LOGGER.debug("GetHsnDetailsResponseDto  ::"
						+ gson2.toJson(getEwbByDateResponseList));
			}
			resp = new JsonObject();
			// adding hdr as S or E
			if (getEwbByDateResponseList != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			}

			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			// adding res
			resp.add("resp", gsonEwb.toJsonTree(getEwbByDateResponseList));

		} catch (Exception ex) {
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception to get HsnDetails with request ", ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/GetGSTINDetails", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getGSTINDetails(
			@RequestBody String jsonString) {
		boolean nicStatus = false;
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject resp = null;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GetGSTINDetails(..) :: Request Json " + obj);
			}
			JsonObject req = obj.get("req").getAsJsonObject();
			String getGstin = req.get("getGstin").getAsString();
			String userGstin = req.get("userGstin").getAsString();
			if (Strings.isNullOrEmpty(getGstin)
					|| Strings.isNullOrEmpty(userGstin))
				throw new AppException("gstin and getgstin are mandatory.");

			GetGSTINResponseDto response = ewbService.getGSTINDetails(getGstin,
					userGstin);
			if (LOGGER.isDebugEnabled()) {
				Gson gson2 = new Gson();
				LOGGER.debug(
						"GetGSTINResponseDto  ::" + gson2.toJson(response));
			}
			resp = new JsonObject();
			// adding hdr as S or E
			if (Strings.isNullOrEmpty(response.getErrorCode())) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			}

			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			// adding res
			resp.add("resp", gsonEwb.toJsonTree(response));

		} catch (Exception ex) {
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception to get GSTINDetails with request ", ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/GetEwayBillGeneratedByConsigner", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEwayBillGeneratedByConsigner(
			@RequestBody String jsonString) {
		boolean nicStatus = false;
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject resp = null;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"getEwayBillGeneratedByConsigner(..) :: Request Json "
								+ obj);
			}
			JsonObject req = obj.get("req").getAsJsonObject();

			String gstin = req.get("gstin").getAsString();
			String docType = req.get("docType").getAsString();
			String docNo = req.get("docNo").getAsString();

			if (Strings.isNullOrEmpty(docType) || Strings.isNullOrEmpty(gstin)
					|| Strings.isNullOrEmpty(docNo))
				throw new AppException(
						"docType, docNo and gstin all are mandatory.");

			GetEwayBillGeneratedByConsignerDto response = ewbService
					.getEwayBillGeneratedByConsigner(docType, docNo, gstin);

			if (LOGGER.isDebugEnabled()) {
				Gson gson2 = new Gson();
				LOGGER.debug("getEwayBillGeneratedByConsigner  ::"
						+ gson2.toJson(response));
			}
			resp = new JsonObject();
			// adding hdr as S or E
			if (response != null) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				nicStatus = true;
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			}

			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
			// adding res
			resp.add("resp", gsonEwb.toJsonTree(response));

		} catch (NullPointerException ex) {
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error(
					"Exception to get EwayBillGeneratedByConsigner with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
		} catch (Exception ex) {
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gsonEwb.toJsonTree(ex.getMessage()));
			LOGGER.error(
					"Exception to get EwayBillGeneratedByConsigner with request ",
					ex);
			reqLogHelper.updateResponsePayload(resp.toString(), nicStatus);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/getMultiVehicleDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMultiVehicleDetails(
			@RequestBody String jsonString) throws IOException {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + requestObject);
			}

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			MultiVehicleDetailsRequestDto req = gson.fromJson(
					reqJson.toString(), MultiVehicleDetailsRequestDto.class);

			if (req.getEwbNo() == null) {
				String msg = "EWB Number should be mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			Long eWbno = req.getEwbNo();
			String docNo = req.getDocNum();
			String supplierGstin = req.getSuppGSTIN();
			List<EwbMultiVehicleDetailsRespDto> listAll = ewbService
					.getMultiVehicleDetails(eWbno, docNo, supplierGstin);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(listAll);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Recipient list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getVehiclesForGroup", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVehiclesForGroup(
			@RequestBody String jsonString) throws IOException {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			AddMultiVehicleDetailsDtoReq req = gson.fromJson(reqJson.toString(),
					AddMultiVehicleDetailsDtoReq.class);

			if (req.getEwbNo() == null || req.getGroupNo() == null) {
				String msg = "EWB Number should be mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			Long eWbno = req.getEwbNo();
			String groupNo = req.getGroupNo();
			EwbMultiVehicleGetVehicleRespDto listVehicle = ewbService
					.getVehiclesforGroup(eWbno, groupNo);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(listVehicle);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Recipient list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

}
