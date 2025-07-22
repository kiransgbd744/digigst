package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx2IsdcSummaryEntity;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */
@Repository("Anx2IsdcSummaryRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx2IsdcSummaryRepository extends 
           CrudRepository<GetAnx2IsdcSummaryEntity, Long>{

}
