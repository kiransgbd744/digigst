package com.ey.advisory.app.services.docs.gstr2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.Anx2InwardErrorItemEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("ComprehensiveFileToInwardTransDocErrorConvertion")
@Slf4j
public class ComprehensiveFileToInwardTransDocErrorConvertion {

	public List<Anx2InwardErrorHeaderEntity> fileToInwardTransDocErrorCon(
			Map<String, List<Object[]>> errDocMapObj,
			Gstr1FileStatusEntity fileStatus, String userName) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ComprehensiveFileToInwardTransDocErrorConvertion "
					+ "convertPRFileToInWardTransError Begining");
		}

		List<Anx2InwardErrorHeaderEntity> documents = new ArrayList<>();

		errDocMapObj.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();
			Anx2InwardErrorHeaderEntity document = new Anx2InwardErrorHeaderEntity();
			List<Anx2InwardErrorItemEntity> lineItems = new ArrayList<>();
			for (Object[] obj : value) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key - " + key + ", Value - "
							+ Arrays.toString(obj));
				}
				// TO-DO Structural Validations
				Anx2InwardErrorItemEntity lineItem = new Anx2InwardErrorItemEntity();
				String regex = "^[0-9]*$";
				Pattern pattern = Pattern.compile(regex);

				String irn = getWithValue(obj[0]);
				String irnDate = getWithValue(obj[1]);
				LocalDate localIrnDate = DateUtil.parseObjToDate(irnDate);
				String taxSceme = getWithValue(obj[2]);
				String supplyType = getWithValue(obj[3]);
				String docCategory = getWithValue(obj[4]);
				String docType = getWithValue(obj[5]);
				String docNo = getWithCsvValue(obj[6]);
				String docdate = getWithValue(obj[7]);
				LocalDate localDocDate = DateUtil.parseObjToDate(docdate);
				String revCharge = getWithValue(obj[8]);
				String supgstin = getWithValue(obj[9]);
				String supTradeName = getWithValue(obj[10]);
				String supLegalName = getWithValue(obj[11]);
				String supAdd1 = getWithValue(obj[12]);
				String supAdd2 = getWithValue(obj[13]);
				String supLocation = getWithValue(obj[14]);
				String supPinCode = getWithValue(obj[15]);
				String supState = getWithValue(obj[16]);
				if (supState != null && !supState.isEmpty()) {
					Matcher supStateMatcher = pattern.matcher(supState);
					if (supStateMatcher.matches()
							&& supState.trim().length() == 1) {
						supState = "0" + supState;
					}
				}
				String supPhone = getWithValue(obj[17]);
				String supEmail = getWithValue(obj[18]);
				String custGstin = getWithValue(obj[19]);
				String custTradeName = getWithValue(obj[20]);
				String custLegalName = getWithValue(obj[21]);
				String custAdd1 = getWithValue(obj[22]);
				String custadd2 = getWithValue(obj[23]);
				String custLocation = getWithValue(obj[24]);
				String custPinCode = getWithValue(obj[25]);
				String custState = getWithValue(obj[26]);
				if (custState != null && !custState.isEmpty()) {
					Matcher custStateMatcher = pattern.matcher(custState);
					if (custStateMatcher.matches()
							&& custState.trim().length() == 1) {
						custState = "0" + custState;
					}
				}
				String billingPos = getWithCsvValue(obj[27]);
				if (billingPos != null && !billingPos.isEmpty()) {
					Matcher billingPosMatcher = pattern.matcher(billingPos);
					if (billingPosMatcher.matches()
							&& billingPos.length() == 1) {
						billingPos = "0" + billingPos;
					}
				}
				String custPhone = getWithValue(obj[28]);
				String custEmail = getWithValue(obj[29]);
				String disGstin = getWithValue(obj[30]);
				String disTradeName = getWithValue(obj[31]);
				String disAdd1 = getWithValue(obj[32]);
				String disAdd2 = getWithValue(obj[33]);
				String disLocation = getWithValue(obj[34]);
				String disPinCode = getWithValue(obj[35]);
				String disState = getWithValue(obj[36]);
				String shipToGstin = getWithValue(obj[37]);
				String shipToTradeName = getWithValue(obj[38]);
				String shipToLegalName = getWithValue(obj[39]);
				String shipToAdd1 = getWithValue(obj[40]);
				String shipToAdd2 = getWithValue(obj[41]);
				String shipToLocation = getWithValue(obj[42]);
				String shipToPin = getWithValue(obj[43]);
				String shipToState = getWithValue(obj[44]);
				if (shipToState != null && !shipToState.isEmpty()) {
					Matcher shipToStateMatcher = pattern.matcher(shipToState);
					if (shipToStateMatcher.matches()
							&& shipToState.trim().length() == 1) {
						shipToState = "0" + shipToState;
					}
				}
				String itemSerNum = getWithValue(obj[45]);
				String proSerNum = getWithValue(obj[46]);
				String prodName = getWithValue(obj[47]);
				String proddes = getWithValue(obj[48]);
				String isService = getWithValue(obj[49]);
				String hsn = getWithCsvValue(obj[50]);
				String barCode = getWithValue(obj[51]);
				String batchName = getWithValue(obj[52]);
				String batchExpiryDate = getWithValue(obj[53]);
				LocalDate localBatchExpDate = DateUtil
						.parseObjToDate(batchExpiryDate);
				String warrentyDate = getWithValue(obj[54]);
				LocalDate localWarrentyDate = DateUtil
						.parseObjToDate(warrentyDate);
				String ordLineRef = getWithValue(obj[55]);
				String attName = getWithValue(obj[56]);
				String attValue = getWithValue(obj[57]);
				String originCountry = getWithValue(obj[58]);
				String uqc = getWithValue(obj[59]);
				String qnt = getWithValue(obj[60]);
				String freeQnt = getWithValue(obj[61]);
				String unitPrice = getWithValue(obj[62]);
				String itemAmt = getWithValue(obj[63]);
				String itemDiscount = getWithValue(obj[64]);
				String preTaxAmt = getWithValue(obj[65]);
				String itemAssessableAmt = getWithValue(obj[66]);
				String igstRate = getWithValue(obj[67]);
				String igstAmt = getWithValue(obj[68]);
				String cgstRate = getWithValue(obj[69]);
				String cgstAmt = getWithValue(obj[70]);
				String sgstRate = getWithValue(obj[71]);
				String sgstAmt = getWithValue(obj[72]);
				String cessAdvRate = getWithValue(obj[73]);
				String cessAdvAmt = getWithValue(obj[74]);
				String cessSpeRate = getWithValue(obj[75]);
				String cessSpeAmt = getWithValue(obj[76]);
				String stateCessAdvRate = getWithValue(obj[77]);
				String stateCessAdvAmt = getWithValue(obj[78]);
				String stateCessSpeRate = getWithValue(obj[79]);
				String stateCessSpeAmt = getWithValue(obj[80]);
				String itemOthCharges = getWithValue(obj[81]);
				String totalItemAmt = getWithValue(obj[82]);
				String invOtherCharge = getWithValue(obj[83]);
				String invAssessableAmt = getWithValue(obj[84]);
				String invIgstAmt = getWithValue(obj[85]);
				String invCgstAmt = getWithValue(obj[86]);
				String invSgstAmt = getWithValue(obj[87]);
				String invCessAdvAmt = getWithValue(obj[88]);
				String invCessSpeAmt = getWithValue(obj[89]);
				String invStateCessAdvAmt = getWithValue(obj[90]);
				String invStateCessSpeAmt = getWithValue(obj[91]);
				String invValue = getWithValue(obj[92]);
				String roundOff = getWithValue(obj[93]);
				String totInvValueInWards = getWithValue(obj[94]);
				String eligibilityIndi = getWithValue(obj[95]);
				String commonSupIndi = getWithValue(obj[96]);
				String avialbleIgst = getWithValue(obj[97]);
				String avialbleCgst = getWithValue(obj[98]);
				String avialbleSgst = getWithValue(obj[99]);
				String avialbleCess = getWithValue(obj[100]);
				String itcEntilement = getWithValue(obj[101]);
				String itcReversalId = getWithValue(obj[102]);
				String tcsFlagIncomTax = getWithValue(obj[103]);
				String tcsRateIncomTax = getWithValue(obj[104]);
				String tcsAmtIncomTax = getWithValue(obj[105]);
				String currencyCode = getWithValue(obj[106]);
				String countryCode = getWithValue(obj[107]);
				String invValueFc = getWithValue(obj[108]);
				String portCode = getWithValue(obj[109]);
				String billOfEntry = getWithValue(obj[110]);
				String billOfEntryDate = getWithValue(obj[111]);
				LocalDate localBillOfEntryDate = DateUtil
						.parseObjToDate(billOfEntryDate);
				String invRemarks = getWithValue(obj[112]);
				String invPeriodStartDate = getWithValue(obj[113]);
				LocalDate localInvPeriodStartDate = DateUtil
						.parseObjToDate(invPeriodStartDate);
				String invPeriodEndDate = getWithValue(obj[114]);
				LocalDate localInvPeriodEndDate = DateUtil
						.parseObjToDate(invPeriodEndDate);
				String preceedingInvNo = getWithCsvValue(obj[115]);// original
																	// Doc
																	// Number
				String preceedingInvDate = getWithValue(obj[116]);
				LocalDate localPreceedingInvDate = DateUtil
						.parseObjToDate(preceedingInvDate);
				String othRef = getWithValue(obj[117]);
				String receipientAdvRef = getWithValue(obj[118]);
				String receipientAdvDate = getWithValue(obj[119]);
				LocalDate localReceipientAdvDate = DateUtil
						.parseObjToDate(receipientAdvDate);
				String tenderRef = getWithValue(obj[120]);
				String contractRef = getWithValue(obj[121]);
				String externalRef = getWithValue(obj[122]);
				String projectRef = getWithValue(obj[123]);
				String custPoRefNo = getWithValue(obj[124]);
				String custPoRefDate = getWithValue(obj[125]);
				LocalDate localCustOoRefDate = DateUtil
						.parseObjToDate(custPoRefDate);
				String payeeName = getWithValue(obj[126]);
				String modeOfPayment = getWithValue(obj[127]);
				String branchOrIfscCode = getWithValue(obj[128]);
				String paymentTerms = getWithValue(obj[129]);
				String paymentInstructions = getWithValue(obj[130]);
				String creditTransfer = getWithValue(obj[131]);
				String derectDebit = getWithValue(obj[132]);
				String creditDays = getWithValue(obj[133]);
				String paidAmt = getWithValue(obj[134]);
				String balanceAmt = getWithValue(obj[135]);
				String paymentDueDate = getWithValue(obj[136]);
				LocalDate localPaymentDueDate = DateUtil
						.parseObjToDate(paymentDueDate);
				String accDetails = getWithValue(obj[137]);
				String ecomGstin = getWithValue(obj[138]);
				String suppDocUrl = getWithValue(obj[139]);
				String suppDocument = getWithValue(obj[140]);
				String additionalInfo = getWithValue(obj[141]);
				String transtype = getWithValue(obj[142]);
				String subSuppType = getWithValue(obj[143]);
				String othSupTypeDes = getWithValue(obj[144]);
				String transporterId = getWithValue(obj[145]);
				String transporterName = getWithValue(obj[146]);
				String transportMode = getWithValue(obj[147]);
				String transPortDocNo = getWithValue(obj[148]);
				String transPortDocDate = getWithValue(obj[149]);
				LocalDate localTransPortDocDate = DateUtil
						.parseObjToDate(transPortDocDate);
				String distance = getWithValue(obj[150]);
				String vehicleNo = getWithValue(obj[151]);
				String vehicleType = getWithValue(obj[152]);
				String retPeriod = getWithCsvValue(obj[153]);
				String orgDocType = getWithValue(obj[154]);
				String orgSupGstin = getWithValue(obj[155]);
				String diffPerFlag = getWithValue(obj[156]);
				String sec7OfIgstFlag = getWithValue(obj[157]);
				String claimRefundFlag = getWithValue(obj[158]);
				String autoPopRefund = getWithValue(obj[159]);
				String crdrPreGst = getWithValue(obj[160]);
				String supplierType = getWithValue(obj[161]);
				String suppCode = getWithValue(obj[162]);
				String productCode = getWithValue(obj[163]);
				String categoryOfProduct = getWithValue(obj[164]);
				String stateAppCess = getWithCsvValue(obj[165]);
				String cIF = getWithValue(obj[166]);
				String customDuty = getWithValue(obj[167]);
				String exchangeRate = getWithValue(obj[168]);
				String reasonForCrDrNote = getWithValue(obj[169]);
				String tcsFlagGst = getWithValue(obj[170]);
				String tcsIgstAmt = getWithValue(obj[171]);
				String tcsCgstAmt = getWithValue(obj[172]);
				String tcsSgstAmt = getWithValue(obj[173]);
				String tdsFlagGst = getWithValue(obj[174]);
				String tdsIgstAmt = getWithValue(obj[175]);
				String tdsCgstAmt = getWithValue(obj[176]);
				String tdsSgstAmt = getWithValue(obj[177]);
				String userId = getWithValue(obj[178]);
				String companyCode = getWithValue(obj[179]);
				String sourceId = getWithValue(obj[180]);
				String sourceFileName = getWithValue(obj[181]);
				String plantCode = getWithValue(obj[182]);
				String division = getWithValue(obj[183]);
				String subDivision = getWithValue(obj[184]);
				String location = getWithValue(obj[185]);
				String purchageOrg = getWithValue(obj[186]);
				String profitCenter1 = getWithValue(obj[187]);
				String profitCenter2 = getWithValue(obj[188]);
				String profitCenter3 = getWithValue(obj[189]);
				String profitCenter4 = getWithValue(obj[190]);
				String profitCenter5 = getWithValue(obj[191]);
				String profitCenter6 = getWithValue(obj[192]);
				String profitCenter7 = getWithValue(obj[193]);
				String profitCenter8 = getWithValue(obj[194]);
				String glAssValue = getWithValue(obj[195]);
				String glIgst = getWithValue(obj[196]);
				String glCgst = getWithValue(obj[197]);
				String glSgst = getWithValue(obj[198]);
				String glAdvCess = getWithValue(obj[199]);
				String glSpeCess = getWithValue(obj[200]);
				String glStateCessAdv = getWithValue(obj[201]);
				String glStateSpeCess = getWithValue(obj[202]);
				String glPostingDate = getWithValue(obj[203]);
				LocalDate localGlPostingDate = DateUtil
						.parseObjToDate(glPostingDate);
				String purchageOrderValue = getWithValue(obj[204]);
				String ewbNo = getWithValue(obj[205]);
				String ewbDate = getWithValue(obj[206]);
				LocalDate localEwbDate = DateUtil.parseObjToDate(ewbDate);
				String accountingVoucharNo = getWithValue(obj[207]);
				String accountingVoucharDate = getWithValue(obj[208]);
				LocalDate localAccountingVoucharDate = DateUtil
						.parseObjToDate(accountingVoucharDate);
				String docRefNo = getWithValue(obj[209]);
				String userDef1 = getWithValue(obj[210]);
				String userDef2 = getWithValue(obj[211]);
				String userDef3 = getWithValue(obj[212]);
				String userDef4 = getWithValue(obj[213]);
				String userDef5 = getWithValue(obj[214]);
				String userDef6 = getWithValue(obj[215]);
				String userDef7 = getWithValue(obj[216]);
				String userDef8 = getWithValue(obj[217]);
				String userDef9 = getWithValue(obj[218]);
				String userDef10 = getWithValue(obj[219]);
				String userDef11 = getWithValue(obj[220]);
				String userDef12 = getWithValue(obj[221]);
				String userDef13 = getWithValue(obj[222]);
				String userDef14 = getWithValue(obj[223]);
				String userDef15 = getWithValue(obj[224]);
				String userDef16 = getWithValue(obj[225]);
				String userDef17 = getWithValue(obj[226]);
				String userDef18 = getWithValue(obj[227]);
				String userDef19 = getWithValue(obj[228]);
				String userDef20 = getWithValue(obj[229]);
				String userDef21 = getWithValue(obj[230]);
				String userDef22 = getWithValue(obj[231]);
				String userDef23 = getWithValue(obj[232]);
				String userDef24 = getWithValue(obj[233]);
				String userDef25 = getWithValue(obj[234]);
				String userDef26 = getWithValue(obj[235]);
				String userDef27 = getWithValue(obj[236]);
				String userDef28 = getWithValue(obj[237]);
				String userDef29 = getWithValue(obj[238]);
				String userDef30 = getWithValue(obj[239]);

				/**
				 * Start - Inward Document Structural Validation Error
				 * Correction Implementation
				 */
				// File ID and Id are not null only in case of Inward Doc
				// Structural Validation Error Correction
				String idStr = null;
				String fileIdStr = null;
				if (obj.length > 240) {
					idStr = (obj[240] != null && !obj[240].toString().isEmpty())
							? String.valueOf(obj[240]) : null;
					fileIdStr = (obj[241] != null
							&& !obj[241].toString().isEmpty())
									? String.valueOf(obj[241]) : null;
				}
				/**
				 * End - Inward Document Structural Validation Error Correction
				 * Implementation
				 */

				// Line Items

				lineItem.setIrn(irn);
				if (localIrnDate != null
						&& !localIrnDate.toString().isEmpty()) {
					lineItem.setIrnDate(String.valueOf(localIrnDate));
				} else {
					lineItem.setIrnDate(irnDate);
				}
				lineItem.setTaxScheme(taxSceme);
				lineItem.setSupplyType(supplyType);
				lineItem.setDocCategory(docCategory);
				lineItem.setDocType(docType);
				lineItem.setDocNo(docNo);
				if (localDocDate != null) {
					lineItem.setDocDate(String.valueOf(localDocDate));
				} else {
					lineItem.setDocDate(docdate);
				}
				lineItem.setReverseCharge(revCharge);
				lineItem.setSgstin(supgstin);
				lineItem.setSupplierTradeName(supTradeName);
				lineItem.setSupplierLegalName(supLegalName);
				lineItem.setCustOrSuppAddress1(supAdd1);
				lineItem.setCustOrSuppAddress2(supAdd2);
				lineItem.setCustOrSuppAddress3(supLocation);
				lineItem.setCustOrSuppAddress4(supPinCode);
				lineItem.setSupplierStateCode(supState);
				lineItem.setSupplierPhone(supPhone);
				lineItem.setSupplierEmail(supEmail);
				lineItem.setCgstin(custGstin);
				lineItem.setCustomerTradeName(custTradeName);
				lineItem.setCustBuidIngNo(custAdd1);
				lineItem.setCustBuidIngName(custadd2);
				lineItem.setCustomerLegalName(custLegalName);
				lineItem.setSupplierLocation(custLocation);
				lineItem.setCustomerPincode(custPinCode);
				lineItem.setBillToState(custState);
				lineItem.setPos(billingPos);
				lineItem.setCustomerPhone(custPhone);
				lineItem.setCustomerEmail(custEmail);
				lineItem.setDispatcherGstin(disGstin);
				lineItem.setDispatcherTradeName(disTradeName);
				lineItem.setDispatcherBuildingNumber(disAdd1);
				lineItem.setDispatcherBuildingName(disAdd2);
				lineItem.setDispatcherLocation(disLocation);
				lineItem.setDispatcherPincode(disPinCode);
				lineItem.setDispatcherStateCode(disState);
				lineItem.setShipToGstin(shipToGstin);
				lineItem.setShipToTradeName(shipToTradeName);
				lineItem.setShipToLegalName(shipToLegalName);
				lineItem.setShipToBuildingNumber(shipToAdd1);
				lineItem.setShipToBuildingName(shipToAdd2);
				lineItem.setShipToLocation(shipToLocation);
				lineItem.setShipToPincode(shipToPin);
				lineItem.setShipToState(shipToState);
				lineItem.setLineNo(itemSerNum);
				lineItem.setSNumber2(proSerNum);
				lineItem.setProductName(prodName);
				lineItem.setItemDescription(proddes);
				lineItem.setIsService(isService);
				lineItem.setHsnSac(hsn);
				lineItem.setBarcode(barCode);
				lineItem.setBatchNameOrNumber(batchName);
				if (localBatchExpDate != null) {
					lineItem.setBatchExpiryDate(
							String.valueOf(localBatchExpDate));
				} else {
					lineItem.setBatchExpiryDate(batchExpiryDate);
				}
				if (localWarrentyDate != null) {
					lineItem.setWarrantyDate(String.valueOf(localWarrentyDate));
				} else {
					lineItem.setWarrantyDate(warrentyDate);
				}
				lineItem.setOrderItemRef(ordLineRef);
				lineItem.setAttributeName(attName);
				lineItem.setAttributeValue(attValue);
				lineItem.setOriginCountry(originCountry);
				lineItem.setUom(uqc);
				lineItem.setQty(qnt);
				lineItem.setFreeQuantity(freeQnt);
				lineItem.setUnitPrice(unitPrice);
				lineItem.setItemAmount(itemAmt);
				lineItem.setItemDiscount(itemDiscount);
				lineItem.setPreTaxAmount(preTaxAmt);
				lineItem.setTaxableValue(itemAssessableAmt);
				lineItem.setIgstRate(igstRate);
				lineItem.setIgstAmount(igstAmt);
				lineItem.setCgstRate(cgstRate);
				lineItem.setCgstAmount(cgstAmt);
				lineItem.setSgstRate(sgstRate);
				lineItem.setSourceIdentifier(sourceId);
				lineItem.setSgstAmount(sgstAmt);
				lineItem.setCessRateAdvalorem(cessAdvRate);
				lineItem.setCessAmountAdvalorem(cessAdvAmt);
				lineItem.setCessRateSpecific(cessSpeRate);
				lineItem.setCessAmountSpecific(cessSpeAmt);
				lineItem.setStateCessRate(stateCessAdvRate);
				lineItem.setStateCessAmount(stateCessAdvAmt);
				lineItem.setStateCessSpeRate(stateCessSpeRate);
				lineItem.setStateCessSpeAmt(stateCessSpeAmt);
				lineItem.setOtherValues(itemOthCharges);
				lineItem.setTotalItemAmount(totalItemAmt);
				lineItem.setInvoiceOtherCharges(invOtherCharge);
				lineItem.setInvoiceAssessableAmount(invAssessableAmt);
				lineItem.setInvoiceIgstAmount(invIgstAmt);
				lineItem.setInvoiceCgstAmount(invCgstAmt);
				lineItem.setInvoiceSgstAmount(invSgstAmt);
				lineItem.setInvoiceCessAdvaloremAmount(invCessAdvAmt);
				lineItem.setInvoiceCessSpecificAmount(invCessSpeAmt);
				lineItem.setInvoiceStateCessAmount(invStateCessAdvAmt);
				lineItem.setInvoiceStateSpecifAmount(invStateCessSpeAmt);
				lineItem.setLineItemAmt(invValue);
				lineItem.setRoundOff(roundOff);
				lineItem.setTotalInvoiceValueInWords(totInvValueInWards);
				lineItem.setEligibilityIndicator(eligibilityIndi);
				lineItem.setCommonSupplyIndicator(commonSupIndi);
				lineItem.setAvailableIgst(avialbleIgst);
				lineItem.setAvailableCgst(avialbleCgst);
				lineItem.setAvailableSgst(avialbleSgst);
				lineItem.setAvailableCess(avialbleCess);
				lineItem.setItcEntitlement(itcEntilement);
				lineItem.setItcReversalIdentifier(itcReversalId);
				lineItem.setTcsFlagIncomeTax(tcsFlagIncomTax);
				lineItem.setTcsAmtIncomeTax(tcsAmtIncomTax);
				lineItem.setTcsRateIncomeTax(tcsRateIncomTax);
				lineItem.setForeignCurrency(currencyCode);
				lineItem.setCountryCode(countryCode);
				lineItem.setInvoiceValueFc(invValueFc);
				lineItem.setPortCode(portCode);
				lineItem.setBillOfEntryNo(billOfEntry);
				if (localBillOfEntryDate != null) {
					lineItem.setBillOfEntryDate(
							String.valueOf(localBillOfEntryDate));
				} else {
					lineItem.setBillOfEntryDate(billOfEntryDate);
				}
				lineItem.setInvoiceRemarks(invRemarks);

				if (localInvPeriodStartDate != null) {
					lineItem.setInvoicePeriodStartDate(
							String.valueOf(localInvPeriodStartDate));
				} else {
					lineItem.setInvoicePeriodStartDate(invPeriodStartDate);
				}
				if (localInvPeriodEndDate != null) {
					lineItem.setInvoicePeriodEndDate(
							String.valueOf(localInvPeriodEndDate));
				} else {
					lineItem.setInvoicePeriodEndDate(invPeriodStartDate);
				}
				lineItem.setOrigDocNo(preceedingInvNo);
				if (localPreceedingInvDate != null) {
					lineItem.setOrigDocDate(
							String.valueOf(localPreceedingInvDate));
				} else {
					lineItem.setOrigDocDate(preceedingInvDate);
				}
				lineItem.setOthRef(othRef);
				lineItem.setReceiptAdvRef(receipientAdvRef);
				if (localReceipientAdvDate != null) {
					lineItem.setReceiptAdvDate(
							String.valueOf(localReceipientAdvDate));
				} else {
					lineItem.setReceiptAdvDate(receipientAdvDate);
				}
				lineItem.setTenderRef(tenderRef);
				lineItem.setExternalReference(externalRef);
				lineItem.setContractReference(contractRef);
				lineItem.setProjectReference(projectRef);
				lineItem.setContractNumber(custPoRefNo);

				if (localCustOoRefDate != null) {
					lineItem.setContractDate(
							String.valueOf(localCustOoRefDate));
				} else {
					lineItem.setContractDate(custPoRefDate);
				}
				lineItem.setPayeeName(payeeName);
				lineItem.setModeOfPayment(
						modeOfPayment != null && !modeOfPayment.isEmpty()
								? modeOfPayment.toUpperCase() : modeOfPayment);
				lineItem.setBranchOrIfscCode(branchOrIfscCode);
				lineItem.setPaymentTerms(paymentTerms);
				lineItem.setPaymentInstruction(paymentInstructions);
				lineItem.setCreditTransfer(creditTransfer);
				lineItem.setDirectDebit(derectDebit);
				lineItem.setCreditDays(creditDays);
				lineItem.setPaidAmount(paidAmt);
				lineItem.setBalanceAmount(balanceAmt);
				if (localPaymentDueDate != null) {
					lineItem.setPaymentDueDate(
							String.valueOf(localPaymentDueDate));
				} else {
					lineItem.setPaymentDueDate(paymentDueDate);
				}

				lineItem.setAccountDetail(accDetails);
				lineItem.setEgstin(ecomGstin);
				lineItem.setSupportingDocURL(suppDocUrl);
				lineItem.setSupportingDoc(suppDocument);
				lineItem.setAdditionalInformation(additionalInfo);
				lineItem.setTransactionType(transtype);
				lineItem.setSubSupplyType(
						subSuppType != null && !subSuppType.isEmpty()
								? subSuppType.toUpperCase() : subSuppType);
				lineItem.setOtherSupplyTypeDescription(othSupTypeDes);
				lineItem.setTransporterID(transporterId);
				lineItem.setTransporterName(transporterName);
				lineItem.setTransportMode(
						transportMode != null && !transportMode.isEmpty()
								? transportMode.toUpperCase() : transportMode);
				lineItem.setTransportDocNo(transPortDocNo);
				if (localTransPortDocDate != null) {
					lineItem.setTransportDocDate(
							String.valueOf(localTransPortDocDate));
				} else {
					lineItem.setTransportDocDate(transPortDocDate);
				}
				lineItem.setDistance(distance);
				lineItem.setVehicleNo(vehicleNo);
				lineItem.setVehicleType(vehicleType);
				lineItem.setTaxperiod(retPeriod);
				lineItem.setOrgDocType(orgDocType);
				lineItem.setOrigSgstin(orgSupGstin);
				lineItem.setDiffPercent(diffPerFlag);
				lineItem.setSection7OfIgstFlag(sec7OfIgstFlag);
				lineItem.setClaimRefundFlag(claimRefundFlag);
				lineItem.setAutoPopToRefundFlag(autoPopRefund);
				lineItem.setCrDrPreGst(crdrPreGst);
				lineItem.setSupplierType(supplierType);
				lineItem.setCustOrSuppCode(suppCode);
				lineItem.setItemCode(productCode);
				lineItem.setItemCategory(categoryOfProduct);
				lineItem.setStateApplyingCess(stateAppCess);
				lineItem.setCifValue(cIF);
				lineItem.setCustomDuty(customDuty);
				lineItem.setExchangeRate(exchangeRate);
				lineItem.setCrDrReason(reasonForCrDrNote);
				lineItem.setTcsFlag(tcsFlagGst);
				lineItem.setTcsIgstAmount(tcsIgstAmt);
				lineItem.setTcsCgstAmount(tcsCgstAmt);
				lineItem.setTcsSgstAmount(tcsSgstAmt);
				lineItem.setTdsFlag(tdsFlagGst);
				lineItem.setTdsIgstAmount(tdsIgstAmt);
				lineItem.setTdsSgstAmount(tdsSgstAmt);
				lineItem.setTdsCgstAmount(tdsCgstAmt);
				lineItem.setUserId(userId);
				lineItem.setCompanyCode(companyCode);
				lineItem.setSourceFileName(sourceFileName);
				lineItem.setPlantCode(plantCode);
				lineItem.setDivision(division);
				lineItem.setLocation(location);
				lineItem.setSubDivision(subDivision);
				lineItem.setPurchaseOrganization(purchageOrg);
				lineItem.setProfitCentre(profitCenter1);
				lineItem.setProfitCentre2(profitCenter2);
				lineItem.setUserAccess1(profitCenter3);
				lineItem.setUserAccess2(profitCenter4);
				lineItem.setUserAccess3(profitCenter5);
				lineItem.setUserAccess4(profitCenter6);
				lineItem.setUserAccess5(profitCenter7);
				lineItem.setUserAccess6(profitCenter8);
				lineItem.setGlCodeTaxableValue(glAssValue);
				lineItem.setGlCodeIgst(glIgst);
				lineItem.setGlCodeCgst(glCgst);
				lineItem.setGlCodeSgst(glSgst);
				lineItem.setGlCodeAdvCess(glAdvCess);
				lineItem.setGlCodeSpCess(glSpeCess);
				lineItem.setGlCodeStateCess(glStateCessAdv);
				lineItem.setGlCodeStateSpCess(glStateSpeCess);
				if (localGlPostingDate != null) {
					lineItem.setPostingDate(String.valueOf(localGlPostingDate));
				} else {
					lineItem.setPostingDate(glPostingDate);
				}
				lineItem.setContractValue(purchageOrderValue);
				lineItem.setEWayBillNo(ewbNo);
				if (localEwbDate != null) {
					lineItem.setEWayBillDate(String.valueOf(localEwbDate));
				} else {
					lineItem.setEWayBillDate(ewbDate);
				}
				lineItem.setPurchaseVoucherNum(accountingVoucharNo);
				if (localAccountingVoucharDate != null) {
					lineItem.setPurchaseVoucherDate(
							String.valueOf(localAccountingVoucharDate));
				} else {
					lineItem.setPurchaseVoucherDate(accountingVoucharDate);
				}
				lineItem.setDocRefNumber(docRefNo);

				lineItem.setUserDefinedField1(userDef1);
				lineItem.setUserDefinedField2(userDef2);
				lineItem.setUserDefinedField3(userDef3);
				lineItem.setUserDefinedField4(userDef4);
				lineItem.setUserDefinedField5(userDef5);
				lineItem.setUserDefinedField6(userDef6);
				lineItem.setUserDefinedField7(userDef7);
				lineItem.setUserDefinedField8(userDef8);
				lineItem.setUserDefinedField9(userDef9);
				lineItem.setUserDefinedField10(userDef10);
				lineItem.setUserDefinedField11(userDef11);
				lineItem.setUserDefinedField12(userDef12);
				lineItem.setUserDefinedField13(userDef13);
				lineItem.setUserDefinedField14(userDef14);
				lineItem.setUserDefinedField15(userDef15);
				lineItem.setUserDefinedField16(userDef16);
				lineItem.setUserDefinedField17(userDef17);
				lineItem.setUserDefinedField18(userDef18);
				lineItem.setUserDefinedField19(userDef19);
				lineItem.setUserDefinedField20(userDef20);
				lineItem.setUserDefinedField21(userDef21);
				lineItem.setUserDefinedField22(userDef22);
				lineItem.setUserDefinedField23(userDef23);
				lineItem.setUserDefinedField24(userDef24);
				lineItem.setUserDefinedField25(userDef25);
				lineItem.setUserDefinedField26(userDef26);
				lineItem.setUserDefinedField27(userDef27);
				lineItem.setUserDefinedField28(userDef28);
				lineItem.setUserDefinedField29(userDef29);
				lineItem.setUserDefinedField30(userDef30);

				lineItem.setIsError("true");
				lineItem.setIsProcessed("false");
				lineItem.setIsDeleted("false");

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
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Key " + key);
				}
				int maxlength = 99;
				if (key.length() > maxlength) {
					lineItem.setDocKey(key.substring(0, maxlength));
				} else {
					lineItem.setDocKey(key);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Key " + key);
					LOGGER.debug("Anx2RawFileToInwardTransDocErrConvertion "
							+ "convertAnx2RawFileToInWardTransError With "
							+ "in for end");
				}
				// lineItem.setLineItems(lineItems);
				lineItem.setCreatedBy(userName);
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				// lineItem.setCreatedDate(convertNow);

				/* Headers into Item */
				lineItems.add(lineItem); // Add Line Items

				if (fileStatus != null) {
					document.setAcceptanceId(fileStatus.getId());
					lineItem.setAcceptanceId(fileStatus.getId());
				}
				Integer derivedRetPeriod = GenUtil
						.convertTaxPeriodToInt(retPeriod);
				if (derivedRetPeriod != null) {
					lineItem.setDerivedTaxperiod(derivedRetPeriod);
				} else {
					lineItem.setDerivedTaxperiod(999999);
				}

				document.setIrn(irn);
				if (localIrnDate != null
						&& !localIrnDate.toString().isEmpty()) {
					document.setIrnDate(String.valueOf(localIrnDate));
				} else {
					document.setIrnDate(irnDate);
				}
				// document.setSupplyType(supplyType);
				document.setDocType(docType);
				document.setDocNo(docNo);
				if (localDocDate != null) {
					document.setDocDate(String.valueOf(localDocDate));
				} else {
					document.setDocDate(docdate);
				}
				document.setReverseCharge(revCharge);
				document.setSgstin(supgstin);
				// document.setSupplierTradeName(supTradeName);
				/*
				 * document.setCustOrSuppAddress1(supAdd1);
				 * document.setCustOrSuppAddress2(supAdd2);
				 * document.setCustOrSuppAddress3(supLocation);
				 * document.setCustOrSuppAddress4(supPinCode);
				 */
				document.setCgstin(custGstin);
				document.setPos(billingPos);
				Predicate<String> p = i -> i != null && !i.isEmpty();
				if (p.test(billingPos)) {
					Matcher billingPosMatcher = pattern.matcher(billingPos);
					if (billingPosMatcher.matches()
							&& billingPos.length() == 1) {
						billingPos = "0" + billingPos;
					}
				}
				document.setDispatcherGstin(disGstin);
				document.setShipToGstin(shipToGstin);
				/*
				 * document.setTaxableValue(itemAssessableAmt);
				 * document.setIgstAmount(igstAmt);
				 * document.setCgstAmount(cgstAmt);
				 * document.setSgstAmount(sgstAmt);
				 * document.setCessAmountAdvalorem(cessAdvAmt);
				 * document.setCessAmountSpecific(cessSpeAmt);
				 * document.setStateCessAmount(stateCessAdvAmt);
				 * document.setStateCessSpeAmt(stateCessSpeAmt);
				 * document.setOtherValues(itemOthCharges);
				 * document.setAvailableIgst(avialbleIgst);
				 * document.setAvailableCgst(avialbleCgst);
				 * document.setAvailableSgst(avialbleSgst);
				 * document.setAvailableCess(avialbleCess);
				 */
				document.setSupplierLegalName(supLegalName);
				document.setItcEntitlement(itcEntilement);
				// document.setItcReversalIdentifier(itcReversalId);
				document.setPortCode(portCode);
				document.setBillOfEntryNo(billOfEntry);
				if (localBillOfEntryDate != null) {
					document.setBillOfEntryDate(
							String.valueOf(localBillOfEntryDate));
				} else {
					document.setBillOfEntryDate(billOfEntryDate);
				}
				/*
				 * document.setOrigDocNo(preceedingInvNo); if
				 * (localPreceedingInvDate != null) { document.setOrigDocDate(
				 * String.valueOf(localPreceedingInvDate)); } else {
				 * document.setOrigDocDate(preceedingInvDate); }
				 */
				if (localPaymentDueDate != null) {
					document.setPaymentDueDate(
							String.valueOf(localPaymentDueDate));
				} else {
					document.setPaymentDueDate(paymentDueDate);
				}
				document.setEgstin(ecomGstin);
				document.setTaxperiod(retPeriod);
				document.setOrigSgstin(orgSupGstin);
				document.setDiffPercent(diffPerFlag);
				document.setSection7OfIgstFlag(sec7OfIgstFlag);
				document.setSupplierType(supplierType);

				/*
				 * document.setClaimRefundFlag(claimRefundFlag);
				 * document.setAutoPopToRefundFlag(autoPopRefund);
				 * document.setCrDrPreGst(crdrPreGst);
				 * document.setSupplyType(supplyType);
				 * document.setCustOrSuppCode(suppCode);
				 * document.setStateApplyingCess(stateAppCess);
				 */
				document.setTcsFlag(tcsFlagGst);
				document.setTdsFlag(tdsFlagGst);
				document.setUserId(userId);
				document.setSourceFileName(sourceFileName);
				document.setProfitCentre(profitCenter1);
				// document.setPlantCode(plantCode);
				document.setDivision(division);
				document.setLocation(location);
				document.setPurchaseOrganization(purchageOrg);
				// document.setUserAccess1(profitCenter2);
				/*
				 * document.setUserAccess2(profitCenter3);
				 * document.setUserAccess3(profitCenter4);
				 * document.setUserAccess4(profitCenter5);
				 * document.setUserAccess5(profitCenter6);
				 * document.setUserAccess6(profitCenter7);
				 */
				if (localGlPostingDate != null) {
					document.setPostingDate(String.valueOf(localGlPostingDate));
				} else {
					document.setPostingDate(glPostingDate);
				}
				document.setEWayBillNo(ewbNo);
				if (localEwbDate != null) {
					document.setEWayBillDate(String.valueOf(localEwbDate));
				} else {
					document.setEWayBillDate(ewbDate);
				}
				// document.setPurchaseVoucherNum(accountingVoucharNo);
				if (localAccountingVoucharDate != null) {
					document.setPurchaseVoucherDate(
							String.valueOf(localAccountingVoucharDate));
				} else {
					document.setPurchaseVoucherDate(accountingVoucharDate);
				}
				document.setInvoiceOtherCharges(invOtherCharge);
				document.setInvoiceAssessableAmount(invAssessableAmt);
				document.setInvoiceIgstAmount(invIgstAmt);
				document.setInvoiceCgstAmount(invCgstAmt);
				document.setInvoiceSgstAmount(invSgstAmt);
				document.setInvoiceCessAdvaloremAmount(invCessAdvAmt);
				document.setInvoiceCessSpecificAmount(invCessSpeAmt);
				document.setInvoiceStateCessAmount(invStateCessAdvAmt);
				document.setInvoiceStateSpecifAmount(invStateCessSpeAmt);
				// document.setInvoiceValue(invValue);
				document.setPortCode(portCode);

				document.setDivision(division);
				document.setLocation(location);
				document.setPurchaseOrganization(purchageOrg);
				// document.setProfitCentre(profitCenter1);
				// document.setUserAccess1(profitCenter2);
				/*
				 * document.setUserdefinedfield1(userDef1);
				 * document.setUserdefinedfield2(userDef2);
				 * document.setUserdefinedfield3(userDef3);
				 * document.setUserDefinedField4(userDef4);
				 */
				document.setUserDefinedField28(userDef28);

				String source = fileStatus.getSource() != null
						? fileStatus.getSource() : null;
				if (source != null && source
						.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
					document.setDataOriginTypeCode(
							GSTConstants.DataOriginTypeCodes.SFTP
									.getDataOriginTypeCode());
				} else {
					document.setDataOriginTypeCode(
							GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
									.getDataOriginTypeCode());
				}

				if (derivedRetPeriod != null) {
					document.setDerivedTaxperiod(derivedRetPeriod);
				} else {
					document.setDerivedTaxperiod(999999);
				}

				document.setIsError("true");
				document.setIsProcessed("false");
				document.setIsDeleted("false");

				if (idStr != null) {
					try {
						Long id = Long.parseLong(idStr);
						document.setId(id);
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
						document.setAcceptanceId(fileId);
					} catch (RuntimeException e) {
						throw new AppException(
								" Error occuured while converting "
										+ "String file id to Long  file id "
										+ e.getMessage());
					}
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Key " + key);
				}
				// int maxlength = 99;
				if (key.length() > maxlength) {
					document.setDocKey(key.substring(0, maxlength));
				} else {
					document.setDocKey(key);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Key " + key);
					LOGGER.debug("Anx2RawFileToInwardTransDocErrConvertion "
							+ "convertAnx2RawFileToInWardTransError With "
							+ "in for end");
				}
			}
			document.setLineItems(lineItems);
			document.setCreatedBy(userName);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			document.setCreatedDate(convertNow);
			documents.add(document);
			document.getLineItems().forEach(item -> {
				item.setDocument(document);
			});
		});
		return documents;

	}

	private String getWithCsvValue(Object value) {
		String val = value != null ? value.toString() : null;
		if (val != null && val.startsWith(GSTConstants.SPE_CHAR)) {
			val = val.substring(1);
			return val;
		}
		return val;
	}

	private String getWithValue(Object obj) {
		String value = obj != null && !obj.toString().trim().isEmpty()
				? String.valueOf(obj.toString().trim()) : null;
		return value;
	}

}
