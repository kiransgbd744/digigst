package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.glrecon.GlReconSrFileUploadEntity;

/**
 * @author Kiran s
 *
 */

@Repository("GlReconSrFileUploadRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface GlReconSrFileUploadRepository
		extends JpaRepository<GlReconSrFileUploadEntity, Long>,
		JpaSpecificationExecutor<GlReconSrFileUploadEntity> {

	@Modifying
	@Query("UPDATE GlReconSrFileUploadEntity e SET e.reqId = :reqId, e.uploadedDocName = :uploadedDocName, e.uploadedDocNumber = :uploadedDocNumber WHERE e.reqId = :reqId")
	public void updateGlReconSrFileUploadEntity(@Param("reqId") Long reqId,
			@Param("uploadedDocName") String uploadedDocName,
			@Param("uploadedDocNumber") String uploadedDocNumber);
	
	List<GlReconSrFileUploadEntity> findByReqId(Long reqId);

}