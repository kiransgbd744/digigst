package com.ey.advisory.app.services.doc.gstr1a;

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
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilDetailsEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExmptSummaryEntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.async.JobStatusConstants;

@Service("SRFileToGstr1ANilDetailsConvertion")
public class SRFileToGstr1ANilDetailsConvertion {

	@Autowired
	@Qualifier("WebUploadKeyGenerator")
	private WebUploadKeyGenerator webUploadKeyGenerator;

	@Autowired
	@Qualifier("GstnApi")
	private GstnApi gstnApi;

	public List<Gstr1ANilNonExemptedAsEnteredEntity> convertSRFileToNilExcelDoc(
			List<Object[]> listOfNil, Gstr1FileStatusEntity fileStatus) {

		List<Gstr1ANilNonExemptedAsEnteredEntity> listNil = new ArrayList<>();

		for (Object[] obj : listOfNil) {
			Gstr1ANilNonExemptedAsEnteredEntity nil = new Gstr1ANilNonExemptedAsEnteredEntity();
			String sgstin = getValue(obj[0]);
			String returnPeriod = getValue(obj[1]);
			String hsn = getValue(obj[2]);
			String description = getValue(obj[3]);
			
			String uqc = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]).trim().toUpperCase() : null;
			
			//String uqc = getValue(obj[4]);
			uqc = uqc != null ? uqc : "OTH";
			String quantity = getValue(obj[5]);
			String nilInterReg = getValue(obj[6]);
			String nilIntraReg = getValue(obj[7]);
			String nilInterUReg = getValue(obj[8]);
			String nilIntraUReg = getValue(obj[9]);
			String extInterReg = getValue(obj[10]);
			String extIntraReg = getValue(obj[11]);
			String extInterUReg = getValue(obj[12]);
			String extIntraUReg = getValue(obj[13]);
			String nonInterReg = getValue(obj[14]);
			String nonIntraReg = getValue(obj[15]);
			String nonInterUReg = getValue(obj[16]);
			String nonIntraUReg = getValue(obj[17]);
			nil.setSgstin(sgstin);
			if (fileStatus != null) {
				nil.setFileId(fileStatus.getId());
			}
			nil.setReturnPeriod(returnPeriod);
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int months = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((months <= 12 && months >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						nil.setDerivedRetPeriod(derivedRetPeriod);
					}
				}
			} else {
				nil.setDerivedRetPeriod(9999);
			}
			nil.setNilInterReg(nilInterReg);
			nil.setNilIntraReg(nilIntraReg);
			nil.setNilInterUnReg(nilInterUReg);
			nil.setNilIntraUnReg(nilIntraUReg);
			nil.setNonInterReg(nonInterReg);
			nil.setNonIntraReg(nonIntraReg);
			nil.setNonInterUnReg(nonInterUReg);
			nil.setNonIntraUnReg(nonIntraUReg);
			nil.setExtInterReg(extInterReg);
			nil.setExtIntraReg(extIntraReg);
			nil.setExtInterUnReg(extInterUReg);
			nil.setExtIntraUnReg(extIntraUReg);
			nil.setHsn(hsn);
			nil.setDescription(description);
			nil.setUqc(
					uqc != null && !uqc.isEmpty() ? uqc.toUpperCase() : null);
			nil.setQnt(quantity);
			String generateExmptKey = genearateKey(sgstin, returnPeriod, hsn,
					description, uqc);
			nil.setNKey(generateExmptKey);
			if (fileStatus != null) {
				nil.setCreatedBy(fileStatus.getUpdatedBy());
				if (fileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
					nil.setDataOriginType("E");
				} else if (fileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.ERP)) {
					nil.setDataOriginType("A");
				}

			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			nil.setCreatedOn(convertNow);
			Boolean naConsideredAsUqcValueInHsn = gstnApi
					.isNAConsideredAsUqcValueInHsn(nil.getReturnPeriod());
			if (naConsideredAsUqcValueInHsn) {
				String hsnCheck = nil.getHsn() != null
						&& nil.getHsn().trim().length() > 1
								? nil.getHsn().substring(0, 2) : nil.getHsn();
				String uqcCheck = nil.getUqc();
				/*
				 * if ("99".equalsIgnoreCase(hsnCheck) ||
				 * "OTH".equalsIgnoreCase(uqcCheck) ||
				 * Strings.isNullOrEmpty(uqcCheck)) { nil.setUqc("NA");
				 * nil.setQnt("0"); }
				 */
			}

			listNil.add(nil);

		}
		return listNil;
	}

	private String getValue(Object obj) {
		String str = obj != null && !obj.toString().isEmpty()
				? String.valueOf(obj) : null;
		return str;
	}

	private String genearateKey(String sgstin, String returnPeriod, String hsn,
			String description, String uqc) {
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod)
				.add(hsn).add(description).add(uqc).toString();
	}

	public List<Gstr1ANilNonExemptedAsEnteredEntity> convertSRFileToNilNonExmpt(
			List<Gstr1ANilNonExemptedAsEnteredEntity> strProcessRecords,
			Gstr1FileStatusEntity fileStatus) {

		List<Gstr1ANilNonExemptedAsEnteredEntity> listNil = new ArrayList<>();
		String generateExmptKey = null;

		for (Gstr1ANilNonExemptedAsEnteredEntity obj : strProcessRecords) {
			Gstr1ANilNonExemptedAsEnteredEntity nil = new Gstr1ANilNonExemptedAsEnteredEntity();
			nil.setId(obj.getId());
			nil.setSgstin(obj.getSgstin());
			if (fileStatus != null) {
				nil.setFileId(fileStatus.getId());
			}
			nil.setReturnPeriod(obj.getReturnPeriod());
			if (obj.getReturnPeriod() != null
					&& obj.getReturnPeriod().matches("[0-9]+")) {
				if (obj.getReturnPeriod().length() == 6) {
					int months = Integer
							.valueOf(obj.getReturnPeriod().substring(0, 2));
					int year = Integer
							.valueOf(obj.getReturnPeriod().substring(2));
					if ((months <= 12 && months >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(obj.getReturnPeriod());
						nil.setDerivedRetPeriod(derivedRetPeriod);
					}
				}
			} else {
				nil.setDerivedRetPeriod(9999);
			}
			nil.setNilInterReg(obj.getNilInterReg());
			nil.setHsn(obj.getHsn());
			nil.setUqc(obj.getUqc() != null && !obj.getUqc().isEmpty()
					? obj.getUqc().toUpperCase() : null);
			nil.setDescription(obj.getDescription());
			nil.setQnt(obj.getQnt());
			nil.setDataOriginType(obj.getDataOriginType());
			nil.setNilIntraReg(obj.getNilIntraReg());
			nil.setNilInterUnReg(obj.getNilInterUnReg());
			nil.setNilIntraUnReg(obj.getNilIntraUnReg());
			nil.setNonInterReg(obj.getNonInterReg());
			nil.setNonIntraReg(obj.getNonIntraReg());
			nil.setNonInterUnReg(obj.getNonInterUnReg());
			nil.setNonIntraUnReg(obj.getNonIntraUnReg());
			nil.setExtInterReg(obj.getExtInterReg());
			nil.setExtIntraReg(obj.getExtIntraReg());
			nil.setExtInterUnReg(obj.getExtInterUnReg());
			nil.setExtIntraUnReg(obj.getExtIntraUnReg());
			
			if(!GSTConstants.NA.equalsIgnoreCase(obj.getUqc())){
				
				 generateExmptKey = genearateKey(obj.getSgstin(),
						obj.getReturnPeriod(), obj.getHsn(), obj.getDescription(),
						(obj.getUqc().equalsIgnoreCase("NA")) ? "OTH":obj.getUqc());
				}else{
				 generateExmptKey = genearateKey(obj.getSgstin(),
						obj.getReturnPeriod(), obj.getHsn(), obj.getDescription(),
						(obj.getUqc().equalsIgnoreCase("NA")) ? "NA":obj.getUqc());

				}
			
			
			nil.setDelete(obj.isDelete());
			nil.setInfo(obj.isInfo());
			nil.setError(obj.isError());
			nil.setNKey(generateExmptKey);
			if (fileStatus != null) {
				nil.setCreatedBy(fileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			nil.setCreatedOn(convertNow);
			listNil.add(nil);

		}
		return listNil;
	}

	public List<Gstr1ANilDetailsEntity> convertSRFileToNNE(
			List<Gstr1ANilNonExemptedAsEnteredEntity> busProcessRecords,
			Gstr1FileStatusEntity fileStatus) {

		List<Gstr1ANilDetailsEntity> listNil = new ArrayList<>();
		String generateExmptKey = null;

		for (Gstr1ANilNonExemptedAsEnteredEntity obj : busProcessRecords) {
			Gstr1ANilDetailsEntity nil = new Gstr1ANilDetailsEntity();
			nil.setSgstin(obj.getSgstin());
			if (fileStatus != null) {
				nil.setFileId(fileStatus.getId());
			}
			nil.setReturnPeriod(obj.getReturnPeriod());
			if (obj.getReturnPeriod() != null
					&& obj.getReturnPeriod().matches("[0-9]+")) {
				if (obj.getReturnPeriod().length() == 6) {
					int months = Integer
							.valueOf(obj.getReturnPeriod().substring(0, 2));
					int year = Integer
							.valueOf(obj.getReturnPeriod().substring(2));
					if ((months <= 12 && months >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(obj.getReturnPeriod());
						nil.setDerivedRetPeriod(derivedRetPeriod);
					}
				}
			} else {
				nil.setDerivedRetPeriod(9999);
			}
			nil.setAsEnterId(obj.getId());
			nil.setDataOriginType(obj.getDataOriginType());
			nil.setHsn(obj.getHsn());
			nil.setDescription(obj.getDescription() != null
					&& obj.getDescription().trim().length() > 300
							? obj.getDescription().substring(0, 300)
							: obj.getDescription());
			nil.setUqc(obj.getUqc());

			BigDecimal quantityInt = BigDecimal.ZERO;
			String qnt = obj.getQnt();
			if (qnt != null && !qnt.isEmpty()) {
				quantityInt = NumberFomatUtil.getBigDecimal(qnt);
				nil.setQnt(quantityInt.setScale(3, BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal nilInterRegInt = BigDecimal.ZERO;
			String nilInterReg = obj.getNilInterReg();
			if (nilInterReg != null && !nilInterReg.isEmpty()) {
				nilInterRegInt = NumberFomatUtil.getBigDecimal(nilInterReg);
				nil.setNilInterReg(
						nilInterRegInt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal nilIntraRegInt = BigDecimal.ZERO;
			String nilIntraReg = obj.getNilIntraReg();
			if (nilIntraReg != null && !nilIntraReg.isEmpty()) {
				nilIntraRegInt = NumberFomatUtil.getBigDecimal(nilIntraReg);
				nil.setNilIntraReg(
						nilIntraRegInt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal nilInterRegUnInt = BigDecimal.ZERO;
			String nilInterUnReg = obj.getNilInterUnReg();
			if (nilInterUnReg != null && !nilInterUnReg.isEmpty()) {
				nilInterRegUnInt = NumberFomatUtil.getBigDecimal(nilInterUnReg);
				nil.setNilInterUnReg(nilInterRegUnInt);
			}
			BigDecimal nilIntraRegUnInt = BigDecimal.ZERO;
			String nilIntraUnReg = obj.getNilIntraUnReg();
			if (nilIntraUnReg != null && !nilIntraUnReg.isEmpty()) {
				nilIntraRegUnInt = NumberFomatUtil.getBigDecimal(nilIntraUnReg);
				nil.setNilIntraUnReg(nilIntraRegUnInt.setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal nonInterRegInt = BigDecimal.ZERO;
			String nonInterReg = obj.getNonInterReg();
			if (nonInterReg != null && !nonInterReg.isEmpty()) {
				nonInterRegInt = NumberFomatUtil.getBigDecimal(nonInterReg);
				nil.setNonInterReg(
						nonInterRegInt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal nonIntraRegInt = BigDecimal.ZERO;
			String nonIntraReg = obj.getNonIntraReg();
			if (nonIntraReg != null && !nonIntraReg.isEmpty()) {
				nonIntraRegInt = NumberFomatUtil.getBigDecimal(nonIntraReg);
				nil.setNonIntraReg(
						nonIntraRegInt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal nonInterRegUnInt = BigDecimal.ZERO;
			String nonInterUnReg = obj.getNonInterUnReg();
			if (nonInterUnReg != null && !nonInterUnReg.isEmpty()) {
				nonInterRegUnInt = NumberFomatUtil.getBigDecimal(nonInterUnReg);
				nil.setNonInterUnReg(nonInterRegUnInt.setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal nonIntraRegUnInt = BigDecimal.ZERO;
			String nonIntraUnReg = obj.getNonIntraUnReg();
			if (nonIntraUnReg != null && !nonIntraUnReg.isEmpty()) {
				nonIntraRegUnInt = NumberFomatUtil.getBigDecimal(nonIntraUnReg);
				nil.setNonIntraUnReg(nonIntraRegUnInt.setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
			}

			BigDecimal extInterRegInt = BigDecimal.ZERO;
			String extInterReg = obj.getExtInterReg();
			if (extInterReg != null && !extInterReg.isEmpty()) {
				extInterRegInt = NumberFomatUtil.getBigDecimal(extInterReg);
				nil.setExtInterReg(
						extInterRegInt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal extIntraRegInt = BigDecimal.ZERO;
			String extIntraReg = obj.getExtIntraReg();
			if (extIntraReg != null && !extIntraReg.isEmpty()) {
				extIntraRegInt = NumberFomatUtil.getBigDecimal(extIntraReg);
				nil.setExtIntraReg(
						extIntraRegInt.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			BigDecimal extInterRegUnInt = BigDecimal.ZERO;
			String extInterUnReg = obj.getExtInterUnReg();
			if (extInterUnReg != null && !extInterUnReg.isEmpty()) {
				extInterRegUnInt = NumberFomatUtil.getBigDecimal(extInterUnReg);
				nil.setExtInterUnReg(extInterRegUnInt);
			}
			BigDecimal extIntraRegUnInt = BigDecimal.ZERO;
			String extIntraUnReg = obj.getExtIntraUnReg();
			if (extIntraUnReg != null && !extIntraUnReg.isEmpty()) {
				extIntraRegUnInt = NumberFomatUtil.getBigDecimal(extIntraUnReg);
				nil.setExtIntraUnReg(extIntraRegUnInt.setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
			}

			
			if(!GSTConstants.NA.equalsIgnoreCase(obj.getUqc())){
				
				 generateExmptKey = genearateKey(obj.getSgstin(),
							obj.getReturnPeriod(), obj.getHsn(), obj.getDescription(),
							(obj.getUqc().equalsIgnoreCase("NA")) ? "OTH"
									: obj.getUqc());
				}else{
				 generateExmptKey = genearateKey(obj.getSgstin(),
							obj.getReturnPeriod(), obj.getHsn(), obj.getDescription(),
							(obj.getUqc().equalsIgnoreCase("NA")) ? "NA"
									: obj.getUqc());

				}
			
			nil.setDelete(obj.isDelete());
			nil.setInfo(obj.isInfo());
			nil.setNKey(generateExmptKey);
			if (fileStatus != null) {
				nil.setCreatedBy(fileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			nil.setCreatedOn(convertNow);
			listNil.add(nil);

		}
		return listNil;
	}

	public List<Gstr1ANilNonExmptSummaryEntity> convertSRFileToNNEToSummary(
			List<Gstr1ANilDetailsEntity> busProcessRecords,
			Gstr1FileStatusEntity fileStatus) {
		String generateExmptKey = null;


		List<Gstr1ANilNonExmptSummaryEntity> listNil = new ArrayList<>();
		for (Gstr1ANilDetailsEntity obj : busProcessRecords) {
			Gstr1ANilNonExmptSummaryEntity nil = new Gstr1ANilNonExmptSummaryEntity();
			Gstr1ANilNonExmptSummaryEntity non = new Gstr1ANilNonExmptSummaryEntity();
			Gstr1ANilNonExmptSummaryEntity expt = new Gstr1ANilNonExmptSummaryEntity();

			Gstr1ANilNonExmptSummaryEntity nil1 = new Gstr1ANilNonExmptSummaryEntity();
			Gstr1ANilNonExmptSummaryEntity non1 = new Gstr1ANilNonExmptSummaryEntity();
			Gstr1ANilNonExmptSummaryEntity expt1 = new Gstr1ANilNonExmptSummaryEntity();

			Gstr1ANilNonExmptSummaryEntity nil2 = new Gstr1ANilNonExmptSummaryEntity();
			Gstr1ANilNonExmptSummaryEntity non2 = new Gstr1ANilNonExmptSummaryEntity();
			Gstr1ANilNonExmptSummaryEntity expt2 = new Gstr1ANilNonExmptSummaryEntity();

			Gstr1ANilNonExmptSummaryEntity nil3 = new Gstr1ANilNonExmptSummaryEntity();
			Gstr1ANilNonExmptSummaryEntity non3 = new Gstr1ANilNonExmptSummaryEntity();
			Gstr1ANilNonExmptSummaryEntity expt3 = new Gstr1ANilNonExmptSummaryEntity();

			nil.setSgstin(obj.getSgstin());
			non.setSgstin(obj.getSgstin());
			expt.setSgstin(obj.getSgstin());
			nil.setReturnPeriod(obj.getReturnPeriod());
			non.setReturnPeriod(obj.getReturnPeriod());
			expt.setReturnPeriod(obj.getReturnPeriod());
			nil.setHsn(obj.getHsn());
			non.setHsn(obj.getHsn());
			expt.setHsn(obj.getHsn());
			nil.setDescription(obj.getDescription());
			non.setDescription(obj.getDescription());
			expt.setDescription(obj.getDescription());
			
			if(obj.getUqc()!=null){
			
			nil.setUqc(obj.getUqc().toUpperCase());
			non.setUqc(obj.getUqc().toUpperCase());
			expt.setUqc(obj.getUqc().toUpperCase());
			}
			nil.setQnt(obj.getQnt());
			non.setQnt(obj.getQnt());
			expt.setQnt(obj.getQnt());

			if (fileStatus != null) {
				nil.setFileId(fileStatus.getId());
				non.setFileId(fileStatus.getId());
				expt.setFileId(fileStatus.getId());
				nil1.setFileId(fileStatus.getId());
				non1.setFileId(fileStatus.getId());
				expt1.setFileId(fileStatus.getId());
				nil2.setFileId(fileStatus.getId());
				non2.setFileId(fileStatus.getId());
				expt2.setFileId(fileStatus.getId());
				nil3.setFileId(fileStatus.getId());
				non3.setFileId(fileStatus.getId());
				expt3.setFileId(fileStatus.getId());
			}

			nil1.setSgstin(obj.getSgstin());
			non1.setSgstin(obj.getSgstin());
			expt1.setSgstin(obj.getSgstin());
			nil1.setReturnPeriod(obj.getReturnPeriod());
			non1.setReturnPeriod(obj.getReturnPeriod());
			expt1.setReturnPeriod(obj.getReturnPeriod());
			nil1.setHsn(obj.getHsn());
			non1.setHsn(obj.getHsn());
			expt1.setHsn(obj.getHsn());
			nil1.setDescription(obj.getDescription());
			non1.setDescription(obj.getDescription());
			expt1.setDescription(obj.getDescription());
			
			if(obj.getUqc()!=null){

			nil1.setUqc(obj.getUqc().toUpperCase());
			non1.setUqc(obj.getUqc().toUpperCase());
			expt1.setUqc(obj.getUqc().toUpperCase());
			}
			nil1.setQnt(obj.getQnt());
			non1.setQnt(obj.getQnt());
			expt1.setQnt(obj.getQnt());

			nil2.setSgstin(obj.getSgstin());
			non2.setSgstin(obj.getSgstin());
			expt2.setSgstin(obj.getSgstin());
			nil2.setReturnPeriod(obj.getReturnPeriod());
			non2.setReturnPeriod(obj.getReturnPeriod());
			expt2.setReturnPeriod(obj.getReturnPeriod());

			nil2.setHsn(obj.getHsn());
			non2.setHsn(obj.getHsn());
			expt2.setHsn(obj.getHsn());
			nil2.setDescription(obj.getDescription());
			non2.setDescription(obj.getDescription());
			expt2.setDescription(obj.getDescription());
			
			if(obj.getUqc()!=null){

			nil2.setUqc(obj.getUqc().toUpperCase());
			non2.setUqc(obj.getUqc().toUpperCase());
			expt2.setUqc(obj.getUqc().toUpperCase());
			}
			nil2.setQnt(obj.getQnt());
			non2.setQnt(obj.getQnt());
			expt2.setQnt(obj.getQnt());

			nil3.setSgstin(obj.getSgstin());
			non3.setSgstin(obj.getSgstin());
			expt3.setSgstin(obj.getSgstin());
			nil3.setReturnPeriod(obj.getReturnPeriod());
			non3.setReturnPeriod(obj.getReturnPeriod());
			expt3.setReturnPeriod(obj.getReturnPeriod());

			nil3.setHsn(obj.getHsn());
			non3.setHsn(obj.getHsn());
			expt3.setHsn(obj.getHsn());
			nil3.setDescription(obj.getDescription());
			non3.setDescription(obj.getDescription());
			expt3.setDescription(obj.getDescription());
			
			if(obj.getUqc()!=null){

			nil3.setUqc(obj.getUqc().toUpperCase());
			non3.setUqc(obj.getUqc().toUpperCase());
			expt3.setUqc(obj.getUqc().toUpperCase());
			}

			nil3.setQnt(obj.getQnt());
			non3.setQnt(obj.getQnt());
			expt3.setQnt(obj.getQnt());
			if (obj.getReturnPeriod() != null
					&& obj.getReturnPeriod().matches("[0-9]+")) {
				if (obj.getReturnPeriod().length() == 6) {
					int months = Integer
							.valueOf(obj.getReturnPeriod().substring(0, 2));
					int year = Integer
							.valueOf(obj.getReturnPeriod().substring(2));
					if ((months <= 12 && months >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(obj.getReturnPeriod());
						nil.setDerivedRetPeriod(derivedRetPeriod);
						non.setDerivedRetPeriod(derivedRetPeriod);
						expt.setDerivedRetPeriod(derivedRetPeriod);

						nil1.setDerivedRetPeriod(derivedRetPeriod);
						non1.setDerivedRetPeriod(derivedRetPeriod);
						expt1.setDerivedRetPeriod(derivedRetPeriod);

						nil2.setDerivedRetPeriod(derivedRetPeriod);
						non2.setDerivedRetPeriod(derivedRetPeriod);
						expt2.setDerivedRetPeriod(derivedRetPeriod);

						nil3.setDerivedRetPeriod(derivedRetPeriod);
						non3.setDerivedRetPeriod(derivedRetPeriod);
						expt3.setDerivedRetPeriod(derivedRetPeriod);
					}
				}
			} else {
				nil.setDerivedRetPeriod(9999);
				non.setDerivedRetPeriod(9999);
				expt.setDerivedRetPeriod(9999);

				nil1.setDerivedRetPeriod(9999);
				non1.setDerivedRetPeriod(9999);
				expt1.setDerivedRetPeriod(9999);

				nil2.setDerivedRetPeriod(9999);
				non2.setDerivedRetPeriod(9999);
				expt2.setDerivedRetPeriod(9999);

				nil3.setDerivedRetPeriod(9999);
				non3.setDerivedRetPeriod(9999);
				expt3.setDerivedRetPeriod(9999);
			}
			nil.setAsProcessedId(obj.getId());
			non.setAsProcessedId(obj.getId());
			expt.setAsProcessedId(obj.getId());

			nil1.setAsProcessedId(obj.getId());
			non1.setAsProcessedId(obj.getId());
			expt1.setAsProcessedId(obj.getId());

			nil2.setAsProcessedId(obj.getId());
			non2.setAsProcessedId(obj.getId());
			expt2.setAsProcessedId(obj.getId());

			nil3.setAsProcessedId(obj.getId());
			non3.setAsProcessedId(obj.getId());
			expt3.setAsProcessedId(obj.getId());

			
			if(!GSTConstants.NA.equalsIgnoreCase(obj.getUqc())){
				
				 generateExmptKey = genearateKey(obj.getSgstin(),
							obj.getReturnPeriod(), obj.getHsn(), obj.getDescription(),
							(obj.getUqc().equalsIgnoreCase("NA")) ? "OTH"
									: obj.getUqc().toUpperCase());
				}else{
				 generateExmptKey = genearateKey(obj.getSgstin(),
							obj.getReturnPeriod(), obj.getHsn(), obj.getDescription(),
							(obj.getUqc().equalsIgnoreCase("NA")) ? "NA"
									: obj.getUqc().toUpperCase());

				}

			nil.setDelete(obj.isDelete());
			nil.setInfo(obj.isInfo());
			nil.setNKey(generateExmptKey);
			non.setDelete(obj.isDelete());
			non.setInfo(obj.isInfo());
			non.setNKey(generateExmptKey);
			expt.setDelete(obj.isDelete());
			expt.setInfo(obj.isInfo());
			expt.setNKey(generateExmptKey);

			nil1.setDelete(obj.isDelete());
			nil1.setInfo(obj.isInfo());
			nil1.setNKey(generateExmptKey);
			non1.setDelete(obj.isDelete());
			non1.setInfo(obj.isInfo());
			non1.setNKey(generateExmptKey);
			expt1.setDelete(obj.isDelete());
			expt1.setInfo(obj.isInfo());
			expt1.setNKey(generateExmptKey);

			nil2.setDelete(obj.isDelete());
			nil2.setInfo(obj.isInfo());
			nil2.setNKey(generateExmptKey);
			non2.setDelete(obj.isDelete());
			non2.setInfo(obj.isInfo());
			non2.setNKey(generateExmptKey);
			expt2.setDelete(obj.isDelete());
			expt2.setInfo(obj.isInfo());
			expt2.setNKey(generateExmptKey);

			nil3.setDelete(obj.isDelete());
			nil3.setInfo(obj.isInfo());
			nil3.setNKey(generateExmptKey);
			non3.setDelete(obj.isDelete());
			non3.setInfo(obj.isInfo());
			non3.setNKey(generateExmptKey);
			expt3.setDelete(obj.isDelete());
			expt3.setInfo(obj.isInfo());
			expt3.setNKey(generateExmptKey);
			if (fileStatus != null) {
				nil.setCreatedBy(fileStatus.getUpdatedBy());
				non.setCreatedBy(fileStatus.getUpdatedBy());
				expt.setCreatedBy(fileStatus.getUpdatedBy());

				nil1.setCreatedBy(fileStatus.getUpdatedBy());
				non1.setCreatedBy(fileStatus.getUpdatedBy());
				expt1.setCreatedBy(fileStatus.getUpdatedBy());

				nil2.setCreatedBy(fileStatus.getUpdatedBy());
				non2.setCreatedBy(fileStatus.getUpdatedBy());
				expt2.setCreatedBy(fileStatus.getUpdatedBy());

				nil3.setCreatedBy(fileStatus.getUpdatedBy());
				non3.setCreatedBy(fileStatus.getUpdatedBy());
				expt3.setCreatedBy(fileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			nil.setCreatedOn(convertNow);
			non.setCreatedOn(convertNow);
			expt.setCreatedOn(convertNow);

			nil.setCreatedOn(convertNow);
			non.setCreatedOn(convertNow);
			expt.setCreatedOn(convertNow);

			nil.setCreatedOn(convertNow);
			non.setCreatedOn(convertNow);
			expt.setCreatedOn(convertNow);

			nil.setCreatedOn(convertNow);
			non.setCreatedOn(convertNow);
			expt.setCreatedOn(convertNow);

			nil.setSupplType(GSTConstants.NIL_SUPPLY_TYPE);
			nil.setTableSection(GSTConstants.GSTR1_8A);

			nil1.setSupplType(GSTConstants.NIL_SUPPLY_TYPE);
			nil1.setTableSection(GSTConstants.GSTR1_8B);

			nil2.setSupplType(GSTConstants.NIL_SUPPLY_TYPE);
			nil2.setTableSection(GSTConstants.GSTR1_8C);

			nil3.setSupplType(GSTConstants.NIL_SUPPLY_TYPE);
			nil3.setTableSection(GSTConstants.GSTR1_8D);

			non.setSupplType(GSTConstants.NON_SUPPLY_TYPE);
			non.setTableSection(GSTConstants.GSTR1_8A);

			non1.setSupplType(GSTConstants.NON_SUPPLY_TYPE);
			non1.setTableSection(GSTConstants.GSTR1_8B);

			non2.setSupplType(GSTConstants.NON_SUPPLY_TYPE);
			non2.setTableSection(GSTConstants.GSTR1_8C);

			non3.setSupplType(GSTConstants.NON_SUPPLY_TYPE);
			non3.setTableSection(GSTConstants.GSTR1_8D);

			expt.setSupplType(GSTConstants.EXMPT_SUPPLY_TYPE);
			expt.setTableSection(GSTConstants.GSTR1_8A);

			expt1.setSupplType(GSTConstants.EXMPT_SUPPLY_TYPE);
			expt1.setTableSection(GSTConstants.GSTR1_8B);

			expt2.setSupplType(GSTConstants.EXMPT_SUPPLY_TYPE);
			expt2.setTableSection(GSTConstants.GSTR1_8C);

			expt3.setSupplType(GSTConstants.EXMPT_SUPPLY_TYPE);
			expt3.setTableSection(GSTConstants.GSTR1_8D);

			nil.setTaxableValue(obj.getNilInterReg());
			nil1.setTaxableValue(obj.getNilIntraReg());
			nil2.setTaxableValue(obj.getNilInterUnReg());
			nil3.setTaxableValue(obj.getNilIntraUnReg());

			non.setTaxableValue(obj.getNonInterReg());
			non1.setTaxableValue(obj.getNonIntraReg());
			non2.setTaxableValue(obj.getNonInterUnReg());
			non3.setTaxableValue(obj.getNonIntraUnReg());

			expt.setTaxableValue(obj.getExtInterReg());
			expt1.setTaxableValue(obj.getExtIntraReg());
			expt2.setTaxableValue(obj.getExtInterUnReg());
			expt3.setTaxableValue(obj.getExtIntraUnReg());

			listNil.add(nil);
			listNil.add(nil1);
			listNil.add(nil2);
			listNil.add(nil3);

			listNil.add(non);
			listNil.add(non1);
			listNil.add(non2);
			listNil.add(non3);

			listNil.add(expt);
			listNil.add(expt1);
			listNil.add(expt2);
			listNil.add(expt3);
		}
		return listNil;
	}

	public List<Gstr1ANilNonExemptedAsEnteredEntity> convertSRFileToNilExcelDocOld(
			List<Object[]> listOfNil, Gstr1FileStatusEntity fileStatus) {

		List<Gstr1ANilNonExemptedAsEnteredEntity> listNil = new ArrayList<>();

		for (Object[] obj : listOfNil) {
			Gstr1ANilNonExemptedAsEnteredEntity nil = new Gstr1ANilNonExemptedAsEnteredEntity();
			String sgstin = getValue(obj[0]);
			String returnPeriod = getValue(obj[1]);
			String nilInterReg = getValue(obj[2]);
			String nilIntraReg = getValue(obj[3]);
			String nilInterUReg = getValue(obj[4]);
			String nilIntraUReg = getValue(obj[5]);
			String extInterReg = getValue(obj[6]);
			String extIntraReg = getValue(obj[7]);
			String extInterUReg = getValue(obj[8]);
			String extIntraUReg = getValue(obj[9]);
			String nonInterReg = getValue(obj[10]);
			String nonIntraReg = getValue(obj[11]);
			String nonInterUReg = getValue(obj[12]);
			String nonIntraUReg = getValue(obj[13]);
			nil.setSgstin(sgstin);
			if (fileStatus != null) {
				nil.setFileId(fileStatus.getId());
			}
			nil.setReturnPeriod(returnPeriod);
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int months = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((months <= 12 && months >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						nil.setDerivedRetPeriod(derivedRetPeriod);
					}
				}
			} else {
				nil.setDerivedRetPeriod(9999);
			}
			nil.setNilInterReg(nilInterReg);
			nil.setNilIntraReg(nilIntraReg);
			nil.setNilInterUnReg(nilInterUReg);
			nil.setNilIntraUnReg(nilIntraUReg);
			nil.setNonInterReg(nonInterReg);
			nil.setNonIntraReg(nonIntraReg);
			nil.setNonInterUnReg(nonInterUReg);
			nil.setNonIntraUnReg(nonIntraUReg);
			nil.setExtInterReg(extInterReg);
			nil.setExtIntraReg(extIntraReg);
			nil.setExtInterUnReg(extInterUReg);
			nil.setExtIntraUnReg(extIntraUReg);
			String generateExmptKey = genearateKeyOld(sgstin, returnPeriod);
			nil.setNKey(generateExmptKey);
			if (fileStatus != null) {
				nil.setCreatedBy(fileStatus.getUpdatedBy());
				if (fileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
					nil.setDataOriginType("E");
				} else if (fileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.ERP)) {
					nil.setDataOriginType("A");
				}

			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			nil.setCreatedOn(convertNow);
			listNil.add(nil);

		}
		return listNil;
	}

	private String genearateKeyOld(String sgstin, String returnPeriod) {
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(returnPeriod)
				.toString();
	}
}
