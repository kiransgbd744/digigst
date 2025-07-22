package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.ClientFilingStatusEntity;

@Repository("ClientFilingStatusRepositoty")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ClientFilingStatusRepositoty
		extends JpaRepository<ClientFilingStatusEntity, Long>,
		JpaSpecificationExecutor<ClientFilingStatusEntity> {

	ClientFilingStatusEntity findByFinancialYear(String financialYear);

	List<ClientFilingStatusEntity> findByFinancialYear(String financialYear,
			Pageable pageReq);

	Optional<ClientFilingStatusEntity> findByGstin(String gstins);

	ClientFilingStatusEntity findByFinancialYearAndGstinAndReturnType(
			String financialYear, String gstins, String returnType);

	Optional<ClientFilingStatusEntity> findByGstinAndFinancialYearAndReturnType(
			String gstin, String financialYear, String returnType);

	List<ClientFilingStatusEntity> findByFinancialYearAndGstinInAndReturnType(
			String financialYear, List<String> gstinList, String returnType);

	@Modifying
	@Query("UPDATE ClientFilingStatusEntity doc SET doc.status='SUBMITTED' WHERE doc.id = :id ")
	void updateRecordById(@Param("id") Long id);

	@Modifying
	@Query("UPDATE ClientFilingStatusEntity doc SET doc.status='INPROGRESS', doc.modifiedOn= :now WHERE doc.id = :id ")
	void updatRecordById(@Param("id") Long id, @Param("now") LocalDateTime now);

	ClientFilingStatusEntity findFirstByFinancialYearOrderByModifiedOnDesc(
			String financialYear);
}
