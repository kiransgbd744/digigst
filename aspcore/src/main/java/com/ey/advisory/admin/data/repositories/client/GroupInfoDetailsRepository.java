package com.ey.advisory.admin.data.repositories.client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GroupInfoEntity;

/**
 * @author Umesha.M
 *
 */
@Repository("groupInfoDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GroupInfoDetailsRepository
		extends CrudRepository<GroupInfoEntity, Long> {

	@Query("SELECT e.id FROM GroupInfoEntity e WHERE e.groupcode=:groupcode "
			+ "AND is_active=false")
	public Long findByGroupId(@Param("groupcode") final String groupcode);
}
