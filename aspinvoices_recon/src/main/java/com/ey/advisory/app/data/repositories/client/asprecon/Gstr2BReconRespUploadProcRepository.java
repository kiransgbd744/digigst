package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2BReconRespUploadProcEntity;

/**
 * @author Akhilesh.Yadav
 *
 */

@Repository("Gstr2BReconRespUploadProcRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2BReconRespUploadProcRepository
		extends JpaRepository<Gstr2BReconRespUploadProcEntity, Integer>,
		JpaSpecificationExecutor<Gstr2BReconRespUploadProcEntity> {

	@Query("Select entity from Gstr2BReconRespUploadProcEntity entity WHERE"
			+ " isActive=TRUE")
	public List<Gstr2BReconRespUploadProcEntity> findAllActiveProc();

}