package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr2b.Gstr2BTagging2AEntity;

/**
 * @author Siva.Reddy
 *
 */

@Repository("Gstr2BTagging2ARepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2BTagging2ARepository
		extends CrudRepository<Gstr2BTagging2AEntity, Long>,
		JpaSpecificationExecutor<Gstr2BTagging2AEntity> {

	@Modifying
	@Query("UPDATE Gstr2BTagging2AEntity b SET b.isDelete = true, "
			+ " b.modifiedBy = 'SYSTEM', b.modifiedOn = CURRENT_TIMESTAMP  "
			+ " WHERE b.isDelete = FALSE AND b.gstin = :gstin AND "
			+ " b.taxPeriod = :taxPeriod AND  b.section = :section AND  b.source = :source")
	int inactiveExistingEntries(@Param("gstin") String rGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("section") String section, @Param("source") String source);

	@Modifying
	@Query("UPDATE Gstr2BTagging2AEntity b SET b.status = :status,b.removalStatus = :removalStatus,"
			+ " b.modifiedBy = 'SYSTEM', b.modifiedOn = CURRENT_TIMESTAMP  "
			+ " WHERE b.isDelete = FALSE AND b.gstin = :gstin AND "
			+ " b.taxPeriod = :taxPeriod AND b.section = :section")
	int updateStatus(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("section") String section, @Param("status") String status,
			@Param("removalStatus") String removalStatus);
	
	@Modifying
	@Query("UPDATE Gstr2BTagging2AEntity b SET b.isDelete = true, "
			+ " b.modifiedBy = 'SYSTEM', b.modifiedOn = CURRENT_TIMESTAMP  "
			+ " WHERE b.isDelete = FALSE AND b.gstin = :gstin AND "
			+ " b.taxPeriod = :taxPeriod AND b.source = :source")
	int inactiveExistingEntries(@Param("gstin") String rGstin,
			@Param("taxPeriod") String taxPeriod,  @Param("source") String source);

}
