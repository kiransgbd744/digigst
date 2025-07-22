package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr3bEntity;

import jakarta.transaction.Transactional;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Gstr3BRepository")
public interface Gstr3BRepository
		extends JpaRepository<Gstr3bEntity, Long>, 
		                         JpaSpecificationExecutor<Gstr3bEntity> {

	/**
	 * @param Table 4
	 */
	
	@Transactional
	@Modifying
	@Query("UPDATE Gstr3bEntity b SET b.isDelete = TRUE "
			+ "WHERE b.isDelete = FALSE AND  b.invKey IN (:invKey) ")
	public void UpdateSameInvKey(@Param("invKey") String invKey);
	
	
	@Query("SELECT e from Gstr3bEntity e where e.id = :id")
	public Gstr3bEntity findId(@Param("id") Long id);


}
