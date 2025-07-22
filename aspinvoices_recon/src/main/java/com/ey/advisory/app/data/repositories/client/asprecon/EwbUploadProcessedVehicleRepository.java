package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedVehicleEntity;

@Repository("EwbUploadProcessedVehicleRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EwbUploadProcessedVehicleRepository
		extends CrudRepository<EwbUploadProcessedVehicleEntity, Long> {

}
