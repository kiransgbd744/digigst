package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdDetailsDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdDocListItems;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdElglstDto;
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
@Service("Gstr6RateDataToIsdaConverter")
@Slf4j
public class Gstr6RateDataToIsdaConverter implements RateDataToGstr6Converter {

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
				List<Gstr6IsdDetailsDto> isdaList = new ArrayList<>();
				List<Gstr6IsdElglstDto> elglstList = new ArrayList<>();
				List<Gstr6IsdElglstDto> inElglstList = new ArrayList<>();
				List<Gstr6IsdDocListItems> itmsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					if (counter2 < totSize) {
						arr2 = objects.get(counter2);
					}
					Long id = new Long(String.valueOf(arr1[0]));
					Gstr6IsdDocListItems itms = setItemDetail(arr1, taxDocType);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
						idsList.add(id);
						Gstr6IsdElglstDto inv = setInvData(arr1, itmsList);
						if (arr1[3].toString().equalsIgnoreCase("ELIGIBLE")) {
							elglstList.add(inv);
						} else if (arr1[3].toString()
								.equalsIgnoreCase("INELIGIBLE")) {
							inElglstList.add(inv);
						}
						itmsList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
						Gstr6IsdDetailsDto b2b = setInv(arr1, elglstList,
								inElglstList);
						isdaList.add(b2b);
						itmsList = new ArrayList<>();
						elglstList = new ArrayList<>();
						inElglstList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewBatch(idsList, totSize, counter2)) {
						SaveGstr6 gstr6 = setBatch(arr1, section, isdaList);
						LOGGER.debug("New {} Batch is formed {}", section,
								gstr6);
						batchesList.add(gstr6);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						isdaList = new ArrayList<>();
						elglstList = new ArrayList<>();
						inElglstList = new ArrayList<>();
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

	private Gstr6IsdDetailsDto setInv(Object[] arr1,
			List<Gstr6IsdElglstDto> elglstList,
			List<Gstr6IsdElglstDto> inElglstList) {
		Gstr6IsdDetailsDto details = new Gstr6IsdDetailsDto();
		details.setElglst(elglstList);
		details.setInelglst(inElglstList);
		return details;
	}

	private SaveGstr6 setBatch(Object[] arr1, String section,
			List<Gstr6IsdDetailsDto> isdaList) {
		SaveGstr6 gstr6 = new SaveGstr6();
		gstr6.setGstin(arr1[1] != null ? String.valueOf(arr1[1]) : null);
		gstr6.setTaxperiod(arr1[2] != null ? String.valueOf(arr1[2]) : null);
		gstr6.setIsdaInvoice(isdaList);
		return gstr6;
	}

	private boolean isNewBatch(List<Long> idsList, int totSize, int counter2) {
		return idsList.size() >= chunkSizeFetcher.getSize()
				|| counter2 == totSize;
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		String cGstin = arr1[5] != null ? String.valueOf(arr1[5]) : null;
		String cGstin2 = arr2[5] != null ? String.valueOf(arr2[5]) : null;
		return cGstin != null && !cGstin.equals(cGstin2)
				|| isNewBatch(idsList, totSize, counter2);
	}

	private Gstr6IsdElglstDto setInvData(Object[] arr1,
			List<Gstr6IsdDocListItems> itmsList) {
		Gstr6IsdElglstDto data = new Gstr6IsdElglstDto();
		data.setTyp(arr1[4] != null ? String.valueOf(arr1[4]) : null);
		data.setCpty(arr1[5] != null ? String.valueOf(arr1[5]) : null);
		data.setStatecd(arr1[6] != null ? String.valueOf(arr1[6]) : null);
		data.setDoclst(itmsList);
		return data;
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2) {
		Long id = new Long(String.valueOf(arr1[0]));
		Long id2 = new Long(String.valueOf(arr2[0]));
		return !id.equals(id2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2);
	}

	private Gstr6IsdDocListItems setItemDetail(Object[] arr1,
			String taxDocType) {
		Gstr6IsdDocListItems items = new Gstr6IsdDocListItems();

		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			items.setFlag(APIConstants.D);
		}else{
			items.setFlag(APIConstants.N);
		}

		items.setCamtc(
				arr1[19] != null ? (BigDecimal) arr1[19] : BigDecimal.ZERO);
		items.setCamti(
				arr1[18] != null ? (BigDecimal) arr1[18] : BigDecimal.ZERO);
		items.setChkSum(arr1[7] != null ? String.valueOf(arr1[7]) : null);
		items.setCrddt(arr1[12] != null ? String.valueOf(arr1[12]) : null);
		items.setCrdnum(arr1[11] != null ? String.valueOf(arr1[11]) : null);
		items.setCsamt(
				arr1[20] != null ? (BigDecimal) arr1[20] : BigDecimal.ZERO);
		items.setDocdt(arr1[10] != null ? String.valueOf(arr1[10]) : null);
		items.setDocnum(arr1[9] != null ? String.valueOf(arr1[9]) : null);
		items.setIamtc(
				arr1[15] != null ? (BigDecimal) arr1[15] : BigDecimal.ZERO);
		items.setIamti(
				arr1[13] != null ? (BigDecimal) arr1[13] : BigDecimal.ZERO);
		items.setIamts(
				arr1[14] != null ? (BigDecimal) arr1[14] : BigDecimal.ZERO);
		items.setIsdDocty(arr1[8] != null ? String.valueOf(arr1[8]) : null);
		items.setSamti(
				arr1[17] != null ? (BigDecimal) arr1[17] : BigDecimal.ZERO);
		items.setSamts(
				arr1[16] != null ? (BigDecimal) arr1[16] : BigDecimal.ZERO);
		return items;
	}

}
