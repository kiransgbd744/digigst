package com.ey.advisory.admin.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ComplianceAutomationLoggerEntity;

/**
 * 
 * @author Anand3.M
 *
 */
@Repository("ComplianceAutomationLogRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ComplianceAutomationLogRepository
		extends CrudRepository<ComplianceAutomationLoggerEntity, Long> {

}
