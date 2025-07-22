package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr2XVerticalErrorEntity;

/**
 * 
 * @author SriBhavya
 *
 */
@Repository("Gstr2xVerticalErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2xVerticalErrorRepository extends CrudRepository<Gstr2XVerticalErrorEntity, Long>{

}
