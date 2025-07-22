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

import com.ey.advisory.app.data.entities.client.OutwardPayloadEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("OutwardPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface OutwardPayloadRepository
		extends CrudRepository<OutwardPayloadEntity, Long> {

	@Modifying
	@Query("UPDATE OutwardPayloadEntity op SET op.status=:status,"
			+ "op.modifiedOn = :modifiedOn, "
			+ "op.errorCount = :errorCount,op.totalCount = :totalCount "
			+ "WHERE op.payloadId =:payloadId ")
	public void updateStatus(@Param("payloadId") String payloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCount") Integer errorCount,
			@Param("totalCount") Integer totalCount);

	@Modifying
	@Query("UPDATE OutwardPayloadEntity op SET op.status=:status,"
			+ "op.modifiedOn = :modifiedOn,op.errorCode = :errorCode,"
			+ "op.jsonErrorResponse = :jsonErrorResp ,"
			+ "op.errorCount = :errorCount,op.totalCount = :totalCount "
			+ "WHERE op.payloadId =:payloadId ")
	public void updateCpiStatus(@Param("payloadId") String payloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("jsonErrorResp") String jsonErrorResp,
			@Param("errorCount") Integer errorCount,
			@Param("totalCount") Integer totalCount);

	@Modifying
	@Query("UPDATE OutwardPayloadEntity op SET op.status=:status,"
			+ "op.modifiedOn = :modifiedOn,op.errorCode = :errorCode,"
			+ "op.jsonErrorResponse = :jsonErrorResp ,"
			+ "op.errorCount = :errorCount,op.totalCount = :totalCount,op.pushType = :pushType "
			+ "WHERE op.payloadId =:payloadId ")
	public void updateCpiStatus(@Param("payloadId") String payloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("jsonErrorResp") String jsonErrorResp,
			@Param("errorCount") Integer errorCount,
			@Param("totalCount") Integer totalCount,
			@Param("pushType") Integer pushType);

	@Query("SELECT op.payloadId FROM OutwardPayloadEntity op "
			+ "WHERE op.payloadId =:payloadId ")
	List<Object> findBypayloadId(@Param("payloadId") String payloadId);

	@Query("SELECT op FROM OutwardPayloadEntity op "
			+ "WHERE op.payloadId =:payloadId ")
	OutwardPayloadEntity getOutwardPayload(
			@Param("payloadId") String payloadId);

	@Query("SELECT op.payloadId,op.createdBy FROM OutwardPayloadEntity op "
			+ "WHERE op.revIntPushStatus=0")
	public List<String[]> findPayloadIdForFailedErpPush();

	@Modifying
	@Query("UPDATE OutwardPayloadEntity SET revIntPushStatus=:revIntPushStatus "
			+ "WHERE payloadId =:payloadId")
	public void updateRevIntPushStatus(
			@Param("payloadId") final String payloadId,
			@Param("revIntPushStatus") Integer revIntPushStatus);

	Optional<OutwardPayloadEntity> findByCloudCheckSumAndStatus(
			String cloudCheckSum, String status);

}