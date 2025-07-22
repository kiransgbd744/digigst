/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.service.gstr1.sales.register.StagingSalesRegisterEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("Gstr1StagingRegisterRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1StagingRegisterRepository
		extends JpaRepository<StagingSalesRegisterEntity, Long>,
		JpaSpecificationExecutor<StagingSalesRegisterEntity> {


	public List<StagingSalesRegisterEntity> findByFileId(Long fileId);
}
