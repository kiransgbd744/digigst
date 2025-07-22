package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr2b.Gstr2BMonitorTagging2AEntity;

/**
 * @author Siva.Reddy
 *
 */

@Repository("Gstr2BMonitorTagging2ARepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2BMonitorTagging2ARepository
		extends CrudRepository<Gstr2BMonitorTagging2AEntity, Long>,
		JpaSpecificationExecutor<Gstr2BMonitorTagging2AEntity> {

	@Modifying
	@Query("UPDATE Gstr2BMonitorTagging2AEntity b SET b.isActive = false, "
			+ " b.modifiedBy = 'SYSTEM', b.modifiedOn = CURRENT_TIMESTAMP  "
			+ " WHERE b.isActive = true AND b.gstin = :gstin AND "
			+ " b.taxPeriod = :taxPeriod")
	int inactiveExistingEntries(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod);

	@Modifying
	@Query("UPDATE Gstr2BMonitorTagging2AEntity b SET b.status = :status, "
			+ " b.completedOn = :completedOn,b.modifiedBy = 'SYSTEM', b.modifiedOn = CURRENT_TIMESTAMP  "
			+ " WHERE b.isActive = true AND b.id = :id")
	int updateStatus(@Param("status") String status,
			@Param("completedOn") LocalDateTime completedOn,
			@Param("id") Long id);

	public List<Gstr2BMonitorTagging2AEntity> findByStatusInAndIsActiveTrue(
			List<String> status);

	public Gstr2BMonitorTagging2AEntity findTop1ByStatusInAndIsActiveTrueOrderByIdAsc(
			List<String> status);

	public Optional<Gstr2BMonitorTagging2AEntity> findByGstinAndTaxPeriodAndIsActiveTrueAndSource(
			String gstin, String taxPeriod, String source);
	
	public List<Gstr2BMonitorTagging2AEntity> findByStatusInAndIsActiveTrueOrderByIdAsc(
			List<String> status);

}
