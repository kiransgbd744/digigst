package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.MasterVendorEntity;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Siva Krishna
 *
 */

/*
 * This class is responsible for convert the vendor objects in to related
 * formats
 */

@Service("MasterDataToVendorConverter")
public class MasterDataToVendorConverter {
	/**
	 * 
	 * @param listOfVendors
	 * @return List<MasterVendorEntity>
	 */

	private static final List<String> OUTSIDEOFINDIA_IMPORTS = ImmutableList.of("Y",
			"N", "n", "y");

	public Pair<List<String>, List<MasterVendorEntity>> convert(
			List<Object[]> listOfVendors) {
		// public List<MasterVendorEntity> convert(List<Object[]> listOfVendors)
		// {

		List<MasterVendorEntity> vendors = new ArrayList<>();

		MasterVendorEntity vendor = null;
		List<String> errorMsgs = new ArrayList<>();
		for (Object[] obj : listOfVendors) {

			vendor = new MasterVendorEntity();

			String recipientGstinOrPan = (obj[0] != null)
					? String.valueOf(obj[0].toString()) : null;

			String supplierCode = (obj[1] != null)
					? String.valueOf(obj[1].toString()) : null;

			String SupplierGstinOrPan = (obj[2] != null)
					? String.valueOf(obj[2].toString()) : null;
			String SupplierName = (obj[3] != null)
					? String.valueOf(obj[3].toString()) : null;
			String SupplierType = (obj[4] != null)
					? String.valueOf(obj[4].toString()) : null;

			String outSideIndia = (obj[5] != null)
					? String.valueOf(obj[5].toString()) : null;
			String emailId = (obj[6] != null)
					? String.valueOf(obj[6].toString()) : null;

			String mobileNo = (obj[7] != null)
					? String.valueOf(obj[7].toString()) : null;

			if (!isPresent(obj[0])) {
				errorMsgs.add(recipientGstinOrPan
						+ " -> RecipientGSTIN/PAN is mandatory.");

			}

			String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";
			Pattern pattern = Pattern.compile(regex);

			String regex_pan = "^[A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";

			Pattern pattern_pan = Pattern.compile(regex_pan);

			Matcher matcher_pan = pattern_pan.matcher(obj[2].toString());

			Matcher matcher_cust = pattern_pan.matcher(obj[0].toString());
			Matcher matcher = pattern.matcher(obj[0].toString());

			Matcher matcher1 = pattern.matcher(obj[2].toString());
			if (obj[0].toString().length() == 15
					|| obj[0].toString().length() == 10) {
				// nothing todo
			} else {
				errorMsgs.add("Invalid recipientGstinOrPan");
			}

			if ((obj[0].toString().length() == 15 && !matcher1.matches())
					|| (obj[0].toString().length() == 10
							&& !matcher_cust.matches())) {
				errorMsgs.add("Invalid SupplierGSTIN/PAN");
				
			}

			/*if (!isPresent(obj[2])) {
				errorMsgs.add(recipientGstinOrPan
						+ " -> SupplierGSTIN/PAN is mandatory.");

			}*/
			if (obj[2].toString().length() == 15
					|| obj[2].toString().length() == 10) {
				// nothing todo
			} else {

				errorMsgs.add("Invalid SupplierGSTIN/PAN");
			}
			if ((obj[2].toString().length() == 15 && !matcher.matches())
					|| (obj[2].toString().length() == 10
							&& !matcher_pan.matches())) {
				errorMsgs.add("Invalid SupplierGSTIN/PAN");
			}

			if (!isPresent(obj[4])) {
				errorMsgs.add(
						recipientGstinOrPan + " -> SupplierType is mandatory.");

			}
			if (supplierCode.length() > 100) {
				supplierCode = supplierCode.substring(0, 100);
			}
			if (SupplierName.length() > 100) {
				SupplierName = SupplierName.substring(0, 100);
			}
			/*if (!OUTSIDEOFINDIA_IMPORTS.contains(obj.toString())) {
				errorMsgs.add("Invalid OutsideIndia");
			}*/
			if (!Arrays.asList(OUTSIDEOFINDIA_IMPORTS).contains(obj.toString())) {
			    errorMsgs.add("Invalid OutsideIndia");
			}

			if (emailId.length() > 100) {
				emailId = emailId.substring(0, 100);
			}

			if (mobileNo.length() > 10) {
				errorMsgs.add("Invalid MobileNo");
			}

			vendor.setCustGstinPan(recipientGstinOrPan);
			vendor.setSupplierCode(supplierCode);
			vendor.setSupplierGstinPan(SupplierGstinOrPan);
			vendor.setLegalName(SupplierName);
			vendor.setSupplierType(SupplierType);
			vendor.setOutSideIndia(outSideIndia);
			vendor.setEmailId(emailId);
			vendor.setMobileNum(mobileNo);

			vendors.add(vendor);

		}
		return new Pair<List<String>, List<MasterVendorEntity>>(errorMsgs,
				vendors);
		// return vendors;
	}
}
