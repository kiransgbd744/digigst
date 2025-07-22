package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Ret1And1AEntity;

import jakarta.transaction.Transactional;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Ret1And1ARepository")
public interface Ret1And1ARepository
		extends JpaRepository<Ret1And1AEntity, Long>, 
		                         JpaSpecificationExecutor<Ret1And1AEntity> {

	@Transactional
	@Modifying
	@Query("UPDATE Ret1And1AEntity b SET b.isDelete = TRUE "
			+ "WHERE b.retInvKey IN (:invKey) ")
	public void UpdateSameInvKey(@Param("invKey") String invKey);

	@Query("SELECT e from Ret1And1AEntity e where e.id = :id")
	public Ret1And1AEntity findId(@Param("id") Long id);
	
}
