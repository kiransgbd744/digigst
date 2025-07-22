package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.asprecon.gstr2.ap.recon.Gstr2ReconGstinTaxPeriodEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr2ReconGstinTaxPeriodRepository")
@Transactional(value = "clientTransactionManager", 
propagation = Propagation.REQUIRED)
public interface Gstr2ReconGstinTaxPeriodRepository
		extends JpaRepository<Gstr2ReconGstinTaxPeriodEntity, Long>,
		JpaSpecificationExecutor<Gstr2ReconGstinTaxPeriodEntity> {
	
	
	@Query("Select gstin from Gstr2ReconGstinTaxPeriodEntity configEntity where"
			+ " configId =:configId")
	public List<String> findAllGstinsByConfigId( @Param("configId") Long configId);

	@Modifying
	@Query("update Gstr2ReconGstinTaxPeriodEntity set status = :status where gstin = "
			+ ":gstin and autoReconDate = :autoReconDate")
	public void updateStatus(@Param("gstin") String gstin, 
			@Param("autoReconDate") LocalDate autoReconDate,
			@Param("status") String status);

	@Query("Select status from Gstr2ReconGstinTaxPeriodEntity  where"
			+ " configId =:configId and status in (:reconStatus) ")
	public List<String> findByConfigIdAndStatusIn(@Param("configId") Long configId,
			@Param("reconStatus") List<String> reconStatus);
	
	@Modifying
	@Query("update Gstr2ReconGstinTaxPeriodEntity set status = :status, "
			+ " completedOn = :completedOn where gstin = "
			+ ":gstin and autoReconDate = :autoReconDate")
	public void updateReconCompletedStatus(@Param("gstin") String gstin, 
			@Param("autoReconDate") LocalDate autoReconDate,
			@Param("status") String status,
			@Param("completedOn") LocalDate completedOn);
}