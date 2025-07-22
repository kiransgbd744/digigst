package com.ey.advisory.common;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnSAPResponseDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateIrnSAPResponseDto;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.EwbSAPResponseDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RestInvoiceDataExtractor implements InvoiceDataExecutor {
	@Override
	public JsonObject createCloudJson(List<ERPRequestLogEntity> restPayloads) {

		JsonParser jsonParser = new JsonParser();
		JsonArray reqarr = new JsonArray();
		JsonObject finalObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			for (ERPRequestLogEntity entityData : restPayloads) {
				JsonObject reqBodyObj = new JsonObject();
				JsonObject nicRespObj = new JsonObject();
				JsonObject nicReqObj = new JsonObject();
				String nicRespData = null;
				String nicReqpData = null;
				String reqBody = null;
				if (entityData.getApiType().equalsIgnoreCase(
						BusinessCriticalConstants.GENEWB_V3)) {
					nicRespData = entityData.getNicResPayload();
					reqBody = entityData.getReqPayload();
					nicReqpData = entityData.getNicReqPayload();
					// reqBodyObj = (JsonObject) jsonParser.parse(reqBody)
					// .getAsJsonObject().get("req");
					OutwardTransDocument hdr = gson.fromJson(
							(JsonObject) jsonParser.parse(reqBody)
									.getAsJsonObject().get("req"),
							OutwardTransDocument.class);
					setPreDocDtls(hdr);
					reqBodyObj = (JsonObject) gson.toJsonTree(hdr,
							OutwardTransDocument.class);
					nicRespObj = (JsonObject) jsonParser.parse(nicRespData);
					nicReqObj = (JsonObject) jsonParser.parse(nicReqpData);
					EwbResponseDto nicResp = gson.fromJson(
							nicRespObj.get("resp"), EwbResponseDto.class);
					EwbSAPResponseDto cloudResp = BusinessCommonUtil
							.convertEWBNICResptoCloud(nicResp, hdr, nicReqObj);
					reqBodyObj.add("ewbDtls", gson.toJsonTree(cloudResp));
				} else if (entityData.getApiType().equalsIgnoreCase(
						BusinessCriticalConstants.GENEWB_IRN_V3)) {
					nicRespData = entityData.getNicResPayload();
					reqBody = entityData.getReqPayload();
					nicReqpData = entityData.getNicReqPayload();
					reqBodyObj = (JsonObject) jsonParser.parse(reqBody)
							.getAsJsonObject().get("req");
					nicRespObj = (JsonObject) jsonParser.parse(nicRespData);
					nicReqObj = (JsonObject) jsonParser.parse(nicReqpData);
					GenerateEWBByIrnResponseDto nicResp = gson.fromJson(
							nicRespObj.get("resp"),
							GenerateEWBByIrnResponseDto.class);
					EwbSAPResponseDto cloudResp = BusinessCommonUtil
							.convertGENEWBIRNResptoCloud(nicResp, nicReqObj);
					reqBodyObj.add("ewbDtls", gson.toJsonTree(cloudResp));
				} else if (entityData.getApiType().equalsIgnoreCase(
						BusinessCriticalConstants.CANEWB_V3)) {
					nicRespData = entityData.getNicResPayload();
					reqBody = entityData.getReqPayload();
					nicReqpData = entityData.getNicReqPayload();
					reqBodyObj = (JsonObject) jsonParser.parse(reqBody)
							.getAsJsonObject().get("req").getAsJsonArray()
							.get(0);
					nicRespObj = (JsonObject) jsonParser.parse(nicRespData);
					nicReqObj = (JsonObject) jsonParser.parse(nicReqpData);
					CancelEwbResponseDto nicResp = gson.fromJson(
							nicRespObj.get("resp"), CancelEwbResponseDto.class);
					EwbSAPResponseDto cloudResp = BusinessCommonUtil
							.convertCANEWBNICResptoCloud(nicResp, nicReqObj);
					reqBodyObj.add("ewbDtls", gson.toJsonTree(cloudResp));

				} else if (entityData.getApiType().equalsIgnoreCase(
						BusinessCriticalConstants.GENEINV_V3)) {
					nicRespData = entityData.getNicResPayload();
					reqBody = entityData.getReqPayload();
					// reqBodyObj = (JsonObject) jsonParser.parse(reqBody)
					// .getAsJsonObject().get("req");
					nicRespObj = (JsonObject) jsonParser.parse(nicRespData);
					GenerateIrnResponseDto nicResp = gson.fromJson(
							nicRespObj.get("resp"),
							GenerateIrnResponseDto.class);
					OutwardTransDocument hdr = gson.fromJson(
							(JsonObject) jsonParser.parse(reqBody)
									.getAsJsonObject().get("req"),
							OutwardTransDocument.class);
					setPreDocDtls(hdr);
					reqBodyObj = (JsonObject) gson.toJsonTree(hdr,
							OutwardTransDocument.class);
					GenerateIrnSAPResponseDto cloudResp = BusinessCommonUtil
							.convertNICResptoCloud(nicResp);
					if (!Strings.isNullOrEmpty(nicResp.getEwbNo())) {
						EwbSAPResponseDto ewayBillDto = setEwayBillDetails(hdr,
								nicResp);
						reqBodyObj.add("ewbDtls", gson.toJsonTree(ewayBillDto));
					}
					reqBodyObj.add("eInvDtls", gson.toJsonTree(cloudResp));
				} else if (entityData.getApiType().equalsIgnoreCase(
						BusinessCriticalConstants.CANEINV_V3)) {
					nicRespData = entityData.getNicResPayload();
					reqBody = entityData.getReqPayload();
					nicReqpData = entityData.getNicReqPayload();
					reqBodyObj = (JsonObject) jsonParser.parse(reqBody)
							.getAsJsonObject().get("req");
					reqBodyObj.remove("irn");
					nicRespObj = (JsonObject) jsonParser.parse(nicRespData);
					nicReqObj = (JsonObject) jsonParser.parse(nicReqpData);
					CancelIrnERPResponseDto nicResp = gson.fromJson(
							nicRespObj.get("resp").getAsJsonArray().get(0),
							CancelIrnERPResponseDto.class);
					if (nicRespObj.get("hdr").getAsJsonObject().get("status")
							.getAsString().equalsIgnoreCase("s")) {
						CancelIrnSAPResponseDto cloudResp = BusinessCommonUtil
								.convertCanNICResptoCloud(nicResp, reqBodyObj);
						reqBodyObj.add("eInvDtls", gson.toJsonTree(cloudResp));
					}
				} else {
					LOGGER.debug("No AutoDrafting for Entity Type {} "
							+ entityData.getApiType());
				}
				reqBodyObj.addProperty("dataOriginTypeCode", "B");
				reqarr.add(reqBodyObj);
			}
		} catch (Exception e) {
			String msg = "Exception occured while Rest request";
			LOGGER.error(msg, e);
		}
		finalObj.add("req", reqarr);
		return finalObj;
	}

	private EwbSAPResponseDto setEwayBillDetails(OutwardTransDocument hdr,
			GenerateIrnResponseDto nicResp) {
		EwbSAPResponseDto ewayBillDto = new EwbSAPResponseDto();
		ewayBillDto.setEwayBillNo(nicResp.getEwbNo());
		ewayBillDto.setEwayBillDate(nicResp.getEwbDt());
		ewayBillDto.setValidUpto(nicResp.getEwbValidTill());
		ewayBillDto.setTransporterID(hdr.getTransporterID());
		ewayBillDto.setTransportMode(hdr.getTransportMode());
		ewayBillDto.setTransportDocNo(hdr.getTransportDocNo());
		ewayBillDto.setTransportDocDate(hdr.getTransportDocDate());
		ewayBillDto.setVehicleNo(hdr.getVehicleNo());
		ewayBillDto.setVehicleType(hdr.getVehicleType());
		ewayBillDto.setAspDistance(Integer.valueOf(nicResp.getNicDistance()));
		ewayBillDto.setFromPlace(
				EyEwbCommonUtil.getFromPlace(hdr.getDispatcherLocation(),
						hdr.getSupplierLocation(), hdr.getDocCategory()));
		ewayBillDto.setFromPincode(
				EyEwbCommonUtil.getFromPinocode(hdr.getDispatcherPincode(),
						hdr.getSupplierPincode(), hdr.getDocCategory()));
		ewayBillDto.setFromState(hdr.getSupplierStateCode() != null
				? hdr.getSupplierStateCode() : null);

		return ewayBillDto;

	}

	private static void setPreDocDtls(OutwardTransDocument hdr) {
		try {
			if (hdr.getPreDocDtls() != null && !hdr.getPreDocDtls().isEmpty()) {
				int preDocSize = hdr.getPreDocDtls().size();
				int lineItemSize = hdr.getLineItems().size();
				if (preDocSize >= lineItemSize) {
					for (int i = 0; i < lineItemSize; i++) {
						hdr.getLineItems().get(i)
								.setPreceedingInvoiceDate(hdr.getPreDocDtls()
										.get(i).getPreceedingInvoiceDate());
						hdr.getLineItems().get(i)
								.setPreceedingInvoiceNumber(hdr.getPreDocDtls()
										.get(i).getPreceedingInvoiceNumber());
					}
				} else if (preDocSize < lineItemSize && preDocSize != 1) {
					for (int i = 0; i < preDocSize; i++) {
						hdr.getLineItems().get(i)
								.setPreceedingInvoiceDate(hdr.getPreDocDtls()
										.get(i).getPreceedingInvoiceDate());
						hdr.getLineItems().get(i)
								.setPreceedingInvoiceNumber(hdr.getPreDocDtls()
										.get(i).getPreceedingInvoiceNumber());
					}
				} else if (preDocSize == 1) {
					for (int i = 0; i < lineItemSize; i++) {

						hdr.getLineItems().get(i)
								.setPreceedingInvoiceDate(hdr.getPreDocDtls()
										.get(0).getPreceedingInvoiceDate());
						hdr.getLineItems().get(i)
								.setPreceedingInvoiceNumber(hdr.getPreDocDtls()
										.get(0).getPreceedingInvoiceNumber());

					}
				} else {
					String msg = String.format(
							"PreDocDtls Condition didn't match, So skipping PreDocDtls Insertion");
					LOGGER.debug(msg);
				}
			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while setting the preDocDtls for DocNo %s",
					hdr.getDocNo());
			LOGGER.error(msg, e);
		}

	}
}