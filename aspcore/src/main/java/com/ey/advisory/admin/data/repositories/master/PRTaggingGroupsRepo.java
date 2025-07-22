package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.master.PRTaggingGroups;

/**
 * @author Sakshi.jain
 *
 */
@Repository("PRTaggingGroupsRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface PRTaggingGroupsRepo
		extends JpaRepository<PRTaggingGroups, Long> {

	@Query(" select g from PRTaggingGroups g where g.isPrRequired = false")
	public List<PRTaggingGroups> getAllPRTaggingReqGroupCode();

	@Modifying
	@Query("Update PRTaggingGroups set isPrRequired = true where groupCode =:groupCode")
	public void updatePrTaggedGroups(@Param("groupCode") String groupCode);
}
