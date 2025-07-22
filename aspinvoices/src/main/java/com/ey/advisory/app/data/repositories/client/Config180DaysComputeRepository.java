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

import com.ey.advisory.app.data.entities.client.Config180DaysComputeEntity;

/**
 * @author Hema G M
 *
 */

@Repository("Config180DaysComputeRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)

public interface Config180DaysComputeRepository
		extends JpaRepository<Config180DaysComputeEntity, Long>,
		JpaSpecificationExecutor<Config180DaysComputeEntity> {

	@Modifying
	@Query("UPDATE Config180DaysComputeEntity SET status =:status "
			+ " where computeId =:computeId")
	public void updateComputeStatus(@Param("status") String status,
			@Param("computeId") Long computeId);

	@Query("Select configEntity from Config180DaysComputeEntity configEntity"
			+ " where computeId =:computeId")
	public Config180DaysComputeEntity findByComputeId(
			@Param("computeId") Long computeId);
	
	@Query("Select e from Config180DaysComputeEntity e where createdBy "
			+ " =:createdBy AND isActive = true AND entityId =:entityId")
	public List<Config180DaysComputeEntity> findByCreatedByAndEntityIdAndIsActiveTrue(
			@Param("createdBy") String createdBy,
			@Param("entityId") Long entityId);
	
	@Modifying
	@Query("UPDATE Config180DaysComputeEntity SET isActive =false")
	public void updateIsActiveFalse();
	
	@Query("Select e from Config180DaysComputeEntity e where "
			+ "isActive = true AND entityId =:entityId")
	public List<Config180DaysComputeEntity> findByEntityIdAndIsActiveTrue(
			@Param("entityId") Long entityId);

}
