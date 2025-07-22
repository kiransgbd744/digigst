package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.ReturnDataStorageStatusEntity;

@Repository("ReturnDataStorageStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ReturnDataStorageStatusRepository
		extends JpaRepository<ReturnDataStorageStatusEntity, Long>,
		JpaSpecificationExecutor<ReturnDataStorageStatusEntity> {

	ReturnDataStorageStatusEntity findByFinancialYear(String financialYear);

	List<ReturnDataStorageStatusEntity> findByFinancialYear(
			String financialYear, Pageable pageReq);

	public Optional<ReturnDataStorageStatusEntity> findTop1ByFinancialYearOrderByModifiedOnDesc(
			String finYear);
}
