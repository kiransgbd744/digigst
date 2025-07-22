/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.B2BInvoiceData;
import com.ey.advisory.app.docs.dto.B2BInvoices;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.ret.RetItemDetailsDto;
import com.ey.advisory.app.docs.dto.ret.SaveRet;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("RateDataToTbl3aConverter")
public class RateDataToTbl3aConverter implements RateDataToRetConverter {

	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;
	
	private RetItemDetailsDto setItemDetail(Object[] arr1, int counter2) {

		BigDecimal cessAmt = (BigDecimal) arr1[9];
		BigDecimal sgstAmt = (BigDecimal) arr1[8];
		BigDecimal cgstAmt = (BigDecimal) arr1[7];
		BigDecimal igstAmt = (BigDecimal) arr1[6];
		BigDecimal taxableVal = (BigDecimal) arr1[5];
		RetItemDetailsDto itm = new RetItemDetailsDto();

		itm.setCessAmount(cessAmt);
		itm.setSgstAmount(sgstAmt);
		itm.setCgstAmount(cgstAmt);
		itm.setIgstAmount(igstAmt);
		itm.setTaxableValue(taxableVal);
		return itm;
	}

	
	private SaveRet setBatch(Object[] arr1, String section/*,
			List<B2BInvoices> b2bList*/) {

		String txPriod = (String) arr1[1];
		String sGstin = (String) arr1[0];
		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveRet ret = new SaveRet();
		ret.setGstin(sGstin);
		ret.setRtnprd(txPriod);
		ret.setIsnil(true);
		ret.setReset(true);
	
		return ret;
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2) {
		Long id = (Long) arr1[16];
		Long id2 = (Long) arr2[16];
		return !id.equals(id2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2);
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		String cGstin = (String) arr1[2];
		String cGstin2 = (String) arr2[2];
		return cGstin != null && !cGstin.equals(cGstin2)
				|| isNewBatch(arr1, arr2, idsList, totSize, counter2);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		String txPriod = (String) arr1[1];
		String sGstin = (String) arr1[0];
		String txPriod2 = (String) arr2[1];
		String sGstin2 = (String) arr2[0];
		return (sGstin != null && !sGstin.equals(sGstin2))
				|| (txPriod != null && !txPriod.equals(txPriod2))
				|| idsList.size() >= chunkSizeFetcher.getSize()
				|| counter2 == totSize;
	}

	@Override
	public SaveBatchProcessDto convertToRetObject(List<Object[]> objects,
			String section, String groupCode, String taxDocType) {
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveRet> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				/*List<B2BInvoices> b2bList = new ArrayList<>();
				List<B2BInvoiceData> invList = new ArrayList<>();
				List<B2bLineItem> itmsList = new ArrayList<>();*/
				List<Long> idsList = new ArrayList<>();
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					// Reading next object[] for the forming the json.
					Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					/**
					 * Reading the next doc if exist.
					 */
					if (counter2 < totSize) {
						arr2 = objects.get(counter + 1);
					}
					Long id = (Long) arr1[16];
					RetItemDetailsDto itms = setItemDetail(arr1, counter2);
					//itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
						/**
						 * This ids are used to update the ret_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						/*B2BInvoiceData inv = setInvData(arr1, section,
								itmsList, taxDocType);*/
						//invList.add(inv);
						//itmsList = new ArrayList<>();
					}
					/*if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
						B2BInvoices b2b = setInv(arr1, invList);
						b2bList.add(b2b);
						itmsList = new ArrayList<>();
						invList = new ArrayList<>();
					}*/
					if (isNewBatch(arr1, arr2, idsList, totSize, counter2)) {
						SaveRet ret = setBatch(arr1, section/*, b2bList*/);
						batchesList.add(ret);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						/*b2bList = new ArrayList<>();
						invList = new ArrayList<>();
						itmsList = new ArrayList<>();*/
					}
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.warn(msg, objects);
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		batchDto.setRet(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}
}
