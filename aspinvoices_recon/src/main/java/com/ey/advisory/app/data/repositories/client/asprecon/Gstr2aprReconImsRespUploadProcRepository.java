package com.ey.advisory.app.data.repositories.client.asprecon;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2aprReconImsRespUploadProcEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr2aprReconImsRespUploadProcRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2aprReconImsRespUploadProcRepository
		extends JpaRepository<Gstr2aprReconImsRespUploadProcEntity, Integer>,
		JpaSpecificationExecutor<Gstr2aprReconImsRespUploadProcEntity> {

	@Query("Select entity from Gstr2aprReconImsRespUploadProcEntity entity WHERE"
			+ " isActive=TRUE")
	public List<Gstr2aprReconImsRespUploadProcEntity> findAllActiveProc();

}