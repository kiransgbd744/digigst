package com.ey.advisory.app.data.repositories.client.supplier.ims;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.service.ims.supplier.SupplierImsGstr1AB2BHeaderStaggingEntity;

/**
 * 
 * @author ashutosh.kar
 *
 */
@Repository("SupplierImsGstr1AInvoiceB2BHeaderStagingRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface SupplierImsGstr1AInvoiceB2BHeaderStagingRepository
		extends JpaRepository<SupplierImsGstr1AB2BHeaderStaggingEntity, Long>, 
		JpaSpecificationExecutor<SupplierImsGstr1AB2BHeaderStaggingEntity> {

}
