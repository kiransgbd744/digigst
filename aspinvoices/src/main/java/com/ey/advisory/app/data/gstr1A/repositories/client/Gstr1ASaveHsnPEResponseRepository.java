package com.ey.advisory.app.data.gstr1A.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ASaveHsnPEResponseEntity;

@Repository("Gstr1ASaveHsnPEResponseRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1ASaveHsnPEResponseRepository
		extends JpaRepository<Gstr1ASaveHsnPEResponseEntity, Long>,
		JpaSpecificationExecutor<Gstr1ASaveHsnPEResponseEntity> {

}
