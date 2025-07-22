/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.DocErrorsDto;
import com.ey.advisory.app.docs.dto.erp.OutwardErrorDocsDto;
import com.ey.advisory.app.docs.dto.erp.OutwardErrorItemDto;
import com.ey.advisory.app.docs.dto.erp.OutwardErrorItemsDto;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.ErrorMasterUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx1AspErrorDocsImpl")
@Slf4j
public class Anx1AspErrorDocsImpl implements Anx1AspErrorDocs {
	
	public static final String TRUE = "TRUE";
	public static final String FALSE = "FALSE";
	
	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;
	
	private OutwardErrorItemDto setItemDetail(Object[] arr1, String errorCode,
			String errDesc) {

		OutwardErrorItemDto itm = new OutwardErrorItemDto();

		itm.setItemNo(arr1[13] != null
				? Long.parseLong(String.valueOf(arr1[13])) : null);
		itm.setErrorField(arr1[14] != null ? String.valueOf(arr1[14]) : null);
		
		/*String errorCode = arr1[15] != null ? String.valueOf(arr1[15]) : null;*/
		/*if (errorCode != null && errorCode.length() > 1) {
			errorCode = errorCode.trim().substring(1);
		}*/
		//String isInward = arr1[7] != null ? String.valueOf(arr1[7]).toUpperCase() : null;
		
		itm.setErrorCode(errorCode);
		itm.setErrorDesc(errDesc);
		itm.setErrorType(arr1[17] != null ? String.valueOf(arr1[17]) : null);
		
		//	String gstnError = null;
			//Only if Invoice is processed then SAVE GSTN will come
			/*if(arr1[5] != null && TRUE.equalsIgnoreCase(String.valueOf(arr1[5]))) {
			 gstnError = arr1[6] != null ? String.valueOf(arr1[6]).toUpperCase() : null;
			 //GSTN error is true
			 if(gstnError != null && TRUE.equalsIgnoreCase(gstnError)) {
					itm.setErrorDesc(arr1[16] != null ? String.valueOf(arr1[16]) : null);
				}
			} else {
				//all ASP errors
				String errDesc = null;
				//String errCode = arr1[15] != null ? String.valueOf(arr1[15]) : null;
				if(errorCode != null && !errorCode.trim().isEmpty()){
					String[] errorCodes = errorCode.split(",");
					List<String> errCodeList = Arrays.asList(errorCodes);
					if(FALSE.equalsIgnoreCase(isInward)) {
						errDesc = ErrorMasterUtil.getErrorDesc(errCodeList, "OUTWARD");
					} else {
						errDesc = ErrorMasterUtil.getErrorDesc(errCodeList, "INWARD");
					}
					
				}
				if (errDesc != null && errDesc.length() > 1) {
					errDesc = errDesc.trim().substring(1);
				}
				itm.setErrorDesc(errDesc);
			}*/

		return itm;
	}
	
	private DocErrorsDto setInvData(Object[] arr1,
			List<OutwardErrorItemDto> itmsList, String accountNum,
			 String entityName, String entityPan) {
		DocErrorsDto err = new DocErrorsDto();
		err.setId(arr1[0] != null ? Long.parseLong(String.valueOf(arr1[0])) : null);
		err.setSgstin(arr1[1] != null ? String.valueOf(arr1[1]) : null);
		err.setDocNo(arr1[2] != null ? String.valueOf(arr1[2]) : null);
		err.setDocType(arr1[3] != null ? String.valueOf(arr1[3]).toUpperCase() : null);
		err.setDocDate(arr1[4] != null ? String.valueOf(arr1[4]) : null);
		err.setDocStatus(arr1[5] != null ? String.valueOf(arr1[5]).toUpperCase() : null);
	
		//Only if Invoice is processed then SAVE GSTN will come
		if(arr1[5] != null && TRUE.equalsIgnoreCase(String.valueOf(arr1[5]))) {
		err.setGstinError(arr1[6] != null ? String.valueOf(arr1[6]).toUpperCase() : null);
		}
		err.setInwdError(arr1[7] != null ? String.valueOf(arr1[7]).toUpperCase() : null);
		
		err.setCompanycode(
				arr1[8] != null ? String.valueOf(arr1[8]).toUpperCase() : null);

		String finYear = arr1[9] != null ? String.valueOf(arr1[9]) : "";
		if(finYear.length() == 6) {
			finYear = finYear.substring(0,4);
		}
		err.setFiscalyear(finYear);
		
		// Dynamically populating accountNum based on the existance
		// onboarding/outward table
		
		if (arr1[10] != null ) {
			err.setAccountVoucherNo(arr1[10] != null
					? String.valueOf(arr1[10]).toUpperCase() : null);
		} else {
			err.setAccountVoucherNo(
					accountNum != null ? accountNum.toUpperCase() : null);
		}  
		err.setEntityName(entityName);
		err.setEntityPan(entityPan);
		err.setPayloadId(arr1[11] != null ? String.valueOf(arr1[11]).toUpperCase() : null);
		err.setReceivedDate(arr1[12] != null ? String.valueOf(arr1[12]).toUpperCase().replace("-", "") : null);
		OutwardErrorItemsDto outwardErrorItemsDto = new OutwardErrorItemsDto();
		outwardErrorItemsDto.setErrors(itmsList);
		err.setErrors(outwardErrorItemsDto);
		
		return err;
		
	}
	
	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2) {
		Long id = arr1[0] != null ? Long.parseLong(String.valueOf(arr1[0])) : null;
		Long id2 = arr2[0] != null ? Long.parseLong(String.valueOf(arr2[0])) : null;
		return !id.equals(id2)
				|| isNewBatch(idsList, totSize, counter2);
	}
	
	private boolean isNewBatch(List<Long> idsList,
			int totSize, int counter2) {
		
		return idsList.size() >= chunkSizeFetcher.getSize()
				|| counter2 == totSize;
	}
	
	
	
	
	@Override
	public List<Pair<OutwardErrorDocsDto, List<Long>>> convertDocsAsDtosByChunking(
			List<Object[]> objs, String accountNum, 
			String entityName, String entityPan) {

		List<DocErrorsDto> invList = new ArrayList<>();
		List<OutwardErrorItemDto> itmsList = new ArrayList<>();
		List<Long> idsList = new ArrayList<>();
		List<Pair<OutwardErrorDocsDto, List<Long>>> list = new ArrayList<>();
		int totSize = objs.size();
		for (int counter = 0; counter < totSize; counter++) {

			Object[] arr1 = objs.get(counter);
			// Reading next object[] for the forming the json.
			Object[] arr2 = objs.get(counter);
			int counter2 = counter + 1;
			/**
			 * Reading the next doc if exist.
			 */
			if (counter2 < objs.size()) {
				arr2 = objs.get(counter2);
			}
			
			
			// OutwardErrorItemDto child = new OutwardErrorItemDto();
			String gstnError = arr1[6] != null
					? String.valueOf(arr1[6]).toUpperCase() : null;
			String isInward = arr1[7] != null
					? String.valueOf(arr1[7]).toUpperCase() : null;
			String errorCode = arr1[15] != null ? String.valueOf(arr1[15])
					: null;
			String errDesc = null;
			
			List<String> errCodeList = null;
			if (errorCode != null && !errorCode.trim().isEmpty()) {
				String[] errorCodes = errorCode.split(",");
				errCodeList = Arrays.asList(errorCodes);

			}
			 //GSTN error is true
			if (gstnError != null && TRUE.equalsIgnoreCase(gstnError)) {
				errDesc = arr1[16] != null ? String.valueOf(arr1[16]) : null;
			} else {
				// all ASP errors
				if (FALSE.equalsIgnoreCase(isInward)) {
					errDesc = ErrorMasterUtil.getErrorDesc(errCodeList,
							"OUTWARD");
				} else {
					errDesc = ErrorMasterUtil.getErrorDesc(errCodeList,
							"INWARD");
				}
			}
			
			List<String> errDescList = null;
			if (errDesc != null && !errDesc.trim().isEmpty()) {
				String[] errDescs = errDesc.split(",");
				errDescList = Arrays.asList(errDescs);
			}
			
			for (int i = 0; i < errCodeList.size(); i++) {
				OutwardErrorItemDto child = setItemDetail(arr1,
						errCodeList.get(i), errDescList.get(i));
				itmsList.add(child);
			}
			
			
			if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
				/**
				 * This ids are used to update the Gstr1_doc_header table as a
				 * single/same batch.
				 */
				idsList.add(Long.parseLong(String.valueOf(arr1[0])));
				DocErrorsDto inv = setInvData(arr1, itmsList, accountNum,
						 entityName, entityPan);
				invList.add(inv);
				itmsList = new ArrayList<>();
			}

			/*
			 * child.setDocumentNumber( obj[1] != null ? String.valueOf(obj[1])
			 * : null);
			 * 
			 * child.setCreatedBy(obj[8] != null ? String.valueOf(obj[8]) :
			 * null); child.setCreatedOn(obj[9] != null ?
			 * String.valueOf(obj[9]).substring(0, 8) : null);
			 * child.setModifiedBy( obj[10] != null ? String.valueOf(obj[10]) :
			 * null); child.setModifiedOn( obj[11] != null ?
			 * String.valueOf(obj[11]).substring(0, 8) : null);
			 * child.setValType(obj[12] != null ? String.valueOf(obj[12]) :
			 * null); int pos = objs.indexOf(obj) ;
			 */
			if (isNewBatch(idsList, totSize, counter2)) {

				OutwardErrorDocsDto dto = new OutwardErrorDocsDto();
				dto.setImData(invList);
				list.add(new Pair<>(dto, idsList));
				invList = new ArrayList<>();
				itmsList = new ArrayList<>();
				idsList = new ArrayList<>();
			}

		}

		/*
		 * if (!idsList.isEmpty()) { OutwardErrorDocsDto dto = new
		 * OutwardErrorDocsDto(); dto.setImData(itmsList); list.add(new
		 * Pair<>(dto, idsList)); }
		 */

		return list;
	}
	

	/*@Override
	public Integer pushToErp(OutwardErrorDocsDto dto,
			String destinationName, String dataType, AnxErpBatchEntity batch) {

		try {
			String xml = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JAXBContext context = JAXBContext
					.newInstance(OutwardErrorDocsDto.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dto, out);
			xml = out.toString();

			if (xml != null && xml.length() > 0) {
				xml = xml.substring(xml.indexOf('\n') + 1);
			}

		String header = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'> <soapenv:Header/> <soapenv:Body> <urn:_-digigst_-saveDocResp>" ;
		String footer = "</urn:_-digigst_-saveDocResp> </soapenv:Body> </soapenv:Envelope>" ;

			if(APIConstants.OUTWARD.equals(dataType)) {
				 header = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'><soapenv:Header/><soapenv:Body><urn:ZreadErrorOtw>";
				 footer = "</urn:ZreadErrorOtw></soapenv:Body></soapenv:Envelope>";
			} else if(APIConstants.INWARD.equals(dataType)) {
				 header = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'><soapenv:Header/><soapenv:Body><urn:ZreadErrorInw>";
				 footer = "</urn:ZreadErrorInw></soapenv:Body></soapenv:Envelope>";
			}
			//final payload using header and footer.
			if (xml != null) {
				xml = header + xml + footer;
			}

			if (xml != null && destinationName != null) {
				return destinationConn.post(destinationName, xml, batch);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}*/
}
	
