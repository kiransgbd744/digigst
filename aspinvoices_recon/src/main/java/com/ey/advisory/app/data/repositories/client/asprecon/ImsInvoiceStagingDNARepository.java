package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.service.ims.ImsInvoiceStagingDNAEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Repository("ImsInvoiceStagingDNARepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsInvoiceStagingDNARepository
		extends JpaRepository<ImsInvoiceStagingDNAEntity, Long>,
		JpaSpecificationExecutor<ImsInvoiceStagingDNAEntity> {

	
}
