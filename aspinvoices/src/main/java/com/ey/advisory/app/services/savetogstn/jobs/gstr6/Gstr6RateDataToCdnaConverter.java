package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnaDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnaNtData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ItemDetails;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6Items;
import com.ey.advisory.app.docs.dto.gstr6.SaveGstr6;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sri Bhavya
 *
 */
@Service("Gstr6RateDataToCdnaConverter")
@Slf4j
public class Gstr6RateDataToCdnaConverter implements RateDataToGstr6Converter {

	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;
	
	@Autowired
	private GstnApi gstnapi;

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
				List<Gstr6CdnaDto> cdnaList = new ArrayList<>();
				List<Gstr6CdnaNtData> invList = new ArrayList<>();
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
						Gstr6CdnaNtData inv = setInvData(arr1, itmsList,
								taxDocType);
						invList.add(inv);
						itmsList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
						Gstr6CdnaDto b2b = setInv(arr1, invList);
						cdnaList.add(b2b);
						itmsList = new ArrayList<>();
						invList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewBatch(idsList, totSize, counter2)) {
						SaveGstr6 gstr6 = setBatch(arr1, section, cdnaList);
						LOGGER.debug("New {} Batch is formed {}", section,
								gstr6);
						batchesList.add(gstr6);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						cdnaList = new ArrayList<>();
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
			List<Gstr6CdnaDto> cdnaList) {
		SaveGstr6 gstr6 = new SaveGstr6();
		gstr6.setGstin(arr1[1] != null ? String.valueOf(arr1[1]) : null);
		gstr6.setTaxperiod(arr1[2] != null ? String.valueOf(arr1[2]) : null);
		gstr6.setCdnaInvoice(cdnaList);
		return gstr6;
	}

	private boolean isNewBatch(List<Long> idsList, int totSize, int counter2) {
		return idsList.size() >= chunkSizeFetcher.getSize()
				|| counter2 == totSize;
	}

	private Gstr6CdnaDto setInv(Object[] arr1, List<Gstr6CdnaNtData> invList) {
		Gstr6CdnaDto data = new Gstr6CdnaDto();
		data.setCgstin(arr1[3] != null ? String.valueOf(arr1[3]) : null);
		// data.setCfs(cfs);
		data.setNt(invList);
		return data;
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		String cGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String cGstin2 = arr2[3] != null ? String.valueOf(arr2[3]) : null;
		return cGstin != null && !cGstin.equals(cGstin2)
				|| isNewBatch(idsList, totSize, counter2);
	}

	private Gstr6CdnaNtData setInvData(Object[] arr1, List<Gstr6Items> itmsList,
			String taxDocType) {
		Gstr6CdnaNtData data = new Gstr6CdnaNtData();
		// data.setChksum(arr1[6] != null ? String.valueOf(arr1[6]) : null);
		// data.setFlag(flag);		
		data.setItms(itmsList);
		data.setOstin(arr1[21] != null ? String.valueOf(arr1[21]) : null);
		data.setNtdt(arr1[6] != null ? String.valueOf(arr1[6]) : null);
		data.setNtnum(arr1[5] != null ? String.valueOf(arr1[5]) : null);
		//data.setNtty(arr1[4] != null ? String.valueOf(arr1[4]) : null);	
		String ntty = arr1[4] != null ? String.valueOf(arr1[4]) : null;
		if(ntty != null){
			data.setNtty(ntty.equalsIgnoreCase("RCR") ? "C" : "D");
		}		
		data.setOntdt(arr1[10] != null ? String.valueOf(arr1[10]) : null);
		data.setOntnum(arr1[9] != null ? String.valueOf(arr1[9]) : null);		
		if (gstnapi.isDelinkingEligible(APIConstants.GSTR6.toUpperCase())) {
			data.setPos(arr1[19] != null ? String.valueOf(arr1[19]) : null);
		}else{
			data.setIdt(arr1[8] != null ? String.valueOf(arr1[8]) : null);
			data.setInum(arr1[7] != null ? String.valueOf(arr1[7]) : null);
		}
		data.setVal(arr1[11] != null
				? new BigDecimal(String.valueOf(arr1[11])) : BigDecimal.ZERO);
		// data.setOpd(opd);
		// data.setRsn(rsn);
		// data.setUpdby(updby);
			
		if(taxDocType!= null && GSTConstants.CAN.equalsIgnoreCase(taxDocType)){
		   data.setFlag(APIConstants.D);
		}
		return data;
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2) {
		Long id = new Long(String.valueOf(arr1[0]));
		Long id2 = new Long(String.valueOf(arr2[0]));
		return !id.equals(id2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2);
	}

	private Gstr6Items setItemDetail(Object[] arr1, int itemNumber) {		
		BigDecimal cessAmt = arr1[18] != null
				? new BigDecimal(String.valueOf(arr1[18])).abs() : BigDecimal.ZERO;
		BigDecimal sgstAmt = arr1[17] != null
				? new BigDecimal(String.valueOf(arr1[17])).abs() : BigDecimal.ZERO;
		BigDecimal cgstAmt = arr1[16] != null
				? new BigDecimal(String.valueOf(arr1[16])).abs() : BigDecimal.ZERO;
		BigDecimal igstAmt = arr1[15] != null
				? new BigDecimal(String.valueOf(arr1[15])).abs() : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[13] != null
				? new BigDecimal(String.valueOf(arr1[13])).abs() : BigDecimal.ZERO;
		BigDecimal taxRate = arr1[14] != null
				? new BigDecimal(String.valueOf(arr1[14])).abs() : BigDecimal.ZERO;

		Gstr6Items items = new Gstr6Items();
		items.setNum(itemNumber);
		Gstr6ItemDetails details = new Gstr6ItemDetails();
		String pos = arr1[19] != null
				? (String.valueOf(arr1[19]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[19]).trim())
						: String.valueOf(arr1[19]))
				: null;
		String cGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String supplyType = arr1[20] != null ? String.valueOf(arr1[20]) : null ;
		/*if (DocAndSupplyTypeConstants.INV_TYPE_SEZWP.equalsIgnoreCase(supplyType) ||
				(cGstin != null && !cGstin.substring(0, 2).equalsIgnoreCase(pos))) {*/
		if (cGstin != null && !cGstin.substring(0, 2).equalsIgnoreCase(pos)) {
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
