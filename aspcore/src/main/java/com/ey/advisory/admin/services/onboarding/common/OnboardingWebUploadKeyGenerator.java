package com.ey.advisory.admin.services.onboarding.common;

import java.math.BigDecimal;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

@Component("OnboardingWebUploadKeyGenerator")
public class OnboardingWebUploadKeyGenerator {
	private final static String WEB_UPLOAD_KEY = "|";

/*	public String generateGstinKey(String sourceIdentifier, String returnPeriod,
			String newPos, String newHsn, BigDecimal newRate,
			String newEcGstin) {
		sourceIdentifier = (sourceIdentifier != null)
				? (String.valueOf(sourceIdentifier)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		newPos = (newPos != null) ? (String.valueOf(newPos)).trim() : "";
		newHsn = (newHsn != null) ? (String.valueOf(newHsn)).trim() : "";
		String newRate1 = (newRate != null) ? newRate.toString()
				: BigDecimal.ZERO.toString();
		newRate.toString();
		newEcGstin = (newEcGstin != null) ? (String.valueOf(newEcGstin)).trim()
				: "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(sourceIdentifier)
				.add(returnPeriod).add(newPos).add(newHsn).add(newRate1)
				.add(newEcGstin).toString();
	}*/
	
	public String generateGstinKey(String sourceIdentifier, String returnPeriod,
	        String newPos, String newHsn, BigDecimal newRate, String newEcGstin) {
	    
	    // Safely handle potential null values
	    sourceIdentifier = (sourceIdentifier != null) ? sourceIdentifier.trim() : "";
	    returnPeriod = (returnPeriod != null) ? returnPeriod.trim() : "";
	    newPos = (newPos != null) ? newPos.trim() : "";
	    newHsn = (newHsn != null) ? newHsn.trim() : "";
	    String newRate1 = (newRate != null) ? newRate.toString() : BigDecimal.ZERO.toString();
	    newEcGstin = (newEcGstin != null) ? newEcGstin.trim() : "";

	    // Generate and return the key
	    return new StringJoiner(WEB_UPLOAD_KEY)
	            .add(sourceIdentifier)
	            .add(returnPeriod)
	            .add(newPos)
	            .add(newHsn)
	            .add(newRate1)
	            .add(newEcGstin)
	            .toString();
	}


	public String generateAtKey(String supplierGSTIN, String returnPeriod,
	        String transactionType, String newPos, BigDecimal newRate) {
	    
	    // Safely handle potential null values
	    supplierGSTIN = (supplierGSTIN != null) ? supplierGSTIN.trim() : "";
	    returnPeriod = (returnPeriod != null) ? returnPeriod.trim() : "";
	    transactionType = (transactionType != null) ? transactionType.trim() : "";
	    newPos = (newPos != null) ? newPos.trim() : "";
	    String newRate1 = (newRate != null) ? newRate.toString() : BigDecimal.ZERO.toString();

	    // Generate and return the key
	    return new StringJoiner(WEB_UPLOAD_KEY)
	            .add(supplierGSTIN)
	            .add(returnPeriod)
	            .add(transactionType)
	            .add(newPos)
	            .add(newRate1)
	            .toString();
	}

	public String generateAtaKey(String supplierGSTIN, String returnPeriod,
	        String transactionType, String newPos, BigDecimal newRate) {
	    
	    // Safely handle nullable inputs
	    supplierGSTIN = (supplierGSTIN != null) ? supplierGSTIN.trim() : "";
	    returnPeriod = (returnPeriod != null) ? returnPeriod.trim() : "";
	    transactionType = (transactionType != null) ? transactionType.trim() : "";
	    newPos = (newPos != null) ? newPos.trim() : "";

	    // Handle newRate safely and assign default value if null
	    String newRate1 = (newRate != null) ? newRate.toString() : BigDecimal.ZERO.toString();

	    // Construct and return the key
	    return new StringJoiner(WEB_UPLOAD_KEY)
	            .add(supplierGSTIN)
	            .add(returnPeriod)
	            .add(transactionType)
	            .add(newPos)
	            .add(newRate1)
	            .toString();
	}


	public String generateInvoiceKey(String sourceIdentifier,
			String returnPeriod, Integer serialNo, String from, String to) {

		sourceIdentifier = (sourceIdentifier != null)
				? (String.valueOf(sourceIdentifier)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		serialNo = (serialNo != null) ? serialNo : 0;
		from = (from != null) ? (String.valueOf(from)).trim() : "";
		to = (to != null) ? (String.valueOf(to)).trim() : "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(sourceIdentifier)
				.add(returnPeriod).add(serialNo.toString()).add(from).add(to)
				.toString();
	}

	public String generateHsnKey(String hsnOrSac, String uqc, String gstin, String returnPeriod) {

	    hsnOrSac = (hsnOrSac != null) ? hsnOrSac.trim() : "";
	    String trimmedUqc = (uqc != null) ? uqc.trim() : ""; // Introduced a new variable
	    gstin = (gstin != null) ? gstin.trim() : "";
	    returnPeriod = (returnPeriod != null) ? returnPeriod.trim() : "";

	    return new StringJoiner(WEB_UPLOAD_KEY)
	            .add(hsnOrSac)
	            .add(trimmedUqc)
	            .add(gstin)
	            .add(returnPeriod)
	            .toString();
	}


	public String generateNilKey(String sgstin, String returnPeriod, String supplyType) {
		sgstin = (sgstin != null) ? (String.valueOf(sgstin)).trim() : "";
		returnPeriod = (returnPeriod != null) ? (String.valueOf(returnPeriod)).trim()
				: "";
		supplyType = (supplyType != null) ? (String.valueOf(supplyType)).trim() : "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod)
				.add(supplyType).toString();
		
	}

}
