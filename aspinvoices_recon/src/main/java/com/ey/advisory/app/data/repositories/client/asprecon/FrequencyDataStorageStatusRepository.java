package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.FrequencyDataStorageStatusEntity;

@Repository("FrequencyDataStorageStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface FrequencyDataStorageStatusRepository
		extends JpaRepository<FrequencyDataStorageStatusEntity, Long>,
		JpaSpecificationExecutor<FrequencyDataStorageStatusEntity> {

	FrequencyDataStorageStatusEntity findByFinancialYear(String financialYear);

	List<FrequencyDataStorageStatusEntity> findByFinancialYear(
			String financialYear, Pageable pageReq);

	public Optional<FrequencyDataStorageStatusEntity> findTop1ByFinancialYearOrderByModifiedOnDesc(
			String finYear);
	
	FrequencyDataStorageStatusEntity findByFinancialYearAndEntityId(String financialYear,Long entityId);
	
	public Optional<FrequencyDataStorageStatusEntity> findTop1ByFinancialYearAndEntityIdOrderByModifiedOnDesc(
			String finYear,Long entityId);
	
}
