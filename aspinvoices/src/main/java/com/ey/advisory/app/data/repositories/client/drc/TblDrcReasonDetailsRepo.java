
package com.ey.advisory.app.data.repositories.client.drc;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.drc.TblDrcDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcReasonDetails;

/**
 * 
 * @author Harsh
 *
 */

@Repository("TblDrcReasonDetailsRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TblDrcReasonDetailsRepo extends JpaRepository<TblDrcReasonDetails, Long>,
		JpaSpecificationExecutor<TblDrcReasonDetails> {
	
	public List<TblDrcReasonDetails> findByDrcDetailsId(
			TblDrcDetails drcDetailsId);
	
}

