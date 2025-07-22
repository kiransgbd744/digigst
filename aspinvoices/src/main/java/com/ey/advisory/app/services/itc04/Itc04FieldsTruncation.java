/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.entities.client.Itc04ItemEntity;
import com.google.common.base.Strings;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Itc04FieldsTruncation")
public class Itc04FieldsTruncation {

	public void truncateHeaderFields(Itc04HeaderEntity document) {

		String jobworkerId = document.getJobWorkerId();
		if (!Strings.isNullOrEmpty(jobworkerId)) {
			if (jobworkerId.length() > 100) {
				document.setJobWorkerId(jobworkerId.substring(0, 100));
			}
		}
		String jobworkerName = document.getJobWorkerName();
		if (!Strings.isNullOrEmpty(jobworkerName)) {
			if (jobworkerName.length() > 100) {
				document.setJobWorkerName(jobworkerName.substring(0, 100));
			}
		}
		String userId = document.getUserId();
		if (!Strings.isNullOrEmpty(userId)) {
			if (userId.length() > 100) {
				document.setUserId(userId.substring(0, 100));
			}
		}
		String companyCode = document.getCompanyCode();
		if (!Strings.isNullOrEmpty(companyCode)) {
			if (companyCode.length() > 100) {
				document.setCompanyCode(companyCode.substring(0, 100));
			}
		}
		String sourceIdentifier = document.getSourceIdentifier();
		if (!Strings.isNullOrEmpty(sourceIdentifier)) {
			if (sourceIdentifier.length() > 100) {
				document.setSourceIdentifier(
						sourceIdentifier.substring(0, 100));
			}
		}
		String sourceFileName = document.getSourceFileName();
		if (!Strings.isNullOrEmpty(sourceFileName)) {
			if (sourceFileName.length() > 100) {
				document.setSourceFileName(sourceFileName.substring(0, 100));
			}
		}
		String division = document.getDivision();
		if (!Strings.isNullOrEmpty(division)) {
			if (division.length() > 100) {
				document.setDivision(division.substring(0, 100));
			}
		}
		String profitCentre1 = document.getProfitCentre1();
		if (!Strings.isNullOrEmpty(profitCentre1)) {
			if (profitCentre1.length() > 100) {
				document.setProfitCentre1(profitCentre1.substring(0, 100));
			}
		}
		String profitCentre2 = document.getProfitCentre2();
		if (!Strings.isNullOrEmpty(profitCentre2)) {
			if (profitCentre2.length() > 100) {
				document.setProfitCentre2(profitCentre2.substring(0, 100));
			}
		}
		String accVoucherNum = document.getAccountingVoucherNumber();
		if (!Strings.isNullOrEmpty(accVoucherNum)) {
			if (accVoucherNum.length() > 100) {
				document.setAccountingVoucherNumber(
						accVoucherNum.substring(0, 100));
			}
		}
	}

	public void truncateItemFields(Itc04ItemEntity item) {

		String productDesc = item.getProductDescription();
		if (!Strings.isNullOrEmpty(productDesc)) {
			if (productDesc.length() > 100) {
				item.setProductDescription(productDesc.substring(0, 100));
			}
		}
		String productCode = item.getProductCode();
		if (!Strings.isNullOrEmpty(productCode)) {
			if (productCode.length() > 100) {
				item.setProductCode(productCode.substring(0, 100));
			}
		}
		String natureOfJw = item.getNatureOfJw();
		if (!Strings.isNullOrEmpty(natureOfJw)) {
			if (natureOfJw.length() > 100) {
				item.setNatureOfJw(natureOfJw.substring(0, 100));
			}
		}
		String uqc = item.getUqc();
		if (!Strings.isNullOrEmpty(uqc)) {
			if (uqc.length() > 100) {
				item.setUqc(uqc.substring(0, 100));
			}
		}
		String plantCode = item.getPlantCode();
		if (!Strings.isNullOrEmpty(plantCode)) {
			if (plantCode.length() > 100) {
				item.setPlantCode(plantCode.substring(0, 100));
			}
		}
		String udf1 = item.getUserdefinedfield1();
		if (!Strings.isNullOrEmpty(udf1)) {
			if (udf1.length() > 500) {
				item.setUserdefinedfield1(udf1.substring(0, 500));
			}
		}
		String udf2 = item.getUserdefinedfield2();
		if (!Strings.isNullOrEmpty(udf2)) {
			if (udf2.length() > 500) {
				item.setUserdefinedfield2(udf2.substring(0, 500));
			}
		}
		String udf3 = item.getUserdefinedfield3();
		if (!Strings.isNullOrEmpty(udf3)) {
			if (udf3.length() > 500) {
				item.setUserdefinedfield3(udf3.substring(0, 500));
			}
		}
		String udf4 = item.getUserDefinedField4();
		if (!Strings.isNullOrEmpty(udf4)) {
			if (udf4.length() > 500) {
				item.setUserDefinedField4(udf4.substring(0, 500));
			}
		}
		String udf5 = item.getUserdefinedfield5();
		if (!Strings.isNullOrEmpty(udf5)) {
			if (udf5.length() > 500) {
				item.setUserdefinedfield5(udf5.substring(0, 500));
			}
		}
		String udf6 = item.getUserdefinedfield6();
		if (!Strings.isNullOrEmpty(udf6)) {
			if (udf6.length() > 500) {
				item.setUserdefinedfield6(udf6.substring(0, 500));
			}
		}
		String udf7 = item.getUserdefinedfield7();
		if (!Strings.isNullOrEmpty(udf7)) {
			if (udf7.length() > 500) {
				item.setUserdefinedfield7(udf7.substring(0, 500));
			}
		}
		String udf8 = item.getUserDefinedField8();
		if (!Strings.isNullOrEmpty(udf8)) {
			if (udf8.length() > 500) {
				item.setUserDefinedField8(udf8.substring(0, 500));
			}
		}
		String udf9 = item.getUserdefinedfield9();
		if (!Strings.isNullOrEmpty(udf9)) {
			if (udf9.length() > 500) {
				item.setUserdefinedfield9(udf9.substring(0, 500));
			}
		}
		String udf10 = item.getUserdefinedfield10();
		if (!Strings.isNullOrEmpty(udf10)) {
			if (udf10.length() > 500) {
				item.setUserdefinedfield10(udf10.substring(0, 500));
			}
		}
	}

}
