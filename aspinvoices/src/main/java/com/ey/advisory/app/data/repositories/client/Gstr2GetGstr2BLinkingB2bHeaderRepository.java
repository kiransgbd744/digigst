package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BLinkingB2bHeaderEntity;

/**
 * @author Hema G M
 *
 */

@Repository("Gstr2GetGstr2BLinkingB2bHeaderRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2GetGstr2BLinkingB2bHeaderRepository
		extends CrudRepository<Gstr2GetGstr2BLinkingB2bHeaderEntity, Long>,
		JpaSpecificationExecutor<Gstr2GetGstr2BLinkingB2bHeaderEntity> {

	@Modifying
	@Query("UPDATE Gstr2GetGstr2BLinkingB2bHeaderEntity b SET b.isDelete = true, "
			+ " b.modifiedBy = :modifiedBy, b.modifiedOn =:modifiedOn  "
			+ " WHERE b.isDelete = FALSE AND b.rGstin = :rGstin AND "
			+ " b.taxPeriod = :taxPeriod")

	int softlyDeleteHeader(@Param("rGstin") String rGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("modifiedBy") String modifiedBy);

	@Modifying
	@Query("UPDATE Gstr2GetGstr2BLinkingB2bHeaderEntity b SET b.isDelete = true, "
			+ " b.modifiedOn =:modifiedOn  "
			+ " WHERE b.isDelete = FALSE AND b.rGstin = :rGstin AND "
			+ " b.taxPeriod = :taxPeriod")

	void softlyDeleteB2bHeader(@Param("rGstin") String rGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("UPDATE Gstr2GetGstr2BLinkingB2bHeaderEntity b SET b.isDelete = true, "
			+ " b.modifiedBy = 'SYSTEM', b.modifiedOn =:modifiedOn "
			+ " WHERE b.isDelete = FALSE AND b.invocationId NOT IN :invocationId"
			+ " AND b.rGstin = :rGstin AND b.taxPeriod = :taxPeriod")

	int softlyDeleteHeader(@Param("invocationId") List<Long> invocationId,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("rGstin") String rGstin,
			@Param("taxPeriod") String taxPeriod);

	@Modifying
	@Query("UPDATE Gstr2GetGstr2BLinkingB2bHeaderEntity b SET b.status = 'SAVED', "
			+ " b.modifiedOn =:modifiedOn WHERE b.invocationId =:invocationId"
			+ " AND b.status = 'ONHOLD'")
	int updateStatus(@Param("invocationId") Long invocationId,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("from Gstr2GetGstr2BLinkingB2bHeaderEntity where docKey=:docKey")
	List<Gstr2GetGstr2BLinkingB2bHeaderEntity> findByInvoiceKey(
			@Param("docKey") String docKey);

	@Modifying
	@Query("UPDATE Gstr2GetGstr2BLinkingB2bHeaderEntity b SET b.isDelete = true, b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> totalIds,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("SELECT COUNT(1) FROM Gstr2GetGstr2BLinkingB2bHeaderEntity where "
			+ "invocationId = :invocationId and isDelete = false")
	Long getCount(@Param("invocationId") Long invocationId);
}
