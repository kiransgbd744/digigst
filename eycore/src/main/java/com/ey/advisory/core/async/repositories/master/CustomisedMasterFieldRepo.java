package com.ey.advisory.core.async.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.CustomisedMasterFieldEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("CustomisedMasterFieldRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface CustomisedMasterFieldRepo
		extends JpaRepository<CustomisedMasterFieldEntity, Long> {

	@Query("SELECT doc.columnName FROM CustomisedMasterFieldEntity doc WHERE "
			+ "doc.fieldName IN (:fieldName) and doc.reportId = :reportId order by doc.seqId asc")
	public List<String> findDBColumn(@Param("fieldName") List<String> fieldName,
			@Param("reportId") Long reportId);

	public List<CustomisedMasterFieldEntity> findByReportIdAndIsActiveTrue(
			Long reportId);

}
