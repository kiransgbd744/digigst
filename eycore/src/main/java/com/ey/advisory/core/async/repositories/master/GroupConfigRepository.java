package com.ey.advisory.core.async.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.GroupConfig;


@Repository
@Transactional
public interface GroupConfigRepository 
				extends JpaRepository<GroupConfig, Long> {
	
	@Query("SELECT g from  GroupConfig g WHERE g.groupCode = "
			+ ":groupCode and g.isActive = true ")
	public List<GroupConfig> getGroupConfigvalues(
					@Param("groupCode") String groupCode);
	
	@Query("SELECT g from  GroupConfig g join Group grp on "
			+ "g.groupId = grp.groupId WHERE g.configCode in "
			+ ":configNames and g.isActive = true and "
			+ "grp.isActive = true "
			+ "order by g.groupCode")
	public List<GroupConfig> getGroupConfigsMatching(
					@Param("configNames") List<String> configNames);

	public List<GroupConfig> findByGroupCodeAndIsActiveTrue(String groupCode);	
	
}
