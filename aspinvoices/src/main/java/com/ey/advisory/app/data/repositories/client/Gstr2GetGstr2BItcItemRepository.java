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

import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BItcItemEntity;

/**
 * @author Hema G M
 *
 */

@Repository("Gstr2GetGstr2BItcItemRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2GetGstr2BItcItemRepository
		extends CrudRepository<Gstr2GetGstr2BItcItemEntity, Long>,
		JpaSpecificationExecutor<Gstr2GetGstr2BItcItemEntity> {

	@Modifying
	@Query("UPDATE Gstr2GetGstr2BItcItemEntity b SET b.isDelete = true, "
			+ " b.modifiedBy = :modifiedBy, b.modifiedOn =:modifiedOn  "
			+ " WHERE b.isDelete = FALSE AND b.rGstin = :rGstin AND "
			+ " b.taxPeriod = :taxPeriod")

	int softlyDeleteHeader(@Param("rGstin") String rGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("modifiedBy") String modifiedBy);

	@Modifying
	@Query("UPDATE Gstr2GetGstr2BItcItemEntity b SET b.isDelete = true, "
			+ " b.modifiedOn =:modifiedOn, b.modifiedBy ='SYSTEM' "
			+ " WHERE b.isDelete = FALSE AND b.invocationId NOT IN :invocationId"
			+ " AND b.rGstin = :rGstin AND b.taxPeriod = :taxPeriod")
	int softlyDeleteHeader(@Param("invocationId") List<Long> invocationId,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("rGstin") String rGstin,
			@Param("taxPeriod") String taxPeriod);

	@Modifying
	@Query("UPDATE Gstr2GetGstr2BItcItemEntity b SET b.status = 'SAVED', "
			+ " b.modifiedOn =:modifiedOn WHERE b.invocationId =:invocationId"
			+ " AND b.status = 'ONHOLD'")
	int updateStatus(@Param("invocationId") Long invocationId,
			@Param("modifiedOn") LocalDateTime modifiedOn);
}
