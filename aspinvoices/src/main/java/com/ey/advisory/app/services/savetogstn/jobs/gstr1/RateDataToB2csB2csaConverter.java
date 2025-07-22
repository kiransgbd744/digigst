package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.docs.dto.B2CSALineItem;
import com.ey.advisory.app.docs.dto.B2CSInvoices;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
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
@Service("rateDataToB2csB2csaConverter")
public class RateDataToB2csB2csaConverter implements RateDataToGstr1Converter {

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
			List<Long> idsList, int totSize, int counter2, String section) {

		// String pos = arr1[2] != null ? String.valueOf(arr1[2]) :
		// APIConstants.EMPTY;
		String eTin = arr1[3] != null ? String.valueOf(arr1[3])
				: APIConstants.EMPTY;
		String eTin2 = arr2[3] != null ? String.valueOf(arr2[3])
				: APIConstants.EMPTY;

		String diffPercent = arr1[4] != null ? String.valueOf(arr1[4])
				: APIConstants.EMPTY;

		String type = arr1[12] != null ? String.valueOf(arr1[12])
				: APIConstants.OE;
		String type2 = arr2[12] != null ? String.valueOf(arr2[12])
				: APIConstants.OE;

		// If change is added as part of User Story 119182: GSTR-1 | Change in
		// B2CS Section Save to GSTN.
		if (APIConstants.B2CS.equalsIgnoreCase(section)) {
			type = APIConstants.OE;
			type2 = APIConstants.OE;
			eTin = null;
			eTin2 = null;
		}

		String pos = arr1[2] != null
				? (String.valueOf(arr1[2]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[2]).trim())
						: String.valueOf(arr1[2]))
				: APIConstants.EMPTY;
		String pos2 = arr2[2] != null
				? (String.valueOf(arr2[2]).trim().length() == 1
						? "0".concat(String.valueOf(arr2[2]).trim())
						: String.valueOf(arr2[2]))
				: APIConstants.EMPTY;

		// String pos2 = arr2[2] != null ? String.valueOf(arr2[2]) :
		// APIConstants.EMPTY;
		String diffPercent2 = arr2[4] != null ? String.valueOf(arr2[4])
				: APIConstants.EMPTY;

		String omon = arr1[13] != null ? String.valueOf(arr1[13])
				: APIConstants.EMPTY;
		String omon2 = arr2[13] != null ? String.valueOf(arr2[13])
				: APIConstants.EMPTY;

		return !pos.equals(pos2) || !eTin.equals(eTin2) || !type.equals(type2)
				|| !diffPercent.equals(diffPercent2) || !omon.equals(omon2)
				|| isNewBatch(arr1, arr2, idsList, totSize, counter2);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {

		/*
		 * String txPriod = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		 * String sGstin = arr1[0] != null ? String.valueOf(arr1[0]) : null;
		 * String txPriod2 = arr2[1] != null ? String.valueOf(arr2[1]) : null;
		 * String sGstin2 = arr2[0] != null ? String.valueOf(arr2[0]) : null;
		 */
		return /*
				 * (sGstin != null && !sGstin.equals(sGstin2)) || (txPriod !=
				 * null && !txPriod.equals(txPriod2)) ||
				 */ counter2 == totSize;
	}

	private B2CSALineItem setItemDetail(Object[] arr1, boolean isSEZSupplier) {

		BigDecimal cess = arr1[10] != null
				? new BigDecimal(String.valueOf(arr1[10])) : BigDecimal.ZERO;
		BigDecimal sgst = arr1[9] != null
				? new BigDecimal(String.valueOf(arr1[9])) : BigDecimal.ZERO;
		BigDecimal cgst = arr1[8] != null
				? new BigDecimal(String.valueOf(arr1[8])) : BigDecimal.ZERO;
		BigDecimal igst = arr1[7] != null
				? new BigDecimal(String.valueOf(arr1[7])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[6] != null
				? new BigDecimal(String.valueOf(arr1[6])) : BigDecimal.ZERO;
		BigDecimal taxRate = arr1[5] != null
				? new BigDecimal(String.valueOf(arr1[5])) : BigDecimal.ZERO;

		// String pos = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String pos = arr1[2] != null
				? (String.valueOf(arr1[2]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[2]).trim())
						: String.valueOf(arr1[2]))
				: null;

		String sGstin = arr1[0] != null ? String.valueOf(arr1[0]) : null;

		B2CSALineItem itms = new B2CSALineItem();
		itms.setRate(taxRate);
		itms.setTaxableValue(taxableVal);
		if (isSEZSupplier || sGstin != null
				&& !sGstin.substring(0, 2).equalsIgnoreCase(pos)) {
			itms.setIgstAmount(igst);
		} else {
			itms.setCgstAmount(cgst);
			itms.setSgstAmount(sgst);
		}

		itms.setCessAmount(cess);
		return itms;
	}

	private B2CSInvoices setPartialInvData(Object[] arr1, String taxDocType,
			boolean isSEZSupplier, String section) {

		String type = arr1[12] != null ? String.valueOf(arr1[12])
				: APIConstants.OE;
		String eTin = arr1[3] != null ? String.valueOf(arr1[3]) : null;

		// If change is added as part of User Story 119182: GSTR-1 | Change in
		// B2CS Section Save to GSTN.
		if (APIConstants.B2CS.equalsIgnoreCase(section)) {
			type = APIConstants.OE;
			eTin = null;
		}
		/*
		 * BigDecimal igst = arr1[7] != null ? new
		 * BigDecimal(String.valueOf(arr1[7])) : BigDecimal.ZERO;
		 */
		String diffPercent = arr1[4] != null ? String.valueOf(arr1[4]) : null;
		// String pos = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String pos = arr1[2] != null
				? (String.valueOf(arr1[2]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[2]).trim())
						: String.valueOf(arr1[2]))
				: null;

		String sGstin = arr1[0] != null ? String.valueOf(arr1[0]) : null;

		B2CSInvoices b2cs = new B2CSInvoices();
		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			b2cs.setInvoiceStatus(APIConstants.D); // D-Delete
		}
		b2cs.setPointOfSupply(pos);
		/*
		 * if (igst != null && BigDecimal.ZERO.equals(igst)) {
		 * b2cs.setSupplyType(APIConstants.SUP_TYPE_INTRA); } else {
		 * b2cs.setSupplyType(APIConstants.SUP_TYPE_INTER); }
		 */

		if (isSEZSupplier || sGstin != null
				&& !sGstin.substring(0, 2).equalsIgnoreCase(pos)) {
			b2cs.setSupplyType(APIConstants.SUP_TYPE_INTER);
		} else {
			b2cs.setSupplyType(APIConstants.SUP_TYPE_INTRA);
		}

		b2cs.setType(type);
		b2cs.setEcomTin(eTin);
		/**
		 * If the attribute is present then the value should be only 0.65
		 */
		if (GSTConstants.L65.equalsIgnoreCase(diffPercent)) {
			b2cs.setDiffPercent(new BigDecimal("0.65"));
		} else if (GSTConstants.L.equalsIgnoreCase(diffPercent)) {
			// needs to clarify
		} else if (GSTConstants.N.equalsIgnoreCase(diffPercent)) {
			// needs to clarify
		}
		return b2cs;

	}

	private SaveGstr1 setBatch(Object[] arr1, String section,
			List<B2CSInvoices> b2csList) {

		String txPriod = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[0] != null ? String.valueOf(arr1[0]) : null;
		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveGstr1 gstr1 = new SaveGstr1();
		gstr1.setSgstin(sGstin);
		gstr1.setTaxperiod(txPriod);
		// gstr1.setGt(new BigDecimal("3782969.01")); // static
		// gstr1.setCur_gt(new BigDecimal("3782969.01")); // static
		if (section.equals(APIConstants.B2CS)) {
			gstr1.setB2csInvoice(b2csList);
		} else if (section.equals(APIConstants.B2CSA)) {
			gstr1.setB2csaInvoice(b2csList);
		}
		return gstr1;
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
				List<B2CSInvoices> b2csList = new ArrayList<>();
				List<B2CSALineItem> itmsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
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

					Long id = arr1[11] != null ? (Long) arr1[11] : null;
					BigDecimal cess = arr1[10] != null
							? new BigDecimal(String.valueOf(arr1[10]))
							: BigDecimal.ZERO;
					BigDecimal sgst = arr1[9] != null
							? new BigDecimal(String.valueOf(arr1[9]))
							: BigDecimal.ZERO;
					BigDecimal cgst = arr1[8] != null
							? new BigDecimal(String.valueOf(arr1[8]))
							: BigDecimal.ZERO;
					BigDecimal igst = arr1[7] != null
							? new BigDecimal(String.valueOf(arr1[7]))
							: BigDecimal.ZERO;
					BigDecimal taxableVal = arr1[6] != null
							? new BigDecimal(String.valueOf(arr1[6]))
							: BigDecimal.ZERO;
					BigDecimal taxRate = arr1[5] != null
							? new BigDecimal(String.valueOf(arr1[5]))
							: BigDecimal.ZERO;

					String sGstin = arr1[0] != null ? String.valueOf(arr1[0])
							: null;
					// String pos = arr1[2] != null ? String.valueOf(arr1[2]):
					// null;
					String pos = arr1[2] != null
							? (String.valueOf(arr1[2]).trim().length() == 1
									? "0".concat(String.valueOf(arr1[2]).trim())
									: String.valueOf(arr1[2]))
							: null;

					if (section.equals(APIConstants.B2CS)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						B2CSInvoices b2cs = setPartialInvData(arr1, taxDocType,
								isSEZSupplier, section);
						b2cs.setRate(taxRate);
						b2cs.setTaxableValue(taxableVal);
						if (isSEZSupplier || sGstin != null && !sGstin
								.substring(0, 2).equalsIgnoreCase(pos)) {
							b2cs.setIgstAmount(igst);
						} else {
							b2cs.setCgstAmount(cgst);
							b2cs.setSgstAmount(sgst);
						}

						b2cs.setCessAmount(cess);
						b2csList.add(b2cs);

					}
					if (section.equals(APIConstants.B2CSA)) {
						B2CSALineItem itms = setItemDetail(arr1, isSEZSupplier);
						itmsList.add(itms);

						if (isNewInvoice(arr1, arr2, idsList, totSize, counter2,
								section)) {
							/**
							 * This ids are used to update the Gstr1_doc_header
							 * table as a single/same batch.
							 */
							String omon = arr1[13] != null
									? String.valueOf(arr1[13]) : null;
							idsList.add(id);
							B2CSInvoices b2cs = setPartialInvData(arr1,
									taxDocType, isSEZSupplier, section);
							b2cs.setOrgMonthInv(omon);
							b2cs.setLineItems(itmsList);
							b2csList.add(b2cs);
							itmsList = new ArrayList<>();
						}
					}

					if (isNewBatch(arr1, arr2, idsList, totSize, counter2)) {

						SaveGstr1 gstr1 = setBatch(arr1, section, b2csList);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);
						/**
						 * reset
						 */
						idsList = new ArrayList<>();
						b2csList = new ArrayList<>();
						itmsList = new ArrayList<>();
					}
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn "
						+ "with arg {} ";
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
