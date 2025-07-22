/*package com.ey.advisory.app.data.repositories.client;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Ret1And1AGstnEntity;

*//**
 * 
 * @author Mahesh.Golla
 *
 *//*
@Service("Ret1And1AGstnRepository")
public interface Ret1And1AGstnRepository
		extends JpaRepository<Ret1And1AGstnEntity, Long>,
		JpaSpecificationExecutor<Ret1And1AGstnEntity> {
	
	
	@Transactional
	@Modifying
	@Query("UPDATE Ret1And1AGstnEntity b SET b.isDelete = TRUE "
			+ "WHERE b.retGstnKey IN (:gstnKey) ")
	public void UpdateSameGstnKey(@Param("gstnKey") String gstnKey);

}
*/