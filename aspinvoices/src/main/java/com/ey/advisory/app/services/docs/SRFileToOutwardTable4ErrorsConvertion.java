package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.OutwardTable4ErrorEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToOutwardTable4ErrorsConvertion")
public class SRFileToOutwardTable4ErrorsConvertion {

	@Autowired
	@Qualifier("SRFileToOutwardTableDetailsConvertion")
	private SRFileToOutwardTableDetailsConvertion
	                           sRFileToOutwardTableDetailsConvertion;

	public List<OutwardTable4ErrorEntity> convertSRFileToOutwardTable4(
			List<Object[]> listOfTable4s,
			Gstr1FileStatusEntity updateFileStatus) {

		List<OutwardTable4ErrorEntity> listTable4s = new ArrayList<>();
		OutwardTable4ErrorEntity table4 = null;
		for (Object[] arr : listOfTable4s) {
			table4 = new OutwardTable4ErrorEntity();
			String retType = getValues(arr[0]);
			String supplierGstin = getValues(arr[1]);
			String returnPeriod = getValues(arr[2]);
			String ecomGstin = getValues(arr[3]);
			String valueofSuppMade = getValues(arr[4]);
			String valueOfSuppRet = getValues(arr[5]);
			String valueOfSuppNet =getValues(arr[6]);
			String igst = getValues(arr[7]);
			String cgst = getValues(arr[8]);
			String sgst = getValues(arr[9]);
			String cess = getValues(arr[10]);
			String profitCentre = getValues(arr[11]);
			String plant = getValues(arr[12]);
			String division = getValues(arr[13]);
			String location = getValues(arr[14]);
			String salesOrg = getValues(arr[15]);
			String distributeChannel = getValues(arr[16]);
			String userAccess1 = getValues(arr[17]);
			String userAccess2 = getValues(arr[18]);
			String userAccess3 = getValues(arr[19]);
			String userAccess4 = getValues(arr[20]);
			String userAccess5 = getValues(arr[21]);
			String userAccess6 = getValues(arr[22]);
			String userDef1 = getValues(arr[23]);
			String userDef2 = getValues(arr[24]);
			String userDef3 = getValues(arr[25]);

			String table4Key = sRFileToOutwardTableDetailsConvertion
					.getTable4GstnKey(arr);

				table4.setRetType(retType);
       			table4.setSgstin(supplierGstin);
				table4.setSgstin(supplierGstin);
				table4.setRetPeriod(returnPeriod);
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

			if (table4Key != null && table4Key.length() > 100) {
				table4.setTable4Key(table4Key.substring(0, 100));
			} else {
				table4.setTable4Key(table4Key);
			}
				table4.setUserDef1(userDef1);
				table4.setUserDef2(userDef2);
				table4.setUserDef3(userDef3);
			if (updateFileStatus != null) {
				table4.setFileId(updateFileStatus.getId());
			}

			listTable4s.add(table4);

		}

		return listTable4s;
	}
	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;

	}


}
