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
@Service("Gstr6RateDataToIsdConverter")
@Slf4j
public class Gstr6RateDataToIsdConverter implements RateDataToGstr6Converter {

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
				// List<Gstr6IsdDetailsDto> isdList = new ArrayList<>();
				Gstr6IsdDetailsDto isdList = new Gstr6IsdDetailsDto();
				List<Gstr6IsdElglstDto> elglstList = new ArrayList<>();
				List<Gstr6IsdElglstDto> inElglstList = new ArrayList<>();
				List<Gstr6IsdDocListItems> engItmsList = new ArrayList<>();
				List<Gstr6IsdDocListItems> inEngItmsList = new ArrayList<>();
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
					if (("ELIGIBLE").equalsIgnoreCase(arr1[3].toString())) {
						engItmsList.add(itms);
						idsList.add(id);
					} else if (("INELIGIBLE")
							.equalsIgnoreCase(arr1[3].toString())) {
						inEngItmsList.add(itms);
						idsList.add(id);
					}
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
						if (engItmsList != null && !engItmsList.isEmpty()) {
							Gstr6IsdElglstDto inv = setInvData(arr1,
									engItmsList);
							elglstList.add(inv);
						}
						if (inEngItmsList != null && !inEngItmsList.isEmpty()) {
							Gstr6IsdElglstDto inv = setInvData(arr1,
									inEngItmsList);
							inElglstList.add(inv);
						}
						engItmsList = new ArrayList<>();
						inEngItmsList = new ArrayList<>();
					} else {
						continue;
					}
					/*
					 * if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
					 * idsList.add(id); Gstr6IsdElglstDto inv = setInvData(arr1,
					 * itmsList, taxDocType); if
					 * (arr1[3].toString().equalsIgnoreCase("ELIGIBLE")) {
					 * elglstList.add(inv); } else if (arr1[3].toString()
					 * .equalsIgnoreCase("INELIGIBLE")) { inElglstList.add(inv);
					 * } itmsList = new ArrayList<>(); } else { continue; }
					 */
					/*
					 * if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
					 * Gstr6IsdDetailsDto b2b = setInv(arr1, elglstList,
					 * inElglstList); isdList.add(b2b); itmsList = new
					 * ArrayList<>(); elglstList = new ArrayList<>();
					 * inElglstList = new ArrayList<>(); } else { continue; }
					 */
					if (isNewBatch(idsList, totSize, counter2)) {
						Gstr6IsdDetailsDto b2b = setInv(arr1, elglstList,
								inElglstList);
						// isdList.add(b2b);
						SaveGstr6 gstr6 = setBatch(arr1, section, b2b);
						LOGGER.debug("New {} Batch is formed {}", section,
								gstr6);
						batchesList.add(gstr6);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						isdList = new Gstr6IsdDetailsDto();
						elglstList = new ArrayList<>();
						inElglstList = new ArrayList<>();
						engItmsList = new ArrayList<>();
						inEngItmsList = new ArrayList<>();
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
		if (elglstList != null && !elglstList.isEmpty()) {
			details.setElglst(elglstList);
		}
		if (inElglstList != null && !inElglstList.isEmpty()) {
			details.setInelglst(inElglstList);
		}
		return details;
	}

	private SaveGstr6 setBatch(Object[] arr1, String section,
			Gstr6IsdDetailsDto isdList) {
		SaveGstr6 gstr6 = new SaveGstr6();
		gstr6.setGstin(arr1[1] != null ? String.valueOf(arr1[1]) : null);
		gstr6.setTaxperiod(arr1[2] != null ? String.valueOf(arr1[2]) : null);
		gstr6.setIsdInvoice(isdList);
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
		String type = arr1[4] != null ? String.valueOf(arr1[4])
				: APIConstants.EMPTY;
		String type2 = arr2[4] != null ? String.valueOf(arr2[4])
				: APIConstants.EMPTY;

		String stateCode = arr1[6] != null ? String.valueOf(arr1[6])
				: APIConstants.EMPTY;
		String stateCode2 = arr2[6] != null ? String.valueOf(arr2[6])
				: APIConstants.EMPTY;

		if ("UR".equalsIgnoreCase(type)) {
			return !type.equals(type2) || !stateCode.equals(stateCode2)
					|| isNewBatch(idsList, totSize, counter2);
		} else {
			return !type.equals(type2) || !stateCode.equals(stateCode2)
					|| isNewCtin(arr1, arr2, idsList, totSize, counter2);
		}

	}

	/*
	 * private boolean isEglible(Object[] arr1, Object[] arr2) { String type =
	 * arr1[3] != null ? String.valueOf(arr1[3]) : null; String type2 = arr2[3]
	 * != null ? String.valueOf(arr2[3]) : null; return type.equals(type2) &&
	 * type.equalsIgnoreCase("ELIGIBLE"); }
	 */

	private Gstr6IsdDocListItems setItemDetail(Object[] arr1,
			String taxDocType) {
		Gstr6IsdDocListItems items = new Gstr6IsdDocListItems();

		BigDecimal iamti = arr1[13] != null ? (BigDecimal) arr1[13]
				: BigDecimal.ZERO;
		// BigDecimal iamts = arr1[14] != null ? (BigDecimal) arr1[14] :
		// BigDecimal.ZERO;
		// BigDecimal iamtc = arr1[15] != null ? (BigDecimal) arr1[15] :
		// BigDecimal.ZERO ;

		BigDecimal samts = arr1[16] != null ? (BigDecimal) arr1[16]
				: BigDecimal.ZERO;
		// BigDecimal samti = arr1[17] != null ? (BigDecimal) arr1[17] :
		// BigDecimal.ZERO;
		// BigDecimal camti = arr1[18] != null ? (BigDecimal) arr1[18] :
		// BigDecimal.ZERO;

		BigDecimal camtc = arr1[19] != null ? (BigDecimal) arr1[19]
				: BigDecimal.ZERO;
		BigDecimal csamt = arr1[20] != null ? (BigDecimal) arr1[20]
				: BigDecimal.ZERO;

		String docType = arr1[8] != null ? String.valueOf(arr1[8]) : null;

		if (docType != null && (docType.equalsIgnoreCase("ISDCN")
				|| docType.equalsIgnoreCase("ISDCNUR"))) {
			items.setDocdt(arr1[12] != null ? String.valueOf(arr1[12]) : null);
			items.setDocnum(arr1[11] != null ? String.valueOf(arr1[11]) : null);
			items.setCrddt(arr1[10] != null ? String.valueOf(arr1[10]) : "");
			items.setCrdnum(arr1[9] != null ? String.valueOf(arr1[9]) : "");
		} else {
			items.setCrddt(arr1[12] != null ? String.valueOf(arr1[12]) : "");
			items.setCrdnum(arr1[11] != null ? String.valueOf(arr1[11]) : "");
			items.setDocdt(arr1[10] != null ? String.valueOf(arr1[10]) : null);
			items.setDocnum(arr1[9] != null ? String.valueOf(arr1[9]) : null);
		}
		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			items.setFlag(APIConstants.D);
		} else {
			items.setFlag(APIConstants.N);
		}
		items.setIsdDocty(docType);
		// items.setChkSum(arr1[7] != null ? String.valueOf(arr1[7]) : null);

		/*
		 * String pos = arr1[6] != null ?
		 * (String.valueOf(arr1[6]).trim().length() == 1 ?
		 * "0".concat(String.valueOf(arr1[6]).trim()) : String.valueOf(arr1[6]))
		 * : null; String sGstin = arr1[1] != null ? String.valueOf(arr1[1]) :
		 * null; if (sGstin != null && !sGstin.substring(0,
		 * 2).equalsIgnoreCase(pos)) {
		 */

		items.setIamti(iamti);
		items.setSamts(samts);
		items.setIamts(BigDecimal.ZERO);
		items.setIamtc(BigDecimal.ZERO);
		items.setSamti(BigDecimal.ZERO);
		items.setCamti(BigDecimal.ZERO);

		items.setCamtc(camtc);
		items.setCsamt(csamt);
		return items;
	}

}
