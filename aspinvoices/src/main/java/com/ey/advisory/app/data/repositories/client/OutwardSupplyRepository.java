package com.ey.advisory.app.data.repositories.client;
/*package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.OutwardSupplyEntity;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Repository("outwardSupplyRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface OutwardSupplyRepository
		extends CrudRepository<OutwardSupplyEntity, Long> {
	@Query("SELECT outwardSupply FROM OutwardSupplyEntity outwardSupply "
			+ "WHERE outwardSupply.status = 'NEW' ORDER BY "
			+ "supplierGSTIN, documentType, documentDate, documentNumber")
	List<OutwardSupplyEntity> findNewInvoices();

}
*/