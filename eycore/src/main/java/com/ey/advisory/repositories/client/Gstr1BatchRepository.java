package com.ey.advisory.repositories.client;

import java.sql.Clob;
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

import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;

@Repository("batchSaveStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1BatchRepository
		extends CrudRepository<Gstr1SaveBatchEntity, Long> {

	List<Gstr1SaveBatchEntity> findByRefIdAndIsDeleteFalse(String refId);

	@Modifying
	@Query("UPDATE Gstr1SaveBatchEntity b SET b.refId = :refId,"
			+ "b.txnId = :txnId, b.modifiedOn = :modifiedOn, b.modifiedBy = "
			+ "'SYSTEM' WHERE b.id = :gstnBatchId")
	void updateBatchRefID(@Param("refId") String refId,
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("txnId") String txnId,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("update Gstr1SaveBatchEntity b set b.status= :status,"
			+ " b.modifiedOn = :modifiedOn, b.gstnRespDate =:modifiedOn,"
			+ " b.modifiedBy = 'SYSTEM' where b.id in (:idList)")
	void updateRefIdStatusForList(@Param("idList") List<Long> idList,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("update Gstr1SaveBatchEntity b set b.status= :status, "
			+ "b.modifiedOn =:modifiedOn, b.gstnRespDate =:modifiedOn,"
			+ " b.modifiedBy = 'SYSTEM' where b.id =:id")
	void updateStatusById(@Param("id") Long id, @Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	/*
	 * @Modifying
	 * 
	 * @Query("UPDATE Gstr1SaveBatchEntity b SET b.errorDesc=:errorDesc, " +
	 * "b.modifiedOn =:modifiedOn, b.gstnRespDate =:modifiedOn, " +
	 * "b.modifiedBy = 'SYSTEM' WHERE b.id = :gstnBatchId") void
	 * updateError(@Param("errorDesc") String errorDesc,
	 * 
	 * @Param("gstnBatchId") Long gstnBatchId,
	 * 
	 * @Param("modifiedOn") LocalDateTime modifiedOn);
	 */

	@Modifying
	@Query("UPDATE Gstr1SaveBatchEntity e SET e.gstnStatus = :status,"
			+ "e.errorCount =:errorCount,e.modifiedOn =:modifiedOn, "
			+ "e.gstnRespDate =:modifiedOn, e.modifiedBy = 'SYSTEM' "
			+ "WHERE e.id = :id")
	void updateGstr1SaveBatchbyBatchId(@Param("id") Long id,
			@Param("status") String status,
			@Param("errorCount") Integer errorCount,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("UPDATE Gstr1SaveBatchEntity e SET e.errorDesc =:errorDesc,e."
			+ "errorCode =:errorCode,e.modifiedOn =:modifiedOn,e.gstnRespDate"
			+ " =:modifiedOn,e.modifiedBy = 'SYSTEM' WHERE e.id = :id")
	void updateErrorMesg(@Param("id") Long id,
			@Param("errorCode") String errorCode,
			@Param("errorDesc") String errorDesc,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("select e FROM Gstr1SaveBatchEntity e WHERE (e.gstnStatus NOT IN("
			+ "'P','PE','ER') OR e.gstnStatus IS NULL) AND e.refId IS NOT "
			+ "NULL AND e.returnType=:returnType AND e.isDelete=false  ")
	/* + " and (e.status != 'POLLING_INPROGRESS' OR e.status IS NULL) ") */
	List<Gstr1SaveBatchEntity> findReferenceIdForPooling(
			@Param("returnType") String returnType);

	@Query("select e FROM Gstr1SaveBatchEntity e WHERE (e.gstnStatus NOT IN("
			+ "'P','PE','ER') OR e.gstnStatus IS NULL) AND e.refId IS NOT "
			+ "NULL AND e.returnType IN (:returnType) AND e.isDelete=false  ")
	/* + " and (e.status != 'POLLING_INPROGRESS' OR e.status IS NULL) ") */
	List<Gstr1SaveBatchEntity> findReferenceIdForPooling(
			@Param("returnType") List<String> returnTypes);
	/*
	 * @Query("select e FROM Gstr1SaveBatchEntity e WHERE e.isDelete=false  AND ("
	 * + "e.gstnStatus IS NULL OR e.gstnStatus NOT IN('P','PE','ER')) " +
	 * "AND refId IS NOT NULL AND e.returnType='GSTR1' " +
	 * "AND e.sgstin IN (:gstin) AND e.returnPeriod IN (:retPeriod)")
	 * List<Gstr1SaveBatchEntity> findgstr1ReferenceIdByGstin(
	 * 
	 * @Param("gstin") List<String> gstin,
	 * 
	 * @Param("retPeriod") List<String> retPeriod);
	 */

	@Query("select e FROM Gstr1SaveBatchEntity e WHERE e.isDelete=false AND "
			+ "(e.gstnStatus NOT IN('P','PE','ER') OR e.gstnStatus IS NULL) "
			+ "AND e.refId IS NOT NULL AND e.returnType='ANX1' "
			+ "AND e.sgstin IN (:gstin) AND e.returnPeriod IN (:retPeriod)")
	List<Gstr1SaveBatchEntity> findanx1ReferenceIdByGstin(
			@Param("gstin") List<String> gstin,
			@Param("retPeriod") String retPeriod);

	@Query("select e FROM Gstr1SaveBatchEntity e WHERE (e.gstnStatus NOT IN("
			+ "'P','PE','ER') OR e.gstnStatus IS NULL) AND e.refId IS NOT NULL "
			+ "AND e.returnType IN ('ANX1','ANX2') AND e.isDelete=false")
	List<Gstr1SaveBatchEntity> findanx1ReferenceId();

	@Query("select b.createdOn FROM  Gstr1SaveBatchEntity b  "
			+ "WHERE b.sgstin=:gstin")
	List<LocalDateTime> selectdateBygstinAndreturnPeriod(
			@Param("gstin") String gstin);

	@Query("select b FROM Gstr1SaveBatchEntity b  "
			+ "WHERE b.refId=:gstnRefId AND b.isDelete=false")
	List<Gstr1SaveBatchEntity> selectByrefId(
			@Param("gstnRefId") String gstnRefId);

	@Query("select b FROM Gstr1SaveBatchEntity b  "
			+ "WHERE b.refId=:gstnRefId AND b.isDelete=false")
	Optional<Gstr1SaveBatchEntity> findactiverefId(
			@Param("gstnRefId") String gstnRefId);

	/*
	 * @Query("select MAX(id) FROM Gstr1SaveBatchEntity b WHERE b.sgstin =:gstin AND "
	 * + "b.returnPeriod =:returnPeriod AND b.returnType =:returnType AND " +
	 * "b.section =:section AND b.isDelete=false") public Long
	 * selectMaxSumryId(@Param("gstin") String gstin,
	 * 
	 * @Param("returnPeriod") String returnPeriod,
	 * 
	 * @Param("returnType") String returnType,
	 * 
	 * @Param("section") String section);
	 */

	@Query("select e.id,e.hMaxId,e.v1MaxId,e.v2MaxId,e.userMaxId FROM Gstr1SaveBatchEntity "
			+ "e WHERE e.id =(select MAX(id) FROM Gstr1SaveBatchEntity b WHERE "
			+ "b.sgstin =:gstin AND b.returnPeriod =:returnPeriod AND "
			+ "b.returnType =:returnType AND "
			+ "b.section =:section AND b.isDelete=false)")
	public List<Object[]> selectMaxSumryRecord(@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("returnType") String returnType,
			@Param("section") String section);

	@Modifying
	@Query("UPDATE Gstr1SaveBatchEntity e SET e.hMaxId=0,e.v1MaxId=0,e.v2MaxId=0,e.userMaxId=0 "
			+ "WHERE e.sgstin = :gstin AND e.returnPeriod = :retPeriod AND "
			+ "e.section = :section AND e.returnType = :returnType "
			+ "AND e.id = :gstnBatchId AND e.isDelete = false")
	void resetMaxIdsOfOldSectionToZero(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod,
			@Param("section") String section,
			@Param("returnType") String returnType,
			@Param("gstnBatchId") Long gstnBatchId);

	@Query("FROM Gstr1SaveBatchEntity e WHERE e.isDelete=false AND (e."
			+ "gstnStatus NOT IN('P','PE','ER') OR e.gstnStatus IS NULL) AND e."
			+ "refId IS NOT NULL AND e.returnType =:returnType AND e.sgstin ="
			+ ":gstin AND e.returnPeriod =:retPeriod AND e.userRequestId ="
			+ ":userRequestId")
	List<Gstr1SaveBatchEntity> findPendingRefIdByUserRequestId(
			@Param("returnType") String returnType,
			@Param("gstin") String gstin, @Param("retPeriod") String retPeriod,
			@Param("userRequestId") Long userRequestId);
	
	@Query("FROM Gstr1SaveBatchEntity e WHERE e.isDelete=false AND (e."
			+ "gstnStatus NOT IN('P','PE','ER') OR e.gstnStatus IS NULL) AND e."
			+ "refId IS NOT NULL AND e.returnType =:returnType AND e.sgstin ="
			+ ":gstin AND e.returnPeriod =:retPeriod AND e.section ="
			+ ":section")
	List<Gstr1SaveBatchEntity> findPendingRefIdByUserRequestId(
			@Param("returnType") String returnType,
			@Param("gstin") String gstin, @Param("retPeriod") String retPeriod,
			@Param("section") String section);
	
	

	@Query("SELECT e.refId FROM Gstr1SaveBatchEntity e " + "WHERE e.id = :id ")
	public String getRefId(@Param("id") Long id);
	
	@Query("SELECT e.id FROM Gstr1SaveBatchEntity e WHERE e.refId = :refId ")
	public Long getBatchId(@Param("refId") String refId);

	@Query("SELECT b.gstnStatus FROM Gstr1SaveBatchEntity b WHERE b.sgstin =:gstin "
			+ "AND b.returnPeriod = :returnPeriod AND b.userRequestId = "
			+ ":userRequestId and b.isDelete=false")
	public List<String> findStatusByUserRequestIdAndIsDeleteFalse(
			@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("userRequestId") Long userRequestId);

	@Query("SELECT b.id FROM Gstr1SaveBatchEntity b WHERE  "
			+ "b.userRequestId = :userRequestId and b.operationType = "
			+ ":operationType and b.isDelete=false")
	public List<Long> findCalculateR6ByUserRequestId(
			@Param("userRequestId") Long userRequestId,
			@Param("operationType") String operationType);

	@Query("SELECT DISTINCT e.section FROM Gstr1SaveBatchEntity e WHERE "
			+ "e.isDelete=false AND e.refId IS NOT NULL AND e.returnType="
			+ ":returnType AND e.sgstin =:gstin AND e.returnPeriod =:retPeriod "
			+ "ORDER BY e.section")
	List<String> findRerurnSavedSections(@Param("returnType") String returnType,
			@Param("gstin") String gstin, @Param("retPeriod") String retPeriod);

	@Query("SELECT b.id FROM Gstr1SaveBatchEntity b WHERE  "
			+ "b.userRequestId = :userRequestId and b.operationType = "
			+ ":operationType and b.isDelete=false and b.section=:section "
			+ " and b.returnType=:returnType")
	public List<Long> findItc04Table5sectionsByUserRequestId(
			@Param("userRequestId") Long userRequestId,
			@Param("operationType") String operationType,
			@Param("section") String section,
			@Param("returnType") String returnType);

	@Query("select e FROM Gstr1SaveBatchEntity e WHERE e.isDelete=false AND "
			+ "e.gstnStatus NOT IN('P','ER') AND e.status IN('GET_INITIATED',"
			+ "'POLLING_INITIATED','POLLING_INPROGRESS','POLLING_FAILED') "
			+ "AND e.returnType='GSTR1' "
			+ "AND e.sgstin =:gstin AND e.returnPeriod =:retPeriod "
			+ "AND e.operationType IN ('Generate Summary (PTF)','Generate Summary')")
	Gstr1SaveBatchEntity findGstr1PollingRefId(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod);

	@Query("select e FROM Gstr1SaveBatchEntity e WHERE e.isDelete=false AND "
			+ "e.gstnStatus NOT IN('P','ER') AND e.status IN('GET_INITIATED',"
			+ "'POLLING_INITIATED','POLLING_INPROGRESS','POLLING_FAILED') "
			+ "AND e.returnType='GSTR1' "
			+ "AND e.sgstin =:gstin AND e.returnPeriod =:retPeriod "
			+ "AND e.operationType =:operationType")
	Gstr1SaveBatchEntity findGstr1PollingRefId(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod,
			@Param("operationType") String operationType);

	@Modifying
	@Query("UPDATE Gstr1SaveBatchEntity e SET e.isDelete = true "
			+ " WHERE e.isDelete=false  AND e.returnType='GSTR1' AND"
			+ " e.operationType IN ('Generate Summary (PTF)','Generate Summary') AND  e.sgstin =:gstin AND "
			+ " e.returnPeriod =:retPeriod")
	void softDeleteGstr1PollingEntries(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod);

	@Modifying
	@Query("UPDATE Gstr1SaveBatchEntity e SET e.isDelete = true "
			+ " WHERE e.isDelete=false  AND e.returnType='GSTR1' AND"
			+ " e.operationType =:operationType AND  e.sgstin =:gstin AND "
			+ " e.returnPeriod =:retPeriod")
	void softDeleteGstr1PollingEntries(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod,
			@Param("operationType") String operationType);

	@Query("select e FROM Gstr1SaveBatchEntity e WHERE e.isDelete=false AND "
			+ " e.status IN :status AND e.refId IS NOT NULL AND "
			+ " e.returnType in ('GSTR1','GSTR1A') AND e.operationType IN ('Generate Summary (PTF)','Generate Summary')")
	List<Gstr1SaveBatchEntity> findGstr1PollingRefIds(
			@Param("status") List<String> status);
	
	@Modifying
	@Query("update Gstr1SaveBatchEntity e set e.status =:status, "
			+ " e.gstnStatus =:gstnStatus,"
			+ " e.getResponsePayload =:responsePayload,"
			+ " e.modifiedOn =:modifiedOn, e.gstnRespDate =:modifiedOn"
			+ " where e.refId =:refId")
	void updateStatusAndReponseForRefId(@Param("status") String status,
			@Param("gstnStatus") String gstnStatus,
			@Param("responsePayload") Clob responsePayload,
			@Param("refId") String refId,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	Gstr1SaveBatchEntity findByRefId(String refId);

	@Query("select e FROM Gstr1SaveBatchEntity e WHERE e.sgstin=:supplierGstin AND "
			+ " e.returnPeriod=:returnPeriod AND e.createdOn=:createdOn AND "
			+ " e.section=:section ")
	Gstr1SaveBatchEntity findByGstinAndReturnPeriodAndCreatedOnAndSection(
			@Param("supplierGstin") String supplierGstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("createdOn") LocalDateTime createdOn,
			@Param("section") String section);
	
	@Query("select e FROM Gstr1SaveBatchEntity e WHERE e.sgstin=:supplierGstin AND "
			+ " e.returnPeriod=:returnPeriod AND e.refId=:refId AND "
			+ " e.gstnStatus=:gstnStatus AND e.returnType=:returnType")
	Gstr1SaveBatchEntity findByGstinAndReturnPeriodAndRefIdAndSection(
			@Param("supplierGstin") String supplierGstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("refId") String refId,
			@Param("gstnStatus") String gstnStatus,
	        @Param("returnType") String returnType);
	
	@Query("select e FROM Gstr1SaveBatchEntity e WHERE e.sgstin=:supplierGstin AND "
			+ " e.returnPeriod=:returnPeriod AND e.createdOn=:createdOn AND "
			+ "  e.section=:section AND e.returnType=:returnType")
	Gstr1SaveBatchEntity findByGstinAndReturnPeriodAndRefIdAndSectionGstr1A(
			@Param("supplierGstin") String supplierGstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("createdOn") LocalDateTime createdOn,
			@Param("section") String section,
	        @Param("returnType") String returnType);
	
	@Query("select e FROM Gstr1SaveBatchEntity e WHERE e.isDelete=false AND "
			+ "e.gstnStatus NOT IN('P','ER') AND e.status IN('GET_INITIATED',"
			+ "'POLLING_INITIATED','POLLING_INPROGRESS','POLLING_FAILED') "
			+ "AND e.returnType='GSTR1A' "
			+ "AND e.sgstin =:gstin AND e.returnPeriod =:retPeriod "
			+ "AND e.operationType IN ('Generate Summary (PTF)','Generate Summary')")
	Gstr1SaveBatchEntity findGstr1APollingRefId(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod);
	
	@Modifying
	@Query("UPDATE Gstr1SaveBatchEntity e SET e.isDelete = true "
			+ " WHERE e.isDelete=false  AND e.returnType='GSTR1A' AND"
			+ " e.operationType IN ('Generate Summary (PTF)','Generate Summary') AND  e.sgstin =:gstin AND "
			+ " e.returnPeriod =:retPeriod")
	void softDeleteGstr1APollingEntries(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod);
	
	@Query("select e FROM Gstr1SaveBatchEntity e WHERE e.sgstin=:supplierGstin AND "
			+ " e.section=:section AND e.refId=:refId AND e.gstnStatus IN('PE','ER') ")
	Gstr1SaveBatchEntity findByGstinAndSectionAndRefId(
			@Param("supplierGstin") String supplierGstin,
			@Param("section") String section,
	        @Param("refId") String refId);
	
	@Query("select count(e) FROM Gstr1SaveBatchEntity e WHERE e.sgstin=:sgstin AND "
			+ " e.returnType= 'IMS' AND e.isDelete=false AND e.gstnStatus IS NULL AND e.section =:tableType ")
	int findImsSaveInprogressCountByGstin  (
			@Param("sgstin") String sgstin, @Param("tableType") String tableType);
	
	@Modifying
	@Query("update Gstr1SaveBatchEntity e set e.status =:status, "
			+ " e.gstnStatus =:gstnStatus,"
			+ " e.getResponsePayload =:responsePayload,"
			+ " e.modifiedOn =:modifiedOn, e.gstnRespDate =:modifiedOn, "
			+ " errorCode =:errorCode, errorDesc =:errorDesc, "
			+ " errorCount=:errorCount"
			+ " where e.refId =:refId")
	void updateStatusAndReponseAndErrorsForRefId(@Param("status") String status,
			@Param("gstnStatus") String gstnStatus,
			@Param("responsePayload") Clob responsePayload,
			@Param("refId") String refId,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("errorDesc") String errorDesc,
			@Param("errorCount") Integer errorCount);


}
