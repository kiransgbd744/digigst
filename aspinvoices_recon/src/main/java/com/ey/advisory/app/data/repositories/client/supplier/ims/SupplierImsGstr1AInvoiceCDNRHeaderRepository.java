
package com.ey.advisory.app.data.repositories.client.supplier.ims;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.service.ims.supplier.SupplierImsGstr1ACDNRHeaderEntity;


@Repository("SupplierImsGstr1AInvoiceCDNRHeaderRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface SupplierImsGstr1AInvoiceCDNRHeaderRepository
		extends JpaRepository<SupplierImsGstr1ACDNRHeaderEntity, Long>, 
		JpaSpecificationExecutor<SupplierImsGstr1ACDNRHeaderEntity> {

}
