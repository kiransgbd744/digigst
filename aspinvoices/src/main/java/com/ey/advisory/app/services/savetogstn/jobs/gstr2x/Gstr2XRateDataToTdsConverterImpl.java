package com.ey.advisory.app.services.savetogstn.jobs.gstr2x;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr2x.Gstr2XDto;
import com.ey.advisory.app.docs.dto.gstr2x.Gstr2XTdsDto;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Service("Gstr2XRateDataToTdsConverterImpl")
@Slf4j
public class Gstr2XRateDataToTdsConverterImpl
		implements RateDataToGstr2XConverter {

	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;

	@Override
	public SaveBatchProcessDto convertToGstr2XObject(List<Object[]> objects,
			String section, String groupCode, String taxDocType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("convertToGstr2XObject with section {} and type {}",
					section, taxDocType);
		}
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<Gstr2XDto> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("{} Batch seperation is started with {} docs",
							section, totSize);
				}
				List<Gstr2XTdsDto> tdsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					if (counter2 < totSize) {
						arr2 = objects.get(counter2);
					}
					Long id = new Long(String.valueOf(arr1[0]));
					idsList.add(id);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
						Gstr2XTdsDto tdsData = setInv(arr1);
						tdsList.add(tdsData);
					} else {
						continue;
					}
					if (isNewBatch(idsList, totSize, counter2)) {
						Gstr2XDto gstr2x = setBatch(arr1, tdsList);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("New {} Batch is formed {}", section,
									gstr2x);
						}
						batchesList.add(gstr2x);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						tdsList = new ArrayList<>();
					}
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = "Zero eligible documents found to do Save to Gstn";
					LOGGER.debug(msg, objects);
				}
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		batchDto.setGstr2x(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;

	}

	private Gstr2XTdsDto setInv(Object[] arr1) {
		Gstr2XTdsDto data = new Gstr2XTdsDto();
		data.setCtin(arr1[3] != null ? String.valueOf(arr1[3]) : null);
		data.setChksum(arr1[6] != null ? String.valueOf(arr1[6]) : null);
		data.setMonth(arr1[5] != null ? String.valueOf(arr1[5]) : null);
		data.setFlag(arr1[7] != null ? String.valueOf(arr1[7]) : null);
		data.setInum(arr1[8] != null ? String.valueOf(arr1[8]) : null);
		data.setIdt(arr1[9] != null ? String.valueOf(arr1[9]) : null);
		String invDate = null;
		if (arr1[9] != null && arr1[9].toString().trim().length() > 0) {
			invDate = ((LocalDate) arr1[9])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
			data.setIdt(invDate);
		}
		return data;
	}

	private Gstr2XDto setBatch(Object[] arr1, List<Gstr2XTdsDto> tdsList) {
		Gstr2XDto dto = new Gstr2XDto();
		dto.setGstin(arr1[2] != null ? String.valueOf(arr1[2]) : null);
		dto.setTaxperiod(arr1[4] != null ? String.valueOf(arr1[4]) : null);
		dto.setTds(tdsList);
		return dto;
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

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2) {
		Long id = new Long(String.valueOf(arr1[0]));
		Long id2 = new Long(String.valueOf(arr2[0]));
		return !id.equals(id2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2);
	}

}
