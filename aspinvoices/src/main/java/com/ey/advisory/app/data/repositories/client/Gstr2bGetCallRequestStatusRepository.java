package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr2b.Gstr2bReportRequestEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("Gstr2bGetCallRequestStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2bGetCallRequestStatusRepository
		extends CrudRepository<Gstr2bReportRequestEntity, Long>,
		JpaSpecificationExecutor<Gstr2bReportRequestEntity> {

}
