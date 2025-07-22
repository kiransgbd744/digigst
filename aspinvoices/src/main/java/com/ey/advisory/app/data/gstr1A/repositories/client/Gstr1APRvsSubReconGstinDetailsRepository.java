package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1APRvsSubmReconGstinDetailEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr1APRvsSubReconGstinDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1APRvsSubReconGstinDetailsRepository
		extends JpaRepository<Gstr1APRvsSubmReconGstinDetailEntity, Long>,
		JpaSpecificationExecutor<Gstr1APRvsSubmReconGstinDetailEntity> {

	@Query("select b.gstin from Gstr1APRvsSubmReconGstinDetailEntity b"
			+ " Where b.reconConfigId =:reconConfigId")
	public List<String> findGstinsByReconConfigId(
			@Param("reconConfigId") Long reconConfigId);

	@Modifying
	@Query("UPDATE Gstr1APRvsSubmReconGstinDetailEntity b SET b.isActive = FALSE WHERE b.reconConfigId <>:configId")
	public void updateOldConfigIdsActiveStatus(
			@Param("configId") Long configId);

	@Modifying
	@Query("UPDATE Gstr1APRvsSubmReconGstinDetailEntity SET isActive = true WHERE reconConfigId =:configId")
	public void updatenewConfigIdActiveStatus(@Param("configId") Long configId);

}
