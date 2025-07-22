package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.ret.GetRetTable5Entity;

/**
 * 
 * @author Anand3.M
 *
 */
@Repository("RetGetTable5GstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface RetGetTable5GstnRepository
		extends CrudRepository<GetRetTable5Entity, Long> {
	@Modifying
	@Query("UPDATE GetRetTable5Entity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	void softlyDeleteTable5Header(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);

}
