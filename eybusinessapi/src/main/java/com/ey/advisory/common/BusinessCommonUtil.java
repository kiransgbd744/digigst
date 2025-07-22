package com.ey.advisory.common;

import java.io.Reader;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.AdditionalDocDetails;
import com.ey.advisory.app.data.business.dto.AttributeDetails;
import com.ey.advisory.app.data.business.dto.ContractDetails;
import com.ey.advisory.app.data.business.dto.OtrdTransDocSoapHdrRq;
import com.ey.advisory.app.data.business.dto.OtrdTrnsDocLnItemsReq;
import com.ey.advisory.app.data.business.dto.OutwardTransDocLineItem;
import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.app.data.business.dto.PreDocDetails;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.common.EyEInvCommonUtil;
import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnSAPResponseDto;
import com.ey.advisory.einv.dto.GenEWBByIrnDispERPReqDto;
import com.ey.advisory.einv.dto.GenEWBByIrnExpShpERPReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIRNSoapReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnERPReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateIrnSAPResponseDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.EwbSAPResponseDto;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BusinessCommonUtil {

	public static String apiType(String urlType) {

		String apiIdentifier = null;
		if (urlType.contains(BusinessCriticalConstants.GEN_EINV_API)) {
			apiIdentifier = APIIdentifiers.GENERATE_EINV;
		} else if (urlType.contains(BusinessCriticalConstants.CAN_EINV_API)) {
			apiIdentifier = APIIdentifiers.CANCEL_EINV;
		} else if (urlType
				.contains(BusinessCriticalConstants.GEN_EWB_BY_IRN_API)
				|| urlType.contains(
						BusinessCriticalConstants.GEN_EWB_BY_IRN_API_L)) {
			apiIdentifier = APIIdentifiers.GENERATE_EWBByIRN;
		} else if (urlType
				.contains(BusinessCriticalConstants.GET_EINV_DTLS_DOC)) {
			apiIdentifier = APIIdentifiers.GET_EINVBYDOCDETAILS;
		} else if (urlType.contains(BusinessCriticalConstants.SYNC_DETAILS)) {
			apiIdentifier = APIIdentifiers.GET_SYNCGSTINFROMCP;
		} else if (urlType.contains(BusinessCriticalConstants.GET_EWB_BY_IRN)) {
			apiIdentifier = APIIdentifiers.GET_EWBDETAILSBYIRN;
		} else if (urlType.contains(BusinessCriticalConstants.GET_EINV_DTLS)) {
			apiIdentifier = APIIdentifiers.GET_EINVBYIRN;
		} else if (urlType.contains(BusinessCriticalConstants.GEN_EWB_API)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.GENERATE_EWB;
		} else if (urlType.contains(BusinessCriticalConstants.CAN_EWB_API)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.CANCEL_EWB;
		} else if (urlType.contains(BusinessCriticalConstants.REJECT_EWAY)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.REJECT_EWB;
		} else if (urlType
				.contains(BusinessCriticalConstants.UPDATE_PARTB_EWAY)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.UPDATE_PARTB;
		} else if (urlType
				.contains(BusinessCriticalConstants.CONSOLIDATE_EWAY_BILL)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.GENERATE_CONSOLIDATED_EWB;
		} else if (urlType
				.contains(BusinessCriticalConstants.UPDATE_EWB_TRANS)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.UPDATE_TRANSPORTER;
		} else if (!urlType.contains(BusinessCriticalConstants.GET_EWB_CONSI)
				&& urlType.contains(BusinessCriticalConstants.GET_EWB)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.GET_EWB;
		} else if (urlType.contains(BusinessCriticalConstants.ADD_MULTI_VEH)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.MultiVehAdd;
		} else if (urlType
				.contains(BusinessCriticalConstants.CHANGE_MULTI_VEH)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.MultiVehUpdate;
		} else if (urlType.contains(BusinessCriticalConstants.INIT_MULTI_VEH)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.MultiVehMovement;
		} else if (urlType.contains(BusinessCriticalConstants.GET_EWB_CONSI)) {
			apiIdentifier = com.ey.advisory.ewb.app.api.APIIdentifiers.GET_EWB_BY_CONSIGNOR;
		}

		if (urlType.contains("v2")) {
			apiIdentifier = apiIdentifier + "-V2";
		} else if (urlType.contains("v3"))
			apiIdentifier = apiIdentifier + "-V3";

		return apiIdentifier;
	}

	public static String convertClobtoString(Clob data) {
		String w = null;
		try {
			Reader charStream = data.getCharacterStream();
			w = IOUtils.toString(charStream);
			charStream.close();
		} catch (Exception e) {
			String msg = "Exception occured while converting clob to String";
			LOGGER.error(msg, e);
		}
		return w;
	}

	public static EwbSAPResponseDto convertEWBNICResptoCloud(
			EwbResponseDto respdata, OutwardTransDocument hdr,
			JsonObject nicReqObj) {
		EwbSAPResponseDto cloudEwbRespDto = new EwbSAPResponseDto();
		try {
			cloudEwbRespDto.setEwayBillNo(respdata.getEwayBillNo());
			cloudEwbRespDto.setEwayBillDate(respdata.getEwayBillDate());
			cloudEwbRespDto.setValidUpto(respdata.getValidUpto());
			cloudEwbRespDto.setAlert(respdata.getAlert());
			cloudEwbRespDto.setTransportMode(
					EyEInvCommonUtil.getTransMode(hdr.getTransportMode()));
			cloudEwbRespDto.setTransporterID(hdr.getTransporterID());
			cloudEwbRespDto.setTransportDocNo(hdr.getTransportDocNo());
			cloudEwbRespDto.setTransportDocDate(hdr.getTransportDocDate());
			cloudEwbRespDto.setVehicleNo(hdr.getVehicleNo());
			cloudEwbRespDto.setVehicleType(hdr.getVehicleType());
			cloudEwbRespDto.setAspDistance(respdata.getNicDistance());
			cloudEwbRespDto.setFromPincode(Integer
					.valueOf(nicReqObj.get("fromPincode").getAsString()));
			cloudEwbRespDto
					.setFromPlace(nicReqObj.get("fromPlace").getAsString());
			cloudEwbRespDto
					.setFromState(nicReqObj.get("fromStateCode").getAsString());
		} catch (Exception e) {
			String msg = "Exception occured while converting ewbNIC to cloud";
			LOGGER.error(msg, e);
		}
		return cloudEwbRespDto;
	}

	public static EwbSAPResponseDto convertCANEWBNICResptoCloud(
			CancelEwbResponseDto respdata, JsonObject nicReqObj) {
		EwbSAPResponseDto cloudEwbRespDto = new EwbSAPResponseDto();
		try {
			cloudEwbRespDto.setEwayBillNo(respdata.getEwayBillNo());
			cloudEwbRespDto.setCancelDate(respdata.getCancelDate());
		} catch (Exception e) {
			String msg = "Exception occured while converting ewbNIC to cloud";
			LOGGER.error(msg, e);
		}
		return cloudEwbRespDto;
	}

	public static EwbSAPResponseDto convertGENEWBIRNResptoCloud(
			GenerateEWBByIrnResponseDto respdata, JsonObject nicReqObj) {
		EwbSAPResponseDto cloudEwbRespDto = new EwbSAPResponseDto();
		try {
			cloudEwbRespDto.setEwayBillNo(respdata.getEwbNo());
			cloudEwbRespDto.setEwayBillDate(respdata.getEwbDt());
			cloudEwbRespDto.setValidUpto(respdata.getEwbValidTill());
			cloudEwbRespDto
					.setNicDistance(Integer.valueOf(respdata.getNicDistance()));
		} catch (Exception e) {
			String msg = "Exception occured while converting ewbNIC to cloud";
			LOGGER.error(msg, e);
		}
		return cloudEwbRespDto;
	}
	
	public static GenerateIrnSAPResponseDto convertNICResptoCloud(
			GenerateIrnResponseDto data) {
		GenerateIrnSAPResponseDto cloudEinvRespDto = new GenerateIrnSAPResponseDto();
		try {
			cloudEinvRespDto.setAckNo(data.getAckNo());
			cloudEinvRespDto.setAckDt(data.getAckDt());
			// cloudEinvRespDto.setBuyerGstin(data.getBuyerGstin());
			// cloudEinvRespDto.setDocDate(data.getDocDate());
			// cloudEinvRespDto.setDocNum(data.getDocNum());
			// cloudEinvRespDto.setEwbNo(data.getEwbNo());
			// cloudEinvRespDto.setEwbNo(data.getEwbNo());
			// cloudEinvRespDto.setEwbDt(data.getEwbDt());
			cloudEinvRespDto.setEwbValidTill(data.getEwbValidTill());
			cloudEinvRespDto.setFormattedQrCode(data.getFormattedQrCode());
			cloudEinvRespDto.setIrn(data.getIrn());
			cloudEinvRespDto.setNicDistance(data.getNicDistance());
			cloudEinvRespDto.setQrData(data.getQrData());
			// cloudEinvRespDto.setSellerGstin(data.getSellerGstin());
			cloudEinvRespDto.setSignedInvoice(data.getSignedInvoice());
			cloudEinvRespDto.setSignedQRCode(data.getSignedQRCode());
			cloudEinvRespDto.setStatus(data.getStatus());

		} catch (Exception e) {
			String msg = "Exception occured while converting clob to String";
			LOGGER.error(msg, e);
		}
		return cloudEinvRespDto;
	}

	public static CancelIrnSAPResponseDto convertCanNICResptoCloud(
			CancelIrnERPResponseDto data, JsonObject nicReqObj) {
		CancelIrnSAPResponseDto cancelSAPRespDto = new CancelIrnSAPResponseDto();
		try {
			cancelSAPRespDto
					.setCancelReason(nicReqObj.get("canReason").getAsString());
			cancelSAPRespDto.setCancelRemarks(
					nicReqObj.get("canRemarks").getAsString());
			if (data.getCancelDate() != null) {
				cancelSAPRespDto.setCancelDate(data.getCancelDate());
			}
			cancelSAPRespDto.setIrn(data.getIrn());
		} catch (Exception e) {
			String msg = "Exception occured while Cancel Resp converting clob to String";
			LOGGER.error(msg, e);
		}
		return cancelSAPRespDto;
	}

	public static OutwardTransDocument convertERPSoapReqToOutwardTransDoc(
			OtrdTransDocSoapHdrRq req) {
		OutwardTransDocument doc = new OutwardTransDocument();
		List<OtrdTrnsDocLnItemsReq> lineItems = req.getLineItems();
		List<OutwardTransDocLineItem> reqLineItems = new ArrayList<>();
		doc.setUserDefinedField28(req.getUserDefinedField28());
		doc.setUserDefinedField29(req.getUserDefinedField29());
		doc.setAcceptanceId(req.getAcceptanceId());
		doc.setAccountDetail(req.getAccountDetail());
		doc.setAccountingVoucherDate(req.getAccountingVoucherDate());
		doc.setAccountingVoucherNumber(req.getAccountingVoucherNumber());
		doc.setAckDate(req.getAckDate());
		doc.setAutoPopToRefundFlag(req.getAutoPopToRefundFlag());
		doc.setBeforeSavingOn(req.getBeforeSavingOn());
		doc.setBillToState(req.getBillToState());
		doc.setBranchOrIfscCode(req.getBranchOrIfscCode());
		doc.setCreatedBy(req.getCreatedBy());
		doc.setCreatedDate(req.getCreatedDate());
		doc.setCancellationReason(req.getCancellationReason());
		doc.setCancellationRemarks(req.getCancellationRemarks());
		doc.setCessAmountAdvalorem(req.getCessAmountAdvalorem());
		doc.setCessAmountSpecific(req.getCessAmountSpecific());
		doc.setCgstAmount(req.getCgstAmount());
		doc.setCgstin(req.getCgstin());
		doc.setCgstInMasterCust(req.isCgstInMasterCust());
		doc.setClaimRefundFlag(req.getClaimRefundFlag());
		doc.setCompanyCode(req.getCompanyCode());
		doc.setCountryCode(req.getCountryCode());
		doc.setCrDrPreGst(req.getCrDrPreGst());
		doc.setCreditDays(req.getCreditDays());
		doc.setCreditTransfer(req.getCreditTransfer());
		doc.setCustomerEmail(req.getCustomerEmail());
		doc.setCustomerPANOrAadhaar(req.getCustomerPANOrAadhaar());
		doc.setCustomerPhone(req.getCustomerPhone());
		doc.setCustomerPincode(req.getCustomerPincode());
		doc.setCustomerTan(req.getCustomerTan());
		doc.setCustomerTradeName(req.getCustomerTradeName());
		doc.setCustOrSuppAddress1(req.getCustOrSuppAddress1());
		doc.setCustOrSuppAddress2(req.getCustOrSuppAddress2());
		doc.setCustOrSuppAddress3(req.getCustOrSuppAddress3());
		doc.setCustOrSuppAddress4(req.getCustOrSuppAddress4());
		doc.setCustOrSuppCode(req.getCustOrSuppCode());
		doc.setCustOrSuppName(req.getCustOrSuppName());
		doc.setCustOrSuppType(req.getCustOrSuppType());
		doc.setDataOriginTypeCode(req.getDataOriginTypeCode());
		doc.setDeleted(req.isDeleted());
		doc.setDerivedCgstinPan(req.getDerivedCgstinPan());
		doc.setDerivedSgstinPan(req.getDerivedSgstinPan());
		doc.setDerivedSupplyType(req.getDerivedSupplyType());
		doc.setDerivedTaxperiod(req.getDerivedTaxperiod());
		doc.setDiffPercent(req.getDiffPercent());
		doc.setDirectDebit(req.getDirectDebit());
		doc.setDispatcherBuildingName(req.getDispatcherBuildingName());
		doc.setDispatcherBuildingNumber(req.getDispatcherBuildingNumber());
		doc.setDispatcherGstin(req.getDispatcherGstin());
		doc.setDispatcherLocation(req.getDispatcherLocation());
		doc.setDispatcherPincode(req.getDispatcherPincode());
		doc.setDispatcherStateCode(req.getDispatcherStateCode());
		doc.setDispatcherTradeName(req.getDispatcherTradeName());
		doc.setDistance(req.getDistance());
		doc.setDistributionChannel(req.getDistributionChannel());
		doc.setDivision(req.getDivision());
		doc.setDocAmount(req.getDocAmount());
		doc.setDocCategory(req.getDocCategory());
		doc.setDocDate(req.getDocDate());
		doc.setDocKey(req.getDocKey());
		doc.setDocNo(req.getDocNo());
		doc.setDocType(req.getDocType());
		doc.setEcomTransactionID(req.getEcomTransactionID());
		doc.setEgstin(req.getEgstin());
		doc.setEInvErrorCode(req.getEInvErrorCode());
		doc.setEInvErrorDesc(req.getEInvErrorDesc());
		doc.setEInvStatus(req.getEInvStatus());
		doc.setEntityId(req.getEntityId());
		doc.setErpBatchId(req.getErpBatchId());
		doc.setEWayBillDate(req.getEWayBillDate());
		doc.setEWayBillNo(req.getEWayBillNo());
		doc.setEwbStatus(req.getEwbStatus());
		doc.setExchangeRate(req.getExchangeRate());
		doc.setExtractedBatchId(req.getExtractedBatchId());
		doc.setExtractedDate(req.getExtractedDate());
		doc.setExtractedOn(req.getExtractedOn());
		doc.setFinYear(req.getFinYear());
		doc.setForeignCurrency(req.getForeignCurrency());
		doc.setFormReturnType(req.getFormReturnType());
		doc.setGlCodeAdvCess(req.getGlCodeAdvCess());
		doc.setGlCodeCgst(req.getGlCodeCgst());
		doc.setGlCodeIgst(req.getGlCodeIgst());
		doc.setGlCodeSgst(req.getGlCodeSgst());
		doc.setGlCodeSpCess(req.getGlCodeSpCess());
		doc.setGlCodeStateCess(req.getGlCodeStateCess());
		doc.setGlPostingDate(req.getGlPostingDate());
		doc.setGlStateCessSpecific(req.getGlStateCessSpecific());
		doc.setGroupId(req.getGroupId());
		doc.setGstnBatchId(req.getGstnBatchId());
		doc.setGstnBifurcation(req.getGstnBifurcation());
		doc.setGstnBifurcationNew(req.getGstnBifurcationNew());
		doc.setGstnError(req.isGstnError());
		doc.setGstrReturnType(req.getGstrReturnType());
		doc.setHciReceivedOn(req.getHciReceivedOn());
		doc.setId(req.getId());
		doc.setIdToken(req.getIdToken());
		doc.setIgstAmount(req.getIgstAmount());
		doc.setInitiatedOn(req.getInitiatedOn());
		doc.setInvoiceAssessableAmount(req.getInvoiceAssessableAmount());
		doc.setInvoiceCessAdvaloremAmount(req.getInvoiceCessAdvaloremAmount());
		doc.setInvoiceCessSpecificAmount(req.getInvoiceCessSpecificAmount());
		doc.setInvoiceCgstAmount(req.getInvoiceCgstAmount());
		doc.setInvoiceIgstAmount(req.getInvoiceIgstAmount());
		doc.setInvoiceOtherCharges(req.getInvoiceOtherCharges());
		doc.setInvoicePeriodEndDate(req.getInvoicePeriodEndDate());
		doc.setInvoicePeriodStartDate(req.getInvoicePeriodStartDate());
		doc.setInvoiceRemarks(req.getInvoiceRemarks());
		doc.setInvoiceSgstAmount(req.getInvoiceSgstAmount());
		doc.setInvoiceStateCessAmount(req.getInvoiceStateCessAmount());
		doc.setInvoiceValueFc(req.getInvoiceValueFc());
		doc.setInvStateCessSpecificAmt(req.getInvStateCessSpecificAmt());
		doc.setIrn(req.getIrn());
		doc.setIrnDate(req.getIrnDate());
		doc.setCgstInMasterCust(req.isCgstInMasterCust());
		doc.setCompliance(req.isCompliance());
		doc.setEinvoice(req.isEinvoice());
		doc.setError(req.isError());
		doc.setEwb(req.isEwb());
		doc.setInfo(req.isInfo());
		doc.setProcessed(req.isProcessed());
		doc.setLineItems(reqLineItems);
		doc.setLocation(req.getLocation());
		doc.setMemoValCess(req.getMemoValCess());
		doc.setMemoValCgst(req.getMemoValCgst());
		doc.setMemoValIgst(req.getMemoValIgst());
		doc.setMemoValSgst(req.getMemoValSgst());
		doc.setModeOfPayment(req.getModeOfPayment());
		doc.setModifiedBy(req.getModifiedBy());
		doc.setOrigCgstin(req.getOrigCgstin());
		doc.setOrigDocType(req.getOrigDocType());
		doc.setOriginalInvoiceDate(req.getOriginalInvoiceDate());
		doc.setOriginalInvoiceNumber(req.getOriginalInvoiceNumber());
		doc.setOtherSupplyTypeDescription(req.getOtherSupplyTypeDescription());
		doc.setOtherValues(req.getOtherValues());
		doc.setPayeeName(req.getPayeeName());
		doc.setPaymentDueDate(req.getPaymentDueDate());
		doc.setPaymentInstruction(req.getPaymentInstruction());
		doc.setPaymentTerms(req.getPaymentTerms());
		doc.setPlantCode(req.getPlantCode());
		doc.setPortCode(req.getPortCode());
		doc.setPos(req.getPos());

		doc.setProfitCentre(req.getProfitCentre());
		doc.setProfitCentre2(req.getProfitCentre2());
		doc.setReceivedDate(req.getReceivedDate());
		doc.setReqReceivedOn(req.getReqReceivedOn());
		doc.setRet1CgstImpact(req.getRet1CgstImpact());
		doc.setRet1IgstImpact(req.getRet1IgstImpact());
		doc.setRet1SgstImpact(req.getRet1SgstImpact());
		doc.setReturnType(req.getReturnType());
		doc.setReverseCharge(req.getReverseCharge());
		doc.setRoundOff(req.getRoundOff());
		doc.setSalesOrderNumber(req.getSalesOrderNumber());
		doc.setSalesOrgnization(req.getSalesOrgnization());
		doc.setSaved(req.isSaved());
		doc.setSavedToGSTNDate(req.getSavedToGSTNDate());
		doc.setSection7OfIgstFlag(req.getSection7OfIgstFlag());
		doc.setSent(req.isSent());
		doc.setSentToGSTNDate(req.getSentToGSTNDate());
		doc.setSgstAmount(req.getSgstAmount());
		doc.setSgstin(req.getSgstin());
		doc.setShippingBillDate(req.getShippingBillDate());
		doc.setShippingBillNo(req.getShippingBillNo());
		doc.setShipToBuildingName(req.getShipToBuildingName());
		doc.setShipToBuildingNumber(req.getShipToBuildingNumber());
		doc.setShipToGstin(req.getShipToGstin());
		doc.setShipToLegalName(req.getShipToLegalName());
		doc.setShipToLocation(req.getShipToLocation());
		doc.setShipToPincode(req.getShipToPincode());
		doc.setShipToState(req.getShipToState());
		doc.setShipToTradeName(req.getShipToTradeName());
		doc.setSourceFileName(req.getSourceFileName());
		doc.setSourceIdentifier(req.getSourceIdentifier());
		doc.setStateApplyingCess(req.getStateApplyingCess());
		doc.setStateCessAmount(req.getStateCessAmount());
		doc.setStatus(req.getStatus());
		doc.setSubDivision(req.getSubDivision());
		doc.setSubmitted(req.isSubmitted());
		doc.setSubSupplyType(req.getSubSupplyType());
		doc.setSupplierBuildingName(req.getSupplierBuildingName());
		doc.setSupplierBuildingNumber(req.getSupplierBuildingNumber());
		doc.setSupplierEmail(req.getSupplierEmail());
		doc.setSupplierLegalName(req.getSupplierLegalName());
		doc.setSupplierLocation(req.getSupplierLocation());
		doc.setSupplierPhone(req.getSupplierPhone());
		doc.setSupplierPincode(req.getSupplierPincode());
		doc.setSupplierStateCode(req.getSupplierStateCode());
		doc.setSupplierTradeName(req.getSupplierTradeName());
		doc.setSupplyType(req.getSupplyType());
		doc.setTableType(req.getTableType());
		doc.setTableTypeNew(req.getTableTypeNew());
		doc.setTaxableValue(req.getTaxableValue());
		doc.setTaxPayable(req.getTaxPayable());
		doc.setTaxperiod(req.getTaxperiod());
		doc.setTaxScheme(req.getTaxScheme());
		doc.setTcsFlag(req.getTcsFlag());
		doc.setTcsFlagIncomeTax(req.getTcsFlagIncomeTax());
		doc.setTdsFlag(req.getTdsFlag());
		doc.setTotalInvoiceValueInWords(req.getTotalInvoiceValueInWords());
		doc.setTransactionType(req.getTransactionType());
		doc.setTransportDocDate(req.getTransportDocDate());
		doc.setTransportDocNo(req.getTransportDocNo());
		doc.setTransporterID(req.getTransporterID());
		doc.setTransporterName(req.getTransporterName());
		doc.setTransportMode(req.getTransportMode());
		doc.setUpdatedDate(req.getUpdatedDate());
		doc.setUserAccess1(req.getUserAccess1());
		doc.setUserAccess2(req.getUserAccess2());
		doc.setUserAccess3(req.getUserAccess3());
		doc.setUserAccess4(req.getUserAccess4());
		doc.setUserAccess5(req.getUserAccess5());
		doc.setUserAccess6(req.getUserAccess6());
		doc.setUserdefinedfield1(req.getUserdefinedfield1());
		doc.setUserdefinedfield2(req.getUserdefinedfield2());
		doc.setUserdefinedfield3(req.getUserdefinedfield3());
		doc.setUserDefinedField4(req.getUserDefinedField4());
		doc.setUserId(req.getUserId());
		doc.setVehicleNo(req.getVehicleNo());
		doc.setVehicleType(req.getVehicleType());

		List<PreDocDetails> preDocLists = req.getPreDocDtls();

		if (preDocLists != null && !preDocLists.isEmpty()) {
			doc.addPreDocDtls(preDocLists);
		}
		List<ContractDetails> contractLists = req.getContrDtls();

		if (contractLists != null && !contractLists.isEmpty()) {
			doc.addCntrDtls(contractLists);
		}
		List<AdditionalDocDetails> addDtlsList = req.getAddlDocDtls();

		if (addDtlsList != null && !addDtlsList.isEmpty()) {
			doc.addAddlDocDtls(addDtlsList);
		}

		for (OtrdTrnsDocLnItemsReq obj : lineItems) {
			OutwardTransDocLineItem item = new OutwardTransDocLineItem();
			List<AttributeDetails> addAttDtls = obj.getAttributeDtls();
			if (addAttDtls != null && !addAttDtls.isEmpty()) {
				item.addAttributeDtls(addAttDtls);
			}
			item.setAccountingVoucherDate(obj.getAccountingVoucherDate());
			item.setAccountingVoucherNumber(obj.getAccountingVoucherNumber());
			item.setAdjustedCessAmtAdvalorem(obj.getAdjustedCessAmtAdvalorem());
			item.setAdjustedCessAmtSpecific(obj.getAdjustedStateCessAmt());
			item.setAdjustedCgstAmt(obj.getAdjustedCgstAmt());
			item.setAdjustedIgstAmt(obj.getAdjustedIgstAmt());
			item.setAdjustedSgstAmt(obj.getAdjustedSgstAmt());
			item.setAdjustedStateCessAmt(obj.getAdjustedStateCessAmt());
			item.setAdjustedTaxableValue(obj.getAdjustedTaxableValue());
			item.setAdjustmentRefDate(obj.getAdjustmentRefDate());
			item.setAdjustmentRefNo(obj.getAdjustmentRefNo());
			item.setAutoPopToRefundFlag(obj.getAutoPopToRefundFlag());
			item.setBalanceAmount(obj.getBalanceAmount());
			item.setBarcode(obj.getBarcode());
			item.setBatchExpiryDate(obj.getBatchExpiryDate());
			item.setBatchNameOrNumber(obj.getBatchNameOrNumber());
			item.setBillToState(obj.getBillToState());
			item.setCessAmountAdvalorem(obj.getCessAmountAdvalorem());
			item.setCessAmountSpecific(obj.getCessAmountSpecific());
			item.setCessRateAdvalorem(obj.getCessRateAdvalorem());
			item.setCessRateSpecific(obj.getCessRateSpecific());
			item.setCgstAmount(obj.getCgstAmount());
			item.setCgstin(obj.getCgstin());
			item.setCgstRate(obj.getCgstRate());
			item.setClaimRefundFlag(obj.getClaimRefundFlag());
			item.setCrDrPreGst(obj.getCrDrPreGst());
			item.setCrDrReason(obj.getCrDrReason());
			item.setCreatedBy(obj.getCreatedBy());
			item.setCreatedDate(obj.getCreatedDate());
			item.setCustOrSuppAddress1(obj.getCustOrSuppAddress1());
			item.setCustOrSuppAddress2(obj.getCustOrSuppAddress2());
			item.setCustOrSuppAddress3(obj.getCustOrSuppAddress3());
			item.setCustOrSuppAddress4(obj.getCustOrSuppAddress4());
			item.setCustOrSuppCode(obj.getCustOrSuppCode());
			item.setCustOrSuppName(obj.getCustOrSuppName());
			item.setCustOrSuppType(obj.getCustOrSuppType());
			item.setDerivedTaxperiod(obj.getDerivedTaxperiod());
			item.setDiffPercent(obj.getDiffPercent());
			item.setDistributionChannel(obj.getDistributionChannel());
			item.setDivision(obj.getDivision());
			item.setDocDate(obj.getDocDate());
			item.setDocReferenceNumber(obj.getDocReferenceNumber());
			item.setEgstin(obj.getEgstin());
			item.setEWayBillDate(obj.getEWayBillDate());
			item.setEWayBillNo(obj.getEWayBillNo());
			item.setExportDuty(obj.getExportDuty());
			item.setFob(obj.getFob());
			item.setFreeQuantity(obj.getFreeQuantity());
			item.setGlCodeAdvCess(obj.getGlCodeAdvCess());
			item.setGlCodeCgst(obj.getGlCodeCgst());
			item.setGlCodeIgst(obj.getGlCodeIgst());
			item.setGlCodeSgst(obj.getGlCodeSgst());
			item.setGlCodeSpCess(obj.getGlCodeSpCess());
			item.setGlCodeStateCess(obj.getGlCodeStateCess());
			item.setGlCodeTaxableValue(obj.getGlCodeTaxableValue());
			item.setHsnSac(obj.getHsnSac());
			item.setId(obj.getId());
			item.setIgstAmount(obj.getIgstAmount());
			item.setIgstRate(obj.getIgstRate());
			item.setInvoiceAssessableAmount(obj.getInvoiceAssessableAmount());
			item.setInvoiceCessAdvaloremAmount(
					obj.getInvoiceCessAdvaloremAmount());
			item.setInvoiceCessSpecificAmount(
					obj.getInvoiceCessSpecificAmount());
			item.setInvoiceCgstAmount(obj.getInvoiceCgstAmount());
			item.setInvoiceIgstAmount(obj.getInvoiceIgstAmount());
			item.setInvoiceOtherCharges(obj.getInvoiceOtherCharges());
			item.setInvoiceSgstAmount(obj.getInvoiceSgstAmount());
			item.setInvoiceStateCessAmount(obj.getInvoiceStateCessAmount());
			item.setInvStateCessSpecificAmt(obj.getInvStateCessSpecificAmt());
			item.setIsService(obj.getIsService());
			item.setItcFlag(obj.getItcFlag());
			item.setItemAmount(obj.getItemAmount());
			item.setItemCategory(obj.getItemCategory());
			item.setItemCode(obj.getItemCode());
			item.setItemDescription(obj.getItemDescription());
			item.setItemDiscount(obj.getItemDiscount());
			item.setLineItemAmt(obj.getLineItemAmt());
			item.setLineNo(obj.getLineNo());
			item.setLocation(obj.getLocation());
			item.setMemoValCess(obj.getMemoValCess());
			item.setMemoValCgst(obj.getMemoValCgst());
			item.setMemoValIgst(obj.getMemoValIgst());
			item.setMemoValSgst(obj.getMemoValSgst());
			item.setModifiedBy(obj.getModifiedBy());
			item.setModifiedDate(obj.getModifiedDate());
			item.setOrderLineReference(obj.getOrderLineReference());
			item.setOrigCgstin(obj.getOrigCgstin());
			item.setOrigDocDate(obj.getOrigDocDate());
			item.setOrigDocNo(obj.getOrigDocNo());
			item.setOrigDocType(obj.getOrigDocType());
			item.setOriginalInvoiceDate(obj.getOriginalInvoiceDate());
			item.setOriginalInvoiceNumber(obj.getOriginalInvoiceNumber());
			item.setOriginCountry(obj.getOriginCountry());
			item.setOtherValues(obj.getOtherValues());
			item.setPaidAmount(obj.getPaidAmount());
			item.setPlantCode(obj.getPlantCode());
			item.setPortCode(obj.getPortCode());
			item.setPos(obj.getPos());
			item.setPreTaxAmount(obj.getPreTaxAmount());
			item.setProductName(obj.getProductName());
			item.setProfitCentre(obj.getProfitCentre());
			item.setProfitCentre2(obj.getProfitCentre2());
			item.setQty(obj.getQty());
			item.setRet1CgstImpact(obj.getRet1CgstImpact());
			item.setRet1IgstImpact(obj.getRet1IgstImpact());
			item.setRet1SgstImpact(obj.getRet1SgstImpact());
			item.setReverseCharge(obj.getReverseCharge());
			item.setSalesOrgnization(obj.getSalesOrgnization());
			item.setSection7OfIgstFlag(obj.getSection7OfIgstFlag());
			item.setSerialNumberII(obj.getSerialNumberII());
			item.setSgstAmount(obj.getSgstAmount());
			item.setSgstRate(obj.getSgstRate());
			item.setShippingBillDate(obj.getShippingBillDate());
			item.setShippingBillNo(obj.getShippingBillNo());
			item.setShipToState(obj.getShipToState());
			item.setSourceFileName(obj.getSourceFileName());
			item.setStateApplyingCess(obj.getStateApplyingCess());
			item.setStateCessAmount(obj.getStateCessAmount());
			item.setStateCessRate(obj.getStateCessRate());
			item.setStateCessSpecificAmt(obj.getStateCessSpecificAmt());
			item.setStateCessSpecificRate(obj.getStateCessSpecificRate());
			item.setSubDivision(obj.getSubDivision());
			item.setSupplyType(obj.getSupplyType());
			item.setTaxableValue(obj.getTaxableValue());
			item.setTaxPayable(obj.getTaxPayable());
			item.setTaxperiod(obj.getTaxperiod());
			item.setTaxRate(obj.getTaxRate());
			item.setTcsAmount(obj.getTcsAmount());
			item.setTcsAmountIncomeTax(obj.getTcsAmountIncomeTax());
			item.setTcsCgstAmount(obj.getTcsCgstAmount());
			item.setTcsFlag(obj.getTcsFlag());
			item.setTcsRateIncomeTax(obj.getTcsRateIncomeTax());
			item.setTcsSgstAmount(obj.getTcsSgstAmount());
			item.setTdsCgstAmount(obj.getTdsCgstAmount());
			item.setTdsFlag(obj.getTdsFlag());
			item.setTdsIgstAmount(obj.getTdsIgstAmount());
			item.setTdsSgstAmount(obj.getTdsSgstAmount());
			item.setTotalAmt(obj.getTotalAmt());
			item.setTotalItemAmount(obj.getTotalItemAmount());
			item.setUnitPrice(obj.getUnitPrice());
			item.setUom(obj.getUom());
			item.setUserdefinedfield1(obj.getUserdefinedfield1());
			item.setUserdefinedfield2(obj.getUserdefinedfield2());
			item.setUserdefinedfield3(obj.getUserdefinedfield3());
			item.setUserDefinedField4(obj.getUserDefinedField4());
			item.setUserDefinedField5(obj.getUserDefinedField5());
			item.setUserDefinedField6(obj.getUserDefinedField6());
			item.setUserDefinedField7(obj.getUserDefinedField7());
			item.setUserDefinedField8(obj.getUserDefinedField8());
			item.setUserDefinedField9(obj.getUserDefinedField9());
			item.setUserDefinedField10(obj.getUserDefinedField10());
			item.setUserDefinedField11(obj.getUserDefinedField11());
			item.setUserDefinedField12(obj.getUserDefinedField12());
			item.setUserDefinedField13(obj.getUserDefinedField13());
			item.setUserDefinedField14(obj.getUserDefinedField14());
			item.setUserDefinedField15(obj.getUserDefinedField15());
			item.setUserDefinedField16(obj.getUserDefinedField16());
			item.setUserDefinedField17(obj.getUserDefinedField17());
			item.setUserDefinedField18(obj.getUserDefinedField18());
			item.setUserDefinedField19(obj.getUserDefinedField19());
			item.setUserDefinedField20(obj.getUserDefinedField20());
			item.setUserDefinedField21(obj.getUserDefinedField21());
			item.setUserDefinedField22(obj.getUserDefinedField22());
			item.setUserDefinedField23(obj.getUserDefinedField23());
			item.setUserDefinedField24(obj.getUserDefinedField24());
			item.setUserDefinedField25(obj.getUserDefinedField25());
			item.setUserDefinedField26(obj.getUserDefinedField26());
			item.setUserDefinedField27(obj.getUserDefinedField27());
			item.setUserDefinedField30(obj.getUserDefinedField30());
			item.setUserAccess1(obj.getUserAccess1());
			item.setUserAccess2(obj.getUserAccess2());
			item.setUserAccess3(obj.getUserAccess3());
			item.setUserAccess4(obj.getUserAccess4());
			item.setUserAccess5(obj.getUserAccess5());
			item.setUserAccess6(obj.getUserAccess6());
			item.setWarrantyDate(obj.getWarrantyDate());

			reqLineItems.add(item);
		}
		return doc;
	}

	public static OutwardTransDocument convertToOutwardTransDocWithNested(
			OtrdTransDocSoapHdrRq req) {
		OutwardTransDocument doc = new OutwardTransDocument();
		List<OtrdTrnsDocLnItemsReq> lineItems = req.getLineItems();
		List<OutwardTransDocLineItem> reqLineItems = new ArrayList<>();
		doc.setUserDefinedField28(req.getUserDefinedField28());
		doc.setUserDefinedField29(req.getUserDefinedField29());
		doc.setAcceptanceId(req.getAcceptanceId());
		doc.setAccountDetail(req.getAccountDetail());
		doc.setAccountingVoucherDate(req.getAccountingVoucherDate());
		doc.setAccountingVoucherNumber(req.getAccountingVoucherNumber());
		doc.setAckDate(req.getAckDate());
		doc.setAutoPopToRefundFlag(req.getAutoPopToRefundFlag());
		doc.setBeforeSavingOn(req.getBeforeSavingOn());
		doc.setBillToState(req.getBillToState());
		doc.setBranchOrIfscCode(req.getBranchOrIfscCode());
		doc.setCreatedBy(req.getCreatedBy());
		doc.setCreatedDate(req.getCreatedDate());
		doc.setCancellationReason(req.getCancellationReason());
		doc.setCancellationRemarks(req.getCancellationRemarks());
		doc.setCessAmountAdvalorem(req.getCessAmountAdvalorem());
		doc.setCessAmountSpecific(req.getCessAmountSpecific());
		doc.setCgstAmount(req.getCgstAmount());
		doc.setCgstin(req.getCgstin());
		doc.setCgstInMasterCust(req.isCgstInMasterCust());
		doc.setClaimRefundFlag(req.getClaimRefundFlag());
		doc.setCompanyCode(req.getCompanyCode());
		doc.setCountryCode(req.getCountryCode());
		doc.setCrDrPreGst(req.getCrDrPreGst());
		doc.setCreditDays(req.getCreditDays());
		doc.setCreditTransfer(req.getCreditTransfer());
		doc.setCustomerEmail(req.getCustomerEmail());
		doc.setCustomerPANOrAadhaar(req.getCustomerPANOrAadhaar());
		doc.setCustomerPhone(req.getCustomerPhone());
		doc.setCustomerPincode(req.getCustomerPincode());
		doc.setCustomerTan(req.getCustomerTan());
		doc.setCustomerTradeName(req.getCustomerTradeName());
		doc.setCustOrSuppAddress1(req.getCustOrSuppAddress1());
		doc.setCustOrSuppAddress2(req.getCustOrSuppAddress2());
		doc.setCustOrSuppAddress3(req.getCustOrSuppAddress3());
		doc.setCustOrSuppAddress4(req.getCustOrSuppAddress4());
		doc.setCustOrSuppCode(req.getCustOrSuppCode());
		doc.setCustOrSuppName(req.getCustOrSuppName());
		doc.setCustOrSuppType(req.getCustOrSuppType());
		doc.setDataOriginTypeCode(req.getDataOriginTypeCode());
		doc.setDeleted(req.isDeleted());
		doc.setDerivedCgstinPan(req.getDerivedCgstinPan());
		doc.setDerivedSgstinPan(req.getDerivedSgstinPan());
		doc.setDerivedSupplyType(req.getDerivedSupplyType());
		doc.setDerivedTaxperiod(req.getDerivedTaxperiod());
		doc.setDiffPercent(req.getDiffPercent());
		doc.setDirectDebit(req.getDirectDebit());
		doc.setDispatcherBuildingName(req.getDispatcherBuildingName());
		doc.setDispatcherBuildingNumber(req.getDispatcherBuildingNumber());
		doc.setDispatcherGstin(req.getDispatcherGstin());
		doc.setDispatcherLocation(req.getDispatcherLocation());
		doc.setDispatcherPincode(req.getDispatcherPincode());
		doc.setDispatcherStateCode(req.getDispatcherStateCode());
		doc.setDispatcherTradeName(req.getDispatcherTradeName());
		doc.setDistance(req.getDistance());
		doc.setDistributionChannel(req.getDistributionChannel());
		doc.setDivision(req.getDivision());
		doc.setDocAmount(req.getDocAmount());
		doc.setDocCategory(req.getDocCategory());
		doc.setDocDate(req.getDocDate());
		doc.setDocKey(req.getDocKey());
		doc.setDocNo(req.getDocNo());
		doc.setDocType(req.getDocType());
		doc.setEcomTransactionID(req.getEcomTransactionID());
		doc.setEgstin(req.getEgstin());
		doc.setEInvErrorCode(req.getEInvErrorCode());
		doc.setEInvErrorDesc(req.getEInvErrorDesc());
		doc.setEInvStatus(req.getEInvStatus());
		doc.setEntityId(req.getEntityId());
		doc.setErpBatchId(req.getErpBatchId());
		doc.setEWayBillDate(req.getEWayBillDate());
		doc.setEWayBillNo(req.getEWayBillNo());
		doc.setEwbStatus(req.getEwbStatus());
		doc.setExchangeRate(req.getExchangeRate());
		doc.setExtractedBatchId(req.getExtractedBatchId());
		doc.setExtractedDate(req.getExtractedDate());
		doc.setExtractedOn(req.getExtractedOn());
		doc.setFinYear(req.getFinYear());
		doc.setForeignCurrency(req.getForeignCurrency());
		doc.setFormReturnType(req.getFormReturnType());
		doc.setGlCodeAdvCess(req.getGlCodeAdvCess());
		doc.setGlCodeCgst(req.getGlCodeCgst());
		doc.setGlCodeIgst(req.getGlCodeIgst());
		doc.setGlCodeSgst(req.getGlCodeSgst());
		doc.setGlCodeSpCess(req.getGlCodeSpCess());
		doc.setGlCodeStateCess(req.getGlCodeStateCess());
		doc.setGlPostingDate(req.getGlPostingDate());
		doc.setGlStateCessSpecific(req.getGlStateCessSpecific());
		doc.setGroupId(req.getGroupId());
		doc.setGstnBatchId(req.getGstnBatchId());
		doc.setGstnBifurcation(req.getGstnBifurcation());
		doc.setGstnBifurcationNew(req.getGstnBifurcationNew());
		doc.setGstnError(req.isGstnError());
		doc.setGstrReturnType(req.getGstrReturnType());
		doc.setHciReceivedOn(req.getHciReceivedOn());
		doc.setId(req.getId());
		doc.setIdToken(req.getIdToken());
		doc.setIgstAmount(req.getIgstAmount());
		doc.setInitiatedOn(req.getInitiatedOn());
		doc.setInvoiceAssessableAmount(req.getInvoiceAssessableAmount());
		doc.setInvoiceCessAdvaloremAmount(req.getInvoiceCessAdvaloremAmount());
		doc.setInvoiceCessSpecificAmount(req.getInvoiceCessSpecificAmount());
		doc.setInvoiceCgstAmount(req.getInvoiceCgstAmount());
		doc.setInvoiceIgstAmount(req.getInvoiceIgstAmount());
		doc.setInvoiceOtherCharges(req.getInvoiceOtherCharges());
		doc.setInvoicePeriodEndDate(req.getInvoicePeriodEndDate());
		doc.setInvoicePeriodStartDate(req.getInvoicePeriodStartDate());
		doc.setInvoiceRemarks(req.getInvoiceRemarks());
		doc.setInvoiceSgstAmount(req.getInvoiceSgstAmount());
		doc.setInvoiceStateCessAmount(req.getInvoiceStateCessAmount());
		doc.setInvoiceValueFc(req.getInvoiceValueFc());
		doc.setInvStateCessSpecificAmt(req.getInvStateCessSpecificAmt());
		doc.setIrn(req.getIrn());
		doc.setIrnDate(req.getIrnDate());
		doc.setCgstInMasterCust(req.isCgstInMasterCust());
		doc.setCompliance(req.isCompliance());
		doc.setEinvoice(req.isEinvoice());
		doc.setError(req.isError());
		doc.setEwb(req.isEwb());
		doc.setInfo(req.isInfo());
		doc.setProcessed(req.isProcessed());
		doc.setLocation(req.getLocation());
		doc.setMemoValCess(req.getMemoValCess());
		doc.setMemoValCgst(req.getMemoValCgst());
		doc.setMemoValIgst(req.getMemoValIgst());
		doc.setMemoValSgst(req.getMemoValSgst());
		doc.setModeOfPayment(req.getModeOfPayment());
		doc.setModifiedBy(req.getModifiedBy());
		doc.setOrigCgstin(req.getOrigCgstin());
		doc.setOrigDocType(req.getOrigDocType());
		doc.setOriginalInvoiceDate(req.getOriginalInvoiceDate());
		doc.setOriginalInvoiceNumber(req.getOriginalInvoiceNumber());
		doc.setOtherSupplyTypeDescription(req.getOtherSupplyTypeDescription());
		doc.setOtherValues(req.getOtherValues());
		doc.setPayeeName(req.getPayeeName());
		doc.setPaymentDueDate(req.getPaymentDueDate());
		doc.setPaymentInstruction(req.getPaymentInstruction());
		doc.setPaymentTerms(req.getPaymentTerms());
		doc.setPlantCode(req.getPlantCode());
		doc.setPortCode(req.getPortCode());
		doc.setPos(req.getPos());
		/*
		 * doc.setPreceedingInvoiceDate(req.getPreceedingInvoiceDate());
		 * doc.setPreceedingInvoiceNumber(req.getPreceedingInvoiceNumber());
		 */
		doc.setProfitCentre(req.getProfitCentre());
		doc.setProfitCentre2(req.getProfitCentre2());
		doc.setReceivedDate(req.getReceivedDate());
		doc.setReqReceivedOn(req.getReqReceivedOn());
		doc.setRet1CgstImpact(req.getRet1CgstImpact());
		doc.setRet1IgstImpact(req.getRet1IgstImpact());
		doc.setRet1SgstImpact(req.getRet1SgstImpact());
		doc.setReturnType(req.getReturnType());
		doc.setReverseCharge(req.getReverseCharge());
		doc.setRoundOff(req.getRoundOff());
		doc.setSalesOrderNumber(req.getSalesOrderNumber());
		doc.setSalesOrgnization(req.getSalesOrgnization());
		doc.setSaved(req.isSaved());
		doc.setSavedToGSTNDate(req.getSavedToGSTNDate());
		doc.setSection7OfIgstFlag(req.getSection7OfIgstFlag());
		doc.setSent(req.isSent());
		doc.setSentToGSTNDate(req.getSentToGSTNDate());
		doc.setSgstAmount(req.getSgstAmount());
		doc.setSgstin(req.getSgstin());
		doc.setShippingBillDate(req.getShippingBillDate());
		doc.setShippingBillNo(req.getShippingBillNo());
		doc.setShipToBuildingName(req.getShipToBuildingName());
		doc.setShipToBuildingNumber(req.getShipToBuildingNumber());
		doc.setShipToGstin(req.getShipToGstin());
		doc.setShipToLegalName(req.getShipToLegalName());
		doc.setShipToLocation(req.getShipToLocation());
		doc.setShipToPincode(req.getShipToPincode());
		doc.setShipToState(req.getShipToState());
		doc.setShipToTradeName(req.getShipToTradeName());
		doc.setSourceFileName(req.getSourceFileName());
		doc.setSourceIdentifier(req.getSourceIdentifier());
		doc.setStateApplyingCess(req.getStateApplyingCess());
		doc.setStateCessAmount(req.getStateCessAmount());
		doc.setStatus(req.getStatus());
		doc.setSubDivision(req.getSubDivision());
		doc.setSubmitted(req.isSubmitted());
		doc.setSubSupplyType(req.getSubSupplyType());
		doc.setSupplierBuildingName(req.getSupplierBuildingName());
		doc.setSupplierBuildingNumber(req.getSupplierBuildingNumber());
		doc.setSupplierEmail(req.getSupplierEmail());
		doc.setSupplierLegalName(req.getSupplierLegalName());
		doc.setSupplierLocation(req.getSupplierLocation());
		doc.setSupplierPhone(req.getSupplierPhone());
		doc.setSupplierPincode(req.getSupplierPincode());
		doc.setSupplierStateCode(req.getSupplierStateCode());
		doc.setSupplierTradeName(req.getSupplierTradeName());
		doc.setSupplyType(req.getSupplyType());
		doc.setTableType(req.getTableType());
		doc.setTableTypeNew(req.getTableTypeNew());
		doc.setTaxableValue(req.getTaxableValue());
		doc.setTaxPayable(req.getTaxPayable());
		doc.setTaxperiod(req.getTaxperiod());
		doc.setTaxScheme(req.getTaxScheme());
		doc.setTcsFlag(req.getTcsFlag());
		doc.setTcsFlagIncomeTax(req.getTcsFlagIncomeTax());
		doc.setTdsFlag(req.getTdsFlag());
		doc.setTotalInvoiceValueInWords(req.getTotalInvoiceValueInWords());
		doc.setTransactionType(req.getTransactionType());
		doc.setTransportDocDate(req.getTransportDocDate());
		doc.setTransportDocNo(req.getTransportDocNo());
		doc.setTransporterID(req.getTransporterID());
		doc.setTransporterName(req.getTransporterName());
		doc.setTransportMode(req.getTransportMode());
		doc.setUpdatedDate(req.getUpdatedDate());
		doc.setUserAccess1(req.getUserAccess1());
		doc.setUserAccess2(req.getUserAccess2());
		doc.setUserAccess3(req.getUserAccess3());
		doc.setUserAccess4(req.getUserAccess4());
		doc.setUserAccess5(req.getUserAccess5());
		doc.setUserAccess6(req.getUserAccess6());
		doc.setUserdefinedfield1(req.getUserdefinedfield1());
		doc.setUserdefinedfield2(req.getUserdefinedfield2());
		doc.setUserdefinedfield3(req.getUserdefinedfield3());
		doc.setUserDefinedField4(req.getUserDefinedField4());
		doc.setUserId(req.getUserId());
		doc.setVehicleNo(req.getVehicleNo());
		doc.setVehicleType(req.getVehicleType());

		List<PreDocDetails> preDocLists = req.getPreDocDtls();

		if (preDocLists != null && !preDocLists.isEmpty()) {
			doc.addPreDocDtls(preDocLists);
		}
		List<ContractDetails> contractLists = req.getContrDtls();

		if (contractLists != null && !contractLists.isEmpty()) {
			doc.addCntrDtls(contractLists);
		}
		List<AdditionalDocDetails> addDtlsList = req.getAddlDocDtls();

		if (addDtlsList != null && !addDtlsList.isEmpty()) {
			doc.addAddlDocDtls(addDtlsList);
		}

		for (OtrdTrnsDocLnItemsReq obj : lineItems) {

			OutwardTransDocLineItem item = new OutwardTransDocLineItem();

			List<AttributeDetails> addAttDtls = obj.getAttributeDtls();
			if (addAttDtls != null && !addAttDtls.isEmpty()) {
				item.addAttributeDtls(addAttDtls);
			}
			item.setAccountingVoucherDate(obj.getAccountingVoucherDate());
			item.setAccountingVoucherNumber(obj.getAccountingVoucherNumber());
			item.setAdjustedCessAmtAdvalorem(obj.getAdjustedCessAmtAdvalorem());
			item.setAdjustedCessAmtSpecific(obj.getAdjustedStateCessAmt());
			item.setAdjustedCgstAmt(obj.getAdjustedCgstAmt());
			item.setAdjustedIgstAmt(obj.getAdjustedIgstAmt());
			item.setAdjustedSgstAmt(obj.getAdjustedSgstAmt());
			item.setAdjustedStateCessAmt(obj.getAdjustedStateCessAmt());
			item.setAdjustedTaxableValue(obj.getAdjustedTaxableValue());
			item.setAdjustmentRefDate(obj.getAdjustmentRefDate());
			item.setAdjustmentRefNo(obj.getAdjustmentRefNo());

			item.setAutoPopToRefundFlag(obj.getAutoPopToRefundFlag());
			item.setBalanceAmount(obj.getBalanceAmount());
			item.setBarcode(obj.getBarcode());
			item.setBatchExpiryDate(obj.getBatchExpiryDate());
			item.setBatchNameOrNumber(obj.getBatchNameOrNumber());
			item.setBillToState(obj.getBillToState());
			item.setCessAmountAdvalorem(obj.getCessAmountAdvalorem());
			item.setCessAmountSpecific(obj.getCessAmountSpecific());
			item.setCessRateAdvalorem(obj.getCessRateAdvalorem());
			item.setCessRateSpecific(obj.getCessRateSpecific());
			item.setCgstAmount(obj.getCgstAmount());
			item.setCgstin(obj.getCgstin());
			item.setCgstRate(obj.getCgstRate());
			item.setClaimRefundFlag(obj.getClaimRefundFlag());
			item.setCrDrPreGst(obj.getCrDrPreGst());
			item.setCrDrReason(obj.getCrDrReason());
			item.setCreatedBy(obj.getCreatedBy());
			item.setCreatedDate(obj.getCreatedDate());
			item.setCustOrSuppAddress1(obj.getCustOrSuppAddress1());
			item.setCustOrSuppAddress2(obj.getCustOrSuppAddress2());
			item.setCustOrSuppAddress3(obj.getCustOrSuppAddress3());
			item.setCustOrSuppAddress4(obj.getCustOrSuppAddress4());
			item.setCustOrSuppCode(obj.getCustOrSuppCode());
			item.setCustOrSuppName(obj.getCustOrSuppName());
			item.setCustOrSuppType(obj.getCustOrSuppType());
			item.setDerivedTaxperiod(obj.getDerivedTaxperiod());
			item.setDiffPercent(obj.getDiffPercent());
			item.setDistributionChannel(obj.getDistributionChannel());
			item.setDivision(obj.getDivision());
			item.setDocDate(obj.getDocDate());
			item.setDocReferenceNumber(obj.getDocReferenceNumber());
			item.setEgstin(obj.getEgstin());
			item.setEWayBillDate(obj.getEWayBillDate());
			item.setEWayBillNo(obj.getEWayBillNo());
			item.setExportDuty(obj.getExportDuty());
			item.setFob(obj.getFob());
			item.setFreeQuantity(obj.getFreeQuantity());
			item.setGlCodeAdvCess(obj.getGlCodeAdvCess());
			item.setGlCodeCgst(obj.getGlCodeCgst());
			item.setGlCodeIgst(obj.getGlCodeIgst());
			item.setGlCodeSgst(obj.getGlCodeSgst());
			item.setGlCodeSpCess(obj.getGlCodeSpCess());
			item.setGlCodeStateCess(obj.getGlCodeStateCess());
			item.setGlCodeTaxableValue(obj.getGlCodeTaxableValue());
			item.setHsnSac(obj.getHsnSac());
			item.setId(obj.getId());
			item.setIgstAmount(obj.getIgstAmount());
			item.setIgstRate(obj.getIgstRate());
			item.setInvoiceAssessableAmount(obj.getInvoiceAssessableAmount());
			item.setInvoiceCessAdvaloremAmount(
					obj.getInvoiceCessAdvaloremAmount());
			item.setInvoiceCessSpecificAmount(
					obj.getInvoiceCessSpecificAmount());
			item.setInvoiceCgstAmount(obj.getInvoiceCgstAmount());
			item.setInvoiceIgstAmount(obj.getInvoiceIgstAmount());
			item.setInvoiceOtherCharges(obj.getInvoiceOtherCharges());
			/* item.setInvoiceReference(obj.getInvoiceReference()); */
			item.setInvoiceSgstAmount(obj.getInvoiceSgstAmount());
			item.setInvoiceStateCessAmount(obj.getInvoiceStateCessAmount());
			item.setInvStateCessSpecificAmt(obj.getInvStateCessSpecificAmt());
			item.setIsService(obj.getIsService());
			item.setItcFlag(obj.getItcFlag());
			item.setItemAmount(obj.getItemAmount());
			item.setItemCategory(obj.getItemCategory());
			item.setItemCode(obj.getItemCode());
			item.setItemDescription(obj.getItemDescription());
			item.setItemDiscount(obj.getItemDiscount());
			item.setLineItemAmt(obj.getLineItemAmt());
			item.setLineNo(obj.getLineNo());
			item.setLocation(obj.getLocation());
			item.setMemoValCess(obj.getMemoValCess());
			item.setMemoValCgst(obj.getMemoValCgst());
			item.setMemoValIgst(obj.getMemoValIgst());
			item.setMemoValSgst(obj.getMemoValSgst());
			item.setModifiedBy(obj.getModifiedBy());
			item.setModifiedDate(obj.getModifiedDate());
			item.setOrderLineReference(obj.getOrderLineReference());
			item.setOrigCgstin(obj.getOrigCgstin());
			item.setOrigDocDate(obj.getOrigDocDate());
			item.setOrigDocNo(obj.getOrigDocNo());
			item.setOrigDocType(obj.getOrigDocType());
			item.setOriginalInvoiceDate(obj.getOriginalInvoiceDate());
			item.setOriginalInvoiceNumber(obj.getOriginalInvoiceNumber());
			item.setOriginCountry(obj.getOriginCountry());
			item.setOtherValues(obj.getOtherValues());
			item.setPaidAmount(obj.getPaidAmount());
			item.setPlantCode(obj.getPlantCode());
			item.setPortCode(obj.getPortCode());
			item.setPos(obj.getPos());

			item.setPreceedingInvoiceDate(obj.getPreceedingInvoiceDate());
			item.setPreceedingInvoiceNumber(obj.getPreceedingInvoiceNumber());
			item.setInvoiceReference(obj.getInvoiceReference());

			item.setContractReference(obj.getContractReference());
			item.setReceiptAdviceDate(obj.getReceiptAdviceDate());
			item.setReceiptAdviceReference(obj.getReceiptAdviceReference());
			item.setTenderReference(obj.getTenderReference());
			item.setExternalReference(obj.getExternalReference());
			item.setCustomerPOReferenceNumber(
					obj.getCustomerPOReferenceNumber());
			item.setCustomerPOReferenceDate(obj.getCustomerPOReferenceDate());
			item.setProjectReference(obj.getProjectReference());
			item.setSupportingDocBase64(obj.getSupportingDocBase64());
			item.setSupportingDocURL(obj.getSupportingDocURL());
			item.setAttributeName(obj.getAttributeName());
			item.setAttributeValue(obj.getAttributeValue());
			item.setAdditionalInformation(obj.getAdditionalInformation());

			item.setPreTaxAmount(obj.getPreTaxAmount());
			item.setProductName(obj.getProductName());
			item.setProfitCentre(obj.getProfitCentre());
			item.setProfitCentre2(obj.getProfitCentre2());
			item.setQty(obj.getQty());
			/*
			 */
			item.setRet1CgstImpact(obj.getRet1CgstImpact());
			item.setRet1IgstImpact(obj.getRet1IgstImpact());
			item.setRet1SgstImpact(obj.getRet1SgstImpact());
			item.setReverseCharge(obj.getReverseCharge());
			item.setSalesOrgnization(obj.getSalesOrgnization());
			item.setSection7OfIgstFlag(obj.getSection7OfIgstFlag());
			item.setSerialNumberII(obj.getSerialNumberII());
			item.setSgstAmount(obj.getSgstAmount());
			item.setSgstRate(obj.getSgstRate());
			item.setShippingBillDate(obj.getShippingBillDate());
			item.setShippingBillNo(obj.getShippingBillNo());
			item.setShipToState(obj.getShipToState());
			item.setSourceFileName(obj.getSourceFileName());
			item.setStateApplyingCess(obj.getStateApplyingCess());
			item.setStateCessAmount(obj.getStateCessAmount());
			item.setStateCessRate(obj.getStateCessRate());
			item.setStateCessSpecificAmt(obj.getStateCessSpecificAmt());
			item.setStateCessSpecificRate(obj.getStateCessSpecificRate());
			item.setSubDivision(obj.getSubDivision());
			item.setSupplyType(obj.getSupplyType());

			item.setTaxableValue(obj.getTaxableValue());
			item.setTaxPayable(obj.getTaxPayable());
			item.setTaxperiod(obj.getTaxperiod());
			item.setTaxRate(obj.getTaxRate());
			item.setTcsAmount(obj.getTcsAmount());
			item.setTcsAmountIncomeTax(obj.getTcsAmountIncomeTax());
			item.setTcsCgstAmount(obj.getTcsCgstAmount());
			item.setTcsFlag(obj.getTcsFlag());
			item.setTcsRateIncomeTax(obj.getTcsRateIncomeTax());
			item.setTcsSgstAmount(obj.getTcsSgstAmount());
			item.setTdsCgstAmount(obj.getTdsCgstAmount());
			item.setTdsFlag(obj.getTdsFlag());
			item.setTdsIgstAmount(obj.getTdsIgstAmount());
			item.setTdsSgstAmount(obj.getTdsSgstAmount());
			item.setTotalAmt(obj.getTotalAmt());
			item.setTotalItemAmount(obj.getTotalItemAmount());
			item.setUnitPrice(obj.getUnitPrice());
			item.setUom(obj.getUom());

			item.setUserdefinedfield1(obj.getUserdefinedfield1());
			item.setUserdefinedfield2(obj.getUserdefinedfield2());
			item.setUserdefinedfield3(obj.getUserdefinedfield3());
			item.setUserDefinedField4(obj.getUserDefinedField4());
			item.setUserDefinedField5(obj.getUserDefinedField5());
			item.setUserDefinedField6(obj.getUserDefinedField6());

			item.setUserDefinedField7(obj.getUserDefinedField7());
			item.setUserDefinedField8(obj.getUserDefinedField8());
			item.setUserDefinedField9(obj.getUserDefinedField9());
			item.setUserDefinedField10(obj.getUserDefinedField10());
			item.setUserDefinedField11(obj.getUserDefinedField11());
			item.setUserDefinedField12(obj.getUserDefinedField12());

			item.setUserDefinedField13(obj.getUserDefinedField13());
			item.setUserDefinedField14(obj.getUserDefinedField14());
			item.setUserDefinedField15(obj.getUserDefinedField15());
			item.setUserDefinedField16(obj.getUserDefinedField16());
			item.setUserDefinedField17(obj.getUserDefinedField17());
			item.setUserDefinedField18(obj.getUserDefinedField18());

			item.setUserDefinedField19(obj.getUserDefinedField19());
			item.setUserDefinedField20(obj.getUserDefinedField20());
			item.setUserDefinedField21(obj.getUserDefinedField21());
			item.setUserDefinedField22(obj.getUserDefinedField22());
			item.setUserDefinedField23(obj.getUserDefinedField23());
			item.setUserDefinedField24(obj.getUserDefinedField24());

			item.setUserDefinedField25(obj.getUserDefinedField25());
			item.setUserDefinedField26(obj.getUserDefinedField26());
			item.setUserDefinedField27(obj.getUserDefinedField27());
			item.setUserDefinedField30(obj.getUserDefinedField30());

			item.setUserAccess1(obj.getUserAccess1());
			item.setUserAccess2(obj.getUserAccess2());
			item.setUserAccess3(obj.getUserAccess3());
			item.setUserAccess4(obj.getUserAccess4());
			item.setUserAccess5(obj.getUserAccess5());
			item.setUserAccess6(obj.getUserAccess6());

			item.setBatchExpiryDate(obj.getBatchExpiryDate());
			item.setWarrantyDate(obj.getWarrantyDate());
			item.setShippingBillDate(obj.getShippingBillDate());

			reqLineItems.add(item);
		}
		doc.setLineItems(reqLineItems);
		return doc;
	}

	public static GenerateEWBByIrnERPReqDto convertERPSoapReqToGenIrn(
			GenerateEWBByIRNSoapReqDto req) {
		GenerateEWBByIrnERPReqDto reqObj = new GenerateEWBByIrnERPReqDto();
		reqObj.setGstin(req.getGstin());
		reqObj.setDistance(req.getDistance());
		reqObj.setIrn(req.getIrn());
		reqObj.setTransMode(req.getTransMode());
		reqObj.setTrnDocDt(req.getTrnDocDt());
		reqObj.setTrnDocNo(req.getTrnDocNo());
		reqObj.setTransId(req.getTransId());
		reqObj.setTransName(req.getTransName());
		reqObj.setVehNo(req.getVehNo());
		reqObj.setVehType(req.getVehType());
		reqObj.setCustPincd(req.getCustPincd());
		reqObj.setDispatcherPincd(req.getDispatcherPincd());
		reqObj.setShipToPincd(req.getShipToPincd());
		reqObj.setSuppPincd(req.getSuppPincd());
		reqObj.setDocCategory(req.getDocCategory());
		reqObj.setSupplyType(req.getSupplyType());

		if (req.getDispDtls() != null) {

			GenEWBByIrnDispERPReqDto erpReqdispDtls = new GenEWBByIrnDispERPReqDto();
			erpReqdispDtls.setAddr1(req.getDispDtls().getAddr1());
			if (!Strings.isNullOrEmpty(req.getDispDtls().getAddr2())) {
				erpReqdispDtls.setAddr2(req.getDispDtls().getAddr2());
			}
			erpReqdispDtls.setLoc(req.getDispDtls().getLoc());
			erpReqdispDtls.setPin(req.getDispDtls().getPin());
			erpReqdispDtls.setStcd(req.getDispDtls().getStcd());
			erpReqdispDtls.setNm(req.getDispDtls().getNm());
			reqObj.setDispDtls(erpReqdispDtls);
		}
		if (req.getExpShipDtls() != null) {
			GenEWBByIrnExpShpERPReqDto erpReqExpShpDtls = new GenEWBByIrnExpShpERPReqDto();
			erpReqExpShpDtls.setAddr1(req.getExpShipDtls().getAddr1());
			if (!Strings.isNullOrEmpty(req.getExpShipDtls().getAddr2())) {
				erpReqExpShpDtls.setAddr2(req.getExpShipDtls().getAddr2());
			}
			erpReqExpShpDtls.setLoc(req.getExpShipDtls().getLoc());
			erpReqExpShpDtls.setPin(req.getExpShipDtls().getPin());
			erpReqExpShpDtls.setStcd(req.getExpShipDtls().getStcd());
			reqObj.setExpShipDtls(erpReqExpShpDtls);
		}

		return reqObj;
	}

}
