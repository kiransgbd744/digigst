package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterCustomerEntity;
import com.ey.advisory.admin.services.gstin.jobs.GSTinConstants;

/**
 * 
 * @author Mahesh.Golla
 *
 */
/*
 * This service is responsible for convert the object of master customer data
 * from excel into related formats
 */

@Service("MasterDataToCustomerConverter")
public class MasterDataToCustomerConverter {

	public List<MasterCustomerEntity> convertCustomer(
			List<Object[]> customerList,
			Gstr1FileStatusEntity updateFileStatus, Long entityId) {
		List<MasterCustomerEntity> customers = new ArrayList<>();

		for (Object[] obj : customerList) {
			MasterCustomerEntity cust = new MasterCustomerEntity();

			String recipientGstinOrPan = (obj[0] != null)
					? String.valueOf(obj[0].toString()) : null;
			String legalName = getValues(obj[1]);
			String tradeName = getValues(obj[2]);
			String recipientType = (obj[3] != null)
					? String.valueOf(obj[3].toString()) : null;
			String recipientCode = getValues(obj[4]);
			String outsideIndia = (obj[5] != null)
					? String.valueOf(obj[5].toString()) : GSTinConstants.N;
			String email = getValues(obj[6]);

			BigDecimal mobileDecimalFormat = BigDecimal.ZERO;

			if (obj[7] != null) {
				String mobileDecimalFormatStr = (String.valueOf(obj[7])).trim();
				mobileDecimalFormat = new BigDecimal(mobileDecimalFormatStr);
			}
			String mobileNo = String.valueOf(mobileDecimalFormat.longValue());
			String customerKey = getCustomerValues(obj);
			if (updateFileStatus != null) {
				cust.setFileId(updateFileStatus.getId());
				cust.setFileName(updateFileStatus.getFileName());
			}

			cust.setRecipientGstnOrPan(recipientGstinOrPan);
			cust.setLegalName(legalName);
			cust.setTradeName(tradeName);
			cust.setRecipientType(recipientType);
			cust.setRecipientCode(recipientCode);
			cust.setOutSideIndia(outsideIndia);
			cust.setEmailId(email);
			cust.setMobileNum(mobileNo);
			cust.setCustKey(customerKey);
			cust.setEntityId(entityId);
			customers.add(cust);
		}
		return customers;
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public String getCustomerValues(Object[] obj) {
		String outSideIndia = (obj[5] != null)
				? String.valueOf(obj[5].toString()) : "";
		String receipienGstin = null;
		if (outSideIndia == ""
				|| GSTinConstants.N.equalsIgnoreCase(outSideIndia)) {
			return receipienGstin = (obj[0] != null)
					? String.valueOf(obj[0].toString()) : "";
		}
		if (GSTinConstants.Y.equalsIgnoreCase(outSideIndia)) {
			return receipienGstin = (obj[1] != null)
					? String.valueOf(obj[1].toString()) : "";
		}
		return receipienGstin;
	}
}
