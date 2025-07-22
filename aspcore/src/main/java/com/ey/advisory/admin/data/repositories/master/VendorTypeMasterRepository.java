package com.ey.advisory.admin.data.repositories.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.VendorTypeMasterEntity;

/**
 * @author Saif.S
 *
 */
@Repository("VendorTypeMasterRepository")
public interface VendorTypeMasterRepository
		extends JpaRepository<VendorTypeMasterEntity, Long>,
		JpaSpecificationExecutor<VendorTypeMasterEntity> {

}
