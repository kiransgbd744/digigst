package com.ey.advisory.repositories.client;

import java.sql.Clob;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.domain.client.B2CQRCodeRequestLogEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("B2CQRCodeLoggerRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface B2CQRCodeLoggerRepository
		extends JpaRepository<B2CQRCodeRequestLogEntity, Long>,
		JpaSpecificationExecutor<B2CQRCodeRequestLogEntity> {

	@Modifying
	@Query("UPDATE B2CQRCodeRequestLogEntity log SET log.respPayload = :respPayload "
			+ "WHERE log.id = :id")
	void updateRespPayload(@Param("respPayload") Clob respPayload,
			@Param("id") Long id);

	Optional<B2CQRCodeRequestLogEntity> findByDocHeaderId(Long id);
	
	@Query("SELECT  doc from B2CQRCodeRequestLogEntity doc "
			+ "WHERE doc.dockey = :key ")
	List<B2CQRCodeRequestLogEntity> findByDockey(@Param("key") String key);


}
