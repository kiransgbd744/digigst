package com.ey.advisory.app.services.common;

import java.util.StringJoiner;

import org.springframework.stereotype.Component;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr1GetKeyGenerator")
public class Gstr1GetKeyGenerator {

	private final static String WEB_UPLOAD_KEY = "|";

	public String generateB2csKey(String sgstin, String returnPeriod, String type, String newPos, String newEcGstin) {
		sgstin = (sgstin != null) ? (String.valueOf(sgstin)).trim() : "";
		returnPeriod = (returnPeriod != null) ? (String.valueOf(returnPeriod)).trim() : "";
		type = (type != null) ? (String.valueOf(type)).trim() : "";
		newPos = (newPos != null) ? (String.valueOf(newPos)).trim() : "";

		newEcGstin = (newEcGstin != null) ? (String.valueOf(newEcGstin)).trim() : "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod).add(type).add(newPos).add(newEcGstin)
				.toString();
	}

	public String generateB2csaKey(String sgstin, String returnPeriod, String type, String month, String newPos,
			String newEcGstin) {
		sgstin = (sgstin != null) ? (String.valueOf(sgstin)).trim() : "";
		returnPeriod = (returnPeriod != null) ? (String.valueOf(returnPeriod)).trim() : "";
		type = (type != null) ? (String.valueOf(type)).trim() : "";
		month = (month != null) ? (String.valueOf(month)).trim() : "";
		newPos = (newPos != null) ? (String.valueOf(newPos)).trim() : "";

		newEcGstin = (newEcGstin != null) ? (String.valueOf(newEcGstin)).trim() : "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod).add(type).add(month).add(newPos)
				.add(newEcGstin).toString();
	}

	public String generateAtKey(String sgstin, String returnPeriod, String type, String newPos) {
		sgstin = (sgstin != null) ? (String.valueOf(sgstin)).trim() : "";
		returnPeriod = (returnPeriod != null) ? (String.valueOf(returnPeriod)).trim() : "";
		type = (type != null) ? (String.valueOf(type)).trim() : "";
        newPos = (newPos != null) ? (String.valueOf(newPos)).trim() : "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod).add(type).add(newPos).toString();
	}
	
	public String generateSupEcomKey(String sgstin, String returnPeriod, String type, String eTin) {
		sgstin = (sgstin != null) ? (String.valueOf(sgstin)).trim() : "";
		returnPeriod = (returnPeriod != null) ? (String.valueOf(returnPeriod)).trim() : "";
		type = (type != null) ? (String.valueOf(type)).trim() : "";
        eTin = (eTin != null) ? (String.valueOf(eTin)).trim() : "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod).add(type).add(eTin).toString();
	}
	
	public String generateAtaKey(String sgstin, String returnPeriod, String type,String month, String newPos) {
		sgstin = (sgstin != null) ? (String.valueOf(sgstin)).trim() : "";
		returnPeriod = (returnPeriod != null) ? (String.valueOf(returnPeriod)).trim() : "";
		type = (type != null) ? (String.valueOf(type)).trim() : "";
		month = (month != null) ? (String.valueOf(month)).trim() : "";
        newPos = (newPos != null) ? (String.valueOf(newPos)).trim() : "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod).add(type).add(month).add(newPos).toString();
	}
	
	public String generateHsnKey(String sgstin, String returnPeriod, String hsn,String uqc, String recordType, String taxRate) {
		sgstin = (sgstin != null) ? (String.valueOf(sgstin)).trim() : "";
		returnPeriod = (returnPeriod != null) ? (String.valueOf(returnPeriod)).trim() : "";
		hsn = (hsn != null) ? (String.valueOf(hsn)).trim() : "";
		uqc = (uqc != null) ? (String.valueOf(uqc)).trim() : "";
		taxRate = (taxRate != null) ? (String.valueOf(taxRate)).trim() : "";
		recordType = (recordType != null) ? (String.valueOf(recordType)).trim() : "";
      
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin)
				.add(returnPeriod).add(recordType).add(hsn).add(uqc)
				.add(taxRate).toString();
      //  return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod).add(recordType).add(hsn).add(uqc).toString();
	}
	
	public String generateDocKey(String sgstin, String returnPeriod, String serialNo,String from,String to) {
		sgstin = (sgstin != null) ? (String.valueOf(sgstin)).trim() : "";
		returnPeriod = (returnPeriod != null) ? (String.valueOf(returnPeriod)).trim() : "";
		serialNo = (serialNo != null) ? (String.valueOf(serialNo)).trim() : "";
		from = (from != null) ? (String.valueOf(from)).trim() : "";
		to = (to != null) ? (String.valueOf(to)).trim() : "";
      
        return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod).add(serialNo).add(from).add(to).toString();
	}

}
