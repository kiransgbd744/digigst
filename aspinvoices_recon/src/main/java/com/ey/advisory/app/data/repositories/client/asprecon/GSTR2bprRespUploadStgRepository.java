package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr2bpr.reconresponse.upload.GSTR2bprRespUploadStgEntity;

/**
 * @author Jithendra.B
 *
 */
@Repository("GSTR2bprRespUploadStgRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GSTR2bprRespUploadStgRepository
		extends JpaRepository<GSTR2bprRespUploadStgEntity, Long>,
		JpaSpecificationExecutor<GSTR2bprRespUploadStgEntity> {

}
