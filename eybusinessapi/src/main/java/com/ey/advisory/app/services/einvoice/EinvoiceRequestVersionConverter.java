/**
 * 
 */
package com.ey.advisory.app.services.einvoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.AdditionalDocDetails;
import com.ey.advisory.app.data.business.dto.AttributeDetails;
import com.ey.advisory.app.data.business.dto.ContractDetails;
import com.ey.advisory.app.data.business.dto.OutwardTransDocLineItem;
import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.app.data.business.dto.PreDocDetails;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EInvoiceOutwardSupplyTypeResolver;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.einv.common.EyEInvCommonUtil;
import com.ey.advisory.einv.dto.AddlDocument;
import com.ey.advisory.einv.dto.Attribute;
import com.ey.advisory.einv.dto.BatchDetails;
import com.ey.advisory.einv.dto.BuyerDetails;
import com.ey.advisory.einv.dto.Contract;
import com.ey.advisory.einv.dto.DispatchDetails;
import com.ey.advisory.einv.dto.DocPerdDtls;
import com.ey.advisory.einv.dto.DocumentDetails;
import com.ey.advisory.einv.dto.EinvEwbDetails;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.ey.advisory.einv.dto.ExportDetails;
import com.ey.advisory.einv.dto.ItemDto;
import com.ey.advisory.einv.dto.PayeeDetails;
import com.ey.advisory.einv.dto.PrecDocument;
import com.ey.advisory.einv.dto.RefDtls;
import com.ey.advisory.einv.dto.SellerDetails;
import com.ey.advisory.einv.dto.ShippingDetails;
import com.ey.advisory.einv.dto.TransactionDetails;
import com.ey.advisory.einv.dto.ValueDetails;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("EinvoiceRequestVersionConverter")
@Slf4j
public class EinvoiceRequestVersionConverter {

	@Autowired
	@Qualifier("EInvoiceDefaultOutwardSupplyTypeResolver")
	EInvoiceOutwardSupplyTypeResolver einvSupplyTypeResolver;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	public EinvoiceRequestDto convert(OutwardTransDocument hdr) {
		try {
			PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_SERVICE",
					"EinvoiceRequestVersionConverter", "convert",
					"Convert_START", true);

			reqLogHelper.logAppMessage(hdr.getDocNo(), null, null,
					"Converting Generate E Invoice Request");
			EinvoiceRequestDto einvRequestDto = new EinvoiceRequestDto();
			if (hdr.getLineItems() == null || hdr.getLineItems().isEmpty()) {
				String errMsg = "Atleast one lineItem is requried to process the Invoice";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			OutwardTransDocLineItem hdrLineItm = hdr.getLineItems().get(0);
			String supType = hdr.getDerivedSupplyType();

			if (Strings.isNullOrEmpty(supType)) {
				supType = einvSupplyTypeResolver.resolve(hdr);
				hdr.setDerivedSupplyType(supType);
			}

			TransactionDetails tranDtls = new TransactionDetails();
			// tranDtls.setEcmGstin(hdr.getEgstin());
			String revFlag = hdr.getReverseCharge();
			tranDtls.setRegRev(revFlag != null ? revFlag.toUpperCase() : "N");
			tranDtls.setSupTyp(
					EyEInvCommonUtil.getSupplyType(supType, hdr.getCgstin()));
			tranDtls.setTaxSch("GST");
			tranDtls.setIgstOnIntra(hdr.getSection7OfIgstFlag());

			DocumentDetails docDetails = new DocumentDetails();
			docDetails.setDt(hdr.getDocDate());
			docDetails.setNo(hdr.getDocNo().toUpperCase());
			docDetails
					.setTyp(EyEInvCommonUtil.convertDocType(hdr.getDocType()));

			SellerDetails sellerDtls = new SellerDetails();
			sellerDtls.setAddr1(hdr.getSupplierBuildingNumber());
			sellerDtls.setAddr2(hdr.getSupplierBuildingName());
			sellerDtls.setEm(hdr.getSupplierEmail());
			sellerDtls.setGstin(hdr.getSgstin());
			sellerDtls.setLglNm(hdr.getSupplierLegalName());
			sellerDtls.setLoc(hdr.getSupplierLocation());
			sellerDtls.setPh(hdr.getSupplierPhone() != null
					? hdr.getSupplierPhone() : null);
			sellerDtls.setPin(hdr.getSupplierPincode() != null
					? hdr.getSupplierPincode() : null);
			sellerDtls.setState(
					EyEInvCommonUtil.getstateCode(hdr.getSupplierStateCode()));
			sellerDtls.setTrdNm(hdr.getSupplierTradeName());

			BuyerDetails buyerDetails = new BuyerDetails();
			buyerDetails.setAddr1(hdr.getCustOrSuppAddress1());
			buyerDetails.setAddr2(hdr.getCustOrSuppAddress2());
			buyerDetails.setEm(hdr.getCustomerEmail());
			buyerDetails.setGstin(
					EyEInvCommonUtil.getBuyerGstin(hdr.getCgstin(), supType));
			buyerDetails.setLglNm(hdr.getCustOrSuppName());
			buyerDetails.setLoc(hdr.getCustOrSuppAddress4());
			buyerDetails.setPh(hdr.getCustomerPhone() != null
					? hdr.getCustomerPhone() : null);
			buyerDetails.setPin(hdr.getCustomerPincode() != null
					? hdr.getCustomerPincode() : null);
			buyerDetails.setPos(EyEInvCommonUtil.getstateCode(hdr.getPos()));
			buyerDetails.setState(
					EyEInvCommonUtil.getstateCode(hdr.getBillToState()));
			buyerDetails.setTrdNm(hdr.getCustomerTradeName());

			DispatchDetails dispatchDetails = new DispatchDetails();
			dispatchDetails.setAddr1(hdr.getDispatcherBuildingNumber());
			dispatchDetails.setAddr2(hdr.getDispatcherBuildingName());
			dispatchDetails.setLoc(hdr.getDispatcherLocation());
			dispatchDetails.setNm(hdr.getDispatcherTradeName());
			dispatchDetails.setPin(hdr.getDispatcherPincode() != null
					? hdr.getDispatcherPincode() : null);
			dispatchDetails.setStcd(EyEInvCommonUtil
					.getstateCode(hdr.getDispatcherStateCode()));

			ShippingDetails shippingDetails = new ShippingDetails();
			shippingDetails.setAddr1(hdr.getShipToBuildingNumber());
			shippingDetails.setAddr2(hdr.getShipToBuildingName());
			shippingDetails.setGstin(hdr.getShipToGstin());
			shippingDetails.setLglNm(hdr.getShipToLegalName());
			shippingDetails.setLoc(hdr.getShipToLocation());
			shippingDetails.setPin(hdr.getShipToPincode() != null
					? hdr.getShipToPincode() : null);
			shippingDetails.setStcd(
					EyEInvCommonUtil.getstateCode(hdr.getShipToState()));
			shippingDetails.setTrdNm(hdr.getShipToTradeName());

			ValueDetails valueDetails = new ValueDetails();
			valueDetails.setAssVal(
					defaultToZeroIfNull(hdr.getInvoiceAssessableAmount()));
			valueDetails.setCesVal(EyEInvCommonUtil.getValueCesVal(
					hdr.getInvoiceCessAdvaloremAmount(),
					hdr.getInvoiceCessSpecificAmount()));
			valueDetails.setCgstVal(
					defaultToZeroIfNull(hdr.getInvoiceCgstAmount()));
			valueDetails.setIgstVal(
					defaultToZeroIfNull(hdr.getInvoiceIgstAmount()));
			valueDetails.setRndOffAmt(defaultToZeroIfNull(hdr.getRoundOff()));
			valueDetails.setSgstVal(
					defaultToZeroIfNull(hdr.getInvoiceSgstAmount()));
			valueDetails.setStCesVal(EyEInvCommonUtil.getStCessVal(
					hdr.getInvoiceStateCessAmount(),
					hdr.getInvStateCessSpecificAmt()));
			valueDetails.setTotInvVal(defaultToZeroIfNull(hdr.getDocAmount()));
			valueDetails.setTotInvValFc(
					defaultToZeroIfNull(hdr.getInvoiceValueFc()));
			valueDetails.setDiscount(
					defaultToZeroIfNull(hdr.getUserDefinedField28()));
			valueDetails.setOthChrg(
					defaultToZeroIfNull(hdr.getInvoiceOtherCharges()));

			PayeeDetails payeeDetails = new PayeeDetails();
			payeeDetails.setAccDet(hdr.getAccountDetail());
			payeeDetails.setCrDay(hdr.getCreditDays());
			payeeDetails.setCrTrn(hdr.getCreditTransfer());
			payeeDetails.setDirDr(hdr.getDirectDebit());
			payeeDetails.setFinInsBr(hdr.getBranchOrIfscCode());
			payeeDetails.setMode(hdr.getModeOfPayment());
			payeeDetails.setNm(hdr.getPayeeName());
			payeeDetails.setPaidAmt(
					defaultToZeroIfNull(hdrLineItm.getPaidAmount()));
			payeeDetails.setPayInstr(hdr.getPaymentInstruction());
			payeeDetails.setPaymtDue(
					defaultToZeroIfNull(hdrLineItm.getBalanceAmount()));
			payeeDetails.setPayTerm(hdr.getPaymentTerms());

			ExportDetails exportDetails = new ExportDetails();
			String countryCode = hdr.getCountryCode();
			String forCur = hdr.getForeignCurrency();
			exportDetails.setCntCode(countryCode);
			exportDetails.setForCur(forCur);
			exportDetails.setPort(hdr.getPortCode());
			exportDetails.setRefClm(hdr.getClaimRefundFlag());
			exportDetails.setShipBDt(hdr.getShippingBillDate());
			exportDetails.setShipBNo(hdr.getShippingBillNo());
			RefDtls referenceDetails = new RefDtls();
			DocPerdDtls docPerdDtls = new DocPerdDtls();
			referenceDetails.setInvRm(hdr.getInvoiceRemarks());
			docPerdDtls.setInvEndDt(hdr.getInvoicePeriodEndDate());
			docPerdDtls.setInvStDt(hdr.getInvoicePeriodStartDate());
			if (!DocPerdDtls.isEmpty(docPerdDtls)) {
				referenceDetails.setDocPerdDtls(docPerdDtls);
			}

			EinvEwbDetails ewbDetails = new EinvEwbDetails();

			ewbDetails.setDistance(
					hdr.getDistance() != null ? hdr.getDistance() : null);
			ewbDetails.setTransId(hdr.getTransporterID());
			ewbDetails.setTransName(hdr.getTransporterName());

			boolean isPartBEligible = isEligibleForPartB(hdr);

			if (isPartBEligible) {
				ewbDetails.setTransDocDt(hdr.getTransportDocDate());
				ewbDetails.setTransDocNo(hdr.getTransportDocNo());
				ewbDetails.setTransMode(
						EyEInvCommonUtil.getTransMode(hdr.getTransportMode()));
				ewbDetails.setVehNo(hdr.getVehicleNo());
				ewbDetails.setVehType(hdr.getVehicleType());

			}

			List<ContractDetails> contractLists = hdr.getContrDtls();
			if (contractLists != null && !contractLists.isEmpty()) {
				List<Contract> contractList = new ArrayList<>();
				for (ContractDetails cont : contractLists) {
					Contract contract = new Contract();
					contract.setContrRefr(cont.getContractReference());
					contract.setExtRefr(cont.getExternalReference());
					contract.setPORefDt(cont.getCustomerPOReferenceDate());
					contract.setPORefr(cont.getCustomerPOReferenceNumber());
					contract.setProjRefr(cont.getProjectReference());
					contract.setRecAdvDt(cont.getReceiptAdviceDate());
					contract.setRecAdvRefr(cont.getReceiptAdviceReference());
					contract.setTendRefr(cont.getTenderReference());
					contractList.add(contract);
				}
				referenceDetails.setContrDtls(contractList);
			}

			List<PreDocDetails> preDocLists = hdr.getPreDocDtls();
			if (preDocLists != null && !preDocLists.isEmpty()) {
				List<PrecDocument> precDocList = new ArrayList<>();
				for (PreDocDetails preDoc : preDocLists) {
					PrecDocument preDocument = new PrecDocument();
					preDocument.setInvNo(preDoc.getPreceedingInvoiceNumber());
					preDocument.setInvDt(preDoc.getPreceedingInvoiceDate());
					preDocument.setOthRefNo(preDoc.getInvoiceReference());
					precDocList.add(preDocument);
				}
				referenceDetails.setPrecDocDtls(precDocList);
			}

			List<AdditionalDocDetails> addDtlsList = hdr.getAddlDocDtls();
			if (addDtlsList != null && !addDtlsList.isEmpty()) {
				List<AddlDocument> addDocList = new ArrayList<>();
				for (AdditionalDocDetails adddtls : addDtlsList) {
					AddlDocument doc = new AddlDocument();
					doc.setDocs(adddtls.getSupportingDocBase64());
					doc.setInfo(adddtls.getAdditionalInformation());
					doc.setUrl(adddtls.getSupportingDocURL());
					addDocList.add(doc);
				}
				einvRequestDto.setAddlDocDtls(addDocList);
			}

			List<ItemDto> itemList = new ArrayList<>();
			List<OutwardTransDocLineItem> lineItems = hdr.getLineItems();
			for (OutwardTransDocLineItem lineItem : lineItems) {
				ItemDto item = new ItemDto();
				item.setAssAmt(defaultToZeroIfNull(lineItem.getTaxableValue()));

				if (lineItem.getAttributeDtls() != null
						&& !lineItem.getAttributeDtls().isEmpty()) {
					List<AttributeDetails> attrList = lineItem
							.getAttributeDtls();
					List<Attribute> attributeList = new ArrayList<>();
					for (AttributeDetails attrs : attrList) {
						Attribute attr = new Attribute();
						attr.setNm(attrs.getAttributeName());
						attr.setVal(attrs.getAttributeValue());
						attributeList.add(attr);
					}
					item.setAttribDtls(attributeList);
				}

				item.setBarcde(lineItem.getBarcode());
				BatchDetails batchDetails = new BatchDetails();
				batchDetails.setExpDt(lineItem.getBatchExpiryDate());
				batchDetails.setNm(lineItem.getBatchNameOrNumber());
				batchDetails.setWrDt(lineItem.getWarrantyDate());
				if (!BatchDetails.isEmpty(batchDetails))
					item.setBchDtls(batchDetails);
				item.setCesAmt(
						defaultToZeroIfNull(lineItem.getCessAmountAdvalorem()));
				item.setCesNonAdvlAmt(
						defaultToZeroIfNull(lineItem.getCessAmountSpecific()));
				item.setCesRt(
						defaultToZeroIfNull(lineItem.getCessRateAdvalorem()));
				item.setCgstAmt(defaultToZeroIfNull(lineItem.getCgstAmount()));
				item.setDiscount(
						defaultToZeroIfNull(lineItem.getItemDiscount()));
				item.setFreeQty(lineItem.getFreeQuantity() != null
						? lineItem.getFreeQuantity() : null);
				item.setGstRt(defaultToZeroIfNull(EyEInvCommonUtil.getGstRate(
						lineItem.getIgstRate(), lineItem.getCgstRate(),
						lineItem.getSgstRate())));
				item.setHsnCd(lineItem.getHsnSac());
				item.setIgstAmt(defaultToZeroIfNull(lineItem.getIgstAmount()));
				item.setIsServc(lineItem.getIsService());
				item.setOrdLineRef(lineItem.getOrderLineReference());
				item.setOrgCntry(lineItem.getOriginCountry());
				item.setOthChrg(lineItem.getOtherValues());
				item.setPrdDesc(lineItem.getItemDescription());
				item.setPrdSlNo(lineItem.getSerialNumberII());
				item.setPreTaxVal(
						defaultToZeroIfNull(lineItem.getPreTaxAmount()));
				item.setQty(defaultToZeroIfNull(lineItem.getQty()));
				item.setSgstAmt(defaultToZeroIfNull(lineItem.getSgstAmount()));
				item.setSlNo(String.valueOf(lineItem.getLineNo()));
				item.setStateCesAmt(defaultToZeroIfNull(
						lineItem.getStateCessSpecificAmt()));
				item.setStateCesNonAdvlAmt(
						defaultToZeroIfNull(lineItem.getStateCessAmount()));
				item.setStateCesRt(defaultToZeroIfNull(
						lineItem.getStateCessSpecificRate()));
				item.setTotAmt(defaultToZeroIfNull(lineItem.getItemAmount()));
				item.setTotItemVal(
						defaultToZeroIfNull(lineItem.getTotalItemAmount()));
				item.setUnit(lineItem.getUom());
				item.setUnitPrice(defaultToZeroIfNull(lineItem.getUnitPrice()));
				itemList.add(item);
			}

			einvRequestDto.setIrn("");
			einvRequestDto.setVersion("1.1");
			einvRequestDto.setTaxSch("GST");

			boolean isEligibleforDocCatFlag = isAddressSuppressRequired(hdr);
			Quartet<Boolean, Boolean, Boolean, Boolean> eligibleAddSuppression = EyEInvCommonUtil
					.eligibleAddresstobeNIC(hdr.getDocCategory(),
							isEligibleforDocCatFlag);
			boolean isSellerDetailsReq = eligibleAddSuppression.getValue0();
			boolean isBuyerDetailsReq = eligibleAddSuppression.getValue1();
			boolean isDispatcherDetailsReq = eligibleAddSuppression.getValue2();
			boolean isShiptoDetailsReq = eligibleAddSuppression.getValue3();

			if (!SellerDetails.isEmpty(sellerDtls) && isSellerDetailsReq) {
				einvRequestDto.setSellerDtls(sellerDtls);
			}

			if (!BuyerDetails.isEmpty(buyerDetails) && isBuyerDetailsReq) {
				einvRequestDto.setBuyerDtls(buyerDetails);
			}

			if (!DispatchDetails.isEmpty(dispatchDetails)
					&& isDispatcherDetailsReq) {
				einvRequestDto.setDispDtls(dispatchDetails);
			}

			if (!ShippingDetails.isEmpty(shippingDetails)
					&& isShiptoDetailsReq) {
				einvRequestDto.setShipDtls(shippingDetails);
			}

			if (!DocumentDetails.isEmpty(docDetails)) {
				einvRequestDto.setDocDtls(docDetails);
			}

			if (!ExportDetails.isEmpty(exportDetails)) {
				einvRequestDto.setExpDtls(exportDetails);
			}

			if (!itemList.isEmpty()) {
				einvRequestDto.setItemList(itemList);
			}

			if (!PayeeDetails.isEmpty(payeeDetails)) {
				einvRequestDto.setPayDtls(payeeDetails);
			}

			if (!RefDtls.isEmpty(referenceDetails))
				einvRequestDto.setRefDtls(referenceDetails);

			if (!TransactionDetails.isEmpty(tranDtls)) {
				einvRequestDto.setTranDtls(tranDtls);
			}
			if (!ValueDetails.isEmpty(valueDetails)) {
				einvRequestDto.setValDtls(valueDetails);
			}

			if (ewbDetails != null && !EinvEwbDetails.isEmpty(ewbDetails)) {
				einvRequestDto.setEwbDetails(ewbDetails);
			}
			PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_SERVICE",
					"EinvoiceRequestVersionConverter", "convert",
					"Convert_END", true);
			
			return einvRequestDto;
		} catch (Exception e) {
			LOGGER.error("Exception occured while conversion", e);
			throw e;
		}
	}

	private boolean isEligibleForPartB(OutwardTransDocument hdr) {

		PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_SERVICE",
				"EinvoiceRequestVersionConverter", "isEligibleForPartB",
				"isEligibleForPartB_DB_START", true);
		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"ewb.partb", TenantContext.getTenantId());

		boolean partBSuppresReq = configMap.get("ewb.partb.suppresreq") == null
				? Boolean.FALSE
				: Boolean.valueOf(
						configMap.get("ewb.partb.suppresreq").getValue());

		PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_SERVICE",
				"EinvoiceRequestVersionConverter", "isEligibleForPartB",
				"isEligibleForPartB_DB_END", true);
		if (!partBSuppresReq) {
			return true;
		}

		if (hdr.getTransportMode() == null
				|| Strings.isNullOrEmpty(hdr.getTransportMode())) {
			return false;
		}

		if ("Road".equalsIgnoreCase(hdr.getTransportMode())) {
			return !Strings.isNullOrEmpty(hdr.getVehicleNo())
					&& !Strings.isNullOrEmpty(hdr.getVehicleType());
		} else if ("InTransit".equalsIgnoreCase(hdr.getTransportMode())) {
			return true;
		} else {
			return !Strings.isNullOrEmpty(hdr.getTransportDocNo())
					&& hdr.getTransportDocDate() != null;
		}
	}

	private boolean isAddressSuppressRequired(OutwardTransDocument hdr) {

		if (Strings.isNullOrEmpty(hdr.getDocCategory())) {
			return false;
		}


		PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_SERVICE",
				"EinvoiceRequestVersionConverter", "isAddressSuppressRequired",
				"isAddressSuppressRequired_START", true);
		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"einv.address", TenantContext.getTenantId());

		boolean docCategorySuppresReq = configMap
				.get("einv.address.suppresreq") == null ? Boolean.FALSE
						: Boolean.valueOf(configMap
								.get("einv.address.suppresreq").getValue());
		
		PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_SERVICE",
				"EinvoiceRequestVersionConverter", "isAddressSuppressRequired",
				"isAddressSuppressRequired_END", true);

		if (docCategorySuppresReq) {
			if ("EXP".equalsIgnoreCase(hdr.getSubSupplyType())
					|| Stream.of("EXPT", "EXPWT").anyMatch(
							hdr.getDerivedSupplyType()::equalsIgnoreCase)) {
				return false;
			}
		}
		return docCategorySuppresReq;
	}

	private BigDecimal defaultToZeroIfNull(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

}
