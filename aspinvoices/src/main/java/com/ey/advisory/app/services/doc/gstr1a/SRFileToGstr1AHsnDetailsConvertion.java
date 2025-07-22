package com.ey.advisory.app.services.doc.gstr1a;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredHsnEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AHsnFileUploadEntity;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Service("SRFileToGstr1AHsnDetailsConvertion")
public class SRFileToGstr1AHsnDetailsConvertion {

	public List<Gstr1AAsEnteredHsnEntity> convertSRFileToOutwardTransDoc(
			List<Object[]> listOfHsns, Gstr1FileStatusEntity fileStatus) {

		List<Gstr1AAsEnteredHsnEntity> hsns = new ArrayList<>();
		Gstr1AAsEnteredHsnEntity hsn = null;
		for (Object[] obj : listOfHsns) {
			hsn = new Gstr1AAsEnteredHsnEntity();
			String serialNo = getValues(obj[0]);
			String gstin = getValues(obj[1]);
			String returnPeriod = getValues(obj[2]);
			String hsnOrSac = getValues(obj[3]);
			String description = getValues(obj[4]);
			String uqc = getValues(obj[5]);

			String qty = getValues(obj[6]);
			String rate = getValues(obj[7]);
			String taxableValue = getValues(obj[8]);

			String igst = getValues(obj[9]);

			String cgst = getValues(obj[10]);

			String sgst = getValues(obj[11]);

			String cess = getValues(obj[12]);

			String invoiceValue = getValues(obj[13]);

			String generateHsnKey = generateHsnKey(obj);
			Integer deriRetPeriod = GenUtil.convertTaxPeriodToInt(returnPeriod);

			hsn.setSgstin(gstin);
			if (fileStatus != null) {
				hsn.setFileId(fileStatus.getId());
				hsn.setCreatedBy(fileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			hsn.setCreatedOn(convertNow);
			hsn.setSerialNo(serialNo);
			hsn.setReturnPeriod(returnPeriod);
			hsn.setHsn(hsnOrSac);
			hsn.setDescription(description);
			hsn.setUqc(uqc);
			hsn.setQuentity(qty);
			hsn.setTaxableValue(taxableValue);
			hsn.setIgst(igst);
			hsn.setCgst(cgst);
			hsn.setSgst(sgst);
			hsn.setCess(cess);
			hsn.setTotalValue(invoiceValue);
			hsn.setInvHsnKey(generateHsnKey);
			hsn.setDerivedRetPeriod(deriRetPeriod);
			hsn.setRate(rate);
			hsn.setError(false);
			hsns.add(hsn);
		}
		return hsns;
	}

	public String generateHsnKey(Object[] arr) {
		String hsnOrSac = arr[3] != null ? String.valueOf(arr[3]) : "";
		String uqc = arr[5] != null ? String.valueOf(arr[5]) : "";
		String gstin = arr[1] != null ? String.valueOf(arr[1]) : "";
		String returnPeriod = arr[2] != null ? String.valueOf(arr[2]) : "";
		String taxRate = arr[7] != null ? String.valueOf(arr[7]) : "";
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(gstin)
				.add(returnPeriod).add(hsnOrSac).add(taxRate).add(uqc)
				.toString();
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;
		return value;
	}

	public List<Gstr1AHsnFileUploadEntity> convertSRFileToHsnTransDoc(
			List<Gstr1AAsEnteredHsnEntity> busProcessRecords,
			Gstr1FileStatusEntity fileStatus) {

		List<Gstr1AHsnFileUploadEntity> hsns = new ArrayList<>();
		Gstr1AHsnFileUploadEntity hsn = null;
		for (Gstr1AAsEnteredHsnEntity obj : busProcessRecords) {
			hsn = new Gstr1AHsnFileUploadEntity();
			Integer deriRetPeriod = GenUtil
					.convertTaxPeriodToInt(obj.getReturnPeriod());
			hsn.setDerivedRetPeriod(deriRetPeriod);

			hsn.setSgstin(obj.getSgstin());
			if (fileStatus != null) {
				hsn.setFileId(fileStatus.getId());
				hsn.setCreatedBy(fileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			hsn.setCreatedOn(convertNow);

			hsn.setReturnPeriod(obj.getReturnPeriod());
			hsn.setHsn(obj.getHsn());
			hsn.setDescription(obj.getDescription());
			hsn.setUqc(obj.getUqc());

			BigDecimal qnt = BigDecimal.ZERO;
			String strQnt = String.valueOf(obj.getQuentity());
			if (strQnt != null && !strQnt.isEmpty()) {
				qnt = NumberFomatUtil.getBigDecimal(strQnt);
				hsn.setQuentity(qnt.setScale(3, BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal taxableValue = BigDecimal.ZERO;
			String strTax = String.valueOf(obj.getTaxableValue());
			if (strTax != null && !strTax.isEmpty()) {
				taxableValue = NumberFomatUtil.getBigDecimal(strTax);
				hsn.setTaxableValue(
						taxableValue.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal igstAmount = BigDecimal.ZERO;
			String strIgst = String.valueOf(obj.getIgst());
			if (strIgst != null && !strIgst.isEmpty()) {
				igstAmount = NumberFomatUtil.getBigDecimal(strIgst);
				hsn.setIgst(igstAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal cgstAmt = BigDecimal.ZERO;
			String strCgst = String.valueOf(obj.getCgst());
			if (strCgst != null && !strCgst.isEmpty()) {
				cgstAmt = NumberFomatUtil.getBigDecimal(strCgst);
				hsn.setCgst(cgstAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal sgstAmt = BigDecimal.ZERO;
			String strSgst = String.valueOf(obj.getSgst());
			if (strSgst != null && !strSgst.isEmpty()) {
				sgstAmt = NumberFomatUtil.getBigDecimal(strSgst);
				hsn.setSgst(sgstAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal cessAmt = BigDecimal.ZERO;
			String strCess = String.valueOf(obj.getCess());
			if (strCess != null && !strCess.isEmpty()) {
				cessAmt = NumberFomatUtil.getBigDecimal(strCess);
				hsn.setCess(cessAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal totalValu = BigDecimal.ZERO;
			String strTotalValue = String.valueOf(obj.getTotalValue());
			if (strTotalValue != null && !strTotalValue.isEmpty()) {
				totalValu = NumberFomatUtil.getBigDecimal(strTotalValue);
				hsn.setTotalValue(
						totalValu.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			hsn.setAsEnterId(obj.getId());

			hsns.add(hsn);
		}
		return hsns;
	}
}
