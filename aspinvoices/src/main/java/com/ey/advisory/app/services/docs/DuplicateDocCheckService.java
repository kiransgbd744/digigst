package com.ey.advisory.app.services.docs;

import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.DuplicateDocCheckDto;
import com.ey.advisory.app.docs.dto.DuplicateInwardDocCheckDto;

public interface DuplicateDocCheckService {

	DuplicateDocCheckDto checkDuplicateDocuments(
			List<OutwardTransDocument> docs);

	DuplicateInwardDocCheckDto checkInwardDuplicateDocuments(
			List<InwardTransDocument> docs,
			DocKeyGenerator<InwardTransDocument, String> docKeyGen,
			InwardTransDocRepository docHeaderRepository);
	
	void softDeleteDuplicateDocuments(List<OutwardTransDocument> docs);
	
	void softDeleteDuplicateDocsInward(List<InwardTransDocument> docs);

}
