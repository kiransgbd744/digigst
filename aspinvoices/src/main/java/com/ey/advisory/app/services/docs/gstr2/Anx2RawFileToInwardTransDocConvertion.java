package com.ey.advisory.app.services.docs.gstr2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */

@Service("Anx2RawFileToInwardTransDocConvertion")
@Slf4j
public class Anx2RawFileToInwardTransDocConvertion {

	public List<InwardTransDocument> convertAnx2RawFileToInwardTransDoc(
			Map<String, List<Object[]>> documentMap,
			Gstr1FileStatusEntity fileStatus, String userName) {
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("ComprehensiveFileToInwardTransDocErrorConvertion "
				+ "convertPRFileToInWardTransError Begining");
		}

		List<InwardTransDocument> documents = new ArrayList<>();

		documentMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();
			InwardTransDocument document = new InwardTransDocument();
			List<InwardTransDocLineItem> lineItems = new ArrayList<>();
			for (Object[] obj : value) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key - " + key + ", Value - "
							+ Arrays.toString(obj));
				}
				// TO-DO Structural Validations
				InwardTransDocLineItem lineItem = new InwardTransDocLineItem();
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
				String finYear = GenUtil.getFinYear(localDocDate);
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
				BigDecimal qty = null;
				if (qnt != null && !qnt.trim().isEmpty()) {
					qty = new BigDecimal(qnt);
					lineItem.setQty(
							qty.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}

				String freeQnt = getWithValue(obj[61]);
				BigDecimal freeQty = null;
				if (freeQnt != null && !freeQnt.trim().isEmpty()) {
					freeQty = new BigDecimal(freeQnt);
					lineItem.setFreeQuantity(
							freeQty.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}

				String unitPrices = getWithValue(obj[62]);
				BigDecimal unitPrice = null;
				if (unitPrices != null && !unitPrices.trim().isEmpty()) {
					unitPrice = new BigDecimal(unitPrices);
					lineItem.setUnitPrice(
							unitPrice.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}

				String itemAmt = getWithValue(obj[63]);
				BigDecimal itemAmount = null;
				if (itemAmt != null && !itemAmt.isEmpty()) {
					itemAmount = new BigDecimal(itemAmt);
					lineItem.setItemAmount(
							itemAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				String itemDiscounts = getWithValue(obj[64]);
				BigDecimal itemDiscount = null;
				if (itemDiscounts != null && !itemDiscounts.isEmpty()) {
					itemDiscount = new BigDecimal(itemDiscounts);
					lineItem.setItemDiscount(itemDiscount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String preTaxAmt = getWithValue(obj[65]);
				BigDecimal preTaxAmount = null;
				if (preTaxAmt != null && !preTaxAmt.trim().isEmpty()) {
					preTaxAmount = new BigDecimal(preTaxAmt);
					lineItem.setPreTaxAmount(preTaxAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String itemAssessableAmt = getWithValue(obj[66]);
				BigDecimal itemAccAmount = null;
				if (itemAssessableAmt != null && !itemAssessableAmt.isEmpty()) {
					itemAccAmount = new BigDecimal(itemAssessableAmt);
					lineItem.setTaxableValue(itemAccAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String igstRates = getWithValue(obj[67]);
				BigDecimal igstRate = null;
				if (igstRates != null && !igstRates.trim().isEmpty()) {
					igstRate = new BigDecimal(igstRates);
					lineItem.setIgstRate(
							igstRate.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}

				String igstAmts = getWithValue(obj[68]);
				BigDecimal igstAmt = null;
				if (igstAmts != null && !igstAmts.trim().isEmpty()) {
					igstAmt = new BigDecimal(igstAmts);
					lineItem.setIgstAmount(
							igstAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}

				String cgstRates = getWithValue(obj[69]);
				BigDecimal cgstRate = null;
				if (cgstRates != null && !cgstRates.trim().isEmpty()) {
					cgstRate = new BigDecimal(cgstRates.trim());
					lineItem.setCgstRate(
							cgstRate.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}

				String cgstAmts = getWithValue(obj[70]);
				BigDecimal cgstAmt = null;
				if (cgstAmts != null && !cgstAmts.trim().isEmpty()) {
					cgstAmt = new BigDecimal(cgstAmts.trim());
					lineItem.setCgstAmount(
							cgstAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}

				String sgstRates = getWithValue(obj[71]);
				BigDecimal sgstRate = null;
				if (sgstRates != null && !sgstRates.trim().isEmpty()) {
					sgstRate = new BigDecimal(sgstRates);
					lineItem.setSgstRate(
							sgstRate.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}

				String sgstAmts = getWithValue(obj[72]);
				BigDecimal sgstAmt = null;
				if (sgstAmts != null && !sgstAmts.trim().isEmpty()) {
					sgstAmt = new BigDecimal(sgstAmts);
					lineItem.setSgstAmount(
							sgstAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}

				String cessAdvRate = getWithValue(obj[73]);
				BigDecimal cessAdvaloremRate = null;
				if (cessAdvRate != null && !cessAdvRate.trim().isEmpty()) {
					cessAdvaloremRate = new BigDecimal(cessAdvRate);
					lineItem.setCessRateAdvalorem(cessAdvaloremRate.setScale(3,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String cessAdvAmt = getWithValue(obj[74]);
				BigDecimal cessAdvaloremAmt = null;
				if (cessAdvAmt != null && !cessAdvAmt.trim().isEmpty()) {
					cessAdvaloremAmt = new BigDecimal(cessAdvAmt);
					lineItem.setCessAmountAdvalorem(cessAdvaloremAmt.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String cessSpeRate = getWithValue(obj[75]);
				BigDecimal cessSpecificRate = null;
				if (cessSpeRate != null && !cessSpeRate.trim().isEmpty()) {
					cessSpecificRate = new BigDecimal(cessSpeRate);
					lineItem.setCessRateSpecific(cessSpecificRate.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String cessSpeAmt = getWithValue(obj[76]);
				BigDecimal cessSpecificAmt = null;
				if (cessSpeAmt != null && !cessSpeAmt.trim().isEmpty()) {
					cessSpecificAmt = new BigDecimal(cessSpeAmt);
					lineItem.setCessAmountSpecific(cessSpecificAmt.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String stateCessAdvRate = getWithValue(obj[77]);
				BigDecimal stateCessAdvaloremRate = null;
				if (stateCessAdvRate != null
						&& !stateCessAdvRate.trim().isEmpty()) {
					stateCessAdvaloremRate = new BigDecimal(stateCessAdvRate);
					lineItem.setStateCessRate(stateCessAdvaloremRate.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String stateCessAdvAmt = getWithValue(obj[78]);
				BigDecimal stateCessAdvaloremAmt = null;
				if (stateCessAdvAmt != null
						&& !stateCessAdvAmt.trim().isEmpty()) {
					stateCessAdvaloremAmt = new BigDecimal(stateCessAdvAmt);
					lineItem.setStateCessAmount(stateCessAdvaloremAmt
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}

				String stateCessSpeRate = getWithValue(obj[79]);
				BigDecimal stateCessRate = null;
				if (stateCessSpeRate != null
						&& !stateCessSpeRate.trim().isEmpty()) {
					stateCessRate = new BigDecimal(stateCessSpeRate);
					lineItem.setStateCessSpecificRate(stateCessRate.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String stateCessSpeAmt = getWithValue(obj[80]);
				BigDecimal stateCessAmt = null;
				if (stateCessSpeAmt != null
						&& !stateCessSpeAmt.trim().isEmpty()) {
					stateCessAmt = new BigDecimal(stateCessSpeAmt);
					lineItem.setStateCessSpecificAmt(stateCessAmt.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String itemOthChargess = getWithValue(obj[81]);

				BigDecimal itemOthCharges = null;
				if (itemOthChargess != null
						&& !itemOthChargess.trim().isEmpty()) {
					itemOthCharges = new BigDecimal(itemOthChargess);
					lineItem.setOtherValues(itemOthCharges.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String totalItemAmts = getWithValue(obj[82]);
				BigDecimal totalItemAmt = null;
				if (totalItemAmts != null && !totalItemAmts.trim().isEmpty()) {
					totalItemAmt = new BigDecimal(totalItemAmts);
					lineItem.setTotalItemAmount(totalItemAmt.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String invOtherCharge = getWithValue(obj[83]);
				BigDecimal invOthCharges = null;
				if (invOtherCharge != null
						&& !invOtherCharge.trim().isEmpty()) {
					invOthCharges = new BigDecimal(invOtherCharge);
					lineItem.setInvoiceOtherCharges(invOthCharges.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String invAssessableAmt = getWithValue(obj[84]);
				BigDecimal invoiceAssessableAmount = null;
				if (invAssessableAmt != null
						&& !invAssessableAmt.toString().trim().isEmpty()) {
					invoiceAssessableAmount = new BigDecimal(invAssessableAmt);
					lineItem.setInvoiceAssessableAmount(invoiceAssessableAmount
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				String invIgstAmt = getWithValue(obj[85]);
				BigDecimal invoiceIGSTAmount = null;
				if (invIgstAmt != null && !invIgstAmt.trim().isEmpty()) {
					invoiceIGSTAmount = new BigDecimal(invIgstAmt);
					lineItem.setInvoiceIgstAmount(invoiceIGSTAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String invCgstAmt = getWithValue(obj[86]);
				BigDecimal invoiceCGSTAmount = null;
				if (invCgstAmt != null && !invCgstAmt.trim().isEmpty()) {
					invoiceCGSTAmount = new BigDecimal(invCgstAmt);
					lineItem.setInvoiceCgstAmount(invoiceCGSTAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String invSgstAmt = getWithValue(obj[87]);
				BigDecimal invoiceSGSTAmount = null;
				if (invSgstAmt != null && !invSgstAmt.trim().isEmpty()) {
					invoiceSGSTAmount = new BigDecimal(invSgstAmt);
					lineItem.setInvoiceSgstAmount(invoiceSGSTAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String invCessAdvAmt = getWithValue(obj[88]);
				BigDecimal invoiceCessAdvaloremAmount = null;
				if (invCessAdvAmt != null && !invCessAdvAmt.trim().isEmpty()) {
					invoiceCessAdvaloremAmount = new BigDecimal(invCessAdvAmt);
					lineItem.setInvoiceCessAdvaloremAmount(
							invoiceCessAdvaloremAmount.setScale(2,
									BigDecimal.ROUND_HALF_EVEN));
				}

				String invCessSpeAmt = getWithValue(obj[89]);
				BigDecimal invoiceCessSpecificAmount = null;
				if (invCessSpeAmt != null && !invCessSpeAmt.trim().isEmpty()) {
					invoiceCessSpecificAmount = new BigDecimal(invCessSpeAmt);
					lineItem.setInvoiceCessSpecificAmount(
							invoiceCessSpecificAmount.setScale(2,
									BigDecimal.ROUND_HALF_EVEN));
				}

				String invStateCessAdvAmt = getWithValue(obj[90]);
				BigDecimal invoiceStateCessAdvaloremAmount = null;
				if (invStateCessAdvAmt != null
						&& !invStateCessAdvAmt.trim().isEmpty()) {
					invoiceStateCessAdvaloremAmount = new BigDecimal(
							invStateCessAdvAmt);
					lineItem.setInvoiceStateCessAmount(
							invoiceStateCessAdvaloremAmount.setScale(2,
									BigDecimal.ROUND_HALF_EVEN));
				}
				String invStateCessSpeAmt = getWithValue(obj[91]);
				BigDecimal invoiceStateCessAmount = null;
				if (invStateCessSpeAmt != null
						&& !invStateCessSpeAmt.trim().isEmpty()) {
					invoiceStateCessAmount = new BigDecimal(invStateCessSpeAmt);
					lineItem.setInvStateCessSpecificAmt(invoiceStateCessAmount
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				String invValue = getWithValue(obj[92]);
				BigDecimal invoiceValue = null;
				if (invValue != null && !invValue.trim().isEmpty()) {
					invoiceValue = new BigDecimal(invValue);
					lineItem.setLineItemAmt(invoiceValue.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String roundOffs = getWithValue(obj[93]);
				BigDecimal roundOff = null;
				if (roundOffs != null && !roundOffs.trim().isEmpty()) {
					roundOff = new BigDecimal(roundOffs);
					lineItem.setRoundOff(
							roundOff.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}

				String totInvValueInWards = getWithValue(obj[94]);
				String eligibilityIndi = getWithValue(obj[95]);
				String commonSupIndi = getWithValue(obj[96]);
				String avialbleIgst = getWithValue(obj[97]);

				BigDecimal avialbleIgsts = null;
				if (avialbleIgst != null && !avialbleIgst.trim().isEmpty()) {
					avialbleIgsts = new BigDecimal(avialbleIgst);
					lineItem.setAvailableIgst(avialbleIgsts.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String avialbleCgst = getWithValue(obj[98]);
				BigDecimal avialbleCgsts = null;
				if (avialbleCgst != null && !avialbleCgst.trim().isEmpty()) {
					avialbleCgsts = new BigDecimal(avialbleCgst);
					lineItem.setAvailableCgst(avialbleCgsts.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String avialbleSgst = getWithValue(obj[99]);
				BigDecimal avialbleSgsts = null;
				if (avialbleSgst != null && !avialbleSgst.trim().isEmpty()) {
					avialbleSgsts = new BigDecimal(avialbleSgst);
					lineItem.setAvailableSgst(avialbleSgsts.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String avialbleCess = getWithValue(obj[100]);
				BigDecimal avialbleCesss = null;
				if (avialbleCess != null && !avialbleCess.trim().isEmpty()) {
					avialbleCesss = new BigDecimal(avialbleCess);
					lineItem.setAvailableCess(avialbleCesss.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String itcEntilement = getWithValue(obj[101]);
				String itcReversalId = getWithValue(obj[102]);
				String tcsFlagIncomTax = getWithValue(obj[103]);
				lineItem.setTcsFlagIncomeTax(tcsFlagIncomTax);
				String tcsRateIncomTax = getWithValue(obj[104]);
				BigDecimal tcsFlagRateIncomeTax = null;
				if (tcsRateIncomTax != null
						&& !tcsRateIncomTax.trim().isEmpty()) {
					tcsFlagRateIncomeTax = new BigDecimal(tcsRateIncomTax);
					lineItem.setTcsRateIncomeTax(tcsFlagRateIncomeTax
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				String tcsAmtIncomTax = getWithValue(obj[105]);
				BigDecimal tcsFlagAmountTax = null;
				if (tcsAmtIncomTax != null
						&& !tcsAmtIncomTax.trim().isEmpty()) {
					tcsFlagAmountTax = new BigDecimal(tcsAmtIncomTax);
					lineItem.setTcsAmountIncomeTax(tcsFlagAmountTax.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String currencyCode = getWithValue(obj[106]);
				String countryCode = getWithValue(obj[107]);
				String invValueFc = getWithValue(obj[108]);
				BigDecimal invValueFC = null;
				if (invValueFc != null && !invValueFc.trim().isEmpty()) {
					invValueFC = new BigDecimal(invValueFc);
					lineItem.setInvoiceValueFc(
							invValueFC.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
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
				BigDecimal paidAmount = null;
				if (paidAmt != null && !paidAmt.trim().isEmpty()) {
					paidAmount = new BigDecimal(paidAmt);
					lineItem.setPaidAmount(
							paidAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				String balanceAmt = getWithValue(obj[135]);
				BigDecimal balanceAmount = null;
				if (balanceAmt != null && !balanceAmt.trim().isEmpty()) {
					balanceAmount = new BigDecimal(balanceAmt);
					lineItem.setBalanceAmount(balanceAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
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
				String distances = getWithValue(obj[150]);
				Integer distance = null;
				if (distances != null && !distances.trim().isEmpty()) {
					distance = Integer.valueOf(distances);
					lineItem.setDistance(distance);
				}
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
				BigDecimal cIFs = null;
				if (cIF != null && !cIF.trim().isEmpty()) {
					cIFs = new BigDecimal(cIF);
					lineItem.setCifValue(
							cIFs.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}

				String customDuty = getWithValue(obj[167]);
				BigDecimal customDutys = null;
				if (customDuty != null && !customDuty.trim().isEmpty()) {
					customDutys = new BigDecimal(customDuty);
					lineItem.setCustomDuty(customDutys.setScale(2,
							BigDecimal.ROUND_HALF_DOWN));
				}
				String exchangeRate = getWithValue(obj[168]);
				String reasonForCrDrNote = getWithValue(obj[169]);
				String tcsFlagGst = getWithValue(obj[170]);
				String tcsIgstAmts = getWithValue(obj[171]);
				BigDecimal tcsIgstAmt = null;
				if (tcsIgstAmts != null && !tcsIgstAmts.trim().isEmpty()) {
					tcsIgstAmt = new BigDecimal(tcsIgstAmts);
					lineItem.setTcsIgstAmount(
							tcsIgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}

				String tcsCgstAmts = getWithValue(obj[172]);
				BigDecimal tcsCgstAmt = null;
				if (tcsCgstAmts != null && !tcsCgstAmts.trim().isEmpty()) {
					tcsCgstAmt = new BigDecimal(tcsCgstAmts);
					lineItem.setTcsCgstAmount(
							tcsCgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				String tcsSgstAmts = getWithValue(obj[173]);
				BigDecimal tcsSgstAmt = null;
				if (tcsSgstAmts != null && !tcsSgstAmts.trim().isEmpty()) {
					tcsSgstAmt = new BigDecimal(tcsSgstAmts);
					lineItem.setTcsSgstAmount(
							tcsSgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}

				String tdsFlagGst = getWithValue(obj[174]);
				String tdsIgstAmts = getWithValue(obj[175]);
				BigDecimal tdsIgstAmt = null;
				if (tdsIgstAmts != null && !tdsIgstAmts.trim().isEmpty()) {
					tdsIgstAmt = new BigDecimal(tdsIgstAmts);
					lineItem.setTdsIgstAmount(
							tdsIgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				String tdsCgstAmts = getWithValue(obj[176]);
				BigDecimal tdsCgstAmt = null;
				if (tdsCgstAmts != null && !tdsCgstAmts.trim().isEmpty()) {
					tdsCgstAmt = new BigDecimal(tdsCgstAmts);
					lineItem.setTdsCgstAmount(
							tdsCgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				String tdsSgstAmts = getWithValue(obj[177]);
				BigDecimal tdsSgstAmt = null;
				if (tdsSgstAmts != null && !tdsSgstAmts.trim().isEmpty()) {
					tdsSgstAmt = new BigDecimal(tdsSgstAmts);
					lineItem.setTdsSgstAmount(
							tdsSgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				
				String userId = truncation100(getWithValue(obj[178]));
				String companyCode = truncation100(getWithValue(obj[179]));
				String sourceId = truncation100(getWithValue(obj[180]));
				String sourceFileName = truncation100(getWithValue(obj[181]));
				String plantCode = truncation100(getWithValue(obj[182]));
				String division = truncation100(getWithValue(obj[183]));
				String subDivision = truncation100(getWithValue(obj[184]));
				String location = truncation100(getWithValue(obj[185]));
				String purchageOrg = truncation100(getWithValue(obj[186]));
				String profitCenter1 = truncation100(getWithValue(obj[187]));
				String profitCenter2 = truncation100(getWithValue(obj[188]));
				String profitCenter3 = truncation100(getWithValue(obj[189]));
				String profitCenter4 = truncation100(getWithValue(obj[190]));
				String profitCenter5 = truncation100(getWithValue(obj[191]));
				String profitCenter6 = truncation100(getWithValue(obj[192]));
				String profitCenter7 =truncation100(getWithValue(obj[193]));
				String profitCenter8 = truncation100(getWithValue(obj[194]));
				String glAssValue = truncation100(getWithValue(obj[195]));
				String glIgst = truncation100(getWithValue(obj[196]));
				String glCgst = truncation100(getWithValue(obj[197]));
				String glSgst = truncation100(getWithValue(obj[198]));
				String glAdvCess = truncation100(getWithValue(obj[199]));
				String glSpeCess = truncation100(getWithValue(obj[200]));
				String glStateCessAdv = truncation100(getWithValue(obj[201]));
				String glStateSpeCess =truncation100(getWithValue(obj[202]));
				String glPostingDate = getWithValue(obj[203]);
				LocalDate localGlPostingDate = DateUtil
						.parseObjToDate(glPostingDate);
				String purchageOrderValue = getWithValue(obj[204]);

				BigDecimal purchageOrderValues = null;
				if (purchageOrderValue != null
						&& !purchageOrderValue.trim().isEmpty()) {
					purchageOrderValues = new BigDecimal(purchageOrderValue);
					lineItem.setContractValue(purchageOrderValues.setScale(2,
							BigDecimal.ROUND_HALF_DOWN));
				}

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

				BigDecimal userDef28s = null;
				if (userDef28 != null && !userDef28.trim().isEmpty()) {
					userDef28s = new BigDecimal(userDef28);
					lineItem.setUserDefinedField28(
							userDef28s.setScale(2, BigDecimal.ROUND_HALF_DOWN));
					document.setUserDefinedField28(
							userDef28s.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}

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

				// lineItem.setIrn(irn);
				// lineItem.setIrnDate(localIrnDate != null
				// ? localIrnDate.atStartOfDay() : null);
				lineItem.setTaxScheme(taxSceme);
				lineItem.setSupplyType(supplyType);
				lineItem.setDocCategory(docCategory);
				lineItem.setDocDate(localDocDate);
				lineItem.setReverseCharge(revCharge);
				lineItem.setSupplierTradeName(supTradeName);
				lineItem.setCustOrSuppName(supLegalName);
				lineItem.setCustOrSuppAddress1(supAdd1);
				lineItem.setCustOrSuppAddress2(supAdd2);
				lineItem.setCustOrSuppAddress3(supLocation);
				lineItem.setCustOrSuppAddress4(supPinCode);
				lineItem.setSupplierStateCode(supState);
				lineItem.setSupplierPhone(supPhone);
				lineItem.setSupplierEmail(supEmail);
				lineItem.setCustomerTradeName(custTradeName);
				lineItem.setCustomerLegalName(custLegalName);
				lineItem.setCustomerBuildingNumber(custAdd1);
				lineItem.setCustomerBuildingName(custadd2);
				lineItem.setCustomerLocation(custLocation);
				lineItem.setCustomerPincode(custPinCode);
				lineItem.setBillToState(custState);
				lineItem.setPos(billingPos);
				lineItem.setCustomerPhone(custPhone);
				lineItem.setCustomerEmail(custEmail);
				// lineItem.setDispatcherGstin(disGstin);
				lineItem.setDispatcherTradeName(disTradeName);
				lineItem.setDispatcherBuildingNumber(disAdd1);
				lineItem.setDispatcherBuildingName(disAdd2);
				lineItem.setDispatcherLocation(disLocation);
				lineItem.setDispatcherPincode(disPinCode);
				lineItem.setDispatcherStateCode(disState);
				// lineItem.setShipToGstin(shipToGstin);
				lineItem.setShipToTradeName(shipToTradeName);
				lineItem.setShipToLegalName(shipToLegalName);
				lineItem.setShipToBuildingNumber(shipToAdd1);
				lineItem.setShipToBuildingName(shipToAdd2);
				lineItem.setShipToLocation(shipToLocation);
				lineItem.setShipToPincode(shipToPin);
				lineItem.setShipToState(shipToState);
				lineItem.setLineNo(
						itemSerNum != null && !itemSerNum.trim().isEmpty()
								? Integer.parseInt(itemSerNum) : null);
				lineItem.setSerialNumberII(proSerNum);
				lineItem.setProductName(prodName);
				lineItem.setItemDescription(proddes);
				lineItem.setIsService(isService);
				lineItem.setHsnSac(hsn);
				lineItem.setBarcode(barCode);
				lineItem.setBatchNameOrNumber(batchName);
				lineItem.setBatchExpiryDate(localBatchExpDate);
				lineItem.setWarrantyDate(localWarrentyDate);
				lineItem.setOrderLineReference(ordLineRef);
				lineItem.setAttributeName(attName);
				lineItem.setAttributeValue(attValue);
				lineItem.setOriginCountry(originCountry);
				lineItem.setUom(uqc);
				// lineItem.setUnitPrice(unitPrice);
				// lineItem.setItemDiscount(itemDiscount);
				lineItem.setSourceIdentifier(sourceId);
				lineItem.setTotalInvoiceValueInWords(totInvValueInWards);
				lineItem.setEligibilityIndicator(eligibilityIndi);
				lineItem.setCommonSupplyIndicator(commonSupIndi);
				if (fileStatus != null) {
					document.setAcceptanceId(fileStatus.getId());
					lineItem.setAcceptanceId(fileStatus.getId());
				}

				lineItem.setItcEntitlement(itcEntilement);
				lineItem.setItcReversalIdentifier(itcReversalId);

				lineItem.setForeignCurrency(currencyCode);
				lineItem.setCountryCode(countryCode);
				lineItem.setPortCode(portCode);
				lineItem.setBillOfEntryNo(billOfEntry);
				lineItem.setBillOfEntryDate(localBillOfEntryDate);
				lineItem.setInvoiceRemarks(invRemarks);

				lineItem.setInvoicePeriodStartDate(localInvPeriodStartDate);

				lineItem.setInvoicePeriodEndDate(localInvPeriodEndDate);
				lineItem.setOrigDocNo(preceedingInvNo);

				lineItem.setOrigDocDate(localPreceedingInvDate);
				lineItem.setInvoiceReference(othRef);
				lineItem.setReceiptAdviceReference(receipientAdvRef);

				lineItem.setReceiptAdviceDate(localReceipientAdvDate);
				lineItem.setTenderReference(tenderRef);
				lineItem.setExternalReference(externalRef);
				lineItem.setContractReference(contractRef);
				lineItem.setProjectReference(projectRef);
				lineItem.setContractNumber(custPoRefNo);

				lineItem.setContractDate(localCustOoRefDate);
				lineItem.setPayeeName(payeeName);
				lineItem.setModeOfPayment(
						modeOfPayment != null && !modeOfPayment.isEmpty()
								? modeOfPayment.toUpperCase() : modeOfPayment);
				lineItem.setBranchOrIfscCode(branchOrIfscCode);
				lineItem.setPaymentTerms(paymentTerms);
				lineItem.setPaymentInstruction(paymentInstructions);
				lineItem.setCreditTransfer(creditTransfer);
				lineItem.setDirectDebit(derectDebit);
				lineItem.setCreditDays(creditDays != null
						? Integer.parseInt(creditDays) : null);

				// lineItem.setPaymentDueDate(localPaymentDueDate);

				lineItem.setAccountDetail(accDetails);
				// lineItem.setEgstin(ecomGstin);
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

				lineItem.setTransportDocDate(localTransPortDocDate);
				// lineItem.setDistance(distance);
				lineItem.setVehicleNo(vehicleNo);
				lineItem.setVehicleType(vehicleType);
				lineItem.setTaxperiod(retPeriod);
				lineItem.setOrigDocType(orgDocType);
				lineItem.setOrigSgstin(orgSupGstin);
				lineItem.setDiffPercent(diffPerFlag);
				lineItem.setSection7OfIgstFlag(sec7OfIgstFlag);
				lineItem.setClaimRefundFlag(claimRefundFlag);
				lineItem.setAutoPopToRefundFlag(autoPopRefund);
				lineItem.setCrDrPreGst(crdrPreGst);
				lineItem.setCustOrSuppType(supplierType);
				lineItem.setCustOrSuppCode(suppCode);
				lineItem.setItemCode(productCode);
				lineItem.setItemCategory(categoryOfProduct);
				lineItem.setStateApplyingCess(stateAppCess);
				lineItem.setExchangeRate(exchangeRate);
				lineItem.setCrDrReason(reasonForCrDrNote);
				// lineItem.setTcsFlag(tcsFlagGst);
				// lineItem.setTdsFlag(tdsFlagGst);
				lineItem.setUserId(userId);
				lineItem.setCompanyCode(companyCode);
				lineItem.setSourceFileName(sourceFileName);
				lineItem.setPlantCode(plantCode);
				lineItem.setDivision(division);
				lineItem.setSubDivision(subDivision);
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
				lineItem.setGlStateCessSpecific(glStateSpeCess);
				lineItem.setPostingDate(localGlPostingDate);

				lineItem.seteWayBillNo(ewbNo);

				lineItem.seteWayBillDate(localEwbDate != null
						? localEwbDate.atStartOfDay() : null);
				lineItem.setPurchaseVoucherNum(accountingVoucharNo);

				lineItem.setPurchaseVoucherDate(localAccountingVoucharDate);
				lineItem.setDocReferenceNumber(docRefNo);

				lineItem.setUserdefinedfield1(userDef1);
				lineItem.setUserdefinedfield2(userDef2);
				lineItem.setUserdefinedfield3(userDef3);
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
				lineItem.setUserDefinedField29(userDef29);
				lineItem.setUserDefinedField30(userDef30);
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
					// lineItem.set(key.substring(0, maxlength));
				} else {
					// lineItem.setDocKey(key);
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
			/*	document.setIrnDate(localIrnDate != null
						? localIrnDate.atStartOfDay() : null);*/
				document.setIrnDate(localIrnDate);
				document.setSupplyType(supplyType);
				document.setDocType(docType);
				document.setDocNo(docNo);
				document.setDocDate(localDocDate);
				document.setFinYear(finYear);
				document.setReverseCharge(revCharge);
				document.setSgstin(supgstin);
				document.setSupplierTradeName(supTradeName);
				document.setCustOrSuppName(supLegalName);
				document.setCustOrSuppAddress1(supAdd1);
				document.setCustOrSuppAddress2(supAdd2);
				document.setCustOrSuppAddress3(supLocation);
				document.setCustOrSuppAddress4(supPinCode);
				document.setCgstin(custGstin);
				document.setPos(billingPos);
				document.setDispatcherGstin(disGstin);
				document.setShipToGstin(shipToGstin);
				document.setItcEntitlement(itcEntilement);
				document.setItcReversalIdentifier(itcReversalId);
				document.setPortCode(portCode);
				document.setBillOfEntryNo(billOfEntry);

				document.setBillOfEntryDate(localBillOfEntryDate);
				document.setOrigDocNo(preceedingInvNo);

				document.setOrigDocDate(localPreceedingInvDate);
				document.setPaymentDueDate(localPaymentDueDate);
				document.setCustOrSuppType(supplierType);
				document.setEgstin(ecomGstin);
				document.setTaxperiod(retPeriod);
				document.setOrigSgstin(orgSupGstin);
				document.setDiffPercent(diffPerFlag);
				document.setSection7OfIgstFlag(sec7OfIgstFlag);
				document.setClaimRefundFlag(claimRefundFlag);
				document.setAutoPopToRefundFlag(autoPopRefund);
				document.setCrDrPreGst(crdrPreGst);
				document.setCustOrSuppType(supplierType);
				document.setCustOrSuppCode(suppCode);
				document.setStateApplyingCess(stateAppCess);
				document.setTcsFlag(tcsFlagGst);
				document.setTdsFlag(tdsFlagGst);
				document.setUserId(userId);
				document.setSourceFileName(sourceFileName);
				document.setProfitCentre(profitCenter1);
				document.setPlantCode(plantCode);
				document.setDivision(division);
				document.setLocation(location);
				document.setPurchaseOrganization(purchageOrg);
				document.setUserAccess1(profitCenter3);
				document.setUserAccess2(profitCenter4);
				document.setUserAccess3(profitCenter5);
				document.setUserAccess4(profitCenter6);
				document.setUserAccess5(profitCenter7);
				document.setUserAccess6(profitCenter8);

				document.setPostingDate(localGlPostingDate);
				document.seteWayBillNo(ewbNo);

				document.seteWayBillDate(localEwbDate != null
						? localEwbDate.atStartOfDay() : null);
				document.setPurchaseVoucherNum(accountingVoucharNo);

				document.setPurchaseVoucherDate(localAccountingVoucharDate);

				document.setPortCode(portCode);

				document.setDivision(division);
				document.setLocation(location);
				document.setPurchaseOrganization(purchageOrg);
				// document.setUserAccess1(profitCenter2);
				document.setUserdefinedfield1(userDef1);
				document.setUserdefinedfield2(userDef2);
				document.setUserdefinedfield3(userDef3);
				document.setUserDefinedField4(userDef4);
				document.setCompanyCode(companyCode);
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
		if (Strings.isNullOrEmpty(val)) {
			return null;
		}
		if (val.startsWith(GSTConstants.SPE_CHAR)) {
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
	private String truncation100(String value) {
		if (value == null)
			return null;
		if (value.length() > 100) {
			value = value.substring(0, 100);
		}
		return value;
	}
}
