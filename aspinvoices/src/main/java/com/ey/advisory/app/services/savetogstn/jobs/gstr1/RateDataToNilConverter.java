package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.NilInvoices;
import com.ey.advisory.app.docs.dto.NilSupplies;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("rateDataToNilConverter")
public class RateDataToNilConverter implements RateDataToGstr1Converter {

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2) {

		String tableType = arr1[3]!= null ? String.valueOf(arr1[3]) : null;
		String tableType2 = arr2[3]!= null ? String.valueOf(arr2[3]) : null;
		return tableType != null && !tableType.equals(tableType2)
				|| isNewBatch(arr1, arr2, idsList, totSize, counter2);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {

		/*String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[2]!= null ? String.valueOf(arr1[2]) : null;
		String txPriod2 = arr2[1]!= null ? String.valueOf(arr2[1]) : null;
		String sGstin2 = arr2[2]!= null ? String.valueOf(arr2[2]) : null;*/
		return /*(sGstin != null && !sGstin.equals(sGstin2))
				|| (txPriod != null && !txPriod.equals(txPriod2))
				||*/ counter2 == totSize;
	}

	@Override
	public SaveBatchProcessDto convertToGstr1Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType, int chunkSize) {
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				List<NilInvoices> nilinvcList = new ArrayList<>();
//				List<NilSupplies> nilsupList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				NilInvoices nilinvc = new NilInvoices();
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					// Reading next object[] for the forming the json.
					Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					/**
					 * Reading the next doc if exist.
					 */
					if (counter2 < totSize) {
						arr2 = objects.get(counter2);
					}
					// first object[]
					Long id = arr1[0]!= null ? (Long) arr1[0] : null;
					String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
					String sGstin = arr1[2]!= null ? String.valueOf(arr1[2]) : null;
					String tableType = arr1[3]!= null ? String.valueOf(arr1[3]) : null;
					String supplyTyp = arr1[4]!= null ? String.valueOf(arr1[4]) : null;
					BigDecimal amount = arr1[5] != null ? new BigDecimal(String.valueOf(arr1[5])) : BigDecimal.ZERO;
					
					// set NilInvoices data
					//Setting default values in first
					if (counter == 0) {
						nilinvc.setTotalNilRatedOutwordSup(BigDecimal.ZERO);
						nilinvc.setTotalExemptedOutwordSup(BigDecimal.ZERO);
						nilinvc.setTotalNonGstOutwordSup(BigDecimal.ZERO);
					}
					//Overriding default values based on conditions.
					if (APIConstants.NIL.equalsIgnoreCase(supplyTyp)) {
						nilinvc.setTotalNilRatedOutwordSup(amount
								.add(nilinvc.getTotalNilRatedOutwordSup()));
					} else if (GSTConstants.EXT.equalsIgnoreCase(supplyTyp)
							|| GSTConstants.EXMPT_SUPPLY_TYPE
									.equalsIgnoreCase(supplyTyp)) {
						nilinvc.setTotalExemptedOutwordSup(amount
								.add(nilinvc.getTotalExemptedOutwordSup()));
					} else if (GSTConstants.NON.equalsIgnoreCase(supplyTyp)
							|| APIConstants.SCH3.equalsIgnoreCase(supplyTyp)) {
						nilinvc.setTotalNonGstOutwordSup(
								amount.add(nilinvc.getTotalNonGstOutwordSup()));
					} else {
						LOGGER.error(
								"Unexpected Supply_Type {} is recived in "
										+ "NILNONEXMPT SaveGstr1, This needs to be "
										+ "hanlded, Please take functional input on this.",
								supplyTyp);
					}
					
					/**
					 * This ids are used to update the Gstr1_doc_header
					 * table as a single/same batch.
					 */
					idsList.add(id);
					
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {

						if (GSTConstants.GSTR1_8A.equals(tableType)) {
							nilinvc.setNatureOfSupType(
									APIConstants.SUP_TYPE_INTRB2B); // B2B Inter
						} else if (GSTConstants.GSTR1_8B.equals(tableType)) {
							nilinvc.setNatureOfSupType(
									APIConstants.SUP_TYPE_INTRAB2B); // B2B
																		// Intra
						} else if (GSTConstants.GSTR1_8C.equals(tableType)) {
							nilinvc.setNatureOfSupType(
									APIConstants.SUP_TYPE_INTRB2C); // B2C Inter
						} else if (GSTConstants.GSTR1_8D.equals(tableType)) {
							nilinvc.setNatureOfSupType(
									APIConstants.SUP_TYPE_INTRAB2C); // B2C
																		// Intra
						} else {
							LOGGER.error(
									"Unexpected Table_Type {} is recived in "
											+ "NILNONEXMPT SaveGstr1, This needs to be "
											+ "hanlded, Please take functional input on this.",
									tableType);
						}
						nilinvcList.add(nilinvc);

						nilinvc = new NilInvoices();
						nilinvc.setTotalNilRatedOutwordSup(BigDecimal.ZERO);
						nilinvc.setTotalExemptedOutwordSup(BigDecimal.ZERO);
						nilinvc.setTotalNonGstOutwordSup(BigDecimal.ZERO);

					}
						 if (isNewBatch(arr1, arr2, idsList, totSize,
						 counter2)) {
						LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}",
								sGstin, txPriod);
						NilSupplies nilsuply = new NilSupplies();
						if(taxDocType != null && GSTConstants.CAN.equalsIgnoreCase(taxDocType)){
						nilsuply.setFlag(APIConstants.D);// D-Delete
						}
						nilsuply.setNilInbvoices(nilinvcList);
//						nilsupList.add(nilsuply);
						nilinvcList = new ArrayList<>();
						/**
						 * set Save GSTR1 data
						 */
						SaveGstr1 gstr1 = new SaveGstr1();

						gstr1.setSgstin(sGstin);
						gstr1.setTaxperiod(txPriod);
						//gstr1.setGt(new BigDecimal("3782969.01")); // static
						//gstr1.setCur_gt(new BigDecimal("3782969.01")); // static
						gstr1.setNilSupplies(nilsuply);

						batchesList.add(gstr1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						nilinvcList = new ArrayList<>();
//					    nilsupList = new ArrayList<>();
					}
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.warn(msg, objects);
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg,ex);
		}
		batchDto.setGstr1(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}
}
