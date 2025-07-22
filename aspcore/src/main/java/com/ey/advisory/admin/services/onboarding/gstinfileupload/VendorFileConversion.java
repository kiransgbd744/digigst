package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterVendorEntity;
import com.ey.advisory.admin.services.gstin.jobs.GSTinConstants;

/**
 * 
 * @author Sasidhar Reddy
 *
 */
@Service("VendorFileConversion")
public class VendorFileConversion {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorFileConversion.class);

	public List<MasterVendorEntity> convertVendorFile(List<Object[]> vendorList,
			Gstr1FileStatusEntity updateFileStatus, Long entityId) {
		List<MasterVendorEntity> List = new ArrayList<>();
		for (Object[] obj : vendorList) {
			MasterVendorEntity data = new MasterVendorEntity();
			String suplierGstinOrPan = (obj[0] != null)
					? String.valueOf(obj[0].toString()) : null;
			String legalName = getValues(obj[1]);
			if (legalName != null && legalName.length() > 100) {
				legalName = legalName.substring(0, 100);
			}
			String supplierType = (obj[2] != null)
					? String.valueOf(obj[2].toString()) : null;
			String supplierCode = getValues(obj[3]);
			if (supplierCode != null && supplierCode.length() > 100) {
				supplierCode = supplierCode.substring(0, 100);
			}
			String outSideIndia = (obj[4] != null)
					? String.valueOf(obj[4].toString()) : null;
			String email = getValues(obj[5]);
			if (email != null && email.length() > 100) {
				email = email.substring(0, 100);
			}
			BigDecimal mobileDecimalFormat = BigDecimal.ZERO;
			if (obj[6] != null) {
				String mobileDecimalFormatStr = (String.valueOf(obj[6])).trim();
				mobileDecimalFormat = new BigDecimal(mobileDecimalFormatStr);
			}
			String mobileNo = String.valueOf(mobileDecimalFormat.longValue());
			if (updateFileStatus != null) {
				data.setFileId(updateFileStatus.getId());
				data.setFileName(updateFileStatus.getFileName());
			}
			String vendorKey = getVendorValues(obj);
			data.setEntityId(entityId);
			data.setSupplierCode(supplierCode);
			data.setSupplierGstinPan(suplierGstinOrPan);
			data.setLegalName(legalName);
			data.setSupplierType(supplierType);
			data.setOutSideIndia(outSideIndia);
			data.setEmailId(email);
			data.setMobileNum(mobileNo);
			data.setVendorKey(vendorKey);
			List.add(data);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("VendorFileCoversion List  -> ", data, List);
			}
		}
		return List;

	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public String getVendorValues(Object[] obj) {
		String outSideIndia = (obj[4] != null)
				? String.valueOf(obj[4].toString()) : "";
		String suplierGstinOrPan = null;
		if (outSideIndia == ""
				|| GSTinConstants.N.equalsIgnoreCase(outSideIndia)) {
			return suplierGstinOrPan = (obj[0] != null)
					? String.valueOf(obj[0].toString()) : "";
		}
		if (GSTinConstants.Y.equalsIgnoreCase(outSideIndia)) {
			return suplierGstinOrPan = (obj[1] != null)
					? String.valueOf(obj[1].toString()) : "";
		}
		return suplierGstinOrPan;
	}
}
