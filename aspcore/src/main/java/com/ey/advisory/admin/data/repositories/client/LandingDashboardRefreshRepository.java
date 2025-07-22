package com.ey.advisory.admin.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.LandingDashboardBatchRefreshEntity;

/**
 * @author Sakshi.jain
 *
 */
@Repository("LandingDashboardRefreshRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface LandingDashboardRefreshRepository
		extends CrudRepository<LandingDashboardBatchRefreshEntity, Long> {
	
	@Modifying
	@Query("UPDATE LandingDashboardBatchRefreshEntity SET status =:status, "
			+ " refreshTime =:updatedOn ,updatedOn =:updatedOn WHERE batchId =:id ")
	public void updateBatchStatus(@Param("id") Long id,
			@Param("status") String status,
			@Param("updatedOn") LocalDateTime modifiedOn);
	

	@Modifying
	@Query("UPDATE LandingDashboardBatchRefreshEntity SET status =:status, isdelete = false, "
			+ " refreshTime =:updatedOn ,updatedOn =:updatedOn WHERE batchId =:id ")
	public void updateSuccessBatchStatus(@Param("id") Long id,
			@Param("status") String status,
			@Param("updatedOn") LocalDateTime modifiedOn);
	
	@Modifying
	@Query("UPDATE LandingDashboardBatchRefreshEntity SET  isdelete = true "
			+ " where derRetPeriod =:taxPeriod and isdelete = false and entityId =:entityId ")
	public void softlyDeleteBatchIds(@Param("taxPeriod") String taxPeriod, @Param("entityId") Long entityId);
	
	
	@Query(value = " Select top 1 * from TBL_LD_BATCH_REFRESH where status = 'COMPLETED' "
			+" and DERIVED_RET_PERIOD =:taxPeriod and IS_DELETE = false and "
			+ " ENTITY_ID =:entityId order by BATCH_ID desc",nativeQuery = true )
	public LandingDashboardBatchRefreshEntity getCompletedLatestBatchId
	( @Param("taxPeriod") String taxPeriod, @Param("entityId") Long entityId );
	

	@Query(value = " Select top 1 * from TBL_LD_BATCH_REFRESH where "
			+" DERIVED_RET_PERIOD =:taxPeriod and "
			+ " ENTITY_ID =:entityId order by BATCH_ID desc",nativeQuery = true )
	public LandingDashboardBatchRefreshEntity getLatestBatchId
	( @Param("taxPeriod") String taxPeriod, @Param("entityId") Long entityId );
	
	@Query("select derRetPeriod from LandingDashboardBatchRefreshEntity where isdelete = false "
			+ " and entityId =:entityId ")
	public List<String> getTaxPeriodHavingBatchIds(@Param("entityId") Long entityId);
	
	

	}
