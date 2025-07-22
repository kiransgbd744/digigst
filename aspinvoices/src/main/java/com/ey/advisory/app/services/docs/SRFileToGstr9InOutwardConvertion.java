package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.master.NatureOfSupEntity;
import com.ey.advisory.app.caches.NatureOfSupCache;
import com.ey.advisory.app.data.entities.client.Gstr9OutwardInwardAsEnteredEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9UserInputEntity;
import com.ey.advisory.app.data.services.gstr9.Gstr9InwardUtil;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("SRFileToGstr9InOutwardConvertion")
public class SRFileToGstr9InOutwardConvertion {

	@Autowired
	@Qualifier("WebUploadKeyGenerator")
	private WebUploadKeyGenerator webUploadKeyGenerator;

	@Autowired
	@Qualifier("DefaultNatureOfSuppliesCache")
	private NatureOfSupCache natureOfSupCache;

	@Autowired
	private Gstr9InwardUtil gstr9InwardUtil;

	private static final List<String> TAX_VALUE = ImmutableList.of("4A", "4B",
			"4C", "4D", "4E", "4F", "4G", "4G1", "4I", "4J", "4K", "4L", "5A", "5B",
			"5C", "5C1", "5D", "5E", "5F", "5H", "5I", "5J", "5K", "10", "11", "16A",
			"16B", "16C");
	private static final List<String> IGST = ImmutableList.of("4A", "4B", "4C",
			"4D", "4E", "4F", "4G", "4G1", "4I", "4J", "4K", "4L", "6B1", "6B2", "6B3",
			"6C1", "6C2", "6C3", "6D1", "6D2", "6D3", "6E1", "6E2", "6F", "6G",
			"6H", "6M", "7A", "7B", "7C", "7D", "7E", "8C", "8E", "8F", "8G",
			"9", "10", "11", "12", "13", "14(1)", "14(2)", "15A", "15B", "15C",
			"15D", "15E", "15F", "15G", "16B", "16C");
	private static final List<String> CGST_SGST = ImmutableList.of("4A", "4B",
			"4E", "4F", "4G", "4G1", "4I", "4J", "4K", "4L", "6B1", "6B2", "6B3",
			"6C1", "6C2", "6C3", "6D1", "6D2", "6D3", "6G", "6H", "6K", "6L",
			"6M", "7A", "7B", "7C", "7D", "7E", "7F", "7G", "8C", "8E", "8F",
			"8G", "9", "10", "11", "12", "13", "14(1)", "14(2)", "15A", "15B",
			"15C", "15D", "15E", "15F", "15G", "16B", "16C");
	private static final List<String> CESS = ImmutableList.of("4A", "4B", "4C",
			"4D", "4E", "4F", "4G", "4G1", "4I", "4J", "4K", "4L", "6B1", "6B2", "6B3",
			"6C1", "6C2", "6C3", "6D1", "6D2", "6D3", "6E1", "6E2", "6F", "6G",
			"6H", "6M", "7A", "7B", "7C", "7D", "7E", "8C", "8E", "8F", "8G",
			"9", "10", "11", "12", "13", "14(1)", "14(2)", "15A", "15B", "15C",
			"15D", "15E", "15F", "15G", "16B", "16C");
	private static final List<String> INTEREST = ImmutableList.of("9", "14(1)",
			"14(2)", "15E", "15F", "15G");
	private static final List<String> LATE_FEE_PENALTY = ImmutableList.of("9",
			"15E", "15F", "15G");
	private static final List<String> OTHER = ImmutableList.of("9");

	private String getValue(Object obj) {
		return obj != null && !obj.toString().trim().isEmpty()
				? String.valueOf(obj).trim() : null;
	}

	public List<Gstr9OutwardInwardAsEnteredEntity> convertSRFileToGstr9InOutwardExcel(
			List<Object[]> listOfTdsTcs, Gstr1FileStatusEntity fileStatus) {

		List<Gstr9OutwardInwardAsEnteredEntity> listinOutward = new ArrayList<>();

		for (Object[] obj : listOfTdsTcs) {
			Gstr9OutwardInwardAsEnteredEntity inOutward = new Gstr9OutwardInwardAsEnteredEntity();

			String gstin = getValue(obj[0]);
			gstin = gstin != null ? gstin.toUpperCase() : null;
			String fy = getValue(obj[1]);
			LocalDate date = DateFormatForStructuralValidatons
					.parseObjToDate(fy);
			if (date != null) {
				fy = date.toString();
			}

			String tableNo = getValue(obj[2]);
			tableNo = tableNo != null ? tableNo.toUpperCase() : null;
			String natureOfSup = getValue(obj[3]);
			String taxbleVal = getValue(obj[4]);
			String igst = getValue(obj[5]);
			String cgst = getValue(obj[6]);
			String sgst = getValue(obj[7]);
			String cess = getValue(obj[8]);
			String interest = getValue(obj[9]);
			String lateFee = getValue(obj[10]);
			String penalty = getValue(obj[11]);
			String other = getValue(obj[12]);
			LocalDate otherDate = DateUtil.parseObjToDate(other);

			String docKey = CommonUtility.generateGstr9DocKey(gstin, fy,
					tableNo);

			if (fileStatus != null) {
				inOutward.setFileId(fileStatus.getId());
			}
			inOutward.setGstin(gstin);
			inOutward.setFy(fy);
			inOutward.setTableNumber(tableNo);
			inOutward.setNatureOfSupp(natureOfSup);
			NatureOfSupEntity findNatureOfSupp = null;
			if (tableNo != null && !tableNo.isEmpty()) {
				natureOfSupCache = StaticContextHolder.getBean(
						"DefaultNatureOfSuppliesCache", NatureOfSupCache.class);
				findNatureOfSupp = natureOfSupCache
						.findNatureOfSupp(tableNo.trim().toUpperCase());
			}
			if (findNatureOfSupp == null) {
				inOutward.setTaxableVal(taxbleVal);
				inOutward.setIgst(igst);
				inOutward.setCgst(cgst);
				inOutward.setSgst(sgst);
				inOutward.setCess(cess);
				inOutward.setInterest(interest);
				inOutward.setLateFee(lateFee);
				inOutward.setPenalty(penalty);
				if (otherDate != null) {
					inOutward.setOther(otherDate.toString());
				} else {
					inOutward.setOther(other);
				}
			} else {

				if (TAX_VALUE.contains(tableNo) && taxbleVal != null
						&& !taxbleVal.isEmpty()) {
					inOutward.setTaxableVal(taxbleVal);
				}
				if (IGST.contains(tableNo) && igst != null && !igst.isEmpty()) {
					inOutward.setIgst(igst);
				}
				if (CGST_SGST.contains(tableNo) && cgst != null
						&& !cgst.isEmpty()) {
					inOutward.setCgst(cgst);
				}
				if (CGST_SGST.contains(tableNo) && sgst != null
						&& !sgst.isEmpty()) {
					inOutward.setSgst(sgst);
				}
				if (CESS.contains(tableNo) && cess != null && !cess.isEmpty()) {
					inOutward.setCess(cess);
				}
				if (INTEREST.contains(tableNo) && interest != null
						&& !interest.isEmpty()) {
					inOutward.setInterest(interest);
				}
				if (LATE_FEE_PENALTY.contains(tableNo) && lateFee != null
						&& !lateFee.isEmpty()) {
					inOutward.setLateFee(lateFee);
				}
				if (LATE_FEE_PENALTY.contains(tableNo) && penalty != null
						&& !penalty.isEmpty()) {
					inOutward.setPenalty(penalty);
				}
				if (other != null && !other.isEmpty()
						&& OTHER.contains(tableNo)) {
					if (otherDate != null) {
						inOutward.setOther(otherDate.toString());
					} else {
						inOutward.setOther(other);
					}
				}
			}
			inOutward.setGst9DocKey(docKey);
			if (fileStatus != null) {
				inOutward.setCreatedBy(fileStatus.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			inOutward.setCreatedOn(convertNow);
			listinOutward.add(inOutward);
		}
		return listinOutward;
	}

	public List<Gstr9UserInputEntity> convertSRFileToGstr9InOut(
			List<Gstr9OutwardInwardAsEnteredEntity> busProcessRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr9UserInputEntity> listInOutGstr9 = new ArrayList<>();

		Gstr9UserInputEntity inOutGstr9 = null;
		for (Gstr9OutwardInwardAsEnteredEntity arr : busProcessRecords) {
			inOutGstr9 = new Gstr9UserInputEntity();
			inOutGstr9.setGstin(arr.getGstin());
			String fy = arr.getFy();
			inOutGstr9.setFy(fy);
			String retPeriod = "03" + fy.trim().substring(5);
			inOutGstr9.setRetPeriod(retPeriod);

			Integer deriRetPeriod = GenUtil.convertTaxPeriodToInt(retPeriod);
			inOutGstr9.setDerivedRetPeriod(deriRetPeriod);
			String subSection = arr.getTableNumber();
			inOutGstr9.setSubSection(subSection);
			if (subSection != null) {
				natureOfSupCache = StaticContextHolder.getBean(
						"DefaultNatureOfSuppliesCache", NatureOfSupCache.class);
				NatureOfSupEntity findNatureOfSupp = natureOfSupCache
						.findNatureOfSupp(subSection.trim().toUpperCase());
				inOutGstr9.setSection(findNatureOfSupp.getSection());
			}
			inOutGstr9.setNatureOfSupplies(arr.getNatureOfSupp());

			BigDecimal tax = BigDecimal.ZERO;
			if (TAX_VALUE.contains(subSection.toUpperCase())
					&& arr.getTaxableVal() != null
					&& !arr.getTaxableVal().isEmpty()) {
				tax = NumberFomatUtil.getBigDecimal(arr.getTaxableVal());
				if ("9".equalsIgnoreCase(subSection)) {
					inOutGstr9.setTxPyble(
							tax.setScale(2, BigDecimal.ROUND_HALF_UP));
				} else {
					inOutGstr9.setTxVal(
							tax.setScale(2, BigDecimal.ROUND_HALF_UP));
				}
			} else {
				inOutGstr9.setTxVal(tax);
			}
			BigDecimal igst = BigDecimal.ZERO;

			if (IGST.contains(subSection.toUpperCase()) && arr.getIgst() != null
					&& !arr.getIgst().isEmpty()) {
				igst = NumberFomatUtil.getBigDecimal(arr.getIgst());
				inOutGstr9.setIgst(igst.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				inOutGstr9.setIgst(igst);
			}

			BigDecimal cgst = BigDecimal.ZERO;
			if (CGST_SGST.contains(subSection.toUpperCase())
					&& arr.getCgst() != null && !arr.getCgst().isEmpty()) {
				cgst = NumberFomatUtil.getBigDecimal(arr.getCgst());

				inOutGstr9.setCgst(cgst.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				inOutGstr9.setCgst(cgst);
			}
			BigDecimal sgst = BigDecimal.ZERO;
			if (CGST_SGST.contains(subSection.toUpperCase())
					&& arr.getSgst() != null && !arr.getSgst().isEmpty()) {
				sgst = NumberFomatUtil.getBigDecimal(arr.getSgst());
				inOutGstr9.setSgst(sgst.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				inOutGstr9.setSgst(sgst);
			}
			BigDecimal cess = BigDecimal.ZERO;
			if (CESS.contains(subSection.toUpperCase()) && arr.getCess() != null
					&& !arr.getCess().isEmpty()) {
				cess = NumberFomatUtil.getBigDecimal(arr.getCess());
				inOutGstr9.setCess(cess.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				inOutGstr9.setCess(cess);
			}
			BigDecimal interest = BigDecimal.ZERO;

			if (INTEREST.contains(subSection.toUpperCase())
					&& arr.getInterest() != null
					&& !arr.getInterest().isEmpty()) {
				interest = NumberFomatUtil.getBigDecimal(arr.getInterest());
				inOutGstr9.setIntr(
						interest.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				inOutGstr9.setIntr(interest);
			}

			BigDecimal lateFee = BigDecimal.ZERO;
			if (LATE_FEE_PENALTY.contains(subSection.toUpperCase())
					&& arr.getLateFee() != null
					&& !arr.getLateFee().isEmpty()) {
				lateFee = NumberFomatUtil.getBigDecimal(arr.getLateFee());
				inOutGstr9
						.setFee(lateFee.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				inOutGstr9.setFee(lateFee);
			}
			BigDecimal penalty = BigDecimal.ZERO;
			if (LATE_FEE_PENALTY.contains(subSection.toUpperCase())
					&& arr.getPenalty() != null
					&& !arr.getPenalty().isEmpty()) {
				penalty = NumberFomatUtil.getBigDecimal(arr.getPenalty());
				inOutGstr9
						.setPen(penalty.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				inOutGstr9.setPen(penalty);
			}

			BigDecimal other = BigDecimal.ZERO;

			if (arr.getOther() != null && !arr.getOther().isEmpty()
					&& OTHER.contains(subSection.toUpperCase())) {
				other = NumberFomatUtil.getBigDecimal(arr.getOther());
				inOutGstr9.setOth(other.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				inOutGstr9.setOth(other);
			}
			if (updateFileStatus != null) {
				inOutGstr9.setFileId(updateFileStatus.getId());

				if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
					inOutGstr9.setSource("E");
					inOutGstr9.setActive(true);
				} else if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.ERP)) {
					inOutGstr9.setSource("A");
				} else if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
					inOutGstr9.setSource(GSTConstants.DataOriginTypeCodes.SFTP
							.getDataOriginTypeCode());
				}
			}

			Map<String, String> itcTypeMap = gstr9InwardUtil.getItcTypeMap();
			String itcType = itcTypeMap.get(subSection);
			inOutGstr9.setItcTyp(itcType);
			inOutGstr9.setAsEnterTableId(arr.getId());
			inOutGstr9.setInfo(arr.isInfo());
			inOutGstr9.setDocKey(arr.getGst9DocKey());
			inOutGstr9.setCreatedBy(arr.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			inOutGstr9.setCreatedOn(convertNow);
			listInOutGstr9.add(inOutGstr9);
		}
		return listInOutGstr9;
	}
}
