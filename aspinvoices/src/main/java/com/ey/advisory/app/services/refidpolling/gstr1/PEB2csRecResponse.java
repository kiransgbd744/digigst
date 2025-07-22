package com.ey.advisory.app.services.refidpolling.gstr1;
/**
 * 
 *//*
package com.ey.advisory.app.services.gstr1refidpolling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.Gstr1SaveBatchEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.docs.dto.B2CSInvoices;
import com.ey.advisory.app.docs.dto.SaveGSTR1;
import com.ey.advisory.app.services.common.DocKeyGenerator;
import com.ey.advisory.app.services.common.DocumentTypeMapperFactory;
import com.ey.advisory.common.GsonLocalDateConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

*//**
 * @author Siva.Nandam
 *
 *//*
public class PEB2csRecResponse implements ResponseHandler {

	
		@Autowired
		@Qualifier("CdnrDefaultDocumentTypeFactoryImpl")
		private DocumentTypeMapperFactory cdnur;
		@Autowired
		@Qualifier("DocKeyGenerator")
		private DocKeyGenerator docKeyGenerator;
		public  Pair<List<String>,Map<String, OutwardTransDocError>> 
		                    response(JsonElement root, Gstr1SaveBatchEntity batch) {
			Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
					new GsonLocalDateConverter()).create();
			List<String> invKeyList = new ArrayList<String>();
		
			SaveGSTR1 gstr1 = gson.fromJson(root, SaveGSTR1.class);
			Map<String, OutwardTransDocError> docErrorMap = new HashMap<>();
			for (B2CSInvoices invoice : gstr1.getB2csInvoice()) {
					String docNo = invoice.get;
					
					 * LocalDate docDate = LocalDate
					 * .parse(docData.getInvoiceDate());
					 

					String idt = invoice.getInvDate();
					String[] arr = idt.split("-");
					
					  LocalDate docDate = deserialize( arr[2] + "-" + arr[1] + "-"
					  + arr[0]);
					 
					  String digiGstDocType = null;
					// Since the Doc Type that GSTN returns is different from the
					// doc type that we maintain in the DB, we need to convert the
					// GSTN doc type to the DigiGST doc type.
					 digiGstDocType=  cdnur.mapDocType(invoice.getCredDebRefVoucher());
					// Now that we have the SGSTIN, DocNo, DocDate and the DigiGST
						// Doc Type, we can create the doc key.
						String invKey = docKeyGenerator.generateKey(
								batch.getSgstin(), docNo, docDate, digiGstDocType);
						invKeyList.add(invKey);

						// At this point of creation, we will not have the 
						// document header id with us. We only have the document key.
						// At a later point, we will use these keys to fetch the ids
						// of these documents. In order to know which error belongs
						// to which document, we need to  map this error object
						// to the corresponding document key. 
						OutwardTransDocError errObj = new OutwardTransDocError();
						errObj.setErrorCode(invoice.getError_cd());
						errObj.setErrorType("GSTIN");
						errObj.setSource("GSTIN");
						errObj.setErrorDesc(invoice.getError_msg());
						errObj.setCreatedBy("SYSTEM");
						errObj.setCreatedDate(LocalDateTime.now());
						//errorDocList.add(errObj);
						
						// Map the document key with the associated error, so that
						// after fetching the actual document ids for these documents,
						// we can update these error objects with the ids.
						docErrorMap.put(invKey, errObj);
		
				
			}
			return new Pair<List<String>,Map<String, OutwardTransDocError>>(
					                                   invKeyList, docErrorMap); 
			
		}
		public LocalDate deserialize(String date) throws JsonParseException {
			return LocalDate.parse(date);}	

	}
*/