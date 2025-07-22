package com.ey.advisory.core.async.repositories.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.ContainerConfig;


@Repository
@Transactional
public interface ContainerConfigRepository 
				extends JpaRepository<ContainerConfig, Long> {
	
	@Query("SELECT g FROM  ContainerConfig g WHERE g.groupCode = "
			+ ":groupCode and g.isActive = true ")
	public ContainerConfig getContainerConfigvalues(
					@Param("groupCode") String groupCode);
	
}
