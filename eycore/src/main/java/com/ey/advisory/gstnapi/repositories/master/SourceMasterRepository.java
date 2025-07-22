package com.ey.advisory.gstnapi.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.gstnapi.domain.master.SourceMasterEntity;

@Repository(value = "SourceMasterRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public interface SourceMasterRepository
		extends JpaRepository<SourceMasterEntity, Long>,
		JpaSpecificationExecutor<SourceMasterEntity> {

	@Query("SELECT e FROM SourceMasterEntity e")
	public List<SourceMasterEntity> getSourceMaster();
}
