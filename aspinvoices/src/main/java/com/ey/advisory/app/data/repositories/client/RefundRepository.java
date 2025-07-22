package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.RefundEntity;

import jakarta.transaction.Transactional;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("RefundRepository")
public interface RefundRepository
		extends JpaRepository<RefundEntity, Long>, 
		                         JpaSpecificationExecutor<RefundEntity> {

	@Transactional
	@Modifying
	@Query("UPDATE RefundEntity b SET b.isDelete = TRUE "
			+ "WHERE b.refundInvkey IN (:invKey) ")
	public void UpdateSameInvKey(@Param("invKey") String invKey);
	
	@Query("SELECT e from RefundEntity e where e.id = :id")
	public RefundEntity findId(@Param("id") Long id);


}
