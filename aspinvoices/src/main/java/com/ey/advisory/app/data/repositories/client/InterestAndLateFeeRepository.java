package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.InterestAndLateFeeEntity;

import jakarta.transaction.Transactional;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("InterestAndLateFeeRepository")
public interface InterestAndLateFeeRepository
		extends JpaRepository<InterestAndLateFeeEntity, Long>, 
		                         JpaSpecificationExecutor<InterestAndLateFeeEntity> {

	/**
	 * @param Table 4
	 */
	
	@Transactional
	@Modifying
	@Query("UPDATE InterestAndLateFeeEntity b SET b.isDelete = TRUE "
			+ "WHERE b.interestInvKey IN (:invKey) ")
	public void UpdateSameInvKey(@Param("invKey") String invKey);
	
	
	@Query("SELECT e from InterestAndLateFeeEntity e where e.id = :id")
	public InterestAndLateFeeEntity findId(@Param("id") Long id);


}
