/*package com.ey.advisory.app.data.repositories.client;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.RefundGstnEntity;

*//**
 * 
 * @author Mahesh.Golla
 *
 *//*
@Service("RefundGstnTable4Repository")
public interface RefundGstnTable4Repository
		extends JpaRepository<RefundGstnEntity, Long>,
		JpaSpecificationExecutor<RefundGstnEntity> {
	
	
	@Transactional
	@Modifying
	@Query("UPDATE RefundGstnEntity b SET b.isDelete = TRUE "
			+ "WHERE b.refundGstnkey IN (:gstnKey) ")
	public void UpdateSameGstnKey(@Param("gstnKey") String gstnKey);

}
*/