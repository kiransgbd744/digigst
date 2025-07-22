/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.vendor.service.VendorValidatorPayloadEntity;

/**
 * @author vishal.verma
 *
 */
@Repository("VendorValidatorPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorValidatorPayloadRepository
		extends CrudRepository<VendorValidatorPayloadEntity, Long>,
		JpaSpecificationExecutor<VendorValidatorPayloadEntity> {

	@Modifying
	@Query("UPDATE VendorValidatorPayloadEntity op SET op.status=:status,"
			+ "op.modifiedOn = :modifiedOn, "
			+ "op.errorCount = :errorCount,op.totalCount = :totalCount "
			+ "WHERE op.payloadId =:payloadId ")
	public void updateStatus(@Param("payloadId") String payloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCount") Integer errorCount,
			@Param("totalCount") Integer totalCount);

	@Modifying
	@Query("UPDATE VendorValidatorPayloadEntity op SET op.status=:status,"
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
	@Query("UPDATE VendorValidatorPayloadEntity op SET op.status=:status,"
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

	@Query("SELECT op.payloadId FROM VendorValidatorPayloadEntity op "
			+ "WHERE op.payloadId =:payloadId ")
	List<Object> findBypayloadId(@Param("payloadId") String payloadId);

	@Query("SELECT op FROM VendorValidatorPayloadEntity op "
			+ "WHERE op.payloadId =:payloadId ")
	VendorValidatorPayloadEntity getGstinValidatorPayload(
			@Param("payloadId") String payloadId);

	@Query("SELECT op.payloadId,op.createdBy FROM VendorValidatorPayloadEntity op "
			+ "WHERE op.revIntPushStatus=0")
	public List<String[]> findPayloadIdForFailedErpPush();

	@Modifying
	@Query("UPDATE VendorValidatorPayloadEntity SET revIntPushStatus=:revIntPushStatus "
			+ "WHERE payloadId =:payloadId")
	public void updateRevIntPushStatus(
			@Param("payloadId") String payloadId,
			@Param("revIntPushStatus") Integer revIntPushStatus);

	Optional<VendorValidatorPayloadEntity> findByCloudCheckSumAndStatus(
			String cloudCheckSum, String status);
	
	@Modifying
	@Query("UPDATE VendorValidatorPayloadEntity SET getFillingFrequencyStatus= true "
			+ "WHERE payloadId =:payloadId")
	public void updateGetFillingFrequencyStatus(
			@Param("payloadId") final String payloadId);
	
	@Modifying
	@Query("UPDATE VendorValidatorPayloadEntity SET getFillingDetailsStatus= true "
			+ "WHERE payloadId =:payloadId")
	public void updateGetFillingDetailsStatus(
			@Param("payloadId") final String payloadId);
	
	@Modifying
	@Query("UPDATE VendorValidatorPayloadEntity SET getGstinValidationStatus= true "
			+ "WHERE payloadId =:payloadId")
	public void updateGetGstinValidationStatus(
			@Param("payloadId") final String payloadId);
	
	@Modifying
	@Query("UPDATE VendorValidatorPayloadEntity op SET "
			+ "op.errorCodeFillingfrequency=:errorCodeFillingfrequency,"
			+ "op.modifiedOn =:modifiedOn, "
			+ "errorMsgFillingfrequency=:errorMsgFillingfrequency "
			+ "WHERE op.payloadId =:payloadId ")
	public void updateFillingFrequencyErrorStatus(
			@Param("payloadId") String payloadId,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCodeFillingfrequency") String errorCodeFillingfrequency,
			@Param("errorMsgFillingfrequency") String errorMsgFillingfrequency);
	
	@Modifying
	@Query("UPDATE VendorValidatorPayloadEntity op SET "
			+ "op.errorCodeFillingDetails=:errorCodeFillingDetails, "
			+ "op.modifiedOn =:modifiedOn, "
			+ "errorMsgFillingDetails=:errorMsgFillingDetails "
			+ " WHERE op.payloadId =:payloadId ")
	public void updateFillingDetailsErrorStatus(
			@Param("payloadId") String payloadId,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCodeFillingDetails") String errorCodeFillingDetails,
			@Param("errorMsgFillingDetails") String errorMsgFillingDetails);

}