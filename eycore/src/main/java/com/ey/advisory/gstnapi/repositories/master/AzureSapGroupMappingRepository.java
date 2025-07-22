package com.ey.advisory.gstnapi.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.gstnapi.domain.master.AzureSapGroupMappingEntity;

@Repository("AzureSapGroupMappingRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface AzureSapGroupMappingRepository
		extends JpaRepository<AzureSapGroupMappingEntity, Long>,
		JpaSpecificationExecutor<AzureSapGroupMappingEntity> {

	@Query("SELECT DISTINCT sapGroupCode from AzureSapGroupMappingEntity")
	List<String> getDistinctGroupCodes();
	
	AzureSapGroupMappingEntity findBySapGroupCode(String sapGroupCode);

}
