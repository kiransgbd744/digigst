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
import com.ey.advisory.app.docs.dto.B2BInvoiceData;
import com.ey.advisory.app.docs.dto.B2BInvoices;
import com.ey.advisory.app.docs.dto.B2bLineItem;
import com.ey.advisory.app.docs.dto.B2bLineItemDetail;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("rateDataToB2bB2baConverter")
public class RateDataToB2bB2baConverter implements RateDataToGstr1Converter {

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

	private B2bLineItem setItemDetail(Object[] arr1, int counter2,
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
		B2bLineItemDetail itemDeta = new B2bLineItemDetail();
		B2bLineItem itms = new B2bLineItem();

		// String pos = arr1[5] != null ? String.valueOf(arr1[5]) : null ;
		String pos = arr1[5] != null
				? (String.valueOf(arr1[5]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[5]).trim())
						: String.valueOf(arr1[5]))
				: null;
		String sec7 = arr1[9] != null ? String.valueOf(arr1[9]) : null;
		String sGstin = arr1[0] != null ? String.valueOf(arr1[0]) : null;
		String supplyType = arr1[17] != null ? String.valueOf(arr1[17]) : null;

		if (APIConstants.Y.equalsIgnoreCase(sec7) || isSEZSupplier
				|| DocAndSupplyTypeConstants.INV_TYPE_SEZWP.equals(supplyType)
				|| (sGstin != null
						&& !sGstin.substring(0, 2).equalsIgnoreCase(pos))
		// || igstAmt.compareTo(BigDecimal.ZERO) > 0
		) {
			itemDeta.setIgstAmount(igstAmt);
		} else {
			itemDeta.setSgstAmount(sgstAmt);
			itemDeta.setCgstAmount(cgstAmt);
		}
		itemDeta.setCessAmount(cessAmt);
		itemDeta.setTaxableValue(taxableVal);
		itemDeta.setRate(taxRate);
		itms.setLineNumber(counter2);
		itms.setItemDetail(itemDeta);
		return itms;
	}

	private B2BInvoiceData setInvData(Object[] arr1, String section,
			List<B2bLineItem> itmsList, String taxDocType) {

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
		String docType = arr1[18] != null ? String.valueOf(arr1[18]) : null;
		String supplyType = arr1[17] != null ? String.valueOf(arr1[17]) : null;
		Long id = arr1[16] != null ? (Long) arr1[16] : null;
		String sec7 = arr1[9] != null ? String.valueOf(arr1[9])
				: APIConstants.N;
		String diffPercent = arr1[8] != null ? String.valueOf(arr1[8]) : null;
		String eTin = arr1[7] != null ? String.valueOf(arr1[7]) : null;
		String revCharge = arr1[6] != null ? String.valueOf(arr1[6]) : null;
		if (revCharge != null && "L".equalsIgnoreCase(revCharge)) {
			revCharge = APIConstants.N;
		}
		String pos = arr1[5] != null
				? (String.valueOf(arr1[5]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[5]).trim())
						: String.valueOf(arr1[5]))
				: null;
		BigDecimal invVal = arr1[4] != null
				? new BigDecimal(String.valueOf(arr1[4])) : BigDecimal.ZERO;
		String invNum = arr1[3] != null ? String.valueOf(arr1[3]) : null;

		B2BInvoiceData inv = new B2BInvoiceData();
		if (GSTConstants.L65.equalsIgnoreCase(diffPercent)) {
			inv.setDiffPercent(new BigDecimal("0.65"));
		} else if (GSTConstants.L.equalsIgnoreCase(diffPercent)) {
			// needs to clarify
		} else if (GSTConstants.N.equalsIgnoreCase(diffPercent)) {
			// needs to clarify
		}

		inv.setEcomTin(eTin);
		inv.setPos(pos);
		inv.setInvoiceValue(invVal);
		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			inv.setInvoiceStatus(APIConstants.D); // D-Delete, A-Accept,
													// R-Reject
		}
		// inv.setCheckSum(""); //Chksum is mandatory only when flag is A/R
		inv.setReverseCharge(revCharge);
		/**
		 * R- Regular B2B Invoices, DE – Deemed Exports, SEWP – SEZ Exports with
		 * payment, SEWOP – SEZ exports without payment, CBW - Custom Bonded
		 * Warehouse
		 */
		inv.setInvoiceType(decideInvType(docType, supplyType, sec7, section));
		inv.setInvoiceNumber(invNum);
		inv.setInvoiceDate(invDate);
		if (section.equals(APIConstants.B2BA)) {
			inv.setOrigInvNumber(oInvNum);
			inv.setOrigInvDate(oInvDate);
		}
		inv.setDocId(id);
		inv.setLineItems(itmsList);
		return inv;
	}

	private String decideInvType(String docType, String supplyType, String sec7,
			String section) {

		/*
		 * old code
		 * 
		 * if (DocAndSupplyTypeConstants.DXP.equals(supplyType)) { return
		 * APIConstants.INV_TYPE_DE; } else if
		 * (DocAndSupplyTypeConstants.SEZ.equals(supplyType)) { if
		 * (BigDecimal.ZERO.equals(taxPayable)) { return
		 * APIConstants.INV_TYPE_SEWOP; } else { return
		 * APIConstants.INV_TYPE_SEWP; } } else { return
		 * APIConstants.INV_TYPE_R; }
		 */

		/*
		 * else if (DocAndSupplyTypeConstants.SEZT.equals(supplyType)) { return
		 * APIConstants.INV_TYPE_SEZWP; } else if
		 * (DocAndSupplyTypeConstants.SEZWT.equals(supplyType)) { return
		 * APIConstants.INV_TYPE_SEZWOP; }
		 */

		if (DocAndSupplyTypeConstants.DXP.equals(supplyType)) {
			return APIConstants.INV_TYPE_DE;
		} else if (DocAndSupplyTypeConstants.INV_TYPE_SEZWP
				.equals(supplyType)) {
			return APIConstants.INV_TYPE_SEWP;
		} else if (DocAndSupplyTypeConstants.INV_TYPE_SEZWOP
				.equals(supplyType)) {
			return APIConstants.INV_TYPE_SEWOP;
		} else if (section.equals(APIConstants.B2B)) {
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
		} else if (section.equals(APIConstants.B2BA)) {
			if (DocAndSupplyTypeConstants.TAX.concat(",")
					.concat(DocAndSupplyTypeConstants.DTA)
					.contains(supplyType)) {
				if (sec7.equals(APIConstants.N)) {
					return APIConstants.INV_TYPE_R;
				} else {
					return APIConstants.INV_TYPE_CBW;
				}
			}
		}
		return null;
	}

	private B2BInvoices setInv(Object[] arr1, List<B2BInvoiceData> invList) {

		String cGstin = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		if (cGstin.equalsIgnoreCase(APIConstants.URP)) {
			cGstin = null;
		}
		B2BInvoices b2b = new B2BInvoices();
		b2b.setCgstin(cGstin);
		b2b.setB2bInvoiceData(invList);
		return b2b;
	}

	private SaveGstr1 setBatch(Object[] arr1, String section,
			List<B2BInvoices> b2bList) {

		String txPriod = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[0] != null ? String.valueOf(arr1[0]) : null;
		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveGstr1 gstr1 = new SaveGstr1();
		gstr1.setSgstin(sGstin);
		gstr1.setTaxperiod(txPriod);
		// gstr1.setGt(new BigDecimal("3782969.01")); // static
		// gstr1.setCur_gt(new BigDecimal("3782969.01")); // static
		if (section.equals(APIConstants.B2B)) {
			gstr1.setB2bInvoice(b2bList);
		} else if (section.equals(APIConstants.B2BA)) {
			gstr1.setB2baInvoice(b2bList);
		}
		return gstr1;
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2, int chunkFetcherSize) {
		Long id = arr1[16] != null ? (Long) arr1[16] : null;
		Long id2 = arr2[16] != null ? (Long) arr2[16] : null;
		return !id.equals(id2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2, chunkFetcherSize);
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkFetcherSize) {
		String cGstin = arr1[2] != null ? String.valueOf(arr1[2])
				: APIConstants.URP;
		String cGstin2 = arr2[2] != null ? String.valueOf(arr2[2])
				: APIConstants.URP;
		return cGstin != null && !cGstin.equalsIgnoreCase(cGstin2)
				|| isNewBatch(arr1, arr2, idsList, totSize, counter2, chunkFetcherSize);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkFetcherSize) {
		/*
		 * String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
		 * String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		 * String txPriod2 = arr2[1]!= null ? String.valueOf(arr2[1]) : null;
		 * String sGstin2 = arr2[0]!= null ? String.valueOf(arr2[0]) : null;
		 */
		return /*
				 * (sGstin != null && !sGstin.equals(sGstin2)) || (txPriod !=
				 * null && !txPriod.equals(txPriod2)) ||
				 */ idsList.size() >= chunkFetcherSize || counter2 == totSize;
	}

	@Override
	public SaveBatchProcessDto convertToGstr1Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType, int chunkSize) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.SAV_GST1_JSON_FORMATION_START,
				PerfamanceEventConstants.RateDataToB2bB2baConverter,
				PerfamanceEventConstants.convertToGstr1Object, null);

		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				List<B2BInvoices> b2bList = new ArrayList<>();
				List<B2BInvoiceData> invList = new ArrayList<>();
				List<B2bLineItem> itmsList = new ArrayList<>();
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
					B2bLineItem itms = setItemDetail(arr1, ++itemNumber,
							isSEZSupplier);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2, chunkSize)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						B2BInvoiceData inv = setInvData(arr1, section, itmsList,
								taxDocType);
						invList.add(inv);
						itmsList = new ArrayList<>();
						itemNumber = 0;
					}
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2,chunkSize)) {
						B2BInvoices b2b = setInv(arr1, invList);
						b2bList.add(b2b);
						itmsList = new ArrayList<>();
						invList = new ArrayList<>();
					}
					if (isNewBatch(arr1, arr2, idsList, totSize, counter2,
							chunkSize)) {
						SaveGstr1 gstr1 = setBatch(arr1, section, b2bList);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						b2bList = new ArrayList<>();
						invList = new ArrayList<>();
						itmsList = new ArrayList<>();
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
		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.SAV_GST1_JSON_FORMATION_END,
				PerfamanceEventConstants.RateDataToB2bB2baConverter,
				PerfamanceEventConstants.convertToGstr1Object, null);
		return batchDto;
	}
}
