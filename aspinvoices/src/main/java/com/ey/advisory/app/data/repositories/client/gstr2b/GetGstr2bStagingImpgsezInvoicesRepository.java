package com.ey.advisory.app.data.repositories.client.gstr2b;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2bStagingImpgsezInvoicesHeaderEntity;

/**
 * @author Ravindra V S
 *	
 */
@Repository("GetGstr2bStagingImpgsezInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2bStagingImpgsezInvoicesRepository
		extends CrudRepository<GetGstr2bStagingImpgsezInvoicesHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr2bStagingImpgsezInvoicesHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.rGstin = :rGstin AND b.taxPeriod = :taxPeriod")

	void softlyDeleteImpgsezHeader(@Param("rGstin") String rGstin, @Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
}
