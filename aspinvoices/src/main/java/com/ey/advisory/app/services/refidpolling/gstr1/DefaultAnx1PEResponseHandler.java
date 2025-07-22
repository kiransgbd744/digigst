package com.ey.advisory.app.services.refidpolling.gstr1;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.repositories.client.DocErrorRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.common.DocKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * @author Siva.Nandam
 *
 */
@Component("DefaultAnx1PEResponseHandler")
public class DefaultAnx1PEResponseHandler implements Anx1GstnResponseHandler {
	
	@Autowired
	@Qualifier("Anx1PEB2bResponserHandler")
	private Anx1ResponseHandler b2bResponserHandler;
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository outwardTransDocumentRepository;
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;
	
	@Autowired
	@Qualifier("DocErrorRepository")
	private DocErrorRepository docErrorRepository;
	
	@Autowired
	@Qualifier("DocKeyGenerator")
	private DocKeyGenerator docKeyGenerator;

	@Autowired
	@Qualifier("Anx1PESezWpResponseHandler")
	private Anx1ResponseHandler sezwpResponseHandler;
	
	@Autowired
	@Qualifier("Anx1PESezWopResponseHandler")
	private Anx1ResponseHandler sezwopResponseHandler;
	
	@Autowired
	@Qualifier("Anx1PeExpWpResponseHandler")
	private Anx1ResponseHandler expResponseHandler;
	
	@Autowired
	@Qualifier("Anx1PeExpWopResponseHandler")
	private Anx1ResponseHandler expwopResponseHandler;
	
	/*@Autowired
	@Qualifier("Anx1PeDeResponseHandler")
	private Anx1ResponseHandler deResponseHandler;*/
	
	@Autowired
	@Qualifier("Anx1PeDeResponseHandler")
	private Anx1PeDeResponseHandler deResponseHandler;
	
	@Autowired
	@Qualifier("Anx1PeMisResponseHandler")
	private Anx1ResponseHandler misResponseHandler;
	
	private static final List<String> SECTIONS = ImmutableList.of(
			APIConstants.B2B, APIConstants.B2BA, APIConstants.SEZWOP,
			APIConstants.SEZWP, APIConstants.EXPWP, APIConstants.EXPWOP,
			APIConstants.MIS, APIConstants.DE);
	public static final String PE = "PE";
	
	@Override
	public void handleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {

		List<OutwardTransDocError> errorDocList = new ArrayList<>();
		//Pair<List<String>, Map<String, OutwardTransDocError>> secResponse = null;
		Triplet<List<String>, Map<String, OutwardTransDocError>,Integer> secResponse=null;
		Integer n = Integer.valueOf(0);
		if (batch.getSection() != null) {
			
			if(!SECTIONS.contains(batch.getSection().toLowerCase())){

				String status = PE;
				LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				// Finally, mark the Batch itself with the status of 'PE'
				batchSaveStatusRepository
						.updateGstr1SaveBatchbyBatchId(batch.getId(), status,n, now);
			}
			if(SECTIONS.contains(batch.getSection().toLowerCase())){
			if (batch.getSection().equalsIgnoreCase(APIConstants.B2B)) {
				secResponse = b2bResponserHandler.response(root, batch,n);
			}
			if (batch.getSection().equalsIgnoreCase(APIConstants.SEZWP)) {
				secResponse = sezwpResponseHandler.response(root, batch,n);
			}
			if (batch.getSection().equalsIgnoreCase(APIConstants.SEZWOP)) {
				secResponse = sezwopResponseHandler.response(root, batch,n);
			}
			if (batch.getSection().equalsIgnoreCase(APIConstants.EXPWP)) {
				secResponse = expResponseHandler.response(root, batch,n);
			}
			if (batch.getSection().equalsIgnoreCase(APIConstants.EXPWOP)) {
				secResponse = expwopResponseHandler.response(root, batch,n);
			}
			
			if (batch.getSection().equalsIgnoreCase(APIConstants.DE)) {
				secResponse = deResponseHandler.response(root, batch,n);
			}
			
			if (batch.getSection().equalsIgnoreCase(APIConstants.MIS)) {
				secResponse = misResponseHandler.response(root, batch,n);
			}
		
		List<String> invKeyList = secResponse.getValue0();

		Map<String, OutwardTransDocError> docErrorMap = secResponse.getValue1();
		int errorCount= docErrorMap.size();
		// Get the list of document id/doc key pairs for which we need to
		// store the errors. This is done to get all the required doc ids/keys
		// in a single stretch.
		List<Object[]> docIds = null; //temeperarly commentted
		/*outwardTransDocumentRepository
				.findDocIdsForBatchByDocKeys(batch.getId(), invKeyList);*/
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		// Set is error to true for all the documents that failed. (i.e that
		// are present in the invoice key list.
		
		//This is commented as  per GSTR1 this should get update as part of saveAll()
		/*outwardTransDocumentRepository.markDocsAsErrorsForBatch(batch.getId(),
				invKeyList, now);*/

		// Mark the successful documents with 'isSavedToGstin' as true.
		outwardTransDocumentRepository.markDocsAsSavedForBatchByErroredDocKeys(
				batch.getId(), invKeyList, now);

		// Attach the errors with the corresponding document header ids.
		// The docIds array will contain an object array where first element
		// is the document header id and the second element is the doc key.

		docIds.stream().forEach(arr -> {
			// Using the invoice key, get the error object
			OutwardTransDocError error = docErrorMap.get(arr[1]);
			// Set the document header id to the error object.
			error.setDocHeaderId((Long) arr[0]);
			errorDocList.add(error);
		});
		docErrorRepository.saveAll(errorDocList);
		// Now save the list of errors to the DB. Now, this error doc list will
		// have the doc ids populated.

		String status = PE;
		// Finally, mark the Batch itself with the status of 'PE'
		batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(),
				status,errorCount, now);
		}
	}

}
	public LocalDate deserialize(String date) throws JsonParseException {
		return LocalDate.parse(date);
	}

}
