package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ey.advisory.app.data.entities.client.Get2BErpConfigRequestEntity;

import jakarta.transaction.Transactional;

@Transactional
public interface Get2BErpConfigRequestRepository 
					extends JpaRepository<Get2BErpConfigRequestEntity, Long>,
					JpaSpecificationExecutor<Get2BErpConfigRequestEntity> {
	
	
	public Get2BErpConfigRequestEntity findTop1ByStatusInAndReturnPeriodListAndGstinListOrderByRequestIdAsc(
			List<String> status,String returnPeriodList,String gstinList);
	
	@Modifying
	@Query("UPDATE Get2BErpConfigRequestEntity log SET "
			+ "log.createdBy = 'SYSTEM',log.status =:status, log.modifiedOn = CURRENT_TIMESTAMP  "
			+ "WHERE log.invocationId =:invocationId " )
	public void updateStatusByConfigId(@Param("invocationId") Long invocationId,
			@Param("status") String status);
	
	@Query("select e from Get2BErpConfigRequestEntity e WHERE "
			+ " e.status In (:status) ")
	public List<Get2BErpConfigRequestEntity> findAllActiveData(@Param("status") List<String> status);

}