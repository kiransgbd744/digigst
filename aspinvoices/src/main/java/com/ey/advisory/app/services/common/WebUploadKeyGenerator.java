package com.ey.advisory.app.services.common;

import static com.ey.advisory.common.GSTConstants.NA;

import java.math.BigDecimal;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

@Component("WebUploadKeyGenerator")
public class WebUploadKeyGenerator {
	private final static String WEB_UPLOAD_KEY = "|";

	public String generateB2csKey(String sourceIdentifier, String returnPeriod,
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
	}

	public String generateAtKey(String supplierGSTIN, String returnPeriod,
			String transactionType, String newPos, BigDecimal newRate) {
		supplierGSTIN = (supplierGSTIN != null)
				? (String.valueOf(supplierGSTIN)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		transactionType = (transactionType != null)
				? (String.valueOf(transactionType)).trim() : "";
		newPos = (newPos != null) ? (String.valueOf(newPos)).trim() : "";

		String newRate1 = (newRate != null) ? newRate.toString()
				: BigDecimal.ZERO.toString();
		newRate.toString();

		return new StringJoiner(WEB_UPLOAD_KEY).add(supplierGSTIN)
				.add(returnPeriod).add(transactionType).add(newPos)
				.add(newRate1).toString();
	}

	public String generateAtaKey(String supplierGSTIN, String returnPeriod,
			String transactionType, String newPos, BigDecimal newRate) {
		supplierGSTIN = (supplierGSTIN != null)
				? (String.valueOf(supplierGSTIN)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		transactionType = (transactionType != null)
				? (String.valueOf(transactionType)).trim() : "";
		newPos = (newPos != null) ? (String.valueOf(newPos)).trim() : "";

		String newRate1 = (newRate != null) ? newRate.toString()
				: BigDecimal.ZERO.toString();
		newRate.toString();

		return new StringJoiner(WEB_UPLOAD_KEY).add(supplierGSTIN)
				.add(returnPeriod).add(transactionType).add(newPos)
				.add(newRate1).toString();
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

	public String generateHsnKey(String hsnOrSac, String uqc, String gstin,
			String returnPeriod) {

		hsnOrSac = (hsnOrSac != null) ? (String.valueOf(hsnOrSac)).trim() : "";
		uqc = (returnPeriod != null) ? (String.valueOf(returnPeriod)).trim()
				: "";
		gstin = (gstin != null) ? (String.valueOf(gstin)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(hsnOrSac).add(uqc)
				.add(gstin).add(returnPeriod).toString();
	}

	public String generateNilKey(String sgstin, String returnPeriod,
			String supplyType) {
		sgstin = (sgstin != null) ? (String.valueOf(sgstin)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		supplyType = (supplyType != null) ? (String.valueOf(supplyType)).trim()
				: "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod)
				.add(supplyType).toString();

	}

	public String generateTable4Key(String returnType, String supplierGstin,
			String returnPeriod, String ecomGstin) {

		returnType = (returnType != null) ? (String.valueOf(returnType)).trim()
				: "";
		supplierGstin = (supplierGstin != null)
				? (String.valueOf(supplierGstin)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		ecomGstin = (ecomGstin != null) ? (String.valueOf(ecomGstin)).trim()
				: "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(ecomGstin).toString();

	}

	public String generatetable3H3Ikey(String returnType, String receiveGstin,
			String returnPeriod, String pos, String supplierGstionorPan,
			String pos2, String hsnSac, BigDecimal rate,
			String differentialPercentageFlag, String section7ofIGSTFlag,
			String autoPopulateToRefund) {

		returnType = (returnType != null) ? (String.valueOf(returnType)).trim()
				: "";
		receiveGstin = (receiveGstin != null)
				? (String.valueOf(receiveGstin)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		pos = (pos != null) ? (String.valueOf(pos)).trim() : "";

		String rate1 = (rate != null) ? rate.toString()
				: BigDecimal.ZERO.toString();
		rate.toString();

		return new StringJoiner(WEB_UPLOAD_KEY).add(returnType)
				.add(receiveGstin).add(returnPeriod).add(pos).add(rate1)
				.toString();
	}

	/**
	 * @param returnType
	 * @param supplierGstin
	 * @param returnPeriod
	 * @param pos
	 * @param rate
	 * @param diffPer
	 * @param section7
	 * @param autoPopRefund
	 * @return
	 */
	public String generateB2cKey(String returnType, String supplierGstin,
			String returnPeriod, String pos, BigDecimal rate, String diffPer,
			String section7, String autoPopRefund) {
		returnType = (returnType != null) ? (String.valueOf(returnType)).trim()
				: "";
		supplierGstin = (supplierGstin != null)
				? (String.valueOf(supplierGstin)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		pos = (pos != null) ? (String.valueOf(pos)).trim() : "";

		String rate1 = (rate != null) ? rate.toString()
				: BigDecimal.ZERO.toString();
		rate.toString();

		return new StringJoiner(WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(pos).add(rate1)
				.add(diffPer).add(section7).add(autoPopRefund).toString();
	}

	public String generateB2cInvKey(String returnType, String supplierGstin,
			String returnPeriod, String pos, BigDecimal rate, String diffPer,
			String section7, String autoPopRefund, String profitCentre,
			String plant, String division, String location, String salesOrg,
			String distributeChannel, String userAccess1, String userAccess2,
			String userAccess3, String userAccess4, String userAccess5,
			String userAccess6) {

		returnType = (returnType != null) ? (String.valueOf(returnType)).trim()
				: "";
		supplierGstin = (supplierGstin != null)
				? (String.valueOf(supplierGstin)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		pos = (pos != null) ? (String.valueOf(pos)).trim() : "";

		String rate1 = (rate != null) ? rate.toString()
				: BigDecimal.ZERO.toString();

		profitCentre = (profitCentre != null)
				? (String.valueOf(profitCentre)).trim() : NA;
		plant = (plant != null) ? (String.valueOf(plant)).trim() : NA;
		division = (division != null) ? (String.valueOf(division)).trim() : NA;
		location = (location != null) ? (String.valueOf(location)).trim() : NA;
		salesOrg = (salesOrg != null) ? (String.valueOf(salesOrg)).trim() : NA;
		distributeChannel = (distributeChannel != null)
				? (String.valueOf(distributeChannel)).trim() : NA;
		userAccess1 = (userAccess1 != null)
				? (String.valueOf(userAccess1)).trim() : NA;
		userAccess2 = (userAccess2 != null)
				? (String.valueOf(userAccess2)).trim() : NA;
		userAccess3 = (userAccess3 != null)
				? (String.valueOf(userAccess3)).trim() : NA;
		userAccess4 = (userAccess4 != null)
				? (String.valueOf(userAccess4)).trim() : NA;
		userAccess5 = (userAccess5 != null)
				? (String.valueOf(userAccess5)).trim() : NA;
		userAccess6 = (userAccess6 != null)
				? (String.valueOf(userAccess6)).trim() : NA;
		return new StringJoiner(WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(pos).add(rate1)
				.add(diffPer).add(section7).add(autoPopRefund).add(profitCentre)
				.add(plant).add(division).add(location).add(salesOrg)
				.add(distributeChannel).add(userAccess1).add(userAccess2)
				.add(userAccess3).add(userAccess4).add(userAccess5)
				.add(userAccess6).toString();
	}

	public String generateTable4InvKey(String returnType, String supplierGstin,
			String returnPeriod, String ecomGstin, String profitCentre,
			String plant, String division, String location, String salesOrg,
			String distributeChannel, String userAccess1, String userAccess2,
			String userAccess3, String userAccess4, String userAccess5,
			String userAccess6) {

		returnType = (returnType != null) ? (String.valueOf(returnType)).trim()
				: "";
		supplierGstin = (supplierGstin != null)
				? (String.valueOf(supplierGstin)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		ecomGstin = (ecomGstin != null) ? (String.valueOf(ecomGstin)).trim()
				: "";
		profitCentre = (profitCentre != null)
				? (String.valueOf(profitCentre)).trim() : NA;
		plant = (plant != null) ? (String.valueOf(plant)).trim() : NA;
		division = (division != null) ? (String.valueOf(division)).trim() : NA;
		location = (location != null) ? (String.valueOf(location)).trim() : NA;
		salesOrg = (salesOrg != null) ? (String.valueOf(salesOrg)).trim() : NA;
		distributeChannel = (distributeChannel != null)
				? (String.valueOf(distributeChannel)).trim() : NA;
		userAccess1 = (userAccess1 != null)
				? (String.valueOf(userAccess1)).trim() : NA;
		userAccess2 = (userAccess2 != null)
				? (String.valueOf(userAccess2)).trim() : NA;
		userAccess3 = (userAccess3 != null)
				? (String.valueOf(userAccess3)).trim() : NA;
		userAccess4 = (userAccess4 != null)
				? (String.valueOf(userAccess4)).trim() : NA;
		userAccess5 = (userAccess5 != null)
				? (String.valueOf(userAccess5)).trim() : NA;
		userAccess6 = (userAccess6 != null)
				? (String.valueOf(userAccess6)).trim() : NA;

		return new StringJoiner(WEB_UPLOAD_KEY).add(returnType)
				.add(supplierGstin).add(returnPeriod).add(ecomGstin)
				.add(profitCentre).add(plant).add(division).add(location)
				.add(salesOrg).add(distributeChannel).add(userAccess1)
				.add(userAccess2).add(userAccess3).add(userAccess4)
				.add(userAccess5).add(userAccess6).toString();
	}
	
	public String generateAtInvKey(String supplierGSTIN, String returnPeriod,
			String transactionType, String month, String orgPos,
			BigDecimal orgRate, String newPos, BigDecimal newRate,
			String profitCentre, String plant, String division, String location,
			String salesOrganization, String disChannel, String userAccess1,
			String userAccess2, String userAccess3, String userAccess4,
			String userAccess5, String userAccess6) {
		supplierGSTIN = (supplierGSTIN != null)
				? (String.valueOf(supplierGSTIN)).trim() : "";
		returnPeriod = (returnPeriod != null)
				? (String.valueOf(returnPeriod)).trim() : "";
		transactionType = (transactionType != null)
				? (String.valueOf(transactionType)).trim() : "";
		month = (month != null) ? (String.valueOf(month)).trim() : "";
		orgPos = (orgPos != null) ? (String.valueOf(orgPos)).trim() : "";
		String orgRate1 = (orgRate != null) ? orgRate.toString()
				: BigDecimal.ZERO.toString();
		orgRate.toString();
		newPos = (newPos != null) ? (String.valueOf(newPos)).trim() : "";

		String newRate1 = (newRate != null) ? newRate.toString()
				: BigDecimal.ZERO.toString();
		newRate.toString();
		profitCentre = (profitCentre != null)
				? (String.valueOf(profitCentre)).trim() : "";
		plant = (plant != null) ? (String.valueOf(plant)).trim() : "";
		division = (division != null) ? (String.valueOf(division)).trim() : "";
		location = (location != null) ? (String.valueOf(location)).trim() : "";
		salesOrganization = (salesOrganization != null)
				? (String.valueOf(salesOrganization)).trim() : "";
		disChannel = (disChannel != null) ? (String.valueOf(disChannel)).trim()
				: "";
		userAccess1 = (userAccess1 != null)
				? (String.valueOf(userAccess1)).trim() : "";
		userAccess2 = (userAccess2 != null)
				? (String.valueOf(userAccess2)).trim() : "";
		userAccess3 = (userAccess3 != null)
				? (String.valueOf(userAccess3)).trim() : "";
		userAccess4 = (userAccess4 != null)
				? (String.valueOf(userAccess4)).trim() : "";
		userAccess5 = (userAccess5 != null)
				? (String.valueOf(userAccess5)).trim() : "";
		userAccess6 = (userAccess6 != null)
				? (String.valueOf(userAccess6)).trim() : "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(supplierGSTIN)
				.add(returnPeriod).add(transactionType).add(month).add(orgPos)
				.add(orgRate1).add(newPos).add(newRate1).add(profitCentre)
				.add(plant).add(division).add(location).add(salesOrganization)
				.add(disChannel).add(userAccess1).add(userAccess2)
				.add(userAccess3).add(userAccess4).add(userAccess5)
				.add(userAccess6).toString();
	}
}