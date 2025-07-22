package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.InwardTable3I3HErrorEntity;

@Service("InwardTable3H3IErrorConvertion")
public class InwardTable3H3IErrorConvertion {

	@Autowired
	@Qualifier("InwardTable3H3IDetailsConvertion")
	private InwardTable3H3IDetailsConvertion inwardTable3H3IDetailsConvertion;

	@SuppressWarnings("unused")
	public List<InwardTable3I3HErrorEntity> convertSRFileToOutward3H3I(
			List<Object[]> strucErrorRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		List<InwardTable3I3HErrorEntity> table3hDetailsEntities = 
				new ArrayList<>();

		for (Object[] obj : strucErrorRecords) {

			InwardTable3I3HErrorEntity table3h3i = 
					new InwardTable3I3HErrorEntity();
			String returnType = getValues(obj[0]);
			String recipientGstin = getValues(obj[1]);
			String returnPeriod = getValues(obj[2]);
			String documentType = getValues(obj[3]);
			String transactionFlag = getValues(obj[4]);
			String supplierGstionorPan = getValues(obj[5]);
			String supplierName = getValues(obj[6]);
			String differentialPercentageFlag = getValues(obj[7]);
			String section7ofIGSTFlag = getValues(obj[8]);
			String autoPopulateToRefund = getValues(obj[9]);
			String pos = getValues(obj[10]);
			String hsn = getValues(obj[11]);
			String taxableValue = getValues(obj[12]);
			String rate = getValues(obj[13]);
			String integratedTaxAmount = getValues(obj[14]);
			String centralTaxAmount = getValues(obj[15]);
			String stateUTTaxAmount = getValues(obj[16]);
			String cessAmount = getValues(obj[17]);
			String eligibilityIndicator = getValues(obj[18]);
			String avilableIGST = getValues(obj[19]);
			String avilableCGST = getValues(obj[20]);
			String avilableSGST = getValues(obj[21]);
			String avilableCess = getValues(obj[22]);
			String profitCentre = getValues(obj[23]);
			String plant = getValues(obj[24]);
			String divison = getValues(obj[25]);
			String location = getValues(obj[26]);
			String purchaseOrganisation = getValues(obj[27]);
			String userAccess1 = getValues(obj[28]);
			String userAccess2 = getValues(obj[29]);
			String userAccess3 = getValues(obj[30]);
			String userAccess4 = getValues(obj[31]);
			String userAccess5 = getValues(obj[32]);
			String userAccess6 = getValues(obj[33]);
			String userDefined1 = getValues(obj[34]);
			String userDefined2 = getValues(obj[35]);
			String userDefined3 = getValues(obj[36]);
			String table3H3IKey = inwardTable3H3IDetailsConvertion
					.getTable3h3iGstnKey(obj);

			table3h3i.setAvailableIGST(avilableIGST);
			table3h3i.setAvailableCGST(avilableCGST);
			table3h3i.setAvailableCess(avilableCess);
			table3h3i.setAvailableSGST(avilableSGST);
			table3h3i.setCessAmount(cessAmount);
			table3h3i.setRate(rate);
			table3h3i.setStateUTTaxAmount(stateUTTaxAmount);
			table3h3i.setCentralTaxAmount(centralTaxAmount);
			table3h3i.setIntegratedTaxAmount(integratedTaxAmount);
			table3h3i.setTaxableValue(taxableValue);
			table3h3i.setReturnType(returnType);
			table3h3i.setRecipientGstin(recipientGstin);
			table3h3i.setReturnPeriod(returnPeriod);
			table3h3i.setTransactionFlag(transactionFlag);
			table3h3i.setSupplierGSTINorpan(supplierGstionorPan);
			table3h3i.setSupplierName(supplierName);
			table3h3i.setPos(pos);
			table3h3i.setEligibilityIndicator(eligibilityIndicator);
			table3h3i.setDivision(divison);
			table3h3i.setLocation(location);
			table3h3i.setAutoPopulateToRefund(autoPopulateToRefund);
			table3h3i.setDiffPercent(differentialPercentageFlag);
			table3h3i.setDocType(documentType);
			table3h3i.setSec70fIGSTFLAG(section7ofIGSTFlag);
			table3h3i.setUserAccess1(userAccess1);
			table3h3i.setUserAccess2(userAccess2);
			table3h3i.setUserAccess3(userAccess3);
			table3h3i.setUserAccess4(userAccess4);
			table3h3i.setUserAccess5(userAccess5);
			table3h3i.setUserAccess6(userAccess6);
			table3h3i.setProfitCentre(profitCentre);
			table3h3i.setPlant(plant);
			table3h3i.setUserDefined1(integratedTaxAmount);
			table3h3i.setUserDefined2(userDefined2);
			table3h3i.setUserDefined3(userDefined3);
			table3h3i.setHsn(hsn);
			table3h3i.setPurchaseOrganisation(purchaseOrganisation);

			if (table3H3IKey != null && table3H3IKey.trim().length() > 100) {
				table3h3i.setTable3H3Ikey(table3H3IKey.substring(0, 100));
			} else {
				table3h3i.setTable3H3Ikey(table3H3IKey);
			}
			if (updateFileStatus != null) {
				table3h3i.setFileId(updateFileStatus.getId());
			}

			table3hDetailsEntities.add(table3h3i);
		}
		return table3hDetailsEntities;

	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;

	}

}