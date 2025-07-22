package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.AutoReconStatusEntity;

@Repository("AutoReconStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface AutoReconStatusRepository
		extends CrudRepository<AutoReconStatusEntity, Long>,
		JpaSpecificationExecutor<AutoReconStatusEntity> {

	@Modifying
	@Query("UPDATE AutoReconStatusEntity SET reconStatus =:reconStatus,"
			+ " reconInitiatedOn =:reconInitiatedOn where id =:configId")
	public void updateAutoReconStatus(@Param("reconStatus") String reconStatus,
			@Param("reconInitiatedOn") LocalDateTime reconInitiatedOn,
			@Param("configId") Long configId);

	@Modifying
	@Query("UPDATE AutoReconStatusEntity SET reconStatus =:reconStatus,"
			+ " reconCompletedOn =:reconCompletedOn where id =:configId")
	public void updateAutoReconStatusOnSuccessOrFailure(
			@Param("reconStatus") String reconStatus,
			@Param("reconCompletedOn") LocalDateTime reconCompletedOn,
			@Param("configId") Long configId);

	List<AutoReconStatusEntity> findByGet2aStatusAndReconStatusIsNull(
			String get2aStatus, Pageable pageable);
	
	@Modifying
	@Query("UPDATE AutoReconStatusEntity set get2aStatus = :get2aStatus, "
			+ "get2ACompletedOn = :get2ACompletedOn, reconStatus =:reconStatus"
			+ " where id = :id")
	void updateGet2aAndReconStatus(@Param("get2aStatus") String get2aStatus,
			@Param("get2ACompletedOn") LocalDateTime get2ACompletedOn,
			@Param("reconStatus") String reconStatus,
			@Param("id") Long id);
	
	

	@Modifying
	@Query("UPDATE AutoReconStatusEntity SET get2aErpPushStatus =:get2aErpPushStatus,"
			+ " get2aErpPushOn =:get2aErpPushOn where id =:configId")
	public void updateERPPushStatus(
			@Param("get2aErpPushStatus") String get2aErpPushStatus,
			@Param("get2aErpPushOn") LocalDateTime get2aErpPushOn,
			@Param("configId") Long configId);


	public AutoReconStatusEntity findByReconStatusInAndDate(
			List<String> inProgressReconStatuses, LocalDate now);

	public List<AutoReconStatusEntity> findByGet2aStatusAndReconStatusIsNullAndDate(
			String string, LocalDate now);

	public List<AutoReconStatusEntity> findByGet6aStatusAndReconStatusIsNullAndDate(
			String get6aStatus, LocalDate now);
	
	@Modifying
	@Query("UPDATE AutoReconStatusEntity set get6aStatus = :get6aStatus, "
			+ "get6aCompletedOn = :get6ACompletedOn, reconStatus =:reconStatus"
			+ " where id = :id")
	void updateGet6aAndReconStatus(@Param("get6aStatus") String get2aStatus,
			@Param("get6ACompletedOn") LocalDateTime get2ACompletedOn,
			@Param("reconStatus") String reconStatus,
			@Param("id") Long id);

	@Modifying
	@Query("UPDATE AutoReconStatusEntity set get2aStatus = :get2aStatus, "
			+ "get2ACompletedOn = :get2ACompletedOn, reconStatus =:reconStatus, get2aRemarks =:remarks "
			+ " where id = :id")
	void updateGet2aAndReconRemarks(@Param("get2aStatus") String get2aStatus,
			@Param("get2ACompletedOn") LocalDateTime get2ACompletedOn,
			@Param("reconStatus") String reconStatus,
			@Param("id") Long id, @Param("remarks") String gstr2Aremarks);
	
	@Modifying
	@Query("UPDATE AutoReconStatusEntity set get6aStatus = :get6aStatus, "
			+ "get6aCompletedOn = :get6ACompletedOn, reconStatus =:reconStatus, get6aRemarks =:remarks "
			+ " where id = :id")
	void updateGet6aAndReconRemarks(@Param("get6aStatus") String get2aStatus,
			@Param("get6ACompletedOn") LocalDateTime get2ACompletedOn,
			@Param("reconStatus") String reconStatus,
			@Param("id") Long id,@Param("remarks") String gstr6Aremarks);


}
