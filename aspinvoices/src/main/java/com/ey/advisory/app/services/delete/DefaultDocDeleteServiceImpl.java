package com.ey.advisory.app.services.delete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.Anx2InwardErrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.DocDeleteReqDto;
import com.ey.advisory.app.services.docs.gstr2.DefaultInwardDocSave240Service;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("DefaultDocDeleteServiceImpl")
public class DefaultDocDeleteServiceImpl implements DocDeleteService {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransRepository;

	@Autowired
	@Qualifier("FileStatusRepository")
	private FileStatusRepository fileStatusRepo;

	@Autowired
	@Qualifier("Anx2InwardErrHeaderRepository")
	private Anx2InwardErrHeaderRepository inwardErrHeaderRepo;
	
	@Autowired
	@Qualifier("DefaultInwardDocSave240Service")
	private DefaultInwardDocSave240Service defaultInwardDocSave240Service;

	@Override
	public void deleteDocuments(DocDeleteReqDto deleteReq) {
		List<Long> docIds = deleteReq.getDocIds();
		if (!docIds.isEmpty()) {
			LocalDateTime updatedDate = LocalDateTime.now();
			// docRepository.updateDocDeletion(docIds,updatedDate);
		}
	}

	@Override
	@Transactional(value = "clientTransactionManager")
	public void deleteFile(Long fileId) {

		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to soft delete data from inward header for file id: {}",
						fileId.toString());
			}
			List<InwardTransDocument> docList = inwardTransRepository.findByAcceptanceIdAndIsDeletedFalse(fileId);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" active records counts:{} found for file id: {}",docList.size(),
						fileId.toString());
			}
			Set<String> docKeySet=new HashSet<>();
			defaultInwardDocSave240Service.softDeleteTheLockedRecord(docList,docKeySet,true);
			defaultInwardDocSave240Service.softDeleteTheGstr2BLockedRecord(docList,docKeySet,true);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" locked records count:{} found for file id: {}",docList.size(),
						fileId.toString());
			}
			docList.parallelStream().forEach(entity->{
				if(!docKeySet.contains(entity.getDocKey())){
					entity.setDeleted(true);
					entity.setModifiedBy(userName);
					entity.setUpdatedDate(LocalDateTime.now());
					entity.setInactiveReason("Invoice deleted from File Status");
				}
			});
			inwardTransRepository.saveAll(docList);
//			inwardTransRepository.deleteFile(fileId, userName, 
//					LocalDateTime.now(), "Invoice deleted from File Status"); 

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to soft delete data from inward error header for file id: {}",
						fileId.toString());
			}

			inwardErrHeaderRepo.deleteInwardErrorFile(fileId, userName, LocalDateTime.now());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"About to update filestatus for file id: %s",
						fileId.toString());
				LOGGER.debug(msg);
			}
             String fileStatus="Deleted";
             if(!docKeySet.isEmpty()){
            	 fileStatus="Processed";
             } 
             
             if(!docKeySet.isEmpty() && docList.size()!=docKeySet.size()){
            	 fileStatus="Partially Deleted";
             } 
			fileStatusRepo.updateFileStatus(fileId, fileStatus, userName, LocalDateTime.now());

		} catch (Exception e) {
			String msg = "Exception ocured while deleting data from file status inward";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		}

	}

	@Override
	public int deleteInvoices(DocDeleteReqDto deleteReq) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"About to delete records from invoice management screen for docNums: %s",
						deleteReq.toString());
				LOGGER.debug(msg);
			}
			
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			return inwardTransRepository.updateInwardDocDeletion(deleteReq.getDocIds(),
					userName, LocalDateTime.now(), "Invoice deleted from Invoice Management");
			
		} catch (Exception e) {
			String msg = "Exception ocured while deleting data from invoice management inward";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		}

	}

	@Override
	public List<InwardTransDocument> getInvoices(DocDeleteReqDto deleteReq) {
		
		return inwardTransRepository.getInwardDocDetails(deleteReq.getDocIds());
	}

}
