package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx2CounterPartyTotalActionSummaryEntity;
/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */
@Repository("Anx2CounterPartySummaryRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx2CounterPartySummaryRepository extends 
           CrudRepository<GetAnx2CounterPartyTotalActionSummaryEntity, Long>{

}
