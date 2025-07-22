/**
 * 
 */
package com.ey.advisory.app.data.services.einvoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.AdditionalDocDetails;
import com.ey.advisory.app.data.entities.client.AttributeDetails;
import com.ey.advisory.app.data.entities.client.ContractDetails;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.entities.client.PreceedingDocDetails;
import com.ey.advisory.app.services.docs.einvoice.EInvoiceOutwardSupplyTypeResolver;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
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
@Component("EinvoiceRequestConverter")
@Slf4j
public class EinvoiceRequestConverter {

	@Autowired
	@Qualifier("EInvoiceDefaultOutwardSupplyTypeResolver")
	EInvoiceOutwardSupplyTypeResolver einvSupplyTypeResolver;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	public EinvoiceRequestDto convert(OutwardTransDocument hdr,
			String isEwbRequired) {
		try {

			EinvoiceRequestDto einvRequestDto = new EinvoiceRequestDto();
			List<Contract> contractList = new ArrayList<>();
			List<PrecDocument> precDocList = new ArrayList<>();
			OutwardTransDocLineItem hdrLineItm = hdr.getLineItems().get(0);
			String supType = hdr.getDerivedSupplyType();

			if (Strings.isNullOrEmpty(supType))
				supType = einvSupplyTypeResolver.resolve(hdr);

			TransactionDetails tranDtls = new TransactionDetails();
//			tranDtls.setEcmGstin(hdr.getEgstin());
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
			valueDetails.setAssVal(hdr.getInvoiceAssessableAmount());
			valueDetails.setCesVal(EyEInvCommonUtil.getValueCesVal(
					hdr.getInvoiceCessAdvaloremAmount(),
					hdr.getInvoiceCessSpecificAmount()));
			valueDetails.setCgstVal(hdr.getInvoiceCgstAmount());
			valueDetails.setIgstVal(hdr.getInvoiceIgstAmount());
			valueDetails.setRndOffAmt(hdr.getRoundOff());
			valueDetails.setSgstVal(hdr.getInvoiceSgstAmount());
			valueDetails.setStCesVal(EyEInvCommonUtil.getStCessVal(
					hdr.getInvoiceStateCessAmount(),
					hdr.getInvStateCessSpecificAmt()));
			valueDetails.setTotInvVal(hdr.getDocAmount());
			valueDetails.setTotInvValFc(hdr.getInvoiceValueFc());
			valueDetails.setDiscount(hdr.getUserDefinedField28() != null
					? hdr.getUserDefinedField28() : null);
			valueDetails.setOthChrg(hdr.getInvoiceOtherCharges());

			PayeeDetails payeeDetails = new PayeeDetails();
			payeeDetails.setAccDet(hdr.getAccountDetail());
			payeeDetails.setCrDay(hdr.getCreditDays());
			payeeDetails.setCrTrn(hdr.getCreditTransfer());
			payeeDetails.setDirDr(hdr.getDirectDebit());
			payeeDetails.setFinInsBr(hdr.getBranchOrIfscCode());
			payeeDetails.setMode(hdr.getModeOfPayment());
			payeeDetails.setNm(hdr.getPayeeName());
			payeeDetails.setPaidAmt(hdrLineItm.getPaidAmount());
			payeeDetails.setPayInstr(hdr.getPaymentInstruction());
			payeeDetails.setPaymtDue(hdrLineItm.getBalanceAmount());
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
			if ("true".equalsIgnoreCase(isEwbRequired)) {
				ewbDetails.setDistance(hdr.getDistance() != null
						? hdr.getDistance().intValue() : null);
				ewbDetails.setTransId(hdr.getTransporterID());
				ewbDetails.setTransName(hdr.getTransporterName());
				boolean isPartBEligible = isEligibleForPartB(hdr);

				if (isPartBEligible) {
					ewbDetails.setTransDocDt(hdr.getTransportDocDate());
					ewbDetails.setTransDocNo(hdr.getTransportDocNo());
					ewbDetails.setTransMode(EyEInvCommonUtil
							.getTransMode(hdr.getTransportMode()));
					ewbDetails.setVehNo(hdr.getVehicleNo());
					ewbDetails.setVehType(hdr.getVehicleType());

				}
			}

			List<ContractDetails> contractLists = hdr.getContrDtls();

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

			List<PreceedingDocDetails> preDocLists = hdr.getPreDocDtls();
			for (PreceedingDocDetails preDoc : preDocLists) {
				PrecDocument preDocument = new PrecDocument();
				preDocument.setInvNo(preDoc.getPreceedingInvoiceNumber());
				preDocument.setInvDt(preDoc.getPreceedingInvoiceDate());
				preDocument.setOthRefNo(preDoc.getInvoiceReference());
				precDocList.add(preDocument);

			}

			referenceDetails.setContrDtls(
					!contractList.isEmpty() ? contractList : null);
			referenceDetails.setPrecDocDtls(
					!precDocList.isEmpty() ? precDocList : null);

			List<AdditionalDocDetails> addDtlsList = hdr.getAddlDocDtls();

			List<AddlDocument> addDocList = new ArrayList<>();
			for (AdditionalDocDetails adddtls : addDtlsList) {
				AddlDocument doc = new AddlDocument();
				doc.setDocs(adddtls.getSupportingDocBase64());
				doc.setInfo(adddtls.getAdditionalInformation());
				doc.setUrl(adddtls.getSupportingDocURL());
				addDocList.add(doc);
			}

			List<ItemDto> itemList = new ArrayList<>();
			List<OutwardTransDocLineItem> lineItems = hdr.getLineItems();
			for (OutwardTransDocLineItem lineItem : lineItems) {
				ItemDto item = new ItemDto();
				item.setAssAmt(lineItem.getTaxableValue());

				List<Attribute> attributeList = new ArrayList<>();
				if (lineItem.getAttribDtls() != null) {
					List<AttributeDetails> attrList = lineItem.getAttribDtls();

					for (AttributeDetails attrs : attrList) {
						Attribute attr = new Attribute();
						attr.setNm(attrs.getAttributeName());
						attr.setVal(attrs.getAttributeValue());
						attributeList.add(attr);
					}
				}
				item.setAttribDtls(attributeList);

				item.setBarcde(lineItem.getBarcode());
				BatchDetails batchDetails = new BatchDetails();
				batchDetails.setExpDt(lineItem.getBatchExpiryDate());
				batchDetails.setNm(lineItem.getBatchNameOrNumber());
				batchDetails.setWrDt(lineItem.getWarrantyDate());
				if (!BatchDetails.isEmpty(batchDetails))
					item.setBchDtls(batchDetails);
				item.setCesAmt(lineItem.getCessAmountAdvalorem());
				item.setCesNonAdvlAmt(lineItem.getCessAmountSpecific());
				item.setCesRt(lineItem.getCessRateAdvalorem());
				item.setCgstAmt(lineItem.getCgstAmount());
				item.setDiscount(lineItem.getItemDiscount());
				item.setFreeQty(lineItem.getFreeQuantity() != null
						? lineItem.getFreeQuantity() : null);
				item.setGstRt(EyEInvCommonUtil.getGstRate(
						lineItem.getIgstRate(), lineItem.getCgstRate(),
						lineItem.getSgstRate()));
				item.setHsnCd(lineItem.getHsnSac());
				item.setIgstAmt(lineItem.getIgstAmount());
				item.setIsServc(lineItem.getIsService());
				item.setOrdLineRef(lineItem.getOrderLineReference());
				item.setOrgCntry(lineItem.getOriginCountry());
				item.setOthChrg(lineItem.getOtherValues());
				item.setPrdDesc(lineItem.getItemDescription());
				item.setPrdSlNo(lineItem.getSerialNumberII());
				item.setPreTaxVal(lineItem.getPreTaxAmount());
				item.setQty(lineItem.getItemQtyUser() != null
						? lineItem.getItemQtyUser() : null);
				item.setSgstAmt(lineItem.getSgstAmount());
				item.setSlNo(String.valueOf(lineItem.getLineNo()));
				item.setStateCesAmt(lineItem.getStateCessAmount());
				item.setStateCesNonAdvlAmt(lineItem.getStateCessSpecificAmt());
				item.setStateCesRt(lineItem.getStateCessRate());
				item.setTotAmt(lineItem.getItemAmount());
				item.setTotItemVal(lineItem.getTotalItemAmount());
				item.setUnit(lineItem.getItemUqcUser());
				item.setUnitPrice(lineItem.getUnitPrice());
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
			if (!addDocList.isEmpty()) {
				einvRequestDto.setAddlDocDtls(addDocList);
			}

			return einvRequestDto;

		} catch (Exception e) {
			LOGGER.error("Exception occured while conversion", e);
			throw new AppException("Exception occured while conversion", e);
		}

	}

	private boolean isAddressSuppressRequired(OutwardTransDocument hdr) {

		if (Strings.isNullOrEmpty(hdr.getDocCategory())) {
			return false;
		}
		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"einv.address", TenantContext.getTenantId());

		boolean docCategorySuppresReq = configMap
				.get("einv.address.suppresreq") == null ? Boolean.FALSE
						: Boolean.valueOf(configMap
								.get("einv.address.suppresreq").getValue());

		return docCategorySuppresReq;
	}

	private boolean isEligibleForPartB(OutwardTransDocument hdr) {

		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"ewb.partb", TenantContext.getTenantId());

		boolean partBSuppresReq = configMap.get("ewb.partb.suppresreq") == null
				? Boolean.FALSE
				: Boolean.valueOf(
						configMap.get("ewb.partb.suppresreq").getValue());

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
}
