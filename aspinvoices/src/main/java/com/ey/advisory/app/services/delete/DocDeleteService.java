package com.ey.advisory.app.services.delete;

import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.docs.dto.DocDeleteReqDto;

/**
 * This interface is responsible to provide methods to soft delete documents
 * from ASP
 * 
 * @author Mohana.Dasari
 *
 */
public interface DocDeleteService {
	
	/**
	 * This method is responsible for soft deleting documents from ASP
	 * @param docs
	 * @return
	 */
	public void deleteDocuments(DocDeleteReqDto deleteReq);  
	
	public void deleteFile(Long fileId);
	
	public int deleteInvoices(DocDeleteReqDto deleteReq);
	
	public List<InwardTransDocument> getInvoices(DocDeleteReqDto deleteReq);

}
