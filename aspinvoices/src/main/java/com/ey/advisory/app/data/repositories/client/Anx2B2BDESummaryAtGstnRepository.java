package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetANX2B2B_DE_SummaryEntity;
/**
 * 
 * @author Dibyakanta.sahoo
 *
 */
@Repository("Anx2B2BDESummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx2B2BDESummaryAtGstnRepository extends 
            CrudRepository<GetANX2B2B_DE_SummaryEntity, Long> {
	
	
}
