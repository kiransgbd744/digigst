package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.app.data.entities.client.AnxOutwardTransDocLineItemError;

/**
 * This class is responsible for converting list of documents from json request
 * to List of Object array
 * 
 * @author Mohana.Dasari
 *
 */
@Component("OutwardDocSvErrListToArrConversion")
public class OutwardDocSvErrListToArrConversion {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardDocSvErrListToArrConversion.class);
	
	public List<Object[]> convertOutwardDocSvErrListToArrObj(
			List<Anx1OutWardErrHeader> correctedErrDocs) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Entered convertOutwardDocSvErrListToArrObj ");
		}
		
		List<Object[]> docObjList = new ArrayList<>();
		if (correctedErrDocs != null && !correctedErrDocs.isEmpty()) {
			correctedErrDocs.forEach(correctedErDoc -> {
				List<AnxOutwardTransDocLineItemError> lineItems = correctedErDoc
						.getLineItems();
				if (lineItems != null && !lineItems.isEmpty()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(" Line Items Size "+lineItems.size());
					}
					lineItems.forEach(lineItem -> {
						// There are only 109 fields in Outward Trans Document,
						// 2 additional fields in 111 fields include id and
						// fileId for SV Error  Correction
						Object[] objArr = new Object[112];
						//set header fields
						objArr[0] = correctedErDoc.getUserId();
						objArr[1] = correctedErDoc.getSourceFileName();
						objArr[2] = correctedErDoc.getProfitCentre();
						objArr[3] = correctedErDoc.getPlantCode();
						objArr[4] = correctedErDoc.getDivision();
						objArr[5] = correctedErDoc.getLocation();
						objArr[6] = correctedErDoc.getSalesOrgnization();
						objArr[7] = correctedErDoc.getDistributionChannel();
						objArr[8] = correctedErDoc.getUserAccess1();
						objArr[9] = correctedErDoc.getUserAccess2();
						objArr[10] = correctedErDoc.getUserAccess3();
						objArr[11] = correctedErDoc.getUserAccess4();
						objArr[12] = correctedErDoc.getUserAccess5();
						objArr[13] = correctedErDoc.getUserAccess6();
						
						objArr[21] = correctedErDoc.getTaxperiod();
						objArr[22] = correctedErDoc.getSgstin();
						objArr[23] = correctedErDoc.getDocType();
						
						objArr[25] = correctedErDoc.getDocNo();
						objArr[26] = correctedErDoc.getDocDate();
						objArr[27] = correctedErDoc.getOrigDocType();
						
						objArr[30] = correctedErDoc.getIsCrDrPreGst();
						
						objArr[32] = correctedErDoc.getCgstin();
						objArr[33] = correctedErDoc.getCustOrSuppType();
						objArr[34] = correctedErDoc.getDiffPercent();
						objArr[35] = correctedErDoc.getOrigCgstin();
						objArr[36] = correctedErDoc.getCustOrSuppName();
						objArr[37] = correctedErDoc.getCustOrSuppCode();
						objArr[38] = correctedErDoc.getCustOrSuppAddress1();
						objArr[39] = correctedErDoc.getCustOrSuppAddress2();
						objArr[40] = correctedErDoc.getCustOrSuppAddress3();
						objArr[41] = correctedErDoc.getCustOrSuppAddress4();
						objArr[42] = correctedErDoc.getBillToState();
						objArr[43] = correctedErDoc.getShipToState();
						objArr[44] = correctedErDoc.getPos();
						objArr[45] = correctedErDoc.getStateApplyingCess();
						objArr[46] = correctedErDoc.getPortCode();
						objArr[47] = correctedErDoc.getShippingBillNo();
						objArr[48] = correctedErDoc.getShippingBillDate();
						
						objArr[57] = correctedErDoc.getSection7OfIgstFlag();
						
						objArr[82] = correctedErDoc.getReverseCharge();
						objArr[83] = correctedErDoc.getTcsFlag();
						objArr[84] = correctedErDoc.getEgstin();
						
						objArr[87] = correctedErDoc.getClaimRefundFlag();
						objArr[88] = correctedErDoc.getAutoPopToRefundFlag();
						
						objArr[90] = correctedErDoc
								.getAccountingVoucherNumber();
						objArr[91] = correctedErDoc.getAccountingVoucherDate();
						objArr[107] = correctedErDoc.getEWayBillNo();
						objArr[108] = correctedErDoc.getEWayBillDate();
						objArr[109] = correctedErDoc.getId();
						objArr[110] = correctedErDoc.getAcceptanceId();
						objArr[111] = correctedErDoc.getDocId();
						
						//set line item fields
						objArr[14] = lineItem.getGlCodeTaxableValue();
						objArr[15] = lineItem.getGlCodeIgst();
						objArr[16] = lineItem.getGlCodeCgst();
						objArr[17] = lineItem.getGlCodeSgst();
						objArr[18] = lineItem.getGlCodeAdvCess();
						objArr[19] = lineItem.getGlCodeSpCess();
						objArr[20] = lineItem.getGlCodeStateCess();
						
						objArr[24] = lineItem.getSupplyType();
						
						objArr[28] = lineItem.getOrigDocNo();
						objArr[29] = lineItem.getOrigDocDate();
						
						objArr[31] = lineItem.getLineNo();
								
						objArr[49] = lineItem.getFob();
						objArr[50] = lineItem.getExportDuty();
						objArr[51] = lineItem.getHsnSac();
						objArr[52] = lineItem.getItemCode();
						objArr[53] = lineItem.getItemDescription();
						objArr[54] = lineItem.getItemCategory();
						objArr[55] = lineItem.getUom();
						objArr[56] = lineItem.getQty();
						
						objArr[58] = lineItem.getTaxableValue();
						objArr[59] = lineItem.getIgstRate();
						objArr[60] = lineItem.getIgstAmount();
						objArr[61] = lineItem.getCgstRate();
						objArr[62] = lineItem.getCgstAmount();
						objArr[63] = lineItem.getSgstRate();
						objArr[64] = lineItem.getSgstAmount();
						objArr[65] = lineItem.getCessRateAdvalorem();
						objArr[66] = lineItem.getCessAmountAdvalorem();
						objArr[67] = lineItem.getCessRateSpecific();
						objArr[68] = lineItem.getCessAmountAdvalorem();
						objArr[69] = lineItem.getStateCessRate();
						objArr[70] = lineItem.getStateCessAmount();
						objArr[71] = lineItem.getOtherValues();
						objArr[72] = lineItem.getLineItemAmt();
						objArr[73] = lineItem.getAdjustmentRefNo();
						objArr[74] = lineItem.getAdjustmentRefDate();
						objArr[75] = lineItem.getAdjustedTaxableValue();
						objArr[76] = lineItem.getAdjustedIgstAmt();
						objArr[77] = lineItem.getAdjustedCgstAmt();
						objArr[78] = lineItem.getAdjustedSgstAmt();
						objArr[79] = lineItem.getAdjustedCessAmtAdvalorem();
						objArr[80] = lineItem.getAdjustedCessAmtSpecific();
						objArr[81] = lineItem.getAdjustedStateCessAmt();
						
						objArr[85] = lineItem.getTcsAmount();
						objArr[86] = lineItem.getItcFlag();
						
						objArr[89] = lineItem.getCrDrReason();
						
						objArr[92] = lineItem.getUserdefinedfield1();
						objArr[93] = lineItem.getUserdefinedfield2();
						objArr[94] = lineItem.getUserdefinedfield3();
						objArr[95] = lineItem.getUserDefinedField4();
						objArr[96] = lineItem.getUserDefinedField5();
						objArr[97] = lineItem.getUserDefinedField6();
						objArr[98] = lineItem.getUserDefinedField7();
						objArr[99] = lineItem.getUserDefinedField8();
						objArr[100] = lineItem.getUserDefinedField9();
						objArr[101] = lineItem.getUserDefinedField10();
						objArr[102] = lineItem.getUserDefinedField11();
						objArr[103] = lineItem.getUserDefinedField12();
						objArr[104] = lineItem.getUserDefinedField13();
						objArr[105] = lineItem.getUserDefinedField14();
						objArr[106] = lineItem.getUserDefinedField15();
						docObjList.add(objArr);
					});
				}
			});
		}
		
		if (LOGGER.isDebugEnabled()) {
			if (docObjList != null && !docObjList.isEmpty()) {
				docObjList.forEach(docObj -> {
					LOGGER.debug(" docObj " + docObj);
				});
			}
			LOGGER.debug(" Exiting convertOutwardDocSvErrListToArrObj ");
		}
		return docObjList;
	}
}
