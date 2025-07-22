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

import com.ey.advisory.domain.client.Gstr2bRegenerateSaveBatchEntity;

@Repository("Gstr2bRegenerateBatchRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2bRegenerateBatchRepository
		extends CrudRepository<Gstr2bRegenerateSaveBatchEntity, Long> {

	@Query("select e FROM Gstr2bRegenerateSaveBatchEntity e WHERE "
			+ "e.gstnSaveStatus NOT IN('P','ER') AND e.status IN('SAVE_SUCCESS',"
			+ "'POLLING_INITIATED','POLLING_INPROGRESS','POLLING_FAILED') "
			+ " AND e.returnType=:returnType AND e.isDelete=false  ")
	/* + " and (e.status != 'POLLING_INPROGRESS' OR e.status IS NULL) ") */
	List<Gstr2bRegenerateSaveBatchEntity> findReferenceIdForPooling(
			@Param("returnType") String returnType);

	List<Gstr2bRegenerateSaveBatchEntity> findByGstnSaveRefIdAndIsDeleteFalse(
			String refId);

	@Modifying
	@Query("update Gstr2bRegenerateSaveBatchEntity b set b.status  =:status,"
			+ " b.modifiedOn = :modifiedOn, b.gstnRespDate =:modifiedOn,"
			+ " b.modifiedBy = 'SYSTEM' where b.id in (:idList)")
	public void updateRefIdStatusForList(@Param("idList") List<Long> idList,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("update Gstr2bRegenerateSaveBatchEntity e set e.status =:status, "
			+ " e.gstnSaveStatus =:gstnStatus,"
			+ " e.getResponsePayload =:responsePayload,"
			+ " e.modifiedOn =:modifiedOn, e.gstnRespDate =:modifiedOn"
			+ " where e.gstnSaveRefId =:refId")
	public void updateStatusAndReponseForRefId(@Param("status") String status,
			@Param("gstnStatus") String gstnStatus,
			@Param("responsePayload") Clob responsePayload,
			@Param("refId") String refId,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("update Gstr2bRegenerateSaveBatchEntity e set e.status =:status, "
			+ " e.gstnSaveStatus =:gstnStatus,"
			+ " e.getResponsePayload =:responsePayload,"
			+ " e.modifiedOn =:modifiedOn, e.gstnRespDate =:modifiedOn, "
			+ " e.errorCode =:errorCode, e.errorDesc =:errorDesc "
			+ " where e.gstnSaveRefId =:refId")
	public void updateErrorStatusAndReponseForRefId(@Param("status") String status,
			@Param("gstnStatus") String gstnStatus,
			@Param("responsePayload") Clob responsePayload,
			@Param("refId") String refId,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("errorDesc") String errorDesc);

	@Modifying
	@Query("UPDATE Gstr2bRegenerateSaveBatchEntity b SET b.isDelete = true "
			+ " where b.supplierGstin =:gstin and b.returnPeriod =:rtnPeriod")
	public void updatepreviousBatch(@Param("gstin") String gstin,
			@Param("rtnPeriod") String rtnPeriod);

}
