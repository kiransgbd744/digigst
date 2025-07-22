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
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsTdsaInvDto;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsaDto;
import com.ey.advisory.app.docs.dto.gstr7.SaveGstr7;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva Reddy
 *
 */
@Service("Gstr7TransRateDataToTdsaConverter")
@Slf4j
public class Gstr7TransRateDataToTdsaConverter
		implements RateDataToGstr7Converter {

	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;

	public SaveBatchProcessDto convertToGstr7Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType) {

		SaveBatchProcessDto result = new SaveBatchProcessDto();
		List<Gstr7TdsaDto> tdsaList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		List<SaveGstr7> batches = new ArrayList<>();
		List<Long> idsList = new ArrayList<>();

		try {
			if (objects != null && !objects.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Total records to process: {}",
							objects.size());
				}

				List<Gstr7TdsTdsaInvDto> invList = new ArrayList<>();

				for (int i = 0; i < objects.size(); i++) {
					Object[] curr = objects.get(i);
					Object[] next = (i + 1 < objects.size())
							? objects.get(i + 1) : curr;

					Long docId = (Long) curr[16];
					idsList.add(docId);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Processing docId: {}, deductee GSTIN: {}, original deductee GSTIN: {}",
								docId, curr[3], curr[4]);
					}

					// Add invoice if docNum and docDate are available
					boolean hasInvoice = curr[5] != null && curr[7] != null;
					if (hasInvoice) {
						invList.add(setInv(curr));
					}

					// Group change
					if (isNewGroup(curr, next)) {
						Gstr7TdsaDto dto = setTdsa(curr, invList);
						tdsaList.add(dto);
						invList = new ArrayList<>();
					}

					// Batch flush
					if (isNewBatch(idsList, objects.size(), i + 1)) {
						SaveGstr7 batch = setBatch(curr, section, tdsaList);
						batches.add(batch);
						batchIdsList.add(new ArrayList<>(idsList));

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Batch completed: GSTIN={}, TaxPeriod={}, Documents={}",
									batch.getGstin(), batch.getTaxperiod(),
									idsList.size());
						}

						// Reset
						tdsaList = new ArrayList<>();
						idsList = new ArrayList<>();
					}
				}
			} else {
				LOGGER.warn(
						"No data found to convert for section {} and groupCode {}",
						section, groupCode);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while converting TDSA data for section {} and groupCode {}: {}",
					section, groupCode, ex.getMessage(), ex);
			throw new AppException(
					"Unexpected error while processing GSTR-7 TDSA", ex);
		}

		result.setGstr7(batches);
		result.setIdsList(batchIdsList);
		return result;
	}

	private Gstr7TdsTdsaInvDto setInv(Object[] arr) {
		Gstr7TdsTdsaInvDto inv = new Gstr7TdsTdsaInvDto();

		inv.setOinum(asStr(arr[6]));
		inv.setOidt(formatDate(arr[8]));
		inv.setOival(toBigDecimal(arr[10]));
		inv.setOamt_ded(toBigDecimal(arr[12]));

		inv.setInum(asStr(arr[5]));
		inv.setIdt(formatDate(arr[7]));
		inv.setIval(toBigDecimal(arr[9]));
		inv.setAmt_ded(toBigDecimal(arr[11]));
		inv.setIamt(toBigDecimal(arr[13]));
		inv.setCamt(toBigDecimal(arr[14]));
		inv.setSamt(toBigDecimal(arr[15]));

		String supplyType = asStr(arr[17]);
		inv.setFlag("CAN".equalsIgnoreCase(supplyType) ? "D" : "N");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Mapped invoice: oinum={}, inum={}, flag={}",
					inv.getOinum(), inv.getInum(), inv.getFlag());
		}

		return inv;
	}

	private Gstr7TdsaDto setTdsa(Object[] arr,
			List<Gstr7TdsTdsaInvDto> invList) {
		Gstr7TdsaDto dto = new Gstr7TdsaDto();
		dto.setGstin_ded(asStr(arr[3]));
		dto.setOgstin_ded(asStr(arr[4]));
		dto.setOmonth(asStr(arr[2]));
		dto.setDocId((Long) arr[16]);

		if (invList != null && !invList.isEmpty()) {
			dto.setGstr7TdsTdsaInvDto(invList);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Set TDSA with invoices for gstin_ded={}, count={}",
						dto.getGstin_ded(), invList.size());
			}
		} else {
			dto.setOamt_ded(toBigDecimal(arr[12]));
			dto.setAmt_ded(toBigDecimal(arr[11]));
			dto.setIamt(toBigDecimal(arr[13]));
			dto.setCamt(toBigDecimal(arr[14]));
			dto.setSamt(toBigDecimal(arr[15]));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Set TDSA summary only for gstin_ded={}",
						dto.getGstin_ded());
			}
		}

		return dto;
	}

	private SaveGstr7 setBatch(Object[] arr1, String section,
			List<Gstr7TdsaDto> tdsList) {
		SaveGstr7 gstr7 = new SaveGstr7();
		gstr7.setGstin(arr1[0] != null ? String.valueOf(arr1[0]) : null);
		gstr7.setTaxperiod(arr1[1] != null ? String.valueOf(arr1[1]) : null);
		gstr7.setTdsaInvoice(tdsList);
		return gstr7;
	}

	private boolean isNewGroup(Object[] curr, Object[] next) {
		return !asStr(curr[3]).equalsIgnoreCase(asStr(next[3]))
				|| !asStr(curr[4]).equalsIgnoreCase(asStr(next[4]))
				|| !asStr(curr[2]).equalsIgnoreCase(asStr(next[2]));
	}

	private boolean isNewBatch(List<Long> idsList, int totalSize,
			int currentIndex) {
		return idsList.size() >= chunkSizeFetcher.getSize()
				|| currentIndex == totalSize;
	}

	private String asStr(Object o) {
		return o != null ? String.valueOf(o) : "";
	}

	private BigDecimal toBigDecimal(Object o) {
		return o != null ? new BigDecimal(o.toString()) : BigDecimal.ZERO;
	}

	private String formatDate(Object o) {
		if (o instanceof LocalDate) {
			return ((LocalDate) o)
					.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		}
		return "";
	}
}
