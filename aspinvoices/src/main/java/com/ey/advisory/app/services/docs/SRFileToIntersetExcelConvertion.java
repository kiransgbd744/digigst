package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.InterestAndLateFeeEntity;
import com.ey.advisory.app.data.entities.client.InterestExcelEntity;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToIntersetExcelConvertion")
public class SRFileToIntersetExcelConvertion {

	public List<InterestExcelEntity> convertSRFileToInterestExcel(
			List<Object[]> refundList, Gstr1FileStatusEntity updateFileStatus) {

		List<InterestExcelEntity> listRefunds = new ArrayList<>();
		InterestExcelEntity refund = null;
		for (Object[] arr : refundList) {
			refund = new InterestExcelEntity();

			String sNumber = getValues(arr[0]);
			String returnType = getValues(arr[1]);
			String supplierGstin = getValues(arr[2]);
			String returnPeriod = getValues(arr[3]);
			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month <= 12 && month >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}

			String returnTable = getValues(arr[4]);
			String interestIgst = getValues(arr[5]);
			String interestCgst = getValues(arr[6]);
			String interestSgst = getValues(arr[7]);
			String interestCess = getValues(arr[8]);
			String lateFeeCgst = getValues(arr[9]);
			String lateFeeSgst = getValues(arr[10]);
			String userDef1 = getNoTrancateValues(arr[11]);
			String userDef2 = getNoTrancateValues(arr[12]);
			String userDef3 = getNoTrancateValues(arr[13]);
			String refundGstnKey = getInterestGstnKey(arr);
			String refundInvKey = getInterestInvKey(arr);

			refund.setSNo(sNumber);
			refund.setReturnType(returnType);
			refund.setSgstin(supplierGstin);
			refund.setRetPeriod(returnPeriod);
			refund.setDerivedRetPeriod(derivedRePeroid);
			refund.setReturnTable(returnTable);
			refund.setInterestIgstAmt(interestIgst);
			refund.setInterestCgstAmt(interestCgst);
			refund.setInterestIgstAmt(interestSgst);
			refund.setInterestCessAmt(interestCess);
			refund.setLateCgstAmt(lateFeeCgst);
			refund.setLateSgstAmt(lateFeeSgst);
			refund.setUserDef1(userDef1);
			refund.setUserDef2(userDef2);
			refund.setUserDef3(userDef3);

			if (refundGstnKey != null && refundGstnKey.length() > 1000) {
				refund.setInterestGstnKey(refundGstnKey.substring(0, 1000));
			} else {
				refund.setInterestGstnKey(refundGstnKey);
			}
			if (refundInvKey != null && refundInvKey.length() > 2200) {
				refund.setInterestInvKey(refundInvKey.substring(0, 2220));
			} else {
				refund.setInterestInvKey(refundInvKey);
			}
			if (updateFileStatus != null) {
				refund.setFileId(updateFileStatus.getId());
				refund.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			refund.setCreatedOn(convertNow);
			listRefunds.add(refund);
		}
		return listRefunds;
	}

	public String getInterestInvKey(Object[] arr) {
		String sNo = (arr[0] != null) ? (String.valueOf(arr[0])).trim() : "";
		String returnType = (arr[1] != null) ? (String.valueOf(arr[1])).trim()
				: "";
		String supplierGstin = (arr[2] != null)
				? (String.valueOf(arr[2])).trim() : "";
		String returnPeriod = (arr[3] != null) ? (String.valueOf(arr[3])).trim()
				: "";
		String returnTable = (arr[4] != null) ? (String.valueOf(arr[4])).trim()
				: "";
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(sNo)
				.add(returnType).add(supplierGstin).add(returnPeriod)
				.add(returnTable).toString();
	}

	public String getInterestGstnKey(Object[] arr) {
		String sNo = (arr[0] != null) ? (String.valueOf(arr[0])).trim() : "";
		String returnType = (arr[1] != null) ? (String.valueOf(arr[1])).trim()
				: "";
		String supplierGstin = (arr[2] != null)
				? (String.valueOf(arr[2])).trim() : "";
		String returnPeriod = (arr[3] != null) ? (String.valueOf(arr[3])).trim()
				: "";
		String returnTable = (arr[4] != null) ? (String.valueOf(arr[4])).trim()
				: "";
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(sNo)
				.add(returnType).add(supplierGstin).add(returnPeriod)
				.add(returnTable).toString();
	}

	private String getNoTrancateValues(Object object) {
		String value = (object != null) ? String.valueOf(object).trim() : null;
		if (value != null && value.length() > 110) {
			return value.substring(0, 110).trim();
		}
		return value;
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public List<InterestAndLateFeeEntity> convertSRFileToIntersetAndLateFee(
			List<InterestExcelEntity> businessProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<InterestAndLateFeeEntity> listB2c = new ArrayList<>();

		InterestAndLateFeeEntity interest = null;
		for (InterestExcelEntity arr : businessProcessedRecords) {
			interest = new InterestAndLateFeeEntity();

			String interestIgst = arr.getInterestIgstAmt();
			if (interestIgst != null && !interestIgst.isEmpty()) {
				BigDecimal interestIgstAmt = NumberFomatUtil
						.getBigDecimal(interestIgst);
				interest.setInterestIgstAmt(interestIgstAmt);
			}

			String interestCgst = arr.getInterestCgstAmt();
			if (interestCgst != null && !interestCgst.isEmpty()) {
				BigDecimal interestCgstAmt = NumberFomatUtil
						.getBigDecimal(interestCgst);
				interest.setInterestCgstAmt(interestCgstAmt);
			}

			String interestSgst = arr.getInterestSgstAmt();
			if (interestSgst != null && !interestSgst.isEmpty()) {
				BigDecimal interestSgstAmt = NumberFomatUtil
						.getBigDecimal(interestSgst);
				interest.setInterestSgstAmt(interestSgstAmt);
			}

			String interestCess = arr.getInterestCessAmt();
			if (interestCess != null && !interestCess.isEmpty()) {
				BigDecimal interestCessAmt = NumberFomatUtil
						.getBigDecimal(interestCess);
				interest.setInterestCessAmt(interestCessAmt);
			}

			String lateFeeCgst = arr.getLateCgstAmt();
			if (lateFeeCgst != null && !lateFeeCgst.isEmpty()) {
				BigDecimal lateFeeCgstAmt = NumberFomatUtil
						.getBigDecimal(lateFeeCgst);
				interest.setLateCgstAmt(lateFeeCgstAmt);
			}

			String lateFeeSgst = arr.getLateSgstAmt();
			if (lateFeeSgst != null && !lateFeeSgst.isEmpty()) {
				BigDecimal lateFeeSgstAmt = NumberFomatUtil
						.getBigDecimal(lateFeeSgst);
				interest.setLateCgstAmt(lateFeeSgstAmt);
			}
			String userDef1 = arr.getUserDef1();
			String userDef2 = arr.getUserDef2();
			String userDef3 = arr.getUserDef3();

			Integer deriRetPeriod = null;

			if (arr.getRetPeriod() != null && !arr.getRetPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(arr.getRetPeriod());
			}

			String interestGstnKey = arr.getInterestGstnKey();
			String interestInvKey = arr.getInterestInvKey();

			interest.setSNo(Integer.valueOf(arr.getSNo()));
			interest.setReturnType(arr.getReturnType());
			interest.setSgstin(arr.getSgstin());
			interest.setRetPeriod(arr.getRetPeriod());
			interest.setDerivedRetPeriod(deriRetPeriod);
			interest.setReturnTable(arr.getReturnTable());
			if (updateFileStatus != null) {
				interest.setFileId(updateFileStatus.getId());
			}
			interest.setAsEnterTableId(arr.getId());
			interest.setInfo(arr.isInfo());
			interest.setReturnTable(arr.getReturnTable());
			if (userDef1 != null && !userDef1.isEmpty()) {
				if (userDef1.length() > 100) {
					interest.setUserDef1(userDef1.substring(0, 100));
				}
			} else {
				interest.setUserDef1(userDef1);
			}
			if (userDef2 != null && !userDef2.isEmpty()) {
				if (userDef2.length() > 100) {
					interest.setUserDef2(userDef2.substring(0, 100));
				}
			} else {
				interest.setUserDef2(userDef2);
			}
			if (userDef3 != null && !userDef3.isEmpty()) {
				if (userDef3.length() > 100) {
					interest.setUserDef3(userDef3.substring(0, 100));
				}
			} else {
				interest.setUserDef3(userDef3);
			}
			interest.setInterestGstnKey(interestGstnKey);
			interest.setInterestInvKey(interestInvKey);
			interest.setCreatedBy(arr.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			interest.setCreatedOn(convertNow);

			listB2c.add(interest);
		}
		return listB2c;
	}
}
