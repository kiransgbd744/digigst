package com.ey.advisory.app.data.repositories.clientBusiness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.data.entities.clientBusiness.CounterPartyEwbBillEntity;

@Repository("CounterPartyEwbBillRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface CounterPartyEwbBillRepository
		extends JpaRepository<CounterPartyEwbBillEntity, Long>,
		JpaSpecificationExecutor<CounterPartyEwbBillEntity> {

	@Modifying
	@Query("update CounterPartyEwbBillEntity set fetchStatus = 'POSTED' where id in (:idList) " )
	void updateEWBStatus(@Param("idList") List<Long> idList);

	@Modifying
	@Query("update CounterPartyEwbBillEntity set fetchStatus = :status where ewbNo = :ewbNo and ewbGenDate = :ewbGenDate")
	void updateStatus(@Param("ewbNo") String ewbNo,
			@Param("status") String status, 
			@Param("ewbGenDate") LocalDate ewbGenDate);

	List<CounterPartyEwbBillEntity> findByFetchStatusIn(List<String> status, Pageable page);
	
	@Query("select c from CounterPartyEwbBillEntity c where "
			+ " c.revIntStatus IN ('NOT_INITIATED','FAILED') and c.clientGstin = :GSTIN "
			+ "and c.fetchStatus = 'SUCCESS' and c.ewbGenDate = :date" )
	List<CounterPartyEwbBillEntity> findCounterPartyForReverseIntegration(
			@Param("GSTIN") String gstin, 
			@Param("date") LocalDate date);
	
	@Modifying
	@Query("update CounterPartyEwbBillEntity set revIntStatus = :status,"
			+ " modifiedOn = :modifiedOn  where clientGstin = :gstin and "
			+ " ewbGenDate = :ewbGenDate")
	void updateRevIntgStatus(@Param("gstin") String gstin,
			@Param("status") String status,
			@Param("ewbGenDate") LocalDate ewbGenDate,
			@Param("modifiedOn") LocalDateTime modifiedOn);

}
