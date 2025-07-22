/**
 * 
 */
package com.ey.advisory.app.data.repositories.clientBusiness;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.data.entities.clientBusiness.ErpBusinessBatchJsonPayloadsEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("ErpBusinessBatchJsonPayloadsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ErpBusinessBatchJsonPayloadsRepository
extends CrudRepository<ErpBusinessBatchJsonPayloadsEntity, Long> {

}