package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr8UrdHeaderEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Repository("Gstr8GetUrdGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr8GetUrdGstnRepository
		extends CrudRepository<GetGstr8UrdHeaderEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr8UrdHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	int softlyDeleteUrdHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("SELECT COUNT(*) FROM GetGstr8UrdHeaderEntity "
			+ "WHERE gstin=:gstin AND isDelete = false ")
	public int gstinCountByRetPerFromTo(@Param("gstin") String gstin);

}