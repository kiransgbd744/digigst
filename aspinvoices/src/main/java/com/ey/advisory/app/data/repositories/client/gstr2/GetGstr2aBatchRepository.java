/*package com.ey.advisory.app.data.repositories.client.gstr2;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2aBatchEntity;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Repository("getGstr2aBatchRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2aBatchRepository
		extends CrudRepository<GetGstr2aBatchEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr2aBatchEntity b SET b.isDelete = true WHERE "
			+ "b.isDelete = false AND b.type = :type AND b.apiSection = "
			+ ":apiSection AND b.cGstin =:cGstin AND b.taxPeriod =:taxPeriod")
	void softlyDelete(@Param("type") String type, 
			@Param("apiSection") String apiSection, 
			@Param("cGstin") String cGstin, 
			@Param("taxPeriod") String taxPeriod);
}
*/