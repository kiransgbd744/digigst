package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Itc04InvoicesEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Itc04GetInvoicesGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Itc04GetInvoicesGstnRepository extends CrudRepository<Itc04InvoicesEntity, Long> {
	
	@Modifying
	@Query("UPDATE Itc04InvoicesEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	int softlyDeleteItc04Header(@Param("sGstin") String gstin,@Param("taxPeriod") String returnPeriod, @Param("modifiedOn") LocalDateTime modifiedOn);

}



