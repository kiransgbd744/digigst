package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.MasterFunctionalityEntity;

/**
 * @author Umesha.M
 *
 */
@Repository("masterFunctionalityRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface MasterFunctionalityRepository
		extends CrudRepository<MasterFunctionalityEntity, Long> {

	/**
	 * @param functionalityId
	 * @return
	 */
	@Query("SELECT entity FROM  MasterFunctionalityEntity entity "
			+ "WHERE entity.id=:functionalityId")
	public List<MasterFunctionalityEntity> findMasterFunctionalityByFuncCode(
			@Param("functionalityId") Long functionalityId);

	@Modifying
	@Transactional
	@Query("UPDATE MasterFunctionalityEntity SET "
			+ "functionalityDesc =:functionalityDesc WHERE "
			+ "functionalityCode=:functionalityCode")
	public void updateMasterFucntionality(
			@Param("functionalityCode") String functionalityCode,
			@Param("functionalityDesc") String functionalityDesc);
}
