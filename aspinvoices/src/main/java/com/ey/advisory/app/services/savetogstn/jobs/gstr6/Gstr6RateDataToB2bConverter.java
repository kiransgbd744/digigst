package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ItemDetails;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6Items;
import com.ey.advisory.app.docs.dto.gstr6.SaveGstr6;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sri Bhavya
 *
 */
@Service("Gstr6RateDataToB2bConverter")
@Slf4j
public class Gstr6RateDataToB2bConverter implements RateDataToGstr6Converter {

	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;

	@Override
	public SaveBatchProcessDto convertToGstr6Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType) {
		LOGGER.debug("convertToGstr6Object with section {} and type {}",
				section, taxDocType);
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr6> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				LOGGER.debug("{} Batch seperation is started with {} docs",
						section, totSize);
				List<Gstr6B2bDto> b2bList = new ArrayList<>();
				List<Gstr6B2bInvoiceData> invList = new ArrayList<>();
				List<Gstr6Items> itmsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				int itemNumber = 0;
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					if (counter2 < totSize) {
						arr2 = objects.get(counter2);
					}
					Long id = new Long(String.valueOf(arr1[0]));
					Gstr6Items itms = setItemDetail(arr1,++itemNumber);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
						idsList.add(id);
						Gstr6B2bInvoiceData inv = setInvData(arr1, itmsList,
								taxDocType);
						invList.add(inv);
						itmsList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
						Gstr6B2bDto b2b = setInv(arr1, invList);
						b2bList.add(b2b);
						itmsList = new ArrayList<>();
						invList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewBatch(idsList, totSize, counter2)) {
						SaveGstr6 gstr6 = setBatch(arr1, section, b2bList);
						LOGGER.debug("New {} Batch is formed {}", section,
								gstr6);
						batchesList.add(gstr6);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						b2bList = new ArrayList<>();
						invList = new ArrayList<>();
						itmsList = new ArrayList<>();
					}
				}

			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.debug(msg, objects);
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		batchDto.setGstr6(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}

	private SaveGstr6 setBatch(Object[] arr1, String section,
			List<Gstr6B2bDto> b2bList) {
		SaveGstr6 gstr6 = new SaveGstr6();
		gstr6.setGstin(arr1[1] != null ? String.valueOf(arr1[1]) : null);
		gstr6.setTaxperiod(arr1[2] != null ? String.valueOf(arr1[2]) : null);
		gstr6.setB2bInvoice(b2bList);
		return gstr6;
	}

	private boolean isNewBatch(List<Long> idsList, int totSize, int counter2) {
		return idsList.size() >= chunkSizeFetcher.getSize()
				|| counter2 == totSize;
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		String cGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String cGstin2 = arr2[3] != null ? String.valueOf(arr2[3]) : null;
		return cGstin != null && !cGstin.equals(cGstin2)
				|| isNewBatch(idsList, totSize, counter2);
	}

	private Gstr6B2bDto setInv(Object[] arr1,
			List<Gstr6B2bInvoiceData> invList) {
		Gstr6B2bDto data = new Gstr6B2bDto();
		data.setCgstin(arr1[3] != null ? String.valueOf(arr1[3]) : null);
		//data.setCfs(arr1[4] != null ? String.valueOf(arr1[4]) : null);
		data.setInv(invList);
		return data;
	}

	private Gstr6B2bInvoiceData setInvData(Object[] arr1,
			List<Gstr6Items> itmsList, String taxDocType) {
		Gstr6B2bInvoiceData invData = new Gstr6B2bInvoiceData();
		//invData.setChksum(arr1[6] != null ? String.valueOf(arr1[6]) : null);
		//invData.setFlag(arr1[5] != null ? String.valueOf(arr1[5]) : null);
		invData.setIdt(arr1[8] != null ? String.valueOf(arr1[8]) : null);
		invData.setInum(arr1[7] != null ? String.valueOf(arr1[7]) : null);
		invData.setItms(itmsList);
		invData.setPos(arr1[10] != null ? String.valueOf(arr1[10]) : null);
		invData.setVal(arr1[9] != null ? (BigDecimal)arr1[9] : BigDecimal.ZERO);
		if(taxDocType!= null && GSTConstants.CAN.equalsIgnoreCase(taxDocType)){
			invData.setFlag(APIConstants.D);
		}
		return invData;
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2) {
		Long id = new Long(String.valueOf(arr1[0]));
		Long id2 = new Long(String.valueOf(arr2[0]));
		return !id.equals(id2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2);
	}

	private Gstr6Items setItemDetail(Object[] arr1, int itemNumber) {
		BigDecimal cessAmt = arr1[17] != null
				? new BigDecimal(String.valueOf(arr1[17])).abs() : BigDecimal.ZERO;
		BigDecimal sgstAmt = arr1[16] != null
				? new BigDecimal(String.valueOf(arr1[16])).abs() : BigDecimal.ZERO;
		BigDecimal cgstAmt = arr1[15] != null
				? new BigDecimal(String.valueOf(arr1[15])).abs() : BigDecimal.ZERO;
		BigDecimal igstAmt = arr1[14] != null
				? new BigDecimal(String.valueOf(arr1[14])).abs() : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[12] != null
				? new BigDecimal(String.valueOf(arr1[12])).abs() : BigDecimal.ZERO;
		BigDecimal taxRate = arr1[13] != null
				? new BigDecimal(String.valueOf(arr1[13])).abs() : BigDecimal.ZERO;

		Gstr6Items items = new Gstr6Items();
		items.setNum(itemNumber);
		Gstr6ItemDetails details = new Gstr6ItemDetails();
		String pos = arr1[10] != null
				? (String.valueOf(arr1[10]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[10]).trim())
						: String.valueOf(arr1[10]))
				: null;
		String cGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
	    String supplyType = arr1[18] != null ? String.valueOf(arr1[18]) : null ;
		if (DocAndSupplyTypeConstants.SEZS.equalsIgnoreCase(supplyType) ||
				(cGstin != null && !cGstin.substring(0, 2).equalsIgnoreCase(pos))) {
			details.setIgstAmount(igstAmt);
		} else {
			details.setSgstAmount(sgstAmt);
			details.setCgstAmount(cgstAmt);
		}
		details.setCessAmount(cessAmt);		
		details.setRate(taxRate);		
		details.setTaxableValue(taxableVal);
		items.setItmdet(details);
		return items;

	}
}
