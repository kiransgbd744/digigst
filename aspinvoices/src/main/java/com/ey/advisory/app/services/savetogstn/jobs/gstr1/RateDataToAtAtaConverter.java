package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.docs.dto.ATInvoices;
import com.ey.advisory.app.docs.dto.ATItemDetails;
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
@Service("rateDataToAtAtaConverter")
@Slf4j
public class RateDataToAtAtaConverter implements RateDataToGstr1Converter {

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
		
		//String pos = arr1[2]!= null ? String.valueOf(arr1[2]) : APIConstants.EMPTY;
		//String pos2 = arr2[2]!= null ?String.valueOf(arr2[2]) : APIConstants.EMPTY;
		
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
		
		String diffPercent = arr1[3] != null ? String.valueOf(arr1[3]) : APIConstants.EMPTY;
		String diffPercent2 = arr2[3] != null ? String.valueOf(arr2[3]) : APIConstants.EMPTY;

		if (APIConstants.AT.equalsIgnoreCase(section)) {
			return !pos.equals(pos2) || !diffPercent.equals(diffPercent2)
					|| isNewBatch(arr1, arr2, idsList, totSize, counter2);
		} else {
			String omon = arr1[11] != null ? String.valueOf(arr1[11])
					: APIConstants.EMPTY;
			String omon2 = arr2[11] != null ? String.valueOf(arr2[11])
					: APIConstants.EMPTY;
			return !pos.equals(pos2) || !diffPercent.equals(diffPercent2)
					|| !omon.equals(omon2)
					|| isNewBatch(arr1, arr2, idsList, totSize, counter2);
		}
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		/*String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		String txPriod2 = arr2[1]!= null ? String.valueOf(arr2[1]) : null;
		String sGstin2 = arr2[0]!= null ? String.valueOf(arr2[0]) : null;*/
		return /*(sGstin != null && !sGstin.equals(sGstin2))
				|| (txPriod != null && !txPriod.equals(txPriod2))
				||*/ counter2 == totSize;
	}

	private ATItemDetails setItemDetail(Object[] arr1, boolean isSEZSupplier) {
		
		BigDecimal cessAmt = arr1[9] != null ? new BigDecimal(String.valueOf(arr1[9])) : BigDecimal.ZERO;
		BigDecimal sgstAmt = arr1[8] != null ? new BigDecimal(String.valueOf(arr1[8])) : BigDecimal.ZERO;
		BigDecimal cgstAmt = arr1[7] != null ? new BigDecimal(String.valueOf(arr1[7])) : BigDecimal.ZERO;
		BigDecimal igstAmt = arr1[6] != null ? new BigDecimal(String.valueOf(arr1[6])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[5] != null ? new BigDecimal(String.valueOf(arr1[5])) : BigDecimal.ZERO;
		BigDecimal taxRate = arr1[4] != null ? new BigDecimal(String.valueOf(arr1[4])) : BigDecimal.ZERO;

		String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		//String pos = arr1[2]!= null ? String.valueOf(arr1[2]) : null;
		String pos = arr1[2] != null
				? (String.valueOf(arr1[2]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[2]).trim())
						: String.valueOf(arr1[2]))
				: null;
		
		ATItemDetails itms = new ATItemDetails();
		itms.setRt(taxRate);
		itms.setAdAmt(taxableVal);
		
		if (isSEZSupplier || sGstin != null
				&& !sGstin.substring(0, 2).equalsIgnoreCase(pos)
				//|| igstAmt.compareTo(BigDecimal.ZERO) > 0
				) {
			itms.setIamt(igstAmt);
		} else {
			itms.setCamt(cgstAmt);
			itms.setSamt(sgstAmt);
		}
		
		itms.setCsamt(cessAmt);
		return itms;
	}

	private ATInvoices setInvData(Object[] arr1, String section,
			List<ATItemDetails> itmsList, String taxDocType) {
		//BigDecimal igstAmt = (BigDecimal) arr1[6];
		
		
		String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		String omon = arr1[11]!= null ? String.valueOf(arr1[11]) : null;
		//String pos = arr1[2]!= null ? String.valueOf(arr1[2]) : null;
		String pos = arr1[2] != null
				? (String.valueOf(arr1[2]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[2]).trim())
						: String.valueOf(arr1[2]))
				: null;
		String diffPercent = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		
		ATInvoices at = new ATInvoices();
		if(taxDocType != null && GSTConstants.CAN.equalsIgnoreCase(taxDocType)){
		at.setFlag(APIConstants.D); // D-Delete
		}
		at.setPos(pos);
		if (pos.equals(sGstin.substring(0, 2))) {
			at.setSplyTy(APIConstants.SUP_TYPE_INTRA);
		} else {
			at.setSplyTy(APIConstants.SUP_TYPE_INTER);
		}
		if(GSTConstants.L65.equalsIgnoreCase(diffPercent)) {
			at.setDiffPercent(new BigDecimal("0.65"));
		} else if(GSTConstants.L.equalsIgnoreCase(diffPercent)) {
			// needs to clarify
		} else if(GSTConstants.N.equalsIgnoreCase(diffPercent)) {
			// needs to clarify
		}
		if (section.equals(APIConstants.ATA)) {
				at.setOmon(omon);
		}
		at.setItems(itmsList);
		return at;
	}

	private SaveGstr1 setBatch(Object[] arr1, String section,
			List<ATInvoices> atList) {
		
		String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveGstr1 gstr1 = new SaveGstr1();
		gstr1.setSgstin(sGstin);
		gstr1.setTaxperiod(txPriod);
		if (section.equals(APIConstants.AT)) {
			gstr1.setAtInvoice(atList);
		} else if (section.equals(APIConstants.ATA)) {
			gstr1.setAtaInvoice(atList);
		}
		//gstr1.setGt(new BigDecimal("3782969.01")); // static
		//gstr1.setCur_gt(new BigDecimal("3782969.01")); // static
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
				List<ATInvoices> atList = new ArrayList<>();
				List<ATItemDetails> itmsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				//Extra new logic to find the supplierGstin is SEZ status 
				String supplierGstin = objects.get(0)[0]!= null ? String.valueOf(objects.get(0)[0]) : null;
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
					Long id = arr1[10]!= null ? (Long) arr1[10] : null;
					ATItemDetails itms = setItemDetail(arr1, isSEZSupplier);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2, section)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						ATInvoices at = setInvData(arr1, section, itmsList, taxDocType);
						atList.add(at);
						itmsList = new ArrayList<>();
					}
					if (isNewBatch(arr1, arr2, idsList, totSize, counter2)) {
						SaveGstr1 gstr1 = setBatch(arr1, section, atList);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						atList = new ArrayList<>();
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
			throw new AppException(msg,ex);
		}
		batchDto.setGstr1(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}
}
