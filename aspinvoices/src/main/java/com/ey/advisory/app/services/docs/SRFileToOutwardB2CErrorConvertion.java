package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.OutwardB2cErrorEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToOutwardB2CErrorConvertion")
public class SRFileToOutwardB2CErrorConvertion {
	private final static String WEB_UPLOAD_KEY = "|";

	public List<OutwardB2cErrorEntity> convertSRFileToOutwardB2c(
			List<Object[]> listOfB2c, Gstr1FileStatusEntity updateFileStatus) {
		List<OutwardB2cErrorEntity> listB2c = new ArrayList<>();

		OutwardB2cErrorEntity outb2c = null;
		for (Object[] arr : listOfB2c) {
			outb2c = new OutwardB2cErrorEntity();
			String returnType = getValues(arr[0]);
			String supplierGstin = getValues(arr[1]);
			String returnPeriod = getValues(arr[2]);
			String docType = getValues(arr[3]);
			String diffPer =getValues(arr[4]);
			String section7 = getValues(arr[5]);
			String autoPopRefund = getValues(arr[6]);
			String pos = getValues(arr[7]);
			String hsnSac =getValues(arr[8]);
			String uom = getValues(arr[9]);
			String qnt = getValues(arr[10]);
			String rate = getValues(arr[11]);
			String stateAppCess = getValues(arr[12]);
			String stateCessRate = getValues(arr[13]);
			String taxableValue = getValues(arr[14]);
			String tcsFlag = getValues(arr[15]);
			String ecomGstin = getValues(arr[16]);
			String ecomSuppMade = getValues(arr[17]);
			String ecomSuppRet = getValues(arr[18]);
			String ecomSuppNet = getValues(arr[19]);
			String igst =getValues(arr[20]);
			String cgst = getValues(arr[21]);
			String sgst = getValues(arr[22]);
			String cess =getValues(arr[23]);
			String tcsAmt = getValues(arr[24]);
			String stateCessamt =getValues(arr[25]);
			String otherAmt = getValues(arr[26]);
			String totalAmt = getValues(arr[27]);
			String profitCentre = getValues(arr[28]);
			String plant = getValues(arr[29]);
			String division = getValues(arr[30]);
			String location = getValues(arr[31]);
			String salesOrg = getValues(arr[32]);
			String distributeChannel = getValues(arr[33]);
			String userAccess1 = getValues(arr[34]);
			String userAccess2 = getValues(arr[35]);
			String userAccess3 = getValues(arr[36]);
			String userAccess4 = getValues(arr[37]);
			String userAccess5 = getValues(arr[38]);
			String userAccess6 = getValues(arr[39]);
			String userDef1 = getValues(arr[40]);
			String userDef2 = getValues(arr[41]);
			String userDef3 = getValues(arr[42]);

			String b2csKey = getB2cKeyValues(arr);
			if(updateFileStatus != null){
				outb2c.setFileId(updateFileStatus.getId());
			}

				outb2c.setRetType(returnType);
				outb2c.setSgstin(supplierGstin);
				outb2c.setRetPeriod(returnPeriod);
				outb2c.setDocType(docType);
				outb2c.setDiffPercent(diffPer);
				outb2c.setSec7OfIgstFlag(section7);
				outb2c.setAutoPopulateToRefund(autoPopRefund);
				outb2c.setPos(pos);
				outb2c.setHsnSac(hsnSac);
				outb2c.setUom(uom);
				outb2c.setQuentity(qnt);
				outb2c.setRate(rate);
				outb2c.setStateApplyCess(stateAppCess);
				outb2c.setStateCessRate(stateCessRate);
				outb2c.setTaxableValue(taxableValue);
				outb2c.setTcsFlag(tcsFlag);
				outb2c.setEcomGstin(ecomGstin);
				outb2c.setEcomValueSuppMade(ecomSuppMade);
				outb2c.setEcomValSuppRet(ecomSuppRet);
				outb2c.setEcomNetValSupp(ecomSuppNet);
				outb2c.setIgstAmt(igst);
				outb2c.setCgstAmt(cgst);
				outb2c.setSgstAmt(sgst);
				outb2c.setCessAmt(cess);
				outb2c.setTcsAmt(tcsAmt);
				outb2c.setStateCessAmt(stateCessamt);
				outb2c.setTotalValue(totalAmt);
				outb2c.setOtherValue(otherAmt);
				outb2c.setProfitCentre(profitCentre);
				outb2c.setPlant(plant);
				outb2c.setDivision(division);
				outb2c.setLocation(location);
				outb2c.setSalesOrganisation(salesOrg);
				outb2c.setDistributionChannel(distributeChannel);
				outb2c.setUserAccess1(userAccess1);
				outb2c.setUserAccess2(userAccess2);
				outb2c.setUserAccess3(userAccess3);
				outb2c.setUserAccess4(userAccess4);
				outb2c.setUserAccess5(userAccess5);
				outb2c.setUserAccess6(userAccess6);
				if(b2csKey != null && b2csKey.trim().length()>100){
					outb2c.setB2cKey(b2csKey.substring(0,100));
				}
				else{
					outb2c.setB2cKey(b2csKey);
				}
				outb2c.setUserDef1(userDef1);
				outb2c.setUserDef2(userDef2);
				outb2c.setUserDef3(userDef3);
			listB2c.add(outb2c);
		}
		return listB2c;
	}

	public String getB2cKeyValues(Object[] arr) {

		String returnType = (arr[0] != null) ? (String.valueOf(arr[0])).trim()
				: "";
		String supplierGstin = (arr[1] != null)
				? (String.valueOf(arr[1])).trim() : "";
		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])).trim()
				: "";
		String pos = (arr[7] != null) ? (String.valueOf(arr[7])).trim() : "";
		String rate = (arr[11] != null) ? (String.valueOf(arr[11])).trim() : "";
		String taxableValue = (arr[14] != null)
				? (String.valueOf(arr[14])).trim() : "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(pos).add(rate)
				.add(taxableValue).toString();
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;

	}

}
