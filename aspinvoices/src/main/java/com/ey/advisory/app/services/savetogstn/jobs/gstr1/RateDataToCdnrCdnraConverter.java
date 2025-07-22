package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.CDNRInvoices;
import com.ey.advisory.app.docs.dto.CdnrLineItem;
import com.ey.advisory.app.docs.dto.CdnrLineItemDetail;
import com.ey.advisory.app.docs.dto.CreditDebitNote;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.app.services.common.DocKeyGenerator;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("rateDataToCdnrCdnraConverter")
public class RateDataToCdnrCdnraConverter implements RateDataToGstr1Converter {

	@Autowired
	@Qualifier("DocKeyGenerator")
	private DocKeyGenerator docKeyGenerator;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private GstnApi gstnapi;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	private static final List<String> REGYPE_IMPORTS = ImmutableList
			.of(GSTConstants.SEZD, GSTConstants.SEZU);

	private boolean isSEZSupplier(String groupCode, String supplierGstin) {

		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);
		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				supplierGstin);
		if (gstin != null) {
			if (gstin.getRegistrationType() != null) {
				String regType = gstin.getRegistrationType();
				if (REGYPE_IMPORTS.contains(regType.toUpperCase())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2, int chunkSize) {

		Long id = arr1[16] != null ? (Long) arr1[16] : null;
		Long id2 = arr2[16] != null ? (Long) arr2[16] : null;
		return !id.equals(id2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2, chunkSize);
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkSize) {
		// URP(UnRegisteredParty) == NULL
		String cGstin = arr1[2] != null ? String.valueOf(arr1[2])
				: APIConstants.URP;
		String cGstin2 = arr2[2] != null ? String.valueOf(arr2[2])
				: APIConstants.URP;
		return cGstin != null && !cGstin.equalsIgnoreCase(cGstin2)
				|| isNewBatch(arr1, arr2, idsList, totSize, counter2, chunkSize);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkSize) {

		/*
		 * String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
		 * String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		 * String txPriod2 = arr2[1]!= null ? String.valueOf(arr2[1]) : null;
		 * String sGstin2 = arr2[0]!= null ? String.valueOf(arr2[0]) : null;
		 */
		return /*
				 * (sGstin != null && !sGstin.equals(sGstin2)) || (txPriod !=
				 * null && !txPriod.equals(txPriod2)) ||
				 */ idsList.size() >= chunkSize
				|| counter2 == totSize;
	}

	private SaveGstr1 setBatch(Object[] arr1, String section,
			List<CDNRInvoices> cdnrList) {

		String txPriod = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[0] != null ? String.valueOf(arr1[0]) : null;
		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveGstr1 gstr1 = new SaveGstr1();
		gstr1.setSgstin(sGstin);
		gstr1.setTaxperiod(txPriod);
		// gstr1.setGt(new BigDecimal("3782969.01")); // static
		// gstr1.setCur_gt(new BigDecimal("3782969.01")); // static
		if (section.equals(APIConstants.CDNR)) {
			gstr1.setCdnrInvoice(cdnrList);
		} else if (section.equals(APIConstants.CDNRA)) {
			gstr1.setCdnraInvoice(cdnrList);
		}
		return gstr1;
	}

	private CDNRInvoices setInv(Object[] arr1,
			List<CreditDebitNote> creditdebitList) {
		String cGstin = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		if (cGstin != null && cGstin.equalsIgnoreCase(APIConstants.URP)) {
			cGstin = null;
		}
		CDNRInvoices cdnr = new CDNRInvoices();
		cdnr.setCpGstin(cGstin);
		cdnr.setCreditDebitNoteDetails(creditdebitList);
		return cdnr;
	}

	private CreditDebitNote setInvData(Object[] arr1, String section,
			List<CdnrLineItem> itmsList, String groupCode, String taxDocType, int chunkSize) {

		String shipDate = null;
		if (arr1[26] != null && String.valueOf(arr1[26]).trim().length() > 0) {
			shipDate = ((LocalDate) arr1[26])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String shipNum = arr1[25] != null ? String.valueOf(arr1[25]) : null;

		// String txDocType = (String) arr1[24];
		String docType = arr1[23] != null ? String.valueOf(arr1[23]) : null;
		String preGst = arr1[22] != null ? String.valueOf(arr1[22]) : null;
		String oInvDate = null;
		if (arr1[21] != null && arr1[21].toString().trim().length() > 0) {
			oInvDate = ((LocalDate) arr1[21])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String oInvNum = arr1[20] != null ? String.valueOf(arr1[20]) : null;

		String invDate = null;
		if (arr1[19] != null && arr1[19].toString().trim().length() > 0) {
			invDate = ((LocalDate) arr1[19])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		// BigDecimal taxPayable = (BigDecimal) arr1[18];
		// String supplyType = String.valueOf(arr1[17]);
		Long id = arr1[16] != null ? (Long) arr1[16] : null;
		// int lineNo = (Integer) arr1[9];
		String diffPercent = arr1[8] != null ? String.valueOf(arr1[8]) : null;
		// String eTin = String.valueOf(arr1[7]);
		// String revCharge = String.valueOf(arr1[6]);
		// String pos = String.valueOf(arr1[5]);
		BigDecimal invVal = arr1[4] != null
				? new BigDecimal(String.valueOf(arr1[4])) : BigDecimal.ZERO;
		String invNum = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		// String cGstin = String.valueOf(arr1[2]);
		// String taxPeriod = String.valueOf(arr1[1]);
		// String sGstin = String.valueOf(arr1[0]);

		CreditDebitNote credeb = new CreditDebitNote();
		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			credeb.setInvoiceStatus(APIConstants.D);// D-Delete, A-Accept,
													// R-Reject
		}
		// credeb.setCheckSum(""); //Chksum is mandatory only when flag is A/R .
		if (GSTConstants.CR.concat(",").concat(GSTConstants.RCR)
				.contains(docType)) {
			credeb.setCredDebRefVoucher(APIConstants.C);
		} else if (GSTConstants.DR.concat(",").concat(GSTConstants.RDR)
				.contains(docType)) {
			credeb.setCredDebRefVoucher(APIConstants.D);
		} else if (GSTConstants.RFV.concat(",").concat(GSTConstants.RRFV)
				.contains(docType)) {
			credeb.setCredDebRefVoucher(APIConstants.R);
		}
		credeb.setCredDebRefVoucherNum(invNum);
		credeb.setCredDebRefVoucherDate(invDate);
		/*
		 * if(preGst) { credeb.setPreGstRegNote(APIConstants.Y); } else {
		 * credeb.setPreGstRegNote(APIConstants.N); }
		 */

		credeb.setTotalNoteVal(invVal);
		if (GSTConstants.L65.equalsIgnoreCase(diffPercent)) {
			credeb.setDiffPercent(new BigDecimal("0.65"));
		} else if (GSTConstants.L.equalsIgnoreCase(diffPercent)) {
			// needs to clarify
		} else if (GSTConstants.N.equalsIgnoreCase(diffPercent)) {
			// needs to clarify
		}

		if (gstnapi.isDelinkingEligible(APIConstants.GSTR1.toUpperCase())) {

			String revCharge = arr1[6] != null ? String.valueOf(arr1[6]) : null;
			if (revCharge != null && "L".equalsIgnoreCase(revCharge)) {
				revCharge = APIConstants.N;
			}
			String pos = arr1[5] != null
					? (String.valueOf(arr1[5]).trim().length() == 1
							? "0".concat(String.valueOf(arr1[5]).trim())
							: String.valueOf(arr1[5]))
					: null;
			String supplyType = arr1[17] != null ? String.valueOf(arr1[17])
					: null;
			String sec7 = arr1[9] != null ? (String.valueOf(arr1[9]).isEmpty()
					? APIConstants.N : String.valueOf(arr1[9]))
					: APIConstants.N;

			credeb.setInvoiceType(
					decideInvType(docType, supplyType, sec7, section));
			credeb.setPos(pos);
			credeb.setReverseCharge(revCharge);

			if (APIConstants.CDNRA.equalsIgnoreCase(section)) {

				credeb.setOriCredDebNum(oInvNum);
				credeb.setOriCredDebDate(oInvDate);
			}
		} else {

			credeb.setPreGstRegNote(preGst);
			if (APIConstants.CDNR.equalsIgnoreCase(section)) {

				credeb.setInvNum(oInvNum); // For CDNR
				credeb.setInvDate(oInvDate); // For CDNR

			} else if (APIConstants.CDNRA.equalsIgnoreCase(section)) {

				credeb.setOriCredDebNum(oInvNum);
				credeb.setOriCredDebDate(oInvDate);

				if (GSTConstants.RCR.concat(",").concat(GSTConstants.RDR)
						.contains(docType)) {
					if (shipNum == null || shipDate == null
							|| shipNum.trim().isEmpty()
							|| shipDate.trim().isEmpty()) {

						String sGstin = arr1[0] != null
								? String.valueOf(arr1[0]) : null;
						LocalDate oInvDateAsDate = (LocalDate) arr1[21];
						String finYear = GenUtil.getFinYear(oInvDateAsDate);
						String oInvKey = docKeyGenerator.generateKey(sGstin,
								invNum, finYear, docType);
						TenantContext.setTenantId(groupCode);
						Object[] originDoc = docRepository
								.fetchOriginDocForRCrRDr(oInvKey);
						if (originDoc != null) {
							shipNum = originDoc[0] != null
									? String.valueOf(originDoc[0]) : null;
							if (originDoc[1] != null
									&& String.valueOf(originDoc[1]).trim()
											.length() > 0) {
								shipDate = ((LocalDate) originDoc[1]).format(
										DateUtil.SUPPORTED_DATE_FORMAT2);
							}
						}
					}
				}

				credeb.setInvNum(shipNum); // For CDNRA
				credeb.setInvDate(shipDate); // For CDNRA

				/*
				 * if (invDate != null) { String invKey =
				 * docKeyGenerator.generateKey(sGstin, invNum, (LocalDate)
				 * arr1[21], docType); TenantContext.setTenantId(groupCode);
				 * List<String> originDocNum = docRepository
				 * .fetchOriginDocNoForCrDr(invKey); if (originDocNum != null &&
				 * !originDocNum.isEmpty()) {
				 * credeb.setInvNum(originDocNum.get(0)); // for CDNRA } else {
				 * credeb.setInvNum(null); // for CDNRA } }
				 */
			}

		}

		credeb.setDocId(id);
		credeb.setCdnrLineItem(itmsList);
		return credeb;
	}

	private CdnrLineItem setItemDetail(Object[] arr1, int counter2,
			boolean isSEZSupplier) {

		BigDecimal cessAmt = arr1[15] != null
				? new BigDecimal(String.valueOf(arr1[15])) : BigDecimal.ZERO;
		BigDecimal sgstAmt = arr1[14] != null
				? new BigDecimal(String.valueOf(arr1[14])) : BigDecimal.ZERO;
		BigDecimal cgstAmt = arr1[13] != null
				? new BigDecimal(String.valueOf(arr1[13])) : BigDecimal.ZERO;
		BigDecimal igstAmt = arr1[12] != null
				? new BigDecimal(String.valueOf(arr1[12])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[11] != null
				? new BigDecimal(String.valueOf(arr1[11])) : BigDecimal.ZERO;
		BigDecimal taxRate = arr1[10] != null
				? new BigDecimal(String.valueOf(arr1[10])) : BigDecimal.ZERO;

		String sGstin = arr1[0] != null ? String.valueOf(arr1[0]) : null;
		// String pos = arr1[5]!= null ? String.valueOf(arr1[5]) : null;
		String pos = arr1[5] != null
				? (String.valueOf(arr1[5]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[5]).trim())
						: String.valueOf(arr1[5]))
				: null;
		String sec7 = arr1[9] != null ? String.valueOf(arr1[9]) : null;
		CdnrLineItem itms = new CdnrLineItem();
		CdnrLineItemDetail itemDet = new CdnrLineItemDetail();
		String supplyType = arr1[17] != null ? String.valueOf(arr1[17]) : null;
		itemDet.setCessAmount(cessAmt);

		if (APIConstants.Y.equalsIgnoreCase(sec7) || isSEZSupplier
				|| DocAndSupplyTypeConstants.INV_TYPE_SEZWP.equals(supplyType)
				|| (sGstin != null
						&& !sGstin.substring(0, 2).equalsIgnoreCase(pos))
		// || igstAmt.compareTo(BigDecimal.ZERO) > 0
		) {
			itemDet.setIgstAmount(igstAmt);
		} else {
			itemDet.setSgstAmount(sgstAmt);
			itemDet.setCgstAmount(cgstAmt);
		}

		itemDet.setTaxableValue(taxableVal);
		itemDet.setRate(taxRate);
		itms.setLineNumber(counter2);
		itms.setItemDetail(itemDet);
		return itms;

	}

	private String decideInvType(String docType, String supplyType, String sec7,
			String section) {

		if (DocAndSupplyTypeConstants.DXP.equals(supplyType)) {
			return APIConstants.INV_TYPE_DE;
		} else if (DocAndSupplyTypeConstants.INV_TYPE_SEZWP
				.equals(supplyType)) {
			return APIConstants.INV_TYPE_SEWP;
		} else if (DocAndSupplyTypeConstants.INV_TYPE_SEZWOP
				.equals(supplyType)) {
			return APIConstants.INV_TYPE_SEWOP;
		} else if (section.equals(APIConstants.CDNR)) {
			if (DocAndSupplyTypeConstants.INV.equals(docType)
					|| DocAndSupplyTypeConstants.TAX.concat(",")
							.concat(DocAndSupplyTypeConstants.DTA)
							.contains(supplyType)) {
				if (sec7.equals(APIConstants.N)) {
					return APIConstants.INV_TYPE_R;
				} else {
					return APIConstants.INV_TYPE_CBW;
				}
			}
		} else if (section.equals(APIConstants.CDNRA)) {
			if (DocAndSupplyTypeConstants.TAX.concat(",")
					.concat(DocAndSupplyTypeConstants.DTA)
					.contains(supplyType)) {
				if (sec7.equals(APIConstants.N)) {
					return APIConstants.INV_TYPE_R;
				} else {
					return APIConstants.INV_TYPE_CBW;
				}
			}
		} else if (APIConstants.INV_TYPE_CBW.equals(supplyType)) {
			return APIConstants.INV_TYPE_CBW;
		}
		return null;
	}

	@Override
	public SaveBatchProcessDto convertToGstr1Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType, int chunkSize) {
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				List<CDNRInvoices> cdnrList = new ArrayList<>();
				List<CdnrLineItem> itmsList = new ArrayList<>();
				List<CreditDebitNote> creditdebitList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				int itemNumber = 0;
				// Extra new logic to find the supplierGstin is SEZ status
				String supplierGstin = objects.get(0)[0] != null
						? String.valueOf(objects.get(0)[0]) : null;
				boolean isSEZSupplier = isSEZSupplier(groupCode, supplierGstin);
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					// Reading next object[] for the forming the json.
					Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					/**
					 * Reading the next doc if exist.
					 */
					if (counter2 < totSize) {
						arr2 = objects.get(counter2);
					}
					Long id = arr1[16] != null ? (Long) arr1[16] : null;
					CdnrLineItem itms = setItemDetail(arr1, ++itemNumber,
							isSEZSupplier);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2, chunkSize)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						CreditDebitNote credeb = setInvData(arr1, section,
								itmsList, groupCode, taxDocType, chunkSize);
						creditdebitList.add(credeb);
						itmsList = new ArrayList<>();
						itemNumber = 0;
					}
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2, chunkSize)) {
						CDNRInvoices cdnr = setInv(arr1, creditdebitList);
						cdnrList.add(cdnr);
						itmsList = new ArrayList<>();
						creditdebitList = new ArrayList<>();
					}
					if (isNewBatch(arr1, arr2, idsList, totSize, counter2, chunkSize)) {
						SaveGstr1 gstr1 = setBatch(arr1, section, cdnrList);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						cdnrList = new ArrayList<>();
						itmsList = new ArrayList<>();
						creditdebitList = new ArrayList<>();
					}
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.warn(msg, objects);
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		batchDto.setGstr1(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}
}
