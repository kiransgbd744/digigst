package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1DocIssuedEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr1GetDocGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1GetDocGstnRepository
		extends CrudRepository<GetGstr1DocIssuedEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1DocIssuedEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	int softlyDeleteDocHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);

}
