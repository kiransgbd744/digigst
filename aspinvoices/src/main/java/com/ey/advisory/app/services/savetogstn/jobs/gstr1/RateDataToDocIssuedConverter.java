package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.DocIssueDetails;
import com.ey.advisory.app.docs.dto.DocIssueInvoices;
import com.ey.advisory.app.docs.dto.DocIssueList;
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
@Service("rateDataToDocIssuedConverter")
public class RateDataToDocIssuedConverter implements RateDataToGstr1Converter {

	/*@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;*/
	
	@Override
	public SaveBatchProcessDto convertToGstr1Object(List<Object[]> objects, 
			String section, String groupCode, String taxDocType, int chunkSize) {
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
//				List<DocIssueInvoices> docinvList = new ArrayList<DocIssueInvoices>();
				List<DocIssueList> docissList = new ArrayList<>();
				List<DocIssueDetails> docIssudetList = new ArrayList<>();
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
						arr2 = objects.get(counter2);
					}
					// first Object[]
					Long id =  arr1[0]!= null ? (Long) arr1[0] : null;
					String sGstin = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
					String txPriod = arr1[2]!= null ? String.valueOf(arr1[2]) : null;
					Integer serialNo = arr1[3]!= null ? Integer.parseInt(String.valueOf(arr1[3])) : null;
					String natureOfDoc = arr1[4]!= null ? String.valueOf(arr1[4]) : null;
					String seriesFrom = arr1[5]!= null ? String.valueOf(arr1[5]) : null;
					String seriesTo = arr1[6]!= null ? String.valueOf(arr1[6]) : null;
					Integer totaNum = arr1[7]!= null ? Integer.parseInt(String.valueOf(arr1[7])) : Integer.MIN_VALUE;
					Integer cancel = arr1[8]!= null ? Integer.parseInt(String.valueOf(arr1[8])) : Integer.MIN_VALUE;
					Integer netNum = arr1[9]!= null ? Integer.parseInt(String.valueOf(arr1[9])) : Integer.MIN_VALUE;
					String invKey = arr1[10]!= null ? String.valueOf(arr1[10]) : null;
					// second Object[]

					Long id2 =  arr2[0]!= null ? (Long) arr2[0] : null;
					String sGstin2 = arr2[1]!= null ? String.valueOf(arr2[1]) : null;
					String txPriod2 = arr2[2]!= null ? String.valueOf(arr2[2]) : null;
					Integer serialNo2 = arr2[3]!= null ? Integer.parseInt(String.valueOf(arr2[3])) : null;

					DocIssueList dociss = new DocIssueList();
					
					/**
					 * set DocissueList
					 */
					dociss.setSerialNum(counter2);
					dociss.setFromSerialNum(seriesFrom);
					dociss.setToSerialNum(seriesTo);
					dociss.setTotalNum(totaNum);
					dociss.setCancelled(cancel);
					dociss.setNetIssued(netNum);

					docissList.add(dociss);
					idsList.add(id);
					if (serialNo!= null && !serialNo.equals(serialNo2)
							/*|| (sGstin != null && !sGstin.equals(sGstin2))
							|| (txPriod != null
									&& !txPriod.equals(txPriod2))*/
							/*|| idsList.size() >= chunkSizeFetcher.getSize()*/
							|| counter2 == totSize) {

						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						//idsList.add(id);
						DocIssueDetails docissdet = new DocIssueDetails();
						docissdet.setDocNum(serialNo);
						docissdet.setDocIssueList(docissList);
						
						docIssudetList.add(docissdet);
						
						docissList = new ArrayList<>();

//					   docIssudetList = new ArrayList<>();
					//	docinvList.add(docinv);
					}
					if (/*(sGstin != null && !sGstin.equals(sGstin2))
							|| (txPriod != null
									&& !txPriod.equals(txPriod2))*/
							/*|| idsList.size() >= chunkSizeFetcher.getSize()
							||*/ counter2 == totSize) {
						
						DocIssueInvoices docinv = new DocIssueInvoices();
						
						if (taxDocType != null
								&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
							docinv.setInvoiceStatus(APIConstants.D); // D-Delete,
																		// A-Accept,
																		// R-Reject
						}
						//docinv.setCheckSum("");
						docinv.setDocIssueDetails(docIssudetList);
						
					    SaveGstr1 gstr1 = new SaveGstr1();
						gstr1.setSgstin(sGstin);
						gstr1.setTaxperiod(txPriod);
						LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod "
								+ "{}", section, sGstin, txPriod);
						//gstr1.setGt(new BigDecimal("3782969.01")); // static
						//gstr1.setCur_gt(new BigDecimal("3782969.01")); // static
						gstr1.setDocIssueInvoices(docinv);

						batchesList.add(gstr1);
						batchIdsList.add(idsList);
						/**
						 * reset
						 */
						idsList = new ArrayList<>();
//						docinvList = new ArrayList<>();
						docissList = new ArrayList<>();
						docIssudetList = new ArrayList<>();

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
