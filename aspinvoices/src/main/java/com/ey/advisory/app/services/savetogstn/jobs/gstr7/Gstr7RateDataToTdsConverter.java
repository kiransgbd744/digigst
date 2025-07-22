package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsDto;
import com.ey.advisory.app.docs.dto.gstr7.SaveGstr7;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sri Bhavya
 *
 */
@Service("Gstr7RateDataToTdsConverter")
@Slf4j
public class Gstr7RateDataToTdsConverter implements RateDataToGstr7Converter{
	
	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;
	
	@Override
	public SaveBatchProcessDto convertToGstr7Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType) {
		LOGGER.debug("convertToGstr7Object with section {} and type {}",
				section, taxDocType);
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr7> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try{
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				LOGGER.debug("{} Batch seperation is started with {} docs",
						section, totSize);
				List<Gstr7TdsDto> tdsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();				
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					Long id = new Long(String.valueOf(arr1[11]));
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
						Gstr7TdsDto itms = setItemDetail(arr1,taxDocType);
						tdsList.add(itms);
						idsList.add(id);
					} else {
						continue;
					}					
					if (isNewBatch(idsList, totSize, counter2)) {
						SaveGstr7 gstr7 = setBatch(arr1, section, tdsList);
						LOGGER.debug("New {} Batch is formed {}", section,
								gstr7);
						batchesList.add(gstr7);
						batchIdsList.add(idsList);
						
						idsList = new ArrayList<>();
						tdsList = new ArrayList<>();						
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
		batchDto.setGstr7(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
		}
	
	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		String cGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		String cGstin2 = arr2[1] != null ? String.valueOf(arr2[1]) : null;
		return cGstin != null && cGstin.equals(cGstin2)
				|| isNewBatch(idsList, totSize, counter2);
	}
	
	private SaveGstr7 setBatch(Object[] arr1, String section,
			List<Gstr7TdsDto> tdsList) {
		SaveGstr7 gstr7 = new SaveGstr7();
		gstr7.setGstin(arr1[1] != null ? String.valueOf(arr1[1]) : null);
		gstr7.setTaxperiod(arr1[0] != null ? String.valueOf(arr1[0]) : null);
		gstr7.setTdsInvoice(tdsList);
		return gstr7;
	}
	
	private boolean isNewBatch(List<Long> idsList, int totSize, int counter2) {
		return /*idsList.size() >= chunkSizeFetcher.getSize() ||*/ counter2 == totSize;
	}

	private Gstr7TdsDto setItemDetail(Object[] arr1, String taxDocType) {
		Gstr7TdsDto tdsData = new Gstr7TdsDto();
		tdsData.setGstin_ded(arr1[2] != null ? String.valueOf(arr1[2]) : null);
		tdsData.setAmt_ded(arr1[4] != null ? (BigDecimal)arr1[4] : BigDecimal.ZERO);
		tdsData.setIamt(arr1[8] != null ? (BigDecimal)arr1[8] : BigDecimal.ZERO);
		tdsData.setCamt(arr1[9] != null ? (BigDecimal)arr1[9] : BigDecimal.ZERO);
		tdsData.setSamt(arr1[10] != null ? (BigDecimal)arr1[10] : BigDecimal.ZERO);	
		if(taxDocType != null && GSTConstants.CAN.equalsIgnoreCase(taxDocType)){
			tdsData.setFlag(APIConstants.D); //D-Delete, A-Accept, R-Reject
		}
		return tdsData;
	}
	
	

}
