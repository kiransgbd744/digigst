/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04ReconProcedureEntity;

/**
 * @author Ravindra V S
 *
 */
@Repository("EwbVsItc04ProcedureRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EwbVsItc04ProcedureRepository
		extends JpaRepository<EwbVsItc04ReconProcedureEntity, Long>,
		JpaSpecificationExecutor<EwbVsItc04ReconProcedureEntity> {

}