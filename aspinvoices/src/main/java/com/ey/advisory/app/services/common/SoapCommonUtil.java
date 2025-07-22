package com.ey.advisory.app.services.common;

import com.ey.advisory.itc.reversal.api.dto.ITCReversal180DayApiReqDocument;
import com.ey.advisory.itc.reversal.api.dto.ITCReversal180DayApiSoapHdrRq;
import com.ey.advisory.itc.reversal.api.dto.ITCReversal180DayApiTransRq;
import com.ey.advisory.soap.integration.api.dto.GstinValidatorApiReqDocument;
import com.ey.advisory.soap.integration.api.dto.GstinValidatorPayloadHdrDataSoapDto;
import com.ey.advisory.soap.integration.api.dto.GstinValidatorPayloadLineDataSoapDto;
import com.ey.advisory.vendor.master.api.dto.VendorMasterApiReqDocument;
import com.ey.advisory.vendor.master.api.dto.VendorMasterApiSoapHdrRq;
import com.ey.advisory.vendor.master.api.dto.VendorMasterApiTransRq;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SoapCommonUtil {

	public static VendorMasterApiReqDocument getVendorMasterApiReq(
			VendorMasterApiSoapHdrRq document) {

		VendorMasterApiReqDocument req = new VendorMasterApiReqDocument();
		req.setIdTokens(document.getIdToken());
		req.setPayloadId(document.getPayloadId());
		req.setCompanyCode(document.getCompanyCode());
		req.setSourceId(document.getSourceId());
		req.setCheckSum("NOCHECKSUM");
		req.setPushType("2");

		JsonArray jsonArray = new JsonArray();
		Gson gson = new Gson();
		for (VendorMasterApiTransRq trans : document.getLineItems()) {
			JsonObject element = new JsonObject();
			element.addProperty("recipientPAN", trans.getRecipientPan());
			element.addProperty("vendorPAN", trans.getVendorPAN());
			element.addProperty("vendorGstin", trans.getVendorGstin());
			element.addProperty("supplierCode", trans.getSupplierCode());
			element.addProperty("vendorName", trans.getVendorName());
			element.addProperty("vendPrimEmailId", trans.getVendPrimEmailId());
			element.addProperty("vendorContactNumber",
					trans.getVendorContactNumber());
			element.addProperty("vendorEmailId1", trans.getVendorEmailId1());
			element.addProperty("vendorEmailId2", trans.getVendorEmailId2());
			element.addProperty("vendorEmailId3", trans.getVendorEmailId3());
			element.addProperty("vendorEmailId4", trans.getVendorEmailId4());
			element.addProperty("recipientEmailId1",
					trans.getRecipientEmailId1());
			element.addProperty("recipientEmailId2",
					trans.getRecipientEmailId2());
			element.addProperty("recipientEmailId3",
					trans.getRecipientEmailId3());
			element.addProperty("recipientEmailId4",
					trans.getRecipientEmailId4());
			element.addProperty("recipientEmailId5",
					trans.getRecipientEmailId5());
			element.addProperty("vendorType", trans.getVendorType());
			element.addProperty("hsn", trans.getHsn());
			element.addProperty("vendorRiskCategory",
					trans.getVendorRiskCategory());
			element.addProperty("vendorPaymentTerms",
					trans.getVendorPaymentTerms());
			element.addProperty("vendorRemarks", trans.getVendorRemarks());
			element.addProperty("approvalStatus", trans.getApprovalStatus());
			element.addProperty("excludeVendorRemarks",
					trans.getExcludeVendorRemarks());
			element.addProperty("isVendorCom", trans.getIsVendorCom());
			element.addProperty("isExcludeVendor", trans.getIsExcludeVendor());
			element.addProperty("isNonComplaintCom",
					trans.getIsNonComplaintCom());
			element.addProperty("isCreditEligibility",
					trans.getIsCreditEligibility());
			element.addProperty("isDelete", trans.getIsDelete());

			jsonArray.add(element);
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("req", jsonArray);
		String jsonString = gson.toJson(jsonObject);
		req.setJsonString(jsonString);
		return req;
	}
	
	public static ITCReversal180DayApiReqDocument getITCReversal180DayApiReq(
			ITCReversal180DayApiSoapHdrRq document) {

		ITCReversal180DayApiReqDocument req = new ITCReversal180DayApiReqDocument();
		req.setIdTokens(document.getIdToken());
		req.setPayloadId(document.getPayloadId());
		req.setCompanyCode(document.getCompanyCode());
		req.setSourceId(document.getSourceId());
		req.setSourceId(document.getDocCount());
		req.setCheckSum("NOCHECKSUM");
		req.setPushType("2");

		JsonArray jsonArray = new JsonArray();
		Gson gson = new Gson();
		for (ITCReversal180DayApiTransRq trans : document.getLineItems()) {
			JsonObject element = new JsonObject();
			
			element.addProperty("actionType", nullCheck(trans.getActionType()));
			element.addProperty("customerGSTIN", nullCheck(trans.getCustomerGSTIN()));
			element.addProperty("supplierGSTIN", nullCheck(trans.getSupplierGSTIN()));
			element.addProperty("supplierName", nullCheck(trans.getSupplierCode()));
			element.addProperty("supplierCode", nullCheck(trans.getSupplierCode()));
			element.addProperty("documentType", nullCheck(trans.getDocumentType()));
			element.addProperty("documentNumber",nullCheck(trans.getDocumentNumber()));
			
			element.addProperty("documentDate", nullCheck(trans.getDocumentDate()));
			element.addProperty("invoiceValue", nullCheck(trans.getInvoiceValue()));
			element.addProperty("fiscalYear", nullCheck(trans.getFiscalYear()));
			element.addProperty("statuDedApplicabl", nullCheck(trans.getStatutoryDeductionsApplicable()));
			element.addProperty("payReferenceNo",nullCheck(trans.getPaymentReferenceNumber()));
			element.addProperty("payReferenceDate",nullCheck(trans.getPaymentReferenceDate()));
			element.addProperty("paymentStatus",nullCheck(trans.getPaymentStatus()));
			element.addProperty("statuDedAmount",
					nullCheck(trans.getStatutoryDeductionAmount()));
			element.addProperty("anyOthDedAmount",
					nullCheck(trans.getAnyOtherDeductionAmount()));
			element.addProperty("remarksforDed", nullCheck(trans.getRemarksforDeductions()));
			element.addProperty("dueDateofPayment", nullCheck(trans.getDueDateofPayment()));
			element.addProperty("payDescription",
					nullCheck(trans.getPaymentDescription()));
			element.addProperty("paidAmttoSupplier",
					nullCheck(trans.getPaidAmounttoSupplier()));
			element.addProperty("currencyCode", nullCheck(trans.getCurrencyCode()));
			element.addProperty("exchangeRate", nullCheck(trans.getExchangeRate()));
			element.addProperty("unpaidAmttoSuplr",
					nullCheck(trans.getUnpaidAmounttoSupplier()));
			element.addProperty("postingDate", nullCheck(trans.getPostingDate()));
			element.addProperty("plantCode", nullCheck(trans.getPlantCode()));
			element.addProperty("profitCentre",
					nullCheck(trans.getProfitCentre()));
			element.addProperty("division",
					nullCheck(trans.getDivision()));
			element.addProperty("userDefinField1", nullCheck(trans.getUserDefinedField1()));
			element.addProperty("userDefinField2", nullCheck(trans.getUserDefinedField2()));
			element.addProperty("userDefinField3", nullCheck(trans.getUserDefinedField3()));
            
			jsonArray.add(element);
			
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("req", jsonArray);
		String jsonString = gson.toJson(jsonObject);
		req.setJsonString(jsonString);
		return req;
	}
	
	private static String nullCheck(String str){
		if(str != null){
			return str.trim();
		}
		return str;
		
	}
	
	public static GstinValidatorApiReqDocument getGstinValidatorApiReq(
			GstinValidatorPayloadHdrDataSoapDto document) {

		GstinValidatorApiReqDocument req = new GstinValidatorApiReqDocument();
		req.setIdTokens(document.getIdToken());
		req.setPayloadId(document.getPayloadId());
		req.setCompanyCode(document.getCompanyCode());
		req.setCheckSum("NOCHECKSUM");
		req.setPushType("2");
 
		JsonArray jsonArray = new JsonArray();
		Gson gson = new Gson();
		for (GstinValidatorPayloadLineDataSoapDto trans : document.getLineItems()) {
			JsonObject element = new JsonObject();
			element.addProperty("customerCode", trans.getCustomerCode());
			element.addProperty("gstin", trans.getGstin());
			jsonArray.add(element);
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("gstinValidation", jsonArray);
		JsonObject request = new JsonObject();
		request.add("req", jsonObject);
		String jsonString = gson.toJson(request);
		req.setJsonString(jsonString);
		return req;
	}
	
	
}
