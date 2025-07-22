package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.EwbEntity;

/**
 * @author Khalid1.Khan
 *
 */

@Repository("EwbRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
@Qualifier("EwbRepository")
public interface EwbRepository extends JpaRepository<EwbEntity, Long>,
		JpaSpecificationExecutor<EwbEntity> {

	@Modifying
	@Query("UPDATE EwbEntity ewb SET "
			+ "ewb.isDelete=:isDelete,ewb.lifeCycleId = :lifeCycleId,"
			+ "ewb.status=:status,ewb.cancelDate = :cancelDate"
			+ " WHERE ewb.ewbNum=:ewbNo")
	void updateEwbCancelStatusByEwbNo(@Param("ewbNo") String ewbNo,
			@Param("isDelete") boolean isDelete,
			@Param("cancelDate") LocalDateTime cancelDate,
			@Param("status") Integer status,
			@Param("lifeCycleId") Long lifeCycleId);

	@Query("select ewb from EwbEntity ewb where ewbNum =:ewbNum")
	public EwbEntity findByEwbNum(@Param("ewbNum") String ewbNum);

	@Query("select docHeaderId from EwbEntity ewb where ewbNum IN (:ewbList)")
	public List<Long> findDocIdsByEwbNums(
			@Param("ewbList") List<String> ewbList);

	@Modifying
	@Query("UPDATE EwbEntity einv SET "
			+ "einv.consolidatedEwbNum=:cEwbNo,einv.consolidatedEwbDate = :cewbDate"
			+ " WHERE einv.ewbNum IN (:ewbList)")
	void updateConsildatedEwb(@Param("ewbList") List<String> ewbList,
			@Param("cEwbNo") String cEwbNo,
			@Param("cewbDate") LocalDateTime cewbDate);

	@Query("select ewb from EwbEntity ewb where ewb.ewbNum =:ewbNum "
			+ "AND ewb.isDelete=false")
	public List<EwbEntity> getEwbValidUpto(@Param("ewbNum") String ewbNum);

	@Query("select ewb.validUpto from EwbEntity ewb where ewb.docHeaderId =:id "
			+ "AND ewb.isDelete=false")
	public LocalDateTime findByIdValidUpto(@Param("id") Long id);

	@Modifying
	@Query("UPDATE EwbEntity ewb SET " + "ewb.lifeCycleId = :lifeCycleId"
			+ " WHERE ewb.ewbNum=:ewbNum")
	void updateLifeCycleIdByEwbNumber(@Param("ewbNum") String ewbNum,
			@Param("lifeCycleId") Long lifeCycleId);

	/*
	 * @Query("select ewb from EwbEntity ewb where ewb.transporterId =:transporterId "
	 * + "AND ewb.isDelete=false") public List<EwbEntity>
	 * findByIdTransporterId(@Param("transporterId") String transporterId);
	 */

	@Query("select ewb from EwbEntity ewb where ewb.ewbNum IN (:ewbList)")
	public List<EwbEntity> findEwbByEwbNums(
			@Param("ewbList") List<String> ewbList);

	@Query("select ewb.validUpto from EwbEntity ewb where ewb.ewbNum =:ewbNum "
			+ "AND ewb.isDelete=false")
	public LocalDateTime findValidUptoByEwbnum(@Param("ewbNum") String ewbNum);

	@Query("select ewb from EwbEntity ewb where ewb.ewbNum = :ewbNum ")
	public EwbEntity findEwbByEwbNum(@Param("ewbNum") String ewbNum);

	@Modifying
	@Query("UPDATE EwbEntity ewb SET ewb.status = :ewbStatus, "
			+ "ewb.rejectDate = :rejectedDate, ewb.isDelete=true "
			+ " WHERE ewb.ewbNum=:ewbNum and ewb.ewbOrigin = 2")
	void updateRejectStatus(@Param("ewbStatus") int ewbNewStatusCode,
			@Param("rejectedDate") LocalDateTime ewbRejectedDate,
			@Param("ewbNum") String ewbNum);

	@Query("select ewb.docHeaderId from EwbEntity ewb where ewb.ewbNum = :ewbNum ")
	public Long findDocIdByEwbNum(@Param("ewbNum") String ewbNum);
	
	@Query("select ewb from EwbEntity ewb where ewb.ewbNum = :ewbNum ")
	Optional<EwbEntity> findEwbDtls(@Param("ewbNum") String ewbNum);

}
