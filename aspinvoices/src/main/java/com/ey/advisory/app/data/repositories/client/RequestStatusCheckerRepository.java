/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.RequestStatusCheckerEntity;

/**
 * @author Balakrishna.S
 *
 */
@Repository("RequestStatusCheckerRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface RequestStatusCheckerRepository extends 
CrudRepository<RequestStatusCheckerEntity, Long> {

}
