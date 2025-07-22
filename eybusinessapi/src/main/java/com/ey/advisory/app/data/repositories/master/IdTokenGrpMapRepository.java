package com.ey.advisory.app.data.repositories.master;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.master.IdTokenGrpMapEntity;

/**
 * @author SivaReddy
 *
 */
@Repository("IdTokenGrpMapRepository")
@Transactional(value = "masterTransactionManager", propagation = Propagation.REQUIRED)
public interface IdTokenGrpMapRepository
		extends JpaRepository<IdTokenGrpMapEntity, Long>,
		JpaSpecificationExecutor<IdTokenGrpMapEntity> {
	
	public IdTokenGrpMapEntity findByGroupCode(String groupCode);

	@Modifying
	@Query("UPDATE IdTokenGrpMapEntity log SET log.idToken = :idToken, log.expiryTime = :expiryTime "
			+ "WHERE  log.groupCode=:groupCode ")
	void updateIdToken(@Param("idToken") String idToken,
			@Param("groupCode") String groupCode, @Param("expiryTime") LocalDateTime expiryTime);
}
