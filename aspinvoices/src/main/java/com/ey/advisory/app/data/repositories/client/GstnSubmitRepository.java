package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Repository("GstnSubmitRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstnSubmitRepository
		extends JpaRepository<GstnSubmitEntity, Long> {

	public List<GstnSubmitEntity> findByGstinAndRetPeriodAndGstnStatusNotAndIsDeleteFalse(
			String gstin, String retPeriod, String status);

	@Query("select e FROM GstnSubmitEntity e WHERE (e.gstnStatus NOT IN("
			+ "'P','PE','ER') OR e.gstnStatus IS NULL) AND e.refId IS NOT "
			+ "NULL AND e.returnType=:returnType AND e.isDelete=false")
	public List<GstnSubmitEntity> findReferenceIdForPooling(
			@Param("returnType") String returnType);

	@Modifying
	@Query("update GstnSubmitEntity b set b.status= :status, b.modifiedOn = "
			+ ":modifiedOn where b.id in (:idList)")
	public void updateRefIdStatusForList(@Param("idList") List<Long> idList,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("UPDATE GstnSubmitEntity e SET e.gstnStatus = :gstnStatus,"
			+ "e.status= :status,e.modifiedOn =:modifiedOn WHERE e.id = :id")
	void updateGstr1SaveBatchbyBatchId(@Param("id") Long id,
			@Param("gstnStatus") String gstnStatus,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("select e.gstnStatus FROM GstnSubmitEntity e WHERE e.gstin = :gstin and "
			+ "e.retPeriod = :retPeriod and e.returnType = :returnType "
			+ "and e.gstnStatus = 'P' and e.isDelete = false")
	public List<String> findStatusPByGstinAndRetPeriodAndRetrunType(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod, @Param("returnType") String returnType);
	
	@Query("select e.gstnStatus FROM GstnSubmitEntity e WHERE e.gstin = :gstin and "
			+ "e.returnType = 'GSTR1' "
			+ "and e.gstnStatus = 'P' and e.isDelete = false")
	public List<String> findStatus(@Param("gstin") String gstin);

	/**
	 * 
	 * This Method used For Gstn Submit API
	 * 
	 * @author Balakrishna.S
	 * @param gstin
	 * @param retPeriod
	 * @param returnType
	 * @return
	 */

	@Query("select e FROM GstnSubmitEntity e WHERE e.gstin IN (:gstin) and "
			+ "e.retPeriod = :retPeriod and e.returnType = :returnType "
			+ "and e.isDelete = false ")
	public List<GstnSubmitEntity> findGstnStatus(
			@Param("gstin") List<String> gstin,
			@Param("retPeriod") String retPeriod,
			@Param("returnType") String returnType);
	
	Optional<GstnSubmitEntity> findTop1ByGstinAndRetPeriodAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
			@Param("gstin") String gstin, 
			@Param("retPeriod") String retPeriod,
			@Param("returnType") String returnType);
	
	@Query("select e FROM GstnSubmitEntity e WHERE e.gstin IN (:gstin) and "
			+ "e.retPeriod = :retPeriod and e.returnType = :returnType "
			+ "and e.isDelete = false")
	public GstnSubmitEntity findGstnStatusForSingleGstin(
			@Param("gstin") String gstin, @Param("retPeriod") String retPeriod,
			@Param("returnType") String returnType);



}
