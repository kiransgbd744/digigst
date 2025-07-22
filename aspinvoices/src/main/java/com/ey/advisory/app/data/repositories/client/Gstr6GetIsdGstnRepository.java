package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6IsdDetailsEntity;

/**
 * 
 * @author Anand3.M
 *
 */
@Repository("Gstr6GetIsdGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr6GetIsdGstnRepository extends CrudRepository<GetGstr6IsdDetailsEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr6IsdDetailsEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.taxPeriod =:taxPeriod")
	void softlyDeleteIsdHeader(@Param("sGstin") String sGstin, @Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

}
