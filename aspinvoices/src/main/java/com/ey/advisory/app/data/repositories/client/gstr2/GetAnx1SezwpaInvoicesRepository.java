package com.ey.advisory.app.data.repositories.client.gstr2;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1SezwpaInvoicesHeaderEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("GetAnx1SezwpaInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetAnx1SezwpaInvoicesRepository
		extends CrudRepository<GetAnx1SezwpaInvoicesHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetAnx1SezwpaInvoicesHeaderEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.sGstin =:sGstin AND b.taxPeriod =:taxPeriod")
	void softlyDeleteSezwpHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);
}
