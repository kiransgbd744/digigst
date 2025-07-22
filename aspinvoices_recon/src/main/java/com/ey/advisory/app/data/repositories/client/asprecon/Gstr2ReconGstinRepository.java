package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconGstinEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr2ReconGstinRepository")
@Transactional(value = "clientTransactionManager", 
propagation = Propagation.REQUIRED)
public interface Gstr2ReconGstinRepository
		extends JpaRepository<Gstr2ReconGstinEntity, Long>,
		JpaSpecificationExecutor<Gstr2ReconGstinEntity> {
	
	
	@Query("Select gstin from Gstr2ReconGstinEntity configEntity where"
			+ " configId =:configId")
	public List<String> findAllGstinsByConfigId( @Param("configId") Long configId);

	@Modifying
	@Query("update Gstr2ReconGstinEntity set status = :status where gstin = "
			+ ":gstin and autoReconDate = :autoReconDate")
	public void updateStatus(@Param("gstin") String gstin, 
			@Param("autoReconDate") LocalDate autoReconDate,
			@Param("status") String status);

	@Query("Select distinct status from Gstr2ReconGstinEntity  where"
			+ " configId =:configId ")
	public List<String> findByConfigId(@Param("configId") Long configId);
	
	@Modifying
	@Query("update Gstr2ReconGstinEntity set status = :status, "
			+ " completedOn = :completedOn where gstin = "
			+ ":gstin and configId = :configId")
	public void updateReconCompletedStatus(@Param("gstin") String gstin, 
			@Param("configId") Long configId,
			@Param("status") String status,
			@Param("completedOn") LocalDateTime completedOn);
	
	@Modifying
	@Query("update Gstr2ReconGstinEntity set status = :status, "
			+ " autoReconId = :autoReconId where gstin = "
			+ ":gstin and configId = :configId")
	public void updateAutoReconIdAndStatus(@Param("gstin") String gstin, 
			@Param("configId") Long configId,
			@Param("autoReconId") Long autoReconId,
			@Param("status") String status);
	
	@Query("Select count(*) from Gstr2ReconGstinEntity configEntity where"
			+ " configId =:configId AND gstin =:gstin AND status =:status")
	public int findCompletedStatusCount(@Param("configId") Long configId, 
			@Param("gstin") String gstin, @Param("status") String status);
	
	@Modifying
	@Query("update Gstr2ReconGstinEntity set status = :status, "
			+ " completedOn = :completedOn where status = 'RECON_INITIATED' "
			+ " and configId = :configId")
	public void updateStatusByConfigIdAndStatus( 
			@Param("completedOn") LocalDateTime completedOn,
			@Param("configId") Long configId,
			@Param("status") String status);

	@Query("select configEntity from Gstr2ReconGstinEntity configEntity where"
			+ " configId IN (:configId)")
	public List<Gstr2ReconGstinEntity> findAllGstinsByConfigIdIn(
			@Param("configId") List<Long> configId);
	
	@Modifying
	@Query("update Gstr2ReconGstinEntity set status = :status, "
			+ " completedOn = :completedOn, isDelta =FALSE where "
			+ " gstin IN (:gstin) and configId = :configId")
	public void updateReconCompletedStatusAndIsDeltaBulk(
			@Param("gstin") List<String> gstin,
			@Param("configId") Long configId, @Param("status") String status,
			@Param("completedOn") LocalDateTime completedOn);
	
	@Modifying
	@Query("update Gstr2ReconGstinEntity set fromDate =:fromDate "
			+ " where gstin =:gstin and configId =:configId")
	public void updateFromDateByGstinAndConfigId(
			@Param("gstin") String gstin,
			@Param("configId") Long configId,
			@Param("fromDate") LocalDateTime fromDate);

}