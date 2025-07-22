package com.ey.advisory.app.services.docs.gstr2;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.google.common.base.Strings;

/**
 * 
 * @author Laxmi.Salukuti
 *
 */
@Component("InwardField240Truncation")
public class InwardField240Truncation {

	/**
	 * This method truncated Inward Header fields
	 * 
	 * @param document
	 */
	public void truncateHeaderFields(InwardTransDocument document) {

		String userId = document.getUserId();
		if (userId != null && !userId.isEmpty()) {
			if (userId.length() > 100) {
				document.setUserId(userId.substring(0, 100));
			}
		}

		String sourceFileName = document.getSourceFileName();
		if (sourceFileName != null && !sourceFileName.isEmpty()) {
			if (sourceFileName.length() > 100) {
				document.setSourceFileName(sourceFileName.substring(0, 100));
			}
		}

		String originalSgstin = document.getOrigSgstin();
		if (originalSgstin != null && !originalSgstin.isEmpty()) {
			if (document.getOrigSgstin().length() > 15) {
				document.setOrigSgstin(originalSgstin.substring(0, 15));
			}
		}

		String supplierName = document.getCustOrSuppName();
		if (supplierName != null && !supplierName.isEmpty()) {
			if (supplierName.length() > 100) {
				document.setCustOrSuppName(supplierName.substring(0, 100));
			}
		}

		String supplierCode = document.getCustOrSuppCode();
		if (supplierCode != null && !supplierCode.isEmpty()) {
			if (supplierCode.length() > 100) {
				document.setCustOrSuppCode(supplierCode.substring(0, 100));
			}
		}

		if (document.getCustOrSuppAddress1() != null
				&& !document.getCustOrSuppAddress1().isEmpty()) {
			if (document.getCustOrSuppAddress1().length() > 120) {
				document.setCustOrSuppAddress1(
						document.getCustOrSuppAddress1().substring(0, 120));
			}
		}

		if (document.getCustOrSuppAddress2() != null
				&& !document.getCustOrSuppAddress2().isEmpty()) {
			if (document.getCustOrSuppAddress2().length() > 120) {
				document.setCustOrSuppAddress2(
						document.getCustOrSuppAddress2().substring(0, 120));
			}
		}
		if (document.getCustOrSuppAddress3() != null
				&& !document.getCustOrSuppAddress3().isEmpty()) {
			if (document.getCustOrSuppAddress3().length() > 60) {
				document.setCustOrSuppAddress3(
						document.getCustOrSuppAddress3().substring(0, 60));
			}
		}
		if (document.getCustOrSuppAddress4() != null
				&& !document.getCustOrSuppAddress4().isEmpty()) {
			if (document.getCustOrSuppAddress4().length() > 10) {
				document.setCustOrSuppAddress4(
						document.getCustOrSuppAddress4().substring(0, 10));
			}
		}

		if (document.getCustOrSuppType() != null
				&& !document.getCustOrSuppType().isEmpty()) {
			if (document.getCustOrSuppType().length() > 1) {
				document.setCustOrSuppType(
						document.getCustOrSuppType().substring(0, 1));
			}
		}

		if (document.getStateApplyingCess() != null
				&& !document.getStateApplyingCess().isEmpty()) {
			if (document.getStateApplyingCess().length() > 2) {
				document.setStateApplyingCess(
						document.getStateApplyingCess().substring(0, 2));
			}
		}

		if (document.getIrn() != null && !document.getIrn().isEmpty()) {
			if (document.getIrn().length() > 64) {
				document.setIrn(document.getIrn().substring(0, 64));
			}
		}

		if (document.getDispatcherGstin() != null
				&& !document.getDispatcherGstin().isEmpty()) {
			if (document.getDispatcherGstin().length() > 15) {
				document.setDispatcherGstin(
						document.getDispatcherGstin().substring(0, 15));
			}
		}

		if (document.getShipToGstin() != null
				&& !document.getShipToGstin().isEmpty()) {
			if (document.getShipToGstin().length() > 15) {
				document.setShipToGstin(
						document.getShipToGstin().substring(0, 15));
			}
		}

		if (document.getEgstin() != null && !document.getEgstin().isEmpty()) {
			if (document.getEgstin().length() > 15) {
				document.setEgstin(document.getEgstin().substring(0, 15));
			}
		}

		if (document.getTcsFlag() != null && !document.getTcsFlag().isEmpty()) {
			if (document.getTcsFlag().length() > 1) {
				document.setTcsFlag(document.getTcsFlag().substring(0, 1));
			}
		}

		if (document.getTdsFlag() != null && !document.getTdsFlag().isEmpty()) {
			if (document.getTdsFlag().length() > 1) {
				document.setTdsFlag(document.getTdsFlag().substring(0, 1));
			}
		}

		if (document.getClaimRefundFlag() != null
				&& !document.getClaimRefundFlag().isEmpty()) {
			if (document.getClaimRefundFlag().length() > 1) {
				document.setClaimRefundFlag(
						document.getClaimRefundFlag().substring(0, 1));
			}
		}

		if (document.getAutoPopToRefundFlag() != null
				&& !document.getAutoPopToRefundFlag().isEmpty()) {
			if (document.getAutoPopToRefundFlag().length() > 1) {
				document.setAutoPopToRefundFlag(
						document.getAutoPopToRefundFlag().substring(0, 1));
			}
		}

		if (document.getCrDrPreGst() != null
				&& !document.getCrDrPreGst().isEmpty()) {
			if (document.getCrDrPreGst().length() > 1) {
				document.setCrDrPreGst(
						document.getCrDrPreGst().substring(0, 1));
			}
		}

		if (document.getPortCode() != null
				&& !document.getPortCode().isEmpty()) {
			if (document.getPortCode().length() > 10) {
				document.setPortCode(document.getPortCode().substring(0, 10));
			}
		}

		if (document.getBillOfEntryNo() != null
				&& !document.getBillOfEntryNo().isEmpty()) {
			if (document.getBillOfEntryNo().length() > 20) {
				document.setBillOfEntryNo(
						document.getBillOfEntryNo().substring(0, 20));
			}
		}

		if (document.getLocation() != null
				&& !document.getLocation().isEmpty()) {
			if (document.getLocation().length() > 100) {
				document.setLocation(document.getLocation().substring(0, 100));
			}
		}

		if (document.getDivision() != null
				&& !document.getDivision().isEmpty()) {
			if (document.getDivision().length() > 100) {
				document.setDivision(document.getDivision().substring(0, 100));
			}
		}

		if (document.getPurchaseOrganization() != null
				&& !document.getPurchaseOrganization().isEmpty()) {
			if (document.getPurchaseOrganization().length() > 100) {
				document.setPurchaseOrganization(
						document.getPurchaseOrganization().substring(0, 100));
			}
		}

		if (document.getProfitCentre() != null
				&& !document.getProfitCentre().isEmpty()) {
			if (document.getProfitCentre().length() > 100) {
				document.setProfitCentre(
						document.getProfitCentre().substring(0, 100));
			}
		}

		if (document.geteWayBillNo() != null
				&& !document.geteWayBillNo().isEmpty()) {
			if (document.geteWayBillNo().length() > 12) {
				document.seteWayBillNo(
						document.geteWayBillNo().substring(0, 12));
			}
		}

	}

	/**
	 * This method truncates Inward Line Item fields
	 * 
	 * @param item
	 */
	public void truncateLineItemFields(InwardTransDocLineItem item) {
		String exchangeRate = item.getExchangeRate();
		if(!Strings.isNullOrEmpty(exchangeRate) && exchangeRate.length()>7){
			item.setExchangeRate(exchangeRate.substring(0, 7));
		}
		
		String glcodeTaxableValue = item.getGlCodeTaxableValue();
		if (glcodeTaxableValue != null && !glcodeTaxableValue.isEmpty()) {
			if (glcodeTaxableValue.length() > 100) {
				item.setGlCodeTaxableValue(
						glcodeTaxableValue.substring(0, 100));
			}
		}

		String glCodeIgst = item.getGlCodeIgst();
		if (glCodeIgst != null && !glCodeIgst.isEmpty()) {
			if (glCodeIgst.length() > 100) {
				item.setGlCodeIgst(glCodeIgst.substring(0, 100));
			}
		}

		String glCodeCgst = item.getGlCodeCgst();
		if (glCodeCgst != null && !glCodeCgst.isEmpty()) {
			if (glCodeCgst.length() > 100) {
				item.setGlCodeCgst(glCodeCgst.substring(0, 100));
			}
		}

		String glCodeSgst = item.getGlCodeSgst();
		if (glCodeSgst != null && !glCodeSgst.isEmpty()) {
			if (glCodeSgst.length() > 100) {
				item.setGlCodeSgst(glCodeSgst.substring(0, 100));
			}
		}

		String glcodeAdvaloremCess = item.getGlCodeAdvCess();
		if (glcodeAdvaloremCess != null && !glcodeAdvaloremCess.isEmpty()) {
			if (glcodeAdvaloremCess.length() > 100) {
				item.setGlCodeAdvCess(glcodeAdvaloremCess.substring(0, 100));
			}
		}

		String glcodeSpecificCess = item.getGlCodeSpCess();
		if (glcodeSpecificCess != null && !glcodeSpecificCess.isEmpty()) {
			if (glcodeSpecificCess.length() > 100) {
				item.setGlCodeSpCess(glcodeSpecificCess.substring(0, 100));
			}
		}

		String glcodeStateCess = item.getGlCodeStateCess();
		if (glcodeStateCess != null && !glcodeStateCess.isEmpty()) {
			if (glcodeStateCess.length() > 100) {
				item.setGlCodeStateCess(glcodeStateCess.substring(0, 100));
			}
		}

		String itemCode = item.getItemCode();
		if (itemCode != null && !itemCode.isEmpty()) {
			if (itemCode.length() > 100) {
				item.setItemCode(itemCode.substring(0, 100));
			}
		}

		String itemDesc = item.getItemDescription();
		if (itemDesc != null && !itemDesc.isEmpty()) {
			if (itemDesc.length() > 300) {
				item.setItemDescription(itemDesc.substring(0, 300));
			}
		}

		String itemCategory = item.getItemCategory();
		if (itemCategory != null && !itemCategory.isEmpty()) {
			if (itemCategory.length() > 100) {
				item.setItemCategory(itemCategory.substring(0, 100));
			}
		}

		String uom = item.getUom();
		if (uom != null && !uom.isEmpty()) {
			if (uom.length() > 100) {
				item.setUom(uom.substring(0, 100));
			}
		}

		String crdrReason = item.getCrDrReason();
		if (crdrReason != null && !crdrReason.isEmpty()) {
			if (crdrReason.length() > 100) {
				item.setCrDrReason(crdrReason.substring(0, 100));
			}
		}

		String purchaseVoucherNum = item.getPurchaseVoucherNum();
		if (purchaseVoucherNum != null && !purchaseVoucherNum.isEmpty()) {
			if (purchaseVoucherNum.length() > 100) {
				item.setPurchaseVoucherNum(
						purchaseVoucherNum.substring(0, 100));
			}
		}

		String paymentVoucherNum = item.getPaymentVoucherNumber();
		if (paymentVoucherNum != null && !paymentVoucherNum.isEmpty()) {
			if (paymentVoucherNum.length() > 100) {
				item.setPaymentVoucherNumber(
						paymentVoucherNum.substring(0, 100));
			}
		}

		String contractNum = item.getContractNumber();
		if (contractNum != null && !contractNum.isEmpty()) {
			if (contractNum.length() > 100) {
				item.setContractNumber(contractNum.substring(0, 100));
			}
		}

		String originalSgstin = item.getOrigSgstin();
		if (originalSgstin != null && !originalSgstin.isEmpty()) {
			if (item.getOrigSgstin().length() > 15) {
				item.setOrigSgstin(originalSgstin.substring(0, 15));
			}
		}
		String userDefField1 = item.getUserdefinedfield1();
		if (userDefField1 != null && !userDefField1.isEmpty()) {
			if (userDefField1.length() > 500) {
				userDefField1 = userDefField1.substring(0, 500);
				item.setUserdefinedfield1(userDefField1);
			}
		}

		String userDefField2 = item.getUserdefinedfield2();
		if (userDefField2 != null && !userDefField2.isEmpty()) {
			if (userDefField2.length() > 500) {
				userDefField2 = userDefField2.substring(0, 500);
				item.setUserdefinedfield2(userDefField2);
			}
		}

		String userDefField3 = item.getUserdefinedfield3();
		if (userDefField3 != null && !userDefField3.isEmpty()) {
			if (userDefField3.length() > 500) {
				userDefField3 = userDefField3.substring(0, 500);
				item.setUserdefinedfield3(userDefField3);
			}
		}

		String userDefField4 = item.getUserDefinedField4();
		if (userDefField4 != null && !userDefField4.isEmpty()) {
			if (userDefField4.length() > 500) {
				userDefField4 = userDefField4.substring(0, 500);
				item.setUserDefinedField4(userDefField4);
			}
		}

		String userDefField5 = item.getUserDefinedField5();
		if (userDefField5 != null && !userDefField5.isEmpty()) {
			if (userDefField5.length() > 500) {
				userDefField5 = userDefField5.substring(0, 500);
				item.setUserDefinedField5(userDefField5);
			}
		}

		String userDefField6 = item.getUserDefinedField6();
		if (userDefField6 != null && !userDefField6.isEmpty()) {
			if (userDefField6.length() > 500) {
				userDefField6 = userDefField6.substring(0, 500);
				item.setUserDefinedField6(userDefField6);
			}
		}

		String userDefField7 = item.getUserDefinedField7();
		if (userDefField7 != null && !userDefField7.isEmpty()) {
			if (userDefField7.length() > 500) {
				userDefField7 = userDefField7.substring(0, 500);
				item.setUserDefinedField7(userDefField7);
			}
		}

		String userDefField8 = item.getUserDefinedField8();
		if (userDefField8 != null && !userDefField8.isEmpty()) {
			if (userDefField8.length() > 500) {
				userDefField8 = userDefField8.substring(0, 500);
				item.setUserDefinedField8(userDefField8);
			}
		}

		String userDefField9 = item.getUserDefinedField9();
		if (userDefField9 != null && !userDefField9.isEmpty()) {
			if (userDefField9.length() > 500) {
				userDefField9 = userDefField9.substring(0, 500);
				item.setUserDefinedField9(userDefField9);
			}
		}

		String userDefField10 = item.getUserDefinedField10();
		if (userDefField10 != null && !userDefField10.isEmpty()) {
			if (userDefField10.length() > 500) {
				userDefField10 = userDefField10.substring(0, 500);
				item.setUserDefinedField10(userDefField10);
			}
		}

		String userDefField11 = item.getUserDefinedField11();
		if (userDefField11 != null && !userDefField11.isEmpty()) {
			if (userDefField11.length() > 500) {
				userDefField11 = userDefField11.substring(0, 500);
				item.setUserDefinedField11(userDefField11);
			}
		}

		String userDefField12 = item.getUserDefinedField12();
		if (userDefField12 != null && !userDefField12.isEmpty()) {
			if (userDefField12.length() > 500) {
				userDefField12 = userDefField12.substring(0, 500);
				item.setUserDefinedField12(userDefField12);
			}
		}

		String userDefField13 = item.getUserDefinedField13();
		if (userDefField13 != null && !userDefField13.isEmpty()) {
			if (userDefField13.length() > 500) {
				userDefField13 = userDefField13.substring(0, 500);
				item.setUserDefinedField13(userDefField13);
			}
		}

		String userDefField14 = item.getUserDefinedField14();
		if (userDefField14 != null && !userDefField14.isEmpty()) {
			if (userDefField14.length() > 500) {
				userDefField14 = userDefField14.substring(0, 500);
				item.setUserDefinedField14(userDefField14);
			}
		}

		String userDefField15 = item.getUserDefinedField15();
		if (userDefField15 != null && !userDefField15.isEmpty()) {
			if (userDefField15.length() > 500) {
				userDefField15 = userDefField15.substring(0, 500);
				item.setUserDefinedField15(userDefField15);
			}
		}

		if (item.getTaxScheme() != null && !item.getTaxScheme().isEmpty()) {
			if (item.getTaxScheme().length() > 10) {
				item.setTaxScheme(item.getTaxScheme().substring(0, 10));
			}
		}

		if (item.getDocCategory() != null && !item.getDocCategory().isEmpty()) {
			if (item.getDocCategory().length() > 4) {
				item.setDocCategory(item.getDocCategory().substring(0, 4));
			}
		}

		if (item.getSupplierTradeName() != null
				&& !item.getSupplierTradeName().isEmpty()) {
			if (item.getSupplierTradeName().length() > 100) {
				item.setSupplierTradeName(
						item.getSupplierTradeName().substring(0, 100));
			}
		}

		if (item.getSupplierPhone() != null
				&& !item.getSupplierPhone().isEmpty()) {
			if (item.getSupplierPhone().length() > 12) {
				item.setSupplierPhone(item.getSupplierPhone().substring(0, 12));
			}
		}

		if (item.getSupplierEmail() != null
				&& !item.getSupplierEmail().isEmpty()) {
			if (item.getSupplierEmail().length() > 100) {
				item.setSupplierEmail(
						item.getSupplierEmail().substring(0, 100));
			}
		}

		if (item.getCustomerTradeName() != null
				&& !item.getCustomerTradeName().isEmpty()) {
			if (item.getCustomerTradeName().length() > 100) {
				item.setCustomerTradeName(
						item.getCustomerTradeName().substring(0, 100));
			}
		}

		if (item.getCustomerLegalName() != null
				&& !item.getCustomerLegalName().isEmpty()) {
			if (item.getCustomerLegalName().length() > 100) {
				item.setCustomerLegalName(
						item.getCustomerLegalName().substring(0, 100));
			}
		}

		if (item.getCustomerBuildingNumber() != null
				&& !item.getCustomerBuildingNumber().isEmpty()) {
			if (item.getCustomerBuildingNumber().length() > 120) {
				item.setCustomerBuildingNumber(
						item.getCustomerBuildingNumber().substring(0, 120));
			}
		}

		if (item.getCustomerBuildingName() != null
				&& !item.getCustomerBuildingName().isEmpty()) {
			if (item.getCustomerBuildingName().length() > 120) {
				item.setCustomerBuildingName(
						item.getCustomerBuildingName().substring(0, 120));
			}
		}

		if (item.getCustomerLocation() != null
				&& !item.getCustomerLocation().isEmpty()) {
			if (item.getCustomerLocation().length() > 100) {
				item.setCustomerLocation(
						item.getCustomerLocation().substring(0, 100));
			}
		}

		if (item.getCustomerPincode() != null
				&& !item.getCustomerPincode().isEmpty()) {
			if (item.getCustomerPincode().length() > 10) {
				item.setCustomerPincode(
						item.getCustomerPincode().substring(0, 10));
			}
		}

		if (item.getBillToState() != null && !item.getBillToState().isEmpty()) {
			if (item.getBillToState().length() > 2) {
				item.setBillToState(item.getBillToState().substring(0, 2));
			}
		}

		if (item.getCustomerPhone() != null
				&& !item.getCustomerPhone().isEmpty()) {
			if (item.getCustomerPhone().length() > 12) {
				item.setCustomerPhone(item.getCustomerPhone().substring(0, 12));
			}
		}

		if (item.getCustomerEmail() != null
				&& !item.getCustomerEmail().isEmpty()) {
			if (item.getCustomerEmail().length() > 100) {
				item.setCustomerEmail(
						item.getCustomerEmail().substring(0, 100));
			}
		}

		if (item.getDispatcherTradeName() != null
				&& !item.getDispatcherTradeName().isEmpty()) {
			if (item.getDispatcherTradeName().length() > 100) {
				item.setDispatcherTradeName(
						item.getDispatcherTradeName().substring(0, 100));
			}
		}

		if (item.getDispatcherBuildingNumber() != null
				&& !item.getDispatcherBuildingNumber().isEmpty()) {
			if (item.getDispatcherBuildingNumber().length() > 120) {
				item.setDispatcherBuildingNumber(
						item.getDispatcherBuildingNumber().substring(0, 120));
			}
		}

		if (item.getDispatcherBuildingName() != null
				&& !item.getDispatcherBuildingName().isEmpty()) {
			if (item.getDispatcherBuildingName().length() > 120) {
				item.setDispatcherBuildingName(
						item.getDispatcherBuildingName().substring(0, 120));
			}
		}

		if (item.getDispatcherLocation() != null
				&& !item.getDispatcherLocation().isEmpty()) {
			if (item.getDispatcherLocation().length() > 100) {
				item.setDispatcherLocation(
						item.getDispatcherLocation().substring(0, 100));
			}
		}

		if (item.getDispatcherPincode() != null
				&& !item.getDispatcherPincode().isEmpty()) {
			if (item.getDispatcherPincode().length() > 10) {
				item.setDispatcherPincode(
						item.getDispatcherPincode().substring(0, 10));
			}
		}

		if (item.getDispatcherStateCode() != null
				&& !item.getDispatcherStateCode().isEmpty()) {
			if (item.getDispatcherStateCode().length() > 2) {
				item.setDispatcherStateCode(
						item.getDispatcherStateCode().substring(0, 2));
			}
		}

		if (item.getShipToTradeName() != null
				&& !item.getShipToTradeName().isEmpty()) {
			if (item.getShipToTradeName().length() > 100) {
				item.setShipToTradeName(
						item.getShipToTradeName().substring(0, 100));
			}
		}

		if (item.getShipToLegalName() != null
				&& !item.getShipToLegalName().isEmpty()) {
			if (item.getShipToLegalName().length() > 100) {
				item.setShipToLegalName(
						item.getShipToLegalName().substring(0, 100));
			}
		}

		if (item.getShipToBuildingNumber() != null
				&& !item.getShipToBuildingNumber().isEmpty()) {
			if (item.getShipToBuildingNumber().length() > 120) {
				item.setShipToBuildingNumber(
						item.getShipToBuildingNumber().substring(0, 120));
			}
		}

		if (item.getShipToBuildingName() != null
				&& !item.getShipToBuildingName().isEmpty()) {
			if (item.getShipToBuildingName().length() > 120) {
				item.setShipToBuildingName(
						item.getShipToBuildingName().substring(0, 120));
			}
		}

		if (item.getShipToLocation() != null
				&& !item.getShipToLocation().isEmpty()) {
			if (item.getShipToLocation().length() > 100) {
				item.setShipToLocation(
						item.getShipToLocation().substring(0, 100));
			}
		}

		if (item.getShipToPincode() != null
				&& !item.getShipToPincode().isEmpty()) {
			if (item.getShipToPincode().length() > 10) {
				item.setShipToPincode(item.getShipToPincode().substring(0, 10));
			}
		}

		if (item.getShipToState() != null && !item.getShipToState().isEmpty()) {
			if (item.getShipToState().length() > 2) {
				item.setShipToState(item.getShipToState().substring(0, 2));
			}
		}

		if (item.getSerialNumberII() != null
				&& !item.getSerialNumberII().isEmpty()) {
			if (item.getSerialNumberII().length() > 20) {
				item.setSerialNumberII(
						item.getSerialNumberII().substring(0, 20));
			}
		}

		if (item.getProductName() != null && !item.getProductName().isEmpty()) {
			if (item.getProductName().length() > 100) {
				item.setProductName(item.getProductName().substring(0, 100));
			}
		}

		if (item.getIsService() != null && !item.getIsService().isEmpty()) {
			if (item.getIsService().length() > 1) {
				item.setIsService(item.getIsService().substring(0, 1));
			}
		}

		if (item.getBarcode() != null && !item.getBarcode().isEmpty()) {
			if (item.getBarcode().length() > 30) {
				item.setBarcode(item.getBarcode().substring(0, 30));
			}
		}

		if (item.getBatchNameOrNumber() != null
				&& !item.getBatchNameOrNumber().isEmpty()) {
			if (item.getBatchNameOrNumber().length() > 20) {
				item.setBatchNameOrNumber(
						item.getBatchNameOrNumber().substring(0, 20));
			}
		}

		if (item.getOrderLineReference() != null
				&& !item.getOrderLineReference().isEmpty()) {
			if (item.getOrderLineReference().length() > 50) {
				item.setOrderLineReference(
						item.getOrderLineReference().substring(0, 50));
			}
		}

		if (item.getAttributeName() != null
				&& !item.getAttributeName().isEmpty()) {
			if (item.getAttributeName().length() > 100) {
				item.setAttributeName(
						item.getAttributeName().substring(0, 100));
			}
		}

		if (item.getAttributeValue() != null
				&& !item.getAttributeValue().isEmpty()) {
			if (item.getAttributeValue().length() > 100) {
				item.setAttributeValue(
						item.getAttributeValue().substring(0, 100));
			}
		}

		if (item.getOriginCountry() != null
				&& !item.getOriginCountry().isEmpty()) {
			if (item.getOriginCountry().length() > 2) {
				item.setOriginCountry(item.getOriginCountry().substring(0, 2));
			}
		}

		if (item.getTotalInvoiceValueInWords() != null
				&& !item.getTotalInvoiceValueInWords().isEmpty()) {
			if (item.getTotalInvoiceValueInWords().length() > 100) {
				item.setTotalInvoiceValueInWords(
						item.getTotalInvoiceValueInWords().substring(0, 100));
			}
		}

		if (item.getTcsFlagIncomeTax() != null
				&& !item.getTcsFlagIncomeTax().isEmpty()) {
			if (item.getTcsFlagIncomeTax().length() > 1) {
				item.setTcsFlagIncomeTax(
						item.getTcsFlagIncomeTax().substring(0, 1));
			}
		}

		if (item.getForeignCurrency() != null
				&& !item.getForeignCurrency().isEmpty()) {
			if (item.getForeignCurrency().length() > 16) {
				item.setForeignCurrency(
						item.getForeignCurrency().substring(0, 16));
			}
		}

		if (item.getCountryCode() != null && !item.getCountryCode().isEmpty()) {
			if (item.getCountryCode().length() > 2) {
				item.setCountryCode(item.getCountryCode().substring(0, 2));
			}
		}

		if (item.getInvoiceRemarks() != null
				&& !item.getInvoiceRemarks().isEmpty()) {
			if (item.getInvoiceRemarks().length() > 100) {
				item.setInvoiceRemarks(
						item.getInvoiceRemarks().substring(0, 100));
			}
		}

		if (item.getInvoiceReference() != null
				&& !item.getInvoiceReference().isEmpty()) {
			if (item.getInvoiceReference().length() > 20) {
				item.setInvoiceReference(
						item.getInvoiceReference().substring(0, 20));
			}
		}

		if (item.getReceiptAdviceReference() != null
				&& !item.getReceiptAdviceReference().isEmpty()) {
			if (item.getReceiptAdviceReference().length() > 20) {
				item.setReceiptAdviceReference(
						item.getReceiptAdviceReference().substring(0, 20));
			}
		}

		if (item.getTenderReference() != null
				&& !item.getTenderReference().isEmpty()) {
			if (item.getTenderReference().length() > 20) {
				item.setTenderReference(
						item.getTenderReference().substring(0, 20));
			}
		}

		if (item.getContractReference() != null
				&& !item.getContractReference().isEmpty()) {
			if (item.getContractReference().length() > 20) {
				item.setContractReference(
						item.getContractReference().substring(0, 20));
			}
		}

		if (item.getExternalReference() != null
				&& !item.getExternalReference().isEmpty()) {
			if (item.getExternalReference().length() > 20) {
				item.setExternalReference(
						item.getExternalReference().substring(0, 20));
			}
		}

		if (item.getProjectReference() != null
				&& !item.getProjectReference().isEmpty()) {
			if (item.getProjectReference().length() > 20) {
				item.setProjectReference(
						item.getProjectReference().substring(0, 20));
			}
		}

		if (item.getPayeeName() != null && !item.getPayeeName().isEmpty()) {
			if (item.getPayeeName().length() > 100) {
				item.setPayeeName(item.getPayeeName().substring(0, 100));
			}
		}

		if (item.getModeOfPayment() != null
				&& !item.getModeOfPayment().isEmpty()) {
			if (item.getModeOfPayment().length() > 18) {
				item.setModeOfPayment(item.getModeOfPayment().substring(0, 18));
			}
		}

		if (item.getBranchOrIfscCode() != null
				&& !item.getBranchOrIfscCode().isEmpty()) {
			if (item.getBranchOrIfscCode().length() > 11) {
				item.setBranchOrIfscCode(
						item.getBranchOrIfscCode().substring(0, 11));
			}
		}

		if (item.getPaymentTerms() != null
				&& !item.getPaymentTerms().isEmpty()) {
			if (item.getPaymentTerms().length() > 100) {
				item.setPaymentTerms(item.getPaymentTerms().substring(0, 100));
			}
		}

		if (item.getPaymentInstruction() != null
				&& !item.getPaymentInstruction().isEmpty()) {
			if (item.getPaymentInstruction().length() > 100) {
				item.setPaymentInstruction(
						item.getPaymentInstruction().substring(0, 100));
			}
		}

		if (item.getCreditTransfer() != null
				&& !item.getCreditTransfer().isEmpty()) {
			if (item.getCreditTransfer().length() > 100) {
				item.setCreditTransfer(
						item.getCreditTransfer().substring(0, 100));
			}
		}

		if (item.getDirectDebit() != null && !item.getDirectDebit().isEmpty()) {
			if (item.getDirectDebit().length() > 100) {
				item.setDirectDebit(item.getDirectDebit().substring(0, 100));
			}
		}

		if (item.getAccountDetail() != null
				&& !item.getAccountDetail().isEmpty()) {
			if (item.getAccountDetail().length() > 18) {
				item.setAccountDetail(item.getAccountDetail().substring(0, 18));
			}
		}

		if (item.getSupportingDocURL() != null
				&& !item.getSupportingDocURL().isEmpty()) {
			if (item.getSupportingDocURL().length() > 100) {
				item.setSupportingDocURL(
						item.getSupportingDocURL().substring(0, 100));
			}
		}

		if (item.getSupportingDoc() != null
				&& !item.getSupportingDoc().isEmpty()) {
			if (item.getSupportingDoc().length() > 1000) {
				item.setSupportingDoc(
						item.getSupportingDoc().substring(0, 1000));
			}
		}

		if (item.getAdditionalInformation() != null
				&& !item.getAdditionalInformation().isEmpty()) {
			if (item.getAdditionalInformation().length() > 100) {
				item.setAdditionalInformation(
						item.getAdditionalInformation().substring(0, 100));
			}
		}

		if (item.getTransactionType() != null
				&& !item.getTransactionType().isEmpty()) {
			if (item.getTransactionType().length() > 1) {
				item.setTransactionType(
						item.getTransactionType().substring(0, 1));
			}
		}

		if (item.getSubSupplyType() != null
				&& !item.getSubSupplyType().isEmpty()) {
			if (item.getSubSupplyType().length() > 10) {
				item.setSubSupplyType(item.getSubSupplyType().substring(0, 10));
			}
		}

		if (item.getOtherSupplyTypeDescription() != null
				&& !item.getOtherSupplyTypeDescription().isEmpty()) {
			if (item.getOtherSupplyTypeDescription().length() > 20) {
				item.setOtherSupplyTypeDescription(
						item.getOtherSupplyTypeDescription().substring(0, 20));
			}
		}

		if (item.getTransporterID() != null
				&& !item.getTransporterID().isEmpty()) {
			if (item.getTransporterID().length() > 15) {
				item.setTransporterID(item.getTransporterID().substring(0, 15));
			}
		}

		if (item.getTransporterName() != null
				&& !item.getTransporterName().isEmpty()) {
			if (item.getTransporterName().length() > 100) {
				item.setTransporterName(
						item.getTransporterName().substring(0, 100));
			}
		}

		if (item.getTransportMode() != null
				&& !item.getTransportMode().isEmpty()) {
			if (item.getTransportMode().length() > 10) {
				item.setTransportMode(item.getTransportMode().substring(0, 10));
			}
		}

		if (item.getTransportDocNo() != null
				&& !item.getTransportDocNo().isEmpty()) {
			if (item.getTransportDocNo().length() > 15) {
				item.setTransportDocNo(
						item.getTransportDocNo().substring(0, 15));
			}
		}

		if (item.getVehicleNo() != null && !item.getVehicleNo().isEmpty()) {
			if (item.getVehicleNo().length() > 20) {
				item.setVehicleNo(item.getVehicleNo().substring(0, 20));
			}
		}

		if (item.getVehicleType() != null && !item.getVehicleType().isEmpty()) {
			if (item.getVehicleType().length() > 1) {
				item.setVehicleType(item.getVehicleType().substring(0, 1));
			}
		}

		if (item.getOrigDocType() != null && !item.getOrigDocType().isEmpty()) {
			if (item.getOrigDocType().length() > 5) {
				item.setOrigDocType(item.getOrigDocType().substring(0, 5));
			}
		}

		if (item.getCompanyCode() != null && !item.getCompanyCode().isEmpty()) {
			if (item.getCompanyCode().length() > 100) {
				item.setCompanyCode(item.getCompanyCode().substring(0, 100));
			}
		}

		if (item.getSourceIdentifier() != null
				&& !item.getSourceIdentifier().isEmpty()) {
			if (item.getSourceIdentifier().length() > 100) {
				item.setSourceIdentifier(
						item.getSourceIdentifier().substring(0, 100));
			}
		}

		if (item.getSubDivision() != null && !item.getSubDivision().isEmpty()) {
			if (item.getSubDivision().length() > 100) {
				item.setSubDivision(item.getSubDivision().substring(0, 100));
			}
		}

		if (item.getProfitCentre2() != null
				&& !item.getProfitCentre2().isEmpty()) {
			if (item.getProfitCentre2().length() > 100) {
				item.setProfitCentre2(
						item.getProfitCentre2().substring(0, 100));
			}
		}

		if (item.getGlStateCessSpecific() != null
				&& !item.getGlStateCessSpecific().isEmpty()) {
			if (item.getGlStateCessSpecific().length() > 100) {
				item.setGlStateCessSpecific(
						item.getGlStateCessSpecific().substring(0, 100));
			}
		}

		if (item.getDocReferenceNumber() != null
				&& !item.getDocReferenceNumber().isEmpty()) {
			if (item.getDocReferenceNumber().length() > 300) {
				item.setDocReferenceNumber(
						item.getDocReferenceNumber().substring(0, 300));
			}
		}

		if (item.getUserDefinedField16() != null
				&& !item.getUserDefinedField16().isEmpty()) {
			if (item.getUserDefinedField16().length() > 500) {
				item.setUserDefinedField16(
						item.getUserDefinedField16().substring(0, 500));
			}
		}

		if (item.getUserDefinedField17() != null
				&& !item.getUserDefinedField17().isEmpty()) {
			if (item.getUserDefinedField17().length() > 500) {
				item.setUserDefinedField17(
						item.getUserDefinedField17().substring(0, 500));
			}
		}

		if (item.getUserDefinedField18() != null
				&& !item.getUserDefinedField18().isEmpty()) {
			if (item.getUserDefinedField18().length() > 500) {
				item.setUserDefinedField18(
						item.getUserDefinedField18().substring(0, 500));
			}
		}

		if (item.getUserDefinedField19() != null
				&& !item.getUserDefinedField19().isEmpty()) {
			if (item.getUserDefinedField19().length() > 500) {
				item.setUserDefinedField19(
						item.getUserDefinedField19().substring(0, 500));
			}
		}

		if (item.getUserDefinedField20() != null
				&& !item.getUserDefinedField20().isEmpty()) {
			if (item.getUserDefinedField20().length() > 500) {
				item.setUserDefinedField20(
						item.getUserDefinedField20().substring(0, 500));
			}
		}

		if (item.getUserDefinedField21() != null
				&& !item.getUserDefinedField21().isEmpty()) {
			if (item.getUserDefinedField21().length() > 500) {
				item.setUserDefinedField21(
						item.getUserDefinedField21().substring(0, 500));
			}
		}

		if (item.getUserDefinedField22() != null
				&& !item.getUserDefinedField22().isEmpty()) {
			if (item.getUserDefinedField22().length() > 500) {
				item.setUserDefinedField22(
						item.getUserDefinedField22().substring(0, 500));
			}
		}

		if (item.getUserDefinedField23() != null
				&& !item.getUserDefinedField23().isEmpty()) {
			if (item.getUserDefinedField23().length() > 500) {
				item.setUserDefinedField23(
						item.getUserDefinedField23().substring(0, 500));
			}
		}

		if (item.getUserDefinedField24() != null
				&& !item.getUserDefinedField24().isEmpty()) {
			if (item.getUserDefinedField24().length() > 500) {
				item.setUserDefinedField24(
						item.getUserDefinedField24().substring(0, 500));
			}
		}

		if (item.getUserDefinedField25() != null
				&& !item.getUserDefinedField25().isEmpty()) {
			if (item.getUserDefinedField25().length() > 500) {
				item.setUserDefinedField25(
						item.getUserDefinedField25().substring(0, 500));
			}
		}

		if (item.getUserDefinedField26() != null
				&& !item.getUserDefinedField26().isEmpty()) {
			if (item.getUserDefinedField26().length() > 500) {
				item.setUserDefinedField26(
						item.getUserDefinedField26().substring(0, 500));
			}
		}

		if (item.getUserDefinedField27() != null
				&& !item.getUserDefinedField27().isEmpty()) {
			if (item.getUserDefinedField27().length() > 500) {
				item.setUserDefinedField27(
						item.getUserDefinedField27().substring(0, 500));
			}
		}

		if (item.getUserDefinedField29() != null
				&& !item.getUserDefinedField29().isEmpty()) {
			if (item.getUserDefinedField29().length() > 500) {
				item.setUserDefinedField29(
						item.getUserDefinedField29().substring(0, 500));
			}
		}

		if (item.getUserDefinedField30() != null
				&& !item.getUserDefinedField30().isEmpty()) {
			if (item.getUserDefinedField30().length() > 500) {
				item.setUserDefinedField30(
						item.getUserDefinedField30().substring(0, 500));
			}
		}

		if (item.getIrn() != null && !item.getIrn().isEmpty()) {
			if (item.getIrn().length() > 64) {
				item.setIrn(item.getIrn().substring(0, 64));
			}
		}

		if (item.getDispatcherGstin() != null
				&& !item.getDispatcherGstin().isEmpty()) {
			if (item.getDispatcherGstin().length() > 15) {
				item.setDispatcherGstin(
						item.getDispatcherGstin().substring(0, 15));
			}
		}

		if (item.getShipToGstin() != null && !item.getShipToGstin().isEmpty()) {
			if (item.getShipToGstin().length() > 15) {
				item.setShipToGstin(item.getShipToGstin().substring(0, 15));
			}
		}

		if (item.getEgstin() != null && !item.getEgstin().isEmpty()) {
			if (item.getEgstin().length() > 15) {
				item.setEgstin(item.getEgstin().substring(0, 15));
			}
		}

		if (item.getTcsFlag() != null && !item.getTcsFlag().isEmpty()) {
			if (item.getTcsFlag().length() > 1) {
				item.setTcsFlag(item.getTcsFlag().substring(0, 1));
			}
		}

		if (item.getTdsFlag() != null && !item.getTdsFlag().isEmpty()) {
			if (item.getTdsFlag().length() > 1) {
				item.setTdsFlag(item.getTdsFlag().substring(0, 1));
			}
		}

		String supplierName = item.getCustOrSuppName();
		if (supplierName != null && !supplierName.isEmpty()) {
			if (supplierName.length() > 100) {
				item.setCustOrSuppName(supplierName.substring(0, 100));
			}
		}

		String supplierCode = item.getCustOrSuppCode();
		if (supplierCode != null && !supplierCode.isEmpty()) {
			if (supplierCode.length() > 100) {
				item.setCustOrSuppCode(supplierCode.substring(0, 100));
			}
		}

		if (item.getCustOrSuppAddress1() != null
				&& !item.getCustOrSuppAddress1().isEmpty()) {
			if (item.getCustOrSuppAddress1().length() > 120) {
				item.setCustOrSuppAddress1(
						item.getCustOrSuppAddress1().substring(0, 120));
			}
		}

		if (item.getCustOrSuppAddress2() != null
				&& !item.getCustOrSuppAddress2().isEmpty()) {
			if (item.getCustOrSuppAddress2().length() > 120) {
				item.setCustOrSuppAddress2(
						item.getCustOrSuppAddress2().substring(0, 120));
			}
		}
		if (item.getCustOrSuppAddress3() != null
				&& !item.getCustOrSuppAddress3().isEmpty()) {
			if (item.getCustOrSuppAddress3().length() > 60) {
				item.setCustOrSuppAddress3(
						item.getCustOrSuppAddress3().substring(0, 60));
			}
		}
		if (item.getCustOrSuppAddress4() != null
				&& !item.getCustOrSuppAddress4().isEmpty()) {
			if (item.getCustOrSuppAddress4().length() > 10) {
				item.setCustOrSuppAddress4(
						item.getCustOrSuppAddress4().substring(0, 10));
			}
		}

		if (item.getCustOrSuppType() != null
				&& !item.getCustOrSuppType().isEmpty()) {
			if (item.getCustOrSuppType().length() > 1) {
				item.setCustOrSuppType(
						item.getCustOrSuppType().substring(0, 1));
			}
		}
		if (item.getClaimRefundFlag() != null
				&& !item.getClaimRefundFlag().isEmpty()) {
			if (item.getClaimRefundFlag().length() > 1) {
				item.setClaimRefundFlag(
						item.getClaimRefundFlag().substring(0, 1));
			}
		}
		if (item.getAutoPopToRefundFlag() != null
				&& !item.getAutoPopToRefundFlag().isEmpty()) {
			if (item.getAutoPopToRefundFlag().length() > 1) {
				item.setAutoPopToRefundFlag(
						item.getAutoPopToRefundFlag().substring(0, 1));
			}
		}
		if (item.getCrDrPreGst() != null && !item.getCrDrPreGst().isEmpty()) {
			if (item.getCrDrPreGst().length() > 1) {
				item.setCrDrPreGst(item.getCrDrPreGst().substring(0, 1));
			}
		}

		if (item.getPortCode() != null && !item.getPortCode().isEmpty()) {
			if (item.getPortCode().length() > 10) {
				item.setPortCode(item.getPortCode().substring(0, 10));
			}
		}

		if (item.getBillOfEntryNo() != null
				&& !item.getBillOfEntryNo().isEmpty()) {
			if (item.getBillOfEntryNo().length() > 20) {
				item.setBillOfEntryNo(item.getBillOfEntryNo().substring(0, 20));
			}
		}

		if (item.getLocation() != null && !item.getLocation().isEmpty()) {
			if (item.getLocation().length() > 100) {
				item.setLocation(item.getLocation().substring(0, 100));
			}
		}

		if (item.getDivision() != null && !item.getDivision().isEmpty()) {
			if (item.getDivision().length() > 100) {
				item.setDivision(item.getDivision().substring(0, 100));
			}
		}

		if (item.getPurchaseOrganization() != null
				&& !item.getPurchaseOrganization().isEmpty()) {
			if (item.getPurchaseOrganization().length() > 100) {
				item.setPurchaseOrganization(
						item.getPurchaseOrganization().substring(0, 100));
			}
		}

		if (item.getProfitCentre() != null
				&& !item.getProfitCentre().isEmpty()) {
			if (item.getProfitCentre().length() > 100) {
				item.setProfitCentre(item.getProfitCentre().substring(0, 100));
			}
		}

		if (item.geteWayBillNo() != null && !item.geteWayBillNo().isEmpty()) {
			if (item.geteWayBillNo().length() > 12) {
				item.seteWayBillNo(item.geteWayBillNo().substring(0, 12));
			}
		}
	}
}
