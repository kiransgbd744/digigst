package com.ey.advisory.core.async.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.Group;

@Repository("GroupRepository")
@Transactional
public interface GroupRepository extends JpaRepository<Group, Long> {
	
	public List<Group> findAll();
		
	public Group findByGroupId(Long groupId);
	@Query("select grp from Group grp where grp.isActive =:isActive ")
	public List<Group> findByIsActive(
			@Param(value="isActive") boolean isActive);

	public Group findByGroupCodeAndIsActiveTrue(String groupCode);
}
