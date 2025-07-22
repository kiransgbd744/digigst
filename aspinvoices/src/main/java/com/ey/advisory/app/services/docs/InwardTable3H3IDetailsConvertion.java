package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.GSTConstants.N;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.data.entities.client.InwardTable3IDetailsEntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("InwardTable3H3IDetailsConvertion")
public class InwardTable3H3IDetailsConvertion {

	private final static String WEB_UPLOAD_KEY = "|";

	@Autowired
	@Qualifier("WebUploadKeyGenerator")
	private WebUploadKeyGenerator webUploadKeyGenerator;

	public List<InwardTable3IDetailsEntity> convertSRFileTo3h3iDoc(
			List<InwardTable3I3HExcelEntity> businessProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		List<InwardTable3IDetailsEntity> table3hDetailsEntities = new ArrayList<>();

		for (InwardTable3I3HExcelEntity obj : businessProcessedRecords) {

			InwardTable3IDetailsEntity table3h3i = new InwardTable3IDetailsEntity();

			BigDecimal taxableValue = BigDecimal.ZERO;
			String taxValue = obj.getTaxableValue();
			if (taxValue != null && !taxValue.isEmpty()) {
				taxableValue = NumberFomatUtil.getBigDecimal(taxValue);
				table3h3i.setTaxableValue(taxableValue);
			}
			BigDecimal rate = BigDecimal.ZERO;
			String rates = obj.getRate();
			if (rates != null && !rates.isEmpty()) {
				rate = NumberFomatUtil.getBigDecimal(rates);
				table3h3i.setTaxRate(rate);
			}
			BigDecimal integratedTaxAmount = BigDecimal.ZERO;
			String igst = obj.getIntegratedTaxAmount();
			if (igst != null && !igst.isEmpty()) {
				integratedTaxAmount = NumberFomatUtil.getBigDecimal(igst);
				table3h3i.setIntegratedTaxAmount(integratedTaxAmount);
			}

			BigDecimal centralTaxAmount = BigDecimal.ZERO;
			String cgst = obj.getCentralTaxAmount();
			if (cgst != null && !cgst.isEmpty()) {
				centralTaxAmount = NumberFomatUtil.getBigDecimal(cgst);
				table3h3i.setCentralTaxAmount(centralTaxAmount);
			}

			BigDecimal stateUtTaxAmount = BigDecimal.ZERO;
			String sgst = obj.getStateUTTaxAmount();
			if (sgst != null && !sgst.isEmpty()) {
				stateUtTaxAmount = NumberFomatUtil.getBigDecimal(sgst);
				table3h3i.setStateUTTaxAmount(stateUtTaxAmount);
			}

			BigDecimal cessAmount = BigDecimal.ZERO;
			String cess = obj.getCessAmount();
			if (cess != null && !cess.isEmpty()) {
				cessAmount = NumberFomatUtil.getBigDecimal(cess);
				table3h3i.setCessAmount(cessAmount);
			}

			BigDecimal totalVlue = BigDecimal.ZERO;
			String totalValue = obj.getTotalValue();
			if (totalValue != null && !totalValue.isEmpty()) {
				totalVlue = NumberFomatUtil.getBigDecimal(totalValue);
				table3h3i.setTotalValue(totalVlue);
			}

			BigDecimal availableIGST = BigDecimal.ZERO;
			String availIgst = obj.getAvailableIGST();
			if (availIgst != null && !availIgst.isEmpty()) {
				availableIGST = NumberFomatUtil.getBigDecimal(availIgst);
				table3h3i.setAvailableIGST(availableIGST);
			}
			BigDecimal availableCGST = BigDecimal.ZERO;
			String availCgst = obj.getAvailableCGST();
			if (availCgst != null && !availCgst.isEmpty()) {
				availableCGST = NumberFomatUtil.getBigDecimal(availCgst);
				table3h3i.setAvailableCGST(availableCGST);
			}
			BigDecimal availableSGST = BigDecimal.ZERO;
			String availSgst = obj.getAvailableSGST();
			if (availSgst != null && !availSgst.isEmpty()) {
				availableSGST = NumberFomatUtil.getBigDecimal(availSgst);
				table3h3i.setAvailableSGST(availableSGST);
			}
			BigDecimal availableCESS = BigDecimal.ZERO;
			String availCess = obj.getAvailableCess();
			if (availCess != null && !availCess.isEmpty()) {
				availableCESS = NumberFomatUtil.getBigDecimal(availCess);
				table3h3i.setAvailableCess(availableCESS);
			}
			String userDefined1 = obj.getUserDefined1();
			String userDefined2 = obj.getUserDefined2();
			String userDefined3 = obj.getUserDefined3();
			if (updateFileStatus != null) {
				table3h3i.setFileId(updateFileStatus.getId());
			}
			Integer deriRetPeriod = null;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}

			table3h3i.setReturnType(obj.getReturnType());
			table3h3i.setCustGstin(obj.getRecipientGstin());
			table3h3i.setReturnPeriod(obj.getReturnPeriod());
			table3h3i.setSupplierGSTINorpan(obj.getSupplierGSTINorpan());
			table3h3i.setTransactionFlag(obj.getTransactionFlag());
			table3h3i.setSupplierGSTINorpan(obj.getSupplierGSTINorpan());
			if (obj.getSupplierName() != null
					&& obj.getSupplierName().length() > 100) {
				table3h3i.setSupplierName(
						obj.getSupplierName().substring(0, 100));
			} else {
				table3h3i.setSupplierName(obj.getSupplierName());
			}
			table3h3i.setDiffPercent(obj.getDiffPercent());
			table3h3i.setAutoPopulateToRefund(obj.getAutoPopulateToRefund());
			table3h3i.setSec70fIGSTFLAG(obj.getSec70fIGSTFLAG());
			table3h3i.setPos(obj.getPos());
			table3h3i.setHsn(obj.getHsn());
			table3h3i.setEligibilityIndicator(obj.getEligibilityIndicator());
			table3h3i.setProfitCentre(obj.getProfitCentre());
			table3h3i.setPlant(obj.getPlant());
			table3h3i.setDivision(obj.getDivision());
			table3h3i.setLocation(obj.getLocation());
			table3h3i.setPurchaseOrganisation(obj.getPurchaseOrganisation());
			table3h3i.setUserAccess1(obj.getUserAccess1());
			table3h3i.setUserAccess2(obj.getUserAccess2());
			table3h3i.setUserAccess3(obj.getUserAccess3());
			table3h3i.setUserAccess4(obj.getUserAccess4());
			table3h3i.setUserAccess5(obj.getUserAccess5());
			table3h3i.setUserAccess6(obj.getUserAccess6());
			table3h3i.setInfo(obj.isInfo());
			table3h3i.setAsEnterTableId(obj.getId());
			if (userDefined1 != null && userDefined1.length() > 100) {
				table3h3i.setUserDefined1(userDefined1.substring(0, 100));
			} else {
				table3h3i.setUserDefined1(userDefined1);
			}
			if (userDefined2 != null && userDefined2.length() > 100) {
				table3h3i.setUserDefined2(userDefined2.substring(0, 100));
			} else {
				table3h3i.setUserDefined2(userDefined2);
			}
			if (userDefined3 != null && userDefined3.length() > 100) {
				table3h3i.setUserDefined3(userDefined3.substring(0, 100));
			} else {
				table3h3i.setUserDefined3(userDefined3);
			}
			// table3h3i.set
			table3h3i.setDerivedRetPeriod(deriRetPeriod);
			table3h3i.setCreatedBy(obj.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			table3h3i.setCreatedOn(convertNow);
			table3h3i.setTable3h3iInvKey(obj.getTable3h3iInvKey());
			table3h3i.setTable3h3iGstnKey(obj.getTable3h3iGstnKey());
			table3hDetailsEntities.add(table3h3i);
			table3h3i.setCessAmount(cessAmount);
		}
		return table3hDetailsEntities;

	}

	public String getTable3h3iGstnKey(Object[] arr) {

		String returnType = (arr[0] != null) ? (String.valueOf(arr[0])) : "";
		String receiveGstin = (arr[1] != null) ? (String.valueOf(arr[1])) : "";
		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])) : "";
		String transFlag = (arr[3] != null) ? (String.valueOf(arr[3])) : "";
		String suppGstn = (arr[4] != null) ? (String.valueOf(arr[4])) : "";
		String pos = (arr[9] != null) ? (String.valueOf(arr[9])) : "";
		String hsn = (arr[10] != null) ? (String.valueOf(arr[10])) : "";
		String rate = (arr[12] != null) ? String.valueOf(arr[12]) : "";
		String diffPer = (arr[6] != null) ? (String.valueOf(arr[6])) : N;
		String sec7 = (arr[7] != null) ? (String.valueOf(arr[7])) : N;
		String autoPopulateRefund = (arr[8] != null) ? (String.valueOf(arr[8]))
				: N;

		return new StringJoiner(WEB_UPLOAD_KEY).add(returnType)
				.add(receiveGstin).add(returnPeriod).add(transFlag)
				.add(suppGstn).add(pos).add(hsn).add(rate).add(diffPer)
				.add(sec7).add(autoPopulateRefund).toString();

	}

	public String getTable3h3iInvKey(Object[] arr) {
		String returnType = (arr[0] != null) ? (String.valueOf(arr[0])) : "";
		String receiveGstin = (arr[1] != null) ? (String.valueOf(arr[1])) : "";
		String returnPeriod = (arr[2] != null) ? (String.valueOf(arr[2])) : "";
		String transFlag = (arr[3] != null) ? (String.valueOf(arr[3])) : "";
		String suppGstn = (arr[4] != null) ? (String.valueOf(arr[4])) : "";
		String pos = (arr[9] != null) ? (String.valueOf(arr[9])) : "";
		String hsn = (arr[10] != null) ? (String.valueOf(arr[10])) : "";
		String rate = (arr[12] != null) ? String.valueOf(arr[12]) : "";
		String diffPer = (arr[6] != null) ? (String.valueOf(arr[6])) : N;
		String sec7 = (arr[7] != null) ? (String.valueOf(arr[7])) : N;
		String autoPopulateRefund = (arr[8] != null) ? (String.valueOf(arr[8]))
				: N;

		String profitCenter = (arr[23] != null) ? (String.valueOf(arr[23]))
				: GSTConstants.NA;
		String plant = (arr[24] != null) ? (String.valueOf(arr[24]))
				: GSTConstants.NA;
		String div = (arr[25] != null) ? (String.valueOf(arr[25]))
				: GSTConstants.NA;

		String loc = (arr[26] != null) ? (String.valueOf(arr[26]))
				: GSTConstants.NA;
		String purchase = (arr[27] != null) ? (String.valueOf(arr[27]))
				: GSTConstants.NA;
		String user1 = (arr[28] != null) ? (String.valueOf(arr[28]))
				: GSTConstants.NA;

		String user2 = (arr[29] != null) ? (String.valueOf(arr[29]))
				: GSTConstants.NA;
		String user3 = (arr[30] != null) ? (String.valueOf(arr[30]))
				: GSTConstants.NA;
		String user4 = (arr[31] != null) ? (String.valueOf(arr[31]))
				: GSTConstants.NA;

		String user5 = (arr[32] != null) ? (String.valueOf(arr[32]))
				: GSTConstants.NA;
		String user6 = (arr[33] != null) ? (String.valueOf(arr[33]))
				: GSTConstants.NA;

		return new StringJoiner(WEB_UPLOAD_KEY).add(returnType)
				.add(receiveGstin).add(returnPeriod).add(transFlag)
				.add(suppGstn).add(pos).add(hsn).add(rate).add(diffPer)
				.add(sec7).add(autoPopulateRefund).add(profitCenter).add(plant)
				.add(div).add(loc).add(purchase).add(user1).add(user2)
				.add(user3).add(user4).add(user5).add(user6).toString();

	}
}