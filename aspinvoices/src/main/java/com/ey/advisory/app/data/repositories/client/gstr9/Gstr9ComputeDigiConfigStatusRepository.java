package com.ey.advisory.app.data.repositories.client.gstr9;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeDigiConfigStatusEntity;

/**
 * @author Shashikant Shukla
 *
 */
@Repository("Gstr9ComputeDigiConfigStatusRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr9ComputeDigiConfigStatusRepository
		extends JpaRepository<Gstr9ComputeDigiConfigStatusEntity, Long>,
		JpaSpecificationExecutor<Gstr9ComputeDigiConfigStatusEntity> {

	Gstr9ComputeDigiConfigStatusEntity findByGstinAndFyAndIsDeleteFalse(
			String gstin, int fy);

	@Modifying
	@Query("UPDATE Gstr9ComputeDigiConfigStatusEntity g SET g.isDelete = true,"
			+ "g.updatedOn = CURRENT_TIMESTAMP "
			+ "WHERE g.gstin = :gstin and g.isDelete = false and g.fy=:fy")
	public int updateActiveExistingRecords(@Param("gstin") String gstin,
			@Param("fy") Integer fy);

	Gstr9ComputeDigiConfigStatusEntity findByConfigId(Long ConfigId);

	@Modifying
	@Query("UPDATE Gstr9ComputeDigiConfigStatusEntity g SET g.status = :status,"
			+ "g.errorDesc = :errorDesc,g.updatedOn = CURRENT_TIMESTAMP "
			+ "WHERE g.configId = :configId")
	public int updateStatus(@Param("configId") Long configId,
			@Param("status") String status,
			@Param("errorDesc") String errorDesc);

	List<Gstr9ComputeDigiConfigStatusEntity> findByGstinInAndFyAndIsDeleteFalse(
			List<String> gstnsLists, Integer fy);

}
