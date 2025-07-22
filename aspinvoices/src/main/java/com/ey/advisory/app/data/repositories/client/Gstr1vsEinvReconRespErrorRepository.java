package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1vsEinvReconRespErrorEntity;

/**
 * 
 * @author Jithendra Kumar B
 *
 */
@Repository("Gstr1vsEinvReconRespErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1vsEinvReconRespErrorRepository
		extends JpaRepository<Gstr1vsEinvReconRespErrorEntity, Long>,
		JpaSpecificationExecutor<Gstr1vsEinvReconRespErrorEntity> {

}
