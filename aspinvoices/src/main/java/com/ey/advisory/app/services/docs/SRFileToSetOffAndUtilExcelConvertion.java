package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.SetOffAndUtilEntity;
import com.ey.advisory.app.data.entities.client.SetOffAndUtilExcelEntity;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToSetOffAndUtilExcelConvertion")
public class SRFileToSetOffAndUtilExcelConvertion {

	public List<SetOffAndUtilExcelEntity> convertSRFileToSetOffAndUtil(
			List<Object[]> listOfSetOffUtil,
			Gstr1FileStatusEntity updateFileStatus) {

		List<SetOffAndUtilExcelEntity> listSetOff = new ArrayList<>();
		SetOffAndUtilExcelEntity setOffAndUtil = null;
		for (Object[] arr : listOfSetOffUtil) {
			setOffAndUtil = new SetOffAndUtilExcelEntity();
			String sNo = getValues(arr[0]);
			String retType = getValues(arr[1]);
			String supplierGstin = getValues(arr[2]);
			String returnPeriod = getValues(arr[3]);
			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month < 12 && month > 01)
							&& (year < 9999 && year > 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}

			String decs = getValues(arr[4]);
			String taxPayRevCharge = getValues(arr[5]);
			String taxPayOthRevCharge = getValues(arr[6]);
			String taxAlreadyRevCharge = getValues(arr[7]);
			String taxAlreadyOthRevCharge = getValues(arr[8]);
			String adjRevCharge = getValues(arr[9]);
			String adjOthRevCharge = getValues(arr[10]);
			String paidThroughIgst = getValues(arr[11]);
			String paidThroughCgst = getValues(arr[12]);
			String paidThroughSgst = getValues(arr[13]);
			String paidThroughCess = getValues(arr[14]);
			String paidInCastCess = getValues(arr[15]);
			String paidInCastInterest = getValues(arr[16]);
			String paidInCashLateFee = getValues(arr[17]);
			String userDef1 = getNoTrancateValues(arr[18]);
			String userDef2 = getNoTrancateValues(arr[19]);
			String userDef3 = getNoTrancateValues(arr[20]);

			String setOffGstnKey = getSetOffGstnKey(arr);
			String setOffInvKey = getSetOffInvKey(arr);
			setOffAndUtil.setSNo(sNo);
			setOffAndUtil.setRetType(retType);
			setOffAndUtil.setSgstin(supplierGstin);
			setOffAndUtil.setRetPeriod(returnPeriod);
			setOffAndUtil.setDerivedRetPeriod(derivedRePeroid);
			setOffAndUtil.setDesc(decs);
			setOffAndUtil.setTaxPayableRevCharge(taxPayRevCharge);
			setOffAndUtil.setTaxPayableOthRevCharge(taxPayOthRevCharge);
			setOffAndUtil.setTaxAlreadyPaidRevCharge(taxAlreadyRevCharge);
			setOffAndUtil.setTaxAlreadyPaidOthRevCharge(taxAlreadyOthRevCharge);
			setOffAndUtil.setAdjRevCharge(adjRevCharge);
			setOffAndUtil.setAdjOthRevCharge(adjOthRevCharge);
			setOffAndUtil.setPaidThrouhItcIgst(paidThroughIgst);
			setOffAndUtil.setPaidThrouhItcCgst(paidThroughCgst);
			setOffAndUtil.setPaidThrouhItcSgst(paidThroughSgst);
			setOffAndUtil.setPaidThrouhItcCess(paidThroughCess);
			setOffAndUtil.setPaidInCashTaxCess(paidInCastCess);
			setOffAndUtil.setPaidInCashTaxInterest(paidInCastInterest);
			setOffAndUtil.setPaidInCashLateFee(paidInCashLateFee);
			if (setOffGstnKey != null && setOffGstnKey.length() > 100) {
				setOffAndUtil.setSetOffGstnKey(setOffGstnKey.substring(0, 100));
			} else {
				setOffAndUtil.setSetOffGstnKey(setOffGstnKey);
			}
			if (setOffInvKey != null && setOffInvKey.length() > 2200) {
				setOffAndUtil.setSetOffInvKey(setOffInvKey.substring(0, 2220));
			} else {
				setOffAndUtil.setSetOffInvKey(setOffInvKey);
			}

			setOffAndUtil.setUserDef1(userDef1);
			setOffAndUtil.setUserDef2(userDef2);
			setOffAndUtil.setUserDef3(userDef3);
			if (updateFileStatus != null) {
				setOffAndUtil.setFileId(updateFileStatus.getId());
				setOffAndUtil.setCreatedBy(updateFileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			setOffAndUtil.setCreatedOn(convertNow);
			listSetOff.add(setOffAndUtil);
		}
		return listSetOff;
	}

	public String getSetOffInvKey(Object[] arr) {
		String sNo = getValues(arr[0]);
		String retType = getValues(arr[1]);
		String supplierGstin = getValues(arr[2]);
		String returnPeriod = getValues(arr[3]);
		String decs = getValues(arr[4]);
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(sNo)
				.add(retType).add(supplierGstin).add(returnPeriod).add(decs)
				.toString();
	}

	public String getSetOffGstnKey(Object[] arr) {
		String sNo = getValues(arr[0]);
		String retType = getValues(arr[1]);
		String supplierGstin = getValues(arr[2]);
		String returnPeriod = getValues(arr[3]);
		String decs = getValues(arr[4]);
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(sNo)
				.add(retType).add(supplierGstin).add(returnPeriod).add(decs)
				.toString();
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

	public List<SetOffAndUtilEntity> convertSRFileToSetOffAndUtilConvertion(
			List<SetOffAndUtilExcelEntity> businessProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<SetOffAndUtilEntity> listSetOff = new ArrayList<>();

		SetOffAndUtilEntity setOff = null;
		for (SetOffAndUtilExcelEntity arr : businessProcessedRecords) {
			setOff = new SetOffAndUtilEntity();

			BigDecimal taxPayRevCharge = BigDecimal.ZERO;
			String igst = arr.getTaxPayableRevCharge();
			if (igst != null && !igst.isEmpty()) {
				taxPayRevCharge = NumberFomatUtil.getBigDecimal(igst);
				setOff.setTaxPayableRevCharge(taxPayRevCharge);
			}

			BigDecimal taxPayOthRevCahrge = BigDecimal.ZERO;
			String taxPayOthRev = arr.getTaxPayableOthRevCharge();
			if (taxPayOthRev != null && !taxPayOthRev.isEmpty()) {
				taxPayOthRevCahrge = NumberFomatUtil
						.getBigDecimal(taxPayOthRev);
				setOff.setTaxPayableOthRevCharge(taxPayOthRevCahrge);
			}
			BigDecimal taxAlrPaidRevCharge = BigDecimal.ZERO;
			String taxAlrPaidRev = arr.getTaxAlreadyPaidRevCharge();
			if (taxAlrPaidRev != null && !taxAlrPaidRev.isEmpty()) {
				taxAlrPaidRevCharge = NumberFomatUtil
						.getBigDecimal(taxAlrPaidRev);
				setOff.setTaxAlreadyPaidRevCharge(taxAlrPaidRevCharge);
			}

			BigDecimal taxAlrRevOth = BigDecimal.ZERO;
			String taxAlrOthRev = arr.getTaxAlreadyPaidOthRevCharge();
			if (taxAlrOthRev != null && !taxAlrOthRev.isEmpty()) {
				taxAlrRevOth = NumberFomatUtil.getBigDecimal(taxAlrOthRev);
				setOff.setTaxAlreadyPaidOthRevCharge(taxAlrRevOth);
			}
			BigDecimal adjRevChargeAmt = BigDecimal.ZERO;
			String adjRevCharge = arr.getAdjRevCharge();
			if (adjRevCharge != null && !adjRevCharge.isEmpty()) {
				adjRevChargeAmt = NumberFomatUtil.getBigDecimal(adjRevCharge);
				setOff.setAdjRevCharge(adjRevChargeAmt);
			}
			BigDecimal adjOthRevCharge = BigDecimal.ZERO;
			String adjOthRev = arr.getTaxAlreadyPaidOthRevCharge();
			if (adjOthRev != null && !adjOthRev.isEmpty()) {
				adjOthRevCharge = NumberFomatUtil.getBigDecimal(adjOthRev);
				setOff.setAdjOthRevCharge(adjOthRevCharge);
			}
			BigDecimal paidThIgst = BigDecimal.ZERO;
			String paidThItcIgst = arr.getPaidThrouhItcIgst();
			if (paidThItcIgst != null && !paidThItcIgst.isEmpty()) {
				paidThIgst = NumberFomatUtil.getBigDecimal(paidThItcIgst);
				setOff.setPaidThrouhItcIgst(paidThIgst);
			}
			BigDecimal paidThCgst = BigDecimal.ZERO;
			String paidThItcCgst = arr.getPaidThrouhItcCgst();
			if (paidThItcCgst != null && !paidThItcCgst.isEmpty()) {
				paidThCgst = NumberFomatUtil.getBigDecimal(paidThItcCgst);
				setOff.setPaidThrouhItcCgst(paidThCgst);
			}
			BigDecimal paidThSgst = BigDecimal.ZERO;
			String paidThItcSgst = arr.getPaidThrouhItcSgst();
			if (paidThItcSgst != null && !paidThItcSgst.isEmpty()) {
				paidThSgst = NumberFomatUtil.getBigDecimal(paidThItcSgst);
				setOff.setPaidThrouhItcSgst(paidThSgst);
			}
			BigDecimal paidThCess = BigDecimal.ZERO;
			String paidThItcCess = arr.getPaidThrouhItcCess();
			if (paidThItcCess != null && !paidThItcCess.isEmpty()) {
				paidThCess = NumberFomatUtil.getBigDecimal(paidThItcCess);
				setOff.setPaidThrouhItcCess(paidThCess);
			}
			BigDecimal paidInCashCess = BigDecimal.ZERO;
			String paidInCashCessAmt = arr.getPaidInCashTaxCess();
			if (paidInCashCessAmt != null && !paidInCashCessAmt.isEmpty()) {
				paidInCashCess = NumberFomatUtil
						.getBigDecimal(paidInCashCessAmt);
				setOff.setPaidInCashTaxCess(paidInCashCess);
			}
			BigDecimal paidInCashInterest = BigDecimal.ZERO;
			String paidInCashInterestAmt = arr.getPaidInCashTaxInterest();
			if (paidInCashInterestAmt != null
					&& !paidInCashInterestAmt.isEmpty()) {
				paidInCashInterest = NumberFomatUtil
						.getBigDecimal(paidInCashInterestAmt);
				setOff.setPaidInCashTaxInterest(paidInCashInterest);
			}
			BigDecimal paidInCashLateFee = BigDecimal.ZERO;
			String paidInCashLateFeeAmt = arr.getPaidInCashLateFee();
			if (paidInCashLateFeeAmt != null
					&& !paidInCashLateFeeAmt.isEmpty()) {
				paidInCashLateFee = NumberFomatUtil
						.getBigDecimal(paidInCashLateFeeAmt);
				setOff.setPaidInCashLateFee(paidInCashLateFee);
			}
			String userDef1 = arr.getUserDef1();
			String userDef2 = arr.getUserDef2();
			String userDef3 = arr.getUserDef3();

			Integer deriRetPeriod = null;

			if (arr.getRetPeriod() != null && !arr.getRetPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(arr.getRetPeriod());
			}

			String retGstnKey = arr.getSetOffGstnKey();
			String retInvKey = arr.getSetOffInvKey();
			setOff.setSNo(Integer.valueOf(arr.getSNo()));
			setOff.setRetType(arr.getRetType());
			setOff.setSgstin(arr.getSgstin());
			setOff.setRetPeriod(arr.getRetPeriod());
			setOff.setDerivedRetPeriod(deriRetPeriod);
			setOff.setDesc(arr.getDesc());
			if (updateFileStatus != null) {
				setOff.setFileId(updateFileStatus.getId());
			}
			setOff.setAsEnterTableId(arr.getId());
			setOff.setInfo(arr.isInfo());
			if (userDef1 != null && !userDef1.isEmpty()) {
				if (userDef1.length() > 100) {
					setOff.setUserDef1(userDef1.substring(0, 100));
				}
			} else {
				setOff.setUserDef1(userDef1);
			}
			if (userDef2 != null && !userDef2.isEmpty()) {
				if (userDef2.length() > 100) {
					setOff.setUserDef2(userDef2.substring(0, 100));
				}
			} else {
				setOff.setUserDef2(userDef2);
			}
			if (userDef3 != null && !userDef3.isEmpty()) {
				if (userDef3.length() > 100) {
					setOff.setUserDef3(userDef3.substring(0, 100));
				}
			} else {
				setOff.setUserDef3(userDef3);
			}
			setOff.setSetOffGstnKey(retGstnKey);
			setOff.setSetOffInvKey(retInvKey);
			setOff.setCreatedBy(arr.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			setOff.setCreatedOn(convertNow);
			listSetOff.add(setOff);
		}
		return listSetOff;
	}
}
