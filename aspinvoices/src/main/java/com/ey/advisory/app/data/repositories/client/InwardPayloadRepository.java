/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.InwardPayloadEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("InwardPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface InwardPayloadRepository
		extends CrudRepository<InwardPayloadEntity, Long> {

	@Modifying
	@Query("UPDATE InwardPayloadEntity ip SET ip.status=:status,"
			+ "ip.modifiedOn = :modifiedOn,"
			+ "ip.errorCount = :errorCount,ip.totalCount = :totalCount "
			+ "WHERE ip.payloadId =:payloadId ")
	public void updateStatus(@Param("payloadId") String payloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCount") Integer errorCount,
			@Param("totalCount") Integer totalCount);

	@Modifying
	@Query("UPDATE InwardPayloadEntity ip SET ip.status=:status,"
			+ "ip.modifiedOn = :modifiedOn,ip.errorCode = :errorCode,"
			+ "ip.jsonErrorResponse = :jsonErrorResp,"
			+ "ip.errorCount = :errorCount,ip.totalCount = :totalCount "
			+ "WHERE ip.payloadId =:payloadId ")
	public void updateCpiStatus(@Param("payloadId") String payloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("jsonErrorResp") String jsonErrorResp,
			@Param("errorCount") Integer errorCount,
			@Param("totalCount") Integer totalCount);

	@Query("SELECT ip.payloadId FROM InwardPayloadEntity ip "
			+ "WHERE ip.payloadId =:payloadId ")
	List<Object> findBypayloadId(@Param("payloadId") String payloadId);

	@Query("SELECT ip FROM InwardPayloadEntity ip "
			+ "WHERE ip.payloadId =:payloadId ")
	InwardPayloadEntity getInwardPayload(@Param("payloadId") String payloadId);

	@Query("SELECT ip.payloadId,ip.createdBy FROM InwardPayloadEntity ip "
			+ "WHERE ip.revIntPushStatus=0")
	public List<String[]> findPayloadIdForFailedErpPush();

	@Modifying
	@Query("UPDATE InwardPayloadEntity SET revIntPushStatus=:revIntPushStatus "
			+ "WHERE payloadId =:payloadId")
	public void updateRevIntPushStatus(
			@Param("payloadId") final String payloadId,
			@Param("revIntPushStatus") Integer revIntPushStatus);

	Optional<InwardPayloadEntity> findByCloudCheckSumAndStatus(
			String cloudCheckSum, String status);

}
