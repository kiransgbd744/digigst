package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.GSTConstants.WEB_UPLOAD_KEY;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2XProcessedTcsTdsEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2xTcsAndTcsaInvoicesEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2xTdsAndTdsaInvoicesEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2XProcessedTcsTdsRepository;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.core.async.JobStatusConstants;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("SRFileToTcsTdsConvertion")
public class SRFileToTcsTdsConvertion {

	@Autowired
	@Qualifier("WebUploadKeyGenerator")
	private WebUploadKeyGenerator webUploadKeyGenerator;

	@Autowired
	@Qualifier("Gstr2XProcessedTcsTdsRepository")
	private Gstr2XProcessedTcsTdsRepository gstr2XProcessedTcsTdsRepository;

	private String getValue(Object obj) {
		return obj != null && !obj.toString().isEmpty() ? String.valueOf(obj)
				: null;
	}

	public String genearateKey(String category, String taxGstin,
			String retPeriod, String deGstin, String monthOfDedUpl,
			String orgMonthOfdeductorOrCollectorUpl) {

		category = (category != null) ? (String.valueOf(category)).trim() : "";
		taxGstin = (taxGstin != null) ? (String.valueOf(taxGstin)).trim() : "";
		retPeriod = (retPeriod != null) ? (String.valueOf(retPeriod)).trim()
				: "";
		deGstin = (deGstin != null) ? (String.valueOf(deGstin)).trim() : "";
		monthOfDedUpl = (monthOfDedUpl != null)
				? (String.valueOf(monthOfDedUpl)).trim() : "";
		orgMonthOfdeductorOrCollectorUpl = (orgMonthOfdeductorOrCollectorUpl != null)
				? (String.valueOf(orgMonthOfdeductorOrCollectorUpl)).trim()
				: "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(category).add(taxGstin)
				.add(retPeriod).add(deGstin).add(monthOfDedUpl)
				.add(orgMonthOfdeductorOrCollectorUpl).toString();
	}

	public String processGenerateKey(String category, String taxGstin,
			String retPeriod) {
		category = (category != null) ? (String.valueOf(category)).trim() : "";
		taxGstin = (taxGstin != null) ? (String.valueOf(taxGstin)).trim() : "";
		retPeriod = (retPeriod != null) ? (String.valueOf(retPeriod)).trim()
				: "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(category).add(taxGstin)
				.add(retPeriod).toString();
	}

	public List<Gstr2XExcelTcsTdsEntity> convertSRFileToTcsTdsExcelDocOld(
			List<Object[]> listOfTdsTcs, Gstr1FileStatusEntity fileStatus) {

		List<Gstr2XExcelTcsTdsEntity> listTcsTDs = new ArrayList<>();

		List<String> sameKeys = new ArrayList<>();
		
		String cleanedTaxPeriod= null;
		String cleanedSgst = null;

		for (Object[] obj : listOfTdsTcs) {
			Gstr2XExcelTcsTdsEntity tdsTcs = new Gstr2XExcelTcsTdsEntity();
			
			String digiGstAction = getValue(obj[0]);
			String digiGstRemarks = getValue(obj[1]);
			String digiGstComment = getValue(obj[2]);
			String gstin = getValue(obj[3]);
			String type = getValue(obj[4]);
			String taxPeriod = getValue(obj[5]);
			String month = getValue(obj[6]);
			String gstinOfDeductorCollector = getValue(obj[7]);
			String deductorCollectorName = getValue(obj[8]);
			String docNo = getValue(obj[9]);
			String docDate = getValue(obj[10]);
			String orgMonth = getValue(obj[11]);
			String orgDocNo = getValue(obj[12]);
			String orgDocDate = getValue(obj[13]);
			String suppliesCollected = getValue(obj[14]);
			String suppliesReturned = getValue(obj[15]);
			String taxableValueNetSupplies = getValue(obj[16]);
			String igst = getValue(obj[17]);
			String cgst = getValue(obj[18]);
			String sgst = getValue(obj[19]);
			String invoiceValue = getValue(obj[20]);
			String originalTaxableValue = getValue(obj[21]);
			String originalInvoiceValue = getValue(obj[22]);
			String pos = getValue(obj[23]);
			String chkSum = getValue(obj[24]);
			String gstnAction = getValue(obj[25]);
			String gstnRemarks = getValue(obj[26]);
			String gstnComment = getValue(obj[27]);
			
			
			String processKey = processGenerateKey(type, gstin,
					taxPeriod);
			if (gstnAction == null) {
				gstnAction = GSTConstants.N;
			}
			if (digiGstAction == null) {
				digiGstAction = GSTConstants.N;
			}

			tdsTcs.setGstin(gstin);
			tdsTcs.setCtin(gstinOfDeductorCollector);
			if (fileStatus != null) {
				tdsTcs.setFileId(fileStatus.getId());
			}
			tdsTcs.setRetPeriod(taxPeriod);
			if (taxPeriod != null && taxPeriod.matches("[0-9]+")) {
				if (taxPeriod.length() == 6) {
					int months = Integer.parseInt(taxPeriod.substring(0, 2));
					int year = Integer.parseInt(taxPeriod.substring(2));
					if ((months <= 12 && months >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(taxPeriod);
						tdsTcs.setDerivedRetPeriod(derivedRetPeriod);
					}
				}
			} else {
				tdsTcs.setDerivedRetPeriod(9999);
			}
			if(month!=null){
	         cleanedTaxPeriod = month.replace("'", "");
			}
			if(sgst!=null){
				cleanedSgst = sgst.replace("'", "");
				}


			tdsTcs.setRecordType(type);
			tdsTcs.setDeductorUplMonth(cleanedTaxPeriod);
			tdsTcs.setOrgDeductorUplMonth(orgMonth);
			tdsTcs.setSaveAction(gstnAction);
			tdsTcs.setUserAction(digiGstAction);
			tdsTcs.setIgstAmt(igst);
			tdsTcs.setCgstAmt(cgst);
			tdsTcs.setSgstAmt(cleanedSgst);
			//colums to be added
			tdsTcs.setDigiGstRemarks(digiGstRemarks);
			tdsTcs.setDigiGstComment(digiGstComment);
			tdsTcs.setDeductorName(deductorCollectorName);
			tdsTcs.setDocNo(docNo);
			tdsTcs.setDocDate(docDate);
			tdsTcs.setOrgDocNo(orgDocNo);
			tdsTcs.setOrgDocDate(orgDocDate);
			tdsTcs.setSuppliesCollected(suppliesCollected);
			tdsTcs.setSuppliesReturned(suppliesReturned);
			tdsTcs.setNetSupplies(taxableValueNetSupplies);
			tdsTcs.setInvoiceValue(invoiceValue);
			tdsTcs.setOrgTaxableValue(originalTaxableValue);
			tdsTcs.setOrgInvoiceValue(originalInvoiceValue);
			tdsTcs.setPos(pos);
			tdsTcs.setChkSum(chkSum);
			tdsTcs.setGstnRemarks(gstnRemarks);
			tdsTcs.setGstnComment(gstnComment);

			
			String generateExmptKey = genearateKey(type, gstin,
					taxPeriod, gstinOfDeductorCollector,month,orgMonth);
			tdsTcs.setDocKey(generateExmptKey);
			if (tdsTcs.getDocKey() != null
					&& sameKeys.contains(tdsTcs.getDocKey())) {
				tdsTcs.setDelete(true);
			}
			sameKeys.add(generateExmptKey);
			tdsTcs.setPsKey(processKey);
			if (fileStatus != null) {
				tdsTcs.setCreatedBy(fileStatus.getUpdatedBy());
				if (fileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
					tdsTcs.setDataOriginTypeCode("E");
				} else if (fileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.ERP)) {
					tdsTcs.setDataOriginTypeCode("A");
				} else if (fileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
					tdsTcs.setDataOriginTypeCode(
							GSTConstants.DataOriginTypeCodes.SFTP
									.getDataOriginTypeCode());
				}

			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			tdsTcs.setCreatedOn(convertNow);
			listTcsTDs.add(tdsTcs);

		}
		return listTcsTDs;
	}

	public List<Gstr2XProcessedTcsTdsEntity> convertSRFileToTcsTds(
			List<Gstr2XExcelTcsTdsEntity> busProcessRecords,
			Gstr1FileStatusEntity fileStatus) {

		List<Gstr2XProcessedTcsTdsEntity> listTcsTDs = new ArrayList<>();

		for (Gstr2XExcelTcsTdsEntity obj : busProcessRecords) {
			Gstr2XProcessedTcsTdsEntity tdsTcs = new Gstr2XProcessedTcsTdsEntity();
			tdsTcs.setAsEnteredId(obj.getId());
			tdsTcs.setGstin(obj.getGstin());
			tdsTcs.setCtin(obj.getCtin());
			if (fileStatus != null) {
				tdsTcs.setFileId(fileStatus.getId());
			}
			tdsTcs.setRetPeriod(obj.getRetPeriod());
			if (obj.getRetPeriod() != null
					&& obj.getRetPeriod().matches("[0-9]+")) {
				if (obj.getRetPeriod().length() == 6) {
					int months = Integer
							.parseInt(obj.getRetPeriod().substring(0, 2));
					int year = Integer
							.parseInt(obj.getRetPeriod().substring(2));
					if ((months <= 12 && months >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(obj.getRetPeriod());
						tdsTcs.setDerivedRetPeriod(derivedRetPeriod);
					}
				}
			} else {
				tdsTcs.setDerivedRetPeriod(9999);
			}
			tdsTcs.setRecordType(obj.getRecordType());
			tdsTcs.setDeductorUplMonth(obj.getDeductorUplMonth());
			tdsTcs.setOrgDeductorUplMonth(obj.getOrgDeductorUplMonth());
			tdsTcs.setSaveAction(obj.getSaveAction());
			tdsTcs.setUserAction(obj.getUserAction());
			String strIgst = obj.getIgstAmt();
			if (strIgst != null && !strIgst.isEmpty()) {
				BigDecimal igst = NumberFomatUtil.getBigDecimal(strIgst);
				tdsTcs.setIgstAmt(igst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String strCgst = obj.getCgstAmt();
			if (strCgst != null && !strCgst.isEmpty()) {
				BigDecimal cgst = NumberFomatUtil.getBigDecimal(strCgst);
				tdsTcs.setCgstAmt(cgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			String strSgst = obj.getSgstAmt();
			if (strSgst != null && !strSgst.isEmpty()) {
				BigDecimal sgst = NumberFomatUtil.getBigDecimal(strSgst);
				tdsTcs.setSgstAmt(sgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			
			//colums to be added
			tdsTcs.setDigiGstRemarks(obj.getDigiGstRemarks());
			tdsTcs.setDigiGstComment(obj.getDigiGstComment());
			tdsTcs.setDeductorName(obj.getDeductorName());
			tdsTcs.setDocNo(obj.getDocNo());
			tdsTcs.setDocDate(obj.getDocDate());
			tdsTcs.setOrgDocNo(obj.getOrgDocNo());
			tdsTcs.setOrgDocDate(obj.getOrgDocDate());
			
			String strSuppliesCollected = obj.getSuppliesCollected();
			if (strSuppliesCollected != null && !strSuppliesCollected.isEmpty()) {
				BigDecimal suppliesCollected = NumberFomatUtil
						.getBigDecimal(strSuppliesCollected);
				tdsTcs.setSuppliesCollected(suppliesCollected
						.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			String strSuppliesReturned = obj.getSuppliesReturned();
			if (strSuppliesReturned != null && !strSuppliesReturned.isEmpty()) {
				BigDecimal suppliesReturned = NumberFomatUtil
						.getBigDecimal(strSuppliesReturned);
				tdsTcs.setSuppliesReturned(suppliesReturned
						.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			String strNetSupplies = obj.getNetSupplies();
			if (strNetSupplies != null && !strNetSupplies.isEmpty()) {
				BigDecimal netSupplies = NumberFomatUtil
						.getBigDecimal(strNetSupplies);
				tdsTcs.setNetSupplies(netSupplies
						.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			String strInvoiceValue = obj.getInvoiceValue();
			if (strInvoiceValue != null && !strInvoiceValue.isEmpty()) {
				BigDecimal invoiceValue = NumberFomatUtil
						.getBigDecimal(strInvoiceValue);
				tdsTcs.setInvoiceValue(invoiceValue
						.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			String strOrgTaxableValue = obj.getOrgTaxableValue();
			if (strOrgTaxableValue != null && !strOrgTaxableValue.isEmpty()) {
				BigDecimal orgTaxableValue = NumberFomatUtil
						.getBigDecimal(strOrgTaxableValue);
				tdsTcs.setOrgTaxableValue(orgTaxableValue
						.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			String StrOrgInvoiceValue = obj.getOrgInvoiceValue();
			if (StrOrgInvoiceValue != null && !StrOrgInvoiceValue.isEmpty()) {
				BigDecimal orgInvoiceValue = NumberFomatUtil
						.getBigDecimal(StrOrgInvoiceValue);
				tdsTcs.setOrgInvoiceValue(orgInvoiceValue
						.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			
			tdsTcs.setPos(obj.getPos());
			tdsTcs.setChkSum(obj.getChkSum());
			tdsTcs.setGstnRemarks(obj.getGstnRemarks());
			tdsTcs.setGstnComment(obj.getGstnComment());
			
			//tdsTcs.setSuppliesCollected(obj.getSuppliesCollected());
			//tdsTcs.setSuppliesReturned(suppliesReturned);
			/*tdsTcs.setNetSupplies(taxableValueNetSupplies);
			tdsTcs.setInvoiceValue(invoiceValue);
			tdsTcs.setOrgTaxableValue(originalTaxableValue);
			tdsTcs.setOrgInvoiceValue(originalInvoiceValue);*/
			

			/*tdsTcs.setRecordType(obj.getRecordType());
			tdsTcs.setDeductorUplMonth(obj.getDeductorUplMonth());
			tdsTcs.setOrgDeductorUplMonth(obj.getOrgDeductorUplMonth());

			String strTotValue = obj.getTaxableType();
			if (strTotValue != null && !strTotValue.isEmpty()) {
				BigDecimal total = NumberFomatUtil.getBigDecimal(strTotValue);
				tdsTcs.setTaxableType(
						total.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String strIgst = obj.getIgstAmt();
			if (strIgst != null && !strIgst.isEmpty()) {
				BigDecimal igst = NumberFomatUtil.getBigDecimal(strIgst);
				tdsTcs.setIgstAmt(igst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String strCgst = obj.getCgstAmt();
			if (strCgst != null && !strCgst.isEmpty()) {
				BigDecimal cgst = NumberFomatUtil.getBigDecimal(strCgst);
				tdsTcs.setCgstAmt(cgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			String strSgst = obj.getSgstAmt();
			if (strSgst != null && !strSgst.isEmpty()) {
				BigDecimal sgst = NumberFomatUtil.getBigDecimal(strSgst);
				tdsTcs.setSgstAmt(sgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String strRegSup = obj.getRegSup();
			if (strRegSup != null && !strRegSup.isEmpty()) {
				BigDecimal regSup = NumberFomatUtil.getBigDecimal(strRegSup);
				tdsTcs.setRegSup(
						regSup.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String strSupRetByRegBuyers = obj.getRegSupRet();
			if (strSupRetByRegBuyers != null
					&& !strSupRetByRegBuyers.isEmpty()) {
				BigDecimal supRetByRegBuyers = NumberFomatUtil
						.getBigDecimal(strSupRetByRegBuyers);
				tdsTcs.setRegSupRet(supRetByRegBuyers
						.setScale(2, BigDecimal.ROUND_HALF_EVEN).abs());
			}

			String strSupToUnRegBuyers = obj.getUnRegSup();
			if (strSupToUnRegBuyers != null && !strSupToUnRegBuyers.isEmpty()) {
				BigDecimal supToUnRegBuyers = NumberFomatUtil
						.getBigDecimal(strSupToUnRegBuyers);
				tdsTcs.setUnRegSup(supToUnRegBuyers.setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
			}

			String strSupRetByUnRegBuyers = obj.getUnRegSupRet();
			if (strSupRetByUnRegBuyers != null
					&& !strSupRetByUnRegBuyers.isEmpty()) {
				BigDecimal supRetByUnRegBuyers = NumberFomatUtil
						.getBigDecimal(strSupRetByUnRegBuyers);
				tdsTcs.setUnRegSupRet(supRetByUnRegBuyers
						.setScale(2, BigDecimal.ROUND_HALF_EVEN).abs());
			}*/

			//tdsTcs.setSaveAction(obj.getSaveAction());
			//tdsTcs.setUserAction(obj.getUserAction());
			tdsTcs.setDocKey(obj.getDocKey());
			tdsTcs.setDataOriginTypeCode(obj.getDataOriginTypeCode());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			tdsTcs.setCreatedOn(convertNow);
			tdsTcs.setPsKey(obj.getPsKey());

			Gstr2XProcessedTcsTdsEntity processEntity = gstr2XProcessedTcsTdsRepository
					.findActiveDocByDocKey(tdsTcs.getDocKey());
			if (processEntity != null && processEntity.getUserAction()
					.equals(tdsTcs.getUserAction())) {
				tdsTcs.setBatchId(processEntity.getBatchId());
				tdsTcs.setSentToGstn(processEntity.isSentToGstn());
				tdsTcs.setSavedToGstn(processEntity.isSavedToGstn());
				tdsTcs.setGstnError(processEntity.isGstnError());
				tdsTcs.setSentToGstnDate(processEntity.getSentToGstnDate());
				tdsTcs.setSavedToGstnDate(processEntity.getSavedToGstnDate());
				tdsTcs.setSubmitted(processEntity.isSubmitted());
				tdsTcs.setSubmittedDate(processEntity.getSubmittedDate());
				tdsTcs.setFiled(processEntity.isFiled());
				tdsTcs.setFiledDate(processEntity.getFiledDate());
				tdsTcs.setGstnErrorCode(processEntity.getGstnErrorCode());
				tdsTcs.setGstnErrorDesc(processEntity.getGstnErrorDesc());
			}

			listTcsTDs.add(tdsTcs);
		}
		return listTcsTDs;
	}

	public Gstr2XProcessedTcsTdsEntity convertBasedOnGetTcsAndTcsaReports(
			GetGstr2xTcsAndTcsaInvoicesEntity listTcsAndTcsa,
			Gstr2XProcessedTcsTdsEntity ent) {
		if (listTcsAndTcsa != null) {
			ent.setRecordType(listTcsAndTcsa.getRecordType());
			ent.setDeductorUplMonth(listTcsAndTcsa.getDeductorUploadedMonth());
			ent.setOrgDeductorUplMonth(
					listTcsAndTcsa.getOrgDeductorUploadedMonth());
			//ent.setTaxableType(listTcsAndTcsa.getTaxableType());
			ent.setIgstAmt(listTcsAndTcsa.getIgstAmt());
			ent.setCgstAmt(listTcsAndTcsa.getCgstAmt());
			ent.setSgstAmt(listTcsAndTcsa.getSgstAmt());
			/*ent.setRegSup(listTcsAndTcsa.getRegSupplier());
			ent.setRegSupRet(listTcsAndTcsa.getRegRetSupplier());
			ent.setUnRegSup(listTcsAndTcsa.getUnRegSupplier());
			ent.setUnRegSupRet(listTcsAndTcsa.getUnRegRetSupplier());*/
			ent.setSaveAction(listTcsAndTcsa.getSaveAction());
			ent.setUserAction(ent.getUserAction());
			//added some colums
			ent.setDigiGstRemarks(ent.getDigiGstRemarks());
			ent.setDigiGstComment(ent.getDigiGstComment());
			ent.setDeductorName(listTcsAndTcsa.getDeductorName());
			ent.setDocNo(listTcsAndTcsa.getDocNo());
			ent.setDocDate(listTcsAndTcsa.getDocDate());
			ent.setOrgDocNo(listTcsAndTcsa.getOrgDocNo());
			ent.setOrgDocDate(listTcsAndTcsa.getOrgDocDate());
			
			ent.setSuppliesCollected(listTcsAndTcsa.getSuppliesCollected());
			ent.setSuppliesReturned(listTcsAndTcsa.getSuppliesReturned());
			ent.setNetSupplies(listTcsAndTcsa.getNetSupplies());
			ent.setInvoiceValue(listTcsAndTcsa.getInvoiceValue());
			ent.setOrgTaxableValue(listTcsAndTcsa.getOrgTaxableValue());
			ent.setOrgInvoiceValue(listTcsAndTcsa.getOrgInvoiceValue());
			ent.setPos(listTcsAndTcsa.getPos());
			ent.setChkSum(listTcsAndTcsa.getChkSum());
			ent.setGstnRemarks(ent.getGstnRemarks());
			ent.setGstnComment(ent.getGstnComment());
			
			
			ent.setDocKey(listTcsAndTcsa.getDocKey());
			ent.setPsKey(ent.getPsKey());
			ent.setCreatedBy(ent.getCreatedBy());
			ent.setDataOriginTypeCode(ent.getDataOriginTypeCode());
			ent.setCreatedOn(ent.getCreatedOn());
			ent.setDerivedRetPeriod(listTcsAndTcsa.getDerReturnPeriod());
			ent.setBatchId(ent.getBatchId());
			ent.setSentToGstn(ent.isSentToGstn());
			ent.setSavedToGstn(ent.isSavedToGstn());
			ent.setGstnError(ent.isGstnError());
			ent.setSentToGstnDate(ent.getSentToGstnDate());
			ent.setSavedToGstnDate(ent.getSavedToGstnDate());
			ent.setSubmitted(ent.isSubmitted());
			ent.setSubmittedDate(ent.getSubmittedDate());
			ent.setFiled(ent.isFiled());
			ent.setFiledDate(ent.getFiledDate());
			ent.setGstnErrorCode(ent.getGstnErrorCode());
			ent.setGstnErrorDesc(ent.getGstnErrorDesc());
			return ent;
		}
		return ent;
	}

	public Gstr2XProcessedTcsTdsEntity convertBasedOnGetTdsAndTdsaReports(
			GetGstr2xTdsAndTdsaInvoicesEntity listTdsAndTdsa,
			Gstr2XProcessedTcsTdsEntity ent) {
		if (listTdsAndTdsa != null) {
			ent.setRecordType(listTdsAndTdsa.getRecordType());
			ent.setDeductorUplMonth(listTdsAndTdsa.getDeductorUploadedMonth());
			ent.setOrgDeductorUplMonth(
					listTdsAndTdsa.getOrgDeductorUploadedMonth());
			ent.setTaxableType(listTdsAndTdsa.getTaxableType());
			ent.setIgstAmt(listTdsAndTdsa.getIgstAmt());
			ent.setCgstAmt(listTdsAndTdsa.getCgstAmt());
			ent.setSgstAmt(listTdsAndTdsa.getSgstAmt());
			ent.setSaveAction(listTdsAndTdsa.getSaveAction());
			ent.setUserAction(ent.getUserAction());
			
			//added some colums
			ent.setDigiGstRemarks(ent.getDigiGstRemarks());
			ent.setDigiGstComment(ent.getDigiGstComment());
			ent.setDeductorName(listTdsAndTdsa.getDeductorName());
			ent.setDocNo(listTdsAndTdsa.getDocNo());
			ent.setDocDate(listTdsAndTdsa.getDocDate());
			ent.setOrgDocNo(listTdsAndTdsa.getOrgDocNo());
			ent.setOrgDocDate(listTdsAndTdsa.getOrgDocDate());
			
			ent.setSuppliesCollected(listTdsAndTdsa.getSuppliesCollected());
			ent.setSuppliesReturned(listTdsAndTdsa.getSuppliesReturned());
			ent.setNetSupplies(listTdsAndTdsa.getNetSupplies());
			ent.setInvoiceValue(listTdsAndTdsa.getInvoiceValue());
			ent.setOrgTaxableValue(listTdsAndTdsa.getOrgTaxableValue());
			ent.setOrgInvoiceValue(listTdsAndTdsa.getOrgInvoiceValue());
			ent.setPos(listTdsAndTdsa.getPos());
			ent.setChkSum(listTdsAndTdsa.getChkSum());
			ent.setGstnRemarks(ent.getGstnRemarks());
			ent.setGstnComment(ent.getGstnComment());
			
			
			
			ent.setDocKey(ent.getDocKey());
			ent.setPsKey(ent.getPsKey());
			ent.setCreatedBy(ent.getCreatedBy());
			ent.setDataOriginTypeCode(ent.getDataOriginTypeCode());
			ent.setCreatedOn(ent.getCreatedOn());
			ent.setDerivedRetPeriod(listTdsAndTdsa.getDerReturnPeriod());
			ent.setBatchId(ent.getBatchId());
			ent.setSentToGstn(ent.isSentToGstn());
			ent.setSavedToGstn(ent.isSavedToGstn());
			ent.setGstnError(ent.isGstnError());
			ent.setSentToGstnDate(ent.getSentToGstnDate());
			ent.setSavedToGstnDate(ent.getSavedToGstnDate());
			ent.setSubmitted(ent.isSubmitted());
			ent.setSubmittedDate(ent.getSubmittedDate());
			ent.setFiled(ent.isFiled());
			ent.setFiledDate(ent.getFiledDate());
			ent.setGstnErrorCode(ent.getGstnErrorCode());
			ent.setGstnErrorDesc(ent.getGstnErrorDesc());
			return ent;
		}
		return ent;
	}
}
