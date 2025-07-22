package com.ey.advisory.app.data.repositories.client.drc01c;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.drc01c.TblDrc01cDetails;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cSuppDocDetails;


@Repository("TblDrc01cSuppDetailsRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TblDrc01cSuppDetailsRepo extends JpaRepository<TblDrc01cSuppDocDetails, Long>,
		JpaSpecificationExecutor<TblDrc01cSuppDocDetails> {
	
	public List<TblDrc01cSuppDocDetails> findByDrc01cDetailsId(
			TblDrc01cDetails drc01cDetailsId);
	
}