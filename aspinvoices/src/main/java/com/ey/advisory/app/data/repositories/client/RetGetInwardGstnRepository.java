package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.ret.GetRetInwardEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("RetGetInwardGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface RetGetInwardGstnRepository
		extends CrudRepository<GetRetInwardEntity, Long> {
	@Modifying
	@Query("UPDATE GetRetInwardEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	void softlyDeleteInwardHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);

}
