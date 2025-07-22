/*package com.ey.advisory.app.data.repositories.client;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.SetOffAndUtilGstnEntity;

*//**
 * 
 * @author Mahesh.Golla
 *
 *//*
@Service("SetOffAndutilGstnRepository")
public interface SetOffAndutilGstnRepository
		extends JpaRepository<SetOffAndUtilGstnEntity, Long>,
		JpaSpecificationExecutor<SetOffAndUtilGstnEntity> {
	
	
	@Transactional
	@Modifying
	@Query("UPDATE SetOffAndUtilGstnEntity b SET b.isDelete = TRUE "
			+ "WHERE b.setOffGstnKey IN (:gstnKey) ")
	public void UpdateSameGstnKey(@Param("gstnKey") String gstnKey);

}
*/