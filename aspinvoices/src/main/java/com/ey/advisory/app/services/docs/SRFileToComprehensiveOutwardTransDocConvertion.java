package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
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
 * @author Mahesh.Golla
 *
 */
@Service("SRFileToComprehensiveOutwardTransDocConvertion")
@Slf4j
public class SRFileToComprehensiveOutwardTransDocConvertion {

	public List<OutwardTransDocument> convertSRFileToOutwardTransDoc(
			Map<String, List<Object[]>> documentMap,
			DocumentKeyBuilder documentKeyBuilder,
			Gstr1FileStatusEntity fileStatus, String userName) {
		List<OutwardTransDocument> documents = new ArrayList<>();

		documentMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();
			OutwardTransDocument document = new OutwardTransDocument();
			List<OutwardTransDocLineItem> lineItems = new ArrayList<>();
			for (Object[] obj : value) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key - " + key + ", Value - "
							+ Arrays.toString(obj));
				}
				// TO-DO Structural Validations
				OutwardTransDocLineItem lineItem = new OutwardTransDocLineItem();

				String irn = obj[0] != null
						&& !obj[0].toString().trim().isEmpty()
								? String.valueOf(obj[0]) : null;
				String irnDate = obj[1] != null
						&& !obj[1].toString().trim().isEmpty()
								? String.valueOf(obj[1]) : null;
				LocalDate localIrnDate = DateUtil.parseObjToDate(irnDate);
				String taxScheme = obj[2] != null
						&& !obj[2].toString().trim().isEmpty()
								? String.valueOf(obj[2]) : null;
				String canReason = obj[3] != null
						&& !obj[3].toString().trim().isEmpty()
								? String.valueOf(obj[3]) : null;

				String canRemarks = (obj[4] != null
						&& !obj[4].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;

				String supplyType = (obj[5] != null
						&& !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]).trim() : null;

				String docCategory = (obj[6] != null
						&& !obj[6].toString().trim().isEmpty())
								? String.valueOf(obj[6]).trim() : null;
				String docType = (obj[7] != null
						&& !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;

				String docNo = (obj[8] != null
						&& !obj[8].toString().trim().isEmpty())
								? String.valueOf(obj[8]).trim() : null;
				String docDate = (obj[9] != null
						&& !obj[9].toString().trim().isEmpty())
								? String.valueOf(obj[9]).trim() : null;
				LocalDate localDocDate = DateUtil.parseObjToDate(docDate);
				String finYear = GenUtil.getFinYear(localDocDate);
				String reverseChargeFlag = (obj[10] != null
						&& !obj[10].toString().trim().isEmpty())
								? String.valueOf(obj[10]).trim() : null;
				String sgstin = (obj[11] != null
						&& !obj[11].toString().trim().isEmpty())
								? String.valueOf(obj[11]).trim() : null;

				String supplierTradeName = (obj[12] != null
						&& !obj[12].toString().trim().isEmpty())
								? String.valueOf(obj[12]).trim() : null;

				String supplierLegalName = (obj[13] != null
						&& !obj[13].toString().trim().isEmpty())
								? String.valueOf(obj[13]).trim() : null;

				String supAddress1 = (obj[14] != null
						&& !obj[14].toString().trim().isEmpty())
								? String.valueOf(obj[14]).trim() : null;

				String supAddress2 = (obj[15] != null
						&& !obj[15].toString().trim().isEmpty())
								? String.valueOf(obj[15]).trim() : null;
				String supplierLocation = (obj[16] != null
						&& !obj[16].toString().trim().isEmpty())
								? String.valueOf(obj[16]).trim() : null;

				Integer supplierPincode = (obj[17] != null
						&& !obj[17].toString().trim().isEmpty())
								? Integer.parseInt(obj[17].toString().trim())
								: null;
				String supplierStateCode = (obj[18] != null
						&& !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18].toString().trim())
								: null;
				// Long supplierPhone = null;
				String supplierPhone = obj[19] != null
						&& !obj[19].toString().isEmpty()
								? String.valueOf(obj[19].toString().trim())
								: null;
				/*
				 * if (sPhone != null && !sPhone.trim().isEmpty()) { if
				 * (!sPhone.matches("-?\\d+(\\.\\d+)?([E-e]-?\\d+)?")) { sPhone
				 * = null; } } if (sPhone != null) { BigDecimal
				 * mobileDecimalFormat = BigDecimal.ZERO; mobileDecimalFormat =
				 * new BigDecimal(sPhone); supplierPhone =
				 * mobileDecimalFormat.longValue(); if (supplierPhone != null &&
				 * supplierPhone.toString().trim().length() > 10) {
				 * supplierPhone = null; } }
				 */
				String supplierEmail = (obj[20] != null
						&& !obj[20].toString().trim().isEmpty())
								? String.valueOf(obj[20]).trim() : null;

				String customerGstin = (obj[21] != null
						&& !obj[21].toString().trim().isEmpty())
								? String.valueOf(obj[21]).trim() : null;

				String customerTradeName = (obj[22] != null
						&& !obj[22].toString().trim().isEmpty())
								? String.valueOf(obj[22]).trim() : null;

				String customerLegalName = (obj[23] != null
						&& !obj[23].toString().trim().isEmpty())
								? String.valueOf(obj[23]).trim() : null;
				String customerAddress1 = (obj[24] != null
						&& !obj[24].toString().trim().isEmpty())
								? String.valueOf(obj[24]).trim() : null;
				String customerAddress2 = (obj[25] != null
						&& !obj[25].toString().trim().isEmpty())
								? String.valueOf(obj[25]).trim() : null;
				String customerLocation = (obj[26] != null
						&& !obj[26].toString().trim().isEmpty())
								? String.valueOf(obj[26]).trim() : null;
				Integer customerPincode = (obj[27] != null
						&& !obj[27].toString().trim().isEmpty())
								? Integer.valueOf(obj[27].toString().trim())
								: null;
				String customerStateCode = (obj[28] != null
						&& !obj[28].toString().trim().isEmpty())
								? String.valueOf(obj[28]).trim() : null;
				String billingPos = (obj[29] != null
						&& !obj[29].toString().trim().isEmpty())
								? String.valueOf(obj[29]).trim() : null;
				// Long customerPhone = null;
				String customerPhone = obj[30] != null
						&& !obj[30].toString().isEmpty()
								? String.valueOf(obj[30].toString().trim())
								: null;
				/*
				 * if (cPhone != null && !cPhone.trim().isEmpty()) { if
				 * (!cPhone.matches("-?\\d+(\\.\\d+)?([E-e]-?\\d+)?")) { cPhone
				 * = null; } } if (cPhone != null) { BigDecimal
				 * mobileDecimalFormat = BigDecimal.ZERO; mobileDecimalFormat =
				 * new BigDecimal(cPhone); customerPhone =
				 * mobileDecimalFormat.longValue(); if (customerPhone != null &&
				 * customerPhone.toString().trim().length() > 10) {
				 * customerPhone = null; } }
				 */
				String customerEmail = (obj[31] != null
						&& !obj[31].toString().trim().isEmpty())
								? String.valueOf(obj[31]).trim() : null;
				String dispatcherGstin = (obj[32] != null
						&& !obj[32].toString().trim().isEmpty())
								? String.valueOf(obj[32]).trim() : null;
				String dispatcherTradeName = (obj[33] != null
						&& !obj[33].toString().trim().isEmpty())
								? String.valueOf(obj[33]).trim() : null;
				String dispatcherAddress1 = (obj[34] != null
						&& !obj[34].toString().trim().isEmpty())
								? String.valueOf(obj[34]).trim() : null;
				String dispatcherAddress2 = (obj[35] != null
						&& !obj[35].toString().trim().isEmpty())
								? String.valueOf(obj[35]).trim() : null;
				String dispatcherLocation = (obj[36] != null
						&& !obj[36].toString().trim().isEmpty())
								? String.valueOf(obj[36]).trim() : null;
				Integer dispatcherPinCode = (obj[37] != null
						&& !obj[37].toString().trim().isEmpty())
								? Integer.valueOf(obj[37].toString().trim())
								: null;
				String dispatcherStateCode = (obj[38] != null
						&& !obj[38].toString().trim().isEmpty())
								? String.valueOf(obj[38].toString().trim())
								: null;
				String shipToGstin = (obj[39] != null
						&& !obj[39].toString().trim().isEmpty())
								? String.valueOf(obj[39].toString().trim())
								: null;
				String shipToTradeName = (obj[40] != null
						&& !obj[40].toString().trim().isEmpty())
								? String.valueOf(obj[40]).trim() : null;
				String shipToLegalName = (obj[41] != null
						&& !obj[41].toString().trim().isEmpty())
								? String.valueOf(obj[41]).trim() : null;
				String shipToAddress1 = (obj[42] != null
						&& !obj[42].toString().trim().isEmpty())
								? String.valueOf(obj[42]).trim() : null;
				String shipToAddress2 = (obj[43] != null
						&& !obj[43].toString().trim().isEmpty())
								? String.valueOf(obj[43]).trim() : null;
				String shipToLocation = (obj[44] != null
						&& !obj[44].toString().trim().isEmpty())
								? String.valueOf(obj[44]).trim() : null;
				Integer shipToPincode = (obj[45] != null
						&& !obj[45].toString().trim().isEmpty())
								? Integer.valueOf(obj[45].toString().trim())
								: null;
				String shipToStateCode = (obj[46] != null
						&& !obj[46].toString().trim().isEmpty())
								? String.valueOf(obj[46]).trim() : null;
				// Line Items
				String lineNo = (obj[47] != null
						&& !obj[47].toString().trim().isEmpty())
								? String.valueOf(obj[47]).trim() : null;
				/*
				 * if (obj[47] != null && !obj[47].toString().trim().isEmpty())
				 * { String lineNoStr = (String.valueOf(obj[47])).trim(); lineNo
				 * = Integer.valueOf(lineNoStr); }
				 */
				String productSerialNumber = obj[48] != null
						&& !obj[48].toString().trim().isEmpty()
								? String.valueOf(obj[48]) : null;
				String productName = obj[49] != null
						&& !obj[49].toString().trim().isEmpty()
								? String.valueOf(obj[49]) : null;
				String productDesription = (obj[50] != null
						&& !obj[50].toString().trim().isEmpty())
								? String.valueOf(obj[50]).trim() : null;
				String isService = (obj[51] != null
						&& !obj[51].toString().trim().isEmpty())
								? String.valueOf(obj[51]).trim() : null;

				String hsn = (obj[52] != null
						&& !obj[52].toString().trim().isEmpty())
								? String.valueOf(obj[52]).trim() : null;
				String barCode = (obj[53] != null
						&& !obj[53].toString().trim().isEmpty())
								? String.valueOf(obj[53]).trim() : null;
				String batchNameOrNo = (obj[54] != null
						&& !obj[54].toString().trim().isEmpty())
								? String.valueOf(obj[54]).trim() : null;
				String batchExpiryDate = (obj[55] != null
						&& !obj[55].toString().trim().isEmpty())
								? String.valueOf(obj[55]).trim() : null;
				LocalDate localBatchExpiryDate = DateUtil
						.parseObjToDate(batchExpiryDate);

				String warranDate = (obj[56] != null
						&& !obj[56].toString().trim().isEmpty())
								? String.valueOf(obj[56]).trim() : null;
				LocalDate localWarranDate = DateUtil.parseObjToDate(warranDate);
				String orderLineRef = (obj[57] != null
						&& !obj[57].toString().trim().isEmpty())
								? String.valueOf(obj[57]).trim() : null;
				String attributeName = (obj[58] != null
						&& !obj[58].toString().trim().isEmpty())
								? String.valueOf(obj[58]).trim() : null;
				String attributeValue = (obj[59] != null
						&& !obj[59].toString().trim().isEmpty())
								? String.valueOf(obj[59]).trim() : null;
				String originCountry = (obj[60] != null
						&& !obj[60].toString().trim().isEmpty())
								? String.valueOf(obj[60]).trim() : null;
				String uom = (obj[61] != null
						&& !obj[61].toString().trim().isEmpty())
								? String.valueOf(obj[61]).trim().toUpperCase() : null;

				BigDecimal qty = null;
				if (obj[62] != null && !obj[62].toString().trim().isEmpty()) {
					qty = new BigDecimal(String.valueOf(obj[62]).trim());
					lineItem.setQty(
							qty.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal freeQty = null;
				if (obj[63] != null && !obj[63].toString().trim().isEmpty()) {
					freeQty = new BigDecimal(String.valueOf(obj[63]).trim());
					lineItem.setFreeQuantity(
							freeQty.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal unitPrice = null;
				if (obj[64] != null && !obj[64].toString().trim().isEmpty()) {
					unitPrice = new BigDecimal(String.valueOf(obj[64]).trim());
					lineItem.setUnitPrice(
							unitPrice.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal itemAmount = null;
				if (obj[65] != null && !obj[65].toString().trim().isEmpty()) {
					itemAmount = new BigDecimal(String.valueOf(obj[65]).trim());
					lineItem.setItemAmount(
							itemAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal itemDiscount = null;
				if (obj[66] != null && !obj[66].toString().trim().isEmpty()) {
					itemDiscount = new BigDecimal(
							String.valueOf(obj[66]).trim());
					lineItem.setItemDiscount(itemDiscount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal preTaxAmount = null;
				if (obj[67] != null && !obj[67].toString().trim().isEmpty()) {
					preTaxAmount = new BigDecimal(
							String.valueOf(obj[67]).trim());
					lineItem.setPreTaxAmount(preTaxAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal itemAccAmount = null;
				if (obj[68] != null && !obj[68].toString().trim().isEmpty()) {
					itemAccAmount = new BigDecimal(
							String.valueOf(obj[68]).trim());
					lineItem.setTaxableValue(itemAccAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal igstRate = null;
				if (obj[69] != null && !obj[69].toString().trim().isEmpty()) {
					igstRate = new BigDecimal(String.valueOf(obj[69]).trim());
					lineItem.setIgstRate(
							igstRate.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal igstAmt = null;
				if (obj[70] != null && !obj[70].toString().trim().isEmpty()) {
					igstAmt = new BigDecimal(String.valueOf(obj[70]).trim());
					lineItem.setIgstAmount(
							igstAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal cgstRate = null;
				if (obj[71] != null && !obj[71].toString().trim().isEmpty()) {
					cgstRate = new BigDecimal(String.valueOf(obj[71]).trim());
					lineItem.setCgstRate(
							cgstRate.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal cgstAmt = null;
				if (obj[72] != null && !obj[72].toString().trim().isEmpty()) {
					cgstAmt = new BigDecimal(String.valueOf(obj[72]).trim());
					lineItem.setCgstAmount(
							cgstAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal sgstRate = null;
				if (obj[73] != null && !obj[73].toString().trim().isEmpty()) {
					sgstRate = new BigDecimal(String.valueOf(obj[73]).trim());
					lineItem.setSgstRate(
							sgstRate.setScale(3, BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal sgstAmt = null;
				if (obj[74] != null && !obj[74].toString().trim().isEmpty()) {
					sgstAmt = new BigDecimal(String.valueOf(obj[74]).trim());
					lineItem.setSgstAmount(
							sgstAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal cessAdvaloremRate = null;
				if (obj[75] != null && !obj[75].toString().trim().isEmpty()) {
					cessAdvaloremRate = new BigDecimal(
							String.valueOf(obj[75]).trim());
					lineItem.setCessRateAdvalorem(cessAdvaloremRate.setScale(3,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal cessAdvaloremAmt = null;
				if (obj[76] != null && !obj[76].toString().trim().isEmpty()) {
					cessAdvaloremAmt = new BigDecimal(
							String.valueOf(obj[76]).trim());
					lineItem.setCessAmountAdvalorem(cessAdvaloremAmt.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal cessSpecificRate = null;
				if (obj[77] != null && !obj[77].toString().trim().isEmpty()) {
					cessSpecificRate = new BigDecimal(
							String.valueOf(obj[77]).trim());
					lineItem.setCessRateSpecific(cessSpecificRate.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal cessSpecificAmt = null;
				if (obj[78] != null && !obj[78].toString().trim().isEmpty()) {
					cessSpecificAmt = new BigDecimal(
							String.valueOf(obj[78]).trim());
					lineItem.setCessAmountSpecific(cessSpecificAmt.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal stateCessAdvaloremRate = null;
				if (obj[79] != null && !obj[79].toString().trim().isEmpty()) {
					stateCessAdvaloremRate = new BigDecimal(
							String.valueOf(obj[79]).trim());
					lineItem.setStateCessRate(stateCessAdvaloremRate.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal stateCessAdvaloremAmt = null;
				if (obj[80] != null && !obj[80].toString().trim().isEmpty()) {
					stateCessAdvaloremAmt = new BigDecimal(
							String.valueOf(obj[80]).trim());
					lineItem.setStateCessAmount(stateCessAdvaloremAmt
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal stateCessRate = null;
				if (obj[81] != null && !obj[81].toString().trim().isEmpty()) {
					stateCessRate = new BigDecimal(
							String.valueOf(obj[81]).trim());
					lineItem.setStateCessSpecificRate(stateCessRate.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal stateCessAmt = null;
				if (obj[82] != null && !obj[82].toString().trim().isEmpty()) {
					stateCessAmt = new BigDecimal(
							String.valueOf(obj[82]).trim());
					lineItem.setStateCessSpecificAmt(stateCessAmt.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal itemOthCharges = null;
				if (obj[83] != null && !obj[83].toString().trim().isEmpty()) {
					itemOthCharges = new BigDecimal(
							String.valueOf(obj[83]).trim());
					lineItem.setOtherValues(itemOthCharges.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal totalItemAmt = null;
				if (obj[84] != null && !obj[84].toString().trim().isEmpty()) {
					totalItemAmt = new BigDecimal(
							String.valueOf(obj[84]).trim());
					lineItem.setTotalItemAmount(totalItemAmt.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal invOthCharges = null;
				if (obj[85] != null && !obj[85].toString().trim().isEmpty()) {
					invOthCharges = new BigDecimal(
							String.valueOf(obj[85]).trim());
					lineItem.setInvoiceOtherCharges(invOthCharges.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
					document.setInvoiceOtherCharges(invOthCharges.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal invoiceAssessableAmount = null;
				if (obj[86] != null && !obj[86].toString().trim().isEmpty()) {
					invoiceAssessableAmount = new BigDecimal(
							String.valueOf(obj[86]).trim());
					lineItem.setInvoiceAssessableAmount(invoiceAssessableAmount
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
					document.setInvoiceAssessableAmount(invoiceAssessableAmount
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal invoiceIGSTAmount = null;
				if (obj[87] != null && !obj[87].toString().trim().isEmpty()) {
					invoiceIGSTAmount = new BigDecimal(
							String.valueOf(obj[87]).trim());
					lineItem.setInvoiceIgstAmount(invoiceIGSTAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
					document.setInvoiceIgstAmount(invoiceIGSTAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal invoiceCGSTAmount = null;
				if (obj[88] != null && !obj[88].toString().trim().isEmpty()) {
					invoiceCGSTAmount = new BigDecimal(
							String.valueOf(obj[88]).trim());
					lineItem.setInvoiceCgstAmount(invoiceCGSTAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
					document.setInvoiceCgstAmount(invoiceCGSTAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal invoiceSGSTAmount = null;
				if (obj[89] != null && !obj[89].toString().trim().isEmpty()) {
					invoiceSGSTAmount = new BigDecimal(
							String.valueOf(obj[89]).trim());
					lineItem.setInvoiceSgstAmount(invoiceSGSTAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
					document.setInvoiceSgstAmount(invoiceSGSTAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				BigDecimal invoiceCessAdvaloremAmount = null;
				if (obj[90] != null && !obj[90].toString().trim().isEmpty()) {
					invoiceCessAdvaloremAmount = new BigDecimal(
							String.valueOf(obj[90]).trim());
					lineItem.setInvoiceCessAdvaloremAmount(
							invoiceCessAdvaloremAmount.setScale(2,
									BigDecimal.ROUND_HALF_EVEN));
					document.setInvoiceCessAdvaloremAmount(
							invoiceCessAdvaloremAmount.setScale(2,
									BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal invoiceCessSpecificAmount = null;
				if (obj[91] != null && !obj[91].toString().trim().isEmpty()) {
					invoiceCessSpecificAmount = new BigDecimal(
							String.valueOf(obj[91]).trim());
					lineItem.setInvoiceCessSpecificAmount(
							invoiceCessSpecificAmount.setScale(2,
									BigDecimal.ROUND_HALF_EVEN));
					document.setInvoiceCessSpecificAmount(
							invoiceCessSpecificAmount.setScale(2,
									BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal invoiceStateCessAdvaloremAmount = null;
				if (obj[92] != null && !obj[92].toString().trim().isEmpty()) {
					invoiceStateCessAdvaloremAmount = new BigDecimal(
							String.valueOf(obj[92]).trim());
					lineItem.setInvoiceStateCessAmount(
							invoiceStateCessAdvaloremAmount.setScale(2,
									BigDecimal.ROUND_HALF_EVEN));
					document.setInvoiceStateCessAmount(
							invoiceStateCessAdvaloremAmount.setScale(2,
									BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal invoiceStateCessAmount = null;
				if (obj[93] != null && !obj[93].toString().trim().isEmpty()) {
					invoiceStateCessAmount = new BigDecimal(
							String.valueOf(obj[93]).trim());
					lineItem.setInvStateCessSpecificAmt(invoiceStateCessAmount
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal invoiceValue = null;
				if (obj[94] != null && !obj[94].toString().trim().isEmpty()) {
					invoiceValue = new BigDecimal(
							String.valueOf(obj[94]).trim());
					lineItem.setLineItemAmt(invoiceValue.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal roundOff = null;
				if (obj[95] != null && !obj[95].toString().trim().isEmpty()) {
					roundOff = new BigDecimal(String.valueOf(obj[95]).trim());
					document.setRoundOff(
							roundOff.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				String totalInvValue = (obj[96] != null
						&& !obj[96].toString().trim().isEmpty())
								? String.valueOf(obj[96]).trim() : null;
				String tcsFlagIncomeTax = (obj[97] != null
						&& !obj[97].toString().trim().isEmpty())
								? String.valueOf(obj[97]).trim() : null;

				BigDecimal tcsFlagRateIncomeTax = null;
				if (obj[98] != null && !obj[98].toString().trim().isEmpty()) {
					tcsFlagRateIncomeTax = new BigDecimal(
							String.valueOf(obj[98]).trim());
					lineItem.setTcsRateIncomeTax(tcsFlagRateIncomeTax
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal tcsFlagAmountTax = null;
				if (obj[99] != null && !obj[99].toString().trim().isEmpty()) {
					tcsFlagAmountTax = new BigDecimal(
							String.valueOf(obj[99]).trim());
					lineItem.setTcsAmountIncomeTax(tcsFlagAmountTax.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}
				String customerPanOrAdhar = (obj[100] != null
						&& !obj[100].toString().trim().isEmpty())
								? String.valueOf(obj[100]).trim() : null;

				String currencyCode = (obj[101] != null
						&& !obj[101].toString().trim().isEmpty())
								? String.valueOf(obj[101]).trim() : null;

				String countryCode = (obj[102] != null
						&& !obj[102].toString().trim().isEmpty())
								? String.valueOf(obj[102]).trim() : null;
				BigDecimal invValueFC = null;
				if (obj[103] != null && !obj[103].toString().trim().isEmpty()) {
					String invValueFCStr = String.valueOf(obj[103]).trim();
					invValueFC = new BigDecimal(invValueFCStr);
					document.setInvoiceValueFc(
							invValueFC.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}

				String portCode = (obj[104] != null
						&& !obj[104].toString().trim().isEmpty())
								? String.valueOf(obj[104]).trim() : null;

				String shippingBillNo = (obj[105] != null
						&& !obj[105].toString().trim().isEmpty())
								? String.valueOf(obj[105]).trim() : null;

				String shippingBillDate = (obj[106] != null
						&& !obj[106].toString().trim().isEmpty())
								? String.valueOf(obj[106]).trim() : null;
				LocalDate localShippingBillDate = DateUtil
						.parseObjToDate(shippingBillDate);

				String invoiceRemarks = (obj[107] != null
						&& !obj[107].toString().trim().isEmpty())
								? String.valueOf(obj[107]).trim() : null;

				String invoicePeriodStartDate = (obj[108] != null
						&& !obj[108].toString().trim().isEmpty())
								? String.valueOf(obj[108]).trim() : null;
				LocalDate localInvoicePeriodStartDate = DateUtil
						.parseObjToDate(invoicePeriodStartDate);
				String invoicePeriodEndDate = (obj[109] != null
						&& !obj[109].toString().trim().isEmpty())
								? String.valueOf(obj[109]).trim() : null;
				LocalDate localInvoicePeriodEndDate = DateUtil
						.parseObjToDate(invoicePeriodEndDate);

				String preceedingInvoiceNumber = (obj[110] != null
						&& !obj[110].toString().trim().isEmpty())
								? String.valueOf(obj[110]).trim() : null;
				String preceedingInvoiceDate = (obj[111] != null
						&& !obj[111].toString().trim().isEmpty())
								? String.valueOf(obj[111]).trim() : null;
				LocalDate localPreceedingInvoiceDate = DateUtil
						.parseObjToDate(preceedingInvoiceDate);
				String otherReference = (obj[112] != null
						&& !obj[112].toString().trim().isEmpty())
								? String.valueOf(obj[112]).trim() : null;
				String receiptAdviceReference = (obj[113] != null
						&& !obj[113].toString().trim().isEmpty())
								? String.valueOf(obj[113]).trim() : null;
				String receiptAdviceDate = (obj[114] != null
						&& !obj[114].toString().trim().isEmpty())
								? String.valueOf(obj[114]).trim() : null;
				LocalDate localreceiptAdviceDate = DateUtil
						.parseObjToDate(receiptAdviceDate);
				String tenderReference = (obj[115] != null
						&& !obj[115].toString().trim().isEmpty())
								? String.valueOf(obj[115]).trim() : null;
				String contractRef = (obj[116] != null
						&& !obj[116].toString().trim().isEmpty())
								? String.valueOf(obj[116]).trim() : null;
				String externalRef = (obj[117] != null
						&& !obj[117].toString().trim().isEmpty())
								? String.valueOf(obj[117]).trim() : null;

				String projectRef = (obj[118] != null
						&& !obj[118].toString().trim().isEmpty())
								? String.valueOf(obj[118]).trim() : null;

				String customerPOReferenceNumber = (obj[119] != null
						&& !obj[119].toString().trim().isEmpty())
								? String.valueOf(obj[119]).trim() : null;
				String customerPOReferenceDate = (obj[120] != null
						&& !obj[120].toString().trim().isEmpty())
								? String.valueOf(obj[120]).trim() : null;
				LocalDate localcustomerPOReferenceDate = DateUtil
						.parseObjToDate(customerPOReferenceDate);

				String payeeName = (obj[121] != null
						&& !obj[121].toString().trim().isEmpty())
								? String.valueOf(obj[121]).trim() : null;

				String modeOfPayment = (obj[122] != null
						&& !obj[122].toString().trim().isEmpty())
								? String.valueOf(obj[122]).trim() : null;

				String branchOrIFSCCode = (obj[123] != null
						&& !obj[123].toString().trim().isEmpty())
								? String.valueOf(obj[123]).trim() : null;

				String paymentTerms = (obj[124] != null
						&& !obj[124].toString().trim().isEmpty())
								? String.valueOf(obj[124]).trim() : null;
				String paymentInstruction = (obj[125] != null
						&& !obj[125].toString().trim().isEmpty())
								? String.valueOf(obj[125]).trim() : null;

				String creditTransfer = (obj[126] != null
						&& !obj[126].toString().trim().isEmpty())
								? String.valueOf(obj[126]).trim() : null;

				String directDebit = (obj[127] != null
						&& !obj[127].toString().trim().isEmpty())
								? String.valueOf(obj[127]).trim() : null;

				Integer creditDays = null;
				if (obj[128] != null && !obj[128].toString().trim().isEmpty()) {
					String lineNoStr = (String.valueOf(obj[128])).trim();
					creditDays = Integer.valueOf(lineNoStr);
				}

				BigDecimal paidAmount = null;
				if (obj[129] != null && !obj[129].toString().trim().isEmpty()) {
					String fobStr = String.valueOf(obj[129]).trim();
					paidAmount = new BigDecimal(fobStr);
					lineItem.setPaidAmount(
							paidAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				BigDecimal balanceAmount = null;
				if (obj[130] != null && !obj[130].toString().trim().isEmpty()) {
					String exportDutyStr = String.valueOf(obj[130]).trim();
					balanceAmount = new BigDecimal(exportDutyStr);
					lineItem.setBalanceAmount(balanceAmount.setScale(2,
							BigDecimal.ROUND_HALF_EVEN));
				}

				String paymentDueDate = (obj[131] != null
						&& !obj[131].toString().trim().isEmpty())
								? String.valueOf(obj[131]).trim() : null;
				LocalDate localPaymentDueDate = DateUtil
						.parseObjToDate(paymentDueDate);
				String accountDetail = (obj[132] != null
						&& !obj[132].toString().trim().isEmpty())
								? String.valueOf(obj[132]).trim() : null;

				String ecomGSTIN = (obj[133] != null
						&& !obj[133].toString().trim().isEmpty())
								? String.valueOf(obj[133]).trim() : null;

				String ecomTransactionId = (obj[134] != null
						&& !obj[134].toString().trim().isEmpty())
								? String.valueOf(obj[134]).trim() : null;
				String supportingDocURL = (obj[135] != null
						&& !obj[135].toString().trim().isEmpty())
								? String.valueOf(obj[135]).trim() : null;
				String supportingDocBase64 = (obj[136] != null
						&& !obj[136].toString().trim().isEmpty())
								? String.valueOf(obj[136]).trim() : null;
				String additionalInfo = (obj[137] != null
						&& !obj[137].toString().trim().isEmpty())
								? String.valueOf(obj[137]).trim() : null;
				String transactionType = (obj[138] != null
						&& !obj[138].toString().trim().isEmpty())
								? String.valueOf(obj[138]).trim() : null;
				String subSupplyType = (obj[139] != null
						&& !obj[139].toString().trim().isEmpty())
								? String.valueOf(obj[139]).trim() : null;

				String otherSupplyTypeDescription = (obj[140] != null
						&& !obj[140].toString().trim().isEmpty())
								? String.valueOf(obj[140]).trim() : null;
				String transporterID = (obj[141] != null
						&& !obj[141].toString().trim().isEmpty())
								? String.valueOf(obj[141]).trim() : null;
				String transporterName = (obj[142] != null
						&& !obj[142].toString().trim().isEmpty())
								? String.valueOf(obj[142]).trim() : null;
				String transportMode = (obj[143] != null
						&& !obj[143].toString().trim().isEmpty())
								? String.valueOf(obj[143]).trim() : null;
				String transportDocNumber = (obj[144] != null
						&& !obj[144].toString().trim().isEmpty())
								? String.valueOf(obj[144]).trim() : null;

				String transportDocDate = (obj[145] != null
						&& !obj[145].toString().trim().isEmpty())
								? String.valueOf(obj[145]).trim() : null;
				LocalDate localTransportDocDate = DateUtil
						.parseObjToDate(transportDocDate);

				BigDecimal distance = null;
				if (obj[146] != null && !obj[146].toString().trim().isEmpty()) {
					String distanceStr = String.valueOf(obj[146]).trim();
					distance = new BigDecimal(distanceStr);
					document.setDistance(
							distance.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}

				String vehicleNumber = (obj[147] != null
						&& !obj[147].toString().trim().isEmpty())
								? String.valueOf(obj[147]).trim() : null;

				String vehicleType = (obj[148] != null
						&& !obj[148].toString().trim().isEmpty())
								? String.valueOf(obj[148]).trim() : null;
				String returnPeriod = (obj[149] != null
						&& !obj[149].toString().trim().isEmpty())
								? String.valueOf(obj[149]).trim() : null;

				Integer derivedRetPeriod = GenUtil
						.convertTaxPeriodToInt(returnPeriod);
				if (derivedRetPeriod != null) {
					document.setDerivedTaxperiod(derivedRetPeriod);
					lineItem.setDerivedTaxperiod(derivedRetPeriod);
				} else {
					document.setDerivedTaxperiod(999999);
					lineItem.setDerivedTaxperiod(999999);
				}
				String originalDocType = (obj[150] != null
						&& !obj[150].toString().trim().isEmpty())
								? String.valueOf(obj[150]).trim() : null;

				String originalCustomerGstin = (obj[151] != null
						&& !obj[151].toString().trim().isEmpty())
								? String.valueOf(obj[151]).trim() : null;
				String diffPerFalg = (obj[152] != null
						&& !obj[152].toString().trim().isEmpty())
								? String.valueOf(obj[152]).trim()
								: GSTConstants.N;
				String sec7ofIgstFlag = (obj[153] != null
						&& !obj[153].toString().trim().isEmpty())
								? String.valueOf(obj[153]).trim() : null;

				String claimRefFlag = (obj[154] != null
						&& !obj[154].toString().trim().isEmpty())
								? String.valueOf(obj[154]).trim()
								: GSTConstants.N;
				String autoPopulateToRefund = (obj[155] != null
						&& !obj[155].toString().trim().isEmpty())
								? String.valueOf(obj[155]).trim()
								: GSTConstants.N;

				String crDrPreGst = (obj[156] != null
						&& !obj[156].toString().trim().isEmpty())
								? String.valueOf(obj[156]).trim()
								: GSTConstants.N;

				/*
				 * String tcsFlag = (obj[157] != null &&
				 * !obj[157].toString().trim().isEmpty()) ?
				 * String.valueOf(obj[157]).trim() : GSTConstants.N;
				 */
				String customerType = (obj[157] != null
						&& !obj[157].toString().trim().isEmpty())
								? String.valueOf(obj[157]).trim() : null;

				String customerCode = (obj[158] != null
						&& !obj[158].toString().trim().isEmpty())
								? String.valueOf(obj[158]).trim() : null;
				String productCode = (obj[159] != null
						&& !obj[159].toString().trim().isEmpty())
								? String.valueOf(obj[159]).trim() : null;

				String categoryOfProduct = (obj[160] != null
						&& !obj[160].toString().trim().isEmpty())
								? String.valueOf(obj[160]).trim() : null;

				String itcFlag = (obj[161] != null
						&& !obj[161].toString().trim().isEmpty())
								? String.valueOf(obj[161]).trim() : null;
				String stateAppCess = (obj[162] != null
						&& !obj[162].toString().trim().isEmpty())
								? String.valueOf(obj[162]).trim() : null;

				BigDecimal fob = null;
				if (obj[163] != null && !obj[163].toString().trim().isEmpty()) {
					String fobStr = String.valueOf(obj[163]).trim();
					fob = new BigDecimal(fobStr);
					lineItem.setFob(
							fob.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				BigDecimal exportDuty = null;
				if (obj[164] != null && !obj[164].toString().trim().isEmpty()) {
					String exportDutyStr = String.valueOf(obj[164]).trim();
					exportDuty = new BigDecimal(exportDutyStr);
					lineItem.setExportDuty(
							exportDuty.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				String exchangerate = (obj[165] != null
						&& !obj[165].toString().trim().isEmpty())
								? String.valueOf(obj[165]).trim() : null;

				String reasonForCrDrNote = (obj[166] != null
						&& !obj[166].toString().trim().isEmpty())
								? String.valueOf(obj[166]).trim() : null;
				String tcsFlagGST = (obj[167] != null
						&& !obj[167].toString().trim().isEmpty())
								? String.valueOf(obj[167]).trim() : null;
				BigDecimal tcsIgstAmt = null;
				if (obj[168] != null && !obj[168].toString().trim().isEmpty()) {
					String tcsIgstStr = String.valueOf(obj[168]).trim();
					tcsIgstAmt = new BigDecimal(tcsIgstStr);
					lineItem.setTcsAmount(
							tcsIgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				BigDecimal tcsCgstAmt = null;
				if (obj[169] != null && !obj[169].toString().trim().isEmpty()) {
					String tcsCgst = String.valueOf(obj[169]).trim();
					tcsCgstAmt = new BigDecimal(tcsCgst);
					lineItem.setTcsCgstAmount(
							tcsCgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				BigDecimal tcsSgstAmt = null;
				if (obj[170] != null && !obj[170].toString().trim().isEmpty()) {
					String tcsSgst = String.valueOf(obj[170]).trim();
					tcsSgstAmt = new BigDecimal(tcsSgst);
					lineItem.setTcsSgstAmount(
							tcsSgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				String tdsFlagGST = (obj[171] != null
						&& !obj[171].toString().trim().isEmpty())
								? String.valueOf(obj[171]).trim() : null;
				BigDecimal tdsIgstAmt = null;
				if (obj[172] != null && !obj[172].toString().trim().isEmpty()) {
					String tdsIgst = String.valueOf(obj[172]).trim();
					tdsIgstAmt = new BigDecimal(tdsIgst);
					lineItem.setTdsIgstAmount(
							tdsIgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				BigDecimal tdsCgstAmt = null;
				if (obj[173] != null && !obj[173].toString().trim().isEmpty()) {
					String tdsCgst = String.valueOf(obj[173]).trim();
					tdsCgstAmt = new BigDecimal(tdsCgst);
					lineItem.setTdsCgstAmount(
							tdsCgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				BigDecimal tdsSgstAmt = null;
				if (obj[174] != null && !obj[174].toString().trim().isEmpty()) {
					String tdsSgst = String.valueOf(obj[174]).trim();
					tdsSgstAmt = new BigDecimal(tdsSgst);
					lineItem.setTdsSgstAmount(
							tdsSgstAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				}
				String userId = (obj[175] != null
						&& !obj[175].toString().trim().isEmpty())
								? String.valueOf(obj[175]).trim() : null;
				String companyCode = (obj[176] != null
						&& !obj[176].toString().trim().isEmpty())
								? String.valueOf(obj[176]).trim() : null;
				String sourceIdentifier = (obj[177] != null
						&& !obj[177].toString().trim().isEmpty())
								? String.valueOf(obj[177]).trim() : null;
				String sourceFileName = (obj[178] != null
						&& !obj[178].toString().trim().isEmpty())
								? String.valueOf(obj[178]).trim() : null;
				String plantCode = (obj[179] != null
						&& !obj[179].toString().trim().isEmpty())
								? String.valueOf(obj[179]).trim() : null;
				String division = (obj[180] != null
						&& !obj[180].toString().trim().isEmpty())
								? String.valueOf(obj[180]).trim() : null;
				String subDivision = (obj[181] != null
						&& !obj[181].toString().trim().isEmpty())
								? String.valueOf(obj[181]).trim() : null;

				String location = (obj[182] != null
						&& !obj[182].toString().trim().isEmpty())
								? String.valueOf(obj[182]).trim() : null;
				String salesOrg = (obj[183] != null
						&& !obj[183].toString().trim().isEmpty())
								? String.valueOf(obj[183]) : null;
				String distChannel = (obj[184] != null
						&& !obj[184].toString().trim().isEmpty())
								? String.valueOf(obj[184]).trim() : null;
				String profitCentre1 = (obj[185] != null
						&& !obj[185].toString().trim().isEmpty())
								? String.valueOf(obj[185]).trim() : null;
				String profitCentre2 = (obj[186] != null
						&& !obj[186].toString().trim().isEmpty())
								? String.valueOf(obj[186]).trim() : null;
				String userAccess1 = (obj[187] != null
						&& !obj[187].toString().trim().isEmpty())
								? String.valueOf(obj[187]).trim() : null;
				String userAccess2 = (obj[188] != null
						&& !obj[188].toString().trim().isEmpty())
								? String.valueOf(obj[188]).trim() : null;
				String userAccess3 = (obj[189] != null
						&& !obj[189].toString().trim().isEmpty())
								? String.valueOf(obj[189]).trim() : null;
				String userAccess4 = (obj[190] != null
						&& !obj[190].toString().trim().isEmpty())
								? String.valueOf(obj[190]).trim() : null;
				String userAccess5 = (obj[191] != null
						&& !obj[191].toString().trim().isEmpty())
								? String.valueOf(obj[191]).trim() : null;
				String userAccess6 = (obj[192] != null
						&& !obj[192].toString().trim().isEmpty())
								? String.valueOf(obj[192]).trim() : null;
				String glAccessableValue = (obj[193] != null
						&& !obj[193].toString().trim().isEmpty())
								? String.valueOf(obj[193]).trim() : null;
				String glCodeIgst = (obj[194] != null
						&& !obj[194].toString().trim().isEmpty())
								? String.valueOf(obj[194]).trim() : null;
				String glCodeCgst = (obj[195] != null
						&& !obj[195].toString().trim().isEmpty())
								? String.valueOf(obj[195]).trim() : null;
				String glCodeSgst = (obj[196] != null
						&& !obj[196].toString().trim().isEmpty())
								? String.valueOf(obj[196]).trim() : null;
				String glAdvaloremCess = (obj[197] != null
						&& !obj[197].toString().trim().isEmpty())
								? String.valueOf(obj[197]).trim() : null;
				String glAdvaloremSpecificCess = (obj[198] != null
						&& !obj[198].toString().trim().isEmpty())
								? String.valueOf(obj[198]).trim() : null;
				String glStateCessAdvalore = (obj[199] != null
						&& !obj[199].toString().trim().isEmpty())
								? String.valueOf(obj[199]).trim() : null;
				String glStateCess = (obj[200] != null
						&& !obj[200].toString().trim().isEmpty())
								? String.valueOf(obj[200]).trim() : null;
				String glPostingDate = (obj[201] != null
						&& !obj[201].toString().trim().isEmpty())
								? String.valueOf(obj[201]).trim() : null;
				LocalDate localGlPostingDate = DateUtil
						.parseObjToDate(glPostingDate);
				String salesOrderNo = (obj[202] != null
						&& !obj[202].toString().trim().isEmpty())
								? String.valueOf(obj[202]).trim() : null;
				String ewbNo = (obj[203] != null
						&& !obj[203].toString().trim().isEmpty())
								? String.valueOf(obj[203]).trim() : null;

				String ewbDate = (obj[204] != null
						&& !obj[204].toString().trim().isEmpty())
								? String.valueOf(obj[204]).trim() : null;
				LocalDate localEwbDate = DateUtil.parseObjToDate(ewbDate);
				String accountingVochar = (obj[205] != null
						&& !obj[205].toString().trim().isEmpty())
								? String.valueOf(obj[205]).trim() : null;
				String accountingVocharDate = (obj[206] != null
						&& !obj[206].toString().trim().isEmpty())
								? String.valueOf(obj[206]).trim() : null;
				LocalDate localAccountingVocharDate = DateUtil
						.parseObjToDate(accountingVocharDate);
				String documentRefNumber = (obj[207] != null
						&& !obj[207].toString().trim().isEmpty())
								? String.valueOf(obj[207]).trim() : null;
				String customerTan = (obj[208] != null
						&& !obj[208].toString().trim().isEmpty())
								? String.valueOf(obj[208]).trim() : null;
				String userDefinedField1 = (obj[209] != null
						&& !obj[209].toString().trim().isEmpty())
								? String.valueOf(obj[209]).trim() : null;
				String userDefinedField2 = (obj[210] != null
						&& !obj[210].toString().trim().isEmpty())
								? String.valueOf(obj[210]).trim() : null;
				String userDefinedField3 = (obj[211] != null
						&& !obj[211].toString().trim().isEmpty())
								? String.valueOf(obj[211]).trim() : null;
				String userDefinedField4 = (obj[212] != null
						&& !obj[212].toString().trim().isEmpty())
								? String.valueOf(obj[212]).trim() : null;
				String userDefinedField5 = (obj[213] != null
						&& !obj[213].toString().trim().isEmpty())
								? String.valueOf(obj[213]).trim() : null;
				String userDefinedField6 = (obj[214] != null
						&& !obj[214].toString().trim().isEmpty())
								? String.valueOf(obj[214]).trim() : null;
				String userDefinedField7 = (obj[215] != null
						&& !obj[215].toString().trim().isEmpty())
								? String.valueOf(obj[215]).trim() : null;
				String userDefinedField8 = (obj[216] != null
						&& !obj[216].toString().trim().isEmpty())
								? String.valueOf(obj[216]).trim() : null;
				String userDefinedField9 = (obj[217] != null
						&& !obj[217].toString().trim().isEmpty())
								? String.valueOf(obj[217]).trim() : null;
				String userDefinedField10 = (obj[218] != null
						&& !obj[218].toString().trim().isEmpty())
								? String.valueOf(obj[218]).trim() : null;
				String userDefinedField11 = (obj[219] != null
						&& !obj[219].toString().trim().isEmpty())
								? String.valueOf(obj[219]).trim() : null;
				String userDefinedField12 = (obj[220] != null
						&& !obj[220].toString().trim().isEmpty())
								? String.valueOf(obj[220]).trim() : null;
				String userDefinedField13 = (obj[221] != null
						&& !obj[221].toString().trim().isEmpty())
								? String.valueOf(obj[221]).trim() : null;
				String userDefinedField14 = (obj[222] != null
						&& !obj[222].toString().trim().isEmpty())
								? String.valueOf(obj[222]).trim() : null;
				String userDefinedField15 = (obj[223] != null
						&& !obj[223].toString().trim().isEmpty())
								? String.valueOf(obj[223]).trim() : null;

				String userDefinedField16 = (obj[224] != null
						&& !obj[224].toString().trim().isEmpty())
								? String.valueOf(obj[224]).trim() : null;
				String userDefinedField17 = (obj[225] != null
						&& !obj[225].toString().trim().isEmpty())
								? String.valueOf(obj[225]).trim() : null;
				String userDefinedField18 = (obj[226] != null
						&& !obj[226].toString().trim().isEmpty())
								? String.valueOf(obj[226]).trim() : null;
				String userDefinedField19 = (obj[227] != null
						&& !obj[227].toString().trim().isEmpty())
								? String.valueOf(obj[227]).trim() : null;
				String userDefinedField20 = (obj[228] != null
						&& !obj[228].toString().trim().isEmpty())
								? String.valueOf(obj[228]).trim() : null;
				String userDefinedField21 = (obj[229] != null
						&& !obj[229].toString().trim().isEmpty())
								? String.valueOf(obj[229]).trim() : null;
				String userDefinedField22 = (obj[230] != null
						&& !obj[230].toString().trim().isEmpty())
								? String.valueOf(obj[230]).trim() : null;
				String userDefinedField23 = (obj[231] != null
						&& !obj[231].toString().trim().isEmpty())
								? String.valueOf(obj[231]).trim() : null;
				String userDefinedField24 = (obj[232] != null
						&& !obj[232].toString().trim().isEmpty())
								? String.valueOf(obj[232]).trim() : null;
				String userDefinedField25 = (obj[233] != null
						&& !obj[233].toString().trim().isEmpty())
								? String.valueOf(obj[233]).trim() : null;
				String userDefinedField26 = (obj[234] != null
						&& !obj[234].toString().trim().isEmpty())
								? String.valueOf(obj[234]).trim() : null;
				String userDefinedField27 = (obj[235] != null
						&& !obj[235].toString().trim().isEmpty())
								? String.valueOf(obj[235]).trim() : null;
				/*
				 * String userDefinedField28 = (obj[236] != null &&
				 * !obj[236].toString().trim().isEmpty()) ?
				 * String.valueOf(obj[236]).trim() : null;
				 */

				BigDecimal userDefinedField28 = null;
				if (obj[236] != null && !obj[236].toString().trim().isEmpty()) {
					String userDefinedField28Int = String.valueOf(obj[236])
							.trim();
					userDefinedField28 = new BigDecimal(userDefinedField28Int);
					LOGGER.debug("UDF 28 {}", userDefinedField28);
					lineItem.setUserDefinedField28(userDefinedField28
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
					document.setUserDefinedField28(userDefinedField28
							.setScale(2, BigDecimal.ROUND_HALF_EVEN));
				}
				String userDefinedField29 = (obj[237] != null
						&& !obj[237].toString().trim().isEmpty())
								? String.valueOf(obj[237]).trim() : null;
				String userDefinedField30 = (obj[238] != null
						&& !obj[238].toString().trim().isEmpty())
								? String.valueOf(obj[238]).trim() : null;
				/**
				 * Start - Outward Document Structural Validation Error
				 * Correction Implementation
				 */
				// File ID and Id are not null only in case of Inward Doc
				// Structural Validation Error Correction
				String idStr = null;
				String fileIdStr = null;
				if (obj.length > 239) {
					fileIdStr = (obj[240] != null
							&& !obj[240].toString().trim().isEmpty())
									? String.valueOf(obj[240]) : null;
					idStr = (obj[241] != null
							&& !obj[241].toString().trim().isEmpty())
									? String.valueOf(obj[241]) : null;
				}
				/**
				 * End - Outward Document Structural Validation Error Correction
				 * Implementation
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
				lineItem.setBatchExpiryDate(localBatchExpiryDate);
				lineItem.setWarrantyDate(localWarranDate);
				lineItem.setOrderLineReference(orderLineRef);
				lineItem.setAttributeName(attributeName);
				lineItem.setAttributeValue(attributeValue);
				lineItem.setOriginCountry(originCountry);
				lineItem.setUom(uom);
				lineItem.setPreceedingInvoiceNumber(preceedingInvoiceNumber);
				lineItem.setPreceedingInvoiceDate(localPreceedingInvoiceDate);
				lineItem.setInvoiceReference(otherReference);
				lineItem.setReceiptAdviceReference(receiptAdviceReference);
				lineItem.setReceiptAdviceDate(localreceiptAdviceDate);
				lineItem.setTenderReference(tenderReference);
				lineItem.setContractReference(contractRef);
				lineItem.setExternalReference(externalRef);
				lineItem.setProjectReference(projectRef);
				lineItem.setCustomerPOReferenceNumber(
						customerPOReferenceNumber);
				lineItem.setCustomerPOReferenceDate(
						localcustomerPOReferenceDate);
				lineItem.setSupportingDocURL(supportingDocURL);
				lineItem.setSupportingDocBase64(supportingDocBase64);
				lineItem.setAdditionalInformation(additionalInfo);
				lineItem.setItemCode(productCode);
				lineItem.setItemCategory(categoryOfProduct);
				lineItem.setItcFlag(itcFlag);
				lineItem.setStateApplyingCess(stateAppCess);
				lineItem.setCrDrReason(reasonForCrDrNote);
				lineItem.setGlCodeTaxableValue(glAccessableValue);
				lineItem.setDocReferenceNumber(documentRefNumber);
				lineItem.setPlantCode(plantCode);
				lineItem.setSupplierBuildingName(supAddress2);
				lineItem.setDispatcherBuildingName(dispatcherAddress2);
				lineItem.setShipToBuildingName(shipToAddress2);
				lineItem.setSupplierEmail(supplierEmail);
				lineItem.setCustomerEmail(customerEmail);
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
				lineItem.setUserDefinedField29(userDefinedField29);
				lineItem.setUserDefinedField30(userDefinedField30);
				lineItem.setUserId(userId);

				if (fileStatus != null) {
					document.setAcceptanceId(fileStatus.getId());
					lineItem.setAcceptanceId(fileStatus.getId());
				}
				lineItems.add(lineItem); // Add Line Items
				document.setIrn(irn);
				document.setIrnDate(localIrnDate != null
						? localIrnDate.atStartOfDay() : null);
				document.setTcsFlag(tcsFlagGST);
				document.setTdsFlag(tdsFlagGST);
				document.setTaxScheme(taxScheme);
				document.setCancellationReason(canReason);
				document.setCancellationRemarks(canRemarks);
				document.setDocCategory(docCategory);
				document.setDocType(docType);
				document.setDocNo(docNo);
				document.setDocDate(localDocDate);
				document.setReverseCharge(reverseChargeFlag);
				document.setCustomerPANOrAadhaar(customerPanOrAdhar);
				document.setEcomTransactionID(ecomTransactionId);
				document.setSgstin(sgstin);
				document.setSupplierLegalName(supplierLegalName);
				document.setSupplierTradeName(supplierTradeName);
				document.setSupplierBuildingNumber(supAddress1);
				document.setSupplierBuildingName(supAddress2);
				document.setSupplierLocation(supplierLocation);
				document.setSupplierPincode(supplierPincode);
				
				document.setSupplierStateCode(supplierStateCode);
				document.setSupplierPhone(supplierPhone);
				document.setSupplierEmail(supplierEmail);
				document.setCgstin(customerGstin);
				document.setCustomerTradeName(customerTradeName);
				document.setCustOrSuppName(customerLegalName);
				document.setCustOrSuppAddress1(customerAddress1);
				document.setCustOrSuppAddress2(customerAddress2);
				document.setCustOrSuppAddress4(customerLocation);
				document.setCustomerPincode(customerPincode);
				document.setBillToState(customerStateCode);
				
				document.setPos(billingPos);
				document.setCustomerPhone(customerPhone);
				document.setCustomerEmail(customerEmail);
				document.setDispatcherGstin(dispatcherGstin);
				document.setDispatcherTradeName(dispatcherTradeName);
				document.setDispatcherBuildingNumber(dispatcherAddress1);
				document.setDispatcherBuildingName(dispatcherAddress2);
				document.setDispatcherLocation(dispatcherLocation);
				document.setDispatcherPincode(dispatcherPinCode);
				 
				document.setDispatcherStateCode(dispatcherStateCode);
				document.setShipToGstin(shipToGstin);
				document.setShipToTradeName(shipToTradeName);
				document.setShipToLegalName(shipToLegalName);
				document.setShipToBuildingNumber(shipToAddress1);
				document.setShipToBuildingName(shipToAddress2);
				document.setShipToLocation(shipToLocation);
				 
				document.setShipToState(shipToStateCode);
				document.setShipToPincode(shipToPincode);
				document.setTotalInvoiceValueInWords(totalInvValue);
				document.setTcsFlagIncomeTax(tcsFlagIncomeTax);
				document.setForeignCurrency(currencyCode);
				document.setCountryCode(countryCode);
				document.setInvoiceRemarks(invoiceRemarks);
				document.setInvoicePeriodStartDate(localInvoicePeriodStartDate);
				document.setInvoicePeriodEndDate(localInvoicePeriodEndDate);
				document.setPayeeName(payeeName);
				document.setModeOfPayment(modeOfPayment);
				document.setBranchOrIfscCode(branchOrIFSCCode);
				document.setPaymentTerms(paymentTerms);
				document.setPaymentInstruction(paymentInstruction);
				document.setCreditTransfer(creditTransfer);
				document.setDirectDebit(directDebit);
				document.setCreditDays(creditDays);
				document.setPaymentDueDate(localPaymentDueDate);
				document.setAccountDetail(accountDetail);
				document.setEgstin(ecomGSTIN);
				document.setTransactionType(transactionType);
				document.setSubSupplyType(subSupplyType);
				document.setOtherSupplyTypeDescription(
						otherSupplyTypeDescription);
				document.setTransporterID(transporterID);
				document.setTransporterName(transporterName);
				document.setTransportMode(transportMode);
				document.setTransportDocNo(transportDocNumber);
				document.setTransportDocDate(localTransportDocDate);
				document.setVehicleNo(vehicleNumber);
				document.setVehicleType(vehicleType);
				document.setTaxperiod(returnPeriod);
				lineItem.setTaxperiod(returnPeriod);
				document.setOrigDocType(originalDocType);
				document.setOrigCgstin(originalCustomerGstin);
				document.setDiffPercent(diffPerFalg);
				document.setSection7OfIgstFlag(sec7ofIgstFlag);
				document.setClaimRefundFlag(claimRefFlag);
				document.setAutoPopToRefundFlag(autoPopulateToRefund);
				document.setCrDrPreGst(crDrPreGst);
				document.setCustOrSuppType(customerType);
				 
				document.setCustOrSuppCode(customerCode);
				document.setExchangeRate(exchangerate);
				document.setStateApplyingCess(stateAppCess);
				document.setUserId(userId);
				document.setSourceFileName(sourceFileName);
				document.setSourceIdentifier(sourceIdentifier);
				document.setCompanyCode(companyCode);
				document.setGlPostingDate(localGlPostingDate);
				document.setSalesOrderNumber(salesOrderNo);
				document.seteWayBillNo(ewbNo);
				document.seteWayBillDate(localEwbDate != null
						? localEwbDate.atStartOfDay() : null);
				document.setAccountingVoucherNumber(accountingVochar);
				document.setAccountingVoucherDate(localAccountingVocharDate);
				document.setCustomerTan(customerTan);
				document.setPortCode(portCode);
				
				if (!Strings.isNullOrEmpty(shippingBillNo)) {

					String str = shippingBillNo.toString().trim();
				    if (str.length() > 7) {
				        str = str.substring(0, 7);
				    }
					
				    document.setShippingBillNo(str);
				}
				//document.setShippingBillNo(shippingBillNo);
				document.setShippingBillDate(localShippingBillDate);

				document.setProfitCentre(profitCentre1);
				document.setProfitCentre2(profitCentre2);
				document.setDivision(division);
				document.setLocation(location);
				document.setSalesOrgnization(salesOrg);
				document.setDistributionChannel(distChannel);
				lineItem.setUserAccess1(userAccess1);
				lineItem.setUserAccess2(userAccess2);
				lineItem.setUserAccess3(userAccess3);
				lineItem.setUserAccess4(userAccess4);
				lineItem.setUserAccess5(userAccess5);
				lineItem.setUserAccess6(userAccess6);
				document.setGlCodeIgst(glCodeIgst);
				document.setGlCodeCgst(glCodeCgst);
				document.setGlCodeSgst(glCodeSgst);
				document.setGlCodeAdvCess(glAdvaloremCess);
				document.setGlCodeSpCess(glAdvaloremSpecificCess);
				document.setGlCodeStateCess(glStateCessAdvalore);
				document.setGlStateCessSpecific(glStateCess);
				document.setUserDefinedField29(userDefinedField29);
				document.setUserDefinedField30(userDefinedField30);

				String source = fileStatus.getSource();
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
				document.setPortCode(portCode);
				document.setDocKey(key);
				document.setFinYear(finYear);

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
			}
			document.setLineItems(lineItems);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			document.setCreatedDate(convertNow);
			document.setCreatedBy(userName);
			documents.add(document);

		});
		return documents;
	}

	/*
	 * private String truncation500Value(Object obj) { String value = obj !=
	 * null && !obj.toString().isEmpty() ? String.valueOf(obj.toString().trim())
	 * : null; if (value != null && value.length() > 500) { value =
	 * value.substring(0, 500); } return value; }
	 * 
	 * private String truncation1000Value(Object obj) { String value = obj !=
	 * null && !obj.toString().isEmpty() ? String.valueOf(obj.toString().trim())
	 * : null; if (value != null && value.length() > 1000) { value =
	 * value.substring(0, 1000); } return value; }
	 * 
	 * private String truncation200Value(Object obj) { String value = obj !=
	 * null && !obj.toString().isEmpty() ? String.valueOf(obj.toString().trim())
	 * : null; if (value != null && value.length() > 200) { value =
	 * value.substring(0, 200); } return value; }
	 * 
	 * private String truncation20Value(Object obj) { String value = obj != null
	 * && !obj.toString().isEmpty() ?
	 * String.valueOf(obj.toString().trim()).trim() : null; if (value != null &&
	 * value.length() > 20) { value = value.substring(0, 20); } return value; }
	 * 
	 * private String truncation30Value(Object obj) { String value = obj != null
	 * && !obj.toString().isEmpty() ? String.valueOf(obj.toString().trim()) :
	 * null; if (value != null && value.length() > 30) { value =
	 * value.substring(0, 30); } return value; }
	 * 
	 * private String truncation300Value(Object obj) { String value = obj !=
	 * null && !obj.toString().isEmpty() ? String.valueOf(obj.toString().trim())
	 * : null; if (value != null && value.length() > 300) { value =
	 * value.substring(0, 300); } return value; }
	 * 
	 * private String truncation50Value(Object obj) { String value = obj != null
	 * && !obj.toString().isEmpty() ? String.valueOf(obj.toString().trim()) :
	 * null; if (value != null && value.length() > 50) { value =
	 * value.substring(0, 50); } return value; }
	 * 
	 * 
	 * private String truncation10Value(Object obj) { String value = obj != null
	 * && !obj.toString().isEmpty() ?
	 * String.valueOf(obj.toString().trim()):null; if(value != null &&
	 * value.length() > 10){ value = value.substring(0,10); } return value; }
	 * 
	 * private String truncation100Value(Object obj) { String value = obj !=
	 * null && !obj.toString().isEmpty() ? String.valueOf(obj.toString().trim())
	 * : null; if (value != null && value.length() > 100) { value =
	 * value.substring(0, 100); } return value;
	 * 
	 * }
	 * 
	 * private String truncation60Value(Object obj) { String value = obj != null
	 * && !obj.toString().isEmpty() ? String.valueOf(obj.toString().trim()) :
	 * null; if (value != null && value.length() > 60) { value =
	 * value.substring(0, 60); } return value; }
	 */
}
