package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.GroupInfoEntity;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Repository("groupInfoRepository")
public interface GroupInfoRepository extends 
JpaRepository<GroupInfoEntity, Long> , 
JpaSpecificationExecutor<GroupInfoEntity>{

	@Query("SELECT g FROM GroupInfoEntity g")
	List<GroupInfoEntity> getAllGroups();
}
