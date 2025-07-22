package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr6.Gstr6ComputeDigiConfigStatusEntity;

/**
 * @author Shashikant Shukla
 *
 */
@Repository("Gstr6ComputeDigiConfigStatusRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr6ComputeDigiConfigStatusRepository
		extends JpaRepository<Gstr6ComputeDigiConfigStatusEntity, Long>,
		JpaSpecificationExecutor<Gstr6ComputeDigiConfigStatusEntity> {

	Gstr6ComputeDigiConfigStatusEntity findByGstinAndTaxPeriodAndIsActiveTrue(
			String gstin, String taxPeriod);

	@Modifying
	@Query("UPDATE Gstr6ComputeDigiConfigStatusEntity g SET g.isActive = false,"
			+ "g.updatedOn = CURRENT_TIMESTAMP "
			+ "WHERE g.gstin = :gstin and g.isActive = true and g.taxPeriod=:taxPeriod")
	public int updateActiveExistingRecords(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod);

	Gstr6ComputeDigiConfigStatusEntity findByConfigId(Long ConfigId);

	@Modifying
	@Query("UPDATE Gstr6ComputeDigiConfigStatusEntity g SET g.status = :status,"
			+ "g.errorDesc = :errorDesc,g.updatedOn = CURRENT_TIMESTAMP "
			+ "WHERE g.configId = :configId")
	public int updateStatus(@Param("configId") Long configId,
			@Param("status") String status,
			@Param("errorDesc") String errorDesc);
	
    Gstr6ComputeDigiConfigStatusEntity findByGstinAndTaxPeriodAndStatusAndIsActiveTrue(String gstin, String taxPeriod, String status);

}
