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

import com.ey.advisory.app.services.gstr3b.qtr.filing.apipush.Gstr3bQtrFilingPayloadEntity;

/**
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr3bQtrFilingPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3bQtrFilingPayloadRepository
		extends CrudRepository<Gstr3bQtrFilingPayloadEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr3bQtrFilingPayloadEntity op SET op.status=:status,"
			+ "op.modifiedOn = :modifiedOn "
			+ "WHERE op.payloadId =:payloadId ")
	public void updateStatus(@Param("payloadId") String payloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("UPDATE Gstr3bQtrFilingPayloadEntity op SET op.status=:status,"
			+ "op.modifiedOn = :modifiedOn,op.errorCode = :errorCode,"
			+ "op.jsonErrorResponse = :jsonErrorResp ,"
			+ "op.pushType = :pushType "
			+ "WHERE op.payloadId =:payloadId ")
	public void updateCpiStatus(@Param("payloadId") String payloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("jsonErrorResp") String jsonErrorResp,
			@Param("pushType") Integer pushType);

	@Query("SELECT op.payloadId FROM Gstr3bQtrFilingPayloadEntity op "
			+ "WHERE op.payloadId =:payloadId ")
	List<Object> findBypayloadId(@Param("payloadId") String payloadId);

	@Query("SELECT op FROM Gstr3bQtrFilingPayloadEntity op "
			+ "WHERE op.payloadId =:payloadId ")
	Gstr3bQtrFilingPayloadEntity getGstr3bQtrFilingPayload(
			@Param("payloadId") String payloadId);

	@Query("SELECT op.payloadId,op.createdBy FROM Gstr3bQtrFilingPayloadEntity op "
			+ "WHERE op.revIntPushStatus=0")
	public List<String[]> findPayloadIdForFailedErpPush();

	@Modifying
	@Query("UPDATE Gstr3bQtrFilingPayloadEntity SET revIntPushStatus=:revIntPushStatus "
			+ "WHERE payloadId =:payloadId")
	public void updateRevIntPushStatus(
			@Param("payloadId") final String payloadId,
			@Param("revIntPushStatus") Integer revIntPushStatus);

	Optional<Gstr3bQtrFilingPayloadEntity> findByCloudCheckSumAndStatus(
			String cloudCheckSum, String status);

}