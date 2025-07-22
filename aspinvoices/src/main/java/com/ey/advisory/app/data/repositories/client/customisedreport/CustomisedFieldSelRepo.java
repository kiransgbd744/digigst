package com.ey.advisory.app.data.repositories.client.customisedreport;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.customisedreport.CustomisedFieldSelEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("CustomisedFieldSelRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface CustomisedFieldSelRepo
		extends JpaRepository<CustomisedFieldSelEntity, Long>,
		JpaSpecificationExecutor<CustomisedFieldSelEntity> {

	public Optional<CustomisedFieldSelEntity> findByReportIdAndEntityIdAndIsActiveTrue(
			Long reportId, Long entityId);

	@Modifying
	@Query("UPDATE CustomisedFieldSelEntity g SET g.isActive = false,g.updatedOn = CURRENT_TIMESTAMP,"
			+ "g.updatedBy =:updatedBy "
			+ "WHERE g.entityId = :entityId and g.isActive = true")
	public int updateActiveExistingRecords(@Param("entityId") Long entityId,
			@Param("updatedBy") String updatedBy);
	
	public Optional<CustomisedFieldSelEntity> findByReportTypeAndEntityIdAndIsActiveTrue(
			String reportType, Long entityId);
	
	public List<CustomisedFieldSelEntity> findByEntityIdAndReportTypeAndIsActiveTrue(
			 Long entityId, String reportType);


}
