package com.ey.advisory.admin.data.repositories.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.Gstr3BItcEntityMaster;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("Gstr3bItcMasterRepo")
public interface Gstr3bItcMasterRepo
		extends JpaRepository<Gstr3BItcEntityMaster, Long>,
		                        JpaSpecificationExecutor<Gstr3BItcEntityMaster> {
}
