package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsDto;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsTdsaInvDto;
import com.ey.advisory.app.docs.dto.gstr7.SaveGstr7;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Service("Gstr7TransRateDataToTdsConverter")
@Slf4j
public class Gstr7TransRateDataToTdsConverter
		implements RateDataToGstr7Converter {

	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;

	@Override
	public SaveBatchProcessDto convertToGstr7Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType) {

		SaveBatchProcessDto result = new SaveBatchProcessDto();
		List<SaveGstr7> batches = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();

		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Starting TDS conversion for section={} with {} records",
							section, totSize);
				}

				List<Gstr7TdsDto> tdsList = new ArrayList<>();
				List<Gstr7TdsTdsaInvDto> invList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();

				for (int i = 0; i < totSize; i++) {
					Object[] curr = objects.get(i);
					Object[] next = (i + 1 < totSize) ? objects.get(i + 1)
							: curr;

					Long docId = curr[10] != null ? (Long) curr[10] : null;
					idsList.add(docId);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Processing docId={} for deductee GSTIN={}",
								docId, curr[2]);
					}

					if (curr[3] != null && curr[4] != null) {
						Gstr7TdsTdsaInvDto inv = setInv(curr);
						invList.add(inv);
					}

					if (isNewDeductee(curr, next)) {
						Gstr7TdsDto tds = setDeductee(curr, invList);
						tdsList.add(tds);
						invList = new ArrayList<>();
					}

					if (isNewBatch(idsList, totSize, i + 1)) {
						SaveGstr7 batch = setBatch(curr, section, tdsList);
						batches.add(batch);
						batchIdsList.add(new ArrayList<>(idsList));

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Batch completed: GSTIN={}, TaxPeriod={}, Documents={}",
									batch.getGstin(), batch.getTaxperiod(),
									idsList.size());
						}

						// Reset
						tdsList = new ArrayList<>();
						idsList = new ArrayList<>();
					}
				}
			} else {
				LOGGER.warn("No records found for section={} groupCode={}",
						section, groupCode);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error during TDS conversion for section={} groupCode={}: {}",
					section, groupCode, ex.getMessage(), ex);
			throw new AppException(
					"Unexpected error during GSTR-7 TDS processing", ex);
		}

		result.setGstr7(batches);
		result.setIdsList(batchIdsList);
		
		LOGGER.debug("save Dto {} ", result);
		return result;
	}

	private Gstr7TdsTdsaInvDto setInv(Object[] arr) {
		Gstr7TdsTdsaInvDto inv = new Gstr7TdsTdsaInvDto();

		inv.setInum(asStr(arr[3]));
		inv.setIdt(
				arr[4] != null
						? ((LocalDate) arr[4]).format(
								DateTimeFormatter.ofPattern("dd-MM-yyyy"))
						: null);
		inv.setIval(toBigDecimal(arr[5]));
		inv.setAmt_ded(toBigDecimal(arr[6]));
		inv.setIamt(toBigDecimal(arr[7]));
		inv.setCamt(toBigDecimal(arr[8]));
		inv.setSamt(toBigDecimal(arr[9]));

		String supplyType = asStr(arr[11]);
		if (APIConstants.TAX.equalsIgnoreCase(supplyType)) {
			inv.setFlag("N");
		} else if ("CAN".equalsIgnoreCase(supplyType)) {
			inv.setFlag("D");
		} else {
			inv.setFlag("N");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Mapped invoice: inum={}, idt={}, flag={}",
					inv.getInum(), inv.getIdt(), inv.getFlag());
		}

		return inv;
	}

	private Gstr7TdsDto setDeductee(Object[] arr,
			List<Gstr7TdsTdsaInvDto> invList) {
		Gstr7TdsDto dto = new Gstr7TdsDto();
		dto.setGstin_ded(asStr(arr[2]));

		if (invList.isEmpty()) {
			dto.setAmt_ded(toBigDecimal(arr[6]));
			dto.setIamt(toBigDecimal(arr[7]));
			dto.setCamt(toBigDecimal(arr[8]));
			dto.setSamt(toBigDecimal(arr[9]));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Set TDS summary for deductee={}",
						dto.getGstin_ded());
			}
		} else {
			dto.setGstr7TdsTdsaInvDto(invList);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Set TDS invoice list for deductee={}, count={}",
						dto.getGstin_ded(), invList.size());
			}
		}

		return dto;
	}

	private SaveGstr7 setBatch(Object[] arr1, String section,
			List<Gstr7TdsDto> tdsList) {
		SaveGstr7 gstr7 = new SaveGstr7();
		gstr7.setGstin(arr1[0] != null ? String.valueOf(arr1[0]) : null);
		gstr7.setTaxperiod(arr1[1] != null ? String.valueOf(arr1[1]) : null);
		gstr7.setTdsInvoice(tdsList);
		return gstr7;
	}

	private boolean isNewDeductee(Object[] current, Object[] next) {
		String currGstin = current[2] != null ? String.valueOf(current[2]) : "";
		String nextGstin = next[2] != null ? String.valueOf(next[2]) : "";
		return !currGstin.equalsIgnoreCase(nextGstin);
	}

	private boolean isNewBatch(List<Long> idsList, int totSize, int counter) {
		return idsList.size() >= chunkSizeFetcher.getSize()
				|| counter == totSize;
	}

	private BigDecimal toBigDecimal(Object obj) {
		return obj != null ? new BigDecimal(obj.toString()) : BigDecimal.ZERO;
	}

	private String asStr(Object o) {
		return o != null ? String.valueOf(o) : "";
	}
}
