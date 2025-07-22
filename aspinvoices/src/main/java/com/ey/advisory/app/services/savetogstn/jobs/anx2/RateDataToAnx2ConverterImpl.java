/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2Data;
import com.ey.advisory.app.docs.dto.anx2.Anx2DocumentData;
import com.ey.advisory.app.docs.dto.anx2.Anx2DocumentDetails;
import com.ey.advisory.app.docs.dto.anx2.SaveAnx2;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("RateDataToAnx2ConverterImpl")
@Slf4j
public class RateDataToAnx2ConverterImpl implements RateDataToAnx2Converter {

	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;
	
	private Anx2DocumentData setInvData(Object[] arr1, String section,
			Anx2DocumentDetails invDetails, String taxDocType) {

		String docType = arr1[6] != null ? String.valueOf(arr1[6]) : null;
		String action = arr1[7] != null ? String.valueOf(arr1[7]) : null;
		String chksum = arr1[8] != null ? String.valueOf(arr1[8]) : null;
		String itcent = arr1[9] != null ? String.valueOf(arr1[9])
				: APIConstants.N;
		Long id = Long.parseLong(String.valueOf(arr1[0]));
		// String sec7 = String.valueOf(arr1[9]);

		Anx2DocumentData inv = new Anx2DocumentData();
		
		if (docType != null) {
			if (GSTConstants.INV.equalsIgnoreCase(docType))
				inv.setInvoiceType(GSTConstants.I);
			if (GSTConstants.CR.equalsIgnoreCase(docType))
				inv.setInvoiceType(GSTConstants.C);
			if (GSTConstants.DR.equalsIgnoreCase(docType))
				inv.setInvoiceType(GSTConstants.D);
		}
		// inv.setInvoiceType(docType);
		inv.setAction(action); // A-accept, R-reject, P-Pending, N-Noaction
		inv.setCheckSum(chksum);
		if (!section.equalsIgnoreCase(APIConstants.SEZWOP)) {
			inv.setItcent(itcent);
		}
		inv.setDocId(id);
		inv.setDoc(invDetails);
		return inv;
	}

	private Anx2Data setInv(Object[] arr1, List<Anx2DocumentData> invDataList) {

		String sGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		Anx2Data inv = new Anx2Data();
		inv.setSgstin(sGstin);
		inv.setInvoiceData(invDataList);
		return inv;
	}

	private SaveAnx2 setBatch(Object[] arr1, String section,
			List<Anx2Data> invList) {

		String txPriod = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String cGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		if(LOGGER.isInfoEnabled()) {
		LOGGER.info("New Anx2 {} Batch with SGSTN {} and TaxPeriod {}", section,
				cGstin, txPriod);
		}
		SaveAnx2 anx2 = new SaveAnx2();
		anx2.setCgstin(cGstin);
		anx2.setTaxperiod(txPriod);

		if (section.equals(APIConstants.B2B)) {
			anx2.setB2bInvoice(invList);
		} else if (section.equals(APIConstants.B2BA)) {
			anx2.setB2baInvoice(invList);
		} else if (section.equals(APIConstants.DE)) {
			anx2.setDeInvoice(invList);
		} else if (section.equals(APIConstants.DEA)) {
			anx2.setDeaInvoice(invList);
		} else if (section.equals(APIConstants.SEZWP)) {
			anx2.setSezwpInvoice(invList);
		} else if (section.equals(APIConstants.SEZWPA)) {
			anx2.setSezwpaInvoice(invList);
		} else if (section.equals(APIConstants.SEZWOP)) {
			anx2.setSezwopInvoice(invList);
		} else if (section.equals(APIConstants.SEZWOPA)) {
			anx2.setSezwopaInvoice(invList);
		}
		return anx2;
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		
		String sGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String sGstin2 = arr2[3] != null ? String.valueOf(arr2[3]) : null;
		return sGstin != null && !sGstin.equals(sGstin2)
				|| isNewBatch(arr1, arr2, idsList, totSize, counter2);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		String txPriod = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String cGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		String txPriod2 = arr2[2] != null ? String.valueOf(arr2[2]) : null;
		String cGstin2 = arr2[1] != null ? String.valueOf(arr2[1]) : null;
		
		return (cGstin != null && !cGstin.equals(cGstin2))
				|| (txPriod != null && !txPriod.equals(txPriod2))
				|| idsList.size() >= chunkSizeFetcher.getSize()
				|| counter2 == totSize;
	}

	@Override
	public SaveBatchProcessDto convertToAnx2Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType) {
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveAnx2> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				List<Anx2Data> invList = new ArrayList<>();
				List<Anx2DocumentData> invDataList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					// Reading next object[] for the forming the json.
					Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					/**
					 * Reading the next doc if exist.
					 */
					if (counter2 < totSize) {
						arr2 = objects.get(counter + 1);
					}
					Long id = Long.parseLong(String.valueOf(arr1[0]));
					Anx2DocumentDetails invDetails = new Anx2DocumentDetails();

					String invDate = null;
					if (arr1[4] != null
							&& arr1[4].toString().trim().length() > 0) {
						invDate = DateUtil.parseObjToDate(arr1[4])
								.format(DateUtil.SUPPORTED_DATE_FORMAT2);
					}
					String invNum = arr1[5] != null ? String.valueOf(arr1[5])
							: null;

					invDetails.setDocDate(invDate);
					invDetails.setDocNum(invNum);
					/**
					 * This ids are used to update the Gstr1_doc_header table as
					 * a single/same batch.
					 */
					idsList.add(id);
					Anx2DocumentData inv = setInvData(arr1, section, invDetails,
							taxDocType);
					invDataList.add(inv);
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
						Anx2Data b2b = setInv(arr1, invDataList);
						invList.add(b2b);
						invDataList = new ArrayList<>();
					}
					if (isNewBatch(arr1, arr2, idsList, totSize, counter2)) {
						SaveAnx2 gstr1 = setBatch(arr1, section, invList);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						invList = new ArrayList<>();
						invDataList = new ArrayList<>();
					}
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.warn(msg, objects);
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		batchDto.setAnx2(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}
}
