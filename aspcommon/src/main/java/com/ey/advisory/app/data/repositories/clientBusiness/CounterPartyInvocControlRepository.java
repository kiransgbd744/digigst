package com.ey.advisory.app.data.repositories.clientBusiness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.data.entities.clientBusiness.CounterPartyInvocControlEntity;

@Repository("CounterPartyInvocControlRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface CounterPartyInvocControlRepository
		extends JpaRepository<CounterPartyInvocControlEntity, Long>,
		JpaSpecificationExecutor<CounterPartyInvocControlEntity> {

	@Modifying
	@Query("update CounterPartyInvocControlEntity set invocSt = :curDate, "
			+ "status = 'INITIATED', errorMsg = :errDesc where gstin = :gstin "
			+ "and ewbGenDate = :ewbGenDate")
	void updateStatusAndStartDate(@Param("gstin") String gstin,
			@Param("curDate") LocalDateTime curDate,
			@Param("errDesc") String errDesc,
			@Param("ewbGenDate") LocalDate ewbGenDate);

	@Modifying
	@Query("update CounterPartyInvocControlEntity set invocEnd = :curDate, "
			+ "status = :status, errorMsg = :errDesc where gstin = :gstin and "
			+ " ewbGenDate = :ewbGenDate")
	void updateStatusAndEndDate(@Param("gstin") String gstin,
			@Param("curDate") LocalDateTime curDate,
			@Param("status") String status, @Param("errDesc") String errDesc,
			@Param("ewbGenDate") LocalDate ewbGenDate);

	// List<String> getAllGstins(@Param("genDate") LocalDate genDate);

	List<CounterPartyInvocControlEntity> findByEwbGenDate(LocalDate ewbGenDate);

	@Modifying
	@Query("update CounterPartyInvocControlEntity set revIntStatus = :revIntStatus "
			+ " where gstin = :gstin and  ewbGenDate = :ewbGenDate")
	void updateRevIntgStatus(@Param("gstin") String gstin,
			@Param("ewbGenDate") LocalDate ewbGenDate,
			@Param("revIntStatus") String revIntStatus);

	@Query("SELECT doc.id from CounterPartyInvocControlEntity doc "
			+ "WHERE doc.gstin = :gstin and doc.ewbGenDate = :ewbGenDate")
	public long getCntrlId(@Param("gstin") String gstin,
			@Param("ewbGenDate") LocalDate ewbGenDate);

}
