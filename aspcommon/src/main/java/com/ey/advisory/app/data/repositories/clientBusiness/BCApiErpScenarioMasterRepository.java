package com.ey.advisory.app.data.repositories.clientBusiness;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.data.entities.clientBusiness.BCApiErpScenarioMasterEntity;

@Repository("BCApiErpScenarioMasterRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface BCApiErpScenarioMasterRepository
		extends CrudRepository<BCApiErpScenarioMasterEntity, Long> {

	BCApiErpScenarioMasterEntity findByScenarioName(String string);
}