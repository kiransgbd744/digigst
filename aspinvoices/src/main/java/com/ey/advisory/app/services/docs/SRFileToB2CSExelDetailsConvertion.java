package com.ey.advisory.app.services.docs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.service.GstnApi;
import com.google.common.base.Strings;

@Service("SRFileToB2CSExelDetailsConvertion")
public class SRFileToB2CSExelDetailsConvertion {
	private final static String WEB_UPLOAD_KEY = "|";

	@Autowired
	@Qualifier("WebUploadKeyGenerator")
	private WebUploadKeyGenerator webUploadKeyGenerator;

	@Autowired
	@Qualifier("GstnApi")
	private GstnApi gstnApi;

	public List<Gstr1AsEnteredB2csEntity> convertSRFileToB2csExcel(
			List<Object[]> b2csObjects, Gstr1FileStatusEntity fileStatusId) {

		List<Gstr1AsEnteredB2csEntity> listOfB2cs = new ArrayList<>();

		for (Object[] obj : b2csObjects) {
			Gstr1AsEnteredB2csEntity b2cs = new Gstr1AsEnteredB2csEntity();
			String sourceIdentifier = getValues(obj[0]);
			String returnPeriod = getValues(obj[1]);
			String transType = (obj[2] != null) ? String.valueOf(obj[2])
					: GSTConstants.N;
			if (transType != null && !transType.isEmpty()) {
				if (transType.length() > 100) {
					transType = transType.substring(0, 100);
				}
			}
			String month = getValues(obj[3]);
			String orgPos = getValues(obj[4]);
			String orgHsnOrSac = getValues(obj[5]);
			
			String orgUom = (obj[6] != null
					&& !obj[6].toString().trim().isEmpty())
							? String.valueOf(obj[6]).trim().toUpperCase() : null;
			
			//String orgUom = getValues(obj[6]);
			orgUom = orgUom != null ? orgUom : "OTH";
			String orgQnt = getValues(obj[7]);
			String orgRate = getValues(obj[8]);
			String orgTaxVal = getValues(obj[9]);
			String orgECGstin = getValues(obj[10]);
			String orgSupVal = getValues(obj[11]);
			String newPos = getValues(obj[12]);
			String newHsn = getValues(obj[13]);

			String newUom = (obj[14] != null
					&& !obj[14].toString().trim().isEmpty())
							? String.valueOf(obj[14]).trim().toUpperCase() : null;
			
			
			//String newUom = getValues(obj[14]);
			newUom = newUom != null ? newUom : "OTH";
			String newQnt = getValues(obj[15]);
			String newRate = getValues(obj[16]);
			String newTaxVal = getValues(obj[17]);
			String newEcGstin = getValues(obj[18]);
			String newSupValue = getValues(obj[19]);
			String igstAmt = getValues(obj[20]);
			String cgstAmt = getValues(obj[21]);
			String sgstAmt = getValues(obj[22]);
			String cessAmt = getValues(obj[23]);
			String totalValue = getValues(obj[24]);
			String profitCenter = getUserAttribuesValues(obj[25]);
			String plant = getUserAttribuesValues(obj[26]);
			String division = getUserAttribuesValues(obj[27]);
			String location = getUserAttribuesValues(obj[28]);
			String salesOrg = getUserAttribuesValues(obj[29]);
			String distributedChannel = getUserAttribuesValues(obj[30]);
			String user01 = getUserAttribuesValues(obj[31]);
			String user02 = getUserAttribuesValues(obj[32]);
			String user03 = getUserAttribuesValues(obj[33]);
			String user04 = getUserAttribuesValues(obj[34]);
			String user05 = getUserAttribuesValues(obj[35]);
			String user06 = getUserAttribuesValues(obj[36]);
			String userDef01 = getValues(obj[37]);
			String userDef02 = getValues(obj[38]);
			String userDef03 = getValues(obj[39]);
			String b2cInvKey = getInvKey(obj);
			if (b2cInvKey != null && b2cInvKey.length() > 2200) {
				b2cInvKey = b2cInvKey.substring(0, 2200);
			}
			String b2cGstnKey = getGstnKey(obj);
			if (b2cGstnKey != null && b2cGstnKey.length() > 1000) {
				b2cGstnKey = b2cGstnKey.substring(0, 1000);
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
			b2cs.setSgstin(sourceIdentifier);
			b2cs.setReturnPeriod(returnPeriod);
			if (fileStatusId != null) {
				b2cs.setFileId(fileStatusId.getId());
			}
			if (month != null && !month.isEmpty()) {
				b2cs.setSectionType(true);
			}
			b2cs.setTransType(transType);
			b2cs.setMonth(month);
			b2cs.setOrgPos(orgPos);
			b2cs.setOrgHsnOrSac(orgHsnOrSac);
			b2cs.setOrgUom(orgUom);
			b2cs.setOrgQnt(orgQnt);
			b2cs.setOrgRate(orgRate);
			b2cs.setOrgTaxVal(orgTaxVal);
			b2cs.setOrgCGstin(orgECGstin);
			b2cs.setOrgSupVal(orgSupVal);
			b2cs.setNewPos(newPos);
			b2cs.setNewHsnOrSac(newHsn);
			b2cs.setNewUom(newUom);
			b2cs.setNewQnt(newQnt);
			b2cs.setNewRate(newRate);
			b2cs.setNewTaxVal(newTaxVal);
			b2cs.setNewGstin(newEcGstin);
			b2cs.setNewSupVal(newSupValue);
			b2cs.setIgstAmt(igstAmt);
			b2cs.setCgstAmt(cgstAmt);
			b2cs.setSgstAmt(sgstAmt);
			b2cs.setCessAmt(cessAmt);
			b2cs.setTotalValue(totalValue);
			b2cs.setProfitCentre(profitCenter);
			b2cs.setPlant(plant);
			b2cs.setDivision(division);
			b2cs.setLocation(location);
			b2cs.setSalesOrganisation(salesOrg);
			b2cs.setDistributionChannel(distributedChannel);
			b2cs.setUserAccess1(user01);
			b2cs.setUserAccess2(user02);
			b2cs.setUserAccess3(user03);
			b2cs.setUserAccess4(user04);
			b2cs.setUserAccess5(user05);
			b2cs.setUserAccess6(user06);
			b2cs.setUserDef1(userDef01);
			b2cs.setUserDef2(userDef02);
			b2cs.setUserDef3(userDef03);
			b2cs.setInvB2csKey(b2cInvKey);
			b2cs.setGstnB2csKey(b2cGstnKey);
			b2cs.setDerivedRetPeriod(derivedRePeroid);

			Boolean naConsideredAsUqcValueInHsn = gstnApi
					.isNAConsideredAsUqcValueInHsn(b2cs.getReturnPeriod());
			if (naConsideredAsUqcValueInHsn) {
				/*String hsnCheck = b2cs.getOrgHsnOrSac() != null
						&& b2cs.getOrgHsnOrSac().trim().length() > 1
								? b2cs.getOrgHsnOrSac().substring(0, 1)
								: b2cs.getOrgHsnOrSac();
				String uqcCheck = b2cs.getOrgUom();
				if ("99".equalsIgnoreCase(hsnCheck)
						|| "OTH".equalsIgnoreCase(uqcCheck)
						|| Strings.isNullOrEmpty(uqcCheck)) {
					b2cs.setOrgUom("NA");
					b2cs.setOrgQnt("0");
				}
				*/
				String newHsnCheck = b2cs.getNewHsnOrSac() != null
						&& b2cs.getNewHsnOrSac().trim().length() > 1
								? b2cs.getNewHsnOrSac().substring(0, 2)
								: b2cs.getNewHsnOrSac();
				String newUqcCheck = b2cs.getNewUom();
				
			/*	if ("99".equalsIgnoreCase(newHsnCheck)
						|| "OTH".equalsIgnoreCase(newUqcCheck)
						|| Strings.isNullOrEmpty(newUqcCheck)) {
					b2cs.setNewUom("NA");
					b2cs.setNewQnt("0");
				}*/
			}

			if (fileStatusId != null) {
				b2cs.setCreatedBy(fileStatusId.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			b2cs.setCreatedOn(convertNow);
			listOfB2cs.add(b2cs);

		}
		return listOfB2cs;
	}

	private String getGstnKey(Object[] obj) {
		String sgstin = (obj[0] != null) ? String.valueOf(obj[0]).trim() : "";
		String retPeriod = (obj[1] != null) ? String.valueOf(obj[1]).trim()
				: "";
		String transType = (obj[2] != null) ? String.valueOf(obj[2]).trim()
				: GSTConstants.N;
		String month = (obj[3] != null) ? String.valueOf(obj[3]).trim()
				: GSTConstants.N;
		String orgPos = (obj[4] != null) ? String.valueOf(obj[4]).trim()
				: GSTConstants.N;
		String orgRate = (obj[8] != null) ? String.valueOf(obj[8]).trim()
				: GSTConstants.N;
		String orgEcom = (obj[10] != null) ? String.valueOf(obj[10]).trim()
				: GSTConstants.N;
		String newPos = (obj[12] != null) ? String.valueOf(obj[12]).trim()
				: GSTConstants.N;
		String newHsn = (obj[13] != null) ? String.valueOf(obj[13]).trim()
				: GSTConstants.N;
		String newUOM = (obj[14] != null) ? String.valueOf(obj[14]).trim().toUpperCase()
				: GSTConstants.N;
		String newRate = (obj[16] != null) ? String.valueOf(obj[16]).trim()
				: GSTConstants.N;
		String newEcom = (obj[18] != null) ? String.valueOf(obj[18]).trim()
				: GSTConstants.N;
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(retPeriod)
				.add(transType).add(month).add(orgPos).add(orgRate).add(orgEcom)
				.add(newPos).add(newHsn).add(newUOM).add(newRate).add(newEcom)
				.toString();

	}

	public String getInvKey(Object[] obj) {
		String sgstin = (obj[0] != null) ? String.valueOf(obj[0]).trim() : "";
		String retPeriod = (obj[1] != null) ? String.valueOf(obj[1]).trim()
				: "";
		String transType = (obj[2] != null) ? String.valueOf(obj[2]).trim()
				: GSTConstants.N;
		String month = (obj[3] != null) ? String.valueOf(obj[3]).trim()
				: GSTConstants.N;
		String orgPos = (obj[4] != null) ? String.valueOf(obj[4]).trim()
				: GSTConstants.N;
		String orgRate = (obj[8] != null) ? String.valueOf(obj[8]).trim()
				: GSTConstants.N;
		String orgEcom = (obj[10] != null) ? String.valueOf(obj[10]).trim()
				: GSTConstants.N;
		String newPos = (obj[12] != null) ? String.valueOf(obj[12]).trim() : "";
		String newHsn = (obj[13] != null) ? String.valueOf(obj[13]).trim() : "";
		String newUOM = (obj[14] != null) ? String.valueOf(obj[14]).trim()
				: GSTConstants.N;
		String newRate = (obj[16] != null) ? String.valueOf(obj[16]).trim()
				: "";
		String newEcom = (obj[18] != null) ? String.valueOf(obj[18]).trim()
				: GSTConstants.N;
		String profitCenter = (obj[25] != null) ? String.valueOf(obj[25]).trim()
				: GSTConstants.NA;
		String plant = (obj[26] != null) ? String.valueOf(obj[26]).trim()
				: GSTConstants.NA;
		String divison = (obj[27] != null) ? String.valueOf(obj[27]).trim()
				: GSTConstants.NA;
		String location = (obj[28] != null) ? String.valueOf(obj[28]).trim()
				: GSTConstants.NA;
		String salesOrg = (obj[29] != null) ? String.valueOf(obj[29]).trim()
				: GSTConstants.NA;
		String distributedChannel = (obj[30] != null)
				? String.valueOf(obj[30]).trim() : GSTConstants.NA;
		String user1 = (obj[31] != null) ? String.valueOf(obj[31]).trim()
				: GSTConstants.NA;
		String user2 = (obj[32] != null) ? String.valueOf(obj[32]).trim()
				: GSTConstants.NA;
		String user3 = (obj[33] != null) ? String.valueOf(obj[33]).trim()
				: GSTConstants.NA;
		String user4 = (obj[34] != null) ? String.valueOf(obj[34]).trim()
				: GSTConstants.NA;
		String user5 = (obj[35] != null) ? String.valueOf(obj[35]).trim()
				: GSTConstants.NA;
		String user6 = (obj[36] != null) ? String.valueOf(obj[36]).trim()
				: GSTConstants.NA;
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(retPeriod)
				.add(transType).add(month).add(orgPos).add(orgRate).add(orgEcom)
				.add(newPos).add(newHsn).add(newUOM).add(newRate).add(newEcom)
				.add(profitCenter).add(plant).add(divison).add(location)
				.add(salesOrg).add(distributedChannel).add(user1).add(user2)
				.add(user3).add(user4).add(user5).add(user6).toString();
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
