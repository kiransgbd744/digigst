package com.ey.advisory.app.services.docs.gstr2;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.google.common.base.Strings;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Component("InwardFieldTruncation")
public class InwardFieldTruncation {

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
			if (document.getCustOrSuppAddress1().length() > 100) {
				document.setCustOrSuppAddress1(
						document.getCustOrSuppAddress1().substring(0, 100));
			}
		}

		if (document.getCustOrSuppAddress2() != null
				&& !document.getCustOrSuppAddress2().isEmpty()) {
			if (document.getCustOrSuppAddress2().length() > 100) {
				document.setCustOrSuppAddress2(
						document.getCustOrSuppAddress2().substring(0, 100));
			}
		}
		if (document.getCustOrSuppAddress3() != null
				&& !document.getCustOrSuppAddress3().isEmpty()) {
			if (document.getCustOrSuppAddress3().length() > 100) {
				document.setCustOrSuppAddress3(
						document.getCustOrSuppAddress3().substring(0, 100));
			}
		}
		if (document.getCustOrSuppAddress4() != null
				&& !document.getCustOrSuppAddress4().isEmpty()) {
			if (document.getCustOrSuppAddress4().length() > 100) {
				document.setCustOrSuppAddress4(
						document.getCustOrSuppAddress4().substring(0, 100));
			}
		}

		if (document.getStateApplyingCess() != null
				&& !document.getStateApplyingCess().isEmpty()) {
			if (document.getStateApplyingCess().length() > 2) {
				document.setStateApplyingCess(
						document.getStateApplyingCess().substring(0, 2));
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
			if (itemDesc.length() > 100) {
				item.setItemDescription(itemDesc.substring(0, 100));
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

		String userDefField1 = item.getUserdefinedfield1();
		if (userDefField1 != null && !userDefField1.isEmpty()) {
			if (userDefField1.length() > 100) {
				userDefField1 = userDefField1.substring(0, 100);
				item.setUserdefinedfield1(userDefField1);
			}
		}

		String userDefField2 = item.getUserdefinedfield2();
		if (userDefField2 != null && !userDefField2.isEmpty()) {
			if (userDefField2.length() > 100) {
				userDefField2 = userDefField2.substring(0, 100);
				item.setUserdefinedfield2(userDefField2);
			}
		}

		String userDefField3 = item.getUserdefinedfield3();
		if (userDefField3 != null && !userDefField3.isEmpty()) {
			if (userDefField3.length() > 100) {
				userDefField3 = userDefField3.substring(0, 100);
				item.setUserdefinedfield3(userDefField3);
			}
		}

		String userDefField4 = item.getUserDefinedField4();
		if (userDefField4 != null && !userDefField4.isEmpty()) {
			if (userDefField4.length() > 100) {
				userDefField4 = userDefField4.substring(0, 100);
				item.setUserDefinedField4(userDefField4);
			}
		}

		String userDefField5 = item.getUserDefinedField5();
		if (userDefField5 != null && !userDefField5.isEmpty()) {
			if (userDefField5.length() > 100) {
				userDefField5 = userDefField5.substring(0, 100);
				item.setUserDefinedField5(userDefField5);
			}
		}

		String userDefField6 = item.getUserDefinedField6();
		if (userDefField6 != null && !userDefField6.isEmpty()) {
			if (userDefField6.length() > 100) {
				userDefField6 = userDefField6.substring(0, 100);
				item.setUserDefinedField6(userDefField6);
			}
		}

		String userDefField7 = item.getUserDefinedField7();
		if (userDefField7 != null && !userDefField7.isEmpty()) {
			if (userDefField7.length() > 100) {
				userDefField7 = userDefField7.substring(0, 100);
				item.setUserDefinedField7(userDefField7);
			}
		}

		String userDefField8 = item.getUserDefinedField8();
		if (userDefField8 != null && !userDefField8.isEmpty()) {
			if (userDefField8.length() > 100) {
				userDefField8 = userDefField8.substring(0, 100);
				item.setUserDefinedField8(userDefField8);
			}
		}

		String userDefField9 = item.getUserDefinedField9();
		if (userDefField9 != null && !userDefField9.isEmpty()) {
			if (userDefField9.length() > 100) {
				userDefField9 = userDefField9.substring(0, 100);
				item.setUserDefinedField9(userDefField9);
			}
		}

		String userDefField10 = item.getUserDefinedField10();
		if (userDefField10 != null && !userDefField10.isEmpty()) {
			if (userDefField10.length() > 100) {
				userDefField10 = userDefField10.substring(0, 100);
				item.setUserDefinedField10(userDefField10);
			}
		}

		String userDefField11 = item.getUserDefinedField11();
		if (userDefField11 != null && !userDefField11.isEmpty()) {
			if (userDefField11.length() > 100) {
				userDefField11 = userDefField11.substring(0, 100);
				item.setUserDefinedField11(userDefField11);
			}
		}

		String userDefField12 = item.getUserDefinedField12();
		if (userDefField12 != null && !userDefField12.isEmpty()) {
			if (userDefField12.length() > 100) {
				userDefField12 = userDefField12.substring(0, 100);
				item.setUserDefinedField12(userDefField12);
			}
		}

		String userDefField13 = item.getUserDefinedField13();
		if (userDefField13 != null && !userDefField13.isEmpty()) {
			if (userDefField13.length() > 100) {
				userDefField13 = userDefField13.substring(0, 100);
				item.setUserDefinedField13(userDefField13);
			}
		}

		String userDefField14 = item.getUserDefinedField14();
		if (userDefField14 != null && !userDefField14.isEmpty()) {
			if (userDefField14.length() > 100) {
				userDefField14 = userDefField14.substring(0, 100);
				item.setUserDefinedField14(userDefField14);
			}
		}

		String userDefField15 = item.getUserDefinedField15();
		if (userDefField15 != null && !userDefField15.isEmpty()) {
			if (userDefField15.length() > 100) {
				userDefField15 = userDefField15.substring(0, 100);
				item.setUserDefinedField15(userDefField15);
			}
		}
		
		String sourceFileName = item.getSourceFileName();
		if (sourceFileName != null && !sourceFileName.isEmpty()) {
			if (sourceFileName.length() > 100) {
				item.setSourceFileName(sourceFileName.substring(0, 100));
			}
		}
		String origSgstin = item.getOrigSgstin();
		if (origSgstin != null && !origSgstin.isEmpty()) {
			if (origSgstin.length() > 15) {
				item.setOrigSgstin(origSgstin.substring(0, 15));
			}
		}

		String custName = item.getCustOrSuppName();
		if (custName != null && !custName.isEmpty()) {
			if (custName.length() > 100) {
				item.setCustOrSuppName(custName.substring(0, 100));
			}
		}
		String custCode = item.getCustOrSuppCode();
		if (custCode != null && !custCode.isEmpty()) {
			if (custCode.length() > 100) {
				item.setCustOrSuppCode(custCode.substring(0, 100));
			}
		}

		String custAddress1 = item.getCustOrSuppAddress1();
		if (custAddress1 != null && !custAddress1.isEmpty()) {
			if (custAddress1.length() > 100) {
				item.setCustOrSuppAddress1(custAddress1.substring(0, 100));
			}
		}

		String custAddress2 = item.getCustOrSuppAddress2();
		if (custAddress2 != null && !custAddress2.isEmpty()) {
			if (custAddress2.length() > 100) {
				item.setCustOrSuppAddress2(custAddress2.substring(0, 100));
			}
		}

		String custAddress3 = item.getCustOrSuppAddress3();
		if (custAddress3 != null && !custAddress3.isEmpty()) {
			if (custAddress3.length() > 100) {
				item.setCustOrSuppAddress3(custAddress3.substring(0, 100));
			}
		}

		String custAddress4 = item.getCustOrSuppAddress4();
		if (custAddress4 != null && !custAddress4.isEmpty()) {
			if (custAddress4.length() > 100) {
				item.setCustOrSuppAddress4(custAddress4.substring(0, 100));
			}
		}
	}
}
