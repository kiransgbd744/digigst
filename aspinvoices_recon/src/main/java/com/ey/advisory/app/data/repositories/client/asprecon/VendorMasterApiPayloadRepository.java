/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;

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

import com.ey.advisory.app.services.vendor.master.apipush.VendorMasterApiPayloadEntity;

/**
 * @author Shashikant.Shukla
 *
 */
@Repository("VendorMasterApiPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorMasterApiPayloadRepository
		extends CrudRepository<VendorMasterApiPayloadEntity, Long> {

	@Modifying
	@Query("UPDATE VendorMasterApiPayloadEntity op SET op.status=:status,"
			+ "op.modifiedOn = :modifiedOn "
			+ "WHERE op.payloadId =:payloadId ")
	public void updateStatus(@Param("payloadId") String payloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("UPDATE VendorMasterApiPayloadEntity op SET op.status=:status,"
			+ "op.modifiedOn = :modifiedOn,op.errorCode = :errorCode,"
			+ "op.jsonErrorResponse = :jsonErrorResp ,"
			+ "op.pushType = :pushType " + "WHERE op.payloadId =:payloadId ")
	public void updateCpiStatus(@Param("payloadId") String payloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("jsonErrorResp") String jsonErrorResp,
			@Param("pushType") Integer pushType);

	@Query("SELECT op.payloadId FROM VendorMasterApiPayloadEntity op "
			+ "WHERE op.payloadId =:payloadId ")
	List<Object> findBypayloadId(@Param("payloadId") String payloadId);

	@Query("SELECT op FROM VendorMasterApiPayloadEntity op "
			+ "WHERE op.payloadId =:payloadId ")
	VendorMasterApiPayloadEntity getVendorMaterApiPayload(
			@Param("payloadId") String payloadId);

	@Query("SELECT op.payloadId,op.createdBy FROM VendorMasterApiPayloadEntity op "
			+ "WHERE op.revIntPushStatus=0")
	public List<String[]> findPayloadIdForFailedErpPush();

	@Modifying
	@Query("UPDATE VendorMasterApiPayloadEntity SET revIntPushStatus=:revIntPushStatus "
			+ "WHERE payloadId =:payloadId")
	public void updateRevIntPushStatus(
			@Param("payloadId") final String payloadId,
			@Param("revIntPushStatus") Integer revIntPushStatus);

	Optional<VendorMasterApiPayloadEntity> findByCloudCheckSumAndStatus(
			String cloudCheckSum, String status);

}