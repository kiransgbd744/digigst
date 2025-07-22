/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.A2PrBigtableEntity;

/**
 * @author Khalid1.Khan
 *
 */

@Repository("Annexure2ComputeRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Annexure2ComputeRepository
		extends CrudRepository<A2PrBigtableEntity, Long> {

	@Modifying
	@Query("UPDATE A2PrBigtableEntity A SET A.a2ActionTaken=:action,"
			+ " A.a2ActionTakenTimeStamp= CURRENT_TIMESTAMP WHERE A.a2Invoicekey IN (:a2InvoiceKeyList)")
	void updateActionStatus(@Param("action") String action,
			@Param("a2InvoiceKeyList") List<String> a2InvoiceKeyList)
			throws DataAccessException;

}
