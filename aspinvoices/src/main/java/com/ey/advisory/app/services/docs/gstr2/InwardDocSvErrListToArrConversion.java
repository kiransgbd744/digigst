package com.ey.advisory.app.services.docs.gstr2;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.Anx2InwardErrorItemEntity;

/**
 * This class is responsible for converting list of documents from json request
 * to List of Object array
 * 
 * @author Mohana.Dasari
 *
 */
@Component("InwardDocSvErrListToArrConversion")
public class InwardDocSvErrListToArrConversion {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardDocSvErrListToArrConversion.class);

	/**
	 * 
	 * @param correctedErrDocs
	 * @return
	 */
	public List<Object[]> convertInwardDocSvErrListToArrObj(
			List<Anx2InwardErrorHeaderEntity> correctedErrDocs) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Entered convertInwardDocSvErrListToArrObj ");
		}
		List<Object[]> docObjList = new ArrayList<>();

		if (correctedErrDocs != null && !correctedErrDocs.isEmpty()) {
			correctedErrDocs.forEach(correctedErrDoc -> {
				// Line Items
				List<Anx2InwardErrorItemEntity> lineItems = correctedErrDoc
						.getLineItems();
				if (lineItems != null && !lineItems.isEmpty()) {
					lineItems.forEach(lineItem -> {
						// There are only 115 fields in Inward Trans Document,
						// 117 fields include id and fileId for SV Error
						// Correction 
						Object[] objArr = new Object[117];
						//set header fields
						objArr[0] = correctedErrDoc.getUserId();
						objArr[1] = correctedErrDoc.getSourceFileName();
						objArr[2] = correctedErrDoc.getProfitCentre();
						objArr[3] = correctedErrDoc.getPlantCode();
						objArr[4] = correctedErrDoc.getDivision();
						objArr[5] = correctedErrDoc.getLocation();
						objArr[6] = correctedErrDoc.getPurchaseOrganization();
						objArr[7] = correctedErrDoc.getUserAccess1();
						objArr[8] = correctedErrDoc.getUserAccess2();
						objArr[9] = correctedErrDoc.getUserAccess3();
						objArr[10] = correctedErrDoc.getUserAccess4();
						objArr[11] = correctedErrDoc.getUserAccess5();
						objArr[12] = correctedErrDoc.getUserAccess6();
						objArr[20] = correctedErrDoc.getTaxperiod();
						objArr[21] = correctedErrDoc.getCgstin();
						objArr[22] = correctedErrDoc.getDocType();

						objArr[24] = correctedErrDoc.getDocNo();
						objArr[25] = correctedErrDoc.getDocDate();
						objArr[28] = correctedErrDoc.getCrDrPreGst();
						objArr[30] = correctedErrDoc.getSgstin();
						objArr[31] = correctedErrDoc.getSupplierType();
						objArr[32] = correctedErrDoc.getDiffPercent();
						objArr[33] = correctedErrDoc.getOrigSgstin();
						objArr[34] = correctedErrDoc.getSupplierLegalName();
						objArr[35] = correctedErrDoc.getCustOrSuppCode();
						objArr[36] = correctedErrDoc.getCustOrSuppAddress1();
						objArr[37] = correctedErrDoc.getCustOrSuppAddress2();
						objArr[38] = correctedErrDoc.getCustOrSuppAddress3();
						objArr[39] = correctedErrDoc.getCustOrSuppAddress4();
						objArr[40] = correctedErrDoc.getPos();
						objArr[41] = correctedErrDoc.getStateApplyingCess();
						objArr[42] = correctedErrDoc.getPortCode();
						objArr[43] = correctedErrDoc.getBillOfEntryNo();
						objArr[44] = correctedErrDoc.getBillOfEntryDate();

						objArr[53] = correctedErrDoc.getSection7OfIgstFlag();

						objArr[69] = correctedErrDoc.getClaimRefundFlag();
						objArr[70] = correctedErrDoc.getAutoPopToRefundFlag();

						objArr[80] = correctedErrDoc.getReverseCharge();

						objArr[87] = correctedErrDoc.getItcEntitlement();

						objArr[92] = correctedErrDoc.getPostingDate();

						objArr[113] = correctedErrDoc.getEWayBillNo();
						objArr[114] = correctedErrDoc.getEWayBillDate();
						objArr[115] = correctedErrDoc.getId();//primary key
						objArr[116] = correctedErrDoc.getAcceptanceId();//fileid

						//set line item fields
						objArr[13] = lineItem.getGlCodeTaxableValue();
						objArr[14] = lineItem.getGlCodeIgst();
						objArr[15] = lineItem.getGlCodeCgst();
						objArr[16] = lineItem.getGlCodeSgst();
						objArr[17] = lineItem.getGlCodeAdvCess();
						objArr[18] = lineItem.getGlCodeSpCess();
						objArr[19] = lineItem.getGlCodeStateCess();

						objArr[23] = lineItem.getSupplyType();

						objArr[26] = lineItem.getOrigDocNo();
						objArr[27] = lineItem.getOrigDocDate();
						objArr[29] = lineItem.getLineNo();

						objArr[45] = lineItem.getCifValue();
						objArr[46] = lineItem.getCustomDuty();
						objArr[47] = lineItem.getHsnSac();
						objArr[48] = lineItem.getItemCode();
						objArr[49] = lineItem.getItemDescription();
						objArr[50] = lineItem.getItemCategory();
						objArr[51] = lineItem.getUom();
						objArr[52] = lineItem.getQty();

						objArr[54] = lineItem.getTaxableValue();
						objArr[55] = lineItem.getIgstRate();
						objArr[56] = lineItem.getIgstAmount();
						objArr[57] = lineItem.getCgstRate();
						objArr[58] = lineItem.getCgstAmount();
						objArr[59] = lineItem.getSgstRate();
						objArr[60] = lineItem.getSgstAmount();
						objArr[61] = lineItem.getCessRateAdvalorem();
						objArr[62] = lineItem.getCessAmountAdvalorem();
						objArr[63] = lineItem.getCessRateSpecific();
						objArr[64] = lineItem.getCessAmountSpecific();
						objArr[65] = lineItem.getStateCessRate();
						objArr[66] = lineItem.getStateCessAmount();
						objArr[67] = lineItem.getOtherValues();
						objArr[68] = lineItem.getLineItemAmt();

						objArr[71] = lineItem.getAdjustmentRefNo();
						objArr[72] = lineItem.getAdjustmentRefDate();
						objArr[73] = lineItem.getAdjustedTaxableValue();
						objArr[74] = lineItem.getAdjustedIgstAmt();
						objArr[75] = lineItem.getAdjustedCgstAmt();
						objArr[76] = lineItem.getAdjustedSgstAmt();
						objArr[77] = lineItem.getAdjustedCessAmtAdvalorem();
						objArr[78] = lineItem.getAdjustedCessAmtSpecific();
						objArr[79] = lineItem.getAdjustedStateCessAmt();

						objArr[81] = lineItem.getEligibilityIndicator();
						objArr[82] = lineItem.getCommonSupplyIndicator();
						objArr[83] = lineItem.getAvailableIgst();
						objArr[84] = lineItem.getAvailableCgst();
						objArr[85] = lineItem.getAvailableSgst();
						objArr[86] = lineItem.getAvailableCess();

						objArr[88] = lineItem.getItcReversalIdentifier();
						objArr[89] = lineItem.getCrDrReason();
						objArr[90] = lineItem.getPurchaseVoucherNum();
						objArr[91] = lineItem.getPurchaseVoucherDate();

						objArr[93] = lineItem.getPaymentVoucherNumber();
						objArr[94] = lineItem.getPaymentVoucherDate();

						objArr[95] = lineItem.getContractNumber();
						objArr[96] = lineItem.getContractDate();
						objArr[97] = lineItem.getContractValue();

						objArr[98] = lineItem.getUserDefinedField1();
						objArr[99] = lineItem.getUserDefinedField2();
						objArr[100] = lineItem.getUserDefinedField3();
						objArr[101] = lineItem.getUserDefinedField4();
						objArr[102] = lineItem.getUserDefinedField5();
						objArr[103] = lineItem.getUserDefinedField6();
						objArr[104] = lineItem.getUserDefinedField7();
						objArr[105] = lineItem.getUserDefinedField8();
						objArr[106] = lineItem.getUserDefinedField9();
						objArr[107] = lineItem.getUserDefinedField10();
						objArr[108] = lineItem.getUserDefinedField11();
						objArr[109] = lineItem.getUserDefinedField12();
						objArr[110] = lineItem.getUserDefinedField13();
						objArr[111] = lineItem.getUserDefinedField14();
						objArr[112] = lineItem.getUserDefinedField15();
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
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Exiting convertInwardDocSvErrListToArrObj ");
		}
		return docObjList;
	}
}
