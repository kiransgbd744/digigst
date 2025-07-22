package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1PRvsSubmReconGstinDetailEntity;

/**
 * 
 * @author Kiran
 *
 */
@Repository("Gstr1PRvsSubReconGstinDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1PRvsSubReconGstinDetailsRepository
		extends JpaRepository<Gstr1PRvsSubmReconGstinDetailEntity, Long>,
		JpaSpecificationExecutor<Gstr1PRvsSubmReconGstinDetailEntity> {

	@Query("select b.gstin from Gstr1PRvsSubmReconGstinDetailEntity b"
			+ " Where b.reconConfigId =:reconConfigId")
	public List<String> findGstinsByReconConfigId(
			@Param("reconConfigId") Long reconConfigId);

	@Modifying
	@Query("UPDATE Gstr1PRvsSubmReconGstinDetailEntity b SET b.isActive = FALSE WHERE b.reconConfigId <>:configId")
	public void updateOldConfigIdsActiveStatus(@Param("configId") Long configId);

	@Modifying
	@Query("UPDATE Gstr1PRvsSubmReconGstinDetailEntity SET isActive = true WHERE reconConfigId =:configId")
	public void updatenewConfigIdActiveStatus(@Param("configId") Long configId);
	
}
