/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.EinvReconRespConfigEntity;

/**
 * @author Siva.Reddy
 *
 */

@Repository("EinvReconRespConfigRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EinvReconRespConfigRepository
		extends JpaRepository<EinvReconRespConfigEntity, Long>,
		JpaSpecificationExecutor<EinvReconRespConfigEntity> {

	@Modifying
	@Query("UPDATE EinvReconRespConfigEntity g SET g.completedOn = :completedOn,g.reconRespStatus = :reconRespStatus "
			+ "WHERE g.reconRespConfigId=:reconRespConfigId")
	public int updateCompletedOnandStatus(
			@Param("reconRespConfigId") Long reconRespConfigId,
			@Param("reconRespStatus") String reconRespStatus,
			@Param("completedOn") LocalDateTime completedOn);
	
	@Query("SELECT g FROM EinvReconRespConfigEntity g "
			+ "WHERE g.reconRespConfigId IN (:reconRespConfigId)")
	public List<EinvReconRespConfigEntity> getReconCreatedOn(
			@Param("reconRespConfigId") List<Long> reconConfigId);

}
