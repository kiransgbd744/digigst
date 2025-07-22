package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadErrorItemEntity;

@Repository("EwbUploadErrorItemRepository")
public interface EwbUploadErrorItemRepository
		extends CrudRepository<EwbUploadErrorItemEntity, Long>,
		JpaSpecificationExecutor<EwbUploadErrorItemEntity>{

}
