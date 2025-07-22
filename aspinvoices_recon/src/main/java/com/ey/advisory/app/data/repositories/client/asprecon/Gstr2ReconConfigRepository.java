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

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;

/**
 * 
 * @author vishal.verma
 *
 */

@Repository("Gstr2ReconConfigRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2ReconConfigRepository
		extends JpaRepository<Gstr2ReconConfigEntity, Long>,
		JpaSpecificationExecutor<Gstr2ReconConfigEntity> {

	@Modifying
	@Query("UPDATE Gstr2ReconConfigEntity SET status =:status,"
			+ " filePath =:filePath, completedOn =:completedOn"
			+ " where configId =:configId")
	public void updateReconConfigStatusAndReportName(
			@Param("status") String status, @Param("filePath") String filePath,
			@Param("completedOn") LocalDateTime completedOn,
			@Param("configId") Long configId);

	@Query("Select configEntity from Gstr2ReconConfigEntity configEntity where"
			+ " configId =:configId")
	public Gstr2ReconConfigEntity findByConfigId(
			@Param("configId") Long configId);

	@Query("Select configId from Gstr2ReconConfigEntity Where entityId "
			+ "=:entityId  AND fromTaxPeriodPR >=:fromTaxPeriodPR "
			+ "AND toTaxPeriodPR <=:toTaxPeriodPR AND status = "
			+ " 'REPORT_GENERATED' AND type = :type")
	public List<Long> findAllConfigId(@Param("entityId") Long entityId,
			@Param("toTaxPeriodPR") Integer toTaxPeriodPR,
			@Param("fromTaxPeriodPR") Integer fromTaxPeriodPR,
			@Param("type") String type);

	public Gstr2ReconConfigEntity findByEntityIdAndAutoReconDate(Long entityId,
			LocalDate now);

	@Modifying
	@Query("UPDATE Gstr2ReconConfigEntity SET status =:status "
			+ " where configId =:configId")
	public void updateReconConfigStatus(@Param("status") String status,
			@Param("configId") Long configId);

	@Query("Select configId from Gstr2ReconConfigEntity Where entityId "
			+ "=:entityId  AND fromTaxPeriodPR >=:fromTaxPeriodPR "
			+ "AND toTaxPeriodPR <=:toTaxPeriodPR AND status = "
			+ " 'REPORT_GENERATED' AND type = :type")
	public List<Long> findAllNonAPConfigId(@Param("entityId") Long entityId,
			@Param("toTaxPeriodPR") Integer toTaxPeriodPR,
			@Param("fromTaxPeriodPR") Integer fromTaxPeriodPR,
			@Param("type") String type);

	@Query("Select configId from Gstr2ReconConfigEntity Where entityId "
			+ "=:entityId  AND fromTaxPeriodPR =:fromTaxPeriodPR "
			+ "AND toTaxPeriodPR =:toTaxPeriodPR AND "
			+ "fromTaxPeriod2A =:fromTaxPeriod2A AND "
			+ "toTaxPeriod2A =:toTaxPeriod2A AND status = "
			+ " 'REPORT_GENERATED' AND type = 'AP_M_2APR' ")
	public List<Long> findAPConfigId(@Param("entityId") Long entityId,
			@Param("toTaxPeriodPR") Integer toTaxPeriodPR,
			@Param("fromTaxPeriodPR") Integer fromTaxPeriodPR,
			@Param("fromTaxPeriod2A") Integer fromTaxPeriod2A,
			@Param("toTaxPeriod2A") Integer toTaxPeriod2A);

	public List<Gstr2ReconConfigEntity> findByTypeIn(
			@Param("type") List<String> type);
	
	public List<Gstr2ReconConfigEntity> findByStatusIn(
			@Param("status") List<String> status);
	
	@Query("Select entityId from Gstr2ReconConfigEntity Where "
			+ " status = 'RECON_HALT' AND type in ('AP_M_2APR','2APR','AUTO_2APR','NON_AP_M_2APR') ")
	public List<Long> findReconHaultEntityId();
	
	public List<Gstr2ReconConfigEntity> findByAutoReconDateAndStatusIn(LocalDate now, List<String> status);
}