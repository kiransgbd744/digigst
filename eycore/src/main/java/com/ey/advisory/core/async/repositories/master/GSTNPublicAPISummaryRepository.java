package com.ey.advisory.core.async.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.GSTNPublicAPISummaryEntity;

@Repository("GSTNPublicAPISummaryRepository")
@Transactional(value = "masterTransactionManager")
public interface GSTNPublicAPISummaryRepository
		extends JpaRepository<GSTNPublicAPISummaryEntity, Long>,
		JpaSpecificationExecutor<GSTNPublicAPISummaryEntity> {

	public List<GSTNPublicAPISummaryEntity> findByTypeAndIsActiveTrue(
			String type);

	@Modifying
	@Query("UPDATE GSTNPublicAPISummaryEntity SET isActive = FALSE , "
			+ " updatedOn = CURRENT_TIMESTAMP WHERE isActive = TRUE ")
	int softDeleteActiveRecords();
}
