package com.ey.advisory.app.services.doc.gstr1a;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnx1OutWardErrHeader;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnxOutwardTransDocLineItemError;
import com.ey.advisory.app.services.docs.DocumentKeyBuilder;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Service("SRFileToGstr1AComprehensiveOutwardTransDocErrConvertion")
@Slf4j
public class SRFileToGstr1AComprehensiveOutwardTransDocErrConvertion {

	/**
	 * 
	 * @param errDocMapObj
	 * @param documentKeyBuilder
	 * @param fileStatus
	 * @return
	 */
	public List<Gstr1AAnx1OutWardErrHeader> convertSRFileToOutWardTransError(
			Map<String, List<Object[]>> errDocMapObj,
			DocumentKeyBuilder documentKeyBuilder,
			Gstr1FileStatusEntity fileStatus, String userName) {
		LOGGER.error("SRFileToOutwardTransDocErrConvertion "
				+ "convertSRFileToOutWardTransError Begining");
		List<Gstr1AAnx1OutWardErrHeader> errorHeaders = new ArrayList<>();
		try {
			errDocMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<Object[]> objs = entry.getValue();
				Gstr1AAnx1OutWardErrHeader errorDocument = new Gstr1AAnx1OutWardErrHeader();
				List<Gstr1AAnxOutwardTransDocLineItemError> lineItems = new ArrayList<>();
				for (Object[] obj : objs) {
					Gstr1AAnxOutwardTransDocLineItemError lineItem = new Gstr1AAnxOutwardTransDocLineItemError();

					String irn = getWithValue(obj[0]);
					String irnDate = getWithValue(obj[1]);
					LocalDate localIrnDate = DateUtil.parseObjToDate(irnDate);
					String taxScheme = getWithValue(obj[2]);
					String canReason = getWithValue(obj[3]);
					String canRemarks = getWithValue((obj[4]));
					String supplyType = getWithValue(obj[5]);

					String docCategory = getWithValue(obj[6]);
					String docType = getWithValue(obj[7]);

					String docNo = getWithValue(obj[8]);
					String docDate = getWithValue(obj[9]);
					LocalDate localDocDate = DateUtil.parseObjToDate(docDate);
					String finYear = GenUtil.getFinYear(localDocDate);
					String reverseChargeFlag = getWithValue(obj[10]);
					String sgstin = getWithValue(obj[11]);

					String supplierTradeName = getWithValue(obj[12]);
					String supplierLegalName = getWithValue(obj[13]);
					String supAddress1 = getWithValue(obj[14]);
					String supAddress2 = getWithValue(obj[15]);
					String supplierLocation = getWithValue(obj[16]);
					String supplierPincode = getWithValue(obj[17]);
					String supplierStateCode = getWithValue(obj[18]);
					String supplierPhone = getWithValue(obj[19]);
					String supplierEmail = getWithValue(obj[20]);
					String customerGstin = getWithValue(obj[21]);

					String customerTradeName = getWithValue(obj[22]);
					String customerLegalName = getWithValue(obj[23]);
					String customerAddress1 = getWithValue(obj[24]);
					String customerAddress2 = getWithValue(obj[25]);
					String customerLocation = getWithValue(obj[26]);
					String customerPincode = getWithValue(obj[27]);
					String customerStateCode = getWithValue(obj[28]);
					String billingPos = getWithValue(obj[29]);
					String customerPhone = getWithValue(obj[30]);
					String customerEmail = getWithValue(obj[31]);
					String dispatcherGstin = getWithValue(obj[32]);
					String dispatcherTradeName = getWithValue(obj[33]);
					String dispatcherAddress1 = getWithValue(obj[34]);
					String dispatcherAddress2 = getWithValue(obj[35]);
					String dispatcherLocation = getWithValue(obj[36]);
					String dispatcherPinCode = getWithValue(obj[37]);
					String dispatcherStateCode = getWithValue(obj[38]);
					String shipToGstin = getWithValue(obj[39]);
					String shipToTradeName = getWithValue(obj[40]);
					String shipToLegalName = getWithValue(obj[41]);
					String shipToAddress1 = getWithValue(obj[42]);
					String shipToAddress2 = getWithValue(obj[43]);
					String shipToLocation = getWithValue(obj[44]);
					String shipToPincode = getWithValue(obj[45]);
					String shipToStateCode = getWithValue(obj[46]);
					// Line Items
					String lineNo = getWithValue(obj[47]);
					;
					String productSerialNumber = getWithValue(obj[48]);
					String productName = getWithValue(obj[49]);
					String productDesription = getWithValue(obj[50]);
					String isService = getWithValue(obj[51]);

					String hsn = getWithValue(obj[52]);
					String barCode = getWithValue(obj[53]);
					String batchNameOrNo = getWithValue(obj[54]);
					String localBatchExpiryDate = getWithValue(obj[55]);
					LocalDate batchExpiryDate = DateUtil
							.parseObjToDate(localBatchExpiryDate);
					String localWarranDate = getWithValue(obj[56]);
					LocalDate warranDate = DateUtil
							.parseObjToDate(localWarranDate);
					String orderLineRef = getWithValue(obj[57]);
					String attributeName = getWithValue(obj[58]);
					String attributeValue = getWithValue(obj[59]);
					String originCountry = getWithValue(obj[60]);
					String uom = getWithValue(obj[61]);

					String qty = getWithValue(obj[62]);

					String freeQty = getWithValue(obj[63]);

					String unitPrice = getWithValue(obj[64]);

					String itemAmount = getWithValue(obj[65]);
					String itemDiscount = getWithValue(obj[66]);

					String preTaxAmount = getWithValue(obj[67]);

					String itemAccAmount = getWithValue(obj[68]);

					String igstRate = getWithValue(obj[69]);
					;

					String igstAmt = getWithValue(obj[70]);

					String cgstRate = getWithValue(obj[71]);
					String cgstAmt = getWithValue(obj[72]);

					String sgstRate = getWithValue(obj[73]);

					String sgstAmt = getWithValue(obj[74]);

					String cessAdvaloremRate = getWithValue(obj[75]);

					String cessAdvaloremAmt = getWithValue(obj[76]);
					String cessSpecificRate = getWithValue(obj[77]);

					String cessSpecificAmt = getWithValue(obj[78]);

					String stateCessAdvaloremRate = getWithValue(obj[79]);

					String stateCessAdvaloremAmt = getWithValue(obj[80]);

					String stateCessRate = getWithValue(obj[81]);

					String stateCessAmt = getWithValue(obj[82]);

					String itemOthCharges = getWithValue(obj[83]);

					String totalItemAmt = getWithValue(obj[84]);

					String invOthCharges = getWithValue(obj[85]);

					String invoiceAssessableAmount = getWithValue(obj[86]);

					String invoiceIGSTAmount = getWithValue(obj[87]);
					String invoiceCGSTAmount = getWithValue(obj[88]);

					String invoiceSGSTAmount = getWithValue(obj[89]);

					String invoiceCessAdvaloremAmount = getWithValue(obj[90]);

					String invoiceCessSpecificAmount = getWithValue(obj[91]);

					String invoiceStateCessAdvaloremAmount = getWithValue(
							obj[92]);

					String invoiceStateCessAmount = getWithValue(obj[93]);

					String invoiceValue = getWithValue(obj[94]);

					String roundOff = getWithValue(obj[95]);

					String totalInvValue = getWithValue(obj[96]);
					String tcsFlagIncomeTax = getWithValue(obj[97]);

					String tcsFlagRateIncomeTax = getWithValue(obj[98]);

					String tcsFlagAmountTax = getWithValue(obj[99]);

					String customerTanOrAdhar = getWithValue(obj[100]);

					String currencyCode = getWithValue(obj[101]);

					String countryCode = getWithValue(obj[102]);
					String invValueFC = getWithValue(obj[103]);

					String portCode = getWithValue(obj[104]);

					String shippingBillNo = getWithValue(obj[105]);

					String localShippingBillDate = getWithValue(obj[106]);
					LocalDate shippingBillDate = DateUtil
							.parseObjToDate(localShippingBillDate);

					String invoiceRemarks = getWithValue(obj[107]);

					String localInvoicePeriodStartDate = getWithValue(obj[108]);
					LocalDate invoicePeriodStartDate = DateUtil
							.parseObjToDate(localInvoicePeriodStartDate);

					String localinvoicePeriodEndDate = getWithValue(obj[109]);
					LocalDate invoicePeriodEndDate = DateUtil
							.parseObjToDate(localinvoicePeriodEndDate);
					String preceedingInvoiceNumber = getWithValue(obj[110]);
					String localpreceedingInvoiceDate = getWithValue(obj[111]);
					LocalDate preceedingInvoiceDate = DateUtil
							.parseObjToDate(localpreceedingInvoiceDate);

					String otherReference = getWithValue(obj[112]);
					String receiptAdviceReference = getWithValue(obj[113]);
					String localreceiptAdviceDate = getWithValue(obj[114]);
					LocalDate receiptAdviceDate = DateUtil
							.parseObjToDate(localreceiptAdviceDate);
					String tenderReference = getWithValue(obj[115]);

					String contractRef = getWithValue(obj[116]);
					String externalRef = getWithValue(obj[117]);

					String projectRef = getWithValue(obj[118]);

					String customerPOReferenceNumber = getWithValue(obj[119]);
					String localCustPOReferDate = getWithValue(obj[120]);
					LocalDate customerPOReferenceDate = DateUtil
							.parseObjToDate(localCustPOReferDate);
					String payeeName = getWithValue(obj[121]);

					String modeOfPayment = getWithValue(obj[122]);

					String branchOrIFSCCode = getWithValue(obj[123]);

					String paymentTerms = getWithValue(obj[124]);
					String paymentInstruction = getWithValue(obj[125]);

					String creditTransfer = getWithValue(obj[126]);

					String directDebit = getWithValue(obj[127]);

					String creditDays = getWithValue(obj[128]);

					String paidAmount = getWithValue(obj[129]);

					String balanceAmount = getWithValue(obj[130]);

					String localpaymentDueDate = getWithValue(obj[131]);
					LocalDate paymentDueDate = DateUtil
							.parseObjToDate(localpaymentDueDate);
					String accountDetail = getWithValue(obj[132]);

					String ecomGSTIN = getWithValue(obj[133]);

					String ecomTransactionId = getWithValue(obj[134]);
					String supportingDocURL = getWithValue(obj[135]);
					String supportingDocBase64 = getWithValue(obj[136]);
					String additionalInfo = getWithValue(obj[137]);
					String transactionType = getWithValue(obj[138]);
					String subSupplyType = getWithValue(obj[139]);

					String otherSupplyTypeDescription = getWithValue(obj[140]);
					String transporterID = getWithValue(obj[141]);
					String transporterName = getWithValue(obj[142]);
					String transportMode = getWithValue(obj[143]);
					String transportDocNumber = getWithValue(obj[144]);

					String localtransportDocDate = getWithValue(obj[145]);
					LocalDate transportDocDate = DateUtil
							.parseObjToDate(localtransportDocDate);

					String distance = getWithValue(obj[146]);

					String vehicleNumber = getWithValue(obj[147]);

					String vehicleType = getWithValue(obj[148]);
					String returnPeriod = getWithValue(obj[149]);
					Integer derivedRetPeriod = GenUtil
							.convertTaxPeriodToInt(returnPeriod);
					if (derivedRetPeriod != null) {
						errorDocument.setDerivedTaxperiod(derivedRetPeriod);
						lineItem.setDerivedTaxperiod(derivedRetPeriod);
					} else {
						lineItem.setDerivedTaxperiod(999999);
						errorDocument.setDerivedTaxperiod(999999);
					}
					String originalDocType = getWithValue(obj[150]);

					String originalCustomerGstin = getWithValue(obj[151]);
					String diffPerFalg = getWithValue(obj[152]);
					String sec7ofIgstFlag = getWithValue(obj[153]);

					String claimRefFlag = getWithValue(obj[154]);
					String autoPopulateToRefund = getWithValue(obj[155]);

					String crDrPreGst = getWithValue(obj[156]);

					String customerType = getWithValue(obj[157]);

					String customerCode = getWithValue(obj[158]);
					String productCode = getWithValue(obj[159]);

					String categoryOfProduct = getWithValue(obj[160]);

					String itcFlag = getWithValue(obj[161]);
					String stateAppCess = getWithValue(obj[162]);

					String fob = getWithValue(obj[163]);

					String exportDuty = getWithValue(obj[164]);

					String exchangerate = getWithValue(obj[165]);

					String reasonForCrDrNote = getWithValue(obj[166]);
					String tcsFlagGST = getWithValue(obj[167]);
					String tcsIgstAmt = getWithValue(obj[168]);

					String tcsCgstAmt = getWithValue(obj[169]);

					String tcsSgstAmt = getWithValue(obj[170]);
					String tdsFlagGST = getWithValue(obj[171]);

					String tdsIgstAmt = getWithValue(obj[172]);

					String tdsCgstAmt = getWithValue(obj[173]);

					String tdsSgstAmt = getWithValue(obj[174]);
					String userId = getWithValue(obj[175]);
					String companyCode = getWithValue(obj[176]);
					String sourceIdentifier = getWithValue(obj[177]);
					String sourceFileName = getWithValue(obj[178]);
					String plantCode = getWithValue(obj[179]);
					String division = getWithValue(obj[180]);
					String subDivision = getWithValue(obj[181]);

					String location = getWithValue(obj[182]);
					String salesOrg = getWithValue(obj[183]);
					String distChannel = getWithValue(obj[184]);
					String profitCentre1 = getWithValue(obj[185]);
					String profitCentre2 = getWithValue(obj[186]);
					String userAccess1 = getWithValue(obj[187]);
					String userAccess2 = getWithValue(obj[188]);
					String userAccess3 = getWithValue(obj[189]);
					String userAccess4 = getWithValue(obj[190]);
					String userAccess5 = getWithValue(obj[191]);
					String userAccess6 = getWithValue(obj[192]);
					String glAccessableValue = getWithValue(obj[193]);
					String glCodeIgst = getWithValue(obj[194]);
					String glCodeCgst = getWithValue(obj[195]);
					String glCodeSgst = getWithValue(obj[196]);
					String glAdvaloremCess = getWithValue(obj[197]);
					String glAdvaloremSpecificCess = getWithValue(obj[198]);
					String glStateCessAdvalore = getWithValue(obj[199]);
					String glStateCess = getWithValue(obj[200]);
					String localGlPostingDate = getWithValue(obj[201]);
					LocalDate glPostingDate = DateUtil
							.parseObjToDate(localGlPostingDate);

					String salesOrderNo = getWithValue(obj[202]);
					String ewbNo = getWithValue(obj[203]);
					String localEwbDate = getWithValue(obj[204]);
					LocalDate ewbDate = DateUtil.parseObjToDate(localEwbDate);

					String accountingVochar = getWithValue(obj[205]);
					String localAccountingVocharDate = getWithValue(obj[206]);
					LocalDate accountingVocharDate = DateUtil
							.parseObjToDate(localAccountingVocharDate);
					String documentRefNumber = getWithValue(obj[207]);
					String customerTan = getWithValue(obj[208]);
					String userDefinedField1 = getWithValue(obj[209]);
					String userDefinedField2 = getWithValue(obj[210]);
					String userDefinedField3 = getWithValue(obj[211]);
					String userDefinedField4 = getWithValue(obj[212]);
					String userDefinedField5 = getWithValue(obj[213]);
					String userDefinedField6 = getWithValue(obj[214]);
					String userDefinedField7 = getWithValue(obj[215]);
					String userDefinedField8 = getWithValue(obj[216]);
					String userDefinedField9 = getWithValue(obj[217]);
					String userDefinedField10 = getWithValue(obj[218]);
					String userDefinedField11 = getWithValue(obj[219]);
					String userDefinedField12 = getWithValue(obj[220]);
					String userDefinedField13 = getWithValue(obj[221]);
					String userDefinedField14 = getWithValue(obj[222]);
					String userDefinedField15 = getWithValue(obj[223]);

					String userDefinedField16 = getWithValue(obj[224]);
					String userDefinedField17 = getWithValue(obj[225]);
					String userDefinedField18 = getWithValue(obj[226]);
					String userDefinedField19 = getWithValue(obj[227]);
					String userDefinedField20 = getWithValue(obj[228]);
					String userDefinedField21 = getWithValue(obj[229]);
					String userDefinedField22 = getWithValue(obj[230]);
					String userDefinedField23 = getWithValue(obj[231]);
					String userDefinedField24 = getWithValue(obj[232]);
					String userDefinedField25 = getWithValue(obj[233]);
					String userDefinedField26 = getWithValue(obj[234]);
					String userDefinedField27 = getWithValue(obj[235]);
					String userDefinedField28 = getWithValue(obj[236]);
					String userDefinedField29 = getWithValue(obj[237]);
					String userDefinedField30 = getWithValue(obj[238]);

					/**
					 * Start - Outward Document Structural Validation Error
					 * Correction Implementation
					 */
					// File ID and Id are not null only in case of Outward Doc
					// Structural Validation Error Correction
					String idStr = null;
					String fileIdStr = null;
					if (obj.length > 239) {
						idStr = (obj[239] != null
								&& !obj[239].toString().isEmpty())
										? String.valueOf(obj[239]) : null;
						fileIdStr = (obj[240] != null
								&& !obj[240].toString().isEmpty())
										? String.valueOf(obj[240]) : null;
					}
					/**
					 * End - Outward Document Structural Validation Error
					 * Correction Implementation
					 */

					lineItem.setSupplyType(supplyType);
					lineItem.setLineNo(lineNo);
					lineItem.setSerialNumberII(productSerialNumber);
					lineItem.setProductName(productName);
					lineItem.setItemDescription(productDesription);
					lineItem.setIsService(isService);
					lineItem.setHsnSac(hsn);
					lineItem.setBarcode(barCode);
					lineItem.setBatchNameOrNumber(batchNameOrNo);
					lineItem.setBatchExpiryDate(
							batchExpiryDate != null ? batchExpiryDate.toString()
									: localBatchExpiryDate);
					lineItem.setWarrantyDate(warranDate != null
							? warranDate.toString() : localWarranDate);
					lineItem.setOrderLineReference(orderLineRef);
					lineItem.setAttributeName(attributeName);
					lineItem.setAttributeValue(attributeValue);
					lineItem.setOriginCountry(originCountry);
					lineItem.setUom(uom);
					lineItem.setQty(qty);
					lineItem.setFreeQuantity(freeQty);
					lineItem.setUnitPrice(unitPrice);
					lineItem.setItemAmount(itemAmount);
					lineItem.setItemDiscount(itemDiscount);
					lineItem.setPreTaxAmount(preTaxAmount);
					lineItem.setOtherValues(itemOthCharges);
					lineItem.setTaxableValue(itemAccAmount);
					lineItem.setIgstRate(igstRate);
					lineItem.setIgstAmount(igstAmt);
					lineItem.setCgstRate(cgstRate);
					lineItem.setCgstAmount(cgstAmt);
					lineItem.setSgstRate(sgstRate);
					lineItem.setSgstAmount(sgstAmt);
					lineItem.setCessRateAdvalorem(cessAdvaloremRate);
					lineItem.setCessAmountAdvalorem(cessAdvaloremAmt);
					lineItem.setCessRateSpecific(cessSpecificRate);
					lineItem.setCessAmountSpecific(cessSpecificAmt);
					lineItem.setStateCessSpecificRate(stateCessRate);
					lineItem.setStateCessSpecificAmt(stateCessAmt);
					lineItem.setStateCessRate(stateCessAdvaloremRate);
					lineItem.setStateCessAmount(stateCessAdvaloremAmt);
					lineItem.setTotalItemAmount(totalItemAmt);
					lineItem.setLineItemAmt(invoiceValue);
					lineItem.setTcsRateIncomeTax(tcsFlagRateIncomeTax);
					lineItem.setTcsAmountIncomeTax(tcsFlagAmountTax);

					lineItem.setPreceedingInvoiceNumber(
							preceedingInvoiceNumber);
					lineItem.setPreceedingInvoiceDate(
							preceedingInvoiceDate != null
									? preceedingInvoiceDate.toString()
									: localpreceedingInvoiceDate);
					lineItem.setInvoiceReference(otherReference);
					lineItem.setReceiptAdviceReference(receiptAdviceReference);
					lineItem.setReceiptAdviceDate(receiptAdviceDate != null
							? receiptAdviceDate.toString()
							: localreceiptAdviceDate);
					lineItem.setTenderReference(tenderReference);
					lineItem.setContractReference(contractRef);
					lineItem.setExternalReference(externalRef);
					lineItem.setProjectReference(projectRef);
					lineItem.setCustomerPOReferenceNumber(
							customerPOReferenceNumber);
					lineItem.setCustomerPOReferenceDate(
							customerPOReferenceDate != null
									? customerPOReferenceDate.toString()
									: localCustPOReferDate);
					lineItem.setPaidAmount(paidAmount);
					lineItem.setBalanceAmount(balanceAmount);
					lineItem.setSupportingDocURL(supportingDocURL);
					lineItem.setSupportingDocBase64(supportingDocBase64);
					lineItem.setInvStateCessSpecificAmt(invoiceStateCessAmount);
					lineItem.setAdditionalInformation(additionalInfo);
					lineItem.setItemCode(productCode);
					lineItem.setItemCategory(categoryOfProduct);
					lineItem.setItcFlag(itcFlag);
					lineItem.setStateApplyingCess(stateAppCess);
					lineItem.setFob(fob);
					lineItem.setExportDuty(exportDuty);
					lineItem.setCrDrReason(reasonForCrDrNote);
					lineItem.setTcsAmount(tcsIgstAmt);
					lineItem.setTcsCgstAmount(tcsCgstAmt);
					lineItem.setTcsSgstAmount(tcsSgstAmt);
					lineItem.setTdsIgstAmount(tdsIgstAmt);
					lineItem.setTdsCgstAmount(tdsCgstAmt);
					lineItem.setTdsSgstAmount(tdsSgstAmt);
					lineItem.setGlCodeTaxableValue(glAccessableValue);
					lineItem.setDocReferenceNumber(documentRefNumber);
					lineItem.setPlantCode(plantCode);
					lineItem.setSubDivision(subDivision);
					lineItem.setUserAccess1(userAccess1);
					lineItem.setUserAccess2(userAccess2);
					lineItem.setUserAccess3(userAccess3);
					lineItem.setUserAccess4(userAccess4);
					lineItem.setUserAccess5(userAccess5);
					lineItem.setUserAccess6(userAccess6);
					lineItem.setUserdefinedfield1(userDefinedField1);
					lineItem.setUserdefinedfield2(userDefinedField2);
					lineItem.setUserdefinedfield3(userDefinedField3);
					lineItem.setUserDefinedField4(userDefinedField4);
					lineItem.setUserDefinedField5(userDefinedField5);
					lineItem.setUserDefinedField6(userDefinedField6);
					lineItem.setUserDefinedField7(userDefinedField7);
					lineItem.setUserDefinedField8(userDefinedField8);
					lineItem.setUserDefinedField9(userDefinedField9);
					lineItem.setUserDefinedField10(userDefinedField10);
					lineItem.setUserDefinedField11(userDefinedField11);
					lineItem.setUserDefinedField12(userDefinedField12);
					lineItem.setUserDefinedField13(userDefinedField13);
					lineItem.setUserDefinedField14(userDefinedField14);
					lineItem.setUserDefinedField15(userDefinedField15);
					lineItem.setUserDefinedField16(userDefinedField16);
					lineItem.setUserDefinedField17(userDefinedField17);
					lineItem.setUserDefinedField18(userDefinedField18);
					lineItem.setUserDefinedField19(userDefinedField19);
					lineItem.setUserDefinedField20(userDefinedField20);
					lineItem.setUserDefinedField21(userDefinedField21);
					lineItem.setUserDefinedField22(userDefinedField22);
					lineItem.setUserDefinedField23(userDefinedField23);
					lineItem.setUserDefinedField24(userDefinedField24);
					lineItem.setUserDefinedField25(userDefinedField25);
					lineItem.setUserDefinedField26(userDefinedField26);
					lineItem.setUserDefinedField27(userDefinedField27);
					lineItem.setUserDefinedField28(userDefinedField28);
					lineItem.setUserDefinedField29(userDefinedField29);
					lineItem.setUserDefinedField30(userDefinedField30);
					lineItem.setInvoiceOtherCharges(invOthCharges);
					lineItem.setInvoiceAssessableAmount(
							invoiceAssessableAmount);
					lineItem.setInvoiceCessSpecificAmount(
							invoiceCessSpecificAmount);
					if (fileStatus != null) {
						errorDocument.setAcceptanceId(fileStatus.getId());
						lineItem.setAcceptanceId(fileStatus.getId());
					} else {
						errorDocument.setAcceptanceId(Long.valueOf(802989));
						lineItem.setAcceptanceId(Long.valueOf(802989));
					}

					/**
					 * Headers into Line Items
					 */
					lineItem.setIrn(irn);
					lineItem.setIrnDate(localIrnDate != null
							? localIrnDate.toString() : irnDate);
					lineItem.setTaxScheme(taxScheme);
					lineItem.setCancellationReason(canReason);
					lineItem.setCancellationRemarks(canRemarks);
					lineItem.setDocCategory(docCategory);
					lineItem.setDocType(docType);
					lineItem.setDocNo(docNo);
					lineItem.setDocDate(localDocDate != null
							? localDocDate.toString() : docDate);
					lineItem.setReverseCharge(reverseChargeFlag);
					lineItem.setSgstin(sgstin);
					lineItem.setSupplierLegalName(supplierLegalName);
					lineItem.setSupplierTradeName(supplierTradeName);
					lineItem.setSupplierBuildingNumber(supAddress1);
					lineItem.setSupplierBuildingName(supAddress2);
					lineItem.setSupplierLocation(supplierLocation);
					lineItem.setSupplierPincode(supplierPincode);
					lineItem.setSupplierStateCode(supplierStateCode);
					lineItem.setSupplierPhone(supplierPhone);
					lineItem.setSupplierEmail(supplierEmail);
					lineItem.setCgstin(customerGstin);
					lineItem.setCustomerTradeName(customerTradeName);
					lineItem.setCustOrSuppName(customerLegalName);
					lineItem.setCustOrSuppAddress1(customerAddress1);
					lineItem.setCustOrSuppAddress2(customerAddress2);
					lineItem.setCustOrSuppAddress4(customerLocation);
					lineItem.setCustomerPincode(customerPincode);
					lineItem.setBillToState(customerStateCode);
					lineItem.setTcsFlag(tcsFlagGST);
					lineItem.setTdsFlag(tdsFlagGST);
					lineItem.setPos(billingPos);
					lineItem.setCustomerPhone(customerPhone);
					lineItem.setCustomerEmail(customerEmail);
					lineItem.setDispatcherGstin(dispatcherGstin);
					lineItem.setDispatcherTradeName(dispatcherTradeName);
					lineItem.setDispatcherBuildingNumber(dispatcherAddress1);
					lineItem.setDispatcherBuildingName(dispatcherAddress2);
					lineItem.setCustomerPANOrAadhaar(customerTanOrAdhar);
					lineItem.setDispatcherLocation(dispatcherLocation);
					lineItem.setDispatcherPincode(dispatcherPinCode);
					lineItem.setDispatcherStateCode(dispatcherStateCode);
					lineItem.setShipToGstin(shipToGstin);
					lineItem.setShipToTradeName(shipToTradeName);
					lineItem.setShipToLegalName(shipToLegalName);
					lineItem.setShipToBuildingNumber(shipToAddress1);
					lineItem.setShipToBuildingName(shipToAddress2);
					lineItem.setShipToLocation(shipToLocation);
					lineItem.setShipToState(shipToStateCode);
					lineItem.setShipToPincode(shipToPincode);
					lineItem.setRoundOff(roundOff);
					lineItem.setTotalInvoiceValueInWords(totalInvValue);
					lineItem.setTcsFlagIncomeTax(tcsFlagIncomeTax);
					lineItem.setForeignCurrency(currencyCode);
					lineItem.setCountryCode(countryCode);
					lineItem.setInvoiceValueFc(invValueFC);
					lineItem.setInvoiceRemarks(invoiceRemarks);
					lineItem.setInvoicePeriodStartDate(
							invoicePeriodStartDate != null
									? invoicePeriodStartDate.toString()
									: localInvoicePeriodStartDate);
					lineItem.setInvoicePeriodEndDate(
							invoicePeriodEndDate != null
									? invoicePeriodEndDate.toString()
									: localinvoicePeriodEndDate);
					lineItem.setPayeeName(payeeName);
					lineItem.setModeOfPayment(modeOfPayment);
					lineItem.setBranchOrIfscCode(branchOrIFSCCode);
					lineItem.setPaymentTerms(paymentTerms);
					lineItem.setPaymentInstruction(paymentInstruction);
					lineItem.setCreditTransfer(creditTransfer);
					lineItem.setDirectDebit(directDebit);
					lineItem.setCreditDays(creditDays);
					lineItem.setPaymentDueDate(paymentDueDate != null
							? paymentDueDate.toString() : localpaymentDueDate);
					lineItem.setAccountDetail(accountDetail);
					lineItem.setEgstin(ecomGSTIN);
					lineItem.setTransactionType(transactionType);
					lineItem.setSubSupplyType(subSupplyType);
					lineItem.setOtherSupplyTypeDescription(
							otherSupplyTypeDescription);
					lineItem.setTransporterID(transporterID);
					lineItem.setTransporterName(transporterName);
					lineItem.setTransportMode(transportMode);
					lineItem.setTransportDocNo(transportDocNumber);
					lineItem.setTransportDocDate(transportDocDate != null
							? transportDocDate.toString()
							: localtransportDocDate);
					lineItem.setDistance(distance);
					lineItem.setVehicleNo(vehicleNumber);
					lineItem.setVehicleType(vehicleType);
					lineItem.setTaxperiod(returnPeriod);

					lineItem.setOrigDocType(originalDocType);
					lineItem.setOrigCgstin(originalCustomerGstin);
					lineItem.setDiffPercent(diffPerFalg);
					lineItem.setSection7OfIgstFlag(sec7ofIgstFlag);
					lineItem.setClaimRefundFlag(claimRefFlag);
					lineItem.setAutoPopToRefundFlag(autoPopulateToRefund);
					lineItem.setIsCrDrPreGst(crDrPreGst);
					lineItem.setCustOrSuppType(customerType);
					lineItem.setCustOrSuppCode(customerCode);
					lineItem.setExchangeRate(exchangerate);
					lineItem.setUserId(userId);
					lineItem.setSourceFileName(sourceFileName);
					lineItem.setSourceIdentifier(sourceIdentifier);
					lineItem.setCompanyCode(companyCode);
					lineItem.setGlPostingDate(glPostingDate != null
							? glPostingDate.toString() : localGlPostingDate);
					lineItem.setSalesOrderNumber(salesOrderNo);
					lineItem.setEWayBillNo(ewbNo);
					lineItem.setEWayBillDate(ewbDate != null
							? ewbDate.toString() : localEwbDate);
					lineItem.setAccountingVoucherNumber(accountingVochar);
					lineItem.setAccountingVoucherDate(
							accountingVocharDate != null
									? accountingVocharDate.toString()
									: localAccountingVocharDate);
					lineItem.setCustomerTan(customerTan);
					lineItem.setInvoiceIgstAmount(invoiceIGSTAmount);
					lineItem.setInvoiceCgstAmount(invoiceCGSTAmount);
					lineItem.setInvoiceSgstAmount(invoiceSGSTAmount);
					lineItem.setInvoiceCessAdvaloremAmount(
							invoiceCessAdvaloremAmount);
					lineItem.setInvoiceStateCessAmount(
							invoiceStateCessAdvaloremAmount);
					lineItem.setEcomTransactionID(ecomTransactionId);
					lineItem.setPortCode(portCode);
					lineItem.setShippingBillNo(shippingBillNo);
					lineItem.setShippingBillDate(shippingBillDate != null
							? shippingBillDate.toString()
							: localShippingBillDate);
					lineItem.setProfitCentre(profitCentre1);
					lineItem.setProfitCentre2(profitCentre2);
					lineItem.setDivision(division);
					lineItem.setLocation(location);
					lineItem.setSalesOrgnization(salesOrg);
					lineItem.setDistributionChannel(distChannel);
					lineItem.setGlCodeIgst(glCodeIgst);
					lineItem.setGlCodeCgst(glCodeCgst);
					lineItem.setGlCodeSgst(glCodeSgst);
					lineItem.setGlCodeAdvCess(glAdvaloremCess);
					lineItem.setGlCodeSpCess(glAdvaloremSpecificCess);
					lineItem.setGlCodeStateCess(glStateCessAdvalore);
					lineItem.setGlStateCessSpecific(glStateCess);
					String source = fileStatus.getSource();
					if (source != null && source.equalsIgnoreCase(
							JobStatusConstants.SFTP_WEB_UPLOAD)) {
						lineItem.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.SFTP
										.getDataOriginTypeCode());
					} else {
						lineItem.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
										.getDataOriginTypeCode());
					}
					lineItem.setCreatedBy(userName);

					lineItem.setIsError("true");
					lineItem.setIsProcessed("false");
					lineItem.setIsDeleted("false");
					lineItem.setFinYear(finYear);

					if (idStr != null) {
						try {
							Long id = Long.parseLong(idStr);
							lineItem.setId(id);
						} catch (RuntimeException e) {
							throw new AppException(
									" Error occured while converting "
											+ "String id to Long id "
											+ e.getMessage());
						}
					}

					if (fileIdStr != null) {
						try {
							Long fileId = Long.parseLong(fileIdStr);
							lineItem.setAcceptanceId(fileId);
						} catch (RuntimeException e) {
							throw new AppException(
									" Error occuured while converting "
											+ "String file id to Long  file id "
											+ e.getMessage());
						}
					}
					int maxlength = 99;
					if (key.length() > maxlength) {
						lineItem.setDocKey(key.substring(0, maxlength));
					} else {
						lineItem.setDocKey(key);
					}
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					lineItem.setCreatedDate(convertNow);
					/*
					 * Headers into Line Items
					 */
					lineItems.add(lineItem); // Add Line Items

					errorDocument.setIrn(irn);
					errorDocument.setIrnDate(localIrnDate != null
							? localIrnDate.toString() : irnDate);
					errorDocument.setTaxScheme(taxScheme);
					errorDocument.setCancellationReason(canReason);
					errorDocument.setCancellationRemarks(canRemarks);
					errorDocument.setDocCategory(docCategory);
					errorDocument.setDocType(docType);
					errorDocument.setDocNo(docNo);
					errorDocument.setDocDate(localDocDate != null
							? localDocDate.toString() : docDate);
					errorDocument.setReverseCharge(reverseChargeFlag);
					errorDocument.setSgstin(sgstin);
					errorDocument.setSupplierLegalName(supplierLegalName);
					errorDocument.setSupplierTradeName(supplierTradeName);
					errorDocument.setSupplierBuildingNumber(supAddress1);
					errorDocument.setSupplierBuildingName(supAddress2);
					errorDocument.setSupplierLocation(supplierLocation);
					errorDocument.setSupplierPincode(supplierPincode);

					errorDocument.setSupplierStateCode(supplierStateCode);
					errorDocument.setSupplierPhone(supplierPhone);
					errorDocument.setSupplierEmail(supplierEmail);
					errorDocument.setCgstin(customerGstin);
					errorDocument.setCustomerTradeName(customerTradeName);
					errorDocument.setCustOrSuppName(customerLegalName);
					errorDocument.setCustOrSuppAddress1(customerAddress1);
					errorDocument.setCustOrSuppAddress2(customerAddress2);
					errorDocument.setCustOrSuppAddress4(customerLocation);
					errorDocument.setCustomerPincode(customerPincode);
					errorDocument.setBillToState(customerStateCode);
					errorDocument.setTcsFlag(tcsFlagGST);
					errorDocument.setTdsFlag(tdsFlagGST);

					errorDocument.setPos(billingPos);
					errorDocument.setCustomerPhone(customerPhone);
					errorDocument.setCustomerEmail(customerEmail);
					errorDocument.setDispatcherGstin(dispatcherGstin);
					errorDocument.setDispatcherTradeName(dispatcherTradeName);
					errorDocument
							.setDispatcherBuildingNumber(dispatcherAddress1);
					errorDocument.setDispatcherBuildingName(dispatcherAddress2);
					errorDocument.setCustomerPANOrAadhaar(customerTanOrAdhar);

					errorDocument.setDispatcherLocation(dispatcherLocation);
					errorDocument.setDispatcherPincode(dispatcherPinCode);

					errorDocument.setDispatcherStateCode(dispatcherStateCode);
					errorDocument.setShipToGstin(shipToGstin);
					errorDocument.setShipToTradeName(shipToTradeName);
					errorDocument.setShipToLegalName(shipToLegalName);
					errorDocument.setShipToBuildingNumber(shipToAddress1);
					errorDocument.setShipToBuildingName(shipToAddress2);
					errorDocument.setShipToLocation(shipToLocation);

					errorDocument.setShipToState(shipToStateCode);
					errorDocument.setShipToPincode(shipToPincode);
					errorDocument.setRoundOff(roundOff);
					errorDocument.setTotalInvoiceValueInWords(totalInvValue);
					errorDocument.setTcsFlagIncomeTax(tcsFlagIncomeTax);
					errorDocument.setForeignCurrency(currencyCode);
					errorDocument.setCountryCode(countryCode);
					errorDocument.setInvoiceValueFc(invValueFC);
					errorDocument.setInvoiceRemarks(invoiceRemarks);
					errorDocument.setInvoicePeriodStartDate(
							invoicePeriodStartDate != null
									? invoicePeriodStartDate.toString()
									: localInvoicePeriodStartDate);
					errorDocument.setInvoicePeriodEndDate(
							invoicePeriodEndDate != null
									? invoicePeriodEndDate.toString()
									: localinvoicePeriodEndDate);
					errorDocument.setPayeeName(payeeName);
					errorDocument.setModeOfPayment(modeOfPayment);
					errorDocument.setBranchOrIfscCode(branchOrIFSCCode);
					errorDocument.setPaymentTerms(paymentTerms);
					errorDocument.setPaymentInstruction(paymentInstruction);
					errorDocument.setCreditTransfer(creditTransfer);
					errorDocument.setDirectDebit(directDebit);
					errorDocument.setCreditDays(creditDays);
					errorDocument.setPaymentDueDate(paymentDueDate != null
							? paymentDueDate.toString() : localpaymentDueDate);
					errorDocument.setAccountDetail(accountDetail);
					errorDocument.setEgstin(ecomGSTIN);
					errorDocument.setTransactionType(transactionType);
					errorDocument.setSubSupplyType(subSupplyType);
					errorDocument.setOtherSupplyTypeDescription(
							otherSupplyTypeDescription);
					errorDocument.setTransporterID(transporterID);
					errorDocument.setTransporterName(transporterName);
					errorDocument.setTransportMode(transportMode);
					errorDocument.setTransportDocNo(transportDocNumber);
					errorDocument.setTransportDocDate(transportDocDate != null
							? transportDocDate.toString()
							: localtransportDocDate);
					errorDocument.setDistance(distance);
					errorDocument.setVehicleNo(vehicleNumber);
					errorDocument.setVehicleType(vehicleType);
					errorDocument.setTaxperiod(returnPeriod);
					errorDocument.setOrigDocType(originalDocType);
					errorDocument.setOrigCgstin(originalCustomerGstin);
					errorDocument.setDiffPercent(diffPerFalg);
					errorDocument.setSection7OfIgstFlag(sec7ofIgstFlag);
					errorDocument.setClaimRefundFlag(claimRefFlag);
					errorDocument.setAutoPopToRefundFlag(autoPopulateToRefund);
					errorDocument.setIsCrDrPreGst(crDrPreGst);
					errorDocument.setCustOrSuppType(customerType);

					errorDocument.setCustOrSuppCode(customerCode);
					errorDocument.setExchangeRate(exchangerate);
					errorDocument.setStateApplyingCess(stateAppCess);
					errorDocument.setUserId(userId);
					errorDocument.setSourceFileName(sourceFileName);
					errorDocument.setSourceIdentifier(sourceIdentifier);
					errorDocument.setCompanyCode(companyCode);
					errorDocument.setGlPostingDate(glPostingDate != null
							? glPostingDate.toString() : localGlPostingDate);
					errorDocument.setSalesOrderNumber(salesOrderNo);
					errorDocument.setEWayBillNo(ewbNo);
					errorDocument.setEWayBillDate(ewbDate != null
							? ewbDate.toString() : localEwbDate);
					errorDocument.setAccountingVoucherNumber(accountingVochar);
					errorDocument.setAccountingVoucherDate(
							accountingVocharDate != null
									? accountingVocharDate.toString()
									: localAccountingVocharDate);
					errorDocument.setCustomerTan(customerTan);
					errorDocument.setInvoiceOtherCharges(invOthCharges);
					errorDocument.setInvoiceCgstAmount(invoiceCGSTAmount);
					errorDocument.setInvoiceSgstAmount(invoiceSGSTAmount);
					errorDocument.setInvoiceCessAdvaloremAmount(
							invoiceCessAdvaloremAmount);
					errorDocument.setInvoiceCessSpecificAmount(
							invoiceCessSpecificAmount);
					errorDocument.setInvoiceStateCessAmount(
							invoiceStateCessAdvaloremAmount);
					errorDocument.setEcomTransactionID(ecomTransactionId);
					errorDocument.setPortCode(portCode);
					errorDocument.setShippingBillNo(shippingBillNo);
					errorDocument.setShippingBillDate(shippingBillDate != null
							? shippingBillDate.toString()
							: localShippingBillDate);
					errorDocument.setInvoiceAssessableAmount(
							invoiceAssessableAmount);
					errorDocument.setInvoiceIgstAmount(invoiceIGSTAmount);
					errorDocument.setProfitCentre(profitCentre1);
					errorDocument.setProfitCentre2(profitCentre2);
					errorDocument.setDivision(division);
					errorDocument.setLocation(location);
					errorDocument.setSalesOrgnization(salesOrg);
					errorDocument.setDistributionChannel(distChannel);
					lineItem.setUserAccess1(userAccess1);
					lineItem.setUserAccess2(userAccess2);
					lineItem.setUserAccess3(userAccess3);
					lineItem.setUserAccess4(userAccess4);
					lineItem.setUserAccess5(userAccess5);
					lineItem.setUserAccess6(userAccess6);
					errorDocument.setGlCodeIgst(glCodeIgst);
					errorDocument.setGlCodeCgst(glCodeCgst);
					errorDocument.setGlCodeSgst(glCodeSgst);
					errorDocument.setGlCodeAdvCess(glAdvaloremCess);
					errorDocument.setGlCodeSpCess(glAdvaloremSpecificCess);
					errorDocument.setGlCodeStateCess(glStateCessAdvalore);
					errorDocument.setGlStateCessSpecific(glStateCess);
					if (source != null && source.equalsIgnoreCase(
							JobStatusConstants.SFTP_WEB_UPLOAD)) {
						errorDocument.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.SFTP
										.getDataOriginTypeCode());
					} else {
						errorDocument.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
										.getDataOriginTypeCode());
					}
					errorDocument.setCreatedBy(userName);

					errorDocument.setIsError("true");
					errorDocument.setIsProcessed("false");
					errorDocument.setIsDeleted("false");
					errorDocument.setFinYear(finYear);
					errorDocument.setUserDefinedField28(userDefinedField28);
					errorDocument.setUserDefinedField29(userDefinedField29);
					errorDocument.setUserDefinedField30(userDefinedField30);

					if (idStr != null) {
						try {
							Long id = Long.parseLong(idStr);
							errorDocument.setId(id);
						} catch (RuntimeException e) {
							throw new AppException(
									" Error occured while converting "
											+ "String id to Long id "
											+ e.getMessage());
						}
					}

					if (fileIdStr != null) {
						try {
							Long fileId = Long.parseLong(fileIdStr);
							errorDocument.setAcceptanceId(fileId);
						} catch (RuntimeException e) {
							throw new AppException(
									" Error occuured while converting "
											+ "String file id to Long  file id "
											+ e.getMessage());
						}
					}
					if (key.length() > maxlength) {
						errorDocument.setDocKey(key.substring(0, maxlength));
					} else {
						errorDocument.setDocKey(key);
					}
				}
				errorDocument.setLineItems(lineItems);
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				errorDocument.setCreatedDate(convertNow);
				errorHeaders.add(errorDocument);

				lineItems.forEach(item -> {
					item.setDocument(errorDocument);
				});
			});

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
		}
		LOGGER.error("SRFileToOutwardTransDocErrConvertion "
				+ "convertSRFileToOutWardTransError Endining");
		return errorHeaders;
	}

	private String getWithValue(Object obj) {
		String value = obj != null && !obj.toString().trim().isEmpty()
				? String.valueOf(obj.toString().trim()) : null;
		return value;
	}
}
