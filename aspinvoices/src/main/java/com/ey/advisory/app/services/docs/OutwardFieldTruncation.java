package com.ey.advisory.app.services.docs;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;

/**
 * This class is responsible for Truncating Outward Fields
 * 
 * @author Mohana.Dasari
 *
 */
@Component("OutwardFieldTruncation")
public class OutwardFieldTruncation {

	/**
	 * This method is responsible for truncating Outward Header fields
	 * 
	 * @param document
	 */
	public void truncateHeaderFields(OutwardTransDocument document) {
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

		String origCgstin = document.getOrigCgstin();
		if (origCgstin != null && !origCgstin.isEmpty()) {
			if (origCgstin.length() > 15) {
				document.setOrigCgstin(origCgstin.substring(0, 15));
			}
		}

		String custName = document.getCustOrSuppName();
		if (custName != null && !custName.isEmpty()) {
			if (custName.length() > 100) {
				document.setCustOrSuppName(custName.substring(0, 100));
			}
		}
		String custCode = document.getCustOrSuppCode();
		if (custCode != null && !custCode.isEmpty()) {
			if (custCode.length() > 100) {
				document.setCustOrSuppCode(custCode.substring(0, 100));
			}
		}
		String custAddress1 = document.getCustOrSuppAddress1();
		if (custAddress1 != null && !custAddress1.isEmpty()) {
			if (custAddress1.length() > 120) {
				document.setCustOrSuppAddress1(custAddress1.substring(0, 120));
			}
		}
		String custAddress2 = document.getCustOrSuppAddress2();
		if (custAddress2 != null && !custAddress2.isEmpty()) {
			if (custAddress2.length() > 120) {
				document.setCustOrSuppAddress2(custAddress2.substring(0, 120));
			}
		}
		String custAddress3 = document.getCustOrSuppAddress3();
		if (custAddress3 != null && !custAddress3.isEmpty()) {
			if (custAddress3.length() > 100) {
				document.setCustOrSuppAddress3(custAddress3.substring(0, 100));
			}
		}
		String custAddress4 = document.getCustOrSuppAddress4();
		if (custAddress4 != null && !custAddress4.isEmpty()) {
			if (custAddress4.length() > 60) {
				document.setCustOrSuppAddress4(custAddress4.substring(0, 60));
			}
		}
		String accountVoucherNo = document.getAccountingVoucherNumber();
		if (accountVoucherNo != null && !accountVoucherNo.isEmpty()) {
			if (accountVoucherNo.length() > 100) {
				document.setAccountingVoucherNumber(
						accountVoucherNo.substring(0, 100));
			}
		}
		String glCodeIgst = document.getGlCodeIgst();
		if (glCodeIgst != null && !glCodeIgst.isEmpty()) {
			if (glCodeIgst.length() > 100) {
				document.setGlCodeIgst(glCodeIgst.substring(0, 100));
			}
		}

		String glCodeCgst = document.getGlCodeCgst();
		if (glCodeCgst != null && !glCodeCgst.isEmpty()) {
			if (glCodeCgst.length() > 100) {
				document.setGlCodeCgst(glCodeCgst.substring(0, 100));
			}
		}

		String glCodeSgst = document.getGlCodeSgst();
		if (glCodeSgst != null && !glCodeSgst.isEmpty()) {
			if (glCodeSgst.length() > 100) {
				document.setGlCodeSgst(glCodeSgst.substring(0, 100));
			}
		}

		String glCodeAdvCess = document.getGlCodeAdvCess();
		if (glCodeAdvCess != null && !glCodeAdvCess.isEmpty()) {
			if (glCodeAdvCess.length() > 100) {
				document.setGlCodeAdvCess(glCodeAdvCess.substring(0, 100));
			}
		}

		String glCodeSpCess = document.getGlCodeSpCess();
		if (glCodeSpCess != null && !glCodeSpCess.isEmpty()) {
			if (glCodeSpCess.length() > 100) {
				document.setGlCodeSpCess(glCodeSpCess.substring(0, 100));
			}
		}

		String glCodeStateCess = document.getGlCodeStateCess();
		if (glCodeStateCess != null && !glCodeStateCess.isEmpty()) {
			if (glCodeStateCess.length() > 100) {
				document.setGlCodeStateCess(glCodeStateCess.substring(0, 100));
			}
		}
		
		String glCodeStateCessSpecific = document.getGlStateCessSpecific();
		if (glCodeStateCessSpecific != null && !glCodeStateCessSpecific.isEmpty()) {
			if (glCodeStateCessSpecific.length() > 100) {
				document.setGlCodeStateCess(glCodeStateCessSpecific.substring(0, 100));
			}
		}

	}

	/**
	 * This method is responsible for truncating Outward Line Item fields
	 * 
	 * @param item
	 */
	public void truncateLineItemFields(OutwardTransDocLineItem item) {
		String glCodeTaxableValue = item.getGlCodeTaxableValue();
		if (glCodeTaxableValue != null && !glCodeTaxableValue.isEmpty()) {
			if (glCodeTaxableValue.length() > 100) {
				item.setGlCodeTaxableValue(
						glCodeTaxableValue.substring(0, 100));
			}
		}

		String itemCode = item.getItemCode();
		if (itemCode != null && !itemCode.isEmpty()) {
			if (itemCode.length() > 100) {
				item.setItemCode(itemCode.substring(0, 100));
			}
		}

		String itemDescription = item.getItemDescription();
		if (itemDescription != null && !itemDescription.isEmpty()) {
			if (itemDescription.length() > 100) {
				item.setItemDescription(itemDescription.substring(0, 100));
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
				item.setUserdefinedfield2(userDefField2.substring(0, 100));
			}
		}

		String userDefField3 = item.getUserdefinedfield3();
		if (userDefField3 != null && !userDefField3.isEmpty()) {
			if (userDefField3.length() > 100) {
				item.setUserdefinedfield3(userDefField3.substring(0, 100));
			}
		}

		String userDefField4 = item.getUserDefinedField4();
		if (userDefField4 != null && !userDefField4.isEmpty()) {
			if (userDefField4.length() > 100) {
				item.setUserDefinedField4(userDefField4.substring(0, 100));
			}
		}

		String userDefField5 = item.getUserDefinedField5();
		if (userDefField5 != null && !userDefField5.isEmpty()) {
			if (userDefField5.length() > 100) {
				item.setUserDefinedField5(userDefField5.substring(0, 100));
			}
		}

		String userDefField6 = item.getUserDefinedField6();
		if (userDefField6 != null && !userDefField6.isEmpty()) {
			if (userDefField6.length() > 100) {
				item.setUserDefinedField6(userDefField6.substring(0, 100));
			}
		}

		String userDefField7 = item.getUserDefinedField7();
		if (userDefField7 != null && !userDefField7.isEmpty()) {
			if (userDefField7.length() > 100) {
				item.setUserDefinedField7(userDefField7.substring(0, 100));
			}
		}

		String userDefField8 = item.getUserDefinedField8();
		if (userDefField8 != null && !userDefField8.isEmpty()) {
			if (userDefField8.length() > 100) {
				item.setUserDefinedField8(userDefField8.substring(0, 100));
			}
		}

		String userDefField9 = item.getUserDefinedField9();
		if (userDefField9 != null && !userDefField9.isEmpty()) {
			if (userDefField9.length() > 100) {
				item.setUserDefinedField9(userDefField9.substring(0, 100));
			}
		}

		String userDefField10 = item.getUserDefinedField10();
		if (userDefField10 != null && !userDefField10.isEmpty()) {
			if (userDefField10.length() > 100) {
				item.setUserDefinedField10(userDefField10.substring(0, 100));
			}
		}

		String userDefField11 = item.getUserDefinedField11();
		if (userDefField11 != null && !userDefField11.isEmpty()) {
			if (userDefField11.length() > 100) {
				item.setUserDefinedField11(userDefField11.substring(0, 100));
			}
		}

		String userDefField12 = item.getUserDefinedField12();
		if (userDefField12 != null && !userDefField12.isEmpty()) {
			if (userDefField12.length() > 100) {
				item.setUserDefinedField12(userDefField12.substring(0, 100));
			}
		}

		String userDefField13 = item.getUserDefinedField13();
		if (userDefField13 != null && !userDefField13.isEmpty()) {
			if (userDefField13.length() > 100) {
				item.setUserDefinedField13(userDefField13.substring(0, 100));
			}
		}

		String userDefField14 = item.getUserDefinedField14();
		if (userDefField14 != null && !userDefField14.isEmpty()) {
			if (userDefField14.length() > 100) {
				item.setUserDefinedField14(userDefField14.substring(0, 100));
			}
		}

		String userDefField15 = item.getUserDefinedField15();
		if (userDefField15 != null && !userDefField15.isEmpty()) {
			if (userDefField15.length() > 100) {
				item.setUserDefinedField15(userDefField15.substring(0, 100));
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
			if (custAddress1.length() > 120) {
				item.setCustOrSuppAddress1(custAddress1.substring(0, 120));
			}
		}
		String custAddress2 = item.getCustOrSuppAddress2();
		if (custAddress2 != null && !custAddress2.isEmpty()) {
			if (custAddress2.length() > 120) {
				item.setCustOrSuppAddress2(custAddress2.substring(0, 120));
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
			if (custAddress4.length() > 60) {
				item.setCustOrSuppAddress4(custAddress4.substring(0, 60));
			}
		}
		String accountVoucherNo = item.getAccountingVoucherNumber();
		if (accountVoucherNo != null && !accountVoucherNo.isEmpty()) {
			if (accountVoucherNo.length() > 100) {
				item.setAccountingVoucherNumber(
						accountVoucherNo.substring(0, 100));
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
		String glCodeAdvCess = item.getGlCodeAdvCess();
		if (glCodeAdvCess != null && !glCodeAdvCess.isEmpty()) {
			if (glCodeAdvCess.length() > 100) {
				item.setGlCodeAdvCess(glCodeAdvCess.substring(0, 100));
			}
		}
		String glCodeSpCess = item.getGlCodeSpCess();
		if (glCodeSpCess != null && !glCodeSpCess.isEmpty()) {
			if (glCodeSpCess.length() > 100) {
				item.setGlCodeSpCess(glCodeSpCess.substring(0, 100));
			}
		}
		String glCodeStateCess = item.getGlCodeStateCess();
		if (glCodeStateCess != null && !glCodeStateCess.isEmpty()) {
			if (glCodeStateCess.length() > 100) {
				item.setGlCodeStateCess(glCodeStateCess.substring(0, 100));
			}
		}

	}

}
