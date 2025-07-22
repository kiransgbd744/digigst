package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.SetOffAndUtilEntity;

import jakarta.transaction.Transactional;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("SetOffUtilRepository")
public interface SetOffUtilRepository
		extends JpaRepository<SetOffAndUtilEntity, Long>, 
		                         JpaSpecificationExecutor<SetOffAndUtilEntity> {

	
	
	@Transactional
	@Modifying
	@Query("UPDATE SetOffAndUtilEntity b SET b.isDelete = TRUE "
			+ "WHERE b.setOffInvKey IN (:invKey) ")
	public void UpdateSameInvKey(@Param("invKey") String invKey);
	
	@Query("SELECT e from SetOffAndUtilEntity e where e.id = :id")
	public SetOffAndUtilEntity findId(@Param("id") Long id);

}
