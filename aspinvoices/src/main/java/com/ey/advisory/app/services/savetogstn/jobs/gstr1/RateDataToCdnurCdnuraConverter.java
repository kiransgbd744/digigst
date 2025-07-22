package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.CDNURInvoices;
import com.ey.advisory.app.docs.dto.CdnurLineItem;
import com.ey.advisory.app.docs.dto.CdnurLineItemDetail;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.app.services.common.DocKeyGenerator;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("rateDataToCdnurCdnuraConverter")
public class RateDataToCdnurCdnuraConverter
		implements RateDataToGstr1Converter {
	
	@Autowired
	@Qualifier("DocKeyGenerator")
	private DocKeyGenerator docKeyGenerator ;
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	
	@Autowired
	private GstnApi gstnapi;
	
	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2, int chunkSize) {

		Long id = arr1[16]!= null ? (Long) arr1[16] : null;
		Long id2 = arr2[16]!= null ? (Long) arr2[16] : null;
		return !id.equals(id2)
				|| isNewBatch(arr1, arr2, idsList, totSize, counter2, chunkSize);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkSize) {

		/*String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		String txPriod2 = arr2[1]!= null ? String.valueOf(arr2[1]) : null;
		String sGstin2 = arr2[0]!= null ? String.valueOf(arr2[0]) : null;*/
		return /*(sGstin != null && !sGstin.equals(sGstin2))
				|| (txPriod != null && !txPriod.equals(txPriod2))
				||*/ idsList.size() >= chunkSize
				|| counter2 == totSize;
	}

	private SaveGstr1 setBatch(Object[] arr1, String section,
			List<CDNURInvoices> cdnurList) {

		String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveGstr1 gstr1 = new SaveGstr1();
		gstr1.setSgstin(sGstin);
		gstr1.setTaxperiod(txPriod);
		//gstr1.setGt(new BigDecimal("3782969.01")); // static
		//gstr1.setCur_gt(new BigDecimal("3782969.01")); // static
		if (section.equals(APIConstants.CDNUR)) {
			gstr1.setCdnurInvoice(cdnurList);
		} else if (section.equals(APIConstants.CDNURA)) {
			gstr1.setCdnuraInvoice(cdnurList);
		}
		return gstr1;
	}

	private CdnurLineItem setItemDetail(Object[] arr1, int counter2) {
		
		BigDecimal cessAmt = arr1[15] != null ? new BigDecimal(String.valueOf(arr1[15])) : BigDecimal.ZERO;
		BigDecimal igstAmt = arr1[12] != null ? new BigDecimal(String.valueOf(arr1[12])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[11] != null ? new BigDecimal(String.valueOf(arr1[11])) : BigDecimal.ZERO;
		BigDecimal taxRate = arr1[10] != null ? new BigDecimal(String.valueOf(arr1[10])) : BigDecimal.ZERO;

		CdnurLineItem itms = new CdnurLineItem();
		CdnurLineItemDetail itemDet = new CdnurLineItemDetail();
		itemDet.setRate(taxRate);
		itemDet.setTaxableValue(taxableVal);
		itemDet.setIgstAmount(igstAmt);
		itemDet.setCessAmount(cessAmt);
		itms.setLineNumber(counter2);
		itms.setItemDetail(itemDet);
		return itms;
	}

	private CDNURInvoices setInvData(Object[] arr1, String section,
			List<CdnurLineItem> itmsList, String groupCode, String taxDocType) {
		
		String shipDate = null;
		if (arr1[26] != null && String.valueOf(arr1[26]).trim().length() > 0) {
			shipDate = ((LocalDate) arr1[26])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String shipNum = arr1[25] != null ? String.valueOf(arr1[25]) : null;
		
		//Possible txDocType values are - CDNUR/CDNUR-B2CL/cdnur-EXPORTS/CDNURA
		//String txDocType = arr1[24] != null ? String.valueOf(arr1[24]) : null;
		String docType = arr1[23]!= null ? String.valueOf(arr1[23]) : null;
		String preGst = arr1[22]!= null ? String.valueOf(arr1[22]) : null;
		String oInvDate = null;
		if (arr1[21] != null && arr1[21].toString().trim().length() > 0) {
			oInvDate = ((LocalDate) arr1[21])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String oInvNum = arr1[20]!= null ? String.valueOf(arr1[20]) : null;
		
		String invDate = null;
		if (arr1[19] != null && arr1[19].toString().trim().length() > 0) {
			invDate = ((LocalDate) arr1[19])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		//BigDecimal taxPayable = (BigDecimal) arr1[18];
	    String supplyType = arr1[17]!= null ? String.valueOf(arr1[17]) : null;
		Long id = arr1[16]!= null ? (Long) arr1[16] : null;

		//int lineNo = (Integer) arr1[9];
		String diffPercent = arr1[8]!= null ? String.valueOf(arr1[8]) : null;
//		String eTin = (String) arr1[7];
//		String revCharge = (String) arr1[6];
//		String pos = (String) arr1[5];
		BigDecimal invVal = arr1[4] != null ? new BigDecimal(String.valueOf(arr1[4])) : BigDecimal.ZERO;
		String invNum = arr1[3]!= null ? String.valueOf(arr1[3]) : null;
//		String cGstin = (String) arr1[2];
//		String taxPeriod = (String) arr1[1];
//		String sGstin = (String) arr1[0];

		CDNURInvoices cdnur = new CDNURInvoices();
		if(taxDocType != null && GSTConstants.CAN.equalsIgnoreCase(taxDocType)){
		cdnur.setInvoiceStatus(APIConstants.D);// D-Delete
		}
		cdnur.setType(decideType(supplyType));

		if (GSTConstants.CR.concat(",").concat(GSTConstants.RCR).contains(docType)) {
			cdnur.setCredDebRefVoucher(APIConstants.C);
		} else if (GSTConstants.DR.concat(",").concat(GSTConstants.RDR).contains(docType)) {
			cdnur.setCredDebRefVoucher(APIConstants.D);
		} else if (GSTConstants.RFV.concat(",").concat(GSTConstants.RRFV).contains(docType)) {
			cdnur.setCredDebRefVoucher(APIConstants.R);
		}
		cdnur.setCredDebRefVoucherNum(invNum);
		cdnur.setCredDebRefVoucherDate(invDate);
		/*if(preGst) {
			cdnur.setPreGstRegNote(GSTConstants.Y);
		} else {
			cdnur.setPreGstRegNote(GSTConstants.N);
		}*/
		
		
		cdnur.setTotalNoteVal(invVal);
		
		
		
		if (gstnapi.isDelinkingEligible(APIConstants.GSTR1.toUpperCase())) {

			String pos = arr1[5] != null
					? (String.valueOf(arr1[5]).trim().length() == 1
							? "0".concat(String.valueOf(arr1[5]).trim())
							: String.valueOf(arr1[5]))
					: null;
			if (APIConstants.B2CL.equalsIgnoreCase(decideType(supplyType))) {

				cdnur.setPos(pos);
				if (GSTConstants.L65.equalsIgnoreCase(diffPercent)) {
					cdnur.setDiffPercent(new BigDecimal("0.65"));
				} else if (GSTConstants.L.equalsIgnoreCase(diffPercent)) {
					// needs to clarify
				} else if (GSTConstants.N.equalsIgnoreCase(diffPercent)) {
					// needs to clarify
				}
			}

			if (section.equals(APIConstants.CDNURA)) {

				cdnur.setOriCredDebNum(oInvNum);
				cdnur.setOriCredDebDate(oInvDate);
			}

		} else {
			
			if(GSTConstants.L65.equalsIgnoreCase(diffPercent)) {
				cdnur.setDiffPercent(new BigDecimal("0.65"));
			} else if(GSTConstants.L.equalsIgnoreCase(diffPercent)) {
				// needs to clarify
			} else if(GSTConstants.N.equalsIgnoreCase(diffPercent)) {
				// needs to clarify
			}
			
			cdnur.setPreGstRegNote(preGst);
			
			if(APIConstants.CDNUR.equalsIgnoreCase(section)) {
				
				cdnur.setInvNum(oInvNum); // For CDNUR
				cdnur.setInvDate(oInvDate); //For CDNUR
				
			} else if (section.equals(APIConstants.CDNURA)) {
				
				cdnur.setOriCredDebNum(oInvNum);
				cdnur.setOriCredDebDate(oInvDate);
				
				if (GSTConstants.RCR.concat(",").concat(GSTConstants.RDR)
						.contains(docType)) {
					if (shipNum == null || shipDate == null
							|| shipNum.trim().isEmpty()
							|| shipDate.trim().isEmpty()) {

						String sGstin = arr1[0] != null ? String.valueOf(arr1[0])
								: null;
						LocalDate oInvDateAsDate = (LocalDate) arr1[21];
						String finYear = GenUtil.getFinYear(oInvDateAsDate);
						String oInvKey = docKeyGenerator.generateKey(sGstin, invNum,
								finYear, docType);
						TenantContext.setTenantId(groupCode);
						Object[] originDoc = docRepository
								.fetchOriginDocForRCrRDr(oInvKey);
						if (originDoc != null) {
							shipNum = originDoc[0] != null
									? String.valueOf(originDoc[0]) : null;
							if (originDoc[1] != null
									&& String.valueOf(originDoc[1]).trim()
											.length() > 0) {
								shipDate = ((LocalDate) originDoc[1]).format(
										DateUtil.SUPPORTED_DATE_FORMAT2);
							}
						}
					}
				}
				
				cdnur.setInvNum(shipNum); // For CDNURA
				cdnur.setInvDate(shipDate); //For CDNURA
				
			}
		}
		
		cdnur.setDocId(id);
		cdnur.setCdnrLineItem(itmsList);
		return cdnur;
	}
	
	private String decideType(String supplyType) {
		
		if (GSTConstants.EXPT.equalsIgnoreCase(supplyType)) {
			return APIConstants.EXPWP.toUpperCase();
		} else if (GSTConstants.EXPWT.equalsIgnoreCase(supplyType)) {
			return APIConstants.EXPWOP.toUpperCase();
		} else if (APIConstants.TAX.equalsIgnoreCase(supplyType)
				|| GSTConstants.DTA.equalsIgnoreCase(supplyType)) {
			return APIConstants.B2CL.toUpperCase();
		}
		return null;
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
				List<CDNURInvoices> cdnurList = new ArrayList<>();
				List<CdnurLineItem> itmsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				int itemNumber = 0;
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
					Long id = arr1[16]!= null ? (Long) arr1[16] : null;
					CdnurLineItem itms = setItemDetail(arr1, ++itemNumber);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2, chunkSize)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						CDNURInvoices cdnur = setInvData(arr1,section,itmsList,
								groupCode, taxDocType);
						cdnurList.add(cdnur);
						itmsList = new ArrayList<>();
						itemNumber = 0;
					}
					if (isNewBatch(arr1, arr2, idsList, totSize, counter2, chunkSize)) {
						SaveGstr1 gstr1 = setBatch(arr1, section, cdnurList);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);
						
						idsList = new ArrayList<>();
						cdnurList = new ArrayList<>();
						itmsList = new ArrayList<>();
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
