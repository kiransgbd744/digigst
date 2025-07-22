package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.CewbEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("CewbRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface CewbRepository extends CrudRepository<CewbEntity, Long> {

	@Modifying
	@Query("UPDATE CewbEntity b SET b.isDelete = TRUE "
			+ "WHERE b.cewbInvKey IN (:invKey) ")
	public void UpdateSameInvKey(@Param("invKey") String invKey);

	@Query("SELECT e from CewbEntity e where e.id = :id")
	public CewbEntity findId(@Param("id") Long id);

	@Modifying
	@Query("UPDATE CewbEntity b SET b.errorCode = :errorCode, "
			+ "b.errorDesc = :errorDesc WHERE b.sNo = :sNo and b.ewbNo = :ewbNo "
			+ " and b.fileId = :fileId ")
	public void updateErrorResponse(@Param("sNo") Long serialNo,
			@Param("ewbNo") String ewbNo, @Param("fileId") Long fileId,
			@Param("errorCode") String errorCode,
			@Param("errorDesc") String errorDesc);

	@Modifying
	@Query("UPDATE CewbEntity b SET b.consolidatedEwbNum = :cEwbNo, "
			+ "b.consolidatedEwbDate = :cewbDate WHERE b.sNo = :sNo "
			+ "and b.ewbNo = :ewbNo and b.fileId = :fileId ")
	public void updateConsolidatedEWB(@Param("sNo") Long serialNo,
			@Param("ewbNo") String ewbNo, @Param("fileId") Long fileId,
			@Param("cEwbNo") String cEwbNo,
			@Param("cewbDate") LocalDateTime cewbDate);

	@Query("SELECT b FROM CewbEntity b WHERE b.isDelete = false")
	public List<CewbEntity> getActiveData();

	@Query("SELECT e from CewbEntity e where e.consolidatedEwbNum = :cEwbNo and e.isDelete=false")
	public List<CewbEntity> findByConsolidatedEWB(
			@Param("cEwbNo") String cEwbNo);

}
