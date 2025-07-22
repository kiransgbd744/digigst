package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.MonitorGstnGetIrnStatusEntity;


/**
 * @author Saif.S
 *
 */
@Repository("MonitorGstnGetIrnStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface MonitorGstnGetIrnStatusRepository
		extends CrudRepository<MonitorGstnGetIrnStatusEntity, Long>,
		JpaSpecificationExecutor<MonitorGstnGetIrnStatusEntity> {

	List<MonitorGstnGetIrnStatusEntity> findByReturnTypeAndIsBatchUpdatedFalse(
			String returnType);
	
	@Modifying
	@Query("UPDATE MonitorGstnGetIrnStatusEntity b SET b.isBatchUpdated =:isBatchUpdated,"
			+ " b.updatedOn =:updatedOn WHERE b.monitorId =:monitorId")
	int updateStatus(@Param("monitorId") Long monitorId,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("isBatchUpdated") boolean isBatchUpdated);
}
