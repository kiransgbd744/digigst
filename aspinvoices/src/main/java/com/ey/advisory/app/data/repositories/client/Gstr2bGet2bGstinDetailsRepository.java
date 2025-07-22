package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr2b.Gstr2bGet2bGstinDetailsEntity;

/**
 * @author Hema G M
 *
 */

@Repository("Gstr2bGet2bGstinDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2bGet2bGstinDetailsRepository
		extends CrudRepository<Gstr2bGet2bGstinDetailsEntity, Long>,
		JpaSpecificationExecutor<Gstr2bGet2bGstinDetailsEntity> {

	@Query("from Gstr2bGet2bGstinDetailsEntity where reqId =:reqId")
	public List<Gstr2bGet2bGstinDetailsEntity> FindByReqId(
			@Param("reqId") Long reqId);

}
