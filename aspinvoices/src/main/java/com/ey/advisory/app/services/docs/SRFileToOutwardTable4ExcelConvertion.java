package com.ey.advisory.app.services.docs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.OutwardTable4ExcelEntity;
import com.ey.advisory.common.GenUtil;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToOutwardTable4ExcelConvertion")
public class SRFileToOutwardTable4ExcelConvertion {

	@Autowired
	@Qualifier("SRFileToOutwardTableDetailsConvertion")
	private SRFileToOutwardTableDetailsConvertion sRFileToOutwardTableDetailsConvertion;

	public List<OutwardTable4ExcelEntity> convertSRFileToOutwardTable4Excel(
			List<Object[]> listOfTable4s,
			Gstr1FileStatusEntity updateFileStatus) {

		List<OutwardTable4ExcelEntity> listTable4s = new ArrayList<>();
		OutwardTable4ExcelEntity table4 = null;
		for (Object[] arr : listOfTable4s) {
			table4 = new OutwardTable4ExcelEntity();
			String retType = getValues(arr[0]);
			String supplierGstin = getValues(arr[1]);
			String returnPeriod = getValues(arr[2]);
			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month <= 12 && month >= 01)
							&& (year <= 9999 && year >= 0000)) {
					Integer derivedRetPeriod = GenUtil
							.convertTaxPeriodToInt(returnPeriod);
					derivedRePeroid = String.valueOf(derivedRetPeriod);
				}
				}
			}

			String ecomGstin = getValues(arr[3]);
			String valueofSuppMade = getValues(arr[4]);
			String valueOfSuppRet = getValues(arr[5]);
			String valueOfSuppNet = getValues(arr[6]);
			String igst = getValues(arr[7]);
			String cgst = getValues(arr[8]);
			String sgst = getValues(arr[9]);
			String cess = getValues(arr[10]);
			String profitCentre = getNoTrancateValues(arr[11]);
			String plant = getNoTrancateValues(arr[12]);
			String division = getNoTrancateValues(arr[13]);
			String location = getNoTrancateValues(arr[14]);
			String salesOrg = getNoTrancateValues(arr[15]);
			String distributeChannel = getNoTrancateValues(arr[16]);
			String userAccess1 = getNoTrancateValues(arr[17]);
			String userAccess2 = getNoTrancateValues(arr[18]);
			String userAccess3 = getNoTrancateValues(arr[19]);
			String userAccess4 = getNoTrancateValues(arr[20]);
			String userAccess5 = getNoTrancateValues(arr[21]);
			String userAccess6 = getNoTrancateValues(arr[22]);
			String userDef1 = getNoTrancateValues(arr[23]);
			String userDef2 = getNoTrancateValues(arr[24]);
			String userDef3 = getNoTrancateValues(arr[25]);

			String table4GstnKey = sRFileToOutwardTableDetailsConvertion
					.getTable4GstnKey(arr);
			String table4InvKey = sRFileToOutwardTableDetailsConvertion
					.getTable4InvKey(arr);

			table4.setRetType(retType);
			table4.setSgstin(supplierGstin);
			table4.setRetPeriod(returnPeriod);
			table4.setDerivedRetPeriod(derivedRePeroid);
			table4.setEcomGstin(ecomGstin);
			table4.setValueOfSupMade(valueofSuppMade);
			table4.setValueOfSupRet(valueOfSuppRet);
			table4.setNetValueOfSup(valueOfSuppNet);
			table4.setIgstAmt(igst);
			table4.setCgstAmt(cgst);
			table4.setSgstAmt(sgst);
			table4.setCessAmt(cess);
			table4.setProfitCentre(profitCentre);
			table4.setPlant(plant);
			table4.setDivision(division);
			table4.setLocation(location);
			table4.setSalesOrganisation(salesOrg);
			table4.setDistributionChannel(distributeChannel);
			table4.setUserAccess1(userAccess1);
			table4.setUserAccess2(userAccess2);
			table4.setUserAccess3(userAccess3);
			table4.setUserAccess4(userAccess4);
			table4.setUserAccess5(userAccess5);
			table4.setUserAccess6(userAccess6);

			if (table4GstnKey != null && table4GstnKey.length() > 100) {
				table4.setTable4Gstnkey(table4GstnKey.substring(0, 100));
			} else {
				table4.setTable4Gstnkey(table4GstnKey);
			}
			if (table4InvKey != null && table4InvKey.length() > 2200) {
				table4.setTable4Invkey(table4InvKey.substring(0, 2220));
			} else {
				table4.setTable4Invkey(table4InvKey);
			}

			table4.setUserDef1(userDef1);
			table4.setUserDef2(userDef2);
			table4.setUserDef3(userDef3);
			if (updateFileStatus != null) {
				table4.setFileId(updateFileStatus.getId());
				table4.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			table4.setCreatedOn(LocalDateTime.now());
			listTable4s.add(table4);
		}
		return listTable4s;
	}

	private String getNoTrancateValues(Object object) {
		String value = (object != null) ? String.valueOf(object).trim() : null;
		if (value != null && value.length() > 110) {
			return value.substring(0, 110).trim();
		}
		return value;
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}
}
