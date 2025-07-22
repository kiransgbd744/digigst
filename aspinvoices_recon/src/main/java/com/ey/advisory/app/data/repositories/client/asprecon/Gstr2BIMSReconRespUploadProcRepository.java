package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2BIMSReconRespUploadProcEntity;

/**
 * @author Sakshi.jain
 *
 */

@Repository("Gstr2BIMSReconRespUploadProcRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2BIMSReconRespUploadProcRepository
		extends JpaRepository<Gstr2BIMSReconRespUploadProcEntity, Integer>,
		JpaSpecificationExecutor<Gstr2BIMSReconRespUploadProcEntity> {

	@Query("Select entity from Gstr2BIMSReconRespUploadProcEntity entity WHERE"
			+ " isActive=TRUE")
	public List<Gstr2BIMSReconRespUploadProcEntity> findAllActiveProc();

}