package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx2ActionSummaryEntity;
/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */
@Repository("GetAnx2ActionSummaryRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetAnx2ActionSummaryRepository extends CrudRepository<GetAnx2ActionSummaryEntity,Long> {

}
