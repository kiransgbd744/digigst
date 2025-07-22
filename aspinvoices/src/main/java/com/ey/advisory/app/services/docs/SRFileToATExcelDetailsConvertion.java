package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.Gstr1ARDetailsEntity;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredAREntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;

@Service("SRFileToATExcelDetailsConvertion")
public class SRFileToATExcelDetailsConvertion {
	private final static String WEB_UPLOAD_KEY = "|";

	@Autowired
	@Qualifier("WebUploadKeyGenerator")
	private WebUploadKeyGenerator webUploadKeyGenerator;

	public List<Gstr1ARDetailsEntity> convertSRFileToAtaDoc(
			List<Gstr1AsEnteredAREntity> busProcessRecords,
			Gstr1FileStatusEntity fileStatus) {
		List<Gstr1ARDetailsEntity> listOfAta = new ArrayList<>();
		Gstr1ARDetailsEntity ataObj = null;
		for (Gstr1AsEnteredAREntity obj : busProcessRecords) {
			ataObj = new Gstr1ARDetailsEntity();

			BigDecimal orgRates = BigDecimal.ZERO;
			String orgRate = obj.getOrgRate();
			if (orgRate != null && !orgRate.isEmpty()) {
				orgRates = NumberFomatUtil.getBigDecimal(orgRate);
				ataObj.setOrgRate(orgRates);
			}
			BigDecimal orgGross = BigDecimal.ZERO;
			String orgGrosss = obj.getOrgGrossAdvRec();
			if (orgGrosss != null && !orgGrosss.isEmpty()) {
				orgGross = NumberFomatUtil.getBigDecimal(orgGrosss);
				ataObj.setOrgGrossAdvRec(orgGross);
			}
			BigDecimal newRate = BigDecimal.ZERO;
			String rate = obj.getNewRate();
			if (rate != null && !rate.isEmpty()) {
				newRate = NumberFomatUtil.getBigDecimal(rate);
				ataObj.setNewRate(newRate);
			}
			BigDecimal newGross = BigDecimal.ZERO;
			String newGros = obj.getNewGrossAdvRec();
			if (newGros != null && !newGros.isEmpty()) {
				newGross = NumberFomatUtil.getBigDecimal(newGros);
				ataObj.setNewGrossAdvRec(newGross);
			}
			BigDecimal igst = BigDecimal.ZERO;
			String igsts = obj.getIgstAmt();
			if (igsts != null && !igsts.isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(igsts);
				ataObj.setIgstAmt(igst);
			}
			BigDecimal cgst = BigDecimal.ZERO;
			String cgsts = obj.getCgstAmt();
			if (cgsts != null && !cgsts.isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(cgsts);
				ataObj.setCgstAmt(cgst);
			}
			BigDecimal sgst = BigDecimal.ZERO;
			String sgsts = obj.getSgstAmt();
			if (sgsts != null && !sgsts.isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(sgsts);
				ataObj.setSgstAmt(sgst);
			}
			BigDecimal cess = BigDecimal.ZERO;
			String cessAmt = obj.getCessAmt();
			if (cessAmt != null && !cessAmt.isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(cessAmt);
				ataObj.setCessAmt(cess);
			}

			ataObj.setSgstin(obj.getSgstin());
			if (fileStatus != null) {
				ataObj.setFileId(fileStatus.getId());
			}
			Integer deriRetPeriod = 0;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}
			ataObj.setReturnPeriod(obj.getReturnPeriod());
			ataObj.setTransType(obj.getTransType());
			ataObj.setMonth(obj.getMonth());
			ataObj.setOrgPos(obj.getOrgPos());
			ataObj.setNewPos(obj.getNewPos());
			ataObj.setDerivedRetPeriod(deriRetPeriod);
			ataObj.setProfitCentre(obj.getProfitCentre());
			ataObj.setPlant(obj.getPlant());
			ataObj.setLocation(obj.getLocation());
			ataObj.setDivision(obj.getDivision());
			ataObj.setSalesOrganisation(obj.getSalesOrganisation());
			ataObj.setDistributionChannel(obj.getDistributionChannel());
			ataObj.setUserAccess1(obj.getUserAccess1());
			ataObj.setUserAccess2(obj.getUserAccess2());
			ataObj.setUserAccess3(obj.getUserAccess3());
			ataObj.setUserAccess4(obj.getUserAccess4());
			ataObj.setUserAccess5(obj.getUserAccess5());
			ataObj.setUserAccess6(obj.getUserAccess6());
			ataObj.setUserDef1(obj.getUserDef1());
			ataObj.setUserDef2(obj.getUserDef2());
			ataObj.setUserDef3(obj.getUserDef3());
			if (fileStatus != null) {
				ataObj.setCreatedBy(fileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			obj.setCreatedOn(convertNow);
			ataObj.setSectionType(obj.isSectionType());
			ataObj.setGstnAtKey(obj.getGstnAtKey());
			ataObj.setInvAtKey(obj.getInvAtKey());
			listOfAta.add(ataObj);
		}

		return listOfAta;
	}

	public List<Gstr1AsEnteredAREntity> convertSRFileToATExcel(
			List<Object[]> atObjects, Gstr1FileStatusEntity fileStatus) {
		List<Gstr1AsEnteredAREntity> listOfAt = new ArrayList<>();

		Gstr1AsEnteredAREntity obj = null;

		for (Object[] arr : atObjects) {
			obj = new Gstr1AsEnteredAREntity();
			String supplierGSTIN = getValues(arr[0]);
			String returnPeriod = getValues(arr[1]);
			String transactionType = getValues(arr[2]);
			if(transactionType == null || transactionType.isEmpty()){
				transactionType = GSTConstants.N;
			}
			String month = getValues(arr[3]);
			String orgPos = getValues(arr[4]);
			String orgRate = getValues(arr[5]);
			String orgGrossAt = getValues(arr[6]);
			String newPos = getValues(arr[7]);
			String newRate = getValues(arr[8]);
			String newGrossAt = getValues(arr[9]);
			String igstAmt = getValues(arr[10]);
			String cgstAmt = getValues(arr[11]);
			String sgstAmt = getValues(arr[12]);
			String cessAmt = getValues(arr[13]);
			String profitCentre = getUserAttribuesValues(arr[14]);
			String plant = getUserAttribuesValues(arr[15]);
			String division = getUserAttribuesValues(arr[16]);
			String location = getUserAttribuesValues(arr[17]);
			String salesOrganization = getValues(arr[18]);
			String disChannel = getValues(arr[19]);
			String userAccess1 = getUserAttribuesValues(arr[20]);
			String userAccess2 = getUserAttribuesValues(arr[21]);
			String userAccess3 = getUserAttribuesValues(arr[22]);
			String userAccess4 = getUserAttribuesValues(arr[23]);
			String userAccess5 = getUserAttribuesValues(arr[24]);
			String userAccess6 = getUserAttribuesValues(arr[25]);
			String userdefined1 = getValues(arr[26]);
			String userdefined2 = getValues(arr[27]);
			String userdefined3 = getValues(arr[28]);

			String invKey = getInvKey(arr);
			if (invKey != null && invKey.length() > 2200) {
				invKey = invKey.substring(0, 2200);
			}
			String gstnKey = getGstnKey(arr);
			if (gstnKey != null && gstnKey.length() > 1000) {
				gstnKey = gstnKey.substring(0, 1000);
			}
			if (fileStatus != null) {
				obj.setFileId(fileStatus.getId());
				obj.setCreatedBy(fileStatus.getUpdatedBy());
			}
			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int months = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((months <= 12 && months >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}
			obj.setSgstin(supplierGSTIN);
			obj.setReturnPeriod(returnPeriod);

			obj.setReturnPeriod(returnPeriod);
			obj.setDerivedRetPeriod(derivedRePeroid);
			obj.setSgstin(supplierGSTIN);
			obj.setReturnPeriod(returnPeriod);
			obj.setReturnType(returnPeriod);
			obj.setTransType(transactionType);
			obj.setMonth(month);
			obj.setOrgPos(orgPos);
			obj.setOrgRate(orgRate);
			obj.setOrgGrossAdvRec(orgGrossAt);
			obj.setNewPos(newPos);
			obj.setNewRate(newRate);
			obj.setNewGrossAdvRec(newGrossAt);
			obj.setIgstAmt(igstAmt);
			obj.setCgstAmt(cgstAmt);
			obj.setSgstAmt(sgstAmt);
			obj.setCessAmt(cessAmt);
			obj.setProfitCentre(profitCentre);
			obj.setDivision(division);
			obj.setLocation(location);
			obj.setPlant(plant);
			obj.setSalesOrganisation(salesOrganization);
			obj.setDistributionChannel(disChannel);
			obj.setUserAccess1(userAccess1);
			obj.setUserAccess2(userAccess2);
			obj.setUserAccess3(userAccess3);
			obj.setUserAccess4(userAccess4);
			obj.setUserAccess5(userAccess5);
			obj.setUserAccess6(userAccess6);
			obj.setUserDef1(userdefined1);
			obj.setUserDef2(userdefined2);
			obj.setUserDef3(userdefined3);
			obj.setInvAtKey(invKey);
			obj.setGstnAtKey(gstnKey);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			obj.setCreatedOn(convertNow);
			if (month != null && !month.isEmpty()) {
				obj.setSectionType(true);
			}

			listOfAt.add(obj);
		}

		return listOfAt;
	}

	private String getGstnKey(Object[] obj) {
		String supplierGSTIN = (obj[0] != null) ? String.valueOf(obj[0]).trim()
				: "";
		String returnPeriod = (obj[1] != null) ? String.valueOf(obj[1]).trim()
				: "";
		String transactionType = (obj[2] != null)
				? String.valueOf(obj[2]).trim() : GSTConstants.N;
		String month = (obj[3] != null) ? String.valueOf(obj[3]).trim() : "";
		String orgPos = (obj[4] != null) ? String.valueOf(obj[4]).trim() : "";
		String orgRate = (obj[5] != null) ? String.valueOf(obj[5]).trim() : "";

		String newPos = (obj[7] != null) ? String.valueOf(obj[7]).trim() : "";

		String newRate = (obj[8] != null) ? String.valueOf(obj[8]).trim() : "";

		return new StringJoiner(WEB_UPLOAD_KEY).add(supplierGSTIN)
				.add(returnPeriod).add(transactionType).add(month).add(orgPos)
				.add(orgRate).add(newPos).add(newRate).toString();

	}

	private String getInvKey(Object[] obj) {
		String supplierGSTIN = (obj[0] != null) ? String.valueOf(obj[0]).trim()
				: "";
		String returnPeriod = (obj[1] != null) ? String.valueOf(obj[1]).trim()
				: "";
		String transactionType = (obj[2] != null)
				? String.valueOf(obj[2]).trim() : GSTConstants.N;
		String month = (obj[3] != null) ? String.valueOf(obj[3]).trim() : "";
		String orgPos = (obj[4] != null) ? String.valueOf(obj[4]).trim() : "";
		String orgRate = (obj[5] != null) ? String.valueOf(obj[5]).trim() : "";

		String newPos = (obj[7] != null) ? String.valueOf(obj[7]).trim() : "";

		String newRate = (obj[8] != null) ? String.valueOf(obj[8]).trim() : "";
		String profitCentre = (obj[14] != null) ? String.valueOf(obj[14]).trim()
				: GSTConstants.NA;
		String plant = (obj[15] != null) ? String.valueOf(obj[15]).trim()
				: GSTConstants.NA;
		String division = (obj[16] != null) ? String.valueOf(obj[16]).trim()
				: GSTConstants.NA;
		String location = (obj[17] != null) ? String.valueOf(obj[17]).trim()
				: GSTConstants.NA;
		String salesOrganization = (obj[18] != null)
				? String.valueOf(obj[18]).trim() : GSTConstants.NA;
		String disChannel = (obj[19] != null) ? String.valueOf(obj[19]).trim()
				: GSTConstants.NA;
		String userAccess1 = (obj[20] != null) ? String.valueOf(obj[20]).trim()
				: GSTConstants.NA;
		String userAccess2 = (obj[21] != null) ? String.valueOf(obj[21]).trim()
				: GSTConstants.NA;
		String userAccess3 = (obj[22] != null) ? String.valueOf(obj[22]).trim()
				: GSTConstants.NA;
		String userAccess4 = (obj[23] != null) ? String.valueOf(obj[23]).trim()
				: GSTConstants.NA;
		String userAccess5 = (obj[24] != null) ? String.valueOf(obj[24]).trim()
				: GSTConstants.NA;
		String userAccess6 = (obj[25] != null) ? String.valueOf(obj[25]).trim()
				: GSTConstants.NA;
		return new StringJoiner(WEB_UPLOAD_KEY).add(supplierGSTIN)
				.add(returnPeriod).add(transactionType).add(month).add(orgPos)
				.add(orgRate).add(newPos).add(newRate).add(profitCentre)
				.add(plant).add(division).add(location).add(salesOrganization)
				.add(disChannel).add(userAccess1).add(userAccess2)
				.add(userAccess3).add(userAccess4).add(userAccess5)
				.add(userAccess6).toString();
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	private String getUserAttribuesValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 110) {
			return value.substring(0, 110).trim();
		}
		return value;
	}

}
