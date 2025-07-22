package com.ey.advisory.app.services.savetogstn.jobs.itc04;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Dto;
import com.ey.advisory.app.docs.dto.itc04.Itc04ItemsDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5ADto;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Service("Itc04RateDataToTable5AConverter")
@Slf4j
public class Itc04RateDataToTable5AConverter implements RateDataToItc04Converter {
	
	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;
	
	@Override
	public SaveBatchProcessDto convertToItc04Object(List<Object[]> objects, String section, String groupCode,
			String taxDocType) {
		LOGGER.debug("convertToItc04Object with section {} and type {}", section, taxDocType);
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<Itc04Dto> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				LOGGER.debug("{} Batch seperation is started with {} docs", section, totSize);
				List<Itc04Table5ADto> table5aList = new ArrayList<>();
				List<Itc04ItemsDto> itmsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					if (counter2 < totSize) {
						arr2 = objects.get(counter2);
					}
					Long id = new Long(String.valueOf(arr1[0]));
					Itc04ItemsDto itms = setItemDetail(arr1);
					idsList.add(id);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
						Itc04Table5ADto table5b = setInv(arr1,itmsList,taxDocType);
						table5aList.add(table5b);
						itmsList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewBatch(idsList, totSize, counter2)) {
						Itc04Dto itc04 = setBatch(arr1, section, table5aList);
						LOGGER.debug("New {} Batch is formed {}", section, itc04);
						batchesList.add(itc04);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						table5aList = new ArrayList<>();
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
		batchDto.setItc04(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}
	private Itc04Dto setBatch(Object[] arr1, String section, List<Itc04Table5ADto> table5aList) {
		Itc04Dto dto = new Itc04Dto();
		dto.setGstin(arr1[1] != null ? String.valueOf(arr1[1]) : null);
		dto.setTaxperiod(arr1[2] != null ? String.valueOf(arr1[2]) : null);
		dto.setTable5a(table5aList);
		return dto;
	}
	private Itc04Table5ADto setInv(Object[] arr1, List<Itc04ItemsDto> itmsList, String taxDocType) {
		Itc04Table5ADto data = new Itc04Table5ADto();
		//data.setCtin(arr1[3] != null ? String.valueOf(arr1[3]) : null);
		//data.setJwStCd(arr1[4] != null ? String.valueOf(arr1[4]) : null);		
		String ctin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		data.setCtin(ctin);
		String jwStCode = arr1[4] != null ? String.valueOf(arr1[4]) : null;
		if (jwStCode != null && !jwStCode.isEmpty()) {
			data.setJwStCd(jwStCode);
		} else {
			if (ctin != null && !ctin.isEmpty()) {
				String jwst=ctin.substring(0, 2);
				data.setJwStCd(jwst);
			}
		}		
		if(taxDocType!= null && GSTConstants.CAN.equalsIgnoreCase(taxDocType)){
			data.setFlag(APIConstants.D);
		}else{
			String saveFlag = arr1[15] != null ? String.valueOf(arr1[15]) : null;
			if (saveFlag != null && !saveFlag.isEmpty()) {
				data.setFlag(APIConstants.E);
			}	
		}
		data.setFlag(Strings.isNullOrEmpty(data.getFlag()) ? APIConstants.N : data.getFlag());
		data.setItc04ItemsDto(itmsList);
		return data;
	}
	private boolean isNewBatch(List<Long> idsList, int totSize, int counter2) {
		return idsList.size() >= chunkSizeFetcher.getSize() || counter2 == totSize;
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList, int totSize, int counter2) {
		String cGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String cGstin2 = arr2[3] != null ? String.valueOf(arr2[3]) : null;
		return cGstin != null && !cGstin.equals(cGstin2) || isNewBatch(idsList, totSize, counter2);
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2, List<Long> idsList, int totSize, int counter2) {
		Long id = new Long(String.valueOf(arr1[0]));
		Long id2 = new Long(String.valueOf(arr2[0]));
		return !id.equals(id2) || isNewCtin(arr1, arr2, idsList, totSize, counter2);
	}

	private Itc04ItemsDto setItemDetail(Object[] arr1) {
		Itc04ItemsDto items = new Itc04ItemsDto();
		items.setOchnum(arr1[5] != null ? String.valueOf(arr1[5]) : null);
		items.setOchdt(arr1[6] != null ? String.valueOf(arr1[6]) : null);
		items.setJw2Chnum(arr1[7] != null ? String.valueOf(arr1[7]) : null);
		items.setJw2Chdate(arr1[8] != null ? String.valueOf(arr1[8]) : null);
		items.setUqc(arr1[10] != null ? String.valueOf(arr1[10]) : null);
		items.setQty(arr1[11] != null ? new BigDecimal(String.valueOf(arr1[11])).abs() : BigDecimal.ZERO);
		items.setLwuqc(arr1[13] != null ? String.valueOf(arr1[13]) : null);
		items.setLwqty(arr1[14] != null ? new BigDecimal(String.valueOf(arr1[14])).abs() : BigDecimal.ZERO);
		String natjw = arr1[9] != null ? String.valueOf(arr1[9]) : null;
		if (natjw != null && !natjw.isEmpty()) {
			natjw = natjw.replaceAll("[^a-zA-Z0-9-/\\s]", "/");
			items.setNatjw(natjw);
		}
		String desc = arr1[12] != null ? String.valueOf(arr1[12]) : null;
		if (desc != null && !desc.isEmpty()) {
			desc = desc.replaceAll("[^a-zA-Z0-9-/\\s]", "/");
			String truncatedDesc = truncateTo70Characters(desc);
			items.setDesc(truncatedDesc);
		}
		return items;
	}
	 public String truncateTo70Characters(String desc) {
	        if (desc.length() > 70) {
	            return desc.substring(0, 70);
	        } else {
	            return desc;
	        }
	    }
}
