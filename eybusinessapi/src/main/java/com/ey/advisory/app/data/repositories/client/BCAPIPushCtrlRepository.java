package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.BCAPIPushCtrlEntity;

/**
 * @author SivaReddy
 *
 */
@Repository("BCAPIPushCtrlRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface BCAPIPushCtrlRepository
		extends JpaRepository<BCAPIPushCtrlEntity, Long>,
		JpaSpecificationExecutor<BCAPIPushCtrlEntity> {

	@Modifying
	@Query("UPDATE BCAPIPushCtrlEntity log SET log.reqPayload = :reqPayload "
			+ "WHERE  log.batchId=:batchId ")
	void updateSAPReq(@Param("reqPayload") String reqPayload,
			@Param("batchId") String batchId);

	@Modifying
	@Query("UPDATE BCAPIPushCtrlEntity log SET log.pushStatus = :pushStatus,"
			+ "log.batchPushedDate = CURRENT_TIMESTAMP,log.respPayload = :respPayload "
			+ "WHERE log.batchId=:batchId ")
	void updateBatchPushStatus(@Param("pushStatus") String pushStatus,
			@Param("respPayload") String respPayload,
			@Param("batchId") String batchId);

	@Modifying
	@Query("UPDATE BCAPIPushCtrlEntity log SET log.pushStatus = :pushStatus,"
			+ "log.batchCompletedDate = CURRENT_TIMESTAMP,log.respPayload = :respPayload "
			+ "WHERE log.batchId=:batchId")
	void updateBatchComplStatus(@Param("pushStatus") String pushStatus,
			@Param("respPayload") String respPayload,
			@Param("batchId") String batchId);

	@Modifying
	@Query("UPDATE BCAPIPushCtrlEntity log SET log.pushStatus = :pushStatus,"
			+ "log.respPayload = :respPayload WHERE log.batchId=:batchId")
	void updateBatchStatus(@Param("pushStatus") String pushStatus,
			@Param("respPayload") String respPayload,
			@Param("batchId") String batchId);

	@Modifying
	@Query("UPDATE BCAPIPushCtrlEntity log SET log.pushStatus = :pushStatus,"
			+ "log.modfOn = CURRENT_TIMESTAMP,log.respPayload = :respPayload,log.isRetry = :isRetry "
			+ "WHERE log.batchId=:batchId")
	void updateBatchModfnStatus(@Param("pushStatus") String pushStatus,
			@Param("respPayload") String respPayload,
			@Param("batchId") String batchId,
			@Param("isRetry") boolean isRetry);

	public List<BCAPIPushCtrlEntity> findByPushStatus(String pushStatus,
			Pageable page);

}
