package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ErpAtEntityInfo;

/**
 * @author Siva.Nandam
 *
 */
@Repository("ErpAtEntityInfoRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ErpAtEntityInfoRepository extends 
                              CrudRepository<ErpAtEntityInfo, Long> {

	
	public List<Long> findByGroupcodeAndIsDeleteFalse(
			@Param("groupcode") String groupcode);
}
