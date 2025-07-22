/**
 * 
 */
package com.ey.advisory.app.services.docs.gstr2;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.common.AppException;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("InwardLowerVersionValidationService")
public class InwardLowerVersionValidationService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardLowerVersionValidationService.class);

	public void SchemaValidationsForLowerVersion(InwardTransDocument document) {

		if (Strings.isNullOrEmpty(document.getDocNo())) {
			String msg = "DocNo cannot be empty";
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (document.getDocNo().length() > 16) {
			String msg = "DocNo maximum length is 16";
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (Strings.isNullOrEmpty(document.getDocType())) {
			String msg = "DocType cannot be empty for docNo : "
					+ document.getDocNo();
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (document.getDocType().length() > 5) {
			String msg = "DocType maximum length is 5 for docNo : "
					+ document.getDocNo();
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (document.getDocDate() == null) {
			String msg = "DocDate cannot be empty for docNo : "
					+ document.getDocNo();
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (Strings.isNullOrEmpty(document.getTaxperiod())) {
			String msg = "taxPeriod cannot be empty for docNo : "
					+ document.getDocNo();
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (document.getTaxperiod().length() > 6) {
			String msg = "taxPeriod maximum length is 6 for docNo : "
					+ document.getDocNo();
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (Strings.isNullOrEmpty(document.getCgstin())) {
			String msg = "custGstin cannot be empty for docNo : "
					+ document.getDocNo();
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (document.getCgstin().length() > 15) {
			String msg = "custGstin maximum length is 15 for docNo : "
					+ document.getDocNo();
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		document.getLineItems().forEach(item -> {

			if (item.getLineNo() == null) {
				String msg = "itemNo cannot be empty for docNo : "
						+ document.getDocNo();
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			if (!StringUtils.isNumeric(item.getLineNo().toString())) {
				String msg = "itemNo should be numeric for docNo : "
						+ document.getDocNo();
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			Integer lineNum = String.valueOf(item.getLineNo()).length();
			if (lineNum > 20) {
				String msg = "itemNo maximum length is 20 for docNo : "
						+ document.getDocNo();
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			if (Strings.isNullOrEmpty(item.getSupplyType())) {
				String msg = "supplyType cannot be empty for docNo : "
						+ document.getDocNo();
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			if (item.getSupplyType().length() > 5) {
				String msg = "supplyType maximum length is 5 for docNo : "
						+ document.getDocNo();
				LOGGER.error(msg);
				throw new AppException(msg);
			}
		});
	}

	public void jsonValidationForDecimal(JsonArray jsonArray) {

		for (JsonElement jsonEle : jsonArray) {

			JsonObject jsonObj = (JsonObject) jsonEle;
			String docAmount = jsonObj.get("docAmt") == null ? null
					: jsonObj.get("docAmt").getAsString().trim();
			jsonObj.addProperty("docAmt", docAmount);

			String invOthChrgs = jsonObj.get("invOtherCharges") == null ? null
					: jsonObj.get("invOtherCharges").getAsString().trim();
			jsonObj.addProperty("invOtherCharges", invOthChrgs);

			String invAssAmt = jsonObj.get("invAssessableAmt") == null ? null
					: jsonObj.get("invAssessableAmt").getAsString().trim();
			jsonObj.addProperty("invAssessableAmt", invAssAmt);

			String invIgstAmount = jsonObj.get("invIgstAmt") == null ? null
					: jsonObj.get("invIgstAmt").getAsString().trim();
			jsonObj.addProperty("invIgstAmt", invIgstAmount);

			String invCgstAmount = jsonObj.get("invCgstAmt") == null ? null
					: jsonObj.get("invCgstAmt").getAsString().trim();
			jsonObj.addProperty("invCgstAmt", invCgstAmount);

			String invSgstAmount = jsonObj.get("invSgstAmt") == null ? null
					: jsonObj.get("invSgstAmt").getAsString().trim();
			jsonObj.addProperty("invSgstAmt", invSgstAmount);

			String invCessAdvaloremAmount = jsonObj
					.get("invCessAdvaloremAmt") == null ? null
							: jsonObj.get("invCessAdvaloremAmt").getAsString()
									.trim();
			jsonObj.addProperty("invCessAdvaloremAmt", invCessAdvaloremAmount);

			String invCessSpecificAmount = jsonObj
					.get("invCessSpecificAmt") == null ? null
							: jsonObj.get("invCessSpecificAmt").getAsString()
									.trim();
			jsonObj.addProperty("invCessSpecificAmt", invCessSpecificAmount);

			String invStateCessAmount = jsonObj.get("invStateCessAmt") == null
					? null
					: jsonObj.get("invStateCessAmt").getAsString().trim();
			jsonObj.addProperty("invStateCessAmt", invStateCessAmount);

			String invStateCessSpecificAmount = jsonObj
					.get("invStateCessSpecificAmt") == null ? null
							: jsonObj.get("invStateCessSpecificAmt")
									.getAsString().trim();
			jsonObj.addProperty("invStateCessSpecificAmt",
					invStateCessSpecificAmount);

			String userdefined28 = jsonObj.get("udf28") == null ? null
					: jsonObj.get("udf28").getAsString().trim();
			jsonObj.addProperty("udf28", userdefined28);

			JsonArray jsonArrayLine = (JsonArray) jsonObj.get("lineItems");
			for (JsonElement jsonLine : jsonArrayLine) {

				JsonObject jsonObjLine = (JsonObject) jsonLine;

				String contractValue = jsonObjLine.get("contractValue") == null
						? null
						: jsonObjLine.get("contractValue").getAsString().trim();
				jsonObjLine.addProperty("contractValue", contractValue);

				String cifVal = jsonObjLine.get("cifValue") == null ? null
						: jsonObjLine.get("cifValue").getAsString().trim();
				jsonObjLine.addProperty("cifValue", cifVal);

				String custDuty = jsonObjLine.get("customDuty") == null ? null
						: jsonObjLine.get("customDuty").getAsString().trim();
				jsonObjLine.addProperty("customDuty", custDuty);

				String itemQuantity = jsonObjLine.get("itemQty") == null ? null
						: jsonObjLine.get("itemQty").getAsString().trim();
				jsonObjLine.addProperty("itemQty", itemQuantity);

				String taxableValue = jsonObjLine.get("taxableVal") == null
						? null
						: jsonObjLine.get("taxableVal").getAsString().trim();
				jsonObjLine.addProperty("taxableVal", taxableValue);

				String igstRate = jsonObjLine.get("igstRt") == null ? null
						: jsonObjLine.get("igstRt").getAsString().trim();
				jsonObjLine.addProperty("igstRt", igstRate);

				String igstAmount = jsonObjLine.get("igstAmt") == null ? null
						: jsonObjLine.get("igstAmt").getAsString().trim();
				jsonObjLine.addProperty("igstAmt", igstAmount);

				String cgstRate = jsonObjLine.get("cgstRt") == null ? null
						: jsonObjLine.get("cgstRt").getAsString().trim();
				jsonObjLine.addProperty("cgstRt", cgstRate);

				String cgstAmount = jsonObjLine.get("cgstAmt") == null ? null
						: jsonObjLine.get("cgstAmt").getAsString().trim();
				jsonObjLine.addProperty("cgstAmt", cgstAmount);

				String sgstRate = jsonObjLine.get("sgstRt") == null ? null
						: jsonObjLine.get("sgstRt").getAsString().trim();
				jsonObjLine.addProperty("sgstRt", sgstRate);

				String sgstAmount = jsonObjLine.get("sgstAmt") == null ? null
						: jsonObjLine.get("sgstAmt").getAsString().trim();
				jsonObjLine.addProperty("sgstAmt", sgstAmount);

				String cessRtAdvalrm = jsonObjLine
						.get("cessRtAdvalorem") == null ? null
								: jsonObjLine.get("cessRtAdvalorem")
										.getAsString().trim();
				jsonObjLine.addProperty("cessRtAdvalorem", cessRtAdvalrm);

				String cessAmtAdvalrm = jsonObjLine
						.get("cessAmtAdvalorem") == null ? null
								: jsonObjLine.get("cessAmtAdvalorem")
										.getAsString().trim();
				jsonObjLine.addProperty("cessAmtAdvalorem", cessAmtAdvalrm);

				String cessRateSpecific = jsonObjLine
						.get("cessRtSpecific") == null ? null
								: jsonObjLine.get("cessRtSpecific")
										.getAsString().trim();
				jsonObjLine.addProperty("cessRtSpecific", cessRateSpecific);

				String cessAmountSpecfic = jsonObjLine
						.get("cessAmtSpecfic") == null ? null
								: jsonObjLine.get("cessAmtSpecfic")
										.getAsString().trim();
				jsonObjLine.addProperty("cessAmtSpecfic", cessAmountSpecfic);

				String stateCessRate = jsonObjLine.get("stateCessRt") == null
						? null
						: jsonObjLine.get("stateCessRt").getAsString().trim();
				jsonObjLine.addProperty("stateCessRt", stateCessRate);

				String stateCessAmount = jsonObjLine.get("stateCessAmt") == null
						? null
						: jsonObjLine.get("stateCessAmt").getAsString().trim();
				jsonObjLine.addProperty("stateCessAmt", stateCessAmount);

				String otherValus = jsonObjLine.get("otherValues") == null
						? null
						: jsonObjLine.get("otherValues").getAsString().trim();
				jsonObjLine.addProperty("otherValues", otherValus);

				String lineItemAmount = jsonObjLine.get("lineItemAmt") == null
						? null
						: jsonObjLine.get("lineItemAmt").getAsString().trim();
				jsonObjLine.addProperty("lineItemAmt", lineItemAmount);

				String availIgst = jsonObjLine.get("availableIgst") == null
						? null
						: jsonObjLine.get("availableIgst").getAsString().trim();
				jsonObjLine.addProperty("availableIgst", availIgst);

				String availCgst = jsonObjLine.get("availableCgst") == null
						? null
						: jsonObjLine.get("availableCgst").getAsString().trim();
				jsonObjLine.addProperty("availableCgst", availCgst);

				String availSgst = jsonObjLine.get("availableSgst") == null
						? null
						: jsonObjLine.get("availableSgst").getAsString().trim();
				jsonObjLine.addProperty("availableSgst", availSgst);

				String availCess = jsonObjLine.get("availableCess") == null
						? null
						: jsonObjLine.get("availableCess").getAsString().trim();
				jsonObjLine.addProperty("availableCess", availCess);

				String itemAmount = jsonObjLine.get("itemAmt") == null ? null
						: jsonObjLine.get("itemAmt").getAsString().trim();
				jsonObjLine.addProperty("itemAmt", itemAmount);

				String itemDisc = jsonObjLine.get("itemDiscount") == null ? null
						: jsonObjLine.get("itemDiscount").getAsString().trim();
				jsonObjLine.addProperty("itemDiscount", itemDisc);

				String preTaxAmount = jsonObjLine.get("preTaxAmt") == null
						? null
						: jsonObjLine.get("preTaxAmt").getAsString().trim();
				jsonObjLine.addProperty("preTaxAmt", preTaxAmount);

				String stateCessSpecificRate = jsonObjLine
						.get("stateCessSpecificRt") == null ? null
								: jsonObjLine.get("stateCessSpecificRt")
										.getAsString().trim();
				jsonObjLine.addProperty("stateCessSpecificRt",
						stateCessSpecificRate);

				String stateCessSpecificAmount = jsonObjLine
						.get("stateCessSpecificAmt") == null ? null
								: jsonObjLine.get("stateCessSpecificAmt")
										.getAsString().trim();
				jsonObjLine.addProperty("stateCessSpecificAmt",
						stateCessSpecificAmount);

				String roundOff = jsonObjLine.get("roundOff") == null ? null
						: jsonObjLine.get("roundOff").getAsString().trim();
				jsonObjLine.addProperty("roundOff", roundOff);

				String tcsRateIncomeTax = jsonObjLine
						.get("tcsRtIncomeTax") == null ? null
								: jsonObjLine.get("tcsRtIncomeTax")
										.getAsString().trim();
				jsonObjLine.addProperty("tcsRtIncomeTax", tcsRateIncomeTax);

				String tcsAmountIncomeTax = jsonObjLine
						.get("tcsAmtIncomeTax") == null ? null
								: jsonObjLine.get("tcsAmtIncomeTax")
										.getAsString().trim();
				jsonObjLine.addProperty("tcsAmtIncomeTax", tcsAmountIncomeTax);

				String invoiceValueFc = jsonObjLine.get("invValueFc") == null
						? null
						: jsonObjLine.get("invValueFc").getAsString().trim();
				jsonObjLine.addProperty("invValueFc", invoiceValueFc);

				String paidAmount = jsonObjLine.get("paidAmt") == null ? null
						: jsonObjLine.get("paidAmt").getAsString().trim();
				jsonObjLine.addProperty("paidAmt", paidAmount);

				String balanceAmount = jsonObjLine.get("balanceAmt") == null
						? null
						: jsonObjLine.get("balanceAmt").getAsString().trim();
				jsonObjLine.addProperty("balanceAmt", balanceAmount);

				String tcsIgstAmount = jsonObjLine.get("tcsIgstAmt") == null
						? null
						: jsonObjLine.get("tcsIgstAmt").getAsString().trim();
				jsonObjLine.addProperty("tcsIgstAmt", tcsIgstAmount);

				String tcsCgstAmount = jsonObjLine.get("tcsCgstAmt") == null
						? null
						: jsonObjLine.get("tcsCgstAmt").getAsString().trim();
				jsonObjLine.addProperty("tcsCgstAmt", tcsCgstAmount);

				String tcsSgstAmount = jsonObjLine.get("tcsSgstAmt") == null
						? null
						: jsonObjLine.get("tcsSgstAmt").getAsString().trim();
				jsonObjLine.addProperty("tcsSgstAmt", tcsSgstAmount);

				String tdsIgstAmount = jsonObjLine.get("tdsIgstAmt") == null
						? null
						: jsonObjLine.get("tdsIgstAmt").getAsString().trim();
				jsonObjLine.addProperty("tdsIgstAmt", tdsIgstAmount);

				String tdsCgstAmount = jsonObjLine.get("tdsCgstAmt") == null
						? null
						: jsonObjLine.get("tdsCgstAmt").getAsString().trim();
				jsonObjLine.addProperty("tdsCgstAmt", tdsCgstAmount);

				String tdsSgstAmount = jsonObjLine.get("tdsSgstAmt") == null
						? null
						: jsonObjLine.get("tdsSgstAmt").getAsString().trim();
				jsonObjLine.addProperty("tdsSgstAmt", tdsSgstAmount);
			}
		}
	}
}
