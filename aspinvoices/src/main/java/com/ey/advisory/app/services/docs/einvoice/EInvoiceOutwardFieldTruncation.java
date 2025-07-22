/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.common.EInvoiceStatus;
import com.ey.advisory.common.EwbProcessingStatus;

import com.google.common.base.Strings;

/**
 * This class is responsible for Truncating Outward Fields
 * 
 * @author Laxmi.Salukuti
 *
 */
@Component("EInvoiceOutwardFieldTruncation")
public class EInvoiceOutwardFieldTruncation {

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
		String accountVoucherNo = document.getAccountingVoucherNumber();
		if (accountVoucherNo != null && !accountVoucherNo.isEmpty()) {
			if (accountVoucherNo.length() > 100) {
				document.setAccountingVoucherNumber(
						accountVoucherNo.substring(0, 100));
			}
		}
		String userDefField1 = document.getUserdefinedfield1();
		if (userDefField1 != null && !userDefField1.isEmpty()) {
			if (userDefField1.length() > 100) {
				document.setUserdefinedfield1(userDefField1.substring(0, 100));
			}
		}
		String userDefField2 = document.getUserdefinedfield2();
		if (userDefField2 != null && !userDefField2.isEmpty()) {
			if (userDefField2.length() > 100) {
				document.setUserdefinedfield2(userDefField2.substring(0, 100));
			}
		}
		String userDefField3 = document.getUserdefinedfield3();
		if (userDefField3 != null && !userDefField3.isEmpty()) {
			if (userDefField3.length() > 100) {
				document.setUserdefinedfield3(userDefField3.substring(0, 100));
			}
		}
		String userDefField4 = document.getUserDefinedField4();
		if (userDefField4 != null && !userDefField4.isEmpty()) {
			if (userDefField4.length() > 100) {
				document.setUserDefinedField4(userDefField4.substring(0, 100));
			}
		}
		String cancelRemarks = document.getCancellationRemarks();
		if (cancelRemarks != null && !cancelRemarks.isEmpty()) {
			if (cancelRemarks.length() > 100) {
				document.setCancellationRemarks(
						cancelRemarks.substring(0, 100));
			}
		}
		String supplierTradeName = document.getSupplierTradeName();
		if (supplierTradeName != null && !supplierTradeName.isEmpty()) {
			if (supplierTradeName.length() > 100) {
				document.setSupplierTradeName(
						supplierTradeName.substring(0, 100));
			}
		}
		String supplierLegalName = document.getSupplierLegalName();
		if (supplierLegalName != null && !supplierLegalName.isEmpty()) {
			if (supplierLegalName.length() > 100) {
				document.setSupplierLegalName(
						supplierLegalName.substring(0, 100));
			}
		}

		String supplierLocation = document.getSupplierLocation();
		if (supplierLocation != null && !supplierLocation.isEmpty()) {
			if (supplierLocation.length() > 50) {
				document.setSupplierLocation(supplierLocation.substring(0, 50));
			}
		}

		if (document.getSupplierPhone() != null) {
			String supplierPhone = document.getSupplierPhone();
			if (supplierPhone.length() > 10) {
				supplierPhone = supplierPhone.substring(0, 10);
				// Long supplierPhn = Long.parseLong(supplierPhone);
				document.setSupplierPhone(supplierPhone);
			}
		}

		if (document.getCustomerPhone() != null
				&& !document.getCustomerPhone().isEmpty()) {
			String customerPhone = document.getCustomerPhone();
			if (customerPhone.length() > 12) {
				customerPhone = customerPhone.substring(0, 12);
				// Long supplierPhn = Long.parseLong(supplierPhone);
				document.setCustomerPhone(customerPhone);
			}
		}

		if (document.getCustomerPhone() != null) {
			String customerPhone = document.getCustomerPhone();
			if (customerPhone.length() > 12) {
				customerPhone = customerPhone.substring(0, 12);
				// Long supplierPhn = Long.parseLong(supplierPhone);
				document.setCustomerPhone(customerPhone);
			}
		}
		String supplierEmail = document.getSupplierEmail();
		if (supplierEmail != null && !supplierEmail.isEmpty()) {
			if (supplierEmail.length() > 50) {
				document.setSupplierEmail(supplierEmail.substring(0, 50));
			}
		}
		String customerTradeName = document.getCustomerTradeName();
		if (customerTradeName != null && !customerTradeName.isEmpty()) {
			if (customerTradeName.length() > 100) {
				document.setCustomerTradeName(
						customerTradeName.substring(0, 100));
			}
		}
		String dispatcherTradeName = document.getDispatcherTradeName();
		if (dispatcherTradeName != null && !dispatcherTradeName.isEmpty()) {
			if (dispatcherTradeName.length() > 100) {
				document.setDispatcherTradeName(
						dispatcherTradeName.substring(0, 100));
			}
		}
		String shipToTradeName = document.getShipToTradeName();
		if (shipToTradeName != null && !shipToTradeName.isEmpty()) {
			if (shipToTradeName.length() > 100) {
				document.setShipToTradeName(shipToTradeName.substring(0, 100));
			}
		}
		String shipToLegalName = document.getShipToLegalName();
		if (shipToLegalName != null && !shipToLegalName.isEmpty()) {
			if (shipToLegalName.length() > 60) {
				document.setShipToLegalName(shipToLegalName.substring(0, 60));
			}
		}
		String invRemarks = document.getInvoiceRemarks();
		if (invRemarks != null && !invRemarks.isEmpty()) {
			if (invRemarks.length() > 100) {
				document.setInvoiceRemarks(invRemarks.substring(0, 100));
			}
		}
		String payeeName = document.getPayeeName();
		if (payeeName != null && !payeeName.isEmpty()) {
			if (payeeName.length() > 100) {
				document.setPayeeName(payeeName.substring(0, 100));
			}
		}
		String paymentTerms = document.getPaymentTerms();
		if (paymentTerms != null && !paymentTerms.isEmpty()) {
			if (paymentTerms.length() > 100) {
				document.setPaymentTerms(paymentTerms.substring(0, 100));
			}
		}
		String paymentInstr = document.getPaymentInstruction();
		if (paymentInstr != null && !paymentInstr.isEmpty()) {
			if (paymentInstr.length() > 100) {
				document.setPaymentInstruction(paymentInstr.substring(0, 100));
			}
		}
		String creditTrans = document.getCreditTransfer();
		if (creditTrans != null && !creditTrans.isEmpty()) {
			if (creditTrans.length() > 100) {
				document.setCreditTransfer(creditTrans.substring(0, 100));
			}
		}
		String directDebit = document.getDirectDebit();
		if (directDebit != null && !directDebit.isEmpty()) {
			if (directDebit.length() > 100) {
				document.setDirectDebit(directDebit.substring(0, 100));
			}
		}
		String transporterName = document.getTransporterName();
		if (transporterName != null && !transporterName.isEmpty()) {
			if (transporterName.length() > 100) {
				document.setTransporterName(transporterName.substring(0, 100));
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
		if (glCodeStateCessSpecific != null
				&& !glCodeStateCessSpecific.isEmpty()) {
			if (glCodeStateCessSpecific.length() > 100) {
				document.setGlCodeStateCess(
						glCodeStateCessSpecific.substring(0, 100));
			}
		}

		if (document.getEwbProcessingStatus() != null
				&& document.geteInvStatus() != null
				&& (document.geteInvStatus() == EInvoiceStatus.NOT_OPTED
						.geteInvoiceStatusCode()
						|| document
								.geteInvStatus() == EInvoiceStatus.NOT_APPLICABLE
										.geteInvoiceStatusCode())
				&& document
						.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
								.getEwbProcessingStatusCode()) {

			String supplierAddress1 = document.getSupplierBuildingNumber();
			if (supplierAddress1 != null && !supplierAddress1.isEmpty()) {
				if (supplierAddress1.length() > 120) {
					document.setSupplierBuildingNumber(
							supplierAddress1.substring(0, 120));
				}
			}
			String supplierAddress2 = document.getSupplierBuildingName();
			if (supplierAddress2 != null && !supplierAddress2.isEmpty()) {
				if (supplierAddress2.length() > 120) {
					document.setSupplierBuildingName(
							supplierAddress2.substring(0, 120));
				}
			}
			String custAddress1 = document.getCustOrSuppAddress1();
			if (custAddress1 != null && !custAddress1.isEmpty()) {
				if (custAddress1.length() > 120) {
					document.setCustOrSuppAddress1(
							custAddress1.substring(0, 120));
				}
			}
			String custAddress2 = document.getCustOrSuppAddress2();
			if (custAddress2 != null && !custAddress2.isEmpty()) {
				if (custAddress2.length() > 120) {
					document.setCustOrSuppAddress2(
							custAddress2.substring(0, 120));
				}
			}
			String dispatcherBulidingNum = document
					.getDispatcherBuildingNumber();
			if (dispatcherBulidingNum != null
					&& !dispatcherBulidingNum.isEmpty()) {
				if (dispatcherBulidingNum.length() > 120) {
					document.setDispatcherBuildingNumber(
							dispatcherBulidingNum.substring(0, 120));
				}
			}
			String dispatcherBulidingName = document
					.getDispatcherBuildingName();
			if (dispatcherBulidingName != null
					&& !dispatcherBulidingName.isEmpty()) {
				if (dispatcherBulidingName.length() > 120) {
					document.setDispatcherBuildingName(
							dispatcherBulidingName.substring(0, 120));
				}
			}
			String shipToBuilingNum = document.getShipToBuildingNumber();
			if (shipToBuilingNum != null && !shipToBuilingNum.isEmpty()) {
				if (shipToBuilingNum.length() > 120) {
					document.setShipToBuildingNumber(
							shipToBuilingNum.substring(0, 120));
				}
			}
			String shipToBuilingName = document.getShipToBuildingName();
			if (shipToBuilingName != null && !shipToBuilingName.isEmpty()) {
				if (shipToBuilingName.length() > 120) {
					document.setShipToBuildingName(
							shipToBuilingName.substring(0, 120));
				}
			}
			String custAddress4 = document.getCustOrSuppAddress4();
			if (custAddress4 != null && !custAddress4.isEmpty()) {
				if (custAddress4.length() > 50) {
					document.setCustOrSuppAddress4(
							custAddress4.substring(0, 50));
				}
			}
			String dispatcherLocation = document.getDispatcherLocation();
			if (dispatcherLocation != null && !dispatcherLocation.isEmpty()) {
				if (dispatcherLocation.length() > 50) {
					document.setDispatcherLocation(
							dispatcherLocation.substring(0, 50));
				}
			}
			String shipToLocation = document.getShipToLocation();
			if (shipToLocation != null && !shipToLocation.isEmpty()) {
				if (shipToLocation.length() > 50) {
					document.setShipToLocation(shipToLocation.substring(0, 50));
				}
			}
		} else {
			String supplierAddress1 = document.getSupplierBuildingNumber();
			if (supplierAddress1 != null && !supplierAddress1.isEmpty()) {
				if (supplierAddress1.length() > 100) {
					document.setSupplierBuildingNumber(
							supplierAddress1.substring(0, 100));
				}
			}
			String supplierAddress2 = document.getSupplierBuildingName();
			if (supplierAddress2 != null && !supplierAddress2.isEmpty()) {
				if (supplierAddress2.length() > 100) {
					document.setSupplierBuildingName(
							supplierAddress2.substring(0, 100));
				}
			}
			String totalInvoiceValueInWords = document.getTotalInvoiceValueInWords();
			if (!Strings.isNullOrEmpty(totalInvoiceValueInWords)) {
				if (totalInvoiceValueInWords.length() > 200) {
					document.setTotalInvoiceValueInWords(
							totalInvoiceValueInWords.substring(0,200));
				}
			}
			String custAddress1 = document.getCustOrSuppAddress1();
			if (custAddress1 != null && !custAddress1.isEmpty()) {
				if (custAddress1.length() > 100) {
					document.setCustOrSuppAddress1(
							custAddress1.substring(0, 100));
				}
			}
			String custAddress2 = document.getCustOrSuppAddress2();
			if (custAddress2 != null && !custAddress2.isEmpty()) {
				if (custAddress2.length() > 100) {
					document.setCustOrSuppAddress2(
							custAddress2.substring(0, 100));
				}
			}
			String dispatcherBulidingNum = document
					.getDispatcherBuildingNumber();
			if (dispatcherBulidingNum != null
					&& !dispatcherBulidingNum.isEmpty()) {
				if (dispatcherBulidingNum.length() > 100) {
					document.setDispatcherBuildingNumber(
							dispatcherBulidingNum.substring(0, 100));
				}
			}
			String dispatcherBulidingName = document
					.getDispatcherBuildingName();
			if (dispatcherBulidingName != null
					&& !dispatcherBulidingName.isEmpty()) {
				if (dispatcherBulidingName.length() > 100) {
					document.setDispatcherBuildingName(
							dispatcherBulidingName.substring(0, 100));
				}
			}
			String shipToBuilingNum = document.getShipToBuildingNumber();
			if (shipToBuilingNum != null && !shipToBuilingNum.isEmpty()) {
				if (shipToBuilingNum.length() > 100) {
					document.setShipToBuildingNumber(
							shipToBuilingNum.substring(0, 100));
				}
			}
			String shipToBuilingName = document.getShipToBuildingName();
			if (shipToBuilingName != null && !shipToBuilingName.isEmpty()) {
				if (shipToBuilingName.length() > 100) {
					document.setShipToBuildingName(
							shipToBuilingName.substring(0, 100));
				}
			}
			String custAddress4 = document.getCustOrSuppAddress4();
			if (custAddress4 != null && !custAddress4.isEmpty()) {
				if (custAddress4.length() > 100) {
					document.setCustOrSuppAddress4(
							custAddress4.substring(0, 100));
				}
			}
			String dispatcherLocation = document.getDispatcherLocation();
			if (dispatcherLocation != null && !dispatcherLocation.isEmpty()) {
				if (dispatcherLocation.length() > 100) {
					document.setDispatcherLocation(
							dispatcherLocation.substring(0, 100));
				}
			}
			String shipToLocation = document.getShipToLocation();
			if (shipToLocation != null && !shipToLocation.isEmpty()) {
				if (shipToLocation.length() > 100) {
					document.setShipToLocation(
							shipToLocation.substring(0, 100));
				}
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
		String itemCode = item.getItemCode();
		if (itemCode != null && !itemCode.isEmpty()) {
			if (itemCode.length() > 100) {
				item.setItemCode(itemCode.substring(0, 100));
			}
		}
		String itemDescription = item.getItemDescription();
		if (itemDescription != null && !itemDescription.isEmpty()) {
			if (itemDescription.length() > 300) {
				item.setItemDescription(itemDescription.substring(0, 300));
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
			if (crdrReason.length() > 200) {
				item.setCrDrReason(crdrReason.substring(0, 200));
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

		String custAddress4 = item.getCustOrSuppAddress4();
		if (custAddress4 != null && !custAddress4.isEmpty()) {
			if (custAddress4.length() > 100) {
				item.setCustOrSuppAddress4(custAddress4.substring(0, 100));
			}
		}

		String barCode = item.getBarcode();
		if (barCode != null && !barCode.isEmpty()) {
			if (barCode.length() > 30) {
				item.setBarcode(barCode.substring(0, 30));
			}
		}
		String batchName = item.getBatchNameOrNumber();
		if (batchName != null && !batchName.isEmpty()) {
			if (batchName.length() > 20) {
				item.setBatchNameOrNumber(batchName.substring(0, 20));
			}
		}
		String orderLineRef = item.getOrderLineReference();
		if (orderLineRef != null && !orderLineRef.isEmpty()) {
			if (orderLineRef.length() > 50) {
				item.setOrderLineReference(orderLineRef.substring(0, 50));
			}
		}
		String attrName = item.getAttributeName();
		if (attrName != null && !attrName.isEmpty()) {
			if (attrName.length() > 100) {
				item.setAttributeName(attrName.substring(0, 100));
			}
		}
		String attrValue = item.getAttributeValue();
		if (attrValue != null && !attrValue.isEmpty()) {
			if (attrValue.length() > 20) {
				item.setAttributeValue(attrValue.substring(0, 20));
			}
		}

		String recAdvicRef = item.getReceiptAdviceReference();
		if (recAdvicRef != null && !recAdvicRef.isEmpty()) {
			if (recAdvicRef.length() > 20) {
				item.setReceiptAdviceReference(recAdvicRef.substring(0, 20));
			}
		}
		String tenderRef = item.getTenderReference();
		if (tenderRef != null && !tenderRef.isEmpty()) {
			if (tenderRef.length() > 20) {
				item.setTenderReference(tenderRef.substring(0, 20));
			}
		}
		String otherRef = item.getInvoiceReference();
		if (otherRef != null && !otherRef.isEmpty()) {
			if (otherRef.length() > 20) {
				item.setInvoiceReference(otherRef.substring(0, 20));
			}
		}
		String contractRef = item.getContractReference();
		if (contractRef != null && !contractRef.isEmpty()) {
			if (contractRef.length() > 20) {
				item.setContractReference(contractRef.substring(0, 20));
			}
		}
		String externalRef = item.getExternalReference();
		if (externalRef != null && !externalRef.isEmpty()) {
			if (externalRef.length() > 20) {
				item.setExternalReference(externalRef.substring(0, 20));
			}
		}
		String projectRef = item.getProjectReference();
		if (projectRef != null && !projectRef.isEmpty()) {
			if (projectRef.length() > 20) {
				item.setProjectReference(projectRef.substring(0, 20));
			}
		}
		String custPoRefNum = item.getCustomerPOReferenceNumber();
		if (custPoRefNum != null && !custPoRefNum.isEmpty()) {
			custPoRefNum = custPoRefNum.replaceAll("\\h", "");
			if (custPoRefNum.length() > 16) {
				item.setCustomerPOReferenceNumber(
						custPoRefNum.substring(0, 16));
			} else {
				item.setCustomerPOReferenceNumber(custPoRefNum);
			}
		}
		String suppDocUrl = item.getSupportingDocURL();
		if (suppDocUrl != null && !suppDocUrl.isEmpty()) {
			if (suppDocUrl.length() > 100) {
				item.setSupportingDocURL(suppDocUrl.substring(0, 100));
			}
		}
		String suppDocument = item.getSupportingDocBase64();
		if (suppDocument != null && !suppDocument.isEmpty()) {
			if (suppDocument.length() > 1000) {
				item.setSupportingDocBase64(suppDocument.substring(0, 1000));
			}
		}
		String additionalInfo = item.getAdditionalInformation();
		if (additionalInfo != null && !additionalInfo.isEmpty()) {
			if (additionalInfo.length() > 1000) {
				item.setAdditionalInformation(
						additionalInfo.substring(0, 1000));
			}
		}
		String supplierLegalName = item.getSupplierLegalName();
		if (supplierLegalName != null && !supplierLegalName.isEmpty()) {
			if (supplierLegalName.length() > 100) {
				item.setSupplierLegalName(supplierLegalName.substring(0, 100));
			}
		}
		String supplierAddress1 = item.getSupplierBuildingNumber();
		if (supplierAddress1 != null && !supplierAddress1.isEmpty()) {
			if (supplierAddress1.length() > 120) {
				item.setSupplierBuildingNumber(
						supplierAddress1.substring(0, 120));
			}
		}
		String supplierLocation = item.getSupplierLocation();
		if (supplierLocation != null && !supplierLocation.isEmpty()) {
			if (supplierLocation.length() > 50) {
				item.setSupplierLocation(supplierLocation.substring(0, 50));
			}
		}
		String dispatcherTradeName = item.getDispatcherTradeName();
		if (dispatcherTradeName != null && !dispatcherTradeName.isEmpty()) {
			if (dispatcherTradeName.length() > 100) {
				item.setDispatcherTradeName(
						dispatcherTradeName.substring(0, 100));
			}
		}
		String dispatcherBulidingNum = item.getDispatcherBuildingNumber();
		if (dispatcherBulidingNum != null && !dispatcherBulidingNum.isEmpty()) {
			if (dispatcherBulidingNum.length() > 120) {
				item.setDispatcherBuildingNumber(
						dispatcherBulidingNum.substring(0, 120));
			}
		}
		String dispatcherLocation = item.getDispatcherLocation();
		if (dispatcherLocation != null && !dispatcherLocation.isEmpty()) {
			if (dispatcherLocation.length() > 100) {
				item.setDispatcherLocation(
						dispatcherLocation.substring(0, 100));
			}
		}
		String shipToLegalName = item.getShipToLegalName();
		if (shipToLegalName != null && !shipToLegalName.isEmpty()) {
			if (shipToLegalName.length() > 60) {
				item.setShipToLegalName(shipToLegalName.substring(0, 60));
			}
		}
		String shipToBuilingNum = item.getShipToBuildingNumber();
		if (shipToBuilingNum != null && !shipToBuilingNum.isEmpty()) {
			if (shipToBuilingNum.length() > 120) {
				item.setShipToBuildingNumber(
						shipToBuilingNum.substring(0, 120));
			}
		}
		String shipToLocation = item.getShipToLocation();
		if (shipToLocation != null && !shipToLocation.isEmpty()) {
			if (shipToLocation.length() > 100) {
				item.setShipToLocation(shipToLocation.substring(0, 100));
			}
		}
		String transporterName = item.getTransporterName();
		if (transporterName != null && !transporterName.isEmpty()) {
			if (transporterName.length() > 100) {
				item.setTransporterName(transporterName.substring(0, 100));
			}
		}

	}
}
