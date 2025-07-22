package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1EInvReconGstinDetailEntity;

/**
 * 
 * @author Rajesh N K
 *
 */
@Repository("Gstr1EInvReconGstinDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1EInvReconGstinDetailsRepository
		extends JpaRepository<Gstr1EInvReconGstinDetailEntity, Long>,
		JpaSpecificationExecutor<Gstr1EInvReconGstinDetailEntity> {

	@Query("select b.gstin from Gstr1EInvReconGstinDetailEntity b"
			+ " Where b.reconConfigId =:reconConfigId")
	public List<String> findGstinsByReconConfigId(
			@Param("reconConfigId") Long reconConfigId);

}
